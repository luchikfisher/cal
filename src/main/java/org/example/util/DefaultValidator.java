package org.example.util;

import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * DefaultValidator — centralized validation implementation.
 */
public class DefaultValidator implements Validator {

    private static final Set<String> ALLOWED_OPERATORS = Set.of(
            Constants.PLUS_OPERATOR,
            Constants.MINUS_OPERATOR,
            Constants.MULTIPLY_OPERATOR,
            Constants.DIVIDE_OPERATOR,
            Constants.SIN_OPERATOR,
            Constants.COS_OPERATOR
    );

    @Override
    public boolean isValidNumber(String input) {
        return Optional.ofNullable(input)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try { Double.parseDouble(s); return true; }
                    catch (NumberFormatException e) { return false; }
                })
                .orElse(false);
    }

    @Override
    public boolean isValidOperator(String input) {
        return Optional.ofNullable(input)
                .map(String::trim)
                .filter(ALLOWED_OPERATORS::contains)
                .isPresent();
    }

    @Override
    public boolean isValidExpression(String expr) {
        return Optional.ofNullable(expr)
                .filter(s -> !s.isBlank())
                .filter(s -> !startsOrEndsWithOperator(s))
                .filter(s -> !hasIllegalOperatorPairs(s))
                .filter(s -> !hasInvalidFunctions(s))
                .isPresent();
    }

    private boolean startsOrEndsWithOperator(String expr) {
        String first = String.valueOf(expr.charAt(0));
        String last = String.valueOf(expr.charAt(expr.length() - 1));
        boolean startsIllegal = isValidOperator(first)
                && !first.equals(Constants.PLUS_OPERATOR)
                && !first.equals(Constants.MINUS_OPERATOR);
        return startsIllegal || isValidOperator(last);
    }

    private boolean hasIllegalOperatorPairs(String expr) {
        return IntStream.range(1, expr.length()).anyMatch(i -> {
            char prev = expr.charAt(i - 1);
            char curr = expr.charAt(i);
            boolean bothOps = isValidOperator(String.valueOf(prev)) && isValidOperator(String.valueOf(curr));
            boolean bothSigns = (prev == '+' || prev == '-') && (curr == '+' || curr == '-');
            return bothOps && !bothSigns;
        });
    }

    private boolean hasInvalidFunctions(String expr) {
        return ALLOWED_OPERATORS.stream()
                .filter(op -> Character.isLetter(op.charAt(0)))
                .anyMatch(op ->
                        (expr.contains(op) && !expr.contains(op + "(")) ||
                                expr.contains(op + "()"));
    }
}
