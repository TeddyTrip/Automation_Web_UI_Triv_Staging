package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;

public class InstallCoinLists {
    
    public String getAssetIDFromApi(String code) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new URL("https://cihuy.triv.id/api/v1/install/coin/lists"));
            
            for (JsonNode coin : root) {
                if (coin.get("code").asText().equalsIgnoreCase(code)) {
                    return String.valueOf(coin.get("id").asInt()); // Mengembalikan "Bitcoin", "Ethereum", dll
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal memanggil API: " + e.getMessage());
        }
        return null; // Atau handle error jika tidak ketemu
    }
    
    public String getLabelFromApi(String code) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new URL("https://cihuy.triv.id/api/v1/install/coin/lists"));
            
            for (JsonNode coin : root) {
                if (coin.get("code").asText().equalsIgnoreCase(code)) {
                    return coin.get("label").asText(); // Mengembalikan "Bitcoin", "Ethereum", dll
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal memanggil API: " + e.getMessage());
        }
        return null; // Atau handle error jika tidak ketemu
    }

    public String getV_MoneyFromApi(String code) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new URL("https://cihuy.triv.id/api/v1/install/coin/lists"));
            
            for (JsonNode coin : root) {
                // Pastikan node code dan v_money tersedia untuk menghindari NullPointerException
                if (coin.has("code") && coin.has("v_money")) {
                    String apiCode = coin.get("code").asText().trim();
                    
                    // Menggunakan equalsIgnoreCase untuk exact match secara utuh.
                    // Ini memastikan "BTC" dan "BTCO" tidak akan saling tertukar.
                    if (apiCode.equalsIgnoreCase(code != null ? code.trim() : "")) {
                        return coin.get("v_money").asText().trim();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal memanggil API: " + e.getMessage());
        }
        return null; // Return null jika data tidak ditemukan atau terjadi error
    }

    public double getBuyPriceFromApi(String code) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new URL("https://cihuy.triv.id/api/v1/install/coin/lists"));
            
            for (JsonNode coin : root) {
                if (coin.get("code").asText().equalsIgnoreCase(code)) {
                    return coin.get("buy").asDouble(); 
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal memanggil API: " + e.getMessage());
        }
        return 0.0; // Atau handle error jika tidak ketemu
    }

    public double getSellPriceFromApi(String code) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new URL("https://cihuy.triv.id/api/v1/install/coin/lists"));
            
            for (JsonNode coin : root) {
                if (coin.get("code").asText().equalsIgnoreCase(code)) {
                    return coin.get("sell").asDouble(); 
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal memanggil API: " + e.getMessage());
        }
        return 0.0; // Atau handle error jika tidak ketemu
    }

    public String getCategoryFromApi(String code) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new URL("https://cihuy.triv.id/api/v1/install/coin/lists"));
            
            for (JsonNode coin : root) {
                if (coin.has("code") && coin.has("category")) {
                    String apiCode = coin.get("code").asText().trim();
                    
                    // Pencocokan presisi (exact match) menggunakan equalsIgnoreCase
                    if (apiCode.equalsIgnoreCase(code != null ? code.trim() : "")) {
                        return coin.get("category").asText().trim();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal memanggil API kategori: " + e.getMessage());
        }
        return null; 
    }

    
}
