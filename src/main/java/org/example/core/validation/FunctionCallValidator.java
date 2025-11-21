package org.example.core.validation;

import org.example.core.operators.factory.OperatorFactory;

public class FunctionCallValidator implements ExpressionValidator {

    @Override
    public ValidationResult validate(String expr) {
        boolean malformed = OperatorFactory.getRegistry().keySet().stream()
                .filter(op -> Character.isLetter(op.charAt(0)))
                .anyMatch(op ->
                        (expr.contains(op) && !expr.contains(op + "(")) ||
                                expr.contains(op + "()")
                );

        return malformed
                ? ValidationResult.fail("Malformed function call")
                : ValidationResult.ok();
    }
}