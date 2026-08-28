$ErrorActionPreference = "Stop"
$PatchId = "triv-web-spot-market-v1-4-2"
$Src = ".patch-src/$PatchId"
$Backup = ".patch-backups/$PatchId"
$Target = "src/test/java/pages/market/SpotMarketPage.java"
if (!(Test-Path $Src)) { throw "Patch source not found: $Src" }
if (!(Test-Path $Target)) { throw "SpotMarketPage.java not found. Apply Spot Market v1.4.1 first." }
$BackupTarget = Join-Path $Backup $Target
New-Item -ItemType Directory -Force -Path (Split-Path $BackupTarget) | Out-Null
Copy-Item $Target $BackupTarget -Force
Copy-Item (Join-Path $Src $Target) $Target -Force
Write-Host "[OK] TRIV Web Spot Market v1.4.2 compile hotfix applied."
