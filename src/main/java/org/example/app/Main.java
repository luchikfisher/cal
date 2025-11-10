package org.example.app;

import org.example.core.builder.CalculatorBuilder;
import org.example.core.engine.CalculatorEngine;
import org.example.io.ConsoleInputProvider;
import org.example.io.ConsoleOutputProvider;

public class Main {
    public static void main(String[] args) {
        CalculatorEngine engine = new CalculatorBuilder()
                // .validator(new DefaultValidator())
                //.parser(new DefaultExpressionParser(...))
                // .strategy(new StandardCalculationStrategy(...))
                .build();

        var input = new ConsoleInputProvider();
        var output = new ConsoleOutputProvider();

        new CalculatorAppController(input, output, engine).run();
    }
}
