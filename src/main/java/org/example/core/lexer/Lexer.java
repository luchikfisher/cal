package org.example.core.lexer;

import java.util.List;

public interface Lexer {
    List<String> tokenize(String expr);
}
