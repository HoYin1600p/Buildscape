Buildscape-com/
├── build.gradle                                            # Root multi-project Gradle build script configuring multi-VersionCluster artifacts
├── settings.gradle                                         # Root Gradle settings defining subprojects
├── gradle.properties                                       # JVM heap and Gradle compiler daemon settings
├── gradlew                                                 # Unix Gradle wrapper shell script
├── gradlew.bat                                             # Windows Gradle wrapper batch script
├── buildscape-com.md                                       # Repository file tree inventory, architecture map, and module guide
│
├── .run/                                                   # Preconfigured IDE run configurations for all 11 loader/VersionCluster environments
│
├── scripts/                                                # Migration tooling, verification harnesses, and parity scripts
│   ├── migration-ledger.ps1                                # PowerShell ledger generator tracking class migration status
│   ├── test-era-wiring.ps1                                 # VersionCluster wiring and ServiceLoader binding verification script
│   └── update_ledger_nonclient.py                          # Automated batch updater parsing reference source sets into ledger
│
├── common/                                                 # Cross-loader shared logic, assets, data, and multi-VersionCluster implementations
│   ├── build.gradle                                        # Build script for the common module defining VersionCluster sourceSets
│   └── src/main/                                           # Main source root containing multi-VersionCluster code and resources
│       ├── java/                                           # Shared cross-loader Java source root
│       │   └── com/kingodogo/buildscape/                   # Base package for all Buildscape mod logic
│       │   │   ├── BuildScape.java                         # Legacy mod class shim retaining backward-compatible constants
│       │   │   ├── BuildscapeCommon.java                   # Primary cross-loader lifecycle entrypoint coordinating mod subsystems
│       │   │   │
│       │   │   ├── adapter/                                # Version compatibility bridges for native Minecraft engine classes
│       │   │   │   ├── v118x/                              # Minecraft 1.18.2 Java 17 native implementations
│       │   │   │   │   ├── BlockFactory.java               # 1.18.2 block instantiation factory and constructor bridge
│       │   │   │   │   ├── CompoundTagData.java            # 1.18.2 NBT CompoundTag persistence wrapper
│       │   │   │   │   ├── DataBlockEntity.java            # 1.18.2 base block entity with custom serialization
│       │   │   │   │   ├── GeyserBaseParticleOptions.java  # 1.18.2 particle options base for geyser effects
│       │   │   │   │   ├── GeyserParticleOptions.java      # 1.18.2 particle parameter payload
│       │   │   │   │   ├── GeyserParticleTypes.java        # 1.18.2 particle type registration holder
│       │   │   │   │   ├── GuiProvider.java                # 1.18.2 client GUI screen provider and factory bridge
│       │   │   │   │   ├── MixinFactory.java               # 1.18.2 mixin hook bridge and reflection adapter
│       │   │   │   │   ├── PacketFactory.java              # 1.18.2 packet encoder, decoder, and handler bridge
│       │   │   │   │   ├── ParticleFactory.java            # 1.18.2 particle provider factory and client renderer bridge
│       │   │   │   │   ├── PlatformAdapterBase.java        # 1.18.2 platform abstraction implementing entity attributes, renderers, audio, and NBT
│       │   │   │   │   ├── RecipeFactory.java              # 1.18.2 recipe construction and serialization factory
│       │   │   │   │   ├── RenderFactory.java              # 1.18.2 block entity and entity renderer provider
│       │   │   │   │   └── WorldGenFactory.java            # 1.18.2 world generation features, placers, and trunk decorators
│       │   │   │   ├── v121x/                              # Minecraft 1.21.1 Java 21 native implementations
│       │   │   │   │   ├── BlockFactory.java               # 1.21.1 block instantiation factory and constructor bridge
│       │   │   │   │   ├── CompoundTagData.java            # 1.21.1 NBT persistence wrapper
│       │   │   │   │   ├── DataBlockEntity.java            # 1.21.1 base block entity with custom serialization
│       │   │   │   │   ├── GeyserBaseParticleOptions.java  # 1.21.1 particle options base for geyser effects
│       │   │   │   │   ├── GeyserParticleOptions.java      # 1.21.1 particle parameter payload
│       │   │   │   │   ├── GeyserParticleTypes.java        # 1.21.1 particle type registration holder
│       │   │   │   │   ├── GuiProvider.java                # 1.21.1 client GUI screen provider and factory bridge
│       │   │   │   │   ├── MixinFactory.java               # 1.21.1 mixin hook bridge and reflection adapter
│       │   │   │   │   ├── PacketFactory.java              # 1.21.1 packet encoder, decoder, and handler bridge
│       │   │   │   │   ├── ParticleFactory.java            # 1.21.1 particle provider factory and client renderer bridge
│       │   │   │   │   ├── PlatformAdapterBase.java        # 1.21.1 platform abstraction implementing entity attributes, renderers, layer definitions, and NBT
│       │   │   │   │   ├── RecipeFactory.java              # 1.21.1 recipe construction and serialization factory
│       │   │   │   │   ├── RenderFactory.java              # 1.21.1 block entity and entity renderer provider
│       │   │   │   │   └── WorldGenFactory.java            # 1.21.1 world generation features, placers, and trunk decorators
│       │   │   │   └── v26x/                               # Minecraft 26.x Java 25 native implementations
│       │   │   │       ├── BlockFactory.java               # 26.x block instantiation factory and constructor bridge
│       │   │   │       ├── CreativeTabBridge.java          # 26.x creative mode inventory tab registration bridge
│       │   │   │       ├── DataBlockEntity.java            # 26.x base block entity with ValueInput/ValueOutput serialization
│       │   │   │       ├── GeyserBaseParticleOptions.java  # 26.x particle options base for geyser effects
│       │   │   │       ├── GeyserParticleOptions.java      # 26.x particle parameter payload
│       │   │   │       ├── GeyserParticleTypes.java        # 26.x particle type registration holder
│       │   │   │       ├── GuiProvider.java                # 26.x client GUI screen provider and factory bridge
│       │   │   │       ├── MixinFactory.java               # 26.x mixin hook bridge and reflection adapter
│       │   │   │       ├── PacketFactory.java              # 26.x packet encoder, decoder, and handler bridge
│       │   │   │       ├── ParticleFactory.java            # 26.x particle provider factory and client renderer bridge
│       │   │   │       ├── PlatformAdapterBase.java        # 26.x platform abstraction implementing entity attributes, renderers, layer definitions, and ValueInput/Output
│       │   │   │       ├── RecipeFactory.java              # 26.x recipe construction and serialization factory
│       │   │   │       ├── RenderFactory.java              # 26.x block entity and entity renderer provider
│       │   │   │       ├── ValueInputData.java             # 26.x read-only data serialization adapter wrapping ValueInput
│       │   │   │       ├── ValueOutputData.java            # 26.x decoupled write-only data serialization adapter wrapping native ValueOutput
│       │   │   │       └── WorldGenFactory.java            # 26.x world generation features, placers, and trunk decorators
│       │   │   │
│       │   │   ├── api/                                    # Online supporters and cosmetic authentication APIs
│       │   │   │   ├── CosmeticAuthManager.java            # Verifies cosmetic unlocking rights for players
│       │   │   │   ├── SupportersApiCache.java             # Local cache for remote supporter metadata
│       │   │   │   ├── SupportersApiClient.java            # Web API client querying Buildscape supporter servers
│       │   │   │   └── model/                              # Data models for supporters and tier levels
│       │   │   │       ├── ApiResponse.java                # Generic container for web service HTTP responses
│       │   │   │       ├── CosmeticData.java               # Model representing cosmetic IDs and metadata
│       │   │   │       ├── MembershipTier.java             # Supporter tier definitions and perks
│       │   │   │       ├── SupporterStatus.java            # Supporter active status and subscription levels
│       │   │   │       └── TiersResponse.java              # Supporter tier list HTTP response model
│       │   │   │
│       │   │   ├── block/                                  # Common block definitions, entities, properties, and interactions
│       │   │   │   ├── AshenKingPillarBlock.java           # Ashen King decorative pillar block logic
│       │   │   │   ├── BambooSignBlockEntity.java          # Custom bamboo sign block entity
│       │   │   │   ├── BigBookBlock.java                   # Open book display block supporting item insertion
│       │   │   │   ├── BigCandleBlock.java                 # Oversized candle block with dynamic flame lighting
│       │   │   │   ├── BigOrnamentBlock.java               # Large hanging ornament decoration block
│       │   │   │   ├── BlockDefinition.java                # Metadata definition schema for all 3,136 blocks
│       │   │   │   ├── BlockEntityDefinition.java          # Metadata descriptor for registered block entities
│       │   │   │   ├── BlockFactory.java                   # Dispatcher delegating creation to VersionCluster BlockFactory
│       │   │   │   ├── BoneDiceBlock.java                  # Interactive dice block that rolls randomized faces
│       │   │   │   ├── BuildersWorkbenchBlock.java         # Palette crafting and inventory sorting workbench
│       │   │   │   ├── BuildersWorkbenchBlockEntity.java   # Inventory and synchronization for the workbench
│       │   │   │   ├── CactusFlowerBlock.java              # Placeable desert flower block
│       │   │   │   ├── CascadeBlock.java                   # Infinite waterfall / water physics emitter block
│       │   │   │   ├── CascadeBlockEntity.java             # State tracker for flowing cascade networks
│       │   │   │   ├── CascadeBlockNoMist.java             # Cascade block variant suppressing mist particles
│       │   │   │   ├── CascadeWaterManager.java            # Water propagation calculator for cascade blocks
│       │   │   │   ├── ClimbableChainBlock.java            # Vertical climbing mechanics for hanging chains
│       │   │   │   ├── CloverBlock.java                    # Low-lying ground clover plant block
│       │   │   │   ├── ColoredMossBlock.java               # Dyed variations of decorative moss blocks
│       │   │   │   ├── ColoredMossLayersBlock.java         # Variable-height stacked moss layer block
│       │   │   │   ├── ColoredSporeBlossomBlock.java       # Dyed hanging spore blossom blocks
│       │   │   │   ├── CommonBlockProperties.java          # Version-agnostic builder for block hardness/sounds
│       │   │   │   ├── CopperBulbBlock.java                # Redstone toggle latch copper lamp
│       │   │   │   ├── CopperChestBlockEntity.java         # Container inventory & oxidation for copper chests
│       │   │   │   ├── CopperOxidationHandler.java         # Oxidation progression & scraping logic for copper
│       │   │   │   ├── CopperTorchBlock.java               # Standing copper torch with distinct particle flames
│       │   │   │   ├── CopperWallTorchBlock.java           # Wall-attached copper torch
│       │   │   │   ├── CreakingHeartBlock.java             # Decorative pale garden creaking heart block
│       │   │   │   ├── CushionBlock.java                   # Seat cushion eliminating fall damage & bouncing
│       │   │   │   ├── CustomSoundType.java                # Abstraction for custom block sound types
│       │   │   │   ├── DecoratedPotBlock.java              # Archaeological pottery block with sherd patterns
│       │   │   │   ├── DecoratedPotBlockEntity.java        # Stores sherd pattern tags on placed decorated pots
│       │   │   │   ├── DryGrassBlock.java                  # Arid dry grass plant block
│       │   │   │   ├── ExperienceFluidBlock.java           # Liquid experience fluid block
│       │   │   │   ├── ExposedCopperBulbBlock.java         # Stage 1 weathered copper bulb
│       │   │   │   ├── EyeblossomBlock.java                # Flower switching states with day/night cycle
│       │   │   │   ├── EyeblossomTransitionHandler.java    # Day/night trigger scheduler for eyeblossoms
│       │   │   │   ├── FallingSandBlock.java               # Gravity-affected decorative sand block
│       │   │   │   ├── FestiveLampBlock.java               # Holiday lantern block
│       │   │   │   ├── FestiveStockingBlock.java           # Wall-hanging holiday stocking block
│       │   │   │   ├── FestiveStockingBlockEntity.java     # Inventory and gift generation for stockings
│       │   │   │   ├── FireflyBushBlock.java               # Foliage block emitting ambient firefly particles
│       │   │   │   ├── FreshCopperBulbBlock.java           # Stage 0 unaffected copper bulb
│       │   │   │   ├── FroglightBlock.java                 # Backported froglight illumination block
│       │   │   │   ├── FrostRoseBlock.java                 # Freezing flower dealing slowness and frost
│       │   │   │   ├── GlassJarBlock.java                  # Decorative glass display jar block
│       │   │   │   ├── GlassJarBlockEntity.java            # Holds displayed item/mob data in glass jars
│       │   │   │   ├── GlazedGlassBlock.java               # Tinted decorative glazed window block
│       │   │   │   ├── GlowLightsBlock.java                # Ceiling-hanging string glow lights
│       │   │   │   ├── GlowLightsBlockEntity.java          # Tracks string connections and light colors
│       │   │   │   ├── GoldenDandelionBlock.java           # Rare dandelion variant with special growth
│       │   │   │   ├── GrassSlabBlock.java                 # Half-slab variant of grass blocks
│       │   │   │   ├── HangingMossBlock.java               # Ceiling-hanging dangling moss block
│       │   │   │   ├── HayBaleSlabBlock.java               # Half-slab variant of hay bales
│       │   │   │   ├── HollowLogBlock.java                 # Crawlable, log-storable hollow wood blocks
│       │   │   │   ├── HollowLogBlockEntity.java           # Stores glass caps and items inside hollow logs
│       │   │   │   ├── HollowPipeBlock.java                # Hollow architectural pipe block
│       │   │   │   ├── IBlockFactory.java                  # Common SPI factory interface for block construction
│       │   │   │   ├── ICommonAnalogOutput.java            # Interface for redstone comparator signal logic
│       │   │   │   ├── ICommonEntityInside.java            # Interface for entity collision and inside triggers
│       │   │   │   ├── ICommonInteractable.java            # Interface for right-click interaction routing
│       │   │   │   ├── ICommonNeighborAware.java           # Interface for block update and redstone changes
│       │   │   │   ├── ICommonPlayerDestroy.java           # Interface for custom drops on player block break
│       │   │   │   ├── ICommonRandomTick.java              # Interface for random tick processing (growth, weathering)
│       │   │   │   ├── ICommonRemoval.java                 # Interface for container dropping upon block removal
│       │   │   │   ├── ICommonShapeUpdate.java             # Interface for connected block shape calculations
│       │   │   │   ├── ICopperChestBlock.java              # Shared interface for all oxidation copper chests
│       │   │   │   ├── IcicleBlock.java                    # Hanging sharp icicle block
│       │   │   │   ├── IcicleCauldronBlock.java            # Cauldron collecting ice and freezing contents
│       │   │   │   ├── IcicleCauldronBlockEntity.java      # Freezing temperature tracker for cauldrons
│       │   │   │   ├── LargeChainBlock.java                # Oversized heavy chain block
│       │   │   │   ├── LeafHedgeBlock.java                 # Leaf hedge block behaving like wall/fence
│       │   │   │   ├── LeafLayersBlock.java                # Variable layer leaf block
│       │   │   │   ├── LeafLitterBlock.java                # Fallen leaf foliage scatter block
│       │   │   │   ├── LogSlabBlock.java                   # Half-slab wooden log block
│       │   │   │   ├── LogStrippingHandler.java            # Axe right-click log stripping logic
│       │   │   │   ├── MangroveLeavesBlock.java            # Mangrove foliage block
│       │   │   │   ├── MangrovePropaguleBlock.java         # Hanging mangrove propagule sapling block
│       │   │   │   ├── MangroveRootsBlock.java             # Waterloggable tangled mangrove root block
│       │   │   │   ├── MangroveSignBlockEntity.java        # Custom mangrove wood sign block entity
│       │   │   │   ├── ModBlock.java                       # Base custom block wrapper
│       │   │   │   ├── ModBlockEntities.java               # Registry holding all block entity type definitions
│       │   │   │   ├── ModBlockProperties.java             # Custom blockstate properties (states, facings, stages)
│       │   │   │   ├── ModBlocks.java                      # Central registry metadata for all 3,136 blocks
│       │   │   │   ├── ModBushBlock.java                   # Base class for ground bush and shrub blocks
│       │   │   │   ├── ModIronBarsBlock.java               # Custom architectural metal iron bar block
│       │   │   │   ├── ModLadderBlock.java                 # Custom wooden and metal ladder block
│       │   │   │   ├── ModLayerBlock.java                  # Multi-layer stackable architectural block
│       │   │   │   ├── ModSlabBlock.java                   # Standard half-slab architectural block
│       │   │   │   ├── ModStairBlock.java                  # Standard stair architectural block
│       │   │   │   ├── ModWallBlock.java                   # Standard connecting wall architectural block
│       │   │   │   ├── MonetFlowerBlock.java               # Decorative flower based on classical water lilies
│       │   │   │   ├── MossLayersBlock.java                # Layered moss block
│       │   │   │   ├── MossOverlayBlock.java               # Surface-hugging moss overlay block
│       │   │   │   ├── MudBlock.java                       # Dense muddy earth block
│       │   │   │   ├── MudSlabBlock.java                   # Slab variant of mud blocks
│       │   │   │   ├── MuffBlock.java                      # Sound dampener block that mutes nearby audio
│       │   │   │   ├── MuffBlockEntity.java                # Radius storage and sound dampening coordinator
│       │   │   │   ├── MulticolorGlowLightsBlock.java      # Rainbow/multicolored hanging string light block
│       │   │   │   ├── MushroomShelvesBlock.java           # Shelf-forming bracket fungi on tree trunks
│       │   │   │   ├── OrnamentBlock.java                  # Small hanging ornament decoration block
│       │   │   │   ├── OxidizedCopperBulbBlock.java        # Stage 3 fully oxidized copper bulb block
│       │   │   │   ├── PackedIcicleBlock.java              # Dense block formed from compact icicles
│       │   │   │   ├── PetalBlock.java                     # Decorative fallen flower petal carpet block
│       │   │   │   ├── PillarBlock.java                    # Segmented structural pillar block
│       │   │   │   ├── PillarBlockEntity.java              # Tracks pillar segment connections and sync
│       │   │   │   ├── PillarPart.java                     # Enum for pillar segment types (Single, Top, Mid, Bot)
│       │   │   │   ├── PipeBlock.java                      # Fluid and bubble transport pipe block
│       │   │   │   ├── PlanterHelper.java                  # Helper checking valid soil for custom vegetation
│       │   │   │   ├── PointedIcicleBlock.java             # Sharp hanging stalactite/icicle hazard that can drop on entities
│       │   │   │   ├── PotentSulfurBlock.java              # Reactive sulfur block generating bubbling steam
│       │   │   │   ├── PotentSulfurBlockEntity.java        # Steam reaction timer and sulfur gas state machine
│       │   │   │   ├── PotentSulfurState.java              # Enum representing sulfur activity stages
│       │   │   │   ├── ResinClumpBlock.java                # Sticky natural resin formation block found on trees and pale wood
│       │   │   │   ├── RoseVinesBlock.java                 # Climbing thorny rose vine block
│       │   │   │   ├── SculkCatalystBlock.java             # Custom decorative sculk catalyst block variant
│       │   │   │   ├── SculkCatalystHandler.java           # Calculates soul absorption and spread for custom catalyst blocks
│       │   │   │   ├── SculkVeinBlock.java                 # Spreading tendril sculk vein surface block
│       │   │   │   ├── SelectableSlotContainer.java        # Container helper for targeted slot raycasts
│       │   │   │   ├── ShelfBlock.java                     # 3-slot display shelf with chain-swapping mechanics
│       │   │   │   ├── ShelfBlockEntity.java               # Stores 3 items and hotbar swap state for shelves
│       │   │   │   ├── SideChainPart.java                  # Orientation state for connected side chains
│       │   │   │   ├── SideChainPartBlock.java             # Horizontal attachment link chain block
│       │   │   │   ├── SilkTouchOnlyGlassBlock.java        # Specialty architectural glass requiring silk touch to collect
│       │   │   │   ├── SilkTouchOnlyPaneBlock.java         # Specialty architectural glass pane requiring silk touch to collect
│       │   │   │   ├── SinksOnFarmland.java                # Behavior determining if a block turns farmland to dirt
│       │   │   │   ├── SmokeVentBlock.java                 # Stackable industrial vent block emitting dyed smoke
│       │   │   │   ├── SmokeVentBlockEntity.java           # Tracks smoke color, power state, and emission
│       │   │   │   ├── SnowOverlayBlock.java               # Surface-hugging dynamic snow blanket overlay block
│       │   │   │   ├── SnowyBushBlock.java                 # Foliage bush covered in dense winter snow
│       │   │   │   ├── SnowyFernBlock.java                 # Winterized ground fern with snow accumulation
│       │   │   │   ├── SnowyGrassBlock.java                # Winterized natural grass plant with frost layers
│       │   │   │   ├── SnowyLargeFernBlock.java            # Two-block high frost-covered fern decoration
│       │   │   │   ├── SnowyLeavesBlock.java               # Tree foliage block coated in realistic snow caps
│       │   │   │   ├── SnowyShortGrassBlock.java           # Low-lying ground grass tuft covered in snow
│       │   │   │   ├── SnowyTallGrassBlock.java            # Two-block high wild grass plant with snow accumulation
│       │   │   │   ├── SoftFabricBlock.java                # Decorative textile block reducing impact and dampening vibrations
│       │   │   │   ├── SpoolBlock.java                     # Wire and thread storage spool block
│       │   │   │   ├── StarBlock.java                      # Glowing holiday star block
│       │   │   │   ├── SteelBoltBlock.java                 # Industrial structural fastener bolt block
│       │   │   │   ├── StrawBedBlock.java                  # Rustic bed allowing daytime rest without spawn reset
│       │   │   │   ├── StringLightBlock.java               # Thin electrical string light wire block
│       │   │   │   ├── SulfurSpikeBlock.java               # Natural pointy sulfur deposit spike block
│       │   │   │   ├── SulfurSpikeLogic.java               # Impalement damage and fluid reaction for sulfur spikes
│       │   │   │   ├── TallDryGrassBlock.java              # Arid tall savanna and desert grass decoration
│       │   │   │   ├── TrappedDecoratedPotBlock.java       # Decorated pot emitting redstone when inspected
│       │   │   │   ├── TrappedDecoratedPotBlockEntity.java # Redstone pulse generator for trapped pots
│       │   │   │   ├── VerticalSlabBlock.java              # Architectural vertical half-slab block
│       │   │   │   ├── WallpaperFlatBlock.java             # Interior decorative wallpaper block adhering flat to wall faces
│       │   │   │   ├── WaterloggableGrateBlock.java        # Water-permeable metal grating block
│       │   │   │   ├── WeatheredCopperBulbBlock.java       # Stage 2 weathered copper bulb block
│       │   │   │   ├── WeatheringBarsBlock.java            # Iron/copper bars that weather and oxidize over time
│       │   │   │   ├── WeatheringBlockLogic.java           # Shared tick progression for weathering metals
│       │   │   │   ├── WeatheringBoltBlock.java            # Fastener bolt block undergoing oxidation
│       │   │   │   ├── WeatheringClimbableChainBlock.java  # Climbable chain block subject to rust and oxidation
│       │   │   │   ├── WeatheringGrateBlock.java           # Metal floor grate block that oxidizes
│       │   │   │   ├── WeatheringLanternBlock.java         # Lantern block that oxidizes over time
│       │   │   │   ├── WeatheringLargeChainBlock.java      # Heavy chain block that oxidizes
│       │   │   │   ├── WildflowersBlock.java               # Cluster of mixed decorative meadow wildflowers
│       │   │   │   ├── WoolLayersBlock.java                # Variable-height stacked carpet and wool layer block
│       │   │   │   ├── behavior/                           # Specialized physical behavior and harvesting math
│       │   │   │   │   ├── FallingSandPhysics.java         # Gravity and collapse math for loose blocks
│       │   │   │   │   ├── MiningSpeedCalculator.java      # Tool speed multipliers and effective harvesting
│       │   │   │   │   ├── SafeDestroySpeed.java           # Safe hardness and break time evaluator
│       │   │   │   │   └── VerticalSlabShape.java          # VoxelShape bounding boxes for vertical slabs
│       │   │   │   └── entity/                             # Block entity serialization abstractions and contracts
│       │   │   │       ├── BufferedBlockEntityData.java    # Memory buffer for cross-thread block entity data
│       │   │   │       ├── IBlockEntityData.java           # Common interface for block entity NBT storage supporting deep-copied subtrees
│       │   │   │       ├── IBlockEntityReadData.java       # Read-only serialization contract for loading block entity states
│       │   │   │       ├── IBlockEntityWriteData.java      # Write-only serialization contract for saving block entity states with copyTo synchronization
│       │   │   │       └── IDataSerializable.java          # Contract for custom entity byte serialization
│       │   │   │
│       │   │   ├── client/                                 # Client-side renderers, GUI screens, widgets, and keybinds
│       │   │   │   ├── BiomeBrushClientHandler.java        # Client preview overlay for the biome brush tool
│       │   │   │   ├── BuildscapeRenderLayers.java         # Maps blocks to Cutout, Translucent, or Solid layers
│       │   │   │   ├── ClientEvents.java                   # Central client-side event bus handling rendering, input, and ticks
│       │   │   │   ├── ConfettiBurstClient.java            # Client particle simulator rendering exploding confetti showers
│       │   │   │   ├── GeyserParticleHandler.java          # Dispatches high-pressure steam and water particles for geyser blocks
│       │   │   │   ├── HammerClientHandler.java            # Client raycaster and 3x3 block break highlight renderer for hammers
│       │   │   │   ├── HomemakerCooldownTracker.java       # Client-side cooldown display for NPC rituals
│       │   │   │   ├── InvisibleFrameOverlayRenderer.java  # Draws faint outline highlights around invisible item frames
│       │   │   │   ├── ModKeyBinds.java                    # Registers client keybindings for wrench rotation and zoom mechanics
│       │   │   │   ├── MuffBlockManager.java               # Client audio dampener checking muff block proximity
│       │   │   │   ├── MuffBlockRenderer.java              # Renders audio-dampening radius spheres in debug mode
│       │   │   │   ├── PillarMarkerManager.java            # Manages client-side visual link indicators between pillar segments
│       │   │   │   ├── PillarMarkerRenderer.java           # Renders glowing vertical connection lines between matching pillars
│       │   │   │   ├── PillarOverlayHandler.java           # Renders pillar configuration HUD tooltips and status markers
│       │   │   │   ├── SmokeVentParticleHandler.java       # Calculates velocity and turbulent thermal drift for colored smoke vents
│       │   │   │   ├── TreeChopHandler.java                # Client preview visualizer highlighting entire trees marked for felling
│       │   │   │   ├── WrenchClientHandler.java            # Handles interaction raycasts and rotation previews for the wrench tool
│       │   │   │   ├── ZoomHandler.java                    # Smooth FOV zoom interpolation controller
│       │   │   │   ├── performance/                        # Startup optimization and state cache precomputation
│       │   │   │   │   ├── BuildscapeBlockStateCacheCoordinator.java # Coordinates parallel pre-baking of complex state caches
│       │   │   │   │   ├── BuildscapeStartupWork.java      # Schedules asynchronous asset loading during game initialization
│       │   │   │   │   └── LaunchFasterInterop.java        # Compatibility hooks for startup optimization and cache warming mods
│       │   │   │   ├── renderer/                           # Custom block entity and entity visual renderers
│       │   │   │   │   ├── ArmorPillarRenderer.java        # BER rendering posed armor stands displaying equipment atop pillars
│       │   │   │   │   ├── ColoredItemFrameRenderer.java   # Custom entity renderer for 16-color tinted item frames
│       │   │   │   │   ├── CopperChestRenderer.java        # BER rendering copper chest models and animated opening lids
│       │   │   │   │   ├── DecoratedPotBlockEntityRenderer.java # BER rendering decorative pots with wobble animations and sherds
│       │   │   │   │   ├── FallingIcicleRenderer.java      # Entity renderer for falling dangerous icicle projectiles
│       │   │   │   │   ├── FestiveGlintHandler.java        # Custom shader and render-type handler applying rainbow holiday glints
│       │   │   │   │   ├── FestiveStockingBlockEntityRenderer.java # BER rendering wall stockings with sway physics and gifts
│       │   │   │   │   ├── FestiveStockingRenderer.java    # Entity renderer for free-hanging festive stocking entities
│       │   │   │   │   ├── GlassJarBlockEntityRenderer.java # BER rendering 3D items and mini entities floating inside glass jars
│       │   │   │   │   ├── HollowLogBlockEntityRenderer.java # BER rendering internal fluid columns, glass caps, and interior items
│       │   │   │   │   ├── IcicleCauldronBlockEntityRenderer.java # BER rendering cauldron freeze levels and embedded giant icicles
│       │   │   │   │   ├── MobPillarRenderer.java          # BER rendering scaled and posed living mobs displayed on pillars
│       │   │   │   │   ├── MobState.java                   # State record capturing pose, age, color, and attachments for pillar mobs
│       │   │   │   │   ├── MobStateParser.java             # Parses NBT tags and item attributes into typed MobState configurations
│       │   │   │   │   ├── PillarBlockEntityRenderer.java  # Central BER rendering spinning items, tools, and mob pedestals
│       │   │   │   │   ├── PipeWaterSurface.java           # Computes fluid meniscus geometry and animated surface ripples for pipes
│       │   │   │   │   ├── ShelfRenderer.java              # BER displaying up to 3 oriented items and weapons along shelf slots
│       │   │   │   │   ├── SignFrameRenderer.java          # BER rendering decorative wooden and metal frames around wall signs
│       │   │   │   │   └── TrappedDecoratedPotBlockEntityRenderer.java # BER rendering trapped pot wobble feedback and hidden redstone cues
│       │   │   │   ├── screen/                             # Configuration menus, editor interfaces, and settings tabs
│       │   │   │   │   ├── AbstractConfigTab.java          # Base class for tabbed configuration screens with scrolling layout
│       │   │   │   │   ├── BuildScapeConfigScreen.java     # Main mod settings menu with live previews and category navigation
│       │   │   │   │   ├── BuildersPouchScreen.java        # Interactive GUI for managing and sorting the multi-slot builder's pouch
│       │   │   │   │   ├── BuildersWorkbenchScreen.java    # Workbench GUI supporting palette crafting, filters, and bulk recipes
│       │   │   │   │   ├── DebugRenderConfig.java          # Toggles rendering hitboxes and debug outlines
│       │   │   │   │   ├── GuiConfigHelper.java            # Helper routines for layout calculation, tooltips, and widget alignment
│       │   │   │   │   ├── GuiEditorScreen.java            # Interactive live editor for rearranging HUD elements and markers
│       │   │   │   │   ├── IScreenDelegate.java            # Interface bridging screen lifecycle events across loader versions
│       │   │   │   │   ├── InventoryItemSelectorScreen.java # Searchable modal picker for choosing items and block categories
│       │   │   │   │   ├── ParticleColorSlots.java         # GUI color slot manager for custom dye selections
│       │   │   │   │   ├── PillarIdDetailConfigTab.java    # Settings tab for configuring individual pillar particle styles
│       │   │   │   │   ├── PillarIdsConfigTab.java         # Settings tab listing all registered pillar identifiers and bindings
│       │   │   │   │   ├── PillarItemsConfigTab.java       # Settings tab defining allowed pedestal display items and tags
│       │   │   │   │   ├── PillarParticlesConfigTab.java   # Settings tab configuring particle spawn rates, colors, and velocities
│       │   │   │   │   ├── WorldSettingsConfigTab.java     # Settings tab for global world mechanics and feature toggles
│       │   │   │   │   ├── widget/                         # Reusable interactive GUI components, buttons, and sliders
│       │   │   │   │   │   ├── ColorPickerWidget.java      # Interactive HSV color wheel and palette selector widget
│       │   │   │   │   │   ├── ColorSwatchButton.java      # Clickable colored swatch button for quick dye selection
│       │   │   │   │   │   ├── ConfigCategoryButton.java   # Sidebar navigation button switching active configuration tabs
│       │   │   │   │   │   ├── CustomButtonRenderer.java   # Renders custom rounded, textured, and styled UI buttons
│       │   │   │   │   │   ├── CustomScrollbarRenderer.java # Renders smooth draggable scrollbar thumbs and tracks
│       │   │   │   │   │   ├── ExistingItemsWidget.java    # Scrollable list showing active registered items with delete actions
│       │   │   │   │   │   ├── FlatIconButton.java         # Minimalist flat icon button with hover animations
│       │   │   │   │   │   ├── ICustomWidget.java          # Interface defining custom layout, render, and input handling for widgets
│       │   │   │   │   │   ├── IntSliderWidget.java        # Draggable slider control for numeric integer configuration values
│       │   │   │   │   │   ├── InventorySortDropdown.java  # Dropdown selector for inventory sorting criteria (name, count, id)
│       │   │   │   │   │   ├── ItemSelectionWidget.java    # Grid widget displaying items with search filtering and selection
│       │   │   │   │   │   ├── PresetsWidget.java          # Widget for saving, loading, and previewing named configuration presets
│       │   │   │   │   │   ├── ScaledTextButton.java       # Button that automatically rescales font size to fit long button labels
│       │   │   │   │   │   ├── SortToggleButton.java       # Toggle button switching between ascending and descending sort orders
│       │   │   │   │   │   ├── TagsSelectorWidget.java     # Tag checklist widget for bulk filtering block and item tags
│       │   │   │   │   │   ├── WideButton.java             # Full-width styled action button for primary dialog actions
│       │   │   │   │   │   └── WidgetLayoutHelper.java     # Grid and flow layout math helper for responsive screen widgets
│       │   │   │   │   └── workbench/                      # Workbench GUI renderers and layout calculations
│       │   │   │   │       └── WbRenderer.java             # Specialized batch renderer for workbench recipe preview grids
│       │   │   │   ├── tooltip/                            # Custom client hover tooltip data carriers
│       │   │   │   │   ├── BuildersPouchTooltipData.java   # Custom tooltip data carrier for builder's pouch item grids
│       │   │   │   │   └── ShulkerBoxTooltipData.java      # Custom tooltip data carrier showing compact shulker box previews
│       │   │   │   └── workbench/                          # Block color sampling catalog for workbench displays
│       │   │   │       └── ClientBlockColorCatalog.java    # Client-side catalog caching average RGB colors for all blocks
│       │   │   │
│       │   │   ├── command/                                # In-game admin and debug commands
│       │   │   │   ├── BuildscapeCommands.java             # Registers in-game admin and debug commands under /buildscape
│       │   │   │   └── FireworkTestCommand.java            # Admin command spawning parametric custom firework shapes for testing
│       │   │   │
│       │   │   ├── config/                                 # Runtime settings and disk persistence configurations
│       │   │   │   ├── BuildScapeConfig.java               # Common gameplay and server balance configuration settings
│       │   │   │   ├── BuildscapeClientConfig.java         # Client graphics, animations, and particle render settings
│       │   │   │   ├── CosmeticsConfig.java                # Configuration toggling visual supporter cosmetic render features
│       │   │   │   ├── GuiConfigData.java                  # Serializable POJO holding client UI widget coordinates and options
│       │   │   │   ├── GuiConfigManager.java               # Disk persistence coordinator saving and loading GUI configuration files
│       │   │   │   ├── PillarIdManager.java                # Central registry managing assigned pillar IDs and custom names
│       │   │   │   ├── PillarParticleConfig.java           # Configures particle rate on decorative pillars
│       │   │   │   ├── PillarResetHandler.java             # Reloads pillar config states upon world join
│       │   │   │   └── PresetsConfig.java                  # Manages built-in and user-defined decoration and particle presets
│       │   │   │
│       │   │   ├── cosmetic/                               # Sign frame attachments and interactions
│       │   │   │   └── sign/                               # Sign frame style models and attachments
│       │   │   │       ├── SignFrameAttachment.java        # Data model representing a decorative frame attached to a wall sign
│       │   │   │       ├── SignFrameInteractionHandler.java # Handles right-click application and removal of sign frames
│       │   │   │       └── SignFrameType.java              # Enum defining decorative sign frame border styles (wood, metal, ornate)
│       │   │   │
│       │   │   ├── cosmetics/                              # Wearable supporter cosmetic armor models and registration
│       │   │   │   ├── CosArmor.java                       # Base abstract model for cosmetic supporter armor pieces
│       │   │   │   ├── CosChest.java                       # Cosmetic chestplate wearable model with custom particle emitters
│       │   │   │   ├── CosFeet.java                        # Cosmetic boot wearable model leaving sparkling footprints
│       │   │   │   ├── CosHead.java                        # Cosmetic hat and crown wearable model with floating halos
│       │   │   │   ├── CosLegs.java                        # Cosmetic leggings wearable model with trailing particle auras
│       │   │   │   ├── CosmeticManager.java                # Coordinates client-side cosmetic equip states and supporter unlocks
│       │   │   │   └── CosmeticRegistry.java               # Registry mapping cosmetic identifiers to 3D models and textures
│       │   │   │
│       │   │   ├── entity/                                 # Custom entity definitions, trading tables, and entities
│       │   │   │   ├── ColoredItemFrameEntity.java         # Entity representing a hanging 16-color dyed item frame
│       │   │   │   ├── EntityDefinition.java               # Metadata schema for Buildscape custom entities
│       │   │   │   ├── FallingIcicleEntity.java            # Hazard entity falling from ceilings and shattering on impact
│       │   │   │   ├── FestiveStockingEntity.java          # Interactive hanging entity storing items and holiday gifts
│       │   │   │   ├── FestiveWanderingHomemakerEntity.java # Holiday variant of the Wandering Homemaker NPC
│       │   │   │   ├── MangroveBoatEntity.java             # Custom boat entity constructed from mangrove wood (for 1.18.2)
│       │   │   │   ├── ModEntities.java                    # Central registry of all custom entity types
│       │   │   │   ├── PoplarBoatEntity.java               # Custom boat entity constructed from poplar wood
│       │   │   │   ├── SeatEntity.java                     # Mountable invisible entity for cushions and chairs
│       │   │   │   ├── WanderingHomemakerEntity.java       # Friendly builder trader NPC summoned via mineral ritual
│       │   │   │   └── WanderingHomemakerTrades.java       # Custom trade offers and emerald costs for the Homemaker NPC
│       │   │   │
│       │   │   ├── event/                                  # Gameplay mechanics, interactions, and common event dispatches
│       │   │   │   ├── AdvancementEvents.java              # Server listener granting advancements for mod exploration and crafting
│       │   │   │   ├── AdvancementMilestoneLogic.java      # Computes milestone advancement unlocks
│       │   │   │   ├── ChainMobHandler.java                # Applies slip and balance physics when mobs walk across chain lines
│       │   │   │   ├── ChainMobLogic.java                  # Mobs lose balance and slip when walking on chains
│       │   │   │   ├── FestiveGlintAnvilHandler.java       # Handles anvil craft recipes applying festive glint shards
│       │   │   │   ├── FestiveGlintAnvilLogic.java         # Merging festive glint shards onto items in anvils
│       │   │   │   ├── FrostRoseDropHandler.java           # Silk-touch vs direct harvest drops for frost roses
│       │   │   │   ├── HollowLogCrawlHandler.java          # Forces crawling animation when entering hollow logs
│       │   │   │   ├── ItemFrameParticleHandler.java       # Spawns subtle color dust particles around colored item frames
│       │   │   │   ├── LogStrippingLogic.java              # Axe right-click logic for stripping logs & slabs
│       │   │   │   ├── ModClientParticleEvents.java        # Client event hooks spawning ambient world and weather particles
│       │   │   │   ├── ModCommonEvents.java                # Central dispatcher called by loader event listeners
│       │   │   │   ├── MudToClayHandler.java               # Pointed dripstone drying mud blocks into clay
│       │   │   │   ├── StockingCraftingHandler.java        # Dynamic crafting recipe handler for dyeing festive stockings
│       │   │   │   ├── StrawBedHandler.java                # Rest mechanic allowing daytime sleep without spawn set
│       │   │   │   ├── TagTooltipHandler.java              # Appends useful architectural and tag metadata to item tooltips
│       │   │   │   ├── WanderingHomemakerSpawningHandler.java # Detects player placement completing 2x2 mineral summoning altars
│       │   │   │   └── WanderingHomemakerSpawningLogic.java # Gameplay event handler
│       │   │   │
│       │   │   ├── firework/                               # Parametric 3D firework shape algorithms and render matrices
│       │   │   │   ├── CustomFireworkRenderer.java         # Renders complex 3D firework point matrices
│       │   │   │   ├── CustomFireworkShape.java            # Base interface for parametric firework geometries
│       │   │   │   ├── CustomFireworkShapeRegistry.java    # Registry lookup for custom firework shapes
│       │   │   │   ├── FireworkPoint.java                  # 3D vector and color record for a particle spark
│       │   │   │   └── shapes/                             # Specific geometric point burst generators
│       │   │   │       ├── CakeFireworkShape.java          # Computes point coordinates for cake-shaped bursts
│       │   │   │       ├── CandyCaneFireworkShape.java     # Computes point coordinates for candy cane bursts
│       │   │   │       ├── ChristmasTreeFireworkShape.java # Computes point coordinates for tree-shaped bursts
│       │   │   │       ├── CrownFireworkShape.java         # Computes point coordinates for crown-shaped bursts
│       │   │   │       ├── PhoenixFireworkShape.java       # Computes point coordinates for phoenix bird bursts
│       │   │   │       ├── PresentsFireworkShape.java      # Computes point coordinates for gift box bursts
│       │   │   │       ├── SnowflakeFireworkShape.java     # Computes point coordinates for snowflake bursts
│       │   │   │       └── TrophyFireworkShape.java        # Computes point coordinates for trophy-shaped bursts
│       │   │   │
│       │   │   ├── fluid/                                  # Custom fluid definitions and registry entries
│       │   │   │   └── ModFluids.java                      # Registry defining liquid experience and custom fluid properties
│       │   │   │
│       │   │   ├── item/                                   # Custom item definitions, properties, and creative tabs
│       │   │   │   ├── BigOrnamentTemplateItem.java        # Crafting pattern item for big ornaments
│       │   │   │   ├── BiomeBrushItem.java                 # Tool that samples biomes and paints foliage
│       │   │   │   ├── BottleOfMistItem.java               # Thrown or consumed bottle releasing cascade mist
│       │   │   │   ├── BuildersHatItem.java                # Wearable cosmetic builder's helmet item
│       │   │   │   ├── BuildersPouchInventory.java         # Data storage for the multi-slot builder's pouch
│       │   │   │   ├── BuildersPouchItem.java              # Portable auto-replenishing inventory pouch item
│       │   │   │   ├── ColoredItemFrameItem.java           # Item placing customizable colored item frames
│       │   │   │   ├── CommonItemProperties.java           # Cross-version item property and stack-size builder
│       │   │   │   ├── ConfettiItem.java                   # Thrown item bursting into colorful particles
│       │   │   │   ├── CopperChestItem.java                # Item representation for placeable copper chests
│       │   │   │   ├── ExperienceBucketItem.java           # Bucket holding liquid experience
│       │   │   │   ├── FestiveGlintShardItem.java          # Crafting shard imparting custom enchantment glint
│       │   │   │   ├── FestiveStarItem.java                # Item for placing glowing holiday stars
│       │   │   │   ├── FestiveStockingItem.java            # Item for placing holiday stockings
│       │   │   │   ├── GlassJarItem.java                   # Item for placing display glass jars
│       │   │   │   ├── GoldenJarItem.java                  # Item for placing ornate golden jars
│       │   │   │   ├── HammerItem.java                     # 3x3 area-of-effect mining hammer tool
│       │   │   │   ├── InfinitePhoenixFireworkStarItem.java # Custom item definition
│       │   │   │   ├── ItemDefinition.java                 # Metadata definition schema for all 3,148 items
│       │   │   │   ├── ItemFactory.java                    # Constructs native Item instances across eras
│       │   │   │   ├── MangroveBoatItem.java               # Boat item carved from mangrove wood (for 1.18.2)
│       │   │   │   ├── MistBlockItem.java                  # Block item for placing cascade mist emitters
│       │   │   │   ├── ModCreativeTabs.java                # Registers Buildscape creative mode inventory tabs
│       │   │   │   ├── ModItems.java                       # Central registry definitions for all 3,148 items
│       │   │   │   ├── MuffBlockItem.java                  # Block item for placing muff blocks
│       │   │   │   ├── PatternItem.java                    # Design pattern item for customized crafting
│       │   │   │   ├── StringlightFrameItem.java           # Crafting component for building string lights
│       │   │   │   └── WrenchItem.java                     # Tool that rotates block orientations and states
│       │   │   │
│       │   │   ├── menu/                                   # Container menu definitions and inventory bridges
│       │   │   │   ├── BuildersPouchMenu.java              # Container menu synchronizing the builder's pouch inventory slots
│       │   │   │   ├── BuildersWorkbenchMenu.java          # Server/client container menu for workbench GUI
│       │   │   │   ├── MenuTypeBridge.java                 # Bridges MenuType constructors across loader boundaries
│       │   │   │   └── ModMenuTypes.java                   # Registry holding container menu types
│       │   │   │
│       │   │   ├── mixin/                                  # Core bytecode transformations augmenting Minecraft internals
│       │   │   │   ├── AbstractContainerMenuMixin.java     # Injects ghost-filtering and slot interaction hooks into container menus
│       │   │   │   ├── AbstractContainerScreenMixin.java   # Injects custom tooltip rendering and ghost-item dragging into container screens
│       │   │   │   ├── AdvancementWidgetMixin.java         # Enhances advancement tree rendering for milestone achievements
│       │   │   │   ├── AgeableMobMixin.java                # Hooks age progression for miniature display mobs on decorative pillars
│       │   │   │   ├── AnvilMenuMixin.java                 # Enables anvil combining of festive glint shards and custom tools
│       │   │   │   ├── AnvilScreenMixin.java               # Renders custom anvil cost indicators and glint preview animations
│       │   │   │   ├── BaseCoralPlantTypeBlockMixin.java   # Allows custom coral plants to survive on artificial moist substrates
│       │   │   │   ├── BeaconBlockEntityAccessor.java      # Provides accessor access to beacon beam vertical obstruction checks
│       │   │   │   ├── BeaconBlockEntityMixin.java         # Extends beacon beam transparency rules to glass jars and grates
│       │   │   │   ├── BeaconBlockEntityRendererMixin.java # Renders custom multi-colored beams through tinted glass jars
│       │   │   │   ├── BlockMixin.java                     # Injects universal hardness, mining speed, and tool effectiveness checks
│       │   │   │   ├── BlockStateBaseMixin.java            # Optimizes block state caching and connection shape lookups
│       │   │   │   ├── BuildscapeBlockModelMixin.java      # Intersects custom dynamic model baking for string light wires
│       │   │   │   ├── BuildscapeBlockStateCacheMixin.java # Accelerates collision shape queries through precomputed spatial caches
│       │   │   │   ├── BuildscapeForgeRegistryMixin.java   # Bridges deferred registry events during Forge initialization
│       │   │   │   ├── BuildscapeMixinPlugin.java          # Controls conditional mixin application based on detected Minecraft version
│       │   │   │   ├── BuildscapeModelBakeryMixin.java     # Injects extra unbaked 3D models into the client model bakery
│       │   │   │   ├── ChainBlockMixin.java                # Enables vertical chain climbing mechanics across vanilla chain blocks
│       │   │   │   ├── ComposterBlockMixin.java            # Registers custom foliage, leaves, and moss into the composter recipe table
│       │   │   │   ├── CoralBlockMixin.java                # Allows coral blocks to remain hydrated when placed adjacent to cascades
│       │   │   │   ├── CreativeModeTabMixin.java           # Customizes creative inventory tab layout and item placement orders
│       │   │   │   ├── ElytraLayerMixin.java               # Hooks elytra rendering for armor stands mounted atop decorative pillars
│       │   │   │   ├── EmbeddiumPipeSpillMixin.java        # Compatibility mixin preventing visual glitching with Embeddium/Sodium
│       │   │   │   ├── EntityAccessor.java                 # Provides accessor access to private entity flags and dimensions
│       │   │   │   ├── EntityMixin.java                    # Hooks entity movement and collision logic for cushion blocks and chains
│       │   │   │   ├── FeatureMixin.java                   # Injects custom placement rules during worldgen biome feature generation
│       │   │   │   ├── FenceBlockMixin.java                # Allows smooth architectural connections between fences and leaf hedges
│       │   │   │   ├── FireworkRocketEntityMixin.java      # Renders custom 3D parametric burst geometry upon firework detonation
│       │   │   │   ├── FireworkStarItemMixin.java          # Enables crafting and color blending for custom parametric firework stars
│       │   │   │   ├── FireworkStarterMixin.java           # Hooks firework launch triggers from dispensers and player hands
│       │   │   │   ├── FlowingFluidMixin.java              # Enables fluid flow and pressure transmission through hollow pipe networks
│       │   │   │   ├── GeneralStatisticsListEntryMixin.java # Adds custom Buildscape player stats into the vanilla statistics screen
│       │   │   │   ├── GeneralStatisticsListMixin.java     # Formats stat counts and icons for custom mod achievements
│       │   │   │   ├── HumanoidArmorLayerMixin.java        # Enables custom cosmetic wearable rendering on player armor layers
│       │   │   │   ├── IMixinFactory.java                  # Service interface providing version-specific mixin implementations
│       │   │   │   ├── IronBarsBlockMixin.java             # Enables connected architectural junctions between iron bars and weathering bars
│       │   │   │   ├── ItemCombinerMenuAccessor.java       # Provides accessor access to item combiner menu slots and results
│       │   │   │   ├── ItemMixin.java                      # Injects custom item use and right-click interaction routing
│       │   │   │   ├── ItemRendererMixin.java              # Injects custom 3D weapon scaling and shelf orientation rendering
│       │   │   │   ├── LavaFluidMixin.java                 # Handles interaction between liquid sulfur, water, and flowing lava
│       │   │   │   ├── LeavesBlockMixin.java               # Adds leaf litter particle drops and waterlogging support to leaves
│       │   │   │   ├── LevelMixin.java                     # Hooks block change notifications for cascade fluid propagation
│       │   │   │   ├── LiquidBlockMixin.java               # Enables custom liquid interaction logic with hollow log caps
│       │   │   │   ├── LiquidBlockRendererMixin.java       # Renders custom fluid textures and water levels inside hollow logs
│       │   │   │   ├── LivingEntityMixin.java              # Handles fall damage dampening on cushion blocks and frost damage
│       │   │   │   ├── MinecraftServerMixin.java           # Coordinates server reload events and dynamic recipe recompilation
│       │   │   │   ├── MixinFactory.java                   # Common dispatcher delegating to VersionCluster-specific MixinFactory implementations
│       │   │   │   ├── PauseScreenMixin.java               # Adds quick mod settings button into the in-game escape pause menu
│       │   │   │   ├── RenderBuffersMixin.java             # Allocates custom translucent render buffers for special visual effects
│       │   │   │   ├── ScreenMixin.java                    # Hooks screen mouse clicks and key inputs for custom GUI extensions
│       │   │   │   ├── ShulkerBoxBlockEntityMixin.java     # Adds ghost filtering and auto-refill capabilities to shulker boxes
│       │   │   │   ├── ShulkerBoxBlockMixin.java           # Handles custom drops and NBT preservation for upgraded shulkers
│       │   │   │   ├── ShulkerBoxMenuMixin.java            # Synchronizes ghost filter item states inside shulker box container menus
│       │   │   │   ├── SignRendererMixin.java              # Renders custom decorative sign frames around placed wall and standing signs
│       │   │   │   ├── SmithingMenuMixin.java              # Enables smithing table recipes for upgrading tools with steel fasteners
│       │   │   │   ├── StonecutterMenuAccessor.java        # Provides accessor access to internal stonecutter menu recipe lists
│       │   │   │   ├── StonecutterMenuMixin.java           # Expands stonecutter menu to support crafting all mod slabs and stairs
│       │   │   │   ├── ThrownTridentRendererMixin.java     # Renders custom festive enchantment glints on flying tridents
│       │   │   │   ├── VineBlockMixin.java                 # Enables climbing mechanics and growth rules for decorative rose vines
│       │   │   │   ├── WallBlockMixin.java                 # Enables seamless connection geometry between walls and leaf hedges
│       │   │   │   └── support/                            # Data carrier structures for mixin injection state
│       │   │   │       ├── FluidConduitSupply.java         # Helper structure tracking fluid volume and head pressure across pipes
│       │   │   │       └── ShulkerGhostFilterCapture.java  # Captures and serializes ghost item filter settings in shulker slots
│       │   │   │
│       │   │   ├── network/                                # Cross-loader networking channel, packet serializers, and dispatchers
│       │   │   │   ├── ActionBarMessagePacket.java         # Server-to-client packet displaying colored action bar notifications
│       │   │   │   ├── BuildersWorkbenchResultsPacket.java # Server-to-client packet synchronizing valid craftable recipes
│       │   │   │   ├── ClearBiomeBrushPacket.java          # Client-to-server packet resetting active biome brush sample caches
│       │   │   │   ├── CommonPacket.java                   # Common interface defining packet byte serialization and execution
│       │   │   │   ├── ConfettiBurstPacket.java            # Server-to-client packet broadcasting confetti explosion coordinates
│       │   │   │   ├── HammerReplacePacket.java            # Client-to-server packet executing 3x3 block replacement actions
│       │   │   │   ├── IPacketFactory.java                 # Service interface providing version-specific networking implementations
│       │   │   │   ├── ModPackets.java                     # Central registry declaring packet channels, IDs, and encoders/decoders
│       │   │   │   ├── NetworkPacketLimits.java            # Security validation utility verifying maximum buffer sizes and bounds
│       │   │   │   ├── PacketDirection.java                # Enum defining packet routing (Client-to-Server or Server-to-Client)
│       │   │   │   ├── PacketFactory.java                  # Common dispatcher delegating to VersionCluster-specific PacketFactory implementations
│       │   │   │   ├── RemovePillarPacket.java             # Client-to-server packet requesting removal of a pillar configuration ID
│       │   │   │   ├── RequestPillarIdsPacket.java         # Client-to-server packet querying registered pillar IDs upon join
│       │   │   │   ├── RotateBlockPacket.java              # Client-to-server packet requesting block rotation via wrench tool
│       │   │   │   ├── SyncConfigPacket.java               # Server-to-client packet synchronizing server gameplay settings to clients
│       │   │   │   ├── SyncGameRulesPacket.java            # Server-to-client packet synchronizing custom game rule values
│       │   │   │   ├── SyncHomemakerCooldownPacket.java    # Server-to-client packet syncing Homemaker summoning ritual cooldowns
│       │   │   │   ├── SyncPillarIdsPacket.java            # Server-to-client packet broadcasting all active pillar IDs to clients
│       │   │   │   ├── SyncSignFramePacket.java            # Server-to-client packet synchronizing sign frame attachment styles
│       │   │   │   ├── TreeChopJobManager.java             # Server task queue managing asynchronous progressive tree felling
│       │   │   │   ├── TreeChopPacket.java                 # Client-to-server packet initiating whole-tree felling jobs
│       │   │   │   ├── UpdateAllPillarIdsPacket.java       # Server-to-client packet performing bulk update of pillar identifier tables
│       │   │   │   ├── UpdateConfigPacket.java             # Client-to-server packet updating mod configuration settings from GUI
│       │   │   │   ├── UpdateGameRulePacket.java           # Client-to-server packet toggling mod gamerules from admin screens
│       │   │   │   └── UpdatePillarDataPacket.java         # Client-to-server packet saving particle settings for a specific pillar ID
│       │   │   │
│       │   │   ├── particle/                               # Custom particle types, physics calculators, and color trackers
│       │   │   │   ├── FireflyTracker.java                 # Client simulator computing erratic hovering flight paths for fireflies
│       │   │   │   ├── ModParticles.java                   # Particle type registry definitions
│       │   │   │   ├── ParticleFactory.java                # Common dispatcher delegating to VersionCluster-specific ParticleFactory implementations
│       │   │   │   ├── ParticleProvider.java               # Interface bridging particle factory creation across loader versions
│       │   │   │   ├── PillarSparkleDataQueue.java         # Queue syncing particle bursts along tall pillars
│       │   │   │   ├── PillarSparkleParticle.java          # Custom sparkling particle class
│       │   │   │   ├── PillarSparkleParticleLogic.java     # Particle velocity and oscillation calculations
│       │   │   │   ├── SmokeColorRegistry.java             # Maps 16 dye colors to RGB particle vectors
│       │   │   │   └── TintedParticleColorTracker.java     # Caches dynamic color blending on active particles
│       │   │   │
│       │   │   ├── pipe/                                   # Architectural fluid, item, and bubble column transit networks
│       │   │   │   └── transport/                          # Network routing graph, pressure physics, and node connections
│       │   │   │       ├── BubbleColumnHandler.java        # Upward and downward bubble physics inside pipes
│       │   │   │       ├── BubbleColumnState.java          # State enum for pipe bubble direction
│       │   │   │       ├── HollowPipeTransportManager.java # Primary routing engine for hollow pipe networks
│       │   │   │       ├── PipeConnectionLogic.java        # Calculates valid adjoining pipe faces
│       │   │   │       ├── PipeFlowState.java              # Flow rate and direction through a pipe node
│       │   │   │       ├── PipeFluidTransport.java         # Fluid transfer rate, capacity, and pressure
│       │   │   │       ├── PipeItemTransit.java            # Item packet traversal and sorting inside pipes
│       │   │   │       ├── PipeOutletWater.java            # Fluid discharge mechanics at open pipe ends
│       │   │   │       ├── PipeTopologyAccess.java         # Interface querying pipe graph connectivity
│       │   │   │       ├── WaterPipeTransport.java         # Water-specific transport and pumping rules
│       │   │   │       └── WorldPipeTopologyAccess.java    # World-based block lookup for pipe graphs
│       │   │   │
│       │   │   ├── platform/                               # Cross-loader Service Provider Interface (SPI) abstractions
│       │   │   │   ├── BlockEntityFactory.java             # Functional factory constructing block entities
│       │   │   │   ├── IPlatformAdapter.java               # Cross-loader abstraction interface for platform services (NBT, attributes, renderers, audio, tabs)
│       │   │   │   └── Services.java                       # ServiceLoader lookup for IPlatformAdapter & IRegistryAdapter
│       │   │   │
│       │   │   ├── recipe/                                 # Dynamic recipe compiling, caching, and custom crafting mechanics
│       │   │   │   ├── CustomFireworkRecipeLogic.java      # Crafting logic for assembling complex 3D shape firework stars
│       │   │   │   ├── DurabilityRecipeLogic.java          # Crafting logic allowing tools to take damage rather than being consumed
│       │   │   │   └── framework/                          # High-performance recipe loader and streaming compiler
│       │   │   │       ├── BuildScapeRecipeLoader.java     # Loads and compiles external dynamic recipes during datapack reloads
│       │   │   │       ├── cache/                          # Binary disk serialization cache for compiled recipes
│       │   │   │       │   └── BinaryRecipeCache.java      # Fast disk cache storing pre-compiled binary recipe representations
│       │   │   │       ├── compiler/                       # Template expansion and recipe AST compilation engine
│       │   │   │       │   ├── AliasResolver.java          # Resolves ingredient tag and item alias shortcuts in recipe JSONs
│       │   │   │       │   ├── BuildScapeRecipeCompiler.java # Compiles raw recipe definitions into optimized runtime recipe objects
│       │   │   │       │   ├── FamilyExpander.java         # Expands stone/wood families into full sets of slabs, stairs, and walls
│       │   │   │       │   └── TemplateEngine.java         # Template processor generating boilerplate recipe variations
│       │   │   │       ├── integration/                    # RecipeManager hooks and runtime injection
│       │   │   │       │   └── RecipeManagerInjector.java  # Injects dynamically compiled recipes into the active RecipeManager
│       │   │   │       ├── parser/                         # Streaming low-allocation JSON recipe parser and IR schema
│       │   │   │       │   ├── RecipeIR.java               # Intermediate representation schema for compiled recipe data
│       │   │   │       │   └── StreamingRecipeParser.java  # Streaming JSON reader minimizing memory allocation during recipe parsing
│       │   │   │       ├── util/                           # Pattern trimming, ingredient caching, and bounding bounds
│       │   │   │       │   ├── IngredientCache.java        # Caches resolved ingredients and tags to accelerate recipe matching
│       │   │   │       │   ├── PatternBounds.java          # Computes tight 2D bounding boxes for shaped crafting recipe patterns
│       │   │   │       │   └── ShapedPatternTrimmer.java   # Trims whitespace from shaped recipe keys to normalize pattern grids
│       │   │   │       └── validation/                     # Recipe integrity checks and ingredient presence verification
│       │   │   │           └── RecipeValidator.java        # Validates recipe syntax, ingredient presence, and output item IDs
│       │   │   │
│       │   │   ├── registry/                               # Multi-loader deferred registration abstractions and wrappers
│       │   │   │   ├── BaseRegistryAdapter.java            # Base class implementing common registration helpers, deferred registrations, and supplier binding
│       │   │   │   ├── IRegistryAdapter.java               # SPI interface for registering blocks, items, entities
│       │   │   │   ├── RegistrySupplier.java               # Lazy reference wrapper for registered game objects
│       │   │   │   └── SimpleRegistrySupplier.java         # Immediate reference implementation of RegistrySupplier
│       │   │   │
│       │   │   ├── sound/                                  # Custom sound event registrations
│       │   │   │   └── ModSounds.java                      # Central registry of all custom sound events
│       │   │   │
│       │   │   ├── stat/                                   # Custom gameplay statistics and tracking
│       │   │   │   └── ModStats.java                       # Central registry of custom player statistics
│       │   │   │
│       │   │   ├── trophy/                                 # Trophy pedestals, tiers, and achievement milestones
│       │   │   │   ├── Trophies.java                       # Master list of achievement and challenge trophies
│       │   │   │   ├── TrophyBlock.java                    # Base trophy pedestal block
│       │   │   │   ├── TrophyBlockEntity.java              # Stores trophy tier, variant, and owner UUID
│       │   │   │   ├── TrophyBlockItem.java                # Item form carrying trophy NBT data
│       │   │   │   ├── TrophyDefinition.java               # Metadata schema defining trophy requirements
│       │   │   │   └── TrophyTier.java                     # Enum for trophy tiers (Bronze, Silver, Gold, etc.)
│       │   │   │
│       │   │   ├── util/                                   # Math, gradient solvers, tree felling, and identifier wrappers
│       │   │   │   ├── BeaconBeamHeightAccessor.java       # Accesses beacon beam height calculations
│       │   │   │   ├── BeaconBeamScanState.java            # State holder for scanning vertical beacon obstructions
│       │   │   │   ├── BeaconScanContext.java              # Context passed through beacon evaluation algorithms
│       │   │   │   ├── BlockRotationLogic.java             # Math for rotating horizontal and axial block states
│       │   │   │   ├── ColorGradientSolver.java            # Math for interpolating smooth RGB hex gradients
│       │   │   │   ├── CommonId.java                       # Cross-version wrapper over ResourceLocation / Identifier
│       │   │   │   ├── ComponentHelper.java                # Cross-version factory creating text components
│       │   │   │   ├── FestiveGlintHelper.java             # Encodes and parses custom festive enchantment glint
│       │   │   │   ├── GhostFilterMenu.java                # Menu logic for setting filter items without consuming them
│       │   │   │   ├── GhostFilterable.java                # Interface for UI slots supporting ghost filter items
│       │   │   │   ├── GoldenDandelionGrowth.java          # Spread and mutation algorithm for golden dandelions
│       │   │   │   ├── StonecutterMenuExtension.java       # Extends stonecutter recipes to support custom slabs/stairs
│       │   │   │   └── TreeChopTraversal.java              # Depth-first tree felling traversal algorithm
│       │   │   │
│       │   │   ├── world/                                  # Custom game rules and world simulation settings
│       │   │   │   └── ModGameRules.java                   # Registers custom game rules controlling tree falling, cascades, and decay
│       │   │   │
│       │   │   └── worldgen/                               # Trunk placers, foliage placers, and configured tree features
│       │   │       ├── MangroveLeafSupport.java            # Generates supporting hanging foliage under mangrove tree canopies
│       │   │       ├── MangrovePlacerLogic.java            # Computes branching trunk and root geometry for mangrove worldgen
│       │   │       ├── MangrovePropaguleLogic.java         # Handles natural hanging propagule growth and fruit maturity
│       │   │       ├── MangroveRootLogic.java              # Generates stilt root structures anchoring mangrove trees in mud
│       │   │       ├── ModConfiguredFeatures.java          # Central registry defining configured tree, plant, and ore features
│       │   │       └── WorldGenTypeHelper.java             # Helper registering custom trunk and foliage placers across eras
│       │
│       └── resources/                                      # Mod assets, datapacks, and Mixin configuration
│           ├── assets/                                     # Client visual textures, models, blockstates, and sounds
│           │   ├── buildscape/                             # Buildscape namespace textures, models, and sounds
│           │   │   ├── blockstates/                        # Blockstate JSONs mapping states to model variants
│           │   │   ├── lang/                               # Localization files (en_us.json)
│           │   │   ├── models/                             # 3D block and item model JSONs (including neoforge_data emissive models)
│           │   │   ├── particles/                          # Particle texture definitions
│           │   │   ├── sounds/                             # Sound event metadata definitions (sounds.json)
│           │   │   └── textures/                           # PNG textures for blocks, items, and entities
│           │   └── minecraft/                              # Vanilla override models, atlases, and blockstates
│           │       ├── atlases/                            # Texture atlas definitions (configured sprite sources for blocks and lights)
│           │       ├── blockstates/                        # Vanilla blockstate additions
│           │       ├── lang/                               # Vanilla string augmentations
│           │       ├── models/                             # Vanilla model augmentations
│           │       └── textures/                           # Vanilla texture extensions
│           ├── data/                                       # Datapack recipes, tags, loot tables, and configured features
│           │   ├── buildscape/                             # Data pack recipes, tags, and loot tables
│           │   │   ├── advancements/                       # Mod achievement criteria and rewards
│           │   │   ├── loot_tables/                        # Block drop definitions
│           │   │   ├── recipes/                            # Crafting, smelting, and stonecutting recipes
│           │   │   ├── recipes_pack/                       # Recipe bundles and alternate craft rules
│           │   │   ├── tags/                               # Block and item tag collections
│           │   │   └── worldgen/                           # World generation feature placements
│           │   ├── forge/                                  # Forge-specific tags and loot modifiers
│           │   │   ├── loot_modifiers/                     # Global loot modifiers
│           │   │   └── tags/                               # Forge convention tags
│           │   └── minecraft/                              # Vanilla tag overrides (mineable/pickaxe, slabs, stairs)
│           │       ├── loot_tables/                        # Vanilla block drop modifications
│           │       ├── recipes/                            # Vanilla recipe overrides
│           │       └── tags/                               # Conventional tags (slabs, stairs, logs)
│           ├── META-INF/                                   # ServiceLoader provider descriptors and mod manifests
│           │   └── services/                               # ServiceLoader SPI provider interfaces
│           └── buildscape.mixins.json                      # Central Mixin specification registering all bytecode transformations
│
├── fabric/                                                 # Fabric Loader Subprojects
│   ├── 1.18.2/                                             # Minecraft 1.18.2 on Fabric (Java 17)
│   │   ├── build.gradle                                    # Gradle build script for Fabric 1.18.2
│   │   └── src/main/                                       # Fabric 1.18.2 source and resource root
│   │   │   ├── java/com/kingodogo/buildscape/              # Fabric 1.18.2 Java source package
│   │   │   │   ├── BuildscapeFabric.java                   # Fabric ModInitializer common mod entrypoint
│   │   │   │   ├── BuildscapeFabricClient.java             # Fabric ClientModInitializer client mod entrypoint
│   │   │   │   ├── platform/FabricPlatformAdapter.java     # Fabric implementation of IPlatformAdapter
│   │   │   │   └── registry/FabricRegistryAdapter.java     # Fabric implementation of IRegistryAdapter
│   │   │   └── resources/                                  # Fabric 1.18.2 mod metadata and SPI bindings
│   │   │       ├── fabric.mod.json                         # Fabric mod metadata manifest
│   │   │       └── META-INF/services/                      # ServiceLoader SPI bindings:
│   │   │           ├── com.kingodogo.buildscape.block.IBlockFactory # Binds VersionCluster BlockFactory
│   │   │           ├── com.kingodogo.buildscape.mixin.IMixinFactory # Binds VersionCluster MixinFactory
│   │   │           ├── com.kingodogo.buildscape.network.IPacketFactory # Binds VersionCluster PacketFactory
│   │   │           ├── com.kingodogo.buildscape.platform.IPlatformAdapter # Binds FabricPlatformAdapter
│   │   │           └── com.kingodogo.buildscape.registry.IRegistryAdapter # Binds FabricRegistryAdapter
│   │
│   ├── 1.21.1/                                             # Minecraft 1.21.1 on Fabric (Java 21)
│   │   ├── build.gradle                                    # Gradle build script for Fabric 1.21.1
│   │   └── src/main/                                       # Fabric 1.21.1 source and resource root
│   │   │   ├── java/com/kingodogo/buildscape/              # Fabric 1.21.1 Java source package
│   │   │   │   ├── BuildscapeFabric.java                   # Fabric ModInitializer common mod entrypoint
│   │   │   │   ├── BuildscapeFabricClient.java             # Fabric ClientModInitializer client mod entrypoint
│   │   │   │   ├── platform/FabricPlatformAdapter.java     # Fabric implementation of IPlatformAdapter
│   │   │   │   └── registry/FabricRegistryAdapter.java     # Fabric implementation of IRegistryAdapter
│   │   │   └── resources/                                  # Fabric 1.21.1 mod metadata and SPI bindings
│   │   │       ├── fabric.mod.json                         # Fabric mod metadata manifest
│   │   │       └── META-INF/services/                      # ServiceLoader SPI bindings:
│   │   │           ├── com.kingodogo.buildscape.block.IBlockFactory # Binds VersionCluster BlockFactory
│   │   │           ├── com.kingodogo.buildscape.mixin.IMixinFactory # Binds VersionCluster MixinFactory
│   │   │           ├── com.kingodogo.buildscape.network.IPacketFactory # Binds VersionCluster PacketFactory
│   │   │           ├── com.kingodogo.buildscape.platform.IPlatformAdapter # Binds FabricPlatformAdapter
│   │   │           └── com.kingodogo.buildscape.registry.IRegistryAdapter # Binds FabricRegistryAdapter
│   │
│   ├── 26.2/                                               # Minecraft 26.2 on Fabric (Java 25)
│   │   ├── build.gradle                                    # Gradle build script for Fabric 26.2
│   │   └── src/main/                                       # Fabric 26.2 source and resource root
│   │   │   ├── java/com/kingodogo/buildscape/              # Fabric 26.2 Java source package
│   │   │   │   ├── BuildscapeFabric.java                   # Fabric ModInitializer common mod entrypoint
│   │   │   │   ├── BuildscapeFabricClient.java             # Fabric ClientModInitializer client mod entrypoint
│   │   │   │   ├── platform/FabricPlatformAdapter.java     # Fabric implementation of IPlatformAdapter
│   │   │   │   └── registry/FabricRegistryAdapter.java     # Fabric implementation of IRegistryAdapter
│   │   │   └── resources/                                  # Fabric 26.2 mod metadata and SPI bindings
│   │   │       ├── fabric.mod.json                         # Fabric mod metadata manifest
│   │   │       └── META-INF/services/                      # ServiceLoader SPI bindings:
│   │   │           ├── com.kingodogo.buildscape.block.IBlockFactory # Binds VersionCluster BlockFactory
│   │   │           ├── com.kingodogo.buildscape.mixin.IMixinFactory # Binds VersionCluster MixinFactory
│   │   │           ├── com.kingodogo.buildscape.network.IPacketFactory # Binds VersionCluster PacketFactory
│   │   │           ├── com.kingodogo.buildscape.platform.IPlatformAdapter # Binds FabricPlatformAdapter
│   │   │           └── com.kingodogo.buildscape.registry.IRegistryAdapter # Binds FabricRegistryAdapter
│   │
│   └── 26.3/                                               # Minecraft 26.3 on Fabric (Java 25)
│       ├── build.gradle                                    # Gradle build script for Fabric 26.3
│       └── src/main/                                       # Fabric 26.3 source and resource root
│       │   ├── java/com/kingodogo/buildscape/              # Fabric 26.3 Java source package
│       │   │   ├── BuildscapeFabric.java                   # Fabric ModInitializer common mod entrypoint
│       │   │   ├── BuildscapeFabricClient.java             # Fabric ClientModInitializer client mod entrypoint
│       │   │   ├── platform/FabricPlatformAdapter.java     # Fabric implementation of IPlatformAdapter
│       │   │   └── registry/FabricRegistryAdapter.java     # Fabric implementation of IRegistryAdapter
│       │   └── resources/                                  # Fabric 26.3 mod metadata and SPI bindings
│       │       ├── fabric.mod.json                         # Fabric mod metadata manifest
│       │       └── META-INF/services/                      # ServiceLoader SPI bindings:
│       │           ├── com.kingodogo.buildscape.block.IBlockFactory # Binds VersionCluster BlockFactory
│       │           ├── com.kingodogo.buildscape.mixin.IMixinFactory # Binds VersionCluster MixinFactory
│       │           ├── com.kingodogo.buildscape.network.IPacketFactory # Binds VersionCluster PacketFactory
│       │           ├── com.kingodogo.buildscape.platform.IPlatformAdapter # Binds FabricPlatformAdapter
│       │           └── com.kingodogo.buildscape.registry.IRegistryAdapter # Binds FabricRegistryAdapter
│
├── forge/                                                  # Forge Loader Subprojects
│   ├── 1.18.2/                                             # Minecraft 1.18.2 on Forge (Java 17)
│   │   ├── build.gradle                                    # Gradle build script for Forge 1.18.2 (with neoforge_data to forge_data jar packaging transform)
│   │   └── src/main/                                       # Forge 1.18.2 source and resource root
│   │   │   ├── java/com/kingodogo/buildscape/              # Forge 1.18.2 Java source package
│   │   │   │   ├── BuildscapeForge.java                    # Forge @Mod entrypoint & FML event bus listeners
│   │   │   │   ├── platform/ForgePlatformAdapter.java      # Forge implementation of IPlatformAdapter
│   │   │   │   └── registry/ForgeRegistryAdapter.java      # Forge implementation of IRegistryAdapter
│   │   │   └── resources/META-INF/                         # Forge 1.18.2 mod metadata and SPI bindings
│   │   │       ├── mods.toml                               # Forge mod metadata descriptor
│   │   │       └── services/                               # ServiceLoader SPI bindings:
│   │   │           ├── com.kingodogo.buildscape.block.IBlockFactory # Binds VersionCluster BlockFactory
│   │   │           ├── com.kingodogo.buildscape.mixin.IMixinFactory # Binds VersionCluster MixinFactory
│   │   │           ├── com.kingodogo.buildscape.network.IPacketFactory # Binds VersionCluster PacketFactory
│   │   │           ├── com.kingodogo.buildscape.platform.IPlatformAdapter # Binds ForgePlatformAdapter
│   │   │           └── com.kingodogo.buildscape.registry.IRegistryAdapter # Binds ForgeRegistryAdapter
│   │
│   ├── 1.21.1/                                             # Minecraft 1.21.1 on Forge (Java 21)
│   │   ├── build.gradle                                    # Gradle build script for Forge 1.21.1 (with neoforge_data to forge_data jar packaging transform)
│   │   └── src/main/                                       # Forge 1.21.1 source and resource root
│   │   │   ├── java/com/kingodogo/buildscape/              # Forge 1.21.1 Java source package
│   │   │   │   ├── BuildscapeForge.java                    # Forge @Mod entrypoint & FML event bus listeners
│   │   │   │   ├── platform/ForgePlatformAdapter.java      # Forge implementation of IPlatformAdapter
│   │   │   │   └── registry/ForgeRegistryAdapter.java      # Forge DeferredRegister / RegisterEvent implementation of IRegistryAdapter
│   │   │   └── resources/META-INF/                         # Forge 1.21.1 mod metadata and SPI bindings
│   │   │       ├── mods.toml                               # Forge mod metadata descriptor
│   │   │       └── services/                               # ServiceLoader SPI bindings:
│   │   │           ├── com.kingodogo.buildscape.block.IBlockFactory # Binds VersionCluster BlockFactory
│   │   │           ├── com.kingodogo.buildscape.mixin.IMixinFactory # Binds VersionCluster MixinFactory
│   │   │           ├── com.kingodogo.buildscape.network.IPacketFactory # Binds VersionCluster PacketFactory
│   │   │           ├── com.kingodogo.buildscape.platform.IPlatformAdapter # Binds ForgePlatformAdapter
│   │   │           └── com.kingodogo.buildscape.registry.IRegistryAdapter # Binds ForgeRegistryAdapter
│   │
│   ├── 26.2/                                               # Minecraft 26.2 on Forge (Java 25)
│   │   ├── build.gradle                                    # Gradle build script for Forge 26.2
│   │   └── src/main/                                       # Forge 26.2 source and resource root
│   │   │   ├── java/com/kingodogo/buildscape/              # Forge 26.2 Java source package
│   │   │   │   ├── BuildscapeForge.java                    # Forge @Mod entrypoint & FML event bus listeners
│   │   │   │   ├── platform/ForgePlatformAdapter.java      # Forge implementation of IPlatformAdapter
│   │   │   │   └── registry/ForgeRegistryAdapter.java      # Forge implementation of IRegistryAdapter
│   │   │   └── resources/META-INF/                         # Forge 26.2 mod metadata and SPI bindings
│   │   │       ├── mods.toml                               # Forge mod metadata descriptor
│   │   │       └── services/                               # ServiceLoader SPI bindings:
│   │   │           ├── com.kingodogo.buildscape.block.IBlockFactory # Binds VersionCluster BlockFactory
│   │   │           ├── com.kingodogo.buildscape.mixin.IMixinFactory # Binds VersionCluster MixinFactory
│   │   │           ├── com.kingodogo.buildscape.network.IPacketFactory # Binds VersionCluster PacketFactory
│   │   │           ├── com.kingodogo.buildscape.platform.IPlatformAdapter # Binds ForgePlatformAdapter
│   │   │           └── com.kingodogo.buildscape.registry.IRegistryAdapter # Binds ForgeRegistryAdapter
│   │
│   └── 26.3/                                               # Minecraft 26.3 on Forge (Java 25)
│       ├── build.gradle                                    # Gradle build script for Forge 26.3
│       └── src/main/                                       # Forge 26.3 source and resource root
│       │   ├── java/com/kingodogo/buildscape/              # Forge 26.3 Java source package
│       │   │   ├── BuildscapeForge.java                    # Forge @Mod entrypoint & FML event bus listeners
│       │   │   ├── platform/ForgePlatformAdapter.java      # Forge implementation of IPlatformAdapter
│       │   │   └── registry/ForgeRegistryAdapter.java      # Forge implementation of IRegistryAdapter
│       │   └── resources/META-INF/                         # Forge 26.3 mod metadata and SPI bindings
│       │       ├── mods.toml                               # Forge mod metadata descriptor
│       │       └── services/                               # ServiceLoader SPI bindings:
│       │           ├── com.kingodogo.buildscape.block.IBlockFactory # Binds VersionCluster BlockFactory
│       │           ├── com.kingodogo.buildscape.mixin.IMixinFactory # Binds VersionCluster MixinFactory
│       │           ├── com.kingodogo.buildscape.network.IPacketFactory # Binds VersionCluster PacketFactory
│       │           ├── com.kingodogo.buildscape.platform.IPlatformAdapter # Binds ForgePlatformAdapter
│       │           └── com.kingodogo.buildscape.registry.IRegistryAdapter # Binds ForgeRegistryAdapter
│
└── neoforge/                                               # NeoForge Loader Subprojects
    ├── 1.21.1/                                             # Minecraft 1.21.1 on NeoForge (Java 21)
    │   ├── build.gradle                                    # Gradle build script for NeoForge 1.21.1
    │   └── src/main/                                       # NeoForge 1.21.1 source and resource root
    │   │   ├── java/com/kingodogo/buildscape/              # NeoForge 1.21.1 Java source package
    │   │   │   ├── BuildscapeNeoForge.java                 # NeoForge @Mod entrypoint, attribute creation & client renderer event listeners
    │   │   │   ├── platform/NeoForgePlatformAdapter.java   # NeoForge implementation of IPlatformAdapter
    │   │   │   └── registry/NeoForgeRegistryAdapter.java   # NeoForge RegisterEvent adapter with registry-key dispatching & intrusive holder binding
    │   │   └── resources/META-INF/                         # NeoForge 1.21.1 mod metadata and SPI bindings
    │   │       ├── neoforge.mods.toml                      # NeoForge mod metadata descriptor
    │   │       └── services/                               # ServiceLoader SPI bindings:
    │   │           ├── com.kingodogo.buildscape.block.IBlockFactory # Binds VersionCluster BlockFactory
    │   │           ├── com.kingodogo.buildscape.mixin.IMixinFactory # Binds VersionCluster MixinFactory
    │   │           ├── com.kingodogo.buildscape.network.IPacketFactory # Binds VersionCluster PacketFactory
    │   │           ├── com.kingodogo.buildscape.platform.IPlatformAdapter # Binds NeoForgePlatformAdapter
    │   │           └── com.kingodogo.buildscape.registry.IRegistryAdapter # Binds NeoForgeRegistryAdapter
    │
    ├── 26.2/                                               # Minecraft 26.2 on NeoForge (Java 25)
    │   ├── build.gradle                                    # Gradle build script for NeoForge 26.2
    │   └── src/main/                                       # NeoForge 26.2 source and resource root
    │   │   ├── java/com/kingodogo/buildscape/              # NeoForge 26.2 Java source package
    │   │   │   ├── BuildscapeNeoForge.java                 # NeoForge @Mod entrypoint & event listeners
    │   │   │   ├── platform/NeoForgePlatformAdapter.java   # NeoForge implementation of IPlatformAdapter
    │   │   │   └── registry/NeoForgeRegistryAdapter.java   # NeoForge 26.2 RegisterEvent adapter with registry-key dispatching
    │   │   └── resources/META-INF/                         # NeoForge 26.2 mod metadata and SPI bindings
    │   │       ├── neoforge.mods.toml                      # NeoForge mod metadata descriptor
    │   │       └── services/                               # ServiceLoader SPI bindings:
    │   │           ├── com.kingodogo.buildscape.block.IBlockFactory # Binds VersionCluster BlockFactory
    │   │           ├── com.kingodogo.buildscape.mixin.IMixinFactory # Binds VersionCluster MixinFactory
    │   │           ├── com.kingodogo.buildscape.network.IPacketFactory # Binds VersionCluster PacketFactory
    │   │           ├── com.kingodogo.buildscape.platform.IPlatformAdapter # Binds NeoForgePlatformAdapter
    │   │           └── com.kingodogo.buildscape.registry.IRegistryAdapter # Binds NeoForgeRegistryAdapter
    │
    └── 26.3/                                               # Minecraft 26.3 on NeoForge (Java 25)
        ├── build.gradle                                    # Gradle build script for NeoForge 26.3
        └── src/main/                                       # NeoForge 26.3 source and resource root
        │   ├── java/com/kingodogo/buildscape/              # NeoForge 26.3 Java source package
        │   │   ├── BuildscapeNeoForge.java                 # NeoForge @Mod entrypoint & event listeners
        │   │   ├── platform/NeoForgePlatformAdapter.java   # NeoForge implementation of IPlatformAdapter
        │   │   └── registry/NeoForgeRegistryAdapter.java   # NeoForge 26.3 RegisterEvent adapter with registry-key dispatching
        │   └── resources/META-INF/                         # NeoForge 26.3 mod metadata and SPI bindings
        │       ├── neoforge.mods.toml                      # NeoForge mod metadata descriptor
        │       └── services/                               # ServiceLoader SPI bindings:
        │           ├── com.kingodogo.buildscape.block.IBlockFactory # Binds VersionCluster BlockFactory
        │           ├── com.kingodogo.buildscape.mixin.IMixinFactory # Binds VersionCluster MixinFactory
        │           ├── com.kingodogo.buildscape.network.IPacketFactory # Binds VersionCluster PacketFactory
        │           ├── com.kingodogo.buildscape.platform.IPlatformAdapter # Binds NeoForgePlatformAdapter
        │           └── com.kingodogo.buildscape.registry.IRegistryAdapter # Binds NeoForgeRegistryAdapter
