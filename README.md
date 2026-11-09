# Hypixel-Skyblock-Singleplayer

A Fabric mod project porting selected Hypixel SkyBlock systems into Minecraft singleplayer.

## Supported Minecraft versions

- Minecraft 26.3 (primary build target)
- Minecraft 26.2 (the project plan's shared-jar compatibility target)

The project plan in [`.azure/project-plan.md`](.azure/project-plan.md) is the source of truth for feature scope, implementation order, research references, and acceptance criteria.

## Development requirements

- Java 25
- Gradle Wrapper (use `./gradlew`, not a separately installed Gradle)
- Fabric Loom / Fabric Loader versions configured in `gradle.properties`

## Build

Windows:

```bat
gradlew.bat build
```

Linux/macOS:

```bash
./gradlew build
```

The distributable mod jar is written to `build/libs/`.

## GitHub workflow

GitHub Actions runs the Gradle build on pushes and pull requests targeting `main`. The workflow uses Java 25.

## Repository hygiene

The `.gitignore` excludes generated Gradle/build output and the local Minecraft `run/` directory. In particular, local worlds, saves, logs, crash reports, and runtime configuration should not be committed.

Keep source code, resources, Gradle wrapper files, and `.azure/` project documentation in version control.
