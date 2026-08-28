package api;

import java.util.List;

public class FuturesMarketEnvironmentDiff {
    private final String vmoneyId;
    private final String currency;
    private final String category;
    private final String normalizedCategory;
    private final String kind;
    private final String expectedLabel;
    private final String productionLabel;
    private final String stagingLabel;
    private final String validationStatus;
    private final String compareStatus;
    private final String updated;
    private final List<String> changes;

    public FuturesMarketEnvironmentDiff(
            String vmoneyId,
            String currency,
            String category,
            String normalizedCategory,
            String kind,
            String expectedLabel,
            String productionLabel,
            String stagingLabel,
            String validationStatus,
            String compareStatus,
            String updated,
            List<String> changes
    ) {
        this.vmoneyId = safe(vmoneyId);
        this.currency = safe(currency);
        this.category = safe(category);
        this.normalizedCategory = safe(normalizedCategory);
        this.kind = safe(kind);
        this.expectedLabel = safe(expectedLabel);
        this.productionLabel = safe(productionLabel);
        this.stagingLabel = safe(stagingLabel);
        this.validationStatus = safe(validationStatus);
        this.compareStatus = safe(compareStatus);
        this.updated = safe(updated);
        this.changes = changes == null ? List.of() : List.copyOf(changes);
    }

    public String getVmoneyId() { return vmoneyId; }
    public String getCurrency() { return currency; }
    public String getCategory() { return category; }
    public String getNormalizedCategory() { return normalizedCategory; }
    public String getKind() { return kind; }
    public String getExpectedLabel() { return expectedLabel; }
    public String getProductionLabel() { return productionLabel; }
    public String getStagingLabel() { return stagingLabel; }
    public String getValidationStatus() { return validationStatus; }
    public String getCompareStatus() { return compareStatus; }
    public String getUpdated() { return updated; }
    public List<String> getChanges() { return changes; }

    public boolean isStagingValid() {
        return "PASS".equals(validationStatus) || "NOT_APPLICABLE".equals(validationStatus);
    }

    public boolean isMissingInStaging() {
        return "MISSING_IN_STAGING".equals(compareStatus);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
