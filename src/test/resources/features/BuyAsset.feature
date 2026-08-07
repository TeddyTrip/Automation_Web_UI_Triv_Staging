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
        | Code | 
        | BTC |
        | ETH |
        | PAXG |
        | USDT |
        | EURS |
    Then Masuk di Dashboard Triv Staging

    #mvn test "-Dcucumber.options=--tags @BuyFlowCSV"
    @BuyFlowCSV
    Scenario: Beli Beberapa Asset Custom via CSV
    Given Menjalankan flow "buy" dengan data "buy-assets" untuk buy
    And Membeli aset secara custom menggunakan data CSV
    Then Masuk di Dashboard Triv Staging

    #mvn test "-Dcucumber.options=--tags @BuyRandomAssetCategory"
    @BuyRandomAssetCategory
    Scenario: Membeli beberapa aset secara acak per kategori 
    Given Mengambil aset secara acak per kategori berdasarkan API install coin lists
    When Membeli aset secara acak per kategori berdasarkan API install coin lists
    Then Masuk di Dashboard Triv Staging

    #mvn clean test "-Dcucumber.options=--tags @BuyFlowCSVWithCertainAmount"
    @BuyFlowCSVWithCertainAmount
    Scenario: Beli Beberapa Asset Custom via CSV
    Given Menjalankan flow "buy" dengan data "buy-assets-with-certain-amount" untuk buy dengan amount dalam IDR
    And Membeli aset secara custom menggunakan data CSV buy dengan amount dalam IDR
    Then Masuk di Dashboard Triv Staging
    

