package org.example.core.parser;

import lombok.RequiredArgsConstructor;
import org.example.config.OperatorConfig;
import org.example.core.operators.base.Operator;
import org.example.core.operators.factory.OperatorFactory;

import java.util.*;

@RequiredArgsConstructor
public class DefaultRpnParser implements RpnParser {

    private static final String LEFT_PAREN = String.valueOf(OperatorConfig.leftParen());
    private static final String RIGHT_PAREN = String.valueOf(OperatorConfig.rightParen());

    @Override
    public List<String> toRpn(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) return List.of();

        List<String> output = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();

        String prev = null;

        for (String token : tokens) {
            token = detectUnary(token, prev);
            handleToken(token, output, stack);
            prev = token;
        }

        drainStack(stack, output);
        return output;
    }

    private String detectUnary(String token, String prev) {
        if (!OperatorFactory.isUnaryCandidate(token)) {
            return token;
        }

        boolean unary =
                prev == null
                        || OperatorFactory.isOperator(prev)
                        || prev.equals("(");

        if (unary) {
            return token;
        }

        return token;
    }

    private void handleToken(String token, List<String> output, Deque<String> stack) {

        if (LEFT_PAREN.equals(token)) {
            stack.push(token);
            return;
        }

        if (RIGHT_PAREN.equals(token)) {
            drainUntilLeftParen(stack, output);
            return;
        }

        Optional<Operator> opOpt = OperatorFactory.get(token);
        if (opOpt.isPresent()) {
            Operator op = opOpt.get();

            // unary operator?
            if (op.getOperandCount() == 1) {
                stack.push(op.getSymbol());
                return;
            }

            pushOperatorWithPrecedence(op, stack, output);
            return;
        }

        // number
        output.add(token);
    }

    private void pushOperatorWithPrecedence(Operator op, Deque<String> stack, List<String> output) {
        while (!stack.isEmpty()) {
            Optional<Operator> topOpt = OperatorFactory.get(stack.peek());
            if (topOpt.isEmpty()) break;

            Operator top = topOpt.get();

            boolean shouldPop =
                    (op.isLeftAssociative() && top.getPrecedence() >= op.getPrecedence()) ||
                            (!op.isLeftAssociative() && top.getPrecedence() > op.getPrecedence());

            if (!shouldPop) break;

            output.add(stack.pop());
        }

        stack.push(op.getSymbol());
    }

    private void drainUntilLeftParen(Deque<String> stack, List<String> output) {
        while (!stack.isEmpty() && !LEFT_PAREN.equals(stack.peek())) {
            output.add(stack.pop());
        }

        if (!stack.isEmpty() && LEFT_PAREN.equals(stack.peek())) {
            stack.pop();
        }

        // if function on top → output it
        if (!stack.isEmpty()) {
            Optional<Operator> topOp = OperatorFactory.get(stack.peek());
            if (topOp.isPresent() && topOp.get().getOperandCount() == 1) {
                output.add(stack.pop());
            }
        }
    }

    private void drainStack(Deque<String> stack, List<String> output) {
        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }
    }
}
