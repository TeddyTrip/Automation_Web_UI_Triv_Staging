@LoginTest
Feature: Fitur Login Website
  Scenario: Login sukses dengan akun yang benar
    Given Membuka halaman login web di cihuy
    When Memasukkan email dari variabel global
    And Memasukkan password dari variabel global
    And Menekan tombol Masuk
    And Menyelesaikan proses TwoFA jika diminta
    Then Masuk di Dashboard Triv Staging