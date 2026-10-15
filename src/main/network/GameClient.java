package main.network;

import java.io.IOException;
import java.time.Duration;

import main.io.ConsoleInput;
import main.io.ConsoleOutput;

/**
 * Thin client run on a remote player's machine: prints the server's text and sends back the chosen
 * index (Req. 17).
 */
public final class GameClient {
    private final MessageChannel channel;
    private final ConsoleInput in;
    private final ConsoleOutput out;

    /** Creates a client for an open connection to the server; {@link #run()} closes it. */
    public GameClient(MessageChannel channel, ConsoleInput in, ConsoleOutput out) {
        this.channel = channel;
        this.in = in;
        this.out = out;
    }

    /**
     * Runs until GAME_OVER, then closes the channel. A lost connection prints "Connection lost" and
     * returns normally.
     *
     * @throws IllegalStateException if the console input ends while a question waits for an answer
     */
    public void run() {
        try (channel) {
            while (true) {
                Message message = channel.receive();
                switch (message.type()) {
                    case EVENT -> out.println(message.text());
                    case ASK -> answer(message);
                    case ASK_TIMED -> answerWithin(message);
                    case GAME_OVER -> {
                        out.println(message.text());
                        return;
                    }
                    case ANSWER -> {
                    }
                }
            }
        } catch (IOException e) {
            out.println("Connection lost");
        }
    }

    private void answer(Message question) throws IOException {
        show(question);
        channel.send(Message.answer(question.id(), in.choose(question.options())));
    }

    /** Sends nothing if the time runs out; the server then treats it as "no". */
    private void answerWithin(Message question) throws IOException {
        show(question);
        int last = question.options().size() - 1;
        var choice = in.readLineWithin(Duration.ofSeconds(question.seconds()))
                .flatMap(line -> ConsoleInput.parseInt(line, 0, last));
        if (choice.isPresent()) {
            channel.send(Message.answer(question.id(), choice.get()));
        }
    }

    private void show(Message question) {
        out.println(question.text());
        out.showOptions(question.options());
        in.discardPending();
    }
}
