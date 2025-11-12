package org.example.core.operators.math;

import org.example.core.operators.base.UnaryOperator;

public class SinOperator implements UnaryOperator {
    @Override
    public String getSymbol() { return "sin"; }

    @Override
    public int getPrecedence() { return 4; }

    @Override
    public boolean isLeftAssociative() { return false; }

    @Override
    public int getOperandCount() { return 1; }

    @Override
    public double apply(double operand) {
        return Math.sin(operand);
    }
}