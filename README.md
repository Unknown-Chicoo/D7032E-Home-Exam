# D7032E-Home-Exam

Exploding Kittens, refactored from `original/ExplodingKittens.java`.

## Build and run

Requires **Java 21** or later (the code uses pattern-matching `switch` and `List.getFirst()`).

Expansions are found with `ServiceLoader`, which reads
`resources/META-INF/services/main.core.expansion.Expansion`. The `resources` folder must therefore
be on the classpath. Without it the game stops at once with "No expansions found".

With the scripts (Linux, macOS, WSL, Git Bash):

```sh
./build.sh                    # compile into build/prod and copy resources/
./run.sh                      # build, then start the menu: local or online game
./run.sh bots 4 42            # build, then a bots-only game: [count] [seed]
./run.sh client <host> 2048   # build, then join a hosted game
./run-tests.sh                # compile everything and run all tests
```

By hand:

```sh
javac -d out $(find src -name '*.java')
java -cp out:resources main.Main                 # menu: local or online game
java -cp out:resources main.Main bots 4 42       # bots-only game: [count] [seed]
java -cp out:resources main.Main client <host> 2048   # join a hosted game
```

On Windows, use `;` instead of `:` in the classpath.

**IntelliJ:** right-click `resources` → *Mark Directory as* → *Resources Root*, so that it is copied
to the output folder.

## Tests

`./run-tests.sh` compiles `src/` and `test/` and runs all JUnit 5 tests. It copies `resources/`
itself. Any arguments are passed on to the JUnit console launcher:

```sh
./run-tests.sh                                                   # all tests
./run-tests.sh --details=summary                                 # all tests, counts only
./run-tests.sh --select-class main.core.engine.NopeResolverTest  # one test class
./run-tests.sh --select-method 'main.expansions.base.effects.AttackEffectTest#rule11a_attackGivesNextPlayerTwoTurns'
./run-tests.sh --select-package main.expansions.base             # one package and its subpackages
```

By hand (after `./run-tests.sh` or the `javac` steps in it have filled `build/`):

```sh
java -jar lib/junit-platform-console-standalone-1.14.4.jar execute \
    --class-path build/prod:build/test --select-class main.core.engine.NopeResolverTest
```

**IntelliJ:** mark `test` as *Test Sources Root* and add
`lib/junit-platform-console-standalone-1.14.4.jar` as a Test-scope library; then run a class or
method from the gutter icon.

`test/` mirrors the packages under `src/`, and test names cite the rule they check (e.g.
`rule11a_attackGivesNextPlayerTwoTurns`). Shared helpers live in `test/main/testing`:

- `ScriptedController`: a `PlayerController` that answers from queues the test fills and records
  what it was asked;
- `RecordingListener`: keeps every event, public and private;
- `Table`: a hand-built base game (players `P1`…`Pn`, chosen hands and deck, seeded
  `JavaRandomizer(42)`, 50 ms Nope window);
- `InMemoryChannel`: a `MessageChannel` for `RemoteController` tests, with no sockets.
