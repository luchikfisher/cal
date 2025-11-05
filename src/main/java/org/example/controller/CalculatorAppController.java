package org.example.controller;

import org.example.constants.Constants;
import org.example.engine.CalculatorEngine;

import java.util.Scanner;

/**
 * CalculatorAppController — unified stateful input controller.
 * Evaluates after two blank lines, exits after three.
 */
public class CalculatorAppController {

    private final Scanner scanner = new Scanner(System.in);
    private final CalculatorEngine engine = new CalculatorEngine();

    public void run() {
        System.out.println("🧮 Unified Stateful Java Calculator (Production)");
        System.out.println(Constants.PROMPT_INPUT);
        System.out.println();

        StringBuilder buffer = new StringBuilder();
        int blankCount = 0;
        boolean running = true;

        while (running) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if (line == null) {
                running = false;
                continue;
            }

            line = line.trim();

            if (line.isEmpty()) {
                blankCount++;

                if (blankCount == Constants.EVAL_THRESHOLD) {
                    evaluateBuffer(buffer);
                } else if (blankCount >= Constants.EXIT_THRESHOLD) {
                    running = false;
                }

            } else {
                blankCount = 0;
                if (!buffer.isEmpty()) buffer.append(' ');
                buffer.append(line);
            }
        }

        System.out.println(Constants.EXIT_MESSAGE);
    }

    private void evaluateBuffer(StringBuilder buffer) {
        if (buffer.isEmpty()) {
            System.err.println(Constants.ERROR_EMPTY);
            return;
        }

        try {
            double result = engine.evaluate(buffer.toString());
            System.out.printf("%s %.2f   [%s]%n",
                    Constants.RESULT_PREFIX, result, buffer);
        } catch (Exception e) {
            System.err.println("❌ " + e.getMessage());
        } finally {
            buffer.setLength(0);
        }
    }
}
