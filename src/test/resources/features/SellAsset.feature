Feature: Penjualan Asset Custom

  Background:
    Given Membuka halaman login web di cihuy
    And Memasukkan email dari variabel global
    And Memasukkan password dari variabel global
    And Menekan tombol Masuk
    And Menyelesaikan proses TwoFA jika diminta

#mvn test "-Dcucumber.options=--tags @SellFlowLengkap"
  @SellFlowLengkap
  Scenario: Jual Beberapa Asset Custom
    And Menjual aset secara custom
      | Code | Category | Amount |
      | BTC  | crypto   | 5000  |
      | XAUT  | gold     | 60000 |
      | USO  | oil      | 7500000  |
      | EXE  | stocks      | 7500000  |
    Then Masuk di Dashboard Triv Staging