package org.example.core.strategy;

import lombok.RequiredArgsConstructor;
import org.example.core.operators.Operator;
import org.example.core.parser.DefaultExpressionParser;
import org.example.util.Validator;

import java.util.*;

/**
 * StandardCalculationStrategy — default algorithm for RPN evaluation.
 */
@RequiredArgsConstructor
public class StandardCalculationStrategy implements CalculationStrategy {

    private final DefaultExpressionParser parser;
    private final Validator validator;

    @Override
    public double compute(List<String> rpn, Deque<Double> stack) {
        if (rpn == null || rpn.isEmpty()) return Double.NaN;

        rpn.forEach(token -> processToken(stack, token));
        return finalizeResult(stack);
    }

    private void processToken(Deque<Double> stack, String token) {
        if (validator.isValidNumber(token)) {
            stack.push(Double.parseDouble(token));
            return;
        }

        parser.findOperator(token)
                .ifPresent(op -> {
                    double[] args = popOperands(stack, op.getOperandCount());
                    stack.push(applySafe(op, args));
                });
    }

    private double[] popOperands(Deque<Double> stack, int count) {
        double[] args = new double[count];
        for (int i = count - 1; i >= 0; i--) {
            args[i] = stack.isEmpty() ? 0.0 : stack.pop();
        }
        return args;
    }

    private double applySafe(Operator op, double[] args) {
        return Optional.ofNullable(op)
                .map(o -> {
                    try {
                        return o.apply(args);
                    } catch (Exception e) {
                        return Double.NaN;
                    }
                })
                .orElse(Double.NaN);
    }

    private double finalizeResult(Deque<Double> stack) {
        return Optional.ofNullable(stack.peek()).orElse(Double.NaN);
    }
}
