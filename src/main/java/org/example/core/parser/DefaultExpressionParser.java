package org.example.core.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.constants.Constants;
import org.example.core.operators.Operator;
import org.example.util.Validator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DefaultExpressionParser — current robust predictive parser implementation.
 */
@RequiredArgsConstructor
public class DefaultExpressionParser implements ExpressionParser {

    @Getter
    private final Map<String, Operator> registry;
    private final Validator validator;

    @Override
    public List<String> parseExpression(String input) {
        return Optional.ofNullable(input)
                .map(String::trim)
                .filter(expr -> !expr.isEmpty())
                .map(expr -> expr.replaceAll("\\s+", ""))
                .filter(validator::isValidExpression)
                .filter(this::isBalanced)
                .map(this::tokenize)
                .orElse(List.of());
    }

    @Override
    public Optional<Operator> findOperator(String symbol) {
        return Optional.ofNullable(registry.get(symbol));
    }

    // ===== existing logic preserved below =====
    private boolean isBalanced(String expr) {
        int depth = 0;
        for (char c : expr.toCharArray()) {
            if (c == Constants.LEFT_PAREN.charAt(0)) depth++;
            else if (c == Constants.RIGHT_PAREN.charAt(0)) depth--;
            if (depth < 0) return false;
        }
        return depth == 0;
    }

    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < expr.length(); ) {
            int next = processChar(expr, tokens, i);
            if (next < 0) return List.of();
            i = next;
            insertImplicitMultiplication(tokens);
        }
        return toRPN(tokens);
    }

    private int processChar(String expr, List<String> tokens, int i) {
        char c = expr.charAt(i);
        String s = String.valueOf(c);

        if (!isRecognizedCharacter(c)) {
            System.err.println("❌ Invalid token: " + s);
            return -1;
        }

        if (Character.isLetter(c)) return readFunction(expr, tokens, i);
        if (c == Constants.LEFT_PAREN.charAt(0) || c == Constants.RIGHT_PAREN.charAt(0))
            return addToken(tokens, s, i);
        if (Character.isDigit(c) || isUnary(tokens)) return readSignedTerm(expr, tokens, i);
        if (validator.isValidOperator(s)) return addToken(tokens, s, i);
        return i + 1;
    }

    private boolean isRecognizedCharacter(char c) {
        return Character.isLetterOrDigit(c) || "+-*/().".indexOf(c) >= 0;
    }

    private int addToken(List<String> tokens, String token, int i) {
        tokens.add(token);
        return i + 1;
    }

    private void insertImplicitMultiplication(List<String> tokens) {
        if (tokens.size() < 2) return;
        String prev = tokens.get(tokens.size() - 2);
        String last = tokens.get(tokens.size() - 1);
        boolean prevVal = validator.isValidNumber(prev) || Constants.RIGHT_PAREN.equals(prev);
        boolean lastVal = validator.isValidNumber(last)
                || Character.isLetter(last.charAt(0))
                || Constants.LEFT_PAREN.equals(last);
        if (prevVal && lastVal) tokens.add(tokens.size() - 1, Constants.MULTIPLY_OPERATOR);
    }

    private boolean isUnary(List<String> tokens) {
        return tokens.isEmpty()
                || Optional.of(tokens.get(tokens.size() - 1))
                .filter(t -> validator.isValidOperator(t) || Constants.LEFT_PAREN.equals(t))
                .isPresent();
    }

    private int readFunction(String expr, List<String> tokens, int i) {
        int j = i;
        while (j < expr.length() && Character.isLetter(expr.charAt(j))) j++;
        String func = expr.substring(i, j);
        if (!validator.isValidOperator(func)) {
            System.err.println("❌ Invalid function: " + func);
            return -1;
        }
        tokens.add(func);
        return j;
    }

    private int readSignedTerm(String expr, List<String> tokens, int i) {
        int j = i, sign = 1;
        while (j < expr.length() && (expr.charAt(j) == '+' || expr.charAt(j) == '-'))
            sign *= (expr.charAt(j++) == '-') ? -1 : 1;
        if (j < expr.length() && Character.isLetter(expr.charAt(j))) {
            if (sign < 0) {
                tokens.add("0");
                tokens.add(Constants.MINUS_OPERATOR);
            }
            return readFunction(expr, tokens, j);
        }
        return readSignedNumber(expr, tokens, j, sign);
    }

    private int readSignedNumber(String expr, List<String> tokens, int j, int sign) {
        int start = j;
        while (j < expr.length() && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) j++;
        if (start == j) return j;
        String numStr = expr.substring(start, j);
        try {
            double num = Double.parseDouble(numStr) * sign;
            tokens.add(Double.toString(num));
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid number: " + numStr);
            return -1;
        }
        return j;
    }

    private List<String> toRPN(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<Object> stack = new ArrayDeque<>();
        tokens.forEach(token -> processToken(token, output, stack));
        flushStack(stack, output);
        return output;
    }

    private void processToken(String token, List<String> out, Deque<Object> stack) {
        if (validator.isValidNumber(token)) {
            out.add(token);
            return;
        }
        if (Constants.LEFT_PAREN.equals(token)) {
            stack.push(token);
            return;
        }
        if (Constants.RIGHT_PAREN.equals(token)) {
            flushParentheses(stack, out);
            return;
        }

        Optional.ofNullable(registry.get(token))
                .ifPresent(op -> pushOperator(op, stack, out));
    }

    private void pushOperator(Operator op, Deque<Object> stack, List<String> out) {
        while (!stack.isEmpty() && stack.peek() instanceof Operator) {
            Operator o2 = (Operator) stack.peek();
            boolean left = op.isLeftAssociative() && op.getPrecedence() <= o2.getPrecedence();
            boolean right = !op.isLeftAssociative() && op.getPrecedence() < o2.getPrecedence();
            if (left || right) out.add(((Operator) stack.pop()).getSymbol());
            else break;
        }
        stack.push(op);
    }

    private void flushParentheses(Deque<Object> stack, List<String> out) {
        while (!stack.isEmpty() && stack.peek() instanceof Operator)
            out.add(((Operator) stack.pop()).getSymbol());
        if (!stack.isEmpty() && Constants.LEFT_PAREN.equals(stack.peek())) stack.pop();
    }

    private void flushStack(Deque<Object> stack, List<String> out) {
        out.addAll(stack.stream()
                .filter(Operator.class::isInstance)
                .map(op -> ((Operator) op).getSymbol())
                .collect(Collectors.toList()));
        stack.clear();
    }

}
