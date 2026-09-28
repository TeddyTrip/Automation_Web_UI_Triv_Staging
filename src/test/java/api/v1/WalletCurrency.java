package api.v1;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class WalletCurrency {


    public String getWalletNameFromApi(String code) {
        System.out.println("🚀 Sedang mengambil data wallet untuk currency/code: " + code);
        
        try {
            // Gabungkan base URL dengan parameter code
            String endpoint = "https://cihuy.triv.id/api/v1/wallet/" + code;
            
            Response response = RestAssured
                    .given()
                    .when()
                    .get(endpoint);

            // Cetak response mentah untuk kebutuhan debugging jika diperlukan
            System.out.println("📦 Response API Wallet: " + response.getBody().asString());

            // Validasi status code sukses (200)
            if (response.getStatusCode() == 200) {
                JsonPath jsonPath = response.jsonPath();
                String walletName = jsonPath.getString("name");
                
                System.out.println("✅ Berhasil mendapatkan wallet name: " + walletName);
                return walletName;
            } else {
                System.out.println("⚠️ Gagal mengambil wallet. Status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("❌ Terjadi error saat memanggil API wallet: " + e.getMessage());
        }
        
        return null;
    }
}
