package pages.sell;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.BasePage;

public class SellInputAmountPage extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By amountAssetSellInputField = By.id("amount_1");
    private By amountIdrSellInputField = By.id("amount_2");

    // Locator untuk snackbar
    private By snackbarContainer = By.cssSelector("div.snackbar");
    private By snackbarMessage = By.cssSelector("ul.warning li");

    private By btnLanjut = By.id("link_to_sell_2");

    private By warningMessage = By.cssSelector("div.snackbar li");

    public SellInputAmountPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public void inputAssetAmount(String amount) {
        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));

    // 1. Cari semua elemen input dengan class yang sama (menggunakan CSS class selector)
    // Gunakan titik (.) untuk class. Class dari snippet Anda: value, text-size-14, text-color-white, currency, valid
    // Kita pakai class yang paling unik, yaitu "value" dan "currency"
    By inputLocator = By.cssSelector("input.value.currency");
    
    // 2. Tunggu sampai minimal ada 1 elemen yang muncul
    List<WebElement> inputs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(inputLocator));
    
    System.out.println("Jumlah input ditemukan: " + inputs.size());

    WebElement targetField = null;

    // 3. Debugging: Cetak ID masing-masing elemen yang ditemukan
    for (int i = 0; i < inputs.size(); i++) {
        String id = inputs.get(i).getAttribute("id");
        System.out.println("Input ke-" + i + " memiliki ID: " + id);
        
        // Asumsi: Anda butuh amount_1 (sesuaikan jika butuh yang lain)
        if (id.equals(amountAssetSellInputField.toString().replace("By.id: ", ""))) {
            targetField = inputs.get(i);
        }
    }

    // 4. Eksekusi
    if (targetField != null) {
        
        // Wait sampai bisa diklik
        wait.until(ExpectedConditions.elementToBeClickable(targetField));
        
        targetField.clear();

        try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

        targetField.sendKeys(amount);

        try {
        Thread.sleep(1000); // Tambahkan jeda 1 detik agar input bisa diproses
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    } else {
        throw new RuntimeException("Elemen target tidak ditemukan di antara list input yang ada!");
    }
    }

    public void inputIdrAmount(String amount) {
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));

    // 1. Cari semua elemen input dengan class yang sama (menggunakan CSS class selector)
    // Gunakan titik (.) untuk class. Class dari snippet Anda: value, text-size-14, text-color-white, currency, valid
    // Kita pakai class yang paling unik, yaitu "value" dan "currency"
    By inputLocator = By.cssSelector("input.value.currency");
    
    // 2. Tunggu sampai minimal ada 1 elemen yang muncul
    List<WebElement> inputs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(inputLocator));
    
    System.out.println("Jumlah input ditemukan: " + inputs.size());

    WebElement targetField = null;

    // 3. Debugging: Cetak ID masing-masing elemen yang ditemukan
    for (int i = 0; i < inputs.size(); i++) {
        String id = inputs.get(i).getAttribute("id");
        System.out.println("Input ke-" + i + " memiliki ID: " + id);
        
        // Asumsi: Anda butuh amount_2 (sesuaikan jika butuh yang lain)
        if (id.equals(amountIdrSellInputField.toString().replace("By.id: ", ""))) {
            targetField = inputs.get(i);
        }
    }

    // 4. Eksekusi
    if (targetField != null) {
        
        // Wait sampai bisa diklik
        wait.until(ExpectedConditions.elementToBeClickable(targetField));
        
        targetField.clear();

        try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

        targetField.sendKeys(amount);

        try {
        Thread.sleep(1000); // Tambahkan jeda 1 detik agar input bisa diproses
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    } else {
        throw new RuntimeException("Elemen target tidak ditemukan di antara list input yang ada!");
    }
}


    public void clickLanjutButton() {
        wait.until(ExpectedConditions.elementToBeClickable(btnLanjut)).click();
    }

    public String getValidationMessage() {
    try {
        // Kita gunakan Javascript untuk mengambil teks dari snackbar
        // .snackbar li ini adalah locator yang kita pakai
        String script = "return document.querySelector('.snackbar li') ? document.querySelector('.snackbar li').innerText : 'NO_ERROR';";
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String message = (String) js.executeScript(script);
        
        if (!message.equals("NO_ERROR")) {
            System.out.println("Pesan yang didapat dari UI (via JS): " + message);
        }
        
        return message; 
    } catch (Exception e) {
        System.out.println("DEBUG: Gagal mengambil pesan via JS. Error: " + e.getMessage());
        return "NO_ERROR"; 
    }
}
}


