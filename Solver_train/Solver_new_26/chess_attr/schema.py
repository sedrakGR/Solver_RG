"""Dataclasses for labels and predictions."""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from typing import Optional


@dataclass(frozen=True)
class SquareSample:
    image_path: Path
    color: int
    piece_type: str
    x: int
    y: int


@dataclass(frozen=True)
class SquarePrediction:
    color: int
    piece_type: str
    x: int
    y: int
    confidence: Optional[float] = None
