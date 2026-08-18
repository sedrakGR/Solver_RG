from __future__ import annotations

import csv
import random
import tempfile
import unittest
from pathlib import Path
from typing import List, Tuple

from PIL import Image, ImageDraw

from chess_attr.board import split_board, warp_board
from chess_attr.classifiers import AxisClassifier, ColorClassifier, TypeClassifier, load_classifier, save_classifier
from chess_attr.constants import TYPE_LABELS, TYPE_NONE
from chess_attr.dataset import load_square_samples
from chess_attr.evaluation import accuracy
from chess_attr.schema import SquareSample
from chess_attr.training import classify_square, evaluate_axis, evaluate_color, evaluate_type


def draw_piece(draw: ImageDraw.ImageDraw, piece_type: str, color: int, rng: random.Random) -> None:
    if piece_type == TYPE_NONE:
        return
    shift_x = rng.randint(-2, 2)
    shift_y = rng.randint(-2, 2)
    fill = 230 if color == 1 else 30
    outline = 250 if color == 1 else 10

    if piece_type == "pawn":
        draw.ellipse((24 + shift_x, 14 + shift_y, 40 + shift_x, 30 + shift_y), fill=fill, outline=outline)
        draw.rectangle((26 + shift_x, 30 + shift_y, 38 + shift_x, 50 + shift_y), fill=fill, outline=outline)
    elif piece_type == "knight":
        draw.polygon(
            [
                (22 + shift_x, 50 + shift_y),
                (44 + shift_x, 50 + shift_y),
                (38 + shift_x, 20 + shift_y),
                (24 + shift_x, 30 + shift_y),
            ],
            fill=fill,
            outline=outline,
        )
    elif piece_type == "bishop":
        draw.polygon(
            [
                (32 + shift_x, 14 + shift_y),
                (44 + shift_x, 34 + shift_y),
                (32 + shift_x, 54 + shift_y),
                (20 + shift_x, 34 + shift_y),
            ],
            fill=fill,
            outline=outline,
        )
    elif piece_type == "rook":
        draw.rectangle((20 + shift_x, 20 + shift_y, 44 + shift_x, 50 + shift_y), fill=fill, outline=outline)
        draw.rectangle((18 + shift_x, 14 + shift_y, 46 + shift_x, 22 + shift_y), fill=fill, outline=outline)
    elif piece_type == "queen":
        draw.rectangle((22 + shift_x, 28 + shift_y, 42 + shift_x, 52 + shift_y), fill=fill, outline=outline)
        draw.ellipse((18 + shift_x, 12 + shift_y, 26 + shift_x, 20 + shift_y), fill=fill, outline=outline)
        draw.ellipse((28 + shift_x, 10 + shift_y, 36 + shift_x, 18 + shift_y), fill=fill, outline=outline)
        draw.ellipse((38 + shift_x, 12 + shift_y, 46 + shift_x, 20 + shift_y), fill=fill, outline=outline)
    elif piece_type == "king":
        draw.rectangle((24 + shift_x, 26 + shift_y, 40 + shift_x, 52 + shift_y), fill=fill, outline=outline)
        draw.rectangle((30 + shift_x, 10 + shift_y, 34 + shift_x, 26 + shift_y), fill=fill, outline=outline)
        draw.rectangle((24 + shift_x, 16 + shift_y, 40 + shift_x, 20 + shift_y), fill=fill, outline=outline)
    else:
        raise ValueError(f"Unsupported piece type {piece_type}")


def render_square(x: int, y: int, piece_type: str, color: int, seed: int) -> Image.Image:
    rng = random.Random(seed)
    base = 200 if (x + y) % 2 == 0 else 90
    jitter = rng.randint(-6, 6)
    background = max(0, min(255, base + jitter))
    image = Image.new("L", (64, 64), color=background)
    draw = ImageDraw.Draw(image)

    # Position hints for x/y training. Top strip encodes x, bottom strip encodes y.
    draw.rectangle((0, 0, 63, 3), fill=18)
    draw.rectangle((0, 60, 63, 63), fill=18)
    for slot in range(8):
        x0 = slot * 8
        x1 = x0 + 7
        top_fill = 36 + slot * 10
        bottom_fill = 36 + slot * 10
        if slot == x - 1:
            top_fill = 180 + slot * 5
        if slot == y - 1:
            bottom_fill = 180 + slot * 5
        draw.rectangle((x0, 0, x1, 3), fill=max(0, min(255, top_fill)))
        draw.rectangle((x0, 60, x1, 63), fill=max(0, min(255, bottom_fill)))

    draw_piece(draw, piece_type=piece_type, color=color, rng=rng)
    return image


def write_dataset(root: Path, num_samples: int, seed: int) -> Tuple[Path, List[SquareSample]]:
    rng = random.Random(seed)
    images_dir = root / "images"
    images_dir.mkdir(parents=True, exist_ok=True)
    csv_path = root / "labels.csv"

    samples: List[SquareSample] = []
    with csv_path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=["image_path", "color", "type", "x", "y"])
        writer.writeheader()
        for idx in range(num_samples):
            x = rng.randint(1, 8)
            y = rng.randint(1, 8)
            piece_type = rng.choice(TYPE_LABELS)
            color = 0 if piece_type == TYPE_NONE else rng.choice([1, 2])
            image = render_square(x=x, y=y, piece_type=piece_type, color=color, seed=seed * 1000 + idx)
            image_path = images_dir / f"sq_{idx:05d}.png"
            image.save(image_path)
            writer.writerow(
                {
                    "image_path": str(image_path.relative_to(root)),
                    "color": color,
                    "type": piece_type,
                    "x": x,
                    "y": y,
                }
            )
            samples.append(SquareSample(image_path=image_path, color=color, piece_type=piece_type, x=x, y=y))
    return csv_path, samples


class ClassifierPipelineTests(unittest.TestCase):
    def test_train_evaluate_and_serialize_models(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            train_csv, _ = write_dataset(root / "train", num_samples=450, seed=7)
            val_csv, _ = write_dataset(root / "val", num_samples=140, seed=19)
            train_samples = load_square_samples(train_csv)
            val_samples = load_square_samples(val_csv)

            color_model = ColorClassifier.train(train_samples)
            type_model = TypeClassifier.train(train_samples, include_none=True)
            x_model = AxisClassifier.train(train_samples, axis="x", deterministic=False)
            y_model = AxisClassifier.train(train_samples, axis="y", deterministic=False)

            color_eval = evaluate_color(color_model, val_samples)
            type_eval = evaluate_type(type_model, val_samples)
            x_eval = evaluate_axis(x_model, val_samples)
            y_eval = evaluate_axis(y_model, val_samples)

            self.assertGreater(color_eval.accuracy, 0.90)
            self.assertGreater(type_eval.accuracy, 0.87)
            self.assertGreater(x_eval.accuracy, 0.85)
            self.assertGreater(y_eval.accuracy, 0.85)

            models_dir = root / "models"
            models_dir.mkdir()
            color_path = models_dir / "color.json"
            type_path = models_dir / "type.json"
            x_path = models_dir / "x.json"
            y_path = models_dir / "y.json"
            save_classifier(color_model, color_path)
            save_classifier(type_model, type_path)
            save_classifier(x_model, x_path)
            save_classifier(y_model, y_path)

            reloaded_color = load_classifier(color_path)
            reloaded_type = load_classifier(type_path)
            reloaded_x = load_classifier(x_path)
            reloaded_y = load_classifier(y_path)

            truth_color: List[int] = []
            pred_color: List[int] = []
            truth_type: List[str] = []
            pred_type: List[str] = []
            truth_x: List[int] = []
            pred_x: List[int] = []
            truth_y: List[int] = []
            pred_y: List[int] = []
            for sample in val_samples:
                c, _ = reloaded_color.predict(sample.image_path)  # type: ignore[union-attr]
                t, _ = reloaded_type.predict(sample.image_path)  # type: ignore[union-attr]
                px, _ = reloaded_x.predict(sample.image_path, metadata={"x": sample.x, "y": sample.y})  # type: ignore[union-attr]
                py, _ = reloaded_y.predict(sample.image_path, metadata={"x": sample.x, "y": sample.y})  # type: ignore[union-attr]
                truth_color.append(sample.color)
                pred_color.append(c)
                truth_type.append(sample.piece_type)
                pred_type.append(t)
                truth_x.append(sample.x)
                pred_x.append(px)
                truth_y.append(sample.y)
                pred_y.append(py)

            self.assertGreater(accuracy(truth_color, pred_color), 0.90)
            self.assertGreater(accuracy(truth_type, pred_type), 0.87)
            self.assertGreater(accuracy(truth_x, pred_x), 0.85)
            self.assertGreater(accuracy(truth_y, pred_y), 0.85)

    def test_deterministic_axis_mode_and_board_split(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            train_csv, _ = write_dataset(root / "train", num_samples=220, seed=31)
            train_samples = load_square_samples(train_csv)

            color_model = ColorClassifier.train(train_samples)
            type_model = TypeClassifier.train(train_samples, include_none=True)
            x_model = AxisClassifier.train(train_samples, axis="x", deterministic=True)
            y_model = AxisClassifier.train(train_samples, axis="y", deterministic=True)

            board = Image.new("L", (512, 512), color=150)
            for row in range(8):
                for col in range(8):
                    x = col + 1
                    y = row + 1
                    piece_type = TYPE_LABELS[(row * 8 + col) % len(TYPE_LABELS)]
                    color = 0 if piece_type == TYPE_NONE else (1 if (x + y) % 2 == 0 else 2)
                    tile = render_square(x, y, piece_type, color, seed=10_000 + row * 8 + col)
                    board.paste(tile.resize((64, 64), Image.BILINEAR), (col * 64, row * 64))

            corners = [(0.0, 0.0), (511.0, 0.0), (511.0, 511.0), (0.0, 511.0)]
            warped = warp_board(board.convert("RGB"), corners, board_size=512)
            squares = split_board(warped, rank_from_white=False)
            self.assertEqual(len(squares), 64)

            for square in squares:
                pred = classify_square(
                    image=square.image,
                    x=square.x,
                    y=square.y,
                    color_model=color_model,
                    type_model=type_model,
                    x_model=x_model,
                    y_model=y_model,
                )
                self.assertEqual(pred["x"], square.x)
                self.assertEqual(pred["y"], square.y)


if __name__ == "__main__":
    unittest.main()
