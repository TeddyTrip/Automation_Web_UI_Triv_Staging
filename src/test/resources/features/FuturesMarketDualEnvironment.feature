@ApiOnly
@FuturesMarketDualEnv
@FuturesMarketApi
Feature: Compare Market Futures Production dan Staging

  # Satu execution mengambil kedua environment.
  # Staging menjadi target utama validasi.
  # Production digunakan sebagai baseline pembanding.
  #
  # updated=v -> ada perubahan/penambahan dibanding Production
  # updated=x -> data structural sama dengan Production
  #
  # price dan change tidak dibandingkan karena volatile.

  Scenario: Validate Staging futures dan compare dengan Production
    Given automation mengambil data market futures dari Production dan Staging dalam satu run
    When automation memvalidasi data futures Staging dan membandingkannya dengan Production
    Then hasil comparison futures harus disimpan sebagai JSON dengan status updated
