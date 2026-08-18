"""Dataset parsing for square-level labels."""

from __future__ import annotations

import csv
from pathlib import Path
from typing import Iterable, List

from .constants import TYPE_NONE, TYPE_TO_INDEX
from .schema import SquareSample


REQUIRED_COLUMNS = {"image_path", "color", "type", "x", "y"}


def _normalize_type(value: str) -> str:
    raw = value.strip().lower()
    if raw in ("", "-", "empty"):
        return TYPE_NONE
    if raw not in TYPE_TO_INDEX:
        raise ValueError(f"Unsupported type label: {value!r}")
    return raw


def load_square_samples(csv_path: Path, root: Path | None = None) -> List[SquareSample]:
    root = root or csv_path.parent
    samples: List[SquareSample] = []
    with csv_path.open("r", encoding="utf-8", newline="") as handle:
        reader = csv.DictReader(handle)
        missing = REQUIRED_COLUMNS.difference(reader.fieldnames or [])
        if missing:
            raise ValueError(f"Missing required columns in {csv_path}: {sorted(missing)}")
        for row in reader:
            image_path = Path(row["image_path"])
            if not image_path.is_absolute():
                image_path = (root / image_path).resolve()
            samples.append(
                SquareSample(
                    image_path=image_path,
                    color=int(row["color"]),
                    piece_type=_normalize_type(row["type"]),
                    x=int(row["x"]),
                    y=int(row["y"]),
                )
            )
    return samples


def write_square_samples(csv_path: Path, samples: Iterable[SquareSample], root: Path | None = None) -> None:
    root = root or csv_path.parent
    root.mkdir(parents=True, exist_ok=True)
    with csv_path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=["image_path", "color", "type", "x", "y"])
        writer.writeheader()
        for sample in samples:
            image_path = sample.image_path
            try:
                rel_path = image_path.resolve().relative_to(root.resolve())
            except ValueError:
                rel_path = image_path
            writer.writerow(
                {
                    "image_path": str(rel_path),
                    "color": sample.color,
                    "type": sample.piece_type,
                    "x": sample.x,
                    "y": sample.y,
                }
            )
