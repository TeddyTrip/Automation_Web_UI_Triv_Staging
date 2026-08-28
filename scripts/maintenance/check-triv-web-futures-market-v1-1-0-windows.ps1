$ErrorActionPreference = "Stop"
$Root = (Get-Location).Path
$Required = @(
    "src\test\java\utils\ConfigReader.java",
    "src\test\resources\config\staging.properties",
    "src\test\resources\config\production.properties",
    "src\test\java\FuturesMarketWebUiTestRunner.java",
    "src\test\java\steps\FuturesMarketWebUiSteps.java",
    "src\test\java\pages\futures\FuturesMarketPage.java",
    "src\test\resources\features\FuturesMarketWebUi.feature",
    "docs\web\WEB-SELECTOR-GUIDE.md"
)
foreach ($file in $Required) {
    if (-not (Test-Path (Join-Path $Root $file))) { throw "Missing $file" }
}

$javaFiles = Get-ChildItem (Join-Path $Root "src\test\java") -Recurse -Filter *.java
$hardcoded = $javaFiles | Select-String -Pattern 'https://cihuy\.triv\.id|https://triv\.co\.id'
if ($hardcoded) {
    $hardcoded | ForEach-Object { Write-Host $_ }
    throw "Masih ada hard-coded environment URL di Java."
}

Write-Host "[OK] Dynamic env + Futures UI patch files verified."
if (Get-Command mvn -ErrorAction SilentlyContinue) {
    mvn -DskipTests test-compile
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
} else {
    Write-Host "[WARN] Maven tidak ditemukan; compile check dilewati."
}
