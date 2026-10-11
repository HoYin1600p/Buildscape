# Buildscape block-state showcase (Minecraft 26.2)

Use Java 25 and Python 3 (standard library only). This tooling targets the `v26x`
VersionCluster with official Mojang names. It never launches a client. The dump is
an explicit task and is not included in normal builds or tests.

## Dump and generate

From the repository root, with dependencies already cached:

```powershell
./gradlew.bat :common:dumpBlockStates --parallel --max-workers=2 --offline --console=plain
python -B scripts/showcase/build_showcase.py common/build/showcase/blockstates.json build/showcase-out
```

The dump defaults to `common/build/showcase/blockstates.json`. Override it with
`-PshowcaseOutput=<path>`; relative paths resolve from the `common` project.
It records each namespaced ID, Java class simple name (empty for anonymous
classes), definition block type, property values, and possible states in vanilla
`StateDefinition.getPossibleStates()` order. The test-only recording service and
`TestBootstrap` provide the same headless registry window as
`BlockstatePropertiesTest`. Previous registry holder maps and freeze state are
restored on exit. Headless failures appear under `unavailable`, with their reasons.
`experience_liquid` is included manually with all sixteen `level=0..15` states;
its actual fluid factory requires loader registration.

Options:

```powershell
python -B scripts/showcase/build_showcase.py common/build/showcase/blockstates.json build/showcase-custom `
  --origin-x 100 --origin-z -200 --ground-top-y -61 --layer-size 200 `
  --include-waterlogged --include-lava-logged
```

Add `--no-floors` to build the upper layers without the grass floor beneath them.
Layer positions are unchanged (only the floor blocks disappear), each fluid cell above
layer 1 gets a glass block under its fluid so it stays sealed, and layer 1 still uses
the real ground. The layout records `"floors": false` and `apply_to_world.py` honours
it; layouts without the key are treated as having floors.

Both logged-state flags default off. `--layer-size` must be 4..200. The default
ground top is y=-61, so the first layer's blocks sit at y=-60. Use a fresh output
directory for each generation: existing command chunks are rejected to prevent
accidentally executing stale trailing files. The generator rejects layouts above
the Overworld height limit instead of writing unusable commands.

Output contains numbered `commands-0001.mcfunction`, etc., with no leading
slashes and at most 400 commands per file; `layout.json`, including every cell,
pair, coordinate, rectangle, layer and command filename; and `summary.txt`.
The printed summary lists material group sizes, excluded logged states and any
headless failures. These generated build outputs are not source deliverables.

## Layout and stability

Material tokens are selected from the IDs present in the dump. Longest matching
tokens distinguish dark/pale oak, red sandstone, flaming steel and ashpen colour
variants; all copper oxidation and wax variants share copper. Leaves and hedges
form a nature group, other plants form another, and dyed wool, concrete,
terracotta, wallpaper and lamps group by dye colour. Groups run in category order:
woods, stones, metals, colours, nature, misc. Within each group, IDs and full
property maps sort deterministically.

Each group occupies its own rectangle on a shelf. Display footprints have one air
coordinate between adjacent cells/rows; rectangles have two coordinates between
them. A vanilla oak sign occupies the gap in front of each group's first row.
Large groups continue on subsequent layers without interleaving another group.
Rectangles and signs fit inside the configured maximum layer dimensions.

Lower/upper blocks use one two-block-high pair for each lower-half state. Stair
top/bottom halves are ordinary independent displays. Head/foot blocks use one pair
per foot state, extending toward its facing, with a two-block footprint. Fluid
cells reserve 3x3 footprints: four glass sides and a glass lid enclose a fluid at
the layer's base y. Existing ground or the upper-layer grass floor seals the
bottom. The next floor sits nine coordinates above the previous layer's tallest
placement, leaving exactly eight intervening air blocks; its displays sit one
coordinate above that floor. Upper floors cover their full used bounding area,
including signs and gaps, with grass blocks. Layer 1 keeps the existing ground.

**Run only in a dedicated showcase world with commands enabled.** Setup force-loads
the used areas and clears the full build volume above the chosen ground. Clears
and floor fills are tiled to stay within vanilla's default 32,768-block fill limit.
Setup uses these verified 26.2 gamerules:

| Older rule / purpose | 26.2 command |
| --- | --- |
| randomTickSpeed | `gamerule minecraft:random_tick_speed 0` |
| doFireTick | `gamerule minecraft:fire_spread_radius_around_player 0` |
| doDaylightCycle | `gamerule minecraft:advance_time false` |
| doWeatherCycle | `gamerule minecraft:advance_weather false` |
| doMobSpawning | `gamerule minecraft:spawn_mobs false` |
| Fluid source conversion | `water_source_conversion false`, `lava_source_conversion false` |
| Vine growth | `spread_vines false` |

Time is set to day. Ticks freeze before clearing/placement and the last file ends
with `tick freeze`. **Tick freeze does not persist across restarts.** Freeze again
immediately on reopening, and keep ticks frozen while viewing fluid levels,
falling blocks, and unsupported wall/hanging states. Grass supports floor plants;
it cannot satisfy every possible wall or ceiling attachment. The first placement
per cell uses `replace`; subsequent parts and a final restoration pass use 26.2's
`strict` mode to retain exact state strings without neighbour shape updates.
Some commands intentionally report no change (already frozen/forced, clearing air,
or restoring an unchanged state). Inspect failures before declaring completion.
Force-loaded chunks and changed gamerules remain until explicitly restored.

The names, sign `front_text.messages` Component NBT (native strings, not embedded
JSON), and state APIs were inspected in the cached official 26.2 client jar.

## Write the layout directly into a world

Create a dedicated Minecraft **26.2** save with Buildscape installed, explore the
entire planned showcase footprint so its chunks exist, then close that save and
the game. Keep the original pristine save separately. The offline editor requires
Python 3 only; it uses the origin, ground height, dimensions and exact cell/part
coordinates recorded in `layout.json`, including clearing the build volume,
upper floors, glass fluid cells and material signs.

```powershell
python -B scripts/showcase/apply_to_world.py "path/to/Buildscape Showcase" build/showcase-out/layout.json --dry-run
python -B scripts/showcase/apply_to_world.py "path/to/Buildscape Showcase" build/showcase-out/layout.json
# Optional new backup location, or explicitly reuse a previous region backup:
python -B scripts/showcase/apply_to_world.py "path/to/Buildscape Showcase" build/showcase-out/layout.json --backup-dir "path/to/region-backup"
```

The tool holds the game's `session.lock` throughout preparation and writing and
refuses a save opened by a client/server. A dry run validates and encodes all
affected chunks without writing files or making a backup. Its JSON report gives
DataVersions, edited chunk/region counts, placement counts (including clears and
floor fills), block entities added and missing chunks. **Require zero skipped
placements** for a complete showcase; reopen the game and generate any reported
missing chunks before retrying. The tool never creates missing chunks.

Before a write, the editor copies the complete Overworld region folder to
`<world>/showcase-region-backup` by default. `--backup-dir` instead names the
directory that receives the region folder's contents; an existing explicit
backup must already contain its region files and is left intact. An existing
default backup requires an explicit `--backup-dir` on subsequent runs. Backups
must be separate from the source region folder. Restore by closing the game and
copying these saved region files back to the original Overworld region folder.
This is a terrain backup; keep a separate full save backup as well.

The editor accepts `dimensions/minecraft/overworld/region` (26.2) and the legacy
`region` location, checks both `level.dat` and each chunk for DataVersion 4903,
and refuses unfinished chunks or incompatible versions. Chunk records use zlib
on writing; gzip and uncompressed records are readable. External oversized `.mcc`
chunks and unsupported compression are explicitly refused before any writes.
Unedited compressed chunks and their timestamps are preserved. Edited sections
have minimal block palettes, neighbour-derived biomes for new sections, minimal
block entities and native 26.2 sign text. Lighting and heightmaps are invalidated
for recomputation, and scheduled block/fluid ticks at replaced positions are
removed. Entity files, POI and other dimensions are left intact.

Open the save with Buildscape installed after writing. The same 26.2 save can be
copied between Fabric and NeoForge instances with matching Buildscape versions:
close both clients first, copy the **whole save folder** into the destination
instance's `saves` directory, then select it there. Keep the original copy until
the destination loads successfully. This editor does not change gamerules or
persist a tick freeze; freeze ticks immediately when viewing unsupported states
and fluid displays, as described above.

The sign fields were verified with `javap` on the official 26.2
`SignBlockEntity.saveAdditional`, `SignText.DIRECT_CODEC` and
`ComponentSerialization` code. `BlockEntity.saveMetadata` supplies `id/x/y/z`,
and `SerializableChunkData` reads `keepPacked`. The real pristine 26.2 save
confirmed DataVersion 4903, section `block_states`/`biomes`, `isLightOn`,
`Heightmaps` and the tick list names. New generated layouts retain each cell's
dump class/type metadata to identify custom block entities; existing layouts
remain supported by known block ID families.

## Run through CMA native plans

Use an existing CMA client connected to this 26.2 showcase world, with operator
permissions and its bridge armed (`/cma bridge arm`). Set `CMA_BASE_URL` and
`CMA_TOKEN_FILE` for that running client using its normal setup. The command files
are plain Minecraft functions; CMA native plans instead require JSON steps with
`action: "command"` and `arguments.command`. Convert each numbered file to its
own bounded plan, outside the source tree, then run them serially:

```powershell
$showcaseDir = (Resolve-Path build/showcase-out).Path
$nativeRunner = "$env:REPOS/CodexMinecraftAugment/scripts/cma-native-plan.ps1"
foreach ($chunk in Get-ChildItem -LiteralPath $showcaseDir -Filter 'commands-*.mcfunction' | Sort-Object Name) {
    $stepNumber = 0
    $steps = @(foreach ($command in Get-Content -LiteralPath $chunk.FullName) {
        $stepNumber++
        # Only these operations can intentionally report "no change".
        $mayBeUnchanged = $command -match '^(forceload |tick freeze$|fill .* minecraft:air replace$)' `
            -or $command.EndsWith(' strict')
        [ordered]@{
            id = "command-$stepNumber"
            action = 'command'
            arguments = @{ command = $command; feedbackTicks = 20 }
            onFailure = $(if ($mayBeUnchanged) { 'continue' } else { 'attention' })
        }
    })
    $plan = [ordered]@{
        schemaVersion = 1
        name = $chunk.BaseName
        defaults = @{ timeoutTicks = 200; maxAttempts = 1; noProgressTicks = 0 }
        steps = $steps
    }
    $planPath = Join-Path $showcaseDir ($chunk.BaseName + '.plan.json')
    $plan | ConvertTo-Json -Depth 20 | Set-Content -LiteralPath $planPath -Encoding utf8
}

# Execute one chunk, inspect its result/events, then choose the next numbered plan.
$submitted = (& $nativeRunner submit `
  -PlanPath (Join-Path $showcaseDir 'commands-0001.plan.json') | ConvertFrom-Json)
$planId = $submitted.plan.planId
& $nativeRunner status -PlanId $planId
$result = (& $nativeRunner wait -PlanId $planId | ConvertFrom-Json)
& $nativeRunner events -PlanId $planId -AfterSequence 0 -Limit 256
if ($result.plan.status -ne 'SUCCEEDED') { throw "Inspect failed plan $planId before continuing" }
# Review all events (page using the returned sequence cursor), including
# continued failures, before proceeding. Only no-change feedback is expected.
```

Do not treat `SUCCEEDED` alone as proof of placement: steps allowed to continue can
still report failures. Review the retained plan events, paginate `events` with
`-AfterSequence`, and inspect the world against `layout.json`. Stop on unknown
block/state, permission, unloaded-chunk or height errors. Do not replay uncertain
mutations blindly. Use `status -PlanId <uuid>` for a snapshot and
`cancel -PlanId <uuid>` to interrupt a pending plan. Freezing server simulation
does not stop CMA's client-side plan processing.

## Local regression tests

```powershell
python -B -m unittest discover -s scripts/showcase -p 'test_*.py' -v
```

The fixtures cover spacing, layer bounds and stacking, pairs, fluid glass cells,
material groups, logged-state flags, chunking, setup and fill limits without
requiring a client or loading Minecraft classes. Anvil tests cover every NBT tag
type, modified UTF-8, region sectors/compression/timestamps, non-spanning palette
packing, section boundaries, block entities, ticks, locking and backups. When the
retained pristine 26.2 world exists under `WORKSPACES`, an integration test copies
it to a temporary directory and runs the CLI dry run with pairs, fluid glass,
signs and custom containers, checking zero skipped placements and unchanged
region fingerprints. The pristine backup is never modified.
