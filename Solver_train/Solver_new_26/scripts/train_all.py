#!/usr/bin/env python3
"""Train color/type/x/y classifiers in one command."""

from __future__ import annotations

import argparse
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))

from chess_attr.classifiers import AxisClassifier, ColorClassifier, TypeClassifier, save_classifier
from chess_attr.dataset import load_square_samples
from chess_attr.evaluation import format_confusion
from chess_attr.training import evaluate_axis, evaluate_color, evaluate_type


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--train-csv", type=Path, required=True)
    parser.add_argument("--val-csv", type=Path)
    parser.add_argument("--models-dir", type=Path, default=Path("models"))
    parser.add_argument("--deterministic-axis", action="store_true")
    parser.add_argument("--exclude-none-type", action="store_true")
    return parser.parse_args()


def print_result(name: str, result: object) -> None:
    # EvalResult is used internally; this keeps script dependency light.
    print(f"{name} accuracy: {result.accuracy:.4f}")  # type: ignore[attr-defined]
    print(format_confusion(result.confusion))  # type: ignore[attr-defined]


def main() -> None:
    args = parse_args()
    args.models_dir.mkdir(parents=True, exist_ok=True)

    train_samples = load_square_samples(args.train_csv)
    val_samples = load_square_samples(args.val_csv) if args.val_csv else None

    color_model = ColorClassifier.train(train_samples)
    type_model = TypeClassifier.train(train_samples, include_none=not args.exclude_none_type)
    x_model = AxisClassifier.train(train_samples, axis="x", deterministic=args.deterministic_axis)
    y_model = AxisClassifier.train(train_samples, axis="y", deterministic=args.deterministic_axis)

    save_classifier(color_model, args.models_dir / "color_model.json")
    save_classifier(type_model, args.models_dir / "type_model.json")
    save_classifier(x_model, args.models_dir / "x_model.json")
    save_classifier(y_model, args.models_dir / "y_model.json")
    print(f"Saved models to {args.models_dir}")

    if val_samples:
        print_result("color", evaluate_color(color_model, val_samples))
        print_result("type", evaluate_type(type_model, val_samples))
        print_result("x", evaluate_axis(x_model, val_samples))
        print_result("y", evaluate_axis(y_model, val_samples))


if __name__ == "__main__":
    main()
