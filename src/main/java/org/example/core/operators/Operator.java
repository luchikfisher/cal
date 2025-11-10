package org.example.core.operators;

public interface Operator {
    String getSymbol();
    int getPrecedence();
    boolean isLeftAssociative();
    int getOperandCount();
    double apply(double... operands);
}
