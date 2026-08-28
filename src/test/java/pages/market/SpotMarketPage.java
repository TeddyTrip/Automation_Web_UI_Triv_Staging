package pages.market;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

/**
 * Spot Market page object.
 *
 * v1.4.5:
 * - horizontally scroll category menu to the requested category (especially All) before click
 * - category scroll uses instant behavior; no slow smooth scrolling
 *
 * v1.4.4:
 * - Web validation always uses Category=All
 * - search keyword uses compact Web format (AR/BTC -> ARBTC)
 * - exact DOM validation remains route symbol format (AR_BTC)
 *
 * v1.4.3:
 * - wait exact searched asset before moving to the next label
 * - configurable result timeout with fast polling
 * - wait active market table rows after category/pair switch
 *
 * v1.4.1:
 * - final selectors based on confirmed TRIV Market DOM
 * - dynamic category + quote pair navigation
 * - visible-element lookup (avoids hidden duplicate search inputs)
 * - JavaScript tab/search interaction to avoid slow scroll/click interception
 * - exact result validation via data-symbol/data-selected-currency
 */
public class SpotMarketPage {
    private static final Duration READY_TIMEOUT = Duration.ofSeconds(20);
    private static final Duration FILTER_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration DEFAULT_RESULT_TIMEOUT = Duration.ofSeconds(6);
    private static final Duration DEFAULT_POLL_INTERVAL = Duration.ofMillis(100);

    private final WebDriver driver;
    private final JavascriptExecutor js;
    private WebElement activeSearch;
    private boolean searchPositioned;

    public SpotMarketPage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
    }

    public void openDefaultPage() {
        openSymbol(ConfigReader.requireProperty("market.default.symbol"));
    }

    public void openSymbol(String routeSymbol) {
        String symbol = requireText(routeSymbol, "market route symbol");
        String template = requireEnvProperty("market.web.path.template");
        String path = String.format(template, symbol);
        String url = ConfigReader.webUrl(path);
        System.out.println("[SPOT UI] Open URL=" + url);
        driver.get(url);
        activeSearch = null;
        searchPositioned = false;
        waitUntilReady();
    }

    public void waitUntilReady() {
        waitForDisplayed(By.cssSelector("ul#container-menu.market-categories"), READY_TIMEOUT);
        // Web Spot validation is intentionally fixed to Category=All.
        // Start from All / IDR and only switch quote tabs during the run.
        selectMarketContext("all", "IDR");
        WebElement search = searchElement();
        positionSearchOnce(search);
        System.out.println("[SPOT UI] Search locator=By.name: search_market");
        System.out.println("[SPOT UI] Results locator=By.cssSelector: tbody.all-market-tbody");
    }

    public void selectMarketContext(String uiCategory, String quote) {
        String category = normalizeLower(uiCategory, "market UI category");
        String pair = normalizeLower(quote, "market quote");

        WebElement categoryButton = waitForDisplayed(
                By.cssSelector("button.category-nav[data-category='" + cssValue(category) + "']"),
                READY_TIMEOUT);
        scrollCategoryIntoView(categoryButton, category);
        clickTabIfNeeded(categoryButton);
        waitUntilActive(By.cssSelector("button.category-nav[data-category='" + cssValue(category) + "']"));

        By exactPair = By.cssSelector("button.pair-nav[data-category='" + cssValue(category)
                + "'][data-pair='" + cssValue(pair) + "']");
        WebElement pairButton = findDisplayed(exactPair);
        if (pairButton == null) {
            // Some category layouts can reuse one visible pair-nav group without matching data-category.
            pairButton = waitForDisplayed(
                    By.cssSelector("button.pair-nav[data-pair='" + cssValue(pair) + "']"),
                    READY_TIMEOUT);
        }
        clickTabIfNeeded(pairButton);
        waitUntilElementActive(pairButton, pair);
        waitUntilActiveMarketRowsReady();

        // DOM can be refreshed by Bootstrap tabs; reacquire search after every context switch.
        activeSearch = null;
        WebElement search = searchElement();
        positionSearchOnce(search);
        System.out.println("[SPOT UI] Context category=" + category + " | pair=" + pair.toUpperCase(Locale.ROOT));
    }

    public void search(String searchKeyword) {
        String value = requireText(searchKeyword, "market search keyword");
        WebElement search = searchElement();
        setSearchValue(search, value);
        newWait(FILTER_TIMEOUT).until(d -> value.equals(currentSearchValue()));
        System.out.println("[SPOT UI] Search keyword=" + value);
    }

    /**
     * Search one label and block until the exact route symbol is visible.
     * The wait returns immediately as soon as the target appears; the configured timeout
     * is only the maximum time used for a genuinely slow/missing result.
     */
    public boolean searchAndWaitExactPair(String searchKeyword, String routeSymbol) {
        String symbol = requireText(routeSymbol, "market route symbol").toUpperCase(Locale.ROOT);
        search(searchKeyword);

        Duration timeout = resultTimeout();
        System.out.println("[SPOT UI] Waiting exact asset=" + symbol
                + " | max=" + timeout.toSeconds() + "s");
        try {
            WebElement pair = newWait(timeout).until(d -> findExactPair(symbol));
            boolean visible = pair != null && pair.isDisplayed();
            if (visible) {
                System.out.println("[SPOT UI] Asset ready=" + symbol);
            }
            return visible;
        } catch (RuntimeException exception) {
            System.out.println("[SPOT UI] Asset wait timeout=" + symbol);
            return false;
        }
    }

    public boolean isExactPairVisible(String routeSymbol) {
        String symbol = requireText(routeSymbol, "market route symbol").toUpperCase(Locale.ROOT);
        try {
            WebElement pair = newWait(resultTimeout()).until(d -> findExactPair(symbol));
            return pair != null && pair.isDisplayed();
        } catch (RuntimeException exception) {
            return false;
        }
    }

    public void clearSearch() {
        WebElement search = searchElement();
        setSearchValue(search, "");
        newWait(FILTER_TIMEOUT).until(d -> currentSearchValue().isEmpty());
    }

    private void waitUntilActiveMarketRowsReady() {
        newWait(READY_TIMEOUT).until(d -> {
            for (WebElement container : driver.findElements(By.cssSelector("tbody.all-market-tbody"))) {
                try {
                    if (!container.isDisplayed()) continue;
                    for (WebElement row : container.findElements(By.cssSelector("tr.triv-market"))) {
                        if (row.isDisplayed()) return true;
                    }
                } catch (StaleElementReferenceException ignored) {
                    // Bootstrap/DataTable can redraw during a category/pair switch.
                }
            }
            return false;
        });
    }

    private WebElement findExactPair(String routeSymbol) {
        By containerLocator = By.cssSelector("tbody.all-market-tbody");
        for (WebElement container : driver.findElements(containerLocator)) {
            try {
                if (!container.isDisplayed()) continue;
                String symbol = cssValue(routeSymbol);
                By exactAnchor = By.cssSelector(
                        "td[data-symbol='" + symbol + "'] "
                                + "a.all-24hticker-yo[data-selected-currency='" + symbol + "']");
                for (WebElement anchor : container.findElements(exactAnchor)) {
                    if (!anchor.isDisplayed()) continue;
                    String selected = safe(anchor.getAttribute("data-selected-currency"));
                    WebElement cell = anchor.findElement(By.xpath("ancestor::td[@data-symbol][1]"));
                    String dataSymbol = safe(cell.getAttribute("data-symbol"));
                    if (routeSymbol.equalsIgnoreCase(selected) && routeSymbol.equalsIgnoreCase(dataSymbol)) {
                        return anchor;
                    }
                }
            } catch (StaleElementReferenceException ignored) {
                // Search/filter can redraw table; next polling cycle reacquires a fresh tbody.
            }
        }
        return null;
    }

    private WebElement searchElement() {
        if (activeSearch != null) {
            try {
                if (activeSearch.isDisplayed()) return activeSearch;
            } catch (StaleElementReferenceException ignored) {
                activeSearch = null;
            }
        }
        activeSearch = waitForDisplayed(By.name("search_market"), READY_TIMEOUT);
        return activeSearch;
    }

    private WebElement waitForDisplayed(By locator, Duration timeout) {
        return newWait(timeout).until(d -> findDisplayed(locator));
    }

    private WebElement findDisplayed(By locator) {
        for (WebElement element : driver.findElements(locator)) {
            try {
                if (element.isDisplayed()) return element;
            } catch (StaleElementReferenceException ignored) {
            }
        }
        return null;
    }

    private void waitUntilActive(By locator) {
        newWait(FILTER_TIMEOUT).until(d -> {
            WebElement element = findDisplayed(locator);
            return element != null && hasActiveClass(element);
        });
    }

    private void waitUntilElementActive(WebElement original, String pair) {
        newWait(FILTER_TIMEOUT).until(d -> {
            try {
                if (original.isDisplayed() && hasActiveClass(original)) return true;
            } catch (StaleElementReferenceException ignored) {
            }
            WebElement fresh = findDisplayed(By.cssSelector(
                    "button.pair-nav[data-pair='" + cssValue(pair) + "']"));
            return fresh != null && hasActiveClass(fresh);
        });
    }

    private boolean hasActiveClass(WebElement element) {
        String cssClass = safe(element.getAttribute("class"));
        for (String token : cssClass.split("\\s+")) {
            if ("active".equals(token)) return true;
        }
        return false;
    }


    private void scrollCategoryIntoView(WebElement categoryButton, String category) {
        WebElement menu = waitForDisplayed(By.cssSelector("ul#container-menu.market-categories"), READY_TIMEOUT);
        js.executeScript(
                "var menu=arguments[0],btn=arguments[1];" +
                "if(menu){" +
                "  menu.style.setProperty('scroll-behavior','auto','important');" +
                "  var target=btn.offsetLeft-(menu.clientWidth/2)+(btn.offsetWidth/2);" +
                "  menu.scrollLeft=Math.max(0,target);" +
                "}" +
                "if(btn){btn.scrollIntoView({block:'nearest',inline:'center',behavior:'auto'});}",
                menu, categoryButton);
        newWait(FILTER_TIMEOUT).until(d -> {
            try {
                return categoryButton.isDisplayed();
            } catch (StaleElementReferenceException ignored) {
                return findDisplayed(By.cssSelector(
                        "button.category-nav[data-category='" + cssValue(category) + "']")) != null;
            }
        });
        System.out.println("[SPOT UI] Category menu horizontal scroll -> " + category);
    }

    private void clickTabIfNeeded(WebElement element) {
        if (hasActiveClass(element)) return;
        js.executeScript("arguments[0].click();", element);
    }

    private void positionSearchOnce(WebElement element) {
        if (searchPositioned) return;
        js.executeScript(
                "document.documentElement.style.setProperty('scroll-behavior','auto','important');" +
                "if(document.body)document.body.style.setProperty('scroll-behavior','auto','important');" +
                "arguments[0].scrollIntoView({block:'center',inline:'nearest',behavior:'auto'});" +
                "arguments[0].focus({preventScroll:true});", element);
        searchPositioned = true;
        System.out.println("[SPOT UI] Search viewport positioned instantly (one-time).");
    }

    private void setSearchValue(WebElement search, String value) {
        js.executeScript(
                "var el=arguments[0],val=arguments[1];" +
                "var setter=Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;" +
                "setter.call(el,val);" +
                "el.dispatchEvent(new Event('input',{bubbles:true}));" +
                "el.dispatchEvent(new KeyboardEvent('keyup',{bubbles:true,key:val.length?val.charAt(val.length-1):'Backspace'}));" +
                "el.dispatchEvent(new Event('change',{bubbles:true}));", search, value);
    }

    private String currentSearchValue() {
        String value = searchElement().getAttribute("value");
        return value == null ? "" : value;
    }

    private WebDriverWait newWait(Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.pollingEvery(pollInterval());
        wait.ignoring(StaleElementReferenceException.class);
        return wait;
    }


    private Duration resultTimeout() {
        return Duration.ofSeconds(readPositiveLong("market.search.result.timeout.seconds", DEFAULT_RESULT_TIMEOUT.toSeconds()));
    }

    private Duration pollInterval() {
        return Duration.ofMillis(readPositiveLong("market.search.poll.millis", DEFAULT_POLL_INTERVAL.toMillis()));
    }

    private long readPositiveLong(String key, long defaultValue) {
        String raw = ConfigReader.getProperty(key);
        if (raw == null || raw.isBlank()) return defaultValue;
        try {
            long value = Long.parseLong(raw.trim());
            return value > 0 ? value : defaultValue;
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private String requireEnvProperty(String key) {
        String value = ConfigReader.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Config environment wajib tidak ditemukan: " + key
                    + " env=" + ConfigReader.getEnvironment());
        }
        return value.trim();
    }

    private String normalizeLower(String value, String field) {
        return requireText(value, field).toLowerCase(Locale.ROOT);
    }

    private String cssValue(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }

    private String safe(String value) { return value == null ? "" : value.trim(); }

    private String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " tidak boleh kosong.");
        }
        return value.trim();
    }
}
