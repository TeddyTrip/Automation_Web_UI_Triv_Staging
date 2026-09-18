package pages.dashboard;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import api.InstallCoinLists;
import pages.BasePage;

public class DashboardPage extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;

    InstallCoinLists installCoinLists = new InstallCoinLists();

    private By iconBuySell = By.cssSelector("a[href='/dashboard/buy-sell']");
    private By iconSwap = By.cssSelector("a[href='/dashboard/coin/swap']");
    private By iconAutoInvest = By.cssSelector("a[href='/dashboard/auto-invest']");
    private By iconSearchDashboard = By.xpath("//img[@class='Rectangle-Copy-63' and starts-with(@src, '/assets/user-dashboard/icons/rectangle-copy-63')]");
    private By searchWalletInputIdentifier = By.cssSelector("input.dashboard-search[name='dashboard_search'][placeholder='Search Wallet']");

    public DashboardPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void clickBuySellIconOnDashboard() { 
        wait.until(ExpectedConditions.elementToBeClickable(iconBuySell)).click(); 
    }

    public void clickSwapIconOnDashboard() {
        wait.until(ExpectedConditions.elementToBeClickable(iconSwap)).click();
    }

    public void clickAutoInvestIconOnDashboard() {
        wait.until(ExpectedConditions.elementToBeClickable(iconAutoInvest)).click();
    }

    public void clickSearchIconOnDashboard() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            // Coba tunggu sampai bisa diklik secara normal
            WebElement searchIcon = wait.until(ExpectedConditions.elementToBeClickable(iconSearchDashboard));
            searchIcon.click();
        } catch (org.openqa.selenium.TimeoutException e) {
            System.out.println("⚠️ Ikon search tidak langsung clickable, mencoba menggunakan JavaScript Click...");
            try {
                WebElement searchIcon = driver.findElement(iconSearchDashboard);
                // Scroll ke elemen dan klik via JS untuk menghindari gangguan overlay/intersepsi
                org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView(true);", searchIcon);
                js.executeScript("arguments[0].click();", searchIcon);
            } catch (Exception ex) {
                System.out.println("❌ Gagal mengklik icon search: " + ex.getMessage());
                throw ex;
            }
        }
    }

    public void searchWalletByVMoney(String code) {
    if (driver == null) {
        throw new IllegalStateException("WebDriver belum diinisialisasi! Pastikan driver sudah diset sebelum memanggil method ini.");
    }

    String vMoneyName = installCoinLists.getV_MoneyFromApi(code);
    if (vMoneyName == null || vMoneyName.isEmpty()) {
        vMoneyName = code; 
    }

    WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(searchWalletInputIdentifier));
    
    org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", searchInput);
    
    searchInput.clear();
    
    // Simulasi pengetikan per karakter
    for (char c : vMoneyName.toCharArray()) {
        searchInput.sendKeys(String.valueOf(c));
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    // Trigger event JavaScript tambahan jika diperlukan oleh framework web
    js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", searchInput);
    js.executeScript("arguments[0].dispatchEvent(new Event('keyup', { bubbles: true }));", searchInput);

    // Menekan tombol Enter agar hasil pencarian keluar
    searchInput.sendKeys(org.openqa.selenium.Keys.ENTER);

    System.out.println("✅ Berhasil mengetik pencarian wallet dan menekan Enter untuk VMoney: " + vMoneyName + " (Code: " + code + ")");
}

    public void hoverAssetCard(String code) {
        String vMoneyName = installCoinLists.getV_MoneyFromApi(code);
        
        By cardIdentifier;
        if (vMoneyName != null && !vMoneyName.trim().isEmpty()) {
            // Jika vMoneyName tersedia, gunakan atribut data-vname
            cardIdentifier = By.xpath("//div[@data-vname='" + vMoneyName.toLowerCase() + "']");
        } else {
            // Jika vMoneyName kosong, fallback menggunakan atribut data-currency dengan parameter code
            cardIdentifier = By.xpath("//div[translate(@data-currency, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='" + code.toLowerCase() + "']");
        }

        // Tunggu hingga card elemen ditemukan di DOM
        WebElement cardElement = wait.until(ExpectedConditions.presenceOfElementLocated(cardIdentifier));

        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", cardElement);

        // Jeda mikro untuk memastikan posisi stabil setelah scroll
        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        // Eksekusi hover menggunakan class Actions
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        actions.moveToElement(cardElement).perform();

        System.out.println("✅ Berhasil melakukan hover pada card aset menggunakan: " + 
            (vMoneyName != null && !vMoneyName.trim().isEmpty() ? "data-vname (" + vMoneyName + ")" : "data-currency (" + code + ")"));
    }

    public boolean hoverAndClickWithdrawButton(String code) {
        String vMoneyName = installCoinLists.getV_MoneyFromApi(code);
        String targetCoin = (vMoneyName != null && !vMoneyName.trim().isEmpty()) ? vMoneyName.toLowerCase() : code.toLowerCase();
        
        // 1. Tentukan identifier card berdasarkan vMoneyName atau code
        By cardIdentifier;
        if (vMoneyName != null && !vMoneyName.trim().isEmpty()) {
            cardIdentifier = By.xpath("//div[@data-vname='" + vMoneyName.toLowerCase() + "']");
        } else {
            cardIdentifier = By.xpath("//div[translate(@data-currency, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='" + code.toLowerCase() + "']");
        }

        try {
            // 2. Cari dan hover card
            WebElement cardElement = wait.until(ExpectedConditions.presenceOfElementLocated(cardIdentifier));
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", cardElement);
            
            Thread.sleep(300);
            
            org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
            actions.moveToElement(cardElement).perform();
            
        } catch (Exception e) {
            System.out.println("⚠️ Card untuk aset " + code + " tidak ditemukan di UI.");
            return false;
        }

        // 3. Deteksi apakah tombol withdraw muncul setelah di-hover (gunakan timeout pendek, misal 3 detik)
        By withdrawButtonIdentifier = By.xpath("//a[contains(@class, 'btn-purple') and contains(@href, '/dashboard/withdraw-coin?coin=" + targetCoin + "')]");
        
        try {
            org.openqa.selenium.support.ui.WebDriverWait shortWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(3));
            WebElement withdrawBtn = shortWait.until(ExpectedConditions.elementToBeClickable(withdrawButtonIdentifier));
            
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            try {
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", withdrawBtn);
                withdrawBtn.click();
            } catch (Exception ex) {
                js.executeScript("arguments[0].click();", withdrawBtn);
            }
            
            System.out.println("✅ Berhasil mengklik tombol withdraw untuk aset: " + code);
            return true;
            
        } catch (org.openqa.selenium.TimeoutException | org.openqa.selenium.NoSuchElementException e) {
            // Tombol tidak ada (karena fitur withdraw tidak aktif/tidak ada tombolnya di UI)
            System.out.println("⏭️ Tombol withdraw tidak tersedia untuk aset " + code + ". Melewati ke aset berikutnya...");
            return false;
        }
    }

//     public void clickRectangleIcon() {
//     WebElement icon = wait.until(ExpectedConditions.elementToBeClickable(iconIdentifier));
//     org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
    
//     try {
//         js.executeScript("arguments[0].scrollIntoView({block: 'center'});", icon);
//         icon.click();
//         System.out.println("✅ Berhasil mengklik icon secara unik.");
//     } catch (Exception e) {
//         System.out.println("⚠️ Klik standar gagal, mengeksekusi klik via JavaScript...");
//         js.executeScript("arguments[0].click();", icon);
//     }
// }


    // public void searchWallet(String keyword) {
    //     WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(searchWalletInputIdentifier));
        
    //     org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
    //     js.executeScript("arguments[0].scrollIntoView({block: 'center'});", searchInput);
        
    //     searchInput.clear();
    //     searchInput.sendKeys(keyword);
    //     System.out.println("✅ Berhasil memasukkan kata kunci pencarian wallet: " + keyword);
    // }
}
