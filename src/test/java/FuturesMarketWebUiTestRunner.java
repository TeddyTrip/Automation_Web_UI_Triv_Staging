import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/FuturesMarketWebUi.feature",
        glue = {"steps", "hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/futures-market-ui.html",
                "json:target/cucumber-reports/futures-market-ui.json"
        },
        tags = "@FuturesMarketWebUi",
        monochrome = true
)
public class FuturesMarketWebUiTestRunner {
}
