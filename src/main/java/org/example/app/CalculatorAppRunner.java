package org.example.app;

import lombok.RequiredArgsConstructor;
import org.example.config.AppInfoConfig;
import org.example.config.CalculatorConfig;
import org.example.config.OperatorConfig;
import org.example.core.evaluation.CalculatorEngine;
import org.example.core.validation.ExpressionValidator;
import org.example.core.validation.ValidationResult;
import org.example.io.input.InputProvider;
import org.example.io.output.OutputProvider;

import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
public final class CalculatorAppRunner {

    private final InputProvider input;
    private final OutputProvider output;
    private final CalculatorEngine engine;
    private final ExpressionValidator validator;

    public void run() {
        printWelcome();

        StringBuilder buffer = new StringBuilder();
        int blankCount = 0;

        final int evalThreshold = CalculatorConfig.evalThreshold();
        final int exitThreshold = CalculatorConfig.exitThreshold();

        while (blankCount < exitThreshold) {
            String line = Optional.ofNullable(input.fetchInput())
                    .map(String::strip)
                    .orElse("");

            blankCount = handleInputLine(line, buffer, blankCount, evalThreshold);
        }

        output.write(CalculatorConfig.exitMessage());
    }

    // ===== Private Helpers =====

    private void printWelcome() {
        Stream.of(
                AppInfoConfig.appName() + " v" + AppInfoConfig.appVersion(),
                CalculatorConfig.promptInput(),
                ""
        ).forEach(output::write);
    }

    private int handleInputLine(String line, StringBuilder buffer, int blankCount, int evalThreshold) {
        if (line.isBlank()) {
            return handleEvaluationTrigger(buffer, blankCount + 1, evalThreshold);
        }

        if (buffer.length() > 0) buffer.append(' ');
        buffer.append(line);
        return 0;
    }

    private int handleEvaluationTrigger(StringBuilder buffer, int count, int evalThreshold) {
        if (count == evalThreshold) {
            String expr = buffer.toString().trim();
            buffer.setLength(0);
            processExpression(expr);
        }
        return count;
    }

    private void processExpression(String expr) {
        ValidationResult result = validator.validate(expr);
        if (!result.isValid()) {
            output.displayError(result.getMessage().orElse("Invalid expression"));
            return;
        }

        evaluateAndDisplay(expr);
    }

    private void evaluateAndDisplay(String expr) {
        try {
            double value = engine.evaluate(expr);
            handleResult(value, expr);
        } catch (ArithmeticException e) {
            output.displayError("[MATH] " + e.getMessage());
        } catch (IllegalArgumentException e) {
            output.displayError("[EVAL] " + e.getMessage());
        } catch (Exception e) {
            output.displayError("[UNEXPECTED] " + e.getMessage());
        }
    }

    private void handleResult(double result, String expression) {
        output.write(String.format(
                OperatorConfig.recordFormat(),
                CalculatorConfig.resultPrefix(),
                result,
                expression
        ));
    }
}