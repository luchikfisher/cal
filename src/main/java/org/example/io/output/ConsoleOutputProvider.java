package org.example.io.output;

/**
 * Console-based output implementation.
 */
public class ConsoleOutputProvider implements OutputProvider {
    @Override
    public void write(String message) {
        System.out.println(message);
    }

    @Override
    public void displayError(String message) {
        System.err.println(message);
    }
}
