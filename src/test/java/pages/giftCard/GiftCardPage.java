package pages.giftCard;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.BasePage;
import utils.ScrollerElement;

import java.time.Duration;

public class GiftCardPage extends BasePage {

    private WebDriver driver;
    private WebDriverWait wait;
    private ScrollerElement scrollerElement;

    private By btnSendGiftCard = By.cssSelector("a[href*='/dashboard/gift-cards/send']");
    private By btnSaveGiftCard = By.cssSelector("a.btn.step-form-gift-card");

    public GiftCardPage(WebDriver driver) {
        this.driver = driver;
        this.scrollerElement = new ScrollerElement(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickBtnKirimGiftCard() {
        scrollerElement.scrollAndClick(btnSendGiftCard);
    }

    public void setBtnSaveGiftCard() {
        scrollerElement.scrollAndClick(btnSaveGiftCard);
    }
}
