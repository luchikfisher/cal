package org.example;

import org.example.app.CalculatorAppController;
import org.example.core.engine.CalculatorEngine;
import org.example.core.parser.ExpressionParserImpl;
import org.example.core.parser.RpnConverterImpl;
import org.example.core.strategy.RpnCalculationStrategy;
import org.example.core.tokenizer.TokenizerImpl;
import org.example.core.validation.DefaultValidator;
import org.example.core.validation.Validator;
import org.example.core.tokenizer.Tokenizer;
import org.example.core.parser.RpnConverter;
import org.example.core.parser.ExpressionParser;
import org.example.core.strategy.CalculationStrategy;
import org.example.io.input.ConsoleInputProvider;
import org.example.io.input.InputProvider;
import org.example.io.output.ConsoleOutputProvider;
import org.example.io.output.OutputProvider;

public class Main {
    public static void main(String[] args) {

        final Validator validator = new DefaultValidator();
        final OutputProvider output = new ConsoleOutputProvider();
        final Tokenizer tokenizer = new TokenizerImpl(validator, output);
        final RpnConverter rpnConverter = new RpnConverterImpl(validator);
        final ExpressionParser parser = new ExpressionParserImpl(validator, tokenizer);
        final CalculationStrategy strategy = new RpnCalculationStrategy(validator);
        final CalculatorEngine engine = new CalculatorEngine(parser, strategy, rpnConverter);

        final InputProvider input = new ConsoleInputProvider();

        new CalculatorAppController(input, output, engine).run();
    }
}