package org.example.app;

import lombok.RequiredArgsConstructor;
import org.example.config.ConfigurationManager;
import org.example.core.engine.CalculatorEngine;
import org.example.io.input.InputProvider;
import org.example.io.output.OutputProvider;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Orchestrates user interaction using injected IO providers.
 * Configuration-driven for flexible behavior and localization.
 */
@RequiredArgsConstructor
public class CalculatorAppController {

    private final InputProvider input;
    private final OutputProvider output;
    private final CalculatorEngine engine;

    public void run() {
        printWelcome();

        StringBuilder buffer = new StringBuilder();
        int blankCount = 0;

        int evalThreshold = ConfigurationManager.getInt("calculator.eval.threshold");
        int exitThreshold = ConfigurationManager.getInt("calculator.exit.threshold");

        while (blankCount < exitThreshold) {
            String line = Optional.ofNullable(input.readLine())
                    .map(String::trim)
                    .orElse("");

            blankCount = processLine(line, buffer, blankCount, evalThreshold);
        }

        output.println(ConfigurationManager.get("calculator.exit.message"));
    }

    private void printWelcome() {
        Stream.of(
                "🧮 " + ConfigurationManager.get("app.name"),
                ConfigurationManager.get("calculator.prompt.input"),
                ""
        ).forEach(output::println);
    }

    private int processLine(String line, StringBuilder buffer, int blankCount, int evalThreshold) {
        if (line.isEmpty()) return handleBlank(buffer, blankCount + 1, evalThreshold);
        if (buffer.length() > 0) buffer.append(' ');
        buffer.append(line);
        return 0;
    }

    private int handleBlank(StringBuilder buffer, int count, int evalThreshold) {
        if (count == evalThreshold) {
            evaluate(buffer.toString());
            buffer.setLength(0);
        }
        return count;
    }

    private void evaluate(String expr) {
        if (expr == null || expr.isBlank()) {
            output.error(ConfigurationManager.get("calculator.error.empty"));
            return;
        }

        try {
            double result = engine.evaluate(expr);
            printResult(result, expr);
        } catch (Exception e) {
            output.error(ConfigurationManager.get("calculator.error.unexpected") + " " + e.getMessage());
        }
    }

    private void printResult(double result, String expression) {
        if (Double.isNaN(result))
            output.error(ConfigurationManager.get("calculator.error.invalid"));
        else
            output.println(String.format(
                    "%s %.4f   [%s]",
                    ConfigurationManager.get("calculator.result.prefix"),
                    result,
                    expression
            ));
    }
}
