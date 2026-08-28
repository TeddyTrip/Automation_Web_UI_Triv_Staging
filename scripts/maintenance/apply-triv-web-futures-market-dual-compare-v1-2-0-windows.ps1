$ErrorActionPreference = "Stop"
$root = (Get-Location).Path
$src = Join-Path $root ".patch-src\triv-web-futures-market-dual-compare-v1-2-0"
$backup = Join-Path $root ".patch-backups\triv-web-futures-market-dual-compare-v1-2-0"
if (!(Test-Path $src)) { throw "Patch source not found: $src" }
if (!(Test-Path (Join-Path $root "src\test\java\FuturesMarketApiTestRunner.java"))) { throw "v1.0.0 patch not detected" }
if (!(Test-Path (Join-Path $root "src\test\java\FuturesMarketWebUiTestRunner.java"))) { throw "v1.1.0 patch not detected" }
Get-ChildItem -Path $src -Recurse -File | ForEach-Object {
  $rel = $_.FullName.Substring($src.Length + 1)
  $dest = Join-Path $root $rel
  if (Test-Path $dest) {
    $backupFile = Join-Path $backup $rel
    New-Item -ItemType Directory -Force -Path (Split-Path $backupFile) | Out-Null
    Copy-Item $dest $backupFile -Force
  }
  New-Item -ItemType Directory -Force -Path (Split-Path $dest) | Out-Null
  Copy-Item $_.FullName $dest -Force
}
Write-Host "[OK] TRIV Web Futures Market Dual Compare v1.2.0 applied."
