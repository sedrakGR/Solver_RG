#!/usr/bin/env python3
"""Build square-label CSV for type training from class-folder image datasets."""

from __future__ import annotations

import argparse
from pathlib import Path
from typing import Dict, Iterable, List, Optional, Tuple
import sys

ROOT = Path(__file__).resolve().parents[1]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))

from chess_attr.constants import TYPE_LABELS, TYPE_NONE
from chess_attr.dataset import write_square_samples
from chess_attr.schema import SquareSample


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--dataset-root",
        type=Path,
        required=True,
        help="Root directory containing class subfolders with images.",
    )
    parser.add_argument(
        "--output-csv",
        type=Path,
        default=Path("data/type_dataset/labels.csv"),
        help="Output labels CSV path.",
    )
    parser.add_argument(
        "--recursive",
        action="store_true",
        help="Recursively scan class folders for image files.",
    )
    parser.add_argument(
        "--default-x",
        type=int,
        default=1,
        help="Dummy x value for type-only datasets (default: 1).",
    )
    parser.add_argument(
        "--default-y",
        type=int,
        default=1,
        help="Dummy y value for type-only datasets (default: 1).",
    )
    return parser.parse_args()


def is_image(path: Path) -> bool:
    return path.suffix.lower() in {".png", ".jpg", ".jpeg", ".bmp", ".webp"}


def normalize_name(name: str) -> str:
    return (
        name.lower()
        .replace("-", "_")
        .replace(" ", "_")
        .replace("__", "_")
        .strip("_")
    )


def resolve_type_and_color(class_name: str) -> Optional[Tuple[str, int]]:
    raw = normalize_name(class_name)

    # Common empty aliases.
    if raw in {"none", "empty", "background", "blank"}:
        return TYPE_NONE, 0

    # Handle openboard-like 13-class labels.
    if raw.startswith("white_"):
        side = 1
        piece = raw.removeprefix("white_")
    elif raw.startswith("black_"):
        side = 2
        piece = raw.removeprefix("black_")
    else:
        side = 1
        piece = raw

    aliases: Dict[str, str] = {
        "p": "pawn",
        "n": "knight",
        "b": "bishop",
        "r": "rook",
        "q": "queen",
        "k": "king",
        "horse": "knight",
        "empty_square": "none",
        "no_piece": "none",
    }
    piece = aliases.get(piece, piece)

    if piece not in TYPE_LABELS:
        return None
    if piece == TYPE_NONE:
        return TYPE_NONE, 0
    return piece, side


def iter_class_dirs(dataset_root: Path) -> Iterable[Path]:
    for entry in sorted(dataset_root.iterdir()):
        if entry.is_dir():
            yield entry


def iter_images(class_dir: Path, recursive: bool) -> Iterable[Path]:
    if recursive:
        for path in sorted(class_dir.rglob("*")):
            if path.is_file() and is_image(path):
                yield path
    else:
        for path in sorted(class_dir.iterdir()):
            if path.is_file() and is_image(path):
                yield path


def main() -> None:
    args = parse_args()
    if not args.dataset_root.exists():
        raise FileNotFoundError(args.dataset_root)

    samples: List[SquareSample] = []
    skipped_dirs: List[str] = []
    counts: Dict[str, int] = {label: 0 for label in TYPE_LABELS}

    for class_dir in iter_class_dirs(args.dataset_root):
        resolved = resolve_type_and_color(class_dir.name)
        if resolved is None:
            skipped_dirs.append(class_dir.name)
            continue
        piece_type, color = resolved
        for image_path in iter_images(class_dir, recursive=args.recursive):
            samples.append(
                SquareSample(
                    image_path=image_path.resolve(),
                    color=color,
                    piece_type=piece_type,
                    x=args.default_x,
                    y=args.default_y,
                )
            )
            counts[piece_type] += 1

    if not samples:
        raise ValueError(
            "No samples found. Check class folder names and image extensions."
        )

    write_square_samples(args.output_csv, samples, root=args.output_csv.parent)
    print(f"Wrote {len(samples)} samples -> {args.output_csv}")
    print("Type counts:")
    for piece_type in TYPE_LABELS:
        print(f"  {piece_type}: {counts[piece_type]}")
    if skipped_dirs:
        print("Skipped class directories (unrecognized names):")
        for name in skipped_dirs:
            print(f"  {name}")


if __name__ == "__main__":
    main()
