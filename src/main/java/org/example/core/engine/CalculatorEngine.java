package org.example.core.engine;

import lombok.RequiredArgsConstructor;
import org.example.core.parser.RpnConverter;
import org.example.core.parser.ExpressionParser;
import org.example.core.strategy.CalculationStrategy;

import java.util.*;

/**
 * CalculatorEngine — clean, safe, Java 11 compatible evaluation logic.
 */
@RequiredArgsConstructor
public class CalculatorEngine implements Engine {

    private final ExpressionParser parser;
    private final CalculationStrategy strategy;
    private final RpnConverter rpnConverter;

    public double evaluate(String input) {
        return Optional.ofNullable(input)
                .filter(s -> !s.isEmpty())
                .map(parser::parseExpression)
                .map(rpnConverter::toRpn)
                .map(this::compute)
                .orElse(Double.NaN);
    }

    private double compute(List<String> rpn) {
        Deque<Double> stack = new ArrayDeque<>();
        return Optional.ofNullable(rpn)
                .filter(list -> !list.isEmpty())
                .map(list -> strategy.compute(list, stack))
                .orElse(Double.NaN);
    }
}
