#!/usr/bin/env python3
"""Train the Y (rank index) classifier."""

from __future__ import annotations

import argparse
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))

from chess_attr.classifiers import AxisClassifier, save_classifier
from chess_attr.dataset import load_square_samples
from chess_attr.evaluation import format_confusion
from chess_attr.training import evaluate_axis


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--train-csv", type=Path, required=True, help="Square-level training CSV.")
    parser.add_argument("--val-csv", type=Path, help="Optional validation CSV.")
    parser.add_argument(
        "--output",
        type=Path,
        default=Path("models/y_model.json"),
        help="Model output path.",
    )
    parser.add_argument(
        "--deterministic",
        action="store_true",
        help="Bypass learning and always return crop metadata y value.",
    )
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    train_samples = load_square_samples(args.train_csv)
    model = AxisClassifier.train(train_samples, axis="y", deterministic=args.deterministic)
    save_classifier(model, args.output)
    print(f"Saved Y model -> {args.output}")

    if args.val_csv:
        val_samples = load_square_samples(args.val_csv)
        result = evaluate_axis(model, val_samples)
        print(f"Validation accuracy: {result.accuracy:.4f}")
        print("Confusion matrix:")
        print(format_confusion(result.confusion))


if __name__ == "__main__":
    main()
