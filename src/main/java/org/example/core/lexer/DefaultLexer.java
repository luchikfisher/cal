package org.example.core.lexer;

import lombok.RequiredArgsConstructor;
import org.example.config.OperatorConfig;
import org.example.core.exception.LexicalException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * DefaultLexer — converts a mathematical expression into lexical tokens.
 * Performs only syntactic tokenization of valid characters.
 * No semantic or structural validation is performed here.
 */
@RequiredArgsConstructor
public class DefaultLexer implements Lexer {

    private static final String WHITESPACE_PATTERN = "\\s+";

    private final LexicalReader reader;

    @Override
    public List<String> tokenize(String expr) {
        return normalizeInput(expr)
                .map(this::scanTokens)
                .map(List::copyOf)
                .orElseGet(List::of);
    }

    /**
     * Normalizes the input by removing whitespace and converting blank results to Optional.empty().
     * No null values are ever returned.
     */
    private Optional<String> normalizeInput(String expr) {

        if (Objects.isNull(expr)) {
            return Optional.empty();
        }

        String normalized = expr.replaceAll(WHITESPACE_PATTERN, "");

        return normalized.isBlank()
                ? Optional.empty()
                : Optional.of(normalized);
    }

    private List<String> scanTokens(String expr) {
        List<String> tokens = new ArrayList<>();

        for (int i = 0; i < expr.length();) {
            int prevSize = tokens.size();
            i = processNextChar(expr, tokens, i);

            if (tokens.size() > prevSize) {
                insertImplicitMultiplication(tokens);
            }
        }

        return tokens;
    }

    private int processNextChar(String expr, List<String> tokens, int i) {
        char c = expr.charAt(i);

        if (!isRecognizedCharacter(c)) {
            throw new LexicalException("Unexpected character in expression: " + c);
        }

        if (Character.isLetter(c)) {
            return reader.readFunctionName(expr, tokens, i);
        }

        if (isParenthesis(c)) {
            tokens.add(String.valueOf(c));
            return i + 1;
        }

        if (Character.isDigit(c) || isUnarySignContext(tokens)) {
            return reader.readSignedFactor(expr, tokens, i);
        }

        if (isOperatorChar(c)) {
            tokens.add(String.valueOf(c));
            return i + 1;
        }

        return i + 1;
    }

    private boolean isRecognizedCharacter(char c) {
        return Character.isLetterOrDigit(c)
                || OperatorConfig.allowedOperatorChars().indexOf(c) >= 0
                || c == OperatorConfig.leftParen()
                || c == OperatorConfig.rightParen();
    }

    private boolean isParenthesis(char c) {
        return c == OperatorConfig.leftParen() || c == OperatorConfig.rightParen();
    }

    private boolean isOperatorChar(char c) {
        return OperatorConfig.allowedOperatorChars().indexOf(c) >= 0;
    }

    private boolean isUnarySignContext(List<String> tokens) {
        if (tokens.isEmpty()) {
            return true;
        }

        String last = tokens.get(tokens.size() - 1);

        return isOperatorChar(last.charAt(0))
                || last.equals(String.valueOf(OperatorConfig.leftParen()));
    }

    private void insertImplicitMultiplication(List<String> tokens) {
        if (tokens.size() < 2) {
            return;
        }

        String prev = tokens.get(tokens.size() - 2);
        String last = tokens.get(tokens.size() - 1);

        // ignore explicit operator cases
        if (isOperatorChar(last.charAt(0)) || last.equals(OperatorConfig.multiplyOperator())) {
            return;
        }

        // ignore parentheses that shouldn't trigger implicit multiplication
        if (prev.equals(String.valueOf(OperatorConfig.leftParen()))
                || last.equals(String.valueOf(OperatorConfig.rightParen()))) {
            return;
        }

        boolean prevIsValue =
                Character.isDigit(prev.charAt(0))
                        || Character.isLetter(prev.charAt(0))
                        || prev.equals(String.valueOf(OperatorConfig.rightParen()));

        boolean lastIsValue =
                Character.isLetter(last.charAt(0))
                        || Character.isDigit(last.charAt(0))
                        || last.equals(String.valueOf(OperatorConfig.leftParen()));

        if (prevIsValue && lastIsValue) {
            tokens.add(tokens.size() - 1, OperatorConfig.multiplyOperator());
        }
    }
}
