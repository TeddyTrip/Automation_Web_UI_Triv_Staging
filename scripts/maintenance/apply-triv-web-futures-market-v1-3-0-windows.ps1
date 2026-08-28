$ErrorActionPreference = "Stop"
$PatchName = "triv-web-futures-market-v1-3-0"
$PatchDir = Join-Path ".patch-src" $PatchName
$BackupDir = Join-Path ".patch-backups" $PatchName

if (-not (Test-Path "pom.xml")) { throw "Jalankan dari root Automation_Web_UI_Triv_Staging-main." }
if (-not (Test-Path $PatchDir)) { throw "Patch source tidak ditemukan: $PatchDir" }
if (-not (Test-Path "src/test/java/FuturesMarketDualEnvironmentTestRunner.java")) { throw "v1.2.0 belum terdeteksi." }

Get-ChildItem -Path $PatchDir -File -Recurse | ForEach-Object {
    $relative = $_.FullName.Substring((Resolve-Path $PatchDir).Path.Length + 1)
    $target = Join-Path (Get-Location) $relative
    if (Test-Path $target) {
        $backup = Join-Path $BackupDir $relative
        New-Item -ItemType Directory -Force -Path (Split-Path $backup) | Out-Null
        Copy-Item $target $backup -Force
    }
    New-Item -ItemType Directory -Force -Path (Split-Path $target) | Out-Null
    Copy-Item $_.FullName $target -Force
}
Write-Host "[OK] TRIV Web Futures Market v1.3.0 applied."
