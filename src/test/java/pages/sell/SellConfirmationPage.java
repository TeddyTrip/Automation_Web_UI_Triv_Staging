package pages.sell;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.PdfReportUtils;

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
        try {
            // 1. Amankan proses klik tombol konfirmasi terlebih dahulu
            WebElement konfirmasiBtn = wait.until(ExpectedConditions.elementToBeClickable(btnKonfirmasi));
            konfirmasiBtn.click();
            System.out.println("✅ Tombol konfirmasi berhasil diklik.");
        } catch (Exception e) {
            System.out.println("❌ Gagal mengklik tombol konfirmasi: " + e.getMessage());
            return false; // Klik gagal, maka transaksi dianggap gagal
        }
        
        // 2. Cek apakah muncul Alert (misalnya peringatan Market Tutup)
        try {
            WebDriverWait waitAlert = new WebDriverWait(driver, java.time.Duration.ofSeconds(3));
            Alert alert = waitAlert.until(ExpectedConditions.alertIsPresent());
            
            // Terima alert jika ada
            alert.accept();
            System.out.println("⚠️ Alert terdeteksi dan di-accept (Market Tutup).");
            
            Thread.sleep(1000); 
            return false; // Transaksi gagal karena memunculkan alert
            
        } catch (org.openqa.selenium.TimeoutException e) {
            // TimeoutException wajar terjadi jika TIDAK ADA alert yang muncul
            System.out.println("✅ Tidak ada alert yang muncul, transaksi sukses.");
            return true; 
        } catch (Exception e) {
            // Penanganan error tak terduga lainnya saat menangani alert
            System.out.println("ℹ️ Catatan saat cek alert: " + e.getMessage());
            return true; 
        }
    }
}
