# Forge 1.18.2 parity harness

This is a **test-only** helper for Forge **40.3.12** and Java 17. Never upload
its jar, include it in a release archive, or ship it to players. It registers
no content and has no Buildscape compile dependency. Its independent settings
are not included in the repository's main settings or jar collection tasks.

## Build

Use the repository wrapper from the repository root, with JDK 25 for Gradle
and a Java 17 toolchain available. The gate is required; without it the standalone
build only has the base plugin and does not resolve Minecraft or Unimined.

```powershell
.\gradlew.bat -p tools/parity-dumper build '-Pbuildscape.parityDumper=true' --offline --no-daemon --console=plain --parallel --max-workers=4
```

The build produces two jars in `tools/parity-dumper/mod/build/libs/`:

- `buildscape-parity-dumper-TEST-ONLY-1.0.0-dev.jar` uses Mojang names. Use it
  in the repository's Unimined dev runs, in `forge/1.18.2/run/server/mods`
  and `forge/1.18.2/run/client/mods`. Buildscape is supplied by the dev run's
  classpath; do not add a second Buildscape jar to those folders.
- `buildscape-parity-dumper-TEST-ONLY-1.0.0.jar` is remapped to SRG. Use it in
  a production Forge 40.3.12 Prism instance's Minecraft `mods` folder beside
  exactly one production Buildscape jar.

Install only the appropriate variant in each run; both have the same mod ID.
Dependencies and Unimined's Forge 40.3.12 caches must already be populated
by the working `forge/1.18.2` build. The standalone build uses the same
Unimined version, mappings, Java target and pinned jopt-simple dependency.
The default Buildscape build does not configure this project, even when the
gate is set: `-p tools/parity-dumper` selects the separate build.

At Milestone 1, the repository's default `build` includes shared-code Forge
1.18.2 alongside the 26.2 loaders, runs the common v118x tests, and produces
`forge/1.18.2/build/libs/buildscape-4.0.3.jar`. Gradle 9.6 runs on JDK 25;
Forge compilation and v118x test execution use the locally available Java 17
toolchain. All dependencies must already be cached for offline use.

```powershell
.\gradlew.bat build --offline --console=plain --parallel --max-workers=8
.\gradlew.bat :forge:1.18.2:build '-Pbuildscape.v118xSource=ref' --offline --console=plain --parallel --max-workers=8
.\gradlew.bat :forge:1.18.2:build '-Pbuildscape.v118x=true' '-Pbuildscape.v118xSource=common' --offline --console=plain --parallel --max-workers=8
```

Use `ref` for the frozen parity baseline and `common` (the default) for the
shared-code candidate. The explicit `buildscape.v118x=true` gate remains
compatible with existing parity tooling. For fast 26.2-only builds, pass
`-Pbuildscape.v118x=false`; this omits the Forge project and skips v118x
verification in `common:check`. It does not control this standalone helper's
separate `buildscape.parityDumper` gate.

## Collect paired dumps

1. Pin the baseline main commit and candidate jar. Prepare two equivalent
   Forge 40.3.12 runs, with identical other mods, configuration, datapacks,
   resource packs, graphics options and fresh equivalent worlds.
2. Put the appropriate helper variant from the build instructions in each
   run's `mods` folder. Use the `-dev` jar for Unimined runs and the SRG jar
   for Prism production runs. Check the actual run's working directory when
   using another launcher. The helper is not added automatically to any run
   classpath.
3. Start each dedicated server normally. After `ServerStartedEvent` it writes
   `<gameDir>/parity-dump/server/`. No commands, players or display are needed.
4. Start each client. After resource loading has completed and the reload
   overlay has closed, it writes `<gameDir>/parity-dump/client/`. Joining a
   world refreshes the client snapshot after tags arrive. F3+T refreshes it
   after resource reload. An integrated server also writes `server/` when
   a world starts. Compare the clients at the same lifecycle point.
5. Check `manifest.json`: `complete` must be `true`. Failures are logged and
   leave an incomplete manifest; they cannot count as a parity pass. Save
   paired snapshots in the task's external workspace before another run
   overwrites them. Remove the helper from the run when finished.

All `buildscape:` and `minecraft:` entries are included, so vanilla mutations
are covered without a fragile manual allowlist. Server snapshots contain
blocks (all states, properties, default, destroy time, resistance, sound and
light), tags, items (stack, rarity and food), creative-tab contents in their
actual order, block entities and valid blocks, entities, menus, particles,
sounds, all static registry entry identities, codec-serialized configured and
placed features, loaded recipes with serialized result stacks and ingredients,
and loaded loot table IDs. Creative-tab records retain absolute indices and
stack NBT, including variants. NBT records retain tag types and sort compound
keys. Recipe result serialization uses `ItemStack.save`, not a display string.

Client snapshots contain the static registry data plus per-state render layers,
block/item color handlers and context-free tint samples 0–7, block entity and
entity renderers, screen factories and particle providers. Recipes, loot tables
and datapack feature registries require a server/world and are covered by the
separate `server/` snapshot; a title-screen client alone cannot prove those.
Both sides record loaded Buildscape mixin configs, configured targets and
**actually applied** mixins from Mixin's ClassInfo cache. The dumper does not
force target classes to load; unvisited targets remain configured-only.

Maps and sets are sorted, JSON keys are recursively sorted, and snapshots have
no timestamps or absolute paths. Lists that encode behavior (tab order,
ingredients, placements, NBT lists) retain their order. Hidden lambda JVM
numbers/addresses are removed from registration identities. Consequently a
screen factory is identified by its registering class, not the screen it
would construct; two different lambdas in the same class can share that
identity. Context-dependent color callbacks report `requiresContext` rather
than inventing a color. These snapshots compare registrations and sampled
properties; in-game behavior and save compatibility still need the later
integration stages. Private client maps try literal 1.18.2 Mojang and SRG field
names, including Forge's SRG-named delegate-keyed color maps on dev clients.
If names differ, an unambiguous generic map type identifies the table; color
delegates are resolved to registry IDs. A client table failure does not stop
the remaining tables: the manifest records it under `errors`, keyed by the
table's filename, and keeps `complete` false. Mixin inspection is pinned to
its 0.8.5 implementation. Inspection failures invalidate the dump.

## Compare

From the repository root (PowerShell paths below are examples):

```powershell
python -B scripts/parity118/jardiff.py baseline.jar candidate.jar --json jar-comparison.json
python -B scripts/parity118/dumpdiff.py baseline/parity-dump/server candidate/parity-dump/server --json server-comparison.json
python -B scripts/parity118/dumpdiff.py baseline/parity-dump/client candidate/parity-dump/client --json client-comparison.json
python -B -m unittest discover -s scripts/parity118 -p 'test_*.py'
```

Store comparison reports outside the repository in its external workspace.
`dumpdiff` also accepts entire `parity-dump/` folders if both sides collected
both snapshots. JSON object order is ignored; array order and value types
are significant. Differences include JSON pointers down to individual values.
Missing files, malformed JSON and incomplete manifests are errors.

`jardiff` compares entry names, class-name sets and non-class content. Class
byte differences are informational because toolchains/remapping may differ.
For JSON, CFG, TOML, MCMETA, TXT and LANG resources, only CRLF versus LF and
trailing newlines are normalized; whitespace/formatting elsewhere still matters.
The manifest ignores timestamp attributes (including folded continuations)
but checks other metadata and named sections. Refmaps compare their `mappings`
objects, ignoring namespace-specific `data`. Other binary entries are exact.
Duplicate ZIP entries and invalid refmap/manifest formats are input errors.
Both scripts use only the Python standard library. Exit status: **0** parity,
**1** differences, **2** invalid input or incomplete dumps.

## Validation

The gated `build` and `check` tasks run `:dumper:selfCheck`, a Java 17 main
using the dump writer without bootstrapping Minecraft or opening a display.
It checks byte equality across object insertion orders, recursive key sorting,
array order, nulls, UTF-8/LF output, non-mutating canonicalization and sorted
snapshot/manifest filenames. It needs no additional test framework dependency;
its output files stay under `mod/build/self-check/`.

Before accepting parity evidence, run the unittest command above, the
standalone gated build, and dedicated-server/client smoke runs against both
pinned jars. Verify Java API compilation, Unimined remapping, production SRG
field lookup, Mixin cache inspection, resource-load timing and paired dump
equality. A Python test pass alone does not verify the helper mod.
