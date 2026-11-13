$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$failures = [System.Collections.Generic.List[string]]::new()

function Assert-Contains {
    param([string]$Path, [string]$Pattern, [string]$Message)
    $content = Get-Content -LiteralPath (Join-Path $projectRoot $Path) -Raw
    if ($content -notmatch $Pattern) {
        $script:failures.Add($Message)
    }
}

$commonBuild = 'common/build.gradle'
foreach ($variant in @('minecraft1182', 'minecraft1211', 'minecraft262', 'minecraft263')) {
    Assert-Contains $commonBuild ("(?m)^\s*" + [regex]::Escape($variant) + "\s*\{") "common must define source set $variant"
    Assert-Contains $commonBuild ([regex]::Escape($variant + 'Elements')) "common must expose configuration $($variant)Elements"
}

$targets = [ordered]@{
    'fabric/1.18.2/build.gradle' = 'minecraft1182Elements'
    'forge/1.21.1/build.gradle' = 'minecraft1211Elements'
    'fabric/1.21.1/build.gradle' = 'minecraft1211Elements'
    'neoforge/1.21.1/build.gradle' = 'minecraft1211Elements'
    'forge/26.2/build.gradle' = 'minecraft262Elements'
    'fabric/26.2/build.gradle' = 'minecraft262Elements'
    'neoforge/26.2/build.gradle' = 'minecraft262Elements'
    'forge/26.3/build.gradle' = 'minecraft263Elements'
    'fabric/26.3/build.gradle' = 'minecraft263Elements'
    'neoforge/26.3/build.gradle' = 'minecraft263Elements'
}

Assert-Contains 'forge/1.18.2/build.gradle' 'common/src/1\.18\.2/java' 'Forge 1.18.2 must compile its versioned common sources'

foreach ($entry in $targets.GetEnumerator()) {
    $configurationPattern = 'configuration:\s*.*' + [regex]::Escape($entry.Value)
    Assert-Contains $entry.Key $configurationPattern "$($entry.Key) must depend on :common configuration $($entry.Value)"
}

Assert-Contains $commonBuild "options\.release\.set\(17\)" 'Minecraft 1.18.2 compilation must target Java 17'
Assert-Contains $commonBuild "options\.release\.set\(21\)" 'Minecraft 1.21.1 compilation must target Java 21'
Assert-Contains $commonBuild "options\.release\.set\(25\)" 'Minecraft 26.2 compilation must target Java 25'

if ($failures.Count -gt 0) {
    $failures | ForEach-Object { Write-Error $_ }
    exit 1
}

Write-Output 'PASS: all eleven loader targets select an explicit common VersionCluster variant'
