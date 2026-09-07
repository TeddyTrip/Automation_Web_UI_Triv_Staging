package pages.autoInvest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import api.InstallCoinLists;

import pages.BasePage;

public class CreateAutoInvestPage extends BasePage{
    private WebDriver driver;
    private WebDriverWait wait;

    InstallCoinLists installCoinLists = new InstallCoinLists();

    private By searchBarAutoInvest = By.id("auto_invest_search_keyword");
    private By btnKonfirmasiAutoInvest = By.xpath("//button[contains(@class, 'btn-primary') and normalize-space()='Konfirmasi']");
    private By dropDownFrequency = By.id("auto_invest_frequency");
    private By weeklyScheduleDropdown = By.id("weeklySchedule");
    private By monthlyScheduleDropdown = By.id("monthlySchedule");
    private By dropdownAssetButton = By.cssSelector("button.select-option-buy");
    private By inputNominalField = By.id("inputAmount");
    private By dropdownAutoInvestSimulationAssetButton = By.xpath("//button[contains(@class, 'select-option-buy') and .//span[@id='icon-selected-simulasi']]");
    private By rbAutoInvestSimulationWeekly = By.id("flexRadioDefault1");
    private By rbAutoInvestSimulationMonthly = By.id("flexRadioDefault2");
    private By btn1TahunAutoInvestSimulation = By.cssSelector("button.button-year[data-period='1']");
    private By btn2TahunAutoInvestSimulation = By.cssSelector("button.button-year[data-period='2']");
    private By btn3TahunAutoInvestSimulation = By.cssSelector("button.button-year[data-period='3']");
    private By searchBarAutoInvestSimulation = By.id("auto_invest_simulation_search_keyword");
    private By textAutoInvestSimulationPriceCalculation = By.cssSelector(".result-panel span.sr-result");

    public CreateAutoInvestPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public void clickAssetDropdown() {
        WebElement dropdownBtn = wait.until(ExpectedConditions.presenceOfElementLocated(dropdownAssetButton));
    
        // Cek status aria-expanded. Jika "false" (tertutup), baru kita klik.
        String isExpanded = dropdownBtn.getAttribute("aria-expanded");
        
        if ("false".equals(isExpanded)) {
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", dropdownBtn);
            System.out.println("Dropdown tertutup, melakukan klik untuk membuka.");
        } else {
            System.out.println("Dropdown sudah terbuka, lanjut ke pencarian.");
        }
    }

    public void clickAssetDropdownAutoInvestSimulation() {
        WebElement dropdownBtn = wait.until(ExpectedConditions.presenceOfElementLocated(dropdownAutoInvestSimulationAssetButton));
    
        // Cek status aria-expanded. Jika "false" (tertutup), baru kita klik.
        String isExpanded = dropdownBtn.getAttribute("aria-expanded");
        
        if ("false".equals(isExpanded)) {
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", dropdownBtn);
            System.out.println("Dropdown tertutup, melakukan klik untuk membuka.");
        } else {
            System.out.println("Dropdown sudah terbuka, lanjut ke pencarian.");
        }
    }

    public void select1YearPeriodAutoInvestSimulation() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btn1TahunAutoInvestSimulation));
        
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
        js.executeScript("arguments[0].click();", btn);
        
        System.out.println("✅ Berhasil memilih periode investasi: 1 Tahun");
    }

    public void select2YearPeriodAutoInvestSimulation() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btn2TahunAutoInvestSimulation));
        
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
        js.executeScript("arguments[0].click();", btn);
        
        System.out.println("✅ Berhasil memilih periode investasi: 2 Tahun");
    }

    public void select3YearPeriodAutoInvestSimulation() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btn3TahunAutoInvestSimulation));
        
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
        js.executeScript("arguments[0].click();", btn);
        
        System.out.println("✅ Berhasil memilih periode investasi: 3 Tahun");
    }



    public void searchAndSelectAsset(String code) {
        String assetVMoneyName = installCoinLists.getV_MoneyFromApi(code);
        String assetLabel = installCoinLists.getLabelFromApi(code);
        
        // 1. Temukan tombol dropdown dan scroll ke tengah
        WebElement dropdownBtn = wait.until(ExpectedConditions.presenceOfElementLocated(dropdownAssetButton));
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", dropdownBtn);

        System.out.println("🖱️ Membuka dropdown aset (menerapkan penanganan klik ganda)...");

        // 2. KLIK PERTAMA (Untuk memberikan fokus / state awal)
        js.executeScript("arguments[0].click();", dropdownBtn);

        // 3. Cek apakah search bar langsung terbuka
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(1));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(searchBarAutoInvest));
        } catch (Exception e) {
            // Jika klik pertama gagal membuka search bar, lakukan klik kedua
            System.out.println("⚠️ Klik pertama tidak membuka dropdown, mengeksekusi klik kedua...");
            js.executeScript("arguments[0].click();", dropdownBtn);
        }

        // 4. Pastikan search bar visible dan masukkan nilai aset
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBarAutoInvest));
        field.clear();
        field.sendKeys(assetVMoneyName);

        // 5. XPath opsi aset yang dicari
        String xpathOption = "//div[contains(@class, 'dropdown-list-buy') and (" +
                            "contains(@data-label, '" + assetLabel + "') or " +
                            "@data-currency='" + code + "' or " +
                            "contains(@data-icon, '" + assetVMoneyName.toLowerCase() + "') or " +
                            "contains(., '" + assetVMoneyName + "'))]";

        // 6. MEKANISME RETRY CERDAS (Menggantikan Thread.sleep kaku agar tahan banting terhadap lag server)
        WebElement element = null;
        int maxRetries = 30;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                WebDriverWait retryWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(3));
                element = retryWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathOption)));
                if (element != null) {
                    break; // Berhasil ditemukan, keluar dari loop
                }
            } catch (Exception e) {
                System.out.println("⏳ API Staging lambat, mencoba ulang mencari aset " + assetVMoneyName + " (Percobaan " + attempt + "/" + maxRetries + ")...");
                try {
                    field.clear();
                    field.sendKeys(assetVMoneyName); // Ketik ulang jika input sempat ter-reset
                    Thread.sleep(1000); // Jeda tunggu singkat untuk server merespon
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (element == null) {
            throw new RuntimeException("❌ Gagal menemukan aset " + assetVMoneyName + " (" + code + ") setelah " + maxRetries + " kali percobaan karena kendala server.");
        }

        // 7. Klik elemen aset yang ditemukan
        js.executeScript("arguments[0].click();", element);
        System.out.println("✅ Berhasil memilih aset: " + assetVMoneyName);
    }

    public void searchAndSelectAssetAutoInvestSimulation(String code) {
    
        String assetVMoney = installCoinLists.getV_MoneyFromApi(code);
        String assetLabel = installCoinLists.getLabelFromApi(code);
    
        // 1. Klik tombol dropdown KANAN secara unik berdasarkan ID span di dalamnya
        WebElement dropdownBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[.//span[@id='icon-selected-simulasi']]"))
        );
        
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", dropdownBtn);

        try {
            dropdownBtn.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", dropdownBtn);
        }

        WebDriverWait dropdownWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(5));
        
        // 2. Gunakan ID unik search bar simulasi di panel kanan
        WebElement field = dropdownWait.until(ExpectedConditions.elementToBeClickable(
            searchBarAutoInvestSimulation
        ));
        field.clear();
        
        // Isi nilai search bar langsung via JavaScript agar framework web langsung memicu filter
        js.executeScript("arguments[0].value = arguments[1];", field, assetVMoney);
        js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", field);
        js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", field);
        js.executeScript("arguments[0].dispatchEvent(new Event('keyup', { bubbles: true }));", field);

        // Beri jeda singkat untuk render hasil filter
        try {
            Thread.sleep(800);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        // 3. XPath universal yang mencocokkan data-currency atau teks di dalam elemen panel kanan yang sedang tampil
        String scopedXpathOption = "(//div[@id='resultList-simulation']//div[contains(@class, 'dropdown-list-buy') and " +
                            "@data-currency='" + code + "' and " +
                            "@data-icon='" + assetVMoney.toLowerCase() + "' and " +
                            "@data-label='" + assetLabel + "' and " +
                            "not(contains(@style, 'display: none'))])[1]";

        System.out.println("🔍 XPATH SIMULASI KANAN: " + scopedXpathOption);

        WebElement element = null;
        int maxRetries = 2;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                WebDriverWait retryWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(2));
                element = retryWait.until(ExpectedConditions.elementToBeClickable(By.xpath(scopedXpathOption)));
                if (element != null) {
                    System.out.println("✅ Elemen visible ditemukan di Simulasi Kanan pada percobaan ke-" + attempt);
                    break;
                }
            } catch (Exception e) {
                System.out.println("⏳ Menunggu hasil filter di panel kanan untuk " + code + " (Percobaan " + attempt + "/" + maxRetries + ")...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Jika masih null, cetak debug seluruh opsi yang tampil di #resultlist-simulation
        if (element == null) {
            System.out.println("⚠️ DEBUG: Opsi visible di #resultlist-simulation tidak ditemukan. Mencetak semua opsi yang terbaca:");
            try {
                java.util.List<WebElement> allOptions = driver.findElements(By.xpath("//div[@id='resultlist-simulation']//div[contains(@class, 'dropdown-list-buy')]"));
                for (WebElement opt : allOptions) {
                    System.out.println("   - currency: " + opt.getAttribute("data-currency") + 
                                    " | label: " + opt.getAttribute("data-label") + 
                                    " | style: " + opt.getAttribute("style"));
                }
            } catch (Exception ex) {
                System.out.println("Gagal mencetak debug: " + ex.getMessage());
            }

            throw new RuntimeException("❌ Aset dengan kode " + code + " tidak ditemukan / tidak tampil di panel Simulasi Auto-Invest!");
        }

        // 4. Eksekusi klik pada elemen yang ditemukan di panel kanan menggunakan Actions / JS Fallback
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        try {
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            Thread.sleep(300);
            actions.moveToElement(element).click().perform();
            System.out.println("✅ Berhasil memilih aset di Simulasi Kanan: " + code);
        } catch (Exception e) {
            System.out.println("⚠️ Actions click gagal, mencoba fallback dengan JS click...");
            js.executeScript("arguments[0].click();", element);
            System.out.println("✅ Berhasil memilih aset via JS Fallback di Simulasi Kanan: " + code);
        }
    }

    public void selectFrequency(String frequencyValue) {
        System.out.println("📌 [Native Select] Memilih frekuensi: " + frequencyValue);
        
        // 1. Tunggu elemen <select> muncul dan bisa dilihat
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(dropDownFrequency));
        
        // 2. Scroll ke tengah layar agar posisi elemen stabil
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        try { Thread.sleep(300); } catch (InterruptedException e) {}

        // 3. Gunakan kelas Select resmi Selenium
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(element);
        
        // 4. Pilih berdasarkan value (misal: "weekly" atau "monthly")
        select.selectByValue(frequencyValue.toLowerCase());
        
        System.out.println("✅ Berhasil memilih frekuensi: " + frequencyValue);
    }

    public void selectNominalButton(String nominalValue) {
        // Locator berdasarkan teks di dalam tag <a> dengan class btn-selected-buy
        By nominalBtn = By.xpath("//a[contains(@class, 'btn-selected-buy') and normalize-space(text())='" + nominalValue + "']");
        wait.until(ExpectedConditions.elementToBeClickable(nominalBtn)).click();
    }

    public void selectWeeklyDay(String dayValue) {
        System.out.println("📌 [Native Select] Memilih hari: " + dayValue);
        
        // 1. Tunggu elemen <select> weekly schedule muncul
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(weeklyScheduleDropdown));
        
        // 2. Scroll ke tengah layar
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        try { Thread.sleep(300); } catch (InterruptedException e) {}

        // 3. Gunakan kelas Select resmi Selenium
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(element);
        
        // 4. Pilih berdasarkan value (misal: "monday", "wednesday", "friday", dll)
        select.selectByValue(dayValue.toLowerCase());
        
        System.out.println("✅ Berhasil memilih hari: " + dayValue);
    }

    

    public void selectMonthlyDate(String dateValue) {
        // 1. Tunggu elemen sampai ada di DOM (presence), baru tunggu sampai terlihat (visibility)
        // Ini penting karena elemen sering disembunyikan (display: none) sebelum frekuensi dipilih
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(monthlyScheduleDropdown));
        wait.until(ExpectedConditions.visibilityOf(element));

        // 2. Scroll ke elemen agar posisinya berada di tengah layar
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);

        // 3. Klik elemennya agar dropdown terbuka secara visual
        element.click();

        // 4. Jeda sebentar agar Anda bisa melihat dropdown terbuka sebelum data dipilih
        try {
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 5. Pilih nilainya
        Select select = new Select(element);
        select.selectByValue(dateValue);
        
        System.out.println("Memilih Tanggal (Monthly): " + dateValue);
    }

    public void inputAmountIDR(String amount){
        System.out.println("⌨️ Memasukkan nominal investasi secara custom: " + amount);
    
        // 1. Tunggu hingga input field terlihat dan bisa diakses
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(inputNominalField));
        
        // 2. Scroll ke tengah layar agar elemen berada di area pandang browser
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", field);
        try { Thread.sleep(300); } catch (InterruptedException e) {}

        // 3. Klik untuk memberi fokus, lalu bersihkan nilai yang ada di dalam input
        field.click();
        field.clear();
        
        // 4. Masukkan nilai kustom (misal: "1000000" atau variabel string lainnya)
        field.sendKeys(amount);
        
        System.out.println("✅ Berhasil memasukkan nominal: " + amount);
    }

    public void confirmAutoInvest() {
        wait.until(ExpectedConditions.elementToBeClickable(btnKonfirmasiAutoInvest)).click();
    }

    public boolean isWeekendErrorAlertDisplayed() {
        try {
            // Gunakan timeout singkat 2 detik
            WebDriverWait shortWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(2));
            
            // Perbaikan: Gunakan normalize-space(.) dan translate() agar case-insensitive & mencakup teks bersarang
            WebElement errorAlert = shortWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'alert-danger') and contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sabtu dan minggu')]")
            ));
            
            return errorAlert.isDisplayed();
        } catch (Exception e) {
            return false; // Mengembalikan false jika alert error weekend tidak muncul
        }
    }

    public void waitForSuccessAlertToComplete() {
        try {
            By successAlert = By.xpath("//div[contains(@class, 'alert-success') and contains(., 'Auto Invest was successfully created')]");
            
            // 1. Tunggu sampai alert sukses muncul di layar
            WebDriverWait shortWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(successAlert));
            System.out.println("✅ Alert sukses terdeteksi muncul di layar.");

            // 2. Tunggu sampai alert sukses tersebut hilang kembali (invisibility)
            // Ini memastikan animasi flash notice selesai dan halaman kembali bersih
            WebDriverWait longWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));
            longWait.until(ExpectedConditions.invisibilityOfElementLocated(successAlert));
            System.out.println("✅ Alert sukses sudah menghilang, lanjut ke iterasi berikutnya.");
            
        } catch (Exception e) {
            System.out.println("ℹ️ Alert sukses tidak terdeteksi atau sudah hilang lebih cepat.");
        }
    }

    public boolean waitForCalculationToComplete() {
        System.out.println("⏳ Menunggu proses kalkulasi selesai (maksimal 20 detik)...");
        
        long startTime = System.currentTimeMillis();
        long timeoutMillis = 20000; // 20 detik

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            try {
                // 1. Cek apakah muncul pesan error bahwa aset tidak dapat disimulasikan
                java.util.List<WebElement> errorElements = driver.findElements(
                    By.xpath("//*[contains(text(), 'Kami tidak dapat melakukan simulasi Auto Invest')]")
                );
                for (WebElement errEl : errorElements) {
                    if (errEl.isDisplayed()) {
                        String errText = errEl.getText().trim();
                        System.out.println("❌ Aset ini melewati simulasi: \"" + errText + "\"");
                        return false; // Mengembalikan false agar bisa langsung dilompati (skip) ke aset berikutnya
                    }
                }

                // 2. Cek kalkulasi harga normal
                WebElement resultElement = driver.findElement(textAutoInvestSimulationPriceCalculation);
                String text = resultElement.getText().trim();
                
                boolean isCalculating = text.toLowerCase().contains("calculating");
                boolean isInvalidOrZero = text.isEmpty() || text.equals("0") || text.equals("0.00");
                
                if (isCalculating || isInvalidOrZero) {
                    System.out.println("⚠️ Status saat ini: \"" + text + "\" (Masih menghitung atau bernilai 0, menunggu...)");
                } else {
                    System.out.println("✅ Berhasil! Aset memiliki harga valid: \"" + text + "\"");
                    return true; // Kalkulasi sukses
                }
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                System.out.println("🔄 Terdeteksi perubahan DOM (StaleElement), mencoba membaca ulang...");
            } catch (Exception e) {
                // Abaikan error sementara selama polling
            }

            try {
                Thread.sleep(1000); // Jeda 1 detik per pengecekan
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("⚠️ Timeout: Kalkulasi tidak selesai dalam waktu 20 detik.");
        return false;
    }

    public void selectWeeklyFrequencyAutoInvestSimulation() {
        WebElement radioBtn = wait.until(ExpectedConditions.elementToBeClickable(rbAutoInvestSimulationWeekly));
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", radioBtn);
        System.out.println("✅ Berhasil memilih frekuensi Auto Invest Simulation: Weekly");
    }

    public void selectMonthlyFrequencyAutoInvestSimulation() {
        WebElement radioBtn = wait.until(ExpectedConditions.elementToBeClickable(rbAutoInvestSimulationMonthly));
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", radioBtn);
        System.out.println("✅ Berhasil memilih frekuensi Auto Invest Simulation: Monthly");
    }
}
