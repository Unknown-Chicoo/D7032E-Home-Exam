#!/bin/sh
# Compiles src/ with plain javac into build/prod and copies resources/ (needed by ServiceLoader).
set -e
cd "$(dirname "$0")"
rm -rf build/prod
javac -Xlint:all -d build/prod $(find src -name '*.java')
cp -r resources/. build/prod/
echo "Built into build/prod"
