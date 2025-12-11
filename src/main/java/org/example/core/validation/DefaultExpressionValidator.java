package org.example.core.validation;

import org.example.core.lexer.Lexer;

import java.util.List;


public class DefaultExpressionValidator implements ExpressionValidator {

    private final List<ExpressionValidator> validators;

    public DefaultExpressionValidator(Lexer lexer) {
        this.validators = List.of(
                new EmptyExpressionValidator(),
                new ParenthesesValidator(),
                new FunctionCallValidator(lexer)
        );
    }

    @Override
    public boolean validate(String expr) {
        for (ExpressionValidator v : validators) {
            if (!v.validate(expr)) return false; // FAIL FAST
        }
        return true;
    }
}