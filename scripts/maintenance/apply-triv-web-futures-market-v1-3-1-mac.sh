#!/usr/bin/env bash
set -euo pipefail

PATCH_NAME="triv-web-futures-market-v1-3-1"
SRC=".patch-src/$PATCH_NAME"
BACKUP=".patch-backups/$PATCH_NAME"

if [[ ! -d "$SRC" ]]; then
  echo "[ERROR] Patch source not found: $SRC"
  exit 1
fi

mkdir -p "$BACKUP/src/test/java/pages/futures" "$BACKUP/src/test/resources/config"

for f in \
  src/test/java/pages/futures/FuturesMarketPage.java \
  src/test/resources/config/config.properties; do
  if [[ -f "$f" ]]; then
    mkdir -p "$BACKUP/$(dirname "$f")"
    cp "$f" "$BACKUP/$f"
  fi
done

cp "$SRC/src/test/java/pages/futures/FuturesMarketPage.java" src/test/java/pages/futures/FuturesMarketPage.java
cp "$SRC/src/test/resources/config/config.properties" src/test/resources/config/config.properties

echo "[OK] TRIV Web Futures Market v1.3.1 applied."
echo "[INFO] Backup: $BACKUP"
