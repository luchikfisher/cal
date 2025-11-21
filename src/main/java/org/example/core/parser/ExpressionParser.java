package org.example.core.parser;

import java.util.List;

/**
 * Defines a generic contract for parsing mathematical expressions
 * into a structured or intermediate representation.
 */
public interface ExpressionParser {

    List<String> parse(String input);
}