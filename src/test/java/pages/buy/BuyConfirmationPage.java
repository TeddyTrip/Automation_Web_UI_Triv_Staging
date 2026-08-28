package pages.buy;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.BasePage;
import utils.PdfReportUtils;
import org.openqa.selenium.TimeoutException;


public class BuyConfirmationPage extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By btnKonfirmasi = By.cssSelector("a.link_to_buy_4");

    public BuyConfirmationPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public boolean clickKonfirmasiButton() {
        wait.until(ExpectedConditions.elementToBeClickable(btnKonfirmasi)).click();

        // Cek apakah ada alert market tutup
        try {
            WebDriverWait waitAlert = new WebDriverWait(driver, java.time.Duration.ofSeconds(3));
            Alert alert = waitAlert.until(ExpectedConditions.alertIsPresent());
            alert.accept(); // Klik OK
            
            // Catat ke PDF bahwa gagal karena market tutup
            PdfReportUtils.addStepToReport(driver, "Konfirmasi Transaksi", "Market Tutup (Alert Ditangani)", "FAILED");
            
            return false; // Beritahu Steps bahwa transaksi GAGAL
        } catch (Exception e) {
            // Tidak ada alert, transaksi sukses
            return true; // Beritahu Steps bahwa transaksi SUKSES
        }
    }

}
