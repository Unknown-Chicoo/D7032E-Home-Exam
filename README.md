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
itself.
