package org.example.io;

/**
 * Console-based output implementation.
 */
public class ConsoleOutputProvider implements OutputProvider {
    @Override
    public void print(String message) {
        System.out.print(message);
    }

    @Override
    public void println(String message) {
        System.out.println(message);
    }

    @Override
    public void error(String message) {
        System.err.println(message);
    }
}
