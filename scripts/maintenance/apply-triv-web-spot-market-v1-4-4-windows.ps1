$ErrorActionPreference = "Stop"
$PatchId = "triv-web-spot-market-v1-4-4"
$Src = ".patch-src/$PatchId"
$Backup = ".patch-backups/$PatchId"
$Files = @(
  "src/test/java/pages/market/SpotMarketPage.java",
  "src/test/java/steps/SpotMarketWebUiSteps.java",
  "src/test/resources/features/SpotMarketWebUi.feature"
)
if (-not (Test-Path $Src)) { throw "Patch source not found: $Src" }
foreach ($File in $Files) {
  if (-not (Test-Path $File)) { throw "Missing $File. Apply previous Spot Market patches first." }
  $BackupFile = Join-Path $Backup $File
  New-Item -ItemType Directory -Force -Path (Split-Path $BackupFile) | Out-Null
  Copy-Item $File $BackupFile -Force
  Copy-Item (Join-Path $Src $File) $File -Force
}
Write-Host "[OK] TRIV Web Spot Market v1.4.4 All-category + compact-search applied."
