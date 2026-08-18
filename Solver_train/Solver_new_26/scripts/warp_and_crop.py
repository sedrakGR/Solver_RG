#!/usr/bin/env python3
"""Warp a board image to canonical view and split into 8x8 crops."""

from __future__ import annotations

import argparse
import csv
from pathlib import Path
import sys

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))

from chess_attr.board import export_board_squares, load_corners, warp_board


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--image", type=Path, required=True, help="Input board image path.")
    parser.add_argument("--corners", type=Path, required=True, help="JSON file with 4 board corners.")
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=Path("data/crops"),
        help="Directory for square crops and metadata.",
    )
    parser.add_argument(
        "--board-size",
        type=int,
        default=512,
        help="Target board resolution for warp.",
    )
    parser.add_argument(
        "--rank-from-white",
        action="store_true",
        help="Encode y labels as chess ranks (8 at top, 1 at bottom).",
    )
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    args.output_dir.mkdir(parents=True, exist_ok=True)

    image = Image.open(args.image).convert("RGB")
    corners = load_corners(args.corners)
    warped = warp_board(image, corners, board_size=args.board_size)
    warped_path = args.output_dir / "warped_board.png"
    warped.save(warped_path)

    squares_dir = args.output_dir / "squares"
    squares = export_board_squares(
        warped,
        output_dir=squares_dir,
        prefix=args.image.stem,
        rank_from_white=args.rank_from_white,
    )

    csv_path = args.output_dir / "squares.csv"
    with csv_path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=["image_path", "x", "y"])
        writer.writeheader()
        for square in squares:
            assert square.path is not None
            writer.writerow(
                {
                    "image_path": str(square.path.relative_to(args.output_dir)),
                    "x": square.x,
                    "y": square.y,
                }
            )

    print(f"Warped board  -> {warped_path}")
    print(f"Crops         -> {squares_dir}")
    print(f"Metadata CSV  -> {csv_path}")


if __name__ == "__main__":
    main()
