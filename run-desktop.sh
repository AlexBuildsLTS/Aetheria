#!/bin/bash
# Simple script to run Aetheria desktop application
# This bypasses Gradle's LWJGL classpath issues

cd "$(dirname "$0")"

echo "Building project..."
./gradlew :desktop:jar --no-daemon

if [ $? -eq 0 ]; then
    echo "Running Aetheria..."
    java -jar desktop/build/libs/Aetheria-desktop.jar
else
    echo "Build failed!"
    exit 1
fi
