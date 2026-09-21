package pages.swap;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.*;
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
        // 1. Dapatkan expected v_money dari API
        String expectedVMoney = installCoinLists.getV_MoneyFromApi(code);
        Assert.assertNotNull("Gagal mendapatkan v_money dari API untuk code: " + code, expectedVMoney);
        System.out.println("Expected v_money dari API: " + expectedVMoney);
        
        // 2. Bentuk format teks target: "Dompet YieldBasis Triv"
        String targetWalletText = "Dompet " + expectedVMoney + " Triv";
        System.out.println("Expected Wallet text: " + targetWalletText);

        // 3. Input v_money ke search box combobox
        WebElement fillSearchBoxComboboxListWallet = wait.until(ExpectedConditions.elementToBeClickable(searchBoxListWallet));
        fillSearchBoxComboboxListWallet.clear();
        fillSearchBoxComboboxListWallet.sendKeys(expectedVMoney);
        
        // Beri jeda agar Select2 selesai merender hasil filter
        try { 
            Thread.sleep(1000); 
        } catch (InterruptedException e) { 
            e.printStackTrace(); 
        }
        
        // 4. Definisikan locator XPath untuk opsi wallet
        String xpath = String.format("//ul[contains(@class, 'select2-results__options')]//li[contains(., '%s')]", targetWalletText);
        By targetOptionLocator = By.xpath(xpath);
        
        // 5. Tangani Stale Element dengan Loop Retry (Maksimal 3 kali percobaan)
        WebElement targetOption = null;
        int attempts = 0;
        while (attempts < 3) {
            try {
                targetOption = wait.until(ExpectedConditions.elementToBeClickable(targetOptionLocator));
                break; 
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                attempts++;
                System.out.println("Stale element terdeteksi, mencoba mengambil ulang elemen... (Percobaan ke-" + attempts + ")");
                try { Thread.sleep(500); } catch (InterruptedException ie) { ie.printStackTrace(); }
            }
        }
        
        Assert.assertNotNull("Wallet dengan target text '" + targetWalletText + "' tidak ditemukan!", targetOption);

        // 6. Scroll dan klik elemen
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", targetOption);
        
        try { 
            Thread.sleep(300); 
        } catch (InterruptedException e) { 
            e.printStackTrace(); 
        }
        
        try {
            targetOption.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", targetOption);
        }
        
        // 7. Tunggu hingga dropdown Select2 benar-benar tertutup agar tidak memblokir elemen berikutnya
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("select2-dropdown")));
        } catch (Exception e) {
            try {
                fillSearchBoxComboboxListWallet.sendKeys(Keys.ESCAPE);
            } catch (Exception ignored) {}
        }
        
        // DIPERBAIKI DI SINI (Menggunakan System.out, bukan System.strOut)
        System.out.println("Berhasil memilih wallet: " + targetWalletText);
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
