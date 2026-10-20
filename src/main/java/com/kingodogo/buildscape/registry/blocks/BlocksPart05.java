package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.block.ModButtonBlock;
import com.kingodogo.buildscape.block.ModPressurePlateBlock;
import com.kingodogo.buildscape.block.ModStairBlock;
import com.kingodogo.buildscape.block.SilkTouchOnlyGlassBlock;
import com.kingodogo.buildscape.block.SilkTouchOnlyPaneBlock;
import com.kingodogo.buildscape.block.VerticalSlabBlock;
import com.kingodogo.buildscape.block.WaterloggableGrateBlock;
import com.kingodogo.buildscape.feature.copper.ModCopperDoorBlock;
import com.kingodogo.buildscape.feature.copper.ModCopperTrapdoorBlock;
import com.kingodogo.buildscape.registry.ModSounds;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.ModBlocks.BLOCKS;

/** Part of the blocks registrations; see {@code ModBlocks}. */
public final class BlocksPart05 {

    private BlocksPart05() {
    }

    /** Forces this class to load so its registrations run in the order {@code ModBlocks} calls it. */
    public static void init() {
    }

        public static final RegistryObject<Block> STEEL_DOOR = BLOCKS.register(
                        "steel_door",
                        () -> new ModCopperDoorBlock(
                                        BlockBehaviour.Properties.copy(BlocksPart04.STEEL_BLOCK.get()).noOcclusion()));
        public static final RegistryObject<Block> STEEL_TRAPDOOR = BLOCKS.register(
                        "steel_trapdoor",
                        () -> new ModCopperTrapdoorBlock(
                                        BlockBehaviour.Properties.copy(BlocksPart04.STEEL_BLOCK.get()).noOcclusion()));
        public static final RegistryObject<Block> STEEL_BUTTON = BLOCKS.register(
                        "steel_button",
                        () -> new ModButtonBlock(
                                        BlockBehaviour.Properties.copy(BlocksPart04.STEEL_BLOCK.get()).noCollission().strength(0.5f)));
        public static final RegistryObject<Block> STEEL_PRESSURE_PLATE = BLOCKS.register(
                        "steel_pressure_plate",
                        () -> new ModPressurePlateBlock(
                                        150,
                                        BlockBehaviour.Properties.copy(BlocksPart04.STEEL_BLOCK.get()).noCollission().strength(0.5f)));

        public static final RegistryObject<Block> SCRAPED_STEEL_SLAB = BLOCKS.register(
                        "scraped_steel_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.copy(BlocksPart04.SCRAPED_STEEL.get())));
        public static final RegistryObject<Block> SCRAPED_STEEL_VERTICAL_SLAB = BLOCKS.register(
                        "scraped_steel_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart04.SCRAPED_STEEL.get(),
                                        BlockBehaviour.Properties.copy(SCRAPED_STEEL_SLAB.get())));

        public static final RegistryObject<Block> RUSTIC_SCRAPED_STEEL_SLAB = BLOCKS.register(
                        "rustic_scraped_steel_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.copy(BlocksPart04.RUSTIC_SCRAPED_STEEL.get())));
        public static final RegistryObject<Block> RUSTIC_SCRAPED_STEEL_VERTICAL_SLAB = BLOCKS.register(
                        "rustic_scraped_steel_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart04.RUSTIC_SCRAPED_STEEL.get(),
                                        BlockBehaviour.Properties.copy(RUSTIC_SCRAPED_STEEL_SLAB.get())));

        public static final RegistryObject<Block> STACKED_STEEL_SLAB = BLOCKS.register(
                        "stacked_steel_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.copy(BlocksPart04.STACKED_STEEL.get())));
        public static final RegistryObject<Block> STACKED_STEEL_VERTICAL_SLAB = BLOCKS.register(
                        "stacked_steel_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart04.STACKED_STEEL.get(),
                                        BlockBehaviour.Properties.copy(STACKED_STEEL_SLAB.get())));
        public static final RegistryObject<Block> STACKED_STEEL_STAIRS = BLOCKS.register(
                        "stacked_steel_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart04.STACKED_STEEL.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart04.STACKED_STEEL.get())));

        public static final RegistryObject<Block> STEEL_PANELS_SLAB = BLOCKS.register(
                        "steel_panels_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.copy(BlocksPart04.STEEL_PANELS.get())));
        public static final RegistryObject<Block> STEEL_PANELS_VERTICAL_SLAB = BLOCKS.register(
                        "steel_panels_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart04.STEEL_PANELS.get(),
                                        BlockBehaviour.Properties.copy(STEEL_PANELS_SLAB.get())));
        public static final RegistryObject<Block> STEEL_PANELS_STAIRS = BLOCKS.register(
                        "steel_panels_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart04.STEEL_PANELS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart04.STEEL_PANELS.get())));

        public static final RegistryObject<Block> CROSSED_STEEL_PANELS_SLAB = BLOCKS.register(
                        "crossed_steel_panels_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.copy(BlocksPart04.CROSSED_STEEL_PANELS.get())));
        public static final RegistryObject<Block> CROSSED_STEEL_PANELS_VERTICAL_SLAB = BLOCKS.register(
                        "crossed_steel_panels_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart04.CROSSED_STEEL_PANELS.get(),
                                        BlockBehaviour.Properties.copy(CROSSED_STEEL_PANELS_SLAB.get())));

        public static final RegistryObject<Block> STEEL_MESH_BLOCK_SLAB = BLOCKS.register(
                        "steel_mesh_block_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.copy(BlocksPart04.STEEL_MESH_BLOCK.get())));
        public static final RegistryObject<Block> STEEL_MESH_BLOCK_VERTICAL_SLAB = BLOCKS.register(
                        "steel_mesh_block_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart04.STEEL_MESH_BLOCK.get(),
                                        BlockBehaviour.Properties.copy(STEEL_MESH_BLOCK_SLAB.get())));
        public static final RegistryObject<Block> STEEL_MESH_BLOCK_STAIRS = BLOCKS.register(
                        "steel_mesh_block_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart04.STEEL_MESH_BLOCK.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart04.STEEL_MESH_BLOCK.get())));
        public static final RegistryObject<Block> STEEL_BLOCK_STAIRS = BLOCKS.register(
                        "steel_block_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart04.STEEL_BLOCK.get().defaultBlockState(),
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
                                                        .sound(SoundType.COPPER)));
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
        public static final RegistryObject<Block> CAUTION_BLACK = BLOCKS.register(
                        "caution_black",
                        () -> new RotatedPillarBlock(
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
                                                        .strength(5.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.COPPER)));
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
        public static final RegistryObject<Block> WHITE_STAINED_BRICKS = BLOCKS.register(
                        "white_stained_bricks",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
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

        public static final RegistryObject<Block> WHITE_STAINED_BRICK_TILES = BLOCKS.register(
                        "white_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_BRICK_TILES = BLOCKS.register(
                        "light_gray_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.METAL)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GRAY_STAINED_BRICK_TILES = BLOCKS.register(
                        "gray_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_STAINED_BRICK_TILES = BLOCKS.register(
                        "black_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BROWN_STAINED_BRICK_TILES = BLOCKS.register(
                        "brown_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> RED_STAINED_BRICK_TILES = BLOCKS.register(
                        "red_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_STAINED_BRICK_TILES = BLOCKS.register(
                        "orange_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_STAINED_BRICK_TILES = BLOCKS.register(
                        "yellow_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIME_STAINED_BRICK_TILES = BLOCKS.register(
                        "lime_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_STAINED_BRICK_TILES = BLOCKS.register(
                        "green_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> CYAN_STAINED_BRICK_TILES = BLOCKS.register(
                        "cyan_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_BRICK_TILES = BLOCKS.register(
                        "light_blue_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_STAINED_BRICK_TILES = BLOCKS.register(
                        "blue_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PURPLE_STAINED_BRICK_TILES = BLOCKS.register(
                        "purple_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> MAGENTA_STAINED_BRICK_TILES = BLOCKS.register(
                        "magenta_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_MAGENTA)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PINK_STAINED_BRICK_TILES = BLOCKS.register(
                        "pink_stained_brick_tiles",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "white_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "white_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> WHITE_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> WHITE_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "white_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "light_gray_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.METAL)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "light_gray_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> LIGHT_GRAY_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.METAL)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "light_gray_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.METAL)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GRAY_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "gray_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GRAY_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "gray_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> GRAY_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GRAY_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "gray_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "black_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "black_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> BLACK_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLACK_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "black_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BROWN_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "brown_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BROWN_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "brown_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> BROWN_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BROWN_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "brown_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> RED_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "red_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> RED_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "red_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> RED_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> RED_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "red_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "orange_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "orange_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> ORANGE_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> ORANGE_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "orange_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "yellow_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "yellow_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> YELLOW_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> YELLOW_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "yellow_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIME_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "lime_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIME_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "lime_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> LIME_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIME_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "lime_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "green_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "green_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> GREEN_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> GREEN_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "green_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> CYAN_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "cyan_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> CYAN_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "cyan_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> CYAN_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> CYAN_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "cyan_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "light_blue_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "light_blue_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> LIGHT_BLUE_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "light_blue_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "blue_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "blue_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> BLUE_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> BLUE_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "blue_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PURPLE_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "purple_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PURPLE_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "purple_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> PURPLE_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PURPLE_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "purple_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> MAGENTA_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "magenta_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_MAGENTA)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> MAGENTA_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "magenta_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> MAGENTA_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_MAGENTA)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> MAGENTA_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "magenta_stained_brick_tiles_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_MAGENTA)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PINK_STAINED_BRICK_TILES_SLAB = BLOCKS.register(
                        "pink_stained_brick_tiles_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PINK_STAINED_BRICK_TILES_STAIRS = BLOCKS.register(
                        "pink_stained_brick_tiles_stairs",
                        () -> new StairBlock(
                                        () -> PINK_STAINED_BRICK_TILES.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                                                        .strength(2.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.STONE)));
        public static final RegistryObject<Block> PINK_STAINED_BRICK_TILES_WALL = BLOCKS.register(
                        "pink_stained_brick_tiles_wall",
                        () -> new WallBlock(
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
}
