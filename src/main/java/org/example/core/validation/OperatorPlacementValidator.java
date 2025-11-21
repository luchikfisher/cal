package org.example.core.validation;

import org.example.config.OperatorConfig;
import org.example.core.operators.factory.OperatorFactory;

import java.util.stream.IntStream;

public class OperatorPlacementValidator implements ExpressionValidator {

    private static final String PLUS = OperatorConfig.plusOperator();
    private static final String MINUS = OperatorConfig.minusOperator();

    @Override
    public ValidationResult validate(String expr) {
        if (hasInvalidOperatorPlacement(expr))
            return ValidationResult.fail("Invalid operator placement");

        if (hasConsecutiveInvalidOperators(expr))
            return ValidationResult.fail("Consecutive invalid operators");

        return ValidationResult.ok();
    }

    private boolean hasInvalidOperatorPlacement(String expr) {
        String first = String.valueOf(expr.charAt(0));
        String last = String.valueOf(expr.charAt(expr.length() - 1));

        boolean startsIllegal = OperatorFactory.getRegistry().containsKey(first)
                && !first.equals(PLUS)
                && !first.equals(MINUS);

        return startsIllegal || OperatorFactory.getRegistry().containsKey(last);
    }

    private boolean hasConsecutiveInvalidOperators(String expr) {

        return IntStream.range(1, expr.length()).anyMatch(i -> {

            char prev = expr.charAt(i - 1);
            char curr = expr.charAt(i);

            boolean prevIsOp = OperatorFactory.getRegistry().containsKey(String.valueOf(prev));
            boolean currIsOp = OperatorFactory.getRegistry().containsKey(String.valueOf(curr));

            if (prevIsOp && currIsOp) {

                return curr != '-' && curr != '+'; // VALID
            }

            return false;
        });
    }
}
