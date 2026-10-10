# Synchronizing the source line and ports

`sync/targets.json` registers the source line and every port. Today the source is
Forge 1.18.2 on `main` in `https://github.com/HoYin1600p/Buildscape.git`;
`mc26.2` combines Fabric and NeoForge on `port/26.2`, with common's `v26x`
VersionCluster. Other targets are scaffolded, and 26.3 is paused by owner decision.
Scaffolded targets do not need existing branches and are skipped by `status`.

The Python tool needs Python 3.9+ and Git, with no third-party Python packages.
Run from a port checkout containing the registry. Git commands use argument
lists, so paths containing spaces and apostrophes work. Local sources are read
only. The tool never pushes, merges into a target branch, or changes the calling
checkout. Applying creates a separate retained worktree and local branch under
`sync/<target>/<short-source-sha>`.

## Running it

Set an external output directory in PowerShell:

```powershell
$env:BUILDSCAPE_SYNC_OUTPUT = Join-Path $env:WORKSPACES 'Buildscape-26/sync-reports'
$env:BUILDSCAPE_SYNC_MC_JAR = 'D:/Minecraft jars/minecraft-26.2.jar'
python -B scripts/sync/sync.py status --fetch
python -B scripts/sync/sync.py plan --target mc26.2
python -B scripts/sync/sync.py briefs --target mc26.2 --output-dir (Join-Path $env:BUILDSCAPE_SYNC_OUTPUT 'briefs')
python -B scripts/sync/sync.py apply --target mc26.2
```

`status --fetch` explicitly fetches the configured branch into
`refs/sync/source/<source-branch>` without changing a local source branch.
Without `--fetch`, the local source branch is preferred; the fetched ref is a
fallback when that branch is absent. To use the fetched tip when a stale local
`main` also exists, continue using `--fetch` for the desired command.
All commands accept `--json`, `--target`, and `--output-dir`. `status` prints
source commits and classifies every changed file; with an output directory it
also saves the reports. Reports contain the complete source diff and mappings.

For an entirely offline run against the owner's local source checkout:

```powershell
$sourceRepo = Join-Path $env:REPOS 'BuildScape'
python -B scripts/sync/sync.py --source-repo $sourceRepo status
python -B scripts/sync/sync.py --source-repo $sourceRepo plan --target mc26.2
```

Global arguments (`--repo`, `--registry`, `--source-repo`) precede the subcommand.
`--source-repo` must contain the configured source branch and base commit;
it cannot be combined with `--fetch`. No remote is required for these runs.

## What is automatic

| Source change | Action |
| --- | --- |
| Resources, excluding loader/build metadata | Convert complete old/new source resource trees in temporary directories, compare outputs, apply only changed converted outputs, then validate |
| Java with one counterpart byte-identical to the old source | Apply the source change with `git merge-file` using the old source as the three-way base |
| Other mapped Java | Port candidate, with all ledger destinations and source diff |
| Unmapped Java | New-feature port candidate requiring a scoped destination contract |
| Build files, loader metadata, mixin configs, access transformers | Review |
| Deleted source files | Review; keep target code/resources and mark ledger rows `needs_resync` |
| Files retired by `sync/removals26x.json` for `mc26.2` | Report as `retired`; never copy them into the target |

The 26.2 removal policy records retired ids and exact `excluded_target_paths`. Shared models
and textures remain available. Apply filters both source resources and converted
outputs against this policy, so later source updates cannot restore retired assets.
Retired changes do not enter the pending review queue.

The ledger's semicolon-separated `active_paths` is authoritative. A same-relative
Java file under the declared `java_root` is the fallback. Multiple destinations
require review even when one is identical, since a coupled adapter can need work.
Renames preserve both source paths and diffs. An identical Java counterpart can
be renamed automatically only when its destination follows the same-relative
mapping and the destination does not already exist. Other Java renames require
porting. Resource renames appear as converted output additions and removals.
Automatic resources stop on conflicting port-specific edits; they do not silently
overwrite them. Unchanged converted outputs preserve hand-maintained target data.

Apply always runs the target's declared validation, including offline Gradle
builds for the active loaders. Commands and output are written as they run to an
external log. A failed conversion, conflict, or validation leaves the worktree
and failed report for inspection; it does not advance the base or commit.
A successful run refreshes the declared frozen snapshot scope, advances
`base_commit` on the new local branch, refreshes automatic ledger verification,
and marks other affected ledger rows `needs_resync`. Snapshot metadata is kept
as reference material; it is never copied into target build files.

The initial snapshot corresponds to `c7d7112cbb351e3f50f9c147dde92ee628553bef`
with **four differing files**, as recorded in the registry. Review these historical
differences before the first real apply: snapshot scope is replaced with exact
new-source bytes after validation. Conversions use the source base commit, not
assumed byte equality with the historical snapshot.

The baseline is an ingestion cursor, not a claim that pending Java is ported.
Unresolved source diffs and review items persist in the declared
`sync/state/<target>.json` queue, are shown in later reports, and remain available
to `briefs` after advancement. Briefs group candidates sharing target files so
delegated work does not conflict. Unmapped features get review briefs; the lead
must approve precise destinations before implementation. Review the queue and
ledger after porting, and remove only resolved queue entries.

An apply branch is review material. The lead reviews automatic changes and
outstanding ports and accepts it into the target branch separately. Repeated
apply of the same source head reuses the completed local branch. A failed branch
blocks retries until reviewed. If a newer source head arrives before the previous
branch is accepted, both branches start from the target baseline; they are
alternatives, not independent patches to merge together. Accept one before the
next sync cycle. Builds use two workers; run one full build at a time.

## Ledger compatibility

The existing `scripts/migration-ledger.ps1` does not accept `needs_resync` and
expects every ledger path to exist in the snapshot. It is deliberately unchanged.
Do not run its Generate mode over sync review rows: renamed/deleted tombstones
must remain available until reviewed. To validate the existing completed rows,
write an external view excluding the sync review rows (including deletion
tombstones), then use the validator's existing `-LedgerPath` parameter:

```powershell
$view = Join-Path $env:BUILDSCAPE_SYNC_OUTPUT 'ledger-validation.csv'
Import-Csv migration/ledger.csv |
    Where-Object { $_.status -ne 'needs_resync' } |
    Export-Csv -LiteralPath $view -NoTypeInformation -Encoding utf8
powershell -NoProfile -File scripts/migration-ledger.ps1 -Mode Validate -LedgerPath $view
```

This validates completed rows, not pending port completeness. Future validator
support should add `needs_resync` as an incomplete status and explicitly allow
review tombstones for source deletions/renames. Automatic resource verification
can name the target validation command/result, but new unmapped resource rows
remain `pending` until the lead records their exact converted destinations.

## Activating another target or source line

Activate one target at a time. Prepare its branch, adapter/loader modules,
reviewed reference snapshot, ledger mapping, converter, and validator first.
Then declare `reference_snapshot`, `mapping_file`, `java_root`, `resource_root`,
`state_file`, converter command and validation commands, and set `base_commit`
to the exact reviewed source snapshot commit. Set `status` to `active` on both
the invoking registry and that target's branch registry. Commands are argument
arrays; placeholders are `{python}`, `{repo}` (the apply worktree), `{resources}`,
`{mc_jar}`, and `{gradle}`. The jar can be provided by `--mc-jar`, the environment,
or the registry. Relative jar paths resolve against the invoking checkout so
ignored local jars need not be present in a new worktree. Conversion rules live in
the target converter; the sync tool also enforces the 26.2 retirement policy.

Change `source.branch` to designate a different main source line. Also change
its loader, Minecraft version, subtree and metadata scope when appropriate.
The new source must contain each active target's base in its ancestry; otherwise
the tool stops for a reviewed rebaseline. Do not replace a base merely to silence
that check. If the source layout moves, supply corresponding snapshots and
converters before activation.

## Scheduling later

`scripts/sync/scheduled-sync.ps1` is a Task Scheduler entry point; it does not
register a task. It fetches, saves status reports, then applies active targets
with new commits to local review branches. It never pushes. Configure
`-OutputDirectory` or `BUILDSCAPE_SYNC_OUTPUT`; the default is the git-ignored
`build/sync-reports` folder of the repository. Pass an absolute local Minecraft
jar path. Overlapping game/heavy-build processes defer
apply. Configure Task Scheduler to disallow overlapping instances, run under
the normal user with Git/Python/tool caches available, and retain reports.
Review and accept each branch before the next cycle. No schedule is installed by
this task.

## Offline tool tests

```powershell
python -B -m unittest discover -s scripts/sync -p 'test_*.py' -v
```

Tests use temporary local repositories, a fake converter and validator, and
require neither network access nor a Minecraft jar.
