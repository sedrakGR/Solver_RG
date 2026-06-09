# A Two-Model Visual System for Chess Board Understanding: Piece Classification and Spatial Neighborhood Detection

## Abstract

We present a two-model computer vision system for analyzing chess board
images. The first component is a pair of standard convolutional classifiers
that identify the type (pawn, knight, bishop, rook, queen, king) and color
(white, black) of a chess piece from a single square crop. The second
component is a Siamese convolutional neural network that determines whether
two square crops originate from spatially adjacent board positions. The piece
classifiers are trained from manually labeled piece-crop datasets, while the
neighborhood detector is trained entirely from unlabeled board images --
spatial labels are generated automatically from known grid geometry, requiring
no manual annotation. Together, these models form a pipeline that reconstructs
both the identity and spatial structure of pieces on a chess board from visual
input alone.

## 1. Introduction

Automated analysis of chess board images is a multi-faceted vision problem.
A complete system must answer two fundamental questions: (1) what piece
occupies each square, and (2) how are the squares spatially arranged? The
first question is a standard image classification task. The second -- spatial
neighborhood detection -- is a relational reasoning task that has received
less attention in the chess vision literature.

We decompose the problem into two independent models that address these
questions separately, then compose their outputs for downstream tasks such as
board state reconstruction, move legality verification, and game-state graph
construction.

**Piece classification** is solved with two standard convolutional neural
networks (one for type, one for color), each operating on a single square
crop. This simple formulation leverages mature transfer-learning techniques
and requires only a folder of labeled piece images.

**Neighborhood detection** is formulated as a binary classification problem
over pairs of square crops and solved with a Siamese neural network. This
formulation has three key advantages: (1) it requires no manual spatial
annotation -- labels are derived deterministically from grid coordinates;
(2) the learned representations transfer across board styles, piece sets,
and lighting conditions; and (3) the Siamese structure shares computation
across the feature extraction stage, handling the combinatorial growth of
pair-wise queries efficiently.

## 2. Model A: Piece Type and Color Classification

### 2.1 Problem Definition

Given a single RGB image crop s containing a chess piece, the task is to
predict:

- **Type:** f_type(s) -> {pawn, knight, bishop, rook, queen, king}
- **Color:** f_color(s) -> {white, black}

These are implemented as two independent classification models, each with its
own softmax output layer.

### 2.2 Dataset

Training images are organized into class-named directories (e.g., `wP/` for
white pawn, `bQ/` for black queen). The system parses a wide range of naming
conventions: short codes (`wP`, `bN`), full names (`white_pawn`,
`black_queen`), and mixed formats (`BishopBlack`, `pawn_white`). Each folder
name is decomposed into a (type_index, color_index) pair.

Training and validation splits are performed with stratification by joint
(type, color) label to ensure all 12 piece-color combinations are represented
in both splits. The default split ratio is 80/20.

### 2.3 Architecture

Both classifiers use a standard convolutional backbone with a replaced
classification head:

| Backbone        | Embedding dim | Head                                  |
|-----------------|---------------|---------------------------------------|
| ResNet-18       | 512           | Linear(512, num_classes)              |
| EfficientNet-B0 | 1280         | Linear(1280, num_classes)             |

The type classifier outputs 6 logits; the color classifier outputs 2 logits.
When pretrained initialization is used, ImageNet weights are loaded for the
backbone and only the classification head is randomly initialized.

Input images are resized to 128x128 pixels and normalized with ImageNet
channel statistics (mean = [0.485, 0.456, 0.406], std = [0.229, 0.224,
0.225]).

### 2.4 Training Procedure

Both models are trained independently using the same protocol:

- **Loss:** cross-entropy over the class logits
- **Optimizer:** AdamW (learning rate 10^-3, weight decay 10^-4)
- **Schedule:** cosine annealing over the full training duration
- **Epochs:** 15 (default)

Data augmentation during training includes random horizontal flips (p=0.5),
random rotation (up to 12 degrees), and color jitter (brightness 0.2,
contrast 0.2, saturation 0.2, hue 0.02).

The best checkpoint is selected by validation accuracy and saved with full
metadata (architecture name, target task, class names, image size, and model
weights).

### 2.5 Evaluation

Performance is measured with overall accuracy, per-class accuracy (diagonal of
the normalized confusion matrix), and a full K x K confusion matrix, where K
is the number of classes (6 for type, 2 for color).

### 2.6 Inference and Visualization

For deployment, both models are loaded and applied jointly. Given a full board
image, the system:

1. Detects or assumes the board boundary
2. Divides the board into an 8x8 grid of square regions
3. Crops each region and runs both classifiers
4. Annotates the image with per-square labels showing predicted type, color,
   and confidence scores (e.g., "e4: white pawn | t=0.98 c=0.95")

This provides a complete per-square inventory of the board.

## 3. Model B: Spatial Neighborhood Detection

### 3.1 Problem Definition

Let B denote a chessboard image and G = {(r, c) : 0 <= r, c < 8} the
standard 8x8 grid of squares. For each square (r, c), a crop function
extracts a rectangular sub-image:

    s(r, c) = crop(B, r, c)

Two squares (r1, c1) and (r2, c2) are defined as 8-connected neighbors if
and only if their Chebyshev distance equals one:

    d_inf((r1,c1), (r2,c2)) = max(|r1 - r2|, |c1 - c2|) = 1

This includes horizontal, vertical, and diagonal adjacency. For an 8x8 board,
there are 210 unique positive (neighbor) pairs and 1,806 unique negative
(non-neighbor) pairs per board image.

The task is to learn a function f(s_A, s_B) -> {0, 1} that predicts whether
two square crops originate from adjacent grid positions, given only their
pixel content.

### 3.2 Dataset Construction

Training data consists of rendered chessboard images from the
MohammedHemed/Chessboard-digital-images_with_fen dataset, comprising 64,408
images with diverse piece configurations, board styles, and color themes.

**Key distinction from the piece classifier:** no manual labeling is required.
Labels are generated automatically by computing Chebyshev distance between
grid coordinates. This makes it possible to scale the training set to
millions of pairs with no annotation effort.

Images are split at the board level into training and validation sets to
prevent information leakage. For each board image, all 210 positive neighbor
pairs are enumerated, and an equal number of negative (non-neighbor) pairs are
sampled uniformly at random from the 1,806 candidates.

Square crops are not stored to disk. Each training sample references a board
image path and two grid coordinates; crops are extracted on-the-fly during
data loading by dividing the board image into grid cells.

### 3.3 Architecture

#### 3.3.1 Feature Extraction (Siamese Backbone)

Both square crops are processed by the same convolutional backbone with shared
parameters. We use ResNet-18 (He et al., 2016) initialized with ImageNet
pretrained weights. The final fully connected classification layer is removed
(replaced with an identity mapping), yielding a 512-dimensional embedding
vector per crop.

The shared-weight design ensures that both crops are projected into the same
embedding space, making the subsequent relation computation meaningful and
symmetric.

#### 3.3.2 Relation Head

The two embedding vectors f_A, f_B in R^512 are fused into a single
representation through four-way concatenation:

    z = [f_A ; f_B ; |f_A - f_B| ; f_A * f_B]    in R^2048

where |.| denotes element-wise absolute value and * denotes element-wise
(Hadamard) product. The absolute difference captures dissimilarity between
the two embeddings, while the element-wise product captures feature
co-activation patterns. This combination has been shown effective in relation
and similarity learning literature (Mou et al., 2017; Chen et al., 2019).

The fused vector is passed through a two-layer perceptron:

    h = ReLU(W_1 z + b_1)        W_1 in R^{256 x 2048}
    o = W_2 dropout(h) + b_2     W_2 in R^{2 x 256}

producing two logits corresponding to the non-neighbor and neighbor classes.
Dropout (p = 0.3) is applied between the hidden and output layers for
regularization.

#### 3.3.3 Parameter Count

| Component                       | Parameters |
|---------------------------------|-----------|
| ResNet-18 backbone (shared)     | 11.2M     |
| Relation head (FC layers)       | 525K      |
| **Total**                       | **11.7M** |

### 3.4 Training Procedure

The model is trained end-to-end (backbone + relation head jointly) using the
AdamW optimizer (Loshchilov and Hutter, 2019) with an initial learning rate
of 10^-3 and weight decay of 10^-4. The learning rate follows a cosine
annealing schedule over the full training duration (Loshchilov and Hutter,
2017).

Standard cross-entropy loss is used over the two-class output logits. Given
the balanced positive/negative sampling, no class weighting is applied.

To improve generalization, synchronized augmentations are applied to both
crops in a pair (using a shared random seed per sample to ensure identical
spatial transforms):

- Random horizontal flip (p = 0.5)
- Random rotation (up to 8 degrees)
- Color jitter (brightness: 0.15, contrast: 0.15, saturation: 0.1, hue: 0.01)

Synchronization is essential: if crop A is flipped but crop B is not, the
augmented pair no longer accurately represents the spatial relationship
between two board regions.

All crops are resized to 64x64 pixels and normalized with ImageNet channel
statistics.

| Configuration | Boards | Pairs    | Est. time/epoch |
|---------------|--------|----------|-----------------|
| CPU (capped)  | 500    | ~210,000 | ~5-10 min       |
| GPU (full)    | 64,408 | ~26.9M  | ~3-5 min        |

### 3.5 Evaluation

Performance is measured on a held-out set of board images (split at the board
level) using accuracy, precision, recall, F1, and a 2x2 confusion matrix.

For qualitative assessment, the model annotates a board image by drawing
colored lines between square centers:

- Green lines indicate predicted neighbor pairs
- Red lines indicate predicted non-neighbor pairs

## 4. System Composition

The two models are designed to operate independently but compose naturally.
In a full pipeline:

1. **Board detection:** locate the chessboard region in the input image and
   rectify perspective distortion (not covered by these models; assumed to be
   a preceding step).

2. **Grid extraction:** divide the detected board region into an 8x8 grid of
   64 square crops.

3. **Piece classification:** for each of the 64 crops, run both the type and
   color classifiers to obtain per-square predictions (type, color,
   confidence).

4. **Neighborhood detection:** for selected pairs of squares, run the Siamese
   model to determine spatial adjacency. This can verify that the grid
   extraction is correct, detect board orientation, or serve as input to a
   graph-based game-state reasoner.

5. **State reconstruction:** combine per-square identities with spatial
   relationships to produce a Forsyth-Edwards Notation (FEN) string or
   equivalent structured representation of the board state.

The models share no weights at inference time but use the same ImageNet
normalization and can share the same ResNet-18 backbone architecture (trained
separately).

## 5. Comparison of the Two Models

| Property           | Piece Classifier (x2)      | Neighborhood Detector      |
|--------------------|----------------------------|----------------------------|
| Input              | 1 square crop              | 2 square crops             |
| Architecture       | Standard CNN               | Siamese CNN + relation MLP |
| Default crop size  | 128x128                    | 64x64                      |
| Output classes     | 6 (type) or 2 (color)      | 2 (neighbor / not)         |
| Labels from        | Folder names (manual)      | Grid geometry (automatic)  |
| Training data      | Labeled piece crops        | Unlabeled board images     |
| Backbone weights   | Separate per model         | Shared (Siamese)           |

## 6. Design Decisions and Rationale

**Why two separate models for type and color?**
A single 12-class model (white_pawn, black_pawn, ...) would be feasible but
couples two orthogonal attributes. Separate models allow independent
evaluation, independent confidence thresholds, and simpler error analysis.

**Why Siamese rather than a single-input classifier for neighborhoods?**
A single-input model would need to process two concatenated or stacked crops
as one image. The Siamese design provides parameter sharing, naturally handles
the pair structure, and produces reusable per-square embeddings that can be
cached for efficient inference over many pairs from the same board.

**Why 8-connectivity rather than 4-connectivity?**
8-connectivity (including diagonals) is more natural for spatial reasoning on
a chessboard, where diagonal relationships are fundamental to piece movement
(bishops, queens, pawns capturing). It also provides 210 positive pairs per
board instead of 112, yielding a richer training signal.

**Why on-the-fly crop extraction?**
Storing precomputed crops for 64,000 boards x 64 squares = 4.1 million
individual image files would create significant I/O overhead and filesystem
pressure. On-the-fly extraction trades modest CPU cost at data-loading time
for dramatically simpler data management.

**Why synchronized augmentation for the Siamese model?**
If crop A is flipped but crop B is not, the augmented pair no longer
accurately represents the spatial relationship between two board regions.
Sharing the random seed ensures both crops undergo identical geometric and
color transforms, preserving label correctness.

## 7. Limitations and Future Work

- The piece classifier assumes the input crop contains exactly one piece. An
  upstream empty-square detector or occupancy classifier would be needed for
  a complete pipeline.
- The neighborhood detector is trained on rendered (synthetic) board images.
  Transfer to photographs with perspective distortion, occlusion, and
  non-uniform lighting has not been evaluated.
- The grid is assumed to be perfectly aligned with the image bounds. A
  preceding board detection and homography correction step would be needed
  for real-world deployment.
- The binary neighborhood formulation does not distinguish between horizontal,
  vertical, and diagonal neighbors. A multi-class extension could encode
  directional adjacency.
- The 64x64 crop resolution for the neighborhood detector may discard fine
  detail on high-resolution boards. Larger crop sizes or multi-scale
  approaches could improve performance.
- The two models are currently trained independently. Joint training or
  multi-task learning could improve shared feature representations.

## References

- He, K., Zhang, X., Ren, S., Sun, J. (2016). Deep Residual Learning for
  Image Recognition. CVPR.
- Loshchilov, I., Hutter, F. (2017). SGDR: Stochastic Gradient Descent with
  Warm Restarts. ICLR.
- Loshchilov, I., Hutter, F. (2019). Decoupled Weight Decay Regularization.
  ICLR.
- Mou, L., Ghamisi, P., Zhu, X. X. (2017). Deep Recurrent Neural Networks
  for Hyperspectral Image Classification. IEEE TGRS.
- Chen, H., Shi, Z., Li, J., Tan, P. (2019). Relation Attention for
  Temporal Action Detection. ACM MM.
- MohammedHemed (2025). Chessboard Digital Images with FEN.
  Hugging Face Hub.
