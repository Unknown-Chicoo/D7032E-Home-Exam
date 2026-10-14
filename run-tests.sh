#!/bin/sh
# Compiles src/ and test/ with plain javac and runs every JUnit 5 test.
set -e
cd "$(dirname "$0")"
JUNIT=lib/junit-platform-console-standalone-1.14.4.jar
rm -rf build
javac -d build/prod $(find src -name '*.java')
cp -r resources/. build/prod/
TESTS=$(find test -name '*.java' 2>/dev/null || true)
if [ -n "$TESTS" ]; then
    javac -d build/test -cp build/prod:$JUNIT $TESTS
else
    mkdir -p build/test
fi
java -jar $JUNIT execute --class-path build/prod:build/test --scan-class-path --disable-banner "$@"
