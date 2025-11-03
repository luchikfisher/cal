package org.example.util;

import lombok.experimental.UtilityClass;

/**
 * Validator — simple, stateless helper for token validation.
 */
@UtilityClass
public class Validator {

    private static final String[] ALLOWED_OPERATORS = {"+", "-", "*", "/", "sin", "cos"};

    public static boolean isValidNumber(String input) {
        if (input == null || input.isBlank()) return false;
        try {
            Double.parseDouble(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidOperator(String input) {
        if (input == null || input.isBlank()) return false;
        for (String op : ALLOWED_OPERATORS)
            if (op.equals(input.trim())) return true;
        return false;
    }
}
