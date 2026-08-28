$ErrorActionPreference = "Stop"
$files = @(
  "src\test\java\FuturesMarketDualEnvironmentTestRunner.java",
  "src\test\java\steps\FuturesMarketDualEnvironmentSteps.java",
  "src\test\java\api\FuturesMarketEnvironmentDiff.java",
  "src\test\java\api\FuturesMarketDualEnvironmentComparator.java",
  "src\test\java\api\FuturesMarketComparisonReportWriter.java",
  "src\test\resources\features\FuturesMarketDualEnvironment.feature",
  "scripts\run\run-futures-market-compare-windows.ps1"
)
foreach ($f in $files) { if (!(Test-Path $f)) { throw "Missing $f" } }
Write-Host "[OK] Dual environment comparison files verified."
if (Get-Command mvn -ErrorAction SilentlyContinue) {
  mvn -q -DskipTests test-compile
  if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
  Write-Host "[OK] Maven test-compile passed."
} else {
  Write-Host "[WARN] Maven not found; compile check skipped."
}
