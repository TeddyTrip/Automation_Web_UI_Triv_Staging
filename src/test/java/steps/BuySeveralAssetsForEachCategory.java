// package steps;

// import io.cucumber.java.en.*;
// import io.restassured.RestAssured;
// import io.restassured.path.json.JsonPath;
// import utils.*;

// import org.openqa.selenium.By;
// import org.openqa.selenium.JavascriptExecutor;
// import org.openqa.selenium.Keys;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import java.time.Duration;
// import java.util.*;
// import java.util.stream.Collectors;

// public class BuySeveralAssetsForEachCategory {

//     // Menyimpan daftar aset yang terpilih untuk diproses di loop pembelian
//     private List<BuyRandomAssets> assetsTerpilih = new ArrayList<>();

//     @And("Memilih asset random sesuai yang ditentukan")
// public void memilih_asset_random() {
//     // 1. Bersihkan list agar selalu fresh di setiap run
//     assetsTerpilih.clear();
//     System.out.println("Menyiapkan daftar aset baru...");

//     String limitStr = ConfigReader.getProperty("jumlah_random_per_kategori");
//     int limit = (limitStr != null) ? Integer.parseInt(limitStr) : 1;
    
//     JsonPath json = RestAssured.get("https://cihuy.triv.id/api/v1/install/coin/lists").jsonPath();
//     List<Map<String, Object>> semuaData = json.getList("");

//     String[] kategoris = {"crypto", "stocks", "usd", "gold", "oil", "euro"};

//     for (String cat : kategoris) {
//         List<Map<String, Object>> filtered = semuaData.stream()
//             .filter(a -> cat.equalsIgnoreCase(String.valueOf(a.get("category"))))
//             .filter(a -> !"PAYPAL".equalsIgnoreCase(String.valueOf(a.get("code"))))
//             .collect(Collectors.toList());

//         Collections.shuffle(filtered);
//         List<Map<String, Object>> selected = filtered.stream().limit(limit).collect(Collectors.toList());

//         for (Map<String, Object> item : selected) {
//             assetsTerpilih.add(new BuyRandomAssets(
//                 String.valueOf(item.get("id")), 
//                 String.valueOf(item.get("wallet_id")), 
//                 String.valueOf(item.get("code")), 
//                 String.valueOf(item.get("currency")), 
//                 String.valueOf(item.get("label")), 
//                 String.valueOf(item.get("buy")), 
//                 String.valueOf(item.get("sell")), 
//                 String.valueOf(item.get("category"))
//             ));
//         }
//     }

//     // 2. Cetak ke console daftar aset yang terpilih
//     System.out.println("--- Daftar Aset yang Dipilih untuk Operasi ---");
//     if (assetsTerpilih.isEmpty()) {
//         System.out.println("Tidak ada aset yang terpilih.");
//     } else {
//         for (BuyRandomAssets a : assetsTerpilih) {
//             System.out.println("Kategori: " + a.category.toUpperCase() + 
//                                " | Kode: " + a.code + 
//                                " | Label: " + a.label);
//         }
//     }
//     System.out.println("----------------------------------------------");
// }

//     @And("Membeli semua aset yang sudah disiapkan secara berurutan")
//     public void eksekusi_pembelian_massal() {
//         WebDriverWait wait = new WebDriverWait(LoginSteps.driver, Duration.ofSeconds(15));
//         String nominalBeli = ConfigReader.getProperty("buy_amount_in_idr");
//         if (nominalBeli == null || nominalBeli.isEmpty()) nominalBeli = "50000";

//         for (BuyRandomAssets asset : assetsTerpilih) {
//             try {
//                 // 1. Reset ke Dashboard
//                 LoginSteps.driver.get("https://cihuy.triv.id/dashboard");

//                 // 2. Klik Icon Beli/Jual
//                 wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("i.icon-buy-sell"))).click();

//                 // 3. Pilih Category
//                 String lower = asset.category.toLowerCase();
//                 String catTab = lower.substring(0, 1).toUpperCase() + lower.substring(1);
//                 String xPathCategory = "//span[contains(@class, 'border-category') and contains(@class, 'nav-link-buy') and normalize-space()='" + catTab + "']";
//                 wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xPathCategory))).click();

//                 // 4. Klik Rectangle Icon
//                 wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("img.Rectangle-Copy-63"))).click();

//                 // 5. Search Aset
//                 var searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("buy_search")));
//                 searchBox.clear();
//                 searchBox.sendKeys(asset.code);
//                 searchBox.sendKeys(Keys.ENTER);

//                 // 6. Klik Aset (FIX: Menggunakan contains(., label) dan JS Click agar lebih stabil)
//                 // XPath ini mencari elemen LI yang di dalamnya mengandung teks label aset, tidak peduli struktur dalamnya
//                 String xPathLabel = "//li[contains(@class, 'currency-option') and contains(., '" + asset.label + "')]";
                
//                 // Tunggu sampai elemen ada di DOM
//                 var assetElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPathLabel)));
                
//                 // Scroll agar elemen terlihat di layar
//                 ((JavascriptExecutor) LoginSteps.driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", assetElement);
                
//                 // Paksa klik menggunakan JavaScript
//                 ((JavascriptExecutor) LoginSteps.driver).executeScript("arguments[0].click();", assetElement);

//                 // 7. Input Nominal
//                 var amountField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("amount_2")));
//                 amountField.clear();
//                 amountField.sendKeys(nominalBeli);

//                 // 8. Klik Lanjut (link_to_buy_3)
//                 wait.until(ExpectedConditions.elementToBeClickable(By.id("link_to_buy_3"))).click();

//                 // 9. Klik Konfirmasi (link_to_buy_4)
//                 wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.link_to_buy_4"))).click();

//                 // 10. Cek Alert Popup Pasar Tutup
//                 boolean isMarketClosed = false;
//                 try {
//                     WebDriverWait alertWait = new WebDriverWait(LoginSteps.driver, Duration.ofSeconds(3));
//                     if (alertWait.until(ExpectedConditions.alertIsPresent()) != null) {
//                         String alertText = LoginSteps.driver.switchTo().alert().getText();
//                         System.out.println("Alert terdeteksi: " + alertText);
//                         LoginSteps.driver.switchTo().alert().accept(); // Klik OK
//                         System.out.println("Melewati aset " + asset.code + " karena pasar tutup.");
//                         isMarketClosed = true;
//                     }
//                 } catch (Exception e) {
//                     // Tidak ada alert, lanjut normal
//                 }

//                 // Jika pasar tutup (isMarketClosed = true), lewati proses di bawah dan lanjut ke aset berikutnya
//                 if (isMarketClosed) {
//                     continue; 
//                 }

//                 // 11. Selesaikan Transaksi (ke Dashboard)
//                 wait.until(ExpectedConditions.elementToBeClickable(By.className("link_to_dashboard_buy"))).click();
                
//                 System.out.println("Berhasil membeli: " + asset.code + " (" + asset.label + ")");

//             } catch (Exception e) {
//                 System.out.println("Gagal memproses aset " + asset.code + ": " + e.getMessage());
//             }
//         }
//     }
// }

