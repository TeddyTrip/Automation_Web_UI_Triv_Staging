#!/usr/bin/env bash
set -euo pipefail
ROOT="$(pwd)"
required=(
  "src/test/java/utils/ConfigReader.java"
  "src/test/resources/config/staging.properties"
  "src/test/resources/config/production.properties"
  "src/test/java/FuturesMarketWebUiTestRunner.java"
  "src/test/java/steps/FuturesMarketWebUiSteps.java"
  "src/test/java/pages/futures/FuturesMarketPage.java"
  "src/test/resources/features/FuturesMarketWebUi.feature"
  "docs/web/WEB-SELECTOR-GUIDE.md"
)
for f in "${required[@]}"; do
  [[ -f "$ROOT/$f" ]] || { echo "[ERROR] Missing $f" >&2; exit 1; }
done

grep -q '^web.base.url=https://cihuy.triv.id$' "$ROOT/src/test/resources/config/staging.properties"
grep -q '^api.base.url=https://cihuy.triv.id/api/v1$' "$ROOT/src/test/resources/config/staging.properties"
grep -q '^web.base.url=https://triv.co.id$' "$ROOT/src/test/resources/config/production.properties"
grep -q '^api.base.url=https://triv.co.id/api/v1$' "$ROOT/src/test/resources/config/production.properties"

if grep -RIn --exclude-dir=report --exclude='*.pdf' --exclude='*.png' -E 'https://cihuy\.triv\.id|https://triv\.co\.id' "$ROOT/src/test/java" >/tmp/triv_web_hardcoded_urls.txt; then
  echo "[ERROR] Masih ada hard-coded environment URL di Java:" >&2
  cat /tmp/triv_web_hardcoded_urls.txt >&2
  exit 1
fi

bash -n "$ROOT/scripts/run/run-futures-market-api-mac.sh"
bash -n "$ROOT/scripts/run/run-futures-market-ui-mac.sh"

echo "[OK] Dynamic env + Futures UI patch files verified."
if command -v mvn >/dev/null 2>&1; then
  mvn -DskipTests test-compile
else
  echo "[WARN] Maven tidak ditemukan; compile check dilewati."
fi
