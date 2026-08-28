package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import utils.ConfigReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FuturesMarketWebUiReportWriter {
    private static final DateTimeFormatter RUN_ID = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final Path runDirectory;

    public FuturesMarketWebUiReportWriter() {
        String requested = System.getProperty("futures.ui.run.id", "").trim();
        String runId = requested.isEmpty() ? LocalDateTime.now().format(RUN_ID) : requested;
        String baseDir = System.getProperty("futures.ui.result.dir", "target/futures-market-ui");
        runDirectory = Path.of(baseDir, runId).toAbsolutePath().normalize();
    }

    public Path getRunDirectory() {
        return runDirectory;
    }

    public void write(List<Map<String, Object>> results, int passed, int failed) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("environment", ConfigReader.getEnvironment());
        summary.put("web_base_url", ConfigReader.getWebBaseUrl());
        summary.put("api_base_url", ConfigReader.getApiBaseUrl());
        summary.put("total", results == null ? 0 : results.size());
        summary.put("passed", passed);
        summary.put("failed", failed);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("summary", summary);
        payload.put("results", results == null ? List.of() : results);

        try {
            Files.createDirectories(runDirectory);
            mapper.writeValue(runDirectory.resolve("web-ui-results.json").toFile(), payload);
            mapper.writeValue(runDirectory.resolve("summary.json").toFile(), summary);
        } catch (IOException exception) {
            throw new IllegalStateException("Gagal menulis Futures Web UI JSON ke " + runDirectory, exception);
        }
    }
}
