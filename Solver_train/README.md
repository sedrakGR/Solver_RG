# Chess Piece Type + Color Classifiers

Train two image classifiers from chess piece images:
- `type_classifier.pt`: predicts piece shape (`pawn, knight, bishop, rook, queen, king`)
- `color_classifier.pt`: predicts side color (`white, black`)

## 1) Install

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

## 2) Dataset Layout

Use a folder structure with one class folder per piece+color class.

### Option A (recommended)
```text
data/
  train/
    wP/ ...
    wN/ ...
    ...
    bK/ ...
  val/
    wP/ ...
    ...
    bK/ ...
```

### Option B (single folder, auto split)
```text
data/
  wP/ ...
  wN/ ...
  ...
  bK/ ...
```

Supported class-folder naming examples:
- `wP, wN, wB, wR, wQ, wK`
- `bP, bN, bB, bR, bQ, bK`
- `white_pawn, black_queen`, etc.

## 3) Train

```bash
python chess_piece_classifier.py train \
  --data-dir ./data \
  --output-dir ./artifacts \
  --epochs 15 \
  --batch-size 32 \
  --img-size 128 \
  --arch resnet18
```

### CPU-only training (no GPU)
```bash
python chess_piece_classifier.py train \
  --data-dir ./data \
  --output-dir ./artifacts \
  --device cpu \
  --batch-size 16 \
  --num-workers 0
```

Outputs:
- `artifacts/type_classifier.pt`
- `artifacts/color_classifier.pt`
- `artifacts/type_classifier.history.json`
- `artifacts/color_classifier.history.json`
- `artifacts/training_summary.json`

## 4) Predict a Single Image

```bash
python chess_piece_classifier.py predict \
  --image ./sample_piece.png \
  --models-dir ./artifacts
```

CPU-only inference:
```bash
python chess_piece_classifier.py predict \
  --image ./sample_piece.png \
  --models-dir ./artifacts \
  --device cpu
```

Optional JSON output:
```bash
python chess_piece_classifier.py predict \
  --image ./sample_piece.png \
  --models-dir ./artifacts \
  --json-output
```

### Draw Regions + Confidence on Output Image

Fastest path (no regions file needed):

```bash
python chess_piece_classifier.py annotate \
  --image ./board.png \
  --models-dir ./artifacts \
  --device cpu \
  --output ./board_annotated.png
```

This auto-generates an 8x8 grid from a centered square.  
If the board is off-center, pass a manual board box:

```bash
python chess_piece_classifier.py annotate \
  --image ./board.png \
  --models-dir ./artifacts \
  --device cpu \
  --board-box 120,40,940,860 \
  --output ./board_annotated.png
```

Optional: custom grid size (default is `8`):

```bash
python chess_piece_classifier.py annotate \
  --image ./board.png \
  --models-dir ./artifacts \
  --device cpu \
  --grid-size 8 \
  --output ./board_annotated.png
```

Manual region file mode is still supported:

Create a regions file (`regions.txt`):

```text
# x1,y1,x2,y2,id(optional)
20,30,120,150,cell_1
140,30,240,150,cell_2
```

Then run:

```bash
python chess_piece_classifier.py annotate \
  --image ./board.png \
  --regions ./regions.txt \
  --models-dir ./artifacts \
  --device cpu \
  --output ./board_annotated.png
```

JSON regions format is also supported:

```json
[
  {"x1": 20, "y1": 30, "x2": 120, "y2": 150, "id": "cell_1"},
  {"x1": 140, "y1": 30, "x2": 240, "y2": 150, "id": "cell_2"}
]
```

## 5) Evaluate on a Held-out Split

Create a held-out split (recommended: `data/test/<class>/*.png`), then run:

```bash
python chess_piece_classifier.py evaluate \
  --data-dir ./data \
  --eval-dir ./data/test \
  --models-dir ./artifacts \
  --device cpu
```

If `--eval-dir` is not provided, it will try: `data/test`, then `data/val`.

Save metrics to JSON:
```bash
python chess_piece_classifier.py evaluate \
  --data-dir ./data \
  --eval-dir ./data/test \
  --models-dir ./artifacts \
  --device cpu \
  --output-json ./artifacts/eval_metrics.json
```

## 6) Suggested Starter Datasets

- Kaggle chess piece classification datasets with 12 classes (`white/black x 6 pieces`) are ideal.
- You can merge multiple such datasets into one folder structure for better generalization.

## 7) Common Errors

### `FileNotFoundError: ... /data`
You do not have a dataset folder yet. Create `data/` and place images in class folders, e.g.:

```text
data/
  train/
    wP/
    wN/
    wB/
    wR/
    wQ/
    wK/
    bP/
    bN/
    bB/
    bR/
    bQ/
    bK/
```

Then run training again with:
```bash
python chess_piece_classifier.py train --data-dir ./data --output-dir ./artifacts --device cpu --batch-size 16 --num-workers 0
```

### `FileNotFoundError: ... /sample_piece.png`
`sample_piece.png` is just an example filename. Use a real image path, e.g.:

```bash
python chess_piece_classifier.py predict --image ./data/train/wP/your_image.png --models-dir ./artifacts --device cpu
```
