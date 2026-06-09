#!/usr/bin/env python3
"""Train a Siamese CNN to detect 4-connected neighbors on a chessboard.

Two squares (r1,c1) and (r2,c2) are 4-connected neighbors iff:
    |r1 - r2| + |c1 - c2| == 1  (Manhattan distance = 1)

Dataset format expected:
    data_dir/
        train/<board_image_1.png> ...   (board images fill the whole image)
        val/<board_image_1.png> ...     (optional; if missing, auto-split from train)

    OR a flat directory:
        data_dir/<board_image_1.png> ...  (script auto-splits train/val 80/20)

Pairs (two square crops + label) are generated on-the-fly from board images.
No manual annotation required.
"""

from __future__ import annotations

import argparse
import json
import random
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, List, Optional, Sequence, Tuple

from PIL import Image, ImageDraw, ImageFile

import torch
import torch.nn as nn
from torch.utils.data import DataLoader, Dataset
from torchvision import models, transforms

ImageFile.LOAD_TRUNCATED_IMAGES = True
if hasattr(torch.backends, "nnpack"):
    try:
        torch.backends.nnpack.enabled = False
    except Exception:
        pass

# ---------------------------------------------------------------------------
# Re-use constants and small helpers from chess_piece_classifier if available
# ---------------------------------------------------------------------------
try:
    from chess_piece_classifier import (  # type: ignore
        IMAGE_EXTENSIONS,
        MEAN,
        STD,
        set_seed,
        resolve_device,
        preprocess_pil,
    )
except ImportError:
    IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".bmp", ".webp", ".jfif"}
    MEAN = (0.485, 0.456, 0.406)
    STD = (0.229, 0.224, 0.225)

    def set_seed(seed: int) -> None:
        random.seed(seed)
        torch.manual_seed(seed)
        if torch.cuda.is_available():
            torch.cuda.manual_seed_all(seed)

    def resolve_device(device_arg: str) -> torch.device:
        if device_arg == "auto":
            return torch.device("cuda" if torch.cuda.is_available() else "cpu")
        if device_arg == "cuda" and not torch.cuda.is_available():
            raise RuntimeError("CUDA requested but no GPU is available.")
        return torch.device(device_arg)

    def preprocess_pil(image: Image.Image, img_size: int) -> torch.Tensor:
        tfm = transforms.Compose([
            transforms.Resize((img_size, img_size)),
            transforms.ToTensor(),
            transforms.Normalize(MEAN, STD),
        ])
        return tfm(image).unsqueeze(0)


# ---------------------------------------------------------------------------
# Data structures
# ---------------------------------------------------------------------------

@dataclass(frozen=True)
class SquarePair:
    board_path: Path
    row_a: int
    col_a: int
    row_b: int
    col_b: int
    label: int       # 1 = neighbor, 0 = not-neighbor
    grid_size: int = 8


# ---------------------------------------------------------------------------
# Pair generation
# ---------------------------------------------------------------------------

def _manhattan(r1: int, c1: int, r2: int, c2: int) -> int:
    return abs(r1 - r2) + abs(c1 - c2)


def generate_pairs(
    board_path: Path,
    grid_size: int,
    neg_ratio: float,
    rng: random.Random,
) -> List[SquarePair]:
    """Generate positive (neighbor) and negative (non-neighbor) square pairs.

    4-connectivity: two squares are neighbors iff Manhattan distance == 1.
    Pairs are generated as upper-triangle only (no duplicates).

    For an 8x8 grid: 7*8 + 8*7 = 112 positive pairs.
    """
    positives: List[SquarePair] = []
    for r in range(grid_size):
        for c in range(grid_size):
            # right neighbor
            if c + 1 < grid_size:
                positives.append(
                    SquarePair(board_path, r, c, r, c + 1, label=1, grid_size=grid_size)
                )
            # down neighbor
            if r + 1 < grid_size:
                positives.append(
                    SquarePair(board_path, r, c, r + 1, c, label=1, grid_size=grid_size)
                )

    # Collect all non-neighbor pairs (upper-triangle)
    all_squares = [(r, c) for r in range(grid_size) for c in range(grid_size)]
    neg_candidates: List[Tuple[int, int, int, int]] = []
    for i, (r1, c1) in enumerate(all_squares):
        for r2, c2 in all_squares[i + 1 :]:
            if _manhattan(r1, c1, r2, c2) > 1:
                neg_candidates.append((r1, c1, r2, c2))

    n_neg = int(round(len(positives) * neg_ratio))
    n_neg = min(n_neg, len(neg_candidates))
    chosen = rng.sample(neg_candidates, n_neg)
    negatives = [
        SquarePair(board_path, r1, c1, r2, c2, label=0, grid_size=grid_size)
        for r1, c1, r2, c2 in chosen
    ]

    pairs = positives + negatives
    rng.shuffle(pairs)
    return pairs


# ---------------------------------------------------------------------------
# Board image discovery and splitting (board-level, not pair-level)
# ---------------------------------------------------------------------------

def collect_board_images(data_dir: Path) -> List[Path]:
    return sorted(
        p for p in data_dir.rglob("*")
        if p.is_file() and p.suffix.lower() in IMAGE_EXTENSIONS
    )


def load_board_splits(
    data_dir: Path,
    val_ratio: float,
    seed: int,
) -> Tuple[List[Path], List[Path]]:
    """Return (train_boards, val_boards).

    If data_dir/train/ exists, use it. If data_dir/val/ also exists, use it.
    Otherwise split from train/ (or from data_dir directly) at the BOARD level.
    """
    train_dir = data_dir / "train"
    val_dir_candidates = [data_dir / "val", data_dir / "valid", data_dir / "validation"]
    val_dir = next((d for d in val_dir_candidates if d.exists()), None)

    if train_dir.exists():
        train_boards = collect_board_images(train_dir)
        if val_dir:
            val_boards = collect_board_images(val_dir)
        else:
            rng = random.Random(seed)
            shuffled = train_boards[:]
            rng.shuffle(shuffled)
            n_val = max(1, int(len(shuffled) * val_ratio))
            val_boards = shuffled[:n_val]
            train_boards = shuffled[n_val:]
    else:
        all_boards = collect_board_images(data_dir)
        if not all_boards:
            raise FileNotFoundError(
                f"No board images found under {data_dir}.\n"
                f"Supported extensions: {sorted(IMAGE_EXTENSIONS)}"
            )
        rng = random.Random(seed)
        shuffled = all_boards[:]
        rng.shuffle(shuffled)
        n_val = max(1, int(len(shuffled) * val_ratio))
        val_boards = shuffled[:n_val]
        train_boards = shuffled[n_val:]

    if not train_boards:
        raise RuntimeError("No training board images found.")
    if not val_boards:
        raise RuntimeError(
            "No validation board images found. Add more data or add a separate val/ directory."
        )
    return train_boards, val_boards


def boards_to_pairs(
    boards: List[Path],
    grid_size: int,
    neg_ratio: float,
    seed: int,
) -> List[SquarePair]:
    rng = random.Random(seed)
    all_pairs: List[SquarePair] = []
    for board_path in boards:
        all_pairs.extend(generate_pairs(board_path, grid_size, neg_ratio, rng))
    rng.shuffle(all_pairs)
    return all_pairs


# ---------------------------------------------------------------------------
# Dataset
# ---------------------------------------------------------------------------

class SquarePairDataset(Dataset):
    def __init__(
        self,
        pairs: Sequence[SquarePair],
        img_size: int,
        augment: bool,
    ) -> None:
        self.pairs = list(pairs)
        self.img_size = img_size

        if augment:
            self.transform = transforms.Compose([
                transforms.Resize((img_size, img_size)),
                transforms.RandomHorizontalFlip(p=0.5),
                transforms.RandomRotation(8),
                transforms.ColorJitter(
                    brightness=0.15, contrast=0.15, saturation=0.1, hue=0.01
                ),
                transforms.ToTensor(),
                transforms.Normalize(MEAN, STD),
            ])
        else:
            self.transform = transforms.Compose([
                transforms.Resize((img_size, img_size)),
                transforms.ToTensor(),
                transforms.Normalize(MEAN, STD),
            ])

    def __len__(self) -> int:
        return len(self.pairs)

    def __getitem__(self, index: int) -> Tuple[torch.Tensor, torch.Tensor, int]:
        # Try up to 5 alternative samples if the board image is corrupted.
        for attempt in range(5):
            idx = (index + attempt) % len(self.pairs)
            pair = self.pairs[idx]
            try:
                board = Image.open(pair.board_path).convert("RGB")
            except Exception:
                continue
            w, h = board.size
            crop_a = self._crop_square(board, pair.row_a, pair.col_a, pair.grid_size, w, h)
            crop_b = self._crop_square(board, pair.row_b, pair.col_b, pair.grid_size, w, h)
            tensor_a = self._apply_transform(crop_a, seed=idx)
            tensor_b = self._apply_transform(crop_b, seed=idx)
            return tensor_a, tensor_b, pair.label

        # Fallback: return black tensors with label 0 (should never happen in practice)
        blank = torch.zeros(3, self.img_size, self.img_size)
        return blank, blank, 0

    @staticmethod
    def _crop_square(
        board: Image.Image,
        row: int,
        col: int,
        grid_size: int,
        w: int,
        h: int,
    ) -> Image.Image:
        x1 = int(col / grid_size * w)
        y1 = int(row / grid_size * h)
        x2 = int((col + 1) / grid_size * w)
        y2 = int((row + 1) / grid_size * h)
        return board.crop((x1, y1, x2, y2))

    def _apply_transform(self, img: Image.Image, seed: int) -> torch.Tensor:
        # Seed both Python random and PyTorch RNG so torchvision augmentations
        # (RandomHorizontalFlip, RandomRotation, ColorJitter) are identical for
        # both crops of the same pair.
        random.seed(seed)
        torch.manual_seed(seed)
        return self.transform(img)


# ---------------------------------------------------------------------------
# Model
# ---------------------------------------------------------------------------

def build_backbone(arch: str, pretrained: bool) -> Tuple[nn.Module, int]:
    """Return (backbone, embed_dim). FC/classifier head is replaced with Identity."""
    if arch == "resnet18":
        weights = models.ResNet18_Weights.DEFAULT if pretrained else None
        net = models.resnet18(weights=weights)
        embed_dim = net.fc.in_features      # 512
        net.fc = nn.Identity()
        return net, embed_dim

    if arch == "efficientnet_b0":
        weights = models.EfficientNet_B0_Weights.DEFAULT if pretrained else None
        net = models.efficientnet_b0(weights=weights)
        embed_dim = net.classifier[1].in_features   # 1280
        net.classifier = nn.Identity()
        return net, embed_dim

    raise ValueError(f"Unsupported arch: {arch!r}. Choose resnet18 or efficientnet_b0.")


class RelationHead(nn.Module):
    """MLP that takes concatenated Siamese features and outputs 2 logits."""

    def __init__(self, embed_dim: int, dropout: float = 0.3) -> None:
        super().__init__()
        # Input: [fA, fB, |fA-fB|, fA*fB]  →  embed_dim * 4
        self.fc1 = nn.Linear(embed_dim * 4, 256)
        self.relu = nn.ReLU(inplace=True)
        self.dropout = nn.Dropout(p=dropout)
        self.fc2 = nn.Linear(256, 2)

    def forward(self, fa: torch.Tensor, fb: torch.Tensor) -> torch.Tensor:
        combined = torch.cat([fa, fb, (fa - fb).abs(), fa * fb], dim=1)
        return self.fc2(self.dropout(self.relu(self.fc1(combined))))


class SiameseNet(nn.Module):
    def __init__(self, arch: str = "resnet18", pretrained: bool = False) -> None:
        super().__init__()
        self.backbone, self.embed_dim = build_backbone(arch, pretrained)
        self.relation_head = RelationHead(self.embed_dim)

    def encode(self, x: torch.Tensor) -> torch.Tensor:
        return self.backbone(x)

    def forward(self, img_a: torch.Tensor, img_b: torch.Tensor) -> torch.Tensor:
        fa = self.encode(img_a)
        fb = self.encode(img_b)
        return self.relation_head(fa, fb)


# ---------------------------------------------------------------------------
# Training
# ---------------------------------------------------------------------------

def run_epoch_siamese(
    model: SiameseNet,
    loader: DataLoader,
    criterion: nn.Module,
    device: torch.device,
    optimizer: Optional[torch.optim.Optimizer] = None,
) -> Tuple[float, float]:
    training = optimizer is not None
    model.train(training)

    total_loss = 0.0
    total_correct = 0
    total_count = 0

    for img_a, img_b, labels in loader:
        img_a = img_a.to(device, non_blocking=True)
        img_b = img_b.to(device, non_blocking=True)
        labels = labels.to(device, non_blocking=True)

        if training:
            optimizer.zero_grad(set_to_none=True)

        with torch.set_grad_enabled(training):
            logits = model(img_a, img_b)
            loss = criterion(logits, labels)
            if training:
                loss.backward()
                optimizer.step()

        preds = logits.argmax(dim=1)
        total_correct += (preds == labels).sum().item()
        batch_size = labels.size(0)
        total_count += batch_size
        total_loss += loss.item() * batch_size

    avg_loss = total_loss / max(total_count, 1)
    avg_acc = total_correct / max(total_count, 1)
    return avg_loss, avg_acc


def _save_checkpoint(
    model: SiameseNet,
    args: argparse.Namespace,
    extra: Dict,
    path: Path,
) -> None:
    torch.save(
        {
            "arch": args.arch,
            "img_size": args.img_size,
            "neg_ratio": args.neg_ratio,
            "grid_size": args.grid_size,
            "state_dict": model.state_dict(),
            **extra,
        },
        path,
    )


def _prune_interval_checkpoints(output_dir: Path, keep_last: int) -> None:
    """Delete old interval checkpoints, keeping only the most recent `keep_last`."""
    ckpts = sorted(output_dir.glob("neighborhood_detector_epoch*.pt"))
    for old in ckpts[: max(0, len(ckpts) - keep_last)]:
        old.unlink(missing_ok=True)


def train_command(args: argparse.Namespace) -> None:
    set_seed(args.seed)
    device = resolve_device(args.device)
    data_dir = Path(args.data_dir).expanduser().resolve()
    output_dir = Path(args.output_dir).expanduser().resolve()
    output_dir.mkdir(parents=True, exist_ok=True)

    if not data_dir.exists():
        raise FileNotFoundError(f"Data directory not found: {data_dir}")

    print(f"Using device: {device}")
    print(f"Loading board images from: {data_dir}")

    train_boards, val_boards = load_board_splits(data_dir, args.val_ratio, args.seed)

    # Optionally cap boards to limit pair count (useful for CPU training)
    if args.max_boards > 0 and len(train_boards) > args.max_boards:
        rng = random.Random(args.seed)
        train_boards = rng.sample(train_boards, args.max_boards)
        print(f"  (capped to {args.max_boards} train boards via --max-boards)")
    max_val = max(1, args.max_boards // 5) if args.max_boards > 0 else 0
    if max_val > 0 and len(val_boards) > max_val:
        rng = random.Random(args.seed + 99)
        val_boards = rng.sample(val_boards, max_val)

    print(f"Train boards: {len(train_boards)}, Val boards: {len(val_boards)}")

    train_pairs = boards_to_pairs(train_boards, args.grid_size, args.neg_ratio, args.seed)
    val_pairs = boards_to_pairs(val_boards, args.grid_size, args.neg_ratio, args.seed + 1)
    print(f"Train pairs:  {len(train_pairs)}, Val pairs:  {len(val_pairs)}")

    train_ds = SquarePairDataset(train_pairs, args.img_size, augment=not args.no_augment)
    val_ds = SquarePairDataset(val_pairs, args.img_size, augment=False)

    train_loader = DataLoader(
        train_ds,
        batch_size=args.batch_size,
        shuffle=True,
        num_workers=args.num_workers,
        pin_memory=(device.type == "cuda"),
    )
    val_loader = DataLoader(
        val_ds,
        batch_size=args.batch_size,
        shuffle=False,
        num_workers=args.num_workers,
        pin_memory=(device.type == "cuda"),
    )

    model = SiameseNet(arch=args.arch, pretrained=args.pretrained).to(device)
    criterion = nn.CrossEntropyLoss()
    optimizer = torch.optim.AdamW(
        model.parameters(), lr=args.lr, weight_decay=args.weight_decay
    )
    scheduler = torch.optim.lr_scheduler.CosineAnnealingLR(optimizer, T_max=args.epochs)

    best_val_acc = -1.0
    history: List[Dict] = []
    best_ckpt_path = output_dir / "neighborhood_detector.pt"
    summary_path = output_dir / "checkpoints_summary.json"

    print(f"\nTraining Siamese neighborhood detector ({args.epochs} epochs)...")
    for epoch in range(1, args.epochs + 1):
        train_loss, train_acc = run_epoch_siamese(
            model, train_loader, criterion, device, optimizer
        )
        val_loss, val_acc = run_epoch_siamese(
            model, val_loader, criterion, device, None
        )
        scheduler.step()

        row: Dict = {
            "epoch": epoch,
            "train_loss": round(train_loss, 6),
            "train_acc": round(train_acc, 6),
            "val_loss": round(val_loss, 6),
            "val_acc": round(val_acc, 6),
        }
        history.append(row)

        print(
            f"epoch {epoch:02d}/{args.epochs} | "
            f"train loss {train_loss:.4f} acc {train_acc:.4f} | "
            f"val loss {val_loss:.4f} acc {val_acc:.4f}"
            + (" *" if val_acc > best_val_acc else "")
        )

        # Best checkpoint
        if val_acc > best_val_acc:
            best_val_acc = val_acc
            _save_checkpoint(model, args, {"best_val_acc": best_val_acc}, best_ckpt_path)

        # Periodic interval checkpoint
        if args.checkpoint_interval > 0 and epoch % args.checkpoint_interval == 0:
            interval_path = output_dir / f"neighborhood_detector_epoch{epoch:03d}.pt"
            _save_checkpoint(
                model,
                args,
                {"epoch": epoch, "val_acc": val_acc, "best_val_acc": best_val_acc},
                interval_path,
            )
            _prune_interval_checkpoints(output_dir, args.keep_last)
            print(f"  -> Interval checkpoint saved: {interval_path.name}")

        # Update running summary after every epoch
        with summary_path.open("w", encoding="utf-8") as fp:
            json.dump(history, fp, indent=2)

    print(f"\nTraining complete.")
    print(f"Best val acc:  {best_val_acc:.4f}")
    print(f"Best model:    {best_ckpt_path}")
    print(f"Full history:  {summary_path}")


# ---------------------------------------------------------------------------
# Checkpoint loading
# ---------------------------------------------------------------------------

def load_siamese_checkpoint(
    checkpoint_path: Path, device: torch.device
) -> Tuple[SiameseNet, int, int]:
    """Return (model, img_size, grid_size). Model is in eval mode."""
    ckpt = torch.load(checkpoint_path, map_location=device, weights_only=True)
    model = SiameseNet(arch=ckpt["arch"], pretrained=False)
    model.load_state_dict(ckpt["state_dict"])
    model.to(device).eval()
    return model, int(ckpt["img_size"]), int(ckpt["grid_size"])


# ---------------------------------------------------------------------------
# Inference: predict-pair
# ---------------------------------------------------------------------------

def predict_pair_command(args: argparse.Namespace) -> None:
    device = resolve_device(args.device)
    model, img_size, _ = load_siamese_checkpoint(
        Path(args.model).expanduser().resolve(), device
    )

    path_a = Path(args.image_a).expanduser().resolve()
    path_b = Path(args.image_b).expanduser().resolve()
    for p in (path_a, path_b):
        if not p.exists():
            raise FileNotFoundError(f"Image not found: {p}")

    img_a = Image.open(path_a).convert("RGB")
    img_b = Image.open(path_b).convert("RGB")
    t_a = preprocess_pil(img_a, img_size).to(device)
    t_b = preprocess_pil(img_b, img_size).to(device)

    with torch.no_grad():
        logits = model(t_a, t_b)
        probs = torch.softmax(logits, dim=1)[0].cpu().tolist()

    result = {
        "image_a": str(path_a),
        "image_b": str(path_b),
        "neighbor_prob": probs[1],
        "not_neighbor_prob": probs[0],
        "prediction": "neighbor" if probs[1] >= 0.5 else "not_neighbor",
    }

    if args.json_output:
        print(json.dumps(result, indent=2))
    else:
        print(
            f"Prediction: {result['prediction']} "
            f"(neighbor_prob={result['neighbor_prob']:.4f})"
        )


# ---------------------------------------------------------------------------
# Inference: predict-board
# ---------------------------------------------------------------------------

def predict_board_command(args: argparse.Namespace) -> None:
    device = resolve_device(args.device)
    model, img_size, grid_size = load_siamese_checkpoint(
        Path(args.model).expanduser().resolve(), device
    )

    board_path = Path(args.image).expanduser().resolve()
    if not board_path.exists():
        raise FileNotFoundError(f"Board image not found: {board_path}")

    board = Image.open(board_path).convert("RGB")
    w, h = board.size

    # All 4-connected pairs for the grid (upper-triangle)
    pair_coords: List[Tuple[int, int, int, int]] = []
    for r in range(grid_size):
        for c in range(grid_size):
            if c + 1 < grid_size:
                pair_coords.append((r, c, r, c + 1))
            if r + 1 < grid_size:
                pair_coords.append((r, c, r + 1, c))

    def crop_and_preprocess(row: int, col: int) -> torch.Tensor:
        x1 = int(col / grid_size * w)
        y1 = int(row / grid_size * h)
        x2 = int((col + 1) / grid_size * w)
        y2 = int((row + 1) / grid_size * h)
        crop = board.crop((x1, y1, x2, y2))
        return preprocess_pil(crop, img_size)

    a_tensors = torch.cat(
        [crop_and_preprocess(r, c) for r, c, _, _ in pair_coords], dim=0
    ).to(device)
    b_tensors = torch.cat(
        [crop_and_preprocess(r2, c2) for _, _, r2, c2 in pair_coords], dim=0
    ).to(device)

    with torch.no_grad():
        logits = model(a_tensors, b_tensors)
        probs = torch.softmax(logits, dim=1).cpu()

    predictions = []
    for i, (r1, c1, r2, c2) in enumerate(pair_coords):
        p_neighbor = float(probs[i, 1].item())
        predictions.append({
            "square_a": {"row": r1, "col": c1},
            "square_b": {"row": r2, "col": c2},
            "neighbor_prob": p_neighbor,
            "not_neighbor_prob": float(probs[i, 0].item()),
            "prediction": "neighbor" if p_neighbor >= 0.5 else "not_neighbor",
        })

    result: Dict = {
        "board_image": str(board_path),
        "grid_size": grid_size,
        "num_pairs": len(predictions),
        "predictions": predictions,
    }

    if args.output:
        draw = ImageDraw.Draw(board)
        sq_w = w / grid_size
        sq_h = h / grid_size
        for pred in predictions:
            r1, c1 = pred["square_a"]["row"], pred["square_a"]["col"]
            r2, c2 = pred["square_b"]["row"], pred["square_b"]["col"]
            cx_a = int((c1 + 0.5) * sq_w)
            cy_a = int((r1 + 0.5) * sq_h)
            cx_b = int((c2 + 0.5) * sq_w)
            cy_b = int((r2 + 0.5) * sq_h)
            color = (0, 200, 0) if pred["prediction"] == "neighbor" else (200, 0, 0)
            draw.line((cx_a, cy_a, cx_b, cy_b), fill=color, width=2)
        out_path = Path(args.output).expanduser().resolve()
        board.save(out_path)
        result["output_image"] = str(out_path)
        print(f"Annotated image saved: {out_path}")

    if args.json_output:
        out_json = Path(args.json_output).expanduser().resolve()
        with out_json.open("w", encoding="utf-8") as fp:
            json.dump(result, fp, indent=2)
        print(f"JSON saved: {out_json}")
    else:
        n_neighbor = sum(1 for p in predictions if p["prediction"] == "neighbor")
        print(f"Board: {board_path}")
        print(f"Total 4-connected pairs: {len(predictions)}")
        print(f"Predicted neighbor: {n_neighbor} / {len(predictions)}")


# ---------------------------------------------------------------------------
# Evaluation
# ---------------------------------------------------------------------------

def evaluate_command(args: argparse.Namespace) -> None:
    device = resolve_device(args.device)
    model, img_size, grid_size = load_siamese_checkpoint(
        Path(args.model).expanduser().resolve(), device
    )

    data_dir = Path(args.data_dir).expanduser().resolve()
    if not data_dir.exists():
        raise FileNotFoundError(f"Data directory not found: {data_dir}")

    if args.eval_dir:
        eval_dir = Path(args.eval_dir).expanduser().resolve()
    else:
        candidates = [data_dir / "test", data_dir / "val", data_dir / "valid"]
        eval_dir = next((d for d in candidates if d.exists()), None)
        if eval_dir is None:
            raise FileNotFoundError(
                "No evaluation directory found. "
                "Provide --eval-dir or create data/test, data/val, or data/valid."
            )

    eval_boards = collect_board_images(eval_dir)
    if not eval_boards:
        raise RuntimeError(f"No board images found under {eval_dir}")

    rng = random.Random(getattr(args, "seed", 42))
    eval_pairs = []
    for bp in eval_boards:
        eval_pairs.extend(generate_pairs(bp, grid_size, neg_ratio=1.0, rng=rng))

    eval_ds = SquarePairDataset(eval_pairs, img_size, augment=False)
    eval_loader = DataLoader(
        eval_ds,
        batch_size=args.batch_size,
        shuffle=False,
        num_workers=args.num_workers,
        pin_memory=(device.type == "cuda"),
    )

    all_preds: List[int] = []
    all_labels: List[int] = []
    model.eval()
    with torch.no_grad():
        for img_a, img_b, labels in eval_loader:
            logits = model(img_a.to(device), img_b.to(device))
            preds = logits.argmax(dim=1).cpu()
            all_preds.extend(preds.tolist())
            all_labels.extend(labels.tolist())

    TP = sum(1 for p, l in zip(all_preds, all_labels) if p == 1 and l == 1)
    FP = sum(1 for p, l in zip(all_preds, all_labels) if p == 1 and l == 0)
    FN = sum(1 for p, l in zip(all_preds, all_labels) if p == 0 and l == 1)
    TN = sum(1 for p, l in zip(all_preds, all_labels) if p == 0 and l == 0)

    accuracy = (TP + TN) / max(len(all_labels), 1)
    precision = TP / max(TP + FP, 1)
    recall = TP / max(TP + FN, 1)
    f1 = 2 * precision * recall / max(precision + recall, 1e-9)

    result = {
        "eval_dir": str(eval_dir),
        "num_boards": len(eval_boards),
        "num_pairs": len(all_labels),
        "accuracy": accuracy,
        "precision": precision,
        "recall": recall,
        "f1": f1,
        "confusion_matrix": [[TN, FP], [FN, TP]],
    }

    if args.json_output:
        out_path = Path(args.json_output).expanduser().resolve()
        with out_path.open("w", encoding="utf-8") as fp:
            json.dump(result, fp, indent=2)
        print(f"Saved metrics to: {out_path}")

    print(f"Eval dir:  {eval_dir}")
    print(f"Boards:    {len(eval_boards)}, Pairs: {len(all_labels)}")
    print(f"Accuracy:  {accuracy:.4f}")
    print(f"Precision: {precision:.4f}")
    print(f"Recall:    {recall:.4f}")
    print(f"F1:        {f1:.4f}")
    print(f"Confusion matrix [[TN,FP],[FN,TP]]: {result['confusion_matrix']}")


# ---------------------------------------------------------------------------
# Dataset download helper
# ---------------------------------------------------------------------------

def download_data_command(args: argparse.Namespace) -> None:
    output_dir = Path(args.output_dir).expanduser().resolve()
    output_dir.mkdir(parents=True, exist_ok=True)

    if args.dataset == "mohammedhemed":
        _download_mohammedhemed(output_dir)
    elif args.dataset == "synthetic-kaggle":
        _print_kaggle_instructions(output_dir)


def _download_mohammedhemed(output_dir: Path) -> None:
    try:
        from huggingface_hub import snapshot_download  # type: ignore
    except ImportError:
        print(
            "huggingface_hub is not installed.\n"
            "Install it with:  pip install huggingface_hub\n"
            "Then re-run this command.",
            file=sys.stderr,
        )
        sys.exit(1)

    repo_id = "MohammedHemed/Chessboard-digital-images_with_fen"
    dest = str(output_dir / "mohammedhemed")
    print(f"Downloading {repo_id} from Hugging Face Hub...")
    local_dir = snapshot_download(repo_id=repo_id, repo_type="dataset", local_dir=dest)
    print(f"Downloaded to: {local_dir}")
    print(
        "\nDataset layout note: images are full chessboard renders.\n"
        "Pass the folder containing those images as --data-dir when training, e.g.:\n"
        f"  python chess_neighborhood_detector.py train --data-dir {dest} "
        "--output-dir ./artifacts --device cpu"
    )


def _print_kaggle_instructions(output_dir: Path) -> None:
    print(
        "To download a synthetic Kaggle chessboard dataset:\n"
        "\n"
        "1. Install the Kaggle CLI:  pip install kaggle\n"
        "2. Save your API key to ~/.kaggle/kaggle.json\n"
        "   (Get it at https://www.kaggle.com/settings → API)\n"
        "3. Download a dataset, e.g.:\n"
        f"   kaggle datasets download -d koryakinp/chess-positions -p {output_dir} --unzip\n"
        "\n"
        "Other suggestions:\n"
        "  - Search Kaggle for 'chess board images' for rendered board datasets.\n"
        "\n"
        "After downloading, point --data-dir at the folder of board images."
    )


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------

def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description=(
            "Siamese CNN for chess square 4-connected neighborhood detection.\n"
            "Two squares are neighbors iff |row_diff| + |col_diff| == 1."
        )
    )
    sub = parser.add_subparsers(dest="command", required=True)

    # ---- train ----
    p_train = sub.add_parser("train", help="Train the Siamese neighborhood model.")
    p_train.add_argument("--data-dir", required=True, help="Directory of board images.")
    p_train.add_argument(
        "--output-dir", default="artifacts", help="Directory for checkpoints and logs."
    )
    p_train.add_argument("--epochs", type=int, default=15, help="Number of epochs.")
    p_train.add_argument("--batch-size", type=int, default=32, help="Batch size.")
    p_train.add_argument(
        "--img-size", type=int, default=64, help="Square crop size (px)."
    )
    p_train.add_argument("--lr", type=float, default=1e-3, help="Learning rate.")
    p_train.add_argument(
        "--weight-decay", type=float, default=1e-4, help="AdamW weight decay."
    )
    p_train.add_argument(
        "--val-ratio",
        type=float,
        default=0.2,
        help="Fraction of boards for validation when no val/ dir is present.",
    )
    p_train.add_argument("--seed", type=int, default=42, help="Random seed.")
    p_train.add_argument(
        "--arch",
        default="resnet18",
        choices=["resnet18", "efficientnet_b0"],
        help="Backbone architecture.",
    )
    p_train.add_argument(
        "--num-workers", type=int, default=0, help="DataLoader workers."
    )
    p_train.add_argument(
        "--pretrained",
        action="store_true",
        help="Use ImageNet pretrained backbone weights (strongly recommended).",
    )
    p_train.add_argument(
        "--no-augment", action="store_true", help="Disable training augmentation."
    )
    p_train.add_argument(
        "--neg-ratio",
        type=float,
        default=1.0,
        help="Ratio of negative to positive pairs (default 1.0 = balanced).",
    )
    p_train.add_argument(
        "--grid-size",
        type=int,
        default=8,
        help="Board grid size (default 8 for standard chess).",
    )
    p_train.add_argument(
        "--max-boards",
        type=int,
        default=0,
        help=(
            "Cap the number of training board images (0 = use all). "
            "Useful for CPU runs: 500 boards → ~112k pairs, fast to train."
        ),
    )
    p_train.add_argument(
        "--checkpoint-interval",
        type=int,
        default=5,
        help="Save an interval checkpoint every N epochs (0 = disabled).",
    )
    p_train.add_argument(
        "--keep-last",
        type=int,
        default=3,
        help="Number of most-recent interval checkpoints to keep.",
    )
    p_train.add_argument(
        "--device",
        default="auto",
        choices=["auto", "cpu", "cuda"],
        help="Training device.",
    )
    p_train.set_defaults(func=train_command)

    # ---- predict-pair ----
    p_pair = sub.add_parser(
        "predict-pair", help="Predict if two square crop images are neighbors."
    )
    p_pair.add_argument("--image-a", required=True, help="Path to first square crop.")
    p_pair.add_argument("--image-b", required=True, help="Path to second square crop.")
    p_pair.add_argument("--model", required=True, help="Path to trained .pt checkpoint.")
    p_pair.add_argument(
        "--device", default="auto", choices=["auto", "cpu", "cuda"]
    )
    p_pair.add_argument(
        "--json-output", action="store_true", help="Print result as JSON."
    )
    p_pair.set_defaults(func=predict_pair_command)

    # ---- predict-board ----
    p_board = sub.add_parser(
        "predict-board",
        help="Run neighborhood predictions for all 4-connected pairs on a board image.",
    )
    p_board.add_argument("--image", required=True, help="Path to full board image.")
    p_board.add_argument("--model", required=True, help="Path to trained .pt checkpoint.")
    p_board.add_argument(
        "--output",
        default=None,
        help="Save annotated board image to this path (green=neighbor, red=not-neighbor).",
    )
    p_board.add_argument(
        "--json-output",
        default=None,
        help="Save all pair predictions as JSON to this path.",
    )
    p_board.add_argument(
        "--device", default="auto", choices=["auto", "cpu", "cuda"]
    )
    p_board.set_defaults(func=predict_board_command)

    # ---- evaluate ----
    p_eval = sub.add_parser(
        "evaluate", help="Evaluate a trained model on a held-out board dataset."
    )
    p_eval.add_argument("--data-dir", required=True, help="Dataset root directory.")
    p_eval.add_argument(
        "--eval-dir",
        default=None,
        help="Evaluation directory. If omitted, tries data/test, data/val, data/valid.",
    )
    p_eval.add_argument("--model", required=True, help="Path to trained .pt checkpoint.")
    p_eval.add_argument("--batch-size", type=int, default=32)
    p_eval.add_argument("--num-workers", type=int, default=0)
    p_eval.add_argument(
        "--device", default="auto", choices=["auto", "cpu", "cuda"]
    )
    p_eval.add_argument(
        "--json-output", default=None, help="Save metrics JSON to this path."
    )
    p_eval.set_defaults(func=evaluate_command)

    # ---- download-data ----
    p_dl = sub.add_parser(
        "download-data", help="Download a sample chessboard image dataset."
    )
    p_dl.add_argument(
        "--dataset",
        required=True,
        choices=["mohammedhemed", "synthetic-kaggle"],
        help=(
            "mohammedhemed: downloads from Hugging Face Hub (requires huggingface_hub). "
            "synthetic-kaggle: prints Kaggle CLI instructions."
        ),
    )
    p_dl.add_argument("--output-dir", default="data", help="Destination directory.")
    p_dl.set_defaults(func=download_data_command)

    # ---- export-vocab ----
    # Relation classifier manifest — advertised to Solver_RG so the UI can list it,
    # but not directly attachable to a nucleus classifier (RuleIs is categorical).
    p_vocab = sub.add_parser(
        "export-vocab",
        help="Emit a manifest.json describing this (relational) classifier for Solver_RG.",
    )
    p_vocab.add_argument("--checkpoint", required=True,
        help="Path to the .pt checkpoint to export (e.g. artifacts/neighborhood_detector.pt).")
    p_vocab.add_argument("--output-dir", required=True,
        help="Output folder; typically Solver_RG/classifiers/<classifierId>/.")
    p_vocab.add_argument("--classifier-id", default=None,
        help="Stable id for this classifier. Defaults to the checkpoint filename stem.")
    p_vocab.add_argument("--display-name", default="Chess Board Neighborhood Detector",
        help="Human-facing label shown in the Solver_RG UI.")
    p_vocab.add_argument("--copy-checkpoint", action="store_true",
        help="Also copy the .pt file next to the manifest.")
    p_vocab.set_defaults(func=export_vocab_command)

    return parser


def export_vocab_command(args: argparse.Namespace) -> None:
    """Emit a relation-kind manifest.json for the neighborhood detector."""
    from datetime import datetime, timezone

    checkpoint_path = Path(args.checkpoint)
    if not checkpoint_path.is_file():
        raise FileNotFoundError(f"Checkpoint not found: {checkpoint_path}")
    checkpoint = torch.load(checkpoint_path, map_location="cpu")

    classifier_id = args.classifier_id or checkpoint_path.stem
    img_size = int(checkpoint.get("img_size", 64))

    manifest = {
        "schemaVersion": 1,
        "classifierId": classifier_id,
        "displayName": args.display_name,
        "kind": "relation",
        "classNames": ["not_neighbor", "neighbor"],
        "checkpointPath": checkpoint_path.name,
        "inputSpec": {
            "modality": "image-pair",
            "channels": 3,
            "height": img_size,
            "width": img_size,
            "normalization": "imagenet",
        },
        "trainingMetadata": {
            "framework": "pytorch",
            "arch": checkpoint.get("arch", "unknown"),
            "bestValAcc": (float(checkpoint.get("best_val_acc")) if checkpoint.get("best_val_acc") is not None else None),
            "producedBy": "Solver_train/chess_neighborhood_detector.py",
            "producedAt": datetime.now(timezone.utc).isoformat(),
        },
    }

    output_dir = Path(args.output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)
    output_path = output_dir / "manifest.json"
    with output_path.open("w", encoding="utf-8") as f:
        json.dump(manifest, f, indent=2)
        f.write("\n")

    if args.copy_checkpoint:
        import shutil
        shutil.copy2(checkpoint_path, output_dir / checkpoint_path.name)

    print(f"Wrote manifest: {output_path}")


def main() -> None:
    parser = build_parser()
    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
