package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpotMarketComparisonReportWriter {
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final Path runDirectory;

    public SpotMarketComparisonReportWriter() {
        String runId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        runDirectory = Path.of("target", "spot-market", runId).toAbsolutePath();
    }

    public Path write(SpotMarketSnapshot production, SpotMarketSnapshot staging,
                      List<SpotMarketEnvironmentDiff> diffs) {
        try {
            Files.createDirectories(runDirectory);
            Files.writeString(runDirectory.resolve("production-raw.json"), production.getRawJson(), StandardCharsets.UTF_8);
            Files.writeString(runDirectory.resolve("staging-raw.json"), staging.getRawJson(), StandardCharsets.UTF_8);
            mapper.writeValue(runDirectory.resolve("production-market.json").toFile(), snapshotMap(production));
            mapper.writeValue(runDirectory.resolve("staging-market.json").toFile(), snapshotMap(staging));

            long same = diffs.stream().filter(d -> "SAME".equals(d.getStatus())).count();
            long updated = diffs.size() - same;
            long invalid = diffs.stream().filter(d -> "FAIL".equals(d.getValidationStatus())).count();

            List<Map<String, Object>> rows = new ArrayList<>();
            for (SpotMarketEnvironmentDiff diff : diffs) rows.add(diffMap(diff));

            Map<String, Object> comparison = new LinkedHashMap<>();
            comparison.put("production_endpoint", production.getEndpoint());
            comparison.put("staging_endpoint", staging.getEndpoint());
            comparison.put("compare_fields", List.of("vmoney_id", "category", "normalized_category", "currency", "kind", "label"));
            comparison.put("ignored_volatile_fields", List.of("price", "change"));
            comparison.put("results", rows);
            mapper.writeValue(runDirectory.resolve("comparison.json").toFile(), comparison);

            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("production_market", production.getMarkets().size());
            summary.put("staging_market", staging.getMarkets().size());
            summary.put("updated_v", updated);
            summary.put("updated_x", same);
            summary.put("staging_validation_failed", invalid);
            summary.put("output_folder", runDirectory.toString());
            mapper.writeValue(runDirectory.resolve("summary.json").toFile(), summary);
            return runDirectory;
        } catch (IOException exception) {
            throw new IllegalStateException("Gagal menulis Spot Market JSON report.", exception);
        }
    }

    private Map<String, Object> snapshotMap(SpotMarketSnapshot snapshot) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("environment", snapshot.getEnvironment());
        map.put("endpoint", snapshot.getEndpoint());
        map.put("kind", "market");
        map.put("total", snapshot.getMarkets().size());
        map.put("markets", snapshot.getMarkets());
        return map;
    }

    private Map<String, Object> diffMap(SpotMarketEnvironmentDiff diff) {
        SpotMarketInfo prod = diff.getProduction();
        SpotMarketInfo stg = diff.getStaging();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("compareStatus", diff.getStatus());
        row.put("updated", diff.getUpdated());
        row.put("validationStatus", diff.getValidationStatus());
        row.put("validationError", diff.getValidationError());
        row.put("vmoney_id", value(stg != null ? stg.getVmoneyId() : (prod != null ? prod.getVmoneyId() : "")));
        row.put("currency", value(stg != null ? stg.getCurrency() : (prod != null ? prod.getCurrency() : "")));
        row.put("quote", value(stg != null ? stg.getQuote() : (prod != null ? prod.getQuote() : "")));
        row.put("productionLabel", prod == null ? "" : prod.getLabel());
        row.put("stagingLabel", stg == null ? "" : stg.getLabel());
        return row;
    }

    private String value(String value) { return value == null ? "" : value; }

    public Path getRunDirectory() { return runDirectory; }
}
