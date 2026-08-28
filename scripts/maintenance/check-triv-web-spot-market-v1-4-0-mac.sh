#!/usr/bin/env bash
set -euo pipefail
required=(
  src/test/java/SpotMarketDualEnvironmentTestRunner.java
  src/test/java/SpotMarketWebUiTestRunner.java
  src/test/java/api/SpotMarketInfo.java
  src/test/java/api/InstallCoinSpotMarkets.java
  src/test/java/pages/market/SpotMarketPage.java
  src/test/java/steps/SpotMarketDualEnvironmentSteps.java
  src/test/java/steps/SpotMarketWebUiSteps.java
  src/test/resources/features/SpotMarketDualEnvironment.feature
  src/test/resources/features/SpotMarketWebUi.feature
  scripts/run/run-spot-market-full-mac.sh
)
for f in "${required[@]}"; do [[ -f "$f" ]] || { echo "[ERROR] Missing $f"; exit 1; }; done
grep -q 'market.web.path.template=/en/markets/%s' src/test/resources/config/staging.properties
grep -q 'market.web.path.template=/id/markets/%s' src/test/resources/config/production.properties
grep -q 'market.ui.quote=IDR' src/test/resources/config/config.properties
echo "[OK] Spot Market v1.4.0 files/config verified."
if command -v mvn >/dev/null 2>&1; then
  echo "[INFO] Running Maven test-compile..."
  mvn -DskipTests test-compile
else
  echo "[WARN] Maven not found; skip test-compile."
fi
