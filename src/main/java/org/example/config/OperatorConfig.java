package org.example.config;

import lombok.NoArgsConstructor;

/**
 * Provides configuration for mathematical operators and symbols.
 */
@NoArgsConstructor
public final class OperatorConfig {


    public static String plusOperator() {
        return ConfigurationManager.getOrThrow("operator.plus");
    }

    public static String minusOperator() {
        return ConfigurationManager.getOrThrow("operator.minus");
    }

    public static String multiplyOperator() {
        return ConfigurationManager.getOrThrow("operator.multiply");
    }

    public static char leftParen() {
        return ConfigurationManager.getOrThrow("operator.left.paren").charAt(0);
    }

    public static char rightParen() {
        return ConfigurationManager.getOrThrow("operator.right.paren").charAt(0);
    }

    public static char unaryMinusOperator() {
        return ConfigurationManager.getOrThrow("operator.unary.minus").charAt(0);
    }

    public static String allowedOperatorChars() {
        return ConfigurationManager.getOrThrow("operator.allowed.chars");
    }

    public static String recordFormat() {
        return ConfigurationManager.getOrThrow("operator.record.format");
    }
}
