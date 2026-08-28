package api;

public class SpotMarketEnvironmentDiff {
    private final String status;
    private final SpotMarketInfo production;
    private final SpotMarketInfo staging;
    private final String validationStatus;
    private final String validationError;

    public SpotMarketEnvironmentDiff(String status, SpotMarketInfo production, SpotMarketInfo staging,
                                     String validationStatus, String validationError) {
        this.status = status;
        this.production = production;
        this.staging = staging;
        this.validationStatus = validationStatus;
        this.validationError = validationError == null ? "" : validationError;
    }

    public String getStatus() { return status; }
    public SpotMarketInfo getProduction() { return production; }
    public SpotMarketInfo getStaging() { return staging; }
    public String getValidationStatus() { return validationStatus; }
    public String getValidationError() { return validationError; }
    public String getUpdated() { return "SAME".equals(status) ? "x" : "v"; }
}
