import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features", // Lokasi file .feature Anda
    glue = {"steps", "hooks"},                        // Lokasi folder steps Anda
    plugin = {
        "pretty",
        "html:target/cucumber-reports/report.html",
        "json:target/cucumber-reports/report.json"
    },
    tags = 
    "@BuyFlowCSVWithCertainAmount", // Hanya jalankan scenario dengan tag ini
    monochrome = true
)
public class TestRunner {
    // Kosongkan saja, ini hanya sebagai pemicu eksekusi
}