package org.example.parser;

import lombok.NoArgsConstructor;
import org.example.constants.Constants;
import org.example.operators.*;
import org.example.util.Validator;

import java.util.*;

/**
 * ExpressionParser — unified mathematical parser using Shunting Yard algorithm.
 * Supports unary +/-, functions (sin, cos), and nested parentheses.
 */
@NoArgsConstructor
public class ExpressionParser {

    private final Map<String, Operator> registry = Map.ofEntries(
            Map.entry(Constants.PLUS_OPERATOR, new AdditionOperator()),
            Map.entry(Constants.MINUS_OPERATOR, new SubtractionOperator()),
            Map.entry(Constants.MULTIPLY_OPERATOR, new MultiplicationOperator()),
            Map.entry(Constants.DIVIDE_OPERATOR, new DivisionOperator()),
            Map.entry(Constants.SIN_OPERATOR, new SinOperator()),
            Map.entry(Constants.COS_OPERATOR, new CosOperator())
    );

    /**
     * Converts an infix expression string into Reverse Polish Notation (RPN).
     */
    public List<String> parseExpression(String input) {
        if (input == null || input.isBlank()) return List.of();
        String expr = input.replaceAll("\\s+", "");
        List<String> tokens = tokenize(expr);
        return toRPN(tokens);
    }

    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        int n = expr.length();

        for (int i = 0; i < n; ) {
            char c = expr.charAt(i);

            // --- Function names ---
            if (Character.isLetter(c)) {
                int j = i + 1;
                while (j < n && Character.isLetter(expr.charAt(j))) j++;
                tokens.add(expr.substring(i, j));
                i = j;
                continue;
            }

            // --- Parentheses ---
            if (c == '(' || c == ')') {
                tokens.add(String.valueOf(c));
                i++;
                continue;
            }

            // --- Number or unary sign ---
            if (Character.isDigit(c) || ((c == '+' || c == '-') && (i == 0 || isPreviousOperatorOrLeftParen(tokens)))) {
                int j = i, minusCount = 0;

                while (j < n && (expr.charAt(j) == '+' || expr.charAt(j) == '-')) {
                    if (expr.charAt(j) == '-') minusCount++;
                    j++;
                }

                int sign = (minusCount % 2 == 0) ? 1 : -1;

                if (j < n && expr.charAt(j) == '(') {
                    tokens.add(sign == 1 ? "+" : "-");
                    i = j;
                    continue;
                }

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
        return registry.containsKey(last) || "(".equals(last);
    }

    private List<String> toRPN(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<Object> stack = new ArrayDeque<>();

        for (String token : tokens) {
            if (Validator.isValidNumber(token)) {
                output.add(token);
                continue;
            }

            // Unified operator/function handling
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

            if ("(".equals(token)) {
                stack.push("(");
                continue;
            }

            if (")".equals(token)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek()))
                    output.add(((Operator) stack.pop()).getSymbol());
                if (stack.isEmpty())
                    throw new IllegalArgumentException("Mismatched parentheses.");
                stack.pop();
                continue;
            }

            throw new IllegalArgumentException("Unexpected token: " + token);
        }

        while (!stack.isEmpty()) {
            Object top = stack.pop();
            if ("(".equals(top))
                throw new IllegalArgumentException("Mismatched parentheses.");
            output.add(((Operator) top).getSymbol());
        }

        return output;
    }

    public Operator getOperator(String s) {
        return registry.get(s);
    }
}
