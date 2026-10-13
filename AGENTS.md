# AGENTS.md: D7032E Home Exam 2026 (Exploding Kittens re-engineering)

This file tells any AI coding agent (Claude Code, Codex, Copilot, Cursor and similar) how to work in this repository.
Read the whole file before doing anything. If you are Claude Code, `CLAUDE.md` imports this file.

---

## 0. TL;DR for the agent

1. **This is an individual university exam.** The student must be able to *explain and defend every design decision* in a one-to-one oral exam. Your job is to make the student understand the work, not only to produce it.
2. **Always ask before deciding. Always explain what you decided.** See §2 for the protocol. Never make an architectural, structural, API, dependency or testing-strategy choice silently.
3. **Every design and code change must be justified with Booch's metrics and SOLID** (§4). Name the principle, say how the change satisfies it, and say what the trade-off is.
4. **Log every decision** in `docs/DECISIONS.md` (§2.4). That log becomes the raw material for the written report (Q2) and the oral exam.
5. **Never modify `original/ExplodingKittens.java`.** It is the reference for Part A Question 1.
6. **Deadline: Tuesday 20 October 2026, 09:00 (Europe/Stockholm).** Prioritise a working, tested and documented solution of rules 1 to 13 over optional extras.

---

## 1. The assignment (summary, so you do not need the PDF)

Course: D7032E Software Engineering, Luleå University of Technology. Teacher: Josef Hallberg.
Source files are in `original/` and the rulebooks are in `rules/` (base game, Imploding, Streaking, Barking, Zombie Kittens).

DevCat wrote `ExplodingKittens.java`, a single 424-line file that contains the server, client, player, game state and all rules. The student must re-engineer it into a well-designed, extensible, testable architecture.

### 1.1 Rules and requirements (copied from the exam; the exam text wins over the official rulebook)

1. There can be between 2 and 5 players.
2. Each player is given 1 Defuse card.
3. Before the first hands are dealt, the deck consists of:
   a. 2 Defuse for 2–4 players, 1 Defuse for 5 players · b. 4 Attack · c. 4 Favor · d. 5 Nope · e. 4 Shuffle · f. 4 Skip · g. 5 SeeTheFuture · h–l. 4 each of HairyPotatoCat, Cattermelon, RainbowRalphingCat, TacoCat, OverweightBikiniCat.
4. Shuffle the deck before the first hands are dealt.
5. Deal 7 cards from the deck to each player.
6. Insert ExplodingKitten cards so that there is 1 fewer than the number of players.
7. Shuffle the deck before the game starts.
8. A random player goes first.
9. On a turn the player may Pass or play one or several cards.
   a. Pass ends the turn and the player draws a card. · b. The player may play as many or as few cards as desired.
10. On drawing an ExplodingKitten the player must play Defuse if they have one, or explode.
    a. A defused kitten is reinserted at a location the player chooses. · b. An exploded player discards their hand (including the kitten) and takes no more turns.
11. Playing a card:
    a. Attack ends the turn without drawing. i. The next player takes 2 turns in a row, unless that player plays Attack, in which case the next player takes an additional 2 (4, then 6, and so on).
    b. Favor: another player gives you 1 card of *their* choice.
    c. Shuffle shuffles the deck.
    d. Skip ends the turn without drawing (only 1 turn if the player has several to take).
    e. SeeTheFuture: view the top 3 cards.
    f. Two of a kind (any kind): steal a *random* card from another player.
    g. Three of a kind (any kind): name a card to receive from another player (if they have it).
12. A Nope can be played within 5 seconds of any card(s) in rules 11 and 12 to stop the action. a. A Nope on a Nope alternates the meaning (Nope ↔ Yup).
13. When all but one player has exploded, the remaining player is announced as the winner to all players.

**Future modifications** (the architecture must support them; implementation is optional and must be agreed with the student):

14. Expansions such as Imploding, Streaking and Barking Kittens (no physical gimmicks such as the Cone of Shame or the Tower of Power crown).
15. A menu for selecting expansions and game variants.
16. Bots that can join instead of players.
17. Decouple networking from game logic so they can vary independently. The client currently relies on hard-coded keywords.

**Quality requirements:**

18. Modifiability and extensibility.
19. Testability, including dependency injection, so that rules and game phases can be tested.
20. *Bonus:* concurrency and interrupts. See lines 148 and 163 of the original: the Nope window reads the same `ObjectInputStream` from a pool thread, and `awaitTermination` is called without `shutdown`.

### 1.2 What is graded

- **Part A, Q1 (2p, max 1 page):** which of rules 1–13 the *original* code fails, and for each one, either the JUnit assert that would test it without modifying the code, or a motivation for why that is not possible.
- **Part A, Q2 (9p, max 2 pages excl. diagrams):** a) shortcomings of the original design using SOLID and Booch; b) design choices in the new design using SOLID and Booch; c) one section *per quality attribute* on how it is satisfied; d) design patterns used, or a motivated choice not to use them.
- **Part B, Q3 (14p):** the refactored code is graded on extensibility (2p), modifiability (3p), testability (1p), unit tests for rules 1–13 (2p), best practices (1p), correct game functionality (2p), error and exception handling (1p), documentation (1p), and fidelity to the Q2 design (1p).
  - Extensibility is judged specifically on whether **an expansion can be added without modifying existing compiled code**, and whether that expansion can be unit-tested and integration-tested before launch.
- **Oral exam (required for Part B):** about 15 minutes on the architecture. Diagrams matter more than long text.

Grades: 5 ≥ 22p, 4 ≥ 18p, 3 ≥ 13p (26p in total).

### 1.3 Hand-in format (check this at the end)

- One compressed file (zip, tar or rar) containing a folder named after the student's username, with a PDF of the written answers and diagrams plus the code.
- Every PDF page shows the student's full name and a page number. Use a 12 pt readable font and running text, not just bullets. Write in Swedish or English.
- External references are cited in the text and listed in a separate references section at the end.
- Instructions for compiling, running and testing are documented.
- The server does not need to host a player. Visual output may differ from the original. The general functionality must remain.

### 1.4 Lessons from previous exams

- The 2024 exam (Point Salad, with Point City as the future variant) and earlier exams use **the same question structure and grading criteria**. The examiner consistently rewards:
  - variants and expansions as plug-ins behind interfaces;
  - rules and scoring as interchangeable strategies;
  - setup and deck composition driven by data or configuration rather than hard-coded loops;
  - player I/O (human, network, bot) hidden behind one abstraction;
  - deterministic, injectable randomness for tests;
  - a test per numbered requirement.
- The pattern is the same as in Point Salad and Point City: a *base game* plus a *variant that changes core rules*. Design so that the variant plugs in without editing the base.

---

## 2. Interaction protocol: always ask, always explain

The student needs to understand and defend everything, so the default is **collaborative and explanatory, not autonomous**.

### 2.1 What requires asking first

Ask before you act on any of the following:

- package and module structure, or new classes and interfaces in the architecture;
- public method signatures and interfaces between modules;
- choice of design pattern, or a decision *not* to use one;
- how a rule is interpreted when the exam text, the official rulebook and the original code disagree;
- build tool, Java version, libraries and dependencies;
- test strategy, test framework and what to mock or fake;
- implementing optional items (expansions, menu, bots, the concurrency bonus);
- deleting or renaming anything the student wrote;
- anything that changes the written report or diagrams.

For trivial mechanics, such as formatting, a private helper's name, or an obvious typo fix, just do it, then mention it in one line afterwards.

### 2.2 How to ask (use this template)

```
### Decision needed: <short topic>
**Context:** <why this decision comes up now; which requirement(s) it touches>
**Option A – <name>:** <what it means in code>
  + <benefit> (e.g. "lower coupling: engine depends only on PlayerController")
  − <cost>
**Option B – <name>:** …
(**Option C** if genuinely different)
**My recommendation:** <A/B/C>, because <reasoning in SOLID / Booch terms and quality attributes 17–19>.
**Question:** Which do you prefer, or do you want to discuss it?
```

Rules for asking:

- Ask about at most 3–4 related decisions at once.
- Give genuine alternatives, not straw men.
- Wait for the answer before writing code that depends on it.
- If the student says "you decide", decide, but still explain and log the decision.

### 2.3 How to explain after a change

After every non-trivial change, report in this shape:

- **What changed:** files, classes and interfaces.
- **Why:** the principles applied, named explicitly (for example "SRP: `Deck` now only manages card order; dealing moved to `Dealer`").
- **Trade-off:** what got worse or more complex, and why that is acceptable.
- **How it is verified:** which tests cover it and how to run them.
- **Oral-exam talking point:** one or two sentences the student could say if the examiner asks "why did you do it this way?", plus one likely follow-up question.

If the student seems unsure, offer to walk through the code or a diagram. Do not dump more text on them.

### 2.4 Decision log: `docs/DECISIONS.md`

Append an entry for every decision, numbered and never rewritten. If a decision is later reversed, add a new entry that supersedes the old one.

```
## D-007: Card effects as registered strategies instead of an enum switch
Date: 2026-10-14 · Status: accepted (supersedes —)
Requirements: 11, 14, 18
Context: …
Options considered: A) enum + switch  B) CardEffect strategy + registry  C) class per card with inheritance
Decision: B
Principles: OCP (new cards = new classes, no edits), SRP, low coupling (engine knows only CardEffect), primitiveness (each effect does one thing)
Consequences: + expansions add effects without recompiling core; − one more indirection, registry must be populated at startup
Oral one-liner: "Adding Imploding Kittens means adding an Expansion that registers new CardEffects – no existing class changes."
```

### 2.5 Academic integrity

- Write code and text the student can explain. Prefer clear and simple over clever.
- For the written answers (Q1–Q3), help the student draft, structure and check their reasoning. Point out gaps against the grading criteria.
- Do not invent references. Every cited source must be real and complete. Ask the student which course literature they want to cite.
- When a finding comes from you (for example a bug in the original code), show the evidence: line numbers and a failing test. That way the student verifies it rather than copying it.

---

## 3. Repository layout (proposed; confirm with the student at the start)

```
/
├── AGENTS.md                 ← this file
├── CLAUDE.md                 ← contains "@AGENTS.md"
├── README.md                 ← build / run / test instructions (graded!)
├── pom.xml | build.gradle    ← build tool: ASK (§7)
├── original/
│   └── ExplodingKittens.java ← untouched reference, never edit
├── rules/                    ← rulebook PDFs
├── docs/
│   ├── DECISIONS.md          ← decision log (§2.4)
│   ├── design.md             ← architecture description for Q2
│   ├── traceability.md       ← requirement → class(es) → test(s)
│   └── diagrams/             ← PlantUML/Mermaid sources + exported PNG/SVG
└── src/
    ├── main/java/<base package>/…
    └── test/java/<base package>/…
```

---

## 4. Design principles: the checklist every change must pass

Before proposing or committing a design or code change, check it against both lists. When you explain a change, cite the items that apply.

### 4.1 Booch's metrics

| Metric | Meaning | What it means concretely in this project |
|---|---|---|
| **Coupling** (aim: low) | How much modules depend on each other. | Game logic must not import `java.net`, `java.io` streams or `System.in`/`System.out`. The engine depends on interfaces (`PlayerController`, `GameEventListener`, `Shuffler`), not on concrete classes. No `public static` mutable state (the original's `deck`, `discard` and `numberOfTurnsToTake`). Collaborators are passed in through constructors and are not created with `new` inside domain classes. Package dependencies are acyclic: `network → engine → model`, never the reverse. |
| **Cohesion** (aim: high) | How closely a module's responsibilities belong together. | Each class can be described in one sentence without "and". `Deck` manages card order, `TurnManager` manages whose turn it is and how many turns remain, and a `CardEffect` does exactly one card's action. The original `game()` method (190 lines covering I/O, validation, rules and turn order) is the counter-example to point at. |
| **Sufficiency** | The abstraction offers enough to meet its requirements. | Every rule 1–13 maps to a class that can actually express it (see `docs/traceability.md`). For example, the deck must support "insert at position N" for rule 10a. |
| **Completeness** | All required parts are present and work together, with no requirement left unaddressed. | All rules 1–13 are implemented *and tested*. Each abstraction covers its domain fully enough that the known expansions fit, for example a deck that supports draw-from-bottom and peek-and-reorder, and a turn order that supports reversal. Balance this against primitiveness: build the hooks, not the whole expansion. |
| **Primitiveness** | Operations are simple and single-purpose, and complex behaviour is composed from them. | `Deck` exposes primitives such as `drawTop()`, `drawBottom()`, `insertAt(i, card)`, `peek(n)`, `shuffle()` and `size()`. "See the Future" is composed *in its effect* from `peek(3)` and does not live in `Deck`. Methods are short. If a method needs a comment to separate its "phases", split it. |

### 4.2 SOLID

| Principle | Meaning | What it means concretely in this project |
|---|---|---|
| **S** Single Responsibility | One reason to change. | Separate game state, rules and effects, turn flow, setup and deck building, player decision-making, presentation and messages, and transport. The original `Player` mixes identity, hand, socket, console I/O and the Nope timeout, which is five reasons to change. |
| **O** Open/Closed | Open for extension, closed for modification. | Adding a card, a combo, an expansion, a bot strategy, a transport or a UI must mean **adding classes and registering them**, not editing existing `switch` or `if` chains or enums. The original `enum Card` plus the `if/else if` chain is the counter-example. This is exactly what Q3 extensibility is graded on. |
| **L** Liskov Substitution | Subtypes are substitutable for their base type. | `HumanConsoleController`, `RemoteController`, `BotController` and the test `ScriptedController` must all honour the same `PlayerController` contract (pre- and post-conditions documented in Javadoc). The engine never checks `instanceof` or `isBot`. Every `CardEffect` must leave the game in a valid state. |
| **I** Interface Segregation | Clients are not forced to depend on methods they do not use. | Keep interfaces narrow and role-based. For example, keep "choose an action", "respond to a Favor", "decide to Nope" and "choose where to put the kitten" as separate small interfaces (or clearly separate methods with sensible defaults). Do not create one huge `GameIO` interface. An effect that only needs the deck receives a deck-level view, not the whole game. |
| **D** Dependency Inversion | High-level modules depend on abstractions, and so do low-level ones. | The engine (high level) and sockets or console (low level) both depend on interfaces defined *in the engine/domain layer*. Randomness (`Shuffler` / `RandomSource`), time (the Nope timeout and clock) and player I/O are injected. One **composition root** (`app` package, in `main`) wires everything together. This is also what makes rules 1–13 testable (requirement 19). |

### 4.3 Red flags to stop on and raise with the student

- a `switch` or `if` chain on card type, player type or message text;
- `static` mutable fields; singletons;
- `new Random()`, `System.currentTimeMillis()` or `Thread.sleep()` inside game logic;
- `System.exit()` anywhere except `main`;
- empty `catch` blocks or `catch (Exception e)` that swallow errors;
- string-matching on protocol messages (`contains("options")`), which is requirement 17;
- a class over roughly 200 lines or a method over roughly 30 lines;
- tests that need sockets, threads, real time or console input to check a game rule.

---

## 5. Architecture direction (a proposal to discuss, not a mandate)

Present this to the student as a starting point at the first design session, and adapt it to their choices. Every element below is an **open decision** until it is confirmed and logged.

### 5.1 Candidate layers and packages

- **`model`**: plain domain objects with no I/O. `Card` / `CardType` (value objects, not a closed enum), `Deck` (draw pile), `DiscardPile`, `Hand`, `PlayerState` (id, hand, alive/exploded), `GameState`.
- **`rules` / `cards`**: `CardEffect` (strategy, one per playable card), `ComboRule` (pair, triple, and later the 5-card combo), `CardRegistry`, `NopeResolver` (stack of Nope/Yup), `DrawHandler` chain (Exploding Kitten, later Imploding and Streaking behaviour).
- **`engine`**: `GameEngine` (game loop), `TurnManager` (turn queue, attack stacking, direction), `WinCondition`, `GameEventListener` / `GameEventBus` (observer for notifications).
- **`setup`**: `GameConfig`, `DeckBuilder` / `DeckRecipe` (card counts per player count, rule 3), `Dealer` (rules 2, 4–7), `Expansion` interface (contributes cards, effects, setup adjustments and hooks).
- **`player`**: `PlayerController` (decision interface), `HumanConsoleController`, `BotController` + `BotStrategy`.
- **`network`**: typed `Message` / `Prompt` objects (for example sealed interfaces or records with an explicit type, not free text), `Transport` interface, socket implementation, `RemoteController` (server side) and `ClientApp` (client side; renders a prompt by its *type*).
- **`app`**: `ServerMain`, `ClientMain`, `Menu` (rule 15), composition root.

### 5.2 Extension points the known expansions will need

Use these to argue *completeness* and *extensibility* in Q2 and Q3a/b. Build the hook, not the whole feature, unless the student decides to implement the expansion.

| Expansion card or mechanic | Core hook it needs |
|---|---|
| Imploding Kitten (cannot be defused, goes back face-up) | `DrawHandler` chain; a card can have face-up/face-down state inside the deck. |
| Reverse | `TurnManager` with a direction. |
| Draw from the Bottom | `Deck.drawBottom()`; "end turn by drawing" parameterised by draw source. |
| Targeted Attack, Personal Attack, Super Skip | `TurnManager` with an explicit turn queue and a "choose victim" option. |
| Alter the Future, Share the Future, Bury | `Deck.peek(n)` / reorder / `insertAt`; prompts to another player. |
| Feral Cat (wild cat card) | `ComboRule` matches on card *attributes* (`isCat`, `isWild`), not on identity. |
| Streaking Kitten (hold one Exploding Kitten safely); kitten count = number of players | Setup adjustment by expansion (`DeckRecipe` modifiers); a rule check for "holding a kitten" when the hand changes. |
| Mark, Curse of the Cat Butt (cards visible or blind) | Per-card visibility state in `Hand`. |
| Barking Kitten, I'll Take That (cards placed *in front of* a player) | A per-player tableau / "in front of" area. |
| Potluck, Nope rules per expansion | Iterating over players in turn direction; `NopeResolver` asks each effect `isNopeable()`. |
| Zombie Kittens (dead players can return) | Player status as state (alive/dead), not a final boolean. |

### 5.3 Patterns likely to be relevant (Q2d asks for motivation, including choosing *not* to use one)

- **Strategy**: card effects, bot behaviour, setup recipes.
- **Command**: a "played action" object that can be Noped before execution.
- **Observer**: game events to views and network.
- **Factory / Builder**: deck creation per configuration and expansions.
- **Chain of Responsibility**: draw handling and Nope interception.
- **State**: game phases or player status.
- **Template Method**: turn structure.
- **Plug-in / ServiceLoader**: expansions.

For each pattern you suggest, also state what it costs (indirection, more classes). Accept a decision not to use it if the student prefers.

### 5.4 Networking (requirement 17)

- The engine talks to a `PlayerController`. Whether that controller is local, remote or a bot is invisible to the engine (DIP, LSP).
- The protocol uses explicit message types (for example `PromptChooseAction`, `PromptGiveCard`, `PromptPlaceKitten`, `PromptNopeWindow`, `Notification`). The client decides how to render and respond based on the type, never on keywords in the text.
- Serialization format (Java object streams vs JSON lines) is an **open decision**: ask.

### 5.5 Concurrency (bonus, requirement 20)

- Problem in the original: two readers on one stream (main thread plus Nope pool thread), a fixed 5.5 s wait because `shutdown()` is never called, and a new `BufferedReader(System.in)` for each read.
- Possible direction: one reader thread per connection feeding a `BlockingQueue` inbox, and a Nope window built on `CompletableFuture` / `poll(timeout)` with an injected timeout (0 or a short value in tests).
- Ask before implementing this, because it is optional.

---

## 6. Known defects in the original code

Use this list to make sure the refactored version fixes them, and to back the student's Q1 answer with evidence. **The student must verify each defect themselves** (read the line, or write the test) before writing Q1. Point to the line and let them reason about it. Some items involve interpretation, so ask the student how they read the rule.

| Rule | Defect (line in `original/ExplodingKittens.java`) |
|---|---|
| 1 | No validation that the total number of players and bots is 2–5 (lines 84–85, 408–416). |
| 3a | `6 - players.size()` Defuse cards gives 4 for 2 players and 3 for 3 players; the rule says 2 (line 107). |
| 10b | A player who explodes while still owing turns (after being attacked) keeps getting prompted and draws again; the `for` loop over `numberOfTurnsToTake` is not exited (lines 192–247). |
| 10a | The insert position for a defused kitten is not validated (crashes on bad input), and the prompt range `[0..size-1]` excludes the bottom of the deck (lines 233–234). |
| 11a | Attack stacking is decided by whether the *top of the discard pile* is an Attack, not by whether the current player is under attack. It can produce 3 turns, or fail to stack (lines 289–294). Interpretation needed: should "+2" be added to the remaining turns or the total turns? Ask the student. |
| 11d | Skip played as the first action on the last of several turns skips the reset of `numberOfTurnsToTake`, so the next player inherits extra turns (lines 338–339 vs 352–353). |
| 11e | SeeTheFuture crashes with fewer than 3 cards in the deck (line 346). |
| 11f | `rnd.nextInt(hand.size()-1)` never picks the last card and throws when the target has 0 or 1 cards (line 262). |
| 11g | Three of a kind can never be played: the validation `yourOptions.contains(response.replaceAll("\\d",""))` rejects `Three X 1 Y` (lines 211, 227). |
| 11b, f, g | Targets are not validated: self, exploded or non-existent player IDs are accepted and can crash the game. Favor with an invalid card name throws `IllegalArgumentException` (lines 260, 277, 308, 318). |
| 12 | The Nope count is derived from consecutive Nopes at the top of the discard pile, so a combo *of Nope cards* corrupts the parity. The Nope read races with normal reads on the same stream (lines 138–179). |
| General | Static mutable `deck`, `discard` and `numberOfTurnsToTake` (lines 8–10). `System.exit` inside `game()`. `main` swallows all exceptions. Bots read from the server console (`readMessage` for `isBot` players). Unknown commands with missing arguments throw `ArrayIndexOutOfBoundsException` and end the game. |

For Q1 testability: almost all logic is reachable only through the constructor → `initGame` → `server()` (blocking on `ServerSocket.accept()`) → `game()` (`System.exit`). Help the student reason precisely about which rules *could* be asserted without modifying the code and which cannot, and why.

---

## 7. Open decisions to settle in the first session

Ask these early, using the §2.2 template, and log the answers:

1. **Java version** (recommend 17 or 21 LTS for records, sealed interfaces and switch expressions).
2. **Build tool:** Maven, Gradle, or plain `javac` plus the JUnit console launcher. The test command must be easy for the examiner to run.
3. **Test framework:** JUnit 5 (recommended). Hand-written fakes, Mockito, or both.
4. **Base package name** and the final package structure (§5.1).
5. **Card modelling:** enum vs value object plus registry vs class hierarchy. This is central to OCP and extensibility.
6. **Rule interpretations** in §6 (Attack stacking, whether a player may Nope their own card, Favor targeting).
7. **Optional scope:** implement any of rules 14–17 and 20, or only design for them?
8. **Diagram tool:** PlantUML or Mermaid; which diagrams (package, class, sequence for turn, Nope and network flow).
9. **Report language:** Swedish or English.
10. **Serialization format** for the network protocol.

---

## 8. Coding standards

- Use standard Java naming conventions: `PascalCase` types, `camelCase` members, `UPPER_SNAKE` constants, lowercase package names. Names come from the domain and the rulebook (`ExplodingKitten`, `Defuse`, `DrawPile`).
- **Constructor injection** for all collaborators. Mark fields `final` wherever possible. Make value objects immutable (records).
- Do not use `null` in public APIs; use `Optional` or explicit result types. Validate input at the boundary (controllers and network), not deep in the domain.
- **Errors:** create domain exceptions such as `InvalidMoveException` or `InvalidTargetException`. An invalid player choice is re-prompted with a clear message; it never crashes the game. Never use empty `catch` blocks. Log or report unexpected errors with context. Graceful disconnect handling is a plus.
- **Documentation:** Javadoc on every public type and method, including contracts and pre/post-conditions for interfaces (supports LSP). Each class has a short header saying its single responsibility. Comments explain *why*, not *what*.
- Keep each change small and coherent, and explain it according to §2.3.
- Do not add a third-party library without asking.

---

## 9. Testing

- **Every rule 1–13 has at least one test**, named by requirement. For example: `Rule03_DeckComposition.twoToFourPlayersGetTwoDefuseCardsInDeck()`, `Rule11a_Attack.attackedPlayerWhoAttacksPassesFourTurnsOn()`.
- Keep `docs/traceability.md` current: requirement → implementing class(es) → test method(s).
- Tests must be **deterministic and fast**:
  - an injected seeded or scripted `Shuffler`;
  - scripted `PlayerController`s that return predefined choices;
  - a zero or short Nope timeout;
  - no sockets, no real console, no `Thread.sleep`.
- Separate **unit tests** (a single effect, `Deck`, `TurnManager`) from **integration tests** (a full game with scripted players driven to a known winner). Network tests, if any, use a loopback or in-memory `Transport`.
- If an expansion is implemented, it has its own tests that run *without modifying* core tests. This is graded under extensibility.
- Test code must not leak into production code: no test-only flags or `if (testing)` branches. Testability comes from DI.
- Run the full test suite before reporting a change as done. Report pass/fail honestly. Never weaken or delete a test to make it pass without asking.
- Document the exact test command in `README.md`.

---

## 10. Documentation deliverables (keep these in sync with the code; Q3i grades fidelity)

- **`README.md`:** prerequisites, build, run server (`[#players] [#bots]`, or the new syntax plus menu), run client (`<server-ip>`), run tests, project structure, and how to add an expansion.
- **`docs/design.md`:** architecture overview, responsibilities per package and class, interfaces, relations, and communication flow. It must let a new developer know where functionality lives (the wording of Q2).
- **Diagrams:** package diagram, class diagram(s), and sequence diagrams for (1) a normal turn, (2) playing a card with the Nope window, and (3) server–client messaging. Update them whenever the structure changes.
- **Report support:**
  - Q2a, original shortcomings: use §4 and §6 as the evidence base.
  - Q2b, SOLID and Booch: draw from `DECISIONS.md`.
  - Q2c: one section *per quality attribute* (modifiability, extensibility, testability, and concurrency if done).
  - Q2d: patterns used and not used, with motivation.
  - Q3a–i: honest self-assessment.
  - The report is written in **running text**, within the page limits.

---

## 11. Definition of done for any task

- [ ] The decision was asked about (or was trivial) and is logged in `docs/DECISIONS.md`.
- [ ] The change is explained (what, why, trade-off, verification, oral talking point).
- [ ] It is checked against the §4 Booch and SOLID checklist with no unaddressed red flags.
- [ ] Tests were added or updated and the full suite passes. The traceability matrix is updated.
- [ ] Javadoc, `README.md`, `design.md` and diagrams reflect the change.
- [ ] `original/ExplodingKittens.java` is untouched.
