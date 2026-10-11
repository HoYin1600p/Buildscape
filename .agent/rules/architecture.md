---
trigger: always_on
description: Architectural layout and tier boundaries for the Buildscape repository.
---

# Buildscape Agent Rules: VersionCluster Architecture

## 1. The Three Architectural Tiers

1. **Tier 1: Neutral Common Core (`common/src/main/java/com/kingodogo/buildscape/`)**
   - Contains all version-invariant gameplay logic, block/item definitions, common packet contracts, and registries.
   - **Invariants:**
     - Must NOT import loader-specific classes (`net.minecraftforge`, `net.fabricmc`, `net.neoforged`).
     - Must NOT call version-divergent Minecraft methods directly (e.g. `GuiGraphics`, data components, `PoseStack`, render buffers).
     - Must route divergent tasks through `Services.PLATFORM`, `MixinFactory`, or dedicated factories.

2. **Tier 2: VersionCluster Adapters (`common/src/main/java/.../adapter/<cluster>/`)**
   - Contains version-specific implementations compiled against their designated Minecraft SDK (`v118x`, `v121x`, `v26x`).
   - Core components:
     - `PlatformAdapterBase.java`: Implements `IPlatformAdapter`.
     - `MixinFactory.java`: Implements `IMixinFactory`.
     - `RenderFactory.java`: Handles entity, block entity, and item rendering.
     - `RecipeFactory.java`: Serializers, pattern matchers, and network codecs.
     - `WorldGenFactory.java`: Placers, decorators, and features.
     - `GuiProvider.java`: Screen widgets and GUI render operations.
     - `PacketFactory.java`: Payload codecs and network dispatch.

3. **Tier 3: Loader Subprojects (`forge/`, `fabric/`, `neoforge/`)**
   - Thin glue modules housing:
     - Entrypoints (`@Mod`, `ModInitializer`).
     - Event bus listeners (`RegisterPayloadHandlersEvent`, `RegisterMenuScreensEvent`, `EntityAttributeCreationEvent`).
     - `META-INF/services/` bindings for `IPlatformAdapter`.
     - Loader-specific `BaseRegistryAdapter` implementations.

---

## 2. Terminology Guidelines

- The version clusters are **`v118x`** (Minecraft 1.18.2), **`v121x`** (Minecraft 1.21.1), and **`v26x`** (26.x snapshot / next-gen).
- Always refer to these version groups as **VersionClusters** or **Adapters**.
- In Gradle properties or scripts, use `versionCluster` or `buildscape.target_cluster`. Never use `activeEra` or `era`.
