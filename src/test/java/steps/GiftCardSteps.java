package steps;

import api.InstallCoinDetails;
import api.InstallCoinLists;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.dashboard.DashboardPage;
import pages.giftCard.*;
import src.test.java.driver.DriverManager;
import utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class GiftCardSteps {

    InstallCoinLists installCoinLists = new InstallCoinLists();
    InstallCoinDetails installCoinDetails = new InstallCoinDetails();
    GiftCardPage giftCardPage = new GiftCardPage(DriverManager.getDriver());
    DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver());
    GiftCardFormPage giftCardFormPage = new GiftCardFormPage(DriverManager.getDriver());
    GiftCardSelectPage giftCardSelectPage = new GiftCardSelectPage(DriverManager.getDriver());
    GiftCardMyCardPage giftCardMyCardPage = new GiftCardMyCardPage(DriverManager.getDriver());
    GiftCardPreviewPage giftCardPreviewPage = new GiftCardPreviewPage(DriverManager.getDriver());

    int attemptIndex = 0;
    List<String> assetQueue = new ArrayList<>();

    @When("User mengklik ikon Gift Card")
    public void user_mengklik_ikon_gift_card() {
        System.out.println("\n[UI Test] Mengklik ikon Gift Card untuk membuka form transaksi...");
        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        if (currentUrl.contains("/my-cards")) {
            DriverManager.getDriver().get("https://cihuy.triv.id/dashboard");
        }
        dashboardPage.clickGiftCardIconDashboard();
    }

    @Then("User diarahkan ke halaman Gift Card")
    public void user_diarahkan_ke_halaman_gift_card() {
        giftCardPage.clickBtnKirimGiftCard();
    }

    @When("User memilih seluruh tema Gift Card")
    public void user_memilih_seluruh_tema_gift_card() {
        Map<String, Object> templateData = GiftCardApiUtils.getRandomTemplateByEvent();

        String eventName = templateData.get("event").toString();       // misal: "congratulations"
        String templateCode = templateData.get("template").toString(); // misal: "template01"

        System.out.println("[API Target UI] Selected Event: " + eventName + " | Selected Template: " + templateCode);

        // 2. Pilih tema pada dropdown berdasarkan value event
        giftCardSelectPage.selectGiftTemplateByValue(eventName);

        // 3. Klik gambar template spesifik berdasarkan data dari API
        giftCardSelectPage.selectCardPicture(eventName.toLowerCase(), templateCode);

        // 4. Klik tombol Simpan
        giftCardSelectPage.clickBtnSimpan();
    }

    @Then("User melanjutkan langkah berikutnya pada form gift card dengan aset {string}")
    public void user_melanjutkan_langkah_berikutnya_pada_form_gift_card_dengan_aset(String vMoney) {
        // Inisialisasi antrean koin HANYA pada eksekusi pertama (ketika antrean masih kosong)
        if (assetQueue.isEmpty()) {
            switch (vMoney.toUpperCase()) {
                case "RANDOM":
                    List<Map<String, Object>> allAssets = installCoinLists.getAllRawAssetsFromApi();

                    String configLimit = ConfigReader.getProperty("jumlah_random_per_kategori");
                    int limitPerCategory = (configLimit != null) ? Integer.parseInt(configLimit) : 2;

                    Map<String, List<Map<String, Object>>> randomMap =
                            CategoryAssetRandomizer.getRandomPerCategory(allAssets, limitPerCategory, "category");

                    CategoryAssetRandomizer.printSummaryReport(randomMap);

                    assetQueue = CategoryAssetRandomizer.extractVMoneyList(randomMap);
                    break;

                case "CSV":
                    try {
                        // 1. Ambil path file CSV (misal: "src/test/resources/data/gift-card-assets.csv" atau via CsvDataManager)
                        String csvPath = CsvDataManager.getPath("giftCard", "gift-card-assets");

                        // 2. Baca file CSV
                        List<Map<String, String>> csvData = CsvUtils.readData(csvPath);

                        // 3. Loop setiap baris data di CSV
                        for (Map<String, String> row : csvData) {
                            // Ambil nilai dari kolom "code" atau "Code" (sesuai header CSV)
                            String coinCode = row.get("code");
                            if (coinCode == null || coinCode.isEmpty()) {
                                coinCode = row.get("Code");
                            }

                            if (coinCode != null && !coinCode.isEmpty()) {
                                // Konversi kode (misal: BTC) ke Label UI lengkap (misal: Bitcoin)
                                String labelUi = installCoinLists.getLabelFromApi(coinCode.trim());

                                // Jika label ditemukan dari API, masukkan label. Jika null, gunakan kode aslinya
                                assetQueue.add(labelUi != null ? labelUi : coinCode.trim());
                            }
                        }
                        System.out.println("[UI Test] Antrean aset berhasil dimuat dari CSV: " + assetQueue);
                    } catch (Exception e) {
                        System.out.println("[UI Error] Gagal membaca data aset dari CSV: " + e.getMessage());
                    }
                    break;

                default:
                    String target = installCoinLists.getV_MoneyFromApi(vMoney);
                    assetQueue.add(target != null ? target : vMoney);
                    break;
            }
        }

        int totalLoopTarget = assetQueue.size();

        // Cek apakah masih ada koin di antrean yang belum diproses
        if (attemptIndex < totalLoopTarget) {
            String targetVMoney = assetQueue.get(attemptIndex); // Berisi label (misal: "Bitcoin")

            // Konversi label ke code (misal: "Bitcoin" -> "BTC")
            String assetCode = installCoinLists.getCodeFromLabel(targetVMoney);

            System.out.println("\n[UI Test] (Iterasi " + (attemptIndex + 1) + " dari " + totalLoopTarget
                    + ") Memproses form dengan aset: " + targetVMoney);

            // 1. Isi form & Submit
            giftCardFormPage.selectCurrencyByVmoney(targetVMoney);

            // Panggil API getMinimalBuyFromApi
            double minBuy = installCoinDetails.getMinimalBuyFromApi(assetCode);
            String nominalInput = java.math.BigDecimal.valueOf(minBuy).toPlainString();

            System.out.println("[UI Test] Minimal buy untuk " + assetCode + ": " + minBuy);
            giftCardFormPage.inputNominalAmount(nominalInput);
            giftCardFormPage.fillDeliveryForm();
            giftCardFormPage.clickBtnSubmitPayment();

            // 2. Klik Lanjut Pembayaran
            giftCardPreviewPage.clickBtnNextPayment();

            // 3. Kondisi A: Jika Popup Error Tampil
            if (giftCardPreviewPage.isPopupPresent()) {
                System.out.println("[UI Test] Popup alert error muncul! Menutup popup...");
                giftCardPreviewPage.closePopupIfPresent();

                // Kembali ke form
                giftCardPreviewPage.clickBtnBackToForm();

                // Naikkan indeks iterasi koin
                attemptIndex++;

                // Jika masih ada sisa koin, jalankan transaksi koin berikutnya langsung di form yang sama
                if (attemptIndex < totalLoopTarget) {
                    user_melanjutkan_langkah_berikutnya_pada_form_gift_card_dengan_aset(vMoney);
                } else {
                    System.out.println("\n[UI Test] Selesai menjalankan seluruh pengujian (" + totalLoopTarget + " kali).");
                    assetQueue.clear();
                    attemptIndex = 0;
                }
                return;
            }
            // 4. Kondisi B: Jika Halaman Input OTP Tampil
            else if (giftCardPreviewPage.isOtpInputPresent()) {
                System.out.println("[UI Test] Halaman OTP muncul. Memproses input OTP...");

                String emailUser = ConfigReader.getProperty("email_global");
                giftCardPreviewPage.submitOtpIfPresent(emailUser);

                // Tunggu 0.5 detik jika berhasil buat gift card
                System.out.println("[UI Test] Menunggu jeda 0.5 detik (0.5 detik) agar sistem siap...");

                WaitUtils.waitForSeconds(0.5);

                // 1. Pindah dari preview ke halaman my-cards
                System.out.println("[UI Test] Membuka halaman My Cards...");
                giftCardPreviewPage.clickBtnSelesai();

                // 2. Klik titik tiga (• • •) lalu klik tombol Selesai di dalam popup
                giftCardMyCardPage.openCardDetailAndClickSelesai();
                WaitUtils.waitForSeconds(60);
            }
            // 5. Kondisi C: Tidak ada OTP & Popup -> Kembali ke form
            else {
                System.out.println("[UI Test] Halaman OTP tidak muncul. Kembali ke form...");
                giftCardPreviewPage.clickBtnBackToForm();
            }


            // Naikkan indeks iterasi koin
            attemptIndex++;

            // Jika masih ada sisa koin di antrean, jalankan siklus navigasi penuh dari awal
            if (attemptIndex < totalLoopTarget) {
                user_mengklik_ikon_gift_card();
                user_diarahkan_ke_halaman_gift_card();
                user_memilih_seluruh_tema_gift_card();
                user_melanjutkan_langkah_berikutnya_pada_form_gift_card_dengan_aset(vMoney);
            } else {
                System.out.println("\n[UI Test] Selesai menjalankan seluruh pengujian ("
                        + totalLoopTarget + " kali).");
                // Reset state antrean setelah seluruh loop selesai
                assetQueue.clear();
                attemptIndex = 0;
            }
        }
    }
}