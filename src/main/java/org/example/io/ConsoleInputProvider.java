package org.example.io;

import java.util.Scanner;

/**
 * Console-based input implementation.
 */
public class ConsoleInputProvider implements InputProvider {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String readLine() {
        System.out.print("> ");
        return scanner.nextLine();
    }
}
