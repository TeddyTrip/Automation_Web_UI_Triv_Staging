package pages;

import src.test.java.driver.DriverManager;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BasePage {
    // Semua class Page akan mewarisi ini, jadi tidak perlu deklarasi ulang
    protected WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
}
