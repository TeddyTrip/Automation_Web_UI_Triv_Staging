package steps;

import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import utils.CategoryAssetRandomizer;
import utils.ConfigReader;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import pages.dashboard.DashboardPage;
import pages.buy.BuyDashboardPage;
import pages.autoInvest.AutoInvestPage;
import pages.autoInvest.CreateAutoInvestPage;
import pages.buy.BuyConfirmationPage;
import pages.buy.BuyHistoryStatement;
import pages.buy.BuyInputAmountPage;
import src.test.java.driver.DriverManager;
import utils.CategoryAssetRandomizer;
import utils.ConfigReader;
import utils.CsvDataManager;
import utils.CsvUtils;
import utils.RandomAutoInvestFrequencyDayDate;
import context.ScenarioContext;
import formula.MinimalBuySellAssetSpotCalculation;

import java.util.*;

import org.junit.Assert;

import api.InstallCoinLists;

public class AutoInvestSteps {

    DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver());
    AutoInvestPage autoInvestPage = new AutoInvestPage(DriverManager.getDriver());
    InstallCoinLists installCoinLists = new InstallCoinLists();
    CreateAutoInvestPage createAutoInvestPage = new CreateAutoInvestPage(DriverManager.getDriver());
    RandomAutoInvestFrequencyDayDate randomAutoInvestFrequencyDayDate = new RandomAutoInvestFrequencyDayDate();

    private Map<String, List<Map<String, Object>>> randomAssetsPerCategory;
    
    @Given("Membuka halaman Auto Invest")
    public void membukaHalamanAutoInvest() {
        dashboardPage.clickAutoInvestIconOnDashboard();
    }

    @When("Mengambil aset secara acak per kategori berdasarkan API install coin lists untuk Auto Invest")
    public void mengambilAsetSecaraAcakPerKategoriBerdasarkanApiInstallCoinListsAutoInvest() {
        String nStr = ConfigReader.getProperty("jumlah_random_per_kategori");
        int n = Integer.parseInt(nStr != null ? nStr : "1");

        List<Map<String, Object>> allCoins = RestAssured
                .given()
                .when()
                .get("https://cihuy.triv.id/api/v1/install/coin/lists")
                .as(new TypeRef<List<Map<String, Object>>>() {});

        randomAssetsPerCategory = CategoryAssetRandomizer.getRandomPerCategory(allCoins, n, "category");
        CategoryAssetRandomizer.printSummaryReport(randomAssetsPerCategory);
    }

@And("Lakukan proses pembuatan transaksi Auto Invest secara random")
public void membuatTransaksiAutoInvestSecaraRandom() {
    Assert.assertNotNull("Random assets belum diinisialisasi!", randomAssetsPerCategory);

    // 2. Iterasi kategori dan aset hasil random
    for (Map.Entry<String, List<Map<String, Object>>> entry : randomAssetsPerCategory.entrySet()) {
        String category = entry.getKey();
        List<Map<String, Object>> coins = entry.getValue();

        for (Map<String, Object> coin : coins) {
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            
            // 1. KLIK TOMBOL LUAR: Masuk ke halaman form Create Auto Invest
            autoInvestPage.clickSetJadwalAutoTriv();
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            
            String code = String.valueOf(coin.get("code"));
            String labelFromApi = installCoinLists.getLabelFromApi(code);
            System.out.println("\n--- Memproses Aset: [" + code + "] " + labelFromApi + " ---");
            Assert.assertNotNull("Label untuk kode " + code + " tidak ditemukan di API!", labelFromApi);

            // 2. Pilih Aset di dalam form
            createAutoInvestPage.searchAndSelectAsset(code); 
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            // 3. Pilih Frekuensi
            String frequency = randomAutoInvestFrequencyDayDate.getRandomFrequency();
            createAutoInvestPage.selectFrequency(frequency);
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            
            String nominal = randomAutoInvestFrequencyDayDate.getRandomNominal();

            if ("weekly".equalsIgnoreCase(frequency)) {
                // Percobaan pertama: Pilih hari random (bisa kena Sabtu/Minggu)
                String day = randomAutoInvestFrequencyDayDate.getRandomDay();
                createAutoInvestPage.selectWeeklyDay(day);
                try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                
                createAutoInvestPage.selectNominalButton(nominal);
                try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                
                // Konfirmasi transaksi pertama kali
                createAutoInvestPage.confirmAutoInvest();
                
                // 🛑 JEDA PENTING: Berikan waktu 1.5 detik agar alert (error/sukses) sempat muncul di DOM
                try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                // =========================================================================
                // PENANGANAN ERROR WEEKEND (BERADA DI DALAM HALAMAN FORM YANG SAMA)
                // =========================================================================
                while (createAutoInvestPage.isWeekendErrorAlertDisplayed()) {
                    System.out.println("⚠️ Alert weekend terdeteksi di dalam form. Memperbaiki hari tanpa keluar halaman...");
                    
                    // Ganti hari ke weekday yang valid (Senin - Jumat) di form yang sama
                    String validWeekday = randomAutoInvestFrequencyDayDate.getRandomWeekday();
                    createAutoInvestPage.selectWeeklyDay(validWeekday);
                    try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    
                    // Klik konfirmasi ulang
                    createAutoInvestPage.confirmAutoInvest();
                    
                    // Jeda lagi untuk menunggu render alert berikutnya
                    try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }

                // Setelah benar-benar lolos dari error weekend, tunggu alert sukses selesai
                try {
                    createAutoInvestPage.waitForSuccessAlertToComplete();
                } catch (Exception e) {
                    System.out.println("⚠️ Alert sukses selesai / tertutup.");
                }

            } else {
                // Jika frekuensi monthly
                String date = randomAutoInvestFrequencyDayDate.getRandomMonthlyDate();
                createAutoInvestPage.selectMonthlyDate(date);
                try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                
                createAutoInvestPage.selectNominalButton(nominal);
                try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                
                createAutoInvestPage.confirmAutoInvest();
                try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                createAutoInvestPage.waitForSuccessAlertToComplete();
            }
        }
    }
}
}
