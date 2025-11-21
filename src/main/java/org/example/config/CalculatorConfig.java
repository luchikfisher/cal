package org.example.config;

import lombok.NoArgsConstructor;

/**
 * Provides calculator-related configuration settings.
 */
@NoArgsConstructor
public final class CalculatorConfig {

    // ===== Thresholds =====
    public static int evalThreshold() {
        return ConfigurationManager.getInt("calculator.eval.threshold");
    }

    public static int exitThreshold() {
        return ConfigurationManager.getInt("calculator.exit.threshold");
    }

    // ===== Messages =====
    public static String exitMessage() {
        return ConfigurationManager.getOrThrow("calculator.exit.message");
    }

    public static String resultPrefix() {
        return ConfigurationManager.getOrThrow("calculator.result.prefix");
    }

    public static String errorEmpty() {
        return ConfigurationManager.getOrThrow("calculator.error.empty");
    }

    public static String promptInput() {
        return ConfigurationManager.getOrThrow("calculator.prompt.input");
    }
}