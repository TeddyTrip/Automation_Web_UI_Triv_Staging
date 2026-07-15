package pages.dashboard;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.BasePage;

public class DashboardPage extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By iconBuySell = By.cssSelector("a[href='/dashboard/buy-sell']");

    public DashboardPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public void clickBuyIconOnDashboard() { 
        wait.until(ExpectedConditions.elementToBeClickable(iconBuySell)).click(); 
    }
    
}
