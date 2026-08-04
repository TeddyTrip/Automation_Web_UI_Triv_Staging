package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;

public class InstallCoinDetails {
    
    public double getMinimalSellFromApi(String code) {
        String assetID = new InstallCoinLists().getAssetIDFromApi(code);
        System.out.println("Asset ID for " + code + ": " + assetID);
        
        if (assetID == null) {
            System.out.println("Asset ID tidak ditemukan untuk kode: " + code);
            return 0.0;
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new URL("https://cihuy.triv.id/api/v1/install/coin/details?coin_id=" + assetID));
            
            // Karena response berupa JSON Object tunggal, 
            // langsung cek dan ambil field "min_sell" tanpa looping 'for'.
            if (root != null && root.has("min_sell")) {
                return root.get("min_sell").asDouble();
            }
            
        } catch (Exception e) {
            System.out.println("Gagal memanggil API: " + e.getMessage());
        }
        
        return 0.0; // Return 0.0 jika gagal
    }
}
