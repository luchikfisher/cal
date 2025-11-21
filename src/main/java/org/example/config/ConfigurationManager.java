package org.example.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Optional;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationManager {

    private static final String CONFIG_FILE = "/application.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigurationManager.class.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Config file not found: " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new ConfigurationLoadException("Failed to load configuration file: " + CONFIG_FILE, e);
        }
    }

    public static Optional<String> get(String key) {
        return Optional.ofNullable(properties.getProperty(key));
    }

    public static String getOrThrow(String key) {
        return get(key)
                .orElseThrow(() ->
                        new ConfigurationLoadException("Missing configuration value for key: " + key)
                );
    }

    public static int getInt(String key) {
        return get(key)
                .map(String::trim)
                .map(Integer::parseInt)
                .orElseThrow(() ->
                        new ConfigurationLoadException("Missing integer value for key: " + key)
                );
    }
}
