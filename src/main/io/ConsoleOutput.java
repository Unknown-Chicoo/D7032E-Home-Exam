package main.io;

import java.io.PrintStream;
import java.util.List;

/** Writes text and numbered option lists to the console. */
public final class ConsoleOutput {
    private final PrintStream out;

    public ConsoleOutput(PrintStream out) {
        this.out = out;
    }

    public void println(String text) {
        out.println(text);
    }

    /** Prints the options numbered from 0, the number the player then types. */
    public void showOptions(List<String> options) {
        for (int i = 0; i < options.size(); i++) {
            out.println("  [" + i + "] " + options.get(i));
        }
    }
}
