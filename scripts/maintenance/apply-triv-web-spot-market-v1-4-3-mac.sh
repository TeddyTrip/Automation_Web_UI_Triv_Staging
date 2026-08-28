#!/usr/bin/env bash
set -euo pipefail
PATCH_ID="triv-web-spot-market-v1-4-3"
SRC=".patch-src/$PATCH_ID"
BACKUP=".patch-backups/$PATCH_ID"
PAGE="src/test/java/pages/market/SpotMarketPage.java"
STEPS="src/test/java/steps/SpotMarketWebUiSteps.java"
CONFIG="src/test/resources/config/config.properties"

[[ -d "$SRC" ]] || { echo "[ERROR] Patch source not found: $SRC"; exit 1; }
[[ -f "$PAGE" ]] || { echo "[ERROR] Missing $PAGE. Apply Spot Market v1.4.2 first."; exit 1; }
[[ -f "$STEPS" ]] || { echo "[ERROR] Missing $STEPS. Apply Spot Market v1.4.1 first."; exit 1; }
[[ -f "$CONFIG" ]] || { echo "[ERROR] Missing $CONFIG"; exit 1; }

mkdir -p "$BACKUP/$(dirname "$PAGE")" "$BACKUP/$(dirname "$STEPS")" "$BACKUP/$(dirname "$CONFIG")"
cp "$PAGE" "$BACKUP/$PAGE"
cp "$STEPS" "$BACKUP/$STEPS"
cp "$CONFIG" "$BACKUP/$CONFIG"
cp "$SRC/$PAGE" "$PAGE"
cp "$SRC/$STEPS" "$STEPS"

upsert_prop() {
  local key="$1" value="$2" file="$3"
  if grep -qE "^${key}=" "$file"; then
    python3 - "$key" "$value" "$file" <<'PY'
import sys
from pathlib import Path
key,value,file=sys.argv[1:]
p=Path(file)
lines=p.read_text().splitlines()
out=[]
replaced=False
for line in lines:
    if line.startswith(key+'='):
        if not replaced:
            out.append(f'{key}={value}')
            replaced=True
    else:
        out.append(line)
if not replaced: out.append(f'{key}={value}')
p.write_text('\n'.join(out)+'\n')
PY
  else
    printf '\n%s=%s\n' "$key" "$value" >> "$file"
  fi
}

upsert_prop "market.search.result.timeout.seconds" "6" "$CONFIG"
upsert_prop "market.search.poll.millis" "100" "$CONFIG"

echo "[OK] TRIV Web Spot Market v1.4.3 search-result wait applied."
