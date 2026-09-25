package pages.giftCard;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScrollerElement;
import utils.UserGiftCardRandomizer;
import utils.WaitUtils;

public class GiftCardFormPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private ScrollerElement scrollerElement;

    private By btnCurrencyDropdown = By.xpath("//button[@data-id='giftcard_currency']");
    private By inputSearchCurrency = By.xpath("//div[contains(@class,'bs-searchbox')]/input[@type='search']");

    private By inputNominal = By.xpath("//input[@name='giftcard_nominal' or contains(@class, 'giftcard_nominal')]");
    //private By btnCurrencyDropdown = By.xpath("//button[@data-id='giftcard_currency']");
    private By inputRecipientName = By.xpath("//input[@id='info_receiver']");
    private By inputRecipientEmail = By.xpath("//input[@id='info_email']");
    private By inputRecipientPhone = By.xpath("//input[@id='info_phone_number']");
    private By inputMessage = By.xpath("//textarea[@id='info_note']");
    private By btnSubmitPayment = By.xpath("//button[@id='btn-form-gift-card']");

    public GiftCardFormPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.scrollerElement = new ScrollerElement(driver);
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public void selectCurrencyByVmoney(String vMoney) {
        WebElement btnDropdown = wait.until(ExpectedConditions
                .presenceOfElementLocated(btnCurrencyDropdown));
        scrollerElement.scrollToElement(btnDropdown);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(btnDropdown)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnDropdown);
        }

        // 2. Ketikkan nama/kode aset (misal "BTC" atau "WBTC") ke kolom search
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(inputSearchCurrency));
        searchBox.clear();
        searchBox.sendKeys(vMoney);

        // Beri sedikit jeda waktu filter JavaScript Bootstrap-Select merender opsi
        WaitUtils.waitForSeconds(0.3);

        // A. Bersihkan teks label dari simbol kurung (misal: "Stasis Euro (EURS)" -> "stasis euro")
        String cleanLabel = vMoney.toLowerCase().replaceAll("\\s*\\(.*?\\)", "").trim();

        // B. Ekstrak kode aset di dalam kurung jika ada (misal: "EURS")
        String codeInLabel = "";
        if (vMoney.contains("(") && vMoney.contains(")")) {
            codeInLabel = vMoney.substring(vMoney.indexOf("(") + 1, vMoney.indexOf(")")).toLowerCase().trim();
        }

        // C. Target pencarian kedua (gunakan codeInLabel jika ada, atau gunakan vMoney asli secara lowercase)
        String secondTarget = codeInLabel.isEmpty() ? vMoney.toLowerCase() : codeInLabel;

        // 3. Locator dinamis fleksibel: Mampu mencocokkan label bersih ATAU kode aset di span
        String optionXpath = String.format(
                "//div[contains(@class,'dropdown-menu') and contains(@class,'show')]//a[@role='option']" +
                        "[.//span[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s') " +
                        "or contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]]",
                cleanLabel, secondTarget
        );

        WebElement optionElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(optionXpath)));

        // 4. Klik opsi yang sudah terfilter
        try {
            wait.until(ExpectedConditions.elementToBeClickable(optionElement)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", optionElement);
        }
    }

    public void inputNominalAmount(String amount) {
        // 1. Tunggu hingga elemen hadir di DOM
        WebElement amountField = wait.until(ExpectedConditions.presenceOfElementLocated(inputNominal));
        scrollerElement.scrollToElement(amountField);

        // 2. Gunakan JavaScript Executor untuk mengisi value dan trigger event listener
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('keyup', { bubbles: true }));",
                amountField, amount
        );

        System.out.println("[UI Test] Nominal berhasil diinput: " + amountField.getAttribute("value"));
    }

    public void fillDeliveryForm() {
        UserGiftCardRandomizer.AccountData randomAccount = UserGiftCardRandomizer.getRandomAccountExcludingGlobal();

        String recipientName = randomAccount.getUsername();
        String recipientEmail = randomAccount.getEmail();
        String randomPhone = UserGiftCardRandomizer.generateRandomPhoneNumber();
        String randomMessage = UserGiftCardRandomizer.generateRandomMessage();

        System.out.println("[UI Test] Mengisi Form Data Pengiriman:");
        System.out.println(" - Nama Penerima  : " + recipientName);
        System.out.println(" - Email Penerima : " + recipientEmail);
        System.out.println(" - Nomor HP       : " + randomPhone);
        System.out.println(" - Pesan          : " + randomMessage);

        WebElement nameField = wait.until(ExpectedConditions
                .presenceOfElementLocated(inputRecipientName));
        scrollerElement.scrollToElement(nameField);
        nameField.clear();
        nameField.sendKeys(recipientName);

        WebElement emailField = wait.until(ExpectedConditions
                .presenceOfElementLocated(inputRecipientEmail));
        emailField.clear();
        emailField.sendKeys(recipientEmail);

        WebElement phoneField = wait.until(ExpectedConditions
                .presenceOfElementLocated(inputRecipientPhone));
        phoneField.clear();
        phoneField.sendKeys(randomPhone);

        WebElement messageField = wait.until(ExpectedConditions
                .presenceOfElementLocated(inputMessage));
        messageField.clear();
        messageField.sendKeys(randomMessage);
    }

    public void clickBtnSubmitPayment() {
        WebElement btnSubmit = wait.until(ExpectedConditions
                .presenceOfElementLocated(btnSubmitPayment));
        scrollerElement.scrollToElement(btnSubmit);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(btnSubmit)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnSubmit);
        }
    }
}
