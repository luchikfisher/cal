package org.example.core.parser;

import lombok.RequiredArgsConstructor;
import org.example.core.tokenizer.Tokenizer;
import org.example.core.validation.Validator;

import java.util.*;

/**
 * ExpressionParserImpl — parses tokens into RPN sequence.
 */
@RequiredArgsConstructor
public class ExpressionParserImpl implements ExpressionParser {

    private final Validator validator;
    private final Tokenizer tokenizer;

    @Override
    public List<String> parseExpression(String input) {
        return Optional.ofNullable(input)
                .filter(expr -> !expr.isBlank())
                .filter(validator::isValidExpression)
                .filter(validator::isBalancedParentheses)
                .map(tokenizer::tokenize)
                .orElse(List.of());
    }
}