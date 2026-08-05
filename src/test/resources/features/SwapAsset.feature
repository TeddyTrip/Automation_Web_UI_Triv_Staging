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
    | UNI       | MCDON   | crypto        | crypto      | 50000  |
    | DASH      | MCDX    | crypto        | crypto      | 50000  |
    | YB        | METAON  | crypto        | crypto      | 50000  |
    | NOK       | MSTRON  | stocks        | crypto      | 50000  |
    | AMD       | MUON    | stocks        | crypto      | 50000  |
    | AAPL      | NVDAON  | stocks        | crypto      | 50000  |
    | SPCX      | NVDAX   | stocks        | crypto      | 50000  |
    | RYAAY     | NVOON   | stocks        | crypto      | 50000  |
    | TLTON     | ONDSON  | crypto        | crypto      | 50000  |
    | AGGON     | ORCLON  | crypto        | crypto      | 50000  |
    | SGOVON    | PGON    | crypto        | crypto      | 50000  |
    | GENIUS    | SBETON  | crypto        | crypto      | 50000  |
    | GRT       | SBUXON  | crypto        | crypto      | 50000  |
    | NFLX      | SPCXON  | stocks        | crypto      | 50000  |
    | TSLA      | SPCXX   | stocks        | crypto      | 50000  |
    | PFE       | SPOTON  | stocks        | crypto      | 50000  |
    | INDA      | TSLAON  | stocks        | crypto      | 50000  |
    | SPCX      | URAON   | stocks        | crypto      | 50000  |
    | AMDON     | VON     | crypto        | crypto      | 50000  |
    | SLVON     | XOMON   | crypto        | crypto      | 50000  |
    Then Masuk di Dashboard Triv Staging


    #mvn test "-Dcucumber.options=--tags @SwapFlowCSV"
    @SwapFlowCSV
    Scenario: Menukar Beberapa Asset Custom via CSV
    Given Menjalankan flow "swap" dengan data "swap-assets" untuk swap
    And Menukar aset secara custom menggunakan data CSV
    Then Masuk di Dashboard Triv Staging