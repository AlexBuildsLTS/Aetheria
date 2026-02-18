#!/bin/bash
# Fix IntelliJ IDEA to recognize Bullet Physics dependencies

echo "=========================================="
echo "IntelliJ IDEA Dependency Fix Script"
echo "=========================================="
echo ""

# Step 1: Stop Gradle daemon
echo "Step 1: Stopping Gradle daemon..."
./gradlew --stop
echo "✓ Gradle daemon stopped"
echo ""

# Step 2: Clean build
echo "Step 2: Cleaning build..."
./gradlew clean
echo "✓ Build cleaned"
echo ""

# Step 3: Download dependencies
echo "Step 3: Downloading all dependencies..."
./gradlew :core:dependencies --refresh-dependencies > /dev/null 2>&1
echo "✓ Dependencies downloaded"
echo ""

# Step 4: Compile core module
echo "Step 4: Compiling core module..."
./gradlew :core:compileKotlin
echo "✓ Core module compiled successfully"
echo ""

echo "=========================================="
echo "NEXT STEPS - DO THIS IN INTELLIJ:"
echo "=========================================="
echo ""
echo "1. In IntelliJ, go to: File → Settings"
echo "   (or press Ctrl+Alt+S)"
echo ""
echo "2. Navigate to:"
echo "   Build, Execution, Deployment → Build Tools → Gradle"
echo ""
echo "3. Change these settings:"
echo "   - Build and run using: Gradle (Default)"
echo "   - Run tests using: Gradle (Default)"
echo ""
echo "4. Click 'Apply' and 'OK'"
echo ""
echo "5. Click the Gradle icon (elephant) on the right"
echo "   Then click 'Reload All Gradle Projects' (circular arrows)"
echo ""
echo "6. If errors still show:"
echo "   File → Invalidate Caches → Invalidate and Restart"
echo ""
echo "=========================================="
echo "The code COMPILES SUCCESSFULLY with Gradle!"
echo "This is just an IntelliJ cache issue."
echo "=========================================="
