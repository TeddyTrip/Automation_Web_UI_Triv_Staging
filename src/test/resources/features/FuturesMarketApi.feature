@ApiOnly
@FuturesMarketApi
Feature: Validasi API Market Futures dari install coin liverate

  Scenario: Compare seluruh market kind futures dengan currency parent
    Given automation mengambil seluruh market dengan kind futures dari API install coin liverate
    When automation membandingkan label futures dengan currency parent
    Then seluruh market futures harus memiliki label sesuai format currency USDT-PERP
    And automation menampilkan summary market futures dari API
