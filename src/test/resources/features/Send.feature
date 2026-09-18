Feature: Transaksi Send Asset

Background:
    Given Membuka halaman login web di cihuy
    And Memasukkan email dari variabel global
    And Memasukkan password dari variabel global
    And Menekan tombol Masuk
    And Menyelesaikan proses TwoFA jika diminta

    #mvn clean test "-Dcucumber.options=--tags @SendAssetRandomly"
    @SendAssetRandomly
    Scenario: Membuat transaksi Send Asset secara random
    Given Mengambil aset secara acak per kategori berdasarkan API install coin lists untuk Send Asset
    And Lakukan proses Send Asset secara random
    Then Masuk di Dashboard Triv Staging