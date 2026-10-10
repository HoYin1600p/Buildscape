param(
    [string]$Repo = (Split-Path -Parent (Split-Path -Parent $PSScriptRoot)),
    [string]$OutputDirectory = $env:BUILDSCAPE_SYNC_OUTPUT,
    [string]$Python = 'python',
    [string]$McJar = $env:BUILDSCAPE_SYNC_MC_JAR
)

$ErrorActionPreference = 'Stop'
if ([string]::IsNullOrWhiteSpace($OutputDirectory)) {
    # build/ is git-ignored, so reports never end up in a commit.
    $OutputDirectory = Join-Path $Repo 'build/sync-reports'
}
$Repo = (Resolve-Path -LiteralPath $Repo).Path
$OutputDirectory = [System.IO.Path]::GetFullPath($OutputDirectory)
New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$tool = Join-Path $Repo 'scripts/sync/sync.py'
$arguments = @('-B', $tool, '--repo', $Repo, 'status', '--fetch', '--json', '--output-dir', $OutputDirectory)
$result = & $Python @arguments
if ($LASTEXITCODE -ne 0) { throw 'Sync status/fetch failed; no apply was started.' }
$plans = @((($result -join "`n") | ConvertFrom-Json))
foreach ($plan in $plans) {
    if (@($plan.commits).Count -eq 0) { continue }
    # Keep full builds from competing with a game or another heavy run.
    $busy = Get-CimInstance Win32_Process | Where-Object {
        $_.Name -eq 'javaw.exe' -or
        ($_.Name -match '^(java|python|python3)\.exe$' -and
            $_.CommandLine -match 'GradleWorkerMain|GradleWrapperMain|pytest|ctrade_testrun')
    }
    if ($busy) { throw 'A game or heavy build/test run is active; leave the report for the next scheduled run.' }
    $applyArguments = @('-B', $tool, '--repo', $Repo, 'apply', '--fetch', '--target', $plan.target,
        '--output-dir', $OutputDirectory)
    if (-not [string]::IsNullOrWhiteSpace($McJar)) { $applyArguments += @('--mc-jar', $McJar) }
    & $Python @applyArguments
    if ($LASTEXITCODE -ne 0) { throw "Sync apply failed for $($plan.target); inspect the retained branch and report." }
}
