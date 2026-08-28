#!/usr/bin/env bash
set -euo pipefail

PAGE="src/test/java/pages/futures/FuturesMarketPage.java"
CONFIG="src/test/resources/config/config.properties"

for f in "$PAGE" "$CONFIG"; do
  [[ -f "$f" ]] || { echo "[ERROR] Missing: $f"; exit 1; }
done

grep -q "Search viewport positioned instantly" "$PAGE" || { echo "[ERROR] instant viewport fix missing"; exit 1; }
grep -q "setter.call(el,val)" "$PAGE" || { echo "[ERROR] fast JS input fix missing"; exit 1; }
if grep -q "search.click()" "$PAGE"; then
  echo "[ERROR] native search.click() still present"
  exit 1
fi
grep -q "futures.search.selector.value=search_markets_futures" "$CONFIG" || { echo "[ERROR] search selector incorrect"; exit 1; }
grep -q "futures.results.selector.value=tbody.all-markets-futures-tbody" "$CONFIG" || { echo "[ERROR] results selector incorrect"; exit 1; }

echo "[OK] Futures v1.3.1 fast-search hotfix verified."

if command -v mvn >/dev/null 2>&1; then
  echo "[INFO] Running Maven test-compile..."
  mvn -DskipTests test-compile
else
  echo "[WARN] Maven not found; skip test-compile."
fi
