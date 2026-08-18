package net.minecraft.world.level.storage.loot;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.ColorCollection;

public class BuiltInLootTables {
   private static final Set LOCATIONS = new HashSet();
   private static final Set IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOCATIONS);
   public static final ResourceKey SPAWN_BONUS_CHEST = register("chests/spawn_bonus_chest");
   public static final ResourceKey END_CITY_TREASURE = register("chests/end_city_treasure");
   public static final ResourceKey SIMPLE_DUNGEON = register("chests/simple_dungeon");
   public static final ResourceKey VILLAGE_WEAPONSMITH = register("chests/village/village_weaponsmith");
   public static final ResourceKey VILLAGE_TOOLSMITH = register("chests/village/village_toolsmith");
   public static final ResourceKey VILLAGE_ARMORER = register("chests/village/village_armorer");
   public static final ResourceKey VILLAGE_CARTOGRAPHER = register("chests/village/village_cartographer");
   public static final ResourceKey VILLAGE_MASON = register("chests/village/village_mason");
   public static final ResourceKey VILLAGE_SHEPHERD = register("chests/village/village_shepherd");
   public static final ResourceKey VILLAGE_BUTCHER = register("chests/village/village_butcher");
   public static final ResourceKey VILLAGE_FLETCHER = register("chests/village/village_fletcher");
   public static final ResourceKey VILLAGE_FISHER = register("chests/village/village_fisher");
   public static final ResourceKey VILLAGE_TANNERY = register("chests/village/village_tannery");
   public static final ResourceKey VILLAGE_TEMPLE = register("chests/village/village_temple");
   public static final ResourceKey VILLAGE_DESERT_HOUSE = register("chests/village/village_desert_house");
   public static final ResourceKey VILLAGE_PLAINS_HOUSE = register("chests/village/village_plains_house");
   public static final ResourceKey VILLAGE_TAIGA_HOUSE = register("chests/village/village_taiga_house");
   public static final ResourceKey VILLAGE_SNOWY_HOUSE = register("chests/village/village_snowy_house");
   public static final ResourceKey VILLAGE_SAVANNA_HOUSE = register("chests/village/village_savanna_house");
   public static final ResourceKey ABANDONED_MINESHAFT = register("chests/abandoned_mineshaft");
   public static final ResourceKey NETHER_BRIDGE = register("chests/nether_bridge");
   public static final ResourceKey STRONGHOLD_LIBRARY = register("chests/stronghold_library");
   public static final ResourceKey STRONGHOLD_CROSSING = register("chests/stronghold_crossing");
   public static final ResourceKey STRONGHOLD_CORRIDOR = register("chests/stronghold_corridor");
   public static final ResourceKey DESERT_PYRAMID = register("chests/desert_pyramid");
   public static final ResourceKey JUNGLE_TEMPLE = register("chests/jungle_temple");
   public static final ResourceKey JUNGLE_TEMPLE_DISPENSER = register("chests/jungle_temple_dispenser");
   public static final ResourceKey IGLOO_CHEST = register("chests/igloo_chest");
   public static final ResourceKey WOODLAND_MANSION = register("chests/woodland_mansion");
   public static final ResourceKey UNDERWATER_RUIN_SMALL = register("chests/underwater_ruin_small");
   public static final ResourceKey UNDERWATER_RUIN_BIG = register("chests/underwater_ruin_big");
   public static final ResourceKey BURIED_TREASURE = register("chests/buried_treasure");
   public static final ResourceKey SHIPWRECK_MAP = register("chests/shipwreck_map");
   public static final ResourceKey SHIPWRECK_SUPPLY = register("chests/shipwreck_supply");
   public static final ResourceKey SHIPWRECK_TREASURE = register("chests/shipwreck_treasure");
   public static final ResourceKey PILLAGER_OUTPOST = register("chests/pillager_outpost");
   public static final ResourceKey BASTION_TREASURE = register("chests/bastion_treasure");
   public static final ResourceKey BASTION_OTHER = register("chests/bastion_other");
   public static final ResourceKey BASTION_BRIDGE = register("chests/bastion_bridge");
   public static final ResourceKey BASTION_HOGLIN_STABLE = register("chests/bastion_hoglin_stable");
   public static final ResourceKey ANCIENT_CITY = register("chests/ancient_city");
   public static final ResourceKey ANCIENT_CITY_ICE_BOX = register("chests/ancient_city_ice_box");
   public static final ResourceKey RUINED_PORTAL = register("chests/ruined_portal");
   public static final ResourceKey TRIAL_CHAMBERS_REWARD = register("chests/trial_chambers/reward");
   public static final ResourceKey TRIAL_CHAMBERS_REWARD_COMMON = register("chests/trial_chambers/reward_common");
   public static final ResourceKey TRIAL_CHAMBERS_REWARD_RARE = register("chests/trial_chambers/reward_rare");
   public static final ResourceKey TRIAL_CHAMBERS_REWARD_UNIQUE = register("chests/trial_chambers/reward_unique");
   public static final ResourceKey TRIAL_CHAMBERS_REWARD_OMINOUS = register("chests/trial_chambers/reward_ominous");
   public static final ResourceKey TRIAL_CHAMBERS_REWARD_OMINOUS_COMMON = register("chests/trial_chambers/reward_ominous_common");
   public static final ResourceKey TRIAL_CHAMBERS_REWARD_OMINOUS_RARE = register("chests/trial_chambers/reward_ominous_rare");
   public static final ResourceKey TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE = register("chests/trial_chambers/reward_ominous_unique");
   public static final ResourceKey TRIAL_CHAMBERS_SUPPLY = register("chests/trial_chambers/supply");
   public static final ResourceKey TRIAL_CHAMBERS_CORRIDOR = register("chests/trial_chambers/corridor");
   public static final ResourceKey TRIAL_CHAMBERS_INTERSECTION = register("chests/trial_chambers/intersection");
   public static final ResourceKey TRIAL_CHAMBERS_INTERSECTION_BARREL = register("chests/trial_chambers/intersection_barrel");
   public static final ResourceKey TRIAL_CHAMBERS_ENTRANCE = register("chests/trial_chambers/entrance");
   public static final ResourceKey TRIAL_CHAMBERS_CORRIDOR_DISPENSER = register("dispensers/trial_chambers/corridor");
   public static final ResourceKey TRIAL_CHAMBERS_CHAMBER_DISPENSER = register("dispensers/trial_chambers/chamber");
   public static final ResourceKey TRIAL_CHAMBERS_WATER_DISPENSER = register("dispensers/trial_chambers/water");
   public static final ResourceKey TRIAL_CHAMBERS_CORRIDOR_POT = register("pots/trial_chambers/corridor");
   public static final ResourceKey EQUIPMENT_TRIAL_CHAMBER = register("equipment/trial_chamber");
   public static final ResourceKey EQUIPMENT_TRIAL_CHAMBER_RANGED = register("equipment/trial_chamber_ranged");
   public static final ResourceKey EQUIPMENT_TRIAL_CHAMBER_MELEE = register("equipment/trial_chamber_melee");
   public static final ColorCollection SHEEP = ColorCollection.NAMES.map((color) -> register("entities/sheep/" + color));
   public static final ResourceKey FISHING = register("gameplay/fishing");
   public static final ResourceKey FISHING_JUNK = register("gameplay/fishing/junk");
   public static final ResourceKey FISHING_TREASURE = register("gameplay/fishing/treasure");
   public static final ResourceKey FISHING_FISH = register("gameplay/fishing/fish");
   public static final ResourceKey CAT_MORNING_GIFT = register("gameplay/cat_morning_gift");
   public static final ResourceKey ARMORER_GIFT = register("gameplay/hero_of_the_village/armorer_gift");
   public static final ResourceKey BUTCHER_GIFT = register("gameplay/hero_of_the_village/butcher_gift");
   public static final ResourceKey CARTOGRAPHER_GIFT = register("gameplay/hero_of_the_village/cartographer_gift");
   public static final ResourceKey CLERIC_GIFT = register("gameplay/hero_of_the_village/cleric_gift");
   public static final ResourceKey FARMER_GIFT = register("gameplay/hero_of_the_village/farmer_gift");
   public static final ResourceKey FISHERMAN_GIFT = register("gameplay/hero_of_the_village/fisherman_gift");
   public static final ResourceKey FLETCHER_GIFT = register("gameplay/hero_of_the_village/fletcher_gift");
   public static final ResourceKey LEATHERWORKER_GIFT = register("gameplay/hero_of_the_village/leatherworker_gift");
   public static final ResourceKey LIBRARIAN_GIFT = register("gameplay/hero_of_the_village/librarian_gift");
   public static final ResourceKey MASON_GIFT = register("gameplay/hero_of_the_village/mason_gift");
   public static final ResourceKey SHEPHERD_GIFT = register("gameplay/hero_of_the_village/shepherd_gift");
   public static final ResourceKey TOOLSMITH_GIFT = register("gameplay/hero_of_the_village/toolsmith_gift");
   public static final ResourceKey WEAPONSMITH_GIFT = register("gameplay/hero_of_the_village/weaponsmith_gift");
   public static final ResourceKey UNEMPLOYED_GIFT = register("gameplay/hero_of_the_village/unemployed_gift");
   public static final ResourceKey BABY_VILLAGER_GIFT = register("gameplay/hero_of_the_village/baby_gift");
   public static final ResourceKey SNIFFER_DIGGING = register("gameplay/sniffer_digging");
   public static final ResourceKey PANDA_SNEEZE = register("gameplay/panda_sneeze");
   public static final ResourceKey CHICKEN_LAY = register("gameplay/chicken_lay");
   public static final ResourceKey ARMADILLO_SHED = register("gameplay/armadillo_shed");
   public static final ResourceKey TURTLE_GROW = register("gameplay/turtle_grow");
   public static final ResourceKey HARVEST_CAVE_VINE = register("harvest/cave_vine");
   public static final ResourceKey HARVEST_SWEET_BERRY_BUSH = register("harvest/sweet_berry_bush");
   public static final ResourceKey HARVEST_BEEHIVE = register("harvest/beehive");
   public static final ResourceKey CARVE_PUMPKIN = register("carve/pumpkin");
   public static final ResourceKey TILL_ROOTED_DIRT = register("till/rooted_dirt");
   public static final ResourceKey PIGLIN_BARTERING = register("gameplay/piglin_bartering");
   public static final ResourceKey SPAWNER_TRIAL_CHAMBER_KEY = register("spawners/trial_chamber/key");
   public static final ResourceKey SPAWNER_TRIAL_CHAMBER_CONSUMABLES = register("spawners/trial_chamber/consumables");
   public static final ResourceKey SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY = register("spawners/ominous/trial_chamber/key");
   public static final ResourceKey SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES = register("spawners/ominous/trial_chamber/consumables");
   public static final ResourceKey SPAWNER_TRIAL_ITEMS_TO_DROP_WHEN_OMINOUS = register("spawners/trial_chamber/items_to_drop_when_ominous");
   public static final ResourceKey ARMADILLO_BRUSH = register("brush/armadillo");
   public static final ResourceKey BOGGED_SHEAR = register("shearing/bogged");
   public static final ResourceKey SHEAR_MOOSHROOM = register("shearing/mooshroom");
   public static final ResourceKey SHEAR_RED_MOOSHROOM = register("shearing/mooshroom/red");
   public static final ResourceKey SHEAR_BROWN_MOOSHROOM = register("shearing/mooshroom/brown");
   public static final ResourceKey SHEAR_SNOW_GOLEM = register("shearing/snow_golem");
   public static final ResourceKey SHEAR_SHEEP = register("shearing/sheep");
   public static final ColorCollection SHEAR_DYED_SHEEP = ColorCollection.NAMES.map((color) -> register("shearing/sheep/" + color));
   public static final ResourceKey CHARGED_CREEPER = register("charged_creeper/root");
   public static final ResourceKey CHARGED_CREEPER_PIGLIN = register("charged_creeper/piglin");
   public static final ResourceKey CHARGED_CREEPER_CREEPER = register("charged_creeper/creeper");
   public static final ResourceKey CHARGED_CREEPER_SKELETON = register("charged_creeper/skeleton");
   public static final ResourceKey CHARGED_CREEPER_WITHER_SKELETON = register("charged_creeper/wither_skeleton");
   public static final ResourceKey CHARGED_CREEPER_ZOMBIE = register("charged_creeper/zombie");
   public static final ResourceKey DESERT_WELL_ARCHAEOLOGY = register("archaeology/desert_well");
   public static final ResourceKey DESERT_PYRAMID_ARCHAEOLOGY = register("archaeology/desert_pyramid");
   public static final ResourceKey TRAIL_RUINS_ARCHAEOLOGY_COMMON = register("archaeology/trail_ruins_common");
   public static final ResourceKey TRAIL_RUINS_ARCHAEOLOGY_RARE = register("archaeology/trail_ruins_rare");
   public static final ResourceKey OCEAN_RUIN_WARM_ARCHAEOLOGY = register("archaeology/ocean_ruin_warm");
   public static final ResourceKey OCEAN_RUIN_COLD_ARCHAEOLOGY = register("archaeology/ocean_ruin_cold");
   public static final ResourceKey ABANDONED_CAMP_BARREL = register("barrels/abandoned_camp_barrel");
   public static final ResourceKey ABANDONED_CAMP_COMMON_CHEST = register("chests/abandoned_camp_common_chest");
   public static final ResourceKey ABANDONED_CAMP_SECRET_CHEST = register("chests/abandoned_camp_secret_chest");

   private static ResourceKey register(final String location) {
      return register(ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace(location)));
   }

   private static ResourceKey register(final ResourceKey location) {
      if (LOCATIONS.add(location)) {
         return location;
      } else {
         throw new IllegalArgumentException(String.valueOf(location.identifier()) + " is already a registered built-in loot table");
      }
   }

   public static Set all() {
      return IMMUTABLE_LOCATIONS;
   }
}
