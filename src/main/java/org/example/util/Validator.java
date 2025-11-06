package org.example.util;

import lombok.experimental.UtilityClass;
import org.example.constants.Constants;

import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

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
        return Optional.ofNullable(input)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        Double.parseDouble(s);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .orElse(false);
    }

    public boolean isValidOperator(String input) {
        return Optional.ofNullable(input)
                .map(String::trim)
                .filter(ALLOWED_OPERATORS::contains)
                .isPresent();
    }

    private boolean isBlank(String s) {
        return Optional.ofNullable(s).map(String::isBlank).orElse(true);
    }

    // ===== EXPRESSION VALIDATION =====
    public boolean isValidExpression(String expr) {
        return Optional.ofNullable(expr)
                .filter(s -> !s.isBlank())
                .filter(s -> !startsOrEndsWithOperator(s))
                .filter(s -> !hasIllegalOperatorPairs(s))
                .filter(s -> !hasInvalidFunctions(s))
                .isPresent();
    }

    private boolean startsOrEndsWithOperator(String expr) {
        if (expr.length() < 1) return false;

        String first = String.valueOf(expr.charAt(0));
        String last = String.valueOf(expr.charAt(expr.length() - 1));

        boolean startsIllegal =
                isValidOperator(first)
                        && !first.equals(Constants.PLUS_OPERATOR)
                        && !first.equals(Constants.MINUS_OPERATOR);

        return startsIllegal || isValidOperator(last);
    }

    private boolean hasIllegalOperatorPairs(String expr) {
        // שימוש ב־IntStream כדי לבדוק רצפים אסורים
        return IntStream.range(1, expr.length())
                .anyMatch(i -> {
                    char prev = expr.charAt(i - 1);
                    char curr = expr.charAt(i);

                    boolean bothOperators = isValidOperator(String.valueOf(prev))
                            && isValidOperator(String.valueOf(curr));
                    boolean bothSigns = (prev == '+' || prev == '-') &&
                            (curr == '+' || curr == '-');

                    return bothOperators && !bothSigns;
                });
    }

    private boolean hasInvalidFunctions(String expr) {
        // שימוש ב־Stream על ALLOWED_OPERATORS במקום לולאה רגילה
        return ALLOWED_OPERATORS.stream()
                .filter(op -> Character.isLetter(op.charAt(0))) // רק פונקציות מילוליות (sin, cos)
                .anyMatch(op ->
                        (expr.contains(op) && !expr.contains(op + "(")) ||
                                expr.contains(op + "()")
                );
    }
}