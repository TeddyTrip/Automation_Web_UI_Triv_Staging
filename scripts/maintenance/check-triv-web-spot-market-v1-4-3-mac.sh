#!/usr/bin/env bash
set -euo pipefail
PAGE="src/test/java/pages/market/SpotMarketPage.java"
STEPS="src/test/java/steps/SpotMarketWebUiSteps.java"
CONFIG="src/test/resources/config/config.properties"

grep -Fq 'searchAndWaitExactPair(String apiLabel, String routeSymbol)' "$PAGE"
grep -Fq 'Waiting exact asset=' "$PAGE"
grep -Fq 'waitUntilActiveMarketRowsReady();' "$PAGE"
grep -Fq 'found = page.searchAndWaitExactPair(market.getLabel(), market.routeSymbol());' "$STEPS"
grep -Fq 'market.search.result.timeout.seconds=6' "$CONFIG"
grep -Fq 'market.search.poll.millis=100' "$CONFIG"
echo "[OK] Spot Market v1.4.3 per-asset explicit wait verified."
if command -v mvn >/dev/null 2>&1; then
  echo "[INFO] Running Maven test-compile..."
  mvn -DskipTests test-compile
else
  echo "[WARN] Maven not found; skip test-compile."
fi
