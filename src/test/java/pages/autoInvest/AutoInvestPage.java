package pages.autoInvest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.BasePage;

public class AutoInvestPage extends BasePage{
    
    private WebDriver driver;
    private WebDriverWait wait;

    private By btnAutoTriv = By.xpath("//button[@class='Triv-Auto-Invest-btn-transparent' and text()='Set Jadwal Auto Triv']");

    public AutoInvestPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public void clickSetJadwalAutoTriv() {
        try {
        // 1. Pastikan tidak ada sisa overlay/backdrop transparan dari alert sebelumnya
        By backdrop = By.cssSelector(".modal-backdrop, .overlay, .swal2-container");
        WebDriverWait shortWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(3));
        shortWait.until(ExpectedConditions.invisibilityOfElementLocated(backdrop));
        } catch (Exception e) {
            // Abaikan jika tidak ada backdrop
        }

        // 2. Tunggu tombol "Set Jadwal" bisa diklik
        // (Sesuaikan 'setJadwalButton' dengan variabel By locator tombol Anda di Page Object)
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnAutoTriv));
        
        // 3. Scroll ke tengah layar dan klik pakai JavaScript agar dijamin merespons di iterasi kedua
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
        js.executeScript("arguments[0].click();", btn);
        
        System.out.println("🖱️ Berhasil mengklik tombol Set Jadwal Auto Triv.");
    }

    
}
