package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
                        .create(ForgeRegistries.BLOCK_ENTITIES, BuildScape.MODID);

        public static final RegistryObject<BlockEntityType<PillarBlockEntity>> PILLAR_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register("pillar_block_entity", () -> BlockEntityType.Builder.of(
                                        PillarBlockEntity::new,
                                        ModBlocks.QUARTZ_PILLAR.get(),
                                        ModBlocks.STONE_PILLAR.get(),
                                        ModBlocks.DEEPSLATE_PILLAR.get(),
                                        ModBlocks.MOSSY_PILLAR.get()).build(null));

        public static final RegistryObject<BlockEntityType<DecoratedPotBlockEntity>> DECORATED_POT_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register(
                                        "decorated_pot_block_entity",
                                        () -> BlockEntityType.Builder.of(
                                                        DecoratedPotBlockEntity::new,
                                                        ModBlocks.DECORATED_POT.get(),
                                                        ModBlocks.BLACK_DECORATED_POT.get(),
                                                        ModBlocks.BLUE_DECORATED_POT.get(),
                                                        ModBlocks.BROWN_DECORATED_POT.get(),
                                                        ModBlocks.CYAN_DECORATED_POT.get(),
                                                        ModBlocks.GRAY_DECORATED_POT.get(),
                                                        ModBlocks.GREEN_DECORATED_POT.get(),
                                                        ModBlocks.LIGHT_BLUE_DECORATED_POT.get(),
                                                        ModBlocks.LIGHT_GRAY_DECORATED_POT.get(),
                                                        ModBlocks.LIME_DECORATED_POT.get(),
                                                        ModBlocks.MAGENTA_DECORATED_POT.get(),
                                                        ModBlocks.ORANGE_DECORATED_POT.get(),
                                                        ModBlocks.PINK_DECORATED_POT.get(),
                                                        ModBlocks.PURPLE_DECORATED_POT.get(),
                                                        ModBlocks.RED_DECORATED_POT.get(),
                                                        ModBlocks.WHITE_DECORATED_POT.get(),
                                                        ModBlocks.YELLOW_DECORATED_POT.get()).build(null));

        public static final RegistryObject<BlockEntityType<IcicleCauldronBlockEntity>> ICICLE_CAULDRON_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register(
                                        "icicle_cauldron_block_entity",
                                        () -> BlockEntityType.Builder.of(
                                                        IcicleCauldronBlockEntity::new,
                                                        ModBlocks.ICICLE_CAULDRON.get()).build(null));

        public static final RegistryObject<BlockEntityType<FestiveStockingBlockEntity>> FESTIVE_STOCKING_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register(
                                        "festive_stocking_block_entity",
                                        () -> BlockEntityType.Builder.of(
                                                        FestiveStockingBlockEntity::new,
                                                        ModBlocks.FESTIVE_STOCKING.get(),
                                                        ModBlocks.BLACK_FESTIVE_STOCKING.get(),
                                                        ModBlocks.BLUE_FESTIVE_STOCKING.get(),
                                                        ModBlocks.BROWN_FESTIVE_STOCKING.get(),
                                                        ModBlocks.CYAN_FESTIVE_STOCKING.get(),
                                                        ModBlocks.GRAY_FESTIVE_STOCKING.get(),
                                                        ModBlocks.GREEN_FESTIVE_STOCKING.get(),
                                                        ModBlocks.LIGHT_BLUE_FESTIVE_STOCKING.get(),
                                                        ModBlocks.LIGHT_GRAY_FESTIVE_STOCKING.get(),
                                                        ModBlocks.LIME_FESTIVE_STOCKING.get(),
                                                        ModBlocks.MAGENTA_FESTIVE_STOCKING.get(),
                                                        ModBlocks.ORANGE_FESTIVE_STOCKING.get(),
                                                        ModBlocks.PINK_FESTIVE_STOCKING.get(),
                                                        ModBlocks.PURPLE_FESTIVE_STOCKING.get(),
                                                        ModBlocks.RED_FESTIVE_STOCKING.get(),
                                                        ModBlocks.WHITE_FESTIVE_STOCKING.get(),
                                                        ModBlocks.YELLOW_FESTIVE_STOCKING.get()).build(null));

        public static final RegistryObject<BlockEntityType<MangroveSignBlockEntity>> MANGROVE_SIGN_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register(
                                        "mangrove_sign_block_entity",
                                        () -> BlockEntityType.Builder.of(
                                                        MangroveSignBlockEntity::new,
                                                        ModBlocks.MANGROVE_SIGN.get(),
                                                        ModBlocks.MANGROVE_WALL_SIGN.get()).build(null));

        public static final RegistryObject<BlockEntityType<BambooSignBlockEntity>> BAMBOO_SIGN_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register(
                                        "bamboo_sign_block_entity",
                                        () -> BlockEntityType.Builder.of(
                                                        BambooSignBlockEntity::new,
                                                        ModBlocks.BAMBOO_SIGN.get(),
                                                        ModBlocks.BAMBOO_WALL_SIGN.get()).build(null));

        public static final RegistryObject<BlockEntityType<GlowLightsBlockEntity>> GLOW_LIGHTS_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register(
                                        "glow_lights_block_entity",
                                        () -> BlockEntityType.Builder.of(
                                                        GlowLightsBlockEntity::new,
                                                        ModBlocks.GLOW_LIGHTS.get()).build(null));

        public static final RegistryObject<BlockEntityType<ColoredItemFrameBlockEntity>> COLORED_ITEM_FRAME_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register(
                                        "colored_item_frame_block_entity",
                                        () -> BlockEntityType.Builder.of(
                                                        ColoredItemFrameBlockEntity::new,
                                                        ModBlocks.WHITE_ITEM_FRAME.get(),
                                                        ModBlocks.ORANGE_ITEM_FRAME.get(),
                                                        ModBlocks.MAGENTA_ITEM_FRAME.get(),
                                                        ModBlocks.LIGHT_BLUE_ITEM_FRAME.get(),
                                                        ModBlocks.YELLOW_ITEM_FRAME.get(),
                                                        ModBlocks.LIME_ITEM_FRAME.get(),
                                                        ModBlocks.PINK_ITEM_FRAME.get(),
                                                        ModBlocks.GRAY_ITEM_FRAME.get(),
                                                        ModBlocks.LIGHT_GRAY_ITEM_FRAME.get(),
                                                        ModBlocks.CYAN_ITEM_FRAME.get(),
                                                        ModBlocks.PURPLE_ITEM_FRAME.get(),
                                                        ModBlocks.BLUE_ITEM_FRAME.get(),
                                                        ModBlocks.BROWN_ITEM_FRAME.get(),
                                                        ModBlocks.GREEN_ITEM_FRAME.get(),
                                                        ModBlocks.RED_ITEM_FRAME.get(),
                                                        ModBlocks.BLACK_ITEM_FRAME.get()).build(null));
}
