@SpotMarketWebUi
@SpotMarket
Feature: Validasi Web UI Spot Market berdasarkan label API

  # Web UI validation selalu menggunakan Category = All.
  # Pair mengikuti quote API: IDR / USDT / BTC / ETH.
  # Search menggunakan format tanpa slash: AR/BTC -> ARBTC.
  # Exact result tetap divalidasi terhadap DOM symbol: AR_BTC.

  Scenario: Validate seluruh Spot Market supported pair berdasarkan API Staging
    Given user membuka halaman Market default sesuai environment aktif
    And automation mengambil seluruh label spot market supported pair dari API install coin liverate
    When user memilih pair pada category All lalu mencari setiap label spot market
    Then seluruh label spot market harus tampil sebagai exact pair pada category All dan pair yang sesuai
