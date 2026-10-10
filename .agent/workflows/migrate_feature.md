# Workflow: Feature Migration

Use this workflow to migrate blocks, items, block entities, entities, recipes, worldgen, or screens from `ref/forge/1.18.2` into the neutral common core and VersionCluster adapters.

---

## Migration Philosophy

- **No Stubs:** Never create empty stub methods or placeholder throws.
- **No Temporary Exclusions:** Never exclude classes from the build.
- **Common Neutrality:** Extract all version-invariant logic into `common/...`.
- **Minimal Adapters:** Place only native constructor signatures, renamed methods, and engine data bridges in `common/.../adapter/<cluster>/`.

---

## Steps

### 1. Analyze the Reference Class
Examine the original 1.18.2 implementation in `ref/forge/1.18.2/src/main/java/...`.
- Identify neutral business logic (tile logic, ticks, state properties, sounds, recipes).
- Identify version-divergent logic (NBT vs data components, PoseStack vs GuiGraphics, registry methods).

### 2. Implement the Neutral Common Definition
- Place the neutral class in `common/src/main/java/com/kingodogo/buildscape/<category>/`.
- Ensure NO loader packages (`net.minecraftforge`, `net.fabricmc`, `net.neoforged`) are imported.
- Register entries in `ModBlocks.java`, `ModItems.java`, `ModBlockEntities.java`, or `ModEntities.java`.

### 3. Implement VersionCluster Factories
For each active VersionCluster (`v118x`, `v121x`, `v26x`):
- **Blocks:** Implement creation in `BlockFactory.java`.
- **Block Entities / Entities:** Implement type instantiation in `PlatformAdapterBase.java`.
- **Renderers:** Implement in `RenderFactory.java`.
- **Recipes:** Implement serializers and codecs in `RecipeFactory.java`.
- **Screens:** Implement rendering in `GuiProvider.java` / `MixinFactory.java`.

### 4. Verify Multi-VersionCluster Compilation
Run the multi-cluster compilation check:
```powershell
.\gradlew.bat :common:compileV118xJava :common:compileV121xJava :common:compileV26xJava --offline --console=plain
```

### 5. Verify Active Loader Compilation
Verify on the active loader (e.g. NeoForge 1.21.1):
```powershell
.\gradlew.bat :neoforge:1.21.1:compileJava --offline --console=plain
```
