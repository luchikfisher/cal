package org.example.constants;

/**
 * Centralized constants for the calculator application.
 * Grouped by logical category: Operators, UI, Behavior, Utility.
 */
public final class Constants {

    private Constants() {}

    // ===== Operators =====
    public static final String PLUS_OPERATOR = "+";
    public static final String MINUS_OPERATOR = "-";
    public static final String MULTIPLY_OPERATOR = "*";
    public static final String DIVIDE_OPERATOR = "/";
    public static final String SIN_OPERATOR = "sin";
    public static final String COS_OPERATOR = "cos";

    // ===== UI / Messages =====
    public static final String PROMPT_INPUT =
            "Enter number/operator or full expression (double empty = evaluate, triple = exit):";
    public static final String ERROR_EMPTY = "❌ Nothing to evaluate.";
    public static final String RESULT_PREFIX = "Result:";
    public static final String EXIT_MESSAGE = "👋 Exiting calculator.";

    // ===== Behavior =====
    public static final int EVAL_THRESHOLD = 2;  // two empty lines → evaluate
    public static final int EXIT_THRESHOLD = 3;  // three empty lines → exit
}
