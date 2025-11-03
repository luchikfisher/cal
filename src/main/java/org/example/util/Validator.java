package org.example.util;

import lombok.experimental.UtilityClass;
import org.example.constants.Constants;

import java.util.Set;

/**
 * Validator — simple, stateless helper for token validation.
 * Checks if a given string is a valid number or a known operator.
 */
@UtilityClass
public class Validator {

    private static final Set<String> ALLOWED_OPERATORS =
            Set.of(Constants.PLUS_OPERATOR, Constants.MINUS_OPERATOR, "*", "/", "sin", "cos");

    /**
     * Checks if a string represents a valid number (integer or decimal).
     */
    public static boolean isValidNumber(String input) {
        if (input == null || input.isBlank()) return false;

        try {
            Double.parseDouble(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if a token is one of the known mathematical operators.
     */
    public static boolean isValidOperator(String input) {
        if (input == null || input.isBlank()) return false;
        return ALLOWED_OPERATORS.contains(input.trim());
    }
}
