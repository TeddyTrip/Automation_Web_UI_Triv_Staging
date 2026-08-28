import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/FuturesMarketApi.feature",
        glue = {"steps", "hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/futures-market-api-report.html",
                "json:target/cucumber-reports/futures-market-api-report.json"
        },
        tags = "@FuturesMarketApi",
        monochrome = true
)
public class FuturesMarketApiTestRunner {
}
