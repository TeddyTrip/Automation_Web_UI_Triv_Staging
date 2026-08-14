Feature: Transaksi Auto Invest

Background:
    Given Membuka halaman login web di cihuy
    And Memasukkan email dari variabel global
    And Memasukkan password dari variabel global
    And Menekan tombol Masuk
    And Menyelesaikan proses TwoFA jika diminta

    #mvn clean test "-Dcucumber.options=--tags @CreateAutoInvestRandom"
    @CreateAutoInvestRandom
    Scenario: Membuat transaksi Auto Invest secara random
    Given Membuka halaman Auto Invest
    When Mengambil aset secara acak per kategori berdasarkan API install coin lists untuk Auto Invest
    And Lakukan proses pembuatan transaksi Auto Invest secara random
    Then Masuk di Dashboard Triv Staging

    #mvn clean test "-Dcucumber.options=--tags @CreateAutoInvestUsingCSV"
    @CreateAutoInvestUsingCSV
    Scenario: Membuat transaksi Auto Invest menggunakan CSV
    Given Membuka halaman Auto Invest
    When Menjalankan flow "autoInvest" dengan data "autoInvest-assets" untuk create Auto Invest
    And Lakukan proses pembuatan transaksi Auto Invest secara berurutan
    Then Masuk di Dashboard Triv Staging
    