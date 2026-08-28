$ErrorActionPreference = "Stop"
mvn clean test "-Dtest=FuturesMarketDualEnvironmentTestRunner"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
