package pages.send;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ConfigReader;

public class SendVerificationEmailPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By otpInput = By.xpath("//input[@id='otp_input' and @placeholder='OTP Code' and contains(@class, 'text-size-12')]");
    private By konfirmasiButton = By.xpath("//a[contains(@class, 'link_to_withdraw_4') and contains(text(), 'Konfirmasi')]");

    public SendVerificationEmailPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public void inputOtpCode(String otpCode) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement otpElement = wait.until(ExpectedConditions.elementToBeClickable(otpInput));
        
        // 1. Fokuskan dan bersihkan input field
        otpElement.click();
        otpElement.clear();
        
        // 2. Ketik kode OTP huruf per huruf (atau bisa langsung otpElement.sendKeys(otpCode))
        for (char c : otpCode.toCharArray()) {
            otpElement.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(50); // Jeda kecil agar natural
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 3. Picu event JavaScript agar framework web mendeteksi perubahannya
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", otpElement);
        js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", otpElement);

        System.out.println("✅ Berhasil mengisi OTP Code: " + otpCode);
    }

    public void clickKonfirmasiButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            // Tunggu sampai tombol konfirmasi bisa diklik
            WebElement btnKonfirmasi = wait.until(ExpectedConditions.elementToBeClickable(konfirmasiButton));
            btnKonfirmasi.click();
            System.out.println("✅ Berhasil mengklik tombol Konfirmasi.");
        } catch (Exception e) {
            System.out.println("⚠️ Gagal klik normal, mencoba menggunakan JavaScript Click untuk tombol Konfirmasi...");
            WebElement btnKonfirmasi = driver.findElement(konfirmasiButton);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", btnKonfirmasi);
            System.out.println("✅ Berhasil mengklik tombol Konfirmasi via JavaScript.");
        }
    }
}
