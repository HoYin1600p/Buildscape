# Buildscape Build & Test Verification Manual

This guide documents the procedures for compiling, building, testing, and debugging Buildscape across all supported loaders and Minecraft versions.

---

## 1. Quick Verification Commands

All tasks can and should be run with `--offline` once dependencies are populated.

### Multi-VersionCluster Compilation Verification
To verify that all three VersionCluster source sets compile cleanly without errors:
```powershell
.\gradlew.bat :common:compileV118xJava :common:compileV121xJava :common:compileV26xJava --offline --console=plain
```

### Full Multi-Project Build (All 11 Subprojects)
To build all Forge, Fabric, NeoForge, and Common subprojects:
```powershell
.\gradlew.bat build -x test --offline --console=plain
```

---

## 2. Launching Clients

### NeoForge 1.21.1 Client
```powershell
.\gradlew.bat :neoforge:1.21.1:runClient --offline --console=plain
```

### Forge 1.18.2 Client
```powershell
.\gradlew.bat :forge:1.18.2:runClient --offline --console=plain
```

### Fabric 1.21.1 Client
```powershell
.\gradlew.bat :fabric:1.21.1:runClient --offline --console=plain
```

---

## 3. Diagnostic & Log Inspection Procedures

### Important Notice on Mod Loading Crashes
When NeoForge or Forge encounters a mod loading error, Minecraft does not immediately terminate the JVM. Instead, it displays an in-game graphical error screen ("Mod loading failures have occurred; consult the issue messages for more details"). The JVM continues to run and render this error screen until a user clicks "Quit Game".

**For Maintainers & Automated Agents:**
- Do not assume the game is frozen or running normally simply because the Gradle task hasn't finished.
- Check `run/crash-reports/` immediately:
  ```powershell
  Get-ChildItem -Path "neoforge/1.21.1/run/crash-reports" | Sort-Object LastWriteTime -Descending | Select-Object -First 1
  ```
- Check the tail of `run/logs/latest.log` or `run/logs/debug.log`:
  ```powershell
  Get-Content -Path "neoforge/1.21.1/run/logs/latest.log" -Tail 40
  ```
- Use `jstack <pid>` to inspect whether the Render thread is executing `Minecraft.runTick` / `TitleScreen` or waiting on an error screen.

---

## 4. Common Runtime Pitfalls & Solutions

| Symptom | Root Cause | Resolution |
| :--- | :--- | :--- |
| `java.lang.IllegalStateException: Registry is already frozen` | Mod code attempted to call `Registry.register(...)` or instantiate a `Block`/`Item`/`BlockEntityType` after vanilla registry freeze. | Wrap the entire initialization block with `Services.PLATFORM.wrapRegistryAction(() -> { ... });`. |
| `java.lang.AssertionError: Missing intrusive holder for ...` | Synthetic `unregisteredIntrusiveHolders` map was erroneously populated on a non-intrusive registry (e.g. `BLOCKSTATE_PROVIDER_TYPE`). | Ensure intrusive holder maps are ONLY attached to registries that actually use intrusive holders (`BLOCK`, `ITEM`, `BLOCK_ENTITY_TYPE`, `ENTITY_TYPE`, `FLUID`). |
| `NoClassDefFoundError: ...ScreenMixin is invalid` | Mixin method used a method reference or lambda capturing `this` (e.g. `this::addRenderableWidget`), generating synthetic private methods. | Delegate widget addition and UI manipulation to reflection/handlers inside `MixinFactory.java`. |
| `InvalidAccessorException: No candidates were found matching ...` | An `@Invoker` or `@Accessor` in `common` targeted a method or field whose signature changed between Minecraft versions. | Do not put version-divergent invokers in `common`. Use reflection or MethodHandles isolated inside the specific VersionCluster's `MixinFactory.java`. |
| `JsonParseException: forge_data should be replaced by neoforge_data` | Model JSON contains legacy Forge lighting data (`"forge_data"`). | In NeoForge targets, transform or rename `"forge_data"` to `"neoforge_data"`. |
| `JsonSyntaxException: Missing axis, expected to find a string` | Blockbench exported dummy 0-degree rotation blocks without an `"axis"` property. | Remove the empty rotation block (`"rotation": {"x": 0, "y": 0, "z": 0, "origin": [...]}`) from the model JSON. |
