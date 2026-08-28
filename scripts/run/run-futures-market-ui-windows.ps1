param(
    [ValidateSet("staging", "production", "prod")]
    [string]$Environment = "staging"
)

mvn clean test "-Dtest=FuturesMarketWebUiTestRunner" "-Denv=$Environment"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
