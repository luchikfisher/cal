package org.example.parser;

import lombok.RequiredArgsConstructor;
import org.example.operators.Operator;
import org.example.util.Validator;
import java.util.*;

/**
 * ExpressionParser — predictive, guard-based Shunting-Yard parser.
 * Supports implicit multiplication, unary signs, signed functions, nested parentheses.
 */
@RequiredArgsConstructor
public class ExpressionParser {

    private final Map<String, Operator> registry;

    public List<String> parseExpression(String input) {
        if (input == null || input.isBlank()) return List.of();
        String expr = input.replaceAll("\\s+", "");
        if (!isBalanced(expr)) return List.of();
        return toRPN(tokenize(expr));
    }

    private boolean isBalanced(String expr) {
        int open = 0;
        for (char c : expr.toCharArray()) {
            if (c == '(') open++;
            if (c == ')') open--;
            if (open < 0) return false;
        }
        return open == 0;
    }

    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < expr.length();) {
            char c = expr.charAt(i);
            String curr = String.valueOf(c);

            if (Character.isLetter(c)) i = readFunction(expr, tokens, i);
            else if (c == '(' || c == ')') { tokens.add(curr); i++; }
            else if (Character.isDigit(c) || isUnary(tokens)) i = readSignedNumberOrFunction(expr, tokens, i);
            else if (registry.containsKey(curr)) { tokens.add(curr); i++; }
            else i++;

            insertImplicitMultiplication(tokens);
        }
        return tokens;
    }

    private void insertImplicitMultiplication(List<String> tokens) {
        if (tokens.size() < 2) return;
        String last = tokens.get(tokens.size() - 1);
        String prev = tokens.get(tokens.size() - 2);

        boolean prevIsVal = Validator.isValidNumber(prev) || ")".equals(prev);
        boolean lastIsVal = Character.isLetter(last.charAt(0)) || "(".equals(last) || Validator.isValidNumber(last);

        if (prevIsVal && lastIsVal) tokens.add(tokens.size() - 1, "*");
    }

    private boolean isUnary(List<String> tokens) {
        if (tokens.isEmpty()) return true;
        String last = tokens.get(tokens.size() - 1);
        return registry.containsKey(last) || "(".equals(last);
    }

    private int readFunction(String expr, List<String> tokens, int i) {
        int j = i + 1;
        while (j < expr.length() && Character.isLetter(expr.charAt(j))) j++;
        String func = expr.substring(i, j);
        if (registry.containsKey(func) && j < expr.length() && expr.charAt(j) == '(') tokens.add(func);
        return j;
    }

    private int readSignedNumberOrFunction(String expr, List<String> tokens, int i) {
        int j = countSigns(expr, i), sign = getSign(expr, i, j);
        if (isNextFunction(expr, j)) { handleSignedFunction(tokens, sign); return readFunction(expr, tokens, j); }
        return readSignedNumber(expr, tokens, j, sign);
    }

    private int countSigns(String expr, int i) {
        int j = i; while (j < expr.length() && (expr.charAt(j) == '+' || expr.charAt(j) == '-')) j++; return j;
    }

    private int getSign(String expr, int i, int j) {
        int minus = 0; for (int k = i; k < j; k++) if (expr.charAt(k) == '-') minus++; return (minus % 2 == 0) ? 1 : -1;
    }

    private boolean isNextFunction(String expr, int j) {
        return j < expr.length() && Character.isLetter(expr.charAt(j));
    }

    private void handleSignedFunction(List<String> tokens, int sign) {
        if (sign == -1) { tokens.add("0"); tokens.add("-"); }
    }

    private int readSignedNumber(String expr, List<String> tokens, int j, int sign) {
        int start = j;
        while (j < expr.length() && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) j++;
        if (start == j) return j;
        try { double num = Double.parseDouble(expr.substring(start, j)) * sign; tokens.add(Double.toString(num)); }
        catch (Exception ignored) {}
        return j;
    }

    private List<String> toRPN(List<String> tokens) {
        List<String> out = new ArrayList<>(); Deque<Object> stack = new ArrayDeque<>();
        for (String token : tokens) processToken(token, out, stack);
        flushRemaining(stack, out); return out;
    }

    private void processToken(String token, List<String> out, Deque<Object> stack) {
        if (Validator.isValidNumber(token)) { out.add(token); return; }
        if ("(".equals(token)) { stack.push("("); return; }
        if (")".equals(token)) { flushParentheses(stack, out); return; }
        Operator op = registry.get(token); if (op != null) pushOperator(op, stack, out);
    }

    private void pushOperator(Operator o1, Deque<Object> stack, List<String> out) {
        while (!stack.isEmpty() && stack.peek() instanceof Operator o2 &&
                ((o1.isLeftAssociative() && o1.getPrecedence() <= o2.getPrecedence()) ||
                        (!o1.isLeftAssociative() && o1.getPrecedence() < o2.getPrecedence())))
            out.add(((Operator) stack.pop()).getSymbol());
        stack.push(o1);
    }

    private void flushParentheses(Deque<Object> stack, List<String> out) {
        while (!stack.isEmpty() && stack.peek() instanceof Operator)
            out.add(((Operator) stack.pop()).getSymbol());
        if (!stack.isEmpty() && "(".equals(stack.peek())) stack.pop();
    }

    private void flushRemaining(Deque<Object> stack, List<String> out) {
        while (!stack.isEmpty() && stack.peek() instanceof Operator)
            out.add(((Operator) stack.pop()).getSymbol());
    }

    public Operator getOperator(String s) { return registry.get(s); }
}
