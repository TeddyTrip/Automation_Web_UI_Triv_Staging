package steps;

import api.FuturesMarketApiComparator;
import api.FuturesMarketComparisonResult;
import api.FuturesMarketInfo;
import api.InstallCoinLiverateMarkets;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class FuturesMarketApiSteps {

    private final InstallCoinLiverateMarkets liverateMarkets =
            new InstallCoinLiverateMarkets();

    private final FuturesMarketApiComparator comparator =
            new FuturesMarketApiComparator();

    private List<FuturesMarketInfo> futuresMarkets = new ArrayList<>();
    private List<FuturesMarketComparisonResult> comparisonResults = new ArrayList<>();

    @Given("automation mengambil seluruh market dengan kind futures dari API install coin liverate")
    public void getFuturesMarkets() {
        futuresMarkets = liverateMarkets.getMarketsByKind("futures");
        Assert.assertFalse(
                "API tidak menghasilkan market kind=futures.",
                futuresMarkets.isEmpty()
        );
    }

    @When("automation membandingkan label futures dengan currency parent")
    public void compareFuturesLabels() {
        comparisonResults = comparator.compare(futuresMarkets);
    }

    @Then("seluruh market futures harus memiliki label sesuai format currency USDT-PERP")
    public void validateAllFuturesMarkets() {
        Assert.assertFalse(
                "Hasil comparison futures kosong.",
                comparisonResults.isEmpty()
        );

        List<String> failures = new ArrayList<>();
        for (FuturesMarketComparisonResult result : comparisonResults) {
            if (!result.isPassed()) {
                FuturesMarketInfo market = result.getMarket();
                failures.add(
                        "currency=" + market.getCurrency()
                                + ", vmoney_id=" + market.getVmoneyId()
                                + ", category=" + market.getOriginalCategory()
                                + ", normalized_category=" + market.getNormalizedCategory()
                                + ", expected=" + result.getExpectedLabel()
                                + ", actual=" + market.getLabel()
                                + ", reason=" + result.getMessage()
                );
            }
        }

        Assert.assertTrue(
                "Futures market API comparison gagal:\n- " + String.join("\n- ", failures),
                failures.isEmpty()
        );
    }

    @Then("automation menampilkan summary market futures dari API")
    public void printSummary() {
        long passed = comparisonResults.stream()
                .filter(FuturesMarketComparisonResult::isPassed)
                .count();
        long failed = comparisonResults.size() - passed;

        System.out.println();
        System.out.println("========================================");
        System.out.println("FUTURES MARKET API COMPARISON SUMMARY");
        System.out.println("========================================");
        System.out.println("Total  : " + comparisonResults.size());
        System.out.println("Passed : " + passed);
        System.out.println("Failed : " + failed);

        for (FuturesMarketComparisonResult result : comparisonResults) {
            FuturesMarketInfo market = result.getMarket();
            System.out.println(
                    (result.isPassed() ? "[PASS] " : "[FAIL] ")
                            + "vmoney_id=" + market.getVmoneyId()
                            + " | category=" + market.getOriginalCategory()
                            + " -> " + market.getNormalizedCategory()
                            + " | currency=" + market.getCurrency()
                            + " | kind=" + market.getKind()
                            + " | expected=" + result.getExpectedLabel()
                            + " | actual=" + market.getLabel()
                            + " | " + result.getMessage()
            );
        }
    }
}
