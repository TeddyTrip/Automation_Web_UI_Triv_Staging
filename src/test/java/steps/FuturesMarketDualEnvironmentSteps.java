package steps;

import api.FuturesMarketApiComparator;
import api.FuturesMarketComparisonReportWriter;
import api.FuturesMarketComparisonResult;
import api.FuturesMarketDualEnvironmentComparator;
import api.FuturesMarketEnvironmentDiff;
import api.FuturesMarketInfo;
import api.InstallCoinLiverateMarkets;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import utils.ConfigReader;

import java.util.ArrayList;
import java.util.List;

public class FuturesMarketDualEnvironmentSteps {

    private final String productionEndpoint = ConfigReader.apiUrl(
            "production", "/install/coin/liverate"
    );
    private final String stagingEndpoint = ConfigReader.apiUrl(
            "staging", "/install/coin/liverate"
    );

    private final InstallCoinLiverateMarkets productionClient =
            new InstallCoinLiverateMarkets(productionEndpoint);
    private final InstallCoinLiverateMarkets stagingClient =
            new InstallCoinLiverateMarkets(stagingEndpoint);
    private final FuturesMarketApiComparator stagingValidator = new FuturesMarketApiComparator();
    private final FuturesMarketDualEnvironmentComparator environmentComparator =
            new FuturesMarketDualEnvironmentComparator();
    private final FuturesMarketComparisonReportWriter writer =
            new FuturesMarketComparisonReportWriter();

    private JsonNode productionRaw;
    private JsonNode stagingRaw;
    private List<FuturesMarketInfo> productionMarkets = List.of();
    private List<FuturesMarketInfo> stagingMarkets = List.of();
    private List<FuturesMarketComparisonResult> stagingValidation = List.of();
    private List<FuturesMarketEnvironmentDiff> comparison = List.of();

    @Given("automation mengambil data market futures dari Production dan Staging dalam satu run")
    public void fetchBothEnvironments() {
        System.out.println("[FUTURES DUAL] Production=" + productionEndpoint);
        productionRaw = productionClient.fetchRawJson();
        productionMarkets = productionClient.extractMarketsByKind(productionRaw, "futures");

        System.out.println("[FUTURES DUAL] Staging=" + stagingEndpoint);
        stagingRaw = stagingClient.fetchRawJson();
        stagingMarkets = stagingClient.extractMarketsByKind(stagingRaw, "futures");

        Assert.assertFalse(
                "Staging wajib memiliki kind=futures untuk feature yang sedang divalidasi.",
                stagingMarkets.isEmpty()
        );

        writer.writeRaw("production", productionEndpoint, productionRaw);
        writer.writeRaw("staging", stagingEndpoint, stagingRaw);
        writer.writeSnapshot("production", productionEndpoint, productionMarkets);
        writer.writeSnapshot("staging", stagingEndpoint, stagingMarkets);
    }

    @When("automation memvalidasi data futures Staging dan membandingkannya dengan Production")
    public void validateAndCompare() {
        stagingValidation = stagingValidator.compare(stagingMarkets);
        comparison = environmentComparator.compare(productionMarkets, stagingMarkets);
        writer.writeComparison(productionEndpoint, stagingEndpoint, comparison);
    }

    @Then("hasil comparison futures harus disimpan sebagai JSON dengan status updated")
    public void verifyAndPrint() {
        List<String> failures = new ArrayList<>();

        for (FuturesMarketComparisonResult result : stagingValidation) {
            if (!result.isPassed()) {
                failures.add(
                        "STAGING_INVALID currency=" + result.getMarket().getCurrency()
                                + ", expected=" + result.getExpectedLabel()
                                + ", actual=" + result.getMarket().getLabel()
                                + ", reason=" + result.getMessage()
                );
            }
        }

        // Prod vs Staging differences are informational update markers, not automatic failures.
        // A scenario fails only when the Staging data itself violates the Futures validation rule.

        long same = comparison.stream().filter(r -> "x".equals(r.getUpdated())).count();
        long updated = comparison.size() - same;

        System.out.println();
        System.out.println("========================================");
        System.out.println("FUTURES PROD VS STAGING SUMMARY");
        System.out.println("========================================");
        System.out.println("Production futures : " + productionMarkets.size());
        System.out.println("Staging futures    : " + stagingMarkets.size());
        System.out.println("updated=v          : " + updated + " (difference/update, bukan otomatis FAIL)");
        System.out.println("updated=x          : " + same);
        System.out.println("Output folder      : " + writer.getRunDirectory());

        for (FuturesMarketEnvironmentDiff diff : comparison) {
            System.out.println(
                    "[" + diff.getCompareStatus() + "] "
                            + "currency=" + diff.getCurrency()
                            + " | prod=" + blankAsDash(diff.getProductionLabel())
                            + " | staging=" + blankAsDash(diff.getStagingLabel())
                            + " | updated=" + diff.getUpdated()
                            + " | validation=" + diff.getValidationStatus()
            );
        }

        Assert.assertTrue(
                "Futures Staging validation gagal:\n- " + String.join("\n- ", failures),
                failures.isEmpty()
        );
    }

    private String blankAsDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
