package api;

import java.util.Locale;
import java.util.Set;

public class SpotMarketInfo {
    private static final Set<String> UI_CATEGORIES = Set.of("crypto", "stocks", "oil");
    private static final Set<String> UI_QUOTES = Set.of("IDR", "USDT", "BTC", "ETH");

    private final String vmoneyId;
    private final String originalCategory;
    private final String normalizedCategory;
    private final String currency;
    private final String label;
    private final String kind;

    public SpotMarketInfo(String vmoneyId, String originalCategory, String normalizedCategory,
                          String currency, String label, String kind) {
        this.vmoneyId = safe(vmoneyId);
        this.originalCategory = safe(originalCategory);
        this.normalizedCategory = safe(normalizedCategory);
        this.currency = safe(currency);
        this.label = safe(label);
        this.kind = safe(kind);
    }

    public String getVmoneyId() { return vmoneyId; }
    public String getOriginalCategory() { return originalCategory; }
    public String getNormalizedCategory() { return normalizedCategory; }
    public String getCurrency() { return currency; }
    public String getLabel() { return label; }
    public String getKind() { return kind; }

    public String getBase() {
        int slash = label.indexOf('/');
        return slash > 0 ? label.substring(0, slash).trim().toUpperCase(Locale.ROOT) : "";
    }

    public String getQuote() {
        int slash = label.indexOf('/');
        return slash >= 0 && slash < label.length() - 1
                ? label.substring(slash + 1).trim().toUpperCase(Locale.ROOT)
                : "";
    }

    public String routeSymbol() {
        return label.trim().toUpperCase(Locale.ROOT).replace('/', '_');
    }

    /**
     * UI tabs only expose Crypto / Stocks / Oil / Favourite / All.
     * Favourite is user-specific and is not an API category, therefore any
     * other API category (gold, usd, euro, etc.) is validated through All.
     */
    public String uiCategory() {
        String category = originalCategory.toLowerCase(Locale.ROOT);
        return UI_CATEGORIES.contains(category) ? category : "all";
    }

    public boolean hasSupportedUiQuote() {
        return UI_QUOTES.contains(getQuote());
    }

    public boolean isQuote(String quote) {
        return quote != null && getQuote().equalsIgnoreCase(quote.trim());
    }

    public boolean isStructurallyValid() {
        return "market".equalsIgnoreCase(kind)
                && !currency.isBlank()
                && !label.isBlank()
                && !getBase().isBlank()
                && !getQuote().isBlank()
                && getBase().equalsIgnoreCase(currency);
    }

    public String validationError() {
        if (!"market".equalsIgnoreCase(kind)) return "kind harus market";
        if (currency.isBlank()) return "currency kosong";
        if (label.isBlank()) return "label kosong";
        if (getBase().isBlank() || getQuote().isBlank()) return "format label harus BASE/QUOTE";
        if (!getBase().equalsIgnoreCase(currency)) {
            return "base label '" + getBase() + "' tidak sama dengan currency '" + currency + "'";
        }
        return "";
    }

    public String comparisonKey() {
        return vmoneyId + "|" + getQuote();
    }

    private static String safe(String value) { return value == null ? "" : value.trim(); }

    @Override
    public String toString() {
        return "SpotMarketInfo{" +
                "vmoneyId='" + vmoneyId + '\'' +
                ", originalCategory='" + originalCategory + '\'' +
                ", normalizedCategory='" + normalizedCategory + '\'' +
                ", currency='" + currency + '\'' +
                ", label='" + label + '\'' +
                ", kind='" + kind + '\'' +
                '}';
    }
}
