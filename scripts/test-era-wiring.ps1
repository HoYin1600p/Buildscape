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
foreach ($variant in @('v118x', 'v121x', 'v26x')) {
    Assert-Contains $commonBuild ("(?m)^\s*" + [regex]::Escape($variant) + "\s*\{") "common must define source set $variant"
    Assert-Contains $commonBuild ([regex]::Escape($variant + 'Elements')) "common must expose configuration $($variant)Elements"
}

$targets = [ordered]@{
    'forge/1.18.2/build.gradle' = 'v118xElements'
    'fabric/1.18.2/build.gradle' = 'v118xElements'
    'forge/1.21.1/build.gradle' = 'v121xElements'
    'fabric/1.21.1/build.gradle' = 'v121xElements'
    'neoforge/1.21.1/build.gradle' = 'v121xElements'
    'forge/26.2/build.gradle' = 'v26xElements'
    'fabric/26.2/build.gradle' = 'v26xElements'
    'neoforge/26.2/build.gradle' = 'v26xElements'
    'forge/26.3/build.gradle' = 'v26xElements'
    'fabric/26.3/build.gradle' = 'v26xElements'
    'neoforge/26.3/build.gradle' = 'v26xElements'
}

foreach ($entry in $targets.GetEnumerator()) {
    $configurationPattern = 'configuration:\s*.*' + [regex]::Escape($entry.Value)
    Assert-Contains $entry.Key $configurationPattern "$($entry.Key) must depend on :common configuration $($entry.Value)"
}

Assert-Contains $commonBuild "options\.release\.set\(17\)" 'v118x compilation must target Java 17'
Assert-Contains $commonBuild "options\.release\.set\(21\)" 'v121x compilation must target Java 21'
Assert-Contains $commonBuild "options\.release\.set\(25\)" 'v26x compilation must target Java 25'

if ($failures.Count -gt 0) {
    $failures | ForEach-Object { Write-Error $_ }
    exit 1
}

Write-Output 'PASS: all eleven loader targets select an explicit common VersionCluster variant'
