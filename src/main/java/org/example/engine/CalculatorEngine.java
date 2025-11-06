package org.example.engine;

import lombok.RequiredArgsConstructor;
import org.example.operators.Operator;
import org.example.parser.ExpressionParser;
import org.example.operators.OperatorFactory;
import org.example.util.Validator;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Core evaluation logic — fully guard-based, never throws exceptions.
 */
@RequiredArgsConstructor
public class CalculatorEngine {

    private final ExpressionParser parser = new ExpressionParser(OperatorFactory.createDefaultRegistry());

    public double evaluate(String input) {
        return Optional.ofNullable(input)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(parser::parseExpression)
                .map(this::compute)
                .orElse(Double.NaN);
    }

    private double compute(List<String> rpn) {
        if (rpn == null || rpn.isEmpty()) return Double.NaN;

        Deque<Double> stack = new ArrayDeque<>();
        // Stream API במקום לולאה רגילה
        rpn.forEach(token -> processToken(stack, token));

        return finalizeResult(stack);
    }

    private void processToken(Deque<Double> stack, String token) {
        if (Validator.isValidNumber(token)) {
            stack.push(Double.parseDouble(token));
            return;
        }

        Optional.ofNullable(parser.getOperator(token))
                .ifPresent(op -> {
                    double[] args = popOperands(stack, op.getOperandCount());
                    stack.push(applySafe(op, args));
                });
    }

    private double[] popOperands(Deque<Double> stack, int count) {
        double[] args = new double[count];
        // שימוש תקין ב־Stream אינדקסי יורד לשמירה על סדר חישוב נכון
        IntStream.iterate(count - 1, i -> i >= 0, i -> i - 1)
                .forEach(i -> args[i] = stack.isEmpty() ? 0.0 : stack.pop());
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