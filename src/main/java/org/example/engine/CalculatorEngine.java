package org.example.engine;

import lombok.RequiredArgsConstructor;
import org.example.operators.Operator;
import org.example.parser.ExpressionParser;
import org.example.operators.OperatorFactory;
import org.example.util.Validator;

import java.util.*;

/**
 * Core evaluation logic — fully guard-based, never throws exceptions.
 */
@RequiredArgsConstructor
public class CalculatorEngine {

    private final ExpressionParser parser = new ExpressionParser(OperatorFactory.createDefaultRegistry());

    public double evaluate(String input) {
        if (input == null || input.isBlank()) return Double.NaN;
        List<String> rpn = parser.parseExpression(input);
        return compute(rpn);
    }

    private double compute(List<String> rpn) {
        if (rpn == null || rpn.isEmpty()) return Double.NaN;
        Deque<Double> stack = new ArrayDeque<>();
        for (String token : rpn) processToken(stack, token);
        return finalizeResult(stack);
    }

    private void processToken(Deque<Double> stack, String token) {
        if (Validator.isValidNumber(token)) { stack.push(Double.parseDouble(token)); return; }
        Operator op = parser.getOperator(token);
        if (op == null) return;
        double[] args = popOperands(stack, op.getOperandCount());
        stack.push(applySafe(op, args));
    }

    private double[] popOperands(Deque<Double> stack, int count) {
        double[] args = new double[count];
        for (int i = count - 1; i >= 0; i--) args[i] = stack.isEmpty() ? 0.0 : stack.pop();
        return args;
    }

    private double applySafe(Operator op, double[] args) {
        try { return op.apply(args); }
        catch (Exception e) { return Double.NaN; }
    }

    private double finalizeResult(Deque<Double> stack) {
        if (stack.isEmpty()) return Double.NaN;
        return stack.peek();
    }
}
