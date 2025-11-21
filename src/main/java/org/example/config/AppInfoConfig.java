package org.example.config;

import lombok.NoArgsConstructor;

/**
 * Provides application-level metadata configuration.
 */
@NoArgsConstructor
public final class AppInfoConfig {

    public static String appName() {
        return ConfigurationManager.getOrThrow("app.name");
    }

    public static String appVersion() {
        return ConfigurationManager.getOrThrow("app.version");
    }
}
