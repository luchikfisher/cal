package org.example.core.operators.impl;

import org.example.core.operators.base.UnaryOperator;

public final class UnaryMinusOperator implements UnaryOperator {

    private static final String SYMBOL = "u-";
    private static final int PRECEDENCE = 4; // Higher than * and /

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

    @Override
    public int getPrecedence() {
        return PRECEDENCE;
    }

    @Override
    public boolean isLeftAssociative() {
        return false;
    }

    @Override
    public int getOperandCount() {
        return 1;
    }

    @Override
    public double apply(double operand) {
        return -operand;
    }
}
