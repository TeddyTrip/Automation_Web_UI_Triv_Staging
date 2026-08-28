package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import utils.ConfigReader;

public class InstallCoinLiverateMarkets {

    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final String endpoint;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public InstallCoinLiverateMarkets() {
        this(System.getProperty(
                "install.coin.liverate.endpoint",
                ConfigReader.apiUrl("/install/coin/liverate")
        ));
    }

    public InstallCoinLiverateMarkets(String endpoint) {
        if (endpoint == null || endpoint.trim().isEmpty()) {
            throw new IllegalArgumentException("endpoint install coin liverate tidak boleh kosong.");
        }
        this.endpoint = endpoint.trim();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<FuturesMarketInfo> getMarketsByKind(String expectedKind) {
        String kind = requireText(expectedKind, "expected kind");
        List<FuturesMarketInfo> result = findMarketsByKind(kind);

        if (result.isEmpty()) {
            throw new IllegalStateException(
                    "Tidak menemukan markets[] dengan kind='" + kind + "' dari " + endpoint
            );
        }
        return result;
    }

    /**
     * Non-strict variant for environment comparison. Production may legitimately
     * have zero futures entries while a feature is still only available in Staging.
     */
    public List<FuturesMarketInfo> findMarketsByKind(String expectedKind) {
        String kind = requireText(expectedKind, "expected kind");
        return extractMarketsByKind(fetchRawJson(), kind);
    }

    public JsonNode fetchRawJson() {
        return requestJson();
    }

    public List<FuturesMarketInfo> extractMarketsByKind(JsonNode root, String expectedKind) {
        String kind = requireText(expectedKind, "expected kind");
        List<FuturesMarketInfo> result = new ArrayList<>();
        collectMarkets(root, kind, result);

        System.out.println("[FUTURES MARKET API] endpoint=" + endpoint);
        System.out.println("[FUTURES MARKET API] kind=" + kind + ", total=" + result.size());
        for (FuturesMarketInfo item : result) {
            System.out.println("[FUTURES MARKET API] " + item);
        }
        return List.copyOf(result);
    }

    public String getEndpoint() {
        return endpoint;
    }

    private JsonNode requestJson() {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(TIMEOUT)
                .header("Accept", "application/json")
                .GET();

        addAuthorizationIfAvailable(builder);

        HttpResponse<String> response;
        try {
            response = httpClient.send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Gagal hit API install coin liverate: " + endpoint,
                    exception
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Request API install coin liverate dihentikan.",
                    exception
            );
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(
                    "API install coin liverate gagal. HTTP "
                            + response.statusCode()
                            + ", endpoint="
                            + endpoint
                            + ", body="
                            + abbreviate(response.body())
            );
        }

        String body = response.body();
        if (body == null || body.isBlank()) {
            throw new IllegalStateException("Response API install coin liverate kosong.");
        }

        try {
            return objectMapper.readTree(body);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Response API install coin liverate bukan JSON valid.",
                    exception
            );
        }
    }

    private void collectMarkets(
            JsonNode node,
            String expectedKind,
            List<FuturesMarketInfo> output
    ) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return;
        }

        if (node.isArray()) {
            for (JsonNode child : node) {
                collectMarkets(child, expectedKind, output);
            }
            return;
        }

        if (!node.isObject()) {
            return;
        }

        JsonNode markets = node.get("markets");
        if (markets != null && markets.isArray()) {
            String vmoneyId = firstText(node, "vmoney_id", "v_money_id", "id");
            String originalCategory = firstText(node, "category");
            String normalizedCategory = normalizeCategory(originalCategory);
            String currency = firstText(node, "currency", "code");

            for (JsonNode market : markets) {
                if (market == null || !market.isObject()) {
                    continue;
                }

                String kind = firstText(market, "kind");
                if (!expectedKind.equalsIgnoreCase(kind)) {
                    continue;
                }

                output.add(new FuturesMarketInfo(
                        vmoneyId,
                        originalCategory,
                        normalizedCategory,
                        currency,
                        firstText(market, "label"),
                        kind
                ));
            }
        }

        node.fields().forEachRemaining(entry -> {
            if (!"markets".equals(entry.getKey())) {
                collectMarkets(entry.getValue(), expectedKind, output);
            }
        });
    }

    private String normalizeCategory(String category) {
        String value = category == null ? "" : category.trim();
        return "stocks".equalsIgnoreCase(value) ? "stocks" : "crypto";
    }

    private String firstText(JsonNode node, String... names) {
        for (String name : names) {
            JsonNode value = node.get(name);
            if (value == null || value.isNull()) {
                continue;
            }
            String text = value.asText("").trim();
            if (!text.isEmpty()) {
                return text;
            }
        }
        return "";
    }

    private void addAuthorizationIfAvailable(HttpRequest.Builder builder) {
        String token = System.getProperty("triv.bearer.token");
        if (token == null || token.isBlank()) {
            token = System.getenv("TRIV_BEARER_TOKEN");
        }
        if (token == null || token.isBlank()) {
            return;
        }

        String authorization = token.trim();
        if (!authorization.toLowerCase(Locale.ROOT).startsWith("bearer ")) {
            authorization = "Bearer " + authorization;
        }
        builder.header("Authorization", authorization);
    }

    private String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " tidak boleh kosong.");
        }
        return value.trim();
    }

    private String abbreviate(String value) {
        if (value == null) {
            return "";
        }
        String clean = value.replaceAll("\\s+", " ").trim();
        return clean.length() <= 500 ? clean : clean.substring(0, 500) + "...";
    }
}
