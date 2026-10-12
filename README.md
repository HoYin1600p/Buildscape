# Buildscape

Buildscape shares gameplay code across VersionClusters and uses loader-specific adapters.

Forge 1.21.1 dev runs are not supported yet; the release jar is tested in a real client.

Every loader uses the single `mod_version=4.0.3` key in `gradle.properties`
for its jar version and loader metadata. Update that key to change the version
across all VersionClusters.

## Build

Run the repository's Gradle 9.6 wrapper on JDK 25. Have a Java 17 toolchain
installed locally for Forge 1.18.2 compilation and v118x test execution.
The wrapper distribution, Minecraft/Forge artifacts and dependencies must
already be cached for offline builds.

```powershell
.\gradlew.bat build --offline --console=plain --parallel --max-workers=8
```

The default build includes Fabric and NeoForge 26.2 and
Forge 1.18.2 using shared code (`buildscape.v118xSource=common`). It runs
`:common:v118xTest`, all `:common:v118xRun*Tests` tasks and the v118x recipe
checks alongside the 26.2 tests. The Forge production jar is
`forge/1.18.2/build/libs/buildscape-forge-1.18.2-4.0.3.jar`. The 26.2 loader jars are
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

Fabric 1.18.2 server support is opt-in. It uses Fabric Loader 0.16.14,
Fabric API 0.77.0+1.18.2 and the same Java 17 toolchain and unified mod version
as Forge. After caching the Fabric dependencies, build its intermediary
release jar with:

```powershell
.\gradlew.bat :fabric:1.18.2:build '-Pbuildscape.fabric118x=true' --offline --console=plain --parallel --max-workers=8
```

The jar is written to `fabric/1.18.2/build/libs`. For a development dedicated
server, accept Minecraft's EULA in `fabric/1.18.2/run/eula.txt` and run
`:fabric:1.18.2:runServer` with the same opt-in property. Client setup remains
under development.
