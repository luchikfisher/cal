package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.constants.Constants;
import org.example.engine.CalculatorEngine;

import java.util.Scanner;

/**
 * Controller layer — handles user interaction with safe, predictive flow.
 */
@RequiredArgsConstructor
public class CalculatorAppController {

    private final Scanner scanner = new Scanner(System.in);
    private final CalculatorEngine engine = new CalculatorEngine();

    public void run() {
        printWelcome();
        StringBuilder buffer = new StringBuilder();
        int blankCount = 0;
        boolean running = true;

        while (running) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            blankCount = processLine(line, buffer, blankCount);
            running = blankCount < Constants.EXIT_THRESHOLD;
        }
        System.out.println(Constants.EXIT_MESSAGE);
    }

    private void printWelcome() {
        System.out.println("🧮 Java Calculator (Predictive Edition)");
        System.out.println(Constants.PROMPT_INPUT);
        System.out.println();
    }

    private int processLine(String line, StringBuilder buffer, int blankCount) {
        if (line.isEmpty()) return handleBlank(buffer, blankCount + 1);
        bufferAppend(buffer, line);
        return 0;
    }

    private void bufferAppend(StringBuilder buffer, String line) {
        if (!buffer.isEmpty()) buffer.append(' ');
        buffer.append(line);
    }

    private int handleBlank(StringBuilder buffer, int count) {
        if (count == Constants.EVAL_THRESHOLD) evaluate(buffer);
        return count;
    }

    private void evaluate(StringBuilder buffer) {
        if (buffer.isEmpty()) {
            System.err.println(Constants.ERROR_EMPTY);
            return;
        }
        double result = engine.evaluate(buffer.toString());
        printResult(result, buffer);
        buffer.setLength(0);
    }

    private void printResult(double result, StringBuilder buffer) {
        if (Double.isNaN(result))
            System.err.println("⚠️ Invalid or incomplete expression.");
        else
            System.out.printf("%s %.4f   [%s]%n", Constants.RESULT_PREFIX, result, buffer);
    }
}
