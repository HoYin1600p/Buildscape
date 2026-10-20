package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.block.building.ModButtonBlock;
import com.kingodogo.buildscape.block.building.ModPressurePlateBlock;
import com.kingodogo.buildscape.block.building.ModStairBlock;
import com.kingodogo.buildscape.block.building.SteelBoltBlock;
import com.kingodogo.buildscape.block.building.VerticalSlabBlock;
import com.kingodogo.buildscape.block.building.WaterloggableGrateBlock;
import com.kingodogo.buildscape.feature.copper.ModCopperDoorBlock;
import com.kingodogo.buildscape.feature.copper.ModCopperTrapdoorBlock;
import com.kingodogo.buildscape.feature.pipe.HollowLogBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.blocks.BlocksRegister.BLOCKS;

/**
 * The blocks FLAMING_STEEL_BLOCK_SLAB through HOLLOW_OAK_LOG of the registry, part 11 of 12, in registration order.
 * Named for its dominant families (steel, wood); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link WoolBlocks#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class SteelBlocks {

    private SteelBlocks() {}

    /** Forces this class to load, which registers its blocks. */
    public static void init() {}

    static {
        WoolBlocks.init();
    }

    public static final RegistryObject<Block> FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register("flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(WoolBlocks.FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(WoolBlocks.FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register("flaming_steel_block_stairs",
            () -> new ModStairBlock(WoolBlocks.FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(WoolBlocks.FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> FLAMING_STEEL_BLOCK_WALL = BLOCKS.register("flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(WoolBlocks.FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> FLAMING_CUT_STEEL = BLOCKS.register("flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> FLAMING_CUT_STEEL_SLAB = BLOCKS.register("flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> FLAMING_CUT_STEEL_STAIRS = BLOCKS.register("flaming_cut_steel_stairs",
            () -> new ModStairBlock(FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> FLAMING_CUT_STEEL_WALL = BLOCKS.register("flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> FLAMING_POLISHED_STEEL = BLOCKS.register("flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "flaming_polished_steel_stairs",
            () -> new ModStairBlock(FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> FLAMING_PRESSED_STEEL = BLOCKS.register("flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register("flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "flaming_pressed_steel_stairs",
            () -> new ModStairBlock(FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> FLAMING_PRESSED_STEEL_WALL = BLOCKS.register("flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> FLAMING_STEEL_CASING = BLOCKS.register("flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> FLAMING_STEEL_GRATE = BLOCKS.register("flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> FLAMING_STEEL_PILLAR = BLOCKS.register("flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> FLAMING_STEEL_BOLTS = BLOCKS.register("flaming_steel_bolts",
            () -> new SteelBoltBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> FLAMING_STEEL_DOOR = BLOCKS.register("flaming_steel_door",
            () -> new ModCopperDoorBlock(
                    BlockBehaviour.Properties.copy(WoolBlocks.FLAMING_STEEL_BLOCK.get()).noOcclusion()));
    public static final RegistryObject<Block> FLAMING_STEEL_TRAPDOOR = BLOCKS.register("flaming_steel_trapdoor",
            () -> new ModCopperTrapdoorBlock(
                    BlockBehaviour.Properties.copy(WoolBlocks.FLAMING_STEEL_BLOCK.get()).noOcclusion()));
    public static final RegistryObject<Block> FLAMING_STEEL_BUTTON = BLOCKS.register("flaming_steel_button",
            () -> new ModButtonBlock(
                    BlockBehaviour.Properties.copy(WoolBlocks.FLAMING_STEEL_BLOCK.get()).noCollission()
                            .strength(0.5f)));
    public static final RegistryObject<Block> FLAMING_STEEL_PRESSURE_PLATE = BLOCKS.register(
            "flaming_steel_pressure_plate",
            () -> new ModPressurePlateBlock(150,
                    BlockBehaviour.Properties.copy(WoolBlocks.FLAMING_STEEL_BLOCK.get()).noCollission()
                            .strength(0.5f)));
    public static final RegistryObject<Block> MAGENTA_FLAMING_STEEL_BLOCK = BLOCKS.register(
            "magenta_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> MAGENTA_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "magenta_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(MAGENTA_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "magenta_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(MAGENTA_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(MAGENTA_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "magenta_flaming_steel_block_stairs",
            () -> new ModStairBlock(MAGENTA_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MAGENTA_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "magenta_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(MAGENTA_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_CUT_STEEL = BLOCKS.register("magenta_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> MAGENTA_FLAMING_CUT_STEEL_SLAB = BLOCKS.register(
            "magenta_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(MAGENTA_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "magenta_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(MAGENTA_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(MAGENTA_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "magenta_flaming_cut_steel_stairs",
            () -> new ModStairBlock(MAGENTA_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MAGENTA_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_CUT_STEEL_WALL = BLOCKS.register(
            "magenta_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(MAGENTA_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_POLISHED_STEEL = BLOCKS.register(
            "magenta_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> MAGENTA_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "magenta_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(MAGENTA_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "magenta_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(MAGENTA_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(MAGENTA_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "magenta_flaming_polished_steel_stairs",
            () -> new ModStairBlock(MAGENTA_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MAGENTA_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "magenta_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(MAGENTA_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_PRESSED_STEEL = BLOCKS.register(
            "magenta_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> MAGENTA_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "magenta_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(MAGENTA_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "magenta_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(MAGENTA_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(MAGENTA_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "magenta_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(MAGENTA_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MAGENTA_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "magenta_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(MAGENTA_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> MAGENTA_FLAMING_STEEL_CASING = BLOCKS.register(
            "magenta_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> MAGENTA_FLAMING_STEEL_GRATE = BLOCKS.register(
            "magenta_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> MAGENTA_FLAMING_STEEL_PILLAR = BLOCKS.register(
            "magenta_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> MAGENTA_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "magenta_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_STEEL_BLOCK = BLOCKS.register(
            "light_blue_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "light_blue_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(LIGHT_BLUE_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "light_blue_flaming_steel_block_stairs",
            () -> new ModStairBlock(LIGHT_BLUE_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "light_blue_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_CUT_STEEL = BLOCKS.register(
            "light_blue_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_CUT_STEEL_SLAB = BLOCKS.register(
            "light_blue_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(LIGHT_BLUE_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "light_blue_flaming_cut_steel_stairs",
            () -> new ModStairBlock(LIGHT_BLUE_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_CUT_STEEL_WALL = BLOCKS.register(
            "light_blue_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_POLISHED_STEEL = BLOCKS.register(
            "light_blue_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "light_blue_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(LIGHT_BLUE_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "light_blue_flaming_polished_steel_stairs",
            () -> new ModStairBlock(LIGHT_BLUE_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "light_blue_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_PRESSED_STEEL = BLOCKS.register(
            "light_blue_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "light_blue_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(LIGHT_BLUE_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "light_blue_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(LIGHT_BLUE_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "light_blue_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(LIGHT_BLUE_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_STEEL_CASING = BLOCKS.register(
            "light_blue_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_STEEL_GRATE = BLOCKS.register(
            "light_blue_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_STEEL_PILLAR = BLOCKS.register(
            "light_blue_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_BLUE_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "light_blue_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> YELLOW_FLAMING_STEEL_BLOCK = BLOCKS.register("yellow_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> YELLOW_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "yellow_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(YELLOW_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "yellow_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(YELLOW_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(YELLOW_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "yellow_flaming_steel_block_stairs",
            () -> new ModStairBlock(YELLOW_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(YELLOW_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "yellow_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(YELLOW_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_CUT_STEEL = BLOCKS.register("yellow_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> YELLOW_FLAMING_CUT_STEEL_SLAB = BLOCKS.register(
            "yellow_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(YELLOW_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "yellow_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(YELLOW_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(YELLOW_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "yellow_flaming_cut_steel_stairs",
            () -> new ModStairBlock(YELLOW_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(YELLOW_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_CUT_STEEL_WALL = BLOCKS.register(
            "yellow_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(YELLOW_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_POLISHED_STEEL = BLOCKS.register(
            "yellow_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> YELLOW_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "yellow_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(YELLOW_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "yellow_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(YELLOW_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(YELLOW_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "yellow_flaming_polished_steel_stairs",
            () -> new ModStairBlock(YELLOW_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(YELLOW_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "yellow_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(YELLOW_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_PRESSED_STEEL = BLOCKS.register(
            "yellow_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> YELLOW_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "yellow_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(YELLOW_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "yellow_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(YELLOW_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(YELLOW_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "yellow_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(YELLOW_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(YELLOW_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "yellow_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(YELLOW_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> YELLOW_FLAMING_STEEL_CASING = BLOCKS.register(
            "yellow_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> YELLOW_FLAMING_STEEL_GRATE = BLOCKS.register("yellow_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> YELLOW_FLAMING_STEEL_PILLAR = BLOCKS.register(
            "yellow_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> YELLOW_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "yellow_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIME_FLAMING_STEEL_BLOCK = BLOCKS.register("lime_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIME_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "lime_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(LIME_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> LIME_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "lime_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(LIME_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(LIME_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> LIME_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "lime_flaming_steel_block_stairs",
            () -> new ModStairBlock(LIME_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIME_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> LIME_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "lime_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(LIME_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> LIME_FLAMING_CUT_STEEL = BLOCKS.register("lime_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIME_FLAMING_CUT_STEEL_SLAB = BLOCKS.register(
            "lime_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(LIME_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> LIME_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "lime_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(LIME_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(LIME_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> LIME_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "lime_flaming_cut_steel_stairs",
            () -> new ModStairBlock(LIME_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIME_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> LIME_FLAMING_CUT_STEEL_WALL = BLOCKS.register(
            "lime_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(LIME_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> LIME_FLAMING_POLISHED_STEEL = BLOCKS.register(
            "lime_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIME_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "lime_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(LIME_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> LIME_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "lime_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(LIME_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(LIME_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> LIME_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "lime_flaming_polished_steel_stairs",
            () -> new ModStairBlock(LIME_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIME_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> LIME_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "lime_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(LIME_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> LIME_FLAMING_PRESSED_STEEL = BLOCKS.register("lime_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIME_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "lime_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(LIME_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> LIME_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "lime_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(LIME_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(LIME_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> LIME_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "lime_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(LIME_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIME_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> LIME_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "lime_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(LIME_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> LIME_FLAMING_STEEL_CASING = BLOCKS.register("lime_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIME_FLAMING_STEEL_GRATE = BLOCKS.register("lime_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> LIME_FLAMING_STEEL_PILLAR = BLOCKS.register("lime_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIME_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "lime_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PINK_FLAMING_STEEL_BLOCK = BLOCKS.register("pink_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PINK_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "pink_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(PINK_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> PINK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "pink_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(PINK_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(PINK_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> PINK_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "pink_flaming_steel_block_stairs",
            () -> new ModStairBlock(PINK_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(PINK_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> PINK_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "pink_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(PINK_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> PINK_FLAMING_CUT_STEEL = BLOCKS.register("pink_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PINK_FLAMING_CUT_STEEL_SLAB = BLOCKS.register(
            "pink_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(PINK_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> PINK_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "pink_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(PINK_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(PINK_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> PINK_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "pink_flaming_cut_steel_stairs",
            () -> new ModStairBlock(PINK_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(PINK_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> PINK_FLAMING_CUT_STEEL_WALL = BLOCKS.register(
            "pink_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(PINK_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> PINK_FLAMING_POLISHED_STEEL = BLOCKS.register(
            "pink_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PINK_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "pink_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(PINK_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> PINK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "pink_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(PINK_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(PINK_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> PINK_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "pink_flaming_polished_steel_stairs",
            () -> new ModStairBlock(PINK_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(PINK_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> PINK_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "pink_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(PINK_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> PINK_FLAMING_PRESSED_STEEL = BLOCKS.register("pink_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PINK_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "pink_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(PINK_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> PINK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "pink_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(PINK_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(PINK_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> PINK_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "pink_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(PINK_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(PINK_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> PINK_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "pink_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(PINK_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> PINK_FLAMING_STEEL_CASING = BLOCKS.register("pink_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PINK_FLAMING_STEEL_GRATE = BLOCKS.register("pink_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> PINK_FLAMING_STEEL_PILLAR = BLOCKS.register("pink_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PINK_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "pink_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GRAY_FLAMING_STEEL_BLOCK = BLOCKS.register("gray_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GRAY_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "gray_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(GRAY_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "gray_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(GRAY_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(GRAY_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "gray_flaming_steel_block_stairs",
            () -> new ModStairBlock(GRAY_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(GRAY_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "gray_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(GRAY_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_CUT_STEEL = BLOCKS.register("gray_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GRAY_FLAMING_CUT_STEEL_SLAB = BLOCKS.register(
            "gray_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(GRAY_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "gray_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(GRAY_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(GRAY_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "gray_flaming_cut_steel_stairs",
            () -> new ModStairBlock(GRAY_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(GRAY_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_CUT_STEEL_WALL = BLOCKS.register(
            "gray_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(GRAY_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_POLISHED_STEEL = BLOCKS.register(
            "gray_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GRAY_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "gray_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(GRAY_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "gray_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(GRAY_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(GRAY_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "gray_flaming_polished_steel_stairs",
            () -> new ModStairBlock(GRAY_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(GRAY_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "gray_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(GRAY_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_PRESSED_STEEL = BLOCKS.register("gray_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GRAY_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "gray_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(GRAY_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "gray_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(GRAY_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(GRAY_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "gray_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(GRAY_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(GRAY_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "gray_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(GRAY_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> GRAY_FLAMING_STEEL_CASING = BLOCKS.register("gray_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GRAY_FLAMING_STEEL_GRATE = BLOCKS.register("gray_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> GRAY_FLAMING_STEEL_PILLAR = BLOCKS.register("gray_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GRAY_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "gray_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_STEEL_BLOCK = BLOCKS.register(
            "light_gray_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "light_gray_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(LIGHT_GRAY_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "light_gray_flaming_steel_block_stairs",
            () -> new ModStairBlock(LIGHT_GRAY_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "light_gray_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_STEEL_BLOCK.get())));

    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_CUT_STEEL = BLOCKS.register(
            "light_gray_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_CUT_STEEL_SLAB = BLOCKS.register(
            "light_gray_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(LIGHT_GRAY_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "light_gray_flaming_cut_steel_stairs",
            () -> new ModStairBlock(LIGHT_GRAY_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_CUT_STEEL_WALL = BLOCKS.register(
            "light_gray_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_POLISHED_STEEL = BLOCKS.register(
            "light_gray_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "light_gray_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(LIGHT_GRAY_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "light_gray_flaming_polished_steel_stairs",
            () -> new ModStairBlock(LIGHT_GRAY_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "light_gray_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_PRESSED_STEEL = BLOCKS.register(
            "light_gray_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "light_gray_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(LIGHT_GRAY_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "light_gray_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(LIGHT_GRAY_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "light_gray_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(LIGHT_GRAY_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_STEEL_CASING = BLOCKS.register(
            "light_gray_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_STEEL_GRATE = BLOCKS.register(
            "light_gray_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_STEEL_PILLAR = BLOCKS.register(
            "light_gray_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "light_gray_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> CYAN_FLAMING_STEEL_BLOCK = BLOCKS.register("cyan_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> CYAN_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "cyan_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(CYAN_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "cyan_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(CYAN_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(CYAN_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "cyan_flaming_steel_block_stairs",
            () -> new ModStairBlock(CYAN_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(CYAN_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "cyan_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(CYAN_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_CUT_STEEL = BLOCKS.register("cyan_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> CYAN_FLAMING_CUT_STEEL_SLAB = BLOCKS.register(
            "cyan_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(CYAN_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "cyan_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(CYAN_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(CYAN_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "cyan_flaming_cut_steel_stairs",
            () -> new ModStairBlock(CYAN_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(CYAN_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_CUT_STEEL_WALL = BLOCKS.register(
            "cyan_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(CYAN_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_POLISHED_STEEL = BLOCKS.register(
            "cyan_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> CYAN_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "cyan_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(CYAN_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "cyan_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(CYAN_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(CYAN_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "cyan_flaming_polished_steel_stairs",
            () -> new ModStairBlock(CYAN_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(CYAN_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "cyan_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(CYAN_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_PRESSED_STEEL = BLOCKS.register("cyan_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> CYAN_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "cyan_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(CYAN_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "cyan_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(CYAN_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(CYAN_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "cyan_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(CYAN_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(CYAN_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "cyan_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(CYAN_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> CYAN_FLAMING_STEEL_CASING = BLOCKS.register("cyan_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> CYAN_FLAMING_STEEL_GRATE = BLOCKS.register("cyan_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> CYAN_FLAMING_STEEL_PILLAR = BLOCKS.register("cyan_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> CYAN_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "cyan_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PURPLE_FLAMING_STEEL_BLOCK = BLOCKS.register("purple_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PURPLE_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "purple_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(PURPLE_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "purple_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(PURPLE_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(PURPLE_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "purple_flaming_steel_block_stairs",
            () -> new ModStairBlock(PURPLE_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(PURPLE_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "purple_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(PURPLE_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_CUT_STEEL = BLOCKS.register("purple_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PURPLE_FLAMING_CUT_STEEL_SLAB = BLOCKS.register(
            "purple_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(PURPLE_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "purple_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(PURPLE_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(PURPLE_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "purple_flaming_cut_steel_stairs",
            () -> new ModStairBlock(PURPLE_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(PURPLE_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_CUT_STEEL_WALL = BLOCKS.register(
            "purple_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(PURPLE_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_POLISHED_STEEL = BLOCKS.register(
            "purple_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PURPLE_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "purple_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(PURPLE_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "purple_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(PURPLE_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(PURPLE_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "purple_flaming_polished_steel_stairs",
            () -> new ModStairBlock(PURPLE_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(PURPLE_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "purple_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(PURPLE_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_PRESSED_STEEL = BLOCKS.register(
            "purple_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PURPLE_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "purple_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(PURPLE_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "purple_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(PURPLE_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(PURPLE_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "purple_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(PURPLE_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(PURPLE_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "purple_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(PURPLE_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> PURPLE_FLAMING_STEEL_CASING = BLOCKS.register(
            "purple_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PURPLE_FLAMING_STEEL_GRATE = BLOCKS.register("purple_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> PURPLE_FLAMING_STEEL_PILLAR = BLOCKS.register(
            "purple_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> PURPLE_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "purple_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLUE_FLAMING_STEEL_BLOCK = BLOCKS.register("blue_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLUE_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "blue_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(BLUE_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "blue_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(BLUE_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(BLUE_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "blue_flaming_steel_block_stairs",
            () -> new ModStairBlock(BLUE_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BLUE_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "blue_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(BLUE_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_CUT_STEEL = BLOCKS.register("blue_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLUE_FLAMING_CUT_STEEL_SLAB = BLOCKS.register(
            "blue_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(BLUE_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "blue_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(BLUE_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(BLUE_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "blue_flaming_cut_steel_stairs",
            () -> new ModStairBlock(BLUE_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BLUE_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_CUT_STEEL_WALL = BLOCKS.register(
            "blue_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(BLUE_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_POLISHED_STEEL = BLOCKS.register(
            "blue_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLUE_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "blue_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(BLUE_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "blue_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(BLUE_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(BLUE_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "blue_flaming_polished_steel_stairs",
            () -> new ModStairBlock(BLUE_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BLUE_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "blue_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(BLUE_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_PRESSED_STEEL = BLOCKS.register("blue_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLUE_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "blue_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(BLUE_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "blue_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(BLUE_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(BLUE_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "blue_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(BLUE_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BLUE_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "blue_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(BLUE_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> BLUE_FLAMING_STEEL_CASING = BLOCKS.register("blue_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLUE_FLAMING_STEEL_GRATE = BLOCKS.register("blue_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> BLUE_FLAMING_STEEL_PILLAR = BLOCKS.register("blue_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLUE_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "blue_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BROWN_FLAMING_STEEL_BLOCK = BLOCKS.register("brown_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BROWN_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "brown_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(BROWN_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "brown_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(BROWN_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(BROWN_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "brown_flaming_steel_block_stairs",
            () -> new ModStairBlock(BROWN_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BROWN_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "brown_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(BROWN_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_CUT_STEEL = BLOCKS.register("brown_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BROWN_FLAMING_CUT_STEEL_SLAB = BLOCKS.register(
            "brown_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(BROWN_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "brown_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(BROWN_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(BROWN_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "brown_flaming_cut_steel_stairs",
            () -> new ModStairBlock(BROWN_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BROWN_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_CUT_STEEL_WALL = BLOCKS.register(
            "brown_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(BROWN_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_POLISHED_STEEL = BLOCKS.register(
            "brown_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BROWN_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "brown_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(BROWN_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "brown_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(BROWN_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(BROWN_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "brown_flaming_polished_steel_stairs",
            () -> new ModStairBlock(BROWN_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BROWN_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "brown_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(BROWN_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_PRESSED_STEEL = BLOCKS.register(
            "brown_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BROWN_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "brown_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(BROWN_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "brown_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(BROWN_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(BROWN_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "brown_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(BROWN_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BROWN_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "brown_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(BROWN_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> BROWN_FLAMING_STEEL_CASING = BLOCKS.register("brown_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BROWN_FLAMING_STEEL_GRATE = BLOCKS.register("brown_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> BROWN_FLAMING_STEEL_PILLAR = BLOCKS.register("brown_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BROWN_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "brown_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GREEN_FLAMING_STEEL_BLOCK = BLOCKS.register("green_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GREEN_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "green_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(GREEN_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "green_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(GREEN_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(GREEN_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "green_flaming_steel_block_stairs",
            () -> new ModStairBlock(GREEN_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(GREEN_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "green_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(GREEN_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_CUT_STEEL = BLOCKS.register("green_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GREEN_FLAMING_CUT_STEEL_SLAB = BLOCKS.register(
            "green_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(GREEN_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "green_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(GREEN_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(GREEN_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "green_flaming_cut_steel_stairs",
            () -> new ModStairBlock(GREEN_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(GREEN_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_CUT_STEEL_WALL = BLOCKS.register(
            "green_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(GREEN_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_POLISHED_STEEL = BLOCKS.register(
            "green_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GREEN_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "green_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(GREEN_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "green_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(GREEN_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(GREEN_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "green_flaming_polished_steel_stairs",
            () -> new ModStairBlock(GREEN_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(GREEN_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "green_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(GREEN_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_PRESSED_STEEL = BLOCKS.register(
            "green_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GREEN_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "green_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(GREEN_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "green_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(GREEN_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(GREEN_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "green_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(GREEN_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(GREEN_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "green_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(GREEN_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> GREEN_FLAMING_STEEL_CASING = BLOCKS.register("green_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GREEN_FLAMING_STEEL_GRATE = BLOCKS.register("green_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> GREEN_FLAMING_STEEL_PILLAR = BLOCKS.register("green_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> GREEN_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "green_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> RED_FLAMING_STEEL_BLOCK = BLOCKS.register("red_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> RED_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "red_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(RED_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> RED_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "red_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(RED_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(RED_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> RED_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "red_flaming_steel_block_stairs",
            () -> new ModStairBlock(RED_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(RED_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> RED_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "red_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(RED_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> RED_FLAMING_CUT_STEEL = BLOCKS.register("red_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> RED_FLAMING_CUT_STEEL_SLAB = BLOCKS.register("red_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(RED_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> RED_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "red_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(RED_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(RED_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> RED_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "red_flaming_cut_steel_stairs",
            () -> new ModStairBlock(RED_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(RED_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> RED_FLAMING_CUT_STEEL_WALL = BLOCKS.register("red_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(RED_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> RED_FLAMING_POLISHED_STEEL = BLOCKS.register("red_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> RED_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "red_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(RED_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> RED_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "red_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(RED_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(RED_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> RED_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "red_flaming_polished_steel_stairs",
            () -> new ModStairBlock(RED_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(RED_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> RED_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "red_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(RED_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> RED_FLAMING_PRESSED_STEEL = BLOCKS.register("red_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> RED_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "red_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(RED_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> RED_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "red_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(RED_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(RED_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> RED_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "red_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(RED_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(RED_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> RED_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "red_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(RED_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> RED_FLAMING_STEEL_CASING = BLOCKS.register("red_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> RED_FLAMING_STEEL_GRATE = BLOCKS.register("red_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> RED_FLAMING_STEEL_PILLAR = BLOCKS.register("red_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> RED_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "red_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLACK_FLAMING_STEEL_BLOCK = BLOCKS.register("black_flaming_steel_block",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLACK_FLAMING_STEEL_BLOCK_SLAB = BLOCKS.register(
            "black_flaming_steel_block_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(BLACK_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "black_flaming_steel_block_vertical_slab",
            () -> new VerticalSlabBlock(BLACK_FLAMING_STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(BLACK_FLAMING_STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_STEEL_BLOCK_STAIRS = BLOCKS.register(
            "black_flaming_steel_block_stairs",
            () -> new ModStairBlock(BLACK_FLAMING_STEEL_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BLACK_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_STEEL_BLOCK_WALL = BLOCKS.register(
            "black_flaming_steel_block_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(BLACK_FLAMING_STEEL_BLOCK.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_CUT_STEEL = BLOCKS.register("black_flaming_cut_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLACK_FLAMING_CUT_STEEL_SLAB = BLOCKS.register(
            "black_flaming_cut_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(BLACK_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_CUT_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "black_flaming_cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(BLACK_FLAMING_CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(BLACK_FLAMING_CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_CUT_STEEL_STAIRS = BLOCKS.register(
            "black_flaming_cut_steel_stairs",
            () -> new ModStairBlock(BLACK_FLAMING_CUT_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BLACK_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_CUT_STEEL_WALL = BLOCKS.register(
            "black_flaming_cut_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(BLACK_FLAMING_CUT_STEEL.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_POLISHED_STEEL = BLOCKS.register(
            "black_flaming_polished_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLACK_FLAMING_POLISHED_STEEL_SLAB = BLOCKS.register(
            "black_flaming_polished_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(BLACK_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "black_flaming_polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(BLACK_FLAMING_POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(BLACK_FLAMING_POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_POLISHED_STEEL_STAIRS = BLOCKS.register(
            "black_flaming_polished_steel_stairs",
            () -> new ModStairBlock(BLACK_FLAMING_POLISHED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BLACK_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_POLISHED_STEEL_WALL = BLOCKS.register(
            "black_flaming_polished_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(BLACK_FLAMING_POLISHED_STEEL.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_PRESSED_STEEL = BLOCKS.register(
            "black_flaming_pressed_steel",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLACK_FLAMING_PRESSED_STEEL_SLAB = BLOCKS.register(
            "black_flaming_pressed_steel_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(BLACK_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "black_flaming_pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(BLACK_FLAMING_PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(BLACK_FLAMING_PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_PRESSED_STEEL_STAIRS = BLOCKS.register(
            "black_flaming_pressed_steel_stairs",
            () -> new ModStairBlock(BLACK_FLAMING_PRESSED_STEEL.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BLACK_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_PRESSED_STEEL_WALL = BLOCKS.register(
            "black_flaming_pressed_steel_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(BLACK_FLAMING_PRESSED_STEEL.get())));
    public static final RegistryObject<Block> BLACK_FLAMING_STEEL_CASING = BLOCKS.register("black_flaming_steel_casing",
            () -> new Block(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLACK_FLAMING_STEEL_GRATE = BLOCKS.register("black_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(WoolBlocks.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> BLACK_FLAMING_STEEL_PILLAR = BLOCKS.register("black_flaming_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));
    public static final RegistryObject<Block> BLACK_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "black_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(WoolBlocks.flamingSteelProperties()));

    public static final RegistryObject<Block> HOLLOW_OAK_LOG = BLOCKS.register("hollow_oak_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
}
