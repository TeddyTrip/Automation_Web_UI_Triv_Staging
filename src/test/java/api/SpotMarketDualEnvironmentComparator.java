package api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpotMarketDualEnvironmentComparator {

    public List<SpotMarketEnvironmentDiff> compare(List<SpotMarketInfo> production, List<SpotMarketInfo> staging) {
        Map<String, SpotMarketInfo> prodMap = index(production);
        Map<String, SpotMarketInfo> stagingMap = index(staging);
        Set<String> keys = new LinkedHashSet<>();
        keys.addAll(prodMap.keySet());
        keys.addAll(stagingMap.keySet());

        List<SpotMarketEnvironmentDiff> result = new ArrayList<>();
        for (String key : keys) {
            SpotMarketInfo prod = prodMap.get(key);
            SpotMarketInfo stg = stagingMap.get(key);
            String status;
            if (prod == null) status = "NEW_IN_STAGING";
            else if (stg == null) status = "MISSING_IN_STAGING";
            else if (sameStructural(prod, stg)) status = "SAME";
            else status = "CHANGED";

            String validationStatus = stg == null ? "NOT_APPLICABLE" : (stg.isStructurallyValid() ? "PASS" : "FAIL");
            String validationError = stg == null ? "" : stg.validationError();
            result.add(new SpotMarketEnvironmentDiff(status, prod, stg, validationStatus, validationError));
        }
        return List.copyOf(result);
    }

    private Map<String, SpotMarketInfo> index(List<SpotMarketInfo> markets) {
        Map<String, SpotMarketInfo> map = new LinkedHashMap<>();
        for (SpotMarketInfo item : markets) {
            String key = item.comparisonKey();
            if (map.putIfAbsent(key, item) != null) {
                throw new IllegalStateException("Duplicate spot market comparison key: " + key);
            }
        }
        return map;
    }

    private boolean sameStructural(SpotMarketInfo a, SpotMarketInfo b) {
        return equalsIgnoreCase(a.getVmoneyId(), b.getVmoneyId())
                && equalsIgnoreCase(a.getOriginalCategory(), b.getOriginalCategory())
                && equalsIgnoreCase(a.getNormalizedCategory(), b.getNormalizedCategory())
                && equalsIgnoreCase(a.getCurrency(), b.getCurrency())
                && equalsIgnoreCase(a.getLabel(), b.getLabel())
                && equalsIgnoreCase(a.getKind(), b.getKind());
    }

    private boolean equalsIgnoreCase(String a, String b) {
        String aa = a == null ? "" : a.trim();
        String bb = b == null ? "" : b.trim();
        return aa.equalsIgnoreCase(bb);
    }
}
