package org.example.core.operators.base;

public interface Operator {
    String getSymbol();
    int getPrecedence();
    boolean isLeftAssociative();
    int getOperandCount();
}