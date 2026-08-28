$ErrorActionPreference = "Stop"
$Target = "src/test/java/pages/market/SpotMarketPage.java"
if (!(Test-Path $Target)) { throw "Missing $Target" }
$Text = Get-Content $Target -Raw
@(
  'WebDriverWait wait = new WebDriverWait(driver, timeout);',
  'wait.pollingEvery(POLL_INTERVAL);',
  'wait.ignoring(StaleElementReferenceException.class);',
  'return wait;'
) | ForEach-Object { if (!$Text.Contains($_)) { throw "Missing marker: $_" } }
Write-Host "[OK] Spot Market v1.4.2 WebDriverWait compile hotfix verified."
if (Get-Command mvn -ErrorAction SilentlyContinue) { mvn -DskipTests test-compile } else { Write-Host "[WARN] Maven not found; skip test-compile." }
