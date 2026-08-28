package steps;

import api.InstallCoinSpotMarkets;
import api.SpotMarketInfo;
import api.SpotMarketSnapshot;
import api.SpotMarketWebUiReportWriter;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import pages.market.SpotMarketPage;
import src.test.java.driver.DriverManager;
import utils.ConfigReader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpotMarketWebUiSteps {
    private final InstallCoinSpotMarkets api = new InstallCoinSpotMarkets();
    private final SpotMarketPage page = new SpotMarketPage(DriverManager.getDriver());
    private final SpotMarketWebUiReportWriter writer = new SpotMarketWebUiReportWriter();
    private final List<String> failures = new ArrayList<>();
    private final List<Map<String, Object>> results = new ArrayList<>();
    private List<SpotMarketInfo> markets = List.of();
    private int passed;

    @Given("user membuka halaman Market default sesuai environment aktif")
    public void openMarket() {
        ConfigReader.printActiveEnvironment();
        page.openDefaultPage();
    }

    @Given("automation mengambil seluruh label spot market supported pair dari API install coin liverate")
    public void loadMarkets() {
        SpotMarketSnapshot snapshot = api.fetch(ConfigReader.getEnvironment());
        markets = snapshot.getMarkets().stream()
                .filter(SpotMarketInfo::hasSupportedUiQuote)
                .sorted(Comparator.comparing(SpotMarketInfo::getQuote)
                        .thenComparing(SpotMarketInfo::getLabel))
                .toList();
        if (markets.isEmpty()) {
            throw new IllegalStateException("Tidak ada spot market dengan quote UI IDR/USDT/BTC/ETH.");
        }
        System.out.println("[SPOT UI] Total supported labels=" + markets.size());
        System.out.println("[SPOT UI] UI category=all (fixed for all Spot Market validation)");
        System.out.println("[SPOT UI] Search format=<BASE><QUOTE> without slash, e.g. AR/BTC -> ARBTC");
    }

    @When("user memilih pair pada category All lalu mencari setiap label spot market")
    public void searchAll() {
        String activePair = null;

        for (SpotMarketInfo market : markets) {
            boolean found = false;
            String error = "";
            String pair = market.getQuote();
            String searchKeyword = toSearchKeyword(market.getLabel());

            try {
                if (!pair.equals(activePair)) {
                    if (activePair != null) page.clearSearch();
                    page.selectMarketContext("all", pair);
                    activePair = pair;
                }

                System.out.println("[SPOT UI] Search label=" + market.getLabel()
                        + " | keyword=" + searchKeyword);

                // Do not move to the next asset until this exact DOM symbol is visible
                // (or the configured maximum timeout is reached).
                found = page.searchAndWaitExactPair(searchKeyword, market.routeSymbol());

                if (found) {
                    passed++;
                    System.out.println("[SPOT UI][PASS] category=all"
                            + " | pair=" + pair
                            + " | label=" + market.getLabel()
                            + " | keyword=" + searchKeyword
                            + " | symbol=" + market.routeSymbol());
                } else {
                    error = "Exact pair tidak ditemukan pada category All / pair aktif";
                    failures.add(market.getLabel() + " => " + error);
                    System.out.println("[SPOT UI][FAIL] category=all"
                            + " | pair=" + pair
                            + " | label=" + market.getLabel()
                            + " | keyword=" + searchKeyword);
                }
            } catch (RuntimeException exception) {
                error = exception.getClass().getSimpleName() + ": " + exception.getMessage();
                failures.add(market.getLabel() + " => " + error);
                System.out.println("[SPOT UI][ERROR] category=all"
                        + " | pair=" + pair
                        + " | label=" + market.getLabel()
                        + " | keyword=" + searchKeyword
                        + " | reason=" + error);
            }

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("vmoney_id", market.getVmoneyId());
            row.put("original_category", market.getOriginalCategory());
            row.put("normalized_category", market.getNormalizedCategory());
            row.put("source_ui_category", market.uiCategory());
            row.put("ui_category", "all");
            row.put("currency", market.getCurrency());
            row.put("quote", pair);
            row.put("kind", market.getKind());
            row.put("label", market.getLabel());
            row.put("search_keyword", searchKeyword);
            row.put("route_symbol", market.routeSymbol());
            row.put("found", found);
            row.put("status", found ? "PASS" : "FAIL");
            row.put("error", error);
            results.add(row);
        }

        page.clearSearch();
        writer.write(results, passed, failures.size(), "IDR,USDT,BTC,ETH");
    }

    @When("user memilih category dan pair lalu mencari setiap label spot market")
    public void searchAllLegacyAlias() {
        searchAll();
    }

    private String toSearchKeyword(String label) {
        if (label == null) return "";
        return label.replace("/", "").replaceAll("\\s+", "").trim();
    }

    @Then("seluruh label spot market harus tampil sebagai exact pair pada category All dan pair yang sesuai")
    public void verify() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("SPOT MARKET WEB UI SUMMARY");
        System.out.println("========================================");
        System.out.println("Environment : " + ConfigReader.getEnvironment());
        System.out.println("Pairs       : IDR, USDT, BTC, ETH");
        System.out.println("Total       : " + markets.size());
        System.out.println("Passed      : " + passed);
        System.out.println("Failed      : " + failures.size());
        System.out.println("Output      : " + writer.getRunDirectory());
        Assert.assertTrue("Spot Market Web UI validation gagal:\n- " + String.join("\n- ", failures), failures.isEmpty());
    }
}
