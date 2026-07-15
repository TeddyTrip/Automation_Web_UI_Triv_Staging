package src.test.java.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import utils.ConfigReader;

public class DriverManager {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            initializeDriver();
        }
        return driver.get();
    }

    private static void initializeDriver() {
        String browser = ConfigReader.getProperty("browser").toLowerCase();
        boolean isIncognito = Boolean.parseBoolean(ConfigReader.getProperty("incognito"));

        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions ffOptions = new FirefoxOptions();
                if (isIncognito) ffOptions.addArguments("-private");
                driver.set(new FirefoxDriver(ffOptions));
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (isIncognito) edgeOptions.addArguments("--inprivate");
                driver.set(new EdgeDriver(edgeOptions));
                break;

            // Opera support removed (dependency not available). Use chrome as fallback.
            case "opera":
                WebDriverManager.chromedriver().setup();
                ChromeOptions operaFallback = new ChromeOptions();
                if (isIncognito) operaFallback.addArguments("--incognito");
                driver.set(new ChromeDriver(operaFallback));
                break;
            
            case "safari":
                // Safari tidak butuh setup() driver karena sudah terinstall di macOS
                SafariOptions safariOptions = new SafariOptions();
                driver.set(new SafariDriver(safariOptions));
                break;

            case "brave":
                WebDriverManager.chromedriver().setup();
                ChromeOptions braveOptions = new ChromeOptions();
                // Brave membutuhkan path ke executable-nya
                braveOptions.setBinary("C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe");
                if (isIncognito) braveOptions.addArguments("--incognito");
                driver.set(new ChromeDriver(braveOptions));
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chOptions = new ChromeOptions();
                if (isIncognito) chOptions.addArguments("--incognito");
                driver.set(new ChromeDriver(chOptions));
                break;
        }

        getDriver().manage().window().maximize();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}