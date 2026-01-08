# Auto-Push Script for BuildScape
# This script monitors for changes and auto-commits/pushes them every 1 minute

param(
    [int]$IntervalSeconds = 60,  # Check every 60 seconds
    [string]$Branch = "1.18.2-U3-2"
)

$projectPath = "Z:\DGA mods\1.18.2-backup\Buildscape-1.18.2"

Write-Host "Starting auto-push monitor for BuildScape..." -ForegroundColor Green
Write-Host "Project: $projectPath" -ForegroundColor Cyan
Write-Host "Branch: $Branch" -ForegroundColor Cyan
Write-Host "Check interval: $IntervalSeconds seconds" -ForegroundColor Cyan
Write-Host "Press Ctrl+C to stop monitoring`n" -ForegroundColor Yellow

Set-Location $projectPath

while ($true) {
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    
    # Check for changes
    $status = git status --porcelain
    
    if ($status) {
        Write-Host "[$timestamp] Changes detected!" -ForegroundColor Yellow
        
        # Get list of modified/new files
        $files = git status --short
        Write-Host $files -ForegroundColor Gray
        
        # Add all changes
        git add -A
        
        # Create commit message with timestamp
        $commitMsg = "Auto-commit: Changes at $timestamp"
        git commit -m $commitMsg
        
        # Push to remote
        Write-Host "Pushing to origin/$Branch..." -ForegroundColor Cyan
        $pushResult = git push origin $Branch 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✓ Successfully pushed changes!" -ForegroundColor Green
        } else {
            Write-Host "✗ Push failed: $pushResult" -ForegroundColor Red
        }
    } else {
        Write-Host "[$timestamp] No changes detected." -ForegroundColor DarkGray
    }
    
    # Wait for the interval
    Start-Sleep -Seconds $IntervalSeconds
}

