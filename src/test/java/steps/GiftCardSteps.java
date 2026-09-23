package steps;

import api.InstallCoinDetails;
import api.InstallCoinLists;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.dashboard.DashboardPage;
import pages.giftCard.GiftCardFormPage;
import pages.giftCard.GiftCardPage;
import pages.giftCard.GiftCardPreviewPage;
import pages.giftCard.GiftCardSelectPage;
import src.test.java.driver.DriverManager;
import utils.CategoryAssetRandomizer;
import utils.ConfigReader;
import utils.GiftCardApiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GiftCardSteps {

    GiftCardPage giftCardPage = new GiftCardPage(DriverManager.getDriver());
    DashboardPage dashboardPage = new DashboardPage(DriverManager.getDriver());
    InstallCoinLists installCoinLists = new InstallCoinLists();
    GiftCardFormPage giftCardFormPage = new GiftCardFormPage(DriverManager.getDriver());
    InstallCoinDetails installCoinDetails = new InstallCoinDetails();
    GiftCardSelectPage giftCardSelectPage = new GiftCardSelectPage(DriverManager.getDriver());
    GiftCardPreviewPage giftCardPreviewPage = new GiftCardPreviewPage(DriverManager.getDriver());

    @When("User mengklik ikon Gift Card")
    public void user_mengklik_ikon_gift_card() {
        dashboardPage.clickGiftCardIconDashboard();
    }

    @Then("User diarahkan ke halaman Gift Card")
    public void user_diarahkan_ke_halaman_gift_card() {
        giftCardPage.clickBtnKirimGiftCard();
    }

    @When("User memilih seluruh tema Gift Card")
    public void userMemilihSeluruhTemaGiftCard() {
        Map<String, Object> templateData = GiftCardApiUtils.getRandomTemplateByEvent();

        String eventName = templateData.get("event").toString();       // misal: "congratulations"
        String templateCode = templateData.get("template").toString(); // misal: "template01"

        System.out.println("[API Target UI] Selected Event: " + eventName + " | Selected Template: " + templateCode);

        // 2. Pilih tema pada dropdown berdasarkan value event
        giftCardSelectPage.selectGiftTemplateByValue(eventName);

        // 3. Klik gambar template spesifik berdasarkan data dari API (bukan thumbnail)
        giftCardSelectPage.selectCardPicture(eventName.toLowerCase(), templateCode);

        // 4. Klik tombol Simpan
        giftCardSelectPage.clickBtnSimpan();
    }

    @Then("User melanjutkan langkah berikutnya pada form gift card dengan aset {string}")
    public void userMelanjutkanLangkahBerikutnyaPadaFormGiftCardDenganAset(String vMoney) {
        List<String> assetQueue = new ArrayList<>();

        if ("RANDOM".equalsIgnoreCase(vMoney)) {
            // 1. Ambil data aset & parse JSON
            List<Map<String, Object>> allAssets = installCoinLists.getAllRawAssetsFromApi();

            // 2. Baca jumlah_random_per_kategori dari config.properties (default 2 jika kosong)
            String configLimit = ConfigReader.getProperty("jumlah_random_per_kategori");
            int limitPerCategory = (configLimit != null) ? Integer.parseInt(configLimit) : 2;

            // 3. Acak aset berdasarkan jumlah limit per kategori
            Map<String, List<Map<String, Object>>> randomMap =
                    CategoryAssetRandomizer.getRandomPerCategory(allAssets, limitPerCategory, "category");

            CategoryAssetRandomizer.printSummaryReport(randomMap);

            // 4. Ekstrak seluruh v_money ke antrean
            assetQueue = CategoryAssetRandomizer.extractVMoneyList(randomMap);
        } else {
            String target = installCoinLists.getV_MoneyFromApi(vMoney);
            assetQueue.add(target != null ? target : vMoney);
        }

        int attemptIndex = 0;
        int totalLoopTarget = assetQueue.size();

        // Loop akan TERUS BERJALAN sebanyak jumlah aset di assetQueue
        do {
            String targetVMoney = assetQueue.get(attemptIndex); // Berisi label (misal: "Bitcoin")

            // Konversi label ke code (misal: "Bitcoin" -> "BTC")
            String assetCode = installCoinLists.getCodeFromLabel(targetVMoney);

            System.out.println("\n[UI Test] (Iterasi " + (attemptIndex + 1) + " dari " + totalLoopTarget
                    + ") Memproses form dengan aset: " + targetVMoney + " (" + assetCode + ")");

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
                System.out.println("[UI Test] Popup alert error muncul! Menutup popup & lanjut ke iterasi berikutnya...");
                giftCardPreviewPage.closePopupIfPresent();
                giftCardPreviewPage.clickBtnBackToForm();
            }
            // 4. Kondisi B: Jika Halaman Input OTP Tampil
            else if (giftCardPreviewPage.isOtpInputPresent()) {
                System.out.println("[UI Test] Halaman OTP muncul. Memproses input OTP...");

                String emailUser = ConfigReader.getProperty("email_global");
                giftCardPreviewPage.submitOtpIfPresent(emailUser);

                // Tunggu 1 menit jika berhasil buat gift card
                System.out.println("[UI Test] Menunggu jeda 1 menit (60 detik) agar sistem siap mengirim gift card berikutnya...");
                try {
                    Thread.sleep(60000); // 60.000 ms = 1 menit
                } catch (InterruptedException e) {
                    System.out.println("[UI Warning] Jeda waktu terinterupsi: " + e.getMessage());
                }

                System.out.println("[UI Test] OTP Berhasil diinput! Menekan tombol Kembali untuk iterasi berikutnya...");

                // Wajib klik Kembali ke form agar bisa lanjut ke transaksi koin berikutnya
                giftCardPreviewPage.clickBtnBackToForm();
            }
            // 5. Kondisi C: Tidak ada OTP & Popup -> Kembali ke form
            else {
                System.out.println("[UI Test] Halaman OTP tidak muncul. Kembali ke form untuk koin berikutnya...");
                giftCardPreviewPage.clickBtnBackToForm();
            }

            attemptIndex++;

            // Tetap looping sampai seluruh daftar koin di assetQueue habis diproses
        } while (attemptIndex < totalLoopTarget);

        System.out.println("\n[UI Test] Selesai menjalankan seluruh looping pengujian (" + totalLoopTarget + " kali).");
    }
}
