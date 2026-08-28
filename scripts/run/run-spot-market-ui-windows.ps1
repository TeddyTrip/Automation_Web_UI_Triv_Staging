param([string]$Environment = 'staging')
$ErrorActionPreference = 'Stop'
mvn clean test '-Dtest=SpotMarketWebUiTestRunner' "-Denv=$Environment"
