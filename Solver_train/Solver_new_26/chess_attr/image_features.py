"""Image loading and feature extraction (Pillow-only)."""

from __future__ import annotations

from pathlib import Path
from typing import List, Sequence, Tuple

from PIL import Image, ImageEnhance, ImageFilter

from .utils import mean, std


def load_grayscale(path: Path, size: Tuple[int, int] | None = None) -> Image.Image:
    image = Image.open(path).convert("L")
    if size is not None:
        image = image.resize(size, Image.BILINEAR)
    return image


def image_to_matrix(image: Image.Image) -> List[List[int]]:
    width, height = image.size
    pixels = list(image.getdata())
    return [pixels[row * width : (row + 1) * width] for row in range(height)]


def flatten_matrix(matrix: Sequence[Sequence[float]]) -> List[float]:
    return [value for row in matrix for value in row]


def downsample_matrix(matrix: Sequence[Sequence[float]], out_w: int, out_h: int) -> List[List[float]]:
    in_h = len(matrix)
    in_w = len(matrix[0]) if in_h else 0
    if in_h == 0 or in_w == 0:
        return [[0.0 for _ in range(out_w)] for _ in range(out_h)]

    x_step = in_w / out_w
    y_step = in_h / out_h
    output: List[List[float]] = []
    for oy in range(out_h):
        row: List[float] = []
        y0 = int(oy * y_step)
        y1 = max(y0 + 1, int((oy + 1) * y_step))
        for ox in range(out_w):
            x0 = int(ox * x_step)
            x1 = max(x0 + 1, int((ox + 1) * x_step))
            bucket = [
                matrix[yy][xx]
                for yy in range(y0, min(y1, in_h))
                for xx in range(x0, min(x1, in_w))
            ]
            row.append(mean(bucket))
        output.append(row)
    return output


def _crop_pixels(matrix: Sequence[Sequence[int]], x0: int, y0: int, x1: int, y1: int) -> List[int]:
    pixels: List[int] = []
    for row in range(y0, y1):
        pixels.extend(matrix[row][x0:x1])
    return pixels


def edge_density(matrix: Sequence[Sequence[int]]) -> float:
    if not matrix or not matrix[0]:
        return 0.0
    height = len(matrix)
    width = len(matrix[0])
    diffs: List[float] = []
    for y in range(height):
        for x in range(width):
            if x + 1 < width:
                diffs.append(abs(matrix[y][x + 1] - matrix[y][x]))
            if y + 1 < height:
                diffs.append(abs(matrix[y + 1][x] - matrix[y][x]))
    return mean(diffs) if diffs else 0.0


def extract_color_signals(image: Image.Image) -> Tuple[float, float]:
    """Return (occupancy_signal, lightness_signal)."""
    blurred = image.filter(ImageFilter.GaussianBlur(radius=0.8))
    matrix = image_to_matrix(blurred)
    width, height = blurred.size
    outer_margin_x = max(4, width // 10)
    outer_margin_y = max(4, height // 10)
    inner_x0 = outer_margin_x
    inner_y0 = outer_margin_y
    inner_x1 = width - outer_margin_x
    inner_y1 = height - outer_margin_y

    center_margin_x = width // 3
    center_margin_y = height // 3
    center_x0 = center_margin_x
    center_y0 = center_margin_y
    center_x1 = width - center_margin_x
    center_y1 = height - center_margin_y

    center = _crop_pixels(matrix, center_x0, center_y0, center_x1, center_y1)
    ring: List[int] = []
    for row in range(inner_y0, inner_y1):
        for col in range(inner_x0, inner_x1):
            inside_center = center_x0 <= col < center_x1 and center_y0 <= row < center_y1
            if not inside_center:
                ring.append(matrix[row][col])

    center_mean = mean(center)
    ring_mean = mean(ring)
    center_std = std(center)
    occupancy_signal = 0.85 * abs(center_mean - ring_mean) + 1.15 * center_std
    lightness_signal = center_mean
    return occupancy_signal, lightness_signal


def extract_type_vector(image: Image.Image, size: int = 16) -> List[float]:
    matrix = image_to_matrix(image.filter(ImageFilter.GaussianBlur(radius=0.7)))
    height = len(matrix)
    width = len(matrix[0]) if height else 0

    if width == 0 or height == 0:
        return [0.0] * (size * size * 2 + 3)

    trim_x = max(3, width // 10)
    trim_y = max(3, height // 10)
    x0 = trim_x
    y0 = trim_y
    x1 = max(x0 + 1, width - trim_x)
    y1 = max(y0 + 1, height - trim_y)
    core = [row[x0:x1] for row in matrix[y0:y1]]

    core_h = len(core)
    core_w = len(core[0]) if core_h else 0
    if core_w == 0 or core_h == 0:
        return [0.0] * (size * size * 2 + 3)

    board_estimate = mean(
        [
            core[0][0],
            core[0][-1],
            core[-1][0],
            core[-1][-1],
        ]
    )
    diff = [[abs(pixel - board_estimate) / 255.0 for pixel in row] for row in core]
    binary = [[1.0 if value > 0.12 else 0.0 for value in row] for row in diff]

    pixel_mass = 0.0
    pixel_cx_sum = 0.0
    pixel_cy_sum = 0.0
    for row_idx, row in enumerate(binary):
        for col_idx, value in enumerate(row):
            pixel_mass += value
            pixel_cx_sum += col_idx * value
            pixel_cy_sum += row_idx * value

    shift_x = 0
    shift_y = 0
    if pixel_mass > 1e-6:
        cx = pixel_cx_sum / pixel_mass
        cy = pixel_cy_sum / pixel_mass
        target_cx = (core_w - 1) / 2.0
        target_cy = (core_h - 1) / 2.0
        shift_x = int(round(target_cx - cx))
        shift_y = int(round(target_cy - cy))

    if shift_x != 0 or shift_y != 0:
        shifted_diff = [[0.0 for _ in range(core_w)] for _ in range(core_h)]
        shifted_bin = [[0.0 for _ in range(core_w)] for _ in range(core_h)]
        for row_idx in range(core_h):
            for col_idx in range(core_w):
                src_x = col_idx - shift_x
                src_y = row_idx - shift_y
                if 0 <= src_x < core_w and 0 <= src_y < core_h:
                    shifted_diff[row_idx][col_idx] = diff[src_y][src_x]
                    shifted_bin[row_idx][col_idx] = binary[src_y][src_x]
        diff = shifted_diff
        binary = shifted_bin

    diff_small = downsample_matrix(diff, size, size)
    bin_small = downsample_matrix(binary, size, size)
    diff_flat = flatten_matrix(diff_small)
    bin_flat = flatten_matrix(bin_small)

    def downsample_1d(values: Sequence[float], out_size: int) -> List[float]:
        if not values:
            return [0.0] * out_size
        step = len(values) / out_size
        result: List[float] = []
        for out_idx in range(out_size):
            start = int(out_idx * step)
            end = max(start + 1, int((out_idx + 1) * step))
            bucket = values[start:min(end, len(values))]
            result.append(mean(bucket) if bucket else 0.0)
        return result

    row_profile = [mean(row) for row in binary]
    col_profile = [mean([binary[row_idx][col_idx] for row_idx in range(core_h)]) for col_idx in range(core_w)]
    row_small = downsample_1d(row_profile, size)
    col_small = downsample_1d(col_profile, size)

    mass = sum(bin_flat)
    occupancy = mass / max(1.0, len(bin_flat))
    if mass <= 1e-6:
        centroid_x = 0.5
        centroid_y = 0.5
    else:
        cx_sum = 0.0
        cy_sum = 0.0
        for row_idx, row in enumerate(bin_small):
            for col_idx, value in enumerate(row):
                cx_sum += col_idx * value
                cy_sum += row_idx * value
        centroid_x = cx_sum / (mass * max(1.0, size - 1))
        centroid_y = cy_sum / (mass * max(1.0, size - 1))

    return bin_flat + diff_flat + row_small + col_small + [occupancy, centroid_x, centroid_y]


def extract_axis_vector(image: Image.Image, size: int = 14, axis: str | None = None) -> List[float]:
    matrix = image_to_matrix(image.filter(ImageFilter.GaussianBlur(radius=0.4)))
    height = len(matrix)
    width = len(matrix[0]) if height else 0
    if width == 0 or height == 0:
        if axis == "x" or axis == "y":
            return [0.0] * (size + 1)
        return [0.0] * (size * 4 + 4)

    x_strip = max(2, width // 12)
    y_strip = max(2, height // 12)

    def downsample_1d(values: Sequence[float], out_size: int) -> List[float]:
        if not values:
            return [0.0] * out_size
        step = len(values) / out_size
        result: List[float] = []
        for out_idx in range(out_size):
            start = int(out_idx * step)
            end = max(start + 1, int((out_idx + 1) * step))
            bucket = values[start:min(end, len(values))]
            result.append(mean(bucket) if bucket else 0.0)
        return result

    top_profile = [mean([matrix[row][col] for row in range(y_strip)]) / 255.0 for col in range(width)]
    bottom_profile = [
        mean([matrix[row][col] for row in range(height - y_strip, height)]) / 255.0
        for col in range(width)
    ]
    left_profile = [mean([matrix[row][col] for col in range(x_strip)]) / 255.0 for row in range(height)]
    right_profile = [
        mean([matrix[row][col] for col in range(width - x_strip, width)]) / 255.0
        for row in range(height)
    ]

    global_top = mean(top_profile)
    global_bottom = mean(bottom_profile)
    global_left = mean(left_profile)
    global_right = mean(right_profile)

    top_small = downsample_1d(top_profile, size)
    bottom_small = downsample_1d(bottom_profile, size)
    left_small = downsample_1d(left_profile, size)
    right_small = downsample_1d(right_profile, size)

    if axis == "x":
        return top_small + [global_top]
    if axis == "y":
        return bottom_small + [global_bottom]

    flat = top_small + bottom_small + left_small + right_small
    flat.extend([global_top, global_bottom, global_left, global_right])
    return flat


def augment_camera_like(image: Image.Image, seed: int) -> Image.Image:
    """Lightweight deterministic augmentation useful for tests and training."""
    # Simple pseudo-random, deterministic transforms from a user-provided seed.
    local_seed = seed
    local_seed = (1103515245 * local_seed + 12345) & 0x7FFFFFFF
    blur_radius = (local_seed % 3) * 0.35
    local_seed = (1103515245 * local_seed + 12345) & 0x7FFFFFFF
    contrast = 0.85 + (local_seed % 40) / 100.0
    local_seed = (1103515245 * local_seed + 12345) & 0x7FFFFFFF
    brightness = 0.85 + (local_seed % 45) / 100.0

    out = image
    if blur_radius > 0:
        out = out.filter(ImageFilter.GaussianBlur(radius=blur_radius))
    out = ImageEnhance.Contrast(out).enhance(contrast)
    out = ImageEnhance.Brightness(out).enhance(brightness)
    return out
