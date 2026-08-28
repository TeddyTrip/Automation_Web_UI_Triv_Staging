package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

/**
 * Central configuration resolver for TRIV Web Automation.
 *
 * Priority:
 * 1. JVM system property (-Dkey=value)
 * 2. Environment variable (KEY -> upper snake case)
 * 3. active environment properties (staging.properties / production.properties)
 * 4. common config.properties
 */
public final class ConfigReader {

    private static final String CONFIG_DIR = "src/test/resources/config/";
    private static final Properties COMMON = load(CONFIG_DIR + "config.properties", true);
    private static final String ACTIVE_ENV = resolveEnvironment();
    private static final Properties ENV = load(CONFIG_DIR + ACTIVE_ENV + ".properties", true);

    private ConfigReader() {
    }

    public static String getProperty(String key) {
        String systemValue = trimToNull(System.getProperty(key));
        if (systemValue != null) {
            return systemValue;
        }

        String envValue = trimToNull(System.getenv(toEnvironmentKey(key)));
        if (envValue != null) {
            return envValue;
        }

        String environmentValue = trimToNull(ENV.getProperty(key));
        if (environmentValue != null) {
            return environmentValue;
        }

        return trimToNull(COMMON.getProperty(key));
    }

    public static String requireProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            throw new IllegalStateException(
                    "Config wajib tidak ditemukan: '" + key + "' untuk env=" + ACTIVE_ENV
            );
        }
        return value;
    }

    public static String getEnvironment() {
        return ACTIVE_ENV;
    }

    public static String getWebBaseUrl() {
        return stripTrailingSlash(requireProperty("web.base.url"));
    }

    public static String getApiBaseUrl() {
        return stripTrailingSlash(requireProperty("api.base.url"));
    }

    /** Resolve a property from a specific environment without changing active.env. */
    public static String getPropertyForEnvironment(String environment, String key) {
        String normalized = normalizeEnvironment(environment);

        String environmentSpecificSystem = trimToNull(System.getProperty(normalized + "." + key));
        if (environmentSpecificSystem != null) {
            return environmentSpecificSystem;
        }

        String environmentSpecificEnv = trimToNull(
                System.getenv(toEnvironmentKey(normalized + "." + key))
        );
        if (environmentSpecificEnv != null) {
            return environmentSpecificEnv;
        }

        Properties properties = load(CONFIG_DIR + normalized + ".properties", true);
        return trimToNull(properties.getProperty(key));
    }

    public static String getApiBaseUrl(String environment) {
        String value = getPropertyForEnvironment(environment, "api.base.url");
        if (value == null) {
            throw new IllegalStateException(
                    "api.base.url tidak ditemukan untuk env=" + environment
            );
        }
        return stripTrailingSlash(value);
    }

    public static String getWebBaseUrl(String environment) {
        String value = getPropertyForEnvironment(environment, "web.base.url");
        if (value == null) {
            throw new IllegalStateException(
                    "web.base.url tidak ditemukan untuk env=" + environment
            );
        }
        return stripTrailingSlash(value);
    }

    public static String apiUrl(String environment, String path) {
        return appendPath(getApiBaseUrl(environment), path);
    }

    public static String webUrl(String environment, String path) {
        return appendPath(getWebBaseUrl(environment), path);
    }

    public static String webUrl(String path) {
        return appendPath(getWebBaseUrl(), path);
    }

    public static String apiUrl(String path) {
        return appendPath(getApiBaseUrl(), path);
    }

    public static void printActiveEnvironment() {
        System.out.println("[CONFIG] env=" + ACTIVE_ENV);
        System.out.println("[CONFIG] web.base.url=" + getWebBaseUrl());
        System.out.println("[CONFIG] api.base.url=" + getApiBaseUrl());
    }

    private static String resolveEnvironment() {
        String fromSystem = trimToNull(System.getProperty("env"));
        String fromEnv = trimToNull(System.getenv("TRIV_ENV"));
        String fromCommon = trimToNull(COMMON.getProperty("active.env"));

        String candidate = fromSystem != null
                ? fromSystem
                : (fromEnv != null ? fromEnv : (fromCommon != null ? fromCommon : "staging"));

        return normalizeEnvironment(candidate);
    }


    private static String normalizeEnvironment(String candidate) {
        String normalized = candidate == null ? "" : candidate.toLowerCase(Locale.ROOT).trim();
        if ("prod".equals(normalized)) {
            normalized = "production";
        }

        if (!"staging".equals(normalized) && !"production".equals(normalized)) {
            throw new IllegalArgumentException(
                    "Environment tidak dikenal: '" + candidate + "'. Gunakan staging atau production."
            );
        }
        return normalized;
    }

    private static Properties load(String path, boolean required) {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(path)) {
            properties.load(input);
            return properties;
        } catch (IOException exception) {
            if (required) {
                throw new ExceptionInInitializerError(
                        "Gagal load config: " + path + " | " + exception.getMessage()
                );
            }
            return properties;
        }
    }

    private static String appendPath(String base, String path) {
        if (path == null || path.isBlank()) {
            return base;
        }
        String cleanPath = path.trim();
        if (!cleanPath.startsWith("/")) {
            cleanPath = "/" + cleanPath;
        }
        return base + cleanPath;
    }

    private static String stripTrailingSlash(String value) {
        String result = value;
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private static String toEnvironmentKey(String key) {
        return key.toUpperCase(Locale.ROOT).replace('.', '_').replace('-', '_');
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
