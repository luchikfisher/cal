package org.example.core.validation;

import org.example.util.Constants;
import org.example.core.operators.factory.OperatorFactory;

import java.util.stream.IntStream;

/**
 * DefaultValidator — centralized validation implementation.
 */
public class DefaultValidator implements Validator {

    @Override
    public boolean isValidNumber(String input) {
        if (input == null || input.isBlank()) return false;
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isValidOperator(String input) {
        if (input == null || input.isBlank()) return false;
        return OperatorFactory.getRegistry().containsKey(input);
    }

    @Override
    public boolean isRecognizedCharacter(char c) {
        return Character.isLetterOrDigit(c) || Constants.ALLOWED_OPERATOR_CHARS.indexOf(c) >= 0;
    }

    @Override
    public boolean isBalancedParentheses(String expr) {
        int depth = expr.chars()
                .map(c -> c == Constants.LEFT_PAREN.charAt(0) ? 1 :
                        c == Constants.RIGHT_PAREN.charAt(0) ? -1 : 0)
                .reduce(0, (acc, val) -> {
                    if (acc < 0) return acc;
                    return acc + val;
                });
        return depth == 0;
    }

    @Override
    public boolean isValidExpression(String expr) {
        if (expr == null || expr.isBlank()) return false;
        return !hasInvalidOperatorPlacement(expr)
                && !hasConsecutiveInvalidOperators(expr)
                && !hasMalformedFunctionCalls(expr);
    }

    private boolean hasInvalidOperatorPlacement(String expr) {
        String first = String.valueOf(expr.charAt(0));
        String last = String.valueOf(expr.charAt(expr.length() - 1));
        boolean startsIllegal = isValidOperator(first)
                && !first.equals(Constants.PLUS_OPERATOR)
                && !first.equals(Constants.MINUS_OPERATOR);
        return startsIllegal || isValidOperator(last);
    }

    private boolean hasConsecutiveInvalidOperators(String expr) {
        return IntStream.range(1, expr.length()).anyMatch(i -> {
            char prev = expr.charAt(i - 1);
            char curr = expr.charAt(i);
            boolean bothOps = isValidOperator(String.valueOf(prev)) && isValidOperator(String.valueOf(curr));
            boolean bothSigns = (prev == '+' || prev == '-') && (curr == '+' || curr == '-');
            return bothOps && !bothSigns;
        });
    }

    private boolean hasMalformedFunctionCalls(String expr) {
        return OperatorFactory.getRegistry().keySet().stream()
                .filter(op -> Character.isLetter(op.charAt(0)))
                .anyMatch(op ->
                        (expr.contains(op) && !expr.contains(op + "(")) ||
                                expr.contains(op + "()"));
    }
}
