#!/usr/bin/env python3
"""
predict_board.py — image-to-situation bridge for Solver_RG.

Given a chess board image + paths to trained type/color classifiers, produces
JSON suitable for Solver_RG's createSituationFromImage bridge.

Output schema (printed to stdout, or saved to --output):

  {
    "name": "<situation name>",
    "gridSize": 8,
    "cells": [
      {
        "x": 1..8, "y": 1..8,
        "occupied": true|false,
        "type":  {"name": "pawn|knight|...|king|empty", "prob": 0.0..1.0},
        "color": {"name": "white|black|none", "prob": 0.0..1.0},
        "occupancyScore": float
      },
      ...
    ]
  }

The piece-type CNN was trained on pieces only (no "empty" class), so an
empty-square detector runs first as a brightness/saturation heuristic. Cells
classified empty bypass the CNNs and emit type="empty", color="none".
"""

import argparse
import json
import sys
from pathlib import Path
from typing import Dict, List, Optional, Tuple

from PIL import Image

THIS_DIR = Path(__file__).resolve().parent
sys.path.insert(0, str(THIS_DIR))

import torch  # type: ignore  # noqa: E402
import torch.nn.functional as F  # type: ignore  # noqa: E402

from chess_piece_classifier import (  # noqa: E402
    load_checkpoint_model,
    preprocess_pil,
    resolve_device,
    estimate_board_box,
    parse_board_box_arg,
    generate_grid_regions,
)


def is_empty_square(crop: Image.Image, threshold_var: float) -> Tuple[bool, float]:
    """Brightness-variance based occupancy heuristic.

    Empty board squares are flat (low pixel variance). A piece introduces
    edges + a distinct silhouette, raising the variance substantially.
    Returns (is_empty, variance_score).
    """
    gray = crop.convert("L").resize((48, 48))
    pixels = list(gray.getdata())
    n = len(pixels)
    mean = sum(pixels) / n
    var = sum((p - mean) ** 2 for p in pixels) / n
    return (var < threshold_var, float(var))


def predict_cell(
    crop: Image.Image,
    type_model,
    type_classes: List[str],
    type_img_size: int,
    color_model,
    color_classes: List[str],
    color_img_size: int,
    device: torch.device,
) -> Tuple[Tuple[str, float], Tuple[str, float]]:
    """Run both classifiers on a single cell crop."""
    with torch.no_grad():
        tx = preprocess_pil(crop, type_img_size).to(device)
        tlogits = type_model(tx)
        tprobs = F.softmax(tlogits, dim=1)[0]
        ti = int(torch.argmax(tprobs).item())
        type_pred = (type_classes[ti], float(tprobs[ti].item()))

        cx = preprocess_pil(crop, color_img_size).to(device)
        clogits = color_model(cx)
        cprobs = F.softmax(clogits, dim=1)[0]
        ci = int(torch.argmax(cprobs).item())
        color_pred = (color_classes[ci], float(cprobs[ci].item()))
    return type_pred, color_pred


def chess_xy_from_grid(row: int, col: int, grid_size: int) -> Tuple[int, int]:
    """Map (row_from_top, col_from_left) to Solver_RG's (cordX, cordY).

    The Solver_RG chess convention is the FEN-style mapping where (1, 1) is
    a1 (bottom-left from White's perspective) and (8, 8) is h8 (top-right).
    The grid we iterate goes top-down, left-to-right.
    """
    x = col + 1                 # 0..7  ->  1..8
    y = grid_size - row         # 0..(N-1) (top=0) -> N..1
    return x, y


def predict_board_command(args: argparse.Namespace) -> Dict:
    image_path = Path(args.image).expanduser().resolve()
    if not image_path.is_file():
        raise FileNotFoundError(f"Image not found: {image_path}")

    models_dir = Path(args.models_dir).expanduser().resolve()
    type_ckpt = Path(args.type_model) if args.type_model else (models_dir / "type_classifier.pt")
    color_ckpt = Path(args.color_model) if args.color_model else (models_dir / "color_classifier.pt")
    for ckpt in (type_ckpt, color_ckpt):
        if not ckpt.is_file():
            raise FileNotFoundError(f"Checkpoint not found: {ckpt}")

    device = resolve_device(args.device)
    type_model, type_classes, type_img_size, _ = load_checkpoint_model(type_ckpt, device)
    color_model, color_classes, color_img_size, _ = load_checkpoint_model(color_ckpt, device)

    image = Image.open(image_path).convert("RGB")
    width, height = image.size

    if args.board_box == "auto":
        x1, y1, x2, y2 = estimate_board_box(image, board_scale=args.board_scale)
    else:
        x1, y1, x2, y2 = parse_board_box_arg(args.board_box, width, height)

    regions = generate_grid_regions((x1, y1, x2, y2), args.grid_size)

    cells: List[Dict] = []
    for idx, region in enumerate(regions):
        row = idx // args.grid_size
        col = idx % args.grid_size
        cx, cy = chess_xy_from_grid(row, col, args.grid_size)

        crop = image.crop((region["x1"], region["y1"], region["x2"], region["y2"]))
        is_empty, occ_score = is_empty_square(crop, args.occupancy_var_threshold)

        if is_empty:
            cell = {
                "x": cx, "y": cy,
                "occupied": False,
                "type":  {"name": "empty", "prob": 1.0},
                "color": {"name": "none",  "prob": 1.0},
                "occupancyScore": occ_score,
            }
        else:
            (type_name, type_prob), (color_name, color_prob) = predict_cell(
                crop, type_model, type_classes, type_img_size,
                color_model, color_classes, color_img_size, device,
            )
            cell = {
                "x": cx, "y": cy,
                "occupied": True,
                "type":  {"name": type_name,  "prob": type_prob},
                "color": {"name": color_name, "prob": color_prob},
                "occupancyScore": occ_score,
            }
        cells.append(cell)

    return {
        "name": args.name or image_path.stem,
        "gridSize": args.grid_size,
        "sourceImage": str(image_path),
        "boardBox": [int(x1), int(y1), int(x2), int(y2)],
        "cells": cells,
    }


def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(
        description="Run type + color classifiers per cell on a chess board image "
                    "and emit Solver_RG situation JSON."
    )
    p.add_argument("--image", required=True, help="Path to a chess board image.")
    p.add_argument("--models-dir", default=str(THIS_DIR / "artifacts"),
                   help="Directory containing type_classifier.pt and color_classifier.pt.")
    p.add_argument("--type-model", default=None, help="Override path to type checkpoint.")
    p.add_argument("--color-model", default=None, help="Override path to color checkpoint.")
    p.add_argument("--device", default="cpu", choices=["auto", "cpu", "cuda"],
                   help="Inference device. Default cpu — keeps the subprocess MVP portable.")
    p.add_argument("--grid-size", type=int, default=8, help="Grid size (default 8).")
    p.add_argument("--board-box", default="auto",
                   help="Board bounding box 'x1,y1,x2,y2' (default: auto-centered).")
    p.add_argument("--board-scale", type=float, default=0.92,
                   help="Auto-mode centered-square scale (fraction of min side).")
    p.add_argument("--occupancy-var-threshold", type=float, default=120.0,
                   help="Per-cell brightness variance below which the cell is treated as empty. "
                        "Tune per board style; 120 works for standard rendered boards.")
    p.add_argument("--name", default=None, help="Situation name (default: image stem).")
    p.add_argument("--output", default="-",
                   help="Output path for JSON. '-' (default) writes to stdout.")
    return p


def main() -> None:
    args = build_parser().parse_args()
    result = predict_board_command(args)
    payload = json.dumps(result, indent=2)
    if args.output == "-":
        sys.stdout.write(payload)
    else:
        Path(args.output).write_text(payload, encoding="utf-8")


if __name__ == "__main__":
    main()
