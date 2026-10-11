# Buildscape

Buildscape shares gameplay code across VersionClusters and uses loader-specific adapters.

## Build

Run the repository's Gradle 9.6 wrapper on JDK 25. Have a Java 17 toolchain
installed locally for Forge 1.18.2 compilation and v118x test execution.
The wrapper distribution, Minecraft/Forge artifacts and dependencies must
already be cached for offline builds.

```powershell
.\gradlew.bat build --offline --console=plain --parallel --max-workers=8
```

At Milestone 1, the default build includes Fabric and NeoForge 26.2 and
Forge 1.18.2 using shared code (`buildscape.v118xSource=common`). It runs
`:common:v118xTest`, all `:common:v118xRun*Tests` tasks and the v118x recipe
checks alongside the 26.2 tests. The Forge production jar is
`forge/1.18.2/build/libs/buildscape-4.0.3.jar`. The 26.2 loader jars are
collected into the root `build/libs` directory.

For a fast 26.2-only build, omit the Forge 1.18.2 project and skip v118x
verification in `common:check`:

```powershell
.\gradlew.bat build '-Pbuildscape.v118x=false' --offline --console=plain --parallel --max-workers=8
```

For parity comparisons, select the frozen reference sources explicitly:

```powershell
.\gradlew.bat :forge:1.18.2:build '-Pbuildscape.v118xSource=ref' --offline --console=plain --parallel --max-workers=8
```

Existing parity tooling can keep its explicit shared-code flags:

```powershell
.\gradlew.bat :forge:1.18.2:build '-Pbuildscape.v118x=true' '-Pbuildscape.v118xSource=common' --offline --console=plain --parallel --max-workers=8
```

The [parity harness](tools/parity-dumper/README.md) is a separate opt-in,
test-only build and is never included in production jars.
