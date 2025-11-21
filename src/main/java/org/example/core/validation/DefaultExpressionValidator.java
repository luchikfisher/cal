package org.example.core.validation;

import java.util.List;


public class DefaultExpressionValidator implements ExpressionValidator {

    private final List<ExpressionValidator> validators;

    public DefaultExpressionValidator() {
        this.validators = List.of(
                new EmptyExpressionValidator(),
                new OperatorPlacementValidator(),
                new ParenthesesValidator(),
                new FunctionCallValidator()
        );
    }

    @Override
    public ValidationResult validate(String expr) {
        for (ExpressionValidator v : validators) {
            ValidationResult result = v.validate(expr);
            if (!result.isValid()) return result; // Fail Fast
        }

        return ValidationResult.ok();
    }
}