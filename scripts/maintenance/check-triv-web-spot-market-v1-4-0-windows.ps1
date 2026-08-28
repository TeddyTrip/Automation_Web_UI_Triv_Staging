$ErrorActionPreference = 'Stop'
$required = @(
'src/test/java/SpotMarketDualEnvironmentTestRunner.java',
'src/test/java/SpotMarketWebUiTestRunner.java',
'src/test/java/api/SpotMarketInfo.java',
'src/test/java/api/InstallCoinSpotMarkets.java',
'src/test/java/pages/market/SpotMarketPage.java',
'src/test/java/steps/SpotMarketDualEnvironmentSteps.java',
'src/test/java/steps/SpotMarketWebUiSteps.java',
'src/test/resources/features/SpotMarketDualEnvironment.feature',
'src/test/resources/features/SpotMarketWebUi.feature'
)
foreach ($f in $required) { if (-not (Test-Path $f)) { throw "Missing $f" } }
Write-Host '[OK] Spot Market v1.4.0 files/config verified.'
if (Get-Command mvn -ErrorAction SilentlyContinue) { mvn -DskipTests test-compile } else { Write-Host '[WARN] Maven not found; skip test-compile.' }
