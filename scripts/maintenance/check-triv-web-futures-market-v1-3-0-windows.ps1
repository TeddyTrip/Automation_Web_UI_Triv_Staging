$ErrorActionPreference = "Stop"
$required = @(
  "src/test/java/api/FuturesMarketWebUiReportWriter.java",
  "src/test/java/pages/futures/FuturesMarketPage.java",
  "src/test/java/steps/FuturesMarketWebUiSteps.java",
  "src/test/java/steps/FuturesMarketDualEnvironmentSteps.java",
  "src/test/java/hooks/Hooks.java",
  "scripts/run/run-futures-market-full-windows.ps1"
)
foreach ($f in $required) { if (-not (Test-Path $f)) { throw "Missing: $f" } }
$config = Get-Content "src/test/resources/config/config.properties" -Raw
if ($config -notmatch "futures.search.selector.type=name") { throw "Search selector type belum final." }
if ($config -notmatch "futures.search.selector.value=search_markets_futures") { throw "Search selector value belum final." }
if ($config -notmatch "futures.results.selector.value=tbody.all-markets-futures-tbody") { throw "Result container selector belum final." }
Write-Host "[OK] Futures v1.3.0 files/selectors verified."
if (Get-Command mvn -ErrorAction SilentlyContinue) { mvn -DskipTests test-compile }
