package pages.buy;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.*;
import api.InstallCoinLists;

public class BuyDashboardPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Dynamic Locator Template (Cara rapi agar tidak ada XPath panjang di dalam method)
    private String categoryXpath = "//span[contains(@class, 'nav-link-buy') and normalize-space()='%s']";
    private String assetXpath = "//li[contains(@class, 'currency-option') and contains(., '%s')]";

    private By searchBox = By.name("buy_search");
    private By searchRectangleIcon = By.cssSelector("img.Rectangle-Copy-63");

    InstallCoinLists installCoinLists = new InstallCoinLists();

    public BuyDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    // --- Helper Methods untuk Locator Dinamis ---
    private By getCategoryLocator(String categoryName) {
        String formattedName = categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1);
        return By.xpath(String.format(categoryXpath, formattedName));
    }

    private By getAssetLocator(String code) {
        return By.xpath(String.format(assetXpath, code));
    }

    public void selectCategory(String category) {
        wait.until(ExpectedConditions.elementToBeClickable(getCategoryLocator(category))).click();
    }

    public void selectAssetByCode(String code) {
        // 1. Dapatkan expected v_money dari API
    String expectedVMoney = installCoinLists.getV_MoneyFromApi(code);
    
    // Pastikan API berhasil mengembalikan data sebelum lanjut ke UI
    Assert.assertNotNull("Gagal mendapatkan v_money dari API untuk code: " + code, expectedVMoney);
    System.out.println("Expected v_money dari API: " + expectedVMoney);

    // 2. Klik icon pencarian
    wait.until(ExpectedConditions.elementToBeClickable(searchRectangleIcon)).click();
    
    // 3. Input code ke search box
    WebElement search = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
    search.clear();
    search.sendKeys(code, Keys.ENTER);
    
    // 4. DETEKSI ASET
    // Catatan: Di HTML yang Anda berikan tidak ada 'data-currency', adanya class 'buy-zs'.
    // Jika Anda tetap ingin menggunakan data-currency (asumsi ada di struktur aslinya), gunakan xpath Anda:
    // String xpath = String.format("//li[@data-currency='%s']", code.toLowerCase());
    
    // Alternatif xpath berdasarkan class (menyesuaikan HTML yang diberikan: buy-zs):
    String xpath = String.format("//li[contains(@class, 'buy-%s')]", code.toLowerCase());
    By assetLocator = By.xpath(xpath);
    
    // Tunggu elemen LI muncul
    WebElement liElement = wait.until(ExpectedConditions.presenceOfElementLocated(assetLocator));
    
    // 5. VERIFIKASI DATA-V-MONEY
    // Cari elemen <span> di dalam <li> yang memiliki atribut data-v-money
    WebElement spanElement = liElement.findElement(By.xpath(".//span[@data-v-money]"));
    
    // Ambil nilai dari atribut data-v-money
    String actualVMoneyAttribute = spanElement.getAttribute("data-v-money");
    // Opsional: Ambil teksnya juga jika API mengembalikan nama lengkap seperti "Zscaler, Inc"
    String actualVMoneyText = spanElement.getText(); 
    
    System.out.println("Actual data-v-money di UI: " + actualVMoneyAttribute);
    
    // Lakukan Pengecekan (Assertion)
    // Ubah actualVMoneyAttribute menjadi actualVMoneyText jika API mengembalikan "Zscaler, Inc" bukan "zscalerinc"
    Assert.assertEquals(
        "Data v_money pada UI tidak sesuai dengan response API!", 
        expectedVMoney.toLowerCase(), // disamakan ke lowercase agar aman
        actualVMoneyAttribute.toLowerCase()
    );

    // 6. Scroll dan Klik
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", liElement);
    
    // Beri jeda sejenak (opsional) agar scroll selesai dengan sempurna sebelum klik
    try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
    
    js.executeScript("arguments[0].click();", liElement);







        // wait.until(ExpectedConditions.elementToBeClickable(searchRectangleIcon)).click();
        
        // var search = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
        // search.clear();
        // search.sendKeys(code, Keys.ENTER);

        // var element = wait.until(ExpectedConditions.presenceOfElementLocated(getAssetLocator(code)));
        // ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        // ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}