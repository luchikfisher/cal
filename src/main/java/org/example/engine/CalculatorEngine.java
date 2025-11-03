package org.example.engine;

import org.example.parser.ExpressionParser;
import org.example.operators.Operator;

import java.util.*;

/**
 * CalculatorEngine — unified evaluator for any input style.
 */
public class CalculatorEngine {

    private final ExpressionParser parser = new ExpressionParser();

    public double evaluate(String rawInput) {
        List<String> rpn = parser.parseFlexible(rawInput);
        if (rpn.isEmpty()) throw new IllegalArgumentException("Empty expression");

        Deque<Double> stack = new ArrayDeque<>();

        for (String token : rpn) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                Operator op = parser.getOperator(token);
                if (op == null)
                    throw new IllegalArgumentException("Unknown operator: " + token);

                int n = op.getOperandCount();
                if (stack.size() < n)
                    throw new IllegalArgumentException("Not enough operands for " + token);

                double[] args = new double[n];
                for (int i = n - 1; i >= 0; i--) args[i] = stack.pop();
                stack.push(op.apply(args));
            }
        }

        if (stack.size() != 1)
            throw new IllegalArgumentException("Invalid expression.");

        return stack.pop();
    }

    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
