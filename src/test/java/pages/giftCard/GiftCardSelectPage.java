package pages.giftCard;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScrollerElement;

import java.util.List;
import java.util.Random;

public class GiftCardSelectPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private ScrollerElement scrollerElement;

    private By dropdownGiftTemplate = By.xpath("//select[@name='gift_template']");
    private By btnSaveDesign = By.xpath("//a[contains(@class,'step-form-gift-card') and normalize-space()='Simpan']");
    private By btnStepFormGiftCard = By.cssSelector(".btn.btn-lg.ml-2.triv__btn-primary.px-5.step-form-gift-card");

    public GiftCardSelectPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.scrollerElement = new ScrollerElement(driver);
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    /**
     * Memilih opsi berdasarkan value atribut
     */
    public void selectGiftTemplateByValue(String optionValue) {
        // 1. Tunggu hingga elemen hadir di DOM (menggunakan presenceOfElementLocated, bukan visibility)
        WebElement dropdownElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(dropdownGiftTemplate)
        );

        // 2. Scroll posisi ke elemen
        scrollerElement.scrollToElement(dropdownElement);

        // 3. Paksa ubah value dan trigger event 'change' via JavaScript
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                dropdownElement, optionValue.toLowerCase()
        );
    }

    public String selectGiftTemplateByText() {
        WebElement dropdownElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(dropdownGiftTemplate)
        );
        scrollerElement.scrollToElement(dropdownElement);
        Select select = new Select(dropdownElement);
        List<WebElement> options = select.getOptions();

        Random random = new Random();
        int randomIndex = random.nextInt(options.size());

        // Memilih berdasarkan value agar terhindar dari ketidakcocokan teks UI
        String value = options.get(randomIndex).getAttribute("value");
        if (value != null && !value.isEmpty()) {
            select.selectByValue(value);
        } else {
            select.selectByIndex(randomIndex);
        }

        return options.get(randomIndex).getText();
    }

    public String selectRandomTemplateWithoutClick() {
        // 1. Tunggu hingga elemen hadir di DOM (tanpa syarat harus visible)
        WebElement dropdownElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(dropdownGiftTemplate)
        );

        // 2. Langsung bungkus ke objek Select
        Select select = new Select(dropdownElement);
        List<WebElement> options = select.getOptions();

        // 3. Ambil index acak
        int randomIndex = new Random().nextInt(options.size());

        // 4. Pilih berdasarkan value untuk menghindari Stale/Mismatch error
        String value = options.get(randomIndex).getAttribute("value");
        if (value != null && !value.isEmpty()) {
            select.selectByValue(value);
        } else {
            select.selectByIndex(randomIndex);
        }

        return options.get(randomIndex).getText();
    }

    public void selectCardPicture(String event, String template) {
        String xpathQuery = String.format("//img[@data-event='%s' and @data-template='%s']", event, template);
        WebElement picture = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathQuery)));
        picture.click();
    }

    // setelah pilih desain tekan simpan disini
    public void clickBtnSimpan() {
        WebElement btnSimpan = wait.until(ExpectedConditions.elementToBeClickable(btnStepFormGiftCard));
        btnSimpan.click();
    }

    public void clickBtnStepForm() {
//        WebElement btnStep = wait.until(ExpectedConditions.elementToBeClickable(btnStepFormGiftCard));
//        btnStep.click();
    }

    public String selectRandomGiftTemplate() {
        WebElement dropdownElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(dropdownGiftTemplate)
        );
        scrollerElement.scrollToElement(dropdownElement);
        Select select = new Select(dropdownElement);

        List<WebElement> options = select.getOptions();

        int randomIndex = new java.util.Random().nextInt(options.size());

        String value = options.get(randomIndex).getAttribute("value");
        if (value != null && !value.isEmpty()) {
            select.selectByValue(value);
        } else {
            select.selectByIndex(randomIndex);
        }

        return options.get(randomIndex).getText();
    }
}
