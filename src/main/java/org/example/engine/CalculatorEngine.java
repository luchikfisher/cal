package org.example.engine;

import org.example.operators.Operator;
import org.example.operators.OperatorFactory;
import org.example.parser.ExpressionParser;
import org.example.util.Validator;

import java.util.*;

/**
 * CalculatorEngine — core evaluator for mathematical expressions.
 * Uses an injected ExpressionParser (DIP-compliant).
 * Converts infix to RPN and evaluates deterministically.
 */
public class CalculatorEngine {

    private final ExpressionParser parser;

    /**
     * Default constructor using the standard operator registry.
     * (Convenience for normal use — Dependency Injection still respected.)
     */
    public CalculatorEngine() {
        this(new ExpressionParser(OperatorFactory.createDefaultRegistry()));
    }

    /**
     * Dependency-injectable constructor (for tests or custom registries).
     */
    public CalculatorEngine(ExpressionParser parser) {
        this.parser = Objects.requireNonNull(parser, "Parser cannot be null");
    }

    /**
     * Evaluates a full mathematical expression string and returns the numeric result.
     *
     * @param rawInput expression string (e.g., "2 + sin(3)")
     * @return computed double value
     * @throws IllegalArgumentException for syntax or semantic errors
     * @throws ArithmeticException      for numeric errors (e.g., division by zero)
     */
    public double evaluate(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            throw new IllegalArgumentException("Expression cannot be null or empty.");
        }

        final List<String> rpn = parser.parseExpression(rawInput);
        if (rpn.isEmpty()) {
            throw new IllegalArgumentException("Empty expression after parsing.");
        }

        final Deque<Double> stack = new ArrayDeque<>();

        for (String token : rpn) {
            processToken(stack, token);
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException(
                    "Invalid expression structure. Remaining elements on stack: " + stack.size());
        }

        return stack.pop();
    }

    /**
     * Processes a single token (number, operator, or function) in RPN order.
     * Extracted for readability, testability, and SRP compliance.
     */
    private void processToken(Deque<Double> stack, String token) {
        if (Validator.isValidNumber(token)) {
            stack.push(Double.parseDouble(token));
            return;
        }

        final Operator op = getOperator(token);
        final int operandsRequired = op.getOperandCount();

        if (stack.size() < operandsRequired) {
            throw new IllegalArgumentException("Not enough operands for operator: " + token);
        }

        final double[] args = new double[operandsRequired];
        for (int i = operandsRequired - 1; i >= 0; i--) {
            args[i] = stack.pop();
        }

        try {
            double result = op.apply(args);
            stack.push(result);
        } catch (ArithmeticException e) {
            throw new ArithmeticException(
                    "Arithmetic error near operator '" + token + "': " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Evaluation failed at operator '" + token + "': " + e.getMessage());
        }
    }

    /**
     * Encapsulated operator lookup to avoid tight coupling with parser internals.
     */
    private Operator getOperator(String symbol) {
        final Operator op = parser.getOperator(symbol);
        if (op == null) {
            throw new IllegalArgumentException("Unknown operator or function: " + symbol);
        }
        return op;
    }
}
