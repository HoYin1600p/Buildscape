package net.minecraft.core.registries;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.advancements.predicates.entity.EntitySubPredicates;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.gametest.framework.BuiltinTestFunctions;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.dialog.DialogTypes;
import net.minecraft.server.dialog.action.ActionTypes;
import net.minecraft.server.dialog.body.DialogBodyTypes;
import net.minecraft.server.dialog.input.InputControlTypes;
import net.minecraft.server.jsonrpc.IncomingRpcMethods;
import net.minecraft.server.jsonrpc.OutgoingRpcMethods;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.permissions.PermissionCheckTypes;
import net.minecraft.server.permissions.PermissionTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Util;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.variant.SpawnConditions;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeSerializers;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplays;
import net.minecraft.world.item.crafting.display.SlotDisplays;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.item.enchantment.providers.EnchantmentProviderTypes;
import net.minecraft.world.item.slot.SlotSources;
import net.minecraft.world.level.biome.BiomeSources;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.chunk.ChunkGenerators;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.WorldCarverTypes;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.feature.FeatureTypes;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderTypes;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierTypes;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacements;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBindings;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorTypes;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionTypes;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviderTypes;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders;
import org.slf4j.Logger;

public class BuiltInRegistries {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map LOADERS = Maps.newLinkedHashMap();
   private static final WritableRegistry WRITABLE_REGISTRY = new MappedRegistry(ResourceKey.createRegistryKey(Registries.ROOT_REGISTRY_NAME), Lifecycle.stable());
   public static final DataComponentInitializers DATA_COMPONENT_INITIALIZERS = new DataComponentInitializers();
   public static final DefaultedRegistry GAME_EVENT = registerDefaulted(Registries.GAME_EVENT, "step", GameEvent::bootstrap);
   public static final Registry SOUND_EVENT = registerSimple(Registries.SOUND_EVENT, (registry) -> SoundEvents.ITEM_PICKUP);
   public static final DefaultedRegistry FLUID = registerDefaultedWithIntrusiveHolders(Registries.FLUID, "empty", (registry) -> Fluids.EMPTY);
   public static final Registry MOB_EFFECT = registerSimple(Registries.MOB_EFFECT, MobEffects::bootstrap);
   public static final DefaultedRegistry BLOCK = registerDefaultedWithIntrusiveHolders(Registries.BLOCK, "air", (registry) -> Blocks.AIR);
   public static final Registry DEBUG_SUBSCRIPTION = registerSimple(Registries.DEBUG_SUBSCRIPTION, DebugSubscriptions::bootstrap);
   public static final DefaultedRegistry ENTITY_TYPE = registerDefaultedWithIntrusiveHolders(Registries.ENTITY_TYPE, "pig", (registry) -> EntityTypes.PIG);
   public static final DefaultedRegistry ITEM = registerDefaultedWithIntrusiveHolders(Registries.ITEM, "air", (registry) -> Items.AIR);
   public static final Registry POTION = registerSimple(Registries.POTION, Potions::bootstrap);
   public static final Registry PARTICLE_TYPE = registerSimple(Registries.PARTICLE_TYPE, (registry) -> ParticleTypes.BLOCK);
   public static final Registry BLOCK_ENTITY_TYPE = registerSimpleWithIntrusiveHolders(Registries.BLOCK_ENTITY_TYPE, (registry) -> BlockEntityTypes.FURNACE);
   public static final Registry CUSTOM_STAT = registerSimple(Registries.CUSTOM_STAT, (registry) -> Stats.JUMP);
   public static final DefaultedRegistry CHUNK_STATUS = registerDefaulted(Registries.CHUNK_STATUS, "empty", (registry) -> ChunkStatus.EMPTY);
   public static final Registry RULE_TEST = registerSimple(Registries.RULE_TEST, (registry) -> RuleTestType.ALWAYS_TRUE_TEST);
   public static final Registry RULE_BLOCK_ENTITY_MODIFIER = registerSimple(Registries.RULE_BLOCK_ENTITY_MODIFIER, (registry) -> RuleBlockEntityModifierType.PASSTHROUGH);
   public static final Registry POS_RULE_TEST = registerSimple(Registries.POS_RULE_TEST, (registry) -> PosRuleTestType.ALWAYS_TRUE_TEST);
   public static final Registry MENU = registerSimple(Registries.MENU, (registry) -> MenuType.ANVIL);
   public static final Registry RECIPE_TYPE = registerSimple(Registries.RECIPE_TYPE, (registry) -> RecipeType.CRAFTING);
   public static final Registry RECIPE_SERIALIZER = registerSimple(Registries.RECIPE_SERIALIZER, RecipeSerializers::bootstrap);
   public static final Registry ATTRIBUTE = registerSimple(Registries.ATTRIBUTE, Attributes::bootstrap);
   public static final Registry POSITION_SOURCE_TYPE = registerSimple(Registries.POSITION_SOURCE_TYPE, (registry) -> PositionSourceType.BLOCK);
   public static final Registry COMMAND_ARGUMENT_TYPE = registerSimple(Registries.COMMAND_ARGUMENT_TYPE, ArgumentTypeInfos::bootstrap);
   public static final Registry STAT_TYPE = registerSimple(Registries.STAT_TYPE, (registry) -> Stats.ITEM_USED);
   public static final DefaultedRegistry VILLAGER_TYPE = registerDefaulted(Registries.VILLAGER_TYPE, "plains", VillagerType::bootstrap);
   public static final DefaultedRegistry VILLAGER_PROFESSION = registerDefaulted(Registries.VILLAGER_PROFESSION, "none", VillagerProfession::bootstrap);
   public static final Registry POINT_OF_INTEREST_TYPE = registerSimple(Registries.POINT_OF_INTEREST_TYPE, PoiTypes::bootstrap);
   public static final DefaultedRegistry MEMORY_MODULE_TYPE = registerDefaulted(Registries.MEMORY_MODULE_TYPE, "dummy", (registry) -> MemoryModuleType.DUMMY);
   public static final DefaultedRegistry SENSOR_TYPE = registerDefaulted(Registries.SENSOR_TYPE, "dummy", (registry) -> SensorType.DUMMY);
   public static final Registry ACTIVITY = registerSimple(Registries.ACTIVITY, (registry) -> Activity.IDLE);
   public static final Registry LOOT_POOL_ENTRY_TYPE = registerSimple(Registries.LOOT_POOL_ENTRY_TYPE, LootPoolEntries::bootstrap);
   public static final Registry LOOT_FUNCTION_TYPE = registerSimple(Registries.LOOT_FUNCTION_TYPE, LootItemFunctions::bootstrap);
   public static final Registry LOOT_CONDITION_TYPE = registerSimple(Registries.LOOT_CONDITION_TYPE, LootItemConditionTypes::bootstrap);
   public static final Registry LOOT_NUMBER_PROVIDER_TYPE = registerSimple(Registries.LOOT_NUMBER_PROVIDER_TYPE, NumberProviderTypes::bootstrap);
   public static final Registry LOOT_NBT_PROVIDER_TYPE = registerSimple(Registries.LOOT_NBT_PROVIDER_TYPE, NbtProviders::bootstrap);
   public static final Registry LOOT_SCORE_PROVIDER_TYPE = registerSimple(Registries.LOOT_SCORE_PROVIDER_TYPE, ScoreboardNameProviders::bootstrap);
   public static final Registry FLOAT_PROVIDER_TYPE = registerSimple(Registries.FLOAT_PROVIDER_TYPE, FloatProviders::bootstrap);
   public static final Registry INT_PROVIDER_TYPE = registerSimple(Registries.INT_PROVIDER_TYPE, IntProviders::bootstrap);
   public static final Registry HEIGHT_PROVIDER_TYPE = registerSimple(Registries.HEIGHT_PROVIDER_TYPE, (registry) -> HeightProviderType.CONSTANT);
   public static final Registry BLOCK_PREDICATE_TYPE = registerSimple(Registries.BLOCK_PREDICATE_TYPE, (registry) -> BlockPredicateType.NOT);
   public static final Registry CARVER_TYPE = registerSimple(Registries.CARVER_TYPE, WorldCarverTypes::bootstrap);
   public static final Registry FEATURE_TYPE = registerSimple(Registries.FEATURE_TYPE, FeatureTypes::bootstrap);
   public static final Registry STRUCTURE_PLACEMENT = registerSimple(Registries.STRUCTURE_PLACEMENT, StructurePlacements::bootstrap);
   public static final Registry STRUCTURE_PIECE = registerSimple(Registries.STRUCTURE_PIECE, (registry) -> StructurePieceType.MINE_SHAFT_ROOM);
   public static final Registry STRUCTURE_TYPE = registerSimple(Registries.STRUCTURE_TYPE, (registry) -> StructureType.JIGSAW);
   public static final Registry PLACEMENT_MODIFIER_TYPE = registerSimple(Registries.PLACEMENT_MODIFIER_TYPE, PlacementModifierTypes::bootstrap);
   public static final Registry BLOCK_STATE_PROVIDER_TYPE = registerSimple(Registries.BLOCK_STATE_PROVIDER_TYPE, BlockStateProviderTypes::bootstrap);
   public static final Registry FOLIAGE_PLACER_TYPE = registerSimple(Registries.FOLIAGE_PLACER_TYPE, (registry) -> FoliagePlacerType.BLOB_FOLIAGE_PLACER);
   public static final Registry TRUNK_PLACER_TYPE = registerSimple(Registries.TRUNK_PLACER_TYPE, (registry) -> TrunkPlacerType.STRAIGHT_TRUNK_PLACER);
   public static final Registry ROOT_PLACER_TYPE = registerSimple(Registries.ROOT_PLACER_TYPE, (registry) -> RootPlacerType.MANGROVE_ROOT_PLACER);
   public static final Registry TREE_DECORATOR_TYPE = registerSimple(Registries.TREE_DECORATOR_TYPE, (registry) -> TreeDecoratorType.LEAVE_VINE);
   public static final Registry FEATURE_SIZE_TYPE = registerSimple(Registries.FEATURE_SIZE_TYPE, (registry) -> FeatureSizeType.TWO_LAYERS_FEATURE_SIZE);
   public static final Registry BIOME_SOURCE = registerSimple(Registries.BIOME_SOURCE, BiomeSources::bootstrap);
   public static final Registry CHUNK_GENERATOR = registerSimple(Registries.CHUNK_GENERATOR, ChunkGenerators::bootstrap);
   public static final Registry MATERIAL_CONDITION_TYPE = registerSimple(Registries.MATERIAL_CONDITION_TYPE, SurfaceRules.ConditionSource::bootstrap);
   public static final Registry MATERIAL_RULE_TYPE = registerSimple(Registries.MATERIAL_RULE_TYPE, SurfaceRules.RuleSource::bootstrap);
   public static final Registry DENSITY_FUNCTION_TYPE = registerSimple(Registries.DENSITY_FUNCTION_TYPE, DensityFunctions::bootstrap);
   public static final Registry STRUCTURE_PROCESSOR = registerSimple(Registries.STRUCTURE_PROCESSOR, StructureProcessorTypes::bootstrap);
   public static final Registry STRUCTURE_POOL_ELEMENT = registerSimple(Registries.STRUCTURE_POOL_ELEMENT, (registry) -> StructurePoolElementType.EMPTY);
   public static final Registry POOL_ALIAS_BINDING_TYPE = registerSimple(Registries.POOL_ALIAS_BINDING, PoolAliasBindings::bootstrap);
   public static final Registry CREATIVE_MODE_TAB = registerSimple(Registries.CREATIVE_MODE_TAB, CreativeModeTabs::bootstrap);
   public static final Registry TRIGGER_TYPES = registerSimple(Registries.TRIGGER_TYPE, CriteriaTriggers::bootstrap);
   public static final Registry NUMBER_FORMAT_TYPE = registerSimple(Registries.NUMBER_FORMAT_TYPE, NumberFormatTypes::bootstrap);
   public static final Registry DATA_COMPONENT_TYPE = registerSimple(Registries.DATA_COMPONENT_TYPE, DataComponents::bootstrap);
   public static final Registry GAME_RULE = registerSimple(Registries.GAME_RULE, GameRules::bootstrap);
   public static final Registry ENTITY_SUB_PREDICATE_TYPE = registerSimple(Registries.ENTITY_SUB_PREDICATE_TYPE, EntitySubPredicates::bootstrap);
   public static final Registry DATA_COMPONENT_PREDICATE_TYPE = registerSimple(Registries.DATA_COMPONENT_PREDICATE_TYPE, DataComponentPredicates::bootstrap);
   public static final Registry MAP_DECORATION_TYPE = registerSimple(Registries.MAP_DECORATION_TYPE, MapDecorationTypes::bootstrap);
   public static final Registry ENCHANTMENT_EFFECT_COMPONENT_TYPE = registerSimple(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, EnchantmentEffectComponents::bootstrap);
   public static final Registry ENCHANTMENT_LEVEL_BASED_VALUE_TYPE = registerSimple(Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE, LevelBasedValue::bootstrap);
   public static final Registry ENCHANTMENT_ENTITY_EFFECT_TYPE = registerSimple(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, EnchantmentEntityEffect::bootstrap);
   public static final Registry ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE = registerSimple(Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE, EnchantmentLocationBasedEffect::bootstrap);
   public static final Registry ENCHANTMENT_VALUE_EFFECT_TYPE = registerSimple(Registries.ENCHANTMENT_VALUE_EFFECT_TYPE, EnchantmentValueEffect::bootstrap);
   public static final Registry ENCHANTMENT_PROVIDER_TYPE = registerSimple(Registries.ENCHANTMENT_PROVIDER_TYPE, EnchantmentProviderTypes::bootstrap);
   public static final Registry CONSUME_EFFECT_TYPE = registerSimple(Registries.CONSUME_EFFECT_TYPE, (registry) -> ConsumeEffect.Type.APPLY_EFFECTS);
   public static final Registry RECIPE_DISPLAY = registerSimple(Registries.RECIPE_DISPLAY, RecipeDisplays::bootstrap);
   public static final Registry SLOT_DISPLAY = registerSimple(Registries.SLOT_DISPLAY, SlotDisplays::bootstrap);
   public static final Registry RECIPE_BOOK_CATEGORY = registerSimple(Registries.RECIPE_BOOK_CATEGORY, RecipeBookCategories::bootstrap);
   public static final Registry TICKET_TYPE = registerSimple(Registries.TICKET_TYPE, (registry) -> TicketType.UNKNOWN);
   public static final Registry INCOMING_RPC_METHOD = registerSimple(Registries.INCOMING_RPC_METHOD, IncomingRpcMethods::bootstrap);
   public static final Registry OUTGOING_RPC_METHOD = registerSimple(Registries.OUTGOING_RPC_METHOD, (registry) -> OutgoingRpcMethods.SERVER_STARTED);
   public static final Registry TEST_ENVIRONMENT_DEFINITION_TYPE = registerSimple(Registries.TEST_ENVIRONMENT_DEFINITION_TYPE, TestEnvironmentDefinition::bootstrap);
   public static final Registry TEST_INSTANCE_TYPE = registerSimple(Registries.TEST_INSTANCE_TYPE, GameTestInstance::bootstrap);
   public static final Registry SPAWN_CONDITION_TYPE = registerSimple(Registries.SPAWN_CONDITION_TYPE, SpawnConditions::bootstrap);
   public static final Registry DIALOG_TYPE = registerSimple(Registries.DIALOG_TYPE, DialogTypes::bootstrap);
   public static final Registry DIALOG_ACTION_TYPE = registerSimple(Registries.DIALOG_ACTION_TYPE, ActionTypes::bootstrap);
   public static final Registry INPUT_CONTROL_TYPE = registerSimple(Registries.INPUT_CONTROL_TYPE, InputControlTypes::bootstrap);
   public static final Registry DIALOG_BODY_TYPE = registerSimple(Registries.DIALOG_BODY_TYPE, DialogBodyTypes::bootstrap);
   public static final Registry PERMISSION_TYPE = registerSimple(Registries.PERMISSION_TYPE, PermissionTypes::bootstrap);
   public static final Registry PERMISSION_CHECK_TYPE = registerSimple(Registries.PERMISSION_CHECK_TYPE, PermissionCheckTypes::bootstrap);
   public static final Registry ENVIRONMENT_ATTRIBUTE = registerSimple(Registries.ENVIRONMENT_ATTRIBUTE, EnvironmentAttributes::bootstrap);
   public static final Registry ATTRIBUTE_TYPE = registerSimple(Registries.ATTRIBUTE_TYPE, AttributeTypes::bootstrap);
   public static final Registry SLOT_SOURCE_TYPE = registerSimple(Registries.SLOT_SOURCE_TYPE, SlotSources::bootstrap);
   public static final Registry TEST_FUNCTION = registerSimple(Registries.TEST_FUNCTION, BuiltinTestFunctions::bootstrap);
   public static final Registry REGISTRY = WRITABLE_REGISTRY;

   private static Registry registerSimple(final ResourceKey name, final BuiltInRegistries.RegistryBootstrap loader) {
      return internalRegister(name, new MappedRegistry(name, Lifecycle.stable(), false), loader);
   }

   private static Registry registerSimpleWithIntrusiveHolders(final ResourceKey name, final BuiltInRegistries.RegistryBootstrap loader) {
      return internalRegister(name, new MappedRegistry(name, Lifecycle.stable(), true), loader);
   }

   private static DefaultedRegistry registerDefaulted(final ResourceKey name, final String defaultKey, final BuiltInRegistries.RegistryBootstrap loader) {
      return (DefaultedRegistry)internalRegister(name, new DefaultedMappedRegistry(defaultKey, name, Lifecycle.stable(), false), loader);
   }

   private static DefaultedRegistry registerDefaultedWithIntrusiveHolders(final ResourceKey name, final String defaultKey, final BuiltInRegistries.RegistryBootstrap loader) {
      return (DefaultedRegistry)internalRegister(name, new DefaultedMappedRegistry(defaultKey, name, Lifecycle.stable(), true), loader);
   }

   private static WritableRegistry internalRegister(final ResourceKey name, final WritableRegistry registry, final BuiltInRegistries.RegistryBootstrap loader) {
      Bootstrap.checkBootstrapCalled(() -> "registry " + String.valueOf(name.identifier()));
      Identifier key = name.identifier();
      LOADERS.put(key, (Supplier)() -> loader.run(registry));
      WRITABLE_REGISTRY.register(name, registry, RegistrationInfo.BUILT_IN);
      return registry;
   }

   public static void bootStrap() {
      createContents();
      freeze();
      validate(REGISTRY);
   }

   private static void createContents() {
      LOADERS.forEach((key, value) -> {
         if (value.get() == null) {
            LOGGER.error("Unable to bootstrap registry '{}'", key);
         }

      });
   }

   private static void freeze() {
      REGISTRY.freeze();

      for(Registry registry : REGISTRY) {
         bindBootstrappedTagsToEmpty(registry);
         registry.freeze();
      }

   }

   private static void validate(final Registry registry) {
      registry.forEach((r) -> {
         if (r.keySet().isEmpty()) {
            Util.logAndPauseIfInIde("Registry '" + String.valueOf(registry.getKey(r)) + "' was empty after loading");
         }

         if (r instanceof DefaultedRegistry) {
            Identifier key = ((DefaultedRegistry)r).getDefaultKey();
            Objects.requireNonNull(r.getValue(key), "Missing default of DefaultedMappedRegistry: " + String.valueOf(key));
         }

      });
   }

   public static HolderGetter acquireBootstrapRegistrationLookup(final Registry registry) {
      return ((WritableRegistry)registry).createRegistrationLookup();
   }

   private static void bindBootstrappedTagsToEmpty(final Registry registry) {
      ((MappedRegistry)registry).bindAllTagsToEmpty();
   }

   @FunctionalInterface
   private interface RegistryBootstrap {
      Object run(Registry registry);
   }
}
