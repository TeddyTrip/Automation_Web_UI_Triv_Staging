#!/usr/bin/env bash
set -euo pipefail
ROOT="$(pwd)"
SRC="$ROOT/.patch-src/triv-web-futures-market-api-v1-0-0"
if [[ ! -f "$ROOT/pom.xml" ]]; then
  echo "[ERROR] Jalankan dari root project (pom.xml tidak ditemukan)." >&2
  exit 1
fi
if [[ ! -d "$SRC" ]]; then
  echo "[ERROR] Patch source tidak ditemukan: $SRC" >&2
  exit 1
fi
cp -R "$SRC/src/test/java/api/." "$ROOT/src/test/java/api/"
cp -R "$SRC/src/test/java/steps/." "$ROOT/src/test/java/steps/"
mkdir -p "$ROOT/src/test/java/pages/futures"
cp -R "$SRC/src/test/java/pages/futures/." "$ROOT/src/test/java/pages/futures/"
cp -R "$SRC/src/test/resources/features/." "$ROOT/src/test/resources/features/"
cp "$SRC/src/test/java/FuturesMarketApiTestRunner.java" "$ROOT/src/test/java/FuturesMarketApiTestRunner.java"
cp "$ROOT/PATCH-README-TRIV-WEB-FUTURES-MARKET-API-V1-0-0.md" "$ROOT/PATCH-README-TRIV-WEB-FUTURES-MARKET-API-V1-0-0.md" 2>/dev/null || true
echo "[OK] TRIV Web Futures Market API v1.0.0 applied."
