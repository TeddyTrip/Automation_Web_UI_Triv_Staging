$ErrorActionPreference = "Stop"
$Root = (Get-Location).Path
$Src = Join-Path $Root ".patch-src\triv-web-futures-market-v1-1-0"
$Backup = Join-Path $Root ".patch-backups\triv-web-futures-market-v1-1-0"

if (-not (Test-Path (Join-Path $Root "pom.xml"))) {
    throw "Jalankan dari root project (pom.xml tidak ditemukan)."
}
if (-not (Test-Path (Join-Path $Root "src\test\java\FuturesMarketApiTestRunner.java"))) {
    throw "Baseline Futures Market API v1.0.0 belum terdeteksi. Apply patch v1.0.0 dulu."
}
if (-not (Test-Path $Src)) {
    throw "Patch source tidak ditemukan: $Src"
}

New-Item -ItemType Directory -Force -Path $Backup | Out-Null
Get-ChildItem -Path $Src -Recurse -File | ForEach-Object {
    $rel = $_.FullName.Substring($Src.Length + 1)
    $target = Join-Path $Root $rel
    if (Test-Path $target) {
        $backupTarget = Join-Path $Backup $rel
        New-Item -ItemType Directory -Force -Path (Split-Path $backupTarget -Parent) | Out-Null
        Copy-Item $target $backupTarget -Force
    }
    New-Item -ItemType Directory -Force -Path (Split-Path $target -Parent) | Out-Null
    Copy-Item $_.FullName $target -Force
}

$oldTemplate = Join-Path $Root "src\test\resources\features\FuturesMarketWebUiTemplate.feature"
if (Test-Path $oldTemplate) { Remove-Item $oldTemplate -Force }

Write-Host "[OK] TRIV Web Futures Market v1.1.0 applied."
Write-Host "[INFO] Backup existing files: $Backup"
