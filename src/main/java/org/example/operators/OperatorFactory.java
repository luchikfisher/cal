package org.example.operators;

import org.example.constants.Constants;

import java.util.Map;

/**
 * OperatorFactory — central registry providing Operator instances.
 * Enables dependency inversion and clean extension of the operator set.
 */
public final class OperatorFactory {

    private OperatorFactory() {}

    public static Map<String, Operator> createDefaultRegistry() {
        return Map.ofEntries(
                Map.entry(Constants.PLUS_OPERATOR, new AdditionOperator()),
                Map.entry(Constants.MINUS_OPERATOR, new SubtractionOperator()),
                Map.entry(Constants.MULTIPLY_OPERATOR, new MultiplicationOperator()),
                Map.entry(Constants.DIVIDE_OPERATOR, new DivisionOperator()),
                Map.entry(Constants.SIN_OPERATOR, new SinOperator()),
                Map.entry(Constants.COS_OPERATOR, new CosOperator())
        );
    }
}
