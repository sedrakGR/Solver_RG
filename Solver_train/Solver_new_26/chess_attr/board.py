"""Board warping and 8x8 square crop extraction."""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Sequence, Tuple

from PIL import Image

from .utils import load_json

Point = Tuple[float, float]


@dataclass(frozen=True)
class SquareCrop:
    x: int
    y: int
    image: Image.Image
    path: Path | None = None


def _solve_linear(matrix: List[List[float]], targets: List[float]) -> List[float]:
    n = len(targets)
    augmented = [row[:] + [targets[row_index]] for row_index, row in enumerate(matrix)]

    for col in range(n):
        pivot = max(range(col, n), key=lambda r: abs(augmented[r][col]))
        if abs(augmented[pivot][col]) < 1e-12:
            raise ValueError("Cannot solve perspective transform (singular matrix).")
        augmented[col], augmented[pivot] = augmented[pivot], augmented[col]

        pivot_value = augmented[col][col]
        for c in range(col, n + 1):
            augmented[col][c] /= pivot_value

        for row in range(n):
            if row == col:
                continue
            factor = augmented[row][col]
            for c in range(col, n + 1):
                augmented[row][c] -= factor * augmented[col][c]

    return [augmented[row][n] for row in range(n)]


def perspective_coeffs(dst_points: Sequence[Point], src_points: Sequence[Point]) -> Tuple[float, ...]:
    if len(dst_points) != 4 or len(src_points) != 4:
        raise ValueError("Expected 4 destination points and 4 source points.")

    matrix: List[List[float]] = []
    targets: List[float] = []
    for (x, y), (u, v) in zip(dst_points, src_points):
        matrix.append([x, y, 1.0, 0.0, 0.0, 0.0, -u * x, -u * y])
        targets.append(u)
        matrix.append([0.0, 0.0, 0.0, x, y, 1.0, -v * x, -v * y])
        targets.append(v)

    solution = _solve_linear(matrix, targets)
    return tuple(solution)


def warp_board(image: Image.Image, corners: Sequence[Point], board_size: int = 512) -> Image.Image:
    dst = [
        (0.0, 0.0),
        (board_size - 1.0, 0.0),
        (board_size - 1.0, board_size - 1.0),
        (0.0, board_size - 1.0),
    ]
    coeffs = perspective_coeffs(dst, corners)
    return image.transform((board_size, board_size), Image.PERSPECTIVE, coeffs, Image.BICUBIC)


def split_board(board_image: Image.Image, rank_from_white: bool = False) -> List[SquareCrop]:
    width, height = board_image.size
    square_w = width // 8
    square_h = height // 8
    crops: List[SquareCrop] = []
    for row in range(8):
        for col in range(8):
            x0 = col * square_w
            y0 = row * square_h
            x1 = width if col == 7 else (col + 1) * square_w
            y1 = height if row == 7 else (row + 1) * square_h
            square = board_image.crop((x0, y0, x1, y1))
            x_value = col + 1
            y_value = 8 - row if rank_from_white else row + 1
            crops.append(SquareCrop(x=x_value, y=y_value, image=square))
    return crops


def export_board_squares(
    board_image: Image.Image,
    output_dir: Path,
    prefix: str = "sq",
    rank_from_white: bool = False,
) -> List[SquareCrop]:
    output_dir.mkdir(parents=True, exist_ok=True)
    squares = split_board(board_image=board_image, rank_from_white=rank_from_white)
    out: List[SquareCrop] = []
    for square in squares:
        filename = f"{prefix}_x{square.x}_y{square.y}.png"
        path = output_dir / filename
        square.image.save(path)
        out.append(SquareCrop(x=square.x, y=square.y, image=square.image, path=path))
    return out


def _parse_points(values: Iterable[Iterable[float]]) -> List[Point]:
    out: List[Point] = []
    for value in values:
        pair = list(value)
        if len(pair) != 2:
            raise ValueError(f"Invalid corner point: {value}")
        out.append((float(pair[0]), float(pair[1])))
    if len(out) != 4:
        raise ValueError("Expected exactly 4 corners.")
    return out


def load_corners(path: Path) -> List[Point]:
    payload = load_json(path)
    if isinstance(payload, list):
        return _parse_points(payload)
    if isinstance(payload, dict):
        if "corners" in payload:
            return _parse_points(payload["corners"])
        if all(key in payload for key in ("top_left", "top_right", "bottom_right", "bottom_left")):
            ordered = [
                payload["top_left"],
                payload["top_right"],
                payload["bottom_right"],
                payload["bottom_left"],
            ]
            return _parse_points(ordered)
    raise ValueError(
        "Unsupported corners format. Provide a JSON list of 4 points or "
        "{corners:[...]} / {top_left,top_right,bottom_right,bottom_left}."
    )


def write_corners(path: Path, corners: Sequence[Point]) -> None:
    from .utils import save_json

    save_json(path, {"corners": list(corners)})
