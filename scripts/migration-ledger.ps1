param(
    [ValidateSet('Generate', 'Validate')]
    [string]$Mode = 'Validate',
    [string]$LedgerPath
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$referenceRoot = Join-Path $projectRoot 'ref/forge/1.18.2'

if ([string]::IsNullOrWhiteSpace($LedgerPath)) {
    $LedgerPath = Join-Path $projectRoot 'migration/ledger.csv'
}
elseif (-not [System.IO.Path]::IsPathRooted($LedgerPath)) {
    $LedgerPath = Join-Path $projectRoot $LedgerPath
}

$allowedStatuses = @(
    'pending',
    'in_progress',
    'common',
    'era_adapter',
    'loader_adapter',
    'consolidated',
    'build_only',
    'deferred_vanilla_transition'
)
$completedStatuses = $allowedStatuses | Where-Object { $_ -notin @('pending', 'in_progress') }
$requiredColumns = @(
    'kind',
    'reference_path',
    'status',
    'active_paths',
    'era_coverage',
    'behavior_notes',
    'verification'
)
$activePathSet = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
$referencePathSet = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)

function Normalize-Path {
    param([string]$Path)
    return ($Path -replace '\\', '/').TrimStart('./')
}

function Get-RipgrepPath {
    $cmd = Get-Command rg -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    $candidates = @(
        "$env:LOCALAPPDATA\Programs\Antigravity IDE\_\resources\app\node_modules\@vscode\ripgrep\bin\rg.exe",
        "$env:LOCALAPPDATA\JetBrains\IntelliJIdea2026.2\acp-agents\cursor\2026.08.11\dist-package\rg.exe",
        "$env:LOCALAPPDATA\OpenAI\Codex\bin\26f5cb65bac96647\rg.exe"
    )
    foreach ($cand in $candidates) {
        if (Test-Path -LiteralPath $cand) { return $cand }
    }
    throw "ripgrep (rg) is required but could not be located."
}

function Get-ReferenceFiles {
    $rows = [System.Collections.Generic.List[object]]::new()
    $roots = @(
        @{ Kind = 'java'; Path = 'src/main/java' },
        @{ Kind = 'resource'; Path = 'src/main/resources' }
    )

    $rg = Get-RipgrepPath
    foreach ($root in $roots) {
        $absoluteRoot = Join-Path $referenceRoot $root.Path
        if (-not (Test-Path -LiteralPath $absoluteRoot)) {
            throw "Reference root does not exist: $absoluteRoot"
        }

        $files = & $rg --files $absoluteRoot
        if ($LASTEXITCODE -ne 0) {
            throw "Failed to enumerate reference files below $absoluteRoot"
        }

        foreach ($file in $files) {
            $absoluteFile = [System.IO.Path]::GetFullPath($file)
            $relativeToReference = $absoluteFile.Substring($referenceRoot.Length).TrimStart('\', '/')
            $rows.Add([pscustomobject]@{
                kind = $root.Kind
                reference_path = Normalize-Path $relativeToReference
            })
        }
    }

    return $rows | Sort-Object kind, reference_path
}

function Get-ExactActivePath {
    param([string]$Kind, [string]$ReferencePath)

    if ($Kind -eq 'java') {
        $suffix = $ReferencePath.Substring('src/main/java/'.Length)
        $candidate = "common/src/main/java/$suffix"
    }
    else {
        $suffix = $ReferencePath.Substring('src/main/resources/'.Length)
        $candidate = "common/src/main/resources/$suffix"
    }

    if ($activePathSet.Contains($candidate)) {
        return $candidate
    }

    return ''
}

function Read-Ledger {
    if (-not (Test-Path -LiteralPath $LedgerPath)) {
        throw "Ledger does not exist: $LedgerPath"
    }

    return @(Import-Csv -LiteralPath $LedgerPath)
}

function Test-Ledger {
    param([object[]]$Rows)

    $errors = [System.Collections.Generic.List[string]]::new()
    if ($Rows.Count -eq 0) {
        $errors.Add('ledger contains no rows')
        return $errors
    }

    $presentColumns = @($Rows[0].PSObject.Properties.Name)
    foreach ($column in $requiredColumns) {
        if ($column -notin $presentColumns) {
            $errors.Add("missing required column '$column'")
        }
    }

    $duplicates = $Rows | Group-Object reference_path | Where-Object Count -gt 1
    foreach ($duplicate in $duplicates) {
        $errors.Add("duplicate reference_path '$($duplicate.Name)'")
    }

    foreach ($row in $Rows) {
        $path = Normalize-Path ([string]$row.reference_path)
        $status = [string]$row.status
        if ($row.kind -notin @('java', 'resource')) {
            $errors.Add("${path}: unknown kind '$($row.kind)'")
        }
        if ($status -notin $allowedStatuses) {
            $errors.Add("${path}: unknown status '$status'")
        }
        if (-not $referencePathSet.Contains($path)) {
            $errors.Add("${path}: missing reference file")
        }

        if ($status -in $completedStatuses) {
            if ([string]::IsNullOrWhiteSpace([string]$row.active_paths)) {
                $errors.Add("${path}: completed row requires active_paths")
            }
            $coverage = [string]$row.era_coverage
            if ($coverage -notmatch '26x') {
                $errors.Add("${path}: completed row requires VersionCluster coverage for 26x")
            }
            if ([string]::IsNullOrWhiteSpace([string]$row.verification)) {
                $errors.Add("${path}: completed row requires verification evidence")
            }
        }
    }

    return $errors
}

$referenceInventory = @(Get-ReferenceFiles)
foreach ($reference in $referenceInventory) {
    [void]$referencePathSet.Add($reference.reference_path)
}

if ($Mode -eq 'Generate') {
    $activeFiles = & rg --files (Join-Path $projectRoot 'common') (Join-Path $projectRoot 'fabric') (Join-Path $projectRoot 'forge') (Join-Path $projectRoot 'neoforge')
    if ($LASTEXITCODE -ne 0) {
        throw 'Failed to enumerate active project files'
    }
    foreach ($activeFile in $activeFiles) {
        $absoluteActiveFile = [System.IO.Path]::GetFullPath($activeFile)
        $relativeActiveFile = $absoluteActiveFile.Substring($projectRoot.Length).TrimStart('\', '/')
        [void]$activePathSet.Add((Normalize-Path $relativeActiveFile))
    }

    $existingByPath = @{}
    if (Test-Path -LiteralPath $LedgerPath) {
        foreach ($row in @(Import-Csv -LiteralPath $LedgerPath)) {
            $existingByPath[(Normalize-Path ([string]$row.reference_path))] = $row
        }
    }

    $generated = foreach ($reference in $referenceInventory) {
        $path = $reference.reference_path
        if ($existingByPath.ContainsKey($path)) {
            $old = $existingByPath[$path]
            [pscustomobject]@{
                kind = $reference.kind
                reference_path = $path
                status = if ([string]::IsNullOrWhiteSpace([string]$old.status)) { 'pending' } else { $old.status }
                active_paths = [string]$old.active_paths
                era_coverage = [string]$old.era_coverage
                behavior_notes = [string]$old.behavior_notes
                verification = [string]$old.verification
            }
        }
        else {
            [pscustomobject]@{
                kind = $reference.kind
                reference_path = $path
                status = 'pending'
                active_paths = Get-ExactActivePath -Kind $reference.kind -ReferencePath $path
                era_coverage = ''
                behavior_notes = ''
                verification = ''
            }
        }
    }

    $ledgerDirectory = Split-Path -Parent $LedgerPath
    if (-not (Test-Path -LiteralPath $ledgerDirectory)) {
        New-Item -ItemType Directory -Path $ledgerDirectory | Out-Null
    }
    $generated | Export-Csv -LiteralPath $LedgerPath -NoTypeInformation -Encoding utf8
    Write-Output "Generated $($generated.Count) ledger rows at $LedgerPath"
}

$ledger = Read-Ledger
$validationErrors = @(Test-Ledger -Rows $ledger)
if ($validationErrors.Count -gt 0) {
    $validationErrors | ForEach-Object { [Console]::Error.WriteLine($_) }
    exit 1
}

$javaCount = @($ledger | Where-Object kind -eq 'java').Count
$resourceCount = @($ledger | Where-Object kind -eq 'resource').Count
Write-Output "VALID: $($ledger.Count) rows ($javaCount Java, $resourceCount resources)"
