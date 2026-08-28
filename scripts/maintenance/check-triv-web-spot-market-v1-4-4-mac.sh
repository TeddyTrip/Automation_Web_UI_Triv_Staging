#!/usr/bin/env bash
set -euo pipefail
PAGE="src/test/java/pages/market/SpotMarketPage.java"
STEPS="src/test/java/steps/SpotMarketWebUiSteps.java"
FEATURE="src/test/resources/features/SpotMarketWebUi.feature"

grep -Fq 'selectMarketContext("all", "IDR");' "$PAGE"
grep -Fq 'searchAndWaitExactPair(String searchKeyword, String routeSymbol)' "$PAGE"
grep -Fq 'page.selectMarketContext("all", pair);' "$STEPS"
grep -Fq 'String searchKeyword = toSearchKeyword(market.getLabel());' "$STEPS"
grep -Fq 'return label.replace("/", "")' "$STEPS"
grep -Fq 'row.put("ui_category", "all");' "$STEPS"
grep -Fq 'row.put("search_keyword", searchKeyword);' "$STEPS"
grep -Fq 'user memilih pair pada category All lalu mencari setiap label spot market' "$FEATURE"

echo "[OK] Spot Market v1.4.4 All-only + no-slash search verified."
if command -v mvn >/dev/null 2>&1; then
  echo "[INFO] Running Maven test-compile..."
  mvn -DskipTests test-compile
else
  echo "[WARN] Maven not found; skip test-compile."
fi
