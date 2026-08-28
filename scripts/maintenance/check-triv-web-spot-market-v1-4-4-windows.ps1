$ErrorActionPreference = "Stop"
$Page = Get-Content "src/test/java/pages/market/SpotMarketPage.java" -Raw
$Steps = Get-Content "src/test/java/steps/SpotMarketWebUiSteps.java" -Raw
$Feature = Get-Content "src/test/resources/features/SpotMarketWebUi.feature" -Raw
$Checks = @(
  @($Page, 'selectMarketContext("all", "IDR");'),
  @($Page, 'searchAndWaitExactPair(String searchKeyword, String routeSymbol)'),
  @($Steps, 'page.selectMarketContext("all", pair);'),
  @($Steps, 'String searchKeyword = toSearchKeyword(market.getLabel());'),
  @($Steps, 'row.put("ui_category", "all");'),
  @($Steps, 'row.put("search_keyword", searchKeyword);'),
  @($Feature, 'user memilih pair pada category All lalu mencari setiap label spot market')
)
foreach ($Check in $Checks) {
  if (-not $Check[0].Contains($Check[1])) { throw "Missing expected marker: $($Check[1])" }
}
Write-Host "[OK] Spot Market v1.4.4 All-only + no-slash search verified."
if (Get-Command mvn -ErrorAction SilentlyContinue) {
  Write-Host "[INFO] Running Maven test-compile..."
  mvn -DskipTests test-compile
} else {
  Write-Host "[WARN] Maven not found; skip test-compile."
}
