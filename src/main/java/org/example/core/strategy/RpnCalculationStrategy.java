package org.example.core.strategy;

import lombok.RequiredArgsConstructor;
import org.example.core.operators.base.BinaryOperator;
import org.example.core.operators.base.Operator;
import org.example.core.operators.base.UnaryOperator;
import org.example.core.operators.factory.OperatorFactory;
import org.example.core.validation.Validator;

import java.util.*;

/**
 * RpnCalculationStrategy — pure RPN evaluation logic (no I/O or try/catch).
 */
@RequiredArgsConstructor
public class RpnCalculationStrategy implements CalculationStrategy {

    private final Validator validator;

    @Override
    public double compute(List<String> rpn, Deque<Double> stack) {
        if (rpn == null || rpn.isEmpty()) return Double.NaN;

        for (String token : rpn) {
            processToken(stack, token);
        }
        return finalizeResult(stack);
    }

    private void processToken(Deque<Double> stack, String token) {
        if (validator.isValidNumber(token)) {
            stack.push(Double.parseDouble(token));
            return;
        }

        OperatorFactory.get(token).ifPresent(op -> {
            double[] args = popOperands(stack, op.getOperandCount());
            stack.push(applyOperator(op, args));
        });
    }

    private double[] popOperands(Deque<Double> stack, int count) {
        if (stack.size() < count) {
            throw new IllegalStateException("Insufficient operands for operator");
        }
        double[] args = new double[count];
        for (int i = count - 1; i >= 0; i--) {
            args[i] = stack.pop();
        }
        return args;
    }

    private double applyOperator(Operator op, double[] args) {
        if (op instanceof BinaryOperator) {
            BinaryOperator binary = (BinaryOperator) op;
            return binary.apply(args[0], args[1]);
        }
        if (op instanceof UnaryOperator) {
            UnaryOperator unary = (UnaryOperator) op;
            return unary.apply(args[0]);
        }
        throw new UnsupportedOperationException("Unknown operator type: " + op.getClass().getSimpleName());
    }

    private double finalizeResult(Deque<Double> stack) {
        return stack.isEmpty() ? Double.NaN : stack.peek();
    }
}