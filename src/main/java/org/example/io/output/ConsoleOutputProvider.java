package org.example.io.output;

/**
 * Console-based output implementation.
 */
public class ConsoleOutputProvider implements OutputProvider {
    @Override
    public void println(String message) {
        System.out.println(message);
    }

    @Override
    public void error(String message) {
        System.err.println(message);
    }
}
