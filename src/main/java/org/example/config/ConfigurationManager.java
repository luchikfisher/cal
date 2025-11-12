package org.example.config;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads configuration from application.properties and provides typed access.
 */
@UtilityClass
public class ConfigurationManager {

    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "/application.properties";

    static {
        try (InputStream input = ConfigurationManager.class.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("❌ Missing configuration file: " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("❌ Failed to load configuration file: " + CONFIG_FILE, e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}
