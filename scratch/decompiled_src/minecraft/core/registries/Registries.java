package net.minecraft.core.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;

public class Registries {
   public static final Identifier ROOT_REGISTRY_NAME = Identifier.withDefaultNamespace("root");
   public static final ResourceKey ACTIVITY = createRegistryKey("activity");
   public static final ResourceKey ATTRIBUTE = createRegistryKey("attribute");
   public static final ResourceKey BIOME_SOURCE = createRegistryKey("worldgen/biome_source");
   public static final ResourceKey BLOCK_ENTITY_TYPE = createRegistryKey("block_entity_type");
   public static final ResourceKey BLOCK_PREDICATE_TYPE = createRegistryKey("block_predicate_type");
   public static final ResourceKey BLOCK_STATE_PROVIDER_TYPE = createRegistryKey("worldgen/block_state_provider_type");
   public static final ResourceKey BLOCK = createRegistryKey("block");
   public static final ResourceKey CARVER_TYPE = createRegistryKey("worldgen/carver_type");
   public static final ResourceKey CHUNK_GENERATOR = createRegistryKey("worldgen/chunk_generator");
   public static final ResourceKey CHUNK_STATUS = createRegistryKey("chunk_status");
   public static final ResourceKey COMMAND_ARGUMENT_TYPE = createRegistryKey("command_argument_type");
   public static final ResourceKey CONSUME_EFFECT_TYPE = createRegistryKey("consume_effect_type");
   public static final ResourceKey CREATIVE_MODE_TAB = createRegistryKey("creative_mode_tab");
   public static final ResourceKey CUSTOM_STAT = createRegistryKey("custom_stat");
   public static final ResourceKey DATA_COMPONENT_PREDICATE_TYPE = createRegistryKey("data_component_predicate_type");
   public static final ResourceKey DATA_COMPONENT_TYPE = createRegistryKey("data_component_type");
   public static final ResourceKey GAME_RULE = createRegistryKey("game_rule");
   public static final ResourceKey DEBUG_SUBSCRIPTION = createRegistryKey("debug_subscription");
   public static final ResourceKey DENSITY_FUNCTION_TYPE = createRegistryKey("worldgen/density_function_type");
   public static final ResourceKey DIALOG_BODY_TYPE = createRegistryKey("dialog_body_type");
   public static final ResourceKey DIALOG_TYPE = createRegistryKey("dialog_type");
   public static final ResourceKey ENCHANTMENT_EFFECT_COMPONENT_TYPE = createRegistryKey("enchantment_effect_component_type");
   public static final ResourceKey ENCHANTMENT_ENTITY_EFFECT_TYPE = createRegistryKey("enchantment_entity_effect_type");
   public static final ResourceKey ENCHANTMENT_LEVEL_BASED_VALUE_TYPE = createRegistryKey("enchantment_level_based_value_type");
   public static final ResourceKey ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE = createRegistryKey("enchantment_location_based_effect_type");
   public static final ResourceKey ENCHANTMENT_PROVIDER_TYPE = createRegistryKey("enchantment_provider_type");
   public static final ResourceKey ENCHANTMENT_VALUE_EFFECT_TYPE = createRegistryKey("enchantment_value_effect_type");
   public static final ResourceKey ENTITY_SUB_PREDICATE_TYPE = createRegistryKey("entity_sub_predicate_type");
   public static final ResourceKey ENTITY_TYPE = createRegistryKey("entity_type");
   public static final ResourceKey ENVIRONMENT_ATTRIBUTE = createRegistryKey("environment_attribute");
   public static final ResourceKey ATTRIBUTE_TYPE = createRegistryKey("attribute_type");
   public static final ResourceKey FEATURE_SIZE_TYPE = createRegistryKey("worldgen/feature_size_type");
   public static final ResourceKey FEATURE_TYPE = createRegistryKey("worldgen/feature_type");
   public static final ResourceKey FLOAT_PROVIDER_TYPE = createRegistryKey("float_provider_type");
   public static final ResourceKey FLUID = createRegistryKey("fluid");
   public static final ResourceKey FOLIAGE_PLACER_TYPE = createRegistryKey("worldgen/foliage_placer_type");
   public static final ResourceKey GAME_EVENT = createRegistryKey("game_event");
   public static final ResourceKey HEIGHT_PROVIDER_TYPE = createRegistryKey("height_provider_type");
   public static final ResourceKey INPUT_CONTROL_TYPE = createRegistryKey("input_control_type");
   public static final ResourceKey INT_PROVIDER_TYPE = createRegistryKey("int_provider_type");
   public static final ResourceKey ITEM = createRegistryKey("item");
   public static final ResourceKey SLOT_SOURCE_TYPE = createRegistryKey("slot_source_type");
   public static final ResourceKey LOOT_CONDITION_TYPE = createRegistryKey("loot_condition_type");
   public static final ResourceKey LOOT_FUNCTION_TYPE = createRegistryKey("loot_function_type");
   public static final ResourceKey LOOT_NBT_PROVIDER_TYPE = createRegistryKey("loot_nbt_provider_type");
   public static final ResourceKey LOOT_NUMBER_PROVIDER_TYPE = createRegistryKey("loot_number_provider_type");
   public static final ResourceKey LOOT_POOL_ENTRY_TYPE = createRegistryKey("loot_pool_entry_type");
   public static final ResourceKey LOOT_SCORE_PROVIDER_TYPE = createRegistryKey("loot_score_provider_type");
   public static final ResourceKey MAP_DECORATION_TYPE = createRegistryKey("map_decoration_type");
   public static final ResourceKey MATERIAL_CONDITION_TYPE = createRegistryKey("worldgen/material_condition_type");
   public static final ResourceKey MATERIAL_RULE_TYPE = createRegistryKey("worldgen/material_rule_type");
   public static final ResourceKey MEMORY_MODULE_TYPE = createRegistryKey("memory_module_type");
   public static final ResourceKey MENU = createRegistryKey("menu");
   public static final ResourceKey MOB_EFFECT = createRegistryKey("mob_effect");
   public static final ResourceKey NUMBER_FORMAT_TYPE = createRegistryKey("number_format_type");
   public static final ResourceKey PARTICLE_TYPE = createRegistryKey("particle_type");
   public static final ResourceKey PLACEMENT_MODIFIER_TYPE = createRegistryKey("worldgen/placement_modifier_type");
   public static final ResourceKey POINT_OF_INTEREST_TYPE = createRegistryKey("point_of_interest_type");
   public static final ResourceKey POOL_ALIAS_BINDING = createRegistryKey("worldgen/pool_alias_binding");
   public static final ResourceKey POSITION_SOURCE_TYPE = createRegistryKey("position_source_type");
   public static final ResourceKey POS_RULE_TEST = createRegistryKey("pos_rule_test");
   public static final ResourceKey POTION = createRegistryKey("potion");
   public static final ResourceKey RECIPE_BOOK_CATEGORY = createRegistryKey("recipe_book_category");
   public static final ResourceKey RECIPE_DISPLAY = createRegistryKey("recipe_display");
   public static final ResourceKey RECIPE_SERIALIZER = createRegistryKey("recipe_serializer");
   public static final ResourceKey RECIPE_TYPE = createRegistryKey("recipe_type");
   public static final ResourceKey ROOT_PLACER_TYPE = createRegistryKey("worldgen/root_placer_type");
   public static final ResourceKey RULE_BLOCK_ENTITY_MODIFIER = createRegistryKey("rule_block_entity_modifier");
   public static final ResourceKey RULE_TEST = createRegistryKey("rule_test");
   public static final ResourceKey SENSOR_TYPE = createRegistryKey("sensor_type");
   public static final ResourceKey SLOT_DISPLAY = createRegistryKey("slot_display");
   public static final ResourceKey SOUND_EVENT = createRegistryKey("sound_event");
   public static final ResourceKey SPAWN_CONDITION_TYPE = createRegistryKey("spawn_condition_type");
   public static final ResourceKey STAT_TYPE = createRegistryKey("stat_type");
   public static final ResourceKey STRUCTURE_PIECE = createRegistryKey("worldgen/structure_piece");
   public static final ResourceKey STRUCTURE_PLACEMENT = createRegistryKey("worldgen/structure_placement");
   public static final ResourceKey STRUCTURE_POOL_ELEMENT = createRegistryKey("worldgen/structure_pool_element");
   public static final ResourceKey STRUCTURE_PROCESSOR = createRegistryKey("worldgen/structure_processor");
   public static final ResourceKey STRUCTURE_TYPE = createRegistryKey("worldgen/structure_type");
   public static final ResourceKey DIALOG_ACTION_TYPE = createRegistryKey("dialog_action_type");
   public static final ResourceKey TEST_ENVIRONMENT_DEFINITION_TYPE = createRegistryKey("test_environment_definition_type");
   public static final ResourceKey TEST_FUNCTION = createRegistryKey("test_function");
   public static final ResourceKey TEST_INSTANCE_TYPE = createRegistryKey("test_instance_type");
   public static final ResourceKey TICKET_TYPE = createRegistryKey("ticket_type");
   public static final ResourceKey TREE_DECORATOR_TYPE = createRegistryKey("worldgen/tree_decorator_type");
   public static final ResourceKey TRUNK_PLACER_TYPE = createRegistryKey("worldgen/trunk_placer_type");
   public static final ResourceKey VILLAGER_PROFESSION = createRegistryKey("villager_profession");
   public static final ResourceKey VILLAGER_TYPE = createRegistryKey("villager_type");
   public static final ResourceKey INCOMING_RPC_METHOD = createRegistryKey("incoming_rpc_methods");
   public static final ResourceKey OUTGOING_RPC_METHOD = createRegistryKey("outgoing_rpc_methods");
   public static final ResourceKey PERMISSION_TYPE = createRegistryKey("permission_type");
   public static final ResourceKey PERMISSION_CHECK_TYPE = createRegistryKey("permission_check_type");
   public static final ResourceKey BANNER_PATTERN = createRegistryKey("banner_pattern");
   public static final ResourceKey BIOME = createRegistryKey("worldgen/biome");
   public static final ResourceKey CAT_SOUND_VARIANT = createRegistryKey("cat_sound_variant");
   public static final ResourceKey CAT_VARIANT = createRegistryKey("cat_variant");
   public static final ResourceKey CARVER = createRegistryKey("worldgen/carver");
   public static final ResourceKey CHAT_TYPE = createRegistryKey("chat_type");
   public static final ResourceKey CHICKEN_SOUND_VARIANT = createRegistryKey("chicken_sound_variant");
   public static final ResourceKey CHICKEN_VARIANT = createRegistryKey("chicken_variant");
   public static final ResourceKey ZOMBIE_NAUTILUS_VARIANT = createRegistryKey("zombie_nautilus_variant");
   public static final ResourceKey FEATURE = createRegistryKey("worldgen/feature");
   public static final ResourceKey COW_SOUND_VARIANT = createRegistryKey("cow_sound_variant");
   public static final ResourceKey COW_VARIANT = createRegistryKey("cow_variant");
   public static final ResourceKey DAMAGE_TYPE = createRegistryKey("damage_type");
   public static final ResourceKey DENSITY_FUNCTION = createRegistryKey("worldgen/density_function");
   public static final ResourceKey DIALOG = createRegistryKey("dialog");
   public static final ResourceKey DIMENSION_TYPE = createRegistryKey("dimension_type");
   public static final ResourceKey ENCHANTMENT_PROVIDER = createRegistryKey("enchantment_provider");
   public static final ResourceKey ENCHANTMENT = createRegistryKey("enchantment");
   public static final ResourceKey FLAT_LEVEL_GENERATOR_PRESET = createRegistryKey("worldgen/flat_level_generator_preset");
   public static final ResourceKey FROG_VARIANT = createRegistryKey("frog_variant");
   public static final ResourceKey INSTRUMENT = createRegistryKey("instrument");
   public static final ResourceKey JUKEBOX_SONG = createRegistryKey("jukebox_song");
   public static final ResourceKey MATERIAL_CONDITION = createRegistryKey("worldgen/material_condition");
   public static final ResourceKey MATERIAL_RULE = createRegistryKey("worldgen/material_rule");
   public static final ResourceKey MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = createRegistryKey("worldgen/multi_noise_biome_source_parameter_list");
   public static final ResourceKey NOISE_SETTINGS = createRegistryKey("worldgen/noise_settings");
   public static final ResourceKey NOISE = createRegistryKey("worldgen/noise");
   public static final ResourceKey PAINTING_VARIANT = createRegistryKey("painting_variant");
   public static final ResourceKey PIG_SOUND_VARIANT = createRegistryKey("pig_sound_variant");
   public static final ResourceKey PIG_VARIANT = createRegistryKey("pig_variant");
   public static final ResourceKey PLACED_FEATURE = createRegistryKey("worldgen/placed_feature");
   public static final ResourceKey PROCESSOR_LIST = createRegistryKey("worldgen/processor_list");
   public static final ResourceKey STRUCTURE_SET = createRegistryKey("worldgen/structure_set");
   public static final ResourceKey STRUCTURE = createRegistryKey("worldgen/structure");
   public static final ResourceKey SULFUR_CUBE_ARCHETYPE = createRegistryKey("sulfur_cube_archetype");
   public static final ResourceKey TEMPLATE_POOL = createRegistryKey("worldgen/template_pool");
   public static final ResourceKey TEST_ENVIRONMENT = createRegistryKey("test_environment");
   public static final ResourceKey TEST_INSTANCE = createRegistryKey("test_instance");
   public static final ResourceKey TIMELINE = createRegistryKey("timeline");
   public static final ResourceKey TRADE_SET = createRegistryKey("trade_set");
   public static final ResourceKey TRIAL_SPAWNER_CONFIG = createRegistryKey("trial_spawner");
   public static final ResourceKey TRIGGER_TYPE = createRegistryKey("trigger_type");
   public static final ResourceKey TRIM_MATERIAL = createRegistryKey("trim_material");
   public static final ResourceKey TRIM_PATTERN = createRegistryKey("trim_pattern");
   public static final ResourceKey VILLAGER_TRADE = createRegistryKey("villager_trade");
   public static final ResourceKey WOLF_VARIANT = createRegistryKey("wolf_variant");
   public static final ResourceKey WOLF_SOUND_VARIANT = createRegistryKey("wolf_sound_variant");
   public static final ResourceKey WORLD_CLOCK = createRegistryKey("world_clock");
   public static final ResourceKey WORLD_PRESET = createRegistryKey("worldgen/world_preset");
   public static final ResourceKey DECORATED_POT_PATTERN = createRegistryKey("decorated_pot_pattern");
   public static final ResourceKey DIMENSION = createRegistryKey("dimension");
   public static final ResourceKey LEVEL_STEM = createRegistryKey("dimension");
   public static final ResourceKey LOOT_TABLE = createRegistryKey("loot_table");
   public static final ResourceKey ITEM_MODIFIER = createRegistryKey("item_modifier");
   public static final ResourceKey PREDICATE = createRegistryKey("predicate");
   public static final ResourceKey SLOT_SOURCE = createRegistryKey("slot_source");
   public static final ResourceKey NUMBER_PROVIDER = createRegistryKey("number_provider");
   public static final ResourceKey ADVANCEMENT = createRegistryKey("advancement");
   public static final ResourceKey RECIPE = createRegistryKey("recipe");

   public static ResourceKey levelStemToLevel(final ResourceKey levelStem) {
      return ResourceKey.create(DIMENSION, levelStem.identifier());
   }

   public static ResourceKey levelToLevelStem(final ResourceKey level) {
      return ResourceKey.create(LEVEL_STEM, level.identifier());
   }

   private static ResourceKey createRegistryKey(final String name) {
      return ResourceKey.createRegistryKey(Identifier.withDefaultNamespace(name));
   }

   private static String registryDirPath(final ResourceKey registryKey) {
      return registryKey.identifier().getPath();
   }

   public static String elementsDirPath(final ResourceKey registryKey) {
      return registryDirPath(registryKey);
   }

   public static String tagsDirPath(final ResourceKey registryKey) {
      return "tags/" + registryDirPath(registryKey);
   }

   public static String componentsDirPath(final ResourceKey registryKey) {
      return "components/" + registryDirPath(registryKey);
   }
}
