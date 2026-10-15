# Exploding Kittens refactor (D7032E home exam 2026)

## Background

- I am refactoring `original/ExplodingKittens.java` (a god class) into the architecture already
  scaffolded under `src/main/...`.
- I must defend every design decision at an oral exam. So keep the architecture as it is,
  explain non-obvious choices briefly, and ask before changing a package, an interface or a
  dependency.

## Working rules

- Work in the phase I name, and only that phase. Finish it by doing the following:
  - compile;
  - from phase 6 on, run the bots-only game and check that it ends with a winner;
  - check the new code against "Design principles" below;
  - stop and summarise what you did and why, in at most 10 bullets, including any SOLID or
    Booch trade-offs.
- **Packages:** all packages start with `main.`, e.g. `main.core.model`.
- **Dependency rules:** these must not be broken.
  - `core.util` imports nothing.
  - `core.model` imports only `util`.
  - `core.events` imports `model`.
  - `core.players` imports `model` and `util`.
  - `core.cards` imports `model`, `players`, `events` and `util`.
  - `core.expansion` imports `cards` and `model`.
  - `core.engine` may import all of `core`.
  - `expansions.*` may import `core.*` except `engine`.
  - `io` imports only core abstractions.
  - `network` imports core abstractions plus `io`.
  - Nothing in `core` imports `expansions`, `io` or `network`.
- **Forbidden in game code:**
  - static mutable state;
  - `new Random()`: use the injected `Randomizer`;
  - `System.out` or `System.in` outside `io` and `Main`;
  - `System.exit` outside `Main`.
- **Javadoc:** every public type gets 1–3 lines of Javadoc stating its responsibility and,
  where relevant, the rule or requirement it covers (e.g. "Rule 11a").
- **Testability:** no tests are written for now; they come once the structure is final. Keep
  the code easy to test:
  - every decision goes through `PlayerController`, so a scripted fake controller can drive
    any rule;
  - randomness comes only from the injected `Randomizer` (seed it with `JavaRandomizer(seed)`);
  - the Nope window is an injected `Duration`, so tests can use e.g. 50 ms instead of 5 s;
  - `RemoteController` only sees a `MessageChannel`, so tests can use an in-memory channel
    instead of real sockets.
  - When tests are added: JUnit 5 in `test/`, mirroring the packages, with names that cite the
    rule (e.g. `rule11a_attackGivesNextPlayerTwoTurns`). The runner is
    `lib/junit-platform-console-standalone-1.14.4.jar` (a Test-scope library in IntelliJ);
    `./run-tests.sh` compiles everything and runs all tests from the command line.

## Design principles

All code must follow SOLID and Booch's metrics. Before finishing a phase, check the new code
against both lists. In the phase summary, name any place where a principle is under tension
and the trade-off you made.

### SOLID

- **S – Single Responsibility:** a class has one reason to change.
  - Rules don't print, views don't decide rules, and the model holds state, not card
    behaviour.
  - One effect per class, one resolver per rule procedure.
- **O – Open/Closed:** open for extension, closed for modification.
  - New cards, combos, expansions, controllers and listeners are added as new classes and
    registrations.
  - Never add an `if`/`switch` on a concrete card type or expansion in `core`.
- **L – Liskov Substitution:** any implementation must work wherever its interface is
  expected.
  - Any `PlayerController` in any seat, any `CardEffect` in the registry, any
    `MessageChannel` under `RemoteController`.
  - No `instanceof` checks on implementations, and no method that throws "not supported".
- **I – Interface Segregation:** keep interfaces small and client-specific.
  - Effects see `GameContext`, not the engine. Controllers see `GameView`, not `GameState`.
  - Keep `CardEffect` and `DrawEffect` separate, rather than one interface with methods left
    empty.
- **D – Dependency Inversion:** depend on abstractions.
  - `core` depends on `CardType`, `Expansion`, `PlayerController`, `GameEventListener`,
    `MessageChannel` and `Randomizer`. (`GameContext` is a concrete class: decision 21.)
  - Concrete classes are created with `new` only in `Main`, `GameSetup` or the expansions.
  - Dependencies come in through constructors.

### Booch's metrics

- **Coupling (low):** classes and packages depend on as little as possible, and only
  downwards: no package cycles, and follow the dependency rules above.
  - Pass the narrowest type: a `GameView` rather than `GameState`, a `Player` rather than an
    index.
- **Cohesion (high):** everything in a class or package serves one purpose. If a method
  doesn't fit the class's Javadoc sentence, it belongs elsewhere.
- **Sufficiency:** a class offers enough of its abstraction to be useful. For example, `Deck`
  supports draw, insert, peek and shuffle, so effects don't need to reach into its list.
- **Completeness:** an interface covers every operation its clients reasonably need.
  `PlayerController` covers every decision the rules require; `choose(Question)` covers new
  ones.
- **Primitiveness:** each operation does one basic thing that can't be built efficiently from
  the others.
  - No convenience methods that just combine other public methods, and no "manager"
    methods doing several jobs.
  - Don't create an interface that has only one implementation and no planned second one;
    the resolvers stay concrete classes.

## Game rules (from the exam)

1. 2–5 players. (Expansions may raise the maximum; use the highest `maxPlayers()` among the
   selected expansions.)
2. Each player gets 1 Defuse.
3. The deck before dealing contains:
   - 2 Defuse for 2–4 players, or 1 Defuse for 5 players;
   - 4 Attack, 4 Favor, 5 Nope, 4 Shuffle, 4 Skip, 5 SeeTheFuture;
   - 4 each of HairyPotatoCat, Cattermelon, RainbowRalphingCat, TacoCat and
     OverweightBikiniCat.
4. Shuffle.
5. Deal 7 cards to each player.
6. Insert (players − 1) Exploding Kittens.
7. Shuffle.
8. A random player starts.
9. On your turn, play any number of cards, one play at a time. Pass means draw, which ends the
   turn.
10. Drawing an Exploding Kitten:
    - If you have a Defuse, discard it and put the kitten back anywhere you choose.
    - Otherwise you explode: discard your whole hand and the kitten, and take no more turns.
11. Card effects:
    - **a. Attack:** ends your turn without drawing. The next player takes 2 turns. If an
      attacked player plays Attack, the following player takes their remaining turns + 2
      (4, 6, …).
    - **b. Favor:** the target gives you 1 card of their choice.
    - **c. Shuffle:** shuffles the deck.
    - **d. Skip:** ends one turn without drawing. Under attack, it skips only one of the
      turns.
    - **e. SeeTheFuture:** you see the top 3 cards; the view is private.
    - **f. Two of a kind:** steal a random card from the target.
    - **g. Three of a kind:** name a card type; you get it from the target if they have it.
12. Nope can be played within 5 seconds of any play from rule 11 to stop it. A Nope on a Nope
    flips it back (Yup).
13. The last player alive wins, and everyone is told.

## Design decisions made after the scaffold

Apply these where the scaffold differs.

1. **`GameEvent`** is a plain (non-sealed) interface.
   - Methods: `String describe()` and `default Optional<Player> privateTo()`.
   - Expansions may define their own event records.
2. **`TurnState`** has only primitive operations; the rules live in the effects.
   - Operations: `current()`, `turnsLeft()`, `turnNumber()`, `endTurn()`,
     `passTo(Player, int)`, `reverse()`, `direction()`, `playerEliminated(Player)`,
     `nextAlive(Player)`.
   - `turnNumber` increments whenever a turn ends. `GameEngine` loops
     `while (turns.turnNumber() == turnAtStart && player.isAlive())`.
   - `AttackEffect` computes the stacking itself:
     - normal attack: `passTo(next, 2)`;
     - if the attacker was itself under attack with `turnsLeft > 1`:
       `passTo(next, turnsLeft + 2)`.
     - This follows rule 11a's example (2 → 4 → 6). The earlier formula `turnsLeft − 1 + 2`
       gave 2 → 3 → 4 and was dropped.
3. **Combos.** There is no `ComboRule` interface and no `SameTypeCombo` class.
   - `CardRegistry.registerCombo(Predicate<List<Card>> matches, CardEffect effect)` stores each
     combo as a private record in a `List`, checked in registration order.
   - `CardRegistry` has three collections:
     - `Map<CardType, CardEffect>` for play effects;
     - `List<Combo>` (the private record), checked in registration order;
     - `Map<CardType, DrawEffect>` for draw effects.
   - `register` and `registerOnDraw` throw if the same type is registered twice.
   - `decorateOnDraw(CardType, UnaryOperator<DrawEffect>)` lets a later expansion wrap an
     existing draw effect.
   - `effectFor(play)`:
     1. With exactly one card and a play effect for its type, return that effect.
     2. Otherwise return the effect of the first combo whose predicate matches.
     3. Otherwise throw `InvalidMoveException(message)`.
   - `BaseGame` has a private static helper `sameType(int n)` that matches exactly n cards of
     the same type, of any type, not only cats (rule 11f/g). It registers
     `registerCombo(sameType(2), new TwoOfAKindEffect())` and
     `registerCombo(sameType(3), new ThreeOfAKindEffect())`.
4. **`InvalidMoveException`** lives in `core.cards` and has a message constructor.
5. **`PlayerController`** gets a general method for questions from expansions:
   `int choose(GameView, Question)`, with `record Question(String prompt, List<String> options)`
   in `core.players`.
6. **No `Clock`.** Delete `Clock` and `SystemClock`.
   - `NopeResolver(Duration window, ExecutorService pool)`.
   - `NopeOffer(Player actor, Play play, boolean currentlyNoped, Duration timeLeft)`.
7. **Nope algorithm:**
   - Loop in rounds.
   - Eligible players are alive players holding a Nope, excluding whoever played the last
     card or Nope.
   - If nobody is eligible, return at once.
   - Ask everyone eligible in parallel, then wait until all have answered or the window
     passes. A late or failed answer counts as "no".
   - Of those who said yes, the first in turn order after the actor wins (decision 22): that
     player's Nope goes to the discard pile, flip `noped`, publish `PlayNoped(by, play, noped)`
     and run another round.
   - If nobody said yes, return `noped`.
   - Only the game thread mutates state. Workers only call `wantsToNope` with a `GameView`
     snapshot.
8. **`Hand`** queries by type only through predicates: `containsMatching(Predicate<CardType>)`
   and `removeFirstMatching(Predicate<CardType>)`.
   - There are no `CardType` overloads (primitiveness: they are built from the predicates).
   - There is no `count`: nothing called it (controllers see `GameView`, not `Hand`), and it is
     just `cards()` filtered and counted, so it was removed (primitiveness).
   - Call sites pass `DEFUSE::equals` or `CardType::isNope`.
9. **`CardType` traits:** `isNope()`, `isNopeable()`, `isCat()` (used by bots). Later:
   `isVisibleInDeck()`.
10. **`Expansion`:**
    - Add `default void adjustKittens(List<Card> kittens, int players) {}`.
    - Add `default int order() { return 0; }`. The base game is 0 and add-ons 10;
      `GameSetup` sorts by it.
    - The menu always includes the base game.
11. **`GameSetup`** follows rules 1–8 in order and returns `Result(GameState, CardRegistry)`.
    - The deck cards come from `DeckFactory`.
    - Deal 7 cards each.
12. **Network:**
    - `enum MessageType { EVENT, ASK, ASK_TIMED, ANSWER, GAME_OVER }`.
    - `record Message(MessageType type, int id, String text, List<String> options, int answerIndex, int seconds)`.
    - `RemoteController` discards any `ANSWER` whose id doesn't match the open question.
    - The client never parses text: it prints, picks an index and sends it.
13. **`io.TextRenderer`** is shared by `ConsoleView`, `ConsoleController` and
    `RemoteController`.
    - It turns events and `GameView`s into text and applies `privateTo`.
    - Unknown events are printed with `describe()`.
14. **`ConsoleInput`** reads stdin into a `BlockingQueue` and offers `readLineWithin(Duration)`
    for Nope prompts.
    - It starts no thread in its constructor. It exposes `readLoop()`, which `Main` submits to
      the shared executor.
15. **`BaseCardType`** must use `OVERWEIGHT_BIKINI_CAT`, not `BEARD_CAT`.
16. **Executor:** `Main` creates the single `ExecutorService` as a cached pool with daemon
    threads, and shuts it down when the game ends.
17. **No `NopeEffect`.** NOPE is not registered as a play effect.
    - In `PlayResolver`, if a single played card `isNope()`, throw
      `InvalidMoveException("Nope can only be played in response to another card")`.
    - Nopes are handled only by `NopeResolver` (rule 12).
18. **Network failures:**
    - `MessageChannel` methods throw checked `IOException`; `receive(Duration)` returns
      `Optional<Message>`.
    - `RemoteController` takes a fallback `PlayerController` (a `BotController`) in its
      constructor. On `IOException` it marks itself disconnected, publishes a
      "<name> disconnected, a bot takes over" event and delegates to the fallback from then on.
    - `GameClient` catches `IOException`, prints "Connection lost" and returns normally.

19. **Targets are gathered before the Nope window.** `CardEffect` has
    `default Play prepare(GameContext ctx, Play play) throws InvalidMoveException { return play; }`.
    - Favor and two/three of a kind override it to ask `chooseTarget` (and
      `chooseCardTypeToName`) and return a completed `Play`. Everyone then knows the target
      before deciding to Nope, as in the original.
    - `prepare` also checks the completed play and throws `InvalidMoveException` if it is
      illegal. There is no separate `validate`: its only caller ran it right after `prepare`, so
      the two were merged (primitiveness). The three effects share `Targeting.withTarget` and
      `Targeting.requireTarget`.
    - `chooseAction` returns only the cards. `PlayResolver` runs: lone-Nope check → `effectFor`
      → cards in hand? → `prepare` → discard + `CardPlayed` → Nope window → `apply`.

20. **Smaller choices made while implementing phases 1–8:**
    - `CardPlayed` carries the whole `Play`, so the table sees the target and named type.
    - New public `PlayerDrew` ("X drew a card") next to the private `CardDrawn`.
    - New private `MoveRejected`: `GameEngine` reports an `InvalidMoveException` to the player,
      who then chooses again.
    - `CardTransferred` hides which card moved; the two players get private events
      (decision 22).
    - `Hand.removeRandom` was removed: two of a kind picks an index with the `Randomizer` and
      uses `GameContext.transfer`, so the event is published in one place.
    - `DrawResolver` ends the turn after a draw unless the draw effect already did (rule 9).
    - `TextRenderer.render(event, Optional<Player> viewer)` and `ConsoleView(..., Optional<Player>)`:
      an empty viewer is a spectator, e.g. in the bots-only mode.
    - `io.ActionMenu` builds the numbered turn options (draw, or play one, two or three cards
      of a type) for both `ConsoleController` and `RemoteController`.
    - `ConsoleInput.discardPending()` drops lines typed before a prompt (a late Nope answer
      must not become the next move).
    - `RemoteController` receives its announcer as a `GameEventListener` (`bus::publish`). A
      failure on a Nope worker thread only sets the flag; the game thread announces it.
      `gameOver(String)` sends `GAME_OVER` and closes the channel.
    - The disconnect event is `network.PlayerDisconnected`, an event record outside `core`
      (decision 1).
    - `SocketChannel` opens both object streams in its constructor (output first, so neither
      side deadlocks). Its timed receive polls for bytes, so it is interruptible and never
      cuts a message in half.
    - Seats are named "Player 1" (local human), "Player 2", … (remote) and "Bot 1", ….
      `Main` has three modes: menu, `bots [count] [seed]` and `client <host> [port]`.
21. **`GameContext` is one concrete class.** The `GameContext` interface and `engine.DefaultGameContext`
    were merged into `public final class GameContext` in `core.cards`.
    - Same constructor `(GameState, Map<Player, PlayerController>, EventBus, Randomizer)` and
      methods; `Main` creates it.
    - Reason: primitiveness ("no interface with one implementation and no planned second one").
      Tests can build a real `GameContext` from a `GameState` and fake controllers, so no
      second implementation is needed.
    - It imports only `model`, `players`, `events` and `util`, so the `cards` rule holds.
      Effects still never see the engine (Interface Segregation).
22. **Review fixes (after phase 8):**
    - **Determinism.** Each `BotController` gets its own `Randomizer`, `JavaRandomizer(seed + seat + 1)`
      with seats counted from 0, so no bot reuses the game's seed; the game keeps
      `JavaRandomizer(seed)`. This also applies to a `RemoteController`'s fallback bot. The
      menu mode picks a seed from `System.nanoTime()`.
      - Separate streams were not enough on their own. "First yes wins" let thread timing
        decide between two bots that both Noped, and a cancelled bot skipped its random draw.
        So a Nope round now waits for every answer (or the deadline) and breaks ties by turn
        order (decision 7). With this, `bots 5 42` gives the same output on every run.
      - Trade-off: a human's Nope takes effect only once every other eligible player has
        answered or the window has passed. Simultaneous Nopes are settled by seat, not by
        network latency.
    - **Rule 12.** In `PlayResolver`, a combo (`cards.size() > 1`) can always be Noped; a single
      card can be Noped if its type `isNopeable()`.
    - **Card transfers.** `GameContext.transfer` publishes the public `CardTransferred(from, to)`
      ("<from>'s card went to <to>"; it has no card field, so no listener can leak it), then the
      private `CardLost` (to `from`) and `CardReceived` (to `to`), which name the card.
    - **Targeting.** Candidates are other living players who hold at least one card. If there is
      none, `Targeting.withTarget` throws `InvalidMoveException` in `prepare`, so nothing is
      discarded.
    - **Starting cards.** `Expansion.createStartingCards(players)` returns the cards that *one*
      player gets (`BaseGame`: 1 Defuse). `DeckFactory.startingHand` is called once for each
      player, so every hand gets its own list.

## Phases

Each phase ends with: it compiles, a check against "Design principles", and a summary. From
phase 6 on, the bots-only game must also run to a winner.

1. **`util` + `model`:** `Hand`, `Deck`, `DiscardPile`, `Player`, `TurnState`, `GameState`.
2. **`events` + `players` (records) + `cards`:** includes `CardRegistry`.
3. **`expansion` + `BaseCardType`/`BaseGame` + `GameSetup`.**
4. **Base effects + `DrawResolver` + `PlayResolver`** (Nope stubbed to "never noped").
5. **`NopeResolver`.**
6. **`GameEngine` + `BotController` + a bots-only `Main` mode:** a seeded 4-bot game must
   finish with a winner.
7. **`io`:** `TextRenderer`, `ConsoleInput`/`ConsoleOutput`, `ConsoleController`,
   `ConsoleView`, `Menu`, `GameConfig`. A local game with 1 human and bots.
8. **`network`:** `Message`, `SocketChannel`, `RemoteController`, `GameServer`, `GameClient`,
   plus wiring in `Main`.
