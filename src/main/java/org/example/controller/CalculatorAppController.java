package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.constants.Constants;
import org.example.engine.CalculatorEngine;

import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;


@RequiredArgsConstructor
public class CalculatorAppController {

    private final Scanner scanner = new Scanner(System.in);
    private final CalculatorEngine engine = new CalculatorEngine();

    public void run() {
        printWelcome();
        StringBuilder buffer = new StringBuilder();
        int blankCount = 0;

        while (blankCount < Constants.EXIT_THRESHOLD) {
            System.out.print("> ");

            String line = Optional.ofNullable(scanner.nextLine())
                    .map(String::trim)
                    .orElse("");

            blankCount = processLine(line, buffer, blankCount);
        }

        System.out.println(Constants.EXIT_MESSAGE);
    }

    private void printWelcome() {
        Stream.of(
                "🧮 Java Calculator (Predictive Edition)",
                Constants.PROMPT_INPUT,
                ""
        ).forEach(System.out::println);
    }

    private int processLine(String line, StringBuilder buffer, int blankCount) {
        if (line.isEmpty()) {
            return handleBlank(buffer, blankCount + 1);
        }
        bufferAppend(buffer, line);
        return 0;
    }

    private void bufferAppend(StringBuilder buffer, String line) {
        if (buffer.length() > 0) buffer.append(' ');
        buffer.append(line);
    }

    private int handleBlank(StringBuilder buffer, int count) {
        if (count == Constants.EVAL_THRESHOLD) {
            evaluate(buffer);
        }
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
                        () -> System.err.println(Constants.ERROR_EMPTY)
                );
    }

    private void printResult(double result, String expression) {
        if (Double.isNaN(result)) {
            System.err.println("⚠️ Invalid or incomplete expression.");
        } else {
            System.out.printf("%s %.4f   [%s]%n", Constants.RESULT_PREFIX, result, expression);
        }
    }
}