package org.example.core.evaluation;

import lombok.RequiredArgsConstructor;
import org.example.core.operators.base.BinaryOperator;
import org.example.core.operators.base.Operator;
import org.example.core.operators.base.UnaryOperator;
import org.example.core.operators.factory.OperatorFactory;

import java.util.Deque;

@RequiredArgsConstructor
public class RpnEvaluationAlgorithm implements EvaluationAlgorithm {

    @Override
    public double execute(TokenIterator tokens, Deque<Double> stack) {
        if (tokens == null || !tokens.hasNext()) return Double.NaN;

        while (tokens.hasNext()) {
            processToken(stack, tokens.next());
        }

        return finalizeResult(stack);
    }

    private void processToken(Deque<Double> stack, String token) {
        if (OperatorFactory.getRegistry().containsKey(token)) {
            Operator op = OperatorFactory.getRegistry().get(token);
            double[] args = popOperands(stack, op.getOperandCount());
            stack.push(applyOperator(op, args));
            return;
        }

        try {
            stack.push(Double.parseDouble(token));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid token in RPN: " + token);
        }
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