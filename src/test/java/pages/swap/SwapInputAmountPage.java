package pages.swap;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import api.InstallCoinDetails;
import api.InstallCoinLists;
import formula.MinimalBuySellAssetSpotCalculation;

import pages.BasePage;

public class SwapInputAmountPage extends BasePage {
    
    private WebDriver driver;
    private WebDriverWait wait;

    InstallCoinLists installCoinLists = new InstallCoinLists();
    InstallCoinDetails installCoinDetails = new InstallCoinDetails();
    MinimalBuySellAssetSpotCalculation minimalBuySellAssetSpotCalculation = new MinimalBuySellAssetSpotCalculation();

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

    public void inputAmountAssetTo(String codeTo) {
        // 1. Ambil minimal asset dari API (Behind the scenes)
        double minimalBuyAsset = installCoinDetails.getMinimalBuyFromApi(codeTo);

        double priceBuy = installCoinLists.getBuyPriceFromApi(codeTo);

        double calculateMinimalBuyAssetAnd1KRupiah = 1000 / priceBuy;
        double finalCalculation = calculateMinimalBuyAssetAnd1KRupiah + minimalBuyAsset;
        
        // 3. Formatting (Misal 8 digit di belakang koma)
        String finalAmountAsset = String.format("%.8f", finalCalculation);
        
        // 4. Input ke UI
        var amountInput = wait.until(ExpectedConditions.elementToBeClickable(amountAssetToInputField));
        amountInput.clear();

        StringSelection stringSelection = new StringSelection(finalAmountAsset);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        amountInput.click();
        amountInput.sendKeys(Keys.chord(Keys.CONTROL, "v"));
        
        System.out.println("Minimal Asset for " + codeTo + " is " + finalAmountAsset);
    }

    public void inputAmountAssetFrom(String codeTo) {
        // 1. Ambil minimal asset dari API (Behind the scenes)
        double minimalSellAsset = installCoinDetails.getMinimalSellFromApi(codeTo);

        double priceSell = installCoinLists.getSellPriceFromApi(codeTo);

        double calculateMinimalBuyAssetAnd1KRupiah = 1000 / priceSell;
        double finalCalculation = calculateMinimalBuyAssetAnd1KRupiah + minimalSellAsset;
        
        // 3. Formatting (Misal 8 digit di belakang koma)
        String finalAmountAsset = String.format("%.8f", finalCalculation);
        
        // 4. Input ke UI
        var amountInput = wait.until(ExpectedConditions.elementToBeClickable(amountAssetToInputField));
        amountInput.clear();

        StringSelection stringSelection = new StringSelection(finalAmountAsset);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        amountInput.click();
        amountInput.sendKeys(Keys.chord(Keys.CONTROL, "v"));
        
        System.out.println("Minimal Asset for " + codeTo + " is " + finalAmountAsset);
    }

    

    public void clickLanjutButton() {
        wait.until(ExpectedConditions.elementToBeClickable(btnLanjut)).click();
    }

}
