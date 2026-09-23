Feature: Beli Gift Card

  Background:
    Given Membuka halaman login web di cihuy
    And Memasukkan email dari variabel global
    And Memasukkan password dari variabel global
    And Menekan tombol Masuk
    And Menyelesaikan proses TwoFA jika diminta

  Scenario:
    When User mengklik ikon Gift Card
    Then User diarahkan ke halaman Gift Card
    When User memilih seluruh tema Gift Card
    Then User melanjutkan langkah berikutnya pada form gift card dengan aset "RANDOM"