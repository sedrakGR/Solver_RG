"""Shared labels and constants."""

from __future__ import annotations

COLOR_NONE = 0
COLOR_SIDE_A = 1
COLOR_SIDE_B = 2

COLOR_LABELS = (COLOR_NONE, COLOR_SIDE_A, COLOR_SIDE_B)

TYPE_NONE = "none"
TYPE_PAWN = "pawn"
TYPE_KNIGHT = "knight"
TYPE_BISHOP = "bishop"
TYPE_ROOK = "rook"
TYPE_QUEEN = "queen"
TYPE_KING = "king"

TYPE_LABELS = (
    TYPE_NONE,
    TYPE_PAWN,
    TYPE_KNIGHT,
    TYPE_BISHOP,
    TYPE_ROOK,
    TYPE_QUEEN,
    TYPE_KING,
)

TYPE_TO_INDEX = {label: idx for idx, label in enumerate(TYPE_LABELS)}
INDEX_TO_TYPE = {idx: label for idx, label in enumerate(TYPE_LABELS)}
