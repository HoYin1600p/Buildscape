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
26.2. The `v118x` and `v121x` source sets remain defined for separately
maintained targets, but the default build compiles only `v26x`.

Each loader jar includes the compiled common `v26x` classes, common resources,
its own metadata, service bindings, and the unified `buildscape.mixins.json`.
The jars are collected in root `build/libs` and also remain in each loader's
`build/libs` directory. Minecraft 26.x uses official names, so these jars do
not need remapping.

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
cache, metadata expansion and access transformer. Client registrations are a
separate integration stage. Use `--no-daemon` for every Forge invocation.
`mod_version_v118x=4.0.3` controls this jar independently of the 26.2 version.
The production jar is `forge/1.18.2/build/libs/buildscape-4.0.3.jar`
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

## Parked targets

By owner decision, `forge:26.2` and `forge:26.3` are parked because Forge 26.x
has no working toolchain. `fabric:26.3` and `neoforge:26.3` follow after 26.2.
`fabric:1.18.2`, `neoforge:1.21.1`, `fabric:1.21.1`, and
`forge:1.21.1` are maintained separately. Their directories remain on disk
and are excluded from settings, so their builds and runs are unavailable in
the default project graph.
