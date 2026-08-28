package api;

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

public class SpotMarketWebUiReportWriter {
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final Path runDirectory = Path.of("target", "spot-market-ui",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))).toAbsolutePath();

    public void write(List<Map<String, Object>> results, int passed, int failed, String quote) {
        try {
            Files.createDirectories(runDirectory);
            mapper.writeValue(runDirectory.resolve("web-ui-results.json").toFile(), results);
            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("environment", utils.ConfigReader.getEnvironment());
            summary.put("quote", quote);
            summary.put("total", results.size());
            summary.put("passed", passed);
            summary.put("failed", failed);
            summary.put("output_folder", runDirectory.toString());
            mapper.writeValue(runDirectory.resolve("summary.json").toFile(), summary);
        } catch (IOException exception) {
            throw new IllegalStateException("Gagal menulis Spot Market UI report.", exception);
        }
    }

    public Path getRunDirectory() { return runDirectory; }
}
