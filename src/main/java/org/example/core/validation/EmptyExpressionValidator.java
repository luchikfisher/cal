package org.example.core.validation;


public class EmptyExpressionValidator implements ExpressionValidator {
    @Override
    public boolean validate(String expr) {
        return expr != null && !expr.isBlank();
    }
}