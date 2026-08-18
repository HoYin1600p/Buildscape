package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class CriteriaTriggers {
   public static final Codec CODEC = BuiltInRegistries.TRIGGER_TYPES.byNameCodec();
   public static final ImpossibleTrigger IMPOSSIBLE = (ImpossibleTrigger)register("impossible", new ImpossibleTrigger());
   public static final KilledTrigger PLAYER_KILLED_ENTITY = (KilledTrigger)register("player_killed_entity", new KilledTrigger());
   public static final KilledTrigger ENTITY_KILLED_PLAYER = (KilledTrigger)register("entity_killed_player", new KilledTrigger());
   public static final EnterBlockTrigger ENTER_BLOCK = (EnterBlockTrigger)register("enter_block", new EnterBlockTrigger());
   public static final InventoryChangeTrigger INVENTORY_CHANGED = (InventoryChangeTrigger)register("inventory_changed", new InventoryChangeTrigger());
   public static final RecipeUnlockedTrigger RECIPE_UNLOCKED = (RecipeUnlockedTrigger)register("recipe_unlocked", new RecipeUnlockedTrigger());
   public static final PlayerHurtEntityTrigger PLAYER_HURT_ENTITY = (PlayerHurtEntityTrigger)register("player_hurt_entity", new PlayerHurtEntityTrigger());
   public static final EntityHurtPlayerTrigger ENTITY_HURT_PLAYER = (EntityHurtPlayerTrigger)register("entity_hurt_player", new EntityHurtPlayerTrigger());
   public static final EnchantedItemTrigger ENCHANTED_ITEM = (EnchantedItemTrigger)register("enchanted_item", new EnchantedItemTrigger());
   public static final FilledBucketTrigger FILLED_BUCKET = (FilledBucketTrigger)register("filled_bucket", new FilledBucketTrigger());
   public static final BrewedPotionTrigger BREWED_POTION = (BrewedPotionTrigger)register("brewed_potion", new BrewedPotionTrigger());
   public static final ConstructBeaconTrigger CONSTRUCT_BEACON = (ConstructBeaconTrigger)register("construct_beacon", new ConstructBeaconTrigger());
   public static final UsedEnderEyeTrigger USED_ENDER_EYE = (UsedEnderEyeTrigger)register("used_ender_eye", new UsedEnderEyeTrigger());
   public static final SummonedEntityTrigger SUMMONED_ENTITY = (SummonedEntityTrigger)register("summoned_entity", new SummonedEntityTrigger());
   public static final BredAnimalsTrigger BRED_ANIMALS = (BredAnimalsTrigger)register("bred_animals", new BredAnimalsTrigger());
   public static final PlayerTrigger LOCATION = (PlayerTrigger)register("location", new PlayerTrigger());
   public static final PlayerTrigger SLEPT_IN_BED = (PlayerTrigger)register("slept_in_bed", new PlayerTrigger());
   public static final CuredZombieVillagerTrigger CURED_ZOMBIE_VILLAGER = (CuredZombieVillagerTrigger)register("cured_zombie_villager", new CuredZombieVillagerTrigger());
   public static final TradeTrigger TRADE = (TradeTrigger)register("villager_trade", new TradeTrigger());
   public static final ItemDurabilityTrigger ITEM_DURABILITY_CHANGED = (ItemDurabilityTrigger)register("item_durability_changed", new ItemDurabilityTrigger());
   public static final LevitationTrigger LEVITATION = (LevitationTrigger)register("levitation", new LevitationTrigger());
   public static final ChangeDimensionTrigger CHANGED_DIMENSION = (ChangeDimensionTrigger)register("changed_dimension", new ChangeDimensionTrigger());
   public static final PlayerTrigger TICK = (PlayerTrigger)register("tick", new PlayerTrigger());
   public static final TameAnimalTrigger TAME_ANIMAL = (TameAnimalTrigger)register("tame_animal", new TameAnimalTrigger());
   public static final ItemUsedOnLocationTrigger PLACED_BLOCK = (ItemUsedOnLocationTrigger)register("placed_block", new ItemUsedOnLocationTrigger());
   public static final ConsumeItemTrigger CONSUME_ITEM = (ConsumeItemTrigger)register("consume_item", new ConsumeItemTrigger());
   public static final EffectsChangedTrigger EFFECTS_CHANGED = (EffectsChangedTrigger)register("effects_changed", new EffectsChangedTrigger());
   public static final UsedTotemTrigger USED_TOTEM = (UsedTotemTrigger)register("used_totem", new UsedTotemTrigger());
   public static final DistanceTrigger NETHER_TRAVEL = (DistanceTrigger)register("nether_travel", new DistanceTrigger());
   public static final FishingRodHookedTrigger FISHING_ROD_HOOKED = (FishingRodHookedTrigger)register("fishing_rod_hooked", new FishingRodHookedTrigger());
   public static final ChanneledLightningTrigger CHANNELED_LIGHTNING = (ChanneledLightningTrigger)register("channeled_lightning", new ChanneledLightningTrigger());
   public static final ShotCrossbowTrigger SHOT_CROSSBOW = (ShotCrossbowTrigger)register("shot_crossbow", new ShotCrossbowTrigger());
   public static final SpearMobsTrigger SPEAR_MOBS_TRIGGER = (SpearMobsTrigger)register("spear_mobs", new SpearMobsTrigger());
   public static final KilledByArrowTrigger KILLED_BY_ARROW = (KilledByArrowTrigger)register("killed_by_arrow", new KilledByArrowTrigger());
   public static final PlayerTrigger RAID_WIN = (PlayerTrigger)register("hero_of_the_village", new PlayerTrigger());
   public static final PlayerTrigger RAID_OMEN = (PlayerTrigger)register("voluntary_exile", new PlayerTrigger());
   public static final SlideDownBlockTrigger HONEY_BLOCK_SLIDE = (SlideDownBlockTrigger)register("slide_down_block", new SlideDownBlockTrigger());
   public static final BeeNestDestroyedTrigger BEE_NEST_DESTROYED = (BeeNestDestroyedTrigger)register("bee_nest_destroyed", new BeeNestDestroyedTrigger());
   public static final TargetBlockTrigger TARGET_BLOCK_HIT = (TargetBlockTrigger)register("target_hit", new TargetBlockTrigger());
   public static final ItemUsedOnLocationTrigger ITEM_USED_ON_BLOCK = (ItemUsedOnLocationTrigger)register("item_used_on_block", new ItemUsedOnLocationTrigger());
   public static final DefaultBlockInteractionTrigger DEFAULT_BLOCK_USE = (DefaultBlockInteractionTrigger)register("default_block_use", new DefaultBlockInteractionTrigger());
   public static final AnyBlockInteractionTrigger ANY_BLOCK_USE = (AnyBlockInteractionTrigger)register("any_block_use", new AnyBlockInteractionTrigger());
   public static final LootTableTrigger GENERATE_LOOT = (LootTableTrigger)register("player_generates_container_loot", new LootTableTrigger());
   public static final PickedUpItemTrigger THROWN_ITEM_PICKED_UP_BY_ENTITY = (PickedUpItemTrigger)register("thrown_item_picked_up_by_entity", new PickedUpItemTrigger());
   public static final PickedUpItemTrigger THROWN_ITEM_PICKED_UP_BY_PLAYER = (PickedUpItemTrigger)register("thrown_item_picked_up_by_player", new PickedUpItemTrigger());
   public static final PlayerInteractTrigger PLAYER_INTERACTED_WITH_ENTITY = (PlayerInteractTrigger)register("player_interacted_with_entity", new PlayerInteractTrigger());
   public static final PlayerInteractTrigger PLAYER_SHEARED_EQUIPMENT = (PlayerInteractTrigger)register("player_sheared_equipment", new PlayerInteractTrigger());
   public static final StartRidingTrigger START_RIDING_TRIGGER = (StartRidingTrigger)register("started_riding", new StartRidingTrigger());
   public static final LightningStrikeTrigger LIGHTNING_STRIKE = (LightningStrikeTrigger)register("lightning_strike", new LightningStrikeTrigger());
   public static final UsingItemTrigger USING_ITEM = (UsingItemTrigger)register("using_item", new UsingItemTrigger());
   public static final DistanceTrigger FALL_FROM_HEIGHT = (DistanceTrigger)register("fall_from_height", new DistanceTrigger());
   public static final DistanceTrigger RIDE_ENTITY_IN_LAVA_TRIGGER = (DistanceTrigger)register("ride_entity_in_lava", new DistanceTrigger());
   public static final KilledTrigger KILL_MOB_NEAR_SCULK_CATALYST = (KilledTrigger)register("kill_mob_near_sculk_catalyst", new KilledTrigger());
   public static final ItemUsedOnLocationTrigger ALLAY_DROP_ITEM_ON_BLOCK = (ItemUsedOnLocationTrigger)register("allay_drop_item_on_block", new ItemUsedOnLocationTrigger());
   public static final PlayerTrigger AVOID_VIBRATION = (PlayerTrigger)register("avoid_vibration", new PlayerTrigger());
   public static final RecipeCraftedTrigger RECIPE_CRAFTED = (RecipeCraftedTrigger)register("recipe_crafted", new RecipeCraftedTrigger());
   public static final RecipeCraftedTrigger CRAFTER_RECIPE_CRAFTED = (RecipeCraftedTrigger)register("crafter_recipe_crafted", new RecipeCraftedTrigger());
   public static final FallAfterExplosionTrigger FALL_AFTER_EXPLOSION = (FallAfterExplosionTrigger)register("fall_after_explosion", new FallAfterExplosionTrigger());

   private static CriterionTrigger register(final String name, final CriterionTrigger criterion) {
      return (CriterionTrigger)Registry.register(BuiltInRegistries.TRIGGER_TYPES, name, criterion);
   }

   public static CriterionTrigger bootstrap(final Registry registry) {
      return IMPOSSIBLE;
   }
}
