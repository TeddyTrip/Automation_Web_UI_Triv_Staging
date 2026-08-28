import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/FuturesMarketDualEnvironment.feature",
        glue = {"steps", "hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/futures-market-dual-env-report.html",
                "json:target/cucumber-reports/futures-market-dual-env-report.json"
        },
        tags = "@FuturesMarketDualEnv",
        monochrome = true
)
public class FuturesMarketDualEnvironmentTestRunner {
}
