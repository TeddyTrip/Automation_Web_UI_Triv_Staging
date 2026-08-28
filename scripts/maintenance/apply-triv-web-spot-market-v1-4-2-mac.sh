#!/usr/bin/env bash
set -euo pipefail
PATCH_ID="triv-web-spot-market-v1-4-2"
SRC=".patch-src/$PATCH_ID"
BACKUP=".patch-backups/$PATCH_ID"
TARGET="src/test/java/pages/market/SpotMarketPage.java"
if [[ ! -d "$SRC" ]]; then echo "[ERROR] Patch source not found: $SRC"; exit 1; fi
if [[ ! -f "$TARGET" ]]; then echo "[ERROR] SpotMarketPage.java not found. Apply Spot Market v1.4.1 first."; exit 1; fi
if ! grep -Fq '.ignoring(StaleElementReferenceException.class);' "$TARGET"; then
  echo "[WARN] Expected v1.4.1 wait implementation marker not found; continuing with backup + overwrite."
fi
mkdir -p "$BACKUP/$(dirname "$TARGET")"
cp "$TARGET" "$BACKUP/$TARGET"
cp "$SRC/$TARGET" "$TARGET"
echo "[OK] TRIV Web Spot Market v1.4.2 compile hotfix applied."
