package org.example.io.output;

/**
 * Defines the contract for any output target.
 */
public interface OutputProvider {
    void println(String message);
    void error(String message);
}
