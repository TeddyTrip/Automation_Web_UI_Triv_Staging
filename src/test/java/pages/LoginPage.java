package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.PdfReportUtils;

import java.time.Duration;

public class LoginPage extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By emailField = By.name("user[email]");
    private By passField = By.name("user[password]");
    private By loginBtn = By.cssSelector(".btn.btn-primary");
    private By otpField = By.id("partitioned");
    private By otpSubmitBtn = By.cssSelector(".btn.btn-primary.btn-login-success");

    public LoginPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterEmailLogin(String email) {
        // Gunakan explicit wait agar script tidak "terburu-buru"
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField)).sendKeys(email);

        // Cetak laporan ke PDF
        PdfReportUtils.addStepToReport(
            driver, 
            "Given Memasukkan email dari variabel global", // Test Case Gherkin
            "Berhasil menginput email: " + email,          // Note / Console Log
            "PASSED"                                       // Status
        );
    }

    public void enterPasswordLogin(String password) {
        // Gunakan explicit wait agar script tidak "terburu-buru"
        wait.until(ExpectedConditions.visibilityOfElementLocated(passField)).sendKeys(password);
        
        // Cetak laporan ke PDF
        PdfReportUtils.addStepToReport(
            driver, 
            "Given Memasukkan password dari variabel global", // Test Case Gherkin
            "Berhasil menginput password: " + password,    // Note / Console Log
            "PASSED"                                       // Status
        );
    }
        
    

    public void clickMasukButtonLogin() { 
        // Cetak laporan SEBELUM pindah halaman (agar tombolnya kelihatan)
        PdfReportUtils.addStepToReport(
            driver, 
            "And Menekan tombol Masuk", 
            "Berhasil menekan tombol login", 
            "PASSED"
        );
        
        // Sekarang compiler akan otomatis mengambil variabel loginBtn (tipe By) dari atas
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn)).click();
    }

    public void inputOtp(String otp) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(otpField)).sendKeys(otp);
        
        PdfReportUtils.addStepToReport(
            driver, 
            "And Menyelesaikan proses TwoFA jika diminta", // Sesuaikan dengan text Gherkin Anda
            "Berhasil menginput kode OTP 2FA: " + otp,     // Note / Console Log
            "PASSED"                                       // Status
        );
        
        wait.until(ExpectedConditions.elementToBeClickable(otpSubmitBtn)).click();
    }

    public boolean isTwoFaPage() {
        String url = driver.getCurrentUrl();
        return url.contains("https://cihuy.triv.id/two_factor") || url.contains("two_factor");
    }
}
