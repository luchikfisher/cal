package org.example.app;

import org.example.core.evaluation.CalculatorEngine;
import org.example.core.evaluation.RpnEvaluationAlgorithm;
import org.example.core.lexer.Lexer;
import org.example.core.lexer.RegexLexer;
import org.example.core.parser.DefaultRpnParser;
import org.example.core.parser.ExpressionParser;
import org.example.core.parser.InfixExpressionParser;
import org.example.core.parser.RpnParser;
import org.example.core.validation.DefaultExpressionValidator;
import org.example.core.validation.ExpressionValidator;
import org.example.core.evaluation.EvaluationAlgorithm;
import org.example.io.input.ConsoleInputProvider;
import org.example.io.input.InputProvider;
import org.example.io.output.ConsoleOutputProvider;
import org.example.io.output.OutputProvider;

/**
 * CalculatorApplication — manual dependency wiring.
 * Builds and connects all components.
 */
public final class CalculatorApplication {

    public static void main(String[] args) {

        // ===== Core Layer =====

        Lexer lexer = new RegexLexer();
        ExpressionValidator validator = new DefaultExpressionValidator(lexer);
        RpnParser rpnParser = new DefaultRpnParser();
        ExpressionParser parser = new InfixExpressionParser(lexer);

        EvaluationAlgorithm algorithm = new RpnEvaluationAlgorithm();
        CalculatorEngine engine = new CalculatorEngine(parser, rpnParser, algorithm);

        // ===== IO Layer =====
        InputProvider input = new ConsoleInputProvider();
        OutputProvider output = new ConsoleOutputProvider();

        // ===== App Runner =====
        CalculatorAppRunner runner = new CalculatorAppRunner(input, output, engine, validator);
        runner.run();
    }
}
