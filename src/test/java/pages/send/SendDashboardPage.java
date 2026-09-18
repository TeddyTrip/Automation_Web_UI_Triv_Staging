package pages.send;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.CsvDataManager;
import utils.CsvUtils;

public class SendDashboardPage {
    
    private WebDriver driver;
    private WebDriverWait wait;

    private By receivingAccountInput = By.xpath("//div[@class='card-box-value']//input[@name='receiving_account']");
    private By amountInput = By.xpath("//input[@id='withdraw_amount']");
    private By amountIdrInput = By.xpath("//input[@id='withdraw_idr']");
    private By buttonKirim = By.xpath("//a[@id='link_to_withdraw_2']");
    private By memoToggleLocator = By.xpath("//div[contains(@class, 'memo-toggle') or contains(@class, 'toggle')]"); // Sesuaikan jika ada class spesifik switch toggle memo
    private By memoIdInputLocator = By.id("memo_id");

    public SendDashboardPage(WebDriver driver) {
        super();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    public void inputWithdrawAmount(String amount) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement inputElement = wait.until(ExpectedConditions.elementToBeClickable(amountInput));
        
        // Bersihkan nilai sebelumnya (jika ada) lalu ketik nominal baru
        inputElement.clear();
        inputElement.sendKeys(amount);
        
        System.out.println("✅ Berhasil memasukkan nominal amount: " + amount);
    }

    public void inputReceivingAccount(String text) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement inputElement = wait.until(ExpectedConditions.elementToBeClickable(receivingAccountInput));
        
        // 1. Klik terlebih dahulu untuk memberikan fokus penuh ke input field
        inputElement.click();
        
        // 2. Bersihkan isi field
        inputElement.clear();
        
        // 3. Ketik teks secara langsung atau per karakter
        for (char c : text.toCharArray()) {
            inputElement.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(30); // Jeda kecil agar natural
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 4. MEMASTIKAN STATE FRAMEWORK FRONTEND TERPICU:
        // Terkadang framework butuh trigger event 'input' atau 'change' agar nilainya tersimpan di state form
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", inputElement);
        js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", inputElement);

        System.out.println("✅ Berhasil mengetik Receiving Account dan memicu event state: " + text);
    }

    /**
     * Function untuk menekan tombol Kirim dan mengembalikan nilai boolean 
     * (true jika pindah halaman, false jika masih di halaman yang sama / ada snackbar)
     */
    public boolean clickKirimButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        WebElement kirimButton = wait.until(ExpectedConditions.elementToBeClickable(buttonKirim));
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", kirimButton);
        
        try {
            kirimButton.click();
        } catch (Exception e) {
            System.out.println("⚠️ Menggunakan fallback JavaScript click untuk tombol Kirim...");
            js.executeScript("arguments[0].click();", kirimButton);
        }
        
        System.out.println("✅ Berhasil menekan tombol Kirim. Memeriksa transisi halaman...");

        // Mengembalikan status true/false berdasarkan perpindahan halaman
        return checkPageTransitionFromInputFormToConfirmation();
    }

    /**
     * Helper internal untuk mendeteksi apakah halaman sudah berpindah ke "Anda mengirimkan"
     */
    private boolean checkPageTransitionFromInputFormToConfirmation() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(), 'Anda mengirimkan')]")
            )) != null;
        } catch (Exception e) {
            return false; // Masih di halaman yang sama (tertahan snackbar error)
        }
    }

    public void selectProtocolIfAvailable(String code, String targetProtocol) {
        if (targetProtocol == null || targetProtocol.trim().isEmpty()) {
            return;
        }

        By protocolSectionLocator = By.cssSelector("p.wallet-protocol");

        try {
            java.util.List<WebElement> sections = driver.findElements(protocolSectionLocator);
            
            if (sections.isEmpty()) {
                System.out.println("ℹ️ Section pilihan protokol tidak ditemukan untuk aset [" + code + "]. Menggunakan protokol default.");
                return;
            }

            String normalizedProtocol = targetProtocol.toLowerCase();
            By protocolItemLocator = By.xpath("//p[contains(@class, 'wallet-protocol')]//span[@data-protocol='" + normalizedProtocol + "' or translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='" + normalizedProtocol + "']");

            org.openqa.selenium.support.ui.WebDriverWait shortWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(3));
            
            // Menunggu elemen muncul dan dipastikan bisa diklik (clickable)
            WebElement protocolElement = shortWait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(protocolItemLocator));

            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", protocolElement);
            
            // Eksekusi klik pada protokol yang ditemukan
            try {
                protocolElement.click();
            } catch (Exception e) {
                // Fallback klik via JavaScript jika elemen tertutup/intercepted
                js.executeScript("arguments[0].click();", protocolElement);
            }

            System.out.println("✅ Berhasil menemukan dan mengklik protokol [" + targetProtocol + "] untuk aset [" + code + "]");

        } catch (org.openqa.selenium.TimeoutException | org.openqa.selenium.NoSuchElementException e) {
            System.out.println("ℹ️ Opsi protokol [" + targetProtocol + "] tidak ditemukan di dalam section untuk aset [" + code + "], melewati...");
        } catch (Exception e) {
            System.out.println("⚠️ Terkendala saat mengklik protokol [" + targetProtocol + "] untuk aset [" + code + "]: " + e.getMessage());
        }
    }


    public boolean handleWithdrawValidation(String addressUser, String withdrawAmount) {
    By snackbarLocator = By.cssSelector("div.snackbar-withdraw");
    WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(4));

    try {
        WebElement snackbar = shortWait.until(ExpectedConditions.visibilityOfElementLocated(snackbarLocator));
        String errorText = snackbar.getText();

        System.out.println("⚠️ Snackbar error terdeteksi: " + errorText);

        if (errorText.contains("Receiving account is required") && errorText.contains("Amount is required")) {
            System.out.println("📝 Mengisi Receiving Account dari CSV & Amount Asset...");
            inputReceivingAccount(addressUser);
            inputWithdrawAmount(withdrawAmount);
            clickKirimButton();
        } 
        else if (errorText.contains("Receiving account is required")) {
            System.out.println("📝 Mengisi Receiving Account dari CSV & Amount Asset...");
            inputReceivingAccount(addressUser);
            clickKirimButton();
        } 
        else if (errorText.contains("Amount is required")) {
            System.out.println("📝 Mengisi Amount Asset menggunakan min withdraw...");
            inputWithdrawAmount(withdrawAmount);
            clickKirimButton();
        } 
        else if (errorText.contains("Can't process transaction above your wallet balance")) {
            System.out.println("⚠️ Saldo tidak mencukupi, menyesuaikan Amount...");
            inputWithdrawAmount(withdrawAmount);
            clickKirimButton();
        } 
        else if (errorText.contains("Amount can not be more than your wallet balance")) {
            System.out.println("⚠️ Saldo dompet tidak mencukupi (melebihi balance). Melewati aset ini...");
            return true; // 🔴 Mengembalikan nilai true agar step definition tahu harus skip
        }
        else {
            System.out.println("ℹ️ Pesan error lain ditemukan: " + errorText);
        }

    } catch (org.openqa.selenium.TimeoutException e) {
        System.out.println("✅ Tidak ada snackbar error yang muncul, transaksi berhasil dilanjutkan.");
    }
    
    return false; // Lanjutkan proses normal jika bukan error saldo
}

    /**
     * Function untuk menangani pengecekan dan pengisian Memo ID secara dinamis
     * @param memoId Nilai memo ID yang diambil dari Excel berdasarkan email terpilih
     */


public void handleMemoIdIfNeeded(String memoIdFromExcel, boolean isOptionalMemoId) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

    try {
        // 1. Validasi Pertama: Deteksi apakah switch/toggle Memo ID ada di halaman web ini
        List<WebElement> memoToggles = driver.findElements(By.xpath("//div[contains(@class, 'memo-toggle')]")); 
        
        if (memoToggles == null || memoToggles.isEmpty()) {
            System.out.println("ℹ️ [Validasi Memo ID]: Protocol/Aset ini tidak memiliki switch Memo ID di UI, melewati...");
            return;
        }

        System.out.println("🔍 [Validasi Memo ID]: Switch/Toggle Memo ID terdeteksi di halaman.");

        // 2. Validasi Kedua: Cek apakah data dari Excel memiliki Memo ID yang valid
        boolean hasValidMemoInExcel = (memoIdFromExcel != null && !memoIdFromExcel.trim().isEmpty() && !memoIdFromExcel.equalsIgnoreCase("nan"));

        // Logika penentuan: Jika optional ataupun wajib, selama Excel memiliki data memo, aktifkan dan isi
        if (hasValidMemoInExcel) {
            System.out.println("✅ [Validasi Memo ID]: Akun penerima memiliki Memo ID (" + memoIdFromExcel + "). Mengaktifkan toggle...");

            WebElement toggleSwitch = memoToggles.get(0);
            if (!toggleSwitch.getAttribute("class").contains("active")) {
                toggleSwitch.click();
            }

            // Isi nilai Memo ID huruf per huruf
            inputMemoId(memoIdFromExcel);

        } else {
            System.out.println("ℹ️ [Validasi Memo ID]: Akun penerima tidak memiliki Memo ID pada data Excel. Toggle dibiarkan non-aktif.");
        }

    } catch (Exception e) {
        System.out.println("⚠️ [Validasi Memo ID]: Terjadi kendala saat memproses Memo ID: " + e.getMessage());
    }
}

    /**
     * Function untuk mengetik Memo ID huruf per huruf
     */
    public void inputMemoId(String memoId) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement memoElement = wait.until(ExpectedConditions.elementToBeClickable(memoIdInputLocator));
        
        memoElement.clear();
        for (char c : memoId.toCharArray()) {
            memoElement.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(50); // Jeda natural 50ms per karakter
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("✅ Berhasil mengetik Memo ID huruf per huruf: " + memoId);
    }
}
