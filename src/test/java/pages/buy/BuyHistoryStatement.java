package pages.buy;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.BasePage;

public class BuyHistoryStatement extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By btnDone = By.cssSelector(".link_to_dashboard_buy");

    public BuyHistoryStatement(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }
    
    public void clickDoneButtonHistoryStatement() {
        wait.until(ExpectedConditions.elementToBeClickable(btnDone)).click();
    }
}
