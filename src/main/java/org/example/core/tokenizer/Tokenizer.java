package org.example.core.tokenizer;

import java.util.List;

/**
 * Tokenizer — interface for breaking an expression string into discrete tokens.
 */
public interface Tokenizer {
    List<String> tokenize(String expr);
}