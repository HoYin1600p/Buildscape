# Buildscape Architecture Manual: VersionCluster Adapters

## 1. Architectural Philosophy

Buildscape targets multiple major Minecraft versions and mod loaders simultaneously from a single unified codebase:
- **Minecraft Versions:** 1.18.2, 1.21.1, 26.x (Snapshot/Next-Gen)
- **Mod Loaders:** Forge, Fabric, NeoForge

Rather than creating separate fork branches or duplicating thousands of lines of identical gameplay and asset logic, Buildscape employs **VersionCluster Adaptation**.

### The Three VersionClusters & Adapters
Minecraft's internal engine architecture historically shifts in major paradigms ("VersionClusters"):

| VersionCluster Identifier | Minecraft Target | Core Architectural Realities |
| :--- | :--- | :--- |
| **`v118x`** | 1.18.2 | PoseStack in rendering; `float[]` beacon colors; legacy NBT item stacks; standard registry mappings; Forge `FMLJavaModLoadingContext`. |
| **`v121x`** | 1.21.1 | Data Components replace ItemStack NBT; `GuiGraphics` in screen rendering; packed integer colors; NeoForge unified event bus; frozen registries with intrusive holders. |
| **`v26x`** | 26.x / Next-Gen | Snapshot pipeline; `Identifier` class renames; modern SubmitNode render graphs; strict data codecs. |

At runtime, the concrete adapter implementation is dynamically resolved by Java's `ServiceLoader` via `Services.PLATFORM` (`IPlatformAdapter.class`).

---

## 2. Directory & Layer Organization

The repository is structured strictly into three functional tiers:

```
Buildscape-com/
├── common/                                      # Multi-Version Source Core
│   └── src/main/java/com/kingodogo/buildscape/
│       ├── BuildscapeCommon.java                # Global neutral entrypoint & lifecycle
│       ├── block/                               # Neutral block definitions & registrations
│       ├── item/                                # Neutral item definitions & tabs
│       ├── entity/                              # Neutral entity definitions
│       ├── network/                             # Neutral packet contracts & dispatchers
│       ├── platform/                            # Platform & Registry Service SPIs
│       ├── mixin/                               # Unified Mixin interfaces & injectors
│       │   ├── IMixinFactory.java               # VersionCluster Mixin capability interface
│       │   └── MixinFactory.java                # Factory delegator
│       └── adapter/                             # VERSIONCLUSTER ADAPTERS (Source Sets)
│           ├── v118x/                           # VersionCluster 1.18.x Adapters & Factories
│           ├── v121x/                           # VersionCluster 1.21.x Adapters & Factories
│           └── v26x/                            # VersionCluster 26.x Adapters & Factories
│
├── fabric/                                      # Fabric Loader Implementations
│   ├── 1.18.2/
│   ├── 1.21.1/
│   ├── 26.2/
│   └── 26.3/
├── forge/                                       # Forge Loader Implementations
│   ├── 1.18.2/
│   ├── 1.21.1/
│   ├── 26.2/
│   └── 26.3/
└── neoforge/                                    # NeoForge Loader Implementations
    ├── 1.21.1/
    ├── 26.2/
    └── 26.3/
```

---

## 3. The Three Functional Tiers

### Tier 1: Neutral Common Core (`common/src/main/java/...`)
Contains all game logic, business logic, block state rules, recipes, definitions, packet contracts, and registries that are invariant across versions and loaders.
- **Rule:** This layer MUST NOT import any loader-specific class (no `net.minecraftforge`, no `net.fabricmc`, no `net.neoforged`).
- **Rule:** This layer MUST NOT directly call version-divergent Minecraft APIs (such as data components vs NBT, `GuiGraphics` vs `PoseStack`, or modern render graph collectors). Divergent operations must go through `Services.PLATFORM` or factory classes.

### Tier 2: VersionCluster Adapters (`common/src/main/java/.../adapter/v...x/`)
Contains version-specific implementations compiled against their respective Minecraft SDK:
- **`PlatformAdapterBase.java`**: Implements `IPlatformAdapter`. Houses version-dependent block entity instantiation, player inventory access, registry safety, particle dispatch, and fluid helpers.
- **`MixinFactory.java`**: Implements `IMixinFactory`. Supplies version-specific logic invoked by mixins (screen widget addition, tooltip rendering, beam clipping, trident accessor unpacking).
- **`RenderFactory.java`**: Houses version-specific entity, block entity, and item rendering logic.
- **`RecipeFactory.java`**: Supplies recipe serializers, pattern encoders, and matching logic.
- **`WorldGenFactory.java`**: Supplies placement modifiers, trunk placers, tree decorators, and foliage placers.
- **`GuiProvider.java`**: Provides abstraction over `GuiGraphics` / `PoseStack` conversions and widget registration.
- **`PacketFactory.java`**: Implements payload stream codecs and network translation.

### Tier 3: Loader Platform Modules (`fabric/`, `forge/`, `neoforge/`)
Contains lightweight loader glue:
- Mod entrypoint classes (`@Mod`, `ModInitializer`).
- Event bus subscriptions (`RegisterPayloadHandlersEvent`, `RegisterMenuScreensEvent`, `EntityAttributeCreationEvent`).
- Service loader binding files (`META-INF/services/com.kingodogo.buildscape.platform.IPlatformAdapter`).
- `BaseRegistryAdapter` subclasses (`NeoForgeRegistryAdapter`, `FabricRegistryAdapter`, `ForgeRegistryAdapter`).
