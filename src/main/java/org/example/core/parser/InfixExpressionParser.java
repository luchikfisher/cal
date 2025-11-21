package org.example.core.parser;

import lombok.RequiredArgsConstructor;
import org.example.core.lexer.Lexer;

import java.util.List;
import java.util.Optional;

/**
 * RpnExpressionParser — converts infix expressions into RPN form.
 */
@RequiredArgsConstructor
public class InfixExpressionParser implements ExpressionParser {

    private final Lexer lexer;

    @Override
    public List<String> parse(String input) {
        return Optional.ofNullable(input)
                .filter(s -> !s.isBlank())
                .map(lexer::tokenize)
                .orElse(List.of());
    }
}
