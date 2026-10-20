package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.block.CascadeBlock;
import com.kingodogo.buildscape.block.CascadeBlockNoMist;
import com.kingodogo.buildscape.block.ModBlock;
import com.kingodogo.buildscape.block.ModSlabBlock;
import com.kingodogo.buildscape.block.ModStairBlock;
import com.kingodogo.buildscape.block.ModWallBlock;
import com.kingodogo.buildscape.block.SmokeVentBlock;
import com.kingodogo.buildscape.registry.ModSounds;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.ModBlocks.BLOCKS;

/** Part of the blocks registrations; see {@code ModBlocks}. */
public final class BlocksPart06 {

    private BlocksPart06() {
    }

    /** Forces this class to load so its registrations run in the order {@code ModBlocks} calls it. */
    public static void init() {
    }

        public static final RegistryObject<Block> RED_STAINED_BRICKS_SLAB = BLOCKS.register(
                        "red_stained_bricks_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_STAINED_BRICKS_SLAB = BLOCKS.register(
                        "orange_stained_bricks_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_STAINED_BRICKS_SLAB = BLOCKS.register(
                        "yellow_stained_bricks_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIME_STAINED_BRICKS_SLAB = BLOCKS.register(
                        "lime_stained_bricks_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_STAINED_BRICKS_SLAB = BLOCKS.register(
                        "green_stained_bricks_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> CYAN_STAINED_BRICKS_SLAB = BLOCKS.register(
                        "cyan_stained_bricks_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_BRICKS_SLAB = BLOCKS.register(
                        "light_blue_stained_bricks_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_STAINED_BRICKS_SLAB = BLOCKS.register(
                        "blue_stained_bricks_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PURPLE_STAINED_BRICKS_SLAB = BLOCKS.register(
                        "purple_stained_bricks_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> MAGENTA_STAINED_BRICKS_SLAB = BLOCKS.register(
                        "magenta_stained_bricks_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_MAGENTA)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PINK_STAINED_BRICKS_SLAB = BLOCKS.register(
                        "pink_stained_bricks_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_STAINED_BRICKS_WALL = BLOCKS.register(
                        "white_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_BRICKS_WALL = BLOCKS.register(
                        "light_gray_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GRAY_STAINED_BRICKS_WALL = BLOCKS.register(
                        "gray_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_STAINED_BRICKS_WALL = BLOCKS.register(
                        "black_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BROWN_STAINED_BRICKS_WALL = BLOCKS.register(
                        "brown_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> RED_STAINED_BRICKS_WALL = BLOCKS.register(
                        "red_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_STAINED_BRICKS_WALL = BLOCKS.register(
                        "orange_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_STAINED_BRICKS_WALL = BLOCKS.register(
                        "yellow_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIME_STAINED_BRICKS_WALL = BLOCKS.register(
                        "lime_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_STAINED_BRICKS_WALL = BLOCKS.register(
                        "green_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> CYAN_STAINED_BRICKS_WALL = BLOCKS.register(
                        "cyan_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_BRICKS_WALL = BLOCKS.register(
                        "light_blue_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_STAINED_BRICKS_WALL = BLOCKS.register(
                        "blue_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PURPLE_STAINED_BRICKS_WALL = BLOCKS.register(
                        "purple_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> MAGENTA_STAINED_BRICKS_WALL = BLOCKS.register(
                        "magenta_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_MAGENTA)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PINK_STAINED_BRICKS_WALL = BLOCKS.register(
                        "pink_stained_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> SMOOTH_STONE_STAIRS = BLOCKS.register("smooth_stone_stairs",
                        () -> new ModStairBlock(
                                        Blocks.SMOOTH_STONE.defaultBlockState(),
                                        BlockBehaviour.Properties
                                                        .copy(Blocks.SMOOTH_STONE)));
        public static final RegistryObject<Block> POLISHED_WHITE_CONCRETE = BLOCKS.register(
                        "polished_white_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_WHITE_CONCRETE_STAIRS = BLOCKS
                        .register("polished_white_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_WHITE_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_ORANGE_CONCRETE = BLOCKS.register(
                        "polished_orange_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_ORANGE_CONCRETE_STAIRS = BLOCKS
                        .register("polished_orange_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_ORANGE_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_MAGENTA_CONCRETE = BLOCKS.register(
                        "polished_magenta_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_MAGENTA)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_MAGENTA_CONCRETE_STAIRS = BLOCKS
                        .register("polished_magenta_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_MAGENTA_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_MAGENTA)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_LIGHT_BLUE_CONCRETE = BLOCKS.register(
                        "polished_light_blue_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_LIGHT_BLUE_CONCRETE_STAIRS = BLOCKS
                        .register("polished_light_blue_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_LIGHT_BLUE_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_YELLOW_CONCRETE = BLOCKS.register(
                        "polished_yellow_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_YELLOW_CONCRETE_STAIRS = BLOCKS
                        .register("polished_yellow_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_YELLOW_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_LIME_CONCRETE = BLOCKS.register(
                        "polished_lime_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_LIME_CONCRETE_STAIRS = BLOCKS
                        .register("polished_lime_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_LIME_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_PINK_CONCRETE = BLOCKS.register(
                        "polished_pink_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_PINK_CONCRETE_STAIRS = BLOCKS
                        .register("polished_pink_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_PINK_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_GRAY_CONCRETE = BLOCKS.register(
                        "polished_gray_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_GRAY_CONCRETE_STAIRS = BLOCKS
                        .register("polished_gray_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_GRAY_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_LIGHT_GRAY_CONCRETE = BLOCKS.register(
                        "polished_light_gray_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_LIGHT_GRAY_CONCRETE_STAIRS = BLOCKS
                        .register("polished_light_gray_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_LIGHT_GRAY_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_CYAN_CONCRETE = BLOCKS.register(
                        "polished_cyan_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_CYAN_CONCRETE_STAIRS = BLOCKS
                        .register("polished_cyan_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_CYAN_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_PURPLE_CONCRETE = BLOCKS.register(
                        "polished_purple_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_PURPLE_CONCRETE_STAIRS = BLOCKS
                        .register("polished_purple_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_PURPLE_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_BLUE_CONCRETE = BLOCKS.register(
                        "polished_blue_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_BLUE_CONCRETE_STAIRS = BLOCKS
                        .register("polished_blue_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_BLUE_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_BROWN_CONCRETE = BLOCKS.register(
                        "polished_brown_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_BROWN_CONCRETE_STAIRS = BLOCKS
                        .register("polished_brown_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_BROWN_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_GREEN_CONCRETE = BLOCKS.register(
                        "polished_green_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_GREEN_CONCRETE_STAIRS = BLOCKS
                        .register("polished_green_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_GREEN_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_RED_CONCRETE = BLOCKS.register(
                        "polished_red_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_RED_CONCRETE_STAIRS = BLOCKS
                        .register("polished_red_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_RED_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_BLACK_CONCRETE = BLOCKS.register(
                        "polished_black_concrete",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_BLACK_CONCRETE_STAIRS = BLOCKS
                        .register("polished_black_concrete_stairs", () -> new ModStairBlock(
                                        POLISHED_BLACK_CONCRETE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_WHITE_CONCRETE_SLAB = BLOCKS
                        .register("polished_white_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_WHITE_CONCRETE_WALL = BLOCKS
                        .register("polished_white_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_ORANGE_CONCRETE_SLAB = BLOCKS
                        .register("polished_orange_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_ORANGE_CONCRETE_WALL = BLOCKS
                        .register("polished_orange_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_MAGENTA_CONCRETE_SLAB = BLOCKS
                        .register("polished_magenta_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_MAGENTA)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_MAGENTA_CONCRETE_WALL = BLOCKS
                        .register("polished_magenta_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_MAGENTA)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_LIGHT_BLUE_CONCRETE_SLAB = BLOCKS
                        .register("polished_light_blue_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_LIGHT_BLUE_CONCRETE_WALL = BLOCKS
                        .register("polished_light_blue_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_YELLOW_CONCRETE_SLAB = BLOCKS
                        .register("polished_yellow_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_YELLOW_CONCRETE_WALL = BLOCKS
                        .register("polished_yellow_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_LIME_CONCRETE_SLAB = BLOCKS
                        .register("polished_lime_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_LIME_CONCRETE_WALL = BLOCKS
                        .register("polished_lime_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_PINK_CONCRETE_SLAB = BLOCKS
                        .register("polished_pink_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_PINK_CONCRETE_WALL = BLOCKS
                        .register("polished_pink_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_GRAY_CONCRETE_SLAB = BLOCKS
                        .register("polished_gray_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_GRAY_CONCRETE_WALL = BLOCKS
                        .register("polished_gray_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_LIGHT_GRAY_CONCRETE_SLAB = BLOCKS
                        .register("polished_light_gray_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_LIGHT_GRAY_CONCRETE_WALL = BLOCKS
                        .register("polished_light_gray_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_CYAN_CONCRETE_SLAB = BLOCKS
                        .register("polished_cyan_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_CYAN_CONCRETE_WALL = BLOCKS
                        .register("polished_cyan_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_PURPLE_CONCRETE_SLAB = BLOCKS
                        .register("polished_purple_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_PURPLE_CONCRETE_WALL = BLOCKS
                        .register("polished_purple_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_BLUE_CONCRETE_SLAB = BLOCKS
                        .register("polished_blue_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_BLUE_CONCRETE_WALL = BLOCKS
                        .register("polished_blue_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_BROWN_CONCRETE_SLAB = BLOCKS
                        .register("polished_brown_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_BROWN_CONCRETE_WALL = BLOCKS
                        .register("polished_brown_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_GREEN_CONCRETE_SLAB = BLOCKS
                        .register("polished_green_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_GREEN_CONCRETE_WALL = BLOCKS
                        .register("polished_green_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_RED_CONCRETE_SLAB = BLOCKS
                        .register("polished_red_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_RED_CONCRETE_WALL = BLOCKS
                        .register("polished_red_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_BLACK_CONCRETE_SLAB = BLOCKS
                        .register("polished_black_concrete_slab", () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_BLACK_CONCRETE_WALL = BLOCKS
                        .register("polished_black_concrete_wall", () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                                        .strength(1.8f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> SMOKE_VENT = BLOCKS.register("smoke_vent", () -> new SmokeVentBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f)
                                        .requiresCorrectToolForDrops()
                                        .sound(SoundType.NETHERITE_BLOCK)
                                        .noOcclusion()));

        public static final RegistryObject<Block> WHITE_REDSTONE_LAMP = BLOCKS.register("white_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.SNOW)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> ORANGE_REDSTONE_LAMP = BLOCKS.register("orange_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties
                                                        .of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_ORANGE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> MAGENTA_REDSTONE_LAMP = BLOCKS.register("magenta_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties
                                                        .of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_MAGENTA)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> LIGHT_BLUE_REDSTONE_LAMP = BLOCKS.register("light_blue_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties
                                                        .of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> YELLOW_REDSTONE_LAMP = BLOCKS.register("yellow_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties
                                                        .of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_YELLOW)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> LIME_REDSTONE_LAMP = BLOCKS.register("lime_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties
                                                        .of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_LIGHT_GREEN)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> PINK_REDSTONE_LAMP = BLOCKS.register("pink_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_PINK)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> GRAY_REDSTONE_LAMP = BLOCKS.register("gray_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_GRAY)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> LIGHT_GRAY_REDSTONE_LAMP = BLOCKS.register("light_gray_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties
                                                        .of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_LIGHT_GRAY)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> CYAN_REDSTONE_LAMP = BLOCKS.register("cyan_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_CYAN)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> PURPLE_REDSTONE_LAMP = BLOCKS.register("purple_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties
                                                        .of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_PURPLE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> BLUE_REDSTONE_LAMP = BLOCKS.register("blue_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_BLUE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> BROWN_REDSTONE_LAMP = BLOCKS.register("brown_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties
                                                        .of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_BROWN)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> GREEN_REDSTONE_LAMP = BLOCKS.register("green_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties
                                                        .of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_GREEN)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> RED_REDSTONE_LAMP = BLOCKS.register("red_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_RED)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> BLACK_REDSTONE_LAMP = BLOCKS.register("black_redstone_lamp",
                        () -> new RedstoneLampBlock(
                                        BlockBehaviour.Properties
                                                        .of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_BLACK)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));

        public static final RegistryObject<Block> CASCADE_BLOCK = BLOCKS.register("cascade_block",
                        () -> new CascadeBlock(
                                        BlockBehaviour.Properties.of(Material.ICE_SOLID)
                                                        .strength(0.5f)
                                                        .sound(SoundType.CORAL_BLOCK)
                                                        .noOcclusion()
                                                        .isViewBlocking((state, getter, pos) -> false)));

        public static final RegistryObject<Block> CASCADE_BLOCK_NO_MIST = BLOCKS.register("cascade_block_no_mist",
                        () -> new CascadeBlockNoMist(
                                        BlockBehaviour.Properties.of(Material.ICE_SOLID)
                                                        .strength(0.5f)
                                                        .sound(SoundType.CORAL_BLOCK)
                                                        .noOcclusion()
                                                        .isViewBlocking((state, getter, pos) -> false)));
        public static final RegistryObject<Block> BLACK_FACTORY_MESH = BLOCKS.register("black_factory_mesh",
                        () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> BLACK_FACTORY_MESH_STAIRS = BLOCKS.register(
                        "black_factory_mesh_stairs",
                        () -> new ModStairBlock(BLACK_FACTORY_MESH.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops()
                                                        .noOcclusion()
                                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> BLACK_FACTORY_MESH_SLAB = BLOCKS.register("black_factory_mesh_slab",
                        () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> BLACK_FACTORY_MESH_WALL = BLOCKS.register("black_factory_mesh_wall",
                        () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> BLUE_FACTORY_MESH = BLOCKS.register("blue_factory_mesh",
                        () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> BLUE_FACTORY_MESH_STAIRS = BLOCKS.register("blue_factory_mesh_stairs",
                        () -> new ModStairBlock(BLUE_FACTORY_MESH.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops()
                                                        .noOcclusion()
                                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> BLUE_FACTORY_MESH_SLAB = BLOCKS.register("blue_factory_mesh_slab",
                        () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> BLUE_FACTORY_MESH_WALL = BLOCKS.register("blue_factory_mesh_wall",
                        () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> BROWN_FACTORY_MESH = BLOCKS.register("brown_factory_mesh",
                        () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> BROWN_FACTORY_MESH_STAIRS = BLOCKS.register(
                        "brown_factory_mesh_stairs",
                        () -> new ModStairBlock(BROWN_FACTORY_MESH.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops()
                                                        .noOcclusion()
                                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> BROWN_FACTORY_MESH_SLAB = BLOCKS.register("brown_factory_mesh_slab",
                        () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> BROWN_FACTORY_MESH_WALL = BLOCKS.register("brown_factory_mesh_wall",
                        () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> GRAY_FACTORY_MESH = BLOCKS.register("gray_factory_mesh",
                        () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> GRAY_FACTORY_MESH_STAIRS = BLOCKS.register("gray_factory_mesh_stairs",
                        () -> new ModStairBlock(GRAY_FACTORY_MESH.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops()
                                                        .noOcclusion()
                                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> GRAY_FACTORY_MESH_SLAB = BLOCKS.register("gray_factory_mesh_slab",
                        () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> GRAY_FACTORY_MESH_WALL = BLOCKS.register("gray_factory_mesh_wall",
                        () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> GREEN_FACTORY_MESH = BLOCKS.register("green_factory_mesh",
                        () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> GREEN_FACTORY_MESH_STAIRS = BLOCKS.register(
                        "green_factory_mesh_stairs",
                        () -> new ModStairBlock(GREEN_FACTORY_MESH.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops()
                                                        .noOcclusion()
                                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> GREEN_FACTORY_MESH_SLAB = BLOCKS.register("green_factory_mesh_slab",
                        () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> GREEN_FACTORY_MESH_WALL = BLOCKS.register("green_factory_mesh_wall",
                        () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> LIGHT_GRAY_FACTORY_MESH = BLOCKS.register("light_gray_factory_mesh",
                        () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> LIGHT_GRAY_FACTORY_MESH_STAIRS = BLOCKS.register(
                        "light_gray_factory_mesh_stairs",
                        () -> new ModStairBlock(LIGHT_GRAY_FACTORY_MESH.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops()
                                                        .noOcclusion()
                                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> LIGHT_GRAY_FACTORY_MESH_SLAB = BLOCKS.register(
                        "light_gray_factory_mesh_slab",
                        () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> LIGHT_GRAY_FACTORY_MESH_WALL = BLOCKS.register(
                        "light_gray_factory_mesh_wall",
                        () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> LIME_FACTORY_MESH = BLOCKS.register("lime_factory_mesh",
                        () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> LIME_FACTORY_MESH_STAIRS = BLOCKS.register("lime_factory_mesh_stairs",
                        () -> new ModStairBlock(LIME_FACTORY_MESH.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops()
                                                        .noOcclusion()
                                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> LIME_FACTORY_MESH_SLAB = BLOCKS.register("lime_factory_mesh_slab",
                        () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> LIME_FACTORY_MESH_WALL = BLOCKS.register("lime_factory_mesh_wall",
                        () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> ORANGE_FACTORY_MESH = BLOCKS.register("orange_factory_mesh",
                        () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> ORANGE_FACTORY_MESH_STAIRS = BLOCKS.register(
                        "orange_factory_mesh_stairs",
                        () -> new ModStairBlock(ORANGE_FACTORY_MESH.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops()
                                                        .noOcclusion()
                                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> ORANGE_FACTORY_MESH_SLAB = BLOCKS.register("orange_factory_mesh_slab",
                        () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> ORANGE_FACTORY_MESH_WALL = BLOCKS.register("orange_factory_mesh_wall",
                        () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> RED_FACTORY_MESH = BLOCKS.register("red_factory_mesh",
                        () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> RED_FACTORY_MESH_STAIRS = BLOCKS.register("red_factory_mesh_stairs",
                        () -> new ModStairBlock(RED_FACTORY_MESH.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops()
                                                        .noOcclusion()
                                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> RED_FACTORY_MESH_SLAB = BLOCKS.register("red_factory_mesh_slab",
                        () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> RED_FACTORY_MESH_WALL = BLOCKS.register("red_factory_mesh_wall",
                        () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> YELLOW_FACTORY_MESH = BLOCKS.register("yellow_factory_mesh",
                        () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> YELLOW_FACTORY_MESH_STAIRS = BLOCKS.register(
                        "yellow_factory_mesh_stairs",
                        () -> new ModStairBlock(YELLOW_FACTORY_MESH.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops()
                                                        .noOcclusion()
                                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> YELLOW_FACTORY_MESH_SLAB = BLOCKS.register("yellow_factory_mesh_slab",
                        () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> YELLOW_FACTORY_MESH_WALL = BLOCKS.register("yellow_factory_mesh_wall",
                        () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                        .strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion()
                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> PACKED_MUD = BLOCKS.register("packed_mud",
                        () -> new ModBlock(BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT)
                                        .strength(1.0f).sound(ModSounds.PACKED_MUD_SOUNDS())));
        public static final RegistryObject<Block> PACKED_MUD_STAIRS = BLOCKS.register("packed_mud_stairs",
                        () -> new ModStairBlock(PACKED_MUD.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(1.0f)
                                                        .sound(ModSounds.PACKED_MUD_SOUNDS())));
        public static final RegistryObject<Block> PACKED_MUD_SLAB = BLOCKS.register("packed_mud_slab",
                        () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT)
                                        .strength(1.0f).sound(ModSounds.PACKED_MUD_SOUNDS())));
        public static final RegistryObject<Block> PACKED_MUD_WALL = BLOCKS.register("packed_mud_wall",
                        () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT)
                                        .strength(1.0f).sound(ModSounds.PACKED_MUD_SOUNDS())));

        public static final RegistryObject<Block> MUD_BRICKS = BLOCKS.register("mud_bricks",
                        () -> new ModBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIRT)
                                        .strength(1.5f, 3.0f).requiresCorrectToolForDrops()
                                        .sound(ModSounds.MUD_BRICKS_SOUNDS())));
        public static final RegistryObject<Block> MUD_BRICK_STAIRS = BLOCKS.register("mud_brick_stairs",
                        () -> new ModStairBlock(MUD_BRICKS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIRT)
                                                        .strength(1.5f, 3.0f).requiresCorrectToolForDrops()
                                                        .sound(ModSounds.MUD_BRICKS_SOUNDS())));
        public static final RegistryObject<Block> MUD_BRICK_SLAB = BLOCKS.register("mud_brick_slab",
                        () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIRT)
                                        .strength(1.5f, 3.0f).requiresCorrectToolForDrops()
                                        .sound(ModSounds.MUD_BRICKS_SOUNDS())));
        public static final RegistryObject<Block> MUD_BRICK_WALL = BLOCKS.register("mud_brick_wall",
                        () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIRT)
                                        .strength(1.5f, 3.0f).requiresCorrectToolForDrops()
                                        .sound(ModSounds.MUD_BRICKS_SOUNDS())));

        public static final RegistryObject<Block> GLASS_SLAB = BLOCKS.register(
                        "glass_slab",
                        () -> new ModSlabBlock(Blocks.GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.GLASS)));
        public static final RegistryObject<Block> WHITE_STAINED_GLASS_SLAB = BLOCKS.register(
                        "white_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.WHITE_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.WHITE_STAINED_GLASS)));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_GLASS_SLAB = BLOCKS.register(
                        "light_gray_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.LIGHT_GRAY_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_STAINED_GLASS)));
        public static final RegistryObject<Block> GRAY_STAINED_GLASS_SLAB = BLOCKS.register(
                        "gray_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.GRAY_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.GRAY_STAINED_GLASS)));
        public static final RegistryObject<Block> BLACK_STAINED_GLASS_SLAB = BLOCKS.register(
                        "black_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.BLACK_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS)));
        public static final RegistryObject<Block> BROWN_STAINED_GLASS_SLAB = BLOCKS.register(
                        "brown_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.BROWN_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.BROWN_STAINED_GLASS)));
        public static final RegistryObject<Block> RED_STAINED_GLASS_SLAB = BLOCKS.register(
                        "red_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.RED_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.RED_STAINED_GLASS)));
        public static final RegistryObject<Block> ORANGE_STAINED_GLASS_SLAB = BLOCKS.register(
                        "orange_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.ORANGE_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.ORANGE_STAINED_GLASS)));
        public static final RegistryObject<Block> YELLOW_STAINED_GLASS_SLAB = BLOCKS.register(
                        "yellow_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.YELLOW_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.YELLOW_STAINED_GLASS)));
        public static final RegistryObject<Block> LIME_STAINED_GLASS_SLAB = BLOCKS.register(
                        "lime_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.LIME_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.LIME_STAINED_GLASS)));
        public static final RegistryObject<Block> GREEN_STAINED_GLASS_SLAB = BLOCKS.register(
                        "green_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.GREEN_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.GREEN_STAINED_GLASS)));
        public static final RegistryObject<Block> CYAN_STAINED_GLASS_SLAB = BLOCKS.register(
                        "cyan_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.CYAN_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.CYAN_STAINED_GLASS)));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_GLASS_SLAB = BLOCKS.register(
                        "light_blue_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.LIGHT_BLUE_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_STAINED_GLASS)));
        public static final RegistryObject<Block> BLUE_STAINED_GLASS_SLAB = BLOCKS.register(
                        "blue_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.BLUE_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.BLUE_STAINED_GLASS)));
        public static final RegistryObject<Block> PURPLE_STAINED_GLASS_SLAB = BLOCKS.register(
                        "purple_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.PURPLE_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.PURPLE_STAINED_GLASS)));
        public static final RegistryObject<Block> MAGENTA_STAINED_GLASS_SLAB = BLOCKS.register(
                        "magenta_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.MAGENTA_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.MAGENTA_STAINED_GLASS)));
        public static final RegistryObject<Block> PINK_STAINED_GLASS_SLAB = BLOCKS.register(
                        "pink_stained_glass_slab",
                        () -> new ModSlabBlock(Blocks.PINK_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(Blocks.PINK_STAINED_GLASS)));
        public static final RegistryObject<Block> GLASS_STAIRS = BLOCKS.register(
                        "glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.GLASS)));
        public static final RegistryObject<Block> WHITE_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "white_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.WHITE_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.WHITE_STAINED_GLASS)));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "light_gray_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_STAINED_GLASS)));
        public static final RegistryObject<Block> GRAY_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "gray_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.GRAY_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.GRAY_STAINED_GLASS)));
        public static final RegistryObject<Block> BLACK_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "black_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.BLACK_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS)));
        public static final RegistryObject<Block> BROWN_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "brown_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.BROWN_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.BROWN_STAINED_GLASS)));
        public static final RegistryObject<Block> RED_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "red_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.RED_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.RED_STAINED_GLASS)));
        public static final RegistryObject<Block> ORANGE_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "orange_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.ORANGE_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.ORANGE_STAINED_GLASS)));
        public static final RegistryObject<Block> YELLOW_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "yellow_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.YELLOW_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.YELLOW_STAINED_GLASS)));
        public static final RegistryObject<Block> LIME_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "lime_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.LIME_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.LIME_STAINED_GLASS)));
        public static final RegistryObject<Block> GREEN_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "green_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.GREEN_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.GREEN_STAINED_GLASS)));
        public static final RegistryObject<Block> CYAN_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "cyan_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.CYAN_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.CYAN_STAINED_GLASS)));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "light_blue_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_STAINED_GLASS)));
        public static final RegistryObject<Block> BLUE_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "blue_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.BLUE_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.BLUE_STAINED_GLASS)));
        public static final RegistryObject<Block> PURPLE_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "purple_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.PURPLE_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.PURPLE_STAINED_GLASS)));
        public static final RegistryObject<Block> MAGENTA_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "magenta_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.MAGENTA_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.MAGENTA_STAINED_GLASS)));
        public static final RegistryObject<Block> PINK_STAINED_GLASS_STAIRS = BLOCKS.register(
                        "pink_stained_glass_stairs",
                        () -> new ModStairBlock(
                                        Blocks.PINK_STAINED_GLASS.defaultBlockState(),
                                        BlockBehaviour.Properties.copy(Blocks.PINK_STAINED_GLASS)));
        public static final RegistryObject<Block> WHITE_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "white_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.WHITE_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.WHITE_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> WHITE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "white_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.WHITE_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.WHITE_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> LIGHT_GRAY_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "light_gray_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.LIGHT_GRAY_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.LIGHT_GRAY_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> LIGHT_GRAY_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "light_gray_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.LIGHT_GRAY_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.LIGHT_GRAY_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> GRAY_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "gray_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.GRAY_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.GRAY_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> GRAY_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "gray_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.GRAY_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.GRAY_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> BLACK_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "black_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.BLACK_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BLACK_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> BLACK_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "black_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.BLACK_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BLACK_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> BROWN_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "brown_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.BROWN_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BROWN_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> BROWN_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "brown_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.BROWN_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BROWN_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> RED_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "red_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.RED_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.RED_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> RED_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "red_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.RED_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.RED_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> ORANGE_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "orange_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.ORANGE_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.ORANGE_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> ORANGE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "orange_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.ORANGE_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.ORANGE_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> YELLOW_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "yellow_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.YELLOW_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.YELLOW_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> YELLOW_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "yellow_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.YELLOW_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.YELLOW_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> LIME_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "lime_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.LIME_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.LIME_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> LIME_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "lime_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.LIME_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.LIME_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> GREEN_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "green_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.GREEN_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.GREEN_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> GREEN_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "green_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.GREEN_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.GREEN_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> CYAN_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "cyan_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.CYAN_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.CYAN_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> CYAN_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "cyan_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.CYAN_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.CYAN_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> LIGHT_BLUE_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "light_blue_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.LIGHT_BLUE_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.LIGHT_BLUE_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> LIGHT_BLUE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "light_blue_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.LIGHT_BLUE_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.LIGHT_BLUE_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> BLUE_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "blue_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.BLUE_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BLUE_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> BLUE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "blue_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.BLUE_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BLUE_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> PURPLE_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "purple_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.PURPLE_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.PURPLE_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> PURPLE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "purple_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.PURPLE_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.PURPLE_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> MAGENTA_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "magenta_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.MAGENTA_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.MAGENTA_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> MAGENTA_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "magenta_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.MAGENTA_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.MAGENTA_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> PINK_MOSAIC_GLASS_SLAB = BLOCKS.register(
                        "pink_mosaic_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.PINK_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.PINK_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> PINK_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                        "pink_mosaic_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.PINK_MOSAIC_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.PINK_MOSAIC_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_WHITE_GLASS_SLAB = BLOCKS.register(
                        "factory_white_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_WHITE_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_WHITE_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_WHITE_GLASS_STAIRS = BLOCKS.register(
                        "factory_white_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_WHITE_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_WHITE_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_LIGHT_GRAY_GLASS_SLAB = BLOCKS.register(
                        "factory_light_gray_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_LIGHT_GRAY_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_LIGHT_GRAY_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_LIGHT_GRAY_GLASS_STAIRS = BLOCKS.register(
                        "factory_light_gray_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_LIGHT_GRAY_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_LIGHT_GRAY_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_GRAY_GLASS_SLAB = BLOCKS.register(
                        "factory_gray_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_GRAY_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_GRAY_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_GRAY_GLASS_STAIRS = BLOCKS.register(
                        "factory_gray_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_GRAY_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_GRAY_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_BLACK_GLASS_SLAB = BLOCKS.register(
                        "factory_black_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_BLACK_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_BLACK_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_BLACK_GLASS_STAIRS = BLOCKS.register(
                        "factory_black_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_BLACK_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_BLACK_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_BROWN_GLASS_SLAB = BLOCKS.register(
                        "factory_brown_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_BROWN_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_BROWN_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_BROWN_GLASS_STAIRS = BLOCKS.register(
                        "factory_brown_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_BROWN_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_BROWN_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_RED_GLASS_SLAB = BLOCKS.register(
                        "factory_red_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_RED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_RED_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_RED_GLASS_STAIRS = BLOCKS.register(
                        "factory_red_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_RED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_RED_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_ORANGE_GLASS_SLAB = BLOCKS.register(
                        "factory_orange_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_ORANGE_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_ORANGE_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_ORANGE_GLASS_STAIRS = BLOCKS.register(
                        "factory_orange_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_ORANGE_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_ORANGE_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_YELLOW_GLASS_SLAB = BLOCKS.register(
                        "factory_yellow_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_YELLOW_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_YELLOW_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_YELLOW_GLASS_STAIRS = BLOCKS.register(
                        "factory_yellow_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_YELLOW_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_YELLOW_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_LIME_GLASS_SLAB = BLOCKS.register(
                        "factory_lime_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_LIME_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_LIME_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_LIME_GLASS_STAIRS = BLOCKS.register(
                        "factory_lime_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_LIME_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_LIME_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_GREEN_GLASS_SLAB = BLOCKS.register(
                        "factory_green_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_GREEN_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_GREEN_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_GREEN_GLASS_STAIRS = BLOCKS.register(
                        "factory_green_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_GREEN_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_GREEN_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_CYAN_GLASS_SLAB = BLOCKS.register(
                        "factory_cyan_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_CYAN_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_CYAN_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_CYAN_GLASS_STAIRS = BLOCKS.register(
                        "factory_cyan_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_CYAN_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_CYAN_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_LIGHT_BLUE_GLASS_SLAB = BLOCKS.register(
                        "factory_light_blue_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_LIGHT_BLUE_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_LIGHT_BLUE_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_LIGHT_BLUE_GLASS_STAIRS = BLOCKS.register(
                        "factory_light_blue_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_LIGHT_BLUE_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_LIGHT_BLUE_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_BLUE_GLASS_SLAB = BLOCKS.register(
                        "factory_blue_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_BLUE_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_BLUE_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_BLUE_GLASS_STAIRS = BLOCKS.register(
                        "factory_blue_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_BLUE_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_BLUE_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_PURPLE_GLASS_SLAB = BLOCKS.register(
                        "factory_purple_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_PURPLE_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_PURPLE_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_PURPLE_GLASS_STAIRS = BLOCKS.register(
                        "factory_purple_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_PURPLE_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_PURPLE_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_MAGENTA_GLASS_SLAB = BLOCKS.register(
                        "factory_magenta_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_MAGENTA_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_MAGENTA_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_MAGENTA_GLASS_STAIRS = BLOCKS.register(
                        "factory_magenta_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_MAGENTA_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_MAGENTA_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_PINK_GLASS_SLAB = BLOCKS.register(
                        "factory_pink_glass_slab",
                        () -> new ModSlabBlock(BlocksPart05.FACTORY_PINK_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_PINK_GLASS.get())));
        public static final RegistryObject<Block> FACTORY_PINK_GLASS_STAIRS = BLOCKS.register(
                        "factory_pink_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart05.FACTORY_PINK_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.FACTORY_PINK_GLASS.get())));
        public static final RegistryObject<Block> WHITE_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "white_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.WHITE_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.WHITE_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> WHITE_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "white_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.WHITE_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.WHITE_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> LIGHT_GRAY_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "light_gray_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.LIGHT_GRAY_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.LIGHT_GRAY_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> LIGHT_GRAY_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "light_gray_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.LIGHT_GRAY_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.LIGHT_GRAY_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> GRAY_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "gray_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.GRAY_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.GRAY_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> GRAY_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "gray_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.GRAY_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.GRAY_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> BLACK_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "black_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.BLACK_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BLACK_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> BLACK_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "black_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.BLACK_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BLACK_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> BROWN_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "brown_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.BROWN_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BROWN_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> BROWN_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "brown_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.BROWN_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BROWN_GLAZED_GLASS.get())));
}
