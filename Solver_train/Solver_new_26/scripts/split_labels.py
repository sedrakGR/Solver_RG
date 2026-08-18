#!/usr/bin/env python3
"""Split square-level labels CSV into train/val CSV files."""

from __future__ import annotations

import argparse
import csv
import random
from pathlib import Path
from typing import List
import sys

ROOT = Path(__file__).resolve().parents[1]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--input-csv", type=Path, required=True, help="Input labels CSV.")
    parser.add_argument("--output-dir", type=Path, required=True, help="Output directory.")
    parser.add_argument(
        "--val-ratio",
        type=float,
        default=0.15,
        help="Validation fraction in [0, 1). Default: 0.15",
    )
    parser.add_argument("--seed", type=int, default=42, help="Random seed.")
    return parser.parse_args()


def write_csv(path: Path, rows: List[dict], fieldnames: List[str]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=fieldnames)
        writer.writeheader()
        for row in rows:
            writer.writerow(row)


def main() -> None:
    args = parse_args()
    if args.val_ratio < 0 or args.val_ratio >= 1:
        raise ValueError("--val-ratio must be in [0, 1).")

    with args.input_csv.open("r", encoding="utf-8", newline="") as handle:
        reader = csv.DictReader(handle)
        fieldnames = list(reader.fieldnames or [])
        rows = list(reader)

    if not rows:
        raise ValueError(f"No rows found in {args.input_csv}")

    rng = random.Random(args.seed)
    rng.shuffle(rows)
    split_idx = int(len(rows) * (1.0 - args.val_ratio))
    split_idx = max(1, min(split_idx, len(rows) - 1))
    train_rows = rows[:split_idx]
    val_rows = rows[split_idx:]

    train_csv = args.output_dir / "train.csv"
    val_csv = args.output_dir / "val.csv"
    write_csv(train_csv, train_rows, fieldnames)
    write_csv(val_csv, val_rows, fieldnames)

    print(f"Input rows : {len(rows)}")
    print(f"Train rows : {len(train_rows)} -> {train_csv}")
    print(f"Val rows   : {len(val_rows)} -> {val_csv}")


if __name__ == "__main__":
    main()
