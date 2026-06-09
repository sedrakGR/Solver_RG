#!/usr/bin/env python3
"""Train and run inference for chess piece type/color classifiers.

This script trains two image classifiers:
1) Piece type: pawn, knight, bishop, rook, queen, king
2) Piece color/side: white, black

Dataset format expected:
- Either:
  data_dir/
    train/<class_name>/*.jpg
    val/<class_name>/*.jpg   (optional; if missing, split from train)
- Or:
  data_dir/<class_name>/*.jpg (script will split into train/val)

class_name examples:
- wP, wN, wB, wR, wQ, wK
- bP, bN, bB, bR, bQ, bK
- white_pawn, black_queen, etc.
"""

from __future__ import annotations

import argparse
import json
import random
import re
from collections import Counter, defaultdict
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Sequence, Tuple

from PIL import Image, ImageDraw, ImageFile

import torch
import torch.nn as nn
from torch.utils.data import DataLoader, Dataset
from torchvision import models, transforms

ImageFile.LOAD_TRUNCATED_IMAGES = True
# Prevent repetitive NNPACK warnings on unsupported CPUs.
if hasattr(torch.backends, "nnpack"):
    try:
        torch.backends.nnpack.enabled = False
    except Exception:
        pass

IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".bmp", ".webp", ".jfif"}
TYPE_NAMES = ["pawn", "knight", "bishop", "rook", "queen", "king"]
COLOR_NAMES = ["white", "black"]
MEAN = (0.485, 0.456, 0.406)
STD = (0.229, 0.224, 0.225)


@dataclass(frozen=True)
class Sample:
    path: Path
    type_idx: int
    color_idx: int
    source_class: str


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


def piece_type_from_token(token: str) -> str:
    token = token.lower()
    if token in {"p", "pawn"} or "pawn" in token:
        return "pawn"
    if token in {"n", "kn", "knight", "horse"} or "knight" in token or "horse" in token:
        return "knight"
    if token in {"b", "bishop"} or "bishop" in token:
        return "bishop"
    if token in {"r", "rook", "castle"} or "rook" in token or "castle" in token:
        return "rook"
    if token in {"q", "queen"} or "queen" in token:
        return "queen"
    if token in {"k", "king"} or "king" in token:
        return "king"
    raise ValueError(f"Could not infer piece type from token '{token}'.")


def parse_class_name(class_name: str) -> Tuple[int, int]:
    """Parse class folder names like wP, bK, white_queen, blackbishop."""
    normalized = re.sub(r"[^a-z]", "", class_name.lower())
    if not normalized:
        raise ValueError(f"Invalid class name '{class_name}'.")

    color_name = None
    remainder = normalized

    if remainder.startswith("white"):
        color_name = "white"
        remainder = remainder[len("white") :]
    elif remainder.startswith("black"):
        color_name = "black"
        remainder = remainder[len("black") :]
    elif "white" in remainder and "black" not in remainder:
        color_name = "white"
        remainder = remainder.replace("white", "")
    elif "black" in remainder and "white" not in remainder:
        color_name = "black"
        remainder = remainder.replace("black", "")
    elif remainder.startswith("w"):
        color_name = "white"
        remainder = remainder[1:]
    elif remainder.startswith("b"):
        color_name = "black"
        remainder = remainder[1:]
    else:
        raise ValueError(
            f"Could not infer color from class name '{class_name}'. "
            "Expected prefix like w/b or white/black."
        )

    if not remainder:
        raise ValueError(f"Could not infer piece type from class name '{class_name}'.")

    type_name = piece_type_from_token(remainder)
    return TYPE_NAMES.index(type_name), COLOR_NAMES.index(color_name)


def is_image(path: Path) -> bool:
    return path.is_file() and path.suffix.lower() in IMAGE_EXTENSIONS


def collect_samples(split_dir: Path) -> List[Sample]:
    if not split_dir.exists():
        raise FileNotFoundError(f"Directory does not exist: {split_dir}")

    samples: List[Sample] = []
    invalid_classes: List[str] = []

    for class_dir in sorted(split_dir.iterdir()):
        if not class_dir.is_dir():
            continue
        if class_dir.name.lower() in {"train", "val", "valid", "validation", "dev", "test"}:
            continue
        try:
            type_idx, color_idx = parse_class_name(class_dir.name)
        except ValueError:
            invalid_classes.append(class_dir.name)
            continue

        for image_path in sorted(class_dir.rglob("*")):
            if not is_image(image_path):
                continue
            samples.append(
                Sample(
                    path=image_path,
                    type_idx=type_idx,
                    color_idx=color_idx,
                    source_class=class_dir.name,
                )
            )

    if invalid_classes:
        print(
            "Warning: skipping class folders that could not be parsed: "
            + ", ".join(invalid_classes)
        )
    return samples


def count_images_per_class(split_dir: Path) -> Dict[str, int]:
    if not split_dir.exists():
        return {}
    counts: Dict[str, int] = {}
    for class_dir in sorted(split_dir.iterdir()):
        if not class_dir.is_dir():
            continue
        counts[class_dir.name] = sum(
            1 for p in class_dir.rglob("*") if is_image(p)
        )
    return counts


def stratified_split(
    samples: Sequence[Sample],
    val_ratio: float,
    seed: int,
) -> Tuple[List[Sample], List[Sample]]:
    rng = random.Random(seed)
    by_joint_label: Dict[Tuple[int, int], List[Sample]] = defaultdict(list)
    for sample in samples:
        by_joint_label[(sample.type_idx, sample.color_idx)].append(sample)

    train_samples: List[Sample] = []
    val_samples: List[Sample] = []

    for _, group in by_joint_label.items():
        group = group[:]
        rng.shuffle(group)

        if len(group) <= 1:
            train_samples.extend(group)
            continue

        n_val = max(1, int(len(group) * val_ratio))
        n_val = min(n_val, len(group) - 1)
        val_samples.extend(group[:n_val])
        train_samples.extend(group[n_val:])

    rng.shuffle(train_samples)
    rng.shuffle(val_samples)
    return train_samples, val_samples


def load_dataset(data_dir: Path, val_ratio: float, seed: int) -> Tuple[List[Sample], List[Sample]]:
    train_dir = data_dir / "train"
    val_dir_candidates = [
        data_dir / "val",
        data_dir / "valid",
        data_dir / "validation",
        data_dir / "dev",
    ]
    val_dir = next((d for d in val_dir_candidates if d.exists()), None)

    if train_dir.exists():
        train_samples = collect_samples(train_dir)

        # If ./train exists but is empty, allow fallback to root-level class folders.
        if not train_samples:
            root_samples = collect_samples(data_dir)
            if root_samples:
                print(
                    "Info: 'train/' exists but has no images; "
                    "using root-level class folders in data_dir instead."
                )
                train_samples, val_samples = stratified_split(root_samples, val_ratio, seed)
                return train_samples, val_samples

        if val_dir:
            val_samples = collect_samples(val_dir)
        else:
            train_samples, val_samples = stratified_split(train_samples, val_ratio, seed)
    else:
        all_samples = collect_samples(data_dir)
        train_samples, val_samples = stratified_split(all_samples, val_ratio, seed)

    if not train_samples:
        if train_dir.exists():
            class_counts = count_images_per_class(train_dir)
        else:
            class_counts = count_images_per_class(data_dir)
        raise RuntimeError(
            "No training images found.\n"
            f"Looked under: {train_dir if train_dir.exists() else data_dir}\n"
            f"Supported extensions: {sorted(IMAGE_EXTENSIONS)}\n"
            f"Per-class image counts: {class_counts if class_counts else '{}'}"
        )
    if not val_samples:
        raise RuntimeError(
            "No validation images found. Add more data or provide separate train/val directories."
        )
    return train_samples, val_samples


def sample_stats(samples: Iterable[Sample]) -> Dict[str, Dict[str, int]]:
    type_counts = Counter(TYPE_NAMES[s.type_idx] for s in samples)
    color_counts = Counter(COLOR_NAMES[s.color_idx] for s in samples)
    return {
        "type_counts": dict(sorted(type_counts.items())),
        "color_counts": dict(sorted(color_counts.items())),
    }


class ChessPieceDataset(Dataset):
    def __init__(self, samples: Sequence[Sample], img_size: int, target: str, augment: bool) -> None:
        if target not in {"type", "color"}:
            raise ValueError(f"Unknown target: {target}")
        self.samples = list(samples)
        self.target = target

        if augment:
            self.transform = transforms.Compose(
                [
                    transforms.Resize((img_size, img_size)),
                    transforms.RandomHorizontalFlip(p=0.5),
                    transforms.RandomRotation(12),
                    transforms.ColorJitter(
                        brightness=0.2, contrast=0.2, saturation=0.2, hue=0.02
                    ),
                    transforms.ToTensor(),
                    transforms.Normalize(MEAN, STD),
                ]
            )
        else:
            self.transform = transforms.Compose(
                [
                    transforms.Resize((img_size, img_size)),
                    transforms.ToTensor(),
                    transforms.Normalize(MEAN, STD),
                ]
            )

    def __len__(self) -> int:
        return len(self.samples)

    def __getitem__(self, index: int) -> Tuple[torch.Tensor, int]:
        sample = self.samples[index]
        image = Image.open(sample.path).convert("RGB")
        x = self.transform(image)
        y = sample.type_idx if self.target == "type" else sample.color_idx
        return x, y


def build_model(arch: str, num_classes: int, pretrained: bool) -> nn.Module:
    if arch == "resnet18":
        weights = models.ResNet18_Weights.DEFAULT if pretrained else None
        model = models.resnet18(weights=weights)
        model.fc = nn.Linear(model.fc.in_features, num_classes)
        return model

    if arch == "efficientnet_b0":
        weights = models.EfficientNet_B0_Weights.DEFAULT if pretrained else None
        model = models.efficientnet_b0(weights=weights)
        in_features = model.classifier[1].in_features
        model.classifier[1] = nn.Linear(in_features, num_classes)
        return model

    raise ValueError(f"Unsupported architecture: {arch}")


def run_epoch(
    model: nn.Module,
    loader: DataLoader,
    criterion: nn.Module,
    device: torch.device,
    optimizer: torch.optim.Optimizer | None = None,
) -> Tuple[float, float]:
    training = optimizer is not None
    model.train(training)

    total_loss = 0.0
    total_correct = 0
    total_count = 0

    for images, labels in loader:
        images = images.to(device, non_blocking=True)
        labels = labels.to(device, non_blocking=True)

        if training:
            optimizer.zero_grad(set_to_none=True)

        with torch.set_grad_enabled(training):
            logits = model(images)
            loss = criterion(logits, labels)
            if training:
                loss.backward()
                optimizer.step()

        predictions = logits.argmax(dim=1)
        total_correct += (predictions == labels).sum().item()
        batch_size = labels.size(0)
        total_count += batch_size
        total_loss += loss.item() * batch_size

    avg_loss = total_loss / max(total_count, 1)
    avg_acc = total_correct / max(total_count, 1)
    return avg_loss, avg_acc


def train_single_classifier(
    target: str,
    train_samples: Sequence[Sample],
    val_samples: Sequence[Sample],
    args: argparse.Namespace,
    output_path: Path,
    device: torch.device,
) -> Dict[str, float]:
    class_names = TYPE_NAMES if target == "type" else COLOR_NAMES
    num_classes = len(class_names)

    train_dataset = ChessPieceDataset(
        train_samples,
        img_size=args.img_size,
        target=target,
        augment=not args.no_augment,
    )
    val_dataset = ChessPieceDataset(
        val_samples,
        img_size=args.img_size,
        target=target,
        augment=False,
    )

    train_loader = DataLoader(
        train_dataset,
        batch_size=args.batch_size,
        shuffle=True,
        num_workers=args.num_workers,
        pin_memory=(device.type == "cuda"),
    )
    val_loader = DataLoader(
        val_dataset,
        batch_size=args.batch_size,
        shuffle=False,
        num_workers=args.num_workers,
        pin_memory=(device.type == "cuda"),
    )

    model = build_model(args.arch, num_classes=num_classes, pretrained=args.pretrained).to(device)
    criterion = nn.CrossEntropyLoss()
    optimizer = torch.optim.AdamW(
        model.parameters(), lr=args.lr, weight_decay=args.weight_decay
    )
    scheduler = torch.optim.lr_scheduler.CosineAnnealingLR(optimizer, T_max=args.epochs)

    best_val_acc = -1.0
    history = []

    print(f"\nTraining {target} classifier ({num_classes} classes)...")
    for epoch in range(1, args.epochs + 1):
        train_loss, train_acc = run_epoch(
            model=model,
            loader=train_loader,
            criterion=criterion,
            device=device,
            optimizer=optimizer,
        )
        val_loss, val_acc = run_epoch(
            model=model,
            loader=val_loader,
            criterion=criterion,
            device=device,
            optimizer=None,
        )
        scheduler.step()

        epoch_stats = {
            "epoch": epoch,
            "train_loss": train_loss,
            "train_acc": train_acc,
            "val_loss": val_loss,
            "val_acc": val_acc,
        }
        history.append(epoch_stats)
        print(
            f"[{target}] epoch {epoch:02d}/{args.epochs} | "
            f"train loss {train_loss:.4f} acc {train_acc:.4f} | "
            f"val loss {val_loss:.4f} acc {val_acc:.4f}"
        )

        if val_acc > best_val_acc:
            best_val_acc = val_acc
            checkpoint = {
                "arch": args.arch,
                "target": target,
                "class_names": class_names,
                "img_size": args.img_size,
                "state_dict": model.state_dict(),
                "best_val_acc": best_val_acc,
            }
            torch.save(checkpoint, output_path)

    with output_path.with_suffix(".history.json").open("w", encoding="utf-8") as fp:
        json.dump(history, fp, indent=2)

    print(f"Saved best {target} checkpoint to: {output_path} (val_acc={best_val_acc:.4f})")
    return {"best_val_acc": best_val_acc}


def train_command(args: argparse.Namespace) -> None:
    set_seed(args.seed)
    data_dir = Path(args.data_dir).expanduser().resolve()
    output_dir = Path(args.output_dir).expanduser().resolve()
    output_dir.mkdir(parents=True, exist_ok=True)

    if not data_dir.exists():
        raise FileNotFoundError(
            "Dataset directory not found: "
            f"{data_dir}\n"
            "Create the dataset folder first and point --data-dir to it.\n"
            "Expected examples:\n"
            "  data/train/wP/*.png, data/train/bK/*.jpg (and optionally data/val/...)\n"
            "or\n"
            "  data/wP/*.png, data/bK/*.jpg (auto split train/val)"
        )

    device = resolve_device(args.device)
    print(f"Using device: {device}")
    print(f"Loading dataset from: {data_dir}")
    train_samples, val_samples = load_dataset(data_dir, args.val_ratio, args.seed)
    print(f"Train samples: {len(train_samples)}")
    print(f"Val samples:   {len(val_samples)}")

    train_stats = sample_stats(train_samples)
    val_stats = sample_stats(val_samples)
    print(f"Train type distribution:  {train_stats['type_counts']}")
    print(f"Train color distribution: {train_stats['color_counts']}")
    print(f"Val type distribution:    {val_stats['type_counts']}")
    print(f"Val color distribution:   {val_stats['color_counts']}")

    type_path = output_dir / "type_classifier.pt"
    color_path = output_dir / "color_classifier.pt"

    type_metrics = train_single_classifier(
        target="type",
        train_samples=train_samples,
        val_samples=val_samples,
        args=args,
        output_path=type_path,
        device=device,
    )
    color_metrics = train_single_classifier(
        target="color",
        train_samples=train_samples,
        val_samples=val_samples,
        args=args,
        output_path=color_path,
        device=device,
    )

    summary = {
        "data_dir": str(data_dir),
        "train_samples": len(train_samples),
        "val_samples": len(val_samples),
        "arch": args.arch,
        "img_size": args.img_size,
        "type_model": str(type_path),
        "color_model": str(color_path),
        "type_best_val_acc": type_metrics["best_val_acc"],
        "color_best_val_acc": color_metrics["best_val_acc"],
    }
    summary_path = output_dir / "training_summary.json"
    with summary_path.open("w", encoding="utf-8") as fp:
        json.dump(summary, fp, indent=2)

    print("\nTraining complete.")
    print(json.dumps(summary, indent=2))


def load_checkpoint_model(
    checkpoint_path: Path, device: torch.device
) -> Tuple[nn.Module, List[str], int, str]:
    checkpoint = torch.load(checkpoint_path, map_location=device)
    class_names = checkpoint["class_names"]
    arch = checkpoint["arch"]
    img_size = int(checkpoint["img_size"])
    target = checkpoint.get("target", "unknown")

    model = build_model(arch=arch, num_classes=len(class_names), pretrained=False)
    model.load_state_dict(checkpoint["state_dict"])
    model.to(device)
    model.eval()
    return model, class_names, img_size, target


def preprocess_for_inference(image_path: Path, img_size: int) -> torch.Tensor:
    image = Image.open(image_path).convert("RGB")
    return preprocess_pil(image, img_size)


def preprocess_pil(image: Image.Image, img_size: int) -> torch.Tensor:
    tfm = transforms.Compose(
        [
            transforms.Resize((img_size, img_size)),
            transforms.ToTensor(),
            transforms.Normalize(MEAN, STD),
        ]
    )
    return tfm(image).unsqueeze(0)


def predict_single(
    model: nn.Module,
    class_names: Sequence[str],
    image_tensor: torch.Tensor,
    device: torch.device,
    top_k: int,
) -> Dict[str, object]:
    with torch.no_grad():
        logits = model(image_tensor.to(device))
        probs = torch.softmax(logits, dim=1)[0].cpu()

    pred_idx = int(probs.argmax().item())
    pred_conf = float(probs[pred_idx].item())
    k = min(top_k, len(class_names))
    top_probs, top_indices = torch.topk(probs, k=k)
    top = [
        {"label": class_names[int(idx)], "confidence": float(prob)}
        for prob, idx in zip(top_probs.tolist(), top_indices.tolist())
    ]
    return {
        "label": class_names[pred_idx],
        "confidence": pred_conf,
        "top_k": top,
    }


def decode_probs_row(
    probs_row: torch.Tensor, class_names: Sequence[str], top_k: int
) -> Dict[str, object]:
    pred_idx = int(probs_row.argmax().item())
    pred_conf = float(probs_row[pred_idx].item())
    k = min(top_k, len(class_names))
    top_probs, top_indices = torch.topk(probs_row, k=k)
    top = [
        {"label": class_names[int(idx)], "confidence": float(prob)}
        for prob, idx in zip(top_probs.tolist(), top_indices.tolist())
    ]
    return {
        "label": class_names[pred_idx],
        "confidence": pred_conf,
        "top_k": top,
    }


def predict_command(args: argparse.Namespace) -> None:
    device = resolve_device(args.device)

    image_path = Path(args.image).expanduser().resolve()
    if not image_path.exists():
        raise FileNotFoundError(
            f"Image not found: {image_path}\n"
            "Pass a real image path with --image, for example:\n"
            "  python chess_piece_classifier.py predict --image ./data/train/wP/example.png "
            "--models-dir ./artifacts --device cpu"
        )

    models_dir = Path(args.models_dir).expanduser().resolve()
    type_model_path = (
        Path(args.type_model).expanduser().resolve()
        if args.type_model
        else models_dir / "type_classifier.pt"
    )
    color_model_path = (
        Path(args.color_model).expanduser().resolve()
        if args.color_model
        else models_dir / "color_classifier.pt"
    )

    if not type_model_path.exists():
        raise FileNotFoundError(f"Type model not found: {type_model_path}")
    if not color_model_path.exists():
        raise FileNotFoundError(f"Color model not found: {color_model_path}")

    type_model, type_class_names, type_img_size, _ = load_checkpoint_model(type_model_path, device)
    color_model, color_class_names, color_img_size, _ = load_checkpoint_model(
        color_model_path, device
    )

    type_tensor = preprocess_for_inference(image_path, type_img_size)
    color_tensor = preprocess_for_inference(image_path, color_img_size)

    type_pred = predict_single(
        model=type_model,
        class_names=type_class_names,
        image_tensor=type_tensor,
        device=device,
        top_k=args.top_k,
    )
    color_pred = predict_single(
        model=color_model,
        class_names=color_class_names,
        image_tensor=color_tensor,
        device=device,
        top_k=max(2, min(args.top_k, len(color_class_names))),
    )

    result = {
        "image": str(image_path),
        "type_prediction": type_pred,
        "color_prediction": color_pred,
    }

    if args.json_output:
        print(json.dumps(result, indent=2))
        return

    print(f"Image: {image_path}")
    print(
        f"Type:  {type_pred['label']} "
        f"(confidence={type_pred['confidence']:.4f})"
    )
    print(
        f"Color: {color_pred['label']} "
        f"(confidence={color_pred['confidence']:.4f})"
    )
    print("Top type candidates:")
    for row in type_pred["top_k"]:
        print(f"  - {row['label']}: {row['confidence']:.4f}")


def parse_regions_file(regions_path: Path) -> List[Dict[str, object]]:
    if not regions_path.exists():
        raise FileNotFoundError(f"Regions file not found: {regions_path}")

    suffix = regions_path.suffix.lower()
    text = regions_path.read_text(encoding="utf-8").strip()
    if not text:
        raise RuntimeError(f"Regions file is empty: {regions_path}")

    regions: List[Dict[str, object]] = []
    if suffix == ".json":
        payload = json.loads(text)
        if not isinstance(payload, list):
            raise RuntimeError("Regions JSON must be a list.")
        for i, row in enumerate(payload):
            if isinstance(row, dict):
                if not all(k in row for k in ("x1", "y1", "x2", "y2")):
                    raise RuntimeError(
                        f"Region #{i} dict must contain x1,y1,x2,y2 keys."
                    )
                regions.append(
                    {
                        "id": str(row.get("id", i)),
                        "x1": int(row["x1"]),
                        "y1": int(row["y1"]),
                        "x2": int(row["x2"]),
                        "y2": int(row["y2"]),
                    }
                )
            elif isinstance(row, (list, tuple)) and len(row) >= 4:
                regions.append(
                    {
                        "id": str(i),
                        "x1": int(row[0]),
                        "y1": int(row[1]),
                        "x2": int(row[2]),
                        "y2": int(row[3]),
                    }
                )
            else:
                raise RuntimeError(
                    f"Invalid region format at index {i}; expected dict or [x1,y1,x2,y2]."
                )
        return regions

    # CSV/TXT format per line: x1,y1,x2,y2 or x1,y1,x2,y2,id
    for i, raw_line in enumerate(text.splitlines()):
        line = raw_line.strip()
        if not line or line.startswith("#"):
            continue
        parts = [p.strip() for p in line.split(",")]
        if len(parts) < 4:
            raise RuntimeError(
                f"Invalid region line {i + 1}: '{line}'. Expected x1,y1,x2,y2[,id]"
            )
        region_id = parts[4] if len(parts) >= 5 else str(i)
        regions.append(
            {
                "id": region_id,
                "x1": int(float(parts[0])),
                "y1": int(float(parts[1])),
                "x2": int(float(parts[2])),
                "y2": int(float(parts[3])),
            }
        )

    if not regions:
        raise RuntimeError(f"No regions parsed from: {regions_path}")
    return regions


def clamp_box(
    x1: int, y1: int, x2: int, y2: int, width: int, height: int
) -> Tuple[int, int, int, int]:
    x1 = max(0, min(x1, width - 1))
    x2 = max(0, min(x2, width - 1))
    y1 = max(0, min(y1, height - 1))
    y2 = max(0, min(y2, height - 1))

    if x2 <= x1:
        x2 = min(width - 1, x1 + 1)
    if y2 <= y1:
        y2 = min(height - 1, y1 + 1)
    return x1, y1, x2, y2


def parse_board_box_arg(board_box_arg: str, width: int, height: int) -> Tuple[int, int, int, int]:
    parts = [p.strip() for p in board_box_arg.split(",")]
    if len(parts) != 4:
        raise RuntimeError(
            "Invalid --board-box format. Expected: x1,y1,x2,y2"
        )
    x1, y1, x2, y2 = [int(float(p)) for p in parts]
    return clamp_box(x1, y1, x2, y2, width, height)


def estimate_board_box(image: Image.Image, board_scale: float = 0.92) -> Tuple[int, int, int, int]:
    width, height = image.size
    side = int(min(width, height) * board_scale)
    side = max(8, side)
    x1 = (width - side) // 2
    y1 = (height - side) // 2
    x2 = x1 + side
    y2 = y1 + side
    return clamp_box(x1, y1, x2, y2, width, height)


def generate_grid_regions(
    board_box: Tuple[int, int, int, int], grid_size: int = 8
) -> List[Dict[str, object]]:
    x1, y1, x2, y2 = board_box
    board_w = x2 - x1
    board_h = y2 - y1

    regions: List[Dict[str, object]] = []
    for row in range(grid_size):
        for col in range(grid_size):
            cell_x1 = int(x1 + (col / grid_size) * board_w)
            cell_y1 = int(y1 + (row / grid_size) * board_h)
            cell_x2 = int(x1 + ((col + 1) / grid_size) * board_w)
            cell_y2 = int(y1 + ((row + 1) / grid_size) * board_h)

            # Chess-style id for default 8x8 grid (top-left = a8)
            if grid_size == 8:
                square_id = f"{chr(ord('a') + col)}{8 - row}"
            else:
                square_id = f"r{row}c{col}"

            regions.append(
                {
                    "id": square_id,
                    "x1": cell_x1,
                    "y1": cell_y1,
                    "x2": cell_x2,
                    "y2": cell_y2,
                }
            )
    return regions


def annotate_command(args: argparse.Namespace) -> None:
    device = resolve_device(args.device)

    image_path = Path(args.image).expanduser().resolve()
    if not image_path.exists():
        raise FileNotFoundError(f"Image not found: {image_path}")
    if args.grid_size <= 0:
        raise RuntimeError("--grid-size must be >= 1")
    if args.board_scale <= 0:
        raise RuntimeError("--board-scale must be > 0")

    models_dir = Path(args.models_dir).expanduser().resolve()
    type_model_path = (
        Path(args.type_model).expanduser().resolve()
        if args.type_model
        else models_dir / "type_classifier.pt"
    )
    color_model_path = (
        Path(args.color_model).expanduser().resolve()
        if args.color_model
        else models_dir / "color_classifier.pt"
    )

    if not type_model_path.exists():
        raise FileNotFoundError(f"Type model not found: {type_model_path}")
    if not color_model_path.exists():
        raise FileNotFoundError(f"Color model not found: {color_model_path}")

    type_model, type_class_names, type_img_size, _ = load_checkpoint_model(type_model_path, device)
    color_model, color_class_names, color_img_size, _ = load_checkpoint_model(
        color_model_path, device
    )

    image = Image.open(image_path).convert("RGB")
    width, height = image.size

    if args.regions:
        regions_path = Path(args.regions).expanduser().resolve()
        regions = parse_regions_file(regions_path)
        board_box = None
        region_source = f"regions file: {regions_path}"
    else:
        if args.board_box and args.board_box.lower() != "auto":
            board_box = parse_board_box_arg(args.board_box, width, height)
            region_source = f"auto-grid from --board-box={args.board_box}"
        else:
            board_box = estimate_board_box(image=image, board_scale=args.board_scale)
            region_source = (
                f"auto-grid from centered square (board_scale={args.board_scale:.2f})"
            )
        regions = generate_grid_regions(board_box=board_box, grid_size=args.grid_size)

    draw = ImageDraw.Draw(image)

    clamped_regions: List[Dict[str, object]] = []
    type_inputs: List[torch.Tensor] = []
    color_inputs: List[torch.Tensor] = []

    for region in regions:
        x1, y1, x2, y2 = clamp_box(
            int(region["x1"]),
            int(region["y1"]),
            int(region["x2"]),
            int(region["y2"]),
            width,
            height,
        )
        crop = image.crop((x1, y1, x2, y2))
        clamped_regions.append(
            {
                "id": region["id"],
                "x1": x1,
                "y1": y1,
                "x2": x2,
                "y2": y2,
            }
        )
        type_inputs.append(preprocess_pil(crop, type_img_size))
        color_inputs.append(preprocess_pil(crop, color_img_size))

    if not clamped_regions:
        raise RuntimeError("No regions to annotate.")

    type_batch = torch.cat(type_inputs, dim=0).to(device)
    color_batch = torch.cat(color_inputs, dim=0).to(device)

    with torch.no_grad():
        type_probs = torch.softmax(type_model(type_batch), dim=1).cpu()
        color_probs = torch.softmax(color_model(color_batch), dim=1).cpu()

    predictions: List[Dict[str, object]] = []
    for i, region in enumerate(clamped_regions):
        type_pred = decode_probs_row(
            probs_row=type_probs[i], class_names=type_class_names, top_k=args.top_k
        )
        color_pred = decode_probs_row(
            probs_row=color_probs[i],
            class_names=color_class_names,
            top_k=max(2, min(args.top_k, len(color_class_names))),
        )

        label = (
            f"{region['id']}: {color_pred['label']} {type_pred['label']} "
            f"| t={type_pred['confidence']:.2f} c={color_pred['confidence']:.2f}"
        )
        x1, y1, x2, y2 = region["x1"], region["y1"], region["x2"], region["y2"]
        draw.rectangle((x1, y1, x2, y2), outline=(0, 255, 0), width=2)
        text_y = max(0, y1 - 14)
        draw.rectangle((x1, text_y, min(width - 1, x1 + 360), y1), fill=(0, 255, 0))
        draw.text((x1 + 2, text_y + 1), label, fill=(0, 0, 0))

        predictions.append(
            {
                "index": i,
                "id": region["id"],
                "box": {"x1": x1, "y1": y1, "x2": x2, "y2": y2},
                "type": type_pred,
                "color": color_pred,
            }
        )

    output_path = (
        Path(args.output).expanduser().resolve()
        if args.output
        else image_path.with_name(f"{image_path.stem}_annotated{image_path.suffix}")
    )
    image.save(output_path)

    result = {
        "image": str(image_path),
        "output_image": str(output_path),
        "num_regions": len(predictions),
        "predictions": predictions,
    }

    if args.output_json:
        output_json = Path(args.output_json).expanduser().resolve()
        with output_json.open("w", encoding="utf-8") as fp:
            json.dump(result, fp, indent=2)

    if args.json_output:
        print(json.dumps(result, indent=2))
        return

    print(f"Input image:  {image_path}")
    print(f"Output image: {output_path}")
    print(f"Region source: {region_source}")
    if board_box is not None:
        print(f"Board box:    x1={board_box[0]}, y1={board_box[1]}, x2={board_box[2]}, y2={board_box[3]}")
    print(f"Regions:      {len(predictions)}")
    for row in predictions:
        print(
            f"- id={row['id']} box={row['box']} "
            f"type={row['type']['label']}({row['type']['confidence']:.4f}) "
            f"color={row['color']['label']}({row['color']['confidence']:.4f})"
        )
    if args.output_json:
        print(f"Saved JSON:   {Path(args.output_json).expanduser().resolve()}")


def evaluate_classifier(
    model: nn.Module,
    class_names: Sequence[str],
    samples: Sequence[Sample],
    img_size: int,
    target: str,
    device: torch.device,
    batch_size: int,
    num_workers: int,
) -> Dict[str, object]:
    dataset = ChessPieceDataset(
        samples=samples,
        img_size=img_size,
        target=target,
        augment=False,
    )
    loader = DataLoader(
        dataset,
        batch_size=batch_size,
        shuffle=False,
        num_workers=num_workers,
        pin_memory=(device.type == "cuda"),
    )

    correct = 0
    total = 0
    num_classes = len(class_names)
    confusion = torch.zeros((num_classes, num_classes), dtype=torch.long)

    with torch.no_grad():
        for images, labels in loader:
            images = images.to(device, non_blocking=True)
            labels = labels.to(device, non_blocking=True)
            logits = model(images)
            preds = logits.argmax(dim=1)

            correct += (preds == labels).sum().item()
            total += labels.size(0)
            for true_label, pred_label in zip(labels.cpu(), preds.cpu()):
                confusion[int(true_label), int(pred_label)] += 1

    per_class_accuracy = {}
    for i, name in enumerate(class_names):
        class_total = int(confusion[i].sum().item())
        class_correct = int(confusion[i, i].item())
        per_class_accuracy[name] = (
            class_correct / class_total if class_total > 0 else None
        )

    return {
        "target": target,
        "samples": total,
        "accuracy": (correct / total) if total else 0.0,
        "per_class_accuracy": per_class_accuracy,
        "class_names": list(class_names),
        "confusion_matrix": confusion.tolist(),
    }


def resolve_eval_dir(data_dir: Path, eval_dir_arg: str | None) -> Path:
    if eval_dir_arg:
        return Path(eval_dir_arg).expanduser().resolve()

    candidates = [
        data_dir / "test",
        data_dir / "val",
        data_dir / "validation",
        data_dir / "valid",
    ]
    for c in candidates:
        if c.exists():
            return c
    raise FileNotFoundError(
        "No evaluation directory found.\n"
        "Provide --eval-dir or create one of: data/test, data/val, data/validation, data/valid"
    )


def evaluate_command(args: argparse.Namespace) -> None:
    device = resolve_device(args.device)
    data_dir = Path(args.data_dir).expanduser().resolve()
    if not data_dir.exists():
        raise FileNotFoundError(f"Data directory not found: {data_dir}")

    eval_dir = resolve_eval_dir(data_dir, args.eval_dir)
    if not eval_dir.exists():
        raise FileNotFoundError(f"Evaluation directory not found: {eval_dir}")

    models_dir = Path(args.models_dir).expanduser().resolve()
    type_model_path = (
        Path(args.type_model).expanduser().resolve()
        if args.type_model
        else models_dir / "type_classifier.pt"
    )
    color_model_path = (
        Path(args.color_model).expanduser().resolve()
        if args.color_model
        else models_dir / "color_classifier.pt"
    )
    if not type_model_path.exists():
        raise FileNotFoundError(f"Type model not found: {type_model_path}")
    if not color_model_path.exists():
        raise FileNotFoundError(f"Color model not found: {color_model_path}")

    eval_samples = collect_samples(eval_dir)
    if not eval_samples:
        raise RuntimeError(
            f"No evaluation images found under {eval_dir}. "
            f"Supported extensions: {sorted(IMAGE_EXTENSIONS)}"
        )

    type_model, type_class_names, type_img_size, _ = load_checkpoint_model(type_model_path, device)
    color_model, color_class_names, color_img_size, _ = load_checkpoint_model(
        color_model_path, device
    )

    type_metrics = evaluate_classifier(
        model=type_model,
        class_names=type_class_names,
        samples=eval_samples,
        img_size=type_img_size,
        target="type",
        device=device,
        batch_size=args.batch_size,
        num_workers=args.num_workers,
    )
    color_metrics = evaluate_classifier(
        model=color_model,
        class_names=color_class_names,
        samples=eval_samples,
        img_size=color_img_size,
        target="color",
        device=device,
        batch_size=args.batch_size,
        num_workers=args.num_workers,
    )

    result = {
        "device": str(device),
        "eval_dir": str(eval_dir),
        "num_samples": len(eval_samples),
        "type": type_metrics,
        "color": color_metrics,
    }

    if args.output_json:
        output_path = Path(args.output_json).expanduser().resolve()
        with output_path.open("w", encoding="utf-8") as fp:
            json.dump(result, fp, indent=2)

    if args.json_output:
        print(json.dumps(result, indent=2))
        return

    print(f"Evaluation dir: {eval_dir}")
    print(f"Samples: {len(eval_samples)}")
    print(
        f"Type accuracy:  {type_metrics['accuracy']:.4f} "
        f"({type_metrics['samples']} samples)"
    )
    for name, acc in type_metrics["per_class_accuracy"].items():
        if acc is None:
            print(f"  - {name}: n/a")
        else:
            print(f"  - {name}: {acc:.4f}")

    print(
        f"Color accuracy: {color_metrics['accuracy']:.4f} "
        f"({color_metrics['samples']} samples)"
    )
    for name, acc in color_metrics["per_class_accuracy"].items():
        if acc is None:
            print(f"  - {name}: n/a")
        else:
            print(f"  - {name}: {acc:.4f}")

    if args.output_json:
        print(f"Saved metrics JSON to: {Path(args.output_json).expanduser().resolve()}")


def export_vocab_command(args: argparse.Namespace) -> None:
    """Emit a manifest.json describing a trained classifier's vocabulary.

    The manifest is the contract that Solver_RG's VocabularyRegistry reads to
    surface NN classifiers in the authoring UI. See
    NN_CLASSIFIER_INTEGRATION_PLAN.md §6.2 for the full schema.
    """
    from datetime import datetime, timezone

    checkpoint_path = Path(args.checkpoint)
    if not checkpoint_path.is_file():
        raise FileNotFoundError(f"Checkpoint not found: {checkpoint_path}")

    checkpoint = torch.load(checkpoint_path, map_location="cpu")
    class_names = list(checkpoint.get("class_names") or [])
    if not class_names:
        raise ValueError(
            f"Checkpoint at {checkpoint_path} has no 'class_names'. "
            "Retrain with a newer version of this script."
        )

    target = checkpoint.get("target", "unknown")
    arch = checkpoint.get("arch", "unknown")
    img_size = int(checkpoint.get("img_size", 128))
    best_val_acc = checkpoint.get("best_val_acc", None)

    default_id = args.classifier_id or checkpoint_path.stem
    default_display = args.display_name or {
        "type": "Chess Piece Type",
        "color": "Chess Piece Color",
    }.get(target, default_id)
    default_suggested = args.suggested_primitive_type or {
        "type": "FigureType",
        "color": "FigureColor",
    }.get(target, None)

    manifest = {
        "schemaVersion": 1,
        "classifierId": default_id,
        "displayName": default_display,
        "kind": "categorical",
        "classNames": class_names,
        "checkpointPath": checkpoint_path.name,
        "inputSpec": {
            "modality": "image",
            "channels": 3,
            "height": img_size,
            "width": img_size,
            "normalization": "imagenet",
        },
        "trainingMetadata": {
            "framework": "pytorch",
            "arch": arch,
            "target": target,
            "bestValAcc": (float(best_val_acc) if best_val_acc is not None else None),
            "producedBy": "Solver_train/chess_piece_classifier.py",
            "producedAt": datetime.now(timezone.utc).isoformat(),
        },
    }
    if default_suggested:
        manifest["suggestedPrimitiveTypeName"] = default_suggested

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


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Train and run inference for chess piece type/color classifiers."
    )
    subparsers = parser.add_subparsers(dest="command", required=True)

    train_parser = subparsers.add_parser("train", help="Train both classifiers.")
    train_parser.add_argument("--data-dir", required=True, help="Dataset root directory.")
    train_parser.add_argument(
        "--output-dir", default="artifacts", help="Directory for checkpoints and logs."
    )
    train_parser.add_argument("--epochs", type=int, default=15, help="Epoch count.")
    train_parser.add_argument("--batch-size", type=int, default=32, help="Batch size.")
    train_parser.add_argument("--img-size", type=int, default=128, help="Input image size.")
    train_parser.add_argument("--lr", type=float, default=1e-3, help="Learning rate.")
    train_parser.add_argument(
        "--weight-decay", type=float, default=1e-4, help="Weight decay."
    )
    train_parser.add_argument(
        "--val-ratio",
        type=float,
        default=0.2,
        help="Validation split ratio when no val folder is present.",
    )
    train_parser.add_argument("--seed", type=int, default=42, help="Random seed.")
    train_parser.add_argument(
        "--arch",
        choices=["resnet18", "efficientnet_b0"],
        default="resnet18",
        help="Backbone architecture.",
    )
    train_parser.add_argument(
        "--num-workers",
        type=int,
        default=0,
        help="DataLoader workers.",
    )
    train_parser.add_argument(
        "--pretrained",
        action="store_true",
        help="Use ImageNet pretrained weights (downloads if missing).",
    )
    train_parser.add_argument(
        "--no-augment",
        action="store_true",
        help="Disable train-time augmentation.",
    )
    train_parser.add_argument(
        "--device",
        default="auto",
        choices=["auto", "cpu", "cuda"],
        help="Training device.",
    )
    train_parser.set_defaults(func=train_command)

    predict_parser = subparsers.add_parser("predict", help="Predict on one image.")
    predict_parser.add_argument("--image", required=True, help="Path to input image.")
    predict_parser.add_argument(
        "--models-dir",
        default="artifacts",
        help="Directory containing type_classifier.pt and color_classifier.pt.",
    )
    predict_parser.add_argument("--type-model", default=None, help="Path to type model.")
    predict_parser.add_argument("--color-model", default=None, help="Path to color model.")
    predict_parser.add_argument("--top-k", type=int, default=3, help="Top-k classes to show.")
    predict_parser.add_argument(
        "--device",
        default="auto",
        choices=["auto", "cpu", "cuda"],
        help="Inference device.",
    )
    predict_parser.add_argument(
        "--json-output", action="store_true", help="Print prediction as JSON."
    )
    predict_parser.set_defaults(func=predict_command)

    eval_parser = subparsers.add_parser(
        "evaluate", help="Evaluate trained models on a held-out dataset split."
    )
    eval_parser.add_argument("--data-dir", required=True, help="Dataset root directory.")
    eval_parser.add_argument(
        "--eval-dir",
        default=None,
        help="Evaluation split directory. If omitted, tries data/test then data/val.",
    )
    eval_parser.add_argument(
        "--models-dir",
        default="artifacts",
        help="Directory containing type_classifier.pt and color_classifier.pt.",
    )
    eval_parser.add_argument("--type-model", default=None, help="Path to type model.")
    eval_parser.add_argument("--color-model", default=None, help="Path to color model.")
    eval_parser.add_argument("--batch-size", type=int, default=32, help="Eval batch size.")
    eval_parser.add_argument("--num-workers", type=int, default=0, help="DataLoader workers.")
    eval_parser.add_argument(
        "--device",
        default="auto",
        choices=["auto", "cpu", "cuda"],
        help="Evaluation device.",
    )
    eval_parser.add_argument(
        "--output-json", default=None, help="Optional path to save metrics JSON."
    )
    eval_parser.add_argument(
        "--json-output", action="store_true", help="Print metrics as JSON."
    )
    eval_parser.set_defaults(func=evaluate_command)

    annotate_parser = subparsers.add_parser(
        "annotate",
        help="Run predictions on regions (or auto-generated grid) and save an annotated image.",
    )
    annotate_parser.add_argument("--image", required=True, help="Path to input image.")
    annotate_parser.add_argument(
        "--regions",
        default=None,
        help=(
            "Optional path to regions file (.json or .txt/.csv). "
            "If omitted, auto-generates a grid from --board-box or centered square."
        ),
    )
    annotate_parser.add_argument(
        "--board-box",
        default="auto",
        help=(
            "Board bounding box as x1,y1,x2,y2 for auto-grid mode. "
            "Use 'auto' (default) to infer centered square."
        ),
    )
    annotate_parser.add_argument(
        "--board-scale",
        type=float,
        default=0.92,
        help="Centered-square scale used in auto mode (fraction of min image side).",
    )
    annotate_parser.add_argument(
        "--grid-size",
        type=int,
        default=8,
        help="Grid size for auto mode (default 8 = 64 regions).",
    )
    annotate_parser.add_argument(
        "--models-dir",
        default="artifacts",
        help="Directory containing type_classifier.pt and color_classifier.pt.",
    )
    annotate_parser.add_argument("--type-model", default=None, help="Path to type model.")
    annotate_parser.add_argument("--color-model", default=None, help="Path to color model.")
    annotate_parser.add_argument(
        "--output",
        default=None,
        help="Output image path. Default: <input>_annotated.<ext>",
    )
    annotate_parser.add_argument("--top-k", type=int, default=3, help="Top-k per classifier.")
    annotate_parser.add_argument(
        "--device",
        default="auto",
        choices=["auto", "cpu", "cuda"],
        help="Inference device.",
    )
    annotate_parser.add_argument(
        "--output-json",
        default=None,
        help="Optional path to save region predictions as JSON.",
    )
    annotate_parser.add_argument(
        "--json-output", action="store_true", help="Print prediction result JSON."
    )
    annotate_parser.set_defaults(func=annotate_command)

    export_parser = subparsers.add_parser(
        "export-vocab",
        help="Emit a manifest.json describing this classifier's vocabulary for Solver_RG.",
    )
    export_parser.add_argument(
        "--checkpoint", required=True,
        help="Path to the .pt checkpoint to export (e.g. artifacts/type_classifier.pt).",
    )
    export_parser.add_argument(
        "--output-dir", required=True,
        help=(
            "Output folder for the manifest. Solver_RG discovers classifiers by "
            "scanning its 'classifiers/' folder, so this is typically "
            "Solver_RG/classifiers/<classifierId>/."
        ),
    )
    export_parser.add_argument(
        "--classifier-id", default=None,
        help="Stable id for this classifier. Defaults to the checkpoint filename stem.",
    )
    export_parser.add_argument(
        "--display-name", default=None,
        help="Human-facing label. Defaults to a name derived from the checkpoint target.",
    )
    export_parser.add_argument(
        "--suggested-primitive-type", default=None,
        help="PrimitiveType name hint so the UI can auto-bind the classifier to matching nuclei.",
    )
    export_parser.add_argument(
        "--copy-checkpoint", action="store_true",
        help="Also copy the .pt file next to the manifest so the folder is self-contained.",
    )
    export_parser.set_defaults(func=export_vocab_command)

    return parser


def main() -> None:
    parser = build_parser()
    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
