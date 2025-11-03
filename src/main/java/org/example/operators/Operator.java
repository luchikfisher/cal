package org.example.operators;

public interface Operator {
    String getSymbol();
    int getPrecedence();
    default boolean isLeftAssociative() { return true; }
    default int getOperandCount() { return 2; }
    double apply(double... operands);
}
