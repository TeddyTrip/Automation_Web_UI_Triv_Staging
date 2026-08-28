$ErrorActionPreference = 'Stop'
$Page = 'src/test/java/pages/market/SpotMarketPage.java'
$Steps = 'src/test/java/steps/SpotMarketWebUiSteps.java'
$Config = 'src/test/resources/config/config.properties'
if (!(Select-String -Path $Page -SimpleMatch 'searchAndWaitExactPair(String apiLabel, String routeSymbol)')) { throw 'Missing searchAndWaitExactPair' }
if (!(Select-String -Path $Steps -SimpleMatch 'found = page.searchAndWaitExactPair(market.getLabel(), market.routeSymbol());')) { throw 'Missing steps integration' }
if (!(Select-String -Path $Config -SimpleMatch 'market.search.result.timeout.seconds=6')) { throw 'Missing timeout config' }
Write-Host '[OK] Spot Market v1.4.3 per-asset explicit wait verified.'
if (Get-Command mvn -ErrorAction SilentlyContinue) { mvn -DskipTests test-compile }
