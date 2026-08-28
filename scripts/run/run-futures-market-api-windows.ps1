param(
    [ValidateSet("staging", "production", "prod")]
    [string]$Environment = "staging"
)

mvn clean test "-Dtest=FuturesMarketApiTestRunner" "-Denv=$Environment"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
