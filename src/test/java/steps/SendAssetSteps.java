package steps;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import context.ScenarioContext;
import formula.MinimalBuySellAssetSpotCalculation;
import formula.MinimalWithdrawAmountPlusPercentage;
import helper.EmailAddressMemoIDSend;
import helper.ProtocolInfo;
import helper.SelectedAssetAndProtocol;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import pages.buy.BuyConfirmationPage;
import pages.buy.BuyDashboardPage;
import pages.buy.BuyHistoryStatement;
import pages.buy.BuyInputAmountPage;
import pages.dashboard.DashboardPage;
import pages.send.SendConfirmationPage;
import pages.send.SendDashboardPage;
import pages.send.SendTransactionSuccess;
import pages.send.SendVerificationEmailPage;
import utils.CategoryAssetRandomizer;
import utils.ConfigReader;
import utils.CsvUtils;
import src.test.java.driver.DriverManager;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.CsvDataManager;
import utils.CsvUtils;

import api.AddressBookNetworkList;
import api.AuthInfo;
import api.InstallCoinDetails;
import api.InstallCoinLists;

public class SendAssetSteps {

    // 1. Deklarasikan variabel dan page objects di atas agar rapi
    private WebDriver driver;
    private WebDriverWait wait;
    
    private DashboardPage dashboardPage;
    private BuyConfirmationPage buyConfirmationPage;
    private BuyHistoryStatement buyHistoryStatement;
    private BuyDashboardPage buyDashboardPage;
    private BuyInputAmountPage buyInputAmountPage;
    private InstallCoinLists installCoinLists;
    private AddressBookNetworkList addressBookNetworkList;
    private SendDashboardPage sendDashboardPage;
    private EmailAddressMemoIDSend emailAddressMemoIDSend;
    private SendConfirmationPage sendConfirmationPage;
    private SendVerificationEmailPage sendVerificationEmailPage;
    private AuthInfo authInfo;
    private MinimalWithdrawAmountPlusPercentage minimalWithdrawAmountPlusPercentage;
    private SendTransactionSuccess sendTransactionSuccess;
    
    private MinimalBuySellAssetSpotCalculation minimalBuyAssetSpotCalculation;
    private ScenarioContext context;
    private CsvUtils csvUtils;

    private Map<String, List<Map<String, Object>>> randomAssetsPerCategory;
    String email = ConfigReader.getProperty("email_global");
    String otpCode = null;

    String csvSend = ConfigReader.getProperty("path_crypto_currency_protocol_with_email_address")
                             .replace("\"", "")
                             .trim();
    
    // 2. Inisialisasi nilainya di dalam @Before hook (aman dari null karena dijalankan saat test mulai)
    @Before
    public void setUp() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        this.dashboardPage = new DashboardPage(driver, wait);
        this.buyConfirmationPage = new BuyConfirmationPage(driver);
        this.buyHistoryStatement = new BuyHistoryStatement(driver);
        this.buyDashboardPage = new BuyDashboardPage(driver);
        this.buyInputAmountPage = new BuyInputAmountPage(driver);
        this.installCoinLists = new InstallCoinLists();
        this.addressBookNetworkList = new AddressBookNetworkList();
        this.sendDashboardPage = new SendDashboardPage(driver);
        this.emailAddressMemoIDSend = new EmailAddressMemoIDSend(null, null, null);
        this.sendConfirmationPage = new SendConfirmationPage(driver);
        this.sendVerificationEmailPage = new SendVerificationEmailPage(driver);
        this.authInfo = new AuthInfo();
        this.minimalWithdrawAmountPlusPercentage = new MinimalWithdrawAmountPlusPercentage();
        this.sendTransactionSuccess = new SendTransactionSuccess(driver);
        
        this.minimalBuyAssetSpotCalculation = new MinimalBuySellAssetSpotCalculation();
        this.context = new ScenarioContext();
        this.csvUtils = new CsvUtils();
    }

    @And("Mengambil aset secara acak per kategori berdasarkan API install coin lists untuk Send Asset")
    public void mengambilAsetSecaraAcakPerKategoriBerdasarkanApiInstallCoinListsSendAsset() {
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

    @Given("Lakukan proses Send Asset secara random")
    public void proceedToSendAsset() {
    // List<SelectedAssetAndProtocol> randomAssets = addressBookNetworkList.getRandomAssetsAndProtocols();
    // Assert.assertFalse("Random assets list kosong atau gagal digenerate!", randomAssets.isEmpty());

    Assert.assertNotNull("Random assets belum diinisialisasi!", randomAssetsPerCategory);

    dashboardPage.clickSearchIconOnDashboard();

    for (Map.Entry<String, List<Map<String, Object>>> entry : randomAssetsPerCategory.entrySet()) {
        
        List<Map<String, Object>> coins = entry.getValue();

        for (Map<String, Object> coin : coins) {
            String code = String.valueOf(coin.get("code"));

            dashboardPage.searchWalletByVMoney(code);

            System.out.println("🚀 Memproses Aset: " + code);

            boolean canSendViaUI = dashboardPage.hoverAndClickWithdrawButton(code);
            if (!canSendViaUI) {
                System.out.println("⏭️ Aset " + code + " tidak memiliki tombol withdraw di UI, melewati...");
                continue; 
            }
            else{
                try { Thread.sleep(20000); } catch (InterruptedException e) { e.printStackTrace(); }

                ProtocolInfo protocolInfo = addressBookNetworkList.getRandomProtocolForCurrency(code);
                String protocol = protocolInfo.getProtocol();
                boolean isOptionalMemoId = protocolInfo.isOptionalMemoId();

                // Ambil nilai min_withdraw dari data API atau tentukan nilainya sesuai batas minimum
                double minWithdraw = minimalWithdrawAmountPlusPercentage.getMinimalWithdrawPriceWithMinerFee(code, protocol);

                // Ubah ke String (pastikan nilainya tidak di bawah minimum agar transaksi valid)
                String amountToSent = String.valueOf(minWithdraw);

                System.out.println("🚀 Memproses Aset: " + code + " | Protokol: " + protocol);

                EmailAddressMemoIDSend emailAddressMemoIDSend = CsvUtils.getAddressProtocolMemoIDWithExactEmail(csvSend, code, protocol);

                if (emailAddressMemoIDSend != null){
                    String selectedEmail = emailAddressMemoIDSend.getEmail();
                    String address = emailAddressMemoIDSend.getAddress();
                    String memoId = emailAddressMemoIDSend.getMemoId();

                    System.out.println("📧 Email Terpilih : " + selectedEmail);
                    System.out.println("📍 Address Sesuai : " + address);

                    sendDashboardPage.inputReceivingAccount(address);

                    sendDashboardPage.handleMemoIdIfNeeded(memoId, isOptionalMemoId);
                    sendDashboardPage.selectProtocolIfAvailable(code, protocol);
                    sendDashboardPage.inputWithdrawAmount(amountToSent);

                    sendDashboardPage.clickKirimButton();

                    // 1. Tangkap hasil pengecekan validasi withdraw
                    boolean shouldSkipAsset = sendDashboardPage.handleWithdrawValidation(address, amountToSent);

                    // 2. Jika bernilai true (terkena error saldo berlebih), langsung skip ke koin berikutnya!
                    if (shouldSkipAsset) {
                        System.out.println("⏭️ Melewati proses untuk aset " + code + " karena limit saldo.");
                        continue; // 🚀 Langsung lanjut ke koin berikutnya di dalam loop
                    }

                    sendConfirmationPage.clickLanjutButton();

                    otpCode = authInfo.getOtp(email);

                    sendVerificationEmailPage.inputOtpCode(otpCode);

                    sendVerificationEmailPage.clickKonfirmasiButton();

                    sendTransactionSuccess.clickSelesaiButton();

                     try { Thread.sleep(10000); } catch (InterruptedException e) { e.printStackTrace(); }
                    dashboardPage.clickSearchIconOnDashboard();
                }
                else{
                    System.out.println("⚠️ Data address untuk aset [" + code + "] dengan protokol [" + protocol + "] tidak ditemukan di Excel!");
                }
            }

        }

       
    }
}
}