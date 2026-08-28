$ErrorActionPreference = 'Stop'
$PatchId = 'triv-web-spot-market-v1-4-3'
$Src = ".patch-src/$PatchId"
$Backup = ".patch-backups/$PatchId"
$Page = 'src/test/java/pages/market/SpotMarketPage.java'
$Steps = 'src/test/java/steps/SpotMarketWebUiSteps.java'
$Config = 'src/test/resources/config/config.properties'
foreach ($f in @($Page,$Steps,$Config)) { if (!(Test-Path $f)) { throw "Missing $f" } }
New-Item -ItemType Directory -Force -Path (Split-Path "$Backup/$Page"), (Split-Path "$Backup/$Steps"), (Split-Path "$Backup/$Config") | Out-Null
Copy-Item $Page "$Backup/$Page" -Force
Copy-Item $Steps "$Backup/$Steps" -Force
Copy-Item $Config "$Backup/$Config" -Force
Copy-Item "$Src/$Page" $Page -Force
Copy-Item "$Src/$Steps" $Steps -Force
$content = Get-Content $Config
foreach ($kv in @(@('market.search.result.timeout.seconds','6'),@('market.search.poll.millis','100'))) {
  $k=$kv[0]; $v=$kv[1]; $matched=$false
  $content = $content | ForEach-Object { if ($_ -match "^$([regex]::Escape($k))=") { if (!$matched) { $matched=$true; "$k=$v" } } else { $_ } }
  if (!$matched) { $content += "$k=$v" }
}
Set-Content -Path $Config -Value $content
Write-Host '[OK] TRIV Web Spot Market v1.4.3 search-result wait applied.'
