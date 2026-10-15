#!/bin/sh
# Builds, then starts the game; arguments go to main.Main.
#   ./run.sh                     menu: local or online game
#   ./run.sh bots [count] [seed] bots-only game, e.g. ./run.sh bots 5 42
#   ./run.sh client <host> [port] join a hosted game
set -e
cd "$(dirname "$0")"
./build.sh > /dev/null
exec java -cp build/prod main.Main "$@"
