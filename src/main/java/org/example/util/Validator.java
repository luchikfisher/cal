package org.example.util;

import lombok.experimental.UtilityClass;
import org.example.constants.Constants;

import java.util.Set;

/**
 * Validator — centralized stateless validation helper.
 * Single source of truth for all syntactic & semantic rules.
 */
@UtilityClass
public class Validator {

    private static final Set<String> ALLOWED_OPERATORS = Set.of(
            Constants.PLUS_OPERATOR,
            Constants.MINUS_OPERATOR,
            Constants.MULTIPLY_OPERATOR,
            Constants.DIVIDE_OPERATOR,
            Constants.SIN_OPERATOR,
            Constants.COS_OPERATOR
    );

    // ===== BASIC CHECKS =====
    public boolean isValidNumber(String input) {
        if (isBlank(input)) return false;
        try { Double.parseDouble(input.trim()); return true; }
        catch (NumberFormatException e) { return false; }
    }

    public boolean isValidOperator(String input) {
        return !isBlank(input) && ALLOWED_OPERATORS.contains(input.trim());
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    // ===== EXPRESSION VALIDATION =====
    public boolean isValidExpression(String expr) {
        if (isBlank(expr)) return false;
        if (startsOrEndsWithOperator(expr)) return false;
        if (hasIllegalOperatorPairs(expr)) return false;
        return !hasInvalidFunctions(expr);
    }

    private boolean startsOrEndsWithOperator(String expr) {
        String first = String.valueOf(expr.charAt(0));
        String last = String.valueOf(expr.charAt(expr.length() - 1));
        return (isValidOperator(first) && !first.equals(Constants.PLUS_OPERATOR) && !first.equals(Constants.MINUS_OPERATOR))
                || (isValidOperator(last));
    }

    private boolean hasIllegalOperatorPairs(String expr) {
        for (int i = 1; i < expr.length(); i++) {
            char prev = expr.charAt(i - 1);
            char curr = expr.charAt(i);

            boolean bothOperators = isValidOperator(String.valueOf(prev)) &&
                    isValidOperator(String.valueOf(curr));
            boolean bothSigns = (prev == '+' || prev == '-') &&
                    (curr == '+' || curr == '-');

            if (bothOperators && !bothSigns) return true;
        }
        return false;
    }


    private boolean hasInvalidFunctions(String expr) {
        for (String op : ALLOWED_OPERATORS) {
            if (Character.isLetter(op.charAt(0))) { // sin, cos, etc.
                if (expr.contains(op) && !expr.contains(op + "(")) return true;
                if (expr.contains(op + "()")) return true;
            }
        }
        return false;
    }
}
