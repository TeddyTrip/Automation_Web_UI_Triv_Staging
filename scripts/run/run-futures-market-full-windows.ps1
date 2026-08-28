$ErrorActionPreference = "Stop"

Write-Host "[1/2] Futures API Production vs Staging comparison"
mvn clean test "-Dtest=FuturesMarketDualEnvironmentTestRunner"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "[2/2] Futures Web UI validation on Staging"
mvn test "-Dtest=FuturesMarketWebUiTestRunner" "-Denv=staging"
exit $LASTEXITCODE
