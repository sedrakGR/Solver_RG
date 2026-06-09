#!/usr/bin/env python3
"""Visual test for the trained Siamese neighborhood detector.

Runs every possible square pair for a single board image, draws the results:
  - GREEN line  = model predicts NEIGHBOR (correct for true neighbors)
  - RED   line  = model predicts NEIGHBOR but they are NOT (false positive)
  - (non-neighbors that the model correctly rejects are not drawn)

Usage:
    python test_neighborhood.py                          # auto-pick a dataset image
    python test_neighborhood.py --image path/to/board.png
    python test_neighborhood.py --image path/to/board.png --model path/to/model.pt
    python test_neighborhood.py --all-pairs              # also draw all neighbor pairs (TP + FN)
"""

from __future__ import annotations

import argparse
import sys
from pathlib import Path
from typing import List, Tuple

from PIL import Image, ImageDraw, ImageFont
import torch
from torchvision import transforms

# ---------------------------------------------------------------------------
# Re-use constants from the main training script
# ---------------------------------------------------------------------------
sys.path.insert(0, str(Path(__file__).parent))
from chess_neighborhood_detector import (  # noqa: E402
    SiameseNet,
    MEAN,
    STD,
    _manhattan,
)

DEFAULT_MODEL    = Path("./artifacts/neighborhood_detector.pt")
DEFAULT_DATA_DIR = Path("./data/mohammedhemed/chess_yolo_data/images")
DEFAULT_OUTPUT   = Path("./test_output.png")
GRID_SIZE        = 8
IMG_SIZE         = 64   # must match training


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def load_model(model_path: Path, device: torch.device) -> SiameseNet:
    checkpoint = torch.load(model_path, map_location=device)
    arch       = checkpoint.get("arch", "resnet18")
    model      = SiameseNet(arch=arch, pretrained=False).to(device)
    key = "model_state_dict" if "model_state_dict" in checkpoint else "state_dict"
    model.load_state_dict(checkpoint[key])
    model.eval()
    return model


def preprocess(crop: Image.Image) -> torch.Tensor:
    tfm = transforms.Compose([
        transforms.Resize((IMG_SIZE, IMG_SIZE)),
        transforms.ToTensor(),
        transforms.Normalize(MEAN, STD),
    ])
    return tfm(crop)


def crop_square(board: Image.Image, row: int, col: int) -> Image.Image:
    w, h = board.size
    x1 = int(col / GRID_SIZE * w)
    y1 = int(row / GRID_SIZE * h)
    x2 = int((col + 1) / GRID_SIZE * w)
    y2 = int((row + 1) / GRID_SIZE * h)
    return board.crop((x1, y1, x2, y2))


def square_center(board: Image.Image, row: int, col: int) -> Tuple[int, int]:
    w, h = board.size
    cx = int((col + 0.5) / GRID_SIZE * w)
    cy = int((row + 0.5) / GRID_SIZE * h)
    return cx, cy


def predict_pairs(
    model: SiameseNet,
    board: Image.Image,
    device: torch.device,
    batch_size: int = 64,
) -> List[Tuple[int, int, int, int, int, int]]:
    """Return list of (r1,c1,r2,c2,label_gt,pred) for all unique upper-triangle pairs."""
    # Build all upper-triangle pairs
    squares = [(r, c) for r in range(GRID_SIZE) for c in range(GRID_SIZE)]
    pairs: List[Tuple[int, int, int, int]] = []
    for i, (r1, c1) in enumerate(squares):
        for r2, c2 in squares[i + 1:]:
            pairs.append((r1, c1, r2, c2))

    results = []
    with torch.no_grad():
        for start in range(0, len(pairs), batch_size):
            batch = pairs[start: start + batch_size]
            crops_a = torch.stack([preprocess(crop_square(board, r1, c1)) for r1, c1, _, _ in batch]).to(device)
            crops_b = torch.stack([preprocess(crop_square(board, r2, c2)) for _, _, r2, c2 in batch]).to(device)
            logits  = model(crops_a, crops_b)
            preds   = logits.argmax(dim=1).cpu().tolist()
            for (r1, c1, r2, c2), pred in zip(batch, preds):
                gt = 1 if _manhattan(r1, c1, r2, c2) == 1 else 0
                results.append((r1, c1, r2, c2, gt, pred))
    return results


def draw_grid(draw: ImageDraw.ImageDraw, board: Image.Image) -> None:
    w, h = board.size
    for i in range(1, GRID_SIZE):
        x = int(i / GRID_SIZE * w)
        draw.line([(x, 0), (x, h)], fill=(180, 180, 180), width=1)
    for i in range(1, GRID_SIZE):
        y = int(i / GRID_SIZE * h)
        draw.line([(0, y), (w, y)], fill=(180, 180, 180), width=1)


def draw_results(
    board: Image.Image,
    results: List[Tuple[int, int, int, int, int, int]],
    show_all_neighbors: bool,
) -> Image.Image:
    """Annotate the board with model predictions."""
    canvas = board.copy().convert("RGBA")
    overlay = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)

    draw_grid(draw, board)

    tp = fn = fp = 0

    for r1, c1, r2, c2, gt, pred in results:
        c1x, c1y = square_center(board, r1, c1)
        c2x, c2y = square_center(board, r2, c2)

        if gt == 1 and pred == 1:          # True positive: correct neighbor
            color = (0, 220, 60, 200)
            draw.line([(c1x, c1y), (c2x, c2y)], fill=color, width=3)
            tp += 1
        elif gt == 1 and pred == 0:        # False negative: missed neighbor
            if show_all_neighbors:
                color = (255, 165, 0, 180)  # orange = missed
                draw.line([(c1x, c1y), (c2x, c2y)], fill=color, width=2)
            fn += 1
        elif gt == 0 and pred == 1:        # False positive: wrongly called neighbor
            color = (220, 30, 30, 180)
            draw.line([(c1x, c1y), (c2x, c2y)], fill=color, width=2)
            fp += 1

    canvas = Image.alpha_composite(canvas, overlay).convert("RGB")

    # Add legend
    try:
        font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 14)
        small = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 12)
    except Exception:
        font = small = ImageFont.load_default()

    draw2 = ImageDraw.Draw(canvas)
    legend = [
        ((0, 200, 60),   f"TP (correct neighbor): {tp}"),
        ((220, 30, 30),  f"FP (wrong neighbor):   {fp}"),
    ]
    if show_all_neighbors:
        legend.append(((255, 165, 0), f"FN (missed neighbor):  {fn}"))

    total = tp + fn
    acc   = tp / total * 100 if total > 0 else 0.0
    legend.append((None, f"Neighbor recall: {acc:.1f}%  ({tp}/{total})"))

    pad, lh = 8, 20
    for i, (color, text) in enumerate(legend):
        y = pad + i * lh
        if color:
            draw2.rectangle([pad, y + 3, pad + 12, y + 13], fill=color)
            draw2.text((pad + 18, y), text, fill=(255, 255, 255), font=small)
        else:
            draw2.text((pad, y), text, fill=(240, 240, 60), font=small)

    return canvas


def find_default_image(data_dir: Path) -> Path:
    """Pick a reproducible test image from the val split."""
    val_dir = data_dir / "val"
    search_dir = val_dir if val_dir.exists() else data_dir
    exts = {".png", ".jpg", ".jpeg", ".bmp", ".webp"}
    imgs = sorted(p for p in search_dir.rglob("*") if p.suffix.lower() in exts)
    if not imgs:
        raise FileNotFoundError(f"No images found under {search_dir}")
    # Deterministic pick: sort and take index 0
    return imgs[0]


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------

def main() -> None:
    parser = argparse.ArgumentParser(description="Visual neighborhood test")
    parser.add_argument("--image",       default=None,                    help="Board image to test (default: first val image)")
    parser.add_argument("--model",       default=str(DEFAULT_MODEL),      help="Path to trained .pt checkpoint")
    parser.add_argument("--data-dir",    default=str(DEFAULT_DATA_DIR),   help="Dataset root (used to auto-pick default image)")
    parser.add_argument("--output",      default=str(DEFAULT_OUTPUT),     help="Where to save the annotated image")
    parser.add_argument("--device",      default="cpu",                   help="cpu / cuda / auto")
    parser.add_argument("--all-pairs",   action="store_true",             help="Also draw missed neighbors (FN) in orange")
    args = parser.parse_args()

    model_path = Path(args.model)
    if not model_path.exists():
        print(f"[ERROR] Model not found: {model_path}", file=sys.stderr)
        print("  Train first with:  bash run_training.sh", file=sys.stderr)
        sys.exit(1)

    # Resolve device
    if args.device == "auto":
        device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    elif args.device == "cuda" and not torch.cuda.is_available():
        print("[WARN] CUDA not available, falling back to CPU")
        device = torch.device("cpu")
    else:
        device = torch.device(args.device)

    # Resolve image
    if args.image:
        image_path = Path(args.image)
    else:
        data_dir = Path(args.data_dir)
        if not data_dir.exists():
            print(f"[ERROR] Data dir not found: {data_dir}", file=sys.stderr)
            print("  Provide --image or --data-dir pointing to the dataset.", file=sys.stderr)
            sys.exit(1)
        image_path = find_default_image(data_dir)
        print(f"Auto-selected image: {image_path}")

    if not image_path.exists():
        print(f"[ERROR] Image not found: {image_path}", file=sys.stderr)
        sys.exit(1)

    # Load
    print(f"Loading model:  {model_path}")
    print(f"Device:         {device}")
    model = load_model(model_path, device)

    board = Image.open(image_path).convert("RGB")
    print(f"Board size:     {board.size[0]}×{board.size[1]}")

    # Run inference on all pairs
    print("Running inference on all square pairs...")
    results = predict_pairs(model, board, device)

    tp = sum(1 for *_, gt, pred in results if gt == 1 and pred == 1)
    fp = sum(1 for *_, gt, pred in results if gt == 0 and pred == 1)
    fn = sum(1 for *_, gt, pred in results if gt == 1 and pred == 0)
    tn = sum(1 for *_, gt, pred in results if gt == 0 and pred == 0)
    total_pos = tp + fn
    total_neg = fp + tn

    print(f"\n{'':=<50}")
    print(f"  True  positives (correct neighbor): {tp:4d} / {total_pos}")
    print(f"  False negatives (missed neighbor):  {fn:4d} / {total_pos}")
    print(f"  False positives (wrong neighbor):   {fp:4d} / {total_neg}")
    print(f"  True  negatives (correct reject):   {tn:4d} / {total_neg}")
    print(f"  Neighbor recall:  {tp/total_pos*100:.1f}%")
    print(f"  Precision:        {tp/(tp+fp)*100:.1f}%" if (tp + fp) > 0 else "  Precision:  n/a")
    print(f"{'':=<50}\n")

    # Draw and save
    annotated = draw_results(board, results, show_all_neighbors=args.all_pairs)
    out_path  = Path(args.output)
    out_path.parent.mkdir(parents=True, exist_ok=True)
    annotated.save(out_path)
    print(f"Saved annotated image → {out_path}")


if __name__ == "__main__":
    main()
