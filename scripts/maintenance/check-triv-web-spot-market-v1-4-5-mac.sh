#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
grep -Fq '@When("user memilih pair pada category All lalu mencari setiap label spot market")' "$ROOT/src/test/java/steps/SpotMarketWebUiSteps.java"
grep -Fq 'Category menu horizontal scroll -> ' "$ROOT/src/test/java/pages/market/SpotMarketPage.java"
grep -Fq 'menu.scrollLeft=Math.max(0,target)' "$ROOT/src/test/java/pages/market/SpotMarketPage.java"
grep -Fq 'user memilih pair pada category All lalu mencari setiap label spot market' "$ROOT/src/test/resources/features/SpotMarketWebUi.feature"
echo "[OK] Spot Market v1.4.5 Cucumber step + horizontal All scroll verified."
if command -v mvn >/dev/null 2>&1; then
  echo "[INFO] Running Maven test-compile..."
  cd "$ROOT"
  mvn -DskipTests test-compile
else
  echo "[WARN] Maven not found; compile check skipped."
fi
