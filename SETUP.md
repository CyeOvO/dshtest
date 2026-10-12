# NeoForge 模组开发环境

Minecraft **1.21.1** / NeoForge **21.1.259** 的 Java 模组项目，基于官方
[NeoForge MDK](https://github.com/NeoForgeMDKs/MDK-1.21.1-ModDevGradle)（ModDevGradle 变体）生成，
并已针对这台机器的具体情况配置好。

首次构建已通过：产出的模组 jar 在 `build/libs/examplemod-1.0.0.jar`。

---

## 一、已安装/已使用的工具链

| 组件 | 版本 | 位置 |
|---|---|---|
| JDK 21（编译目标） | 21.0.7 | `C:\Program Files\Java\jdk-21` |
| JDK 17（备用，做 1.20.x 模组时用） | 17.0.12 | `C:\Program Files\Java\jdk-17` |
| Gradle | 9.2.1（wrapper 自动下载） | `D:\DSH\gradle-home` |
| Git | 2.55.0 | `C:\Program Files\Git` |
| IntelliJ IDEA Community | 2025.2.6.2 | `C:\Program Files\JetBrains\...` |
| VS Code | 1.141.0 | `D:\Users\Afro\AppData\Local\Programs\Microsoft VS Code` |

> Gradle **不使用** `C:\Users\Afro\.gradle`，而是用 `D:\DSH\gradle-home`
> （通过环境变量 `GRADLE_USER_HOME` 指定），这样整套环境都留在工作区内。

---

## 二、这台机器上的三个关键配置（重要）

### 1. 代理：必须开在 `127.0.0.1:7890`

`github.com`、`services.gradle.org`、`maven.neoforged.net` 都无法直连，
必须依靠本地代理 **`127.0.0.1:7890`**。相关位置：

- **Git**（仓库级配置，已写入 `.git/config`）：
  ```
  http.sslBackend=openssl                              # 修复下面这个报错
  http.https://github.com.proxy=http://127.0.0.1:7890
  ```
  用 `openssl` 而不是默认的 `schannel`，是因为 schannel 在本机报
  `SEC_E_NO_CREDENTIALS (0x8009030e)`，导致任何 clone/pull 都失败。

- **Gradle**：写在 `D:\DSH\gradle-home\gradle.properties` 里。

**如果代理端口变了，这两个地方都要改。**

### 2. 编码：必须强制 UTF-8

系统区域是 **中文(简体) / 代码页 936（GBK）**。如果不强制 UTF-8，`javac`
会按 GBK 解码 Minecraft 的 UTF-8 源码，构建直接失败。

因此 `gradle.properties` 与 `dev.bat` 里都固定了：

```
-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Duser.language=en -Duser.country=US
```

**改这些文件时不要把这段删掉。**

### 3. 用 Eclipse 编译器（ECJ）编译 Minecraft

`build.gradle` 的 `neoForge { neoFormRuntime { useEclipseCompiler = true } }` 是**必需的**。

默认的 javac 在反编译→重编译 Minecraft 这一步会失败：

```
ERROR Line: 1, error while writing net.minecraft.world.ticks.package-info: null
Caused by: java.io.IOException: Compilation failed
```

已确认这不是你的代码问题：该 `package-info.java` 单独编译完全正常，
classpath（含 `jsr305`）也完整，换 JDK（Oracle 21.0.7 / JBR 21.0.9）同样报错。
是 javac 往 NFRT 的内存 jar 文件系统写 `package-info.class` 时出错。
**ECJ 能正常写入，所以改用 ECJ。** 这只影响 Minecraft 自身的重编译，
你写的模组代码仍由 Gradle 用 javac 编译。

---

## 三、日常工作流

**一律通过 `dev.bat` 执行**——它会自动设置 `JAVA_HOME`、`GRADLE_USER_HOME`、代理和 UTF-8：

```bat
cd D:\DSH\neoforge-mod

dev.bat build        :: 编译并打包出模组 jar
dev.bat runClient    :: 启动带你这个模组的开发版客户端
dev.bat runServer    :: 启动开发版服务端
dev.bat runData      :: 运行数据生成器（方块/物品模型、配方、语言文件等）
```

产物在 `build\libs\`。

> 请在**普通命令行窗口**里运行，不要在受限的沙箱 shell 里跑——
> Gradle 需要写 `D:\DSH\gradle-home` 并访问网络。

---

## 四、用 IDE 打开

**IntelliJ IDEA**（NeoForge 官方推荐）：

1. `File > Open` → 选择 `D:\DSH\neoforge-mod`
2. 等它自动导入 Gradle 项目
3. 把 Gradle 的 JVM 设为 JDK 21：`Settings > Build Tools > Gradle > Gradle JVM`
4. 运行配置 `runClient` / `runServer` 会自动出现在 Gradle 工具窗的
   `neoforge` 任务组下

**VS Code**：直接打开文件夹，Java 扩展会自动识别 Gradle 项目。

---

## 五、项目结构

```
neoforge-mod/
├── build.gradle                 构建配置（NeoForge 21.1.259、Java 21、ECJ）
├── gradle.properties            mod id / 名称 / 版本 / 包名
├── settings.gradle
├── dev.bat                      便捷启动脚本（环境变量都在这）
├── SETUP.md                     本文件
└── src/main/
    ├── java/com/example/examplemod/
    │   ├── ExampleMod.java          主 @Mod 类 + 示例方块/物品/创造模式标签页
    │   ├── ExampleModClient.java    仅客户端逻辑
    │   └── Config.java              示例配置项
    ├── resources/assets/examplemod/lang/en_us.json
    └── templates/META-INF/neoforge.mods.toml   模组元数据（构建时填充变量）
```

`src/generated/resources` 由 `runData` 生成，也会被打进 jar。

---

## 六、改成你自己的模组

模板目前叫 `examplemod`。改名时以下几处要**保持一致**：

1. `gradle.properties` → `mod_id`、`mod_name`、`mod_group_id`、`mod_authors`、`mod_license`
2. `ExampleMod.java` → `MODID` 常量 **和** `@Mod(...)` 注解
3. 目录 `src/main/java/com/example/examplemod/` → 你自己的包名
4. 目录 `src/main/resources/assets/examplemod/` → `assets/<你的mod_id>/`
5. `build.gradle` 不用改，它全部从 `gradle.properties` 读取

`mod_id` 必须是小写，且上述各处完全一致。

---

## 七、常见问题

**构建报 `package-info` 错误** → `useEclipseCompiler = true` 被删了，加回去。

**下载依赖失败 / 超时** → 本地代理没开，确认 `127.0.0.1:7890` 在监听。

**中文注释变乱码** → UTF-8 的 `-D` 参数被删了，见第二节第 2 点。

**首次构建很慢** → 正常。要反编译整个 Minecraft（约 1~2 分钟），
结果会缓存到 `D:\DSH\gradle-home\caches`，之后构建是秒级。
