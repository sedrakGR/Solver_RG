"""Chess attribute classifiers package."""

from .classifiers import (
    AxisClassifier,
    ColorClassifier,
    TypeClassifier,
    load_classifier,
    save_classifier,
)
from .constants import COLOR_LABELS, TYPE_LABELS

__all__ = [
    "AxisClassifier",
    "ColorClassifier",
    "TypeClassifier",
    "load_classifier",
    "save_classifier",
    "COLOR_LABELS",
    "TYPE_LABELS",
]
