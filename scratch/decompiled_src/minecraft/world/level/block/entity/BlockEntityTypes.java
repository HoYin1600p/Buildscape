package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.slf4j.Logger;

public class BlockEntityTypes {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final BlockEntityType FURNACE = register(BlockEntityTypeIds.FURNACE, FurnaceBlockEntity::new, Blocks.FURNACE);
   public static final BlockEntityType CHEST = register(BlockEntityTypeIds.CHEST, ChestBlockEntity::new, Util.copyAndAdd(Blocks.COPPER_CHEST.asList(), (Object)Blocks.CHEST));
   public static final BlockEntityType TRAPPED_CHEST = register(BlockEntityTypeIds.TRAPPED_CHEST, TrappedChestBlockEntity::new, Blocks.TRAPPED_CHEST);
   public static final BlockEntityType ENDER_CHEST = register(BlockEntityTypeIds.ENDER_CHEST, EnderChestBlockEntity::new, Blocks.ENDER_CHEST);
   public static final BlockEntityType JUKEBOX = register(BlockEntityTypeIds.JUKEBOX, JukeboxBlockEntity::new, Blocks.JUKEBOX);
   public static final BlockEntityType DISPENSER = register(BlockEntityTypeIds.DISPENSER, DispenserBlockEntity::new, Blocks.DISPENSER);
   public static final BlockEntityType DROPPER = register(BlockEntityTypeIds.DROPPER, DropperBlockEntity::new, Blocks.DROPPER);
   public static final BlockEntityType SIGN = register(BlockEntityTypeIds.SIGN, SignBlockEntity::new, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.CHERRY_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.PALE_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.CHERRY_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.PALE_OAK_WALL_SIGN, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN, Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN, Blocks.MANGROVE_SIGN, Blocks.MANGROVE_WALL_SIGN, Blocks.BAMBOO_SIGN, Blocks.BAMBOO_WALL_SIGN, Blocks.POPLAR_SIGN, Blocks.POPLAR_WALL_SIGN);
   public static final BlockEntityType HANGING_SIGN = register(BlockEntityTypeIds.HANGING_SIGN, HangingSignBlockEntity::new, Blocks.OAK_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.CHERRY_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.PALE_OAK_HANGING_SIGN, Blocks.CRIMSON_HANGING_SIGN, Blocks.WARPED_HANGING_SIGN, Blocks.MANGROVE_HANGING_SIGN, Blocks.BAMBOO_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN, Blocks.POPLAR_HANGING_SIGN, Blocks.POPLAR_WALL_HANGING_SIGN);
   public static final BlockEntityType MOB_SPAWNER = register(BlockEntityTypeIds.MOB_SPAWNER, SpawnerBlockEntity::new, Blocks.SPAWNER);
   public static final BlockEntityType CREAKING_HEART = register(BlockEntityTypeIds.CREAKING_HEART, CreakingHeartBlockEntity::new, Blocks.CREAKING_HEART);
   public static final BlockEntityType PISTON = register(BlockEntityTypeIds.PISTON, PistonMovingBlockEntity::new, Blocks.MOVING_PISTON);
   public static final BlockEntityType BREWING_STAND = register(BlockEntityTypeIds.BREWING_STAND, BrewingStandBlockEntity::new, Blocks.BREWING_STAND);
   public static final BlockEntityType ENCHANTING_TABLE = register(BlockEntityTypeIds.ENCHANTING_TABLE, EnchantingTableBlockEntity::new, Blocks.ENCHANTING_TABLE);
   public static final BlockEntityType END_PORTAL = register(BlockEntityTypeIds.END_PORTAL, TheEndPortalBlockEntity::new, Blocks.END_PORTAL);
   public static final BlockEntityType BEACON = register(BlockEntityTypeIds.BEACON, BeaconBlockEntity::new, Blocks.BEACON);
   public static final BlockEntityType SKULL = register(BlockEntityTypeIds.SKULL, SkullBlockEntity::new, Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD);
   public static final BlockEntityType DAYLIGHT_DETECTOR = register(BlockEntityTypeIds.DAYLIGHT_DETECTOR, DaylightDetectorBlockEntity::new, Blocks.DAYLIGHT_DETECTOR);
   public static final BlockEntityType HOPPER = register(BlockEntityTypeIds.HOPPER, HopperBlockEntity::new, Blocks.HOPPER);
   public static final BlockEntityType COMPARATOR = register(BlockEntityTypeIds.COMPARATOR, ComparatorBlockEntity::new, Blocks.COMPARATOR);
   public static final BlockEntityType BANNER = register(BlockEntityTypeIds.BANNER, BannerBlockEntity::new, Util.join(Blocks.BANNER.asList(), Blocks.WALL_BANNER.asList()));
   public static final BlockEntityType STRUCTURE_BLOCK = register(BlockEntityTypeIds.STRUCTURE_BLOCK, StructureBlockEntity::new, Blocks.STRUCTURE_BLOCK);
   public static final BlockEntityType END_GATEWAY = register(BlockEntityTypeIds.END_GATEWAY, TheEndGatewayBlockEntity::new, Blocks.END_GATEWAY);
   public static final BlockEntityType COMMAND_BLOCK = register(BlockEntityTypeIds.COMMAND_BLOCK, CommandBlockEntity::new, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK);
   public static final BlockEntityType SHULKER_BOX = register(BlockEntityTypeIds.SHULKER_BOX, ShulkerBoxBlockEntity::new, Util.copyAndAdd(Blocks.DYED_SHULKER_BOX.asList(), (Object)Blocks.SHULKER_BOX));
   public static final BlockEntityType CONDUIT = register(BlockEntityTypeIds.CONDUIT, ConduitBlockEntity::new, Blocks.CONDUIT);
   public static final BlockEntityType BARREL = register(BlockEntityTypeIds.BARREL, BarrelBlockEntity::new, Blocks.BARREL);
   public static final BlockEntityType SMOKER = register(BlockEntityTypeIds.SMOKER, SmokerBlockEntity::new, Blocks.SMOKER);
   public static final BlockEntityType BLAST_FURNACE = register(BlockEntityTypeIds.BLAST_FURNACE, BlastFurnaceBlockEntity::new, Blocks.BLAST_FURNACE);
   public static final BlockEntityType LECTERN = register(BlockEntityTypeIds.LECTERN, LecternBlockEntity::new, Blocks.LECTERN);
   public static final BlockEntityType BELL = register(BlockEntityTypeIds.BELL, BellBlockEntity::new, Blocks.BELL);
   public static final BlockEntityType JIGSAW = register(BlockEntityTypeIds.JIGSAW, JigsawBlockEntity::new, Blocks.JIGSAW);
   public static final BlockEntityType CAMPFIRE = register(BlockEntityTypeIds.CAMPFIRE, CampfireBlockEntity::new, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
   public static final BlockEntityType BEEHIVE = register(BlockEntityTypeIds.BEEHIVE, BeehiveBlockEntity::new, Blocks.BEE_NEST, Blocks.BEEHIVE);
   public static final BlockEntityType SCULK_SENSOR = register(BlockEntityTypeIds.SCULK_SENSOR, SculkSensorBlockEntity::new, Blocks.SCULK_SENSOR);
   public static final BlockEntityType CALIBRATED_SCULK_SENSOR = register(BlockEntityTypeIds.CALIBRATED_SCULK_SENSOR, CalibratedSculkSensorBlockEntity::new, Blocks.CALIBRATED_SCULK_SENSOR);
   public static final BlockEntityType SCULK_CATALYST = register(BlockEntityTypeIds.SCULK_CATALYST, SculkCatalystBlockEntity::new, Blocks.SCULK_CATALYST);
   public static final BlockEntityType SCULK_SHRIEKER = register(BlockEntityTypeIds.SCULK_SHRIEKER, SculkShriekerBlockEntity::new, Blocks.SCULK_SHRIEKER);
   public static final BlockEntityType CHISELED_BOOKSHELF = register(BlockEntityTypeIds.CHISELED_BOOKSHELF, ChiseledBookShelfBlockEntity::new, Blocks.CHISELED_BOOKSHELF);
   public static final BlockEntityType SHELF = register(BlockEntityTypeIds.SHELF, ShelfBlockEntity::new, Blocks.ACACIA_SHELF, Blocks.BAMBOO_SHELF, Blocks.BIRCH_SHELF, Blocks.CHERRY_SHELF, Blocks.CRIMSON_SHELF, Blocks.DARK_OAK_SHELF, Blocks.JUNGLE_SHELF, Blocks.MANGROVE_SHELF, Blocks.OAK_SHELF, Blocks.PALE_OAK_SHELF, Blocks.SPRUCE_SHELF, Blocks.WARPED_SHELF, Blocks.POPLAR_SHELF);
   public static final BlockEntityType BRUSHABLE_BLOCK = register(BlockEntityTypeIds.BRUSHABLE_BLOCK, BrushableBlockEntity::new, Blocks.SUSPICIOUS_SAND, Blocks.SUSPICIOUS_GRAVEL);
   public static final BlockEntityType DECORATED_POT = register(BlockEntityTypeIds.DECORATED_POT, DecoratedPotBlockEntity::new, Blocks.DECORATED_POT);
   public static final BlockEntityType CRAFTER = register(BlockEntityTypeIds.CRAFTER, CrafterBlockEntity::new, Blocks.CRAFTER);
   public static final BlockEntityType TRIAL_SPAWNER = register(BlockEntityTypeIds.TRIAL_SPAWNER, TrialSpawnerBlockEntity::new, Blocks.TRIAL_SPAWNER);
   public static final BlockEntityType VAULT = register(BlockEntityTypeIds.VAULT, VaultBlockEntity::new, Blocks.VAULT);
   public static final BlockEntityType TEST_BLOCK = register(BlockEntityTypeIds.TEST_BLOCK, TestBlockEntity::new, Blocks.TEST_BLOCK);
   public static final BlockEntityType TEST_INSTANCE_BLOCK = register(BlockEntityTypeIds.TEST_INSTANCE_BLOCK, TestInstanceBlockEntity::new, Blocks.TEST_INSTANCE_BLOCK);
   public static final BlockEntityType COPPER_GOLEM_STATUE = register(BlockEntityTypeIds.COPPER_GOLEM_STATUE, CopperGolemStatueBlockEntity::new, Blocks.COPPER_GOLEM_STATUE.asList());
   public static final BlockEntityType POTENT_SULFUR = register(BlockEntityTypeIds.POTENT_SULFUR, PotentSulfurBlockEntity::new, Blocks.POTENT_SULFUR);
   static final Set OP_ONLY_CUSTOM_DATA = Set.of(COMMAND_BLOCK, LECTERN, SIGN, HANGING_SIGN, MOB_SPAWNER, TRIAL_SPAWNER);

   private static BlockEntityType register(final ResourceKey key, final BlockEntityType.BlockEntitySupplier factory, final Block... validBlocks) {
      Identifier id = key.identifier();
      if (validBlocks.length == 0) {
         LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", id);
      }

      if (id.getNamespace().equals("minecraft")) {
         Util.fetchChoiceType(References.BLOCK_ENTITY, id.getPath());
      }

      return (BlockEntityType)Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key, new BlockEntityType(factory, Set.of(validBlocks)));
   }

   private static BlockEntityType register(final ResourceKey id, final BlockEntityType.BlockEntitySupplier factory, final List validBlocks) {
      return register(id, factory, (Block[])validBlocks.toArray(new Block[0]));
   }
}
