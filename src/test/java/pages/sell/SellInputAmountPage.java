package pages.sell;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import formula.MinimalBuySellAssetSpotCalculation;
import pages.BasePage;

public class SellInputAmountPage extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By amountAssetSellInputField = By.id("amount_1");
    private By amountIdrSellInputField = By.id("amount_2");
    private By button25Locator = By.xpath("//span[@class='auto-input-amount' and normalize-space()='25%']");
    private By button50Locator = By.xpath("//span[@class='auto-input-amount' and normalize-space()='50%']");
    private By button75Locator = By.xpath("//span[@class='auto-input-amount' and normalize-space()='75%']");
    private By button100Locator = By.xpath("//span[@class='auto-input-amount' and normalize-space()='100%']");

    // Locator untuk snackbar
    private By snackbarContainer = By.cssSelector("div.snackbar");
    private By snackbarMessage = By.cssSelector("ul.warning li");

    private By btnLanjut = By.id("link_to_sell_2");

    private By warningMessage = By.cssSelector("div.snackbar li");

    MinimalBuySellAssetSpotCalculation minimalBuySellAssetSpotCalculation = new MinimalBuySellAssetSpotCalculation();

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

    public void inputAmountInIDRUsingMinimumSellTransaction(String code) {
        String amountInIDR = minimalBuySellAssetSpotCalculation.getMinimalSellPriceWithCertainCalculation(code);

        String finalAmount = String.format("%.0f", Double.parseDouble(amountInIDR));
        
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

            targetField.sendKeys(finalAmount);

            try {
                Thread.sleep(1000); // Tambahkan jeda 1 detik agar input bisa diproses
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } 
        else {
            throw new RuntimeException("Elemen target tidak ditemukan di antara list input yang ada!");
        }
    }

    public void inputCustomAmountInIDR(String amountInIDR) {

        String finalAmount = String.format("%.0f", Double.parseDouble(amountInIDR));
        
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

            targetField.sendKeys(finalAmount);

            try {
                Thread.sleep(1000); // Tambahkan jeda 1 detik agar input bisa diproses
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } 
        else {
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

    public void click25Percent() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(button25Locator));
            btn.click();
            System.out.println("✅ Berhasil mengklik tombol 25%");
        } catch (Exception e) {
            System.out.println("⚠️ Gagal klik normal, mencoba JavaScript Click untuk tombol 25%...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(button25Locator));
        }
    }

    public void click50Percent() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(button50Locator));
            btn.click();
            System.out.println("✅ Berhasil mengklik tombol 50%");
        } catch (Exception e) {
            System.out.println("⚠️ Gagal klik normal, mencoba JavaScript Click untuk tombol 50%...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(button50Locator));
        }
    }

    public void click75Percent() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(button75Locator));
            btn.click();
            System.out.println("✅ Berhasil mengklik tombol 75%");
        } catch (Exception e) {
            System.out.println("⚠️ Gagal klik normal, mencoba JavaScript Click untuk tombol 75%...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(button75Locator));
        }
    }

    public void click100Percent() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(button100Locator));
            btn.click();
            System.out.println("✅ Berhasil mengklik tombol 100%");
        } catch (Exception e) {
            System.out.println("⚠️ Gagal klik normal, mencoba JavaScript Click untuk tombol 100%...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(button100Locator));
        }
    }
}


