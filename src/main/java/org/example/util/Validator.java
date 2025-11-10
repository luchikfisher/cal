package org.example.util;

public interface Validator {
    boolean isValidNumber(String input);
    boolean isValidOperator(String input);
    boolean isValidExpression(String expr);
}
