#!/usr/bin/env python3
"""Run end-to-end square classification on a board image."""

from __future__ import annotations

import argparse
from pathlib import Path
from typing import List, Sequence
import sys

from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))

from chess_attr.board import load_corners, split_board, warp_board
from chess_attr.classifiers import AxisClassifier, ColorClassifier, TypeClassifier, load_classifier
from chess_attr.constants import TYPE_NONE
from chess_attr.training import classify_square
from chess_attr.utils import save_json


TYPE_SHORT = {
    "none": "-",
    "pawn": "P",
    "knight": "N",
    "bishop": "B",
    "rook": "R",
    "queen": "Q",
    "king": "K",
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--image", type=Path, required=True, help="Input board image.")
    parser.add_argument("--corners", type=Path, help="Optional corners JSON for perspective warp.")
    parser.add_argument("--color-model", type=Path, default=Path("models/color_model.json"))
    parser.add_argument("--type-model", type=Path, default=Path("models/type_model.json"))
    parser.add_argument("--x-model", type=Path, default=Path("models/x_model.json"))
    parser.add_argument("--y-model", type=Path, default=Path("models/y_model.json"))
    parser.add_argument("--board-size", type=int, default=512, help="Warp/canonical board size.")
    parser.add_argument(
        "--rank-from-white",
        action="store_true",
        help="Interpret y index as chess rank (8 at top, 1 at bottom).",
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=Path("data/inference/output.json"),
        help="Output JSON file.",
    )
    parser.add_argument(
        "--viz-output",
        type=Path,
        default=Path("data/inference/output_viz.png"),
        help="Visualization image output path.",
    )
    parser.add_argument(
        "--no-viz",
        action="store_true",
        help="Disable visualization image output.",
    )
    parser.add_argument(
        "--show-empty",
        action="store_true",
        help="Show labels for empty squares in visualization.",
    )
    return parser.parse_args()


def _require_model(path: Path, expected_type: type) -> object:
    loaded = load_classifier(path)
    if not isinstance(loaded, expected_type):
        raise TypeError(f"Expected {expected_type.__name__} at {path}, got {type(loaded).__name__}")
    return loaded


def _short_type(piece_type: str) -> str:
    return TYPE_SHORT.get(piece_type, piece_type[:1].upper())


def render_visualization(
    board: Image.Image,
    predictions: Sequence[dict],
    output_path: Path,
    show_empty: bool = False,
) -> None:
    output_path.parent.mkdir(parents=True, exist_ok=True)
    draw = ImageDraw.Draw(board)
    font = ImageFont.load_default()

    width, height = board.size
    square_w = width // 8
    square_h = height // 8

    for idx, pred in enumerate(predictions):
        row = idx // 8
        col = idx % 8
        x0 = col * square_w
        y0 = row * square_h
        x1 = width if col == 7 else (col + 1) * square_w
        y1 = height if row == 7 else (row + 1) * square_h

        color_label = int(pred["color"])
        piece_type = str(pred["type"])
        conf = float(pred["confidence"])
        if color_label == 0 and not show_empty:
            outline = (90, 90, 90)
        elif color_label == 1:
            outline = (72, 189, 96)  # side-A
        elif color_label == 2:
            outline = (231, 98, 84)  # side-B
        else:
            outline = (90, 90, 90)  # empty/unknown

        draw.rectangle((x0, y0, x1, y1), outline=outline, width=2)

        if color_label == 0 and not show_empty:
            continue
        label = f"{_short_type(piece_type)} c{color_label} {conf:.2f}"
        text_bbox = draw.textbbox((0, 0), label, font=font)
        text_w = text_bbox[2] - text_bbox[0]
        text_h = text_bbox[3] - text_bbox[1]
        pad = 2
        tx0 = x0 + pad
        ty0 = y0 + pad
        tx1 = min(x1 - 1, tx0 + text_w + 2 * pad)
        ty1 = min(y1 - 1, ty0 + text_h + 2 * pad)
        draw.rectangle((tx0, ty0, tx1, ty1), fill=(0, 0, 0))
        draw.text((tx0 + pad, ty0 + pad), label, fill=(255, 255, 255), font=font)

    # Grid lines for easier visual inspection.
    for i in range(9):
        x = min(width - 1, i * square_w)
        y = min(height - 1, i * square_h)
        draw.line((x, 0, x, height), fill=(220, 220, 220), width=1)
        draw.line((0, y, width, y), fill=(220, 220, 220), width=1)

    board.save(output_path)


def main() -> None:
    args = parse_args()

    color_model = _require_model(args.color_model, ColorClassifier)
    type_model = _require_model(args.type_model, TypeClassifier)
    x_model = _require_model(args.x_model, AxisClassifier)
    y_model = _require_model(args.y_model, AxisClassifier)

    image = Image.open(args.image).convert("RGB")
    if args.corners:
        corners = load_corners(args.corners)
        board = warp_board(image, corners, board_size=args.board_size)
    else:
        board = image.resize((args.board_size, args.board_size), Image.BILINEAR)

    predictions: List[dict] = []
    squares = split_board(board, rank_from_white=args.rank_from_white)
    for square in squares:
        result = classify_square(
            image=square.image,
            x=square.x,
            y=square.y,
            color_model=color_model,  # type: ignore[arg-type]
            type_model=type_model,  # type: ignore[arg-type]
            x_model=x_model,  # type: ignore[arg-type]
            y_model=y_model,  # type: ignore[arg-type]
        )
        if int(result["color"]) == 0:
            result["type"] = TYPE_NONE
        predictions.append(result)

    payload = {
        "image": str(args.image),
        "board_size": args.board_size,
        "num_squares": len(predictions),
        "predictions": predictions,
    }
    save_json(args.output, payload)
    print(f"Wrote inference output -> {args.output}")
    if not args.no_viz:
        render_visualization(
            board=board.copy(),
            predictions=predictions,
            output_path=args.viz_output,
            show_empty=args.show_empty,
        )
        print(f"Wrote visualization -> {args.viz_output}")


if __name__ == "__main__":
    main()
