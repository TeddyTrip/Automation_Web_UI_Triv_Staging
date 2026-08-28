#!/usr/bin/env bash
set -euo pipefail
PATCH_ID="triv-web-spot-market-v1-4-0"
SRC=".patch-src/$PATCH_ID"
BACKUP=".patch-backups/$PATCH_ID"
if [[ ! -d "$SRC" ]]; then echo "[ERROR] Patch source not found: $SRC"; exit 1; fi
if [[ ! -f "src/test/java/pages/futures/FuturesMarketPage.java" ]]; then echo "[ERROR] Apply Futures v1.3.1 first."; exit 1; fi
mkdir -p "$BACKUP"
while IFS= read -r -d '' file; do
  rel="${file#$SRC/}"
  if [[ -f "$rel" ]]; then mkdir -p "$BACKUP/$(dirname "$rel")"; cp "$rel" "$BACKUP/$rel"; fi
  mkdir -p "$(dirname "$rel")"
  cp "$file" "$rel"
done < <(find "$SRC" -type f -print0)
chmod +x scripts/run/run-spot-market-*.sh 2>/dev/null || true
echo "[OK] TRIV Web Spot Market v1.4.0 applied."
