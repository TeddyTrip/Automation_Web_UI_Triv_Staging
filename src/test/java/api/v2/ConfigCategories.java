package api.v2;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import java.util.List;
import io.restassured.response.Response;


public class ConfigCategories {
    
    public List<String> getCategoriesFromApi() {
        System.out.println("🚀 Sedang melakukan request untuk mengambil daftar categories dari API...");

        // 1. Ambil response sebagai objek Response terlebih dahulu
        Response response = RestAssured
                .given()
                .when()
                .get("https://cihuy.triv.id/api/v2/config/categories"); // ⚠️ Pastikan endpoint ini diganti dengan URL asli yang benar!

        // 2. Cetak response mentah ke console untuk memastikan apakah itu JSON atau HTML error
        String rawResponse = response.getBody().asString();
        System.out.println("📦 Raw Response dari Server: " + rawResponse);

        // 3. Validasi status code (pastikan sukses 200)
        if (response.getStatusCode() != 200) {
            System.out.println("❌ Request gagal dengan Status Code: " + response.getStatusCode());
            return null;
        }

        // 4. Parse ke JSON jika response dipastikan valid
        JsonPath responseJson = response.jsonPath();
        List<String> categoriesList = responseJson.getList("data.category", String.class);

        if (categoriesList != null && !categoriesList.isEmpty()) {
            System.out.println("✅ Berhasil mengambil data categories!");
            System.out.println("📋 Daftar Category: " + categoriesList);
        } else {
            System.out.println("⚠️ Warning: List categories kosong.");
        }

        return categoriesList;
    }
}
