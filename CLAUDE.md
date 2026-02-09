
# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BuildScape is a Minecraft Forge mod for 1.18.2 that adds building blocks and decorative items. Written in Java 17, built with Gradle 8.14 and Forge 40.3.11. Mod ID is `buildscape`.

## Build & Run Commands

All commands use the Gradle wrapper (`gradlew.bat` on Windows, `./gradlew` on Unix):

```bash
gradlew build              # Full build, outputs JAR to build/libs/
gradlew runClient          # Launch Minecraft client in dev mode (username: "Dev", 4GB heap)
gradlew runServer          # Launch dedicated server (headless, 4GB heap)
gradlew runData            # Run data generators (outputs to src/generated/resources/)
gradlew gameTestServer     # Run game tests
gradlew clean              # Clean build artifacts
```

No automated test suite or linter is configured. Testing is done via `runClient` and `gameTestServer`.

## Architecture

### Entry Point & Registration

`BuildScape.java` is the `@Mod` entry point. Its constructor wires everything together:
- Retrieves the mod event bus via `FMLJavaModLoadingContext.get().getModEventBus()`
- Calls `.register(modEventBus)` on every `DeferredRegister` (blocks, items, particles, block entities, entities, recipe serializers, world gen features)
- Registers lifecycle listeners (`commonSetup` for `FMLCommonSetupEvent`)
- Registers to `MinecraftForge.EVENT_BUS` for runtime game events
- Defines the custom `CreativeModeTab` with explicit item ordering in `fillItemList()`

### Registry Pattern

All game objects use Forge's `DeferredRegister<T>` + `RegistryObject<T>` pattern:
- `ModBlocks.BLOCKS` — block registry, each block is a `RegistryObject<Block>` created via lambda supplier
- `ModItems.ITEMS` — item registry, most items are `BlockItem` wrappers referencing their block via `.get()`
- `ModParticles` — particle type registry
- `ModBlockEntities` — block entity registry
- `ModMessages` — network packet registration using `SimpleChannel` with protocol versioning

### Package Structure

```
com.kingodogo.buildscape/
├── block/          # Block classes + ModBlocks registry (~85 files)
├── item/           # Item classes + ModItems registry
├── client/         # Client-only code: keybinds, renderers, screens, widgets
├── particle/       # Custom particle types + ModParticles registry
├── network/        # Client-server packets (SimpleChannel)
├── event/          # @SubscribeEvent handlers for game events
├── config/         # Mod configuration
├── cosmetics/      # Supporter/cosmetic content
├── data/           # Data generation providers (recipes, loot tables, tags)
├── mixin/          # Bytecode transformations via Mixin
├── recipe/         # Custom recipe types
├── sound/          # Sound registry
├── util/           # Utility classes
├── entity/         # Custom entities
├── world/          # World generation hooks
└── worldgen/       # World generation features
```

### Networking

`ModMessages` registers packets via `SimpleChannel` with incrementing IDs. Each packet class has static `encode`, `decode`, and `handle` methods. Registration happens inside `FMLCommonSetupEvent` via `event.enqueueWork()` for thread safety.

### Mixins

Configured in `src/main/resources/META-INF/buildscape.mixins.json`. Server-side mixins patch WallBlock, IronBarsBlock, FenceBlock, ChainBlock, LivingEntity, and Entity. Client-side mixin patches PauseScreen. Mixin classes live in `com.kingodogo.buildscape.mixin`.

### Access Transformers

`src/main/resources/META-INF/accesstransformer.cfg` exposes private constructors for `TrunkPlacerType`, `FoliagePlacerType`, `TreeDecoratorType`, and `GameRules$BooleanValue`.

### Resources

- `src/main/resources/assets/buildscape/` — blockstates, models, textures, particles, sounds, lang files
- `src/main/resources/data/buildscape/` — recipes, loot tables, tags, worldgen configs
- `src/generated/resources/` — auto-generated resources from `runData` (included as a source set)

### Key Dependencies

- Minecraft Forge 1.18.2-40.3.11
- JEI 9.7.1.255 (runtime only, for recipe viewing)
- SpongePowered Mixin 0.8.5
- Mojang official mappings for 1.18.2

### Gradle Properties

Version metadata and mod identity are defined in `gradle.properties` and substituted into `META-INF/mods.toml` and `pack.mcmeta` at build time via `processResources`.
