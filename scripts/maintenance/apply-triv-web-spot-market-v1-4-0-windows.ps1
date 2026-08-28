$ErrorActionPreference = 'Stop'
$patchId = 'triv-web-spot-market-v1-4-0'
$src = ".patch-src/$patchId"
$backup = ".patch-backups/$patchId"
if (-not (Test-Path $src)) { throw "Patch source not found: $src" }
if (-not (Test-Path 'src/test/java/pages/futures/FuturesMarketPage.java')) { throw 'Apply Futures v1.3.1 first.' }
Get-ChildItem -Path $src -Recurse -File | ForEach-Object {
  $rel = $_.FullName.Substring((Resolve-Path $src).Path.Length + 1)
  $dest = Join-Path (Get-Location) $rel
  if (Test-Path $dest) {
    $b = Join-Path $backup $rel
    New-Item -ItemType Directory -Force -Path (Split-Path $b) | Out-Null
    Copy-Item $dest $b -Force
  }
  New-Item -ItemType Directory -Force -Path (Split-Path $dest) | Out-Null
  Copy-Item $_.FullName $dest -Force
}
Write-Host '[OK] TRIV Web Spot Market v1.4.0 applied.'
