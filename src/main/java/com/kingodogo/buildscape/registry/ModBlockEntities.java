package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.sign.BambooSignBlockEntity;
import com.kingodogo.buildscape.block.BuildersWorkbenchBlockEntity;
import com.kingodogo.buildscape.block.water.CascadeBlockEntity;
import com.kingodogo.buildscape.block.storage.DecoratedPotBlockEntity;
import com.kingodogo.buildscape.block.decor.FestiveStockingBlockEntity;
import com.kingodogo.buildscape.block.storage.GlassJarBlockEntity;
import com.kingodogo.buildscape.block.light.GlowLightsBlockEntity;
import com.kingodogo.buildscape.block.sign.MangroveSignBlockEntity;
import com.kingodogo.buildscape.block.decor.MuffBlockEntity;
import com.kingodogo.buildscape.block.storage.ShelfBlockEntity;
import com.kingodogo.buildscape.block.decor.SmokeVentBlockEntity;
import com.kingodogo.buildscape.block.storage.TrappedDecoratedPotBlockEntity;
import com.kingodogo.buildscape.feature.copper.CopperChestBlockEntity;
import com.kingodogo.buildscape.feature.geyser.PotentSulfurBlockEntity;
import com.kingodogo.buildscape.feature.icicle.IcicleCauldronBlockEntity;
import com.kingodogo.buildscape.feature.pillar.PillarBlockEntity;
import com.kingodogo.buildscape.feature.pipe.HollowLogBlockEntity;
import com.kingodogo.buildscape.feature.sculk.SculkCatalystBlockEntity;
import com.kingodogo.buildscape.feature.trophy.Trophies;
import com.kingodogo.buildscape.feature.trophy.TrophyBlockEntity;
import com.kingodogo.buildscape.feature.trophy.TrophyDefinition;
import com.kingodogo.buildscape.registry.blocks.CardboardBlocks;
import com.kingodogo.buildscape.registry.blocks.CopperBlocks;
import com.kingodogo.buildscape.registry.blocks.DecorBlocks;
import com.kingodogo.buildscape.registry.blocks.MasonryBlocks;
import com.kingodogo.buildscape.registry.blocks.SandstoneBlocks;
import com.kingodogo.buildscape.registry.blocks.ShapedStoneBlocks;
import com.kingodogo.buildscape.registry.blocks.ShapedWoodBlocks;
import com.kingodogo.buildscape.registry.blocks.SteelBlocks;
import com.kingodogo.buildscape.registry.blocks.StoneBlocks;
import com.kingodogo.buildscape.registry.blocks.WoodBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

/**
 * Deferred register for the mod's block entity types (sculk catalyst, signs, pillars, workbench, shelf, trophy,
 * hollow log and others), each bound to the blocks that may own it.
 */
public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(ForgeRegistries.BLOCK_ENTITIES, BuildScape.MODID);
    public static final RegistryObject<BlockEntityType<SculkCatalystBlockEntity>> SCULK_CATALYST_BLOCK_ENTITY = BLOCK_ENTITIES
            .register("sculk_catalyst", () -> BlockEntityType.Builder.of(
                    SculkCatalystBlockEntity::new, CopperBlocks.SCULK_CATALYST.get()).build(null));
    public static final RegistryObject<BlockEntityType<MangroveSignBlockEntity>> MANGROVE_SIGN_BLOCK_ENTITY = BLOCK_ENTITIES
            .register(
                    "mangrove_sign_block_entity",
                    () -> BlockEntityType.Builder.of(
                            MangroveSignBlockEntity::new,
                            DecorBlocks.MANGROVE_SIGN.get(),
                            DecorBlocks.MANGROVE_WALL_SIGN.get()).build(null));
    public static final RegistryObject<BlockEntityType<BambooSignBlockEntity>> BAMBOO_SIGN_BLOCK_ENTITY = BLOCK_ENTITIES
            .register(
                    "bamboo_sign_block_entity",
                    () -> BlockEntityType.Builder.of(
                            BambooSignBlockEntity::new,
                            WoodBlocks.BAMBOO_SIGN.get(),
                            WoodBlocks.BAMBOO_WALL_SIGN.get()).build(null));
    public static final RegistryObject<BlockEntityType<PillarBlockEntity>> PILLAR_BLOCK_ENTITY = BLOCK_ENTITIES
            .register("pillar_block_entity", () -> BlockEntityType.Builder.of(
                    PillarBlockEntity::new,
                    StoneBlocks.QUARTZ_PILLAR.get(),
                    StoneBlocks.STONE_PILLAR.get(),
                    StoneBlocks.DEEPSLATE_PILLAR.get(),
                    StoneBlocks.MOSSY_PILLAR.get(),
                    StoneBlocks.AMETHYST_PILLAR.get(),
                    StoneBlocks.ANDESITE_PILLAR.get(),
                    StoneBlocks.BASALT_PILLAR.get(),
                    StoneBlocks.BLACKSTONE_PILLAR.get(),
                    StoneBlocks.BLUE_ICE_PILLAR.get(),
                    StoneBlocks.CALCITE_PILLAR.get(),
                    StoneBlocks.CINNABAR_PILLAR.get(),
                    StoneBlocks.COPPER_PILLAR.get(),
                    StoneBlocks.DARK_PRISMARINE_PILLAR.get(),
                    StoneBlocks.DIORITE_PILLAR.get(),
                    StoneBlocks.DRIPSTONE_PILLAR.get(),
                    StoneBlocks.EXPOSED_COPPER_PILLAR.get(),
                    StoneBlocks.GRANITE_PILLAR.get(),
                    StoneBlocks.NETHERRACK_PILLAR.get(),
                    StoneBlocks.OBSIDIAN_PILLAR.get(),
                    StoneBlocks.OXIDIZED_COPPER_PILLAR.get(),
                    StoneBlocks.PACKED_MUD_PILLAR.get(),
                    StoneBlocks.PRISMARINE_PILLAR.get(),
                    StoneBlocks.SCULK_PILLAR.get(),
                    StoneBlocks.SULFUR_PILLAR.get(),
                    StoneBlocks.TUFF_PILLAR.get(),
                    StoneBlocks.WEATHERED_COPPER_PILLAR.get(),
                    StoneBlocks.ASHENKING_DIAMOND_PILLAR.get(),
                    StoneBlocks.ASHENKING_GOLD_PILLAR.get(),
                    StoneBlocks.ASHENKING_EMERALD_PILLAR.get(),
                    StoneBlocks.ASHENKING_NETHERITE_PILLAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<DecoratedPotBlockEntity>> DECORATED_POT_BLOCK_ENTITY = BLOCK_ENTITIES
            .register(
                    "decorated_pot_block_entity",
                    () -> BlockEntityType.Builder.of(
                            DecoratedPotBlockEntity::new,
                            StoneBlocks.DECORATED_POT.get(),
                            StoneBlocks.BLACK_DECORATED_POT.get(),
                            StoneBlocks.BLUE_DECORATED_POT.get(),
                            StoneBlocks.BROWN_DECORATED_POT.get(),
                            StoneBlocks.CYAN_DECORATED_POT.get(),
                            WoodBlocks.GRAY_DECORATED_POT.get(),
                            WoodBlocks.GREEN_DECORATED_POT.get(),
                            WoodBlocks.LIGHT_BLUE_DECORATED_POT.get(),
                            WoodBlocks.LIGHT_GRAY_DECORATED_POT.get(),
                            WoodBlocks.LIME_DECORATED_POT.get(),
                            WoodBlocks.MAGENTA_DECORATED_POT.get(),
                            WoodBlocks.ORANGE_DECORATED_POT.get(),
                            WoodBlocks.PINK_DECORATED_POT.get(),
                            WoodBlocks.PURPLE_DECORATED_POT.get(),
                            WoodBlocks.RED_DECORATED_POT.get(),
                            WoodBlocks.WHITE_DECORATED_POT.get(),
                            WoodBlocks.YELLOW_DECORATED_POT.get()).build(null));

    public static final RegistryObject<BlockEntityType<TrappedDecoratedPotBlockEntity>> TRAPPED_DECORATED_POT_BLOCK_ENTITY = BLOCK_ENTITIES
            .register(
                    "trapped_decorated_pot_block_entity",
                    () -> BlockEntityType.Builder.of(
                            TrappedDecoratedPotBlockEntity::new,
                            WoodBlocks.TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.BLACK_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.BLUE_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.BROWN_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.CYAN_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.GRAY_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.GREEN_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.LIGHT_BLUE_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.LIGHT_GRAY_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.LIME_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.MAGENTA_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.ORANGE_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.PINK_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.PURPLE_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.RED_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.WHITE_TRAPPED_DECORATED_POT.get(),
                            WoodBlocks.YELLOW_TRAPPED_DECORATED_POT.get()).build(null));

    public static final RegistryObject<BlockEntityType<IcicleCauldronBlockEntity>> ICICLE_CAULDRON_BLOCK_ENTITY = BLOCK_ENTITIES
            .register(
                    "icicle_cauldron_block_entity",
                    () -> BlockEntityType.Builder.of(
                            IcicleCauldronBlockEntity::new,
                            DecorBlocks.ICICLE_CAULDRON.get()).build(null));

    public static final RegistryObject<BlockEntityType<FestiveStockingBlockEntity>> FESTIVE_STOCKING_BLOCK_ENTITY = BLOCK_ENTITIES
            .register(
                    "festive_stocking_block_entity",
                    () -> BlockEntityType.Builder.of(
                            FestiveStockingBlockEntity::new,
                            DecorBlocks.FESTIVE_STOCKING.get(),
                            DecorBlocks.BLACK_FESTIVE_STOCKING.get(),
                            DecorBlocks.BLUE_FESTIVE_STOCKING.get(),
                            DecorBlocks.BROWN_FESTIVE_STOCKING.get(),
                            DecorBlocks.CYAN_FESTIVE_STOCKING.get(),
                            DecorBlocks.GRAY_FESTIVE_STOCKING.get(),
                            DecorBlocks.GREEN_FESTIVE_STOCKING.get(),
                            DecorBlocks.LIGHT_BLUE_FESTIVE_STOCKING.get(),
                            DecorBlocks.LIGHT_GRAY_FESTIVE_STOCKING.get(),
                            DecorBlocks.LIME_FESTIVE_STOCKING.get(),
                            DecorBlocks.MAGENTA_FESTIVE_STOCKING.get(),
                            DecorBlocks.ORANGE_FESTIVE_STOCKING.get(),
                            DecorBlocks.PINK_FESTIVE_STOCKING.get(),
                            DecorBlocks.PURPLE_FESTIVE_STOCKING.get(),
                            DecorBlocks.RED_FESTIVE_STOCKING.get(),
                            DecorBlocks.WHITE_FESTIVE_STOCKING.get(),
                            DecorBlocks.YELLOW_FESTIVE_STOCKING.get()).build(null));

    public static final RegistryObject<BlockEntityType<GlowLightsBlockEntity>> GLOW_LIGHTS_BLOCK_ENTITY = BLOCK_ENTITIES
            .register(
                    "glow_lights_block_entity",
                    () -> BlockEntityType.Builder.of(
                            GlowLightsBlockEntity::new,
                            DecorBlocks.GLOW_LIGHTS.get()).build(null));

    public static final RegistryObject<BlockEntityType<SmokeVentBlockEntity>> SMOKE_VENT_BLOCK_ENTITY = BLOCK_ENTITIES
            .register(
                    "smoke_vent_block_entity",
                    () -> BlockEntityType.Builder.of(
                            SmokeVentBlockEntity::new,
                            MasonryBlocks.SMOKE_VENT.get()).build(null));

    public static final RegistryObject<BlockEntityType<CascadeBlockEntity>> CASCADE_BLOCK_ENTITY = BLOCK_ENTITIES
            .register(
                    "cascade_block_entity",
                    () -> BlockEntityType.Builder.of(
                            CascadeBlockEntity::new,
                            MasonryBlocks.CASCADE_BLOCK.get(),
                            MasonryBlocks.CASCADE_BLOCK_NO_MIST.get()).build(null));

    public static final RegistryObject<BlockEntityType<MuffBlockEntity>> MUFF_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "muff_block_entity",
            () -> BlockEntityType.Builder.of(
                    MuffBlockEntity::new,
                    ShapedWoodBlocks.MUFF_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<GlassJarBlockEntity>> GLASS_JAR_BLOCK_ENTITY = BLOCK_ENTITIES
            .register(
                    "glass_jar_block_entity",
                    () -> BlockEntityType.Builder.of(
                            GlassJarBlockEntity::new,
                            CopperBlocks.GLASS_JAR.get(),
                            CopperBlocks.WHITE_GLASS_JAR.get(),
                            CopperBlocks.ORANGE_GLASS_JAR.get(),
                            CopperBlocks.MAGENTA_GLASS_JAR.get(),
                            CopperBlocks.LIGHT_BLUE_GLASS_JAR.get(),
                            CopperBlocks.YELLOW_GLASS_JAR.get(),
                            CopperBlocks.LIME_GLASS_JAR.get(),
                            CopperBlocks.PINK_GLASS_JAR.get(),
                            CopperBlocks.GRAY_GLASS_JAR.get(),
                            CopperBlocks.LIGHT_GRAY_GLASS_JAR.get(),
                            CopperBlocks.CYAN_GLASS_JAR.get(),
                            CopperBlocks.PURPLE_GLASS_JAR.get(),
                            CopperBlocks.BLUE_GLASS_JAR.get(),
                            CopperBlocks.BROWN_GLASS_JAR.get(),
                            CopperBlocks.GREEN_GLASS_JAR.get(),
                            CopperBlocks.RED_GLASS_JAR.get(),
                            CopperBlocks.BLACK_GLASS_JAR.get(),
                            CopperBlocks.TINTED_GLASS_JAR.get(),
                            CopperBlocks.GOLDEN_JAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<BuildersWorkbenchBlockEntity>> BUILDERS_WORKBENCH_BE = BLOCK_ENTITIES
            .register("builders_workbench",
                    () -> BlockEntityType.Builder.of(
                            BuildersWorkbenchBlockEntity::new,
                            SandstoneBlocks.BUILDERS_WORKBENCH.get()).build(null));

    public static final RegistryObject<BlockEntityType<CopperChestBlockEntity>> COPPER_CHEST = BLOCK_ENTITIES.register(
            "copper_chest",
            () -> BlockEntityType.Builder.of(
                    CopperChestBlockEntity::new,
                    CopperBlocks.COPPER_CHEST.get(),
                    CopperBlocks.EXPOSED_COPPER_CHEST.get(),
                    CopperBlocks.WEATHERED_COPPER_CHEST.get(),
                    CopperBlocks.OXIDIZED_COPPER_CHEST.get(),
                    CopperBlocks.WAXED_COPPER_CHEST.get(),
                    CopperBlocks.WAXED_EXPOSED_COPPER_CHEST.get(),
                    CopperBlocks.WAXED_WEATHERED_COPPER_CHEST.get(),
                    CopperBlocks.WAXED_OXIDIZED_COPPER_CHEST.get()).build(null));

    public static final RegistryObject<BlockEntityType<PotentSulfurBlockEntity>> POTENT_SULFUR = BLOCK_ENTITIES
            .register("potent_sulfur",
                    () -> BlockEntityType.Builder.of(
                            PotentSulfurBlockEntity::new,
                            ShapedStoneBlocks.POTENT_SULFUR.get()).build(null));

    public static final RegistryObject<BlockEntityType<ShelfBlockEntity>> SHELF = BLOCK_ENTITIES.register("shelf",
            () -> BlockEntityType.Builder.of(
                    ShelfBlockEntity::new,
                    CardboardBlocks.OAK_SHELF.get(),
                    CardboardBlocks.SPRUCE_SHELF.get(),
                    CardboardBlocks.BIRCH_SHELF.get(),
                    CardboardBlocks.JUNGLE_SHELF.get(),
                    CardboardBlocks.ACACIA_SHELF.get(),
                    CardboardBlocks.DARK_OAK_SHELF.get(),
                    CardboardBlocks.MANGROVE_SHELF.get(),
                    CardboardBlocks.CHERRY_SHELF.get(),
                    CardboardBlocks.CRIMSON_SHELF.get(),
                    CardboardBlocks.WARPED_SHELF.get(),
                    CardboardBlocks.BAMBOO_SHELF.get(),
                    CardboardBlocks.STRIPPED_BAMBOO_SHELF.get(),
                    CardboardBlocks.PALE_OAK_SHELF.get(),
                    CardboardBlocks.POPLAR_SHELF.get()).build(null));

    public static final RegistryObject<BlockEntityType<TrophyBlockEntity>> TROPHY_BLOCK_ENTITY = BLOCK_ENTITIES
            .register("trophy_block_entity",
                    () -> BlockEntityType.Builder.of(
                            TrophyBlockEntity::new,
                            Trophies.getAll().stream()
                                    .map(TrophyDefinition::getBlock)
                                    .filter(Objects::nonNull)
                                    .toArray(Block[]::new))
                            .build(null));

    public static final RegistryObject<BlockEntityType<HollowLogBlockEntity>> HOLLOW_LOG_BLOCK_ENTITY = BLOCK_ENTITIES
            .register("hollow_log",
                    () -> BlockEntityType.Builder.of(
                            HollowLogBlockEntity::new,
                            SteelBlocks.HOLLOW_OAK_LOG.get(),
                            CardboardBlocks.STRIPPED_HOLLOW_OAK_LOG.get(),
                            CardboardBlocks.HOLLOW_SPRUCE_LOG.get(),
                            CardboardBlocks.STRIPPED_HOLLOW_SPRUCE_LOG.get(),
                            CardboardBlocks.HOLLOW_BIRCH_LOG.get(),
                            CardboardBlocks.STRIPPED_HOLLOW_BIRCH_LOG.get(),
                            CardboardBlocks.HOLLOW_JUNGLE_LOG.get(),
                            CardboardBlocks.STRIPPED_HOLLOW_JUNGLE_LOG.get(),
                            CardboardBlocks.HOLLOW_ACACIA_LOG.get(),
                            CardboardBlocks.STRIPPED_HOLLOW_ACACIA_LOG.get(),
                            CardboardBlocks.HOLLOW_DARK_OAK_LOG.get(),
                            CardboardBlocks.STRIPPED_HOLLOW_DARK_OAK_LOG.get(),
                            CardboardBlocks.HOLLOW_CRIMSON_STEM.get(),
                            CardboardBlocks.STRIPPED_HOLLOW_CRIMSON_STEM.get(),
                            CardboardBlocks.HOLLOW_WARPED_STEM.get(),
                            CardboardBlocks.STRIPPED_HOLLOW_WARPED_STEM.get(),
                            CardboardBlocks.HOLLOW_MANGROVE_LOG.get(),
                            CardboardBlocks.STRIPPED_HOLLOW_MANGROVE_LOG.get(),
                            CardboardBlocks.HOLLOW_ASHPEN_LOG.get(),
                            CardboardBlocks.STRIPPED_HOLLOW_ASHPEN_LOG.get(),
                            CardboardBlocks.HOLLOW_POPLAR_LOG.get(),
                            CardboardBlocks.STRIPPED_HOLLOW_POPLAR_LOG.get(),
                            CardboardBlocks.HOLLOW_PALE_OAK_LOG.get(),
                            CardboardBlocks.STRIPPED_HOLLOW_PALE_OAK_LOG.get(),
                            CardboardBlocks.HOLLOW_CHERRY_LOG.get(),
                            CardboardBlocks.STRIPPED_HOLLOW_CHERRY_LOG.get(),
                            DecorBlocks.HOLLOW_STEEL_PIPE.get()).build(null));
}
