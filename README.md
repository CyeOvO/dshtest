# DSH Test

A NeoForge mod for Minecraft **1.21.1**.

[![Build](https://github.com/CyeOvO/dshtest/actions/workflows/build.yml/badge.svg)](https://github.com/CyeOvO/dshtest/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/CyeOvO/dshtest)](https://github.com/CyeOvO/dshtest/releases/latest)
[![License](https://img.shields.io/github/license/CyeOvO/dshtest)](LICENSE)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.259-orange)

<!-- Replace this line with a one-sentence pitch once the mod does something specific. -->
This is a NeoForge test mod, used to verify the mod development environment.

---

## Download

**[⬇ Latest release](https://github.com/CyeOvO/dshtest/releases/latest)** — grab the
`dshtest-<version>.jar` file from the Assets section.

## Requirements

| | Version |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.259 or newer |
| Java | 21 |

## Installation

1. Install [NeoForge](https://neoforged.net/) for Minecraft 1.21.1.
2. Download the latest `dshtest-<version>.jar` from the [Releases](../../releases) page.
3. Drop the jar into your `.minecraft/mods` folder.
4. Launch the game with the NeoForge profile.

## Building from source

```bash
git clone https://github.com/CyeOvO/dshtest.git
cd dshtest

# Windows
gradlew.bat build

# macOS / Linux
./gradlew build
```

The compiled mod jar is written to `build/libs/`.

To launch a development client with the mod loaded:

```bash
gradlew.bat runClient     # Windows
./gradlew runClient       # macOS / Linux
```

## Project layout

```
src/main/java/io/github/cyeovo/dshtest/
├── DSHTest.java            main @Mod entry point
├── DSHTestClient.java      client-only setup
└── Config.java             mod configuration
src/main/resources/assets/dshtest/lang/en_us.json
src/main/templates/META-INF/neoforge.mods.toml
```

Mod metadata such as name, version, authors and license lives in
[`gradle.properties`](gradle.properties).

## Development environment notes

This project was generated from the official
[NeoForge MDK](https://github.com/NeoForgeMDKs/MDK-1.21.1-ModDevGradle) (ModDevGradle variant).

**Windows users on a non-UTF-8 system locale:** the build forces
`-Dfile.encoding=UTF-8` for all JVMs. Without it, `javac` misreads the UTF-8
Minecraft sources and fails. See [`SETUP.md`](SETUP.md) for the full rationale,
including why the Eclipse compiler is used for Minecraft recompilation.

## License

[MIT](LICENSE) © CyeOvO

---

Minecraft is a trademark of Mojang Synergies AB. This project is not affiliated
with or endorsed by Mojang or Microsoft.
