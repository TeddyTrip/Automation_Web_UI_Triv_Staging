// package temporaryfile;

// import org.openqa.selenium.By;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.support.ui.ExpectedConditions;

// import utils.PdfReportUtil;

// public class temporaryCode {
//     // Locators
//     private By amountField = By.id("amount_2");
//     private By btnLanjut = By.id("link_to_buy_3");

//     public BuyAssetPage(WebDriver driver) { super(driver); }

//     public void inputAmountInIDR(String amount) {
//         try {
//             var amountInput = wait.until(ExpectedConditions.elementToBeClickable(amountField));
//             amountInput.clear();
//             amountInput.sendKeys(amount);
            
//             // Dokumentasi Sukses
//             PdfReportUtil.addStepToReport(driver, "Input Nominal IDR", "Berhasil input nominal: " + amount, "PASSED");
//         } catch (Exception e) {
//             // Dokumentasi Gagal
//             PdfReportUtil.addStepToReport(driver, "Input Nominal IDR", "Gagal input nominal: " + e.getMessage(), "FAILED");
//             throw e; // PENTING: Melempar kembali error agar status tes di Cucumber jadi FAILED
//         }
//     }

//     public void clickLanjutButton() {
//         try {
//             wait.until(ExpectedConditions.elementToBeClickable(btnLanjut)).click();
            
//             // Dokumentasi Sukses
//             PdfReportUtil.addStepToReport(driver, "Klik Tombol Lanjut", "Berhasil klik tombol Lanjut", "PASSED");
//         } catch (Exception e) {
//             // Dokumentasi Gagal
//             PdfReportUtil.addStepToReport(driver, "Klik Tombol Lanjut", "Gagal klik tombol Lanjut: " + e.getMessage(), "FAILED");
//             throw e; // PENTING: Melempar kembali error
//         }
//     }
// }
