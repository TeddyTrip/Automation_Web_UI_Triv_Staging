#!/usr/bin/env bash
set -euo pipefail
required=(
  src/test/java/pages/market/SpotMarketPage.java
  src/test/java/api/SpotMarketInfo.java
  src/test/java/steps/SpotMarketWebUiSteps.java
  src/test/resources/features/SpotMarketWebUi.feature
)
for f in "${required[@]}"; do [[ -f "$f" ]] || { echo "[ERROR] Missing $f"; exit 1; }; done
grep -Fq 'By.name("search_market")' src/test/java/pages/market/SpotMarketPage.java
grep -Fq 'tbody.all-market-tbody' src/test/java/pages/market/SpotMarketPage.java
grep -Fq 'button.category-nav[data-category=' src/test/java/pages/market/SpotMarketPage.java
grep -Fq 'button.pair-nav[data-category=' src/test/java/pages/market/SpotMarketPage.java
grep -Fq 'a.all-24hticker-yo[data-selected-currency=' src/test/java/pages/market/SpotMarketPage.java
grep -Fq 'market.ui.pairs=IDR,USDT,BTC,ETH' src/test/resources/config/config.properties
grep -Fq 'supported pair' src/test/resources/features/SpotMarketWebUi.feature
echo "[OK] Spot Market v1.4.1 final selectors + category/pair flow verified."
if command -v mvn >/dev/null 2>&1; then
  echo "[INFO] Running Maven test-compile..."
  mvn -DskipTests test-compile
else
  echo "[WARN] Maven not found; skip test-compile."
fi
