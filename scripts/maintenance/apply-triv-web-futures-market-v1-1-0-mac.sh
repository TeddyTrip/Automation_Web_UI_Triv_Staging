#!/usr/bin/env bash
set -euo pipefail
ROOT="$(pwd)"
SRC="$ROOT/.patch-src/triv-web-futures-market-v1-1-0"
BACKUP="$ROOT/.patch-backups/triv-web-futures-market-v1-1-0"

if [[ ! -f "$ROOT/pom.xml" ]]; then
  echo "[ERROR] Jalankan dari root project (pom.xml tidak ditemukan)." >&2
  exit 1
fi
if [[ ! -f "$ROOT/src/test/java/FuturesMarketApiTestRunner.java" ]]; then
  echo "[ERROR] Baseline Futures Market API v1.0.0 belum terdeteksi. Apply patch v1.0.0 dulu." >&2
  exit 1
fi
if [[ ! -d "$SRC" ]]; then
  echo "[ERROR] Patch source tidak ditemukan: $SRC" >&2
  exit 1
fi

mkdir -p "$BACKUP"
while IFS= read -r -d '' file; do
  rel="${file#$SRC/}"
  if [[ -f "$ROOT/$rel" ]]; then
    mkdir -p "$BACKUP/$(dirname "$rel")"
    cp "$ROOT/$rel" "$BACKUP/$rel"
  fi
  mkdir -p "$ROOT/$(dirname "$rel")"
  cp "$file" "$ROOT/$rel"
done < <(find "$SRC" -type f -print0)

# Template v1.0.0 sudah digantikan oleh feature runnable v1.1.0.
rm -f "$ROOT/src/test/resources/features/FuturesMarketWebUiTemplate.feature"
cp "$ROOT/PATCH-README-TRIV-WEB-FUTURES-MARKET-V1-1-0.md" "$ROOT/PATCH-README-TRIV-WEB-FUTURES-MARKET-V1-1-0.md" 2>/dev/null || true

echo "[OK] TRIV Web Futures Market v1.1.0 applied."
echo "[INFO] Backup existing files: $BACKUP"
