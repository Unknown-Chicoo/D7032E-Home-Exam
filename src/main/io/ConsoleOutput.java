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
        throw new UnsupportedOperationException("TODO: print a line");
    }

    public void showOptions(List<String> options) {
        throw new UnsupportedOperationException("TODO: print numbered options");
    }
}
