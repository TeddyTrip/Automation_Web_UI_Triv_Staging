package api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FuturesMarketApiComparator {

    public List<FuturesMarketComparisonResult> compare(
            List<FuturesMarketInfo> markets
    ) {
        if (markets == null || markets.isEmpty()) {
            throw new IllegalArgumentException("Daftar futures market tidak boleh kosong.");
        }

        List<FuturesMarketComparisonResult> results = new ArrayList<>();
        Set<String> seenLabels = new HashSet<>();

        for (FuturesMarketInfo market : markets) {
            String expected = market.expectedFuturesLabel();
            String actual = market.getLabel();

            List<String> errors = new ArrayList<>();

            if (!"futures".equalsIgnoreCase(market.getKind())) {
                errors.add("kind bukan futures: " + market.getKind());
            }
            if (market.getCurrency().isBlank()) {
                errors.add("currency parent kosong");
            }
            if (actual.isBlank()) {
                errors.add("label futures kosong");
            }
            if (!actual.isBlank() && !actual.equalsIgnoreCase(expected)) {
                errors.add("expected label=" + expected + ", actual=" + actual);
            }

            if (!actual.isBlank()) {
                String normalized = actual.toUpperCase(Locale.ROOT);
                if (!seenLabels.add(normalized)) {
                    errors.add("duplicate label futures=" + actual);
                }
            }

            boolean passed = errors.isEmpty();
            results.add(new FuturesMarketComparisonResult(
                    market,
                    expected,
                    passed,
                    passed ? "MATCH" : String.join("; ", errors)
            ));
        }

        return List.copyOf(results);
    }
}
