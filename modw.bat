@echo off
setlocal enabledelayedexpansion
set "ROOT_DIR=%~dp0"
set "ROOT_DIR=%ROOT_DIR:~0,-1%"

set "MC=1.20.1"
set "LOADER=fabric"
set "EXTRA_ARGS="

if /i "%~1"=="setup-dev" goto setup_dev

:parse
if "%~1"=="" goto run
set "ARG=%~1"
if /i "%ARG:~0,5%"=="-Pmc=" (
    set "MC=%ARG:~5%"
) else if /i "%ARG%"=="-Pmc" (
    set "MC=%~2"
    shift
) else if /i "%ARG:~0,9%"=="-Ploader=" (
    set "LOADER=%ARG:~9%"
) else if /i "%ARG%"=="-Ploader" (
    set "LOADER=%~2"
    shift
) else (
    set "EXTRA_ARGS=!EXTRA_ARGS! %1"
)
shift
goto parse

:run
set "JAVA_21="
set "JAVA_25="
if exist "%ROOT_DIR%\local.properties" (
    for /f "usebackq tokens=1,2 delims==" %%A in ("%ROOT_DIR%\local.properties") do (
        if "%%A"=="java.21.home" set "JAVA_21=%%B"
        if "%%A"=="java.25.home" set "JAVA_25=%%B"
    )
)
if "%JAVA_21%"=="" set "JAVA_21=%JAVA_21_HOME%"
if "%JAVA_25%"=="" set "JAVA_25=%JAVA_25_HOME%"

if "%LOADER%"=="forge" (
    if not "%JAVA_21%"=="" set "JAVA_HOME=%JAVA_21%"
    set "GRADLE_VER=8.10.2"
    set "DAEMON_JVM=21"
) else (
    if not "%JAVA_25%"=="" (
        set "JAVA_HOME=%JAVA_25%"
    ) else if not "%JAVA_21%"=="" (
        set "JAVA_HOME=%JAVA_21%"
    )
    set "GRADLE_VER=9.6.0"
    set "DAEMON_JVM=25"
)

set "TARGET_DIR=%ROOT_DIR%\.gradle\build-roots\%MC%\%LOADER%"
if not exist "%TARGET_DIR%\gradle\wrapper" mkdir "%TARGET_DIR%\gradle\wrapper"
echo rootProject.name = 'buildscape-%MC%-%LOADER%' > "%TARGET_DIR%\settings.gradle"
copy /y "%ROOT_DIR%\shared\build-logic\target.gradle" "%TARGET_DIR%\build.gradle" >nul
copy /y "%ROOT_DIR%\gradle.properties" "%TARGET_DIR%\gradle.properties" >nul
(
echo mc=%MC%
echo loader=%LOADER%
) >> "%TARGET_DIR%\gradle.properties"
if exist "%ROOT_DIR%\local.properties" copy /y "%ROOT_DIR%\local.properties" "%TARGET_DIR%\local.properties" >nul
copy /y "%ROOT_DIR%\gradle\wrapper\gradle-wrapper.jar" "%TARGET_DIR%\gradle\wrapper\gradle-wrapper.jar" >nul
copy /y "%ROOT_DIR%\gradlew.bat" "%TARGET_DIR%\gradlew.bat" >nul
copy /y "%ROOT_DIR%\gradlew" "%TARGET_DIR%\gradlew" >nul

(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=https\://services.gradle.org/distributions/gradle-%GRADLE_VER%-bin.zip
echo networkTimeout=10000
echo validateDistributionUrl=true
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
) > "%TARGET_DIR%\gradle\wrapper\gradle-wrapper.properties"

echo toolchainVersion=%DAEMON_JVM% > "%TARGET_DIR%\gradle\gradle-daemon-jvm.properties"

call "%TARGET_DIR%\gradlew.bat" -p "%TARGET_DIR%" -PtemplateRoot="%ROOT_DIR%" -Pmc=%MC% -Ploader=%LOADER% %EXTRA_ARGS%
exit /b %errorlevel%

:setup_dev
echo Configuring recursive submodule updates and initializing submodules...
git -C "%ROOT_DIR%" config submodule.recurse true
git -C "%ROOT_DIR%" submodule update --init --recursive
if exist "%ROOT_DIR%\scripts\hooks\prepare-commit-msg" (
    if not exist "%ROOT_DIR%\.git\hooks" mkdir "%ROOT_DIR%\.git\hooks"
    copy /y "%ROOT_DIR%\scripts\hooks\prepare-commit-msg" "%ROOT_DIR%\.git\hooks\prepare-commit-msg" >nul
    echo Installed .git/hooks/prepare-commit-msg
)
echo Developer setup complete.
exit /b 0
