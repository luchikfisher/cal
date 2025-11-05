package org.example.operators;

public class CosOperator implements Operator {
    @Override public String getSymbol() { return "cos"; }
    @Override public int getPrecedence() { return 4; }
    @Override public boolean isLeftAssociative() { return false; }
    @Override public int getOperandCount() { return 1; }
    @Override public double apply(double... operands) { return Math.cos(operands[0]); }
}
