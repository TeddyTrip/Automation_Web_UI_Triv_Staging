$ErrorActionPreference = "Stop"
$PatchId = "triv-web-spot-market-v1-4-1"
$Src = ".patch-src/$PatchId"
$Backup = ".patch-backups/$PatchId"
if (-not (Test-Path $Src)) { throw "Patch source not found: $Src" }
if (-not (Test-Path "src/test/java/pages/market/SpotMarketPage.java")) { throw "Apply Spot Market v1.4.0 first." }
Get-ChildItem -Path $Src -File -Recurse | ForEach-Object {
  $rel = $_.FullName.Substring((Resolve-Path $Src).Path.Length + 1)
  $dest = Join-Path (Get-Location) $rel
  if (Test-Path $dest) {
    $backupDest = Join-Path $Backup $rel
    New-Item -ItemType Directory -Force -Path (Split-Path $backupDest) | Out-Null
    Copy-Item $dest $backupDest -Force
  }
  New-Item -ItemType Directory -Force -Path (Split-Path $dest) | Out-Null
  Copy-Item $_.FullName $dest -Force
}
Write-Host "[OK] TRIV Web Spot Market v1.4.1 applied."
