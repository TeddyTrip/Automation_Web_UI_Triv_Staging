$ErrorActionPreference = "Stop"
$page = "src/test/java/pages/futures/FuturesMarketPage.java"
$config = "src/test/resources/config/config.properties"

if (-not (Test-Path $page)) { throw "Missing: $page" }
if (-not (Test-Path $config)) { throw "Missing: $config" }

$pageText = Get-Content $page -Raw
$configText = Get-Content $config -Raw
if ($pageText -notmatch "Search viewport positioned instantly") { throw "instant viewport fix missing" }
if ($pageText -notmatch "setter.call\(el,val\)") { throw "fast JS input fix missing" }
if ($pageText -match "search\.click\(\)") { throw "native search.click() still present" }
if ($configText -notmatch "futures.search.selector.value=search_markets_futures") { throw "search selector incorrect" }
if ($configText -notmatch "futures.results.selector.value=tbody.all-markets-futures-tbody") { throw "results selector incorrect" }
Write-Host "[OK] Futures v1.3.1 fast-search hotfix verified."

if (Get-Command mvn -ErrorAction SilentlyContinue) {
  Write-Host "[INFO] Running Maven test-compile..."
  mvn -DskipTests test-compile
}
