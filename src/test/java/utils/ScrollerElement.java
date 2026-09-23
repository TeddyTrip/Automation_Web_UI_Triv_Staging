package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ScrollerElement {

    private WebDriver driver;
    private WebDriverWait wait;

    public ScrollerElement(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Jeda default 10 detik
    }

    public void scrollAndClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollAndClick(element);
    }

    public void scrollAndClick(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1. Scroll otomatis ke tengah (berlaku untuk Scroll Up maupun Scroll Down)
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'nearest'});", element);

        // 2. Beri waktu jeda singkat agar animasi scroll selesai
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            // 3. Coba klik secara normal
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (ElementClickInterceptedException e) {
            // 4. Jika terhalang sticky header saat scroll up, beri sedikit penyesuaian offset ke atas (contoh: -100px)
            js.executeScript("window.scrollBy(0, -100);");

            try {
                element.click();
            } catch (Exception ex) {
                // 5. Fallback terakhir: Klik langsung via JavaScript DOM
                js.executeScript("arguments[0].click();", element);
            }
        }
    }

    public void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll elemen ke tengah layar (viewport) secara halus
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'nearest'});", element);

        // Beri jeda sangat singkat agar animasi scroll selesai sebelum Selenium berinteraksi
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void scrollToElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollToElement(element);
    }
}
