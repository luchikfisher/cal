package org.example.operators;

public class DivisionOperator implements Operator {
    @Override public String getSymbol() { return "/"; }
    @Override public int getPrecedence() { return 3; }
    @Override public boolean isLeftAssociative() { return true; }
    @Override public int getOperandCount() { return 2; }
    @Override public double apply(double... operands) {
        if (Math.abs(operands[1]) < 1e-10)
            throw new ArithmeticException("❌ Division by zero.");
        return operands[0] / operands[1];
    }
}
