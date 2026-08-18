# Chess Piece Recognition via Decoupled Transfer-Learning Classifiers

## Abstract

We present a supervised image-classification pipeline for reconstructing the state of a chessboard from a photograph or screenshot. The task is decomposed into two independent sub-problems — *piece type* (six classes) and *piece color* (two classes) — each addressed by a separately trained convolutional classifier built on a ResNet-18 backbone initialized from ImageNet-pretrained weights. On a corpus of 4,557 square-cropped piece images aggregated from two publicly available chess-vision datasets, both classifiers converge to perfect validation accuracy within fifteen epochs. At inference the board is partitioned into an 8×8 grid of crops, and the two classifiers are applied independently to each cell to recover the full board configuration.

## 1. Problem formulation

Given an image `x ∈ ℝ^{H×W×3}` depicting a single chessboard square, we want to recover the symbolic label

`y = (t, c) ∈ T × C`

where `T = {pawn, knight, bishop, rook, queen, king}` and `C = {white, black}`, together with a distinguished *empty* case handled implicitly at the system level. Rather than learn a single 12-way classifier over the joint product `T × C`, we factorize:

`p(t, c | x) ≈ p_θ(t | x) · p_φ(c | x)`

and train two separate networks, `f_θ` producing logits over `T` and `f_φ` producing logits over `C`. This factorization is well-motivated here because piece color is conveyed almost entirely by low-level chromatic statistics (piece luminance vs. square luminance), whereas piece type depends on high-level shape and silhouette cues. Decoupling allows each network to allocate its capacity to the cues most discriminative for its own label.

## 2. Data

### 2.1 Sources and composition

The training corpus comprises 4,557 images drawn from two open chess-vision datasets (chessvision and openboard variants) and organized into twelve folders indexed by the standard algebraic notation `{w,b} × {P,N,B,R,Q,K}`. The empirical class frequencies are strongly unbalanced:

| Class | Count || Class | Count |
|---|---|---|---|---|
| wP | 1143 || bP | 1137 |
| wR | 333 || bR | 333 |
| bB | 242 || wB | 239 |
| wN | 207 || bN | 188 |
| bK | 216 || wK | 204 |
| wQ | 162 || bQ | 153 |

Pawns account for roughly half of the corpus, reflecting their natural abundance in chess positions, while queens are the least represented.

### 2.2 Preprocessing

Images are resized to 128×128 px and normalized channel-wise with ImageNet statistics (μ = (0.485, 0.456, 0.406), σ = (0.229, 0.224, 0.225)) so as to match the distribution under which the backbone was originally pretrained. During training we apply stochastic augmentations — horizontal flip (`p = 0.5`), rotation up to ±12°, and color jitter (brightness/contrast/saturation = 0.2, hue = 0.02) — to improve robustness to board orientation, lighting, and theme variation. At validation and inference time only the deterministic resize + normalize chain is applied.

### 2.3 Splitting protocol

The dataset is partitioned into 80% training (3,652 samples) and 20% validation (905 samples) using *stratified sampling on the joint label* `(t, c)`. Stratification on the joint label, rather than on either marginal, preserves the rarer queen classes in both splits and yields identical label distributions across train and val up to rounding. The split is seeded (`seed = 42`) for reproducibility.

## 3. Model

### 3.1 Backbone

Both classifiers use ResNet-18 (He et al., 2016) as their feature extractor. ResNet-18 was selected as a balance point between capacity and cost: it is deep enough to produce highly discriminative representations for this task, yet small enough to train on CPU in a reasonable time and to run inference on all 64 board cells in a single batch. The network is initialized with ImageNet (ILSVRC-2012) pretrained weights, giving it a useful prior over edges, textures, and object parts that transfers well to chess pieces despite the domain shift.

### 3.2 Task heads

The final 512-dimensional global-average-pooled feature is fed into a task-specific linear layer:

- *Type head*: `Linear(512 → 6)`, softmax over `T`.
- *Color head*: `Linear(512 → 2)`, softmax over `C`.

All backbone weights are unfrozen; the entire network is fine-tuned end-to-end rather than used as a frozen feature extractor. Given the strong overlap between ImageNet low-level features and chess-piece imagery, and the modest dataset size, full fine-tuning empirically works well here without the overfitting risk that would arise on much smaller data.

## 4. Training

### 4.1 Objective

For each classifier we minimize the categorical cross-entropy

`L(θ) = - (1/N) · Σᵢ log p_θ(yᵢ | xᵢ)`

where `yᵢ` is the ground-truth type or color label, respectively. No class-balancing weights or focal loss modifications are applied; the strong performance of the base objective made such adjustments unnecessary on this dataset (see §5).

### 4.2 Optimization

We use AdamW (decoupled weight decay) with learning rate `1 × 10⁻³`, weight decay `1 × 10⁻⁴`, default β₁, β₂, and a cosine-annealing schedule `η_t = η_max · ½(1 + cos(πt/T))` over `T = 15` epochs. AdamW's decoupled weight decay is preferred over L2-regularized Adam when fine-tuning pretrained networks, as it avoids the implicit interaction between adaptive learning rates and L2 that can distort the effective regularization strength. The cosine schedule provides smooth annealing without manual milestone tuning.

Mini-batches of 32 images are drawn by a shuffled DataLoader each epoch. Training runs for 15 epochs with best-on-validation checkpoint selection: at the end of each epoch we evaluate on the validation set and persist the model with the highest top-1 accuracy seen so far.

## 5. Results

Both classifiers converge rapidly and reach perfect validation accuracy within the 15-epoch budget.

### 5.1 Piece type classifier

| Epoch | Train acc | Val acc | Train loss | Val loss |
|---|---|---|---|---|
| 1 | 0.7886 | 0.9514 | 0.6447 | 0.1668 |
| 5 | 0.9855 | 0.9923 | 0.0488 | 0.0191 |
| 9 | 0.9962 | 0.9978 | 0.0140 | 0.0058 |
| 13 | 0.9986 | **1.0000** | 0.0051 | 0.0033 |
| 15 | 0.9997 | **1.0000** | 0.0025 | 0.0026 |

Best validation accuracy: 1.0000 (epochs 13 and 15).

### 5.2 Piece color classifier

| Epoch | Train acc | Val acc | Train loss | Val loss |
|---|---|---|---|---|
| 1 | 0.9351 | 0.9812 | 0.1814 | 0.0512 |
| 5 | 0.9890 | 0.9702 | 0.0291 | 0.0676 |
| 9 | 0.9945 | 0.9978 | 0.0160 | 0.0035 |
| 11 | 0.9989 | **1.0000** | 0.0042 | 0.0017 |
| 15 | 0.9995 | **1.0000** | 0.0019 | 0.0007 |

Best validation accuracy: 1.0000 (epochs 11, 12, 13, 15).

### 5.3 Discussion

The color classifier converges visibly faster than the type classifier — validation accuracy exceeds 98% after a single epoch — consistent with the hypothesis that piece color is largely decidable from low-level chromatic statistics that ImageNet-pretrained features already represent almost perfectly. Piece type requires the network to adapt its higher-level representations to chess-specific silhouettes and therefore benefits from several additional epochs of fine-tuning.

The attainment of perfect validation accuracy indicates that the 12-folder dataset is visually highly separable at 128×128 resolution and that the train/val distributions are closely matched. This is not evidence that the classifier will generalize perfectly to arbitrary board photographs — in particular, new piece sets, unusual board themes, strong perspective distortion, and partial occlusion are all out-of-distribution with respect to the present corpus. Rigorous assessment of real-world robustness would require a held-out evaluation set drawn from sources disjoint from the training datasets.

## 6. Inference

At deployment the input is a full-board image. A bounding box for the board is either supplied externally or estimated by a centered-square heuristic; the box is then partitioned into an 8×8 grid and each cell is cropped. All 64 crops are stacked into a single batch and passed through the two classifiers, yielding per-cell distributions over type and color from which a discrete board state can be decoded (e.g., to FEN) once an empty-square rule is applied at the application layer.

## 7. Limitations and future work

- **Empty-square handling.** The present classifiers assume each cell contains a piece; a separate *occupied vs. empty* classifier, or an `empty` class folded into the type head, is required for a complete pipeline.
- **Domain coverage.** The training corpus consists of clean, axis-aligned synthetic or semi-synthetic board crops. Photographs with perspective, shadow, hand occlusion, or unusual piece sets are likely to degrade performance and warrant either domain-specific data collection or heavier augmentation (perspective warp, cutout, random erasing).
- **Class imbalance.** Although accuracy is unaffected here, the queen classes are underrepresented; on harder datasets, class-weighted loss or oversampling via `WeightedRandomSampler` would be prudent.
- **Joint vs. factored modeling.** The decoupled formulation discards any correlation between type and color that might aid disambiguation under heavy occlusion. A shared-trunk, two-head architecture could recover most of the factored model's benefits while enabling joint inference.

## References

- He, K., Zhang, X., Ren, S., & Sun, J. (2016). *Deep Residual Learning for Image Recognition.* CVPR.
- Deng, J., Dong, W., Socher, R., Li, L.-J., Li, K., & Fei-Fei, L. (2009). *ImageNet: A Large-Scale Hierarchical Image Database.* CVPR.
- Loshchilov, I., & Hutter, F. (2019). *Decoupled Weight Decay Regularization.* ICLR.
- Loshchilov, I., & Hutter, F. (2017). *SGDR: Stochastic Gradient Descent with Warm Restarts.* ICLR.
- PyTorch: https://pytorch.org — torchvision: https://pytorch.org/vision
