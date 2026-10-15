package main.core.model;

/** A seat at the table: name, hand and whether still in the game. Compared by identity. */
public final class Player {
    private final String name;
    private final Hand hand;
    private boolean alive = true;

    /** Creates a living player with an empty hand. */
    public Player(String name) {
        this.name = name;
        this.hand = new Hand();
    }

    /** The name shown to everyone, e.g. "Bot 2". */
    public String name() {
        return name;
    }

    /** The player's cards. */
    public Hand hand() {
        return hand;
    }

    /** Whether the player has not exploded yet. */
    public boolean isAlive() {
        return alive;
    }

    /** Marks the player as exploded; they take no more turns (rule 10). Cannot be undone. */
    public void eliminate() {
        alive = false;
    }
}
