"""Classifier implementations and model serialization."""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Mapping, MutableMapping, Optional, Sequence, Tuple, Union

from PIL import Image

from .constants import (
    COLOR_NONE,
    COLOR_SIDE_A,
    COLOR_SIDE_B,
    TYPE_LABELS,
    TYPE_NONE,
)
from .image_features import (
    extract_axis_vector,
    extract_color_signals,
    extract_type_vector,
    load_grayscale,
)
from .schema import SquareSample
from .utils import load_json, mean, save_json


def _vector_mean(vectors: Sequence[Sequence[float]]) -> List[float]:
    if not vectors:
        return []
    width = len(vectors[0])
    sums = [0.0] * width
    for vector in vectors:
        for idx, value in enumerate(vector):
            sums[idx] += value
    return [value / len(vectors) for value in sums]


def _vector_distance(a: Sequence[float], b: Sequence[float]) -> float:
    return sum((ax - bx) ** 2 for ax, bx in zip(a, b)) ** 0.5


def _quantile(values: Sequence[float], q: float) -> float:
    if not values:
        return 0.0
    ordered = sorted(values)
    q = max(0.0, min(1.0, q))
    idx = int(round((len(ordered) - 1) * q))
    return ordered[idx]


def _as_image(source: Union[Path, Image.Image]) -> Image.Image:
    if isinstance(source, Path):
        return load_grayscale(source, size=(64, 64))
    return source.convert("L").resize((64, 64), Image.BILINEAR)


@dataclass
class ColorClassifier:
    occupied_threshold: float
    light_threshold: float
    side_a_is_brighter: bool
    side_centroids: Dict[int, List[float]]
    model_version: str = "color_hybrid_v3"

    @staticmethod
    def _side_feature_vector(image: Image.Image) -> List[float]:
        occupancy, lightness = extract_color_signals(image)
        type_vec = extract_type_vector(image, size=8)
        diff_strength = mean(type_vec[: 8 * 8])
        signed_tone = type_vec[-1]
        return [occupancy / 100.0, lightness / 255.0, diff_strength, signed_tone]

    @classmethod
    def train(cls, samples: Iterable[SquareSample]) -> "ColorClassifier":
        none_occ: List[float] = []
        piece_occ: List[float] = []
        side_a_light: List[float] = []
        side_b_light: List[float] = []
        side_vectors: Dict[int, List[List[float]]] = {COLOR_SIDE_A: [], COLOR_SIDE_B: []}

        for sample in samples:
            image = load_grayscale(sample.image_path, size=(64, 64))
            occupancy, lightness = extract_color_signals(image)
            if sample.color == COLOR_NONE:
                none_occ.append(occupancy)
            else:
                piece_occ.append(occupancy)
                vector = cls._side_feature_vector(image)
                if sample.color == COLOR_SIDE_A:
                    side_a_light.append(lightness)
                    side_vectors[COLOR_SIDE_A].append(vector)
                elif sample.color == COLOR_SIDE_B:
                    side_b_light.append(lightness)
                    side_vectors[COLOR_SIDE_B].append(vector)

        if not piece_occ:
            occupied_threshold = _quantile(none_occ, 0.95) + 1.0
        else:
            none_q = _quantile(none_occ, 0.95)
            piece_q = _quantile(piece_occ, 0.05)
            if piece_q > none_q:
                occupied_threshold = (none_q + piece_q) / 2.0
            else:
                occupied_threshold = (mean(none_occ) + mean(piece_occ)) / 2.0

        a_light = mean(side_a_light) if side_a_light else 180.0
        b_light = mean(side_b_light) if side_b_light else 80.0
        light_threshold = (a_light + b_light) / 2.0
        side_a_is_brighter = a_light >= b_light

        side_centroids = {
            COLOR_SIDE_A: _vector_mean(side_vectors[COLOR_SIDE_A]) if side_vectors[COLOR_SIDE_A] else [0.6, 0.75, 0.25, 0.2],
            COLOR_SIDE_B: _vector_mean(side_vectors[COLOR_SIDE_B]) if side_vectors[COLOR_SIDE_B] else [0.6, 0.25, 0.25, -0.2],
        }

        return cls(
            occupied_threshold=occupied_threshold,
            light_threshold=light_threshold,
            side_a_is_brighter=side_a_is_brighter,
            side_centroids=side_centroids,
        )

    def predict(self, source: Union[Path, Image.Image]) -> Tuple[int, float]:
        image = _as_image(source)
        occupancy, lightness = extract_color_signals(image)
        occ_margin = occupancy - self.occupied_threshold
        if occupancy < self.occupied_threshold:
            confidence = max(0.01, min(0.99, 0.6 + abs(occ_margin) / 40.0))
            return COLOR_NONE, confidence

        vector = self._side_feature_vector(image)
        dist_a = _vector_distance(vector, self.side_centroids[COLOR_SIDE_A])
        dist_b = _vector_distance(vector, self.side_centroids[COLOR_SIDE_B])
        centroid_label = COLOR_SIDE_A if dist_a <= dist_b else COLOR_SIDE_B

        if self.side_a_is_brighter:
            threshold_label = COLOR_SIDE_A if lightness >= self.light_threshold else COLOR_SIDE_B
        else:
            threshold_label = COLOR_SIDE_A if lightness < self.light_threshold else COLOR_SIDE_B

        if centroid_label == threshold_label:
            label = centroid_label
            confidence = 0.65 + min(0.34, abs(dist_a - dist_b))
        else:
            # If side models disagree, prefer the larger margin source.
            light_margin = abs(lightness - self.light_threshold) / 255.0
            centroid_margin = abs(dist_a - dist_b)
            if centroid_margin >= light_margin:
                label = centroid_label
                confidence = 0.55 + min(0.3, centroid_margin)
            else:
                label = threshold_label
                confidence = 0.55 + min(0.3, light_margin)
        return label, max(0.01, min(0.99, confidence))

    def to_dict(self) -> Dict[str, object]:
        return {
            "kind": "color",
            "model_version": self.model_version,
            "occupied_threshold": self.occupied_threshold,
            "light_threshold": self.light_threshold,
            "side_a_is_brighter": self.side_a_is_brighter,
            "side_centroids": self.side_centroids,
        }

    @classmethod
    def from_dict(cls, payload: Mapping[str, object]) -> "ColorClassifier":
        # Backward compatibility with centroid-format payloads.
        if "side_centroids" not in payload:
            if "centroids" in payload:
                raw = payload["centroids"]
                assert isinstance(raw, MutableMapping)
                side_centroids = {
                    COLOR_SIDE_A: list(raw.get(COLOR_SIDE_A, [0.6, 0.75, 0.25, 0.2])),  # type: ignore[arg-type]
                    COLOR_SIDE_B: list(raw.get(COLOR_SIDE_B, [0.6, 0.25, 0.25, -0.2])),  # type: ignore[arg-type]
                }
                return cls(
                    occupied_threshold=float(payload.get("occupied_threshold", 0.2)) * 100.0,
                    light_threshold=float(payload.get("light_threshold", 128.0)),
                    side_a_is_brighter=bool(payload.get("side_a_is_brighter", True)),
                    side_centroids=side_centroids,
                    model_version="color_hybrid_v3_compat",
                )
            occupied = float(payload["occupied_threshold"])
            light = float(payload["light_threshold"])
            side_a_is_brighter = bool(payload["side_a_is_brighter"])
            return cls(
                occupied_threshold=occupied,
                light_threshold=light,
                side_a_is_brighter=side_a_is_brighter,
                side_centroids={
                    COLOR_SIDE_A: [0.6, light / 255.0, 0.25, 0.2],
                    COLOR_SIDE_B: [0.6, 1.0 - (light / 255.0), 0.25, -0.2],
                },
                model_version="color_hybrid_v3_compat",
            )

        raw_side = payload["side_centroids"]
        assert isinstance(raw_side, MutableMapping)
        return cls(
            occupied_threshold=float(payload["occupied_threshold"]),
            light_threshold=float(payload["light_threshold"]),
            side_a_is_brighter=bool(payload["side_a_is_brighter"]),
            side_centroids={int(key): list(value) for key, value in raw_side.items()},  # type: ignore[misc]
            model_version=str(payload.get("model_version", "color_hybrid_v3")),
        )


@dataclass
class TypeClassifier:
    labels: List[str]
    prototypes: Dict[str, List[float]]
    none_occupancy_threshold: Optional[float] = None
    model_version: str = "type_prototype_v1"

    @classmethod
    def train(cls, samples: Iterable[SquareSample], include_none: bool = True) -> "TypeClassifier":
        grouped: Dict[str, List[List[float]]] = {}
        none_occ: List[float] = []
        piece_occ: List[float] = []
        for sample in samples:
            label = sample.piece_type
            if not include_none and label == TYPE_NONE:
                continue
            image = load_grayscale(sample.image_path, size=(64, 64))
            occupancy, _ = extract_color_signals(image)
            if label == TYPE_NONE:
                none_occ.append(occupancy)
            else:
                piece_occ.append(occupancy)
            vector = extract_type_vector(image, size=16)
            grouped.setdefault(label, []).append(vector)

        if not grouped:
            raise ValueError("TypeClassifier received zero training samples.")

        labels = [label for label in TYPE_LABELS if label in grouped]
        prototypes = {label: _vector_mean(grouped[label]) for label in labels}
        threshold: Optional[float] = None
        if TYPE_NONE in labels and none_occ and piece_occ:
            none_q = _quantile(none_occ, 0.95)
            piece_q = _quantile(piece_occ, 0.05)
            if piece_q > none_q:
                threshold = (none_q + piece_q) / 2.0
            else:
                threshold = (mean(none_occ) + mean(piece_occ)) / 2.0
        return cls(labels=labels, prototypes=prototypes, none_occupancy_threshold=threshold)

    def predict(self, source: Union[Path, Image.Image]) -> Tuple[str, float]:
        image = _as_image(source)
        occupancy, _ = extract_color_signals(image)
        candidate_labels = list(self.labels)
        if TYPE_NONE in self.labels and self.none_occupancy_threshold is not None:
            if occupancy < self.none_occupancy_threshold:
                margin = self.none_occupancy_threshold - occupancy
                confidence = 0.6 + min(0.35, margin / 40.0)
                return TYPE_NONE, confidence
            candidate_labels = [label for label in self.labels if label != TYPE_NONE]
            if not candidate_labels:
                return TYPE_NONE, 1.0

        vector = extract_type_vector(image, size=16)
        best_label = None
        best_distance = None
        for label in candidate_labels:
            prototype = self.prototypes[label]
            distance = _vector_distance(vector, prototype)
            if best_distance is None or distance < best_distance:
                best_label = label
                best_distance = distance
        assert best_label is not None
        confidence = 1.0 / (1.0 + (best_distance or 0.0))
        return best_label, confidence

    def to_dict(self) -> Dict[str, object]:
        return {
            "kind": "type",
            "model_version": self.model_version,
            "labels": self.labels,
            "prototypes": self.prototypes,
            "none_occupancy_threshold": self.none_occupancy_threshold,
        }

    @classmethod
    def from_dict(cls, payload: Mapping[str, object]) -> "TypeClassifier":
        raw_prototypes = payload["prototypes"]
        assert isinstance(raw_prototypes, MutableMapping)
        return cls(
            labels=list(payload["labels"]),  # type: ignore[arg-type]
            prototypes={key: list(value) for key, value in raw_prototypes.items()},  # type: ignore[misc]
            none_occupancy_threshold=(
                float(payload["none_occupancy_threshold"])
                if payload.get("none_occupancy_threshold") is not None
                else None
            ),
            model_version=str(payload.get("model_version", "type_prototype_v1")),
        )


@dataclass
class AxisClassifier:
    axis: str
    deterministic: bool
    centroids: Dict[int, List[float]]
    model_version: str = "axis_centroid_v3"

    @classmethod
    def train(cls, samples: Iterable[SquareSample], axis: str, deterministic: bool = False) -> "AxisClassifier":
        if axis not in ("x", "y"):
            raise ValueError("axis must be 'x' or 'y'")
        grouped: Dict[int, List[List[float]]] = {value: [] for value in range(1, 9)}
        for sample in samples:
            target = sample.x if axis == "x" else sample.y
            image = load_grayscale(sample.image_path, size=(64, 64))
            grouped[target].append(extract_axis_vector(image, size=14, axis=axis))

        if deterministic:
            return cls(axis=axis, deterministic=True, centroids={})

        missing = [label for label in range(1, 9) if not grouped[label]]
        if missing:
            raise ValueError(f"Cannot train {axis} classifier; missing labels: {missing}")
        centroids = {label: _vector_mean(vectors) for label, vectors in grouped.items()}
        return cls(axis=axis, deterministic=False, centroids=centroids)

    def predict(
        self,
        source: Union[Path, Image.Image],
        metadata: Optional[Mapping[str, int]] = None,
    ) -> Tuple[int, float]:
        if self.deterministic:
            if metadata is None or self.axis not in metadata:
                raise ValueError(
                    f"Deterministic {self.axis} classifier needs metadata with '{self.axis}' value."
                )
            return int(metadata[self.axis]), 1.0

        image = _as_image(source)
        vector = extract_axis_vector(image, size=14, axis=self.axis)
        best_label = None
        best_distance = None
        for label, centroid in self.centroids.items():
            distance = _vector_distance(vector, centroid)
            if best_distance is None or distance < best_distance:
                best_label = label
                best_distance = distance
        assert best_label is not None
        confidence = 1.0 / (1.0 + (best_distance or 0.0))
        return best_label, confidence

    def to_dict(self) -> Dict[str, object]:
        return {
            "kind": "axis",
            "axis": self.axis,
            "deterministic": self.deterministic,
            "model_version": self.model_version,
            "centroids": self.centroids,
        }

    @classmethod
    def from_dict(cls, payload: Mapping[str, object]) -> "AxisClassifier":
        raw_centroids = payload.get("centroids", {})
        assert isinstance(raw_centroids, MutableMapping)
        parsed: Dict[int, List[float]] = {}
        for key, value in raw_centroids.items():
            label = int(key)
            if isinstance(value, list):
                parsed[label] = [float(v) for v in value]
            else:
                parsed[label] = [float(value)]
        return cls(
            axis=str(payload["axis"]),
            deterministic=bool(payload["deterministic"]),
            centroids=parsed,
            model_version=str(payload.get("model_version", "axis_centroid_v3")),
        )


Classifier = Union[ColorClassifier, TypeClassifier, AxisClassifier]


def save_classifier(classifier: Classifier, path: Path) -> None:
    save_json(path, classifier.to_dict())


def load_classifier(path: Path) -> Classifier:
    payload = load_json(path)
    kind = payload.get("kind")
    if kind == "color":
        return ColorClassifier.from_dict(payload)
    if kind == "type":
        return TypeClassifier.from_dict(payload)
    if kind == "axis":
        return AxisClassifier.from_dict(payload)
    raise ValueError(f"Unsupported classifier kind: {kind!r}")
