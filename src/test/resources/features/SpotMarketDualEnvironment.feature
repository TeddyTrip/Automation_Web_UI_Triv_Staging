@ApiOnly
@SpotMarketDualEnv
@SpotMarketApi
Feature: Compare Spot Market Production dan Staging

  # Semua markets[] dengan kind=market dibandingkan secara structural.
  # price/change tidak dibandingkan karena volatile.
  # updated=v hanya berarti ada difference/update, bukan otomatis FAIL.

  Scenario: Validate Staging spot market dan compare dengan Production
    Given automation mengambil data spot market dari Production dan Staging dalam satu run
    When automation memvalidasi data spot market Staging dan membandingkannya dengan Production
    Then hasil comparison spot market harus disimpan sebagai JSON dengan status updated
