package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.block.BuildersWorkbenchBlock;
import com.kingodogo.buildscape.block.FallingSandBlock;
import com.kingodogo.buildscape.block.GlazedGlassBlock;
import com.kingodogo.buildscape.block.ModBlock;
import com.kingodogo.buildscape.block.ModStairBlock;
import com.kingodogo.buildscape.block.MudBlock;
import com.kingodogo.buildscape.block.MudSlabBlock;
import com.kingodogo.buildscape.block.SilkTouchOnlyGlassBlock;
import com.kingodogo.buildscape.block.SilkTouchOnlyPaneBlock;
import com.kingodogo.buildscape.block.WaterloggableGrateBlock;
import com.kingodogo.buildscape.feature.copper.CopperBulbBlock;
import com.kingodogo.buildscape.feature.copper.ExposedCopperBulbBlock;
import com.kingodogo.buildscape.feature.copper.FreshCopperBulbBlock;
import com.kingodogo.buildscape.feature.copper.OxidizedCopperBulbBlock;
import com.kingodogo.buildscape.feature.copper.WeatheredCopperBulbBlock;
import com.kingodogo.buildscape.registry.ModItems;
import com.kingodogo.buildscape.registry.ModSounds;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.ModBlocks.BLOCKS;

/** Part of the blocks registrations; see {@code ModBlocks}. */
public final class BlocksPart01 {

    private BlocksPart01() {}

    /** Forces this class to load so its registrations run in the order {@code ModBlocks} calls it. */
    public static void init() {}

    public static final RegistryObject<Block> BUILDERS_WORKBENCH = BLOCKS.register(
            "builders_workbench",
            () -> new BuildersWorkbenchBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(2.5f)
                            .sound(SoundType.WOOD)));

    public static final RegistryObject<Block> BLACK_SAND = BLOCKS.register(
            "black_sand",
            () -> new FallingSandBlock(
                    BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_BLACK)
                            .strength(0.5f)
                            .sound(SoundType.SAND)));

    public static final RegistryObject<Block> BLUE_SAND = BLOCKS.register(
            "blue_sand",
            () -> new FallingSandBlock(
                    BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_BLUE)
                            .strength(0.5f)
                            .sound(SoundType.SAND)));

    public static final RegistryObject<Block> GREEN_SAND = BLOCKS.register(
            "green_sand",
            () -> new FallingSandBlock(
                    BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_GREEN)
                            .strength(0.5f)
                            .sound(SoundType.SAND)));

    public static final RegistryObject<Block> ORANGE_SAND = BLOCKS.register(
            "orange_sand",
            () -> new FallingSandBlock(
                    BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_ORANGE)
                            .strength(0.5f)
                            .sound(SoundType.SAND)));

    public static final RegistryObject<Block> PINK_SAND = BLOCKS.register(
            "pink_sand",
            () -> new FallingSandBlock(
                    BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_PINK)
                            .strength(0.5f)
                            .sound(SoundType.SAND)));

    public static final RegistryObject<Block> RED_SAND = BLOCKS.register(
            "red_sand",
            () -> new FallingSandBlock(
                    BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_RED)
                            .strength(0.5f)
                            .sound(SoundType.SAND)));

    public static final RegistryObject<Block> WHITE_SAND = BLOCKS.register(
            "white_sand",
            () -> new FallingSandBlock(
                    BlockBehaviour.Properties.of(Material.SAND, MaterialColor.SNOW)
                            .strength(0.5f)
                            .sound(SoundType.SAND)));

    public static final RegistryObject<Block> YELLOW_SAND = BLOCKS.register(
            "yellow_sand",
            () -> new FallingSandBlock(
                    BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_YELLOW)
                            .strength(0.5f)
                            .sound(SoundType.SAND)));

    public static final RegistryObject<Block> BLACK_SANDSTONE = BLOCKS.register(
            "black_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLACK_SANDSTONE_STAIRS = BLOCKS.register("black_sandstone_stairs",
            () -> new ModStairBlock(
                    BLACK_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE),
                    ModItems.BLACK_SANDSTONE_STAIRS));
    public static final RegistryObject<Block> BLUE_SANDSTONE = BLOCKS.register(
            "blue_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLUE_SANDSTONE_STAIRS = BLOCKS.register("blue_sandstone_stairs",
            () -> new ModStairBlock(
                    BLUE_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GREEN_SANDSTONE = BLOCKS.register(
            "green_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GREEN_SANDSTONE_STAIRS = BLOCKS.register("green_sandstone_stairs",
            () -> new ModStairBlock(
                    GREEN_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> ORANGE_SANDSTONE = BLOCKS.register(
            "orange_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> ORANGE_SANDSTONE_STAIRS = BLOCKS.register("orange_sandstone_stairs",
            () -> new ModStairBlock(
                    ORANGE_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PINK_SANDSTONE = BLOCKS.register(
            "pink_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PINK_SANDSTONE_STAIRS = BLOCKS.register("pink_sandstone_stairs",
            () -> new ModStairBlock(
                    PINK_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> RED_SANDSTONE = BLOCKS.register(
            "red_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> RED_SANDSTONE_STAIRS = BLOCKS.register("red_sandstone_stairs",
            () -> new ModStairBlock(
                    RED_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> WHITE_SANDSTONE = BLOCKS.register(
            "white_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> WHITE_SANDSTONE_STAIRS = BLOCKS.register("white_sandstone_stairs",
            () -> new ModStairBlock(
                    WHITE_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> YELLOW_SANDSTONE = BLOCKS.register(
            "yellow_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> YELLOW_SANDSTONE_STAIRS = BLOCKS.register("yellow_sandstone_stairs",
            () -> new ModStairBlock(
                    YELLOW_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLACK_TILES = BLOCKS.register(
            "black_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE),
                    ModItems.BLACK_TILES));
    public static final RegistryObject<Block> BLACK_TILES_STAIRS = BLOCKS.register("black_tiles_stairs",
            () -> new ModStairBlock(
                    BLACK_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> BLUE_TILES = BLOCKS.register(
            "blue_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> BLUE_TILES_STAIRS = BLOCKS.register(
            "blue_tiles_stairs",
            () -> new ModStairBlock(
                    BLUE_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> BROWN_TILES = BLOCKS.register(
            "brown_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> BROWN_TILES_STAIRS = BLOCKS.register("brown_tiles_stairs",
            () -> new ModStairBlock(
                    BROWN_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> CYAN_TILES = BLOCKS.register(
            "cyan_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> CYAN_TILES_STAIRS = BLOCKS.register(
            "cyan_tiles_stairs",
            () -> new ModStairBlock(
                    CYAN_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> GRAY_TILES = BLOCKS.register(
            "gray_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> GRAY_TILES_STAIRS = BLOCKS.register(
            "gray_tiles_stairs",
            () -> new ModStairBlock(
                    GRAY_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> GREEN_TILES = BLOCKS.register(
            "green_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> GREEN_TILES_STAIRS = BLOCKS.register("green_tiles_stairs",
            () -> new ModStairBlock(
                    GREEN_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> LIGHT_BLUE_TILES = BLOCKS.register(
            "light_blue_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> LIGHT_BLUE_TILES_STAIRS = BLOCKS.register("light_blue_tiles_stairs",
            () -> new ModStairBlock(
                    LIGHT_BLUE_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> LIGHT_GRAY_TILES = BLOCKS.register(
            "light_gray_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> LIGHT_GRAY_TILES_STAIRS = BLOCKS.register("light_gray_tiles_stairs",
            () -> new ModStairBlock(
                    LIGHT_GRAY_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> LIME_TILES = BLOCKS.register(
            "lime_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> LIME_TILES_STAIRS = BLOCKS.register(
            "lime_tiles_stairs",
            () -> new ModStairBlock(
                    LIME_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> MAGENTA_TILES = BLOCKS.register(
            "magenta_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_MAGENTA)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> MAGENTA_TILES_STAIRS = BLOCKS.register("magenta_tiles_stairs",
            () -> new ModStairBlock(
                    MAGENTA_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_MAGENTA)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> ORANGE_TILES = BLOCKS.register(
            "orange_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> ORANGE_TILES_STAIRS = BLOCKS.register("orange_tiles_stairs",
            () -> new ModStairBlock(
                    ORANGE_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> PINK_TILES = BLOCKS.register(
            "pink_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> PINK_TILES_STAIRS = BLOCKS.register(
            "pink_tiles_stairs",
            () -> new ModStairBlock(
                    PINK_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> PURPLE_TILES = BLOCKS.register(
            "purple_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> PURPLE_TILES_STAIRS = BLOCKS.register("purple_tiles_stairs",
            () -> new ModStairBlock(
                    PURPLE_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> RED_TILES = BLOCKS.register(
            "red_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> RED_TILES_STAIRS = BLOCKS.register(
            "red_tiles_stairs",
            () -> new ModStairBlock(
                    RED_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> WHITE_TILES = BLOCKS.register(
            "white_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> WHITE_TILES_STAIRS = BLOCKS.register("white_tiles_stairs",
            () -> new ModStairBlock(
                    WHITE_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> YELLOW_TILES = BLOCKS.register(
            "yellow_tiles",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> YELLOW_TILES_STAIRS = BLOCKS.register("yellow_tiles_stairs",
            () -> new ModStairBlock(
                    YELLOW_TILES.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> BLACK_MOSAIC_GLASS = BLOCKS.register("black_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> BLUE_MOSAIC_GLASS = BLOCKS.register(
            "blue_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> BROWN_MOSAIC_GLASS = BLOCKS.register("brown_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> CYAN_MOSAIC_GLASS = BLOCKS.register(
            "cyan_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> GRAY_MOSAIC_GLASS = BLOCKS.register(
            "gray_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> GREEN_MOSAIC_GLASS = BLOCKS.register("green_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> LIGHT_BLUE_MOSAIC_GLASS = BLOCKS.register("light_blue_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> LIGHT_GRAY_MOSAIC_GLASS = BLOCKS.register("light_gray_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> LIME_MOSAIC_GLASS = BLOCKS.register(
            "lime_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> MAGENTA_MOSAIC_GLASS = BLOCKS.register("magenta_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_MAGENTA)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> ORANGE_MOSAIC_GLASS = BLOCKS.register("orange_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> PINK_MOSAIC_GLASS = BLOCKS.register(
            "pink_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> PURPLE_MOSAIC_GLASS = BLOCKS.register("purple_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> RED_MOSAIC_GLASS = BLOCKS.register(
            "red_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> WHITE_MOSAIC_GLASS = BLOCKS.register("white_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> YELLOW_MOSAIC_GLASS = BLOCKS.register("yellow_mosaic_glass",
            () -> new SilkTouchOnlyGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> BIT_CHISELED_COPPER = BLOCKS.register("bit_chiseled_copper",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_COPPER_BLOCK = BLOCKS.register(
            "bit_copper_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_COPPER_BLOCK_STAIRS = BLOCKS.register("bit_copper_block_stairs",
            () -> new ModStairBlock(
                    BIT_COPPER_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_COPPER_BULB = BLOCKS.register(
            "bit_copper_bulb",
            () -> new FreshCopperBulbBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                            .strength(0.75f, 1.5f)
                            .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT)
                                    ? 15
                                    : 0)
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
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_CUT_COPPER_STAIRS = BLOCKS.register("bit_cut_copper_stairs",
            () -> new ModStairBlock(
                    BIT_CUT_COPPER.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_EXPOSED_CHISELED_COPPER = BLOCKS
            .register("bit_exposed_chiseled_copper", () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.TERRACOTTA_LIGHT_GRAY)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_EXPOSED_COPPER_BLOCK = BLOCKS.register("bit_exposed_copper_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.TERRACOTTA_LIGHT_GRAY)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_EXPOSED_COPPER_BLOCK_STAIRS = BLOCKS
            .register("bit_exposed_copper_block_stairs", () -> new ModStairBlock(
                    BIT_EXPOSED_COPPER_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.TERRACOTTA_LIGHT_GRAY)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_EXPOSED_COPPER_BULB = BLOCKS.register("bit_exposed_copper_bulb",
            () -> new ExposedCopperBulbBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.TERRACOTTA_LIGHT_GRAY)
                            .strength(0.75f, 1.5f)
                            .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT)
                                    ? 12
                                    : 0)
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
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_EXPOSED_CUT_COPPER_STAIRS = BLOCKS
            .register("bit_exposed_cut_copper_stairs", () -> new ModStairBlock(
                    BIT_EXPOSED_CUT_COPPER.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.TERRACOTTA_LIGHT_GRAY)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_WEATHERED_CHISELED_COPPER = BLOCKS
            .register("bit_weathered_chiseled_copper", () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.WARPED_NYLIUM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_WEATHERED_COPPER_BLOCK = BLOCKS
            .register("bit_weathered_copper_block", () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.WARPED_NYLIUM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_WEATHERED_COPPER_BLOCK_STAIRS = BLOCKS
            .register("bit_weathered_copper_block_stairs", () -> new ModStairBlock(
                    BIT_WEATHERED_COPPER_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.WARPED_NYLIUM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_WEATHERED_COPPER_BULB = BLOCKS
            .register("bit_weathered_copper_bulb", () -> new WeatheredCopperBulbBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.WARPED_NYLIUM)
                            .strength(0.75f, 1.5f)
                            .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT)
                                    ? 8
                                    : 0)
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
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_WEATHERED_CUT_COPPER_STAIRS = BLOCKS
            .register("bit_weathered_cut_copper_stairs", () -> new ModStairBlock(
                    BIT_WEATHERED_CUT_COPPER.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.WARPED_NYLIUM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_OXIDIZED_CHISELED_COPPER = BLOCKS
            .register("bit_oxidized_chiseled_copper", () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_OXIDIZED_COPPER_BLOCK = BLOCKS
            .register("bit_oxidized_copper_block", () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_OXIDIZED_COPPER_BLOCK_STAIRS = BLOCKS
            .register("bit_oxidized_copper_block_stairs", () -> new ModStairBlock(
                    BIT_OXIDIZED_COPPER_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_OXIDIZED_COPPER_BULB = BLOCKS.register("bit_oxidized_copper_bulb",
            () -> new OxidizedCopperBulbBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                            .strength(0.75f, 1.5f)
                            .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT)
                                    ? 4
                                    : 0)
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
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_OXIDIZED_CUT_COPPER_STAIRS = BLOCKS
            .register("bit_oxidized_cut_copper_stairs", () -> new ModStairBlock(
                    BIT_OXIDIZED_CUT_COPPER.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_CHISELED_TUFF = BLOCKS.register(
            "bit_chiseled_tuff",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_GRAY)
                            .strength(1.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.TUFF)));
    public static final RegistryObject<Block> BIT_CHISELED_TUFF_BRICKS = BLOCKS.register("bit_chiseled_tuff_bricks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_GRAY)
                            .strength(1.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.TUFF)));
    public static final RegistryObject<Block> BIT_POLISHED_TUFF = BLOCKS.register(
            "bit_polished_tuff",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_GRAY)
                            .strength(1.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.TUFF)));
    public static final RegistryObject<Block> BIT_POLISHED_TUFF_STAIRS = BLOCKS.register("bit_polished_tuff_stairs",
            () -> new ModStairBlock(
                    BIT_POLISHED_TUFF.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_GRAY)
                            .strength(1.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.TUFF)));
    public static final RegistryObject<Block> BIT_TUFF_BRICKS = BLOCKS.register(
            "bit_tuff_bricks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_GRAY)
                            .strength(1.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.TUFF)));
    public static final RegistryObject<Block> BIT_TUFF_BRICKS_STAIRS = BLOCKS.register("bit_tuff_bricks_stairs",
            () -> new ModStairBlock(
                    BIT_TUFF_BRICKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_GRAY)
                            .strength(1.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.TUFF)));
    public static final RegistryObject<Block> BIT_POLISHED_TUFF_SLAB = BLOCKS.register("bit_polished_tuff_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_GRAY)
                            .strength(1.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.TUFF)));
    public static final RegistryObject<Block> BIT_POLISHED_TUFF_WALL = BLOCKS.register("bit_polished_tuff_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_GRAY)
                            .strength(1.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.TUFF)));
    public static final RegistryObject<Block> BIT_TUFF_BRICKS_SLAB = BLOCKS.register("bit_tuff_bricks_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_GRAY)
                            .strength(1.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.TUFF)));
    public static final RegistryObject<Block> BIT_TUFF_BRICKS_WALL = BLOCKS.register("bit_tuff_bricks_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_GRAY)
                            .strength(1.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.TUFF)));
    public static final RegistryObject<Block> PODZOL_SLAB = BLOCKS.register(
            "podzol_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.PODZOL)
                            .strength(0.5f)
                            .sound(SoundType.GRASS)));
    public static final RegistryObject<Block> DIRT_SLAB = BLOCKS.register(
            "dirt_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT)
                            .strength(0.5f)
                            .sound(SoundType.GRAVEL)));
    public static final RegistryObject<Block> MYCELIUM_SLAB = BLOCKS.register(
            "mycelium_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.COLOR_PURPLE)
                            .strength(0.5f)
                            .sound(SoundType.GRASS)));
    public static final RegistryObject<Block> MUD = BLOCKS.register("mud", () -> new MudBlock(
            BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.COLOR_BROWN)
                    .strength(0.5f)
                    .sound(ModSounds.MUD_SOUNDS())));
    public static final RegistryObject<Block> MUD_STAIRS = BLOCKS.register("mud_stairs",
            () -> new ModStairBlock(MUD.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MUD.get())));
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
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLACK_SMOOTH_SANDSTONE_STAIRS = BLOCKS
            .register("black_smooth_sandstone_stairs", () -> new ModStairBlock(
                    BLACK_SMOOTH_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLUE_SMOOTH_SANDSTONE = BLOCKS.register("blue_smooth_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLUE_SMOOTH_SANDSTONE_STAIRS = BLOCKS
            .register("blue_smooth_sandstone_stairs", () -> new ModStairBlock(
                    BLUE_SMOOTH_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GREEN_SMOOTH_SANDSTONE = BLOCKS.register("green_smooth_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GREEN_SMOOTH_SANDSTONE_STAIRS = BLOCKS
            .register("green_smooth_sandstone_stairs", () -> new ModStairBlock(
                    GREEN_SMOOTH_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> ORANGE_SMOOTH_SANDSTONE = BLOCKS.register("orange_smooth_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> ORANGE_SMOOTH_SANDSTONE_STAIRS = BLOCKS
            .register("orange_smooth_sandstone_stairs", () -> new ModStairBlock(
                    ORANGE_SMOOTH_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PINK_SMOOTH_SANDSTONE = BLOCKS.register("pink_smooth_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PINK_SMOOTH_SANDSTONE_STAIRS = BLOCKS
            .register("pink_smooth_sandstone_stairs", () -> new ModStairBlock(
                    PINK_SMOOTH_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> RED_SMOOTH_SANDSTONE = BLOCKS.register("red_smooth_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> RED_SMOOTH_SANDSTONE_STAIRS = BLOCKS
            .register("red_smooth_sandstone_stairs", () -> new ModStairBlock(
                    RED_SMOOTH_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> WHITE_SMOOTH_SANDSTONE = BLOCKS.register("white_smooth_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> WHITE_SMOOTH_SANDSTONE_STAIRS = BLOCKS
            .register("white_smooth_sandstone_stairs", () -> new ModStairBlock(
                    WHITE_SMOOTH_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> YELLOW_SMOOTH_SANDSTONE = BLOCKS.register("yellow_smooth_sandstone",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> YELLOW_SMOOTH_SANDSTONE_STAIRS = BLOCKS
            .register("yellow_smooth_sandstone_stairs", () -> new ModStairBlock(
                    YELLOW_SMOOTH_SANDSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLACK_MOSAIC_GLASS_PANE = BLOCKS.register("black_mosaic_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> BLUE_MOSAIC_GLASS_PANE = BLOCKS.register("blue_mosaic_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> BROWN_MOSAIC_GLASS_PANE = BLOCKS.register("brown_mosaic_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> CYAN_MOSAIC_GLASS_PANE = BLOCKS.register("cyan_mosaic_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> GRAY_MOSAIC_GLASS_PANE = BLOCKS.register("gray_mosaic_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> GREEN_MOSAIC_GLASS_PANE = BLOCKS.register("green_mosaic_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> LIGHT_BLUE_MOSAIC_GLASS_PANE = BLOCKS
            .register("light_blue_mosaic_glass_pane", () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> LIGHT_GRAY_MOSAIC_GLASS_PANE = BLOCKS
            .register("light_gray_mosaic_glass_pane", () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> LIME_MOSAIC_GLASS_PANE = BLOCKS.register("lime_mosaic_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> MAGENTA_MOSAIC_GLASS_PANE = BLOCKS
            .register("magenta_mosaic_glass_pane", () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_MAGENTA)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> ORANGE_MOSAIC_GLASS_PANE = BLOCKS.register("orange_mosaic_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> PINK_MOSAIC_GLASS_PANE = BLOCKS.register("pink_mosaic_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> PURPLE_MOSAIC_GLASS_PANE = BLOCKS.register("purple_mosaic_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> RED_MOSAIC_GLASS_PANE = BLOCKS.register("red_mosaic_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> WHITE_MOSAIC_GLASS_PANE = BLOCKS.register("white_mosaic_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> YELLOW_MOSAIC_GLASS_PANE = BLOCKS.register("yellow_mosaic_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> BLACK_GLAZED_GLASS = BLOCKS.register("black_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> BLUE_GLAZED_GLASS = BLOCKS.register(
            "blue_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> BROWN_GLAZED_GLASS = BLOCKS.register("brown_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> CYAN_GLAZED_GLASS = BLOCKS.register(
            "cyan_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> GRAY_GLAZED_GLASS = BLOCKS.register(
            "gray_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> GREEN_GLAZED_GLASS = BLOCKS.register("green_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> LIGHT_BLUE_GLAZED_GLASS = BLOCKS.register("light_blue_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> LIGHT_GRAY_GLAZED_GLASS = BLOCKS.register("light_gray_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> LIME_GLAZED_GLASS = BLOCKS.register(
            "lime_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> MAGENTA_GLAZED_GLASS = BLOCKS.register("magenta_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_MAGENTA)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> ORANGE_GLAZED_GLASS = BLOCKS.register("orange_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> PINK_GLAZED_GLASS = BLOCKS.register(
            "pink_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> PURPLE_GLAZED_GLASS = BLOCKS.register("purple_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> RED_GLAZED_GLASS = BLOCKS.register(
            "red_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> WHITE_GLAZED_GLASS = BLOCKS.register("white_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> YELLOW_GLAZED_GLASS = BLOCKS.register("yellow_glazed_glass",
            () -> new GlazedGlassBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> BLACK_GLAZED_GLASS_PANE = BLOCKS.register("black_glazed_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> BLUE_GLAZED_GLASS_PANE = BLOCKS.register("blue_glazed_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> BROWN_GLAZED_GLASS_PANE = BLOCKS.register("brown_glazed_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> CYAN_GLAZED_GLASS_PANE = BLOCKS.register("cyan_glazed_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> GRAY_GLAZED_GLASS_PANE = BLOCKS.register("gray_glazed_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> GREEN_GLAZED_GLASS_PANE = BLOCKS.register("green_glazed_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> LIGHT_BLUE_GLAZED_GLASS_PANE = BLOCKS
            .register("light_blue_glazed_glass_pane", () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> LIGHT_GRAY_GLAZED_GLASS_PANE = BLOCKS
            .register("light_gray_glazed_glass_pane", () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> LIME_GLAZED_GLASS_PANE = BLOCKS.register("lime_glazed_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> MAGENTA_GLAZED_GLASS_PANE = BLOCKS
            .register("magenta_glazed_glass_pane", () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(
                            Material.GLASS,
                            MaterialColor.COLOR_MAGENTA)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> ORANGE_GLAZED_GLASS_PANE = BLOCKS.register("orange_glazed_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> PINK_GLAZED_GLASS_PANE = BLOCKS.register("pink_glazed_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> PURPLE_GLAZED_GLASS_PANE = BLOCKS.register("purple_glazed_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> RED_GLAZED_GLASS_PANE = BLOCKS.register("red_glazed_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> WHITE_GLAZED_GLASS_PANE = BLOCKS.register("white_glazed_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> YELLOW_GLAZED_GLASS_PANE = BLOCKS.register("yellow_glazed_glass_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                            .strength(0.3f)
                            .sound(SoundType.GLASS)
                            .noOcclusion()));
    public static final RegistryObject<Block> BLACK_SANDSTONE_SLAB = BLOCKS.register("black_sandstone_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLUE_SANDSTONE_SLAB = BLOCKS.register("blue_sandstone_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GREEN_SANDSTONE_SLAB = BLOCKS.register("green_sandstone_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> ORANGE_SANDSTONE_SLAB = BLOCKS.register("orange_sandstone_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PINK_SANDSTONE_SLAB = BLOCKS.register("pink_sandstone_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> RED_SANDSTONE_SLAB = BLOCKS.register("red_sandstone_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> WHITE_SANDSTONE_SLAB = BLOCKS.register("white_sandstone_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> YELLOW_SANDSTONE_SLAB = BLOCKS.register("yellow_sandstone_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLACK_SANDSTONE_WALL = BLOCKS.register("black_sandstone_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLUE_SANDSTONE_WALL = BLOCKS.register("blue_sandstone_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GREEN_SANDSTONE_WALL = BLOCKS.register("green_sandstone_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> ORANGE_SANDSTONE_WALL = BLOCKS.register("orange_sandstone_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PINK_SANDSTONE_WALL = BLOCKS.register("pink_sandstone_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> RED_SANDSTONE_WALL = BLOCKS.register("red_sandstone_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> WHITE_SANDSTONE_WALL = BLOCKS.register("white_sandstone_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> YELLOW_SANDSTONE_WALL = BLOCKS.register("yellow_sandstone_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLACK_SMOOTH_SANDSTONE_SLAB = BLOCKS
            .register("black_smooth_sandstone_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLUE_SMOOTH_SANDSTONE_SLAB = BLOCKS
            .register("blue_smooth_sandstone_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GREEN_SMOOTH_SANDSTONE_SLAB = BLOCKS
            .register("green_smooth_sandstone_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> ORANGE_SMOOTH_SANDSTONE_SLAB = BLOCKS
            .register("orange_smooth_sandstone_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PINK_SMOOTH_SANDSTONE_SLAB = BLOCKS
            .register("pink_smooth_sandstone_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> RED_SMOOTH_SANDSTONE_SLAB = BLOCKS
            .register("red_smooth_sandstone_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> WHITE_SMOOTH_SANDSTONE_SLAB = BLOCKS
            .register("white_smooth_sandstone_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> YELLOW_SMOOTH_SANDSTONE_SLAB = BLOCKS
            .register("yellow_smooth_sandstone_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> WHITE_SMOOTH_SANDSTONE_WALL = BLOCKS
            .register("white_smooth_sandstone_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLACK_SMOOTH_SANDSTONE_WALL = BLOCKS
            .register("black_smooth_sandstone_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> RED_SMOOTH_SANDSTONE_WALL = BLOCKS
            .register("red_smooth_sandstone_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> ORANGE_SMOOTH_SANDSTONE_WALL = BLOCKS
            .register("orange_smooth_sandstone_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> YELLOW_SMOOTH_SANDSTONE_WALL = BLOCKS
            .register("yellow_smooth_sandstone_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GREEN_SMOOTH_SANDSTONE_WALL = BLOCKS
            .register("green_smooth_sandstone_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLUE_SMOOTH_SANDSTONE_WALL = BLOCKS
            .register("blue_smooth_sandstone_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PINK_SMOOTH_SANDSTONE_WALL = BLOCKS
            .register("pink_smooth_sandstone_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
}
