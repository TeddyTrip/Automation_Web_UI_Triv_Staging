package api;

import java.net.URI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;


public class UserWallet {
    
    public static String getWalletNameByAssetCode(String targetCode) throws Exception {
        String url = "https://cihuy.triv.id/api/v1/wallet?q=" + targetCode;
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        // Loop melalui semua hasil yang diberikan API
        for (JsonNode node : root) {
            // Ambil code dari JSON
            String codeFromApi = node.get("code").asText();
            
            // Pengecekan ketat: Apakah codeFromApi SAMA PERSIS dengan targetCode?
            // Kita gunakan equalsIgnoreCase agar "doge" tetap dianggap sama dengan "DOGE"
            if (codeFromApi.equalsIgnoreCase(targetCode)) {
                return node.get("name").asText(); 
            }
        }
        
        // Jika loop selesai dan tidak ketemu, lempar error agar tes berhenti
        throw new RuntimeException("Data untuk kode '" + targetCode + "' tidak ditemukan secara spesifik di API!");
    }
}
