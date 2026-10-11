# Buildscape Maintainer Guide: Which Thing Goes Where?

This guide walks maintainers through implementing and modifying features across all supported Minecraft versions and mod loaders.

---

## 1. Quick Location Map

| Feature Type | Neutral Common Location | VersionCluster Adapter Implementation (`adapter/<cluster>/`) | Loader Platform Location |
| :--- | :--- | :--- | :--- |
| **Blocks** | `common/.../block/ModBlocks.java`<br>`common/.../block/BlockDefinition.java` | `BlockFactory.java` (creates version-specific Block instances) | Auto-registered via `BaseRegistryAdapter` |
| **Items** | `common/.../item/ModItems.java`<br>`common/.../item/ItemDefinition.java` | `ItemFactory.java` | Auto-registered via `BaseRegistryAdapter` |
| **Block Entities** | `common/.../block/ModBlockEntities.java` | `PlatformAdapterBase.createBlockEntityType(...)` | `PlatformAdapterBase.java` |
| **Block Entity Renderers** | N/A (Client only) | `RenderFactory.registerBlockEntityRenderers(...)` | Invoked during client setup |
| **Entities** | `common/.../entity/ModEntities.java` | `PlatformAdapterBase.create...Entity(...)` | Entity attribute creation in Loader Mod classes |
| **Entity Renderers** | N/A (Client only) | `RenderFactory.registerEntityRenderers(...)` | Invoked during client setup |
| **Recipes** | `common/.../recipe/` | `RecipeFactory.java` (Serializers, patterns, matchers) | Auto-registered via `BaseRegistryAdapter` |
| **World Gen** | `common/.../worldgen/` | `WorldGenFactory.java` (placers, decorators, codecs) | Registered via `Services.PLATFORM.registerWorldGen()` |
| **Packets / Network** | `common/.../network/` | `PacketFactory.java` (payload codecs, stream serialization) | Registered via loader network events (`RegisterPayloadHandlersEvent`) |
| **Screens / UI** | `common/.../menu/` | `GuiProvider.java` / `MixinFactory.java` | Screens registered in loader client setup (`RegisterMenuScreensEvent`) |
| **Mixins** | `common/.../mixin/` | `adapter/<cluster>/MixinFactory.java` | `buildscape.mixins.json` |

---

## 2. Step-by-Step Implementation Guides

### Adding a New Block
1. **Define Block in Neutral Common:**
   In `common/src/main/java/com/kingodogo/buildscape/block/ModBlocks.java`:
   ```java
   public static final RegistrySupplier<BlockDefinition> MY_BLOCK = register("my_block",
           () -> new BlockDefinition("my_block", BlockProperties.STONE));
   ```
2. **Implement Block Factory in Each VersionCluster:**
   In `common/.../adapter/<cluster>/BlockFactory.java`, add the instantiation logic mapping `BlockDefinition` to `net.minecraft.world.level.block.Block`.
3. **Add Model & Blockstate JSONs:**
   Under `common/src/main/resources/assets/buildscape/blockstates/my_block.json` and `models/block/my_block.json`.
   > **Note on Model JSONs:** Do NOT include empty Blockbench rotation blocks (`"rotation": {"x": 0, "y": 0, "z": 0, ...}`). In Minecraft models, rotation requires an explicit `"axis"` string.

---

### Adding a New Block Entity
1. **Define Block Entity Type in Neutral Common:**
   In `common/src/main/java/com/kingodogo/buildscape/block/ModBlockEntities.java`:
   ```java
   public static final BlockEntityType<MyBlockEntity> MY_BLOCK_ENTITY =
           Services.PLATFORM.createBlockEntityType(MyBlockEntity::new, state -> state.is(ModBlocks.MY_BLOCK.get()));
   ```
2. **Define Block Entity Class in Neutral Common:**
   Extend `BlockEntity` and use `Services.PLATFORM` for reading/writing NBT and syncing packets.
3. **Add Renderer (If Custom Rendering Needed):**
   Register the renderer in `common/.../adapter/<cluster>/RenderFactory.java`:
   ```java
   registerBlockEntityRenderer(ModBlockEntities.MY_BLOCK_ENTITY, MyBlockEntityRenderer::new);
   ```

---

### Adding a New Recipe Serializer
1. **Declare Serializer Supplier:**
   In `common/.../adapter/<cluster>/RecipeFactory.java`:
   ```java
   private static final RegistrySupplier<RecipeSerializer<MyCustomRecipe>> MY_RECIPE =
           Services.REGISTRY.registerRecipeSerializer("my_recipe", MyRecipeSerializer::new);
   ```
2. **Implement Recipe & Serializer:**
   - In `v118x`: Use legacy `FriendlyByteBuf` and `JsonObject` deserializers.
   - In `v121x` and `v26x`: Use modern `MapCodec` and `StreamCodec<RegistryFriendlyByteBuf, MyCustomRecipe>`.

---

### Adding a New Screen or GUI Feature
1. **Container Menu:**
   Define your `MenuType` and `AbstractContainerMenu` in `common/.../menu/`.
2. **Screen Rendering:**
   In `adapter/<cluster>/GuiProvider.java`:
   - `v118x`: Renders using `com.mojang.blaze3d.vertex.PoseStack`.
   - `v121x` & `v26x`: Renders using `net.minecraft.client.gui.GuiGraphics`.
3. **Screen Mixins:**
   If modifying a vanilla screen (e.g., Anvil, Stonecutter, Creative Inventory):
   - Place the mixin in `common/.../mixin/`.
   - Never inject version-specific widgets directly inside the mixin class.
   - Delegate to `MixinFactory.get().addWidgetToScreen(...)` or `MixinFactory.get().renderAnvilZeroCostLabel(...)`.

---

### Resource Handling & Model Rules
- **NeoForge Data vs Forge Data:**
  In block models utilizing directional lighting attributes:
  - 1.18.2 Forge uses `"forge_data": { "block_light": 15, "sky_light": 15 }`.
  - 1.21.1+ NeoForge strictly rejects `"forge_data"` and requires `"neoforge_data"`.
- **Textures:**
  Place all shared textures in `common/src/main/resources/assets/buildscape/textures/`.
  Ensure texture references in model JSONs use valid namespaces (`buildscape:block/...` rather than `buildscape:textures/textures/block/...`).
