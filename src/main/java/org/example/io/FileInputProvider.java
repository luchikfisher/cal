package org.example.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * FileInputProvider — reads expressions line-by-line from a file.
 * Useful for batch evaluations or testing input redirection.
 */
public class FileInputProvider implements InputProvider {

    private final BufferedReader reader;

    public FileInputProvider(String filePath) throws IOException {
        this.reader = new BufferedReader(new FileReader(filePath));
    }

    @Override
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.err.println("❌ Error reading file input: " + e.getMessage());
            return null;
        }
    }
}
