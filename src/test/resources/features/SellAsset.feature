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
      | Code | 
      | BTC  |
      | XAUT  |
      | USO  |
      | USDT  |
    Then Masuk di Dashboard Triv Staging

  #mvn test "-Dcucumber.options=--tags @SellFlowCSV"
  @SellFlowCSV
    Scenario: Jual Beberapa Asset Custom via CSV
    Given Menjalankan flow "sell" dengan data "sell-assets" untuk sell
    And Menjual aset secara custom menggunakan data CSV
    Then Masuk di Dashboard Triv Staging