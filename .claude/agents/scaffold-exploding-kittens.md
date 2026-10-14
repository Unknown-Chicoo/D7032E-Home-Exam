/a---
name: scaffold-exploding-kittens
description: Creates the package and file skeleton for the refactored Exploding Kittens (D7032E home exam 2026), with signatures and TODO stubs only and no game logic. Use when asked to scaffold or generate the project structure.
tools: Read, Write, Edit, Bash, Glob, Grep
---

# Task: scaffold the Exploding Kittens architecture

Create the **skeleton** of the project described below: every package, every file, and the
public types with their method signatures. Do **not** implement game logic.

## Ground rules

1. **Skeleton only.**
   - Interfaces, records, enums and the sealed hierarchy are written in full, because they are
     only declarations.
   - Classes get their fields, their constructors (that only assign fields) and trivial getters.
   - Every other method body is `throw new UnsupportedOperationException("TODO: <what> (rule N)");`.
     Use the rule or requirement number given below when there is one.
2. **It must compile.** Finish with the verification step at the bottom and fix every error.
3. **Javadoc.**
   - Every type gets a 1–3 line Javadoc saying its single responsibility.
   - Where it is relevant, the Javadoc names the exam requirement it covers (e.g. "Rule 12",
     "Req. 16").
4. **Java 17+.** Plain `javac`, no Maven or Gradle. Source root is `../../src`, resources are in
   `resources/`.
5. **Fixed names.** Do not add, rename or merge files beyond this list. If something seems to be
   missing, add a `// TODO(design): ...` comment instead of inventing a class.
6. **Respect the dependency rules** in the table. They are the point of the design.
7. **Leave `ExplodingKittens.java` (the original) untouched.**
   - If it is in the repo, keep it, e.g. under `legacy/`.
   - Do not copy logic from it into the stubs.

## Dependency rules (allowed imports, besides `java.*`)

| Package | May import |
|---|---|
| `core.util` | nothing |
| `core.model` | `core.util` |
| `core.events` | `core.model` |
| `core.players` | `core.model`, `core.util` |
| `core.cards` | `core.model`, `core.players`, `core.events`, `core.util` |
| `core.expansion` | `core.cards`, `core.model` |
| `core.engine` | all of `core.*` |
| `expansions.*` | `core.expansion`, `core.cards`, `core.model`, `core.players`, `core.events`, `core.util`. **Never** `core.engine`, `io`, `network` |
| `io` | `core.players`, `core.events`, `core.expansion`, `core.model`, `core.util` |
| `network` | `core.players`, `core.events`, `core.model`, `io` |
| `Main` (default package) | everything |

Nothing in `core` may import `expansions`, `io` or `network`.

## File tree

```
src/
├── Main.java
├── core/
│   ├── util/        Randomizer, JavaRandomizer, Clock, SystemClock
│   ├── model/       CardType, Card, Hand, Player, Deck, DiscardPile, TurnState, GameState,
│   │                TurnAction, Pass, Play
│   ├── events/      GameEvent, GameEventListener, EventBus
│   ├── players/     PlayerController, BotController, GameView, NopeOffer
│   ├── cards/       CardEffect, DrawEffect, GameContext, CardRegistry, InvalidMoveException
│   ├── expansion/   Expansion, ExpansionLoader, DeckFactory
│   └── engine/      GameEngine, GameSetup, PlayResolver, DrawResolver, NopeResolver,
│                    DefaultGameContext
├── expansions/
│   └── base/        BaseGame, BaseCardType
│       └── effects/ AttackEffect, SkipEffect, FavorEffect, ShuffleEffect, SeeTheFutureEffect,
│                    NopeEffect, TwoOfAKindEffect, ThreeOfAKindEffect, ExplodeOrDefuseEffect
├── io/              ConsoleInput, ConsoleOutput, ConsoleController, ConsoleView, Menu, GameConfig
└── network/         Message, MessageType, MessageChannel, SocketChannel, RemoteController,
                     GameServer, GameClient
resources/
└── META-INF/services/core.expansion.Expansion      (one line: expansions.base.BaseGame)
test/                                               (create the folder with the same package dirs; no tests yet)
```

## Type specifications

### core.util
- `interface Randomizer`
  - `int nextInt(int bound)`
  - `<T> void shuffle(List<T> list)`
- `final class JavaRandomizer implements Randomizer`
  - Constructors: `()` and `(long seed)`. Wraps `java.util.Random`. These are the only bodies you
    may implement here, because they are one-liners.
- `interface Clock`
  - `Instant now()`
- `final class SystemClock implements Clock`

### core.model (state only: no I/O, no card behaviour)
- `interface CardType`
  - `String name()`
  - `default boolean isCat() { return false; }`
  - `default boolean isNope() { return false; }`
  - `default boolean isNopeable() { return true; }`
- `record Card(CardType type)`
- `final class Hand` (a player's cards live here)
  - Fields: `List<Card> cards`.
  - Methods:
    - `add(Card)`
    - `boolean remove(Card)`
    - `Optional<Card> removeFirst(CardType)`
    - `boolean contains(CardType)`
    - `int count(CardType)`
    - `int size()`
    - `List<Card> cards()` (unmodifiable view)
    - `Card removeRandom(Randomizer)`
    - `List<Card> clear()`
- `final class Player`
  - Fields: `String name`, `Hand hand`, `boolean alive = true`.
  - Methods: `name()`, `hand()`, `isAlive()`, `eliminate()`.
  - It does **not** know its controller.
- `final class Deck`
  - `Card drawTop()`
  - `void insertAt(int position, Card)`
  - `List<Card> peek(int n)`
  - `void shuffle(Randomizer)`
  - `void addAll(Collection<Card>)`
  - `int size()`
  - `boolean isEmpty()`
- `final class DiscardPile`
  - `add(Card)`
  - `addAll(Collection<Card>)`
  - `Optional<Card> top()`
  - `int size()`
- `final class TurnState` (replaces the original static `numberOfTurnsToTake`)
  - Constructor: `(List<Player> players, int startIndex)`.
  - Methods:
    - `Player current()`
    - `int turnsLeft()`
    - `void endTurn()` (rule 10)
    - `void attack()` (rule 11: Attack, stacking)
    - `void playerEliminated(Player)`
    - `Player nextAlive(Player)`
- `final class GameState`
  - Constructor: `(List<Player> players, Deck deck, DiscardPile discard, TurnState turns)`.
  - Getters plus:
    - `List<Player> alivePlayers()`
    - `Optional<Player> winner()` (rule 13)
- `sealed interface TurnAction permits Pass, Play`
- `record Pass(Player player) implements TurnAction`
- `record Play(Player player, List<Card> cards, Optional<Player> target, Optional<CardType> namedType) implements TurnAction`

### core.events (Observer)
- `sealed interface GameEvent`
  - `default Optional<Player> privateTo() { return Optional.empty(); }`
  - Nested records implementing it:
    - `TurnStarted(Player player, int turnsLeft)`
    - `CardPlayed(Player player, List<Card> cards)`
    - `PlayNoped(Player by, Play play)`
    - `CardDrawn(Player player, Card card)`: overrides `privateTo` to return `player`
    - `CardTransferred(Player from, Player to, Card card)`
    - `FutureSeen(Player player, List<Card> top)`: overrides `privateTo` to return `player`
    - `DeckShuffled()`
    - `KittenDefused(Player player)`
    - `PlayerExploded(Player player)`
    - `GameWon(Player winner)`
- `interface GameEventListener`
  - `void onEvent(GameEvent event)`
- `final class EventBus`
  - `void subscribe(GameEventListener)`
  - `void publish(GameEvent)`

### core.players (Strategy: where decisions come from)
- `interface PlayerController` (Req. 16 and 17: humans, bots and remote players look the same)
  - `TurnAction chooseAction(GameView view)`
  - `Player chooseTarget(GameView view, List<Player> candidates)`
  - `Card chooseCardToGive(GameView view, Player requester)` (Favor)
  - `CardType chooseCardTypeToName(GameView view, List<CardType> options)` (three of a kind)
  - `int chooseDeckPosition(GameView view, int deckSize)` (Defuse)
  - `boolean wantsToNope(GameView view, NopeOffer offer)` (rule 12)
- `record GameView(Player self, List<Card> hand, int deckSize, Map<Player, Integer> handSizes, int turnsLeft)`
  - It only holds what one player may see.
- `record NopeOffer(Player actor, Play play, Instant deadline)`
- `final class BotController implements PlayerController`
  - Constructor: `(Randomizer)`.
  - Decides only through `CardType` traits, never through `BaseCardType` (Req. 16).

### core.cards (the contract card behaviour is written against)
- `final class InvalidMoveException extends Exception`
  - It goes here, **not** in `engine`; otherwise `cards → engine → cards` would form a cycle.
- `interface CardEffect` (Strategy)
  - `default void validate(GameContext ctx, Play play) throws InvalidMoveException {}`
  - `void apply(GameContext ctx, Play play)`
- `interface DrawEffect`
  - `void onDraw(GameContext ctx, Player player, Card card)`
- `interface GameContext` (Dependency inversion: effects see only this, never the engine)
  - `GameState state()`
  - `PlayerController controllerOf(Player)`
  - `GameView viewFor(Player)`
  - `void transfer(Card card, Player from, Player to)`
  - `void publish(GameEvent)`
  - `Randomizer random()`
- `final class CardRegistry` (Registry; one per game, never static)
  - Write side, used at setup:
    - `register(CardType, CardEffect)`
    - `registerCombo(int size, CardEffect)`
    - `registerOnDraw(CardType, DrawEffect)`
  - Read side, used during play:
    - `CardEffect effectFor(Play) throws InvalidMoveException`
    - `Optional<DrawEffect> onDrawFor(CardType)`

### core.expansion (Plugin and Abstract Factory)
- `interface Expansion`
  - `String name()`
  - `List<Card> createDeckCards(int players)`
  - `default List<Card> createStartingCards(int players) { return List.of(); }`
  - `default List<Card> createKittens(int players) { return List.of(); }`
  - `void registerEffects(CardRegistry registry)`
  - `default int maxPlayers() { return 5; }`
- `final class ExpansionLoader`
  - `List<Expansion> available()` (`ServiceLoader.load(Expansion.class)`)
  - `Optional<Expansion> byName(String)`
- `final class DeckFactory`
  - `List<Card> deckCards(List<Expansion>, int players)`
  - `List<Card> startingCards(List<Expansion>, int players)`
  - `List<Card> kittens(List<Expansion>, int players)`

### core.engine (procedures; plain final classes with constructor injection)
- `final class GameSetup` (rules 1–8)
  - Constructor: `(DeckFactory, Randomizer)`.
  - Nested `record Result(GameState state, CardRegistry registry)`.
  - Method: `Result setUp(List<Expansion> expansions, List<String> playerNames)`.
- `final class DefaultGameContext implements GameContext`
  - Constructor: `(GameState, Map<Player, PlayerController>, EventBus, Randomizer)`.
- `final class NopeResolver` (rule 12, Req. 20)
  - Constructor: `(Clock, Duration window)`.
  - Method: `boolean isNoped(GameContext ctx, Play play)`.
- `final class PlayResolver` (rule 11: effect → validate → Nope window → apply)
  - Constructor: `(CardRegistry, NopeResolver)`.
  - Method: `void resolve(GameContext ctx, Play play) throws InvalidMoveException`.
- `final class DrawResolver` (rule 10: draw → draw effect or hand)
  - Constructor: `(CardRegistry)`.
  - Method: `void draw(GameContext ctx, Player player)`.
- `final class GameEngine` (game loop)
  - Constructor: `(GameContext, PlayResolver, DrawResolver)`.
  - Method: `Player run()` (rule 13).

### expansions.base (the base game as a plug-in; rules 2–13)
- `enum BaseCardType implements CardType`
  - Constants: `EXPLODING_KITTEN, DEFUSE, ATTACK, FAVOR, NOPE, SHUFFLE, SKIP, SEE_THE_FUTURE, TACOCAT, HAIRY_POTATO_CAT, CATTERMELON, BEARD_CAT, RAINBOW_RALPHING_CAT`.
  - Override the traits:
    - the five cats → `isCat()` returns true
    - `NOPE` → `isNope()` returns true
    - `EXPLODING_KITTEN` and `DEFUSE` → `isNopeable()` returns false
- `final class BaseGame implements Expansion`
  - The card counts in `createDeckCards`, `createStartingCards` and `createKittens` are TODO
    stubs that cite rules 2, 3 and 6.
  - `registerEffects` is written in full, because it is only wiring:
    - `register(ATTACK, new AttackEffect())`
    - `register(SKIP, new SkipEffect())`
    - `register(FAVOR, new FavorEffect())`
    - `register(SHUFFLE, new ShuffleEffect())`
    - `register(SEE_THE_FUTURE, new SeeTheFutureEffect())`
    - `register(NOPE, new NopeEffect())`
    - `registerCombo(2, new TwoOfAKindEffect())`
    - `registerCombo(3, new ThreeOfAKindEffect())`
    - `registerOnDraw(EXPLODING_KITTEN, new ExplodeOrDefuseEffect())`
- In `effects/`:
  - Every class except `ExplodeOrDefuseEffect` is `final class X implements CardEffect` with
    stubbed `apply` and, where it applies, `validate`:
    - `TwoOfAKindEffect` and `ThreeOfAKindEffect` need a target.
    - `FavorEffect` needs a target.
    - `NopeEffect.validate` always throws, because a Nope is only playable inside the Nope window.
  - `ExplodeOrDefuseEffect implements DrawEffect`.

### io (console adapter)
- `final class ConsoleInput`
  - Constructor: `(InputStream)`. This is the single reader of `System.in`.
  - Methods:
    - `String readLine()`
    - `int readInt(int min, int max)`
    - `int choose(List<String> options)`
- `final class ConsoleOutput`
  - Constructor: `(PrintStream)`.
  - Methods: `println(String)`, `showOptions(List<String>)`.
- `final class ConsoleController implements PlayerController`
  - Constructor: `(ConsoleInput, ConsoleOutput)`.
- `final class ConsoleView implements GameEventListener`
  - Constructor: `(ConsoleOutput, Player viewer)`.
  - Respects `privateTo()`.
- `record GameConfig(List<Expansion> expansions, int humans, int bots, boolean online, int port, Duration nopeWindow)`
- `final class Menu` (Req. 15)
  - Constructor: `(ConsoleInput, ConsoleOutput, ExpansionLoader)`.
  - Method: `GameConfig run()`.

### network (network adapter; Req. 17: typed messages, no keyword parsing)
- `enum MessageType`
  - `EVENT, ASK_ACTION, ASK_TARGET, ASK_CARD, ASK_CARD_TYPE, ASK_POSITION, ASK_NOPE, ANSWER, GAME_OVER`
- `record Message(MessageType type, String text, List<String> options, int answerIndex) implements Serializable`
  - Static factories: `ask(type, text, options)`, `answer(int)`, `event(String)`.
- `interface MessageChannel extends AutoCloseable`
  - `void send(Message)`
  - `Message receive()`
  - `Optional<Message> receive(Duration timeout)`
- `final class SocketChannel implements MessageChannel`
  - Constructor: `(Socket)`. Uses Object streams.
- `final class RemoteController implements PlayerController, GameEventListener`
  - Constructor: `(MessageChannel, Player)`. It is a remote proxy of a human.
- `final class GameServer`
  - Constructor: `(int port)`.
  - Method: `List<MessageChannel> acceptClients(int count)`.
- `final class GameClient`
  - Constructor: `(MessageChannel, ConsoleInput, ConsoleOutput)`.
  - Method: `void run()`.

### Main (composition root, the only place that calls `new` on adapters)
- `public static void main(String[] args)`. The body is a stub containing an ordered comment plan:
  1. Parse args: `client <host>` goes to `GameClient`.
  2. Otherwise, `Menu.run()` gives a `GameConfig`.
  3. Accept remote clients.
  4. Build controllers: Console, Remote, Bot.
  5. `GameSetup.setUp(...)`.
  6. Build the `EventBus` and subscribe views.
  7. Build `DefaultGameContext` and the resolvers.
  8. `GameEngine.run()`.
  9. Close the channels.

## Verification (must pass before you finish)

```sh
mkdir -p out && javac -d out $(find src -name '*.java') && cp -r resources/* out/ && echo COMPILED
# dependency check: each of these must print nothing
grep -rlE 'import (expansions|io|network)\.' src/core
grep -rlE 'import core\.engine' src/expansions src/io src/network
grep -rlE 'import core\.(cards|engine|expansion|players|events)' src/core/model src/core/util
grep -rlE 'import core\.(cards|engine|expansion|events)' src/core/players
```

## Final report

Report to the user:

1. The created file tree.
2. That everything compiles.
3. That the dependency checks are clean.
4. A list of every `// TODO(design)` you added.

Do not summarise anything else.
