package pages.send;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SendTransactionSuccess {
    
    private WebDriver driver;
    private WebDriverWait wait;

    private By selesaiButtonLocator = By.xpath("//a[contains(@class, 'link_to_dashboard_buy') and contains(text(), 'Selesai')]");

    public SendTransactionSuccess(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public void clickSelesaiButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            // Tunggu sampai tombol Selesai bisa diklik
            WebElement btnSelesai = wait.until(ExpectedConditions.elementToBeClickable(selesaiButtonLocator));
            btnSelesai.click();
            System.out.println("✅ Berhasil mengklik tombol Selesai.");
        } catch (Exception e) {
            System.out.println("⚠️ Gagal klik normal, mencoba menggunakan JavaScript Click untuk tombol Selesai...");
            WebElement btnSelesai = driver.findElement(selesaiButtonLocator);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", btnSelesai);
            System.out.println("✅ Berhasil mengklik tombol Selesai via JavaScript.");
        }
    }
}
