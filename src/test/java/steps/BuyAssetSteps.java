package steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import pages.dashboard.DashboardPage;
import pages.buy.BuyDashboardPage;
import pages.buy.BuyConfirmationPage;
import pages.buy.BuyHistoryStatement;
import pages.buy.BuyInputAmountPage;
import src.test.java.driver.DriverManager;
import java.util.*;

public class BuyAssetSteps {

    DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver());
    BuyConfirmationPage buyConfirmationPage = new BuyConfirmationPage(DriverManager.getDriver());
    BuyHistoryStatement buyHistoryStatement = new BuyHistoryStatement(DriverManager.getDriver());
    BuyDashboardPage buyDashboardPage = new BuyDashboardPage(DriverManager.getDriver());
    BuyInputAmountPage buyInputAmountPage = new BuyInputAmountPage(DriverManager.getDriver());

    @And("Membeli aset secara custom")
    public void membeli_asset_custom(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        

        for (Map<String, String> row : data) {
            dashboardPage.clickBuySellIconOnDashboard();
            
            // row.get("Code") akan mengambil nilai dari kolom "Code" di tabel feature
            String code = row.get("Code");
            String category = row.get("Category");
            String amount = row.get("Amount");

            // Sekarang kita panggil method-nya dengan data tersebut
            buyDashboardPage.selectCategory(category);
            buyDashboardPage.selectAssetByCode(code);
            buyInputAmountPage.inputAmountInIDR(amount);

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

