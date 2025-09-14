#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   bash build.sh .
#   (or pass another path to your project root)

PROJECT_DIR="${1:-.}"
cd "$PROJECT_DIR"

# Ensure gradle is installed
if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle is required. On Ubuntu: sudo apt-get install -y gradle"
  exit 1
fi

# Build WAR
gradle --no-daemon clean war

echo
echo "WAR built at: $(readlink -f build/libs/ssrgt_solver.war)"

