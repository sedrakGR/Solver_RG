"""Training/evaluation helpers."""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Mapping

from PIL import Image

from .classifiers import AxisClassifier, ColorClassifier, TypeClassifier
from .evaluation import accuracy, confusion_matrix
from .schema import SquareSample


@dataclass(frozen=True)
class EvalResult:
    accuracy: float
    confusion: Dict[object, Dict[object, int]]


def evaluate_color(model: ColorClassifier, samples: Iterable[SquareSample]) -> EvalResult:
    truth: List[int] = []
    pred: List[int] = []
    for sample in samples:
        label, _ = model.predict(sample.image_path)
        truth.append(sample.color)
        pred.append(label)
    return EvalResult(accuracy=accuracy(truth, pred), confusion=confusion_matrix(truth, pred))


def evaluate_type(model: TypeClassifier, samples: Iterable[SquareSample]) -> EvalResult:
    truth: List[str] = []
    pred: List[str] = []
    for sample in samples:
        label, _ = model.predict(sample.image_path)
        truth.append(sample.piece_type)
        pred.append(label)
    return EvalResult(accuracy=accuracy(truth, pred), confusion=confusion_matrix(truth, pred))


def evaluate_axis(model: AxisClassifier, samples: Iterable[SquareSample]) -> EvalResult:
    truth: List[int] = []
    pred: List[int] = []
    axis = model.axis
    for sample in samples:
        expected = sample.x if axis == "x" else sample.y
        metadata = {"x": sample.x, "y": sample.y}
        label, _ = model.predict(sample.image_path, metadata=metadata)
        truth.append(expected)
        pred.append(label)
    return EvalResult(accuracy=accuracy(truth, pred), confusion=confusion_matrix(truth, pred))


def classify_square(
    image: Image.Image,
    x: int,
    y: int,
    color_model: ColorClassifier,
    type_model: TypeClassifier,
    x_model: AxisClassifier,
    y_model: AxisClassifier,
) -> Mapping[str, object]:
    gray = image.convert("L").resize((64, 64), Image.BILINEAR)
    color, color_conf = color_model.predict(gray)
    piece_type, type_conf = type_model.predict(gray)
    x_pred, x_conf = x_model.predict(gray, metadata={"x": x, "y": y})
    y_pred, y_conf = y_model.predict(gray, metadata={"x": x, "y": y})
    return {
        "x": x_pred,
        "y": y_pred,
        "color": color,
        "type": piece_type,
        "confidence": round((color_conf + type_conf + x_conf + y_conf) / 4.0, 4),
    }


def ensure_models_exist(model_paths: Mapping[str, Path]) -> None:
    missing = [name for name, path in model_paths.items() if not path.exists()]
    if missing:
        raise FileNotFoundError(
            "Missing model files: "
            + ", ".join(f"{name} -> {model_paths[name]}" for name in missing)
        )
