package org.example.core.operators.impl;

import org.example.core.operators.base.UnaryOperator;

public class SinOperator implements UnaryOperator {
    @Override
    public String getSymbol() { return "sin"; }

    @Override
    public int getPrecedence() { return 3; }

    @Override
    public boolean isLeftAssociative() { return false; }

    @Override
    public int getOperandCount() { return 1; }

    @Override
    public double apply(double operand) {
        return Math.sin(operand);
    }

    @Override
    public boolean isFunction() {
        return true;
    }

}