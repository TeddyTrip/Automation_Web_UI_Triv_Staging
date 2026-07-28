Feature: Swap Asset
  Background:
    Given Membuka halaman login web di cihuy
    And Memasukkan email dari variabel global
    And Memasukkan password dari variabel global
    And Menekan tombol Masuk
    And Menyelesaikan proses TwoFA jika diminta

    #mvn test "-Dcucumber.options=--tags @SwapFlowCustom"
    @SwapFlowCustom
    Scenario: Swap Beberapa Asset Custom
    And Swap aset secara custom
      | Code From | Code To | Category From | Category To | Amount |
      | BTC       | USDT    | crypto        | usd         | 50000  |
      | USDT      | PAXG    | usd           | gold        | 50000  |
      | XAUT      | XLM     | gold          | crypto      | 50000  |
      | EURS      | RYAAY   | euro          | stocks      | 50000  |
      | TSLA      | YB      | stocks        | crypto      | 50000  |
    Then Masuk di Dashboard Triv Staging