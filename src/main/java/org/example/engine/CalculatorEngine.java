package org.example.engine;

import org.example.parser.ExpressionParser;
import org.example.operators.Operator;
import org.example.util.Validator;

import java.util.*;

/**
 * CalculatorEngine — unified evaluator for mathematical expressions (RPN).
 */
public class CalculatorEngine {

    private final ExpressionParser parser = new ExpressionParser();

    public double evaluate(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            throw new IllegalArgumentException("Expression cannot be null or empty.");
        }

        List<String> rpn = parser.parseExpression(rawInput);
        if (rpn.isEmpty()) throw new IllegalArgumentException("Empty expression.");

        Deque<Double> stack = new ArrayDeque<>();

        for (String token : rpn) {
            if (Validator.isValidNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                Operator op = parser.getOperator(token);
                if (op == null)
                    throw new IllegalArgumentException("Unknown operator: " + token);

                int n = op.getOperandCount();
                if (stack.size() < n)
                    throw new IllegalArgumentException("Not enough operands for operator: " + token);

                double[] args = new double[n];
                for (int i = n - 1; i >= 0; i--) args[i] = stack.pop();

                try {
                    stack.push(op.apply(args));
                } catch (ArithmeticException ex) {
                    throw new ArithmeticException(ex.getMessage() + " near operator: " + token);
                }
            }
        }

        if (stack.size() != 1)
            throw new IllegalArgumentException("Invalid expression (remaining elements on stack).");

        return stack.pop();
    }
}
