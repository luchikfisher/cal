package org.example.operators;

public class SinOperator implements Operator {
    @Override public String getSymbol() { return "sin"; }
    @Override public int getPrecedence() { return 4; }
    @Override public double apply(double... operands) { return Math.sin(operands[0]); }
}
