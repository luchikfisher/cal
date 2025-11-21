package org.example.core.lexer;

import java.util.List;

/**
 * Lexer
 * -----
 * Responsible for lexical analysis — splitting a raw expression string
 * into discrete tokens (numbers, operators, parentheses, functions, etc.).
 *
 * The lexer does not perform validation or evaluation.
 */
public interface Lexer {
    List<String> tokenize(String expr);
}
