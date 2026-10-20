package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.block.BigBookBlock;
import com.kingodogo.buildscape.block.CherryTreeGrower;
import com.kingodogo.buildscape.block.ColoredMossBlock;
import com.kingodogo.buildscape.block.ColoredMossLayersBlock;
import com.kingodogo.buildscape.block.CushionBlock;
import com.kingodogo.buildscape.block.LogSlabBlock;
import com.kingodogo.buildscape.block.ModDoorBlock;
import com.kingodogo.buildscape.block.ModSlabBlock;
import com.kingodogo.buildscape.block.ModStairBlock;
import com.kingodogo.buildscape.block.ModTrapdoorBlock;
import com.kingodogo.buildscape.block.ModWallBlock;
import com.kingodogo.buildscape.block.MossOverlayBlock;
import com.kingodogo.buildscape.block.MuffBlock;
import com.kingodogo.buildscape.block.PaleOakTreeGrower;
import com.kingodogo.buildscape.block.PoplarTreeGrower;
import com.kingodogo.buildscape.block.StrawBedBlock;
import com.kingodogo.buildscape.block.VerticalSlabBlock;
import com.kingodogo.buildscape.block.WallpaperFlatBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringVerticalSlabBlock;
import com.kingodogo.buildscape.registry.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WoodButtonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.blocks.BlocksRegister.BLOCKS;

/**
 * The blocks CINNABAR_BRICKS_SLAB through PALE_MOSS_CARPET of the registry, part 8 of 12, in registration order.
 * Named for its dominant families (wood, wool, stone); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link ShapedStoneBlocks#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class ShapedWoodBlocks {

    private ShapedWoodBlocks() {}

    /** Forces this class to load, which registers its blocks. */
    public static void init() {}

    static {
        ShapedStoneBlocks.init();
    }

    public static final RegistryObject<Block> CINNABAR_BRICKS_SLAB = BLOCKS.register("cinnabar_bricks_slab",
            () -> new ModSlabBlock(ShapedStoneBlocks.CINNABAR_BRICKS.get(),
                    BlockBehaviour.Properties.copy(ShapedStoneBlocks.CINNABAR_BRICKS.get())));
    public static final RegistryObject<Block> CINNABAR_BRICKS_WALL = BLOCKS.register("cinnabar_bricks_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(ShapedStoneBlocks.CINNABAR_BRICKS.get())));

    public static final RegistryObject<Block> POPLAR_LOG = BLOCKS.register("poplar_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
    public static final RegistryObject<Block> STRIPPED_POPLAR_LOG = BLOCKS.register("stripped_poplar_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final RegistryObject<Block> POPLAR_WOOD = BLOCKS.register("poplar_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));
    public static final RegistryObject<Block> STRIPPED_POPLAR_WOOD = BLOCKS.register("stripped_poplar_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final RegistryObject<Block> POPLAR_PLANKS = BLOCKS.register("poplar_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> POPLAR_STAIRS = BLOCKS.register("poplar_stairs",
            () -> new ModStairBlock(POPLAR_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));
    public static final RegistryObject<Block> POPLAR_SLAB = BLOCKS.register("poplar_slab",
            () -> new ModSlabBlock(POPLAR_PLANKS.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> POPLAR_FENCE = BLOCKS.register("poplar_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE)));
    public static final RegistryObject<Block> POPLAR_FENCE_GATE = BLOCKS.register("poplar_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE)));
    public static final RegistryObject<Block> POPLAR_DOOR = BLOCKS.register("poplar_door",
            () -> new ModDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR)));
    public static final RegistryObject<Block> POPLAR_TRAPDOOR = BLOCKS.register("poplar_trapdoor",
            () -> new ModTrapdoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR)));
    public static final RegistryObject<Block> POPLAR_PRESSURE_PLATE = BLOCKS.register("poplar_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE)));
    public static final RegistryObject<Block> POPLAR_BUTTON = BLOCKS.register("poplar_button",
            () -> new WoodButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON)));
    public static final RegistryObject<Block> POPLAR_SAPLING = BLOCKS.register("poplar_sapling",
            () -> new SaplingBlock(new PoplarTreeGrower(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));
    public static final RegistryObject<Block> ORANGE_POPLAR_LEAVES = BLOCKS.register("orange_poplar_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
    public static final RegistryObject<Block> RED_POPLAR_LEAVES = BLOCKS.register("red_poplar_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
    public static final RegistryObject<Block> YELLOW_POPLAR_LEAVES = BLOCKS.register("yellow_poplar_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
    public static final RegistryObject<Block> POPLAR_LOG_SLAB = BLOCKS.register("poplar_log_slab",
            () -> new LogSlabBlock(POPLAR_LOG.get(), BlockBehaviour.Properties.copy(POPLAR_LOG.get())));
    public static final RegistryObject<Block> POPLAR_LOG_STAIRS = BLOCKS.register("poplar_log_stairs",
            () -> new ModStairBlock(POPLAR_LOG.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(POPLAR_LOG.get())));
    public static final RegistryObject<Block> STRIPPED_POPLAR_LOG_SLAB = BLOCKS.register("stripped_poplar_log_slab",
            () -> new LogSlabBlock(STRIPPED_POPLAR_LOG.get(),
                    BlockBehaviour.Properties.copy(STRIPPED_POPLAR_LOG.get())));
    public static final RegistryObject<Block> STRIPPED_POPLAR_LOG_STAIRS = BLOCKS.register(
            "stripped_poplar_log_stairs",
            () -> new ModStairBlock(STRIPPED_POPLAR_LOG.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(STRIPPED_POPLAR_LOG.get())));
    public static final RegistryObject<Block> POPLAR_WOOD_SLAB = BLOCKS.register("poplar_wood_slab",
            () -> new LogSlabBlock(POPLAR_WOOD.get(), BlockBehaviour.Properties.copy(POPLAR_WOOD.get())));
    public static final RegistryObject<Block> POPLAR_WOOD_STAIRS = BLOCKS.register("poplar_wood_stairs",
            () -> new ModStairBlock(POPLAR_WOOD.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(POPLAR_WOOD.get())));
    public static final RegistryObject<Block> STRIPPED_POPLAR_WOOD_SLAB = BLOCKS.register(
            "stripped_poplar_wood_slab",
            () -> new LogSlabBlock(STRIPPED_POPLAR_WOOD.get(),
                    BlockBehaviour.Properties.copy(STRIPPED_POPLAR_WOOD.get())));
    public static final RegistryObject<Block> STRIPPED_POPLAR_WOOD_STAIRS = BLOCKS.register(
            "stripped_poplar_wood_stairs",
            () -> new ModStairBlock(STRIPPED_POPLAR_WOOD.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(STRIPPED_POPLAR_WOOD.get())));
    public static final RegistryObject<Block> POPLAR_LOG_WALL = BLOCKS.register("poplar_log_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                    .strength(2.0f).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_POPLAR_LOG_WALL = BLOCKS.register("stripped_poplar_log_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                    .strength(2.0f).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> WHITE_WOOL_SLAB = BLOCKS.register("white_wool_slab",
            () -> new ModSlabBlock(Blocks.WHITE_WOOL, BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)));
    public static final RegistryObject<Block> WHITE_WOOL_STAIRS = BLOCKS.register("white_wool_stairs",
            () -> new ModStairBlock(Blocks.WHITE_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)));
    public static final RegistryObject<Block> WHITE_WOOL_WALL = BLOCKS.register("white_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)));
    public static final RegistryObject<Block> ORANGE_WOOL_SLAB = BLOCKS.register("orange_wool_slab",
            () -> new ModSlabBlock(Blocks.ORANGE_WOOL, BlockBehaviour.Properties.copy(Blocks.ORANGE_WOOL)));
    public static final RegistryObject<Block> ORANGE_WOOL_STAIRS = BLOCKS.register("orange_wool_stairs",
            () -> new ModStairBlock(Blocks.ORANGE_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.ORANGE_WOOL)));
    public static final RegistryObject<Block> ORANGE_WOOL_WALL = BLOCKS.register("orange_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.ORANGE_WOOL)));
    public static final RegistryObject<Block> MAGENTA_WOOL_SLAB = BLOCKS.register("magenta_wool_slab",
            () -> new ModSlabBlock(Blocks.MAGENTA_WOOL,
                    BlockBehaviour.Properties.copy(Blocks.MAGENTA_WOOL)));
    public static final RegistryObject<Block> MAGENTA_WOOL_STAIRS = BLOCKS.register("magenta_wool_stairs",
            () -> new ModStairBlock(Blocks.MAGENTA_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.MAGENTA_WOOL)));
    public static final RegistryObject<Block> MAGENTA_WOOL_WALL = BLOCKS.register("magenta_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.MAGENTA_WOOL)));
    public static final RegistryObject<Block> LIGHT_BLUE_WOOL_SLAB = BLOCKS.register("light_blue_wool_slab",
            () -> new ModSlabBlock(Blocks.LIGHT_BLUE_WOOL,
                    BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_WOOL)));
    public static final RegistryObject<Block> LIGHT_BLUE_WOOL_STAIRS = BLOCKS.register("light_blue_wool_stairs",
            () -> new ModStairBlock(Blocks.LIGHT_BLUE_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_WOOL)));
    public static final RegistryObject<Block> LIGHT_BLUE_WOOL_WALL = BLOCKS.register("light_blue_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_WOOL)));
    public static final RegistryObject<Block> YELLOW_WOOL_SLAB = BLOCKS.register("yellow_wool_slab",
            () -> new ModSlabBlock(Blocks.YELLOW_WOOL, BlockBehaviour.Properties.copy(Blocks.YELLOW_WOOL)));
    public static final RegistryObject<Block> YELLOW_WOOL_STAIRS = BLOCKS.register("yellow_wool_stairs",
            () -> new ModStairBlock(Blocks.YELLOW_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.YELLOW_WOOL)));
    public static final RegistryObject<Block> YELLOW_WOOL_WALL = BLOCKS.register("yellow_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.YELLOW_WOOL)));
    public static final RegistryObject<Block> LIME_WOOL_SLAB = BLOCKS.register("lime_wool_slab",
            () -> new ModSlabBlock(Blocks.LIME_WOOL, BlockBehaviour.Properties.copy(Blocks.LIME_WOOL)));
    public static final RegistryObject<Block> LIME_WOOL_STAIRS = BLOCKS.register("lime_wool_stairs",
            () -> new ModStairBlock(Blocks.LIME_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.LIME_WOOL)));
    public static final RegistryObject<Block> LIME_WOOL_WALL = BLOCKS.register("lime_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.LIME_WOOL)));
    public static final RegistryObject<Block> PINK_WOOL_SLAB = BLOCKS.register("pink_wool_slab",
            () -> new ModSlabBlock(Blocks.PINK_WOOL, BlockBehaviour.Properties.copy(Blocks.PINK_WOOL)));
    public static final RegistryObject<Block> PINK_WOOL_STAIRS = BLOCKS.register("pink_wool_stairs",
            () -> new ModStairBlock(Blocks.PINK_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.PINK_WOOL)));
    public static final RegistryObject<Block> PINK_WOOL_WALL = BLOCKS.register("pink_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.PINK_WOOL)));
    public static final RegistryObject<Block> GRAY_WOOL_SLAB = BLOCKS.register("gray_wool_slab",
            () -> new ModSlabBlock(Blocks.GRAY_WOOL, BlockBehaviour.Properties.copy(Blocks.GRAY_WOOL)));
    public static final RegistryObject<Block> GRAY_WOOL_STAIRS = BLOCKS.register("gray_wool_stairs",
            () -> new ModStairBlock(Blocks.GRAY_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.GRAY_WOOL)));
    public static final RegistryObject<Block> GRAY_WOOL_WALL = BLOCKS.register("gray_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.GRAY_WOOL)));
    public static final RegistryObject<Block> LIGHT_GRAY_WOOL_SLAB = BLOCKS.register("light_gray_wool_slab",
            () -> new ModSlabBlock(Blocks.LIGHT_GRAY_WOOL,
                    BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_WOOL)));
    public static final RegistryObject<Block> LIGHT_GRAY_WOOL_STAIRS = BLOCKS.register("light_gray_wool_stairs",
            () -> new ModStairBlock(Blocks.LIGHT_GRAY_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_WOOL)));
    public static final RegistryObject<Block> LIGHT_GRAY_WOOL_WALL = BLOCKS.register("light_gray_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_WOOL)));
    public static final RegistryObject<Block> CYAN_WOOL_SLAB = BLOCKS.register("cyan_wool_slab",
            () -> new ModSlabBlock(Blocks.CYAN_WOOL, BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL)));
    public static final RegistryObject<Block> CYAN_WOOL_STAIRS = BLOCKS.register("cyan_wool_stairs",
            () -> new ModStairBlock(Blocks.CYAN_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL)));
    public static final RegistryObject<Block> CYAN_WOOL_WALL = BLOCKS.register("cyan_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL)));
    public static final RegistryObject<Block> PURPLE_WOOL_SLAB = BLOCKS.register("purple_wool_slab",
            () -> new ModSlabBlock(Blocks.PURPLE_WOOL, BlockBehaviour.Properties.copy(Blocks.PURPLE_WOOL)));
    public static final RegistryObject<Block> PURPLE_WOOL_STAIRS = BLOCKS.register("purple_wool_stairs",
            () -> new ModStairBlock(Blocks.PURPLE_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.PURPLE_WOOL)));
    public static final RegistryObject<Block> PURPLE_WOOL_WALL = BLOCKS.register("purple_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.PURPLE_WOOL)));
    public static final RegistryObject<Block> BLUE_WOOL_SLAB = BLOCKS.register("blue_wool_slab",
            () -> new ModSlabBlock(Blocks.BLUE_WOOL, BlockBehaviour.Properties.copy(Blocks.BLUE_WOOL)));
    public static final RegistryObject<Block> BLUE_WOOL_STAIRS = BLOCKS.register("blue_wool_stairs",
            () -> new ModStairBlock(Blocks.BLUE_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.BLUE_WOOL)));
    public static final RegistryObject<Block> BLUE_WOOL_WALL = BLOCKS.register("blue_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.BLUE_WOOL)));
    public static final RegistryObject<Block> BROWN_WOOL_SLAB = BLOCKS.register("brown_wool_slab",
            () -> new ModSlabBlock(Blocks.BROWN_WOOL, BlockBehaviour.Properties.copy(Blocks.BROWN_WOOL)));
    public static final RegistryObject<Block> BROWN_WOOL_STAIRS = BLOCKS.register("brown_wool_stairs",
            () -> new ModStairBlock(Blocks.BROWN_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.BROWN_WOOL)));
    public static final RegistryObject<Block> BROWN_WOOL_WALL = BLOCKS.register("brown_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_WOOL)));
    public static final RegistryObject<Block> GREEN_WOOL_SLAB = BLOCKS.register("green_wool_slab",
            () -> new ModSlabBlock(Blocks.GREEN_WOOL, BlockBehaviour.Properties.copy(Blocks.GREEN_WOOL)));
    public static final RegistryObject<Block> GREEN_WOOL_STAIRS = BLOCKS.register("green_wool_stairs",
            () -> new ModStairBlock(Blocks.GREEN_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.GREEN_WOOL)));
    public static final RegistryObject<Block> GREEN_WOOL_WALL = BLOCKS.register("green_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.GREEN_WOOL)));
    public static final RegistryObject<Block> RED_WOOL_SLAB = BLOCKS.register("red_wool_slab",
            () -> new ModSlabBlock(Blocks.RED_WOOL, BlockBehaviour.Properties.copy(Blocks.RED_WOOL)));
    public static final RegistryObject<Block> RED_WOOL_STAIRS = BLOCKS.register("red_wool_stairs",
            () -> new ModStairBlock(Blocks.RED_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.RED_WOOL)));
    public static final RegistryObject<Block> RED_WOOL_WALL = BLOCKS.register("red_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.RED_WOOL)));
    public static final RegistryObject<Block> BLACK_WOOL_SLAB = BLOCKS.register("black_wool_slab",
            () -> new ModSlabBlock(Blocks.BLACK_WOOL, BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));
    public static final RegistryObject<Block> BLACK_WOOL_STAIRS = BLOCKS.register("black_wool_stairs",
            () -> new ModStairBlock(Blocks.BLACK_WOOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));
    public static final RegistryObject<Block> BLACK_WOOL_WALL = BLOCKS.register("black_wool_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));

    public static final RegistryObject<Block> RED_MOSS_CARPET = BLOCKS.register("red_moss_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET)));
    public static final RegistryObject<Block> RED_MOSS_OVERLAY = BLOCKS.register("red_moss_overlay",
            () -> new MossOverlayBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET)));
    public static final RegistryObject<Block> RED_MOSS_LAYERS = BLOCKS.register("red_moss_layers",
            () -> new ColoredMossLayersBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET),
                    () -> ModItems.RED_MOSS_LAYERS.get(), () -> ShapedWoodBlocks.RED_MOSS_BLOCK.get()));
    public static final RegistryObject<Block> RED_MOSS_BLOCK = BLOCKS.register("red_moss_block",
            () -> new ColoredMossBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK), RED_MOSS_CARPET,
                    RED_MOSS_OVERLAY, RED_MOSS_LAYERS, () -> POPLAR_SAPLING.get(), () -> CopperBlocks.RED_BUSH.get()));
    public static final RegistryObject<Block> RED_MOSS_BLOCK_SLAB = BLOCKS.register("red_moss_block_slab",
            () -> new ModSlabBlock(RED_MOSS_BLOCK.get(),
                    BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK)));
    public static final RegistryObject<Block> RED_MOSS_BLOCK_STAIRS = BLOCKS.register("red_moss_block_stairs",
            () -> new ModStairBlock(RED_MOSS_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK)));

    public static final RegistryObject<Block> ORANGE_MOSS_CARPET = BLOCKS.register("orange_moss_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET)));
    public static final RegistryObject<Block> ORANGE_MOSS_OVERLAY = BLOCKS.register("orange_moss_overlay",
            () -> new MossOverlayBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET)));
    public static final RegistryObject<Block> ORANGE_MOSS_LAYERS = BLOCKS.register("orange_moss_layers",
            () -> new ColoredMossLayersBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET),
                    () -> ModItems.ORANGE_MOSS_LAYERS.get(), () -> ShapedWoodBlocks.ORANGE_MOSS_BLOCK.get()));
    public static final RegistryObject<Block> ORANGE_MOSS_BLOCK = BLOCKS.register("orange_moss_block",
            () -> new ColoredMossBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK),
                    ORANGE_MOSS_CARPET, ORANGE_MOSS_OVERLAY, ORANGE_MOSS_LAYERS, () -> POPLAR_SAPLING.get(),
                    () -> CopperBlocks.RED_BUSH.get()));
    public static final RegistryObject<Block> ORANGE_MOSS_BLOCK_SLAB = BLOCKS.register("orange_moss_block_slab",
            () -> new ModSlabBlock(ORANGE_MOSS_BLOCK.get(),
                    BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK)));
    public static final RegistryObject<Block> ORANGE_MOSS_BLOCK_STAIRS = BLOCKS.register("orange_moss_block_stairs",
            () -> new ModStairBlock(ORANGE_MOSS_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK)));

    public static final RegistryObject<Block> YELLOW_MOSS_CARPET = BLOCKS.register("yellow_moss_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET)));
    public static final RegistryObject<Block> YELLOW_MOSS_OVERLAY = BLOCKS.register("yellow_moss_overlay",
            () -> new MossOverlayBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET)));
    public static final RegistryObject<Block> YELLOW_MOSS_LAYERS = BLOCKS.register("yellow_moss_layers",
            () -> new ColoredMossLayersBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET),
                    () -> ModItems.YELLOW_MOSS_LAYERS.get(), () -> ShapedWoodBlocks.YELLOW_MOSS_BLOCK.get()));
    public static final RegistryObject<Block> YELLOW_MOSS_BLOCK = BLOCKS.register("yellow_moss_block",
            () -> new ColoredMossBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK),
                    YELLOW_MOSS_CARPET, YELLOW_MOSS_OVERLAY, YELLOW_MOSS_LAYERS, () -> POPLAR_SAPLING.get(),
                    () -> CopperBlocks.RED_BUSH.get()));
    public static final RegistryObject<Block> YELLOW_MOSS_BLOCK_SLAB = BLOCKS.register("yellow_moss_block_slab",
            () -> new ModSlabBlock(YELLOW_MOSS_BLOCK.get(),
                    BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK)));
    public static final RegistryObject<Block> YELLOW_MOSS_BLOCK_STAIRS = BLOCKS.register("yellow_moss_block_stairs",
            () -> new ModStairBlock(YELLOW_MOSS_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK)));

    public static final RegistryObject<Block> TUFF_SLAB = BLOCKS.register("tuff_slab",
            () -> new ModSlabBlock(Blocks.TUFF, BlockBehaviour.Properties.copy(Blocks.TUFF)));
    public static final RegistryObject<Block> TUFF_STAIRS = BLOCKS.register("tuff_stairs",
            () -> new ModStairBlock(Blocks.TUFF.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.TUFF)));
    public static final RegistryObject<Block> TUFF_WALL = BLOCKS.register("tuff_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(Blocks.TUFF)));
    public static final RegistryObject<Block> POLISHED_TUFF = BLOCKS.register("polished_tuff",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.TUFF)));
    public static final RegistryObject<Block> POLISHED_TUFF_SLAB = BLOCKS.register("polished_tuff_slab",
            () -> new ModSlabBlock(POLISHED_TUFF.get(),
                    BlockBehaviour.Properties.copy(POLISHED_TUFF.get())));
    public static final RegistryObject<Block> POLISHED_TUFF_STAIRS = BLOCKS.register("polished_tuff_stairs",
            () -> new ModStairBlock(POLISHED_TUFF.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(POLISHED_TUFF.get())));
    public static final RegistryObject<Block> POLISHED_TUFF_WALL = BLOCKS.register("polished_tuff_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(POLISHED_TUFF.get())));
    public static final RegistryObject<Block> CHISELED_TUFF = BLOCKS.register("chiseled_tuff",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.TUFF)));
    public static final RegistryObject<Block> TUFF_BRICKS = BLOCKS.register("tuff_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.TUFF)));
    public static final RegistryObject<Block> TUFF_BRICK_SLAB = BLOCKS.register("tuff_brick_slab",
            () -> new ModSlabBlock(TUFF_BRICKS.get(), BlockBehaviour.Properties.copy(TUFF_BRICKS.get())));
    public static final RegistryObject<Block> TUFF_BRICK_STAIRS = BLOCKS.register("tuff_brick_stairs",
            () -> new ModStairBlock(TUFF_BRICKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(TUFF_BRICKS.get())));
    public static final RegistryObject<Block> TUFF_BRICK_WALL = BLOCKS.register("tuff_brick_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(TUFF_BRICKS.get())));
    public static final RegistryObject<Block> CHISELED_TUFF_BRICKS = BLOCKS.register("chiseled_tuff_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.TUFF)));

    public static final RegistryObject<Block> WHITE_WOOL_VERTICAL_SLAB = BLOCKS.register("white_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.WHITE_WOOL,
                    BlockBehaviour.Properties.copy(WHITE_WOOL_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "orange_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.ORANGE_WOOL,
                    BlockBehaviour.Properties.copy(ORANGE_WOOL_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "magenta_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.MAGENTA_WOOL,
                    BlockBehaviour.Properties.copy(MAGENTA_WOOL_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.LIGHT_BLUE_WOOL,
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_WOOL_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "yellow_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.YELLOW_WOOL,
                    BlockBehaviour.Properties.copy(YELLOW_WOOL_SLAB.get())));
    public static final RegistryObject<Block> LIME_WOOL_VERTICAL_SLAB = BLOCKS.register("lime_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.LIME_WOOL,
                    BlockBehaviour.Properties.copy(LIME_WOOL_SLAB.get())));
    public static final RegistryObject<Block> PINK_WOOL_VERTICAL_SLAB = BLOCKS.register("pink_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.PINK_WOOL,
                    BlockBehaviour.Properties.copy(PINK_WOOL_SLAB.get())));
    public static final RegistryObject<Block> GRAY_WOOL_VERTICAL_SLAB = BLOCKS.register("gray_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.GRAY_WOOL,
                    BlockBehaviour.Properties.copy(GRAY_WOOL_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.LIGHT_GRAY_WOOL,
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_WOOL_SLAB.get())));
    public static final RegistryObject<Block> CYAN_WOOL_VERTICAL_SLAB = BLOCKS.register("cyan_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.CYAN_WOOL,
                    BlockBehaviour.Properties.copy(CYAN_WOOL_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_WOOL_VERTICAL_SLAB = BLOCKS.register(
            "purple_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.PURPLE_WOOL,
                    BlockBehaviour.Properties.copy(PURPLE_WOOL_SLAB.get())));
    public static final RegistryObject<Block> BLUE_WOOL_VERTICAL_SLAB = BLOCKS.register("blue_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BLUE_WOOL,
                    BlockBehaviour.Properties.copy(BLUE_WOOL_SLAB.get())));
    public static final RegistryObject<Block> BROWN_WOOL_VERTICAL_SLAB = BLOCKS.register("brown_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BROWN_WOOL,
                    BlockBehaviour.Properties.copy(BROWN_WOOL_SLAB.get())));
    public static final RegistryObject<Block> GREEN_WOOL_VERTICAL_SLAB = BLOCKS.register("green_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.GREEN_WOOL,
                    BlockBehaviour.Properties.copy(GREEN_WOOL_SLAB.get())));
    public static final RegistryObject<Block> RED_WOOL_VERTICAL_SLAB = BLOCKS.register("red_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.RED_WOOL,
                    BlockBehaviour.Properties.copy(RED_WOOL_SLAB.get())));
    public static final RegistryObject<Block> BLACK_WOOL_VERTICAL_SLAB = BLOCKS.register("black_wool_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BLACK_WOOL,
                    BlockBehaviour.Properties.copy(BLACK_WOOL_SLAB.get())));
    public static final RegistryObject<Block> RED_MOSS_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "red_moss_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    RED_MOSS_BLOCK.get(),
                    BlockBehaviour.Properties.copy(RED_MOSS_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_MOSS_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "orange_moss_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    ORANGE_MOSS_BLOCK.get(),
                    BlockBehaviour.Properties.copy(ORANGE_MOSS_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_MOSS_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "yellow_moss_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    YELLOW_MOSS_BLOCK.get(),
                    BlockBehaviour.Properties.copy(YELLOW_MOSS_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> TUFF_VERTICAL_SLAB = BLOCKS.register("tuff_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.TUFF,
                    BlockBehaviour.Properties.copy(TUFF_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_TUFF_VERTICAL_SLAB = BLOCKS.register(
            "polished_tuff_vertical_slab",
            () -> new VerticalSlabBlock(
                    POLISHED_TUFF.get(),
                    BlockBehaviour.Properties.copy(POLISHED_TUFF_SLAB.get())));
    public static final RegistryObject<Block> TUFF_BRICK_VERTICAL_SLAB = BLOCKS.register("tuff_brick_vertical_slab",
            () -> new VerticalSlabBlock(
                    TUFF_BRICKS.get(),
                    BlockBehaviour.Properties.copy(TUFF_BRICK_SLAB.get())));
    public static final RegistryObject<Block> SULFUR_VERTICAL_SLAB = BLOCKS.register("sulfur_vertical_slab",
            () -> new VerticalSlabBlock(
                    ShapedStoneBlocks.SULFUR.get(),
                    BlockBehaviour.Properties.copy(ShapedStoneBlocks.SULFUR_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_SULFUR_VERTICAL_SLAB = BLOCKS.register(
            "polished_sulfur_vertical_slab",
            () -> new VerticalSlabBlock(
                    ShapedStoneBlocks.POLISHED_SULFUR.get(),
                    BlockBehaviour.Properties.copy(ShapedStoneBlocks.POLISHED_SULFUR_SLAB.get())));
    public static final RegistryObject<Block> SULFUR_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "sulfur_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    ShapedStoneBlocks.SULFUR_BRICKS.get(),
                    BlockBehaviour.Properties.copy(ShapedStoneBlocks.SULFUR_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> CINNABAR_VERTICAL_SLAB = BLOCKS.register("cinnabar_vertical_slab",
            () -> new VerticalSlabBlock(
                    ShapedStoneBlocks.CINNABAR.get(),
                    BlockBehaviour.Properties.copy(ShapedStoneBlocks.CINNABAR_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_CINNABAR_VERTICAL_SLAB = BLOCKS.register(
            "polished_cinnabar_vertical_slab",
            () -> new VerticalSlabBlock(
                    ShapedStoneBlocks.POLISHED_CINNABAR.get(),
                    BlockBehaviour.Properties.copy(ShapedStoneBlocks.POLISHED_CINNABAR_SLAB.get())));
    public static final RegistryObject<Block> CINNABAR_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "cinnabar_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    ShapedStoneBlocks.CINNABAR_BRICKS.get(),
                    BlockBehaviour.Properties.copy(CINNABAR_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_ASHPEN_LOG_VERTICAL_SLAB = BLOCKS.register(
            "stripped_ashpen_log_vertical_slab",
            () -> new VerticalSlabBlock(
                    ShapedStoneBlocks.STRIPPED_ASHPEN_LOG.get(),
                    BlockBehaviour.Properties.copy(ShapedStoneBlocks.STRIPPED_ASHPEN_LOG_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_ASHPEN_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_ashpen_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    ShapedStoneBlocks.STRIPPED_ASHPEN_WOOD.get(),
                    BlockBehaviour.Properties.copy(ShapedStoneBlocks.STRIPPED_ASHPEN_WOOD_SLAB.get())));
    public static final RegistryObject<Block> POPLAR_VERTICAL_SLAB = BLOCKS.register("poplar_vertical_slab",
            () -> new VerticalSlabBlock(
                    POPLAR_PLANKS.get(),
                    BlockBehaviour.Properties.copy(POPLAR_SLAB.get())));
    public static final RegistryObject<Block> POPLAR_LOG_VERTICAL_SLAB = BLOCKS.register("poplar_log_vertical_slab",
            () -> new VerticalSlabBlock(
                    POPLAR_LOG.get(),
                    BlockBehaviour.Properties.copy(POPLAR_LOG_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_POPLAR_LOG_VERTICAL_SLAB = BLOCKS.register(
            "stripped_poplar_log_vertical_slab",
            () -> new VerticalSlabBlock(
                    STRIPPED_POPLAR_LOG.get(),
                    BlockBehaviour.Properties.copy(STRIPPED_POPLAR_LOG_SLAB.get())));
    public static final RegistryObject<Block> POPLAR_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "poplar_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    POPLAR_WOOD.get(),
                    BlockBehaviour.Properties.copy(POPLAR_WOOD_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_POPLAR_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_poplar_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    STRIPPED_POPLAR_WOOD.get(),
                    BlockBehaviour.Properties.copy(STRIPPED_POPLAR_WOOD_SLAB.get())));
    public static final RegistryObject<Block> OAK_VERTICAL_SLAB = BLOCKS.register("oak_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.OAK_PLANKS,
                    BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> SPRUCE_VERTICAL_SLAB = BLOCKS.register("spruce_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.SPRUCE_PLANKS,
                    BlockBehaviour.Properties.copy(Blocks.SPRUCE_SLAB)));
    public static final RegistryObject<Block> BIRCH_VERTICAL_SLAB = BLOCKS.register("birch_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BIRCH_PLANKS,
                    BlockBehaviour.Properties.copy(Blocks.BIRCH_SLAB)));
    public static final RegistryObject<Block> JUNGLE_VERTICAL_SLAB = BLOCKS.register("jungle_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.JUNGLE_PLANKS,
                    BlockBehaviour.Properties.copy(Blocks.JUNGLE_SLAB)));
    public static final RegistryObject<Block> ACACIA_VERTICAL_SLAB = BLOCKS.register("acacia_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.ACACIA_PLANKS,
                    BlockBehaviour.Properties.copy(Blocks.ACACIA_SLAB)));
    public static final RegistryObject<Block> DARK_OAK_VERTICAL_SLAB = BLOCKS.register("dark_oak_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.DARK_OAK_PLANKS,
                    BlockBehaviour.Properties.copy(Blocks.DARK_OAK_SLAB)));
    public static final RegistryObject<Block> CRIMSON_VERTICAL_SLAB = BLOCKS.register("crimson_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.CRIMSON_PLANKS,
                    BlockBehaviour.Properties.copy(Blocks.CRIMSON_SLAB)));
    public static final RegistryObject<Block> WARPED_VERTICAL_SLAB = BLOCKS.register("warped_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.WARPED_PLANKS,
                    BlockBehaviour.Properties.copy(Blocks.WARPED_SLAB)));
    public static final RegistryObject<Block> STONE_VERTICAL_SLAB = BLOCKS.register("stone_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.STONE,
                    BlockBehaviour.Properties.copy(Blocks.STONE_SLAB)));
    public static final RegistryObject<Block> SMOOTH_STONE_VERTICAL_SLAB = BLOCKS.register(
            "smooth_stone_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.SMOOTH_STONE,
                    BlockBehaviour.Properties.copy(Blocks.SMOOTH_STONE_SLAB)));
    public static final RegistryObject<Block> GRANITE_VERTICAL_SLAB = BLOCKS.register("granite_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.GRANITE,
                    BlockBehaviour.Properties.copy(Blocks.GRANITE_SLAB)));
    public static final RegistryObject<Block> POLISHED_GRANITE_VERTICAL_SLAB = BLOCKS.register(
            "polished_granite_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.POLISHED_GRANITE,
                    BlockBehaviour.Properties.copy(Blocks.POLISHED_GRANITE_SLAB)));
    public static final RegistryObject<Block> DIORITE_VERTICAL_SLAB = BLOCKS.register("diorite_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.DIORITE,
                    BlockBehaviour.Properties.copy(Blocks.DIORITE_SLAB)));
    public static final RegistryObject<Block> POLISHED_DIORITE_VERTICAL_SLAB = BLOCKS.register(
            "polished_diorite_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.POLISHED_DIORITE,
                    BlockBehaviour.Properties.copy(Blocks.POLISHED_DIORITE_SLAB)));
    public static final RegistryObject<Block> ANDESITE_VERTICAL_SLAB = BLOCKS.register("andesite_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.ANDESITE,
                    BlockBehaviour.Properties.copy(Blocks.ANDESITE_SLAB)));
    public static final RegistryObject<Block> POLISHED_ANDESITE_VERTICAL_SLAB = BLOCKS.register(
            "polished_andesite_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.POLISHED_ANDESITE,
                    BlockBehaviour.Properties.copy(Blocks.POLISHED_ANDESITE_SLAB)));
    public static final RegistryObject<Block> COBBLESTONE_VERTICAL_SLAB = BLOCKS.register(
            "cobblestone_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.COBBLESTONE,
                    BlockBehaviour.Properties.copy(Blocks.COBBLESTONE_SLAB)));
    public static final RegistryObject<Block> MOSSY_COBBLESTONE_VERTICAL_SLAB = BLOCKS.register(
            "mossy_cobblestone_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.MOSSY_COBBLESTONE,
                    BlockBehaviour.Properties.copy(Blocks.MOSSY_COBBLESTONE_SLAB)));
    public static final RegistryObject<Block> STONE_BRICK_VERTICAL_SLAB = BLOCKS.register(
            "stone_brick_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.STONE_BRICKS,
                    BlockBehaviour.Properties.copy(Blocks.STONE_BRICK_SLAB)));
    public static final RegistryObject<Block> MOSSY_STONE_BRICK_VERTICAL_SLAB = BLOCKS.register(
            "mossy_stone_brick_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.MOSSY_STONE_BRICKS,
                    BlockBehaviour.Properties.copy(Blocks.MOSSY_STONE_BRICK_SLAB)));
    public static final RegistryObject<Block> NETHER_BRICK_VERTICAL_SLAB = BLOCKS.register(
            "nether_brick_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.NETHER_BRICKS,
                    BlockBehaviour.Properties.copy(Blocks.NETHER_BRICK_SLAB)));
    public static final RegistryObject<Block> RED_NETHER_BRICK_VERTICAL_SLAB = BLOCKS.register(
            "red_nether_brick_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.RED_NETHER_BRICKS,
                    BlockBehaviour.Properties.copy(Blocks.RED_NETHER_BRICK_SLAB)));
    public static final RegistryObject<Block> OAK_LOG_STAIRS = BLOCKS.register("oak_log_stairs",
            () -> new ModStairBlock(Blocks.OAK_LOG.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));

    public static final RegistryObject<Block> CUT_COPPER_VERTICAL_SLAB = BLOCKS.register("cut_copper_vertical_slab",
            () -> new WeatheringVerticalSlabBlock(
                    Blocks.CUT_COPPER,
                    BlockBehaviour.Properties.copy(Blocks.CUT_COPPER)));
    public static final RegistryObject<Block> EXPOSED_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "exposed_cut_copper_vertical_slab",
            () -> new WeatheringVerticalSlabBlock(
                    Blocks.EXPOSED_CUT_COPPER,
                    BlockBehaviour.Properties.copy(Blocks.EXPOSED_CUT_COPPER)));
    public static final RegistryObject<Block> WEATHERED_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "weathered_cut_copper_vertical_slab",
            () -> new WeatheringVerticalSlabBlock(
                    Blocks.WEATHERED_CUT_COPPER,
                    BlockBehaviour.Properties.copy(Blocks.WEATHERED_CUT_COPPER)));
    public static final RegistryObject<Block> OXIDIZED_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "oxidized_cut_copper_vertical_slab",
            () -> new WeatheringVerticalSlabBlock(
                    Blocks.OXIDIZED_CUT_COPPER,
                    BlockBehaviour.Properties.copy(Blocks.OXIDIZED_CUT_COPPER)));
    public static final RegistryObject<Block> WAXED_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "waxed_cut_copper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.WAXED_CUT_COPPER,
                    BlockBehaviour.Properties.copy(Blocks.WAXED_CUT_COPPER)));
    public static final RegistryObject<Block> WAXED_EXPOSED_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "waxed_exposed_cut_copper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.WAXED_EXPOSED_CUT_COPPER,
                    BlockBehaviour.Properties.copy(Blocks.WAXED_EXPOSED_CUT_COPPER)));
    public static final RegistryObject<Block> WAXED_WEATHERED_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "waxed_weathered_cut_copper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.WAXED_WEATHERED_CUT_COPPER,
                    BlockBehaviour.Properties.copy(Blocks.WAXED_WEATHERED_CUT_COPPER)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "waxed_oxidized_cut_copper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.WAXED_OXIDIZED_CUT_COPPER,
                    BlockBehaviour.Properties.copy(Blocks.WAXED_OXIDIZED_CUT_COPPER)));
    public static final RegistryObject<Block> COBBLED_DEEPSLATE_VERTICAL_SLAB = BLOCKS.register(
            "cobbled_deepslate_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.COBBLED_DEEPSLATE,
                    BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final RegistryObject<Block> POLISHED_DEEPSLATE_VERTICAL_SLAB = BLOCKS.register(
            "polished_deepslate_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.POLISHED_DEEPSLATE,
                    BlockBehaviour.Properties.copy(Blocks.POLISHED_DEEPSLATE)));
    public static final RegistryObject<Block> DEEPSLATE_BRICK_VERTICAL_SLAB = BLOCKS.register(
            "deepslate_brick_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.DEEPSLATE_BRICKS,
                    BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_BRICKS)));
    public static final RegistryObject<Block> DEEPSLATE_TILE_VERTICAL_SLAB = BLOCKS.register(
            "deepslate_tile_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.DEEPSLATE_TILES,
                    BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_TILES)));
    public static final RegistryObject<Block> BLACKSTONE_VERTICAL_SLAB = BLOCKS.register("blackstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BLACKSTONE,
                    BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_VERTICAL_SLAB = BLOCKS.register(
            "polished_blackstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.POLISHED_BLACKSTONE,
                    BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE)));
    public static final RegistryObject<Block> POLISHED_BLACKSTONE_BRICK_VERTICAL_SLAB = BLOCKS.register(
            "polished_blackstone_brick_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.POLISHED_BLACKSTONE_BRICKS,
                    BlockBehaviour.Properties.copy(Blocks.POLISHED_BLACKSTONE_BRICKS)));
    public static final RegistryObject<Block> PRISMARINE_VERTICAL_SLAB = BLOCKS.register("prismarine_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.PRISMARINE,
                    BlockBehaviour.Properties.copy(Blocks.PRISMARINE)));
    public static final RegistryObject<Block> PRISMARINE_BRICK_VERTICAL_SLAB = BLOCKS.register(
            "prismarine_brick_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.PRISMARINE_BRICKS,
                    BlockBehaviour.Properties.copy(Blocks.PRISMARINE_BRICKS)));
    public static final RegistryObject<Block> DARK_PRISMARINE_VERTICAL_SLAB = BLOCKS.register(
            "dark_prismarine_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.DARK_PRISMARINE,
                    BlockBehaviour.Properties.copy(Blocks.DARK_PRISMARINE)));
    public static final RegistryObject<Block> BRICK_VERTICAL_SLAB = BLOCKS.register("brick_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BRICKS,
                    BlockBehaviour.Properties.copy(Blocks.BRICKS)));
    public static final RegistryObject<Block> END_STONE_BRICK_VERTICAL_SLAB = BLOCKS.register(
            "end_stone_brick_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.END_STONE_BRICKS,
                    BlockBehaviour.Properties.copy(Blocks.END_STONE_BRICKS)));
    public static final RegistryObject<Block> PURPUR_VERTICAL_SLAB = BLOCKS.register("purpur_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.PURPUR_BLOCK,
                    BlockBehaviour.Properties.copy(Blocks.PURPUR_BLOCK)));
    public static final RegistryObject<Block> QUARTZ_VERTICAL_SLAB = BLOCKS.register("quartz_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.QUARTZ_BLOCK,
                    BlockBehaviour.Properties.copy(Blocks.QUARTZ_BLOCK)));
    public static final RegistryObject<Block> SMOOTH_QUARTZ_VERTICAL_SLAB = BLOCKS.register(
            "smooth_quartz_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.SMOOTH_QUARTZ,
                    BlockBehaviour.Properties.copy(Blocks.SMOOTH_QUARTZ)));
    public static final RegistryObject<Block> TERRACOTTA_SLAB = BLOCKS.register("terracotta_slab",
            () -> new ModSlabBlock(Blocks.TERRACOTTA, BlockBehaviour.Properties.copy(Blocks.TERRACOTTA)));
    public static final RegistryObject<Block> TERRACOTTA_STAIRS = BLOCKS.register("terracotta_stairs",
            () -> new ModStairBlock(Blocks.TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.TERRACOTTA)));
    public static final RegistryObject<Block> TERRACOTTA_VERTICAL_SLAB = BLOCKS.register("terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.TERRACOTTA,
                    BlockBehaviour.Properties.copy(TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> WHITE_TERRACOTTA_SLAB = BLOCKS.register("white_terracotta_slab",
            () -> new ModSlabBlock(Blocks.WHITE_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.WHITE_TERRACOTTA)));
    public static final RegistryObject<Block> WHITE_TERRACOTTA_STAIRS = BLOCKS.register("white_terracotta_stairs",
            () -> new ModStairBlock(Blocks.WHITE_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.WHITE_TERRACOTTA)));
    public static final RegistryObject<Block> WHITE_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "white_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.WHITE_TERRACOTTA,
                    BlockBehaviour.Properties.copy(WHITE_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_TERRACOTTA_SLAB = BLOCKS.register("orange_terracotta_slab",
            () -> new ModSlabBlock(Blocks.ORANGE_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.ORANGE_TERRACOTTA)));
    public static final RegistryObject<Block> ORANGE_TERRACOTTA_STAIRS = BLOCKS.register("orange_terracotta_stairs",
            () -> new ModStairBlock(Blocks.ORANGE_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.ORANGE_TERRACOTTA)));
    public static final RegistryObject<Block> ORANGE_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "orange_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.ORANGE_TERRACOTTA,
                    BlockBehaviour.Properties.copy(ORANGE_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_TERRACOTTA_SLAB = BLOCKS.register("magenta_terracotta_slab",
            () -> new ModSlabBlock(Blocks.MAGENTA_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.MAGENTA_TERRACOTTA)));
    public static final RegistryObject<Block> MAGENTA_TERRACOTTA_STAIRS = BLOCKS.register(
            "magenta_terracotta_stairs",
            () -> new ModStairBlock(Blocks.MAGENTA_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.MAGENTA_TERRACOTTA)));
    public static final RegistryObject<Block> MAGENTA_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "magenta_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.MAGENTA_TERRACOTTA,
                    BlockBehaviour.Properties.copy(MAGENTA_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_TERRACOTTA_SLAB = BLOCKS.register(
            "light_blue_terracotta_slab",
            () -> new ModSlabBlock(Blocks.LIGHT_BLUE_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_TERRACOTTA)));
    public static final RegistryObject<Block> LIGHT_BLUE_TERRACOTTA_STAIRS = BLOCKS.register(
            "light_blue_terracotta_stairs",
            () -> new ModStairBlock(Blocks.LIGHT_BLUE_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_TERRACOTTA)));
    public static final RegistryObject<Block> LIGHT_BLUE_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.LIGHT_BLUE_TERRACOTTA,
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_TERRACOTTA_SLAB = BLOCKS.register("yellow_terracotta_slab",
            () -> new ModSlabBlock(Blocks.YELLOW_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.YELLOW_TERRACOTTA)));
    public static final RegistryObject<Block> YELLOW_TERRACOTTA_STAIRS = BLOCKS.register("yellow_terracotta_stairs",
            () -> new ModStairBlock(Blocks.YELLOW_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.YELLOW_TERRACOTTA)));
    public static final RegistryObject<Block> YELLOW_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "yellow_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.YELLOW_TERRACOTTA,
                    BlockBehaviour.Properties.copy(YELLOW_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> LIME_TERRACOTTA_SLAB = BLOCKS.register("lime_terracotta_slab",
            () -> new ModSlabBlock(Blocks.LIME_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.LIME_TERRACOTTA)));
    public static final RegistryObject<Block> LIME_TERRACOTTA_STAIRS = BLOCKS.register("lime_terracotta_stairs",
            () -> new ModStairBlock(Blocks.LIME_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.LIME_TERRACOTTA)));
    public static final RegistryObject<Block> LIME_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "lime_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.LIME_TERRACOTTA,
                    BlockBehaviour.Properties.copy(LIME_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> PINK_TERRACOTTA_SLAB = BLOCKS.register("pink_terracotta_slab",
            () -> new ModSlabBlock(Blocks.PINK_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.PINK_TERRACOTTA)));
    public static final RegistryObject<Block> PINK_TERRACOTTA_STAIRS = BLOCKS.register("pink_terracotta_stairs",
            () -> new ModStairBlock(Blocks.PINK_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.PINK_TERRACOTTA)));
    public static final RegistryObject<Block> PINK_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "pink_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.PINK_TERRACOTTA,
                    BlockBehaviour.Properties.copy(PINK_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> GRAY_TERRACOTTA_SLAB = BLOCKS.register("gray_terracotta_slab",
            () -> new ModSlabBlock(Blocks.GRAY_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.GRAY_TERRACOTTA)));
    public static final RegistryObject<Block> GRAY_TERRACOTTA_STAIRS = BLOCKS.register("gray_terracotta_stairs",
            () -> new ModStairBlock(Blocks.GRAY_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.GRAY_TERRACOTTA)));
    public static final RegistryObject<Block> GRAY_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "gray_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.GRAY_TERRACOTTA,
                    BlockBehaviour.Properties.copy(GRAY_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_TERRACOTTA_SLAB = BLOCKS.register(
            "light_gray_terracotta_slab",
            () -> new ModSlabBlock(Blocks.LIGHT_GRAY_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_TERRACOTTA)));
    public static final RegistryObject<Block> LIGHT_GRAY_TERRACOTTA_STAIRS = BLOCKS.register(
            "light_gray_terracotta_stairs",
            () -> new ModStairBlock(Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_TERRACOTTA)));
    public static final RegistryObject<Block> LIGHT_GRAY_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.LIGHT_GRAY_TERRACOTTA,
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> CYAN_TERRACOTTA_SLAB = BLOCKS.register("cyan_terracotta_slab",
            () -> new ModSlabBlock(Blocks.CYAN_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.CYAN_TERRACOTTA)));
    public static final RegistryObject<Block> CYAN_TERRACOTTA_STAIRS = BLOCKS.register("cyan_terracotta_stairs",
            () -> new ModStairBlock(Blocks.CYAN_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.CYAN_TERRACOTTA)));
    public static final RegistryObject<Block> CYAN_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "cyan_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.CYAN_TERRACOTTA,
                    BlockBehaviour.Properties.copy(CYAN_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_TERRACOTTA_SLAB = BLOCKS.register("purple_terracotta_slab",
            () -> new ModSlabBlock(Blocks.PURPLE_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.PURPLE_TERRACOTTA)));
    public static final RegistryObject<Block> PURPLE_TERRACOTTA_STAIRS = BLOCKS.register("purple_terracotta_stairs",
            () -> new ModStairBlock(Blocks.PURPLE_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.PURPLE_TERRACOTTA)));
    public static final RegistryObject<Block> PURPLE_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "purple_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.PURPLE_TERRACOTTA,
                    BlockBehaviour.Properties.copy(PURPLE_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> BLUE_TERRACOTTA_SLAB = BLOCKS.register("blue_terracotta_slab",
            () -> new ModSlabBlock(Blocks.BLUE_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.BLUE_TERRACOTTA)));
    public static final RegistryObject<Block> BLUE_TERRACOTTA_STAIRS = BLOCKS.register("blue_terracotta_stairs",
            () -> new ModStairBlock(Blocks.BLUE_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.BLUE_TERRACOTTA)));
    public static final RegistryObject<Block> BLUE_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "blue_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BLUE_TERRACOTTA,
                    BlockBehaviour.Properties.copy(BLUE_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> BROWN_TERRACOTTA_SLAB = BLOCKS.register("brown_terracotta_slab",
            () -> new ModSlabBlock(Blocks.BROWN_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.BROWN_TERRACOTTA)));
    public static final RegistryObject<Block> BROWN_TERRACOTTA_STAIRS = BLOCKS.register("brown_terracotta_stairs",
            () -> new ModStairBlock(Blocks.BROWN_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.BROWN_TERRACOTTA)));
    public static final RegistryObject<Block> BROWN_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "brown_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BROWN_TERRACOTTA,
                    BlockBehaviour.Properties.copy(BROWN_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> GREEN_TERRACOTTA_SLAB = BLOCKS.register("green_terracotta_slab",
            () -> new ModSlabBlock(Blocks.GREEN_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.GREEN_TERRACOTTA)));
    public static final RegistryObject<Block> GREEN_TERRACOTTA_STAIRS = BLOCKS.register("green_terracotta_stairs",
            () -> new ModStairBlock(Blocks.GREEN_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.GREEN_TERRACOTTA)));
    public static final RegistryObject<Block> GREEN_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "green_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.GREEN_TERRACOTTA,
                    BlockBehaviour.Properties.copy(GREEN_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> RED_TERRACOTTA_SLAB = BLOCKS.register("red_terracotta_slab",
            () -> new ModSlabBlock(Blocks.RED_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.RED_TERRACOTTA)));
    public static final RegistryObject<Block> RED_TERRACOTTA_STAIRS = BLOCKS.register("red_terracotta_stairs",
            () -> new ModStairBlock(Blocks.RED_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.RED_TERRACOTTA)));
    public static final RegistryObject<Block> RED_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "red_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.RED_TERRACOTTA,
                    BlockBehaviour.Properties.copy(RED_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> BLACK_TERRACOTTA_SLAB = BLOCKS.register("black_terracotta_slab",
            () -> new ModSlabBlock(Blocks.BLACK_TERRACOTTA,
                    BlockBehaviour.Properties.copy(Blocks.BLACK_TERRACOTTA)));
    public static final RegistryObject<Block> BLACK_TERRACOTTA_STAIRS = BLOCKS.register("black_terracotta_stairs",
            () -> new ModStairBlock(Blocks.BLACK_TERRACOTTA.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.BLACK_TERRACOTTA)));
    public static final RegistryObject<Block> BLACK_TERRACOTTA_VERTICAL_SLAB = BLOCKS.register(
            "black_terracotta_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BLACK_TERRACOTTA,
                    BlockBehaviour.Properties.copy(BLACK_TERRACOTTA_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_OAK_LOG_STAIRS = BLOCKS.register("stripped_oak_log_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_OAK_LOG.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final RegistryObject<Block> SPRUCE_LOG_STAIRS = BLOCKS.register("spruce_log_stairs",
            () -> new ModStairBlock(Blocks.SPRUCE_LOG.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG)));
    public static final RegistryObject<Block> STRIPPED_SPRUCE_LOG_STAIRS = BLOCKS.register(
            "stripped_spruce_log_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_SPRUCE_LOG.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_SPRUCE_LOG)));
    public static final RegistryObject<Block> BIRCH_LOG_STAIRS = BLOCKS.register("birch_log_stairs",
            () -> new ModStairBlock(Blocks.BIRCH_LOG.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.BIRCH_LOG)));
    public static final RegistryObject<Block> STRIPPED_BIRCH_LOG_STAIRS = BLOCKS.register(
            "stripped_birch_log_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_BIRCH_LOG.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_BIRCH_LOG)));
    public static final RegistryObject<Block> JUNGLE_LOG_STAIRS = BLOCKS.register("jungle_log_stairs",
            () -> new ModStairBlock(Blocks.JUNGLE_LOG.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.JUNGLE_LOG)));
    public static final RegistryObject<Block> STRIPPED_JUNGLE_LOG_STAIRS = BLOCKS.register(
            "stripped_jungle_log_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_JUNGLE_LOG.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_JUNGLE_LOG)));
    public static final RegistryObject<Block> ACACIA_LOG_STAIRS = BLOCKS.register("acacia_log_stairs",
            () -> new ModStairBlock(Blocks.ACACIA_LOG.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.ACACIA_LOG)));
    public static final RegistryObject<Block> STRIPPED_ACACIA_LOG_STAIRS = BLOCKS.register(
            "stripped_acacia_log_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_ACACIA_LOG.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_ACACIA_LOG)));
    public static final RegistryObject<Block> DARK_OAK_LOG_STAIRS = BLOCKS.register("dark_oak_log_stairs",
            () -> new ModStairBlock(Blocks.DARK_OAK_LOG.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.DARK_OAK_LOG)));
    public static final RegistryObject<Block> STRIPPED_DARK_OAK_LOG_STAIRS = BLOCKS.register(
            "stripped_dark_oak_log_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_DARK_OAK_LOG.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_DARK_OAK_LOG)));
    public static final RegistryObject<Block> CRIMSON_STEM_STAIRS = BLOCKS.register("crimson_stem_stairs",
            () -> new ModStairBlock(Blocks.CRIMSON_STEM.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.CRIMSON_STEM)));
    public static final RegistryObject<Block> STRIPPED_CRIMSON_STEM_STAIRS = BLOCKS.register(
            "stripped_crimson_stem_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_CRIMSON_STEM.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_CRIMSON_STEM)));
    public static final RegistryObject<Block> WARPED_STEM_STAIRS = BLOCKS.register("warped_stem_stairs",
            () -> new ModStairBlock(Blocks.WARPED_STEM.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.WARPED_STEM)));
    public static final RegistryObject<Block> STRIPPED_WARPED_STEM_STAIRS = BLOCKS.register(
            "stripped_warped_stem_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_WARPED_STEM.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_WARPED_STEM)));

    public static final RegistryObject<Block> ACACIA_WOOD_SLAB = BLOCKS.register("acacia_wood_slab",
            () -> new ModSlabBlock(Blocks.ACACIA_WOOD, BlockBehaviour.Properties.copy(Blocks.ACACIA_WOOD)));
    public static final RegistryObject<Block> ACACIA_WOOD_STAIRS = BLOCKS.register("acacia_wood_stairs",
            () -> new ModStairBlock(Blocks.ACACIA_WOOD.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.ACACIA_WOOD)));
    public static final RegistryObject<Block> ACACIA_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "acacia_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.ACACIA_WOOD,
                    BlockBehaviour.Properties.copy(ACACIA_WOOD_SLAB.get())));
    public static final RegistryObject<Block> AMETHYST_BLOCK_STAIRS = BLOCKS.register("amethyst_block_stairs",
            () -> new ModStairBlock(Blocks.AMETHYST_BLOCK.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)));
    public static final RegistryObject<Block> BIRCH_WOOD_SLAB = BLOCKS.register("birch_wood_slab",
            () -> new ModSlabBlock(Blocks.BIRCH_WOOD, BlockBehaviour.Properties.copy(Blocks.BIRCH_WOOD)));
    public static final RegistryObject<Block> BIRCH_WOOD_STAIRS = BLOCKS.register("birch_wood_stairs",
            () -> new ModStairBlock(Blocks.BIRCH_WOOD.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.BIRCH_WOOD)));
    public static final RegistryObject<Block> BIRCH_WOOD_VERTICAL_SLAB = BLOCKS.register("birch_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BIRCH_WOOD,
                    BlockBehaviour.Properties.copy(BIRCH_WOOD_SLAB.get())));
    public static final RegistryObject<Block> CRIMSON_HYPHAE_SLAB = BLOCKS.register("crimson_hyphae_slab",
            () -> new ModSlabBlock(Blocks.CRIMSON_HYPHAE,
                    BlockBehaviour.Properties.copy(Blocks.CRIMSON_HYPHAE)));
    public static final RegistryObject<Block> CRIMSON_HYPHAE_STAIRS = BLOCKS.register("crimson_hyphae_stairs",
            () -> new ModStairBlock(Blocks.CRIMSON_HYPHAE.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.CRIMSON_HYPHAE)));
    public static final RegistryObject<Block> CRIMSON_HYPHAE_VERTICAL_SLAB = BLOCKS.register(
            "crimson_hyphae_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.CRIMSON_HYPHAE,
                    BlockBehaviour.Properties.copy(CRIMSON_HYPHAE_SLAB.get())));
    public static final RegistryObject<Block> CRYING_OBSIDIAN_SLAB = BLOCKS.register("crying_obsidian_slab",
            () -> new ModSlabBlock(Blocks.CRYING_OBSIDIAN,
                    BlockBehaviour.Properties.copy(Blocks.CRYING_OBSIDIAN)));
    public static final RegistryObject<Block> CRYING_OBSIDIAN_STAIRS = BLOCKS.register("crying_obsidian_stairs",
            () -> new ModStairBlock(Blocks.CRYING_OBSIDIAN.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.CRYING_OBSIDIAN)));
    public static final RegistryObject<Block> CRYING_OBSIDIAN_VERTICAL_SLAB = BLOCKS.register(
            "crying_obsidian_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.CRYING_OBSIDIAN,
                    BlockBehaviour.Properties.copy(CRYING_OBSIDIAN_SLAB.get())));
    public static final RegistryObject<Block> CUT_RED_SANDSTONE_STAIRS = BLOCKS.register("cut_red_sandstone_stairs",
            () -> new ModStairBlock(Blocks.CUT_RED_SANDSTONE.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.CUT_RED_SANDSTONE)));
    public static final RegistryObject<Block> CUT_RED_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "cut_red_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.CUT_RED_SANDSTONE,
                    BlockBehaviour.Properties.copy(Blocks.CUT_RED_SANDSTONE)));
    public static final RegistryObject<Block> CUT_SANDSTONE_STAIRS = BLOCKS.register("cut_sandstone_stairs",
            () -> new ModStairBlock(Blocks.CUT_SANDSTONE.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.CUT_SANDSTONE)));
    public static final RegistryObject<Block> CUT_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "cut_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.CUT_SANDSTONE,
                    BlockBehaviour.Properties.copy(Blocks.CUT_SANDSTONE)));
    public static final RegistryObject<Block> DARK_OAK_WOOD_SLAB = BLOCKS.register("dark_oak_wood_slab",
            () -> new ModSlabBlock(Blocks.DARK_OAK_WOOD,
                    BlockBehaviour.Properties.copy(Blocks.DARK_OAK_WOOD)));
    public static final RegistryObject<Block> DARK_OAK_WOOD_STAIRS = BLOCKS.register("dark_oak_wood_stairs",
            () -> new ModStairBlock(Blocks.DARK_OAK_WOOD.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.DARK_OAK_WOOD)));
    public static final RegistryObject<Block> DARK_OAK_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "dark_oak_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.DARK_OAK_WOOD,
                    BlockBehaviour.Properties.copy(DARK_OAK_WOOD_SLAB.get())));
    public static final RegistryObject<Block> DIRT_STAIRS = BLOCKS.register("dirt_stairs",
            () -> new ModStairBlock(Blocks.DIRT.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.DIRT)));
    public static final RegistryObject<Block> JUNGLE_WOOD_SLAB = BLOCKS.register("jungle_wood_slab",
            () -> new ModSlabBlock(Blocks.JUNGLE_WOOD, BlockBehaviour.Properties.copy(Blocks.JUNGLE_WOOD)));
    public static final RegistryObject<Block> JUNGLE_WOOD_STAIRS = BLOCKS.register("jungle_wood_stairs",
            () -> new ModStairBlock(Blocks.JUNGLE_WOOD.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.JUNGLE_WOOD)));
    public static final RegistryObject<Block> JUNGLE_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "jungle_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.JUNGLE_WOOD,
                    BlockBehaviour.Properties.copy(JUNGLE_WOOD_SLAB.get())));
    public static final RegistryObject<Block> MOSS_BLOCK_STAIRS = BLOCKS.register("moss_block_stairs",
            () -> new ModStairBlock(Blocks.MOSS_BLOCK.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK)));
    public static final RegistryObject<Block> MYCELIUM_STAIRS = BLOCKS.register("mycelium_stairs",
            () -> new ModStairBlock(Blocks.MYCELIUM.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.MYCELIUM)));
    public static final RegistryObject<Block> OAK_WOOD_SLAB = BLOCKS.register("oak_wood_slab",
            () -> new ModSlabBlock(Blocks.OAK_WOOD, BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));
    public static final RegistryObject<Block> OAK_WOOD_STAIRS = BLOCKS.register("oak_wood_stairs",
            () -> new ModStairBlock(Blocks.OAK_WOOD.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));
    public static final RegistryObject<Block> OAK_WOOD_VERTICAL_SLAB = BLOCKS.register("oak_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.OAK_WOOD,
                    BlockBehaviour.Properties.copy(OAK_WOOD_SLAB.get())));
    public static final RegistryObject<Block> PODZOL_STAIRS = BLOCKS.register("podzol_stairs",
            () -> new ModStairBlock(Blocks.PODZOL.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.PODZOL)));
    public static final RegistryObject<Block> SANDSTONE_VERTICAL_SLAB = BLOCKS.register("sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.SANDSTONE,
                    BlockBehaviour.Properties.copy(Blocks.SANDSTONE)));
    public static final RegistryObject<Block> SMOOTH_RED_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "smooth_red_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.SMOOTH_RED_SANDSTONE,
                    BlockBehaviour.Properties.copy(Blocks.SMOOTH_RED_SANDSTONE)));
    public static final RegistryObject<Block> SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "smooth_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.SMOOTH_SANDSTONE,
                    BlockBehaviour.Properties.copy(Blocks.SMOOTH_SANDSTONE)));
    public static final RegistryObject<Block> SPRUCE_WOOD_SLAB = BLOCKS.register("spruce_wood_slab",
            () -> new ModSlabBlock(Blocks.SPRUCE_WOOD, BlockBehaviour.Properties.copy(Blocks.SPRUCE_WOOD)));
    public static final RegistryObject<Block> SPRUCE_WOOD_STAIRS = BLOCKS.register("spruce_wood_stairs",
            () -> new ModStairBlock(Blocks.SPRUCE_WOOD.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.SPRUCE_WOOD)));
    public static final RegistryObject<Block> SPRUCE_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "spruce_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.SPRUCE_WOOD,
                    BlockBehaviour.Properties.copy(SPRUCE_WOOD_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_ACACIA_WOOD_SLAB = BLOCKS.register(
            "stripped_acacia_wood_slab",
            () -> new ModSlabBlock(Blocks.STRIPPED_ACACIA_WOOD,
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_ACACIA_WOOD)));
    public static final RegistryObject<Block> STRIPPED_ACACIA_WOOD_STAIRS = BLOCKS.register(
            "stripped_acacia_wood_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_ACACIA_WOOD.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_ACACIA_WOOD)));
    public static final RegistryObject<Block> STRIPPED_ACACIA_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_acacia_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.STRIPPED_ACACIA_WOOD,
                    BlockBehaviour.Properties.copy(STRIPPED_ACACIA_WOOD_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_BIRCH_WOOD_SLAB = BLOCKS.register("stripped_birch_wood_slab",
            () -> new ModSlabBlock(Blocks.STRIPPED_BIRCH_WOOD,
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_BIRCH_WOOD)));
    public static final RegistryObject<Block> STRIPPED_BIRCH_WOOD_STAIRS = BLOCKS.register(
            "stripped_birch_wood_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_BIRCH_WOOD.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_BIRCH_WOOD)));
    public static final RegistryObject<Block> STRIPPED_BIRCH_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_birch_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.STRIPPED_BIRCH_WOOD,
                    BlockBehaviour.Properties.copy(STRIPPED_BIRCH_WOOD_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_CRIMSON_HYPHAE_SLAB = BLOCKS.register(
            "stripped_crimson_hyphae_slab",
            () -> new ModSlabBlock(Blocks.STRIPPED_CRIMSON_HYPHAE,
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_CRIMSON_HYPHAE)));
    public static final RegistryObject<Block> STRIPPED_CRIMSON_HYPHAE_STAIRS = BLOCKS.register(
            "stripped_crimson_hyphae_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_CRIMSON_HYPHAE.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_CRIMSON_HYPHAE)));
    public static final RegistryObject<Block> STRIPPED_CRIMSON_HYPHAE_VERTICAL_SLAB = BLOCKS.register(
            "stripped_crimson_hyphae_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.STRIPPED_CRIMSON_HYPHAE,
                    BlockBehaviour.Properties.copy(STRIPPED_CRIMSON_HYPHAE_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_DARK_OAK_WOOD_SLAB = BLOCKS.register(
            "stripped_dark_oak_wood_slab",
            () -> new ModSlabBlock(Blocks.STRIPPED_DARK_OAK_WOOD,
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_DARK_OAK_WOOD)));
    public static final RegistryObject<Block> STRIPPED_DARK_OAK_WOOD_STAIRS = BLOCKS.register(
            "stripped_dark_oak_wood_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_DARK_OAK_WOOD.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_DARK_OAK_WOOD)));
    public static final RegistryObject<Block> STRIPPED_DARK_OAK_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_dark_oak_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.STRIPPED_DARK_OAK_WOOD,
                    BlockBehaviour.Properties.copy(STRIPPED_DARK_OAK_WOOD_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_JUNGLE_WOOD_SLAB = BLOCKS.register(
            "stripped_jungle_wood_slab",
            () -> new ModSlabBlock(Blocks.STRIPPED_JUNGLE_WOOD,
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_JUNGLE_WOOD)));
    public static final RegistryObject<Block> STRIPPED_JUNGLE_WOOD_STAIRS = BLOCKS.register(
            "stripped_jungle_wood_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_JUNGLE_WOOD.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_JUNGLE_WOOD)));
    public static final RegistryObject<Block> STRIPPED_JUNGLE_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_jungle_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.STRIPPED_JUNGLE_WOOD,
                    BlockBehaviour.Properties.copy(STRIPPED_JUNGLE_WOOD_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_OAK_WOOD_SLAB = BLOCKS.register("stripped_oak_wood_slab",
            () -> new ModSlabBlock(Blocks.STRIPPED_OAK_WOOD,
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final RegistryObject<Block> STRIPPED_OAK_WOOD_STAIRS = BLOCKS.register("stripped_oak_wood_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_OAK_WOOD.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final RegistryObject<Block> STRIPPED_OAK_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_oak_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.STRIPPED_OAK_WOOD,
                    BlockBehaviour.Properties.copy(STRIPPED_OAK_WOOD_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_SPRUCE_WOOD_SLAB = BLOCKS.register(
            "stripped_spruce_wood_slab",
            () -> new ModSlabBlock(Blocks.STRIPPED_SPRUCE_WOOD,
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_SPRUCE_WOOD)));
    public static final RegistryObject<Block> STRIPPED_SPRUCE_WOOD_STAIRS = BLOCKS.register(
            "stripped_spruce_wood_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_SPRUCE_WOOD.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_SPRUCE_WOOD)));
    public static final RegistryObject<Block> STRIPPED_SPRUCE_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_spruce_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.STRIPPED_SPRUCE_WOOD,
                    BlockBehaviour.Properties.copy(STRIPPED_SPRUCE_WOOD_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_WARPED_HYPHAE_SLAB = BLOCKS.register(
            "stripped_warped_hyphae_slab",
            () -> new ModSlabBlock(Blocks.STRIPPED_WARPED_HYPHAE,
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_WARPED_HYPHAE)));
    public static final RegistryObject<Block> STRIPPED_WARPED_HYPHAE_STAIRS = BLOCKS.register(
            "stripped_warped_hyphae_stairs",
            () -> new ModStairBlock(Blocks.STRIPPED_WARPED_HYPHAE.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.STRIPPED_WARPED_HYPHAE)));
    public static final RegistryObject<Block> STRIPPED_WARPED_HYPHAE_VERTICAL_SLAB = BLOCKS.register(
            "stripped_warped_hyphae_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.STRIPPED_WARPED_HYPHAE,
                    BlockBehaviour.Properties.copy(STRIPPED_WARPED_HYPHAE_SLAB.get())));
    public static final RegistryObject<Block> TINTED_GLASS_SLAB = BLOCKS.register("tinted_glass_slab",
            () -> new ModSlabBlock(Blocks.TINTED_GLASS,
                    BlockBehaviour.Properties.copy(Blocks.TINTED_GLASS)));
    public static final RegistryObject<Block> TINTED_GLASS_STAIRS = BLOCKS.register("tinted_glass_stairs",
            () -> new ModStairBlock(Blocks.TINTED_GLASS.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.TINTED_GLASS)));
    public static final RegistryObject<Block> TINTED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "tinted_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.TINTED_GLASS,
                    BlockBehaviour.Properties.copy(TINTED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> WARPED_HYPHAE_SLAB = BLOCKS.register("warped_hyphae_slab",
            () -> new ModSlabBlock(Blocks.WARPED_HYPHAE,
                    BlockBehaviour.Properties.copy(Blocks.WARPED_HYPHAE)));
    public static final RegistryObject<Block> WARPED_HYPHAE_STAIRS = BLOCKS.register("warped_hyphae_stairs",
            () -> new ModStairBlock(Blocks.WARPED_HYPHAE.defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.WARPED_HYPHAE)));
    public static final RegistryObject<Block> WARPED_HYPHAE_VERTICAL_SLAB = BLOCKS.register(
            "warped_hyphae_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.WARPED_HYPHAE,
                    BlockBehaviour.Properties.copy(WARPED_HYPHAE_SLAB.get())));
    public static final RegistryObject<Block> MUFF_BLOCK = BLOCKS.register("muff_block",
            () -> new MuffBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));

    public static final RegistryObject<Block> WHITE_WALLPAPER = BLOCKS.register("white_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.SNOW).strength(0.8F)
                    .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> ORANGE_WALLPAPER = BLOCKS.register("orange_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_ORANGE)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> MAGENTA_WALLPAPER = BLOCKS.register("magenta_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_MAGENTA)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> LIGHT_BLUE_WALLPAPER = BLOCKS.register("light_blue_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_BLUE)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> YELLOW_WALLPAPER = BLOCKS.register("yellow_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_YELLOW)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> LIME_WALLPAPER = BLOCKS.register("lime_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_GREEN)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> PINK_WALLPAPER = BLOCKS.register("pink_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_PINK)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> GRAY_WALLPAPER = BLOCKS.register("gray_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GRAY)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> LIGHT_GRAY_WALLPAPER = BLOCKS.register("light_gray_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_GRAY)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CYAN_WALLPAPER = BLOCKS.register("cyan_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_CYAN)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> PURPLE_WALLPAPER = BLOCKS.register("purple_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_PURPLE)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BLUE_WALLPAPER = BLOCKS.register("blue_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BLUE)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BROWN_WALLPAPER = BLOCKS.register("brown_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BROWN)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> GREEN_WALLPAPER = BLOCKS.register("green_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GREEN)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> RED_WALLPAPER = BLOCKS.register("red_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_RED)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BLACK_WALLPAPER = BLOCKS.register("black_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BLACK)
                    .strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> WHITE_WALLPAPER_SLAB = BLOCKS.register(
            "white_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.SNOW)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> WHITE_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "white_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(WHITE_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_WALLPAPER_SLAB = BLOCKS.register(
            "orange_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_ORANGE)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> ORANGE_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "orange_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(ORANGE_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_WALLPAPER_SLAB = BLOCKS.register(
            "magenta_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_MAGENTA)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> MAGENTA_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "magenta_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MAGENTA_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_WALLPAPER_SLAB = BLOCKS.register(
            "light_blue_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> LIGHT_BLUE_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_WALLPAPER_SLAB = BLOCKS.register(
            "yellow_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_YELLOW)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> YELLOW_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "yellow_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(YELLOW_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> LIME_WALLPAPER_SLAB = BLOCKS.register(
            "lime_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> LIME_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "lime_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(LIME_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> PINK_WALLPAPER_SLAB = BLOCKS.register(
            "pink_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_PINK)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> PINK_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "pink_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(PINK_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> GRAY_WALLPAPER_SLAB = BLOCKS.register(
            "gray_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GRAY)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> GRAY_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "gray_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(GRAY_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_WALLPAPER_SLAB = BLOCKS.register(
            "light_gray_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> LIGHT_GRAY_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> CYAN_WALLPAPER_SLAB = BLOCKS.register(
            "cyan_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_CYAN)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CYAN_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "cyan_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(CYAN_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_WALLPAPER_SLAB = BLOCKS.register(
            "purple_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_PURPLE)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> PURPLE_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "purple_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(PURPLE_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> BLUE_WALLPAPER_SLAB = BLOCKS.register(
            "blue_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BLUE)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BLUE_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "blue_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(BLUE_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> BROWN_WALLPAPER_SLAB = BLOCKS.register(
            "brown_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BROWN)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BROWN_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "brown_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(BROWN_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> GREEN_WALLPAPER_SLAB = BLOCKS.register(
            "green_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GREEN)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> GREEN_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "green_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(GREEN_WALLPAPER_SLAB.get())));
    public static final RegistryObject<Block> RED_WALLPAPER_SLAB = BLOCKS.register(
            "red_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_RED)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> RED_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "red_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(RED_WALLPAPER_SLAB.get())));

    public static final RegistryObject<Block> BLACK_WALLPAPER_SLAB = BLOCKS.register(
            "black_wallpaper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BLACK)
                            .strength(0.8F)
                            .sound(SoundType.WOOL)));
    public static final RegistryObject<Block> BLACK_WALLPAPER_VERTICAL_SLAB = BLOCKS.register(
            "black_wallpaper_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(BLACK_WALLPAPER_SLAB.get())));

    public static final RegistryObject<Block> WHITE_WALLPAPER_FLAT = BLOCKS.register("white_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.SNOW)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> ORANGE_WALLPAPER_FLAT = BLOCKS.register("orange_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_ORANGE)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> MAGENTA_WALLPAPER_FLAT = BLOCKS.register("magenta_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_MAGENTA)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> LIGHT_BLUE_WALLPAPER_FLAT = BLOCKS.register("light_blue_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_BLUE)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> YELLOW_WALLPAPER_FLAT = BLOCKS.register("yellow_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_YELLOW)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> LIME_WALLPAPER_FLAT = BLOCKS.register("lime_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_GREEN)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> PINK_WALLPAPER_FLAT = BLOCKS.register("pink_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_PINK)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> GRAY_WALLPAPER_FLAT = BLOCKS.register("gray_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GRAY)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> LIGHT_GRAY_WALLPAPER_FLAT = BLOCKS.register("light_gray_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_GRAY)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> CYAN_WALLPAPER_FLAT = BLOCKS.register("cyan_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_CYAN)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> PURPLE_WALLPAPER_FLAT = BLOCKS.register("purple_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_PURPLE)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> BLUE_WALLPAPER_FLAT = BLOCKS.register("blue_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BLUE)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> BROWN_WALLPAPER_FLAT = BLOCKS.register("brown_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BROWN)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> GREEN_WALLPAPER_FLAT = BLOCKS.register("green_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GREEN)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> RED_WALLPAPER_FLAT = BLOCKS.register("red_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_RED)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> BLACK_WALLPAPER_FLAT = BLOCKS.register("black_wallpaper_flat",
            () -> new WallpaperFlatBlock(BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BLACK)
                    .strength(0.8F).sound(SoundType.WOOL).noOcclusion()));

    public static final RegistryObject<Block> WHITE_CUSHION = BLOCKS.register("white_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> ORANGE_CUSHION = BLOCKS.register("orange_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.ORANGE_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> MAGENTA_CUSHION = BLOCKS.register("magenta_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.MAGENTA_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> LIGHT_BLUE_CUSHION = BLOCKS.register("light_blue_cushion",
            () -> new CushionBlock(
                    BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> YELLOW_CUSHION = BLOCKS.register("yellow_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.YELLOW_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> LIME_CUSHION = BLOCKS.register("lime_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.LIME_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> PINK_CUSHION = BLOCKS.register("pink_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.PINK_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> GRAY_CUSHION = BLOCKS.register("gray_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.GRAY_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> LIGHT_GRAY_CUSHION = BLOCKS.register("light_gray_cushion",
            () -> new CushionBlock(
                    BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> CYAN_CUSHION = BLOCKS.register("cyan_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> PURPLE_CUSHION = BLOCKS.register("purple_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.PURPLE_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> BLUE_CUSHION = BLOCKS.register("blue_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.BLUE_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> BROWN_CUSHION = BLOCKS.register("brown_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> GREEN_CUSHION = BLOCKS.register("green_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.GREEN_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> RED_CUSHION = BLOCKS.register("red_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.RED_WOOL).strength(0.4F).noOcclusion()));
    public static final RegistryObject<Block> BLACK_CUSHION = BLOCKS.register("black_cushion",
            () -> new CushionBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL).strength(0.4F).noOcclusion()));

    public static final RegistryObject<Block> STRAW_BED = BLOCKS.register("straw_bed",
            () -> new StrawBedBlock(BlockBehaviour.Properties.copy(Blocks.HAY_BLOCK).noOcclusion()));

    public static final RegistryObject<Block> BIG_BOOK = BLOCKS.register("big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> GLOWING_BIG_BOOK = BLOCKS.register("glowing_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF).lightLevel(s -> 15)));
    public static final RegistryObject<Block> WHITE_BIG_BOOK = BLOCKS.register("white_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> ORANGE_BIG_BOOK = BLOCKS.register("orange_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> MAGENTA_BIG_BOOK = BLOCKS.register("magenta_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> LIGHT_BLUE_BIG_BOOK = BLOCKS.register("light_blue_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> YELLOW_BIG_BOOK = BLOCKS.register("yellow_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> LIME_BIG_BOOK = BLOCKS.register("lime_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> PINK_BIG_BOOK = BLOCKS.register("pink_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> GRAY_BIG_BOOK = BLOCKS.register("gray_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> LIGHT_GRAY_BIG_BOOK = BLOCKS.register("light_gray_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> CYAN_BIG_BOOK = BLOCKS.register("cyan_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> PURPLE_BIG_BOOK = BLOCKS.register("purple_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> BLUE_BIG_BOOK = BLOCKS.register("blue_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> BROWN_BIG_BOOK = BLOCKS.register("brown_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> GREEN_BIG_BOOK = BLOCKS.register("green_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> RED_BIG_BOOK = BLOCKS.register("red_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> BLACK_BIG_BOOK = BLOCKS.register("black_big_book",
            () -> new BigBookBlock(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));

    public static final RegistryObject<Block> PALE_OAK_LOG = BLOCKS.register("pale_oak_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
    public static final RegistryObject<Block> STRIPPED_PALE_OAK_LOG = BLOCKS.register("stripped_pale_oak_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final RegistryObject<Block> PALE_OAK_WOOD = BLOCKS.register("pale_oak_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));
    public static final RegistryObject<Block> STRIPPED_PALE_OAK_WOOD = BLOCKS.register("stripped_pale_oak_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final RegistryObject<Block> PALE_OAK_PLANKS = BLOCKS.register("pale_oak_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> PALE_OAK_STAIRS = BLOCKS.register("pale_oak_stairs",
            () -> new StairBlock(() -> PALE_OAK_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));
    public static final RegistryObject<Block> PALE_OAK_SLAB = BLOCKS.register("pale_oak_slab",
            () -> new ModSlabBlock(PALE_OAK_PLANKS.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> PALE_OAK_VERTICAL_SLAB = BLOCKS.register("pale_oak_vertical_slab",
            () -> new VerticalSlabBlock(PALE_OAK_PLANKS.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> PALE_OAK_FENCE = BLOCKS.register("pale_oak_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE)));
    public static final RegistryObject<Block> PALE_OAK_FENCE_GATE = BLOCKS.register("pale_oak_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE)));
    public static final RegistryObject<Block> PALE_OAK_DOOR = BLOCKS.register("pale_oak_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR)));
    public static final RegistryObject<Block> PALE_OAK_TRAPDOOR = BLOCKS.register("pale_oak_trapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR)));
    public static final RegistryObject<Block> PALE_OAK_BUTTON = BLOCKS.register("pale_oak_button",
            () -> new WoodButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON)));
    public static final RegistryObject<Block> PALE_OAK_PRESSURE_PLATE = BLOCKS.register("pale_oak_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE)));
    public static final RegistryObject<Block> PALE_OAK_LEAVES = BLOCKS.register("pale_oak_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
    public static final RegistryObject<Block> PALE_OAK_SAPLING = BLOCKS.register("pale_oak_sapling",
            () -> new SaplingBlock(new PaleOakTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));

    public static final RegistryObject<Block> PALE_OAK_LOG_SLAB = BLOCKS.register("pale_oak_log_slab",
            () -> new LogSlabBlock(PALE_OAK_LOG.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> PALE_OAK_LOG_VERTICAL_SLAB = BLOCKS.register("pale_oak_log_vertical_slab",
            () -> new VerticalSlabBlock(PALE_OAK_LOG.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> STRIPPED_PALE_OAK_LOG_SLAB = BLOCKS.register("stripped_pale_oak_log_slab",
            () -> new LogSlabBlock(STRIPPED_PALE_OAK_LOG.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> STRIPPED_PALE_OAK_LOG_VERTICAL_SLAB = BLOCKS.register(
            "stripped_pale_oak_log_vertical_slab",
            () -> new VerticalSlabBlock(STRIPPED_PALE_OAK_LOG.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> PALE_OAK_WOOD_SLAB = BLOCKS.register("pale_oak_wood_slab",
            () -> new LogSlabBlock(PALE_OAK_WOOD.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> PALE_OAK_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "pale_oak_wood_vertical_slab",
            () -> new VerticalSlabBlock(PALE_OAK_WOOD.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> STRIPPED_PALE_OAK_WOOD_SLAB = BLOCKS.register(
            "stripped_pale_oak_wood_slab",
            () -> new LogSlabBlock(STRIPPED_PALE_OAK_WOOD.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> STRIPPED_PALE_OAK_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_pale_oak_wood_vertical_slab",
            () -> new VerticalSlabBlock(STRIPPED_PALE_OAK_WOOD.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));

    public static final RegistryObject<Block> CHERRY_LOG = BLOCKS.register("cherry_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
    public static final RegistryObject<Block> STRIPPED_CHERRY_LOG = BLOCKS.register("stripped_cherry_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final RegistryObject<Block> CHERRY_WOOD = BLOCKS.register("cherry_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));
    public static final RegistryObject<Block> STRIPPED_CHERRY_WOOD = BLOCKS.register("stripped_cherry_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final RegistryObject<Block> CHERRY_PLANKS = BLOCKS.register("cherry_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> CHERRY_STAIRS = BLOCKS.register("cherry_stairs",
            () -> new ModStairBlock(CHERRY_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));
    public static final RegistryObject<Block> CHERRY_SLAB = BLOCKS.register("cherry_slab",
            () -> new ModSlabBlock(CHERRY_PLANKS.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> CHERRY_VERTICAL_SLAB = BLOCKS.register("cherry_vertical_slab",
            () -> new VerticalSlabBlock(CHERRY_PLANKS.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> CHERRY_FENCE = BLOCKS.register("cherry_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE)));
    public static final RegistryObject<Block> CHERRY_FENCE_GATE = BLOCKS.register("cherry_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE)));
    public static final RegistryObject<Block> CHERRY_DOOR = BLOCKS.register("cherry_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR)));
    public static final RegistryObject<Block> CHERRY_TRAPDOOR = BLOCKS.register("cherry_trapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR)));
    public static final RegistryObject<Block> CHERRY_BUTTON = BLOCKS.register("cherry_button",
            () -> new WoodButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON)));
    public static final RegistryObject<Block> CHERRY_PRESSURE_PLATE = BLOCKS.register("cherry_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE)));
    public static final RegistryObject<Block> CHERRY_LEAVES = BLOCKS.register("cherry_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
    public static final RegistryObject<Block> CHERRY_SAPLING = BLOCKS.register("cherry_sapling",
            () -> new SaplingBlock(new CherryTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));

    public static final RegistryObject<Block> CHERRY_LOG_SLAB = BLOCKS.register("cherry_log_slab",
            () -> new LogSlabBlock(CHERRY_LOG.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> CHERRY_LOG_VERTICAL_SLAB = BLOCKS.register("cherry_log_vertical_slab",
            () -> new VerticalSlabBlock(CHERRY_LOG.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> STRIPPED_CHERRY_LOG_SLAB = BLOCKS.register("stripped_cherry_log_slab",
            () -> new LogSlabBlock(STRIPPED_CHERRY_LOG.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> STRIPPED_CHERRY_LOG_VERTICAL_SLAB = BLOCKS.register(
            "stripped_cherry_log_vertical_slab",
            () -> new VerticalSlabBlock(STRIPPED_CHERRY_LOG.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> CHERRY_WOOD_SLAB = BLOCKS.register("cherry_wood_slab",
            () -> new LogSlabBlock(CHERRY_WOOD.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> CHERRY_WOOD_VERTICAL_SLAB = BLOCKS.register("cherry_wood_vertical_slab",
            () -> new VerticalSlabBlock(CHERRY_WOOD.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> STRIPPED_CHERRY_WOOD_SLAB = BLOCKS.register("stripped_cherry_wood_slab",
            () -> new LogSlabBlock(STRIPPED_CHERRY_WOOD.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));
    public static final RegistryObject<Block> STRIPPED_CHERRY_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_cherry_wood_vertical_slab",
            () -> new VerticalSlabBlock(STRIPPED_CHERRY_WOOD.get(), BlockBehaviour.Properties.copy(Blocks.OAK_SLAB)));

    public static final RegistryObject<Block> PALE_MOSS_CARPET = BLOCKS.register("pale_moss_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET)));
}
