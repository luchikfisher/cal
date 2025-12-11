package org.example.core.lexer;

import org.example.core.exception.LexicalException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.example.config.OperatorConfig;
import org.example.core.operators.factory.OperatorFactory;

public final class RegexLexer implements Lexer {

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "(?<ILLEGAL>[^0-9a-zA-Z+\\-*/^().\\s])"
                    + "|(?<NUMBER>\\d+(?:\\.\\d+)?)"
                    + "|(?<FUNC>[a-zA-Z][a-zA-Z0-9]*)"
                    + "|(?<PLUS>\\+)"
                    + "|(?<MINUS>-)"
                    + "|(?<MULT>\\*)"
                    + "|(?<DIV>/)"
                    + "|(?<POW>\\^)"
                    + "|(?<LPAREN>\\()"
                    + "|(?<RPAREN>\\))"
    );

    private static final Set<String> FUNCTIONS = OperatorFactory.getFunctionNames();

    private static final Set<String> OPERATORS =
            Stream.concat(
                    OperatorFactory.getBinaryOperators().stream(),
                    OperatorFactory.getUnaryOperators().stream()
            ).collect(Collectors.toUnmodifiableSet());

    private static final String LEFT_PAREN = String.valueOf(OperatorConfig.leftParen());
    private static final String RIGHT_PAREN = String.valueOf(OperatorConfig.rightParen());

    @Override
    public List<String> tokenize(String expr) {
        return Optional.ofNullable(expr)
                .map(this::scanTokens)
                .map(List::copyOf)
                .orElse(List.of());
    }

    private List<String> scanTokens(String expr) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(expr);

        String last = null;

        while (matcher.find()) {

            String raw = extractToken(matcher)
                    .orElseThrow(() ->
                            new LexicalException("Unrecognized token: " + matcher.group()));

            String token = applyUnary(raw, last);

            if (needsImplicitMultiplication(last, token)) {
                tokens.add("*");
            }

            tokens.add(token);
            last = token;
        }
        System.out.println("TOKEN: " + tokens);
        return tokens;
    }

    private Optional<String> extractToken(Matcher m) {
        if (m.group("ILLEGAL") != null)
            throw new LexicalException("Illegal character: " + m.group("ILLEGAL"));
        if (m.group("NUMBER") != null) return Optional.of(m.group("NUMBER"));
        if (m.group("FUNC")   != null) return Optional.of(m.group("FUNC"));
        if (m.group("PLUS")   != null) return Optional.of("+");
        if (m.group("MINUS")  != null) return Optional.of("-");
        if (m.group("MULT")   != null) return Optional.of("*");
        if (m.group("DIV")    != null) return Optional.of("/");
        if (m.group("POW")    != null) return Optional.of("^");
        if (m.group("LPAREN") != null) return Optional.of("(");
        if (m.group("RPAREN") != null) return Optional.of(")");

        return Optional.empty();
    }

    // ==========================
    //       UNARY HANDLING
    // ==========================

    private String applyUnary(String token, String last) {
        return token; // no unary here
    }
    // ==========================
    //   IMPLICIT MULTIPLICATION
    // ==========================
    private boolean needsImplicitMultiplication(String prev, String curr) {

        if (isFunction(prev) && LEFT_PAREN.equals(curr)) {
            return false;
        }

        return isValue(prev) && isPrefixValue(curr);
    }

    private boolean isValue(String t) {
        if (t == null) return false;

        // unary is NEVER a value
        if ("u-".equals(t) || "u+".equals(t)) return false;

        return isNumber(t)
                || isIdentifier(t)
                || isFunction(t)
                || RIGHT_PAREN.equals(t);
    }

    private boolean isPrefixValue(String t) {
        if (t == null) return false;

        // unary is ALWAYS prefix-value
        if ("u-".equals(t) || "u+".equals(t)) return true;

        return isNumber(t)
                || isIdentifier(t)
                || isFunction(t)
                || LEFT_PAREN.equals(t);
    }

    // ==========================
    //   NUMBER / IDENTIFIER
    // ==========================
    private boolean isNumber(String t) {
        boolean seenDot = false;
        if (t == null) return false;

        for (char c : t.toCharArray()) {
            if (c == '.') {
                if (seenDot) return false;
                seenDot = true;
            } else if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean isIdentifier(String t) {
        if (t == null) return false;

        if ("u-".equals(t) || "u+".equals(t)) return false;

        return Character.isLetter(t.charAt(0));
    }

    private boolean isFunction(String t) {
        return t != null && FUNCTIONS.contains(t);
    }
}