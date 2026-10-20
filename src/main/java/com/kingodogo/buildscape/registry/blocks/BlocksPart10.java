package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ExperienceCauldronBlock;
import com.kingodogo.buildscape.block.ExperienceFluidBlock;
import com.kingodogo.buildscape.block.ModBlock;
import com.kingodogo.buildscape.block.ModStairBlock;
import com.kingodogo.buildscape.block.ModTrapdoorBlock;
import com.kingodogo.buildscape.block.ShelfBlock;
import com.kingodogo.buildscape.block.VerticalSlabBlock;
import com.kingodogo.buildscape.block.WaterloggableGrateBlock;
import com.kingodogo.buildscape.feature.pipe.HollowLogBlock;
import com.kingodogo.buildscape.registry.ModFluids;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WoodButtonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.ModBlocks.BLOCKS;

/** Part of the blocks registrations; see {@code ModBlocks}. */
public final class BlocksPart10 {

    private BlocksPart10() {}

    /** Forces this class to load so its registrations run in the order {@code ModBlocks} calls it. */
    public static void init() {}

    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_CUT_STEEL = BLOCKS.register(
            "light_gray_flaming_cut_steel",
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_STEEL_GRATE = BLOCKS.register(
            "light_gray_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(BlocksPart09.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_STEEL_PILLAR = BLOCKS.register(
            "light_gray_flaming_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> LIGHT_GRAY_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "light_gray_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> CYAN_FLAMING_STEEL_BLOCK = BLOCKS.register("cyan_flaming_steel_block",
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> CYAN_FLAMING_STEEL_GRATE = BLOCKS.register("cyan_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(BlocksPart09.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> CYAN_FLAMING_STEEL_PILLAR = BLOCKS.register("cyan_flaming_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> CYAN_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "cyan_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> PURPLE_FLAMING_STEEL_BLOCK = BLOCKS.register("purple_flaming_steel_block",
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> PURPLE_FLAMING_STEEL_GRATE = BLOCKS.register("purple_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(BlocksPart09.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> PURPLE_FLAMING_STEEL_PILLAR = BLOCKS.register(
            "purple_flaming_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> PURPLE_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "purple_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> BLUE_FLAMING_STEEL_BLOCK = BLOCKS.register("blue_flaming_steel_block",
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> BLUE_FLAMING_STEEL_GRATE = BLOCKS.register("blue_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(BlocksPart09.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> BLUE_FLAMING_STEEL_PILLAR = BLOCKS.register("blue_flaming_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> BLUE_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "blue_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> BROWN_FLAMING_STEEL_BLOCK = BLOCKS.register("brown_flaming_steel_block",
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> BROWN_FLAMING_STEEL_GRATE = BLOCKS.register("brown_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(BlocksPart09.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> BROWN_FLAMING_STEEL_PILLAR = BLOCKS.register("brown_flaming_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> BROWN_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "brown_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> GREEN_FLAMING_STEEL_BLOCK = BLOCKS.register("green_flaming_steel_block",
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> GREEN_FLAMING_STEEL_GRATE = BLOCKS.register("green_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(BlocksPart09.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> GREEN_FLAMING_STEEL_PILLAR = BLOCKS.register("green_flaming_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> GREEN_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "green_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> RED_FLAMING_STEEL_BLOCK = BLOCKS.register("red_flaming_steel_block",
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> RED_FLAMING_STEEL_GRATE = BLOCKS.register("red_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(BlocksPart09.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> RED_FLAMING_STEEL_PILLAR = BLOCKS.register("red_flaming_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> RED_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "red_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> BLACK_FLAMING_STEEL_BLOCK = BLOCKS.register("black_flaming_steel_block",
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
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
            () -> new Block(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> BLACK_FLAMING_STEEL_GRATE = BLOCKS.register("black_flaming_steel_grate",
            () -> new WaterloggableGrateBlock(BlocksPart09.flamingSteelProperties().noOcclusion()
                    .isRedstoneConductor((state, reader, pos) -> false).isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> BLACK_FLAMING_STEEL_PILLAR = BLOCKS.register("black_flaming_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));
    public static final RegistryObject<Block> BLACK_FLAMING_BOLTED_STEEL_PILLAR = BLOCKS.register(
            "black_flaming_bolted_steel_pillar",
            () -> new RotatedPillarBlock(BlocksPart09.flamingSteelProperties()));

    public static final RegistryObject<Block> HOLLOW_OAK_LOG = BLOCKS.register("hollow_oak_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
    public static final RegistryObject<Block> STRIPPED_HOLLOW_OAK_LOG = BLOCKS.register("stripped_hollow_oak_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final RegistryObject<Block> HOLLOW_SPRUCE_LOG = BLOCKS.register("hollow_spruce_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG)));
    public static final RegistryObject<Block> STRIPPED_HOLLOW_SPRUCE_LOG = BLOCKS.register("stripped_hollow_spruce_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_SPRUCE_LOG)));
    public static final RegistryObject<Block> HOLLOW_BIRCH_LOG = BLOCKS.register("hollow_birch_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.BIRCH_LOG)));
    public static final RegistryObject<Block> STRIPPED_HOLLOW_BIRCH_LOG = BLOCKS.register("stripped_hollow_birch_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_BIRCH_LOG)));
    public static final RegistryObject<Block> HOLLOW_JUNGLE_LOG = BLOCKS.register("hollow_jungle_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.JUNGLE_LOG)));
    public static final RegistryObject<Block> STRIPPED_HOLLOW_JUNGLE_LOG = BLOCKS.register("stripped_hollow_jungle_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_JUNGLE_LOG)));
    public static final RegistryObject<Block> HOLLOW_ACACIA_LOG = BLOCKS.register("hollow_acacia_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_LOG)));
    public static final RegistryObject<Block> STRIPPED_HOLLOW_ACACIA_LOG = BLOCKS.register("stripped_hollow_acacia_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_ACACIA_LOG)));
    public static final RegistryObject<Block> HOLLOW_DARK_OAK_LOG = BLOCKS.register("hollow_dark_oak_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_LOG)));
    public static final RegistryObject<Block> STRIPPED_HOLLOW_DARK_OAK_LOG = BLOCKS.register(
            "stripped_hollow_dark_oak_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_DARK_OAK_LOG)));
    public static final RegistryObject<Block> HOLLOW_CRIMSON_STEM = BLOCKS.register("hollow_crimson_stem",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.CRIMSON_STEM)));
    public static final RegistryObject<Block> STRIPPED_HOLLOW_CRIMSON_STEM = BLOCKS.register(
            "stripped_hollow_crimson_stem",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_CRIMSON_STEM)));
    public static final RegistryObject<Block> HOLLOW_WARPED_STEM = BLOCKS.register("hollow_warped_stem",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_STEM)));
    public static final RegistryObject<Block> STRIPPED_HOLLOW_WARPED_STEM = BLOCKS.register(
            "stripped_hollow_warped_stem",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_WARPED_STEM)));
    public static final RegistryObject<Block> HOLLOW_MANGROVE_LOG = BLOCKS.register("hollow_mangrove_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(BlocksPart04.MANGROVE_LOG.get())));
    public static final RegistryObject<Block> STRIPPED_HOLLOW_MANGROVE_LOG = BLOCKS.register(
            "stripped_hollow_mangrove_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(BlocksPart04.STRIPPED_MANGROVE_LOG.get())));
    public static final RegistryObject<Block> HOLLOW_ASHPEN_LOG = BLOCKS.register("hollow_ashpen_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(BlocksPart07.ASHPEN_LOG.get())));
    public static final RegistryObject<Block> STRIPPED_HOLLOW_ASHPEN_LOG = BLOCKS.register("stripped_hollow_ashpen_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(BlocksPart07.STRIPPED_ASHPEN_LOG.get())));
    public static final RegistryObject<Block> HOLLOW_POPLAR_LOG = BLOCKS.register("hollow_poplar_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(BlocksPart08.POPLAR_LOG.get())));
    public static final RegistryObject<Block> STRIPPED_HOLLOW_POPLAR_LOG = BLOCKS.register("stripped_hollow_poplar_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(BlocksPart08.STRIPPED_POPLAR_LOG.get())));
    public static final RegistryObject<Block> HOLLOW_PALE_OAK_LOG = BLOCKS.register("hollow_pale_oak_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(BlocksPart09.PALE_OAK_LOG.get())));
    public static final RegistryObject<Block> STRIPPED_HOLLOW_PALE_OAK_LOG = BLOCKS.register(
            "stripped_hollow_pale_oak_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(BlocksPart09.STRIPPED_PALE_OAK_LOG.get())));
    public static final RegistryObject<Block> HOLLOW_CHERRY_LOG = BLOCKS.register("hollow_cherry_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(BlocksPart09.CHERRY_LOG.get())));
    public static final RegistryObject<Block> STRIPPED_HOLLOW_CHERRY_LOG = BLOCKS.register("stripped_hollow_cherry_log",
            () -> new HollowLogBlock(BlockBehaviour.Properties.copy(BlocksPart09.STRIPPED_CHERRY_LOG.get())));

    public static final RegistryObject<Block> CARDBOARD_BLOCK = BLOCKS.register("cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> CARDBOARD_SLAB = BLOCKS.register("cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> CARDBOARD_STAIRS = BLOCKS.register("cardboard_stairs",
            () -> new ModStairBlock(
                    CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> CARDBOARD_WALL = BLOCKS.register("cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> CARDBOARD_VERTICAL_SLAB = BLOCKS.register("cardboard_vertical_slab",
            () -> new VerticalSlabBlock(CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> CARDBOARD_TRAPDOOR = BLOCKS.register("cardboard_trapdoor",
            () -> new ModTrapdoorBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));
    public static final RegistryObject<Block> CARDBOARD_BUTTON = BLOCKS.register("cardboard_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.WOOD)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> CARDBOARD_PRESSURE_PLATE = BLOCKS.register("cardboard_pressure_plate",
            () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> SMOOTH_CARDBOARD_BLOCK = BLOCKS.register("smooth_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> SMOOTH_CARDBOARD_SLAB = BLOCKS.register("smooth_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> SMOOTH_CARDBOARD_STAIRS = BLOCKS.register("smooth_cardboard_stairs",
            () -> new ModStairBlock(
                    SMOOTH_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> SMOOTH_CARDBOARD_WALL = BLOCKS.register("smooth_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> SMOOTH_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "smooth_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(SMOOTH_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(SMOOTH_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> BUNDLED_CARDBOARD = BLOCKS.register("bundled_cardboard",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BUNDLED_CARDBOARD_SLAB = BLOCKS.register("bundled_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BUNDLED_CARDBOARD_STAIRS = BLOCKS.register("bundled_cardboard_stairs",
            () -> new ModStairBlock(
                    BUNDLED_CARDBOARD.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BUNDLED_CARDBOARD_WALL = BLOCKS.register("bundled_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BUNDLED_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "bundled_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(BUNDLED_CARDBOARD.get(),
                    BlockBehaviour.Properties.copy(BUNDLED_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> PRESSED_CARDBOARD = BLOCKS.register("pressed_cardboard",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PRESSED_CARDBOARD_SLAB = BLOCKS.register("pressed_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PRESSED_CARDBOARD_STAIRS = BLOCKS.register("pressed_cardboard_stairs",
            () -> new ModStairBlock(
                    PRESSED_CARDBOARD.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PRESSED_CARDBOARD_WALL = BLOCKS.register("pressed_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PRESSED_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "pressed_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(PRESSED_CARDBOARD.get(),
                    BlockBehaviour.Properties.copy(PRESSED_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> THICK_CARDBOARD_BLOCK = BLOCKS.register("thick_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> THICK_CARDBOARD_SLAB = BLOCKS.register("thick_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> THICK_CARDBOARD_STAIRS = BLOCKS.register("thick_cardboard_stairs",
            () -> new ModStairBlock(
                    THICK_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> THICK_CARDBOARD_WALL = BLOCKS.register("thick_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> THICK_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "thick_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(THICK_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(THICK_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_CARDBOARD_BLOCK = BLOCKS.register("stripped_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_CARDBOARD_SLAB = BLOCKS.register("stripped_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_CARDBOARD_STAIRS = BLOCKS.register("stripped_cardboard_stairs",
            () -> new ModStairBlock(
                    STRIPPED_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_CARDBOARD_WALL = BLOCKS.register("stripped_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(STRIPPED_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(STRIPPED_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_CARDBOARD_TRAPDOOR = BLOCKS.register(
            "stripped_cardboard_trapdoor",
            () -> new ModTrapdoorBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));
    public static final RegistryObject<Block> STRIPPED_CARDBOARD_BUTTON = BLOCKS.register("stripped_cardboard_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.SAND)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> STRIPPED_CARDBOARD_PRESSURE_PLATE = BLOCKS.register(
            "stripped_cardboard_pressure_plate",
            () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> STRIPPED_SMOOTH_CARDBOARD_BLOCK = BLOCKS.register(
            "stripped_smooth_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_SMOOTH_CARDBOARD_SLAB = BLOCKS.register(
            "stripped_smooth_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_SMOOTH_CARDBOARD_STAIRS = BLOCKS.register(
            "stripped_smooth_cardboard_stairs",
            () -> new ModStairBlock(
                    STRIPPED_SMOOTH_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_SMOOTH_CARDBOARD_WALL = BLOCKS.register(
            "stripped_smooth_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_SMOOTH_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_smooth_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(STRIPPED_SMOOTH_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(STRIPPED_SMOOTH_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_BUNDLED_CARDBOARD = BLOCKS.register("stripped_bundled_cardboard",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_BUNDLED_CARDBOARD_SLAB = BLOCKS.register(
            "stripped_bundled_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_BUNDLED_CARDBOARD_STAIRS = BLOCKS.register(
            "stripped_bundled_cardboard_stairs",
            () -> new ModStairBlock(
                    STRIPPED_BUNDLED_CARDBOARD.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_BUNDLED_CARDBOARD_WALL = BLOCKS.register(
            "stripped_bundled_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_BUNDLED_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_bundled_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(STRIPPED_BUNDLED_CARDBOARD.get(),
                    BlockBehaviour.Properties.copy(STRIPPED_BUNDLED_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_PRESSED_CARDBOARD = BLOCKS.register("stripped_pressed_cardboard",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_PRESSED_CARDBOARD_SLAB = BLOCKS.register(
            "stripped_pressed_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_PRESSED_CARDBOARD_STAIRS = BLOCKS.register(
            "stripped_pressed_cardboard_stairs",
            () -> new ModStairBlock(
                    STRIPPED_PRESSED_CARDBOARD.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_PRESSED_CARDBOARD_WALL = BLOCKS.register(
            "stripped_pressed_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_PRESSED_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_pressed_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(STRIPPED_PRESSED_CARDBOARD.get(),
                    BlockBehaviour.Properties.copy(STRIPPED_PRESSED_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_THICK_CARDBOARD_BLOCK = BLOCKS.register(
            "stripped_thick_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_THICK_CARDBOARD_SLAB = BLOCKS.register(
            "stripped_thick_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_THICK_CARDBOARD_STAIRS = BLOCKS.register(
            "stripped_thick_cardboard_stairs",
            () -> new ModStairBlock(
                    STRIPPED_THICK_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_THICK_CARDBOARD_WALL = BLOCKS.register(
            "stripped_thick_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_THICK_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "stripped_thick_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(STRIPPED_THICK_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(STRIPPED_THICK_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> TINTED_CARDBOARD_BLOCK = BLOCKS.register("tinted_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_CARDBOARD_SLAB = BLOCKS.register("tinted_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_CARDBOARD_STAIRS = BLOCKS.register("tinted_cardboard_stairs",
            () -> new ModStairBlock(
                    TINTED_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_CARDBOARD_WALL = BLOCKS.register("tinted_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "tinted_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(TINTED_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(TINTED_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> TINTED_CARDBOARD_TRAPDOOR = BLOCKS.register("tinted_cardboard_trapdoor",
            () -> new ModTrapdoorBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));
    public static final RegistryObject<Block> TINTED_CARDBOARD_BUTTON = BLOCKS.register("tinted_cardboard_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.COLOR_BROWN)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> TINTED_CARDBOARD_PRESSURE_PLATE = BLOCKS.register(
            "tinted_cardboard_pressure_plate",
            () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> TINTED_SMOOTH_CARDBOARD_BLOCK = BLOCKS.register(
            "tinted_smooth_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_SMOOTH_CARDBOARD_SLAB = BLOCKS.register(
            "tinted_smooth_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_SMOOTH_CARDBOARD_STAIRS = BLOCKS.register(
            "tinted_smooth_cardboard_stairs",
            () -> new ModStairBlock(
                    TINTED_SMOOTH_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_SMOOTH_CARDBOARD_WALL = BLOCKS.register(
            "tinted_smooth_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_SMOOTH_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "tinted_smooth_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(TINTED_SMOOTH_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(TINTED_SMOOTH_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> TINTED_BUNDLED_CARDBOARD = BLOCKS.register("tinted_bundled_cardboard",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_BUNDLED_CARDBOARD_SLAB = BLOCKS.register(
            "tinted_bundled_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_BUNDLED_CARDBOARD_STAIRS = BLOCKS.register(
            "tinted_bundled_cardboard_stairs",
            () -> new ModStairBlock(
                    TINTED_BUNDLED_CARDBOARD.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_BUNDLED_CARDBOARD_WALL = BLOCKS.register(
            "tinted_bundled_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_BUNDLED_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "tinted_bundled_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(TINTED_BUNDLED_CARDBOARD.get(),
                    BlockBehaviour.Properties.copy(TINTED_BUNDLED_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> TINTED_PRESSED_CARDBOARD = BLOCKS.register("tinted_pressed_cardboard",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_PRESSED_CARDBOARD_SLAB = BLOCKS.register(
            "tinted_pressed_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_PRESSED_CARDBOARD_STAIRS = BLOCKS.register(
            "tinted_pressed_cardboard_stairs",
            () -> new ModStairBlock(
                    TINTED_PRESSED_CARDBOARD.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_PRESSED_CARDBOARD_WALL = BLOCKS.register(
            "tinted_pressed_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_PRESSED_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "tinted_pressed_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(TINTED_PRESSED_CARDBOARD.get(),
                    BlockBehaviour.Properties.copy(TINTED_PRESSED_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> TINTED_THICK_CARDBOARD_BLOCK = BLOCKS.register(
            "tinted_thick_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_THICK_CARDBOARD_SLAB = BLOCKS.register(
            "tinted_thick_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_THICK_CARDBOARD_STAIRS = BLOCKS.register(
            "tinted_thick_cardboard_stairs",
            () -> new ModStairBlock(
                    TINTED_THICK_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_THICK_CARDBOARD_WALL = BLOCKS.register(
            "tinted_thick_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> TINTED_THICK_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "tinted_thick_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(TINTED_THICK_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(TINTED_THICK_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> WASHED_CARDBOARD_BLOCK = BLOCKS.register("washed_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_CARDBOARD_SLAB = BLOCKS.register("washed_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_CARDBOARD_STAIRS = BLOCKS.register("washed_cardboard_stairs",
            () -> new ModStairBlock(
                    WASHED_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_CARDBOARD_WALL = BLOCKS.register("washed_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "washed_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(WASHED_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(WASHED_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> WASHED_CARDBOARD_TRAPDOOR = BLOCKS.register("washed_cardboard_trapdoor",
            () -> new ModTrapdoorBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));
    public static final RegistryObject<Block> WASHED_CARDBOARD_BUTTON = BLOCKS.register("washed_cardboard_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.CLAY)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> WASHED_CARDBOARD_PRESSURE_PLATE = BLOCKS.register(
            "washed_cardboard_pressure_plate",
            () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> WASHED_SMOOTH_CARDBOARD_BLOCK = BLOCKS.register(
            "washed_smooth_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_SMOOTH_CARDBOARD_SLAB = BLOCKS.register(
            "washed_smooth_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_SMOOTH_CARDBOARD_STAIRS = BLOCKS.register(
            "washed_smooth_cardboard_stairs",
            () -> new ModStairBlock(
                    WASHED_SMOOTH_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_SMOOTH_CARDBOARD_WALL = BLOCKS.register(
            "washed_smooth_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_SMOOTH_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "washed_smooth_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(WASHED_SMOOTH_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(WASHED_SMOOTH_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> WASHED_BUNDLED_CARDBOARD = BLOCKS.register("washed_bundled_cardboard",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_BUNDLED_CARDBOARD_SLAB = BLOCKS.register(
            "washed_bundled_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_BUNDLED_CARDBOARD_STAIRS = BLOCKS.register(
            "washed_bundled_cardboard_stairs",
            () -> new ModStairBlock(
                    WASHED_BUNDLED_CARDBOARD.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_BUNDLED_CARDBOARD_WALL = BLOCKS.register(
            "washed_bundled_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_BUNDLED_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "washed_bundled_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(WASHED_BUNDLED_CARDBOARD.get(),
                    BlockBehaviour.Properties.copy(WASHED_BUNDLED_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> WASHED_PRESSED_CARDBOARD = BLOCKS.register("washed_pressed_cardboard",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_PRESSED_CARDBOARD_SLAB = BLOCKS.register(
            "washed_pressed_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_PRESSED_CARDBOARD_STAIRS = BLOCKS.register(
            "washed_pressed_cardboard_stairs",
            () -> new ModStairBlock(
                    WASHED_PRESSED_CARDBOARD.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_PRESSED_CARDBOARD_WALL = BLOCKS.register(
            "washed_pressed_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_PRESSED_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "washed_pressed_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(WASHED_PRESSED_CARDBOARD.get(),
                    BlockBehaviour.Properties.copy(WASHED_PRESSED_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> WASHED_THICK_CARDBOARD_BLOCK = BLOCKS.register(
            "washed_thick_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_THICK_CARDBOARD_SLAB = BLOCKS.register(
            "washed_thick_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_THICK_CARDBOARD_STAIRS = BLOCKS.register(
            "washed_thick_cardboard_stairs",
            () -> new ModStairBlock(
                    WASHED_THICK_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_THICK_CARDBOARD_WALL = BLOCKS.register(
            "washed_thick_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.CLAY)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> WASHED_THICK_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "washed_thick_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(WASHED_THICK_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(WASHED_THICK_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> BURNT_CARDBOARD_BLOCK = BLOCKS.register("burnt_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_CARDBOARD_SLAB = BLOCKS.register("burnt_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_CARDBOARD_STAIRS = BLOCKS.register("burnt_cardboard_stairs",
            () -> new ModStairBlock(
                    BURNT_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_CARDBOARD_WALL = BLOCKS.register("burnt_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "burnt_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(BURNT_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(BURNT_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> BURNT_CARDBOARD_TRAPDOOR = BLOCKS.register("burnt_cardboard_trapdoor",
            () -> new ModTrapdoorBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));
    public static final RegistryObject<Block> BURNT_CARDBOARD_BUTTON = BLOCKS.register("burnt_cardboard_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.COLOR_BLACK)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> BURNT_CARDBOARD_PRESSURE_PLATE = BLOCKS.register(
            "burnt_cardboard_pressure_plate",
            () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> BURNT_SMOOTH_CARDBOARD_BLOCK = BLOCKS.register(
            "burnt_smooth_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_SMOOTH_CARDBOARD_SLAB = BLOCKS.register(
            "burnt_smooth_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_SMOOTH_CARDBOARD_STAIRS = BLOCKS.register(
            "burnt_smooth_cardboard_stairs",
            () -> new ModStairBlock(
                    BURNT_SMOOTH_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_SMOOTH_CARDBOARD_WALL = BLOCKS.register(
            "burnt_smooth_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_SMOOTH_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "burnt_smooth_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(BURNT_SMOOTH_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(BURNT_SMOOTH_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> BURNT_BUNDLED_CARDBOARD = BLOCKS.register("burnt_bundled_cardboard",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_BUNDLED_CARDBOARD_SLAB = BLOCKS.register(
            "burnt_bundled_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_BUNDLED_CARDBOARD_STAIRS = BLOCKS.register(
            "burnt_bundled_cardboard_stairs",
            () -> new ModStairBlock(
                    BURNT_BUNDLED_CARDBOARD.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_BUNDLED_CARDBOARD_WALL = BLOCKS.register(
            "burnt_bundled_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_BUNDLED_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "burnt_bundled_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(BURNT_BUNDLED_CARDBOARD.get(),
                    BlockBehaviour.Properties.copy(BURNT_BUNDLED_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> BURNT_PRESSED_CARDBOARD = BLOCKS.register("burnt_pressed_cardboard",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_PRESSED_CARDBOARD_SLAB = BLOCKS.register(
            "burnt_pressed_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_PRESSED_CARDBOARD_STAIRS = BLOCKS.register(
            "burnt_pressed_cardboard_stairs",
            () -> new ModStairBlock(
                    BURNT_PRESSED_CARDBOARD.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_PRESSED_CARDBOARD_WALL = BLOCKS.register(
            "burnt_pressed_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_PRESSED_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "burnt_pressed_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(BURNT_PRESSED_CARDBOARD.get(),
                    BlockBehaviour.Properties.copy(BURNT_PRESSED_CARDBOARD_SLAB.get())));
    public static final RegistryObject<Block> BURNT_THICK_CARDBOARD_BLOCK = BLOCKS.register(
            "burnt_thick_cardboard_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_THICK_CARDBOARD_SLAB = BLOCKS.register("burnt_thick_cardboard_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_THICK_CARDBOARD_STAIRS = BLOCKS.register(
            "burnt_thick_cardboard_stairs",
            () -> new ModStairBlock(
                    BURNT_THICK_CARDBOARD_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_THICK_CARDBOARD_WALL = BLOCKS.register("burnt_thick_cardboard_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f, 0.5f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BURNT_THICK_CARDBOARD_VERTICAL_SLAB = BLOCKS.register(
            "burnt_thick_cardboard_vertical_slab",
            () -> new VerticalSlabBlock(BURNT_THICK_CARDBOARD_BLOCK.get(),
                    BlockBehaviour.Properties.copy(BURNT_THICK_CARDBOARD_SLAB.get())));

    public static final RegistryObject<Block> OAK_SHELF = BLOCKS.register("oak_shelf",
            () -> new ShelfBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2.0f, 3.0f)));
    public static final RegistryObject<Block> SPRUCE_SHELF = BLOCKS.register("spruce_shelf",
            () -> new ShelfBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS).strength(2.0f, 3.0f)));
    public static final RegistryObject<Block> BIRCH_SHELF = BLOCKS.register("birch_shelf",
            () -> new ShelfBlock(BlockBehaviour.Properties.copy(Blocks.BIRCH_PLANKS).strength(2.0f, 3.0f)));
    public static final RegistryObject<Block> JUNGLE_SHELF = BLOCKS.register("jungle_shelf",
            () -> new ShelfBlock(BlockBehaviour.Properties.copy(Blocks.JUNGLE_PLANKS).strength(2.0f, 3.0f)));
    public static final RegistryObject<Block> ACACIA_SHELF = BLOCKS.register("acacia_shelf",
            () -> new ShelfBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS).strength(2.0f, 3.0f)));
    public static final RegistryObject<Block> DARK_OAK_SHELF = BLOCKS.register("dark_oak_shelf",
            () -> new ShelfBlock(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_PLANKS).strength(2.0f, 3.0f)));
    public static final RegistryObject<Block> MANGROVE_SHELF = BLOCKS.register("mangrove_shelf",
            () -> new ShelfBlock(
                    BlockBehaviour.Properties.copy(BlocksPart04.MANGROVE_PLANKS.get()).strength(2.0f, 3.0f)));
    public static final RegistryObject<Block> CHERRY_SHELF = BLOCKS.register("cherry_shelf",
            () -> new ShelfBlock(
                    BlockBehaviour.Properties.copy(BlocksPart09.CHERRY_PLANKS.get()).strength(2.0f, 3.0f)));
    public static final RegistryObject<Block> CRIMSON_SHELF = BLOCKS.register("crimson_shelf",
            () -> new ShelfBlock(BlockBehaviour.Properties.copy(Blocks.CRIMSON_PLANKS).strength(2.0f, 3.0f)));
    public static final RegistryObject<Block> WARPED_SHELF = BLOCKS.register("warped_shelf",
            () -> new ShelfBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS).strength(2.0f, 3.0f)));
    public static final RegistryObject<Block> BAMBOO_SHELF = BLOCKS.register("bamboo_shelf",
            () -> new ShelfBlock(BlockBehaviour.Properties.copy(BlocksPart03.BAMBOO_BLOCK.get()).strength(2.0f, 3.0f)));
    public static final RegistryObject<Block> STRIPPED_BAMBOO_SHELF = BLOCKS.register("stripped_bamboo_shelf",
            () -> new ShelfBlock(
                    BlockBehaviour.Properties.copy(BlocksPart03.STRIPPED_BAMBOO_BLOCK.get()).strength(2.0f, 3.0f)));
    public static final RegistryObject<Block> PALE_OAK_SHELF = BLOCKS.register("pale_oak_shelf",
            () -> new ShelfBlock(
                    BlockBehaviour.Properties.copy(BlocksPart09.PALE_OAK_PLANKS.get()).strength(2.0f, 3.0f)));
    public static final RegistryObject<Block> POPLAR_SHELF = BLOCKS.register("poplar_shelf",
            () -> new ShelfBlock(
                    BlockBehaviour.Properties.copy(BlocksPart08.POPLAR_PLANKS.get()).strength(2.0f, 3.0f)));

    public static final RegistryObject<Block> EXPERIENCE_BLOCK = BLOCKS.register("experience_liquid",
            () -> new ExperienceFluidBlock(
                    () -> ModFluids.EXPERIENCE_STILL.get(),
                    BlockBehaviour.Properties.of(Material.WATER)
                            .noCollission()
                            .strength(100.0F)
                            .noDrops()
                            .lightLevel(state -> 15)));

    public static final RegistryObject<Block> EXPERIENCE_CAULDRON = BLOCKS.register("experience_cauldron",
            () -> new ExperienceCauldronBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                            .requiresCorrectToolForDrops()
                            .strength(2.0F)
                            .lightLevel(state -> 15)
                            .noOcclusion(),
                    BuildScape.EXPERIENCE_CAULDRON_INTERACTIONS));
}
