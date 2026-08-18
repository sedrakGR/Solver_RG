# Chess Attribute Classifiers

Implementation of square-level chess attribute classifiers from `Chess_Attribute_Classifier_System_Specification.pdf`.

## Implemented classifiers

- `ColorClassifier`: predicts `0` (none), `1` (side-A), `2` (side-B)
- `TypeClassifier`: predicts `none|pawn|knight|bishop|rook|queen|king`
- `AxisClassifier` (X): predicts board file index `1..8` (learned or deterministic)
- `AxisClassifier` (Y): predicts board rank index `1..8` (learned or deterministic)

All classifiers are trained and saved independently as JSON model artifacts.

## Repository layout

- `chess_attr/`
- `scripts/train_color.py`
- `scripts/train_type.py`
- `scripts/train_x.py`
- `scripts/train_y.py`
- `scripts/train_all.py`
- `scripts/warp_and_crop.py`
- `scripts/build_labels_from_annotations.py`
- `scripts/split_labels.py`
- `scripts/infer_board.py`
- `tests/test_classifiers.py`
- `docs/IMPLEMENTATION_REPORT.md`
- `docs/TYPE_CPU_TRAINING.md`

## Data format

Training scripts expect square-level CSV with:

- `image_path,color,type,x,y`

Example:

```csv
image_path,color,type,x,y
squares/sq_00001.png,1,pawn,5,2
squares/sq_00002.png,0,none,6,2
```

## Quickstart

### 1) Build square labels from board images + annotations

```bash
python3 scripts/build_labels_from_annotations.py \
  --input-csv data/boards.csv \
  --output-dir data/square_dataset
```

`data/boards.csv` must include `image_path,labels_path` and may include `corners_path`.
Each `labels_path` file must be CSV with columns: `x,y,color,type`.

### 2) Train models

```bash
python3 scripts/train_all.py \
  --train-csv data/square_dataset/labels.csv \
  --models-dir models \
  --deterministic-axis
```

### 3) Run inference

```bash
python3 scripts/infer_board.py \
  --image data/example_board.png \
  --corners data/example_corners.json \
  --color-model models/color_model.json \
  --type-model models/type_model.json \
  --x-model models/x_model.json \
  --y-model models/y_model.json \
  --output data/inference/output.json \
  --viz-output data/inference/output_viz.png \
  --show-empty
```

## Notes

- X/Y are deterministically available after 8x8 splitting. Learned X/Y models are implemented, but deterministic mode is recommended for production reliability.
- Full implementation details, datasets, and validation logs are in `docs/IMPLEMENTATION_REPORT.md`.
- CPU-first figure-type training guide: `docs/TYPE_CPU_TRAINING.md`.
