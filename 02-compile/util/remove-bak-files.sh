#!/usr/bin/env sh
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
TARGET="$SCRIPT_DIR/../../01-code/is-packages"

find "$TARGET" -type f -name "*.bak" -delete
