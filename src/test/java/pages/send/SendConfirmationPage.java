package pages.send;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ConfigReader;

public class SendConfirmationPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By lanjutButton = By.xpath("//a[contains(@class, 'link_to_withdraw_3') and contains(text(), 'Lanjut')]");

    public SendConfirmationPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public void clickLanjutButton() {
        // Sesuaikan locator dengan pilihan di atas
        By lanjutButtonLocator = By.xpath("//a[contains(@class, 'link_to_withdraw_3') and contains(text(), 'Lanjut')]");
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            // Tunggu sampai elemen bisa diklik
            WebElement btnLanjut = wait.until(ExpectedConditions.elementToBeClickable(lanjutButtonLocator));
            btnLanjut.click();
            System.out.println("✅ Berhasil mengklik tombol Lanjut.");
        } catch (Exception e) {
            System.out.println("⚠️ Gagal klik normal, mencoba menggunakan JavaScript Click...");
            WebElement btnLanjut = driver.findElement(lanjutButtonLocator);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", btnLanjut);
            System.out.println("✅ Berhasil mengklik tombol Lanjut via JavaScript.");
        }
    }
}
