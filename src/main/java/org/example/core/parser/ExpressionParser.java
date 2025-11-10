package org.example.core.parser;

import org.example.core.operators.Operator;

import java.util.List;
import java.util.Optional;

/**
 * Defines contract for parsing mathematical expressions into RPN tokens.
 */
public interface ExpressionParser {
    List<String> parseExpression(String input);
    Optional<Operator> findOperator(String symbol);
}
