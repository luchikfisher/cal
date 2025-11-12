package org.example.core.validation;

public interface Validator {
    boolean isValidNumber(String input);
    boolean isValidOperator(String input);
    boolean isValidExpression(String expr);
    boolean isRecognizedCharacter(char c);
    boolean isBalancedParentheses(String expr);
}
