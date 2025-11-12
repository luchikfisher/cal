package org.example.core.tokenizer;

import lombok.RequiredArgsConstructor;
import org.example.io.output.OutputProvider;
import org.example.util.Constants;
import org.example.core.validation.Validator;

import java.util.*;

/**
 * TokenizerImpl — performs lexical analysis and splits expressions into tokens.
 */
@RequiredArgsConstructor
public class TokenizerImpl implements Tokenizer {

    private final Validator validator;
    private final OutputProvider output;

    @Override
    public List<String> tokenize(String expr) {
        return Optional.ofNullable(expr)
                .map(s -> s.replaceAll("\\s+", ""))
                .filter(s -> !s.isBlank())
                .map(s -> {
                    List<String> tokens = new ArrayList<>();
                    for (int i = 0; i < s.length(); ) {
                        int next = processChar(s, tokens, i);
                        if (next < 0) return List.<String>of();
                        i = next;
                        insertImplicitMultiplication(tokens);
                    }
                    return tokens;
                })
                .orElse(List.of());
    }

    private int processChar(String expr, List<String> tokens, int i) {
        char c = expr.charAt(i);
        String s = String.valueOf(c);

        if (!validator.isRecognizedCharacter(c)) {
            output.error("❌ Invalid token: " + s);
            return -1;
        }

        if (Character.isLetter(c)) return readFunction(expr, tokens, i);
        if (c == Constants.LEFT_PAREN.charAt(0) || c == Constants.RIGHT_PAREN.charAt(0)) {
            tokens.add(s);
            return i + 1;
        }
        if (Character.isDigit(c) || isUnary(tokens)) return readSignedTerm(expr, tokens, i);
        if (validator.isValidOperator(s)) {
            tokens.add(s);
            return i + 1;
        }
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
        if (tokens.isEmpty()) return true;
        String last = tokens.get(tokens.size() - 1);
        return validator.isValidOperator(last) || Constants.LEFT_PAREN.equals(last);
    }


    private int readFunction(String expr, List<String> tokens, int i) {
        int j = i;
        while (j < expr.length() && Character.isLetter(expr.charAt(j))) j++;
        String func = expr.substring(i, j);
        if (!validator.isValidOperator(func)) {
            output.error("❌ Invalid function: " + func);
            return -1;
        }
        tokens.add(func);
        return j;
    }

    private int readSignedTerm(String expr, List<String> tokens, int i) {
        int j = i, sign = 1;
        while (j < expr.length() && isSign(expr.charAt(j)))
            sign *= expr.charAt(j++) == '-' ? -1 : 1;

        if (j < expr.length()) {
            char next = expr.charAt(j);
            if (Character.isLetter(next)) return readSignedFunction(expr, tokens, j, sign);
            if (next == '(') return readUnaryParenthesis(tokens, sign, j);
        }
        return readSignedNumber(expr, tokens, j, sign);
    }

    private boolean isSign(char c) {
        return c == '+' || c == '-';
    }

    private int readSignedFunction(String expr, List<String> tokens, int j, int sign) {
        if (sign < 0) {
            tokens.add("0");
            tokens.add(Constants.MINUS_OPERATOR);
        }
        return readFunction(expr, tokens, j);
    }

    private int readUnaryParenthesis(List<String> tokens, int sign, int j) {
        if (sign < 0) {
            tokens.add("0");
            tokens.add(Constants.MINUS_OPERATOR);
        }
        tokens.add(Constants.LEFT_PAREN);
        return j + 1;
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
            throw new IllegalArgumentException("Invalid number format: " + numStr, e);
        }
        return j;
    }
}