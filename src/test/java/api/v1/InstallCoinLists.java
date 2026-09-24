package api.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstallCoinLists {

        
    private List<Map<String, Object>> allCoinsList;

    public void setAllCoinsList(List<Map<String, Object>> allCoinsList) {
        this.allCoinsList = allCoinsList;
    }
    
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

    public String getMainCategoryFromApi(String code) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new URL("https://cihuy.triv.id/api/v1/install/coin/lists"));
            
            for (JsonNode coin : root) {
                if (coin.has("code") && coin.has("main_category")) {
                    String apiCode = coin.get("code").asText().trim();
                    
                    // Pencocokan presisi (exact match) menggunakan equalsIgnoreCase
                    if (apiCode.equalsIgnoreCase(code != null ? code.trim() : "")) {
                        return coin.get("main_category").asText().trim();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal memanggil API kategori: " + e.getMessage());
        }
        return null; 
    }

    public Map<String, String> getCategoryAndMainCategoryAsset(String code) {
        Map<String, String> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new URL("https://cihuy.triv.id/api/v2/config/categories"));
            
            for (JsonNode coin : root) {
                if (coin.has("code")) {
                    String apiCode = coin.get("code").asText().trim();
                    
                    if (apiCode.equalsIgnoreCase(code != null ? code.trim() : "")) {
                        result.put("mainCategory", coin.has("main_category") ? coin.get("main_category").asText().trim() : null);
                        result.put("category", coin.has("category") ? coin.get("category").asText().trim() : null);
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal memanggil API kategori: " + e.getMessage());
        }
        return null; 
    }

    public Map<String, Object> getCoinDataMapFromApi(String code) {
        if (this.allCoinsList == null || code == null) {
            return null;
        }

        for (Map<String, Object> coinData : this.allCoinsList) {
            // Sesuaikan key dengan struktur API ("currency" atau "code")
            String currentCode = (String) coinData.get("currency"); 
            
            if (currentCode != null && currentCode.equalsIgnoreCase(code)) {
                return coinData; // Mengembalikan map data lengkap untuk koin/jaringan tersebut
            }
        }
        
        return null; // Return null jika tidak ditemukan
    }







    
    // --- Helper Internal untuk Validasi List ---
    private boolean hasAccess(Map<String, Object> coinData, String feature) {
        if (coinData == null || !coinData.containsKey("access")) return false;
        @SuppressWarnings("unchecked")
        List<String> accessList = (List<String>) coinData.get("access");
        return accessList != null && accessList.contains(feature.toLowerCase());
    }

    private boolean canDo(Map<String, Object> coinData, String action) {
        if (coinData == null || !coinData.containsKey("coin_can")) return false;
        @SuppressWarnings("unchecked")
        List<String> coinCanList = (List<String>) coinData.get("coin_can");
        return coinCanList != null && coinCanList.contains(action.toLowerCase());
    }

    // --- Pengecekan Kategori "access" ---
    
    public boolean canBuyAccess(Map<String, Object> coinData) {
        return hasAccess(coinData, "buy");
    }

    public boolean canSellAccess(Map<String, Object> coinData) {
        return hasAccess(coinData, "sell");
    }

    public boolean canLiveRate(Map<String, Object> coinData) {
        return hasAccess(coinData, "liverate");
    }

    public boolean canSwap(Map<String, Object> coinData) {
        return hasAccess(coinData, "swap");
    }

    public boolean canReceive(Map<String, Object> coinData) {
        return hasAccess(coinData, "receive");
    }

    public boolean canSend(Map<String, Object> coinData) {
        return hasAccess(coinData, "send");
    }

    public boolean canLoan(Map<String, Object> coinData) {
        return hasAccess(coinData, "loan");
    }

    public boolean canGift(Map<String, Object> coinData) {
        return hasAccess(coinData, "gift");
    }

    // --- Pengecekan Kategori "coin_can" ---

    public boolean canBuyAction(Map<String, Object> coinData) {
        return canDo(coinData, "buy");
    }

    public boolean canSellAction(Map<String, Object> coinData) {
        return canDo(coinData, "sell");
    }

    // --- Pengecekan Market & Atribut Lainnya ---

    public boolean isValidMarketService(Map<String, Object> coinData) {
        if (coinData == null) return false;
        
        // Jika key tidak ada atau bernilai null, anggap valid sesuai ketentuan
        if (!coinData.containsKey("market_service") || coinData.get("market_service") == null) {
            return true; 
        }
        
        Object service = coinData.get("market_service");
        String serviceStr = service.toString().trim().toLowerCase();
        
        // Daftar market service yang diizinkan
        return serviceStr.equals("binance") || 
            serviceStr.equals("bybit") || 
            serviceStr.equals("bitget") || 
            serviceStr.equals("mexc");
    }

    public boolean isMarketIdr(Map<String, Object> coinData) {
        if (coinData == null || !coinData.containsKey("market_idr")) return false;
        Object marketIdr = coinData.get("market_idr");
        return marketIdr instanceof Boolean && (Boolean) marketIdr;
    }
}
