package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.ConfigReader;

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

public class InstallCoinSpotMarkets {
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SpotMarketSnapshot fetch(String environment) {
        String endpoint = ConfigReader.apiUrl(environment, "/install/coin/liverate");
        String raw = request(endpoint);
        JsonNode root;
        try {
            root = objectMapper.readTree(raw);
        } catch (IOException exception) {
            throw new IllegalStateException("Response install coin liverate bukan JSON valid: " + endpoint, exception);
        }

        List<SpotMarketInfo> result = new ArrayList<>();
        collect(root, result);
        if (result.isEmpty()) {
            throw new IllegalStateException("Tidak menemukan markets[] kind=market dari " + endpoint);
        }

        System.out.println("[SPOT MARKET API] environment=" + environment + ", endpoint=" + endpoint);
        System.out.println("[SPOT MARKET API] kind=market, total=" + result.size());
        for (SpotMarketInfo item : result) {
            System.out.println("[SPOT MARKET API] " + item);
        }
        return new SpotMarketSnapshot(environment, endpoint, raw, result);
    }

    private String request(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(TIMEOUT)
                .header("Accept", "application/json")
                .GET();
        addAuthorizationIfAvailable(builder);
        try {
            HttpResponse<String> response = httpClient.send(
                    builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("API install coin liverate gagal. HTTP " + response.statusCode()
                        + ", endpoint=" + endpoint + ", body=" + abbreviate(response.body()));
            }
            if (response.body() == null || response.body().isBlank()) {
                throw new IllegalStateException("Response API install coin liverate kosong: " + endpoint);
            }
            return response.body();
        } catch (IOException exception) {
            throw new IllegalStateException("Gagal hit API install coin liverate: " + endpoint, exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Request API install coin liverate dihentikan.", exception);
        }
    }

    private void collect(JsonNode node, List<SpotMarketInfo> output) {
        if (node == null || node.isNull() || node.isMissingNode()) return;
        if (node.isArray()) {
            for (JsonNode child : node) collect(child, output);
            return;
        }
        if (!node.isObject()) return;

        JsonNode markets = node.get("markets");
        if (markets != null && markets.isArray()) {
            String vmoneyId = firstText(node, "vmoney_id", "v_money_id", "id");
            String originalCategory = firstText(node, "category");
            String normalizedCategory = normalizeCategory(originalCategory);
            String currency = firstText(node, "currency", "code");
            for (JsonNode market : markets) {
                if (market == null || !market.isObject()) continue;
                String kind = firstText(market, "kind");
                if (!"market".equalsIgnoreCase(kind)) continue;
                output.add(new SpotMarketInfo(vmoneyId, originalCategory, normalizedCategory,
                        currency, firstText(market, "label"), kind));
            }
        }

        node.fields().forEachRemaining(entry -> {
            if (!"markets".equals(entry.getKey())) collect(entry.getValue(), output);
        });
    }

    private String normalizeCategory(String category) {
        String value = category == null ? "" : category.trim();
        return "stocks".equalsIgnoreCase(value) ? "stocks" : "crypto";
    }

    private String firstText(JsonNode node, String... names) {
        for (String name : names) {
            JsonNode value = node.get(name);
            if (value == null || value.isNull()) continue;
            String text = value.asText("").trim();
            if (!text.isEmpty()) return text;
        }
        return "";
    }

    private void addAuthorizationIfAvailable(HttpRequest.Builder builder) {
        String token = System.getProperty("triv.bearer.token");
        if (token == null || token.isBlank()) token = System.getenv("TRIV_BEARER_TOKEN");
        if (token == null || token.isBlank()) return;
        String authorization = token.trim();
        if (!authorization.toLowerCase(Locale.ROOT).startsWith("bearer ")) authorization = "Bearer " + authorization;
        builder.header("Authorization", authorization);
    }

    private String abbreviate(String value) {
        if (value == null) return "";
        String clean = value.replaceAll("\\s+", " ").trim();
        return clean.length() <= 500 ? clean : clean.substring(0, 500) + "...";
    }
}
