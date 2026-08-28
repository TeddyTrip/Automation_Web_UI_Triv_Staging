package api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Compares structural Futures metadata only. Volatile fields such as price and
 * change are intentionally excluded so updated=v means an actual market/config change.
 */
public class FuturesMarketDualEnvironmentComparator {

    public List<FuturesMarketEnvironmentDiff> compare(
            List<FuturesMarketInfo> production,
            List<FuturesMarketInfo> staging
    ) {
        List<FuturesMarketInfo> prod = production == null ? List.of() : production;
        List<FuturesMarketInfo> stg = staging == null ? List.of() : staging;

        Map<String, FuturesMarketInfo> prodMap = index(prod);
        Map<String, FuturesMarketInfo> stgMap = index(stg);
        List<FuturesMarketEnvironmentDiff> results = new ArrayList<>();

        for (Map.Entry<String, FuturesMarketInfo> entry : stgMap.entrySet()) {
            FuturesMarketInfo stagingItem = entry.getValue();
            FuturesMarketInfo productionItem = prodMap.remove(entry.getKey());
            results.add(compareStagingItem(productionItem, stagingItem));
        }

        // Anything still in Production is marked as a structural difference (updated=v).
        // It is NOT an automatic test failure; only invalid Staging market data fails validation.
        for (FuturesMarketInfo productionItem : prodMap.values()) {
            results.add(new FuturesMarketEnvironmentDiff(
                    productionItem.getVmoneyId(),
                    productionItem.getCurrency(),
                    productionItem.getOriginalCategory(),
                    productionItem.getNormalizedCategory(),
                    productionItem.getKind(),
                    productionItem.expectedFuturesLabel(),
                    productionItem.getLabel(),
                    "",
                    "NOT_APPLICABLE",
                    "MISSING_IN_STAGING",
                    "v",
                    List.of("Production futures market tidak ditemukan di Staging")
            ));
        }

        return List.copyOf(results);
    }

    private FuturesMarketEnvironmentDiff compareStagingItem(
            FuturesMarketInfo production,
            FuturesMarketInfo staging
    ) {
        List<String> changes = new ArrayList<>();
        String expected = staging.expectedFuturesLabel();
        boolean stagingValid = "futures".equalsIgnoreCase(staging.getKind())
                && !staging.getCurrency().isBlank()
                && !staging.getLabel().isBlank()
                && staging.getLabel().equalsIgnoreCase(expected);

        String compareStatus;
        if (production == null) {
            compareStatus = "NEW_IN_STAGING";
            changes.add("Futures market baru di Staging");
        } else {
            compareField("category", production.getOriginalCategory(), staging.getOriginalCategory(), changes);
            compareField("normalized_category", production.getNormalizedCategory(), staging.getNormalizedCategory(), changes);
            compareField("currency", production.getCurrency(), staging.getCurrency(), changes);
            compareField("kind", production.getKind(), staging.getKind(), changes);
            compareField("label", production.getLabel(), staging.getLabel(), changes);
            compareStatus = changes.isEmpty() ? "SAME" : "CHANGED";
        }

        return new FuturesMarketEnvironmentDiff(
                staging.getVmoneyId(),
                staging.getCurrency(),
                staging.getOriginalCategory(),
                staging.getNormalizedCategory(),
                staging.getKind(),
                expected,
                production == null ? "" : production.getLabel(),
                staging.getLabel(),
                stagingValid ? "PASS" : "FAIL",
                compareStatus,
                "SAME".equals(compareStatus) ? "x" : "v",
                changes
        );
    }

    private Map<String, FuturesMarketInfo> index(List<FuturesMarketInfo> items) {
        Map<String, FuturesMarketInfo> result = new LinkedHashMap<>();
        for (FuturesMarketInfo item : items) {
            String key = identityKey(item);
            if (result.putIfAbsent(key, item) != null) {
                throw new IllegalStateException(
                        "Duplicate Futures identity saat compare environment: " + key
                );
            }
        }
        return result;
    }

    private String identityKey(FuturesMarketInfo item) {
        String vmoney = normalize(item.getVmoneyId());
        String currency = normalize(item.getCurrency());
        return (!vmoney.isEmpty() ? "vmoney:" + vmoney : "currency:" + currency)
                + "|kind:" + normalize(item.getKind());
    }

    private void compareField(String name, String production, String staging, List<String> changes) {
        if (!normalize(production).equals(normalize(staging))) {
            changes.add(name + ": prod='" + safe(production) + "' -> staging='" + safe(staging) + "'");
        }
    }

    private String normalize(String value) {
        return safe(value).toUpperCase(Locale.ROOT);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
