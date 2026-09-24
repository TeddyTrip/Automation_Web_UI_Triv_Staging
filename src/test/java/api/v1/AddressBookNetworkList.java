package api.v1;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import helper.ProtocolInfo;
import helper.SelectedAssetAndProtocol;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

import io.restassured.response.Response;


public class AddressBookNetworkList {

    // Variabel penampung data list API jaringan/network
    private List<Map<String, Object>> networkDataList;

    public void setNetworkDataList(List<Map<String, Object>> networkDataList) {
        this.networkDataList = networkDataList;
    }

    /**
     * Mengambil data network list langsung dari API endpoint.
     */
    public void fetchNetworkDataFromApi() {
        try {
            System.out.println("🌐 Mengambil data network list dari API...");
            Response response = RestAssured.given()
                    .header("Accept", "application/json")
                    .get("https://cihuy.triv.id/api/v1/user/address-book/network/list");

            if (response.getStatusCode() == 200) {
                this.networkDataList = response.as(new TypeRef<List<Map<String, Object>>>() {});
                System.out.println("✅ Berhasil memuat " + this.networkDataList.size() + " data network dari API.");
            } else {
                System.out.println("❌ Gagal mengambil data API. Status Code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Terjadi exception saat memanggil API: " + e.getMessage());
        }
    }

    // Helper internal untuk memastikan data selalu ada sebelum diproses
    private void ensureFetchNetworkDataFromApiDataLoaded() {
        if (this.networkDataList == null || this.networkDataList.isEmpty()) {
            fetchNetworkDataFromApi();
        }
    }

    /**
     * Mengambil satu protokol secara random untuk currency tertentu yang mendukung fitur withdraw.
     * @param currencyCode Kode mata uang, contoh: "BTC", "ETH", "USDT"
     * @return Objek ProtocolInfo yang dipilih secara random, atau null jika tidak ditemukan.
     */
    public ProtocolInfo getRandomProtocolForCurrency(String currencyCode) {
        ensureFetchNetworkDataFromApiDataLoaded();
        
        List<ProtocolInfo> protocolList = new ArrayList<>();
        
        if (this.networkDataList == null || currencyCode == null || currencyCode.trim().isEmpty()) {
            return null;
        }

        // 1. Kumpulkan semua protokol yang valid dan support withdraw untuk currency tersebut
        for (Map<String, Object> item : this.networkDataList) {
            String currency = (String) item.get("currency");
            
            if (currency != null && currency.equalsIgnoreCase(currencyCode.trim())) {
                @SuppressWarnings("unchecked")
                List<String> enabledFeatures = (List<String>) item.get("enabled_features");
                
                // Lewati protokol jika fitur "withdraw" (send) tidak tersedia
                if (enabledFeatures == null || !enabledFeatures.contains("withdraw")) {
                    continue;
                }

                String protocol = (String) item.get("protocol");
                
                boolean isOptionalMemoId = false;
                if (item.containsKey("is_optional_memo_id") && item.get("is_optional_memo_id") != null) {
                    isOptionalMemoId = (Boolean) item.get("is_optional_memo_id");
                }
                
                protocolList.add(new ProtocolInfo(protocol, isOptionalMemoId));
            }
        }
        
        // 2. Jika tidak ada protokol sama sekali
        if (protocolList.isEmpty()) {
            System.out.println("⚠️ Tidak ada protokol yang mendukung withdraw untuk currency: " + currencyCode);
            return null;
        }

        // 3. Jika ada lebih dari 1 protokol, pilih secara random. Jika cuma 1, langsung ambil index ke-0.
        Random random = new Random();
        ProtocolInfo selectedProtocol = protocolList.get(random.nextInt(protocolList.size()));
        
        System.out.println("🎲 Berhasil memilih random protokol untuk " + currencyCode + ": " + 
                        (selectedProtocol.getProtocol() != null ? selectedProtocol.getProtocol() : "NATIVE") + 
                        " (Dari total " + protocolList.size() + " pilihan protokol)");

        return selectedProtocol;
    }

    public double getMinWithdrawWithProtocol(String currencyCode, String protocolName) {
        ensureFetchNetworkDataFromApiDataLoaded();
        
        if (this.networkDataList == null || currencyCode == null || protocolName == null) {
            return 0.0;
        }

        for (Map<String, Object> item : this.networkDataList) {
            String currency = (String) item.get("currency");
            String protocol = (String) item.get("protocol");
            
            if (currency != null && currency.equalsIgnoreCase(currencyCode) &&
                protocol != null && protocol.equalsIgnoreCase(protocolName)) {
                
                Object minWithdrawObj = item.get("min_withdraw");
                if (minWithdrawObj != null && minWithdrawObj instanceof Number) {
                    return ((Number) minWithdrawObj).doubleValue();
                }
            }
        }
        return 0.0;
    }

    public double getMinerFeeCoin(String currencyCode, String protocolName) {
        ensureFetchNetworkDataFromApiDataLoaded();

        if (this.networkDataList == null || currencyCode == null) return 0.0;

        for (Map<String, Object> item : this.networkDataList) {
            String currency = (String) item.get("currency");
            String protocol = (String) item.get("protocol");
            
            // Penanganan pencocokan protokol (termasuk jika null / native)
            boolean isProtocolMatch = (protocolName == null || protocolName.trim().isEmpty()) 
                ? (protocol == null || protocol.equalsIgnoreCase("NATIVE") || protocol.isEmpty()) 
                : protocolName.equalsIgnoreCase(protocol);

            if (currency != null && currency.equalsIgnoreCase(currencyCode.trim()) && isProtocolMatch) {
                @SuppressWarnings("unchecked")
                Map<String, Object> minerFeeMap = (Map<String, Object>) item.get("miner_fee");
                
                if (minerFeeMap != null && minerFeeMap.containsKey("coin")) {
                    Object coinObj = minerFeeMap.get("coin");
                    if (coinObj instanceof Number) {
                        return ((Number) coinObj).doubleValue();
                    }
                }
            }
        }
        return 0.0;
    }


    // Helper untuk membaca nilai count dari config.properties secara otomatis
    private int getAssetCountFromConfig() {
        Properties prop = new Properties();
        int defaultCount = 10; // Nilai default jika config tidak ditemukan
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("⚠️ File config.properties tidak ditemukan di resources, menggunakan default count: " + defaultCount);
                return defaultCount;
            }
            prop.load(input);
            String countStr = prop.getProperty("random.asset.count");
            if (countStr != null && !countStr.trim().isEmpty()) {
                return Integer.parseInt(countStr.trim());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Gagal membaca 'random.asset.count' dari config.properties: " + e.getMessage() + ". Menggunakan default: " + defaultCount);
        }
        return defaultCount;
    }

    public List<SelectedAssetAndProtocol> getRandomAssetsAndProtocols() {
        List<SelectedAssetAndProtocol> selectedList = new ArrayList<>();
        if (this.networkDataList == null || this.networkDataList.isEmpty()) {
            return selectedList;
        }

        // Ambil jumlah asset yang ingin dirandom langsung dari config.properties
        int count = getAssetCountFromConfig();

        // 1. Group data valid yang memiliki fitur "withdraw" berdasarkan currency-nya
        Map<String, List<Map<String, Object>>> currencyToItems = new HashMap<>();
        for (Map<String, Object> item : this.networkDataList) {
            @SuppressWarnings("unchecked")
            List<String> enabledFeatures = (List<String>) item.get("enabled_features");
            if (enabledFeatures != null && enabledFeatures.contains("withdraw")) {
                String currency = (String) item.get("currency");
                currencyToItems.computeIfAbsent(currency, k -> new ArrayList<>()).add(item);
            }
        }

        if (currencyToItems.isEmpty()) {
            return selectedList;
        }

        // 2. Ambil daftar semua currency yang unik lalu acak urutannya
        List<String> availableCurrencies = new ArrayList<>(currencyToItems.keySet());
        Collections.shuffle(availableCurrencies);

        Random random = new Random();
        int targetCount = Math.min(count, availableCurrencies.size());

        // 3. Iterasi sebanyak 'count' dari config untuk mengambil asset unik beserta random protokolnya
        for (int i = 0; i < targetCount; i++) {
            String currency = availableCurrencies.get(i);
            List<Map<String, Object>> protocolItems = currencyToItems.get(currency);

            // Random pilih salah satu protokol yang tersedia untuk asset tersebut
            Map<String, Object> selectedItem = protocolItems.get(random.nextInt(protocolItems.size()));
            String protocol = (String) selectedItem.get("protocol");

            // Cek status is_optional_memo_id
            boolean isOptionalMemoId = false;
            if (selectedItem.containsKey("is_optional_memo_id") && selectedItem.get("is_optional_memo_id") != null) {
                isOptionalMemoId = (Boolean) selectedItem.get("is_optional_memo_id");
            }

            // Jika true, random keputusan apakah mau pakai memo atau tidak (true/false)
            boolean useMemoId = isOptionalMemoId && random.nextBoolean();

            selectedList.add(new SelectedAssetAndProtocol(currency, protocol, isOptionalMemoId, useMemoId));
        }

        return selectedList;
    }

    public boolean isCurrencyAvailableForWithdraw(String code) {
        if (code == null || code.trim().isEmpty()) {
            System.out.println("⚠️ Code currency tidak boleh kosong.");
            return false;
        }

        if (this.networkDataList == null || this.networkDataList.isEmpty()) {
            System.out.println("⚠️ Data network list dari API masih kosong.");
            return false;
        }

        // Looping untuk mencari apakah ada item dari currency tersebut yang support withdraw
        for (Map<String, Object> item : this.networkDataList) {
            String currency = (String) item.get("currency");
            
            if (currency != null && currency.equalsIgnoreCase(code.trim())) {
                @SuppressWarnings("unchecked")
                List<String> enabledFeatures = (List<String>) item.get("enabled_features");
                
                if (enabledFeatures != null && enabledFeatures.contains("withdraw")) {
                    System.out.println("✅ Aset " + code + " tersedia dan support untuk withdraw.");
                    return true;
                }
            }
        }

        System.out.println("❌ Aset " + code + " tidak ditemukan atau tidak mendukung fitur withdraw.");
        return false;
    }
}


