@FuturesMarketWebUi
@FuturesMarket
Feature: Validasi Web UI Market Futures berdasarkan label API

  # Flow:
  # 1. Buka /id/futures/BTCUSDT-PERP pada environment aktif.
  # 2. Ambil seluruh markets[].label dengan kind=futures dari API environment aktif.
  # 3. Clear search bar.
  # 4. Paste exact label Futures.
  # 5. Cari exact pair HANYA di tbody.all-markets-futures-tbody.
  # 6. Ulangi sampai seluruh label selesai.
  #
  # Final locator hasil Chrome DevTools:
  # Search : name=search_markets_futures
  # Result : css=tbody.all-markets-futures-tbody
  # Pair   : css=a.change-market-futures-currency[data-selected-currency='<LABEL>']

  Scenario: Search seluruh pair Futures berdasarkan label hasil API
    Given user membuka halaman Futures default BTCUSDT-PERP sesuai environment aktif
    And automation mengambil seluruh label market dengan kind futures dari API install coin liverate
    When user mencari setiap label futures pada search bar Futures
    Then seluruh label futures harus tampil sebagai exact pair pada hasil pencarian
