# NeoForge 模组开发环境

Minecraft **1.21.1** / NeoForge **21.1.259** 的 Java 模组项目，基于官方
[NeoForge MDK](https://github.com/NeoForgeMDKs/MDK-1.21.1-ModDevGradle)（ModDevGradle 变体）生成，
并已针对这台机器的具体情况配置好。

- 模组：**DSH Test**（mod_id `dshtest`）
- GitHub 仓库：**https://github.com/CyeOvO/dshtest**
- 构建产物：`build/libs/dshtest-1.0.0.jar`

---

## 零、推送到 GitHub

仓库已建好并关联：

```
origin    https://github.com/CyeOvO/dshtest.git          ← 你的仓库
upstream  https://github.com/NeoForgeMDKs/MDK-...git     ← MDK 模板（可忽略）
```

Git 凭据没有写入明文文件，而是由 `gh` 保存。登录状态放在工作区内：

```
GH_CONFIG_DIR=D:\DSH\gh-config
```

**推送步骤**（在项目目录）：

```bat
set GH_CONFIG_DIR=D:\DSH\gh-config
set HTTPS_PROXY=http://127.0.0.1:7890

git add -A
git commit -m "你的改动说明"
git push
```

如果 `git push` 提示要用户名密码，说明 `gh` 的凭据没被用上，改用下面这条
（把 token 临时设进环境变量，不写入任何文件）：

```bat
for /f %i in ('gh auth token') do set GH_TOKEN=%i
git push https://x-access-token:%GH_TOKEN%@github.com/CyeOvO/dshtest.git main
```

> 注意：`gh` 的 OAuth token 目前包含 `repo` 和 `workflow` 两个权限。
> `workflow` 是推送 `.github/workflows/` 里的自动构建配置所必需的。

---

## 零之二、发布新版本

发布流程已经自动化：**打一个 tag 并推送，GitHub 自动构建、建 Release、附上 jar。**

```bat
git tag -a v1.0.1 -m "DSH Test 1.0.1"
git push origin v1.0.1
```

等 1~2 分钟，Release 会出现在
https://github.com/CyeOvO/dshtest/releases

**版本号怎么定**（`gradle.properties` 里的 `mod_version` 与 tag 要对应）：

| 改动性质 | 版本号 | 示例 |
|---|---|---|
| 修 bug | 第三位 +1 | `1.0.0` → `1.0.1` |
| 加功能 | 第二位 +1 | `1.0.0` → `1.1.0` |
| 不兼容改动 | 第一位 +1 | `1.0.0` → `2.0.0` |

发版前记得先把 `gradle.properties` 的 `mod_version` 改成对应版本并提交，
否则 jar 内部的版本号会和 tag 对不上。

**带 `-` 的 tag 会自动标记为预发布**，例如 `v1.1.0-beta.1`。

### 两条自动流程

| 工作流 | 触发条件 | 做什么 |
|---|---|---|
| `build.yml` | push / PR | 编译并验证，jar 可在 Actions 页面下载 |
| `release.yml` | 推送 `v*` tag | 编译 + 创建 Release + 附上 jar |

发布状态标记在 README 顶部，会自动更新。

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
├── README.md                    中文说明（仓库首页）
├── README.en-US.md              英文说明
├── SETUP.md                     本文件
└── src/main/
    ├── java/io/github/cyeovo/dshtest/
    │   ├── DSHTest.java              主 @Mod 类 + 网络包注册
    │   ├── DSHTestClient.java        仅客户端初始化
    │   ├── Config.java               配置项
    │   ├── client/
    │   │   ├── DSHTestKeybinds.java  X 键按键绑定
    │   │   └── WeatherTimeScreen.java 天气/时间控制界面
    │   └── network/
    │       ├── WeatherTimePayload.java  客户端 -> 服务端请求
    │       └── WeatherTimeHandler.java  服务端执行修改
    ├── resources/assets/dshtest/lang/
    │   ├── en_us.json
    │   └── zh_cn.json
    └── templates/META-INF/neoforge.mods.toml   模组元数据（构建时填充变量）
```

`src/generated/resources` 由 `runData` 生成，也会被打进 jar。

---

## 六、二次开发注意事项

模组已从模板改名完毕，现在是 `dshtest`。

**新增方块/物品时**，光注册 Java 类不够，还要补资源文件，否则游戏日志会刷
`Unable to load model` 警告：

```
src/main/resources/assets/dshtest/
├── blockstates/<名字>.json     方块状态
├── models/block/<名字>.json    方块模型
├── models/item/<名字>.json     物品模型
└── textures/block|item/<名字>.png
```

**改 `mod_id` 要同步的地方**（已经改好了，列出来是备查）：

1. `gradle.properties` → `mod_id`
2. `DSHTest.java` → `MODID` 常量 **和** `@Mod(...)` 注解
3. 目录 `src/main/java/<包名>/` → 你自己的包名
4. 目录 `src/main/resources/assets/<mod_id>/`
5. `build.gradle` 不用改，它全部从 `gradle.properties` 读取
6. 语言文件里的 `key.categories.<mod_id>` 等键名

`mod_id` 必须是小写，且上述各处完全一致。**已发布的模组不要再改 `mod_id`**，
否则老存档会认不出来。

**给界面加翻译**：`assets/dshtest/lang/` 下每个语言一个 json，键名要和代码里
`Component.translatable("...")` 的字符串一致。

---

## 七、常见问题

**构建报 `package-info` 错误** → `useEclipseCompiler = true` 被删了，加回去。

**下载依赖失败 / 超时** → 本地代理没开，确认 `127.0.0.1:7890` 在监听。

**中文注释变乱码** → UTF-8 的 `-D` 参数被删了，见第二节第 2 点。

**首次构建很慢** → 正常。要反编译整个 Minecraft（约 1~2 分钟），
结果会缓存到 `D:\DSH\gradle-home\caches`，之后构建是秒级。
