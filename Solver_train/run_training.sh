#!/usr/bin/env bash
# run_training.sh — Download data and train the neighborhood detector end-to-end.
# Usage:  bash run_training.sh
#         bash run_training.sh --device cuda --epochs 20

set -euo pipefail

# ---------------------------------------------------------------------------
# Defaults (override via args)
# ---------------------------------------------------------------------------
DATA_DIR="./data/mohammedhemed/chess_yolo_data/images"
OUTPUT_DIR="./artifacts"
EPOCHS=15
BATCH_SIZE=16
IMG_SIZE=64
LR=1e-3
NEG_RATIO=1.0
GRID_SIZE=8
CHECKPOINT_INTERVAL=5
KEEP_LAST=3
NUM_WORKERS=0
DEVICE="cpu"
MAX_BOARDS=500   # cap boards for CPU; set 0 for all 64k (GPU recommended)
ARCH="resnet18"
PRETRAINED="--pretrained"   # remove this line to train from scratch

# ---------------------------------------------------------------------------
# Parse optional CLI overrides
# ---------------------------------------------------------------------------
while [[ $# -gt 0 ]]; do
  case $1 in
    --device)           DEVICE="$2";            shift 2 ;;
    --epochs)           EPOCHS="$2";            shift 2 ;;
    --batch-size)       BATCH_SIZE="$2";        shift 2 ;;
    --img-size)         IMG_SIZE="$2";          shift 2 ;;
    --data-dir)         DATA_DIR="$2";          shift 2 ;;
    --output-dir)       OUTPUT_DIR="$2";        shift 2 ;;
    --arch)             ARCH="$2";              shift 2 ;;
    --num-workers)      NUM_WORKERS="$2";       shift 2 ;;
    --no-pretrained)    PRETRAINED="";          shift ;;
    --max-boards)       MAX_BOARDS="$2";        shift 2 ;;
    *) echo "Unknown option: $1"; exit 1 ;;
  esac
done

PYTHON=${PYTHON:-python3.10}

echo "========================================"
echo " Chess Neighborhood Detector — Training"
echo "========================================"
echo "  device:    $DEVICE"
echo "  epochs:    $EPOCHS"
echo "  batch:     $BATCH_SIZE"
echo "  img_size:  $IMG_SIZE"
echo "  arch:      $ARCH"
echo "  data_dir:  $DATA_DIR"
echo "  output:    $OUTPUT_DIR"
echo "========================================"

# ---------------------------------------------------------------------------
# Step 1: Install dependencies if needed
# ---------------------------------------------------------------------------
echo ""
echo "[1/3] Checking dependencies..."
$PYTHON -c "import torch, torchvision, PIL" 2>/dev/null || {
  echo "Installing required packages..."
  pip install torch torchvision pillow --quiet
}

if [[ "$DATA_DIR" == *"mohammedhemed"* ]]; then
  $PYTHON -c "import huggingface_hub" 2>/dev/null || {
    echo "Installing huggingface_hub..."
    pip install huggingface_hub --quiet
  }
fi

# ---------------------------------------------------------------------------
# Step 2: Download dataset if not already present
# ---------------------------------------------------------------------------
echo ""
echo "[2/3] Checking dataset..."
if [ -d "$DATA_DIR" ] && [ "$(find "$DATA_DIR" -maxdepth 3 \( -name "*.png" -o -name "*.jpg" \) 2>/dev/null | head -1)" ]; then
  echo "  Dataset already present at $DATA_DIR — skipping download."
else
  RAW_DIR="./data/mohammedhemed"
  if [ ! -f "$RAW_DIR/chess_yolo_data-20250419T135412Z-001.zip" ]; then
    echo "  Downloading MohammedHemed chessboard dataset..."
    $PYTHON chess_neighborhood_detector.py download-data \
      --dataset mohammedhemed \
      --output-dir ./data
  fi
  echo "  Extracting images from zip files..."
  $PYTHON -c "
import zipfile
from pathlib import Path
dest = Path('$RAW_DIR')
for zp in sorted(dest.glob('*.zip')):
    print(f'  Extracting {zp.name}...')
    with zipfile.ZipFile(zp) as z:
        members = [m for m in z.namelist() if '/images/' in m and not m.endswith('/')]
        z.extractall(dest, members=members)
print('  Extraction complete.')
"
fi

# ---------------------------------------------------------------------------
# Step 3: Train
# ---------------------------------------------------------------------------
echo ""
echo "[3/3] Starting training..."
$PYTHON chess_neighborhood_detector.py train \
  --data-dir        "$DATA_DIR" \
  --output-dir      "$OUTPUT_DIR" \
  --epochs          "$EPOCHS" \
  --batch-size      "$BATCH_SIZE" \
  --img-size        "$IMG_SIZE" \
  --lr              "$LR" \
  --neg-ratio       "$NEG_RATIO" \
  --grid-size       "$GRID_SIZE" \
  --checkpoint-interval "$CHECKPOINT_INTERVAL" \
  --keep-last       "$KEEP_LAST" \
  --num-workers     "$NUM_WORKERS" \
  --arch            "$ARCH" \
  --device          "$DEVICE" \
  --max-boards      "$MAX_BOARDS" \
  $PRETRAINED

echo ""
echo "Done. Artifacts saved to: $OUTPUT_DIR"
echo "  Best model:  $OUTPUT_DIR/neighborhood_detector.pt"
echo "  History:     $OUTPUT_DIR/checkpoints_summary.json"
