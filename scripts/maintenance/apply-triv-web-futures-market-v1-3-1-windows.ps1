$ErrorActionPreference = "Stop"
$patchName = "triv-web-futures-market-v1-3-1"
$src = ".patch-src/$patchName"
$backup = ".patch-backups/$patchName"

if (-not (Test-Path $src)) { throw "Patch source not found: $src" }

$files = @(
  "src/test/java/pages/futures/FuturesMarketPage.java",
  "src/test/resources/config/config.properties"
)

foreach ($file in $files) {
  if (Test-Path $file) {
    $dest = Join-Path $backup $file
    New-Item -ItemType Directory -Force -Path (Split-Path $dest) | Out-Null
    Copy-Item $file $dest -Force
  }
}

Copy-Item "$src/src/test/java/pages/futures/FuturesMarketPage.java" "src/test/java/pages/futures/FuturesMarketPage.java" -Force
Copy-Item "$src/src/test/resources/config/config.properties" "src/test/resources/config/config.properties" -Force
Write-Host "[OK] TRIV Web Futures Market v1.3.1 applied."
Write-Host "[INFO] Backup: $backup"
