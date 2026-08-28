$ErrorActionPreference = "Stop"
$Root = (Resolve-Path (Join-Path $PSScriptRoot "../..")).Path
$Src = Join-Path $Root ".patch-src/triv-web-spot-market-v1-4-5"
if (!(Test-Path $Src)) { throw "Patch source not found: $Src" }
Copy-Item (Join-Path $Src "src/test/java/pages/market/SpotMarketPage.java") (Join-Path $Root "src/test/java/pages/market/SpotMarketPage.java") -Force
Copy-Item (Join-Path $Src "src/test/java/steps/SpotMarketWebUiSteps.java") (Join-Path $Root "src/test/java/steps/SpotMarketWebUiSteps.java") -Force
Copy-Item (Join-Path $Src "src/test/resources/features/SpotMarketWebUi.feature") (Join-Path $Root "src/test/resources/features/SpotMarketWebUi.feature") -Force
Write-Host "[OK] TRIV Web Spot Market v1.4.5 applied."
