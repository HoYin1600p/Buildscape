package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.block.ModSlabBlock;
import com.kingodogo.buildscape.block.ModStairBlock;
import com.kingodogo.buildscape.block.ModWallBlock;
import com.kingodogo.buildscape.block.VerticalSlabBlock;
import com.kingodogo.buildscape.block.WoolLayersBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringLargeChainBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.blocks.BlocksRegister.BLOCKS;

/**
 * The blocks LARGE_WEATHERED_COPPER_CHAIN through FLAMING_STEEL_BLOCK of the registry, part 10 of 12, in registration order.
 * Named for its dominant families (wool, light, copper); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link CopperBlocks#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class WoolBlocks {

    private WoolBlocks() {}

    /** Forces this class to load, which registers its blocks. */
    public static void init() {}

    static {
        CopperBlocks.init();
    }

    public static final RegistryObject<Block> LARGE_WEATHERED_COPPER_CHAIN = BLOCKS.register(
            "large_weathered_copper_chain",
            () -> new WeatheringLargeChainBlock(
                    BlockBehaviour.Properties.copy(Blocks.CHAIN)
                            .sound(SoundType.CHAIN)));
    public static final RegistryObject<Block> LARGE_OXIDIZED_COPPER_CHAIN = BLOCKS.register(
            "large_oxidized_copper_chain",
            () -> new WeatheringLargeChainBlock(
                    BlockBehaviour.Properties.copy(Blocks.CHAIN)
                            .sound(SoundType.CHAIN)));

    public static final RegistryObject<Block> WHITE_LAYERED_WOOL = BLOCKS.register(
            "white_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)));
    public static final RegistryObject<Block> ORANGE_LAYERED_WOOL = BLOCKS.register(
            "orange_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ORANGE_WOOL)));
    public static final RegistryObject<Block> MAGENTA_LAYERED_WOOL = BLOCKS.register(
            "magenta_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.MAGENTA_WOOL)));
    public static final RegistryObject<Block> LIGHT_BLUE_LAYERED_WOOL = BLOCKS.register(
            "light_blue_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_WOOL)));
    public static final RegistryObject<Block> YELLOW_LAYERED_WOOL = BLOCKS.register(
            "yellow_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.YELLOW_WOOL)));
    public static final RegistryObject<Block> LIME_LAYERED_WOOL = BLOCKS.register(
            "lime_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.LIME_WOOL)));
    public static final RegistryObject<Block> PINK_LAYERED_WOOL = BLOCKS.register(
            "pink_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.PINK_WOOL)));
    public static final RegistryObject<Block> GRAY_LAYERED_WOOL = BLOCKS.register(
            "gray_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.GRAY_WOOL)));
    public static final RegistryObject<Block> LIGHT_GRAY_LAYERED_WOOL = BLOCKS.register(
            "light_gray_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_WOOL)));
    public static final RegistryObject<Block> CYAN_LAYERED_WOOL = BLOCKS.register(
            "cyan_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL)));
    public static final RegistryObject<Block> PURPLE_LAYERED_WOOL = BLOCKS.register(
            "purple_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.PURPLE_WOOL)));
    public static final RegistryObject<Block> BLUE_LAYERED_WOOL = BLOCKS.register(
            "blue_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BLUE_WOOL)));
    public static final RegistryObject<Block> BROWN_LAYERED_WOOL = BLOCKS.register(
            "brown_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BROWN_WOOL)));
    public static final RegistryObject<Block> GREEN_LAYERED_WOOL = BLOCKS.register(
            "green_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.GREEN_WOOL)));
    public static final RegistryObject<Block> RED_LAYERED_WOOL = BLOCKS.register(
            "red_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.RED_WOOL)));
    public static final RegistryObject<Block> BLACK_LAYERED_WOOL = BLOCKS.register(
            "black_layered_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));

    public static final RegistryObject<Block> WHITE_LAYERED_WOOL_SLAB = BLOCKS.register("white_layered_wool_slab",
            () -> new ModSlabBlock(WHITE_LAYERED_WOOL.get(), BlockBehaviour.Properties.copy(WHITE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> WHITE_LAYERED_WOOL_STAIRS = BLOCKS.register("white_layered_wool_stairs",
            () -> new ModStairBlock(WHITE_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(WHITE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> WHITE_LAYERED_WOOL_WALL = BLOCKS.register("white_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(WHITE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> WHITE_LAYERED_WOOL_CARPET = BLOCKS.register("white_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(WHITE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> WHITE_LAYERED_WOOL_LAYERS = BLOCKS.register("white_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(WHITE_LAYERED_WOOL.get()), "white"));
    public static final RegistryObject<Block> WHITE_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "white_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(WHITE_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(WHITE_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> ORANGE_LAYERED_WOOL_SLAB = BLOCKS.register("orange_layered_wool_slab",
            () -> new ModSlabBlock(ORANGE_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(ORANGE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> ORANGE_LAYERED_WOOL_STAIRS = BLOCKS.register("orange_layered_wool_stairs",
            () -> new ModStairBlock(ORANGE_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(ORANGE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> ORANGE_LAYERED_WOOL_WALL = BLOCKS.register("orange_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(ORANGE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> ORANGE_LAYERED_WOOL_CARPET = BLOCKS.register("orange_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(ORANGE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> ORANGE_LAYERED_WOOL_LAYERS = BLOCKS.register("orange_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(ORANGE_LAYERED_WOOL.get()), "orange"));
    public static final RegistryObject<Block> ORANGE_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "orange_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(ORANGE_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(ORANGE_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> MAGENTA_LAYERED_WOOL_SLAB = BLOCKS.register("magenta_layered_wool_slab",
            () -> new ModSlabBlock(MAGENTA_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(MAGENTA_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> MAGENTA_LAYERED_WOOL_STAIRS = BLOCKS.register(
            "magenta_layered_wool_stairs",
            () -> new ModStairBlock(MAGENTA_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MAGENTA_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> MAGENTA_LAYERED_WOOL_WALL = BLOCKS.register("magenta_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(MAGENTA_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> MAGENTA_LAYERED_WOOL_CARPET = BLOCKS.register(
            "magenta_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(MAGENTA_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> MAGENTA_LAYERED_WOOL_LAYERS = BLOCKS.register(
            "magenta_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(MAGENTA_LAYERED_WOOL.get()), "magenta"));
    public static final RegistryObject<Block> MAGENTA_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "magenta_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(MAGENTA_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(MAGENTA_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> LIGHT_BLUE_LAYERED_WOOL_SLAB = BLOCKS.register(
            "light_blue_layered_wool_slab",
            () -> new ModSlabBlock(LIGHT_BLUE_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_LAYERED_WOOL_STAIRS = BLOCKS.register(
            "light_blue_layered_wool_stairs",
            () -> new ModStairBlock(LIGHT_BLUE_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_LAYERED_WOOL_WALL = BLOCKS.register(
            "light_blue_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(LIGHT_BLUE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_LAYERED_WOOL_CARPET = BLOCKS.register(
            "light_blue_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(LIGHT_BLUE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_LAYERED_WOOL_LAYERS = BLOCKS.register(
            "light_blue_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(LIGHT_BLUE_LAYERED_WOOL.get()), "light_blue"));
    public static final RegistryObject<Block> LIGHT_BLUE_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(LIGHT_BLUE_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> YELLOW_LAYERED_WOOL_SLAB = BLOCKS.register("yellow_layered_wool_slab",
            () -> new ModSlabBlock(YELLOW_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(YELLOW_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> YELLOW_LAYERED_WOOL_STAIRS = BLOCKS.register("yellow_layered_wool_stairs",
            () -> new ModStairBlock(YELLOW_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(YELLOW_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> YELLOW_LAYERED_WOOL_WALL = BLOCKS.register("yellow_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(YELLOW_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> YELLOW_LAYERED_WOOL_CARPET = BLOCKS.register("yellow_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(YELLOW_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> YELLOW_LAYERED_WOOL_LAYERS = BLOCKS.register("yellow_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(YELLOW_LAYERED_WOOL.get()), "yellow"));
    public static final RegistryObject<Block> YELLOW_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "yellow_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(YELLOW_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(YELLOW_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> LIME_LAYERED_WOOL_SLAB = BLOCKS.register("lime_layered_wool_slab",
            () -> new ModSlabBlock(LIME_LAYERED_WOOL.get(), BlockBehaviour.Properties.copy(LIME_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> LIME_LAYERED_WOOL_STAIRS = BLOCKS.register("lime_layered_wool_stairs",
            () -> new ModStairBlock(LIME_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIME_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> LIME_LAYERED_WOOL_WALL = BLOCKS.register("lime_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(LIME_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> LIME_LAYERED_WOOL_CARPET = BLOCKS.register("lime_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(LIME_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> LIME_LAYERED_WOOL_LAYERS = BLOCKS.register("lime_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(LIME_LAYERED_WOOL.get()), "lime"));
    public static final RegistryObject<Block> LIME_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "lime_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(LIME_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(LIME_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> PINK_LAYERED_WOOL_SLAB = BLOCKS.register("pink_layered_wool_slab",
            () -> new ModSlabBlock(PINK_LAYERED_WOOL.get(), BlockBehaviour.Properties.copy(PINK_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> PINK_LAYERED_WOOL_STAIRS = BLOCKS.register("pink_layered_wool_stairs",
            () -> new ModStairBlock(PINK_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(PINK_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> PINK_LAYERED_WOOL_WALL = BLOCKS.register("pink_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(PINK_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> PINK_LAYERED_WOOL_CARPET = BLOCKS.register("pink_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(PINK_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> PINK_LAYERED_WOOL_LAYERS = BLOCKS.register("pink_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(PINK_LAYERED_WOOL.get()), "pink"));
    public static final RegistryObject<Block> PINK_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "pink_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(PINK_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(PINK_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> GRAY_LAYERED_WOOL_SLAB = BLOCKS.register("gray_layered_wool_slab",
            () -> new ModSlabBlock(GRAY_LAYERED_WOOL.get(), BlockBehaviour.Properties.copy(GRAY_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> GRAY_LAYERED_WOOL_STAIRS = BLOCKS.register("gray_layered_wool_stairs",
            () -> new ModStairBlock(GRAY_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(GRAY_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> GRAY_LAYERED_WOOL_WALL = BLOCKS.register("gray_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(GRAY_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> GRAY_LAYERED_WOOL_CARPET = BLOCKS.register("gray_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(GRAY_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> GRAY_LAYERED_WOOL_LAYERS = BLOCKS.register("gray_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(GRAY_LAYERED_WOOL.get()), "gray"));
    public static final RegistryObject<Block> GRAY_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "gray_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(GRAY_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(GRAY_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> LIGHT_GRAY_LAYERED_WOOL_SLAB = BLOCKS.register(
            "light_gray_layered_wool_slab",
            () -> new ModSlabBlock(LIGHT_GRAY_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_LAYERED_WOOL_STAIRS = BLOCKS.register(
            "light_gray_layered_wool_stairs",
            () -> new ModStairBlock(LIGHT_GRAY_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_LAYERED_WOOL_WALL = BLOCKS.register(
            "light_gray_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(LIGHT_GRAY_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_LAYERED_WOOL_CARPET = BLOCKS.register(
            "light_gray_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(LIGHT_GRAY_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_LAYERED_WOOL_LAYERS = BLOCKS.register(
            "light_gray_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(LIGHT_GRAY_LAYERED_WOOL.get()), "light_gray"));
    public static final RegistryObject<Block> LIGHT_GRAY_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(LIGHT_GRAY_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> CYAN_LAYERED_WOOL_SLAB = BLOCKS.register("cyan_layered_wool_slab",
            () -> new ModSlabBlock(CYAN_LAYERED_WOOL.get(), BlockBehaviour.Properties.copy(CYAN_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> CYAN_LAYERED_WOOL_STAIRS = BLOCKS.register("cyan_layered_wool_stairs",
            () -> new ModStairBlock(CYAN_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(CYAN_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> CYAN_LAYERED_WOOL_WALL = BLOCKS.register("cyan_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(CYAN_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> CYAN_LAYERED_WOOL_CARPET = BLOCKS.register("cyan_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(CYAN_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> CYAN_LAYERED_WOOL_LAYERS = BLOCKS.register("cyan_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(CYAN_LAYERED_WOOL.get()), "cyan"));
    public static final RegistryObject<Block> CYAN_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "cyan_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(CYAN_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(CYAN_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> PURPLE_LAYERED_WOOL_SLAB = BLOCKS.register("purple_layered_wool_slab",
            () -> new ModSlabBlock(PURPLE_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(PURPLE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> PURPLE_LAYERED_WOOL_STAIRS = BLOCKS.register("purple_layered_wool_stairs",
            () -> new ModStairBlock(PURPLE_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(PURPLE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> PURPLE_LAYERED_WOOL_WALL = BLOCKS.register("purple_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(PURPLE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> PURPLE_LAYERED_WOOL_CARPET = BLOCKS.register("purple_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(PURPLE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> PURPLE_LAYERED_WOOL_LAYERS = BLOCKS.register("purple_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(PURPLE_LAYERED_WOOL.get()), "purple"));
    public static final RegistryObject<Block> PURPLE_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "purple_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(PURPLE_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(PURPLE_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> BLUE_LAYERED_WOOL_SLAB = BLOCKS.register("blue_layered_wool_slab",
            () -> new ModSlabBlock(BLUE_LAYERED_WOOL.get(), BlockBehaviour.Properties.copy(BLUE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> BLUE_LAYERED_WOOL_STAIRS = BLOCKS.register("blue_layered_wool_stairs",
            () -> new ModStairBlock(BLUE_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BLUE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> BLUE_LAYERED_WOOL_WALL = BLOCKS.register("blue_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(BLUE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> BLUE_LAYERED_WOOL_CARPET = BLOCKS.register("blue_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(BLUE_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> BLUE_LAYERED_WOOL_LAYERS = BLOCKS.register("blue_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(BLUE_LAYERED_WOOL.get()), "blue"));
    public static final RegistryObject<Block> BLUE_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "blue_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(BLUE_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(BLUE_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> BROWN_LAYERED_WOOL_SLAB = BLOCKS.register("brown_layered_wool_slab",
            () -> new ModSlabBlock(BROWN_LAYERED_WOOL.get(), BlockBehaviour.Properties.copy(BROWN_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> BROWN_LAYERED_WOOL_STAIRS = BLOCKS.register("brown_layered_wool_stairs",
            () -> new ModStairBlock(BROWN_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BROWN_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> BROWN_LAYERED_WOOL_WALL = BLOCKS.register("brown_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(BROWN_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> BROWN_LAYERED_WOOL_CARPET = BLOCKS.register("brown_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(BROWN_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> BROWN_LAYERED_WOOL_LAYERS = BLOCKS.register("brown_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(BROWN_LAYERED_WOOL.get()), "brown"));
    public static final RegistryObject<Block> BROWN_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "brown_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(BROWN_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(BROWN_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> GREEN_LAYERED_WOOL_SLAB = BLOCKS.register("green_layered_wool_slab",
            () -> new ModSlabBlock(GREEN_LAYERED_WOOL.get(), BlockBehaviour.Properties.copy(GREEN_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> GREEN_LAYERED_WOOL_STAIRS = BLOCKS.register("green_layered_wool_stairs",
            () -> new ModStairBlock(GREEN_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(GREEN_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> GREEN_LAYERED_WOOL_WALL = BLOCKS.register("green_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(GREEN_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> GREEN_LAYERED_WOOL_CARPET = BLOCKS.register("green_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(GREEN_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> GREEN_LAYERED_WOOL_LAYERS = BLOCKS.register("green_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(GREEN_LAYERED_WOOL.get()), "green"));
    public static final RegistryObject<Block> GREEN_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "green_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(GREEN_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(GREEN_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> RED_LAYERED_WOOL_SLAB = BLOCKS.register("red_layered_wool_slab",
            () -> new ModSlabBlock(RED_LAYERED_WOOL.get(), BlockBehaviour.Properties.copy(RED_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> RED_LAYERED_WOOL_STAIRS = BLOCKS.register("red_layered_wool_stairs",
            () -> new ModStairBlock(RED_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(RED_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> RED_LAYERED_WOOL_WALL = BLOCKS.register("red_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(RED_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> RED_LAYERED_WOOL_CARPET = BLOCKS.register("red_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(RED_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> RED_LAYERED_WOOL_LAYERS = BLOCKS.register("red_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(RED_LAYERED_WOOL.get()), "red"));
    public static final RegistryObject<Block> RED_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "red_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(RED_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(RED_LAYERED_WOOL_SLAB.get())));

    public static final RegistryObject<Block> BLACK_LAYERED_WOOL_SLAB = BLOCKS.register("black_layered_wool_slab",
            () -> new ModSlabBlock(BLACK_LAYERED_WOOL.get(), BlockBehaviour.Properties.copy(BLACK_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> BLACK_LAYERED_WOOL_STAIRS = BLOCKS.register("black_layered_wool_stairs",
            () -> new ModStairBlock(BLACK_LAYERED_WOOL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BLACK_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> BLACK_LAYERED_WOOL_WALL = BLOCKS.register("black_layered_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(BLACK_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> BLACK_LAYERED_WOOL_CARPET = BLOCKS.register("black_layered_wool_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(BLACK_LAYERED_WOOL.get())));
    public static final RegistryObject<Block> BLACK_LAYERED_WOOL_LAYERS = BLOCKS.register("black_layered_wool_layers",
            () -> new WoolLayersBlock(BlockBehaviour.Properties.copy(BLACK_LAYERED_WOOL.get()), "black"));
    public static final RegistryObject<Block> BLACK_LAYERED_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "black_layered_wool_vertical_slab",
            () -> new VerticalSlabBlock(BLACK_LAYERED_WOOL.get(),
                    BlockBehaviour.Properties.copy(BLACK_LAYERED_WOOL_SLAB.get())));

    static BlockBehaviour.Properties flamingSteelProperties() {
        return BlockBehaviour.Properties.of(Material.METAL, MaterialColor.NETHER)
                .strength(5.0f, 6.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHERITE_BLOCK);
    }

    public static final RegistryObject<Block> FLAMING_STEEL_BLOCK = BLOCKS.register("flaming_steel_block",
            () -> new Block(flamingSteelProperties()));
}
