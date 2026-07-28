package steps;

import java.util.List;
import java.util.Map;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import pages.BasePage;
import pages.dashboard.DashboardPage;
import pages.swap.SwapDashboardPage;
import pages.swap.SwapHistoryStatementPage;
import src.test.java.driver.DriverManager;
import pages.swap.SwapInputAmountPage;
import pages.swap.SwapConfirmationPage;

public class SwapAssetSteps extends BasePage {
    
    DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver());
    SwapDashboardPage swapDashboardPage = new SwapDashboardPage(DriverManager.getDriver());
    SwapInputAmountPage swapInputAmountPage = new SwapInputAmountPage(DriverManager.getDriver());
    SwapConfirmationPage swapConfirmationPage = new SwapConfirmationPage(DriverManager.getDriver());
    SwapHistoryStatementPage swapHistoryStatementPage = new SwapHistoryStatementPage(DriverManager.getDriver());

    @And("Swap aset secara custom")
    public void swap_asset_custom(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : data) {
            dashboardPage.clickBuySellIconOnDashboard();
            dashboardPage.clickSwapIconOnDashboard();

            // row.get("Code") akan mengambil nilai dari kolom "Code" di tabel feature
            String codeFrom = row.get("Code From");
            String codeTo = row.get("Code To");
            String categoryFrom = row.get("Category From");
            String categoryTo = row.get("Category To");
            String amount = row.get("Amount");

            // Sekarang kita panggil method-nya dengan data tersebut
            swapDashboardPage.selectCategory(categoryTo);
            swapDashboardPage.selectAssetByCode(codeTo);

            swapInputAmountPage.clickComboboxListWallet();
            swapInputAmountPage.getSearchboxComboboxListWallet(codeFrom);

            swapInputAmountPage.inputAmountAssetTo(amount, codeTo);

            swapInputAmountPage.clickLanjutButton();

            // Tambahkan sleep singkat untuk memberi waktu snackbar muncul
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

            // --- VALIDATION LOGIC ---
            String validationMessage = swapInputAmountPage.getValidationMessage();
            System.out.println("Pesan validasi yang diterima: " + validationMessage);

            // 1. Jika Minimum Swap, ambil angkanya, input ulang, dan lanjut
            if (validationMessage.contains("Minimum swap")) {
                System.out.println("Pesan validasi terdeteksi: " + validationMessage);
                
                // Regex: Mengganti semua karakter KECUALI angka (0-9) dan titik (.) dengan string kosong
                // Contoh: "Minimum swap is 0.00003849 BTC" -> "0.00003849"
                String minAmount = validationMessage.replaceAll("[^0-9.]", "");
                
                System.out.println("Nilai minimum yang diekstrak: " + minAmount);

                if (!minAmount.isEmpty()) {
                    swapInputAmountPage.inputAmountAssetTo(amount, codeTo);
                    swapInputAmountPage.clickLanjutButton();
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
                boolean isTransactionSuccess = swapConfirmationPage.clickKonfirmasiButton();

                if (isTransactionSuccess) {
                    swapHistoryStatementPage.clickDoneButtonHistoryStatement();
                } else {
                    System.out.println("Transaksi untuk " + code + " gagal saat konfirmasi.");
                    continue; // Lanjut ke aset berikutnya
                }
            }
            // 3. Jika "SUCCESS" atau tidak ada snackbar, lanjut ke proses konfirmasi
            
        }
    }

}
