package org.example.io.output;

/**
 * Defines the contract for any output target.
 */
public interface OutputProvider {
    void write(String message);
    void displayError(String message);
}
