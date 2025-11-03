package org.example.operators;

public class AdditionOperator implements Operator {
    @Override public String getSymbol() { return "+"; }
    @Override public int getPrecedence() { return 2; }
    @Override public double apply(double... operands) { return operands[0] + operands[1]; }
}
