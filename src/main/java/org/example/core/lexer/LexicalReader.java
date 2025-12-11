package org.example.core.lexer;

import org.example.config.OperatorConfig;
import java.util.List;
import java.util.regex.Pattern;

public final class LexicalReader {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");

    public int readFunctionName(String expr, List<String> tokens, int i) {
        int j = i;
        while (j < expr.length() && Character.isLetter(expr.charAt(j))) {
            j++;
        }
        tokens.add(expr.substring(i, j));
        return j;
    }

    public int readSignedFactor(String expr, List<String> tokens, int i) {

        int j = i;

        int sign = 1;
        while (j < expr.length() && isSign(expr.charAt(j))) {
            if (expr.charAt(j) == OperatorConfig.minusOperator().charAt(0)) {
                sign = -sign;
            }
            j++;
        }

        if (j < expr.length()) {

            char next = expr.charAt(j);

            if (Character.isDigit(next) || next == '.') {
                if (sign < 0) tokens.add("u-");
                return readUnsignedNumber(expr, tokens, j);
            }

            if (Character.isLetter(next)) {
                if (sign < 0) tokens.add("u-");
                return readFunctionName(expr, tokens, j);
            }

            if (next == OperatorConfig.leftParen()) {
                if (sign < 0) tokens.add("u-");
                tokens.add(String.valueOf(next));
                return j + 1;
            }
        }
        return j;
    }

    private int readUnsignedNumber(String expr, List<String> tokens, int j) {
        int start = j;

        while (j < expr.length()) {
            char c = expr.charAt(j);
            if (!Character.isDigit(c) && c != '.') break;
            j++;
        }

        tokens.add(expr.substring(start, j));
        return j;
    }

    private boolean isSign(char c) {
        return c == OperatorConfig.plusOperator().charAt(0)
                || c == OperatorConfig.minusOperator().charAt(0);
    }
}