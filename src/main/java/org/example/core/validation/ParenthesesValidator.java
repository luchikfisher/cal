package org.example.core.validation;

import org.example.config.OperatorConfig;

public class ParenthesesValidator implements ExpressionValidator {

    private static final char LEFT = OperatorConfig.leftParen();
    private static final char RIGHT = OperatorConfig.rightParen();

    @Override
    public ValidationResult validate(String expr) {
        int depth = 0;
        for (char c : expr.toCharArray()) {
            if (c == LEFT) depth++;
            else if (c == RIGHT) depth--;
            if (depth < 0) return ValidationResult.fail("Unbalanced parentheses");
        }
        return depth == 0 ? ValidationResult.ok() : ValidationResult.fail("Unbalanced parentheses");
    }
}