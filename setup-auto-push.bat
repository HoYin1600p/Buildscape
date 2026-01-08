@echo off
echo ============================================
echo BuildScape Auto-Push Setup
echo ============================================
echo.
echo This will create a Windows Task Scheduler job to auto-push changes every minute.
echo.
pause

:: Create Task Scheduler job
schtasks /create /tn "BuildScape Auto-Push" /tr "powershell.exe -ExecutionPolicy Bypass -File \"Z:\DGA mods\1.18.2-backup\Buildscape-1.18.2\auto-push.ps1\"" /sc minute /mo 1 /f

if %errorlevel% equ 0 (
    echo.
    echo ✓ Successfully created scheduled task!
    echo   Task Name: BuildScape Auto-Push
    echo   Frequency: Every 1 minute
    echo.
    echo To manage the task:
    echo   - Start: schtasks /run /tn "BuildScape Auto-Push"
    echo   - Stop: schtasks /end /tn "BuildScape Auto-Push"
    echo   - Delete: schtasks /delete /tn "BuildScape Auto-Push" /f
    echo.
) else (
    echo.
    echo ✗ Failed to create scheduled task. Please run as Administrator.
    echo.
)

pause

