package pages.giftCard;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class GiftCardMyCardPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By btnActionMore = By.cssSelector("a[href*='/dashboard/gift-cards/'][data-remote='true']");
    private By btnSelesaiPopup = By.cssSelector("button.btn-selesai");

    public GiftCardMyCardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openCardDetailAndClickSelesai() {
        // 1. Klik ikon titik tiga (• • •) pada baris gift card
        WebElement btnMore = wait.until(ExpectedConditions.elementToBeClickable(btnActionMore));
        try {
            btnMore.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnMore);
        }

        // 2. Tunggu hingga tombol 'Selesai' di dalam popup muncul
        WebElement btnSelesai = wait.until(ExpectedConditions.presenceOfElementLocated(btnSelesaiPopup));

        // 3. Scroll ke tombol Selesai dan Klik
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnSelesai);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(btnSelesai)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnSelesai);
        }

        System.out.println("[UI Test] Popup detail gift card dibuka dan tombol Selesai berhasil diklik.");
    }
}
