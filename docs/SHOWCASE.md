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
requiring a client or loading Minecraft classes.
