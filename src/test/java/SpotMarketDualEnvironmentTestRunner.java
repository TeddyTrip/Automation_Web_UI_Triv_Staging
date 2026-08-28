import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/SpotMarketDualEnvironment.feature",
        glue = {"steps", "hooks"},
        plugin = {"pretty", "html:target/cucumber-reports/spot-market-compare.html", "json:target/cucumber-reports/spot-market-compare.json"},
        tags = "@SpotMarketDualEnv",
        monochrome = true
)
public class SpotMarketDualEnvironmentTestRunner {
}
