package org.example.io.input;

import java.util.Scanner;

/**
 * Console-based input implementation.
 */
public class ConsoleInputProvider implements InputProvider {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String fetchInput() {
        System.out.print("> ");
        return scanner.nextLine();
    }
}