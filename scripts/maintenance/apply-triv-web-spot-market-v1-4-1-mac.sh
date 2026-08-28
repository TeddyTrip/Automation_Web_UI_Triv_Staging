#!/usr/bin/env bash
set -euo pipefail
PATCH_ID="triv-web-spot-market-v1-4-1"
SRC=".patch-src/$PATCH_ID"
BACKUP=".patch-backups/$PATCH_ID"
if [[ ! -d "$SRC" ]]; then echo "[ERROR] Patch source not found: $SRC"; exit 1; fi
if [[ ! -f "src/test/java/pages/market/SpotMarketPage.java" ]]; then echo "[ERROR] Apply Spot Market v1.4.0 first."; exit 1; fi
mkdir -p "$BACKUP"
while IFS= read -r -d '' file; do
  rel="${file#$SRC/}"
  if [[ -f "$rel" ]]; then mkdir -p "$BACKUP/$(dirname "$rel")"; cp "$rel" "$BACKUP/$rel"; fi
  mkdir -p "$(dirname "$rel")"
  cp "$file" "$rel"
done < <(find "$SRC" -type f -print0)
echo "[OK] TRIV Web Spot Market v1.4.1 applied."
