#!/usr/bin/env bash
set -euo pipefail

required=(
"src/test/java/api/FuturesMarketWebUiReportWriter.java"
"src/test/java/pages/futures/FuturesMarketPage.java"
"src/test/java/steps/FuturesMarketWebUiSteps.java"
"src/test/java/steps/FuturesMarketDualEnvironmentSteps.java"
"src/test/java/hooks/Hooks.java"
"scripts/run/run-futures-market-full-mac.sh"
)
for f in "${required[@]}"; do
  [[ -f "$f" ]] || { echo "[ERROR] Missing: $f"; exit 1; }
done

grep -q '^futures.search.selector.type=name$' src/test/resources/config/config.properties
grep -q '^futures.search.selector.value=search_markets_futures$' src/test/resources/config/config.properties
grep -q '^futures.results.selector.value=tbody.all-markets-futures-tbody$' src/test/resources/config/config.properties
grep -q "^futures.pair.selector.template=a.change-market-futures-currency\[data-selected-currency='%s'\]$" src/test/resources/config/config.properties
grep -q '^@ApiOnly$' src/test/resources/features/FuturesMarketDualEnvironment.feature
grep -q 'Difference/update, bukan otomatis FAIL\|difference/update, bukan otomatis FAIL' src/test/java/steps/FuturesMarketDualEnvironmentSteps.java || true
grep -q 'API-only scenario detected' src/test/java/hooks/Hooks.java

bash -n scripts/run/run-futures-market-full-mac.sh

echo "[OK] Futures v1.3.0 files/selectors verified."
if command -v mvn >/dev/null 2>&1; then
  echo "[INFO] Running Maven test-compile..."
  mvn -DskipTests test-compile
else
  echo "[WARN] Maven tidak ditemukan. Skip test-compile."
fi
