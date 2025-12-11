package org.example.core.exception;

public class UnknownOperatorException extends EvaluationException {
    public UnknownOperatorException(String operatorType) {
        super("Unknown operator type: " + operatorType);
    }
}