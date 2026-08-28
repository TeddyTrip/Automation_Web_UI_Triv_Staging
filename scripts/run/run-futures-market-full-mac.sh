#!/usr/bin/env bash
set -euo pipefail

echo "[1/2] Futures API Production vs Staging comparison"
mvn clean test "-Dtest=FuturesMarketDualEnvironmentTestRunner"

echo

echo "[2/2] Futures Web UI validation on Staging"
mvn test "-Dtest=FuturesMarketWebUiTestRunner" "-Denv=staging"
