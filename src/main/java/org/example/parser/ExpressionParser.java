package org.example.parser;

import lombok.RequiredArgsConstructor;
import org.example.operators.Operator;
import org.example.util.Validator;

import java.util.*;

/**
 * ExpressionParser — strict mathematical parser using Dijkstra's Shunting Yard algorithm.
 *
 * Rules:
 *  - Requires parentheses for all functions (sin(x), cos(x)).
 *  - Supports unary + and - before numbers and functions.
 *  - Fully supports nested functions (sin(cos(-3+5))).
 *  - Throws explicit syntax errors for invalid input.
 */
@RequiredArgsConstructor
public class ExpressionParser {

    private final Map<String, Operator> registry;

    /**
     * Converts an infix mathematical expression into Reverse Polish Notation (RPN).
     */
    public List<String> parseExpression(String input) {
        if (input == null || input.isBlank()) return List.of();
        String expr = input.replaceAll("\\s+", "");
        List<String> tokens = tokenize(expr);
        validateParenthesesBalance(tokens);
        return toRPN(tokens);
    }

    /**
     * Splits the expression into tokens (numbers, operators, parentheses, functions).
     * Handles unary +/-, nested parentheses, and required parentheses for functions.
     */
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

                if (!registry.containsKey(func))
                    throw new IllegalArgumentException("Unknown function: " + func);

                if (j >= n || expr.charAt(j) != '(')
                    throw new IllegalArgumentException("Missing parentheses after function '" + func + "'");

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

            // --- Numbers or unary signs (+/-) ---
            if (Character.isDigit(c) || ((c == '+' || c == '-') && (i == 0 || isPreviousOperatorOrLeftParen(tokens)))) {
                int j = i;
                int minusCount = 0;

                // Count consecutive +/- signs
                while (j < n && (expr.charAt(j) == '+' || expr.charAt(j) == '-')) {
                    if (expr.charAt(j) == '-') minusCount++;
                    j++;
                }

                int sign = (minusCount % 2 == 0) ? 1 : -1;

                // Unary +/- before function or parentheses
                if (isUnaryBeforeFunctionOrParen(expr, j)) {
                    if (sign == -1) {
                        tokens.add("0");
                        tokens.add("-");
                    }
                    i = j;
                    continue;
                }

                // Parse numeric literal
                int startNum = j;
                while (j < n && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) j++;
                if (startNum == j)
                    throw new IllegalArgumentException("Invalid numeric sequence near index " + i);

                double num = Double.parseDouble(expr.substring(startNum, j));
                tokens.add(Double.toString(num * sign));
                i = j;
                continue;
            }

            // --- Binary operators ---
            if (registry.containsKey(String.valueOf(c))) {
                tokens.add(String.valueOf(c));
                i++;
                continue;
            }

            throw new IllegalArgumentException("Invalid character in expression: '" + c + "'");
        }

        return tokens;
    }

    /**
     * Converts infix tokens into postfix (RPN) using Shunting Yard algorithm.
     */
    private List<String> toRPN(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<Object> stack = new ArrayDeque<>();

        for (String token : tokens) {
            if (Validator.isValidNumber(token)) {
                output.add(token);
                continue;
            }

            if (registry.containsKey(token)) {
                Operator o1 = registry.get(token);
                while (!stack.isEmpty()
                        && stack.peek() instanceof Operator
                        && shouldPop((Operator) stack.peek(), o1)) {
                    output.add(((Operator) stack.pop()).getSymbol());
                }
                stack.push(o1);
                continue;
            }

            if ("(".equals(token)) {
                stack.push("(");
                continue;
            }

            if (")".equals(token)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    Object top = stack.pop();
                    if (!(top instanceof Operator))
                        throw new IllegalArgumentException("Unexpected token near ')'");
                    output.add(((Operator) top).getSymbol());
                }
                if (stack.isEmpty())
                    throw new IllegalArgumentException("Mismatched parentheses.");
                stack.pop(); // remove '('
                continue;
            }

            throw new IllegalArgumentException("Unexpected token: " + token);
        }

        while (!stack.isEmpty()) {
            Object top = stack.pop();
            if ("(".equals(top))
                throw new IllegalArgumentException("Mismatched parentheses at end of expression.");
            output.add(((Operator) top).getSymbol());
        }

        return output;
    }

    /**
     * Ensures the number of opening and closing parentheses is balanced.
     */
    private void validateParenthesesBalance(List<String> tokens) {
        int balance = 0;
        for (String t : tokens) {
            if ("(".equals(t)) balance++;
            if (")".equals(t)) balance--;
            if (balance < 0)
                throw new IllegalArgumentException("Mismatched parentheses: unexpected ')'");
        }
        if (balance != 0)
            throw new IllegalArgumentException("Mismatched parentheses: missing ')'");
    }

    /**
     * Determines if the operator o2 on the stack should be popped before o1.
     */
    private boolean shouldPop(Operator o2, Operator o1) {
        return (o1.isLeftAssociative() && o1.getPrecedence() <= o2.getPrecedence())
                || (!o1.isLeftAssociative() && o1.getPrecedence() < o2.getPrecedence());
    }

    /**
     * Determines if the current position points to a unary +/- before a function or parentheses.
     */
    private boolean isUnaryBeforeFunctionOrParen(String expr, int j) {
        return j < expr.length() && (Character.isLetter(expr.charAt(j)) || expr.charAt(j) == '(');
    }

    /**
     * Determines if the previous token was an operator or '(',
     * so the next +/- is treated as unary.
     */
    private boolean isPreviousOperatorOrLeftParen(List<String> tokens) {
        if (tokens.isEmpty()) return true;
        String last = tokens.get(tokens.size() - 1);
        return registry.containsKey(last) || "(".equals(last);
    }

    public Operator getOperator(String symbol) {
        return registry.get(symbol);
    }
}
