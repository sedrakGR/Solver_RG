# Figure-Type Training (CPU Only)

This guide trains only the `type` classifier (`none/pawn/knight/bishop/rook/queen/king`) on CPU.

## 1) Activate virtual environment

From repository root:

```bash
source .venv/bin/activate
```

Verify:

```bash
python -c "import PIL; print(PIL.__version__)"
```

## 2) Datasets to download (specific)

For **type/shape** first:

1. Kaggle (synthetic base): `thefamousrat/synthetic-chess-board-images`
   - URL: `https://www.kaggle.com/datasets/thefamousrat/synthetic-chess-board-images`
2. Kaggle (camera-like variations): `mmkoya/chessrender360`
   - URL: `https://www.kaggle.com/datasets/mmkoya/chessrender360`
3. Your own screen/camera board captures (recommended for domain match)

For later stages:

1. Corners/keypoints: `surawut/chessboard-dataset-yolo` (HF) and Roboflow `chessboard-corners-wwivs`
2. Color/X/Y can reuse the same square dataset produced for type.

## 3) Download commands

If you have internet and credentials on your machine:

### Kaggle

```bash
kaggle datasets download -d thefamousrat/synthetic-chess-board-images -p data/raw/kaggle --unzip
kaggle datasets download -d mmkoya/chessrender360 -p data/raw/kaggle --unzip
```

## 4) Prepare board manifest + annotations

Create board manifest `data/boards/boards.csv` with:

- `image_path`
- `labels_path` (CSV for that board; columns: `x,y,color,type`)
- optional `corners_path`

Example labels file (`labels_path`):

```csv
x,y,color,type
1,1,0,none
2,1,1,pawn
3,1,2,knight
```

If your dataset is already piece crops organized by class folders, use this shortcut instead:

```bash
python scripts/build_type_labels_from_class_dirs.py \
  --dataset-root data/raw/openboard/train \
  --output-csv data/type_dataset/labels.csv \
  --recursive
```

Then split/train from `data/type_dataset/labels.csv`.

## 5) Build square-level labels from board + annotations

```bash
python scripts/build_labels_from_annotations.py \
  --input-csv data/boards/boards.csv \
  --output-dir data/square_dataset_type \
  --board-size 512
```

Output:

- `data/square_dataset_type/squares/*.png`
- `data/square_dataset_type/labels.csv`

## 6) Split train/validation

```bash
python scripts/split_labels.py \
  --input-csv data/square_dataset_type/labels.csv \
  --output-dir data/square_dataset_type/splits \
  --val-ratio 0.15 \
  --seed 42
```

## 7) Train type model on CPU

```bash
python scripts/train_type.py \
  --train-csv data/square_dataset_type/splits/train.csv \
  --val-csv data/square_dataset_type/splits/val.csv \
  --output models/type_model.json
```

Optional (6-class piece-only type model):

```bash
python scripts/train_type.py \
  --train-csv data/square_dataset_type/splits/train.csv \
  --val-csv data/square_dataset_type/splits/val.csv \
  --exclude-none \
  --output models/type_model_6class.json
```

## 8) Quick inference smoke test (after all models exist)

```bash
python scripts/infer_board.py \
  --image data/example/board.png \
  --corners data/example/corners.json \
  --color-model models/color_model.json \
  --type-model models/type_model.json \
  --x-model models/x_model.json \
  --y-model models/y_model.json \
  --output data/inference/output.json
```

## 9) Recommended dataset allocation by classifier

1. `type`: `thefamousrat/synthetic-chess-board-images` + `mmkoya/chessrender360` + your own captures
2. `color`: same datasets as `type`, with balanced light/dark piece samples
3. `x`/`y`: same square dataset; deterministic mode is typically preferred in production
4. corners model (separate pipeline): `surawut/chessboard-dataset-yolo` + Roboflow corners dataset
