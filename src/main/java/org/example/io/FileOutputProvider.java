package org.example.io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * FileOutputProvider — writes output messages to a file.
 * Enables logging or exporting evaluation results.
 */
public class FileOutputProvider implements OutputProvider {

    private final PrintWriter writer;

    public FileOutputProvider(String filePath) throws IOException {
        this.writer = new PrintWriter(new FileWriter(filePath, true)); // append mode
    }

    @Override
    public void print(String message) {
        writer.print(message);
        writer.flush();
    }

    @Override
    public void println(String message) {
        writer.println(message);
        writer.flush();
    }

    @Override
    public void error(String message) {
        writer.println("❌ " + message);
        writer.flush();
    }

    public void close() {
        writer.close();
    }
}
