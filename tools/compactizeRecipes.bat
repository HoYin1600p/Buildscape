@echo off
setlocal
cd /d "%~dp0.."
if exist "%~dp0..\gradlew.bat" (
    echo Formatting BDRE Recipe Packs via Gradle...
    call "%~dp0..\gradlew.bat" compactizeRecipes
) else (
    echo Running python compactize script...
    python "%~dp0compactizeRecipes.py"
)
endlocal
