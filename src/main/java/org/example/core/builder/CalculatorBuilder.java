package org.example.core.builder;

import org.example.core.engine.CalculatorEngine;
import org.example.core.operators.OperatorFactory;
import org.example.core.parser.DefaultExpressionParser;
import org.example.core.parser.ExpressionParser;
import org.example.core.strategy.CalculationStrategy;
import org.example.core.strategy.StandardCalculationStrategy;
import org.example.util.DefaultValidator;
import org.example.util.Validator;

/**
 * Builder for constructing a fully configured CalculatorEngine.
 */

public class CalculatorBuilder {

    private Validator validator;
    private ExpressionParser parser;
    private CalculationStrategy strategy;

    @SuppressWarnings("unused")
    public CalculatorBuilder withValidator(Validator validator) {
        this.validator = validator;
        return this;
    }

    @SuppressWarnings("unused")
    public CalculatorBuilder withParser(ExpressionParser parser) {
        this.parser = parser;
        return this;
    }

    @SuppressWarnings("unused")
    public CalculatorBuilder withStrategy(CalculationStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public CalculatorEngine build() {
        if (validator == null) validator = new DefaultValidator();
        if (parser == null) parser = new DefaultExpressionParser(OperatorFactory.getRegistry(), validator);
        if (strategy == null) strategy = new StandardCalculationStrategy((DefaultExpressionParser) parser, validator);
        return new CalculatorEngine(parser, strategy);
    }
}
