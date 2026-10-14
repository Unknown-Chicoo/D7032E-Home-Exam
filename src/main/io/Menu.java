package main.io;

import main.core.expansion.ExpansionLoader;

/** Start menu that asks for expansions, players and mode (Req. 15). */
public final class Menu {
    private final ConsoleInput in;
    private final ConsoleOutput out;
    private final ExpansionLoader loader;

    public Menu(ConsoleInput in, ConsoleOutput out, ExpansionLoader loader) {
        this.in = in;
        this.out = out;
        this.loader = loader;
    }

    public GameConfig run() {
        throw new UnsupportedOperationException("TODO: ask for game settings (Req. 15)");
    }
}
