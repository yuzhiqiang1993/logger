# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android logging library project that provides a comprehensive logging solution for Android applications. The project consists of:

- **Main Library Module** (`logger/`): A reusable logging library (version 1.2.1)
- **Demo Application** (`app/`): Sample app demonstrating the library's functionality

The library supports multiple output targets (console, file, visual interface), JSON formatting, and customizable log configurations.

## Common Development Commands

### Building and Running
- **Build entire project**: `./gradlew build`
- **Run demo app**: `./gradlew :app:installDebug`
- **Build library release**: `./gradlew :logger:assembleRelease`
- **Clean project**: `./gradlew clean`

### Testing and Quality
- **Run tests**: `./gradlew test`
- **Run lint**: `./gradlew lint`
- **Run demo app tests**: `./gradlew :app:test`

### Publishing
- **Publish to Maven Central**: `./gradlew :logger:publishReleasePublicationToMavenCentral`
- **Publish with documentation**: `./gradlew :logger:publishAllPublicationsToMavenCentral`

## Architecture Overview

### Core Library Architecture

The library follows a modular, extensible architecture using several design patterns:

1. **Strategy Pattern**: Different printer implementations (`AbsPrinter`)
   - `ConsoleLogPrinter`: Logs to Android Logcat
   - `FileLogPrinter`: Writes to local files with rotation
   - `ViewLogPrinter`: Displays in visual UI

2. **Builder Pattern**: Configuration classes for each printer
   - `ConsoleLogConfig.Builder()`
   - `FileLogConfig.Builder()`
   - `ViewLogConfig.Builder()`

3. **Singleton Pattern**: Main `Logger` object with static methods

### Package Structure

```
logger/src/main/java/com/yzq/logger/
├── Logger.kt                    # Main entry point (object)
├── common/                      # Utilities and extensions
├── console/                     # Console printer implementation
├── core/                        # Base interfaces (AbsPrinter, AbsLogConfig)
├── data/                        # Data models and enums
├── file/                        # File logging with rotation
└── view/                        # Visual log viewer
    ├── core/                    # View core components
    ├── log_view/                # Log viewing activity
    └── popupwindow/             # UI components
```

### Key Classes

- **`Logger`**: Main singleton object for all logging operations
- **`AbsPrinter`**: Abstract base class for all printer implementations
- **`FileLogPrinter`**: Handles file logging with automatic rotation
- **`ViewLogPrinter`**: Provides visual log interface with filtering
- **`ConsoleLogPrinter`**: Enhanced console logging with formatting

## Library Usage Pattern

### Initialization
```kotlin
// Configure printers
val consolePrinter = ConsoleLogPrinter.getInstance(
    ConsoleLogConfig.Builder()
        .enable(true)
        .showStackTrace(true)
        .showThreadInfo(true)
        .build()
)

val filePrinter = FileLogPrinter.getInstance(
    FileLogConfig.Builder()
        .enable(true)
        .writeLogInterval(10)
        .logCapacity(100)
        .build()
)

val viewPrinter = ViewLogPrinter.getInstance(
    ViewLogConfig.Builder()
        .enable(true)
        .cacheSize(1000)
        .build()
)

// Register printers
Logger.addPrinter(consolePrinter)
    .addPrinter(filePrinter)
    .addPrinter(viewPrinter)
```

### Logging Methods
- Standard levels: `v()`, `d()`, `i()`, `w()`, `e()`, `wtf()`
- With custom tags: `Logger.i("tag", "message")`
- With exceptions: `Logger.e("tag", "message", exception)`
- JSON support: `Logger.json(jsonObject.toString())`
- Thread info variants: `it()`, `dt()`, `vt()`, etc.

## Build Configuration

### Gradle Setup
- Uses **Version Catalog** (`gradle/libs.versions.toml`) for dependency management
- **Kotlin DSL** for all build scripts
- **Custom XeonYu plugins** for Android builds
- **Vanniktech Publishing** plugin for Maven Central releases

### Dependencies
- **Core**: AndroidX, Material Design, Kotlin Coroutines
- **XeonYu ecosystem**: Custom utility libraries
- **JSON**: Built-in Android JSON support (JSONObject, JSONArray)
- **ViewBinding**: Enabled for UI components

### Version Information
- **Compile SDK**: 34
- **Target SDK**: 33
- **Min SDK**: 23 (Android 6.0+)
- **Library Version**: 1.2.1
- **Kotlin**: 1.7.21

## Development Guidelines

### Adding New Features
1. Extend `AbsPrinter` for new output targets
2. Create corresponding `AbsLogConfig` subclass with Builder pattern
3. Add new log methods to `Logger` object if needed
4. Update demo app to showcase new features

### File Logging Configuration
- Files follow naming pattern: `log-YYYY-MM-DD-N`
- Automatic rotation when size limits reached
- Background writing with configurable intervals
- Automatic cleanup based on retention policies

### Visual Log Viewer
- Located in `view/log_view/LogViewActivity.kt`
- Supports filtering by log level and keywords
- Long-press to copy log entries
- RecyclerView adapter for efficient scrolling

## Testing

### Test Structure
- Unit tests for each printer implementation
- Integration tests for file operations
- UI tests for visual log viewer
- Demo app serves as functional testing

### Running Tests
- Run all tests: `./gradlew test`
- Run specific module tests: `./gradlew :logger:test`
- Run debug build tests: `./gradlew :app:testDebugUnitTest`

## Important Notes

- The library uses **synchronized collections** for thread-safe printer management
- **AppContext** from XeonYu library is used for application context access
- File logging requires storage permissions on older Android versions
- Visual log viewer uses ViewBinding for type-safe view references
- All configuration classes use immutable properties for safety