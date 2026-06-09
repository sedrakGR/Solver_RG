# Chess Vision System -- Technical Reference

## Overview

This repository contains two complementary image classification models for
chess board analysis:

1. **Piece Classifier** (`chess_piece_classifier.py`) -- identifies the type
   and color of a chess piece from a single square crop.
2. **Neighborhood Detector** (`chess_neighborhood_detector.py`) -- determines
   whether two square crops are spatially adjacent on the board.

Together they form a pipeline: the piece classifier tells you **what** is on
each square; the neighborhood detector tells you **how squares relate
spatially**.

```
Board image
    |
    v
[Grid extraction] --> 64 square crops
    |
    +---> [Piece Classifier]        --> per-square: type + color
    |
    +---> [Neighborhood Detector]   --> pairwise: adjacent or not
    |
    v
[Game state reconstruction]         --> FEN, move validation, etc.
```

---

# Part A: Piece Type & Color Classifier

## A.1 Problem Formulation

**Input:** a single RGB image crop containing one chess piece.

**Outputs (two independent models):**

| Model            | Classes | Labels                                     |
|------------------|---------|---------------------------------------------|
| Type classifier  | 6       | pawn, knight, bishop, rook, queen, king     |
| Color classifier | 2       | white, black                                |

Each model is a standard multi-class classifier with softmax output.

---

## A.2 Data Pipeline

### A.2.1 Dataset Layout

Images are organized in class-named folders. Supported naming conventions:

| Pattern             | Examples                         |
|---------------------|----------------------------------|
| Color prefix + code | `wP`, `bN`, `wQ`, `bK`          |
| Full name           | `white_pawn`, `black_queen`      |
| Reversed order      | `pawn_white`, `BishopBlack`      |
| Mixed case          | `WhiteKnight`, `blackRook`       |

The parser (`parse_class_name`) handles all these via fuzzy token matching.
It extracts a `(type_idx, color_idx)` tuple per folder.

### A.2.2 Train / Validation Split

If `data_dir/train/` and `data_dir/val/` exist, they are used directly.
Otherwise, a stratified split (grouped by joint type+color label) is performed
at `--val-ratio` (default 0.2). Per-group: at least 1 sample goes to val, at
least 1 stays in train.

### A.2.3 Transforms

**Training augmentations:**

| Transform           | Parameters                                   |
|----------------------|----------------------------------------------|
| Resize               | (img_size, img_size), default 128x128        |
| RandomHorizontalFlip | p = 0.5                                      |
| RandomRotation       | degrees = 12                                 |
| ColorJitter          | brightness=0.2, contrast=0.2, saturation=0.2, hue=0.02 |
| ToTensor             | scales [0, 255] to [0.0, 1.0]               |
| Normalize            | mean=(0.485, 0.456, 0.406), std=(0.229, 0.224, 0.225) |

**Validation:** Resize + ToTensor + Normalize only.

---

## A.3 Model Architecture

### A.3.1 Backbone

Standard single-input CNN. The final classification layer is replaced to match
the target class count.

| Architecture    | Head replacement                           | Default img_size |
|-----------------|--------------------------------------------|------------------|
| ResNet-18       | `model.fc = Linear(512, num_classes)`      | 128              |
| EfficientNet-B0 | `model.classifier[1] = Linear(1280, num_classes)` | 128      |

Two separate model instances are trained: one for type (6 classes), one for
color (2 classes).

### A.3.2 Pretrained Initialization

When `--pretrained` is set, ImageNet weights are loaded
(`ResNet18_Weights.DEFAULT` or `EfficientNet_B0_Weights.DEFAULT`).

---

## A.4 Training Configuration

| Parameter       | Default | CLI flag          |
|-----------------|---------|-------------------|
| Epochs          | 15      | `--epochs`        |
| Batch size      | 32      | `--batch-size`    |
| Image size      | 128     | `--img-size`      |
| Learning rate   | 1e-3    | `--lr`            |
| Weight decay    | 1e-4    | `--weight-decay`  |
| Val ratio       | 0.2     | `--val-ratio`     |
| Architecture    | resnet18| `--arch`          |

**Loss:** CrossEntropyLoss

**Optimizer:** AdamW (lr=1e-3, weight_decay=1e-4)

**Scheduler:** CosineAnnealingLR (T_max = epochs)

### A.4.1 Checkpoint Format

```python
{
    "arch": str,           # "resnet18" or "efficientnet_b0"
    "target": str,         # "type" or "color"
    "class_names": list,   # e.g. ["pawn", "knight", ..., "king"]
    "img_size": int,       # e.g. 128
    "state_dict": dict,    # model.state_dict()
    "best_val_acc": float, # e.g. 0.97
}
```

### A.4.2 Outputs

| File                              | Content                        |
|-----------------------------------|--------------------------------|
| `type_classifier.pt`             | Best type model checkpoint     |
| `color_classifier.pt`            | Best color model checkpoint    |
| `type_classifier.history.json`   | Per-epoch type training history|
| `color_classifier.history.json`  | Per-epoch color training history|
| `training_summary.json`          | Combined summary               |

---

## A.5 Inference Modes

### A.5.1 Single Prediction (`predict`)

Accepts one image. Loads both type and color models. Returns top-k predictions
with confidence scores for each.

### A.5.2 Board Annotation (`annotate`)

Accepts a full board image. Generates an 8x8 grid (auto-detected or manual
`--board-box`), crops each cell, runs both classifiers, and draws results:

- Green rectangle around each cell
- Label: `"e4: white pawn | t=0.98 c=0.95"` (square ID, color, type,
  confidence for each model)

Supports both auto-grid and manual region file (text or JSON).

### A.5.3 Evaluation (`evaluate`)

Runs on a held-out directory. Reports per-model:
- Overall accuracy
- Per-class accuracy
- Confusion matrix (num_classes x num_classes)

---

# Part B: Neighborhood Detector

## B.1 Problem Formulation

**Input:** two RGB image crops, each representing one square of an 8x8
chessboard.

**Output:** binary classification -- `neighbor` (label 1) or `not_neighbor`
(label 0).

**Adjacency definition (8-connectivity):**

```
max(|r1 - r2|, |c1 - c2|) == 1   (Chebyshev distance = 1)
```

This includes horizontal, vertical, and diagonal adjacency.

---

## B.2 Data Pipeline

### B.2.1 Source Data

Full-board chessboard images (rendered or photographed). No per-square
annotations are needed; labels are generated automatically from grid geometry.

Primary dataset: **MohammedHemed/Chessboard-digital-images_with_fen**
(64,408 rendered board images from Hugging Face Hub).

### B.2.2 Train / Validation Split

Splitting is done at the **board level** (not pair level) to prevent data
leakage. If `train/` and `val/` directories exist, those are used. Otherwise,
an 80/20 random split is applied with a fixed seed.

### B.2.3 Pair Generation

From each board image, square pairs are enumerated on an 8x8 grid:

| Direction        | Count per board |
|------------------|-----------------|
| Horizontal       | 8 x 7 = 56     |
| Vertical         | 7 x 8 = 56     |
| Diagonal NW-SE   | 7 x 7 = 49     |
| Diagonal NE-SW   | 7 x 7 = 49     |
| **Total positive** | **210**       |

Negative pairs (Chebyshev distance > 1) are sampled from the remaining
C(64,2) - 210 = 1,806 candidates per board. Controlled by `--neg-ratio`
(default 1.0 = balanced).

| Scale           | Boards | Total pairs |
|-----------------|--------|-------------|
| CPU (capped)    | 500    | ~210,000    |
| GPU (full)      | 64,408 | ~26.9M     |

### B.2.4 Square Crop Extraction

Crops are extracted on-the-fly in `__getitem__`. For square (r, c) on a
board of size (W, H) with grid_size G:

```
x1 = int(c / G * W),   y1 = int(r / G * H)
x2 = int((c+1) / G * W),  y2 = int((r+1) / G * H)
```

### B.2.5 Transforms

**Training augmentations:**

| Transform           | Parameters                                     |
|----------------------|------------------------------------------------|
| Resize               | (img_size, img_size), default 64x64            |
| RandomHorizontalFlip | p = 0.5                                        |
| RandomRotation       | degrees = 8                                    |
| ColorJitter          | brightness=0.15, contrast=0.15, saturation=0.1, hue=0.01 |
| ToTensor             | scales [0, 255] to [0.0, 1.0]                 |
| Normalize            | mean=(0.485, 0.456, 0.406), std=(0.229, 0.224, 0.225) |

Both crops in a pair share the same random augmentation seed per sample
to preserve their spatial relationship.

**Validation:** Resize + ToTensor + Normalize only.

### B.2.6 Corrupted Image Handling

`__getitem__` retries up to 5 sequential indices on PIL errors, gracefully
skipping truncated or malformed files.

---

## B.3 Model Architecture

### B.3.1 Overall Design: Siamese Relation Network

```
                   +-------------+
  crop_A  ------> |  Backbone   | -----> fA  (512-dim)
                   | (shared W)  |                     \
                   +-------------+                      \
                                                         --> RelationHead --> logits [2]
                   +-------------+                      /
  crop_B  ------> |  Backbone   | -----> fB  (512-dim)
                   | (shared W)  |                     /
                   +-------------+
```

Both branches share **identical weights** (true Siamese -- one `nn.Module`
instance called twice).

### B.3.2 Backbone Options

| Architecture    | Embed dim | Parameters | Notes                           |
|-----------------|-----------|------------|---------------------------------|
| ResNet-18       | 512       | ~11.2M     | Default. Good speed/accuracy.   |
| EfficientNet-B0 | 1280     | ~5.3M      | Smaller params, higher embed dim.|

The final classification layer is replaced with `nn.Identity()`.

### B.3.3 Relation Head (MLP)

```
Input:  [fA, fB, |fA - fB|, fA * fB]   # shape: (batch, embed_dim * 4)
        |
  Linear(embed_dim * 4, 256)
        |
  ReLU
        |
  Dropout(0.3)
        |
  Linear(256, 2)                        # 2-class logits
```

The four-way feature combination captures both similarity (element-wise
product) and dissimilarity (absolute difference).

For ResNet-18: input dimension = 512 x 4 = 2048.

### B.3.4 Parameter Count

| Component                       | Parameters |
|---------------------------------|-----------|
| ResNet-18 backbone (shared)     | 11.2M     |
| Relation head (FC layers)       | 525K      |
| **Total**                       | **11.7M** |

---

## B.4 Training Configuration

| Parameter        | Default  | CLI flag            |
|------------------|----------|---------------------|
| Epochs           | 15       | `--epochs`          |
| Batch size       | 16       | `--batch-size`      |
| Image size       | 64x64    | `--img-size`        |
| Learning rate    | 1e-3     | `--lr`              |
| Weight decay     | 1e-4     | `--weight-decay`    |
| Neg:Pos ratio    | 1.0      | `--neg-ratio`       |
| Grid size        | 8        | `--grid-size`       |
| Val ratio        | 0.2      | `--val-ratio`       |
| Max boards (CPU) | 500      | `--max-boards`      |
| Architecture     | resnet18 | `--arch`            |

**Loss:** CrossEntropyLoss

**Optimizer:** AdamW (lr=1e-3, weight_decay=1e-4)

**Scheduler:** CosineAnnealingLR (T_max = epochs)

### B.4.1 Checkpoint Format

```python
{
    "arch": str,           # "resnet18" or "efficientnet_b0"
    "img_size": int,       # e.g. 64
    "neg_ratio": float,    # e.g. 1.0
    "grid_size": int,      # e.g. 8
    "state_dict": dict,    # model.state_dict()
    "best_val_acc": float, # e.g. 0.95
}
```

### B.4.2 Checkpointing

- **Best model:** saved on val accuracy improvement (`neighborhood_detector.pt`)
- **Interval checkpoints:** every N epochs (default 5), keeping last K (default 3)
- **Summary JSON:** `checkpoints_summary.json` updated after every epoch

---

## B.5 Inference Modes

### B.5.1 Pair Prediction (`predict-pair`)

Accepts two individual square crop images. Returns neighbor probability.

### B.5.2 Board Prediction (`predict-board`)

Accepts a full board image. Enumerates all neighbor pairs on the grid, runs
batch inference, and optionally draws colored lines:
- **Green line:** predicted neighbor
- **Red line:** predicted not-neighbor

### B.5.3 Evaluation (`evaluate`)

Runs on a held-out board image directory. Reports accuracy, precision, recall,
F1, and a 2x2 confusion matrix.

---

# Part C: Shared Infrastructure

## C.1 Reproducibility

Both scripts use `set_seed(seed)` which fixes `random`, `torch.manual_seed`,
and `torch.cuda.manual_seed_all`. Default seed: 42.

## C.2 ImageNet Normalization

Both models use the same normalization constants:
- mean = (0.485, 0.456, 0.406)
- std = (0.229, 0.224, 0.225)

## C.3 Shared Utilities

The neighborhood detector imports from the piece classifier when available:
`IMAGE_EXTENSIONS`, `MEAN`, `STD`, `set_seed`, `resolve_device`,
`preprocess_pil`. Fallback definitions are provided if the import fails.

---

# Part D: Model Comparison

| Property           | Piece Classifier (x2)      | Neighborhood Detector      |
|--------------------|----------------------------|----------------------------|
| Input              | 1 square crop              | 2 square crops             |
| Architecture       | Standard CNN               | Siamese CNN + relation MLP |
| Default img_size   | 128x128                    | 64x64                      |
| Output classes     | 6 (type) or 2 (color)      | 2 (neighbor / not)         |
| Labels from        | Folder names (manual)      | Grid geometry (automatic)  |
| Training data      | Labeled piece crops        | Unlabeled board images     |
| Checkpoint files   | `type_classifier.pt`, `color_classifier.pt` | `neighborhood_detector.pt` |
| Backbone weights   | Separate per model         | Shared (Siamese)           |

---

# Part E: File Inventory

| File                                | Purpose                              |
|-------------------------------------|--------------------------------------|
| `chess_piece_classifier.py`         | Type + color classifier: train, predict, annotate, evaluate |
| `chess_neighborhood_detector.py`    | Neighborhood detector: train, predict-pair, predict-board, evaluate |
| `test_neighborhood.py`             | Visual smoke test for neighborhood model |
| `run_training.sh`                   | End-to-end neighborhood training launcher |
| `artifacts/type_classifier.pt`      | Best type model checkpoint           |
| `artifacts/color_classifier.pt`     | Best color model checkpoint          |
| `artifacts/neighborhood_detector.pt`| Best neighborhood model checkpoint   |
| `artifacts/checkpoints_summary.json`| Neighborhood per-epoch history       |

---

# Part F: Dependencies

| Package         | Version used | Purpose                          |
|-----------------|-------------|----------------------------------|
| torch           | 2.10.0+cpu  | Training and inference           |
| torchvision     | 0.25.0+cpu  | Pretrained backbones, transforms |
| Pillow          | 9.5.0       | Image loading and drawing        |
| huggingface_hub | 0.33.4      | Dataset download                 |
