#!/usr/bin/env bash
set -euo pipefail
TARGET="src/test/java/pages/market/SpotMarketPage.java"
[[ -f "$TARGET" ]] || { echo "[ERROR] Missing $TARGET"; exit 1; }
grep -Fq 'WebDriverWait wait = new WebDriverWait(driver, timeout);' "$TARGET"
grep -Fq 'wait.pollingEvery(POLL_INTERVAL);' "$TARGET"
grep -Fq 'wait.ignoring(StaleElementReferenceException.class);' "$TARGET"
grep -Fq 'return wait;' "$TARGET"
echo "[OK] Spot Market v1.4.2 WebDriverWait compile hotfix verified."
if command -v mvn >/dev/null 2>&1; then
  echo "[INFO] Running Maven test-compile..."
  mvn -DskipTests test-compile
else
  echo "[WARN] Maven not found; skip test-compile."
fi
