#!/usr/bin/env bash
set -euo pipefail
ROOT="$(pwd)"
SRC="$ROOT/.patch-src/triv-web-futures-market-dual-compare-v1-2-0"
BACKUP="$ROOT/.patch-backups/triv-web-futures-market-dual-compare-v1-2-0"
[[ -d "$SRC" ]] || { echo "[ERROR] Patch source not found: $SRC"; exit 1; }
[[ -f "$ROOT/src/test/java/FuturesMarketApiTestRunner.java" ]] || { echo "[ERROR] v1.0.0 Futures Market API patch not detected."; exit 1; }
[[ -f "$ROOT/src/test/java/FuturesMarketWebUiTestRunner.java" ]] || { echo "[ERROR] v1.1.0 Dynamic Env/UI patch not detected."; exit 1; }
mkdir -p "$BACKUP"
while IFS= read -r -d '' f; do
  rel="${f#$SRC/}"
  dest="$ROOT/$rel"
  if [[ -f "$dest" ]]; then
    mkdir -p "$BACKUP/$(dirname "$rel")"
    cp "$dest" "$BACKUP/$rel"
  fi
  mkdir -p "$(dirname "$dest")"
  cp "$f" "$dest"
done < <(find "$SRC" -type f -print0)
echo "[OK] TRIV Web Futures Market Dual Compare v1.2.0 applied."
echo "[INFO] Backup: $BACKUP"
