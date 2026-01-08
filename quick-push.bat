@echo off
cd /d "Z:\DGA mods\1.18.2-backup\Buildscape-1.18.2"

echo Checking for changes...
git status --short

echo.
echo Adding all changes...
git add -A

echo.
echo Committing...
git commit -m "Quick push: %date% %time%"

echo.
echo Pushing to origin/1.18.2-U3-2...
git push origin 1.18.2-U3-2

echo.
echo Done!
pause

