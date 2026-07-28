package steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import pages.dashboard.DashboardPage;
import pages.buy.BuyDashboardPage;
import pages.buy.BuyConfirmationPage;
import pages.buy.BuyHistoryStatement;
import pages.buy.BuyInputAmountPage;
import src.test.java.driver.DriverManager;
import utils.CsvDataManager;
import utils.CsvUtils;
import context.ScenarioContext;
import formula.MinimalBuySellAssetSpotCalculation;

import java.util.*;

public class BuyAssetSteps {

    DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver());
    BuyConfirmationPage buyConfirmationPage = new BuyConfirmationPage(DriverManager.getDriver());
    BuyHistoryStatement buyHistoryStatement = new BuyHistoryStatement(DriverManager.getDriver());
    BuyDashboardPage buyDashboardPage = new BuyDashboardPage(DriverManager.getDriver());
    BuyInputAmountPage buyInputAmountPage = new BuyInputAmountPage(DriverManager.getDriver());
    MinimalBuySellAssetSpotCalculation minimalBuyAssetSpotCalculation = new MinimalBuySellAssetSpotCalculation();
    
    ScenarioContext context = new ScenarioContext();
    CsvUtils csvUtils = new CsvUtils();

    @And("Membeli aset secara custom")
    public void membeli_asset_custom(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        

        for (Map<String, String> row : data) {
            dashboardPage.clickBuySellIconOnDashboard();
            
            // row.get("Code") akan mengambil nilai dari kolom "Code" di tabel feature
            String code = row.get("Code");
            String category = row.get("Category");

            // Sekarang kita panggil method-nya dengan data tersebut
            buyDashboardPage.selectCategory(category);
            buyDashboardPage.selectAssetByCode(code);
            buyInputAmountPage.inputAmountInIDRUsingMinimumBuyTransaction(code);

            buyInputAmountPage.clickLanjutButton();

            boolean isTransactionSuccess = buyConfirmationPage.clickKonfirmasiButton();

            if (isTransactionSuccess) {
                buyHistoryStatement.clickDoneButtonHistoryStatement();
            } else {
                // Jika isSuccess = false, kita tidak klik 'Done'. 
                // Loop akan lanjut ke item berikutnya. 
                // Karena di awal loop ada 'dashboardPage.clickBuyIconOnDashboard()',
                // sistem akan otomatis pindah ke proses berikutnya dengan bersih.
                System.out.println("Transaksi untuk " + code + " gagal karena Market Tutup, lanjut ke asset berikutnya.");
            }

        
        }
    }

    
    @Given("Menjalankan flow {string} dengan data {string} untuk buy")
    public void load_data_dinamis_buy(String flow, String file) throws Exception {
        // 1. Dapatkan path lengkap (contoh: src/test/resources/data/buy/buy-assets.csv)
        String path = CsvDataManager.getPath(flow, file);
        
        // 2. Baca file CSV dan simpan ke dalam context
        List<Map<String, String>> data = CsvUtils.readData(path);
        context.setContext("csvData", data);
        
        System.out.println("Data berhasil dimuat dari: " + path);
    }


    @And("Membeli aset secara custom menggunakan data CSV")
    public void membeli_aset_dari_csv() {
        // Ambil data dari context dan cast kembali ke bentuk List Map
        List<Map<String, String>> data = (List<Map<String, String>>) context.getContext("csvData");
        
        for (Map<String, String> row : data) {
            dashboardPage.clickBuySellIconOnDashboard();
            
            // Akses data menggunakan nama kolom yang ada di CSV (Case Sensitive)
            String code = row.get("Code");
            String market_service = row.get("Market_Service");
            String category = row.get("Category");

            System.out.println("Processing: " + code + " | Market Service: " + market_service + " | Category: " + category);
            
            // Sekarang kita panggil method-nya dengan data tersebut
            buyDashboardPage.selectCategory(category);
            buyDashboardPage.selectAssetByCode(code);
            buyInputAmountPage.inputAmountInIDRUsingMinimumBuyTransaction(code);

            buyInputAmountPage.clickLanjutButton();

            boolean isTransactionSuccess = buyConfirmationPage.clickKonfirmasiButton();

            if (isTransactionSuccess) {
                buyHistoryStatement.clickDoneButtonHistoryStatement();
            } else {
                // Jika isSuccess = false, kita tidak klik 'Done'. 
                // Loop akan lanjut ke item berikutnya. 
                // Karena di awal loop ada 'dashboardPage.clickBuyIconOnDashboard()',
                // sistem akan otomatis pindah ke proses berikutnya dengan bersih.
                System.out.println("Transaksi untuk " + code + " gagal karena Market Tutup, lanjut ke asset berikutnya.");
            }
        }
    }






}

