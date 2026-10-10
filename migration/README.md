# Buildscape Migration Ledger

`ledger.csv` is the authoritative file-by-file record for the migration from
`ref/forge/1.18.2`. Regenerate it with:

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
5. Compile all supported eras and affected loader targets.
6. Set the final disposition, list every active destination in `active_paths`,
   set `era_coverage` to include `118x;121x;26x`, and record the exact successful
   command/result in `verification`.
7. Run ledger validation. A row is not complete if validation fails.

## Completion dispositions

- `common`: behavior is fully represented by shared common code.
- `adapter`: shared behavior is in common and changed Minecraft APIs are
  bridged by thin VersionCluster adapters.
- `loader_adapter`: the responsibility is genuinely loader-specific.
- `consolidated`: reference behavior is preserved by other named active files.
- `build_only`: the reference file is verified development/build infrastructure;
  `active_paths` must name the active replacement or explicitly record `n/a`.
- `deferred_vanilla_transition`: Buildscape behavior is fully migrated now, and a
  later transition to an equivalent vanilla feature is documented but not
  implemented during the migration freeze.

`partial`, `registered_only`, assumed equivalence, and undocumented deletion are
not valid completion states.

## VersionCluster compilation mapping

The active source locations remain unchanged. `common/build.gradle` exposes three
explicit compilable views of the existing tree:

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

