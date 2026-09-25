package pages.giftCard;

import api.AuthInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScrollerElement;

import java.time.Duration;

public class GiftCardPreviewPage {

    private WebDriver driver;
    private AuthInfo authInfo;
    private WebDriverWait wait;
    private ScrollerElement scrollerElement;

    private By btnNextPayment = By.xpath("//button[@id='btn-submit-gift-card']");
    private By btnBackToForm = By.xpath("//button[contains(@class,'back-to-form-gift-card')]");
    private By btnCloseSwalPopup = By.xpath("//button[contains(@class,'swal2-cancel') and normalize-space()='Close']");

    private By btnSelesai = By.cssSelector("a.btn-selesai[href*='/gift-cards/my-cards']");
    private By inputOtpField = By.xpath("//input[@id='otp' or @name='otp']");
    private By btnSubmitOtp = By.xpath("//button[contains(@class,'btn-2fa-submit-gift-card')]");

    public GiftCardPreviewPage(WebDriver driver){
        super();
        this.driver = driver;
        this.authInfo = new AuthInfo();
        this.scrollerElement = new ScrollerElement(driver);
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }
    /**
     * Klik tombol Lanjut pada konfirmasi pembayaran
     */
    public void clickBtnNextPayment() {
        WebElement btnNext = wait.until(ExpectedConditions.presenceOfElementLocated(btnNextPayment));
        scrollerElement.scrollToElement(btnNext);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(btnNext)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnNext);
        }
    }

    /**
     * Klik tombol Kembali untuk merubah data form
     */
    public void clickBtnBackToForm() {
        WebElement btnBack = wait.until(ExpectedConditions.presenceOfElementLocated(btnBackToForm));
        scrollerElement.scrollToElement(btnBack);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(btnBack)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnBack);
        }
    }

    public boolean isPopupPresent() {
        try {
            WebElement btnClose = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//button[contains(@class,'swal2-cancel') and normalize-space()='Close']")
                    ));
            return btnClose.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void closePopupIfPresent() {
        if (isPopupPresent()) {
            try {
                WebElement btnClose = driver.findElement(
                        By.xpath("//button[contains(@class,'swal2-cancel') and normalize-space()='Close']")
                );
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnClose);
                System.out.println("[UI Test] Popup berhasil ditutup.");
            } catch (Exception e) {
                System.out.println("[UI Test] Gagal menutup popup via JS.");
            }
        }
    }

    public boolean isOtpInputPresent() {
        try {
            WebElement otpInput = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(inputOtpField));
            return otpInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void submitOtpIfPresent(String email) {
        if (isOtpInputPresent()) {
            System.out.println("[UI Test] Halaman Input OTP terdeteksi.");

            // 1. Ambil kode OTP dari API Helper
            String otpCode = authInfo.getOtp(email);
            System.out.println("[UI Test] Memasukkan Kode OTP: " + otpCode);

            // 2. Input Kode OTP
            WebElement otpInput = driver.findElement(inputOtpField);
            scrollerElement.scrollToElement(otpInput);
            otpInput.clear();
            otpInput.sendKeys(otpCode);

            // 3. Klik Tombol Lanjut OTP
            WebElement btnSubmit = wait.until(ExpectedConditions.elementToBeClickable(btnSubmitOtp));
            try {
                btnSubmit.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnSubmit);
            }
        } else {
            System.out.println("[UI Test] Halaman OTP tidak tampil!");
        }
    }

    public void clickBtnSelesai() {
        scrollerElement.scrollAndClick(btnSelesai);
    }
}
