package pages.futures;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

import java.time.Duration;
import java.util.Locale;

/**
 * Futures Market page object using stable selectors confirmed from Chrome DevTools.
 *
 * v1.3.1:
 * - avoid native click on the off-screen search input
 * - disable smooth scrolling and perform one instant positioning only
 * - set/clear search value via JS + dispatch input/keyup/change events
 * - re-resolve result container while polling to avoid stale references
 */
public class FuturesMarketPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;
    private boolean searchViewportPrepared;

    public FuturesMarketPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.js = (JavascriptExecutor) driver;
    }

    public void openDefaultPage() {
        openSymbol(ConfigReader.requireProperty("futures.default.symbol"));
    }

    public void openSymbol(String symbol) {
        String cleanSymbol = requireText(symbol, "futures symbol");
        String url = ConfigReader.webUrl("/id/futures/" + cleanSymbol);
        System.out.println("[FUTURES UI] Open URL=" + url);
        driver.get(url);
        searchViewportPrepared = false;
        waitUntilMarketListReady();
    }

    public void waitUntilMarketListReady() {
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBarLocator()));
        wait.until(ExpectedConditions.presenceOfElementLocated(resultsContainerLocator()));
        prepareSearchViewportOnce(search);
    }

    public void searchExactLabel(String label) {
        String cleanLabel = requireText(label, "futures label");
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBarLocator()));
        prepareSearchViewportOnce(search);
        setSearchValue(search, cleanLabel);
        wait.until(d -> cleanLabel.equals(searchValue()));
        System.out.println("[FUTURES UI] Search label=" + cleanLabel);
    }

    public boolean isExactPairVisible(String label) {
        try {
            WebElement pair = new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
                try {
                    // Re-resolve the container on every poll. Search filtering can update the DOM.
                    WebElement container = d.findElement(resultsContainerLocator());
                    WebElement candidate = container.findElement(pairLabelWithinContainerLocator(label));
                    return candidate.isDisplayed() ? candidate : null;
                } catch (RuntimeException ignored) {
                    return null;
                }
            });
            return pair != null && pair.isDisplayed();
        } catch (RuntimeException exception) {
            return false;
        }
    }

    public void clearSearch() {
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBarLocator()));
        prepareSearchViewportOnce(search);
        setSearchValue(search, "");
        wait.until(d -> searchValue().isEmpty());
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public By getSearchBarLocatorForDebug() {
        return searchBarLocator();
    }

    public By getResultsContainerLocatorForDebug() {
        return resultsContainerLocator();
    }

    public By getPairLocatorForDebug(String label) {
        return pairLabelWithinContainerLocator(label);
    }

    private void prepareSearchViewportOnce(WebElement search) {
        if (searchViewportPrepared) {
            return;
        }

        // Site/browser CSS may use smooth scrolling. Force automation scrolling to be instant.
        js.executeScript(
                "document.documentElement.style.setProperty('scroll-behavior','auto','important');" +
                "if (document.body) document.body.style.setProperty('scroll-behavior','auto','important');" +
                "var r=arguments[0].getBoundingClientRect();" +
                "var target=Math.max(0, window.pageYOffset + r.top - (window.innerHeight * 0.35));" +
                "window.scrollTo(0,target);" +
                "arguments[0].focus({preventScroll:true});",
                search
        );
        searchViewportPrepared = true;
        System.out.println("[FUTURES UI] Search viewport positioned instantly (one-time).");
    }

    private void setSearchValue(WebElement search, String value) {
        js.executeScript(
                "var el=arguments[0], val=arguments[1];" +
                "var setter=Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;" +
                "setter.call(el,val);" +
                "el.dispatchEvent(new Event('input',{bubbles:true}));" +
                "el.dispatchEvent(new KeyboardEvent('keyup',{bubbles:true,key: val.length ? val.charAt(val.length-1) : 'Backspace'}));" +
                "el.dispatchEvent(new Event('change',{bubbles:true}));",
                search,
                value
        );
    }

    private String searchValue() {
        WebElement search = driver.findElement(searchBarLocator());
        String value = search.getAttribute("value");
        return value == null ? "" : value;
    }

    private By searchBarLocator() {
        return configurableLocator(
                ConfigReader.requireProperty("futures.search.selector.type"),
                ConfigReader.requireProperty("futures.search.selector.value")
        );
    }

    private By resultsContainerLocator() {
        return configurableLocator(
                ConfigReader.requireProperty("futures.results.selector.type"),
                ConfigReader.requireProperty("futures.results.selector.value")
        );
    }

    private By pairLabelWithinContainerLocator(String label) {
        String cleanLabel = requireText(label, "futures label");
        String type = ConfigReader.requireProperty("futures.pair.selector.type");
        String template = ConfigReader.requireProperty("futures.pair.selector.template");

        String value;
        if ("xpath".equalsIgnoreCase(type.trim())) {
            value = String.format(template, xpathLiteral(cleanLabel));
        } else {
            value = String.format(template, cssAttributeValue(cleanLabel));
        }
        return configurableLocator(type, value);
    }

    private By configurableLocator(String rawType, String value) {
        String type = requireText(rawType, "selector type").toLowerCase(Locale.ROOT);
        String cleanValue = requireText(value, "selector value");
        return switch (type) {
            case "css", "cssselector" -> By.cssSelector(cleanValue);
            case "id" -> By.id(cleanValue);
            case "name" -> By.name(cleanValue);
            case "classname", "class" -> By.className(cleanValue);
            case "xpath" -> By.xpath(cleanValue);
            default -> throw new IllegalArgumentException(
                    "Selector type Futures tidak didukung: " + type
                            + ". Gunakan xpath/css/id/name/className."
            );
        };
    }

    private String cssAttributeValue(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }

    private String xpathLiteral(String value) {
        if (!value.contains("'")) return "'" + value + "'";
        if (!value.contains("\"")) return "\"" + value + "\"";
        return "concat('" + value.replace("'", "',\"'\",'") + "')";
    }

    private String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " tidak boleh kosong.");
        }
        return value.trim();
    }
}
