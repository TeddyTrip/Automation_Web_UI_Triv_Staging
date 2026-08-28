package pages.buy;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

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

    public void selectCategory(String code) {
        // Dapatkan nama category asset dari API berdasarkan code
        String categoryName = installCoinLists.getCategoryFromApi(code);
        
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new RuntimeException("Category untuk asset dengan code '" + code + "' tidak ditemukan dari API!");
        }

        // Panggil getCategoryLocator untuk mendapatkan element-nya, lalu klik
        wait.until(ExpectedConditions.elementToBeClickable(getCategoryLocator(categoryName))).click();
    }

    public void selectAssetByCode(String code) {
        // 1. Dapatkan expected v_money dari API, lalu ubah ke huruf kecil (lowercase)
        String expectedVMoney = installCoinLists.getV_MoneyFromApi(code);
        
        Assert.assertNotNull("Gagal mendapatkan v_money dari API untuk code: " + code, expectedVMoney);
        
        String targetVMoney = expectedVMoney.toLowerCase();
        System.out.println("Expected v_money dari API (lowercase): " + targetVMoney);

        // 2. Klik icon pencarian
        wait.until(ExpectedConditions.elementToBeClickable(searchRectangleIcon)).click();
        
        // 3. Input code ke search box
        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
        search.clear();
        search.sendKeys(expectedVMoney, Keys.ENTER);
        
        
        // 4. BUAT XPATH BERDASARKAN DATA-V-MONEY (HURUF KECIL)
        // Mencari elemen <li> yang di dalamnya memiliki <span> dengan atribut data-v-money persis bernilai targetVMoney
        String xpath = String.format("//li[.//span[@data-v-money='%s']]", targetVMoney);
        By assetLocator = By.xpath(xpath);
        
        // Tunggu elemen muncul
        WebElement liElement = wait.until(ExpectedConditions.presenceOfElementLocated(assetLocator));
        
        // 5. AMBIL DAN VERIFIKASI HANYA ATRIBUT DATA-V-MONEY (DIUBAH KE HURUF KECIL)
        WebElement spanElement = liElement.findElement(By.xpath(".//span[@data-v-money]"));
        String actualVMoneyAttribute = spanElement.getAttribute("data-v-money").toLowerCase();
        
        System.out.println("Actual data-v-money di UI (lowercase): " + actualVMoneyAttribute);
        
        // Lakukan Assertion murni berdasarkan data-v-money lowercase (mengabaikan teks isi span)
        Assert.assertEquals(
            "Atribut data-v-money pada UI tidak sesuai dengan response API!", 
            targetVMoney, 
            actualVMoneyAttribute
        );

        // 6. Scroll dan Klik elemen yang valid
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", liElement);
        
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