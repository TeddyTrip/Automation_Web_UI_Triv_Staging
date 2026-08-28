#!/usr/bin/env bash
set -euo pipefail
PATCH_ID="triv-web-spot-market-v1-4-4"
SRC=".patch-src/$PATCH_ID"
BACKUP=".patch-backups/$PATCH_ID"
PAGE="src/test/java/pages/market/SpotMarketPage.java"
STEPS="src/test/java/steps/SpotMarketWebUiSteps.java"
FEATURE="src/test/resources/features/SpotMarketWebUi.feature"

[[ -d "$SRC" ]] || { echo "[ERROR] Patch source not found: $SRC"; exit 1; }
[[ -f "$PAGE" ]] || { echo "[ERROR] Missing $PAGE. Apply Spot Market v1.4.3 first."; exit 1; }
[[ -f "$STEPS" ]] || { echo "[ERROR] Missing $STEPS. Apply Spot Market v1.4.3 first."; exit 1; }
[[ -f "$FEATURE" ]] || { echo "[ERROR] Missing $FEATURE. Apply Spot Market v1.4.1 first."; exit 1; }

for f in "$PAGE" "$STEPS" "$FEATURE"; do
  mkdir -p "$BACKUP/$(dirname "$f")"
  cp "$f" "$BACKUP/$f"
  cp "$SRC/$f" "$f"
done

echo "[OK] TRIV Web Spot Market v1.4.4 All-category + compact-search applied."
