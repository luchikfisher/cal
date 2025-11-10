package org.example.io;

/**
 * Defines the contract for any output target.
 */
public interface OutputProvider {
    void print(String message);
    void println(String message);
    void error(String message);
}
