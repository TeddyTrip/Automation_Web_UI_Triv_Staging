#!/usr/bin/env bash
set -euo pipefail
printf '\n[1/2] Spot Market API Production vs Staging\n'
mvn clean test "-Dtest=SpotMarketDualEnvironmentTestRunner"
printf '\n[2/2] Spot Market Web UI validation on Staging\n'
mvn test "-Dtest=SpotMarketWebUiTestRunner" "-Denv=staging"
