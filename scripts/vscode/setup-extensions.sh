#!/bin/bash
# Setup VS Code Extensions for Payroc SDK Java Development (macOS/Linux)
# This script installs the recommended extensions for developing the Payroc Java SDK

echo "Installing VS Code extensions for Payroc SDK Java development..."
echo ""

# Check if code command is available
if ! command -v code &> /dev/null; then
    echo "Error: VS Code command 'code' not found in PATH."
    echo "Please ensure VS Code is installed and the 'code' command is available."
    echo "You can add it to PATH by opening VS Code and running 'Shell Command: Install code command in PATH' from the command palette."
    exit 1
fi

# Extensions to install
extensions=(
    "vscjava.vscode-java-pack"
    "redhat.java"
    "vscjava.vscode-java-debug"
    "vscjava.vscode-gradle"
    "sonarsource.sonarlint-vscode"
    "shengchen.vscode-checkstyle"
    "VisualStudioExptTeam.vscodeintellicode"
)

# Install each extension
for extension in "\"; do
    echo "Installing \..."
    code --install-extension "\" --force
    if [ $? -eq 0 ]; then
        echo "Installed: \"
    else
        echo "Failed to install: \"
    fi
done

echo ""
echo "Extensions installation complete!"
echo ""
echo "Next steps:"
echo "1. Reload VS Code (Cmd+Shift+P > Reload Window)"
echo "2. Build the project: ./gradlew build"
echo "3. Run tests from terminal: ./gradlew test"
echo ""
echo "For more information, see CONTRIBUTING.md"
