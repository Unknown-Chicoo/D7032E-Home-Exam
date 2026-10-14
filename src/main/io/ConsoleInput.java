package main.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/** The single reader of the console input stream. */
public final class ConsoleInput {
    private final BufferedReader reader;

    public ConsoleInput(InputStream in) {
        this.reader = new BufferedReader(new InputStreamReader(in));
    }

    public String readLine() {
        throw new UnsupportedOperationException("TODO: read one line");
    }

    public int readInt(int min, int max) {
        throw new UnsupportedOperationException("TODO: read an int in [min, max], re-asking on bad input");
    }

    public int choose(List<String> options) {
        throw new UnsupportedOperationException("TODO: read the index of one of the options");
    }
}
