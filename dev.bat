@echo off
REM ============================================================
REM  NeoForge mod development environment for this machine.
REM  Sets JDK 21 + Gradle user home + local proxy + UTF-8, then runs gradlew.
REM
REM  Usage:  dev.bat build
REM          dev.bat runClient
REM          dev.bat runServer
REM          dev.bat runData
REM          dev.bat --stop
REM ============================================================

set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "GRADLE_USER_HOME=D:\DSH\gradle-home"

REM Local proxy is needed to reach GitHub / Gradle / NeoForge maven from this machine.
set "HTTP_PROXY=http://127.0.0.1:7890"
set "HTTPS_PROXY=http://127.0.0.1:7890"

REM CRITICAL: system locale is zh-CN / code page 936 (GBK). Every JVM the
REM NeoForge toolchain forks must be forced to UTF-8 or the Minecraft sources
REM get misread and the build fails with a package-info.java error.
set "JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Duser.language=en -Duser.country=US"

cd /d "%~dp0"
call gradlew.bat %*
