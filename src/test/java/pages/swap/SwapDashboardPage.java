package pages.swap;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.*;
import api.InstallCoinLists;
import pages.BasePage;

public class SwapDashboardPage extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;

    InstallCoinLists installCoinLists = new InstallCoinLists();

    private By searchRectangleIcon = (By.xpath("//img[contains(@src, 'rectangle-copy-63')]"));
    private By searchBox = By.name("buy_search");
    private String categoryXpath = "//li[contains(@class, 'asset-tab-buy') and @data-type='%s']";


    public SwapDashboardPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    private By getCategoryLocator(String categoryName) {
        String formattedName = categoryName.substring(0, 1).toLowerCase() + categoryName.substring(1);
        return By.xpath(String.format(categoryXpath, formattedName));
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
        search.sendKeys(code, Keys.ENTER);
        
        
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
        
        
        
        // // 1. Klik icon pencarian
        // wait.until(ExpectedConditions.elementToBeClickable(searchRectangleIcon)).click();
        
        // // 2. Input code ke search box
        // var search = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
        // search.clear();
        // search.sendKeys(code, Keys.ENTER);
        
        // // 3. DETEKSI ASET BERDASARKAN DATA-CURRENCY
        // // Kita menggunakan .toLowerCase() karena data-currency biasanya selalu huruf kecil
        // String xpath = String.format(categoryXpath.toString(), code.toLowerCase());
        // By assetLocator = By.xpath(xpath);
        
        // // 4. Pilih elemen berdasarkan locator unik tersebut
        // var element = wait.until(ExpectedConditions.presenceOfElementLocated(assetLocator));
        
        // // 5. Scroll dan Klik
        // JavascriptExecutor js = (JavascriptExecutor) driver;
        // js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        // js.executeScript("arguments[0].click();", element);
    }


}
