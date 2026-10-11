# Building and testing Buildscape 26.2

The default build contains `common`, `fabric:26.2`, and `neoforge:26.2`.
Use JDK 25 and the Gradle 9.6.0 wrapper. Both loaders use Unimined
1.4.41-kappa to prepare Minecraft, loader dependencies, libraries, natives,
and real loader development runs. Fabric uses loader 0.19.5 and Fabric API
0.149.0+26.2; NeoForge uses 26.2.0.88.

## Build

Once the dependencies are cached:

```powershell
.\gradlew.bat build --offline --console=plain --stacktrace --parallel --max-workers=2
.\gradlew.bat :common:compileV26xJava --offline --console=plain
```

On Unix, use `./gradlew` with the same task names and options. On a clean
clone, dependencies must first be populated by running the build without
`--offline` in a network-enabled environment. Subsequent offline builds also
need the Unimined Minecraft/loader caches, not only the Maven artifacts.

Common remains a plain `java-library`. Its `v26x` compile jar is the official,
unobfuscated Mojang client jar downloaded from an immutable public URL and
verified against Mojang's published SHA-1. The build can reuse Unimined's
verified official client cache. No files under `ref/artifacts` are needed for
26.2. The default build also compiles `v118x` and checks its recipe tooling;
`v121x` remains defined for a separately maintained target.

Each loader jar includes the compiled common `v26x` classes, common resources,
its own metadata, service bindings, and the unified `buildscape.mixins.json`.
The jars are collected in root `build/libs` and also remain in each loader's
`build/libs` directory. Minecraft 26.x uses official names, so these jars do
not need remapping.

## Shared 1.18.2 recipe checks

`common:check` includes fifteen standalone JavaExec runners, the seven-test
`v118xTest` JUnit regression suite, `v118xVerifyBundledRecipeCache`, and
`v118xCheckRecipePackFormat`. The last two use the frozen pack under
`ref/forge/1.18.2/src/main/resources/data/buildscape/recipes_pack`.
Verification compares the entire generated cache with the bundled bytes;
format checking rejects duplicate JSON keys and noncanonical formatting.

The `v118xTest` source set compiles Java 17 bytecode against the shared
v118x output and the final `compileV118xJava` classpath, including Unimined's
mapped Minecraft jar. Runners use JDK 17 and Minecraft 1.18.2's resolved
library versions. The existing 26.2 `common:test` task is unchanged.

```powershell
.\gradlew.bat :common:check --offline --no-daemon --console=plain --max-workers=8
.\gradlew.bat :common:v118xVerifyBundledRecipeCache :common:v118xCheckRecipePackFormat --offline --no-daemon --console=plain --max-workers=8
.\gradlew.bat :common:v118xSyncBundledRecipeCache --offline --no-daemon --console=plain --max-workers=8
```

Explicit synchronization writes `common/build/generated/v118xRecipeCache/recipes.bscb`.
It does not modify the frozen pack and is not required by `check`.

| Reference test | Shared task |
| --- | --- |
| PipeSpillTest | v118xRunPipeSpillTests |
| PipeOutletWaterTest | v118xRunPipeOutletTests |
| RecipeManagerInjectorTest | v118xRunRecipeManagerInjectorTests |
| BinaryRecipeCacheTest | v118xRunBinaryRecipeCacheTests |
| TreeChopTraversalTest | v118xRunTreeChopTraversalTests |
| BeaconBeamScanStateTest | v118xRunBeaconBeamScanTests |
| GoldenDandelionGrowthTest | v118xRunGoldenDandelionGrowthTests |
| MangroveLeafSupportTest | v118xRunMangroveLeafSupportTests |
| EyeblossomTransitionTest | v118xRunEyeblossomTransitionTests |
| CopperRodLogicTest | v118xRunCopperRodLogicTests |
| HazeBushResourcesTest | v118xRunHazeBushResourcesTests |
| ParticleColorSlotsTest | v118xRunParticleColorSlotsTests |
| StairModelTest | v118xRunStairModelTests |
| PatternBoundsTest | v118xRunPatternBoundsTests |
| RecipePackFormatterTest | v118xRunRecipePackFormatterTests |
| RecipeCacheRegressionTest | v118xTest |

No reference test is excluded. Copper rod checks use the relocated v118x
oxidation helpers after vanilla bootstrap. The regression suite uses JUnit 5
and validates shared recipe records with known items, replacing Forge event
bootstrap and pre-resolved native caches. It retains the reference checks for
hash normalization, duplicate keys and IDs, formatting, tampering, family
defaults, invalid records and unbound tags. Resource checks read the frozen
1.18.2 resources first; runners write temporary data under ordinary build output.

The shared generator uses main's schema, cache and generator versions for its
hash, including category boundaries and normalized line endings. Runtime cache
hashing delegates to that generator. The binary layout remains version 5.
Reference-pack family expansion and the `forge:` tag alias are selected for
1.18.2; the existing 26.2 family defaults and `c:` alias remain in use for 26.2.

## Development clients

```powershell
.\gradlew.bat :fabric:26.2:runClient --offline --console=plain
.\gradlew.bat :neoforge:26.2:runClient --offline --console=plain
```

These are Unimined loader runs, including Fabric API on Fabric. Run one
client at a time, asynchronously when diagnosing startup. Inspect the run's
`logs/latest.log` and `crash-reports` under the loader's run directory. A
running window may display a mod loading failure; confirm the title screen
or the actual startup error and then close the development client.

## Forge 1.18.2 toolchain spike

Forge 1.18.2 is configured only with `-Pbuildscape.v118x=true`. Without that
property, the default 26.2 build does not configure or resolve this project.
It uses the same Unimined plugin, Forge 40.3.12, Mojang development mappings
and SRG production mappings, and a Java 17 toolchain for compilation and runs.
Keep JDK 25 available for Gradle and the 26.2 projects, and JDK 17 for this target.

Once Forge 40.3.12 and the other dependencies have been cached:

```powershell
.\gradlew.bat :forge:1.18.2:build '-Pbuildscape.v118x=true' '-Pbuildscape.v118xSource=ref' --offline --no-daemon --console=plain --parallel --max-workers=8
.\gradlew.bat :forge:1.18.2:build '-Pbuildscape.v118x=true' '-Pbuildscape.v118xSource=common' --offline --no-daemon --console=plain --parallel --max-workers=8
```

Quote the dotted `-P` arguments in PowerShell so they reach Gradle intact.

`buildscape.v118xSource` defaults to `ref`, compiling the frozen Java sources
and packaging resources from `ref/forge/1.18.2/src/main`. The opt-in `common`
mode compiles the Forge loader, shared sources and the v118x source root (including
the v118x adapter). It packages the v118x mixin configuration and Forge service
bindings ahead of the reference resources, preserving the reference recipe pack,
cache, metadata expansion and access transformer. Client registrations are
provided by physical-client-only Forge subscribers and the v118x client
factories. Use `--no-daemon` for every Forge invocation.
`mod_version` (4.0.3) in gradle.properties sets this jar and every other loader jar.
The production jar is `forge/1.18.2/build/libs/buildscape-forge-1.18.2-4.0.3.jar`
(the `-dev` jar is not a production artifact). It is the output of `remapJar`,
which remaps classes to SRG names and generates the SRG mixin refmap. Text
resources are normalised from CRLF to LF so the jar does not depend on the
checkout's line endings. The bundled recipe cache is copied unchanged from the
frozen reference. The development access transformer is derived at configuration
time from the reference file (unimined does not translate SRG ids in it); the
jar ships the original.

A clean cache needs dependency population in a network-enabled environment
before this offline command can work; cached Forge 40.2.10 is insufficient.

`:forge:1.18.2:runServer` (same two `-P` properties) runs a dedicated server in
`forge/1.18.2/run/server`; accept the EULA there yourself first. Gradle does not
forward stdin, so stop it by ending only the server's Java process.

### Forge 1.18.2 client verification

The common-source client registers render layers, reference block/item tints,
block entity and entity renderers, copper chest and jar item renderers, menu
screens, particle providers, the cinematic zoom key, input and render events,
texture stitching, hedge model baking, shulker tooltip components and the
Mods-menu config screen. Experience fluid uses the reference fog colour and
distances. Haze bushes emit nearby particles through a client-only
`animateTick` mixin and track loaded chunks and block changes for distant haze.
The client mixin list retains every reference client entry; new item-extension
and haze mixins also belong exclusively to that list. Their package names
remain in the root mixin package to pass the existing cluster-selection plugin.

Implementation has received source review only. The build and dedicated-server
smoke test must pass before Milestone 1 acceptance. A running client is still
needed to verify title-screen startup, world loading, resource-pack reloads,
transparency and tints, all renderers (including chest oxidation variants and
both boats), chest/jar inventory models, workbench/pouch interactions, shulker
tooltips, particles at each particle setting, haze drainage and chunk reloads,
zoom and tool input, world overlays, fluid fog and config-screen navigation.
Compare these behaviours with the reference jar and inspect `latest.log` for
client errors. These visual and interaction checks have not been run.

## Parked targets

By owner decision, `forge:26.2` and `forge:26.3` are parked because Forge 26.x
has no working toolchain. `fabric:26.3` and `neoforge:26.3` follow after 26.2.
`fabric:1.18.2`, `neoforge:1.21.1`, `fabric:1.21.1`, and
`forge:1.21.1` are maintained separately. Their directories remain on disk
and are excluded from settings, so their builds and runs are unavailable in
the default project graph.
