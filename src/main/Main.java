package main;

/** Composition root: the only place that creates adapters and wires the game together. */
public final class Main {

    public static void main(String[] args) {
        // 1. Parse args: "client <host>" connects a GameClient and returns.
        // 2. Otherwise, Menu.run() gives a GameConfig.
        // 3. Accept remote clients through GameServer.
        // 4. Build controllers: ConsoleController, RemoteController, BotController.
        // 5. GameSetup.setUp(...).
        // 6. Build the EventBus and subscribe the views.
        // 7. Build DefaultGameContext and the resolvers.
        // 8. GameEngine.run().
        // 9. Close the channels.
        throw new UnsupportedOperationException("TODO: wire and start the game");
    }
}
