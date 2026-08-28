import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/SpotMarketWebUi.feature",
        glue = {"steps", "hooks"},
        plugin = {"pretty", "html:target/cucumber-reports/spot-market-ui.html", "json:target/cucumber-reports/spot-market-ui.json"},
        tags = "@SpotMarketWebUi",
        monochrome = true
)
public class SpotMarketWebUiTestRunner {
}
