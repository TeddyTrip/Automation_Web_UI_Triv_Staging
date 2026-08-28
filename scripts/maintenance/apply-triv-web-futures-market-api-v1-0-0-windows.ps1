$ErrorActionPreference = "Stop"
$Root = (Get-Location).Path
$Src = Join-Path $Root ".patch-src\triv-web-futures-market-api-v1-0-0"
if (-not (Test-Path (Join-Path $Root "pom.xml"))) { throw "Jalankan dari root project (pom.xml tidak ditemukan)." }
if (-not (Test-Path $Src)) { throw "Patch source tidak ditemukan: $Src" }
Copy-Item "$Src\src\test\java\api\*" "$Root\src\test\java\api\" -Force
Copy-Item "$Src\src\test\java\steps\*" "$Root\src\test\java\steps\" -Force
New-Item -ItemType Directory -Force "$Root\src\test\java\pages\futures" | Out-Null
Copy-Item "$Src\src\test\java\pages\futures\*" "$Root\src\test\java\pages\futures\" -Force
Copy-Item "$Src\src\test\resources\features\*" "$Root\src\test\resources\features\" -Force
Copy-Item "$Src\src\test\java\FuturesMarketApiTestRunner.java" "$Root\src\test\java\FuturesMarketApiTestRunner.java" -Force
Write-Host "[OK] TRIV Web Futures Market API v1.0.0 applied."
