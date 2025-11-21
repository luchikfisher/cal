package org.example.core.operators.impl;

import org.example.core.operators.base.UnaryOperator;

public class CosOperator implements UnaryOperator {
    @Override
    public String getSymbol() { return "cos"; }

    @Override
    public int getPrecedence() { return 3; }

    @Override
    public boolean isLeftAssociative() { return false; }

    @Override
    public int getOperandCount() { return 1; }

    @Override
    public double apply(double operand) {
        return Math.cos(operand);
    }
}