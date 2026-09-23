package pages.dashboard;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.BasePage;
import utils.ScrollerElement;

public class DashboardPage extends BasePage {
    private WebDriverWait wait;
    private ScrollerElement scrollerElement;

    private By iconBuySell = By.cssSelector("a[href='/dashboard/buy-sell']");
    private By iconSwap = By.cssSelector("a[href='/dashboard/coin/swap']");
    private By iconAutoInvest = By.cssSelector("a[href='/dashboard/auto-invest']");
    private By iconGiftCard = By.cssSelector("a[href='/dashboard/gift-cards']");

    public DashboardPage(WebDriver driver) {
        super();
        this.scrollerElement = new ScrollerElement(driver);
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
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
        /*wait.until(ExpectedConditions.elementToBeClickable(iconGiftCard)).click();*/
        scrollerElement.scrollAndClick(iconGiftCard);
    }
}
