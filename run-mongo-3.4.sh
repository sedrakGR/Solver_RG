#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   bash run-mongo-3.4.sh ./db
#   (creates ./db folder if missing, then runs MongoDB 3.4 in Docker)

DB_DIR="${1:-./db}"
mkdir -p "$DB_DIR"

docker run -d --name mongo34 \
  -p 27017:27017 \
  -v "$(readlink -f "$DB_DIR")":/data/db \
  mongo:3.4 --dbpath /data/db

