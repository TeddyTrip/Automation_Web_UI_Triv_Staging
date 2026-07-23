package pages.swap;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import api.InstallCoinLists;

import pages.BasePage;

public class SwapInputAmountPage extends BasePage {
    
    private WebDriver driver;
    private WebDriverWait wait;

    InstallCoinLists installCoinLists = new InstallCoinLists();

    private By searchBoxListWallet = By.className("select2-search__field");
    private By amountAssetFromInputField = By.id("amount_2");
    private By amountAssetToInputField = By.id("amount_1");
    private By btnLanjut = By.id("link_to_buy_3");
    private By comboboxListWallet = By.id("select2-payment_type-container");

    public SwapInputAmountPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    // public void getAmountAssetFromAmountIDR(Integer code) {
    //     Integer amountAsset = installCoinLists.getAmountAssetFromAmountIDR(code);
    // }

    public void clickComboboxListWallet() {
        var clickComboboxListWallet = wait.until(ExpectedConditions.elementToBeClickable(comboboxListWallet));
        clickComboboxListWallet.click();
    }

    public void getSearchboxComboboxListWallet(String code) {
        WebElement fillSearchBoxComboboxListWallet = wait.until(ExpectedConditions.elementToBeClickable(searchBoxListWallet));
        fillSearchBoxComboboxListWallet.clear();
        fillSearchBoxComboboxListWallet.sendKeys(code, Keys.ENTER);
    }

    public String getValidationMessage() {
    try {
        // Kita gunakan Javascript untuk mengambil teks dari snackbar
        // .snackbar li ini adalah locator yang kita pakai
        String script = "return document.querySelector('.snackbar li') ? document.querySelector('.snackbar li').innerText : 'NO_ERROR';";
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String message = (String) js.executeScript(script);
        
        if (!message.equals("NO_ERROR")) {
            System.out.println("Pesan yang didapat dari UI (via JS): " + message);
        }
        
        return message; 
    } catch (Exception e) {
        System.out.println("DEBUG: Gagal mengambil pesan via JS. Error: " + e.getMessage());
        return "NO_ERROR"; 
    }
}

    public void inputAmountAssetTo(String idrAmount, String codeTo) {
        // // 1. Ambil harga dari API (Behind the scenes)
        // double buyPrice = installCoinLists.getMinimalBuyPriceFromApi(codeTo);
        
        // // 2. Kalkulasi (Behind the scenes)
        // double amountIdr = Double.parseDouble(idrAmount);
        // double calculatedAsset = amountIdr / buyPrice;
        
        // // 3. Formatting (Misal 8 digit di belakang koma)
        // String finalAmount = String.format("%.8f", calculatedAsset);
        
        // // 4. Input ke UI
        // var amountInput = wait.until(ExpectedConditions.elementToBeClickable(amountAssetToInputField));
        // amountInput.clear();
        // amountInput.sendKeys(finalAmount);
        
        // System.out.println("Auto-calculated " + idrAmount + " IDR to " + finalAmount + " " + codeTo);
    }

    

    public void clickLanjutButton() {
        wait.until(ExpectedConditions.elementToBeClickable(btnLanjut)).click();
    }

}
