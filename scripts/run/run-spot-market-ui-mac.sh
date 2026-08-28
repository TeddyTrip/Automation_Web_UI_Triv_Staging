#!/usr/bin/env bash
set -euo pipefail
ENVIRONMENT="${1:-staging}"
mvn clean test "-Dtest=SpotMarketWebUiTestRunner" "-Denv=${ENVIRONMENT}"
