package org.example.core.validation;

import lombok.RequiredArgsConstructor;
import org.example.core.lexer.Lexer;
import org.example.core.operators.factory.OperatorFactory;

import java.util.List;

@RequiredArgsConstructor
public class OperatorPlacementValidator implements ExpressionValidator {

    private final Lexer lexer;

    @Override
    public boolean validate(String expr) {

        List<String> tokens = lexer.tokenize(expr);
        if (isBinary(tokens.get(0))) return false;

        if (isBinary(tokens.get(tokens.size() - 1))) return false;

        for (int i = 1; i < tokens.size(); i++) {
            String prev = tokens.get(i - 1);
            String curr = tokens.get(i);

            if (isBinary(prev) && isBinary(curr)) {
                return false;
            }
        }

        return true;
    }

    private boolean isBinary(String token) {
        return OperatorFactory.getBinaryOperators().contains(token);
    }
}