package org.example.core.validation;

import lombok.RequiredArgsConstructor;
import org.example.core.lexer.Lexer;
import org.example.core.operators.factory.OperatorFactory;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class FunctionCallValidator implements ExpressionValidator {

    private final Lexer lexer;
    private final Set<String> functions = OperatorFactory.getFunctionNames();

    @Override
    public boolean validate(String expr) {

        List<String> tokens = lexer.tokenize(expr);

        for (int i = 0; i < tokens.size(); i++) {
            String t = tokens.get(i);
            if (functions.contains(t)) {
                if (i == tokens.size() - 1 || !tokens.get(i + 1).equals("(")) {
                    return false;
                }
                if (i + 2 < tokens.size() && tokens.get(i + 2).equals(")")) {
                    return false;
                }
            }
        }

        return true;
    }
}