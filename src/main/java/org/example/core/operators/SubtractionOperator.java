package org.example.core.operators;

public class SubtractionOperator implements Operator {
    @Override public String getSymbol() { return "-"; }
    @Override public int getPrecedence() { return 2; }
    @Override public boolean isLeftAssociative() { return true; }
    @Override public int getOperandCount() { return 2; }
    @Override public double apply(double... operands) { return operands[0] - operands[1]; }
}
