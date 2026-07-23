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
      | SPYX  | crypto   | 50000  |
      | MSFTON  | crypto   | 50000 |
      | SLVON  | gold   | 50000  |
      | USOON  | oil   | 50000 |
      | COPXON  | stocks   | 50000 |
    Then Masuk di Dashboard Triv Staging

    #mvn test "-Dcucumber.options=--tags @BuyFlowCSV"
    @BuyFlowCSV
    Scenario: Beli Beberapa Asset Custom via CSV
    Given Menjalankan flow "buy" dengan data "buy-assets"
    And Membeli aset secara custom menggunakan data CSV
    Then Masuk di Dashboard Triv Staging