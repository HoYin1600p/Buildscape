package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.sound.ModSounds;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
                ForgeRegistries.BLOCKS,
                BuildScape.MODID);

/*
        public static final RegistryObject<Block> MIRROR_BLOCK = BLOCKS.register(
                "mirror_block",
                () -> new MirrorBlock(
                        BlockBehaviour.Properties.of(Material.GLASS)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
*/

        public static final RegistryObject<Block> BLACK_SAND = BLOCKS.register(
                "black_sand",
                () -> new FallingSandBlock(
                        BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_BLACK)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.SAND)));

        public static final RegistryObject<Block> BLUE_SAND = BLOCKS.register(
                "blue_sand",
                () -> new FallingSandBlock(
                        BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_BLUE)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.SAND)));

        public static final RegistryObject<Block> GREEN_SAND = BLOCKS.register(
                "green_sand",
                () -> new FallingSandBlock(
                        BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_GREEN)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.SAND)));

        public static final RegistryObject<Block> ORANGE_SAND = BLOCKS.register(
                "orange_sand",
                () -> new FallingSandBlock(
                        BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_ORANGE)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.SAND)));

        public static final RegistryObject<Block> PINK_SAND = BLOCKS.register(
                "pink_sand",
                () -> new FallingSandBlock(
                        BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_PINK)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.SAND)));

        public static final RegistryObject<Block> RED_SAND = BLOCKS.register(
                "red_sand",
                () -> new FallingSandBlock(
                        BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_RED)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.SAND)));

        public static final RegistryObject<Block> WHITE_SAND = BLOCKS.register(
                "white_sand",
                () -> new FallingSandBlock(
                        BlockBehaviour.Properties.of(Material.SAND, MaterialColor.SNOW)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.SAND)));

        public static final RegistryObject<Block> YELLOW_SAND = BLOCKS.register(
                "yellow_sand",
                () -> new FallingSandBlock(
                        BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_YELLOW)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.SAND)));

        public static final RegistryObject<Block> BLACK_SANDSTONE = BLOCKS.register(
                "black_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_SANDSTONE_STAIRS = BLOCKS.register("black_sandstone_stairs",
                () -> new ModStairBlock(
                        BLACK_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE),
                        ModItems.BLACK_SANDSTONE_STAIRS));
        public static final RegistryObject<Block> BLUE_SANDSTONE = BLOCKS.register(
                "blue_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_SANDSTONE_STAIRS = BLOCKS.register("blue_sandstone_stairs",
                () -> new ModStairBlock(
                        BLUE_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_SANDSTONE = BLOCKS.register(
                "green_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_SANDSTONE_STAIRS = BLOCKS.register("green_sandstone_stairs",
                () -> new ModStairBlock(
                        GREEN_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_SANDSTONE = BLOCKS.register(
                "orange_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_SANDSTONE_STAIRS = BLOCKS.register("orange_sandstone_stairs",
                () -> new ModStairBlock(
                        ORANGE_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PINK_SANDSTONE = BLOCKS.register(
                "pink_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PINK_SANDSTONE_STAIRS = BLOCKS.register("pink_sandstone_stairs",
                () -> new ModStairBlock(
                        PINK_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> RED_SANDSTONE = BLOCKS.register(
                "red_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> RED_SANDSTONE_STAIRS = BLOCKS.register("red_sandstone_stairs",
                () -> new ModStairBlock(
                        RED_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_SANDSTONE = BLOCKS.register(
                "white_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_SANDSTONE_STAIRS = BLOCKS.register("white_sandstone_stairs",
                () -> new ModStairBlock(
                        WHITE_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_SANDSTONE = BLOCKS.register(
                "yellow_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_SANDSTONE_STAIRS = BLOCKS.register("yellow_sandstone_stairs",
                () -> new ModStairBlock(
                        YELLOW_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_TILES = BLOCKS.register(
                "black_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE),
                        ModItems.BLACK_TILES));
        public static final RegistryObject<Block> BLACK_TILES_STAIRS = BLOCKS.register("black_tiles_stairs",
                () -> new ModStairBlock(
                        BLACK_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> BLUE_TILES = BLOCKS.register(
                "blue_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> BLUE_TILES_STAIRS = BLOCKS.register(
                "blue_tiles_stairs",
                () -> new ModStairBlock(
                        BLUE_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> BROWN_TILES = BLOCKS.register(
                "brown_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> BROWN_TILES_STAIRS = BLOCKS.register("brown_tiles_stairs",
                () -> new ModStairBlock(
                        BROWN_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> CYAN_TILES = BLOCKS.register(
                "cyan_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> CYAN_TILES_STAIRS = BLOCKS.register(
                "cyan_tiles_stairs",
                () -> new ModStairBlock(
                        CYAN_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> GRAY_TILES = BLOCKS.register(
                "gray_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> GRAY_TILES_STAIRS = BLOCKS.register(
                "gray_tiles_stairs",
                () -> new ModStairBlock(
                        GRAY_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> GREEN_TILES = BLOCKS.register(
                "green_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> GREEN_TILES_STAIRS = BLOCKS.register("green_tiles_stairs",
                () -> new ModStairBlock(
                        GREEN_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> LIGHT_BLUE_TILES = BLOCKS.register(
                "light_blue_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> LIGHT_BLUE_TILES_STAIRS = BLOCKS.register("light_blue_tiles_stairs",
                () -> new ModStairBlock(
                        LIGHT_BLUE_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> LIGHT_GRAY_TILES = BLOCKS.register(
                "light_gray_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> LIGHT_GRAY_TILES_STAIRS = BLOCKS.register("light_gray_tiles_stairs",
                () -> new ModStairBlock(
                        LIGHT_GRAY_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> LIME_TILES = BLOCKS.register(
                "lime_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> LIME_TILES_STAIRS = BLOCKS.register(
                "lime_tiles_stairs",
                () -> new ModStairBlock(
                        LIME_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> MAGENTA_TILES = BLOCKS.register(
                "magenta_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> MAGENTA_TILES_STAIRS = BLOCKS.register("magenta_tiles_stairs",
                () -> new ModStairBlock(
                        MAGENTA_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> ORANGE_TILES = BLOCKS.register(
                "orange_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> ORANGE_TILES_STAIRS = BLOCKS.register("orange_tiles_stairs",
                () -> new ModStairBlock(
                        ORANGE_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> PINK_TILES = BLOCKS.register(
                "pink_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> PINK_TILES_STAIRS = BLOCKS.register(
                "pink_tiles_stairs",
                () -> new ModStairBlock(
                        PINK_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> PURPLE_TILES = BLOCKS.register(
                "purple_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> PURPLE_TILES_STAIRS = BLOCKS.register("purple_tiles_stairs",
                () -> new ModStairBlock(
                        PURPLE_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> RED_TILES = BLOCKS.register(
                "red_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> RED_TILES_STAIRS = BLOCKS.register(
                "red_tiles_stairs",
                () -> new ModStairBlock(
                        RED_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> WHITE_TILES = BLOCKS.register(
                "white_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> WHITE_TILES_STAIRS = BLOCKS.register("white_tiles_stairs",
                () -> new ModStairBlock(
                        WHITE_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> YELLOW_TILES = BLOCKS.register(
                "yellow_tiles",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> YELLOW_TILES_STAIRS = BLOCKS.register("yellow_tiles_stairs",
                () -> new ModStairBlock(
                        YELLOW_TILES.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> BLACK_MOSAIC_GLASS = BLOCKS.register("black_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BLUE_MOSAIC_GLASS = BLOCKS.register(
                "blue_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BROWN_MOSAIC_GLASS = BLOCKS.register("brown_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> CYAN_MOSAIC_GLASS = BLOCKS.register(
                "cyan_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> GRAY_MOSAIC_GLASS = BLOCKS.register(
                "gray_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> GREEN_MOSAIC_GLASS = BLOCKS.register("green_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIGHT_BLUE_MOSAIC_GLASS = BLOCKS.register("light_blue_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIGHT_GRAY_MOSAIC_GLASS = BLOCKS.register("light_gray_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIME_MOSAIC_GLASS = BLOCKS.register(
                "lime_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> MAGENTA_MOSAIC_GLASS = BLOCKS.register("magenta_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> ORANGE_MOSAIC_GLASS = BLOCKS.register("orange_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> PINK_MOSAIC_GLASS = BLOCKS.register(
                "pink_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> PURPLE_MOSAIC_GLASS = BLOCKS.register("purple_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> RED_MOSAIC_GLASS = BLOCKS.register(
                "red_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> WHITE_MOSAIC_GLASS = BLOCKS.register("white_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> YELLOW_MOSAIC_GLASS = BLOCKS.register("yellow_mosaic_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BIT_CHISELED_COPPER = BLOCKS.register("bit_chiseled_copper",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_COPPER_BLOCK = BLOCKS.register(
                "bit_copper_block",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_COPPER_BLOCK_STAIRS = BLOCKS.register("bit_copper_block_stairs",
                () -> new ModStairBlock(
                        BIT_COPPER_BLOCK.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_COPPER_BULB = BLOCKS.register(
                "bit_copper_bulb",
                () -> new FreshCopperBulbBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(0.75f, 1.5f)
                                .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 15 : 0)
                                .requiresCorrectToolForDrops()
                                .sound(ModSounds.COPPER_BULB_SOUNDS())
                                .isRedstoneConductor((state, reader, pos) -> false)));
        public static final RegistryObject<Block> BIT_COPPER_GRATE = BLOCKS.register(
                "bit_copper_grate",
                () -> new WaterloggableGrateBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(0.75f, 1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(ModSounds.COPPER_GRATE_SOUNDS())
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> BIT_CUT_COPPER = BLOCKS.register(
                "bit_cut_copper",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_CUT_COPPER_STAIRS = BLOCKS.register("bit_cut_copper_stairs",
                () -> new ModStairBlock(
                        BIT_CUT_COPPER.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_EXPOSED_CHISELED_COPPER = BLOCKS
                .register("bit_exposed_chiseled_copper", () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.TERRACOTTA_LIGHT_GRAY)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_EXPOSED_COPPER_BLOCK = BLOCKS.register("bit_exposed_copper_block",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.TERRACOTTA_LIGHT_GRAY)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_EXPOSED_COPPER_BLOCK_STAIRS = BLOCKS
                .register("bit_exposed_copper_block_stairs", () -> new ModStairBlock(
                        BIT_EXPOSED_COPPER_BLOCK.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.TERRACOTTA_LIGHT_GRAY)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_EXPOSED_COPPER_BULB = BLOCKS.register("bit_exposed_copper_bulb",
                () -> new ExposedCopperBulbBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.TERRACOTTA_LIGHT_GRAY)
                                .strength(0.75f, 1.5f)
                                .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 12 : 0)
                                .requiresCorrectToolForDrops()
                                .sound(ModSounds.COPPER_BULB_SOUNDS())
                                .isRedstoneConductor((state, reader, pos) -> false)));
        public static final RegistryObject<Block> BIT_EXPOSED_COPPER_GRATE = BLOCKS.register("bit_exposed_copper_grate",
                () -> new WaterloggableGrateBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.TERRACOTTA_LIGHT_GRAY)
                                .strength(0.75f, 1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(ModSounds.COPPER_GRATE_SOUNDS())
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> BIT_EXPOSED_CUT_COPPER = BLOCKS.register("bit_exposed_cut_copper",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.TERRACOTTA_LIGHT_GRAY)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_EXPOSED_CUT_COPPER_STAIRS = BLOCKS
                .register("bit_exposed_cut_copper_stairs", () -> new ModStairBlock(
                        BIT_EXPOSED_CUT_COPPER.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.TERRACOTTA_LIGHT_GRAY)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_WEATHERED_CHISELED_COPPER = BLOCKS
                .register("bit_weathered_chiseled_copper", () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.WARPED_NYLIUM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_WEATHERED_COPPER_BLOCK = BLOCKS
                .register("bit_weathered_copper_block", () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.WARPED_NYLIUM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_WEATHERED_COPPER_BLOCK_STAIRS = BLOCKS
                .register("bit_weathered_copper_block_stairs", () -> new ModStairBlock(
                        BIT_WEATHERED_COPPER_BLOCK.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.WARPED_NYLIUM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_WEATHERED_COPPER_BULB = BLOCKS
                .register("bit_weathered_copper_bulb", () -> new WeatheredCopperBulbBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.WARPED_NYLIUM)
                                .strength(0.75f, 1.5f)
                                .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 8 : 0)
                                .requiresCorrectToolForDrops()
                                .sound(ModSounds.COPPER_BULB_SOUNDS())
                                .isRedstoneConductor((state, reader, pos) -> false)));
        public static final RegistryObject<Block> BIT_WEATHERED_COPPER_GRATE = BLOCKS
                .register("bit_weathered_copper_grate", () -> new WaterloggableGrateBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.WARPED_NYLIUM)
                                .strength(0.75f, 1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(ModSounds.COPPER_GRATE_SOUNDS())
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> BIT_WEATHERED_CUT_COPPER = BLOCKS.register("bit_weathered_cut_copper",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.WARPED_NYLIUM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_WEATHERED_CUT_COPPER_STAIRS = BLOCKS
                .register("bit_weathered_cut_copper_stairs", () -> new ModStairBlock(
                        BIT_WEATHERED_CUT_COPPER.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.WARPED_NYLIUM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_OXIDIZED_CHISELED_COPPER = BLOCKS
                .register("bit_oxidized_chiseled_copper", () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_OXIDIZED_COPPER_BLOCK = BLOCKS
                .register("bit_oxidized_copper_block", () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_OXIDIZED_COPPER_BLOCK_STAIRS = BLOCKS
                .register("bit_oxidized_copper_block_stairs", () -> new ModStairBlock(
                        BIT_OXIDIZED_COPPER_BLOCK.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_OXIDIZED_COPPER_BULB = BLOCKS.register("bit_oxidized_copper_bulb",
                () -> new OxidizedCopperBulbBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                                .strength(0.75f, 1.5f)
                                .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 4 : 0)
                                .requiresCorrectToolForDrops()
                                .sound(ModSounds.COPPER_BULB_SOUNDS())
                                .isRedstoneConductor((state, reader, pos) -> false)));
        public static final RegistryObject<Block> BIT_OXIDIZED_COPPER_GRATE = BLOCKS
                .register("bit_oxidized_copper_grate", () -> new WaterloggableGrateBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                                .strength(0.75f, 1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(ModSounds.COPPER_GRATE_SOUNDS())
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> BIT_OXIDIZED_CUT_COPPER = BLOCKS.register("bit_oxidized_cut_copper",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_OXIDIZED_CUT_COPPER_STAIRS = BLOCKS
                .register("bit_oxidized_cut_copper_stairs", () -> new ModStairBlock(
                        BIT_OXIDIZED_CUT_COPPER.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_CHISELED_TUFF = BLOCKS.register(
                "bit_chiseled_tuff",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_GRAY)
                                .strength(1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.TUFF)));
        public static final RegistryObject<Block> BIT_CHISELED_TUFF_BRICKS = BLOCKS.register("bit_chiseled_tuff_bricks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_GRAY)
                                .strength(1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.TUFF)));
        public static final RegistryObject<Block> BIT_POLISHED_TUFF = BLOCKS.register(
                "bit_polished_tuff",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_GRAY)
                                .strength(1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.TUFF)));
        public static final RegistryObject<Block> BIT_POLISHED_TUFF_STAIRS = BLOCKS.register("bit_polished_tuff_stairs",
                () -> new ModStairBlock(
                        BIT_POLISHED_TUFF.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_GRAY)
                                .strength(1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.TUFF)));
        public static final RegistryObject<Block> BIT_TUFF_BRICKS = BLOCKS.register(
                "bit_tuff_bricks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_GRAY)
                                .strength(1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.TUFF)));
        public static final RegistryObject<Block> BIT_TUFF_BRICKS_STAIRS = BLOCKS.register("bit_tuff_bricks_stairs",
                () -> new ModStairBlock(
                        BIT_TUFF_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_GRAY)
                                .strength(1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.TUFF)));
        public static final RegistryObject<Block> BIT_POLISHED_TUFF_SLAB = BLOCKS.register("bit_polished_tuff_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_GRAY)
                                .strength(1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.TUFF)));
        public static final RegistryObject<Block> BIT_POLISHED_TUFF_WALL = BLOCKS.register("bit_polished_tuff_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_GRAY)
                                .strength(1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.TUFF)));
        public static final RegistryObject<Block> BIT_TUFF_BRICKS_SLAB = BLOCKS.register("bit_tuff_bricks_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_GRAY)
                                .strength(1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.TUFF)));
        public static final RegistryObject<Block> BIT_TUFF_BRICKS_WALL = BLOCKS.register("bit_tuff_bricks_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_GRAY)
                                .strength(1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.TUFF)));
        public static final RegistryObject<Block> PODZOL_SLAB = BLOCKS.register(
                "podzol_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.PODZOL)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> DIRT_SLAB = BLOCKS.register(
                "dirt_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.GRAVEL)));
        public static final RegistryObject<Block> MYCELIUM_SLAB = BLOCKS.register(
                "mycelium_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.COLOR_PURPLE)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> MUD = BLOCKS.register("mud", () -> new MudBlock(
                BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.COLOR_BROWN)
                        .strength(0.5f)
                        .sound(ModSounds.MUD_SOUNDS())));
        public static final RegistryObject<Block> MUD_SLAB = BLOCKS.register(
                "mud_slab",
                () -> new MudSlabBlock(
                        BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.COLOR_BROWN)
                                .strength(0.5f)
                                .sound(ModSounds.MUD_SOUNDS())));
        public static final RegistryObject<Block> BLACK_SMOOTH_SANDSTONE = BLOCKS.register("black_smooth_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_SMOOTH_SANDSTONE_STAIRS = BLOCKS
                .register("black_smooth_sandstone_stairs", () -> new ModStairBlock(
                        BLACK_SMOOTH_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_SMOOTH_SANDSTONE = BLOCKS.register("blue_smooth_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_SMOOTH_SANDSTONE_STAIRS = BLOCKS
                .register("blue_smooth_sandstone_stairs", () -> new ModStairBlock(
                        BLUE_SMOOTH_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_SMOOTH_SANDSTONE = BLOCKS.register("green_smooth_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_SMOOTH_SANDSTONE_STAIRS = BLOCKS
                .register("green_smooth_sandstone_stairs", () -> new ModStairBlock(
                        GREEN_SMOOTH_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_SMOOTH_SANDSTONE = BLOCKS.register("orange_smooth_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_SMOOTH_SANDSTONE_STAIRS = BLOCKS
                .register("orange_smooth_sandstone_stairs", () -> new ModStairBlock(
                        ORANGE_SMOOTH_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PINK_SMOOTH_SANDSTONE = BLOCKS.register("pink_smooth_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PINK_SMOOTH_SANDSTONE_STAIRS = BLOCKS
                .register("pink_smooth_sandstone_stairs", () -> new ModStairBlock(
                        PINK_SMOOTH_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> RED_SMOOTH_SANDSTONE = BLOCKS.register("red_smooth_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> RED_SMOOTH_SANDSTONE_STAIRS = BLOCKS
                .register("red_smooth_sandstone_stairs", () -> new ModStairBlock(
                        RED_SMOOTH_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_SMOOTH_SANDSTONE = BLOCKS.register("white_smooth_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_SMOOTH_SANDSTONE_STAIRS = BLOCKS
                .register("white_smooth_sandstone_stairs", () -> new ModStairBlock(
                        WHITE_SMOOTH_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_SMOOTH_SANDSTONE = BLOCKS.register("yellow_smooth_sandstone",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_SMOOTH_SANDSTONE_STAIRS = BLOCKS
                .register("yellow_smooth_sandstone_stairs", () -> new ModStairBlock(
                        YELLOW_SMOOTH_SANDSTONE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_MOSAIC_GLASS_PANE = BLOCKS.register("black_mosaic_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BLUE_MOSAIC_GLASS_PANE = BLOCKS.register("blue_mosaic_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BROWN_MOSAIC_GLASS_PANE = BLOCKS.register("brown_mosaic_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> CYAN_MOSAIC_GLASS_PANE = BLOCKS.register("cyan_mosaic_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> GRAY_MOSAIC_GLASS_PANE = BLOCKS.register("gray_mosaic_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> GREEN_MOSAIC_GLASS_PANE = BLOCKS.register("green_mosaic_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIGHT_BLUE_MOSAIC_GLASS_PANE = BLOCKS
                .register("light_blue_mosaic_glass_pane", () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIGHT_GRAY_MOSAIC_GLASS_PANE = BLOCKS
                .register("light_gray_mosaic_glass_pane", () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIME_MOSAIC_GLASS_PANE = BLOCKS.register("lime_mosaic_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> MAGENTA_MOSAIC_GLASS_PANE = BLOCKS
                .register("magenta_mosaic_glass_pane", () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> ORANGE_MOSAIC_GLASS_PANE = BLOCKS.register("orange_mosaic_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> PINK_MOSAIC_GLASS_PANE = BLOCKS.register("pink_mosaic_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> PURPLE_MOSAIC_GLASS_PANE = BLOCKS.register("purple_mosaic_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> RED_MOSAIC_GLASS_PANE = BLOCKS.register("red_mosaic_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> WHITE_MOSAIC_GLASS_PANE = BLOCKS.register("white_mosaic_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> YELLOW_MOSAIC_GLASS_PANE = BLOCKS.register("yellow_mosaic_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BLACK_GLAZED_GLASS = BLOCKS.register("black_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BLUE_GLAZED_GLASS = BLOCKS.register(
                "blue_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BROWN_GLAZED_GLASS = BLOCKS.register("brown_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> CYAN_GLAZED_GLASS = BLOCKS.register(
                "cyan_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> GRAY_GLAZED_GLASS = BLOCKS.register(
                "gray_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> GREEN_GLAZED_GLASS = BLOCKS.register("green_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIGHT_BLUE_GLAZED_GLASS = BLOCKS.register("light_blue_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIGHT_GRAY_GLAZED_GLASS = BLOCKS.register("light_gray_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIME_GLAZED_GLASS = BLOCKS.register(
                "lime_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> MAGENTA_GLAZED_GLASS = BLOCKS.register("magenta_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> ORANGE_GLAZED_GLASS = BLOCKS.register("orange_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> PINK_GLAZED_GLASS = BLOCKS.register(
                "pink_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> PURPLE_GLAZED_GLASS = BLOCKS.register("purple_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> RED_GLAZED_GLASS = BLOCKS.register(
                "red_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> WHITE_GLAZED_GLASS = BLOCKS.register("white_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> YELLOW_GLAZED_GLASS = BLOCKS.register("yellow_glazed_glass",
                () -> new GlazedGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BLACK_GLAZED_GLASS_PANE = BLOCKS.register("black_glazed_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BLUE_GLAZED_GLASS_PANE = BLOCKS.register("blue_glazed_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BROWN_GLAZED_GLASS_PANE = BLOCKS.register("brown_glazed_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> CYAN_GLAZED_GLASS_PANE = BLOCKS.register("cyan_glazed_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> GRAY_GLAZED_GLASS_PANE = BLOCKS.register("gray_glazed_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> GREEN_GLAZED_GLASS_PANE = BLOCKS.register("green_glazed_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIGHT_BLUE_GLAZED_GLASS_PANE = BLOCKS
                .register("light_blue_glazed_glass_pane", () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIGHT_GRAY_GLAZED_GLASS_PANE = BLOCKS
                .register("light_gray_glazed_glass_pane", () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIME_GLAZED_GLASS_PANE = BLOCKS.register("lime_glazed_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> MAGENTA_GLAZED_GLASS_PANE = BLOCKS
                .register("magenta_glazed_glass_pane", () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> ORANGE_GLAZED_GLASS_PANE = BLOCKS.register("orange_glazed_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> PINK_GLAZED_GLASS_PANE = BLOCKS.register("pink_glazed_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> PURPLE_GLAZED_GLASS_PANE = BLOCKS.register("purple_glazed_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> RED_GLAZED_GLASS_PANE = BLOCKS.register("red_glazed_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> WHITE_GLAZED_GLASS_PANE = BLOCKS.register("white_glazed_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> YELLOW_GLAZED_GLASS_PANE = BLOCKS.register("yellow_glazed_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BLACK_SANDSTONE_SLAB = BLOCKS.register("black_sandstone_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_SANDSTONE_SLAB = BLOCKS.register("blue_sandstone_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_SANDSTONE_SLAB = BLOCKS.register("green_sandstone_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_SANDSTONE_SLAB = BLOCKS.register("orange_sandstone_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PINK_SANDSTONE_SLAB = BLOCKS.register("pink_sandstone_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> RED_SANDSTONE_SLAB = BLOCKS.register("red_sandstone_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_SANDSTONE_SLAB = BLOCKS.register("white_sandstone_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_SANDSTONE_SLAB = BLOCKS.register("yellow_sandstone_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_SANDSTONE_WALL = BLOCKS.register("black_sandstone_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_SANDSTONE_WALL = BLOCKS.register("blue_sandstone_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_SANDSTONE_WALL = BLOCKS.register("green_sandstone_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_SANDSTONE_WALL = BLOCKS.register("orange_sandstone_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PINK_SANDSTONE_WALL = BLOCKS.register("pink_sandstone_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> RED_SANDSTONE_WALL = BLOCKS.register("red_sandstone_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_SANDSTONE_WALL = BLOCKS.register("white_sandstone_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_SANDSTONE_WALL = BLOCKS.register("yellow_sandstone_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_SMOOTH_SANDSTONE_SLAB = BLOCKS
                .register("black_smooth_sandstone_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_SMOOTH_SANDSTONE_SLAB = BLOCKS
                .register("blue_smooth_sandstone_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_SMOOTH_SANDSTONE_SLAB = BLOCKS
                .register("green_smooth_sandstone_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_SMOOTH_SANDSTONE_SLAB = BLOCKS
                .register("orange_smooth_sandstone_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PINK_SMOOTH_SANDSTONE_SLAB = BLOCKS
                .register("pink_smooth_sandstone_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> RED_SMOOTH_SANDSTONE_SLAB = BLOCKS
                .register("red_smooth_sandstone_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_SMOOTH_SANDSTONE_SLAB = BLOCKS
                .register("white_smooth_sandstone_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_SMOOTH_SANDSTONE_SLAB = BLOCKS
                .register("yellow_smooth_sandstone_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_SMOOTH_SANDSTONE_WALL = BLOCKS
                .register("white_smooth_sandstone_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_SMOOTH_SANDSTONE_WALL = BLOCKS
                .register("black_smooth_sandstone_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> RED_SMOOTH_SANDSTONE_WALL = BLOCKS
                .register("red_smooth_sandstone_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_SMOOTH_SANDSTONE_WALL = BLOCKS
                .register("orange_smooth_sandstone_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_SMOOTH_SANDSTONE_WALL = BLOCKS
                .register("yellow_smooth_sandstone_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_SMOOTH_SANDSTONE_WALL = BLOCKS
                .register("green_smooth_sandstone_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_SMOOTH_SANDSTONE_WALL = BLOCKS
                .register("blue_smooth_sandstone_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PINK_SMOOTH_SANDSTONE_WALL = BLOCKS
                .register("pink_smooth_sandstone_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> SMOOTH_SANDSTONE_WALL = BLOCKS.register("smooth_sandstone_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_TILES_SLAB = BLOCKS.register(
                "black_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> BLUE_TILES_SLAB = BLOCKS.register(
                "blue_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> BROWN_TILES_SLAB = BLOCKS.register(
                "brown_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> CYAN_TILES_SLAB = BLOCKS.register(
                "cyan_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> GRAY_TILES_SLAB = BLOCKS.register(
                "gray_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> GREEN_TILES_SLAB = BLOCKS.register(
                "green_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> LIGHT_BLUE_TILES_SLAB = BLOCKS.register("light_blue_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> LIGHT_GRAY_TILES_SLAB = BLOCKS.register("light_gray_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> LIME_TILES_SLAB = BLOCKS.register(
                "lime_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> MAGENTA_TILES_SLAB = BLOCKS.register("magenta_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> ORANGE_TILES_SLAB = BLOCKS.register(
                "orange_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> PINK_TILES_SLAB = BLOCKS.register(
                "pink_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> PURPLE_TILES_SLAB = BLOCKS.register(
                "purple_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> RED_TILES_SLAB = BLOCKS.register(
                "red_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> WHITE_TILES_SLAB = BLOCKS.register(
                "white_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> YELLOW_TILES_SLAB = BLOCKS.register(
                "yellow_tiles_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> BLACK_TILES_WALL = BLOCKS.register(
                "black_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> BLUE_TILES_WALL = BLOCKS.register(
                "blue_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> BROWN_TILES_WALL = BLOCKS.register(
                "brown_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> CYAN_TILES_WALL = BLOCKS.register(
                "cyan_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> GRAY_TILES_WALL = BLOCKS.register(
                "gray_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> GREEN_TILES_WALL = BLOCKS.register(
                "green_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> LIGHT_BLUE_TILES_WALL = BLOCKS.register("light_blue_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> LIGHT_GRAY_TILES_WALL = BLOCKS.register("light_gray_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> LIME_TILES_WALL = BLOCKS.register(
                "lime_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> MAGENTA_TILES_WALL = BLOCKS.register("magenta_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> ORANGE_TILES_WALL = BLOCKS.register(
                "orange_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> PINK_TILES_WALL = BLOCKS.register(
                "pink_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> PURPLE_TILES_WALL = BLOCKS.register(
                "purple_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> RED_TILES_WALL = BLOCKS.register(
                "red_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> WHITE_TILES_WALL = BLOCKS.register(
                "white_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> YELLOW_TILES_WALL = BLOCKS.register(
                "yellow_tiles_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> POLISHED_BASALT_STAIRS = BLOCKS.register("polished_basalt_stairs",
                () -> new ModStairBlock(
                        Blocks.POLISHED_BASALT.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(1.5f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.BASALT)));
        public static final RegistryObject<Block> POLISHED_BASALT_SLAB = BLOCKS.register("polished_basalt_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(1.5f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.BASALT)));
        public static final RegistryObject<Block> POLISHED_BASALT_WALL = BLOCKS.register("polished_basalt_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(1.5f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.BASALT)));
        public static final RegistryObject<Block> DRIPSTONE_BLOCK_STAIRS = BLOCKS.register("dripstone_block_stairs",
                () -> new ModStairBlock(
                        Blocks.DRIPSTONE_BLOCK.defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_BROWN)
                                .strength(1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.DRIPSTONE_BLOCK)));
        public static final RegistryObject<Block> DRIPSTONE_BLOCK_SLAB = BLOCKS.register("dripstone_block_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_BROWN)
                                .strength(1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.DRIPSTONE_BLOCK)));
        public static final RegistryObject<Block> DRIPSTONE_BLOCK_WALL = BLOCKS.register("dripstone_block_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_BROWN)
                                .strength(1.5f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.DRIPSTONE_BLOCK)));
        public static final RegistryObject<Block> END_STONE_STAIRS = BLOCKS.register(
                "end_stone_stairs",
                () -> new ModStairBlock(
                        Blocks.END_STONE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND)
                                .strength(3.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> END_STONE_SLAB = BLOCKS.register(
                "end_stone_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND)
                                .strength(3.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> END_STONE_WALL = BLOCKS.register(
                "end_stone_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND)
                                .strength(3.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> STONE_WALL = BLOCKS.register(
                "stone_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE)
                                .strength(1.5f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> QUARTZ_BRICKS_STAIRS = BLOCKS.register("quartz_bricks_stairs",
                () -> new ModStairBlock(
                        Blocks.QUARTZ_BRICKS.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> QUARTZ_BRICKS_SLAB = BLOCKS.register("quartz_bricks_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> QUARTZ_BRICKS_WALL = BLOCKS.register("quartz_bricks_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> CALCITE_STAIRS = BLOCKS.register(
                "calcite_stairs",
                () -> new ModStairBlock(
                        Blocks.CALCITE.defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_WHITE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> CALCITE_SLAB = BLOCKS.register(
                "calcite_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_WHITE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> CALCITE_WALL = BLOCKS.register(
                "calcite_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_WHITE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> BEDROCK_STAIRS = BLOCKS.register(
                "bedrock_stairs",
                () -> new ModStairBlock(
                        Blocks.BEDROCK.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(-1.0f, 3600000.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BEDROCK_SLAB = BLOCKS.register(
                "bedrock_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(-1.0f, 3600000.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BEDROCK_WALL = BLOCKS.register(
                "bedrock_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(-1.0f, 3600000.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BEDROCK_PANE = BLOCKS.register(
                "bedrock_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(-1.0f, 3600000.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)
                                .noOcclusion()));
        public static final RegistryObject<Block> OBSIDIAN_STAIRS = BLOCKS.register(
                "obsidian_stairs",
                () -> new ModStairBlock(
                        Blocks.OBSIDIAN.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(50.0f, 1200.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> OBSIDIAN_SLAB = BLOCKS.register(
                "obsidian_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(50.0f, 1200.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PRISMARINE_BRICKS_WALL = BLOCKS.register("prismarine_bricks_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                .strength(0.4f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> DARK_PRISMARINE_WALL = BLOCKS.register("dark_prismarine_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                .strength(0.4f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> QUARTZ_BLOCK_WALL = BLOCKS.register(
                "quartz_block_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                                .strength(0.4f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> SMOOTH_QUARTZ_WALL = BLOCKS.register("smooth_quartz_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                                .strength(0.4f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> SMOOTH_BASALT_STAIRS = BLOCKS.register("smooth_basalt_stairs",
                () -> new ModStairBlock(
                        Blocks.SMOOTH_BASALT.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.4f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.BASALT)));
        public static final RegistryObject<Block> SMOOTH_BASALT_SLAB = BLOCKS.register("smooth_basalt_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(0.4f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.BASALT)));
        public static final RegistryObject<Block> MOSS_BLOCK_SLAB = BLOCKS.register(
                "moss_block_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.MOSS, MaterialColor.COLOR_GREEN)
                                .strength(0.4f)
                                .sound(net.minecraft.world.level.block.SoundType.MOSS)));
        public static final RegistryObject<Block> AMETHYST_BLOCK_SLAB = BLOCKS.register("amethyst_block_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.AMETHYST,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(0.4f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.AMETHYST)));
        public static final RegistryObject<Block> MOSSY_CALCITE = BLOCKS.register(
                "mossy_calcite",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_WHITE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> MOSSY_CALCITE_STAIRS = BLOCKS.register("mossy_calcite_stairs",
                () -> new ModStairBlock(
                        MOSSY_CALCITE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_WHITE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> MOSSY_CALCITE_SLAB = BLOCKS.register("mossy_calcite_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_WHITE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> MOSSY_CALCITE_WALL = BLOCKS.register("mossy_calcite_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.TERRACOTTA_WHITE)
                                .strength(0.75f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CALCITE)));
        public static final RegistryObject<Block> BIT_COPPER_BLOCK_SLAB = BLOCKS.register("bit_copper_block_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_COPPER_BLOCK_WALL = BLOCKS.register("bit_copper_block_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_EXPOSED_COPPER_BLOCK_SLAB = BLOCKS
                .register("bit_exposed_copper_block_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.TERRACOTTA_LIGHT_GRAY)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_EXPOSED_COPPER_BLOCK_WALL = BLOCKS
                .register("bit_exposed_copper_block_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.TERRACOTTA_LIGHT_GRAY)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_WEATHERED_COPPER_BLOCK_SLAB = BLOCKS
                .register("bit_weathered_copper_block_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.WARPED_NYLIUM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_WEATHERED_COPPER_BLOCK_WALL = BLOCKS
                .register("bit_weathered_copper_block_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.WARPED_NYLIUM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_OXIDIZED_COPPER_BLOCK_SLAB = BLOCKS
                .register("bit_oxidized_copper_block_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_OXIDIZED_COPPER_BLOCK_WALL = BLOCKS
                .register("bit_oxidized_copper_block_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_CUT_COPPER_SLAB = BLOCKS.register("bit_cut_copper_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_CUT_COPPER_WALL = BLOCKS.register("bit_cut_copper_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_EXPOSED_CUT_COPPER_SLAB = BLOCKS
                .register("bit_exposed_cut_copper_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.TERRACOTTA_LIGHT_GRAY)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_EXPOSED_CUT_COPPER_WALL = BLOCKS
                .register("bit_exposed_cut_copper_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.TERRACOTTA_LIGHT_GRAY)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_WEATHERED_CUT_COPPER_SLAB = BLOCKS
                .register("bit_weathered_cut_copper_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.WARPED_NYLIUM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_WEATHERED_CUT_COPPER_WALL = BLOCKS
                .register("bit_weathered_cut_copper_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.WARPED_NYLIUM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_OXIDIZED_CUT_COPPER_SLAB = BLOCKS
                .register("bit_oxidized_cut_copper_slab", () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));
        public static final RegistryObject<Block> BIT_OXIDIZED_CUT_COPPER_WALL = BLOCKS
                .register("bit_oxidized_cut_copper_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.COPPER)));

        public static final RegistryObject<Block> MOSS_LAYERS = BLOCKS.register(
                "moss_layers",
                () -> new MossLayersBlock(
                        BlockBehaviour.Properties.of(
                                        Material.REPLACEABLE_PLANT,
                                        MaterialColor.COLOR_GREEN)
                                .strength(0.4f)
                                .sound(net.minecraft.world.level.block.SoundType.MOSS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));

        public static final RegistryObject<Block> MOSS_OVERLAY = BLOCKS.register(
                "moss_overlay",
                () -> new MossOverlayBlock(
                        BlockBehaviour.Properties.of(
                                        Material.REPLACEABLE_PLANT,
                                        MaterialColor.COLOR_GREEN)
                                .strength(0.4f)
                                .sound(net.minecraft.world.level.block.SoundType.MOSS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));

        public static final RegistryObject<Block> SNOW_OVERLAY = BLOCKS.register(
                "snow_overlay",
                () -> new SnowOverlayBlock(
                        BlockBehaviour.Properties.of(
                                        Material.REPLACEABLE_PLANT,
                                        MaterialColor.SNOW)
                                .strength(0.4f)
                                .sound(net.minecraft.world.level.block.SoundType.SNOW)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));

        public static final RegistryObject<Block> BLACK_CONCRETE_STAIRS = BLOCKS.register("black_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.BLACK_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_CONCRETE_STAIRS = BLOCKS.register("blue_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.BLUE_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BROWN_CONCRETE_STAIRS = BLOCKS.register("brown_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.BROWN_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> CYAN_CONCRETE_STAIRS = BLOCKS.register("cyan_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.CYAN_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GRAY_CONCRETE_STAIRS = BLOCKS.register("gray_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.GRAY_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_CONCRETE_STAIRS = BLOCKS.register("green_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.GREEN_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_BLUE_CONCRETE_STAIRS = BLOCKS
                .register("light_blue_concrete_stairs", () -> new ModStairBlock(
                        Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_GRAY_CONCRETE_STAIRS = BLOCKS
                .register("light_gray_concrete_stairs", () -> new ModStairBlock(
                        Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> LIME_CONCRETE_STAIRS = BLOCKS.register("lime_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.LIME_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> MAGENTA_CONCRETE_STAIRS = BLOCKS.register("magenta_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.MAGENTA_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_CONCRETE_STAIRS = BLOCKS.register("orange_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.ORANGE_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PINK_CONCRETE_STAIRS = BLOCKS.register("pink_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.PINK_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PURPLE_CONCRETE_STAIRS = BLOCKS.register("purple_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.PURPLE_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> RED_CONCRETE_STAIRS = BLOCKS.register("red_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.RED_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_CONCRETE_STAIRS = BLOCKS.register("white_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.WHITE_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_CONCRETE_STAIRS = BLOCKS.register("yellow_concrete_stairs",
                () -> new ModStairBlock(
                        Blocks.YELLOW_CONCRETE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));

        public static final RegistryObject<Block> WHITE_CONCRETE_WALL = BLOCKS.register("white_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_GRAY_CONCRETE_WALL = BLOCKS.register("light_gray_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GRAY_CONCRETE_WALL = BLOCKS.register("gray_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_CONCRETE_WALL = BLOCKS.register("black_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BROWN_CONCRETE_WALL = BLOCKS.register("brown_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> RED_CONCRETE_WALL = BLOCKS.register(
                "red_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_CONCRETE_WALL = BLOCKS.register("orange_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_CONCRETE_WALL = BLOCKS.register("yellow_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> LIME_CONCRETE_WALL = BLOCKS.register("lime_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_CONCRETE_WALL = BLOCKS.register("green_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> CYAN_CONCRETE_WALL = BLOCKS.register("cyan_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_BLUE_CONCRETE_WALL = BLOCKS.register("light_blue_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_CONCRETE_WALL = BLOCKS.register("blue_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PURPLE_CONCRETE_WALL = BLOCKS.register("purple_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> MAGENTA_CONCRETE_WALL = BLOCKS.register("magenta_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PINK_CONCRETE_WALL = BLOCKS.register("pink_concrete_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));

        public static final RegistryObject<Block> BLACK_CONCRETE_SLAB = BLOCKS.register("black_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_CONCRETE_SLAB = BLOCKS.register("blue_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> BROWN_CONCRETE_SLAB = BLOCKS.register("brown_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> CYAN_CONCRETE_SLAB = BLOCKS.register("cyan_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GRAY_CONCRETE_SLAB = BLOCKS.register("gray_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_CONCRETE_SLAB = BLOCKS.register("green_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_BLUE_CONCRETE_SLAB = BLOCKS.register("light_blue_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_GRAY_CONCRETE_SLAB = BLOCKS.register("light_gray_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> LIME_CONCRETE_SLAB = BLOCKS.register("lime_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> MAGENTA_CONCRETE_SLAB = BLOCKS.register("magenta_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.STONE,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_CONCRETE_SLAB = BLOCKS.register("orange_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PINK_CONCRETE_SLAB = BLOCKS.register("pink_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> PURPLE_CONCRETE_SLAB = BLOCKS.register("purple_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> RED_CONCRETE_SLAB = BLOCKS.register(
                "red_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_CONCRETE_SLAB = BLOCKS.register("white_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_CONCRETE_SLAB = BLOCKS.register("yellow_concrete_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));

        public static final RegistryObject<Block> BROWN_MUSHROOM_SHELVES = BLOCKS.register("brown_mushroom_shelves",
                () -> new MushroomShelvesBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                .noOcclusion()));

        public static final RegistryObject<Block> RED_MUSHROOM_SHELVES = BLOCKS.register("red_mushroom_shelves",
                () -> new MushroomShelvesBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                .noOcclusion()));

        public static final RegistryObject<Block> QUARTZ_PILLAR = BLOCKS.register(
                "quartz_pillar",
                () -> new PillarBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.STONE)
                                .noOcclusion()));

        public static final RegistryObject<Block> QUARTZ_PILLAR_STAIRS = BLOCKS.register("quartz_pillar_stairs",
                () -> new ModStairBlock(
                        QUARTZ_PILLAR.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));

        public static final RegistryObject<Block> QUARTZ_PILLAR_SLAB = BLOCKS.register("quartz_pillar_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));

        public static final RegistryObject<Block> QUARTZ_PILLAR_WALL = BLOCKS.register("quartz_pillar_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                                .strength(0.8f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.STONE)));

        public static final RegistryObject<Block> STONE_PILLAR = BLOCKS.register(
                "stone_pillar",
                () -> new PillarBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE)
                                .strength(1.5f, 6.0f)
                                .sound(net.minecraft.world.level.block.SoundType.STONE)
                                .noOcclusion()));

        public static final RegistryObject<Block> DEEPSLATE_PILLAR = BLOCKS.register(
                "deepslate_pillar",
                () -> new PillarBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(3.0f, 6.0f)
                                .sound(net.minecraft.world.level.block.SoundType.DEEPSLATE)
                                .noOcclusion()));

        public static final RegistryObject<Block> MOSSY_PILLAR = BLOCKS.register(
                "mossy_pillar",
                () -> new PillarBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(2.0f, 6.0f)
                                .sound(net.minecraft.world.level.block.SoundType.STONE)
                                .noOcclusion()));

        public static final RegistryObject<Block> ASHENKING_DIAMOND_PILLAR = BLOCKS.register(
                "ashenking_diamond_pillar",
                () -> new AshenKingPillarBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIAMOND)
                                .strength(3.0f, 6.0f)
                                .sound(net.minecraft.world.level.block.SoundType.STONE)
                                .lightLevel(state -> 15)
                                .noOcclusion()));

        public static final RegistryObject<Block> ASHENKING_GOLD_PILLAR = BLOCKS.register(
                "ashenking_gold_pillar",
                () -> new AshenKingPillarBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.GOLD)
                                .strength(3.0f, 6.0f)
                                .sound(net.minecraft.world.level.block.SoundType.STONE)
                                .lightLevel(state -> 15)
                                .noOcclusion()));

        public static final RegistryObject<Block> ASHENKING_EMERALD_PILLAR = BLOCKS.register(
                "ashenking_emerald_pillar",
                () -> new AshenKingPillarBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.EMERALD)
                                .strength(3.0f, 6.0f)
                                .sound(net.minecraft.world.level.block.SoundType.STONE)
                                .lightLevel(state -> 15)
                                .noOcclusion()));

        public static final RegistryObject<Block> ASHENKING_NETHERITE_PILLAR = BLOCKS.register(
                "ashenking_netherite_pillar",
                () -> new AshenKingPillarBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(5.0f, 6.0f)
                                .sound(net.minecraft.world.level.block.SoundType.STONE)
                                .lightLevel(state -> 15)
                                .noOcclusion()));

        public static final RegistryObject<Block> DECORATED_POT = BLOCKS.register(
                "decorated_pot",
                () -> new DecoratedPotBlock());

        public static final RegistryObject<Block> BLACK_DECORATED_POT = BLOCKS.register("black_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> BLUE_DECORATED_POT = BLOCKS.register("blue_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> BROWN_DECORATED_POT = BLOCKS.register("brown_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> CYAN_DECORATED_POT = BLOCKS.register("cyan_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> GRAY_DECORATED_POT = BLOCKS.register("gray_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> GREEN_DECORATED_POT = BLOCKS.register("green_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> LIGHT_BLUE_DECORATED_POT = BLOCKS.register("light_blue_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> LIGHT_GRAY_DECORATED_POT = BLOCKS.register("light_gray_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> LIME_DECORATED_POT = BLOCKS.register("lime_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> MAGENTA_DECORATED_POT = BLOCKS.register("magenta_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> ORANGE_DECORATED_POT = BLOCKS.register("orange_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> PINK_DECORATED_POT = BLOCKS.register("pink_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> PURPLE_DECORATED_POT = BLOCKS.register("purple_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> RED_DECORATED_POT = BLOCKS.register(
                "red_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> WHITE_DECORATED_POT = BLOCKS.register("white_decorated_pot",
                () -> new DecoratedPotBlock());
        public static final RegistryObject<Block> YELLOW_DECORATED_POT = BLOCKS.register("yellow_decorated_pot",
                () -> new DecoratedPotBlock());

        // =====================================================================
        //  Trapped Decorated Pots – same textures, spawn-egg trap mechanic
        // =====================================================================

        public static final RegistryObject<Block> TRAPPED_DECORATED_POT = BLOCKS.register("trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> BLACK_TRAPPED_DECORATED_POT = BLOCKS.register("black_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> BLUE_TRAPPED_DECORATED_POT = BLOCKS.register("blue_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> BROWN_TRAPPED_DECORATED_POT = BLOCKS.register("brown_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> CYAN_TRAPPED_DECORATED_POT = BLOCKS.register("cyan_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> GRAY_TRAPPED_DECORATED_POT = BLOCKS.register("gray_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> GREEN_TRAPPED_DECORATED_POT = BLOCKS.register("green_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> LIGHT_BLUE_TRAPPED_DECORATED_POT = BLOCKS.register("light_blue_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> LIGHT_GRAY_TRAPPED_DECORATED_POT = BLOCKS.register("light_gray_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> LIME_TRAPPED_DECORATED_POT = BLOCKS.register("lime_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> MAGENTA_TRAPPED_DECORATED_POT = BLOCKS.register("magenta_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> ORANGE_TRAPPED_DECORATED_POT = BLOCKS.register("orange_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> PINK_TRAPPED_DECORATED_POT = BLOCKS.register("pink_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> PURPLE_TRAPPED_DECORATED_POT = BLOCKS.register("purple_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> RED_TRAPPED_DECORATED_POT = BLOCKS.register("red_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> WHITE_TRAPPED_DECORATED_POT = BLOCKS.register("white_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());
        public static final RegistryObject<Block> YELLOW_TRAPPED_DECORATED_POT = BLOCKS.register("yellow_trapped_decorated_pot",
                () -> new TrappedDecoratedPotBlock());

        public static final RegistryObject<Block> BLACK_CARPET_LAYERS = BLOCKS.register("black_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BLACK)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "black"));
        public static final RegistryObject<Block> BLUE_CARPET_LAYERS = BLOCKS.register("blue_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BLUE)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "blue"));
        public static final RegistryObject<Block> BROWN_CARPET_LAYERS = BLOCKS.register("brown_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BROWN)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "brown"));
        public static final RegistryObject<Block> CYAN_CARPET_LAYERS = BLOCKS.register("cyan_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_CYAN)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "cyan"));
        public static final RegistryObject<Block> GRAY_CARPET_LAYERS = BLOCKS.register("gray_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GRAY)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "gray"));
        public static final RegistryObject<Block> GREEN_CARPET_LAYERS = BLOCKS.register("green_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GREEN)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "green"));
        public static final RegistryObject<Block> LIGHT_BLUE_CARPET_LAYERS = BLOCKS.register("light_blue_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOL,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "light_blue"));
        public static final RegistryObject<Block> LIGHT_GRAY_CARPET_LAYERS = BLOCKS.register("light_gray_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOL,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "light_gray"));
        public static final RegistryObject<Block> LIME_CARPET_LAYERS = BLOCKS.register("lime_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOL,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "lime"));
        public static final RegistryObject<Block> MAGENTA_CARPET_LAYERS = BLOCKS.register("magenta_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_MAGENTA)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "magenta"));
        public static final RegistryObject<Block> ORANGE_CARPET_LAYERS = BLOCKS.register("orange_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_ORANGE)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "orange"));
        public static final RegistryObject<Block> PINK_CARPET_LAYERS = BLOCKS.register("pink_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_PINK)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "pink"));
        public static final RegistryObject<Block> PURPLE_CARPET_LAYERS = BLOCKS.register("purple_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_PURPLE)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "purple"));
        public static final RegistryObject<Block> RED_CARPET_LAYERS = BLOCKS.register(
                "red_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_RED)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "red"));
        public static final RegistryObject<Block> WHITE_CARPET_LAYERS = BLOCKS.register("white_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.SNOW)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "white"));
        public static final RegistryObject<Block> YELLOW_CARPET_LAYERS = BLOCKS.register("yellow_carpet_layers",
                () -> new WoolLayersBlock(
                        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_YELLOW)
                                .strength(0.8f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "yellow"));

        public static final RegistryObject<Block> OAK_LEAF_LAYERS = BLOCKS.register(
                "oak_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "oak"));

        public static final RegistryObject<Block> SPRUCE_LEAF_LAYERS = BLOCKS.register("spruce_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PODZOL)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "spruce"));

        public static final RegistryObject<Block> BIRCH_LEAF_LAYERS = BLOCKS.register(
                "birch_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.SAND)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "birch"));

        public static final RegistryObject<Block> JUNGLE_LEAF_LAYERS = BLOCKS.register("jungle_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.DIRT)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "jungle"));

        public static final RegistryObject<Block> ACACIA_LEAF_LAYERS = BLOCKS.register("acacia_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(
                                        Material.LEAVES,
                                        MaterialColor.COLOR_ORANGE)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "acacia"));

        public static final RegistryObject<Block> DARK_OAK_LEAF_LAYERS = BLOCKS.register("dark_oak_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.COLOR_BROWN)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "dark_oak"));

        public static final RegistryObject<Block> AZALEA_LEAF_LAYERS = BLOCKS.register("azalea_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                                .strength(0.2f)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "azalea"));

        public static final RegistryObject<Block> FLOWERING_AZALEA_LEAF_LAYERS = BLOCKS
                .register("flowering_azalea_leaf_layers", () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                                .strength(0.2f)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "flowering_azalea"));

        public static final RegistryObject<Block> SNOWY_OAK_LEAF_LAYERS = BLOCKS.register("snowy_oak_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.SNOW)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "snowy_oak"));

        public static final RegistryObject<Block> SNOWY_SPRUCE_LEAF_LAYERS = BLOCKS.register("snowy_spruce_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PODZOL)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "snowy_spruce"));

        public static final RegistryObject<Block> SNOWY_BIRCH_LEAF_LAYERS = BLOCKS.register("snowy_birch_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.SAND)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "snowy_birch"));

        public static final RegistryObject<Block> SNOWY_JUNGLE_LEAF_LAYERS = BLOCKS.register("snowy_jungle_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.DIRT)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "snowy_jungle"));

        public static final RegistryObject<Block> SNOWY_ACACIA_LEAF_LAYERS = BLOCKS.register("snowy_acacia_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(
                                        Material.LEAVES,
                                        MaterialColor.COLOR_ORANGE)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "snowy_acacia"));

        public static final RegistryObject<Block> SNOWY_DARK_OAK_LEAF_LAYERS = BLOCKS
                .register("snowy_dark_oak_leaf_layers", () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.COLOR_BROWN)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "snowy_dark_oak"));

        public static final RegistryObject<Block> SNOWY_MANGROVE_LEAF_LAYERS = BLOCKS
                .register("snowy_mangrove_leaf_layers", () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "snowy_mangrove"));

        public static final RegistryObject<Block> SNOWY_AZALEA_LEAF_LAYERS = BLOCKS.register("snowy_azalea_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                                .strength(0.2f)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "snowy_azalea"));

        public static final RegistryObject<Block> SNOWY_FLOWERING_AZALEA_LEAF_LAYERS = BLOCKS
                .register("snowy_flowering_azalea_leaf_layers", () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                                .strength(0.2f)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "snowy_flowering_azalea"));

        public static final RegistryObject<Block> MANGROVE_LEAF_LAYERS = BLOCKS.register("mangrove_leaf_layers",
                () -> new LeafLayersBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        "mangrove"));

        public static final RegistryObject<Block> OAK_LEAF_HEDGE = BLOCKS.register(
                "oak_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.OAK_LEAF_HEDGE));

        public static final RegistryObject<Block> SPRUCE_LEAF_HEDGE = BLOCKS.register(
                "spruce_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PODZOL)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.SPRUCE_LEAF_HEDGE));

        public static final RegistryObject<Block> BIRCH_LEAF_HEDGE = BLOCKS.register(
                "birch_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SAND)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.BIRCH_LEAF_HEDGE));

        public static final RegistryObject<Block> JUNGLE_LEAF_HEDGE = BLOCKS.register(
                "jungle_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.DIRT)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.JUNGLE_LEAF_HEDGE));

        public static final RegistryObject<Block> ACACIA_LEAF_HEDGE = BLOCKS.register(
                "acacia_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_ORANGE)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.ACACIA_LEAF_HEDGE));

        public static final RegistryObject<Block> DARK_OAK_LEAF_HEDGE = BLOCKS.register("dark_oak_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BROWN)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.DARK_OAK_LEAF_HEDGE));

        public static final RegistryObject<Block> AZALEA_LEAF_HEDGE = BLOCKS.register(
                "azalea_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.AZALEA_LEAF_HEDGE));

        public static final RegistryObject<Block> FLOWERING_AZALEA_LEAF_HEDGE = BLOCKS
                .register("flowering_azalea_leaf_hedge", () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.FLOWERING_AZALEA_LEAF_HEDGE));

        public static final RegistryObject<Block> SNOWY_OAK_LEAF_HEDGE = BLOCKS.register("snowy_oak_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.SNOWY_OAK_LEAF_HEDGE));

        public static final RegistryObject<Block> SNOWY_SPRUCE_LEAF_HEDGE = BLOCKS.register("snowy_spruce_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PODZOL)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.SNOWY_SPRUCE_LEAF_HEDGE));

        public static final RegistryObject<Block> SNOWY_BIRCH_LEAF_HEDGE = BLOCKS.register("snowy_birch_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SAND)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.SNOWY_BIRCH_LEAF_HEDGE));

        public static final RegistryObject<Block> SNOWY_JUNGLE_LEAF_HEDGE = BLOCKS.register("snowy_jungle_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.DIRT)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.SNOWY_JUNGLE_LEAF_HEDGE));

        public static final RegistryObject<Block> SNOWY_ACACIA_LEAF_HEDGE = BLOCKS.register("snowy_acacia_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_ORANGE)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.SNOWY_ACACIA_LEAF_HEDGE));

        public static final RegistryObject<Block> SNOWY_DARK_OAK_LEAF_HEDGE = BLOCKS
                .register("snowy_dark_oak_leaf_hedge", () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BROWN)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.SNOWY_DARK_OAK_LEAF_HEDGE));

        public static final RegistryObject<Block> SNOWY_MANGROVE_LEAF_HEDGE = BLOCKS
                .register("snowy_mangrove_leaf_hedge", () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.SNOWY_MANGROVE_LEAF_HEDGE));

        public static final RegistryObject<Block> SNOWY_AZALEA_LEAF_HEDGE = BLOCKS.register("snowy_azalea_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.SNOWY_AZALEA_LEAF_HEDGE));

        public static final RegistryObject<Block> SNOWY_FLOWERING_AZALEA_LEAF_HEDGE = BLOCKS
                .register("snowy_flowering_azalea_leaf_hedge", () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE));

        public static final RegistryObject<Block> MANGROVE_LEAF_HEDGE = BLOCKS.register("mangrove_leaf_hedge",
                () -> new LeafHedgeBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                                .strength(0.2F)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false),
                        ModItems.MANGROVE_LEAF_HEDGE));

        public static final RegistryObject<Block> HAY_BALE_SLAB = BLOCKS.register(
                "hay_bale_slab",
                () -> new HayBaleSlabBlock(
                        BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.COLOR_YELLOW)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));

        public static final RegistryObject<Block> BAMBOO_BLOCK = BLOCKS.register(
                "bamboo_block",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)));
        public static final RegistryObject<Block> BAMBOO_BLOCK_STAIRS = BLOCKS.register("bamboo_block_stairs",
                () -> new ModStairBlock(
                        BAMBOO_BLOCK.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)));
        public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK = BLOCKS.register("stripped_bamboo_block",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)));
        public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_STAIRS = BLOCKS.register(
                "stripped_bamboo_block_stairs",
                () -> new StairBlock(
                        () -> STRIPPED_BAMBOO_BLOCK.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PLANT)
                                .strength(2.0f)
                                .sound(SoundType.BAMBOO)));
        public static final RegistryObject<Block> BAMBOO_WOOD = BLOCKS.register(
                "bamboo_wood",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)));
        public static final RegistryObject<Block> STRIPPED_BAMBOO_WOOD = BLOCKS.register(
                "stripped_bamboo_wood",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)));
        public static final RegistryObject<Block> BAMBOO_BLOCK_SLAB = BLOCKS.register(
                "bamboo_block_slab",
                () -> new LogSlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)));
        public static final RegistryObject<Block> BAMBOO_BLOCK_FENCE = BLOCKS.register("bamboo_block_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)));
        public static final RegistryObject<Block> BAMBOO_BLOCK_FENCE_GATE = BLOCKS.register("bamboo_block_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)));
        public static final RegistryObject<Block> BAMBOO_BLOCK_PRESSURE_PLATE = BLOCKS
                .register("bamboo_block_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)
                                .noCollission()));
        public static final RegistryObject<Block> BAMBOO_BLOCK_BUTTON = BLOCKS.register("bamboo_block_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_YELLOW)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)
                                .noCollission()));
        public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_SLAB = BLOCKS
                .register("stripped_bamboo_block_slab", () -> new LogSlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)));
        public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_FENCE = BLOCKS
                .register("stripped_bamboo_block_fence", () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)));
        public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_FENCE_GATE = BLOCKS
                .register("stripped_bamboo_block_fence_gate", () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)));
        public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_PRESSURE_PLATE = BLOCKS.register(
                "stripped_bamboo_block_pressure_plate",
                () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)
                                .noCollission()));
        public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_BUTTON = BLOCKS
                .register("stripped_bamboo_block_button", () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_YELLOW)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)
                                .noCollission()));
        public static final RegistryObject<Block> BAMBOO_DOOR = BLOCKS.register(
                "bamboo_door",
                () -> new ModDoorBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(3.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)
                                .noOcclusion()));
        public static final RegistryObject<Block> BAMBOO_TRAPDOOR = BLOCKS.register(
                "bamboo_trapdoor",
                () -> new ModTrapdoorBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(3.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)
                                .noOcclusion()
                                .isValidSpawn((state, reader, pos, entityType) -> false)));
        public static final RegistryObject<Block> BAMBOO_SIGN = BLOCKS.register(
                "bamboo_sign",
                () -> new ModBambooStandingSignBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(1.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)
                                .noCollission(),
                        ModWoodTypes.BAMBOO));
        public static final RegistryObject<Block> BAMBOO_WALL_SIGN = BLOCKS.register(
                "bamboo_wall_sign",
                () -> new ModBambooWallSignBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(1.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)
                                .noCollission()
                                .dropsLike(BAMBOO_SIGN.get()),
                        ModWoodTypes.BAMBOO));
        public static final RegistryObject<Block> OAK_WOOD_WALL = BLOCKS.register(
                "oak_wood_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> SPRUCE_WOOD_WALL = BLOCKS.register(
                "spruce_wood_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> BIRCH_WOOD_WALL = BLOCKS.register(
                "birch_wood_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> DARK_OAK_WOOD_WALL = BLOCKS.register("dark_oak_wood_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> JUNGLE_WOOD_WALL = BLOCKS.register(
                "jungle_wood_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.DIRT)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> ACACIA_WOOD_WALL = BLOCKS.register(
                "acacia_wood_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> BAMBOO_BLOCK_WALL = BLOCKS.register(
                "bamboo_block_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)));
        public static final RegistryObject<Block> MANGROVE_WOOD_WALL = BLOCKS.register("mangrove_wood_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_OAK_WOOD_WALL = BLOCKS.register("stripped_oak_wood_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_SPRUCE_WOOD_WALL = BLOCKS
                .register("stripped_spruce_wood_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_BIRCH_WOOD_WALL = BLOCKS.register("stripped_birch_wood_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_DARK_OAK_WOOD_WALL = BLOCKS
                .register("stripped_dark_oak_wood_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_JUNGLE_WOOD_WALL = BLOCKS
                .register("stripped_jungle_wood_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.DIRT)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_ACACIA_WOOD_WALL = BLOCKS
                .register("stripped_acacia_wood_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_WALL = BLOCKS
                .register("stripped_bamboo_block_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.BAMBOO)));
        public static final RegistryObject<Block> STRIPPED_MANGROVE_WOOD_WALL = BLOCKS
                .register("stripped_mangrove_wood_wall", () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> ASHPEN_WHITE_PLANKS = BLOCKS.register("ashpen_white_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SNOW)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ASHPEN_WHITE_STAIRS = BLOCKS.register("ashpen_white_stairs",
                () -> new ModStairBlock(
                        ASHPEN_WHITE_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SNOW)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ASHPEN_WHITE_SLAB = BLOCKS.register(
                "ashpen_white_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SNOW)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ASHPEN_WHITE_FENCE = BLOCKS.register("ashpen_white_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SNOW)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ASHPEN_WHITE_FENCE_GATE = BLOCKS.register("ashpen_white_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SNOW)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ASHPEN_WHITE_PRESSURE_PLATE = BLOCKS
                .register("ashpen_white_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SNOW)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> ASHPEN_WHITE_BUTTON = BLOCKS.register("ashpen_white_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.SNOW)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> BLACK_ASHPEN_PLANKS = BLOCKS.register("ashpen_black_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BLACK_ASHPEN_STAIRS = BLOCKS.register("ashpen_black_stairs",
                () -> new ModStairBlock(
                        BLACK_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BLACK_ASHPEN_SLAB = BLOCKS.register(
                "ashpen_black_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BLACK_ASHPEN_FENCE = BLOCKS.register("ashpen_black_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BLACK_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_black_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BLACK_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_black_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> BLACK_ASHPEN_BUTTON = BLOCKS.register("ashpen_black_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_BLACK)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> BLUE_ASHPEN_PLANKS = BLOCKS.register("ashpen_blue_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLUE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BLUE_ASHPEN_STAIRS = BLOCKS.register("ashpen_blue_stairs",
                () -> new ModStairBlock(
                        BLUE_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLUE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BLUE_ASHPEN_SLAB = BLOCKS.register(
                "ashpen_blue_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLUE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BLUE_ASHPEN_FENCE = BLOCKS.register(
                "ashpen_blue_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLUE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BLUE_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_blue_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLUE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BLUE_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_blue_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLUE)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> BLUE_ASHPEN_BUTTON = BLOCKS.register("ashpen_blue_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_BLUE)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> BROWN_ASHPEN_PLANKS = BLOCKS.register("ashpen_brown_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BROWN_ASHPEN_STAIRS = BLOCKS.register("ashpen_brown_stairs",
                () -> new ModStairBlock(
                        BROWN_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BROWN_ASHPEN_SLAB = BLOCKS.register(
                "ashpen_brown_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BROWN_ASHPEN_FENCE = BLOCKS.register("ashpen_brown_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BROWN_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_brown_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BROWN_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_brown_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> BROWN_ASHPEN_BUTTON = BLOCKS.register("ashpen_brown_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_BROWN)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> CYAN_ASHPEN_PLANKS = BLOCKS.register("ashpen_cyan_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> CYAN_ASHPEN_STAIRS = BLOCKS.register("ashpen_cyan_stairs",
                () -> new ModStairBlock(
                        CYAN_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> CYAN_ASHPEN_SLAB = BLOCKS.register(
                "ashpen_cyan_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> CYAN_ASHPEN_FENCE = BLOCKS.register(
                "ashpen_cyan_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> CYAN_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_cyan_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> CYAN_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_cyan_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> CYAN_ASHPEN_BUTTON = BLOCKS.register("ashpen_cyan_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_CYAN)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> GRAY_ASHPEN_PLANKS = BLOCKS.register("ashpen_gray_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> GRAY_ASHPEN_STAIRS = BLOCKS.register("ashpen_gray_stairs",
                () -> new ModStairBlock(
                        GRAY_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> GRAY_ASHPEN_SLAB = BLOCKS.register(
                "ashpen_gray_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> GRAY_ASHPEN_FENCE = BLOCKS.register(
                "ashpen_gray_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> GRAY_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_gray_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> GRAY_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_gray_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> GRAY_ASHPEN_BUTTON = BLOCKS.register("ashpen_gray_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_GRAY)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> GREEN_ASHPEN_PLANKS = BLOCKS.register("ashpen_green_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> GREEN_ASHPEN_STAIRS = BLOCKS.register("ashpen_green_stairs",
                () -> new ModStairBlock(
                        GREEN_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> GREEN_ASHPEN_SLAB = BLOCKS.register(
                "ashpen_green_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> GREEN_ASHPEN_FENCE = BLOCKS.register("ashpen_green_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> GREEN_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_green_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> GREEN_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_green_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> GREEN_ASHPEN_BUTTON = BLOCKS.register("ashpen_green_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_GREEN)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_PLANKS = BLOCKS.register("ashpen_light_blue_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_STAIRS = BLOCKS.register("ashpen_light_blue_stairs",
                () -> new ModStairBlock(
                        LIGHT_BLUE_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_SLAB = BLOCKS.register("ashpen_light_blue_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_FENCE = BLOCKS.register("ashpen_light_blue_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_FENCE_GATE = BLOCKS
                .register("ashpen_light_blue_fence_gate", () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_light_blue_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_BUTTON = BLOCKS.register("ashpen_light_blue_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_PLANKS = BLOCKS.register("ashpen_light_gray_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_STAIRS = BLOCKS.register("ashpen_light_gray_stairs",
                () -> new ModStairBlock(
                        LIGHT_GRAY_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_SLAB = BLOCKS.register("ashpen_light_gray_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_FENCE = BLOCKS.register("ashpen_light_gray_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_FENCE_GATE = BLOCKS
                .register("ashpen_light_gray_fence_gate", () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_light_gray_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_BUTTON = BLOCKS.register("ashpen_light_gray_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> LIME_ASHPEN_PLANKS = BLOCKS.register("ashpen_lime_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIME_ASHPEN_STAIRS = BLOCKS.register("ashpen_lime_stairs",
                () -> new ModStairBlock(
                        LIME_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIME_ASHPEN_SLAB = BLOCKS.register(
                "ashpen_lime_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIME_ASHPEN_FENCE = BLOCKS.register(
                "ashpen_lime_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIME_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_lime_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> LIME_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_lime_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(
                                        Material.WOOD,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> LIME_ASHPEN_BUTTON = BLOCKS.register("ashpen_lime_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> MAGENTA_ASHPEN_PLANKS = BLOCKS.register("ashpen_magenta_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_MAGENTA)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> MAGENTA_ASHPEN_STAIRS = BLOCKS.register("ashpen_magenta_stairs",
                () -> new ModStairBlock(
                        MAGENTA_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_MAGENTA)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> MAGENTA_ASHPEN_SLAB = BLOCKS.register("ashpen_magenta_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_MAGENTA)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> MAGENTA_ASHPEN_FENCE = BLOCKS.register("ashpen_magenta_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_MAGENTA)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> MAGENTA_ASHPEN_FENCE_GATE = BLOCKS
                .register("ashpen_magenta_fence_gate", () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_MAGENTA)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> MAGENTA_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_magenta_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_MAGENTA)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> MAGENTA_ASHPEN_BUTTON = BLOCKS.register("ashpen_magenta_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> ORANGE_ASHPEN_PLANKS = BLOCKS.register("ashpen_orange_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ORANGE_ASHPEN_STAIRS = BLOCKS.register("ashpen_orange_stairs",
                () -> new ModStairBlock(
                        ORANGE_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ORANGE_ASHPEN_SLAB = BLOCKS.register("ashpen_orange_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ORANGE_ASHPEN_FENCE = BLOCKS.register("ashpen_orange_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ORANGE_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_orange_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ORANGE_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_orange_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> ORANGE_ASHPEN_BUTTON = BLOCKS.register("ashpen_orange_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_ORANGE)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> PINK_ASHPEN_PLANKS = BLOCKS.register("ashpen_pink_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> PINK_ASHPEN_STAIRS = BLOCKS.register("ashpen_pink_stairs",
                () -> new ModStairBlock(
                        PINK_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> PINK_ASHPEN_SLAB = BLOCKS.register(
                "ashpen_pink_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> PINK_ASHPEN_FENCE = BLOCKS.register(
                "ashpen_pink_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> PINK_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_pink_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> PINK_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_pink_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> PINK_ASHPEN_BUTTON = BLOCKS.register("ashpen_pink_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_PINK)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> PURPLE_ASHPEN_PLANKS = BLOCKS.register("ashpen_purple_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PURPLE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> PURPLE_ASHPEN_STAIRS = BLOCKS.register("ashpen_purple_stairs",
                () -> new ModStairBlock(
                        PURPLE_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PURPLE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> PURPLE_ASHPEN_SLAB = BLOCKS.register("ashpen_purple_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PURPLE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> PURPLE_ASHPEN_FENCE = BLOCKS.register("ashpen_purple_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PURPLE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> PURPLE_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_purple_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PURPLE)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> PURPLE_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_purple_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PURPLE)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> PURPLE_ASHPEN_BUTTON = BLOCKS.register("ashpen_purple_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_PURPLE)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> RED_ASHPEN_PLANKS = BLOCKS.register(
                "ashpen_red_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> RED_ASHPEN_STAIRS = BLOCKS.register(
                "ashpen_red_stairs",
                () -> new ModStairBlock(
                        RED_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> RED_ASHPEN_SLAB = BLOCKS.register(
                "ashpen_red_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> RED_ASHPEN_FENCE = BLOCKS.register(
                "ashpen_red_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> RED_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_red_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> RED_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_red_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> RED_ASHPEN_BUTTON = BLOCKS.register(
                "ashpen_red_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_RED)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> YELLOW_ASHPEN_PLANKS = BLOCKS.register("ashpen_yellow_planks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> YELLOW_ASHPEN_STAIRS = BLOCKS.register("ashpen_yellow_stairs",
                () -> new ModStairBlock(
                        YELLOW_ASHPEN_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> YELLOW_ASHPEN_SLAB = BLOCKS.register("ashpen_yellow_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> YELLOW_ASHPEN_FENCE = BLOCKS.register("ashpen_yellow_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> YELLOW_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_yellow_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f, 3.0f)
                                .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> YELLOW_ASHPEN_PRESSURE_PLATE = BLOCKS
                .register("ashpen_yellow_pressure_plate", () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> YELLOW_ASHPEN_BUTTON = BLOCKS.register("ashpen_yellow_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_YELLOW)
                                .strength(0.5f)
                                .sound(SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> DIAMOND_CHAIN = BLOCKS.register(
                "diamond_chain",
                () -> new ClimbableChainBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.DIAMOND)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> GOLD_CHAIN = BLOCKS.register(
                "gold_chain",
                () -> new ClimbableChainBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.GOLD)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> EMERALD_CHAIN = BLOCKS.register(
                "emerald_chain",
                () -> new ClimbableChainBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.EMERALD)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> ANCIENT_STEEL_CHAIN = BLOCKS.register("ancient_steel_chain",
                () -> new ClimbableChainBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> NETHERITE_CHAIN = BLOCKS.register(
                "netherite_chain",
                () -> new ClimbableChainBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
                                .strength(50.0f, 1200.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> LARGE_IRON_CHAIN = BLOCKS.register(
                "large_iron_chain",
                () -> new LargeChainBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> LARGE_GOLD_CHAIN = BLOCKS.register(
                "large_gold_chain",
                () -> new LargeChainBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.GOLD)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> LARGE_DIAMOND_CHAIN = BLOCKS.register("large_diamond_chain",
                () -> new LargeChainBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.DIAMOND)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> LARGE_EMERALD_CHAIN = BLOCKS.register("large_emerald_chain",
                () -> new LargeChainBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.EMERALD)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> LARGE_ANCIENT_STEEL_CHAIN = BLOCKS
                .register("large_ancient_steel_chain", () -> new LargeChainBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> LARGE_NETHERITE_CHAIN = BLOCKS.register("large_netherite_chain",
                () -> new LargeChainBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
                                .strength(50.0f, 1200.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> COPPER_CHAIN = BLOCKS.register(
                "copper_chain",
                () -> new ClimbableChainBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> EXPOSED_COPPER_CHAIN = BLOCKS.register("exposed_copper_chain",
                () -> new ClimbableChainBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.TERRACOTTA_ORANGE)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> WEATHERED_COPPER_CHAIN = BLOCKS.register("weathered_copper_chain",
                () -> new ClimbableChainBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.WARPED_WART_BLOCK)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> OXIDIZED_COPPER_CHAIN = BLOCKS.register("oxidized_copper_chain",
                () -> new ClimbableChainBlock(
                        BlockBehaviour.Properties.of(
                                        Material.METAL,
                                        MaterialColor.WARPED_NYLIUM)
                                .strength(3.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(net.minecraft.world.level.block.SoundType.CHAIN)
                                .noOcclusion()
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> RED_ROSE_VINES = BLOCKS.register(
                "red_rose_vines",
                () -> new RoseVinesBlock(
                        BlockBehaviour.Properties.of(
                                        Material.REPLACEABLE_PLANT,
                                        MaterialColor.COLOR_RED)
                                .strength(0.2f)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.VINE_SOUNDS())
                                .noCollission()
                                .noOcclusion()));
        public static final RegistryObject<Block> BLACK_ROSE_VINES = BLOCKS.register(
                "black_rose_vines",
                () -> new RoseVinesBlock(
                        BlockBehaviour.Properties.of(
                                        Material.REPLACEABLE_PLANT,
                                        MaterialColor.COLOR_BLACK)
                                .strength(0.2f)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.VINE_SOUNDS())
                                .noCollission()
                                .noOcclusion()));
        public static final RegistryObject<Block> BLUE_ROSE_VINES = BLOCKS.register(
                "blue_rose_vines",
                () -> new RoseVinesBlock(
                        BlockBehaviour.Properties.of(
                                        Material.REPLACEABLE_PLANT,
                                        MaterialColor.COLOR_BLUE)
                                .strength(0.2f)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.VINE_SOUNDS())
                                .noCollission()
                                .noOcclusion()));
        public static final RegistryObject<Block> WHITE_ROSE_VINES = BLOCKS.register(
                "white_rose_vines",
                () -> new RoseVinesBlock(
                        BlockBehaviour.Properties.of(
                                        Material.REPLACEABLE_PLANT,
                                        MaterialColor.SNOW)
                                .strength(0.2f)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.VINE_SOUNDS())
                                .noCollission()
                                .noOcclusion()));
        public static final RegistryObject<Block> RED_MONETS = BLOCKS.register(
                "red_monets",
                () -> new MonetFlowerBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_RED)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> BLUE_MONETS = BLOCKS.register(
                "blue_monets",
                () -> new MonetFlowerBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BLUE)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> PURPLE_MONETS = BLOCKS.register(
                "purple_monets",
                () -> new MonetFlowerBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PURPLE)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> LIGHT_BLUE_MONETS = BLOCKS.register(
                "light_blue_monets",
                () -> new MonetFlowerBlock(
                        BlockBehaviour.Properties.of(
                                        Material.PLANT,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> PINK_MONETS = BLOCKS.register(
                "pink_monets",
                () -> new MonetFlowerBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PINK)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> YELLOW_MONETS = BLOCKS.register(
                "yellow_monets",
                () -> new MonetFlowerBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_YELLOW)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> CLOVER = BLOCKS.register(
                "clover",
                () -> new CloverBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                                .noCollission()
                                .instabreak()
                                .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> RED_PETAL = BLOCKS.register(
                "red_petal",
                () -> new PetalBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_RED)
                                .noCollission()
                                .instabreak()
                                .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> BLUE_PETAL = BLOCKS.register(
                "blue_petal",
                () -> new PetalBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BLUE)
                                .noCollission()
                                .instabreak()
                                .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> ORANGE_PETAL = BLOCKS.register(
                "orange_petal",
                () -> new PetalBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_ORANGE)
                                .noCollission()
                                .instabreak()
                                .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> PINK_PETAL = BLOCKS.register(
                "pink_petal",
                () -> new PetalBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PINK)
                                .noCollission()
                                .instabreak()
                                .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> PURPLE_PETAL = BLOCKS.register(
                "purple_petal",
                () -> new PetalBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PURPLE)
                                .noCollission()
                                .instabreak()
                                .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> RED_SPORE_BLOSSOM = BLOCKS.register(
                "red_spore_blossom",
                () -> new ColoredSporeBlossomBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_RED)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.SPORE_BLOSSOM),
                        ColoredSporeBlossomBlock.hexToVector3f("#fe4a4e"),
                        ColoredSporeBlossomBlock.hexToRGB("#fe4a4e")));
        public static final RegistryObject<Block> CYAN_SPORE_BLOSSOM = BLOCKS.register("cyan_spore_blossom",
                () -> new ColoredSporeBlossomBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_CYAN)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.SPORE_BLOSSOM),
                        ColoredSporeBlossomBlock.hexToVector3f("#1fdee2"),
                        ColoredSporeBlossomBlock.hexToRGB("#1fdee2")));
        public static final RegistryObject<Block> BLUE_SPORE_BLOSSOM = BLOCKS.register("blue_spore_blossom",
                () -> new ColoredSporeBlossomBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BLUE)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.SPORE_BLOSSOM),
                        ColoredSporeBlossomBlock.hexToVector3f("#4a7df4"),
                        ColoredSporeBlossomBlock.hexToRGB("#4a7df4")));
        public static final RegistryObject<Block> PURPLE_SPORE_BLOSSOM = BLOCKS.register("purple_spore_blossom",
                () -> new ColoredSporeBlossomBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PURPLE)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.SPORE_BLOSSOM),
                        ColoredSporeBlossomBlock.hexToVector3f("#a146f2"),
                        ColoredSporeBlossomBlock.hexToRGB("#a146f2")));
        public static final RegistryObject<Block> ORANGE_SPORE_BLOSSOM = BLOCKS.register("orange_spore_blossom",
                () -> new ColoredSporeBlossomBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_ORANGE)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.SPORE_BLOSSOM),
                        ColoredSporeBlossomBlock.hexToVector3f("#f76e32"),
                        ColoredSporeBlossomBlock.hexToRGB("#f76e32")));
        public static final RegistryObject<Block> BIG_CANDLE = BLOCKS.register(
                "big_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_WHITE_CANDLE = BLOCKS.register(
                "big_white_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_ORANGE_CANDLE = BLOCKS.register(
                "big_orange_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_MAGENTA_CANDLE = BLOCKS.register("big_magenta_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_LIGHT_BLUE_CANDLE = BLOCKS.register("big_light_blue_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_YELLOW_CANDLE = BLOCKS.register(
                "big_yellow_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_LIME_CANDLE = BLOCKS.register(
                "big_lime_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_PINK_CANDLE = BLOCKS.register(
                "big_pink_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_GRAY_CANDLE = BLOCKS.register(
                "big_gray_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_LIGHT_GRAY_CANDLE = BLOCKS.register("big_light_gray_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_CYAN_CANDLE = BLOCKS.register(
                "big_cyan_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_PURPLE_CANDLE = BLOCKS.register(
                "big_purple_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_BLUE_CANDLE = BLOCKS.register(
                "big_blue_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_BROWN_CANDLE = BLOCKS.register(
                "big_brown_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_GREEN_CANDLE = BLOCKS.register(
                "big_green_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_RED_CANDLE = BLOCKS.register(
                "big_red_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_BLACK_CANDLE = BLOCKS.register(
                "big_black_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_AMETHYST_CANDLE = BLOCKS.register("big_amethyst_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> BIG_SCULK_CANDLE = BLOCKS.register(
                "big_sculk_candle",
                () -> new BigCandleBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.CANDLE)
                                .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                        : 0)));
        public static final RegistryObject<Block> WHITE_ORNAMENT = BLOCKS.register(
                "white_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> ORANGE_ORNAMENT = BLOCKS.register(
                "orange_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> MAGENTA_ORNAMENT = BLOCKS.register(
                "magenta_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> LIGHT_BLUE_ORNAMENT = BLOCKS.register("light_blue_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> YELLOW_ORNAMENT = BLOCKS.register(
                "yellow_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> LIME_ORNAMENT = BLOCKS.register(
                "lime_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> PINK_ORNAMENT = BLOCKS.register(
                "pink_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> GRAY_ORNAMENT = BLOCKS.register(
                "gray_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> LIGHT_GRAY_ORNAMENT = BLOCKS.register("light_gray_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> CYAN_ORNAMENT = BLOCKS.register(
                "cyan_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> PURPLE_ORNAMENT = BLOCKS.register(
                "purple_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> BLUE_ORNAMENT = BLOCKS.register(
                "blue_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> BROWN_ORNAMENT = BLOCKS.register(
                "brown_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> GREEN_ORNAMENT = BLOCKS.register(
                "green_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> RED_ORNAMENT = BLOCKS.register(
                "red_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> BLACK_ORNAMENT = BLOCKS.register(
                "black_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> GLASS_ORNAMENT = BLOCKS.register(
                "glass_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.NONE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> 7)));
        public static final RegistryObject<Block> TINTED_GLASS_ORNAMENT = BLOCKS.register("tinted_glass_ornament",
                () -> new OrnamentBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.NONE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .dynamicShape()
                                .lightLevel(state -> 4)));
        public static final RegistryObject<Block> WHITE_STRING_LIGHT = BLOCKS.register("white_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> ORANGE_STRING_LIGHT = BLOCKS.register("orange_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> MAGENTA_STRING_LIGHT = BLOCKS.register("magenta_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_MAGENTA)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIGHT_BLUE_STRING_LIGHT = BLOCKS.register("light_blue_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> YELLOW_STRING_LIGHT = BLOCKS.register("yellow_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIME_STRING_LIGHT = BLOCKS.register(
                "lime_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> PINK_STRING_LIGHT = BLOCKS.register(
                "pink_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> GRAY_STRING_LIGHT = BLOCKS.register(
                "gray_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> LIGHT_GRAY_STRING_LIGHT = BLOCKS.register("light_gray_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(
                                        Material.GLASS,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> CYAN_STRING_LIGHT = BLOCKS.register(
                "cyan_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> PURPLE_STRING_LIGHT = BLOCKS.register("purple_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BLUE_STRING_LIGHT = BLOCKS.register(
                "blue_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BROWN_STRING_LIGHT = BLOCKS.register("brown_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> GREEN_STRING_LIGHT = BLOCKS.register("green_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> RED_STRING_LIGHT = BLOCKS.register(
                "red_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> BLACK_STRING_LIGHT = BLOCKS.register("black_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> MULTICOLOR_STRING_LIGHT = BLOCKS.register("multicolor_string_light",
                () -> new StringLightBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.NONE)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .lightLevel(state -> state.getValue(StringLightBlock.LIT) ? 15
                                        : 0)));
        public static final RegistryObject<Block> WHITE_STAR = BLOCKS.register(
                "white_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> ORANGE_STAR = BLOCKS.register(
                "orange_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_ORANGE)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> MAGENTA_STAR = BLOCKS.register(
                "magenta_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(
                                        Material.PLANT,
                                        MaterialColor.COLOR_MAGENTA)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> LIGHT_BLUE_STAR = BLOCKS.register(
                "light_blue_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(
                                        Material.PLANT,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> YELLOW_STAR = BLOCKS.register(
                "yellow_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_YELLOW)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> LIME_STAR = BLOCKS.register(
                "lime_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(
                                        Material.PLANT,
                                        MaterialColor.COLOR_LIGHT_GREEN)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> PINK_STAR = BLOCKS.register(
                "pink_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PINK)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> GRAY_STAR = BLOCKS.register(
                "gray_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_GRAY)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> LIGHT_GRAY_STAR = BLOCKS.register(
                "light_gray_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(
                                        Material.PLANT,
                                        MaterialColor.COLOR_LIGHT_GRAY)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> CYAN_STAR = BLOCKS.register(
                "cyan_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_CYAN)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> PURPLE_STAR = BLOCKS.register(
                "purple_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PURPLE)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BLUE_STAR = BLOCKS.register(
                "blue_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BLUE)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BROWN_STAR = BLOCKS.register(
                "brown_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BROWN)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> GREEN_STAR = BLOCKS.register(
                "green_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_GREEN)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> RED_STAR = BLOCKS.register(
                "red_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_RED)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BLACK_STAR = BLOCKS.register(
                "black_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BLACK)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> GLOW_STAR = BLOCKS.register(
                "glow_star",
                () -> new StarBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.NONE)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> SNOWY_LEAVES = BLOCKS.register(
                "snowy_leaves",
                () -> new SnowyLeavesBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isValidSpawn((state, reader, pos, entityType) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_OAK_LEAVES = BLOCKS.register(
                "snowy_oak_leaves",
                () -> new SnowyLeavesBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isValidSpawn((state, reader, pos, entityType) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_SPRUCE_LEAVES = BLOCKS.register("snowy_spruce_leaves",
                () -> new SnowyLeavesBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isValidSpawn((state, reader, pos, entityType) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_BIRCH_LEAVES = BLOCKS.register("snowy_birch_leaves",
                () -> new SnowyLeavesBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isValidSpawn((state, reader, pos, entityType) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_JUNGLE_LEAVES = BLOCKS.register("snowy_jungle_leaves",
                () -> new SnowyLeavesBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isValidSpawn((state, reader, pos, entityType) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_ACACIA_LEAVES = BLOCKS.register("snowy_acacia_leaves",
                () -> new SnowyLeavesBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isValidSpawn((state, reader, pos, entityType) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_DARK_OAK_LEAVES = BLOCKS.register("snowy_dark_oak_leaves",
                () -> new SnowyLeavesBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isValidSpawn((state, reader, pos, entityType) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_MANGROVE_LEAVES = BLOCKS.register("snowy_mangrove_leaves",
                () -> new SnowyLeavesBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isValidSpawn((state, reader, pos, entityType) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_AZALEA_LEAVES = BLOCKS.register("snowy_azalea_leaves",
                () -> new SnowyLeavesBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES)
                                .strength(0.2f)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isValidSpawn((state, reader, pos, entityType) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_FLOWERING_AZALEA_LEAVES = BLOCKS
                .register("snowy_flowering_azalea_leaves", () -> new SnowyLeavesBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES)
                                .strength(0.2f)
                                .sound(com.kingodogo.buildscape.sound.ModSounds.AZALEA_SOUNDS())
                                .noOcclusion()
                                .isValidSpawn((state, reader, pos, entityType) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_SHORT_GRASS = BLOCKS.register(
                "snowy_short_grass",
                () -> new SnowyShortGrassBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> SNOWY_TALL_GRASS = BLOCKS.register(
                "snowy_tall_grass",
                () -> new SnowyTallGrassBlock(
                        BlockBehaviour.Properties.of(
                                        Material.REPLACEABLE_PLANT,
                                        MaterialColor.SNOW)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> SNOWY_FERN = BLOCKS.register(
                "snowy_fern",
                () -> new SnowyFernBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> SNOWY_LARGE_FERN = BLOCKS.register(
                "snowy_large_fern",
                () -> new SnowyLargeFernBlock(
                        BlockBehaviour.Properties.of(
                                        Material.REPLACEABLE_PLANT,
                                        MaterialColor.SNOW)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> SNOWY_BUSH = BLOCKS.register(
                "snowy_bush",
                () -> new SnowyBushBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> GLOW_LIGHTS = BLOCKS.register(
                "glow_lights",
                () -> new GlowLightsBlock(
                        BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.SLIME_BLOCK)
                                .lightLevel(state -> state.getValue(GlowLightsBlock.LIT) ? 15
                                        : 0)
                                .noOcclusion()
                                .noCollission()
                                .instabreak()));
        public static final RegistryObject<Block> MULTICOLOR_GLOW_LIGHTS = BLOCKS.register("multicolor_glow_lights",
                () -> new MulticolorGlowLightsBlock(
                        BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.SLIME_BLOCK)
                                .lightLevel(state -> state.getValue(
                                        MulticolorGlowLightsBlock.LIT) ? 15 : 0)
                                .noOcclusion()
                                .noCollission()
                                .instabreak()));
        public static final RegistryObject<Block> SNOW_BRICKS = BLOCKS.register(
                "snow_bricks",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.SNOW, MaterialColor.SNOW)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.SNOW)));
        public static final RegistryObject<Block> SNOW_BRICKS_STAIRS = BLOCKS.register("snow_bricks_stairs",
                () -> new ModStairBlock(
                        SNOW_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.SNOW, MaterialColor.SNOW)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.SNOW)));
        public static final RegistryObject<Block> SNOW_BRICKS_SLAB = BLOCKS.register(
                "snow_bricks_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.SNOW, MaterialColor.SNOW)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.SNOW)));
        public static final RegistryObject<Block> SNOW_BRICKS_WALL = BLOCKS.register(
                "snow_bricks_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.SNOW, MaterialColor.SNOW)
                                .strength(0.2f)
                                .sound(net.minecraft.world.level.block.SoundType.SNOW)));
        public static final RegistryObject<Block> SNOW_STAIRS = BLOCKS.register(
                "snow_stairs",
                () -> new ModStairBlock(
                        Blocks.SNOW_BLOCK.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.SNOW, MaterialColor.SNOW)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.SNOW)));
        public static final RegistryObject<Block> SNOW_SLAB = BLOCKS.register(
                "snow_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.SNOW, MaterialColor.SNOW)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.SNOW)));
        public static final RegistryObject<Block> SNOWY_GRASS_BLOCK = BLOCKS.register(
                "snowy_grass_block",
                () -> new SnowyGrassBlock(
                        BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.GRASS)
                                .strength(0.6f)
                                .randomTicks()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> SNOWY_GRASS_BLOCK_STAIRS = BLOCKS.register("snowy_grass_block_stairs",
                () -> new ModStairBlock(
                        SNOWY_GRASS_BLOCK.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.GRASS)
                                .strength(0.6f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> SNOWY_GRASS_BLOCK_SLAB = BLOCKS.register("snowy_grass_block_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.GRASS)
                                .strength(0.6f)
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> ICICLE = BLOCKS.register(
                "icicle",
                () -> new PointedIcicleBlock(
                        BlockBehaviour.Properties.of(Material.ICE, MaterialColor.ICE)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()
                                .randomTicks()
                                .dynamicShape()));
        public static final RegistryObject<Block> ICICLE_BLOCK = BLOCKS.register(
                "icicle_block",
                () -> new IcicleBlock(
                        BlockBehaviour.Properties.of(Material.ICE, MaterialColor.ICE)
                                .strength(0.5f)
                                .friction(0.989F)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> PACKED_ICICLE_BLOCK = BLOCKS.register("packed_icicle_block",
                () -> new PackedIcicleBlock(
                        BlockBehaviour.Properties.of(Material.ICE, MaterialColor.ICE)
                                .strength(0.5f)
                                .friction(0.989F)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> ICICLE_CAULDRON = BLOCKS.register(
                "icicle_cauldron",
                () -> new IcicleCauldronBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.METAL)
                                .noOcclusion()));
        public static final RegistryObject<Block> MANGROVE_LOG = BLOCKS.register(
                "mangrove_log",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_MANGROVE_LOG = BLOCKS.register("stripped_mangrove_log",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_WOOD = BLOCKS.register(
                "mangrove_wood",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_MANGROVE_WOOD = BLOCKS.register("stripped_mangrove_wood",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_LEAVES = BLOCKS.register(
                "mangrove_leaves",
                () -> new MangroveLeavesBlock(
                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.COLOR_GREEN)
                                .strength(0.2f)
                                .randomTicks()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noOcclusion()
                                .isValidSpawn((state, reader, pos, entityType) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> MANGROVE_ROOTS = BLOCKS.register(
                "mangrove_roots",
                () -> new MangroveRootsBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(0.7f)
                                .sound(
                                        com.kingodogo.buildscape.sound.ModSounds
                                                .MANGROVE_ROOTS_SOUNDS())
                                .noOcclusion()));
        public static final RegistryObject<Block> MUDDY_MANGROVE_ROOTS = BLOCKS.register("muddy_mangrove_roots",
                () -> new MangroveRootsBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                                .strength(0.7f)
                                .sound(
                                        com.kingodogo.buildscape.sound.ModSounds
                                                .MUDDY_MANGROVE_ROOTS_SOUNDS())
                                .noOcclusion()));
        public static final RegistryObject<Block> MANGROVE_PROPAGULE = BLOCKS.register("mangrove_propagule",
                () -> new MangrovePropaguleBlock(
                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_GREEN)
                                .strength(0.0f)
                                .randomTicks()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)
                                .noCollission()
                                .noOcclusion()));
        public static final RegistryObject<Block> MANGROVE_PLANKS = BLOCKS.register(
                "mangrove_planks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f, 3.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_STAIRS = BLOCKS.register(
                "mangrove_stairs",
                () -> new ModStairBlock(
                        MANGROVE_PLANKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f, 3.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_SLAB = BLOCKS.register(
                "mangrove_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f, 3.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_FENCE = BLOCKS.register(
                "mangrove_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f, 3.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_FENCE_GATE = BLOCKS.register("mangrove_fence_gate",
                () -> new FenceGateBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(2.0f, 3.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_DOOR = BLOCKS.register(
                "mangrove_door",
                () -> new ModDoorBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(3.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                .noOcclusion()));
        public static final RegistryObject<Block> MANGROVE_TRAPDOOR = BLOCKS.register(
                "mangrove_trapdoor",
                () -> new ModTrapdoorBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(3.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                .noOcclusion()
                                .isValidSpawn((state, reader, pos, entityType) -> false)));
        public static final RegistryObject<Block> MANGROVE_BUTTON = BLOCKS.register(
                "mangrove_button",
                () -> new WoodButtonBlock(
                        BlockBehaviour.Properties.of(
                                        Material.DECORATION,
                                        MaterialColor.COLOR_RED)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> MANGROVE_PRESSURE_PLATE = BLOCKS.register("mangrove_pressure_plate",
                () -> new PressurePlateBlock(
                        PressurePlateBlock.Sensitivity.EVERYTHING,
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(0.5f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                .noCollission()));
        public static final RegistryObject<Block> MANGROVE_SIGN = BLOCKS.register(
                "mangrove_sign",
                () -> new ModStandingSignBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(1.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                .noCollission(),
                        ModWoodTypes.MANGROVE));
        public static final RegistryObject<Block> MANGROVE_WALL_SIGN = BLOCKS.register("mangrove_wall_sign",
                () -> new ModWallSignBlock(
                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                .strength(1.0f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOD)
                                .noCollission()
                                .dropsLike(MANGROVE_SIGN.get()),
                        ModWoodTypes.MANGROVE));
        public static final RegistryObject<Block> FESTIVE_STOCKING = BLOCKS.register(
                "festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "festive"));
        public static final RegistryObject<Block> BLACK_FESTIVE_STOCKING = BLOCKS.register("black_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "black"));
        public static final RegistryObject<Block> BLUE_FESTIVE_STOCKING = BLOCKS.register("blue_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "blue"));
        public static final RegistryObject<Block> BROWN_FESTIVE_STOCKING = BLOCKS.register("brown_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "brown"));
        public static final RegistryObject<Block> CYAN_FESTIVE_STOCKING = BLOCKS.register("cyan_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "cyan"));
        public static final RegistryObject<Block> GRAY_FESTIVE_STOCKING = BLOCKS.register("gray_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "gray"));
        public static final RegistryObject<Block> GREEN_FESTIVE_STOCKING = BLOCKS.register("green_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "green"));
        public static final RegistryObject<Block> LIGHT_BLUE_FESTIVE_STOCKING = BLOCKS
                .register("light_blue_festive_stocking", () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "light_blue"));
        public static final RegistryObject<Block> LIGHT_GRAY_FESTIVE_STOCKING = BLOCKS
                .register("light_gray_festive_stocking", () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "light_gray"));
        public static final RegistryObject<Block> LIME_FESTIVE_STOCKING = BLOCKS.register("lime_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "lime"));
        public static final RegistryObject<Block> MAGENTA_FESTIVE_STOCKING = BLOCKS.register("magenta_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "magenta"));
        public static final RegistryObject<Block> ORANGE_FESTIVE_STOCKING = BLOCKS.register("orange_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "orange"));
        public static final RegistryObject<Block> PINK_FESTIVE_STOCKING = BLOCKS.register("pink_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "pink"));
        public static final RegistryObject<Block> PURPLE_FESTIVE_STOCKING = BLOCKS.register("purple_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "purple"));
        public static final RegistryObject<Block> RED_FESTIVE_STOCKING = BLOCKS.register("red_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "red"));
        public static final RegistryObject<Block> WHITE_FESTIVE_STOCKING = BLOCKS.register("white_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "white"));
        public static final RegistryObject<Block> YELLOW_FESTIVE_STOCKING = BLOCKS.register("yellow_festive_stocking",
                () -> new FestiveStockingBlock(
                        BlockBehaviour.Properties.of(Material.DECORATION)
                                .strength(0.1f)
                                .sound(net.minecraft.world.level.block.SoundType.WOOL)
                                .noCollission()
                                .noOcclusion(),
                        "yellow"));
        public static final RegistryObject<Block> FESTIVE_LAMP = BLOCKS.register(
                "festive_lamp",
                () -> new FestiveLampBlock(
                        BlockBehaviour.Properties.of(
                                        Material.BUILDABLE_GLASS,
                                        MaterialColor.COLOR_RED)
                                .strength(0.3f)
                                .sound(net.minecraft.world.level.block.SoundType.GLASS)
                                .lightLevel(state -> state.getValue(
                                        net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT)
                                        ? 15
                                        : 0)));
        public static final RegistryObject<Block> FROST_ROSE = BLOCKS.register(
                "frost_rose",
                () -> new FrostRoseBlock(
                        BlockBehaviour.Properties.of(
                                        Material.PLANT,
                                        MaterialColor.COLOR_LIGHT_BLUE)
                                .noCollission()
                                .instabreak()
                                .sound(net.minecraft.world.level.block.SoundType.GRASS)));
        public static final RegistryObject<Block> STEEL_BLOCK = BLOCKS.register(
                "steel_block",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        // Steel Block Variants
        public static final RegistryObject<Block> STEEL_BLOCK_STAIRS = BLOCKS.register(
                "steel_block_stairs",
                () -> new ModStairBlock(
                        STEEL_BLOCK.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> PRESSED_STEEL = BLOCKS.register(
                "pressed_steel",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        // Pressed Steel Variants
        public static final RegistryObject<Block> PRESSED_STEEL_STAIRS = BLOCKS.register(
                "pressed_steel_stairs",
                () -> new ModStairBlock(
                        PRESSED_STEEL.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CUT_STEEL = BLOCKS.register(
                "cut_steel",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        // Cut Steel Variants
        public static final RegistryObject<Block> CUT_STEEL_STAIRS = BLOCKS.register(
                "cut_steel_stairs",
                () -> new ModStairBlock(
                        CUT_STEEL.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> POLISHED_STEEL = BLOCKS.register(
                "polished_steel",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        // Polished Steel Variants
        public static final RegistryObject<Block> POLISHED_STEEL_STAIRS = BLOCKS.register(
                "polished_steel_stairs",
                () -> new ModStairBlock(
                        POLISHED_STEEL.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> FACTORY_STEEL_PANEL = BLOCKS.register(
                "factory_steel_panel",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> STEEL_CASING = BLOCKS.register(
                "steel_casing",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> STEEL_TRIM = BLOCKS.register(
                "steel_trim",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> STEEL_PILLAR = BLOCKS.register(
                "steel_pillar",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> BOLTED_STEEL_PILLAR = BLOCKS.register(
                "bolted_steel_pillar",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> STEEL_GRATE = BLOCKS.register(
                "steel_grate",
                () -> new WaterloggableGrateBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(ModSounds.COPPER_GRATE_SOUNDS())
                                .noOcclusion()
                                .isRedstoneConductor((state, reader, pos) -> false)
                                .isSuffocating((state, reader, pos) -> false)
                                .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> STEEL_FAN = BLOCKS.register(
                "steel_fan",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)
                                .lightLevel((state) -> 15)));
        public static final RegistryObject<Block> STEEL_BLOCK_SLAB = BLOCKS.register(
                "steel_block_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> STEEL_BLOCK_WALL = BLOCKS.register(
                "steel_block_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> POLISHED_STEEL_SLAB = BLOCKS.register(
                "polished_steel_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> POLISHED_STEEL_WALL = BLOCKS.register(
                "polished_steel_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> PRESSED_STEEL_SLAB = BLOCKS.register(
                "pressed_steel_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> PRESSED_STEEL_WALL = BLOCKS.register(
                "pressed_steel_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CUT_STEEL_SLAB = BLOCKS.register(
                "cut_steel_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CUT_STEEL_WALL = BLOCKS.register(
                "cut_steel_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        // Caution Blocks
        public static final RegistryObject<Block> CAUTION_BLACK = BLOCKS.register(
                "caution_black",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        // Caution Stairs
        public static final RegistryObject<Block> CAUTION_BLACK_STAIRS = BLOCKS.register(
                "caution_black_stairs",
                () -> new StairBlock(
                        () -> CAUTION_BLACK.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_BLUE = BLOCKS.register(
                "caution_blue",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLUE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_BLUE_STAIRS = BLOCKS.register(
                "caution_blue_stairs",
                () -> new StairBlock(
                        () -> CAUTION_BLUE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLUE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_FACTORY = BLOCKS.register(
                "caution_factory",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_FACTORY_STAIRS = BLOCKS.register(
                "caution_factory_stairs",
                () -> new StairBlock(
                        () -> CAUTION_FACTORY.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_RED = BLOCKS.register(
                "caution_red",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_RED)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_RED_STAIRS = BLOCKS.register(
                "caution_red_stairs",
                () -> new StairBlock(
                        () -> CAUTION_RED.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_RED)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> FRAMED_CAUTION = BLOCKS.register(
                "framed_caution",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> FRAMED_CAUTION_STAIRS = BLOCKS.register(
                "framed_caution_stairs",
                () -> new StairBlock(
                        () -> FRAMED_CAUTION.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_WHITE = BLOCKS.register(
                "caution_white",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.SNOW)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_WHITE_STAIRS = BLOCKS.register(
                "caution_white_stairs",
                () -> new StairBlock(
                        () -> CAUTION_WHITE.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.SNOW)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_LIME = BLOCKS.register(
                "caution_lime",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_LIME_STAIRS = BLOCKS.register(
                "caution_lime_stairs",
                () -> new StairBlock(
                        () -> CAUTION_LIME.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_PINK = BLOCKS.register(
                "caution_pink",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_PINK)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_PINK_STAIRS = BLOCKS.register(
                "caution_pink_stairs",
                () -> new StairBlock(
                        () -> CAUTION_PINK.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_PINK)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_YELLOW = BLOCKS.register(
                "caution_yellow",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_YELLOW_STAIRS = BLOCKS.register(
                "caution_yellow_stairs",
                () -> new StairBlock(
                        () -> CAUTION_YELLOW.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_CANDY = BLOCKS.register(
                "caution_candy",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_RED)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_CANDY_STAIRS = BLOCKS.register(
                "caution_candy_stairs",
                () -> new StairBlock(
                        () -> CAUTION_CANDY.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_RED)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_COTTONCANDY = BLOCKS.register(
                "caution_cottoncandy",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_PINK)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_COTTONCANDY_STAIRS = BLOCKS.register(
                "caution_cottoncandy_stairs",
                () -> new StairBlock(
                        () -> CAUTION_COTTONCANDY.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_PINK)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_MINTCANDY = BLOCKS.register(
                "caution_mintcandy",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_MINTCANDY_STAIRS = BLOCKS.register(
                "caution_mintcandy_stairs",
                () -> new StairBlock(
                        () -> CAUTION_MINTCANDY.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_CITRUSCANDY = BLOCKS.register(
                "caution_citruscandy",
                () -> new RotatedPillarBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_CITRUSCANDY_STAIRS = BLOCKS.register(
                "caution_citruscandy_stairs",
                () -> new StairBlock(
                        () -> CAUTION_CITRUSCANDY.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        // Caution Slabs
        public static final RegistryObject<Block> CAUTION_BLACK_SLAB = BLOCKS.register(
                "caution_black_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_BLUE_SLAB = BLOCKS.register(
                "caution_blue_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLUE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_FACTORY_SLAB = BLOCKS.register(
                "caution_factory_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_RED_SLAB = BLOCKS.register(
                "caution_red_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_RED)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> FRAMED_CAUTION_SLAB = BLOCKS.register(
                "framed_caution_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_WHITE_SLAB = BLOCKS.register(
                "caution_white_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.SNOW)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_LIME_SLAB = BLOCKS.register(
                "caution_lime_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_PINK_SLAB = BLOCKS.register(
                "caution_pink_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_PINK)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_YELLOW_SLAB = BLOCKS.register(
                "caution_yellow_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_CANDY_SLAB = BLOCKS.register(
                "caution_candy_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_RED)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_COTTONCANDY_SLAB = BLOCKS.register(
                "caution_cottoncandy_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_PINK)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_MINTCANDY_SLAB = BLOCKS.register(
                "caution_mintcandy_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> CAUTION_CITRUSCANDY_SLAB = BLOCKS.register(
                "caution_citruscandy_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                .strength(5.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.COPPER)));
        // Factory Glass Blocks
        public static final RegistryObject<Block> FACTORY_WHITE_GLASS = BLOCKS.register(
                "factory_white_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_LIGHT_GRAY_GLASS = BLOCKS.register(
                "factory_light_gray_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_GRAY_GLASS = BLOCKS.register(
                "factory_gray_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_BLACK_GLASS = BLOCKS.register(
                "factory_black_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_BROWN_GLASS = BLOCKS.register(
                "factory_brown_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_RED_GLASS = BLOCKS.register(
                "factory_red_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_ORANGE_GLASS = BLOCKS.register(
                "factory_orange_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_YELLOW_GLASS = BLOCKS.register(
                "factory_yellow_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_LIME_GLASS = BLOCKS.register(
                "factory_lime_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_GREEN_GLASS = BLOCKS.register(
                "factory_green_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_CYAN_GLASS = BLOCKS.register(
                "factory_cyan_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_LIGHT_BLUE_GLASS = BLOCKS.register(
                "factory_light_blue_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_BLUE_GLASS = BLOCKS.register(
                "factory_blue_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_PURPLE_GLASS = BLOCKS.register(
                "factory_purple_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_MAGENTA_GLASS = BLOCKS.register(
                "factory_magenta_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_MAGENTA)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_PINK_GLASS = BLOCKS.register(
                "factory_pink_glass",
                () -> new SilkTouchOnlyGlassBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        // Factory Glass Panes
        public static final RegistryObject<Block> FACTORY_WHITE_GLASS_PANE = BLOCKS.register(
                "factory_white_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_LIGHT_GRAY_GLASS_PANE = BLOCKS.register(
                "factory_light_gray_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_GRAY_GLASS_PANE = BLOCKS.register(
                "factory_gray_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_BLACK_GLASS_PANE = BLOCKS.register(
                "factory_black_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_BROWN_GLASS_PANE = BLOCKS.register(
                "factory_brown_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_RED_GLASS_PANE = BLOCKS.register(
                "factory_red_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_ORANGE_GLASS_PANE = BLOCKS.register(
                "factory_orange_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_YELLOW_GLASS_PANE = BLOCKS.register(
                "factory_yellow_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_LIME_GLASS_PANE = BLOCKS.register(
                "factory_lime_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_GREEN_GLASS_PANE = BLOCKS.register(
                "factory_green_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_CYAN_GLASS_PANE = BLOCKS.register(
                "factory_cyan_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_LIGHT_BLUE_GLASS_PANE = BLOCKS.register(
                "factory_light_blue_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_BLUE_GLASS_PANE = BLOCKS.register(
                "factory_blue_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_PURPLE_GLASS_PANE = BLOCKS.register(
                "factory_purple_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_MAGENTA_GLASS_PANE = BLOCKS.register(
                "factory_magenta_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_MAGENTA)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        public static final RegistryObject<Block> FACTORY_PINK_GLASS_PANE = BLOCKS.register(
                "factory_pink_glass_pane",
                () -> new SilkTouchOnlyPaneBlock(
                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                                .strength(0.3f)
                                .sound(SoundType.GLASS)
                                .noOcclusion()));
        // Stained Bricks
        public static final RegistryObject<Block> WHITE_STAINED_BRICKS = BLOCKS.register(
                "white_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        // Stained Bricks Stairs
        public static final RegistryObject<Block> WHITE_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "white_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> WHITE_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_BRICKS = BLOCKS.register(
                "light_gray_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "light_gray_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> LIGHT_GRAY_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GRAY_STAINED_BRICKS = BLOCKS.register(
                "gray_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GRAY_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "gray_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> GRAY_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_STAINED_BRICKS = BLOCKS.register(
                "black_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "black_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> BLACK_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BROWN_STAINED_BRICKS = BLOCKS.register(
                "brown_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BROWN_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "brown_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> BROWN_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> RED_STAINED_BRICKS = BLOCKS.register(
                "red_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> RED_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "red_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> RED_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_STAINED_BRICKS = BLOCKS.register(
                "orange_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "orange_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> ORANGE_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_STAINED_BRICKS = BLOCKS.register(
                "yellow_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "yellow_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> YELLOW_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIME_STAINED_BRICKS = BLOCKS.register(
                "lime_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIME_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "lime_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> LIME_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_STAINED_BRICKS = BLOCKS.register(
                "green_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "green_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> GREEN_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> CYAN_STAINED_BRICKS = BLOCKS.register(
                "cyan_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> CYAN_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "cyan_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> CYAN_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_BRICKS = BLOCKS.register(
                "light_blue_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "light_blue_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> LIGHT_BLUE_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_STAINED_BRICKS = BLOCKS.register(
                "blue_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "blue_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> BLUE_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PURPLE_STAINED_BRICKS = BLOCKS.register(
                "purple_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PURPLE_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "purple_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> PURPLE_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> MAGENTA_STAINED_BRICKS = BLOCKS.register(
                "magenta_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_MAGENTA)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> MAGENTA_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "magenta_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> MAGENTA_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_MAGENTA)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PINK_STAINED_BRICKS = BLOCKS.register(
                "pink_stained_bricks",
                () -> new Block(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PINK_STAINED_BRICKS_STAIRS = BLOCKS.register(
                "pink_stained_bricks_stairs",
                () -> new StairBlock(
                        () -> PINK_STAINED_BRICKS.get().defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        // Stained Bricks Slabs
        public static final RegistryObject<Block> WHITE_STAINED_BRICKS_SLAB = BLOCKS.register(
                "white_stained_bricks_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_BRICKS_SLAB = BLOCKS.register(
                "light_gray_stained_bricks_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GRAY_STAINED_BRICKS_SLAB = BLOCKS.register(
                "gray_stained_bricks_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_STAINED_BRICKS_SLAB = BLOCKS.register(
                "black_stained_bricks_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BROWN_STAINED_BRICKS_SLAB = BLOCKS.register(
                "brown_stained_bricks_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
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
        // Stained Bricks Walls
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
                        net.minecraft.world.level.block.Blocks.SMOOTH_STONE.defaultBlockState(),
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE)
                                .strength(2.0f, 6.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        public static final RegistryObject<Block> POLISHED_WHITE_CONCRETE = BLOCKS.register(
                "polished_white_concrete",
                () -> new ModBlock(
                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                .strength(1.8f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)));
        // Polished White Concrete Variants
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
        // Polished Orange Concrete Variants
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
        // Polished Magenta Concrete Variants
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
        // Polished Light Blue Concrete Variants
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
        // Polished Yellow Concrete Variants
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
        // Polished Lime Concrete Variants
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
        // Polished Pink Concrete Variants
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
        // Polished Gray Concrete Variants
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
        // Polished Light Gray Concrete Variants
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
        // Polished Cyan Concrete Variants
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
        // Polished Purple Concrete Variants
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
        // Polished Blue Concrete Variants
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
        // Polished Brown Concrete Variants
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
        // Polished Green Concrete Variants
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
        // Polished Red Concrete Variants
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
        // Polished Black Concrete Variants
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

    // Colored Redstone Lamps
    public static final RegistryObject<Block> WHITE_REDSTONE_LAMP =
            BLOCKS.register("white_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.SNOW)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> ORANGE_REDSTONE_LAMP =
            BLOCKS.register("orange_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_ORANGE)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> MAGENTA_REDSTONE_LAMP =
            BLOCKS.register("magenta_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_MAGENTA)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> LIGHT_BLUE_REDSTONE_LAMP =
            BLOCKS.register("light_blue_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_LIGHT_BLUE)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> YELLOW_REDSTONE_LAMP =
            BLOCKS.register("yellow_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_YELLOW)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> LIME_REDSTONE_LAMP =
            BLOCKS.register("lime_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_LIGHT_GREEN)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> PINK_REDSTONE_LAMP =
            BLOCKS.register("pink_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_PINK)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> GRAY_REDSTONE_LAMP =
            BLOCKS.register("gray_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_GRAY)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> LIGHT_GRAY_REDSTONE_LAMP =
            BLOCKS.register("light_gray_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_LIGHT_GRAY)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> CYAN_REDSTONE_LAMP =
            BLOCKS.register("cyan_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_CYAN)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> PURPLE_REDSTONE_LAMP =
            BLOCKS.register("purple_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_PURPLE)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> BLUE_REDSTONE_LAMP =
            BLOCKS.register("blue_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_BLUE)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> BROWN_REDSTONE_LAMP =
            BLOCKS.register("brown_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_BROWN)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> GREEN_REDSTONE_LAMP =
            BLOCKS.register("green_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_GREEN)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> RED_REDSTONE_LAMP =
            BLOCKS.register("red_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_RED)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> BLACK_REDSTONE_LAMP =
            BLOCKS.register("black_redstone_lamp", () ->
                    new RedstoneLampBlock(
                            BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS, MaterialColor.COLOR_BLACK)
                                    .strength(0.3f)
                                    .sound(SoundType.GLASS)
                                    .lightLevel(state -> state.getValue(
                                            net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT
                                    ) ? 15 : 0)
                    )
            );

    public static final RegistryObject<Block> CASCADE_BLOCK =
            BLOCKS.register("cascade_block", () ->
                    new CascadeBlock(
                            BlockBehaviour.Properties.of(Material.ICE_SOLID)
                                    .strength(0.5f)
                                    .sound(SoundType.CORAL_BLOCK)
                                    .noOcclusion()
                                    .isViewBlocking((state, getter, pos) -> false)
                    )
            );

    public static final RegistryObject<Block> CASCADE_BLOCK_NO_MIST =
            BLOCKS.register("cascade_block_no_mist", () ->
                    new CascadeBlockNoMist(
                            BlockBehaviour.Properties.of(Material.ICE_SOLID)
                                    .strength(0.5f)
                                    .sound(SoundType.CORAL_BLOCK)
                                    .noOcclusion()
                                    .isViewBlocking((state, getter, pos) -> false)
                    )
            );
    public static final RegistryObject<Block> BLACK_FACTORY_MESH = BLOCKS.register("black_factory_mesh",
            () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> BLACK_FACTORY_MESH_STAIRS = BLOCKS.register("black_factory_mesh_stairs",
            () -> new ModStairBlock(BLACK_FACTORY_MESH.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> BLACK_FACTORY_MESH_SLAB = BLOCKS.register("black_factory_mesh_slab",
            () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> BLACK_FACTORY_MESH_WALL = BLOCKS.register("black_factory_mesh_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> BLUE_FACTORY_MESH = BLOCKS.register("blue_factory_mesh",
            () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> BLUE_FACTORY_MESH_STAIRS = BLOCKS.register("blue_factory_mesh_stairs",
            () -> new ModStairBlock(BLUE_FACTORY_MESH.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> BLUE_FACTORY_MESH_SLAB = BLOCKS.register("blue_factory_mesh_slab",
            () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> BLUE_FACTORY_MESH_WALL = BLOCKS.register("blue_factory_mesh_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> BROWN_FACTORY_MESH = BLOCKS.register("brown_factory_mesh",
            () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> BROWN_FACTORY_MESH_STAIRS = BLOCKS.register("brown_factory_mesh_stairs",
            () -> new ModStairBlock(BROWN_FACTORY_MESH.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> BROWN_FACTORY_MESH_SLAB = BLOCKS.register("brown_factory_mesh_slab",
            () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> BROWN_FACTORY_MESH_WALL = BLOCKS.register("brown_factory_mesh_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> GRAY_FACTORY_MESH = BLOCKS.register("gray_factory_mesh",
            () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> GRAY_FACTORY_MESH_STAIRS = BLOCKS.register("gray_factory_mesh_stairs",
            () -> new ModStairBlock(GRAY_FACTORY_MESH.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> GRAY_FACTORY_MESH_SLAB = BLOCKS.register("gray_factory_mesh_slab",
            () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> GRAY_FACTORY_MESH_WALL = BLOCKS.register("gray_factory_mesh_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> GREEN_FACTORY_MESH = BLOCKS.register("green_factory_mesh",
            () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> GREEN_FACTORY_MESH_STAIRS = BLOCKS.register("green_factory_mesh_stairs",
            () -> new ModStairBlock(GREEN_FACTORY_MESH.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> GREEN_FACTORY_MESH_SLAB = BLOCKS.register("green_factory_mesh_slab",
            () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> GREEN_FACTORY_MESH_WALL = BLOCKS.register("green_factory_mesh_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> LIGHT_GRAY_FACTORY_MESH = BLOCKS.register("light_gray_factory_mesh",
            () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> LIGHT_GRAY_FACTORY_MESH_STAIRS = BLOCKS.register("light_gray_factory_mesh_stairs",
            () -> new ModStairBlock(LIGHT_GRAY_FACTORY_MESH.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> LIGHT_GRAY_FACTORY_MESH_SLAB = BLOCKS.register("light_gray_factory_mesh_slab",
            () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> LIGHT_GRAY_FACTORY_MESH_WALL = BLOCKS.register("light_gray_factory_mesh_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> LIME_FACTORY_MESH = BLOCKS.register("lime_factory_mesh",
            () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> LIME_FACTORY_MESH_STAIRS = BLOCKS.register("lime_factory_mesh_stairs",
            () -> new ModStairBlock(LIME_FACTORY_MESH.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> LIME_FACTORY_MESH_SLAB = BLOCKS.register("lime_factory_mesh_slab",
            () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> LIME_FACTORY_MESH_WALL = BLOCKS.register("lime_factory_mesh_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> ORANGE_FACTORY_MESH = BLOCKS.register("orange_factory_mesh",
            () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> ORANGE_FACTORY_MESH_STAIRS = BLOCKS.register("orange_factory_mesh_stairs",
            () -> new ModStairBlock(ORANGE_FACTORY_MESH.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> ORANGE_FACTORY_MESH_SLAB = BLOCKS.register("orange_factory_mesh_slab",
            () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> ORANGE_FACTORY_MESH_WALL = BLOCKS.register("orange_factory_mesh_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> RED_FACTORY_MESH = BLOCKS.register("red_factory_mesh",
            () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> RED_FACTORY_MESH_STAIRS = BLOCKS.register("red_factory_mesh_stairs",
            () -> new ModStairBlock(RED_FACTORY_MESH.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> RED_FACTORY_MESH_SLAB = BLOCKS.register("red_factory_mesh_slab",
            () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> RED_FACTORY_MESH_WALL = BLOCKS.register("red_factory_mesh_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> YELLOW_FACTORY_MESH = BLOCKS.register("yellow_factory_mesh",
            () -> new ModBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> YELLOW_FACTORY_MESH_STAIRS = BLOCKS.register("yellow_factory_mesh_stairs",
            () -> new ModStairBlock(YELLOW_FACTORY_MESH.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> YELLOW_FACTORY_MESH_SLAB = BLOCKS.register("yellow_factory_mesh_slab",
            () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> YELLOW_FACTORY_MESH_WALL = BLOCKS.register("yellow_factory_mesh_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion().sound(net.minecraft.world.level.block.SoundType.COPPER)));
    public static final RegistryObject<Block> PACKED_MUD = BLOCKS.register("packed_mud",
            () -> new ModBlock(BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(1.0f).sound(ModSounds.PACKED_MUD_SOUNDS())));
    public static final RegistryObject<Block> PACKED_MUD_STAIRS = BLOCKS.register("packed_mud_stairs",
            () -> new ModStairBlock(PACKED_MUD.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(1.0f).sound(ModSounds.PACKED_MUD_SOUNDS())));
    public static final RegistryObject<Block> PACKED_MUD_SLAB = BLOCKS.register("packed_mud_slab",
            () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(1.0f).sound(ModSounds.PACKED_MUD_SOUNDS())));
    public static final RegistryObject<Block> PACKED_MUD_WALL = BLOCKS.register("packed_mud_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(1.0f).sound(ModSounds.PACKED_MUD_SOUNDS())));

    public static final RegistryObject<Block> MUD_BRICKS = BLOCKS.register("mud_bricks",
            () -> new ModBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIRT).strength(1.5f, 3.0f).requiresCorrectToolForDrops().sound(ModSounds.MUD_BRICKS_SOUNDS())));
    public static final RegistryObject<Block> MUD_BRICK_STAIRS = BLOCKS.register("mud_brick_stairs",
            () -> new ModStairBlock(MUD_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIRT).strength(1.5f, 3.0f).requiresCorrectToolForDrops().sound(ModSounds.MUD_BRICKS_SOUNDS())));
    public static final RegistryObject<Block> MUD_BRICK_SLAB = BLOCKS.register("mud_brick_slab",
            () -> new ModSlabBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIRT).strength(1.5f, 3.0f).requiresCorrectToolForDrops().sound(ModSounds.MUD_BRICKS_SOUNDS())));
    public static final RegistryObject<Block> MUD_BRICK_WALL = BLOCKS.register("mud_brick_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIRT).strength(1.5f, 3.0f).requiresCorrectToolForDrops().sound(ModSounds.MUD_BRICKS_SOUNDS())));



        public static final RegistryObject<Block> GLASS_SLAB = BLOCKS.register(
                "glass_slab",
                () -> new ModSlabBlock(Blocks.GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.GLASS)
                ));
        public static final RegistryObject<Block> WHITE_STAINED_GLASS_SLAB = BLOCKS.register(
                "white_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.WHITE_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.WHITE_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_GLASS_SLAB = BLOCKS.register(
                "light_gray_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.LIGHT_GRAY_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> GRAY_STAINED_GLASS_SLAB = BLOCKS.register(
                "gray_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.GRAY_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.GRAY_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> BLACK_STAINED_GLASS_SLAB = BLOCKS.register(
                "black_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.BLACK_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> BROWN_STAINED_GLASS_SLAB = BLOCKS.register(
                "brown_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.BROWN_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.BROWN_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> RED_STAINED_GLASS_SLAB = BLOCKS.register(
                "red_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.RED_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.RED_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> ORANGE_STAINED_GLASS_SLAB = BLOCKS.register(
                "orange_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.ORANGE_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.ORANGE_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> YELLOW_STAINED_GLASS_SLAB = BLOCKS.register(
                "yellow_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.YELLOW_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.YELLOW_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> LIME_STAINED_GLASS_SLAB = BLOCKS.register(
                "lime_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.LIME_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.LIME_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> GREEN_STAINED_GLASS_SLAB = BLOCKS.register(
                "green_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.GREEN_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.GREEN_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> CYAN_STAINED_GLASS_SLAB = BLOCKS.register(
                "cyan_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.CYAN_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.CYAN_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_GLASS_SLAB = BLOCKS.register(
                "light_blue_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.LIGHT_BLUE_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> BLUE_STAINED_GLASS_SLAB = BLOCKS.register(
                "blue_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.BLUE_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.BLUE_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> PURPLE_STAINED_GLASS_SLAB = BLOCKS.register(
                "purple_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.PURPLE_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.PURPLE_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> MAGENTA_STAINED_GLASS_SLAB = BLOCKS.register(
                "magenta_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.MAGENTA_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.MAGENTA_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> PINK_STAINED_GLASS_SLAB = BLOCKS.register(
                "pink_stained_glass_slab",
                () -> new ModSlabBlock(Blocks.PINK_STAINED_GLASS, 
                        BlockBehaviour.Properties.copy(Blocks.PINK_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> GLASS_STAIRS = BLOCKS.register(
                "glass_stairs",
                () -> new ModStairBlock(
                        Blocks.GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.GLASS)
                ));
        public static final RegistryObject<Block> WHITE_STAINED_GLASS_STAIRS = BLOCKS.register(
                "white_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.WHITE_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.WHITE_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_GLASS_STAIRS = BLOCKS.register(
                "light_gray_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> GRAY_STAINED_GLASS_STAIRS = BLOCKS.register(
                "gray_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.GRAY_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.GRAY_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> BLACK_STAINED_GLASS_STAIRS = BLOCKS.register(
                "black_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.BLACK_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.BLACK_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> BROWN_STAINED_GLASS_STAIRS = BLOCKS.register(
                "brown_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.BROWN_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.BROWN_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> RED_STAINED_GLASS_STAIRS = BLOCKS.register(
                "red_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.RED_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.RED_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> ORANGE_STAINED_GLASS_STAIRS = BLOCKS.register(
                "orange_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.ORANGE_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.ORANGE_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> YELLOW_STAINED_GLASS_STAIRS = BLOCKS.register(
                "yellow_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.YELLOW_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.YELLOW_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> LIME_STAINED_GLASS_STAIRS = BLOCKS.register(
                "lime_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.LIME_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.LIME_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> GREEN_STAINED_GLASS_STAIRS = BLOCKS.register(
                "green_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.GREEN_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.GREEN_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> CYAN_STAINED_GLASS_STAIRS = BLOCKS.register(
                "cyan_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.CYAN_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.CYAN_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_GLASS_STAIRS = BLOCKS.register(
                "light_blue_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> BLUE_STAINED_GLASS_STAIRS = BLOCKS.register(
                "blue_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.BLUE_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.BLUE_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> PURPLE_STAINED_GLASS_STAIRS = BLOCKS.register(
                "purple_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.PURPLE_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.PURPLE_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> MAGENTA_STAINED_GLASS_STAIRS = BLOCKS.register(
                "magenta_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.MAGENTA_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.MAGENTA_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> PINK_STAINED_GLASS_STAIRS = BLOCKS.register(
                "pink_stained_glass_stairs",
                () -> new ModStairBlock(
                        Blocks.PINK_STAINED_GLASS.defaultBlockState(),
                        BlockBehaviour.Properties.copy(Blocks.PINK_STAINED_GLASS)
                ));
        public static final RegistryObject<Block> WHITE_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "white_mosaic_glass_slab",
                () -> new ModSlabBlock(WHITE_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(WHITE_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> WHITE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "white_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        WHITE_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(WHITE_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> LIGHT_GRAY_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "light_gray_mosaic_glass_slab",
                () -> new ModSlabBlock(LIGHT_GRAY_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(LIGHT_GRAY_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> LIGHT_GRAY_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "light_gray_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        LIGHT_GRAY_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(LIGHT_GRAY_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> GRAY_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "gray_mosaic_glass_slab",
                () -> new ModSlabBlock(GRAY_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(GRAY_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> GRAY_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "gray_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        GRAY_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(GRAY_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> BLACK_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "black_mosaic_glass_slab",
                () -> new ModSlabBlock(BLACK_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(BLACK_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> BLACK_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "black_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        BLACK_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(BLACK_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> BROWN_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "brown_mosaic_glass_slab",
                () -> new ModSlabBlock(BROWN_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(BROWN_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> BROWN_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "brown_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        BROWN_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(BROWN_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> RED_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "red_mosaic_glass_slab",
                () -> new ModSlabBlock(RED_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(RED_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> RED_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "red_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        RED_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(RED_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> ORANGE_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "orange_mosaic_glass_slab",
                () -> new ModSlabBlock(ORANGE_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(ORANGE_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> ORANGE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "orange_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        ORANGE_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(ORANGE_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> YELLOW_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "yellow_mosaic_glass_slab",
                () -> new ModSlabBlock(YELLOW_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(YELLOW_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> YELLOW_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "yellow_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        YELLOW_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(YELLOW_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> LIME_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "lime_mosaic_glass_slab",
                () -> new ModSlabBlock(LIME_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(LIME_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> LIME_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "lime_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        LIME_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(LIME_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> GREEN_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "green_mosaic_glass_slab",
                () -> new ModSlabBlock(GREEN_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(GREEN_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> GREEN_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "green_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        GREEN_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(GREEN_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> CYAN_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "cyan_mosaic_glass_slab",
                () -> new ModSlabBlock(CYAN_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(CYAN_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> CYAN_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "cyan_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        CYAN_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(CYAN_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> LIGHT_BLUE_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "light_blue_mosaic_glass_slab",
                () -> new ModSlabBlock(LIGHT_BLUE_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(LIGHT_BLUE_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> LIGHT_BLUE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "light_blue_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        LIGHT_BLUE_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(LIGHT_BLUE_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> BLUE_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "blue_mosaic_glass_slab",
                () -> new ModSlabBlock(BLUE_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(BLUE_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> BLUE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "blue_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        BLUE_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(BLUE_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> PURPLE_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "purple_mosaic_glass_slab",
                () -> new ModSlabBlock(PURPLE_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(PURPLE_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> PURPLE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "purple_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        PURPLE_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(PURPLE_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> MAGENTA_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "magenta_mosaic_glass_slab",
                () -> new ModSlabBlock(MAGENTA_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(MAGENTA_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> MAGENTA_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "magenta_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        MAGENTA_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(MAGENTA_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> PINK_MOSAIC_GLASS_SLAB = BLOCKS.register(
                "pink_mosaic_glass_slab",
                () -> new ModSlabBlock(PINK_MOSAIC_GLASS.get(), 
                        BlockBehaviour.Properties.copy(PINK_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> PINK_MOSAIC_GLASS_STAIRS = BLOCKS.register(
                "pink_mosaic_glass_stairs",
                () -> new ModStairBlock(
                        PINK_MOSAIC_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(PINK_MOSAIC_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_WHITE_GLASS_SLAB = BLOCKS.register(
                "factory_white_glass_slab",
                () -> new ModSlabBlock(FACTORY_WHITE_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_WHITE_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_WHITE_GLASS_STAIRS = BLOCKS.register(
                "factory_white_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_WHITE_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_WHITE_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_LIGHT_GRAY_GLASS_SLAB = BLOCKS.register(
                "factory_light_gray_glass_slab",
                () -> new ModSlabBlock(FACTORY_LIGHT_GRAY_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_LIGHT_GRAY_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_LIGHT_GRAY_GLASS_STAIRS = BLOCKS.register(
                "factory_light_gray_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_LIGHT_GRAY_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_LIGHT_GRAY_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_GRAY_GLASS_SLAB = BLOCKS.register(
                "factory_gray_glass_slab",
                () -> new ModSlabBlock(FACTORY_GRAY_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_GRAY_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_GRAY_GLASS_STAIRS = BLOCKS.register(
                "factory_gray_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_GRAY_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_GRAY_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_BLACK_GLASS_SLAB = BLOCKS.register(
                "factory_black_glass_slab",
                () -> new ModSlabBlock(FACTORY_BLACK_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_BLACK_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_BLACK_GLASS_STAIRS = BLOCKS.register(
                "factory_black_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_BLACK_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_BLACK_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_BROWN_GLASS_SLAB = BLOCKS.register(
                "factory_brown_glass_slab",
                () -> new ModSlabBlock(FACTORY_BROWN_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_BROWN_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_BROWN_GLASS_STAIRS = BLOCKS.register(
                "factory_brown_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_BROWN_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_BROWN_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_RED_GLASS_SLAB = BLOCKS.register(
                "factory_red_glass_slab",
                () -> new ModSlabBlock(FACTORY_RED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_RED_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_RED_GLASS_STAIRS = BLOCKS.register(
                "factory_red_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_RED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_RED_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_ORANGE_GLASS_SLAB = BLOCKS.register(
                "factory_orange_glass_slab",
                () -> new ModSlabBlock(FACTORY_ORANGE_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_ORANGE_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_ORANGE_GLASS_STAIRS = BLOCKS.register(
                "factory_orange_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_ORANGE_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_ORANGE_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_YELLOW_GLASS_SLAB = BLOCKS.register(
                "factory_yellow_glass_slab",
                () -> new ModSlabBlock(FACTORY_YELLOW_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_YELLOW_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_YELLOW_GLASS_STAIRS = BLOCKS.register(
                "factory_yellow_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_YELLOW_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_YELLOW_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_LIME_GLASS_SLAB = BLOCKS.register(
                "factory_lime_glass_slab",
                () -> new ModSlabBlock(FACTORY_LIME_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_LIME_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_LIME_GLASS_STAIRS = BLOCKS.register(
                "factory_lime_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_LIME_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_LIME_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_GREEN_GLASS_SLAB = BLOCKS.register(
                "factory_green_glass_slab",
                () -> new ModSlabBlock(FACTORY_GREEN_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_GREEN_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_GREEN_GLASS_STAIRS = BLOCKS.register(
                "factory_green_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_GREEN_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_GREEN_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_CYAN_GLASS_SLAB = BLOCKS.register(
                "factory_cyan_glass_slab",
                () -> new ModSlabBlock(FACTORY_CYAN_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_CYAN_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_CYAN_GLASS_STAIRS = BLOCKS.register(
                "factory_cyan_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_CYAN_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_CYAN_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_LIGHT_BLUE_GLASS_SLAB = BLOCKS.register(
                "factory_light_blue_glass_slab",
                () -> new ModSlabBlock(FACTORY_LIGHT_BLUE_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_LIGHT_BLUE_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_LIGHT_BLUE_GLASS_STAIRS = BLOCKS.register(
                "factory_light_blue_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_LIGHT_BLUE_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_LIGHT_BLUE_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_BLUE_GLASS_SLAB = BLOCKS.register(
                "factory_blue_glass_slab",
                () -> new ModSlabBlock(FACTORY_BLUE_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_BLUE_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_BLUE_GLASS_STAIRS = BLOCKS.register(
                "factory_blue_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_BLUE_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_BLUE_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_PURPLE_GLASS_SLAB = BLOCKS.register(
                "factory_purple_glass_slab",
                () -> new ModSlabBlock(FACTORY_PURPLE_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_PURPLE_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_PURPLE_GLASS_STAIRS = BLOCKS.register(
                "factory_purple_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_PURPLE_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_PURPLE_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_MAGENTA_GLASS_SLAB = BLOCKS.register(
                "factory_magenta_glass_slab",
                () -> new ModSlabBlock(FACTORY_MAGENTA_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_MAGENTA_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_MAGENTA_GLASS_STAIRS = BLOCKS.register(
                "factory_magenta_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_MAGENTA_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_MAGENTA_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_PINK_GLASS_SLAB = BLOCKS.register(
                "factory_pink_glass_slab",
                () -> new ModSlabBlock(FACTORY_PINK_GLASS.get(), 
                        BlockBehaviour.Properties.copy(FACTORY_PINK_GLASS.get())
                ));
        public static final RegistryObject<Block> FACTORY_PINK_GLASS_STAIRS = BLOCKS.register(
                "factory_pink_glass_stairs",
                () -> new ModStairBlock(
                        FACTORY_PINK_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(FACTORY_PINK_GLASS.get())
                ));
        public static final RegistryObject<Block> WHITE_GLAZED_GLASS_SLAB = BLOCKS.register(
                "white_glazed_glass_slab",
                () -> new ModSlabBlock(WHITE_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(WHITE_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> WHITE_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "white_glazed_glass_stairs",
                () -> new ModStairBlock(
                        WHITE_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(WHITE_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> LIGHT_GRAY_GLAZED_GLASS_SLAB = BLOCKS.register(
                "light_gray_glazed_glass_slab",
                () -> new ModSlabBlock(LIGHT_GRAY_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(LIGHT_GRAY_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> LIGHT_GRAY_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "light_gray_glazed_glass_stairs",
                () -> new ModStairBlock(
                        LIGHT_GRAY_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(LIGHT_GRAY_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> GRAY_GLAZED_GLASS_SLAB = BLOCKS.register(
                "gray_glazed_glass_slab",
                () -> new ModSlabBlock(GRAY_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(GRAY_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> GRAY_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "gray_glazed_glass_stairs",
                () -> new ModStairBlock(
                        GRAY_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(GRAY_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> BLACK_GLAZED_GLASS_SLAB = BLOCKS.register(
                "black_glazed_glass_slab",
                () -> new ModSlabBlock(BLACK_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(BLACK_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> BLACK_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "black_glazed_glass_stairs",
                () -> new ModStairBlock(
                        BLACK_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(BLACK_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> BROWN_GLAZED_GLASS_SLAB = BLOCKS.register(
                "brown_glazed_glass_slab",
                () -> new ModSlabBlock(BROWN_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(BROWN_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> BROWN_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "brown_glazed_glass_stairs",
                () -> new ModStairBlock(
                        BROWN_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(BROWN_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> RED_GLAZED_GLASS_SLAB = BLOCKS.register(
                "red_glazed_glass_slab",
                () -> new ModSlabBlock(RED_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(RED_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> RED_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "red_glazed_glass_stairs",
                () -> new ModStairBlock(
                        RED_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(RED_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> ORANGE_GLAZED_GLASS_SLAB = BLOCKS.register(
                "orange_glazed_glass_slab",
                () -> new ModSlabBlock(ORANGE_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(ORANGE_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> ORANGE_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "orange_glazed_glass_stairs",
                () -> new ModStairBlock(
                        ORANGE_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(ORANGE_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> YELLOW_GLAZED_GLASS_SLAB = BLOCKS.register(
                "yellow_glazed_glass_slab",
                () -> new ModSlabBlock(YELLOW_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(YELLOW_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> YELLOW_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "yellow_glazed_glass_stairs",
                () -> new ModStairBlock(
                        YELLOW_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(YELLOW_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> LIME_GLAZED_GLASS_SLAB = BLOCKS.register(
                "lime_glazed_glass_slab",
                () -> new ModSlabBlock(LIME_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(LIME_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> LIME_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "lime_glazed_glass_stairs",
                () -> new ModStairBlock(
                        LIME_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(LIME_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> GREEN_GLAZED_GLASS_SLAB = BLOCKS.register(
                "green_glazed_glass_slab",
                () -> new ModSlabBlock(GREEN_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(GREEN_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> GREEN_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "green_glazed_glass_stairs",
                () -> new ModStairBlock(
                        GREEN_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(GREEN_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> CYAN_GLAZED_GLASS_SLAB = BLOCKS.register(
                "cyan_glazed_glass_slab",
                () -> new ModSlabBlock(CYAN_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(CYAN_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> CYAN_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "cyan_glazed_glass_stairs",
                () -> new ModStairBlock(
                        CYAN_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(CYAN_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> LIGHT_BLUE_GLAZED_GLASS_SLAB = BLOCKS.register(
                "light_blue_glazed_glass_slab",
                () -> new ModSlabBlock(LIGHT_BLUE_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(LIGHT_BLUE_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> LIGHT_BLUE_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "light_blue_glazed_glass_stairs",
                () -> new ModStairBlock(
                        LIGHT_BLUE_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(LIGHT_BLUE_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> BLUE_GLAZED_GLASS_SLAB = BLOCKS.register(
                "blue_glazed_glass_slab",
                () -> new ModSlabBlock(BLUE_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(BLUE_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> BLUE_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "blue_glazed_glass_stairs",
                () -> new ModStairBlock(
                        BLUE_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(BLUE_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> PURPLE_GLAZED_GLASS_SLAB = BLOCKS.register(
                "purple_glazed_glass_slab",
                () -> new ModSlabBlock(PURPLE_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(PURPLE_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> PURPLE_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "purple_glazed_glass_stairs",
                () -> new ModStairBlock(
                        PURPLE_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(PURPLE_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> MAGENTA_GLAZED_GLASS_SLAB = BLOCKS.register(
                "magenta_glazed_glass_slab",
                () -> new ModSlabBlock(MAGENTA_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(MAGENTA_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> MAGENTA_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "magenta_glazed_glass_stairs",
                () -> new ModStairBlock(
                        MAGENTA_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(MAGENTA_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> PINK_GLAZED_GLASS_SLAB = BLOCKS.register(
                "pink_glazed_glass_slab",
                () -> new ModSlabBlock(PINK_GLAZED_GLASS.get(), 
                        BlockBehaviour.Properties.copy(PINK_GLAZED_GLASS.get())
                ));
        public static final RegistryObject<Block> PINK_GLAZED_GLASS_STAIRS = BLOCKS.register(
                "pink_glazed_glass_stairs",
                () -> new ModStairBlock(
                        PINK_GLAZED_GLASS.get().defaultBlockState(),
                        BlockBehaviour.Properties.copy(PINK_GLAZED_GLASS.get())
                ));

        // Log Slabs with Sitting Feature
        public static final RegistryObject<Block> OAK_LOG_SLAB = BLOCKS.register("oak_log_slab",
                () -> new LogSlabBlock(Blocks.OAK_LOG, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_OAK_LOG_SLAB = BLOCKS.register("stripped_oak_log_slab",
                () -> new LogSlabBlock(Blocks.STRIPPED_OAK_LOG, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> SPRUCE_LOG_SLAB = BLOCKS.register("spruce_log_slab",
                () -> new LogSlabBlock(Blocks.SPRUCE_LOG, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_SPRUCE_LOG_SLAB = BLOCKS.register("stripped_spruce_log_slab",
                () -> new LogSlabBlock(Blocks.STRIPPED_SPRUCE_LOG, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> BIRCH_LOG_SLAB = BLOCKS.register("birch_log_slab",
                () -> new LogSlabBlock(Blocks.BIRCH_LOG, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_BIRCH_LOG_SLAB = BLOCKS.register("stripped_birch_log_slab",
                () -> new LogSlabBlock(Blocks.STRIPPED_BIRCH_LOG, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> JUNGLE_LOG_SLAB = BLOCKS.register("jungle_log_slab",
                () -> new LogSlabBlock(Blocks.JUNGLE_LOG, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.DIRT).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_JUNGLE_LOG_SLAB = BLOCKS.register("stripped_jungle_log_slab",
                () -> new LogSlabBlock(Blocks.STRIPPED_JUNGLE_LOG, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.DIRT).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> ACACIA_LOG_SLAB = BLOCKS.register("acacia_log_slab",
                () -> new LogSlabBlock(Blocks.ACACIA_LOG, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_ACACIA_LOG_SLAB = BLOCKS.register("stripped_acacia_log_slab",
                () -> new LogSlabBlock(Blocks.STRIPPED_ACACIA_LOG, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> DARK_OAK_LOG_SLAB = BLOCKS.register("dark_oak_log_slab",
                () -> new LogSlabBlock(Blocks.DARK_OAK_LOG, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_DARK_OAK_LOG_SLAB = BLOCKS.register("stripped_dark_oak_log_slab",
                () -> new LogSlabBlock(Blocks.STRIPPED_DARK_OAK_LOG, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> CRIMSON_STEM_SLAB = BLOCKS.register("crimson_stem_slab",
                () -> new LogSlabBlock(Blocks.CRIMSON_STEM, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.STEM)));
        public static final RegistryObject<Block> STRIPPED_CRIMSON_STEM_SLAB = BLOCKS.register("stripped_crimson_stem_slab",
                () -> new LogSlabBlock(Blocks.STRIPPED_CRIMSON_STEM, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.STEM)));
        public static final RegistryObject<Block> WARPED_STEM_SLAB = BLOCKS.register("warped_stem_slab",
                () -> new LogSlabBlock(Blocks.WARPED_STEM, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.STEM)));
        public static final RegistryObject<Block> STRIPPED_WARPED_STEM_SLAB = BLOCKS.register("stripped_warped_stem_slab",
                () -> new LogSlabBlock(Blocks.STRIPPED_WARPED_STEM, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.STEM)));
        public static final RegistryObject<Block> MANGROVE_LOG_SLAB = BLOCKS.register("mangrove_log_slab",
                () -> new LogSlabBlock(MANGROVE_LOG.get(), BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_MANGROVE_LOG_SLAB = BLOCKS.register("stripped_mangrove_log_slab",
                () -> new LogSlabBlock(STRIPPED_MANGROVE_LOG.get(), BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));

        // Ashpen Log
        public static final RegistryObject<Block> ASHPEN_LOG = BLOCKS.register("ashpen_log",
                () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
        public static final RegistryObject<Block> ASHPEN_WOOD = BLOCKS.register("ashpen_wood",
                () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));
        public static final RegistryObject<Block> ASHPEN_LOG_SLAB = BLOCKS.register("ashpen_log_slab",
                () -> new LogSlabBlock(ASHPEN_LOG.get(), BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        public static final RegistryObject<Block> ASHPEN_WOOD_SLAB = BLOCKS.register("ashpen_wood_slab",
                () -> new LogSlabBlock(ASHPEN_WOOD.get(), BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0f).sound(net.minecraft.world.level.block.SoundType.WOOD)));
}
