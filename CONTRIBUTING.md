# Contributing to Payroc Java SDK

## Getting Started

### Prerequisites

To build and test this source code, you'll need:

- **Java 11 or higher** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **Gradle** - [Install](https://gradle.org/install/) (build system)
- **Git** - [Download](https://git-scm.com/)

### Installation

Clone the repository and build the project:

```bash
git clone https://github.com/payroc/payroc-sdk-java.git
cd payroc-sdk-java
./gradlew build
```

On Windows, use `gradlew.bat` instead:

```bash
gradlew.bat build
```

### Building

Build the project:

```bash
./gradlew build
```

Build without running tests:

```bash
./gradlew build -x test
```

### Project Structure

- `src/main/java/` - Main SDK source code
- `src/test/java/` - Test files
- `sample-app/` - Sample application demonstrating SDK usage
- `gradle/` - Gradle wrapper files
- `build.gradle` - Build configuration

## Testing

### Overview

This project has two types of tests:

1. **Unit/Wire Tests** - Mock-based tests that run without external dependencies
2. **Integration Tests** - Tests that run against the Payroc UAT environment

### Running Unit/Wire Tests

**Important:** All commands must be run from the **project root directory** (`payroc-sdk-java/`).

#### Prerequisites

- **No external services required** - Tests use MockWebServer (included as a test dependency)
- **No environment variables required** - Unit tests work out of the box
- **No build step required** - Tests will compile automatically

#### Quick Start

Execute all unit/wire tests:

```bash
./gradlew test
```

**Windows:**
```powershell
.\gradlew test
```

That's it! The tests will:
1. Automatically compile the project if needed
2. Run all unit and wire tests (~195 tests)
3. Complete in about 1-2 minutes

#### Common Test Commands

Force re-run all tests (ignoring Gradle cache):

```bash
./gradlew test --rerun-tasks
```

Run specific test class:

```bash
./gradlew test --tests "ClassName"
```

Run tests matching a pattern:

```bash
./gradlew test --tests "*Payments*"
```

Run tests with verbose output:

```bash
./gradlew test --info
```

View test results in HTML report:

**macOS/Linux:**
```bash
./gradlew test && open build/reports/tests/test/index.html
```

**Windows (PowerShell):**
```powershell
.\gradlew test; start build/reports/tests/test/index.html
```

View test results summary (Windows PowerShell):

```powershell
.\gradlew test --rerun-tasks; $sum=0; $passed=0; $failed=0; Get-ChildItem build/test-results/test/*.xml | ForEach-Object { [xml]$xml = Get-Content $_; $sum += $xml.testsuite.tests; $passed += ($xml.testsuite.tests - $xml.testsuite.failures - $xml.testsuite.errors); $failed += ($xml.testsuite.failures + $xml.testsuite.errors) }; Write-Host "Total Tests: $sum | Passed: $passed | Failed: $failed"
```

### Running Integration Tests

Integration tests run against the Payroc UAT environment and require API credentials.

#### Prerequisites

Set the following environment variables before running integration tests:

```bash
export PAYROC_API_KEY_PAYMENTS="your-payments-api-key"
export PAYROC_API_KEY_GENERIC="your-generic-api-key"
export TERMINAL_ID_AVS="your-terminal-id-with-avs"
export TERMINAL_ID_NO_AVS="your-terminal-id-without-avs"
```

**Windows (PowerShell):**
```powershell
$env:PAYROC_API_KEY_PAYMENTS="your-payments-api-key"
$env:PAYROC_API_KEY_GENERIC="your-generic-api-key"
$env:TERMINAL_ID_AVS="your-terminal-id-with-avs"
$env:TERMINAL_ID_NO_AVS="your-terminal-id-without-avs"
```

#### Running Integration Tests

Run all integration tests:

```bash
./gradlew test --tests "com.payroc.api.integration.*"
```

Run specific integration test category:

```bash
./gradlew test --tests "com.payroc.api.integration.cardpayments.refunds.*"
```

Run a specific integration test:

```bash
./gradlew test --tests "com.payroc.api.integration.cardpayments.refunds.CreateTests.smokeTest"
```

> **Note:** Integration tests are tagged with `@Tag("integration")` and are located in `src/test/java/com/payroc/api/integration/`. See `src/test/java/com/payroc/api/integration/README.md` for more details.

### Prerequisites for Running Tests in VS Code

To run and debug tests directly in VS Code, you'll need to:

1. **Install project dependencies** - Required before running tests:

```bash
./gradlew build
```

2. **Install recommended VS Code extensions** - For development:

- **[Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)** - Comprehensive Java development suite
- **[Language Support for Java](https://marketplace.visualstudio.com/items?itemName=redhat.java)** - Red Hat Java language support
- **[Debugger for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-debug)** - Lightweight Java debugger
- **[Gradle for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle)** - Gradle integration
- **[SonarLint](https://marketplace.visualstudio.com/items?itemName=sonarsource.sonarlint-vscode)** - Code quality analysis
- **[Checkstyle for Java](https://marketplace.visualstudio.com/items?itemName=shengchen.vscode-checkstyle)** - CheckStyle linting
- **[Visual Studio IntelliCode](https://marketplace.visualstudio.com/items?itemName=VisualStudioExptTeam.vscodeintellicode)** - AI-assisted code completion

#### Quick Setup

We've provided a setup script to automatically install all recommended extensions:

**Windows (PowerShell):**
```powershell
.\vscode-scripts\setup-extensions.ps1
```

**macOS/Linux (Bash):**
```bash
bash vscode-scripts/setup-extensions.sh
```

After running the script, reload VS Code (`Ctrl+Shift+P` > `Reload Window`) to activate the extensions.

> **Note:** The setup script installs VS Code extensions only. Make sure you've already built the project with `./gradlew build`.

> **Recommendation:** While VS Code has a Test Runner extension available, we recommend running tests from the command line using `./gradlew test` for the most reliable results. This is the standard approach for CI/CD pipelines and development workflows.

### Test Configuration

Tests are configured in `build.gradle` and use JUnit 5 testing framework. The project uses:

- **MockWebServer** (OkHttp) for mocking HTTP requests in unit/wire tests
- **JUnit 5** for test framework
- **No external services** required for unit/wire tests

## Code Quality

### Building and Checking

Build and run all checks:

```bash
./gradlew check
```

### Testing

Run the full test suite:

```bash
./gradlew test
```

### Static Analysis

The project uses CheckStyle and SpotBugs for code quality analysis:

```bash
./gradlew checkstyleMain
./gradlew spotbugsMain
```

### Code Coverage

Generate code coverage reports:

```bash
./gradlew test jacocoTestReport
```

Coverage reports will be available in `build/reports/jacoco/`

### Building the Sample App

Build the included sample application:

```bash
./gradlew :sample-app:build
```

Run the sample app:

```bash
./gradlew :sample-app:run
```

## About Generated Code

**Important**: Most files in this SDK are automatically generated by [Fern](https://buildwithfern.com) from the API definition. Direct modifications to generated files will be overwritten the next time the SDK is generated.

### Generated Files

The following directories contain generated code:
- `src/main/java/com/payroc/api/resources/` - API client resources and types
- `src/main/java/com/payroc/api/types/` - Data types and models

### How to Customize

If you need to customize the SDK, you have two options:

#### Option 1: Use `.fernignore`

For custom code that should persist across SDK regenerations:

1. Create or edit the `.fernignore` file in the project root
2. Add file patterns for files you want to preserve (similar to `.gitignore` syntax)
3. Add your custom code to those files

Files listed in `.fernignore` will not be overwritten when the SDK is regenerated.

For more information, see the [Fern documentation on custom code](https://buildwithfern.com/learn/sdks/overview/custom-code).

#### Option 2: Contribute to the Generator

If you want to change how code is generated for all users of this SDK:

1. The Java SDK generator lives in the [Fern repository](https://github.com/fern-api/fern)
2. Generator code is located at `generators/java/sdk/`
3. Follow the [Fern contributing guidelines](https://github.com/fern-api/fern/blob/main/CONTRIBUTING.md)
4. Submit a pull request with your changes to the generator

This approach is best for:
- Bug fixes in generated code
- New features that would benefit all users
- Improvements to code generation patterns

## Making Changes

### Workflow

1. Create a new branch for your changes
2. Make your modifications
3. Run tests to ensure nothing breaks: `./gradlew test`
4. Run all checks: `./gradlew check`
5. Build the project: `./gradlew build`
6. Commit your changes with a clear commit message
7. Push your branch and create a pull request

### Commit Messages

Write clear, descriptive commit messages that explain what changed and why.

### Code Style

This project follows Java naming conventions and best practices. Run `./gradlew check` before committing to ensure your code meets the project's standards.

## Questions or Issues?

If you have questions or run into issues:

1. Check the [Fern documentation](https://buildwithfern.com)
2. Search existing [GitHub issues](https://github.com/payroc/payroc-sdk-java/issues)
3. Open a new issue if your question hasn't been addressed

## License

By contributing to this project, you agree that your contributions will be licensed under the same license as the project.
