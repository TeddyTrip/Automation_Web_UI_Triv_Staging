package pages.buy;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.BasePage;

public class BuyInputAmountPage extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;
    
    private By amountInputField = By.id("amount_2");
    private By btnLanjut = By.id("link_to_buy_3");

    public BuyInputAmountPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public void inputAmountInIDR(String amount) {
        var amountInput = wait.until(ExpectedConditions.elementToBeClickable(amountInputField));
        amountInput.clear();
        amountInput.sendKeys(amount);
    }

    public void clickLanjutButton() {
        wait.until(ExpectedConditions.elementToBeClickable(btnLanjut)).click();
    }

    
    
}
