package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import src.test.java.driver.DriverManager;
import utils.ConfigReader;
import utils.ScreenRecorderUtil;

public class Hooks {

    private static final String API_ONLY_TAG = "@ApiOnly";

    @Before
    public void setup(Scenario scenario) {
        if (isApiOnly(scenario)) {
            System.out.println("[HOOK] API-only scenario detected. Skip browser + screen recorder: " + scenario.getName());
            return;
        }

        String scenarioName = scenario.getName().replaceAll("[^a-zA-Z0-9]", "_");

        try {
            ScreenRecorderUtil.startRecord(scenarioName);
            System.out.println("🎥 [RECORDING STARTED] Skenario: " + scenario.getName());
        } catch (Exception e) {
            System.out.println("[WARN] Screen recorder gagal start: " + e.getMessage());
        }

        DriverManager.getDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        if (isApiOnly(scenario)) {
            System.out.println("[HOOK] API-only scenario finished. No browser/recorder cleanup required: " + scenario.getName());
            return;
        }

        // Keep reading report config for compatibility with existing functional automation.
        String orientation = ConfigReader.getProperty("report.orientation");
        boolean isLandscape = orientation != null && orientation.equalsIgnoreCase("landscape");
        if (isLandscape) {
            // Existing PDF generation remains disabled; variable retained intentionally.
        }

        try {
            ScreenRecorderUtil.stopRecord();
            System.out.println("💾 [RECORDING STOPPED & SAVED] Skenario: " + scenario.getName());
        } catch (Exception e) {
            System.out.println("[WARN] Screen recorder gagal stop: " + e.getMessage());
        }

        DriverManager.quitDriver();
    }

    private boolean isApiOnly(Scenario scenario) {
        return scenario.getSourceTagNames().contains(API_ONLY_TAG);
    }
}
