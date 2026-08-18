# Training Approach — Technical Doc

A practical walkthrough of how the two chess-piece classifiers in this repo are trained. Target audience: engineers who want to run, modify, or reproduce the pipeline.

## What gets trained

Two independent image classifiers, both emitted from a single `train` run:

| Artifact | Task | Classes |
|---|---|---|
| `artifacts/type_classifier.pt` | Piece shape | pawn, knight, bishop, rook, queen, king (6) |
| `artifacts/color_classifier.pt` | Side color | white, black (2) |

They share the same dataset, backbone, and training loop — only the label head and supervision target differ.

## Pipeline overview

```
data/<class>/*.png
      │
      ▼
 collect_samples  ──► parse_class_name ──► (type_idx, color_idx)
      │
      ▼
 stratified_split (by joint label) ──► train / val sample lists
      │
      ▼
 ChessPieceDataset (PIL → resize → augment → normalize → tensor)
      │
      ▼
 ResNet-18 (ImageNet weights) with replaced fc head
      │
      ▼
 AdamW + CrossEntropy + CosineAnnealingLR  ──► best-val checkpoint
```

All of this is driven by the CLI in [chess_piece_classifier.py:1178-1354](chess_piece_classifier.py#L1178-L1354).

## Data handling

**Layout.** Two forms accepted (see [load_dataset](chess_piece_classifier.py#L221-L268)):
- `data/train/<class>/` + optional `data/val/<class>/` — use provided split.
- `data/<class>/` — auto-split inside the script.

**Class-name parsing.** [parse_class_name](chess_piece_classifier.py#L95-L132) normalizes folder names like `wP`, `bK`, `white_queen`, `blackbishop` into a `(type_idx, color_idx)` pair. This is where the two labels-from-one-folder trick happens: the dataset has 12 folders but each image becomes two labels.

**Stratified split.** [stratified_split](chess_piece_classifier.py#L190-L218) groups by the joint `(type, color)` label before sampling the val fraction (default 0.2), so every class stays proportionally represented in both splits.

**Transforms.**
- Always: `Resize((img_size, img_size))` → `ToTensor()` → `Normalize(ImageNet mean/std)`.
- Train-only augmentation: `RandomHorizontalFlip(p=0.5)`, `RandomRotation(12)`, `ColorJitter(brightness=0.2, contrast=0.2, saturation=0.2, hue=0.02)`. Defined in [ChessPieceDataset.__init__](chess_piece_classifier.py#L280-L307).

Note: horizontal flip does not destroy the color label (pieces look the same flipped), but for asymmetric-shape cues like the knight's facing direction, it acts as regularization.

## Model

[build_model](chess_piece_classifier.py#L320-L334) supports two backbones; `resnet18` is the default.

- **ResNet-18** with `ResNet18_Weights.DEFAULT` when `--pretrained` is set, else random init. Final `fc` replaced with `nn.Linear(in_features, num_classes)`.
- **EfficientNet-B0** as an alternative; `classifier[1]` gets the same swap.

The entire network is unfrozen — no layer-freezing / two-phase fine-tuning. In practice, 15 epochs of full fine-tuning already saturates val accuracy on this dataset.

## Training loop

Per classifier ([train_single_classifier](chess_piece_classifier.py#L376-L473)):

- **Loss:** `CrossEntropyLoss` (standard logits → softmax internally).
- **Optimizer:** `AdamW(lr=1e-3, weight_decay=1e-4)` by default.
- **LR schedule:** `CosineAnnealingLR(T_max=epochs)` — smooth decay over the run.
- **Epoch driver:** [run_epoch](chess_piece_classifier.py#L337-L373) handles both training and eval, toggled by whether an optimizer is passed.
- **Checkpointing:** save on every improvement in val accuracy only (no periodic saves). The checkpoint dict carries `arch`, `target`, `class_names`, `img_size`, `state_dict`, `best_val_acc` so inference can fully rebuild the model without external config.
- **Per-epoch history** is dumped to `*.history.json` alongside the `.pt` file.

The two classifiers are trained sequentially from the same train/val sample lists, with `target="type"` then `target="color"`. Each runs its own fresh model, optimizer, and scheduler.

## Default hyperparameters

From the `train` subparser ([chess_piece_classifier.py:1184-1231](chess_piece_classifier.py#L1184-L1231)):

| Flag | Default |
|---|---|
| `--epochs` | 15 |
| `--batch-size` | 32 |
| `--img-size` | 128 |
| `--lr` | 1e-3 |
| `--weight-decay` | 1e-4 |
| `--val-ratio` | 0.2 |
| `--arch` | `resnet18` |
| `--seed` | 42 |
| `--pretrained` | off by default — pass the flag to enable |
| `--no-augment` | off (augmentation on) |

## Determinism

[set_seed](chess_piece_classifier.py#L63-L67) seeds Python's `random`, `torch`, and `torch.cuda`. DataLoader workers and CUDA nondeterministic ops are not explicitly controlled, so bit-exact repro across runs isn't guaranteed, but results are close.

## Reproducing the reference run

```bash
python chess_piece_classifier.py train \
  --data-dir ./data \
  --output-dir ./artifacts \
  --epochs 15 \
  --batch-size 32 \
  --img-size 128 \
  --arch resnet18 \
  --pretrained
```

On the bundled 4,557-image dataset this produces val accuracies of 1.0 for both heads (see [artifacts/training_summary.json](artifacts/training_summary.json)).

## Inference path (for context)

- `predict` — single image through both models, prints label + confidence + top-k.
- `annotate` — auto-generates an 8×8 grid from a centered square (or accepts `--board-box` / a regions file), runs both models on each cell in one batch, writes an annotated image and optional JSON. Batched inference lives in [annotate_command](chess_piece_classifier.py#L838-L995).
- `evaluate` — runs both models on a held-out split, returns per-class accuracy and confusion matrices ([evaluate_classifier](chess_piece_classifier.py#L998-L1054)).

## Extension points

- **Swap backbone:** add a branch to [build_model](chess_piece_classifier.py#L320-L334).
- **Larger inputs:** raise `--img-size`; the checkpoint records it so inference adapts automatically.
- **Freeze-then-finetune:** after `build_model`, set `requires_grad=False` on every param except the new head for a warmup phase, then unfreeze.
- **Class imbalance:** the dataset is pawn-heavy and queen-light. A `WeightedRandomSampler` or class-weighted `CrossEntropyLoss` would help if you see queen-class regressions on harder datasets.
