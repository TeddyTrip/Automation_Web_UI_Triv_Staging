Feature: Pembelian Asset Custom

  Background:
    Given Membuka halaman login web di cihuy
    And Memasukkan email dari variabel global
    And Memasukkan password dari variabel global
    And Menekan tombol Masuk
    And Menyelesaikan proses TwoFA jika diminta

#mvn test "-Dcucumber.options=--tags @BuyFlowLengkap"
  @BuyFlowLengkap
  Scenario: Beli Beberapa Asset Custom
    And Membeli aset secara custom
      | Code | Category | Amount |
      | BTC  | crypto   | 50000  |
      | XAUT  | gold     | 100000 |
      | USO  | oil      | 75000  |
      | USDT  | usd      | 75000  |
    Then Masuk di Dashboard Triv Staging