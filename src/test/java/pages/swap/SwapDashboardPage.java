package pages.swap;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.BasePage;

public class SwapDashboardPage extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By searchRectangleIcon = (By.xpath("//a[@id='sell-search']//img[contains(@class, 'Rectangle-Copy-63')]"));
    private By searchBox = By.name("buy_search");
    private String categoryXpath = "//li[contains(@class, 'asset-tab-buy') and @data-type='%s']";


    public SwapDashboardPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    private By getCategoryLocator(String categoryName) {
        String formattedName = categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1);
        return By.xpath(String.format(categoryXpath, formattedName));
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
        String xpath = String.format(categoryXpath.toString(), code.toLowerCase());
        By assetLocator = By.xpath(xpath);
        
        // 4. Pilih elemen berdasarkan locator unik tersebut
        var element = wait.until(ExpectedConditions.presenceOfElementLocated(assetLocator));
        
        // 5. Scroll dan Klik
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        js.executeScript("arguments[0].click();", element);
    }


}
