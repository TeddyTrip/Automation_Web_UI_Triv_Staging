$ErrorActionPreference = "Stop"
$Root = (Resolve-Path (Join-Path $PSScriptRoot "../..")).Path
$Steps = Get-Content (Join-Path $Root "src/test/java/steps/SpotMarketWebUiSteps.java") -Raw
$Page = Get-Content (Join-Path $Root "src/test/java/pages/market/SpotMarketPage.java") -Raw
if ($Steps -notmatch 'user memilih pair pada category All lalu mencari setiap label spot market') { throw "New Cucumber step missing" }
if ($Page -notmatch 'Category menu horizontal scroll') { throw "Horizontal category scroll missing" }
Write-Host "[OK] Spot Market v1.4.5 verified."
