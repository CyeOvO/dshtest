# DSH Test

[简体中文](README.md) | **English**

A NeoForge mod for Minecraft **1.21.1**.

[![Build](https://github.com/CyeOvO/dshtest/actions/workflows/build.yml/badge.svg)](https://github.com/CyeOvO/dshtest/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/CyeOvO/dshtest)](https://github.com/CyeOvO/dshtest/releases/latest)
[![License](https://img.shields.io/github/license/CyeOvO/dshtest)](LICENSE)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.259-orange)

A small NeoForge mod that adds an in-game screen for changing the world time
and weather.

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

## Features

### Weather & Time control screen

Press **`X`** in-game to open the control screen. From there you can:

- **Set the time of day** with a slider (shows the clock time, e.g. `06:00`),
  then press *Set Time* to apply it.
- **Change the weather** to Clear, Rain, or Thunder for 10 in-game minutes.

The key can be rebound in **Options → Controls → DSH Test**.

> **Note:** Weather and time are controlled by the server, so the screen sends a
> request to the server rather than changing anything locally. Changing them
> requires **permission level 2** — the same level the vanilla `/weather` and
> `/time` commands need. In singleplayer you always have it; on a multiplayer
> server you need to be an operator. Without permission the mod replies with a
> chat message instead of silently doing nothing.

### Languages

The interface ships in English and Simplified Chinese, and follows your game
language setting.

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
├── DSHTest.java                  main @Mod entry point, payload registration
├── DSHTestClient.java            client-only setup
├── Config.java                   mod configuration
├── client/
│   ├── DSHTestKeybinds.java      the X key binding
│   └── WeatherTimeScreen.java    the control screen
└── network/
    ├── WeatherTimePayload.java   client -> server request
    └── WeatherTimeHandler.java   applies the change on the server
src/main/resources/assets/dshtest/lang/
├── en_us.json
└── zh_cn.json
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

This project was generated from the official
[NeoForge MDK](https://github.com/NeoForgeMDKs/MDK-1.21.1-ModDevGradle). The files
supplied by that template (`build.gradle`, `settings.gradle`, `gradlew`,
`.gitignore`, and similar) remain under their original license:
MIT © 2023 NeoForged project.

---

Minecraft is a trademark of Mojang Synergies AB. This project is not affiliated
with or endorsed by Mojang or Microsoft.
