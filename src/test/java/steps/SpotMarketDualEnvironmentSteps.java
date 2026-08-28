package steps;

import api.InstallCoinSpotMarkets;
import api.SpotMarketComparisonReportWriter;
import api.SpotMarketDualEnvironmentComparator;
import api.SpotMarketEnvironmentDiff;
import api.SpotMarketSnapshot;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SpotMarketDualEnvironmentSteps {
    private final InstallCoinSpotMarkets api = new InstallCoinSpotMarkets();
    private final SpotMarketDualEnvironmentComparator comparator = new SpotMarketDualEnvironmentComparator();
    private final SpotMarketComparisonReportWriter writer = new SpotMarketComparisonReportWriter();
    private SpotMarketSnapshot production;
    private SpotMarketSnapshot staging;
    private List<SpotMarketEnvironmentDiff> diffs = List.of();
    private Path output;

    @Given("automation mengambil data spot market dari Production dan Staging dalam satu run")
    public void fetchBoth() {
        production = api.fetch("production");
        staging = api.fetch("staging");
    }

    @When("automation memvalidasi data spot market Staging dan membandingkannya dengan Production")
    public void compare() {
        diffs = comparator.compare(production.getMarkets(), staging.getMarkets());
        output = writer.write(production, staging, diffs);
    }

    @Then("hasil comparison spot market harus disimpan sebagai JSON dengan status updated")
    public void verify() {
        long same = diffs.stream().filter(d -> "SAME".equals(d.getStatus())).count();
        long updated = diffs.size() - same;
        List<String> invalid = new ArrayList<>();

        System.out.println();
        System.out.println("========================================");
        System.out.println("SPOT MARKET PROD VS STAGING SUMMARY");
        System.out.println("========================================");
        System.out.println("Production market : " + production.getMarkets().size());
        System.out.println("Staging market    : " + staging.getMarkets().size());
        System.out.println("updated=v         : " + updated + " (difference/update, bukan otomatis FAIL)");
        System.out.println("updated=x         : " + same);
        System.out.println("Output folder     : " + output);

        for (SpotMarketEnvironmentDiff diff : diffs) {
            String prod = diff.getProduction() == null ? "-" : diff.getProduction().getLabel();
            String stg = diff.getStaging() == null ? "-" : diff.getStaging().getLabel();
            System.out.println("[" + diff.getStatus() + "] prod=" + prod + " | staging=" + stg
                    + " | updated=" + diff.getUpdated() + " | validation=" + diff.getValidationStatus());
            if ("FAIL".equals(diff.getValidationStatus())) {
                invalid.add("staging_label=" + stg + ", reason=" + diff.getValidationError());
            }
        }

        Assert.assertTrue("Spot Market staging validation gagal:\n- " + String.join("\n- ", invalid), invalid.isEmpty());
    }
}
