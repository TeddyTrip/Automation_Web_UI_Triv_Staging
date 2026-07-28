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
                if (coin.get("code").asText().equalsIgnoreCase(code)) {
                    return coin.get("v_money").asText(); 
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal memanggil API: " + e.getMessage());
        }
        return null; // Atau handle error jika tidak ketemu
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

    
}
