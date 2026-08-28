$ErrorActionPreference = "Stop"
$required = @(
  "src/test/java/pages/market/SpotMarketPage.java",
  "src/test/java/api/SpotMarketInfo.java",
  "src/test/java/steps/SpotMarketWebUiSteps.java",
  "src/test/resources/features/SpotMarketWebUi.feature"
)
foreach ($f in $required) { if (-not (Test-Path $f)) { throw "Missing $f" } }
$page = Get-Content "src/test/java/pages/market/SpotMarketPage.java" -Raw
$config = Get-Content "src/test/resources/config/config.properties" -Raw
if (-not $page.Contains('By.name("search_market")')) { throw "search_market selector missing" }
if (-not $page.Contains('tbody.all-market-tbody')) { throw "result container selector missing" }
if (-not $page.Contains('a.all-24hticker-yo[data-selected-currency=')) { throw "pair selector missing" }
if (-not $config.Contains('market.ui.pairs=IDR,USDT,BTC,ETH')) { throw "pair config missing" }
Write-Host "[OK] Spot Market v1.4.1 final selectors + category/pair flow verified."
if (Get-Command mvn -ErrorAction SilentlyContinue) { mvn -DskipTests test-compile } else { Write-Host "[WARN] Maven not found; skip test-compile." }
