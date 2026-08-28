package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FuturesMarketComparisonReportWriter {
    private static final DateTimeFormatter RUN_ID = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    private final Path runDirectory;

    public FuturesMarketComparisonReportWriter() {
        String requested = System.getProperty("futures.run.id", "").trim();
        String runId = requested.isEmpty() ? LocalDateTime.now().format(RUN_ID) : requested;
        String baseDir = System.getProperty("futures.result.dir", "target/futures-market");
        this.runDirectory = Path.of(baseDir, runId).toAbsolutePath().normalize();
    }

    public Path getRunDirectory() {
        return runDirectory;
    }

    public void writeRaw(String environment, String endpoint, JsonNode raw) {
        writeJson(environment + "-raw.json", raw);
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("environment", environment);
        source.put("endpoint", endpoint);
        writeJson(environment + "-source.json", source);
    }

    public void writeSnapshot(String environment, String endpoint, List<FuturesMarketInfo> markets) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("environment", environment);
        payload.put("endpoint", endpoint);
        payload.put("kind", "futures");
        payload.put("total", markets == null ? 0 : markets.size());
        payload.put("markets", markets == null ? List.of() : markets);
        writeJson(environment + "-futures.json", payload);
    }

    public void writeComparison(
            String productionEndpoint,
            String stagingEndpoint,
            List<FuturesMarketEnvironmentDiff> results
    ) {
        long same = results.stream().filter(r -> "SAME".equals(r.getCompareStatus())).count();
        long changed = results.stream().filter(r -> "CHANGED".equals(r.getCompareStatus())).count();
        long newInStaging = results.stream().filter(r -> "NEW_IN_STAGING".equals(r.getCompareStatus())).count();
        long missingInStaging = results.stream().filter(FuturesMarketEnvironmentDiff::isMissingInStaging).count();
        long updated = results.stream().filter(r -> "v".equals(r.getUpdated())).count();
        long validationFailed = results.stream().filter(r -> "FAIL".equals(r.getValidationStatus())).count();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("total_comparison", results.size());
        summary.put("same", same);
        summary.put("changed", changed);
        summary.put("new_in_staging", newInStaging);
        summary.put("missing_in_staging", missingInStaging);
        summary.put("updated_v", updated);
        summary.put("updated_x", results.size() - updated);
        summary.put("staging_validation_failed", validationFailed);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("production_endpoint", productionEndpoint);
        payload.put("staging_endpoint", stagingEndpoint);
        payload.put("compare_fields", List.of(
                "vmoney_id", "category", "normalized_category", "currency", "kind", "label"
        ));
        payload.put("ignored_volatile_fields", List.of("price", "change"));
        payload.put("summary", summary);
        payload.put("results", results);

        writeJson("comparison.json", payload);
        writeJson("summary.json", summary);
    }

    private void writeJson(String filename, Object data) {
        try {
            Files.createDirectories(runDirectory);
            objectMapper.writeValue(runDirectory.resolve(filename).toFile(), data);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Gagal menulis Futures comparison JSON: " + runDirectory.resolve(filename),
                    exception
            );
        }
    }
}
