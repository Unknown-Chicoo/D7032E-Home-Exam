package main;

import java.io.IOException;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.core.engine.DefaultGameContext;
import main.core.engine.DrawResolver;
import main.core.engine.GameEngine;
import main.core.engine.GameSetup;
import main.core.engine.NopeResolver;
import main.core.engine.PlayResolver;
import main.core.events.EventBus;
import main.core.expansion.DeckFactory;
import main.core.expansion.Expansion;
import main.core.expansion.ExpansionLoader;
import main.core.model.Player;
import main.core.players.BotController;
import main.core.players.PlayerController;
import main.core.util.JavaRandomizer;
import main.core.util.Randomizer;
import main.io.ConsoleController;
import main.io.ConsoleInput;
import main.io.ConsoleOutput;
import main.io.ConsoleView;
import main.io.GameConfig;
import main.io.Menu;
import main.io.TextRenderer;
import main.network.GameClient;
import main.network.GameServer;
import main.network.MessageChannel;
import main.network.Message;
import main.network.RemoteController;
import main.network.SocketChannel;

/**
 * Composition root: the only place that creates adapters and wires the game together.
 * Usage: no arguments for the menu, {@code bots [count] [seed]} for a bots-only game,
 * {@code client <host> [port]} to join a hosted game.
 */
public final class Main {
    private static final Duration NOPE_WINDOW = Duration.ofSeconds(5);
    private static final int DEFAULT_PORT = 2048;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool(task -> {
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            return thread;
        });
        ConsoleOutput out = new ConsoleOutput(System.out);
        try {
            if (args.length > 0 && args[0].equals("bots")) {
                int count = args.length > 1 ? Integer.parseInt(args[1]) : 4;
                Randomizer random = args.length > 2 ? new JavaRandomizer(Long.parseLong(args[2])) : new JavaRandomizer();
                runBotsOnly(count, random, pool, out);
                return;
            }
            ConsoleInput in = new ConsoleInput(System.in, out);
            pool.submit(in::readLoop);
            if (args.length > 1 && args[0].equals("client")) {
                int port = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_PORT;
                runClient(args[1], port, in, out);
            } else {
                runFromMenu(new Menu(in, out, new ExpansionLoader()).run(), in, out, pool);
            }
        } catch (IOException e) {
            out.println("Network error: " + e.getMessage());
        } catch (IllegalStateException e) {
            out.println("Stopped: " + e.getMessage());
        } finally {
            pool.shutdownNow();
        }
    }

    private static void runBotsOnly(int count, Randomizer random, ExecutorService pool, ConsoleOutput out) {
        GameSetup.Result setup = new GameSetup(new DeckFactory(), random).setUp(new ExpansionLoader().available(),
                names("Bot ", 1, count));
        Map<Player, PlayerController> controllers = new LinkedHashMap<>();
        setup.state().players().forEach(p -> controllers.put(p, new BotController(random)));
        EventBus bus = new EventBus();
        bus.subscribe(new ConsoleView(out, new TextRenderer(), Optional.empty()));
        play(setup, controllers, bus, random, NOPE_WINDOW, pool);
    }

    private static void runClient(String host, int port, ConsoleInput in, ConsoleOutput out) throws IOException {
        out.println("Connecting to " + host + ":" + port + " ...");
        new GameClient(new SocketChannel(new Socket(host, port)), in, out).run();
    }

    /** Seat order: the local human (Player 1), then remote players (Player 2, ...), then bots. */
    private static void runFromMenu(GameConfig config, ConsoleInput in, ConsoleOutput out, ExecutorService pool)
            throws IOException {
        Randomizer random = new JavaRandomizer();
        TextRenderer renderer = new TextRenderer();
        int remotes = Math.max(0, config.humans() - 1);
        List<MessageChannel> channels = List.of();
        if (config.online()) {
            out.println("Waiting for " + remotes + " player(s): run \"java main.Main client <this host> "
                    + config.port() + "\" on their machines ...");
            channels = new GameServer(config.port()).acceptClients(remotes);
        }
        List<String> names = new ArrayList<>();
        if (config.humans() > 0) {
            names.add("Player 1");
        }
        names.addAll(names("Player ", 2, remotes));
        names.addAll(names("Bot ", 1, config.bots()));
        List<Expansion> expansions = config.expansions();
        GameSetup.Result setup = new GameSetup(new DeckFactory(), random).setUp(expansions, names);

        List<Player> seats = setup.state().players();
        EventBus bus = new EventBus();
        Map<Player, PlayerController> controllers = new LinkedHashMap<>();
        List<RemoteController> remoteControllers = new ArrayList<>();
        int seat = 0;
        if (config.humans() > 0) {
            Player you = seats.get(seat++);
            controllers.put(you, new ConsoleController(in, out, renderer));
            bus.subscribe(new ConsoleView(out, renderer, Optional.of(you)));
        } else {
            bus.subscribe(new ConsoleView(out, renderer, Optional.empty()));
        }
        for (MessageChannel channel : channels) {
            Player remote = seats.get(seat++);
            greet(channel, remote);
            RemoteController controller = new RemoteController(channel, remote, renderer, new BotController(random),
                    bus::publish);
            controllers.put(remote, controller);
            bus.subscribe(controller);
            remoteControllers.add(controller);
        }
        while (seat < seats.size()) {
            controllers.put(seats.get(seat++), new BotController(random));
        }
        play(setup, controllers, bus, random, config.nopeWindow(), pool);
        remoteControllers.forEach(remote -> remote.gameOver("Game over. Thanks for playing!"));
    }

    private static void greet(MessageChannel channel, Player player) {
        try {
            channel.send(Message.event("Connected. You are " + player.name() + "."));
        } catch (IOException e) {
            // the RemoteController notices on its first message and hands the seat to a bot
        }
    }

    private static List<String> names(String prefix, int first, int count) {
        List<String> names = new ArrayList<>();
        for (int i = first; i < first + count; i++) {
            names.add(prefix + i);
        }
        return names;
    }

    private static Player play(GameSetup.Result setup, Map<Player, PlayerController> controllers, EventBus bus,
            Randomizer random, Duration nopeWindow, ExecutorService pool) {
        DefaultGameContext ctx = new DefaultGameContext(setup.state(), controllers, bus, random);
        PlayResolver plays = new PlayResolver(setup.registry(), new NopeResolver(nopeWindow, pool));
        return new GameEngine(ctx, plays, new DrawResolver(setup.registry())).run();
    }
}
