package pages.sell;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.PdfReportUtil;

public class SellConfirmationPage {
    private WebDriver driver;
    private WebDriverWait wait;
    
    private By btnKonfirmasi = By.cssSelector("a.link_to_sell_4");

    public SellConfirmationPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public boolean clickKonfirmasiButton() {
        wait.until(ExpectedConditions.elementToBeClickable(btnKonfirmasi)).click();
        
        try {
            // Tunggu Alert muncul
            WebDriverWait waitAlert = new WebDriverWait(driver, java.time.Duration.ofSeconds(5));
            Alert alert = waitAlert.until(ExpectedConditions.alertIsPresent());
            
            // Terima alert
            alert.accept();
            System.out.println("Alert terdeteksi dan di-accept.");
            
            // Tambahkan jeda singkat agar browser sempat mereset status alert
            Thread.sleep(1000); 
            
            return false; // Transaksi gagal karena alert (Market Tutup)
        } catch (Exception e) {
            // Tidak ada alert, transaksi mungkin sukses
            return true; 
        }
    }
}
