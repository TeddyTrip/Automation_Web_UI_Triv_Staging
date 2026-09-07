Feature: Transaksi PPOB

Background:
    Given Membuka halaman login web di cihuy
    And Memasukkan email dari variabel global
    And Memasukkan password dari variabel global
    And Menekan tombol Masuk
    And Menyelesaikan proses TwoFA jika diminta

    #mvn clean test "-Dcucumber.options=--tags @CreatePPOBRandom"
    @CreatePPOBRandom
    Scenario: Membuat transaksi PPOB secara random
    Given Membuka halaman PPOB
    When Memilih transaksi salah satu transaksi PPOB secara random
    And Lakukan proses pembuatan transaksi PPOB 
    Then Masuk di Dashboard Triv Staging