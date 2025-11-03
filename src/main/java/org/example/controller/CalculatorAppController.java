package org.example.controller;

import org.example.constants.Constants;
import org.example.engine.CalculatorEngine;

import java.util.Scanner;

/**
 * CalculatorAppController — unified stateful input controller.
 * Evaluates after 2 blank lines, exits after 3.
 */
public class CalculatorAppController {

    private final Scanner scanner = new Scanner(System.in);
    private final CalculatorEngine engine = new CalculatorEngine();

    private static final int EVAL_THRESHOLD = 2;
    private static final int EXIT_THRESHOLD = 3;

    public void run() {
        System.out.println("🧮 Unified Stateful Java Calculator (Phase A–C+)");
        System.out.println(Constants.PROMPT_INPUT);
        System.out.println();

        StringBuilder buffer = new StringBuilder();
        int blankCount = 0;

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();

            if (line == null) break;
            line = line.trim();

            if (line.isEmpty()) {
                blankCount++;

                if (blankCount == EVAL_THRESHOLD) {
                    evaluateBuffer(buffer);
                }
                if (blankCount >= EXIT_THRESHOLD) break;

            } else {
                // user typed something, reset blank counter
                blankCount = 0;

                // append space if needed
                if (buffer.length() > 0) buffer.append(' ');
                buffer.append(line);
            }
        }

        System.out.println(Constants.EXIT_MESSAGE);
    }

    private void evaluateBuffer(StringBuilder buffer) {
        if (buffer.length() == 0) {
            System.err.println(Constants.ERROR_EMPTY);
            return;
        }

        try {
            double result = engine.evaluate(buffer.toString());
            System.out.printf("%s %.8f   [%s]%n",
                    Constants.RESULT_PREFIX, result, buffer.toString());
        } catch (Exception e) {
            System.err.println("❌ " + e.getMessage());
        } finally {
            buffer.setLength(0); // reset buffer
        }
    }
}
