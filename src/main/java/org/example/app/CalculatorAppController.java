package org.example.app;

import lombok.RequiredArgsConstructor;
import org.example.util.Constants;
import org.example.core.engine.CalculatorEngine;
import org.example.io.InputProvider;
import org.example.io.OutputProvider;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Orchestrates user interaction using injected IO providers.
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

        while (blankCount < Constants.EXIT_THRESHOLD) {
            String line = Optional.ofNullable(input.readLine())
                    .map(String::trim)
                    .orElse("");

            blankCount = processLine(line, buffer, blankCount);
        }

        output.println(Constants.EXIT_MESSAGE);
    }

    private void printWelcome() {
        Stream.of(
                "🧮 Java Calculator (Predictive Edition)",
                Constants.PROMPT_INPUT,
                ""
        ).forEach(output::println);
    }

    private int processLine(String line, StringBuilder buffer, int blankCount) {
        if (line.isEmpty()) return handleBlank(buffer, blankCount + 1);
        if (buffer.length() > 0) buffer.append(' ');
        buffer.append(line);
        return 0;
    }

    private int handleBlank(StringBuilder buffer, int count) {
        if (count == Constants.EVAL_THRESHOLD) evaluate(buffer);
        return count;
    }

    private void evaluate(StringBuilder buffer) {
        Optional.ofNullable(buffer.toString())
                .filter(expr -> !expr.isBlank())
                .ifPresentOrElse(
                        expr -> {
                            double result = engine.evaluate(expr);
                            printResult(result, expr);
                            buffer.setLength(0);
                        },
                        () -> output.error(Constants.ERROR_EMPTY)
                );
    }

    private void printResult(double result, String expression) {
        if (Double.isNaN(result))
            output.error("⚠️ Invalid or incomplete expression.");
        else
            output.println(String.format("%s %.4f   [%s]", Constants.RESULT_PREFIX, result, expression));
    }
}
