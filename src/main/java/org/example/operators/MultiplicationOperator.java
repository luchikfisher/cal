package org.example.operators;

public class MultiplicationOperator implements Operator {
    @Override public String getSymbol() { return "*"; }
    @Override public int getPrecedence() { return 3; }
    @Override public boolean isLeftAssociative() { return true; }
    @Override public int getOperandCount() { return 2; }
    @Override public double apply(double... operands) { return operands[0] * operands[1]; }
}
