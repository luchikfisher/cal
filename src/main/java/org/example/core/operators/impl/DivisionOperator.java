package org.example.core.operators.impl;

import org.example.core.operators.base.BinaryOperator;

public class DivisionOperator implements BinaryOperator {
    @Override
    public String getSymbol() { return "/"; }

    @Override
    public int getPrecedence() { return 2; }

    @Override
    public boolean isLeftAssociative() { return true; }

    @Override
    public int getOperandCount() { return 2; }

    @Override
    public double apply(double left, double right) {
        return left / right;
    }
}