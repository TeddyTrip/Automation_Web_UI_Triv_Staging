package pages.sell;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import api.InstallCoinLists;

import pages.BasePage;

public class SellDashboardPage extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;

    InstallCoinLists installCoinLists = new InstallCoinLists();

    private By sellTab = By.xpath("//div[contains(@class, 'tab-title-sell') and text()='Jual']");
    private By searchBox = By.name("sell_search");
    private By searchRectangleIcon = (By.xpath("//a[@id='sell-search']//img[contains(@class, 'Rectangle-Copy-63')]"));

    // private String categoryXpath = "//span[contains(@class, 'nav-link-sell') and normalize-space()='%s']";
    // private String assetXpath = "//li[contains(@class, 'currency-option') and contains(., '%s')]";

    // Menggunakan span karena kategorinya adalah span.nav-link-sell
    private String categoryXpath = "//span[contains(@class, 'nav-link-sell') and normalize-space()='%s']";
    
    // Menggunakan div atau span yang mengandung nama aset (lebih aman daripada li)
    private String assetXpath = "//*[contains(text(), '%s')]";

    public SellDashboardPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public void clickSellIconOnDashboard() {
        wait.until(ExpectedConditions.elementToBeClickable(sellTab)).click();
    }

    // --- Helper Methods untuk Locator Dinamis ---
    private By getCategoryLocator(String categoryName) {
        String formattedName = categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1);
        return By.xpath(String.format(categoryXpath, formattedName));
    }

    private By getAssetLocator(String code) {
        return By.xpath(String.format(assetXpath, code));
    }

    public void selectCategory(String category) {
        wait.until(ExpectedConditions.elementToBeClickable(getCategoryLocator(category))).click();
    }

    public void selectAssetByCode(String code) {
        // 1. Klik icon pencarian
        wait.until(ExpectedConditions.elementToBeClickable(searchRectangleIcon)).click();
        
        // 2. Input code ke search box
        var search = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
        search.clear();
        search.sendKeys(code, Keys.ENTER);
        
        // 3. DETEKSI ASET BERDASARKAN DATA-CURRENCY
        // Kita menggunakan .toLowerCase() karena data-currency biasanya selalu huruf kecil
        String xpath = String.format("//li[@data-currency='%s']", code.toLowerCase());
        By assetLocator = By.xpath(xpath);
        
        // 4. Pilih elemen berdasarkan locator unik tersebut
        var element = wait.until(ExpectedConditions.presenceOfElementLocated(assetLocator));
        
        // 5. Scroll dan Klik
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }

//     public void selectAssetByCode(String code) {
//     // 1. Klik icon pencarian
//     wait.until(ExpectedConditions.elementToBeClickable(searchRectangleIcon)).click();
    
//     // 2. Input code ke search box
//     var search = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
//     search.clear();
//     search.sendKeys(code, Keys.ENTER);
    
//     // 3. DETEKSI ASET DENGAN 'CONTAINS' (Tidak harus sama persis)
//     // XPath: //li[contains(@data-currency, 'xau')]
//     // Ini akan cocok jika data-currency berisi "xau", "xau-pro", "gold-xau", dll.
//     String xpath = String.format("//li[contains(@data-currency, '%s')]", code.toLowerCase());
//     By assetLocator = By.xpath(xpath);
    
//     // 4. Pilih elemen berdasarkan locator
//     var element = wait.until(ExpectedConditions.presenceOfElementLocated(assetLocator));
    
//     // 5. Scroll dan Klik
//     JavascriptExecutor js = (JavascriptExecutor) driver;
//     js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
//     js.executeScript("arguments[0].click();", element);
// }
}
