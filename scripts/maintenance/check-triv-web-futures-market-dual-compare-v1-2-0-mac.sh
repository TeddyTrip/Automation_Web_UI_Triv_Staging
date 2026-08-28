#!/usr/bin/env bash
set -euo pipefail
files=(
  "src/test/java/FuturesMarketDualEnvironmentTestRunner.java"
  "src/test/java/steps/FuturesMarketDualEnvironmentSteps.java"
  "src/test/java/api/FuturesMarketEnvironmentDiff.java"
  "src/test/java/api/FuturesMarketDualEnvironmentComparator.java"
  "src/test/java/api/FuturesMarketComparisonReportWriter.java"
  "src/test/resources/features/FuturesMarketDualEnvironment.feature"
  "scripts/run/run-futures-market-compare-mac.sh"
)
for f in "${files[@]}"; do [[ -f "$f" ]] || { echo "[ERROR] Missing $f"; exit 1; }; done
grep -q 'getPropertyForEnvironment' src/test/java/utils/ConfigReader.java
grep -q 'findMarketsByKind' src/test/java/api/InstallCoinLiverateMarkets.java
echo "[OK] Dual environment comparison files verified."
if command -v mvn >/dev/null 2>&1; then
  mvn -q -DskipTests test-compile
  echo "[OK] Maven test-compile passed."
else
  echo "[WARN] Maven not found; compile check skipped."
fi
