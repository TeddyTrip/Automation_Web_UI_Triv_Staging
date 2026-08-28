package steps;

import api.FuturesMarketInfo;
import api.FuturesMarketWebUiReportWriter;
import api.InstallCoinLiverateMarkets;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import pages.futures.FuturesMarketPage;
import src.test.java.driver.DriverManager;
import utils.ConfigReader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FuturesMarketWebUiSteps {

    private final InstallCoinLiverateMarkets liverateMarkets = new InstallCoinLiverateMarkets();
    private final FuturesMarketPage futuresPage = new FuturesMarketPage(DriverManager.getDriver());
    private final FuturesMarketWebUiReportWriter reportWriter = new FuturesMarketWebUiReportWriter();

    private List<FuturesMarketInfo> futuresMarkets = new ArrayList<>();
    private final List<String> uiFailures = new ArrayList<>();
    private final List<Map<String, Object>> uiResults = new ArrayList<>();
    private int passed;

    @Given("user membuka halaman Futures default BTCUSDT-PERP sesuai environment aktif")
    public void openDefaultFuturesPage() {
        ConfigReader.printActiveEnvironment();
        futuresPage.openDefaultPage();
        Assert.assertTrue(
                "Halaman Futures tidak terbuka pada environment yang dipilih. URL=" + futuresPage.currentUrl(),
                futuresPage.currentUrl().startsWith(ConfigReader.getWebBaseUrl())
        );
        System.out.println("[FUTURES UI] Search locator=" + futuresPage.getSearchBarLocatorForDebug());
        System.out.println("[FUTURES UI] Result container=" + futuresPage.getResultsContainerLocatorForDebug());
    }

    @Given("automation mengambil seluruh label market dengan kind futures dari API install coin liverate")
    public void loadFuturesLabelsFromApi() {
        futuresMarkets = liverateMarkets.getMarketsByKind("futures");
        Assert.assertFalse("Tidak ada market kind=futures dari API.", futuresMarkets.isEmpty());
        System.out.println("[FUTURES UI] API source=" + ConfigReader.getApiBaseUrl());
        System.out.println("[FUTURES UI] Total futures labels=" + futuresMarkets.size());
    }

    @When("user mencari setiap label futures pada search bar Futures")
    public void searchAllFuturesLabels() {
        passed = 0;
        uiFailures.clear();
        uiResults.clear();

        for (FuturesMarketInfo market : futuresMarkets) {
            String label = market.getLabel();
            boolean found = false;
            String error = "";
            try {
                futuresPage.clearSearch();
                futuresPage.searchExactLabel(label);
                found = futuresPage.isExactPairVisible(label);

                if (found) {
                    passed++;
                    System.out.println("[FUTURES UI][PASS] currency=" + market.getCurrency() + " | label=" + label);
                } else {
                    error = "exact pair tidak terlihat di result container setelah search";
                    uiFailures.add("currency=" + market.getCurrency() + ", label=" + label + ", reason=" + error);
                    System.out.println("[FUTURES UI][FAIL] label=" + label);
                }
            } catch (RuntimeException exception) {
                error = exception.getClass().getSimpleName() + ": " + exception.getMessage();
                uiFailures.add("currency=" + market.getCurrency() + ", label=" + label + ", reason=" + error);
            }

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("vmoney_id", market.getVmoneyId());
            row.put("currency", market.getCurrency());
            row.put("kind", market.getKind());
            row.put("label", label);
            row.put("found", found);
            row.put("status", found ? "PASS" : "FAIL");
            row.put("error", error);
            uiResults.add(row);
        }

        futuresPage.clearSearch();
        reportWriter.write(uiResults, passed, uiFailures.size());
    }

    @Then("seluruh label futures harus tampil sebagai exact pair pada hasil pencarian")
    public void verifyAllFuturesPairs() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("FUTURES MARKET WEB UI SUMMARY");
        System.out.println("========================================");
        System.out.println("Environment : " + ConfigReader.getEnvironment());
        System.out.println("Total       : " + futuresMarkets.size());
        System.out.println("Passed      : " + passed);
        System.out.println("Failed      : " + uiFailures.size());
        System.out.println("Output      : " + reportWriter.getRunDirectory());

        Assert.assertTrue(
                "Futures Web UI validation gagal:\n- " + String.join("\n- ", uiFailures),
                uiFailures.isEmpty()
        );
    }
}
