#!/usr/bin/env bash
set -euo pipefail

PATCH_NAME="triv-web-futures-market-v1-3-0"
PATCH_DIR=".patch-src/$PATCH_NAME"
BACKUP_DIR=".patch-backups/$PATCH_NAME"

if [[ ! -f "pom.xml" ]]; then
  echo "[ERROR] Jalankan dari root Automation_Web_UI_Triv_Staging-main (pom.xml tidak ditemukan)."
  exit 1
fi
if [[ ! -d "$PATCH_DIR" ]]; then
  echo "[ERROR] Patch source tidak ditemukan: $PATCH_DIR"
  exit 1
fi
if [[ ! -f "src/test/java/FuturesMarketDualEnvironmentTestRunner.java" ]]; then
  echo "[ERROR] v1.2.0 belum terdeteksi. Apply Futures Market v1.0.0 + v1.1.0 + v1.2.0 terlebih dahulu."
  exit 1
fi

mkdir -p "$BACKUP_DIR"
while IFS= read -r -d '' source; do
  rel="${source#$PATCH_DIR/}"
  target="$rel"
  if [[ -f "$target" ]]; then
    mkdir -p "$BACKUP_DIR/$(dirname "$rel")"
    cp "$target" "$BACKUP_DIR/$rel"
  fi
  mkdir -p "$(dirname "$target")"
  cp "$source" "$target"
done < <(find "$PATCH_DIR" -type f -print0)

chmod +x scripts/run/run-futures-market-full-mac.sh 2>/dev/null || true

echo "[OK] TRIV Web Futures Market v1.3.0 applied."
echo "[INFO] Backup existing files: $(pwd)/$BACKUP_DIR"
echo "[NEXT] bash scripts/maintenance/check-triv-web-futures-market-v1-3-0-mac.sh"
