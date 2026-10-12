# DSH Test

[English](README.md) | **简体中文**

一个面向 Minecraft **1.21.1** 的 NeoForge 模组。

[![构建状态](https://github.com/CyeOvO/dshtest/actions/workflows/build.yml/badge.svg)](https://github.com/CyeOvO/dshtest/actions/workflows/build.yml)
[![最新版本](https://img.shields.io/github/v/release/CyeOvO/dshtest)](https://github.com/CyeOvO/dshtest/releases/latest)
[![许可证](https://img.shields.io/github/license/CyeOvO/dshtest)](LICENSE)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.259-orange)

一个小型 NeoForge 模组，提供游戏内界面用于修改世界的时间和天气。

---

## 下载

**[⬇ 最新版本](https://github.com/CyeOvO/dshtest/releases/latest)** — 在 Assets 区域下载
`dshtest-<版本号>.jar`。

## 环境要求

| | 版本 |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.259 或更高 |
| Java | 21 |

## 安装方法

1. 为 Minecraft 1.21.1 安装 [NeoForge](https://neoforged.net/)。
2. 在 [Releases](../../releases) 页面下载最新的 `dshtest-<版本号>.jar`。
3. 把 jar 放进 `.minecraft/mods` 文件夹。
4. 用 NeoForge 配置启动游戏。

## 功能

### 天气与时间控制界面

游戏中按 **`X`** 键打开控制界面。在里面可以：

- **设置时间**：拖动滑块选择时刻（界面直接显示 `06:00` 这样的时钟时间），
  然后点 *Set Time* 生效。
- **切换天气**：晴天、下雨、雷暴，各持续 10 分钟游戏时间。

按键可以在 **选项 → 控制 → DSH Test** 里改绑。

> **说明：** 天气和时间由服务端控制，所以本界面不会在本地直接修改，而是向
> 服务端发送请求。修改它们需要**权限等级 2**——和原版 `/weather`、`/time`
> 命令要求的等级相同。单机模式下你始终拥有该权限；联机时你需要是管理员。
> 权限不足时模组会在聊天栏给出提示，而不是默默失败。

### 语言

界面自带英文和简体中文，会跟随你的游戏语言设置。

## 从源码构建

```bash
git clone https://github.com/CyeOvO/dshtest.git
cd dshtest

# Windows
gradlew.bat build

# macOS / Linux
./gradlew build
```

编译好的模组 jar 会输出到 `build/libs/`。

启动一个已加载本模组的开发客户端：

```bash
gradlew.bat runClient     # Windows
./gradlew runClient       # macOS / Linux
```

## 项目结构

```
src/main/java/io/github/cyeovo/dshtest/
├── DSHTest.java                  主 @Mod 入口，网络包注册
├── DSHTestClient.java            客户端专用初始化
├── Config.java                   模组配置
├── client/
│   ├── DSHTestKeybinds.java      X 键按键绑定
│   └── WeatherTimeScreen.java    控制界面
└── network/
    ├── WeatherTimePayload.java   客户端 -> 服务端的请求
    └── WeatherTimeHandler.java   在服务端执行修改
src/main/resources/assets/dshtest/lang/
├── en_us.json
└── zh_cn.json
src/main/templates/META-INF/neoforge.mods.toml
```

模组名称、版本、作者、许可证等元数据在
[`gradle.properties`](gradle.properties) 里。

## 开发环境说明

本项目基于官方
[NeoForge MDK](https://github.com/NeoForgeMDKs/MDK-1.21.1-ModDevGradle)（ModDevGradle 变体）生成。

**系统区域设置为非 UTF-8 的 Windows 用户注意：** 构建会为所有 JVM 强制加上
`-Dfile.encoding=UTF-8`。否则 `javac` 会错误解析 UTF-8 编码的 Minecraft 源码并
导致编译失败。完整原因（包括为何重编译 Minecraft 时使用 Eclipse 编译器）见
[`SETUP.md`](SETUP.md)。

## 许可证

[MIT](LICENSE) © CyeOvO

---

Minecraft 是 Mojang Synergies AB 的商标。本项目与 Mojang 或 Microsoft 无隶属
关系，也未获得其认可。
