package org.example.core.validation;

public class EmptyExpressionValidator implements ExpressionValidator {

    @Override
    public ValidationResult validate(String expr) {
        return (expr == null || expr.isBlank())
                ? ValidationResult.fail("Expression is empty")
                : ValidationResult.ok();
    }
}