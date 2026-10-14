package main.core.model;

/** A seat at the table: name, hand and whether still in the game. Knows nothing of its controller. */
public final class Player {
    private final String name;
    private final Hand hand;
    private boolean alive = true;

    public Player(String name) {
        this.name = name;
        this.hand = new Hand();
    }

    public String name() {
        return name;
    }

    public Hand hand() {
        return hand;
    }

    public boolean isAlive() {
        return alive;
    }

    public void eliminate() {
        throw new UnsupportedOperationException("TODO: mark the player as out of the game");
    }
}
