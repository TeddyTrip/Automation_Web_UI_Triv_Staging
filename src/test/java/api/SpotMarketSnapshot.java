package api;

import java.util.List;

public class SpotMarketSnapshot {
    private final String environment;
    private final String endpoint;
    private final String rawJson;
    private final List<SpotMarketInfo> markets;

    public SpotMarketSnapshot(String environment, String endpoint, String rawJson, List<SpotMarketInfo> markets) {
        this.environment = environment;
        this.endpoint = endpoint;
        this.rawJson = rawJson;
        this.markets = List.copyOf(markets);
    }

    public String getEnvironment() { return environment; }
    public String getEndpoint() { return endpoint; }
    public String getRawJson() { return rawJson; }
    public List<SpotMarketInfo> getMarkets() { return markets; }
}
