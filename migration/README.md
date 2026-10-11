# Buildscape Migration Ledger

`ledger.csv` is the authoritative file-by-file record for the migration from the
frozen Forge 1.18.2 source in `ref/forge/1.18.2`.

Source updates are tracked by [the sync system](../docs/SYNC.md). Successful
sync branches mark affected review rows `needs_resync`, retain deleted-source
rows for review, and refresh automatic verification. The legacy ledger validator
does not accept that status: use the filtered external ledger view documented
there until the validator gains incomplete-status and tombstone support. Keep
sync review rows and the target's persistent review queue until the port is
accepted; a new source baseline alone does not complete a port.

## Scope

This branch targets Minecraft 26.2 only (Fabric and NeoForge). Minecraft 1.18.2
and 1.21.1 are maintained separately; Forge 26.x and 26.3 are parked. A completed
row therefore records where the behaviour lives in the 26.2 tree (`common`,
`fabric/26.2`, `neoforge/26.2`) and uses `era_coverage` `26x`. The `v118x` and
`v121x` adapter trees are not part of this scope and are not cited as evidence.

Regenerate the ledger with:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/migration-ledger.ps1 -Mode Generate
```

Validate it with:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/migration-ledger.ps1 -Mode Validate
```

Generation preserves existing row decisions and adds newly discovered reference
files as `pending`. Exact same-path files in common are suggested in
`active_paths`, but a suggested path is not evidence of behavioral parity.

## Row lifecycle

1. Change one reference row from `pending` to `in_progress` before comparison.
2. Read the complete reference file/resource and every directly coupled
   Buildscape dependency needed to understand its behavior.
3. Compare all active counterparts. Record key responsibilities and differences
   in `behavior_notes`.
4. Implement shared behavior in common and only native API translation in the
   established `adapter/v118x`, `adapter/v121x`, and `adapter/v26x` trees.
5. Build the 26.2 targets (`gradlew build --offline`).
6. Set the final disposition, list every active destination in `active_paths`
   (each path must exist), set `era_coverage` to `26x`, and record the exact
   successful command/result and commit in `verification`. Java rows cite the
   build and test count (plus the covering test class when there is one);
   resource rows cite `python scripts/resources26x/check.py --mc-jar <26.2 client jar>`.
7. Run ledger validation. A row is not complete if validation fails.

Resource rows map each reference path to its converted path: `loot_tables` to
`loot_table`, `recipes` to `recipe`, `advancements` to `advancement`, `tags/blocks`
(and `items`, `entity_types`, `fluids`) to the singular folder, and `data/forge/tags`
to `data/c/tags`. The empty Forge `global_loot_modifiers.json` was removed on purpose.

## Completion dispositions

- `common`: behavior is fully represented by shared common code.
- `adapter` (written `era_adapter` in `ledger.csv`): shared behavior is in common
  and changed Minecraft APIs are bridged by thin VersionCluster adapters
  (for 26.2, `adapter/v26x`).
- `loader_adapter`: the responsibility is genuinely loader-specific.
- `consolidated`: reference behavior is preserved by other named active files.
- `build_only`: the reference file is verified development/build infrastructure;
  `active_paths` must name the active replacement or explicitly record `n/a`.
- `deferred_vanilla_transition`: Buildscape behavior is fully migrated now, and a
  later transition to an equivalent vanilla feature is documented but not
  implemented during the migration freeze.

`partial`, `registered_only`, assumed equivalence, and undocumented deletion are
not valid completion states.

## VersionCluster compilation mapping (historical)

The multi-era views below describe the earlier three-era plan and still exist in
`common/build.gradle`, but only `v26x` is in scope for this port. The active source
locations remain unchanged. `common/build.gradle` exposes three explicit compilable
views of the existing tree:

- `v118x`: shared sources plus `common/.../adapter/v118x`, Java 17, and
  `ref/artifacts/minecraft-1.18.2.jar`;
- `v121x`: shared sources plus `common/.../adapter/v121x`, Java 21, and
  `ref/artifacts/minecraft-1.21.1.jar`;
- `v26x`: shared sources plus `common/.../adapter/v26x`, Java 25, and
  `ref/artifacts/minecraft-26.2.jar`.

Every loader target names one of these configurations explicitly. The mapping is
checked by `scripts/test-era-wiring.ps1`. A combined representative invocation of
Forge 1.18.2, NeoForge 1.21.1, and Fabric 26.3 compiled successfully on October 7,
2026, proving that distinct VersionClusters can now be requested in one Gradle build.

## Coupled files

Files that cannot compile independently may be migrated in one batch, but every
reference path keeps its own ledger row. Each row must name its own behavior and
active destinations; package-level notes never replace file-level evidence.

## Resource ownership

Loader metadata, access transformers, mixin configurations, and pack metadata
must receive deliberate `loader_adapter`, `consolidated`, or `build_only`
dispositions. Do not copy Forge metadata into common or other loaders blindly.

