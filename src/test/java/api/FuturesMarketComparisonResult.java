package api;

public class FuturesMarketComparisonResult {
    private final FuturesMarketInfo market;
    private final String expectedLabel;
    private final boolean passed;
    private final String message;

    public FuturesMarketComparisonResult(
            FuturesMarketInfo market,
            String expectedLabel,
            boolean passed,
            String message
    ) {
        this.market = market;
        this.expectedLabel = expectedLabel;
        this.passed = passed;
        this.message = message;
    }

    public FuturesMarketInfo getMarket() {
        return market;
    }

    public String getExpectedLabel() {
        return expectedLabel;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getMessage() {
        return message;
    }
}
