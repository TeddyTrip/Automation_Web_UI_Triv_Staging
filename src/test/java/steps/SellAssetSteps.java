package steps;

import pages.dashboard.DashboardPage;
import pages.sell.SellConfirmationPage;
import pages.sell.SellDashboardPage;
import pages.sell.SellHistoryStatement;
import pages.sell.SellInputAmountPage;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import src.test.java.driver.DriverManager;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SellAssetSteps {
    
    DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver());
    SellConfirmationPage sellConfirmationPage = new SellConfirmationPage(DriverManager.getDriver());
    SellDashboardPage sellDashboardPage = new SellDashboardPage(DriverManager.getDriver());
    SellHistoryStatement sellHistoryStatement = new SellHistoryStatement(DriverManager.getDriver());
    SellInputAmountPage sellInputAmountPage = new SellInputAmountPage(DriverManager.getDriver());

    private static final Logger logger = LoggerFactory.getLogger(SellAssetSteps.class);

    @And("Menjual aset secara custom")
    public void menjual_asset_custom(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : data) {
            dashboardPage.clickBuySellIconOnDashboard();
            sellDashboardPage.clickSellIconOnDashboard();

            // row.get("Code") akan mengambil nilai dari kolom "Code" di tabel feature
            String code = row.get("Code");
            String category = row.get("Category");
            String amount = row.get("Amount");

            // Sekarang kita panggil method-nya dengan data tersebut
            sellDashboardPage.selectCategory(category);
            sellDashboardPage.selectAssetByCode(code);
            sellInputAmountPage.inputIdrAmount(amount); // Tambahkan ini jika ada input untuk IDR

            sellInputAmountPage.clickLanjutButton();

            // Tambahkan sleep singkat untuk memberi waktu snackbar muncul
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

            // --- VALIDATION LOGIC ---
            String validationMessage = sellInputAmountPage.getValidationMessage();
            System.out.println("Pesan validasi yang diterima: " + validationMessage);

            // 1. Jika Minimum Sell, ambil angkanya, input ulang, dan lanjut
            if (validationMessage.contains("Minimum sell")) {
                System.out.println("Pesan validasi terdeteksi: " + validationMessage);
                
                // Regex: Mengganti semua karakter KECUALI angka (0-9) dan titik (.) dengan string kosong
                // Contoh: "Minimum sell is 0.00003849 BTC" -> "0.00003849"
                String minAmount = validationMessage.replaceAll("[^0-9.]", "");
                
                System.out.println("Nilai minimum yang diekstrak: " + minAmount);

                if (!minAmount.isEmpty()) {
                    sellInputAmountPage.inputAssetAmount(minAmount);
                    sellInputAmountPage.clickLanjutButton();
                } else {
                    System.out.println("Gagal mengekstrak nilai minimum dari pesan. Skip aset.");
                    continue; // Melewati aset ini jika ekstraksi gagal
                }
            } 
            // 2. Jika saldo tidak cukup, log error dan skip ke aset berikutnya
            else if (validationMessage.contains("Can't process") || validationMessage.toLowerCase().contains("balance")) {
                System.out.println("Saldo tidak cukup untuk " + code + ". Melanjutkan ke aset berikutnya.");
                continue; 
            }
            else {
                    // --- PROCEED TO CONFIRMATION ---
                boolean isTransactionSuccess = sellConfirmationPage.clickKonfirmasiButton();

                if (isTransactionSuccess) {
                    sellHistoryStatement.clickDoneButtonHistoryStatement();
                } else {
                    System.out.println("Transaksi untuk " + code + " gagal saat konfirmasi.");
                    continue; // Lanjut ke aset berikutnya
                }
            }
            // 3. Jika "SUCCESS" atau tidak ada snackbar, lanjut ke proses konfirmasi
            
        }
    }
}
