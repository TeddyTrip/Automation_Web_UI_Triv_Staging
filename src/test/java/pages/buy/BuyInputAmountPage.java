package pages.buy;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;

import api.InstallCoinLists;
import formula.MinimalBuySellAssetSpotCalculation;
import pages.BasePage;

public class BuyInputAmountPage extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;

    InstallCoinLists installCoinLists = new InstallCoinLists();
    MinimalBuySellAssetSpotCalculation minimalBuySellAssetSpotCalculation = new MinimalBuySellAssetSpotCalculation();
    
    private By amountIdrBuyInputField = By.id("amount_2");
    private By btnLanjut = By.id("link_to_buy_3");

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

    public void clickLanjutButton() {
        wait.until(ExpectedConditions.elementToBeClickable(btnLanjut)).click();
    }
    
}
