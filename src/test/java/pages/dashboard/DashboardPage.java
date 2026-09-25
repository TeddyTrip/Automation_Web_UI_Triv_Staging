package pages.dashboard;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.BasePage;
import utils.ScrollerElement;

public class DashboardPage extends BasePage {

    private WebDriver driver;
    private WebDriverWait wait;
    private ScrollerElement scrollerElement;

    private By iconBuySell = By.cssSelector("a[href='/dashboard/buy-sell']");
    private By iconSwap = By.cssSelector("a[href='/dashboard/coin/swap']");
    private By iconAutoInvest = By.cssSelector("a[href='/dashboard/auto-invest']");
    private By iconGiftCard = By.cssSelector("a[href='/dashboard/gift-cards']");

    public DashboardPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
        this.scrollerElement = new ScrollerElement(driver);
    }

    public void clickBuySellIconOnDashboard() { 
        wait.until(ExpectedConditions.elementToBeClickable(iconBuySell)).click(); 
    }

    public void clickSwapIconOnDashboard() {
        wait.until(ExpectedConditions.elementToBeClickable(iconSwap)).click();
    }

    public void clickAutoInvestIconOnDashboard() {
        wait.until(ExpectedConditions.elementToBeClickable(iconAutoInvest)).click();
    }

    public void clickGiftCardIconDashboard(){
        // 1. Pastikan URL berada di dashboard utama
        String currentUrl = driver.getCurrentUrl();
        if (!currentUrl.endsWith("/dashboard")) {
            driver.get("https://cihuy.triv.id/dashboard");
        }

        // 2. Re-find element secara langsung untuk menghindari Stale Element
        WebElement iconElement = wait.until(ExpectedConditions.presenceOfElementLocated(iconGiftCard));

        try {
            scrollerElement.scrollAndClick(iconGiftCard);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", iconElement);
        }
    }
}
