package main.io;

import java.util.ArrayList;
import java.util.List;

import main.core.expansion.Expansion;
import main.core.expansion.ExpansionLoader;

/** Start menu that asks for expansions, players and mode (Req. 15). */
public final class Menu {
    private final ConsoleInput in;
    private final ConsoleOutput out;
    private final ExpansionLoader loader;

    /** Creates a menu that offers the expansions {@code loader} finds. */
    public Menu(ConsoleInput in, ConsoleOutput out, ExpansionLoader loader) {
        this.in = in;
        this.out = out;
        this.loader = loader;
    }

    /**
     * Asks the questions on the console, re-asking after invalid answers. Expansions with order 0 are
     * always included; the player count stays between 2 and the highest {@code maxPlayers()} of the
     * chosen expansions (rule 1).
     *
     * @throws IllegalStateException if the console input ends, or no expansions are installed
     */
    public GameConfig run() {
        out.println("=== Exploding Kittens ===");
        List<Expansion> available = loader.available();
        out.println("Expansions found:");
        available.stream().filter(e -> e.order() == 0).forEach(e -> out.println("  " + e.name() + " (always included)"));
        List<Expansion> addOns = available.stream().filter(e -> e.order() != 0).toList();
        if (addOns.isEmpty()) {
            out.println("No add-on expansions installed.");
        }
        addOns.forEach(e -> out.println("  " + e.name()));

        List<Expansion> expansions = new ArrayList<>();
        for (Expansion expansion : available) {
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
