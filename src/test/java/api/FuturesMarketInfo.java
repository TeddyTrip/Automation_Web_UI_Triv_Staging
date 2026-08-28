package api;

public class FuturesMarketInfo {
    private final String vmoneyId;
    private final String originalCategory;
    private final String normalizedCategory;
    private final String currency;
    private final String label;
    private final String kind;

    public FuturesMarketInfo(
            String vmoneyId,
            String originalCategory,
            String normalizedCategory,
            String currency,
            String label,
            String kind
    ) {
        this.vmoneyId = safe(vmoneyId);
        this.originalCategory = safe(originalCategory);
        this.normalizedCategory = safe(normalizedCategory);
        this.currency = safe(currency);
        this.label = safe(label);
        this.kind = safe(kind);
    }

    public String getVmoneyId() {
        return vmoneyId;
    }

    public String getOriginalCategory() {
        return originalCategory;
    }

    public String getNormalizedCategory() {
        return normalizedCategory;
    }

    public String getCurrency() {
        return currency;
    }

    public String getLabel() {
        return label;
    }

    public String getKind() {
        return kind;
    }

    public String expectedFuturesLabel() {
        return currency.toUpperCase() + "USDT-PERP";
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    public String toString() {
        return "FuturesMarketInfo{" +
                "vmoneyId='" + vmoneyId + '\'' +
                ", originalCategory='" + originalCategory + '\'' +
                ", normalizedCategory='" + normalizedCategory + '\'' +
                ", currency='" + currency + '\'' +
                ", label='" + label + '\'' +
                ", kind='" + kind + '\'' +
                '}';
    }
}
