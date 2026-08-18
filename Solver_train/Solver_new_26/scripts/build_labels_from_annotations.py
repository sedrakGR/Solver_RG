#!/usr/bin/env python3
"""Build square-level labels from board images and explicit square annotations."""

from __future__ import annotations

import argparse
import csv
from pathlib import Path
from typing import Dict, List, Tuple
import sys

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))

from chess_attr.board import export_board_squares, load_corners, warp_board
from chess_attr.dataset import write_square_samples
from chess_attr.schema import SquareSample


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--input-csv",
        type=Path,
        required=True,
        help=(
            "CSV with board rows. Required columns: image_path, labels_path. "
            "Optional: corners_path."
        ),
    )
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=Path("data/square_dataset"),
        help="Output directory for generated square crops and labels CSV.",
    )
    parser.add_argument(
        "--board-size",
        type=int,
        default=512,
        help="Board size after optional perspective warp.",
    )
    parser.add_argument(
        "--rank-from-white",
        action="store_true",
        help="Interpret y index as chess rank (8 at top, 1 at bottom).",
    )
    parser.add_argument(
        "--image-root",
        type=Path,
        help="Optional root for relative image/corners/labels paths in input CSV.",
    )
    parser.add_argument(
        "--allow-missing-squares",
        action="store_true",
        help="Allow labels files with fewer than 64 squares.",
    )
    return parser.parse_args()


def resolve_path(value: str, root: Path) -> Path:
    path = Path(value)
    if path.is_absolute():
        return path
    return (root / path).resolve()


def load_square_labels(path: Path) -> Dict[Tuple[int, int], Tuple[int, str]]:
    labels: Dict[Tuple[int, int], Tuple[int, str]] = {}
    with path.open("r", encoding="utf-8", newline="") as handle:
        reader = csv.DictReader(handle)
        required = {"x", "y", "color", "type"}
        missing = required.difference(reader.fieldnames or [])
        if missing:
            raise ValueError(f"Missing required columns in {path}: {sorted(missing)}")
        for row in reader:
            x = int(row["x"])
            y = int(row["y"])
            color = int(row["color"])
            piece_type = row["type"].strip().lower()
            if x < 1 or x > 8 or y < 1 or y > 8:
                raise ValueError(f"Invalid square coordinates in {path}: x={x}, y={y}")
            labels[(x, y)] = (color, piece_type)
    return labels


def main() -> None:
    args = parse_args()
    root = args.image_root or args.input_csv.parent
    args.output_dir.mkdir(parents=True, exist_ok=True)
    squares_dir = args.output_dir / "squares"
    squares_dir.mkdir(parents=True, exist_ok=True)

    output_samples: List[SquareSample] = []

    with args.input_csv.open("r", encoding="utf-8", newline="") as handle:
        reader = csv.DictReader(handle)
        expected = {"image_path", "labels_path"}
        missing = expected.difference(reader.fieldnames or [])
        if missing:
            raise ValueError(f"Missing required columns: {sorted(missing)}")

        for row_idx, row in enumerate(reader):
            image_path = resolve_path(row["image_path"], root)
            labels_path = resolve_path(row["labels_path"], root)
            square_labels = load_square_labels(labels_path)
            if not args.allow_missing_squares and len(square_labels) != 64:
                raise ValueError(
                    f"{labels_path} has {len(square_labels)} labels; expected 64 "
                    "(use --allow-missing-squares to bypass)."
                )

            image = Image.open(image_path).convert("RGB")
            corners_field = row.get("corners_path", "").strip()
            if corners_field:
                corners = load_corners(resolve_path(corners_field, root))
                board = warp_board(image, corners, board_size=args.board_size)
            else:
                board = image.resize((args.board_size, args.board_size), Image.BILINEAR)

            prefix = f"{image_path.stem}_{row_idx:04d}"
            crops = export_board_squares(
                board,
                output_dir=squares_dir,
                prefix=prefix,
                rank_from_white=args.rank_from_white,
            )
            for crop in crops:
                assert crop.path is not None
                key = (crop.x, crop.y)
                if key not in square_labels:
                    if args.allow_missing_squares:
                        continue
                    raise ValueError(f"Missing label for square {key} in {labels_path}")
                color, piece_type = square_labels[key]
                output_samples.append(
                    SquareSample(
                        image_path=crop.path,
                        color=color,
                        piece_type=piece_type,
                        x=crop.x,
                        y=crop.y,
                    )
                )

    labels_csv = args.output_dir / "labels.csv"
    write_square_samples(labels_csv, output_samples, root=args.output_dir)
    print(f"Wrote {len(output_samples)} square samples")
    print(f"Labels CSV -> {labels_csv}")


if __name__ == "__main__":
    main()
