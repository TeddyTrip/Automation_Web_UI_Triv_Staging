package pages.dashboard;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.BasePage;

public class DashboardPage extends BasePage {
    private WebDriverWait wait;

    private By iconBuySell = By.cssSelector("a[href='/dashboard/buy-sell']");
    private By iconSwap = By.cssSelector("a[href='/dashboard/swap']");

    public DashboardPage(WebDriver driver) {
        super();
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public void clickBuySellIconOnDashboard() { 
        wait.until(ExpectedConditions.elementToBeClickable(iconBuySell)).click(); 
    }

    public void clickSwapIconOnDashboard() {
        wait.until(ExpectedConditions.elementToBeClickable(iconSwap)).click();
    }
}
