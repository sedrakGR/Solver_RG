"""Evaluation utilities."""

from __future__ import annotations

from collections import defaultdict
from typing import Dict, Iterable, List, Sequence, Tuple, TypeVar

T = TypeVar("T")


def accuracy(truth: Sequence[T], pred: Sequence[T]) -> float:
    if not truth:
        return 0.0
    if len(truth) != len(pred):
        raise ValueError("truth and pred must have equal length")
    correct = sum(1 for t, p in zip(truth, pred) if t == p)
    return correct / len(truth)


def confusion_matrix(truth: Iterable[T], pred: Iterable[T]) -> Dict[T, Dict[T, int]]:
    matrix: Dict[T, Dict[T, int]] = defaultdict(lambda: defaultdict(int))  # type: ignore[assignment]
    for t, p in zip(truth, pred):
        matrix[t][p] += 1
    return {label: dict(counts) for label, counts in matrix.items()}


def format_confusion(matrix: Dict[T, Dict[T, int]]) -> str:
    rows: List[str] = []
    for truth_label in sorted(matrix.keys(), key=str):
        preds = matrix[truth_label]
        pieces = [f"{pred_label}:{count}" for pred_label, count in sorted(preds.items(), key=lambda item: str(item[0]))]
        rows.append(f"{truth_label} -> " + ", ".join(pieces))
    return "\n".join(rows)
