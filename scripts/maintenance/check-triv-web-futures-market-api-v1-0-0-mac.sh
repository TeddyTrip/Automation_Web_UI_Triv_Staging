#!/usr/bin/env bash
set -euo pipefail
ROOT="$(pwd)"
required=(
  "src/test/java/api/FuturesMarketInfo.java"
  "src/test/java/api/InstallCoinLiverateMarkets.java"
  "src/test/java/api/FuturesMarketApiComparator.java"
  "src/test/java/api/FuturesMarketComparisonResult.java"
  "src/test/java/steps/FuturesMarketApiSteps.java"
  "src/test/java/FuturesMarketApiTestRunner.java"
  "src/test/java/pages/futures/FuturesMarketPage.java"
  "src/test/resources/features/FuturesMarketApi.feature"
  "src/test/resources/features/FuturesMarketWebUiTemplate.feature"
)
for file in "${required[@]}"; do
  [[ -f "$ROOT/$file" ]] || { echo "[ERROR] Missing $file" >&2; exit 1; }
done
grep -q 'kind futures' "$ROOT/src/test/resources/features/FuturesMarketApi.feature"
grep -q 'BTCUSDT-PERP' "$ROOT/src/test/resources/features/FuturesMarketWebUiTemplate.feature"
echo "[OK] Patch files verified."
if command -v mvn >/dev/null 2>&1; then
  mvn -DskipTests test-compile
else
  echo "[WARN] Maven tidak ditemukan; compile check dilewati."
fi
