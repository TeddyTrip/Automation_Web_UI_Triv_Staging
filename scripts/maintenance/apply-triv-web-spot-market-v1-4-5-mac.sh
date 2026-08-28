#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
SRC="$ROOT/.patch-src/triv-web-spot-market-v1-4-5"
[[ -d "$SRC" ]] || { echo "[ERROR] Patch source not found: $SRC"; exit 1; }
[[ -f "$ROOT/src/test/java/pages/market/SpotMarketPage.java" ]] || { echo "[ERROR] SpotMarketPage.java missing. Apply v1.4.x first."; exit 1; }
BACKUP="$ROOT/.patch-backups/triv-web-spot-market-v1-4-5"
mkdir -p "$BACKUP/src/test/java/pages/market" "$BACKUP/src/test/java/steps" "$BACKUP/src/test/resources/features"
cp -f "$ROOT/src/test/java/pages/market/SpotMarketPage.java" "$BACKUP/src/test/java/pages/market/SpotMarketPage.java" || true
cp -f "$ROOT/src/test/java/steps/SpotMarketWebUiSteps.java" "$BACKUP/src/test/java/steps/SpotMarketWebUiSteps.java" || true
cp -f "$ROOT/src/test/resources/features/SpotMarketWebUi.feature" "$BACKUP/src/test/resources/features/SpotMarketWebUi.feature" || true
cp -f "$SRC/src/test/java/pages/market/SpotMarketPage.java" "$ROOT/src/test/java/pages/market/SpotMarketPage.java"
cp -f "$SRC/src/test/java/steps/SpotMarketWebUiSteps.java" "$ROOT/src/test/java/steps/SpotMarketWebUiSteps.java"
cp -f "$SRC/src/test/resources/features/SpotMarketWebUi.feature" "$ROOT/src/test/resources/features/SpotMarketWebUi.feature"
echo "[OK] TRIV Web Spot Market v1.4.5 applied."
