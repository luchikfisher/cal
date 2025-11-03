package org.example.parser;

import lombok.NoArgsConstructor;
import org.example.operators.*;
import org.example.util.Validator;

import java.util.*;

@NoArgsConstructor
public class ExpressionParser {

    private final Map<String, Operator> registry = Map.ofEntries(
            Map.entry("+", new AdditionOperator()),
            Map.entry("-", new SubtractionOperator()),
            Map.entry("*", new MultiplicationOperator()),
            Map.entry("/", new DivisionOperator()),
            Map.entry("sin", new SinOperator()),
            Map.entry("cos", new CosOperator())
    );

    public List<String> parseFlexible(String input) {
        if (input == null || input.isBlank()) return List.of();
        String expr = input.replaceAll("\\s+", ""); // remove spaces
        List<String> tokens = tokenize(expr);
        return toRPN(tokens);
    }

    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        int n = expr.length();

        for (int i = 0; i < n; ) {
            char c = expr.charAt(i);

            // --- Function names (sin, cos, etc.) ---
            if (Character.isLetter(c)) {
                int j = i + 1;
                while (j < n && Character.isLetter(expr.charAt(j))) j++;
                String func = expr.substring(i, j);
                tokens.add(func);
                i = j;
                continue;
            }

            // --- Parentheses ---
            if (c == '(' || c == ')') {
                tokens.add(String.valueOf(c));
                i++;
                continue;
            }

            // --- Number or Unary sign sequence ---
            if (Character.isDigit(c) || ((c == '+' || c == '-') && (i == 0 || isPreviousOperatorOrLeftParen(tokens)))) {
                int j = i;
                int minusCount = 0;

                // Count consecutive +/-
                while (j < n && (expr.charAt(j) == '+' || expr.charAt(j) == '-')) {
                    if (expr.charAt(j) == '-') minusCount++;
                    j++;
                }

                // Determine resulting sign
                int sign = (minusCount % 2 == 0) ? 1 : -1;

                // If next is parenthesis => unary minus before bracket e.g. "-(3+4)"
                if (j < n && expr.charAt(j) == '(') {
                    tokens.add(sign == 1 ? "+" : "-");
                    i = j;
                    continue;
                }

                // Now parse number after signs
                int startNum = j;
                while (j < n && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) j++;
                if (startNum == j)
                    throw new IllegalArgumentException("Invalid numeric sequence near index " + i);

                double num = Double.parseDouble(expr.substring(startNum, j));
                tokens.add(Double.toString(num * sign));
                i = j;
                continue;
            }

            // --- Operators ---
            if (registry.containsKey(String.valueOf(c))) {
                tokens.add(String.valueOf(c));
                i++;
                continue;
            }

            throw new IllegalArgumentException("Invalid character: " + c);
        }

        return tokens;
    }

    private boolean isPreviousOperatorOrLeftParen(List<String> tokens) {
        if (tokens.isEmpty()) return true;
        String last = tokens.get(tokens.size() - 1);
        return registry.containsKey(last) || last.equals("(");
    }

    private List<String> toRPN(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<Object> stack = new ArrayDeque<>();
        Object paren = new Object();

        for (String token : tokens) {
            if (Validator.isValidNumber(token)) {
                output.add(token);
                continue;
            }

            if (registry.containsKey(token)) {
                Operator o1 = registry.get(token);
                while (!stack.isEmpty() && stack.peek() instanceof Operator o2 &&
                        ((o1.isLeftAssociative() && o1.getPrecedence() <= o2.getPrecedence()) ||
                                (!o1.isLeftAssociative() && o1.getPrecedence() < o2.getPrecedence()))) {
                    output.add(o2.getSymbol());
                    stack.pop();
                }
                stack.push(o1);
                continue;
            }

            if (token.equals("(")) {
                stack.push(paren);
                continue;
            }

            if (token.equals(")")) {
                while (!stack.isEmpty() && stack.peek() != paren)
                    output.add(((Operator) stack.pop()).getSymbol());
                if (stack.isEmpty())
                    throw new IllegalArgumentException("Mismatched parentheses");
                stack.pop();
                continue;
            }

            // Functions like sin/cos
            if (registry.containsKey(token)) {
                stack.push(registry.get(token));
            } else if (Character.isLetter(token.charAt(0))) {
                stack.push(token);
            }
        }

        while (!stack.isEmpty()) {
            Object top = stack.pop();
            if (top == paren)
                throw new IllegalArgumentException("Mismatched parentheses");
            output.add(((Operator) top).getSymbol());
        }

        return output;
    }

    public Operator getOperator(String s) {
        return registry.get(s);
    }
}
