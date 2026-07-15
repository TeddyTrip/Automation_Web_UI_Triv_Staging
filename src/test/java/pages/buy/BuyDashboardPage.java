package pages.buy;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BuyDashboardPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Dynamic Locator Template (Cara rapi agar tidak ada XPath panjang di dalam method)
    private String categoryXpath = "//span[contains(@class, 'nav-link-buy') and normalize-space()='%s']";
    private String assetXpath = "//li[contains(@class, 'currency-option') and contains(., '%s')]";

    private By searchBox = By.name("buy_search");
    private By searchRectangleIcon = By.cssSelector("img.Rectangle-Copy-63");

    public BuyDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
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
        wait.until(ExpectedConditions.elementToBeClickable(searchRectangleIcon)).click();
        
        var search = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
        search.clear();
        search.sendKeys(code, Keys.ENTER);

        var element = wait.until(ExpectedConditions.presenceOfElementLocated(getAssetLocator(code)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}