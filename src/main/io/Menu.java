package main.io;

import java.util.ArrayList;
import java.util.List;

import main.core.expansion.Expansion;
import main.core.expansion.ExpansionLoader;

/** Start menu that asks for expansions, players and mode; the base game (order 0) is always included (Req. 15). */
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
        out.println("=== Exploding Kittens ===");
        List<Expansion> expansions = new ArrayList<>();
        for (Expansion expansion : loader.available()) {
            if (expansion.order() == 0 || yes("Add the expansion \"" + expansion.name() + "\"?")) {
                expansions.add(expansion);
            }
        }
        int max = expansions.stream().mapToInt(Expansion::maxPlayers).max().orElse(5);
        out.println("How many human players? 0 = watch bots, 1 = just you, more = the others join over the network. (0-" + max + ")");
        int humans = in.readInt(0, max);
        int minBots = Math.max(0, 2 - humans);
        int maxBots = max - humans;
        int bots = minBots;
        if (maxBots > minBots) {
            out.println("How many bots? (" + minBots + "-" + maxBots + ")");
            bots = in.readInt(minBots, maxBots);
        }
        boolean online = humans > 1;
        int port = GameConfig.DEFAULT_PORT;
        if (online) {
            out.println("Port to listen on? (1024-65535, usually " + GameConfig.DEFAULT_PORT + ")");
            port = in.readInt(1024, 65535);
        }
        return new GameConfig(List.copyOf(expansions), humans, bots, online, port, GameConfig.DEFAULT_NOPE_WINDOW);
    }

    private boolean yes(String question) {
        out.println(question);
        out.showOptions(List.of("No", "Yes"));
        return in.readInt(0, 1) == 1;
    }
}
