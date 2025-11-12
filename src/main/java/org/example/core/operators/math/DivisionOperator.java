package org.example.core.operators.math;

import org.example.core.operators.base.BinaryOperator;

public class DivisionOperator implements BinaryOperator {
    @Override
    public String getSymbol() { return "/"; }

    @Override
    public int getPrecedence() { return 3; }

    @Override
    public boolean isLeftAssociative() { return true; }

    @Override
    public int getOperandCount() { return 2; }

    @Override
    public double apply(double left, double right) {
        if (right == 0) throw new ArithmeticException("Division by zero");
        return left / right;
    }
}