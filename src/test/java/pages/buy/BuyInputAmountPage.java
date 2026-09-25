package pages.buy;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import api.v1.InstallCoinLists;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.time.Duration;

import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;

import formula.MinimalBuySellAssetSpotCalculation;
import pages.BasePage;

public class BuyInputAmountPage extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;

    InstallCoinLists installCoinLists = new InstallCoinLists();
    MinimalBuySellAssetSpotCalculation minimalBuySellAssetSpotCalculation = new MinimalBuySellAssetSpotCalculation();
    
    private By amountIdrBuyInputField = By.id("amount_2");
    private By btnLanjut = By.id("link_to_buy_3");
    private By button25Locator = By.xpath("//span[@class='auto-input-amount-buy' and normalize-space()='25%']");
    private By button50Locator = By.xpath("//span[@class='auto-input-amount-buy' and normalize-space()='50%']");
    private By button75Locator = By.xpath("//span[@class='auto-input-amount-buy' and normalize-space()='75%']");
    private By button100Locator = By.xpath("//span[@class='auto-input-amount-buy' and normalize-space()='100%']");

    public BuyInputAmountPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public void inputAmountInIDRUsingMinimumBuyTransaction(String code) {
        // 1. Dapatkan hasil kalkulasi
        String amountInIDR = minimalBuySellAssetSpotCalculation.getMinimalBuyPriceWithCertainCalculation(code);
        
        // 2. Format angka (gunakan "%.0f" jika ingin dibulatkan tanpa angka desimal di belakang koma)
        String finalAmount = String.format("%.0f", Double.parseDouble(amountInIDR));
        
        // 3. Tunggu hingga elemen bisa diklik
        var amountInput = wait.until(ExpectedConditions.elementToBeClickable(amountIdrBuyInputField));
        amountInput.clear();
        
        // 4. Salin (Copy) teks hasil kalkulasi ke Clipboard komputer Anda
        StringSelection stringSelection = new StringSelection(finalAmount);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        
        // 5. Klik elemen untuk memastikan fokus berada di dalam textbox
        amountInput.click();
        
        // 6. Lakukan perintah Paste (Ctrl + V) meniru tindakan manual
        amountInput.sendKeys(Keys.chord(Keys.CONTROL, "v"));
        
        // 7. (Opsional) Kirim dispatch event agar frontend web mendeteksi perubahan state
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", amountInput);
        js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", amountInput);
        
        System.out.println("Berhasil paste nilai " + finalAmount + " untuk aset: " + code);
    }

    public void inputCustomAmountInIDR(String amountInIDR) {
    
        String finalAmount = String.format("%.0f", Double.parseDouble(amountInIDR));
        
        var amountInput = wait.until(ExpectedConditions.elementToBeClickable(amountIdrBuyInputField));
        amountInput.clear();
        
        StringSelection stringSelection = new StringSelection(finalAmount);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        
        amountInput.click();
        
        amountInput.sendKeys(Keys.chord(Keys.CONTROL, "v"));
    }

    public void clickLanjutButton() {
        wait.until(ExpectedConditions.elementToBeClickable(btnLanjut)).click();
    }

    public void click25Percent() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(button25Locator));
            btn.click();
            System.out.println("✅ Berhasil mengklik tombol 25%");
        } catch (Exception e) {
            System.out.println("⚠️ Gagal klik normal, mencoba JavaScript Click untuk tombol 25%...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(button25Locator));
        }
    }

    public void click50Percent() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(button50Locator));
            btn.click();
            System.out.println("✅ Berhasil mengklik tombol 50%");
        } catch (Exception e) {
            System.out.println("⚠️ Gagal klik normal, mencoba JavaScript Click untuk tombol 50%...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(button50Locator));
        }
    }

    public void click75Percent() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(button75Locator));
            btn.click();
            System.out.println("✅ Berhasil mengklik tombol 75%");
        } catch (Exception e) {
            System.out.println("⚠️ Gagal klik normal, mencoba JavaScript Click untuk tombol 75%...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(button75Locator));
        }
    }

    public void click100Percent() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(button100Locator));
            btn.click();
            System.out.println("✅ Berhasil mengklik tombol 100%");
        } catch (Exception e) {
            System.out.println("⚠️ Gagal klik normal, mencoba JavaScript Click untuk tombol 100%...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(button100Locator));
        }
    }
    
}
