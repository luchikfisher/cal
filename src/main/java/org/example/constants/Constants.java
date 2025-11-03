package org.example.constants;

public final class Constants {
    private Constants() {}

    // Operators
    public static final String PLUS_OPERATOR = "+";
    public static final String MINUS_OPERATOR = "-";

    // Messages
    public static final String PROMPT_INPUT =
            "Enter number/operator or full expression (double empty = exit):";
    public static final String ERROR_INVALID_INPUT = "❌ Invalid input.";
    public static final String ERROR_EMPTY = "❌ Nothing to evaluate.";
    public static final String RESULT_PREFIX = "Result: ";
    public static final String EXIT_MESSAGE = "👋 Exiting calculator.";

    // Behavior
    public static final int EXIT_THRESHOLD = 2;

    // Utility
    public static boolean isSignChar(char c) {
        return c == PLUS_OPERATOR.charAt(0) || c == MINUS_OPERATOR.charAt(0);
    }
}
