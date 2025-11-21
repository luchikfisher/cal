package org.example.core.lexer;

import lombok.RequiredArgsConstructor;
import org.example.config.OperatorConfig;
import org.example.core.exception.LexicalException;

import java.util.ArrayList;
import java.util.List;

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

        String normalized = normalizeInput(expr);
        if (normalized == null) {
            return List.of(); // empty expression → empty tokens
        }

        List<String> tokens = scanTokens(normalized);
        return List.copyOf(tokens); // defensive copy
    }

    private String normalizeInput(String expr) {
        if (expr == null) return null;
        String normalized = expr.replaceAll(WHITESPACE_PATTERN, "");
        return normalized.isBlank() ? null : normalized;
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

        if (Character.isLetter(c)) return reader.readFunctionName(expr, tokens, i);
        if (isParenthesis(c)) { tokens.add(String.valueOf(c)); return i + 1; }
        if (Character.isDigit(c) || isUnarySignContext(tokens)) return reader.readSignedFactor(expr, tokens, i);
        if (isOperatorChar(c)) { tokens.add(String.valueOf(c)); return i + 1; }

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
        if (tokens.isEmpty()) return true;
        String last = tokens.get(tokens.size() - 1);
        return isOperatorChar(last.charAt(0))
                || last.equals(String.valueOf(OperatorConfig.leftParen()));
    }

    private void insertImplicitMultiplication(List<String> tokens) {
        if (tokens.size() < 2) return;

        String prev = tokens.get(tokens.size() - 2);
        String last = tokens.get(tokens.size() - 1);

        // Ignore if last token is already an operator or multiplication
        if (isOperatorChar(last.charAt(0)) || last.equals(OperatorConfig.multiplyOperator())) {
            return;
        }

        // Cases that should not trigger implicit multiplication
        if (prev.equals(String.valueOf(OperatorConfig.leftParen())) ||
                last.equals(String.valueOf(OperatorConfig.rightParen()))) {
            return;
        }

        // Define what counts as a "value-like" token
        boolean prevIsValue = Character.isDigit(prev.charAt(0))
                || Character.isLetter(prev.charAt(0))
                || prev.equals(String.valueOf(OperatorConfig.rightParen()));

        boolean lastIsValue = Character.isLetter(last.charAt(0))
                || Character.isDigit(last.charAt(0))
                || last.equals(String.valueOf(OperatorConfig.leftParen()));

        // Insert implicit multiplication: e.g., 2(3), (2)(3), 2sin(x)
        if (prevIsValue && lastIsValue) {
            tokens.add(tokens.size() - 1, OperatorConfig.multiplyOperator());
        }
    }
}