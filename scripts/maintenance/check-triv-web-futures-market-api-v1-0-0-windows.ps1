$ErrorActionPreference = "Stop"
$Root = (Get-Location).Path
$Required = @(
  "src\test\java\api\FuturesMarketInfo.java",
  "src\test\java\api\InstallCoinLiverateMarkets.java",
  "src\test\java\api\FuturesMarketApiComparator.java",
  "src\test\java\api\FuturesMarketComparisonResult.java",
  "src\test\java\steps\FuturesMarketApiSteps.java",
  "src\test\java\FuturesMarketApiTestRunner.java",
  "src\test\java\pages\futures\FuturesMarketPage.java",
  "src\test\resources\features\FuturesMarketApi.feature",
  "src\test\resources\features\FuturesMarketWebUiTemplate.feature"
)
foreach ($File in $Required) {
  if (-not (Test-Path (Join-Path $Root $File))) { throw "Missing $File" }
}
Write-Host "[OK] Patch files verified."
if (Get-Command mvn -ErrorAction SilentlyContinue) {
  mvn -DskipTests test-compile
} else {
  Write-Warning "Maven tidak ditemukan; compile check dilewati."
}
