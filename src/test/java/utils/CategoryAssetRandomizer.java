package utils;

import java.util.*;

public class CategoryAssetRandomizer {

    public static Map<String, List<Map<String, Object>>> getRandomPerCategory(
            List<Map<String, Object>> allData, int n, String categoryKey) {
        
        if (allData == null || allData.isEmpty()) {
            throw new IllegalArgumentException("Data dari API kosong atau null!");
        }

        // Kelompokkan data berdasarkan kategorinya masing-masing dengan pengecualian spesifik
        Map<String, List<Map<String, Object>>> groupedByCategory = new HashMap<>();
        for (Map<String, Object> item : allData) {
            String code = String.valueOf(item.get("code"));
            String vMoney = String.valueOf(item.get("v_money"));
            String category = String.valueOf(item.get(categoryKey));
            
            // Pengecualian spesifik: Lewati jika code PAYPAL, v_money Paypal, dan category usd
            boolean isPaypalUsd = "PAYPAL".equalsIgnoreCase(code) 
                    && "Paypal".equalsIgnoreCase(vMoney) 
                    && "usd".equalsIgnoreCase(category);

            if (isPaypalUsd) {
                continue; // Abaikan aset ini agar tidak ikut di-random
            }

            groupedByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(item);
        }

        // Tentukan urutan kategori yang diinginkan
        List<String> preferredOrder = Arrays.asList("crypto", "stocks", "usd", "oil", "gold", "euro");
        
        // Gunakan LinkedHashMap agar urutan penyimpanannya sesuai dengan preferredOrder
        Map<String, List<Map<String, Object>>> randomizedResult = new LinkedHashMap<>();

        // 1. Masukkan kategori sesuai urutan prioritas yang diminta
        for (String prefCat : preferredOrder) {
            for (String actualCat : groupedByCategory.keySet()) {
                if (actualCat.equalsIgnoreCase(prefCat)) {
                    List<Map<String, Object>> list = groupedByCategory.get(actualCat);

                    Collections.shuffle(list);
                    int countToTake = Math.min(n, list.size());
                    List<Map<String, Object>> selectedItems = new ArrayList<>(list.subList(0, countToTake));
                    
                    randomizedResult.put(actualCat, selectedItems);
                }
            }
        }

        // 2. Masukkan sisa kategori lain (jika ada kategori di luar list di atas)
        for (Map.Entry<String, List<Map<String, Object>>> entry : groupedByCategory.entrySet()) {
            String category = entry.getKey();
            boolean existsInPreferred = preferredOrder.stream().anyMatch(p -> p.equalsIgnoreCase(category));
            
            if (!existsInPreferred) {
                List<Map<String, Object>> list = entry.getValue();

                Collections.shuffle(list);
                int countToTake = Math.min(n, list.size());
                List<Map<String, Object>> selectedItems = new ArrayList<>(list.subList(0, countToTake));
                
                randomizedResult.put(category, selectedItems);
            }
        }

        return randomizedResult;
    }

    public static void printSummaryReport(Map<String, List<Map<String, Object>>> randomAssets) {
        int totalAssets = 0;
        List<String> allAssetCodes = new ArrayList<>();

        System.out.println("\n=============================================");
        System.out.println("      LAPORAN RANDOM ASSET TERPILIH");
        System.out.println("=============================================");

        for (Map.Entry<String, List<Map<String, Object>>> entry : randomAssets.entrySet()) {
            String category = entry.getKey();
            List<Map<String, Object>> assets = entry.getValue();

            System.out.println("Kategori: [" + category.toUpperCase() + "]");
            for (Map<String, Object> asset : assets) {
                String label = String.valueOf(asset.get("label"));
                String code = String.valueOf(asset.get("code"));
                
                // Menampilkan format: Pax Gold - PAXG
                System.out.println("   - " + label + " - " + code);
                
                allAssetCodes.add(code);
                totalAssets++;
            }
        }

        System.out.println("\n[DAFTAR SEMUA ASSET (CODE)]: " + String.join(", ", allAssetCodes));
        System.out.println("TOTAL ASSET DI-RANDOM: " + totalAssets);
        System.out.println("=============================================\n");
    }
}