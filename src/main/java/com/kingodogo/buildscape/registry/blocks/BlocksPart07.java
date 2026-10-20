package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.block.LogSlabBlock;
import com.kingodogo.buildscape.block.ModSlabBlock;
import com.kingodogo.buildscape.block.ModStairBlock;
import com.kingodogo.buildscape.block.VerticalSlabBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.ModBlocks.BLOCKS;

/** Part of the blocks registrations; see {@code ModBlocks}. */
public final class BlocksPart07 {

    private BlocksPart07() {
    }

    /** Forces this class to load so its registrations run in the order {@code ModBlocks} calls it. */
    public static void init() {
    }

        public static final RegistryObject<Block> RED_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "red_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.RED_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.RED_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> RED_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "red_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.RED_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.RED_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> ORANGE_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "orange_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.ORANGE_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.ORANGE_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> ORANGE_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "orange_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.ORANGE_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.ORANGE_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> YELLOW_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "yellow_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.YELLOW_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.YELLOW_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> YELLOW_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "yellow_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.YELLOW_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.YELLOW_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> LIME_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "lime_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.LIME_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.LIME_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> LIME_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "lime_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.LIME_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.LIME_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> GREEN_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "green_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.GREEN_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.GREEN_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> GREEN_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "green_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.GREEN_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.GREEN_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> CYAN_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "cyan_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.CYAN_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.CYAN_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> CYAN_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "cyan_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.CYAN_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.CYAN_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> LIGHT_BLUE_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "light_blue_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.LIGHT_BLUE_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.LIGHT_BLUE_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> LIGHT_BLUE_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "light_blue_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.LIGHT_BLUE_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.LIGHT_BLUE_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> BLUE_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "blue_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.BLUE_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BLUE_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> BLUE_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "blue_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.BLUE_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BLUE_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> PURPLE_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "purple_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.PURPLE_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.PURPLE_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> PURPLE_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "purple_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.PURPLE_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.PURPLE_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> MAGENTA_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "magenta_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.MAGENTA_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.MAGENTA_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> MAGENTA_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "magenta_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.MAGENTA_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.MAGENTA_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> PINK_GLAZED_GLASS_SLAB = BLOCKS.register(
                        "pink_glazed_glass_slab",
                        () -> new ModSlabBlock(BlocksPart01.PINK_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.PINK_GLAZED_GLASS.get())));
        public static final RegistryObject<Block> PINK_GLAZED_GLASS_STAIRS = BLOCKS.register(
                        "pink_glazed_glass_stairs",
                        () -> new ModStairBlock(
                                        BlocksPart01.PINK_GLAZED_GLASS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.PINK_GLAZED_GLASS.get())));

        public static final RegistryObject<Block> OAK_LOG_SLAB = BLOCKS.register("oak_log_slab",
                        () -> new LogSlabBlock(Blocks.OAK_LOG,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_OAK_LOG_SLAB = BLOCKS.register("stripped_oak_log_slab",
                        () -> new LogSlabBlock(Blocks.STRIPPED_OAK_LOG,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> SPRUCE_LOG_SLAB = BLOCKS.register("spruce_log_slab",
                        () -> new LogSlabBlock(Blocks.SPRUCE_LOG,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_SPRUCE_LOG_SLAB = BLOCKS.register("stripped_spruce_log_slab",
                        () -> new LogSlabBlock(Blocks.STRIPPED_SPRUCE_LOG,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> BIRCH_LOG_SLAB = BLOCKS.register("birch_log_slab",
                        () -> new LogSlabBlock(Blocks.BIRCH_LOG,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_BIRCH_LOG_SLAB = BLOCKS.register("stripped_birch_log_slab",
                        () -> new LogSlabBlock(Blocks.STRIPPED_BIRCH_LOG,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> JUNGLE_LOG_SLAB = BLOCKS.register("jungle_log_slab",
                        () -> new LogSlabBlock(Blocks.JUNGLE_LOG,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.DIRT).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_JUNGLE_LOG_SLAB = BLOCKS.register("stripped_jungle_log_slab",
                        () -> new LogSlabBlock(Blocks.STRIPPED_JUNGLE_LOG,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.DIRT).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ACACIA_LOG_SLAB = BLOCKS.register("acacia_log_slab",
                        () -> new LogSlabBlock(Blocks.ACACIA_LOG,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                                                        .strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_ACACIA_LOG_SLAB = BLOCKS.register("stripped_acacia_log_slab",
                        () -> new LogSlabBlock(Blocks.STRIPPED_ACACIA_LOG,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                                                        .strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> DARK_OAK_LOG_SLAB = BLOCKS.register("dark_oak_log_slab",
                        () -> new LogSlabBlock(Blocks.DARK_OAK_LOG,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                                                        .strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_DARK_OAK_LOG_SLAB = BLOCKS
                        .register("stripped_dark_oak_log_slab",
                                        () -> new LogSlabBlock(Blocks.STRIPPED_DARK_OAK_LOG, BlockBehaviour.Properties
                                                        .of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> CRIMSON_STEM_SLAB = BLOCKS.register("crimson_stem_slab",
                        () -> new LogSlabBlock(Blocks.CRIMSON_STEM,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(2.0f)
                                                        .sound(SoundType.STEM)));
        public static final RegistryObject<Block> STRIPPED_CRIMSON_STEM_SLAB = BLOCKS
                        .register("stripped_crimson_stem_slab",
                                        () -> new LogSlabBlock(Blocks.STRIPPED_CRIMSON_STEM, BlockBehaviour.Properties
                                                        .of(Material.WOOD, MaterialColor.COLOR_RED).strength(2.0f)
                                                        .sound(SoundType.STEM)));
        public static final RegistryObject<Block> WARPED_STEM_SLAB = BLOCKS.register("warped_stem_slab",
                        () -> new LogSlabBlock(Blocks.WARPED_STEM,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN)
                                                        .strength(2.0f)
                                                        .sound(SoundType.STEM)));
        public static final RegistryObject<Block> STRIPPED_WARPED_STEM_SLAB = BLOCKS
                        .register("stripped_warped_stem_slab",
                                        () -> new LogSlabBlock(Blocks.STRIPPED_WARPED_STEM, BlockBehaviour.Properties
                                                        .of(Material.WOOD, MaterialColor.COLOR_CYAN).strength(2.0f)
                                                        .sound(SoundType.STEM)));
        public static final RegistryObject<Block> MANGROVE_LOG_SLAB = BLOCKS.register("mangrove_log_slab",
                        () -> new LogSlabBlock(BlocksPart04.MANGROVE_LOG.get(),
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_LOG_STAIRS = BLOCKS.register("mangrove_log_stairs",
                        () -> new ModStairBlock(BlocksPart04.MANGROVE_LOG.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart04.MANGROVE_LOG.get())));
        public static final RegistryObject<Block> STRIPPED_MANGROVE_LOG_SLAB = BLOCKS
                        .register("stripped_mangrove_log_slab",
                                        () -> new LogSlabBlock(BlocksPart04.STRIPPED_MANGROVE_LOG.get(), BlockBehaviour.Properties
                                                        .of(Material.WOOD, MaterialColor.COLOR_RED).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_MANGROVE_LOG_STAIRS = BLOCKS.register(
                        "stripped_mangrove_log_stairs",
                        () -> new ModStairBlock(BlocksPart04.STRIPPED_MANGROVE_LOG.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(BlocksPart04.STRIPPED_MANGROVE_LOG.get())));

        public static final RegistryObject<Block> ASHPEN_LOG = BLOCKS.register("ashpen_log",
                        () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));
        public static final RegistryObject<Block> ASHPEN_WOOD = BLOCKS.register("ashpen_wood",
                        () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));
        public static final RegistryObject<Block> ASHPEN_LOG_SLAB = BLOCKS.register("ashpen_log_slab",
                        () -> new LogSlabBlock(ASHPEN_LOG.get(),
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ASHPEN_LOG_STAIRS = BLOCKS.register("ashpen_log_stairs",
                        () -> new ModStairBlock(ASHPEN_LOG.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(ASHPEN_LOG.get())));
        public static final RegistryObject<Block> ASHPEN_WOOD_SLAB = BLOCKS.register("ashpen_wood_slab",
                        () -> new LogSlabBlock(ASHPEN_WOOD.get(),
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ASHPEN_WOOD_STAIRS = BLOCKS.register("ashpen_wood_stairs",
                        () -> new ModStairBlock(ASHPEN_WOOD.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(ASHPEN_WOOD.get())));
        public static final RegistryObject<Block> STRIPPED_ASHPEN_LOG = BLOCKS.register("stripped_ashpen_log",
                        () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)));
        public static final RegistryObject<Block> STRIPPED_ASHPEN_WOOD = BLOCKS.register("stripped_ashpen_wood",
                        () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
        public static final RegistryObject<Block> STRIPPED_ASHPEN_LOG_SLAB = BLOCKS.register("stripped_ashpen_log_slab",
                        () -> new LogSlabBlock(STRIPPED_ASHPEN_LOG.get(),
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_ASHPEN_LOG_STAIRS = BLOCKS.register("stripped_ashpen_log_stairs",
                        () -> new ModStairBlock(STRIPPED_ASHPEN_LOG.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(STRIPPED_ASHPEN_LOG.get())));
        public static final RegistryObject<Block> STRIPPED_ASHPEN_WOOD_SLAB = BLOCKS.register("stripped_ashpen_wood_slab",
                        () -> new LogSlabBlock(STRIPPED_ASHPEN_WOOD.get(),
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_ASHPEN_WOOD_STAIRS = BLOCKS.register("stripped_ashpen_wood_stairs",
                        () -> new ModStairBlock(STRIPPED_ASHPEN_WOOD.get().defaultBlockState(),
                                        BlockBehaviour.Properties.copy(STRIPPED_ASHPEN_WOOD.get())));

        public static final RegistryObject<Block> BIT_POLISHED_TUFF_VERTICAL_SLAB = BLOCKS.register(
                        "bit_polished_tuff_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BIT_POLISHED_TUFF.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BIT_POLISHED_TUFF_SLAB.get())));
        public static final RegistryObject<Block> BIT_TUFF_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "bit_tuff_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BIT_TUFF_BRICKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BIT_TUFF_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> PODZOL_VERTICAL_SLAB = BLOCKS.register("podzol_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart01.PODZOL_SLAB.get())));
        public static final RegistryObject<Block> DIRT_VERTICAL_SLAB = BLOCKS.register("dirt_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart01.DIRT_SLAB.get())));
        public static final RegistryObject<Block> MYCELIUM_VERTICAL_SLAB = BLOCKS.register("mycelium_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart01.MYCELIUM_SLAB.get())));
        public static final RegistryObject<Block> MUD_VERTICAL_SLAB = BLOCKS.register("mud_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.MUD.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.MUD_SLAB.get())));
        public static final RegistryObject<Block> BLACK_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "black_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BLACK_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BLACK_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> BLUE_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "blue_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BLUE_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BLUE_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> GREEN_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "green_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.GREEN_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.GREEN_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> ORANGE_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "orange_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.ORANGE_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.ORANGE_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> PINK_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "pink_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.PINK_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.PINK_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> RED_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "red_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.RED_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.RED_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> WHITE_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "white_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.WHITE_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.WHITE_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> YELLOW_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "yellow_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.YELLOW_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.YELLOW_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> BLACK_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "black_smooth_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BLACK_SMOOTH_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BLACK_SMOOTH_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> BLUE_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "blue_smooth_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BLUE_SMOOTH_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.BLUE_SMOOTH_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> GREEN_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "green_smooth_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.GREEN_SMOOTH_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.GREEN_SMOOTH_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> ORANGE_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "orange_smooth_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.ORANGE_SMOOTH_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.ORANGE_SMOOTH_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> PINK_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "pink_smooth_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.PINK_SMOOTH_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.PINK_SMOOTH_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> RED_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "red_smooth_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.RED_SMOOTH_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.RED_SMOOTH_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> WHITE_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "white_smooth_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.WHITE_SMOOTH_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.WHITE_SMOOTH_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> YELLOW_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
                        "yellow_smooth_sandstone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.YELLOW_SMOOTH_SANDSTONE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart01.YELLOW_SMOOTH_SANDSTONE_SLAB.get())));
        public static final RegistryObject<Block> BLACK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "black_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BLACK_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.BLACK_TILES_SLAB.get())));
        public static final RegistryObject<Block> BLUE_TILES_VERTICAL_SLAB = BLOCKS.register("blue_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BLUE_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.BLUE_TILES_SLAB.get())));
        public static final RegistryObject<Block> BROWN_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "brown_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BROWN_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.BROWN_TILES_SLAB.get())));
        public static final RegistryObject<Block> CYAN_TILES_VERTICAL_SLAB = BLOCKS.register("cyan_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.CYAN_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.CYAN_TILES_SLAB.get())));
        public static final RegistryObject<Block> GRAY_TILES_VERTICAL_SLAB = BLOCKS.register("gray_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.GRAY_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.GRAY_TILES_SLAB.get())));
        public static final RegistryObject<Block> GREEN_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "green_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.GREEN_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.GREEN_TILES_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_BLUE_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "light_blue_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.LIGHT_BLUE_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.LIGHT_BLUE_TILES_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_GRAY_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "light_gray_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.LIGHT_GRAY_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.LIGHT_GRAY_TILES_SLAB.get())));
        public static final RegistryObject<Block> LIME_TILES_VERTICAL_SLAB = BLOCKS.register("lime_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.LIME_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.LIME_TILES_SLAB.get())));
        public static final RegistryObject<Block> MAGENTA_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "magenta_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.MAGENTA_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.MAGENTA_TILES_SLAB.get())));
        public static final RegistryObject<Block> ORANGE_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "orange_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.ORANGE_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.ORANGE_TILES_SLAB.get())));
        public static final RegistryObject<Block> PINK_TILES_VERTICAL_SLAB = BLOCKS.register("pink_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.PINK_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.PINK_TILES_SLAB.get())));
        public static final RegistryObject<Block> PURPLE_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "purple_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.PURPLE_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.PURPLE_TILES_SLAB.get())));
        public static final RegistryObject<Block> RED_TILES_VERTICAL_SLAB = BLOCKS.register("red_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.RED_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.RED_TILES_SLAB.get())));
        public static final RegistryObject<Block> WHITE_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "white_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.WHITE_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.WHITE_TILES_SLAB.get())));
        public static final RegistryObject<Block> YELLOW_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "yellow_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.YELLOW_TILES.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.YELLOW_TILES_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_BASALT_VERTICAL_SLAB = BLOCKS.register(
                        "polished_basalt_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.POLISHED_BASALT,
                                        BlockBehaviour.Properties.copy(BlocksPart02.POLISHED_BASALT_SLAB.get())));
        public static final RegistryObject<Block> DRIPSTONE_BLOCK_VERTICAL_SLAB = BLOCKS.register(
                        "dripstone_block_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.DRIPSTONE_BLOCK,
                                        BlockBehaviour.Properties.copy(BlocksPart02.DRIPSTONE_BLOCK_SLAB.get())));
        public static final RegistryObject<Block> END_STONE_VERTICAL_SLAB = BLOCKS.register("end_stone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.END_STONE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.END_STONE_SLAB.get())));
        public static final RegistryObject<Block> QUARTZ_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "quartz_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.QUARTZ_BRICKS,
                                        BlockBehaviour.Properties.copy(BlocksPart02.QUARTZ_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> QUARTZ_PILLAR_VERTICAL_SLAB = BLOCKS.register(
                        "quartz_pillar_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.QUARTZ_PILLAR,
                                        BlockBehaviour.Properties.copy(BlocksPart02.QUARTZ_PILLAR_SLAB.get())));
        public static final RegistryObject<Block> CALCITE_VERTICAL_SLAB = BLOCKS.register("calcite_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.CALCITE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.CALCITE_SLAB.get())));
        public static final RegistryObject<Block> BONE_BLOCK_VERTICAL_SLAB = BLOCKS.register(
                        "bone_block_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.BONE_BLOCK,
                                        BlockBehaviour.Properties.copy(BlocksPart02.BONE_BLOCK_SLAB.get())));
        public static final RegistryObject<Block> STACKED_BONE_VERTICAL_SLAB = BLOCKS.register(
                        "stacked_bone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart02.STACKED_BONE_BLOCK.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.STACKED_BONE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_BONE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_bone_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart02.POLISHED_BONE_BLOCK.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.POLISHED_BONE_SLAB.get())));
        public static final RegistryObject<Block> BONE_BRICK_VERTICAL_SLAB = BLOCKS.register(
                        "bone_brick_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart02.BONE_BRICKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.BONE_BRICK_SLAB.get())));
        public static final RegistryObject<Block> OBSIDIAN_VERTICAL_SLAB = BLOCKS.register("obsidian_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.OBSIDIAN,
                                        BlockBehaviour.Properties.copy(BlocksPart02.OBSIDIAN_SLAB.get())));
        public static final RegistryObject<Block> SMOOTH_BASALT_VERTICAL_SLAB = BLOCKS.register(
                        "smooth_basalt_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.SMOOTH_BASALT,
                                        BlockBehaviour.Properties.copy(BlocksPart02.SMOOTH_BASALT_SLAB.get())));
        public static final RegistryObject<Block> MOSS_BLOCK_VERTICAL_SLAB = BLOCKS.register("moss_block_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart02.MOSS_BLOCK_SLAB.get())));
        public static final RegistryObject<Block> AMETHYST_BLOCK_VERTICAL_SLAB = BLOCKS.register(
                        "amethyst_block_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart02.AMETHYST_BLOCK_SLAB.get())));
        public static final RegistryObject<Block> MOSSY_CALCITE_VERTICAL_SLAB = BLOCKS.register(
                        "mossy_calcite_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart02.MOSSY_CALCITE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.MOSSY_CALCITE_SLAB.get())));
        public static final RegistryObject<Block> BIT_COPPER_BLOCK_VERTICAL_SLAB = BLOCKS.register(
                        "bit_copper_block_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BIT_COPPER_BLOCK.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.BIT_COPPER_BLOCK_SLAB.get())));
        public static final RegistryObject<Block> BIT_EXPOSED_COPPER_BLOCK_VERTICAL_SLAB = BLOCKS.register(
                        "bit_exposed_copper_block_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BIT_EXPOSED_COPPER_BLOCK.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.BIT_EXPOSED_COPPER_BLOCK_SLAB.get())));
        public static final RegistryObject<Block> BIT_WEATHERED_COPPER_BLOCK_VERTICAL_SLAB = BLOCKS.register(
                        "bit_weathered_copper_block_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BIT_WEATHERED_COPPER_BLOCK.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.BIT_WEATHERED_COPPER_BLOCK_SLAB.get())));
        public static final RegistryObject<Block> BIT_OXIDIZED_COPPER_BLOCK_VERTICAL_SLAB = BLOCKS.register(
                        "bit_oxidized_copper_block_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BIT_OXIDIZED_COPPER_BLOCK.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.BIT_OXIDIZED_COPPER_BLOCK_SLAB.get())));
        public static final RegistryObject<Block> BIT_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
                        "bit_cut_copper_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BIT_CUT_COPPER.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.BIT_CUT_COPPER_SLAB.get())));
        public static final RegistryObject<Block> BIT_EXPOSED_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
                        "bit_exposed_cut_copper_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BIT_EXPOSED_CUT_COPPER.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.BIT_EXPOSED_CUT_COPPER_SLAB.get())));
        public static final RegistryObject<Block> BIT_WEATHERED_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
                        "bit_weathered_cut_copper_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BIT_WEATHERED_CUT_COPPER.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.BIT_WEATHERED_CUT_COPPER_SLAB.get())));
        public static final RegistryObject<Block> BIT_OXIDIZED_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
                        "bit_oxidized_cut_copper_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BIT_OXIDIZED_CUT_COPPER.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart02.BIT_OXIDIZED_CUT_COPPER_SLAB.get())));
        public static final RegistryObject<Block> BLACK_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "black_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.BLACK_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.BLACK_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> BLUE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "blue_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.BLUE_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.BLUE_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> BROWN_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "brown_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.BROWN_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.BROWN_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> CYAN_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "cyan_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.CYAN_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.CYAN_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> GRAY_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "gray_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.GRAY_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.GRAY_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> GREEN_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "green_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.GREEN_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.GREEN_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_BLUE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "light_blue_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.LIGHT_BLUE_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.LIGHT_BLUE_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_GRAY_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "light_gray_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.LIGHT_GRAY_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.LIGHT_GRAY_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> LIME_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "lime_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.LIME_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.LIME_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> MAGENTA_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "magenta_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.MAGENTA_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.MAGENTA_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> ORANGE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "orange_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.ORANGE_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.ORANGE_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> PINK_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "pink_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.PINK_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.PINK_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> PURPLE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "purple_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.PURPLE_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.PURPLE_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> RED_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "red_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.RED_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.RED_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> WHITE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "white_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.WHITE_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.WHITE_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> YELLOW_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "yellow_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.YELLOW_CONCRETE,
                                        BlockBehaviour.Properties.copy(BlocksPart02.YELLOW_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> HAY_BALE_VERTICAL_SLAB = BLOCKS.register("hay_bale_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart03.HAY_BALE_SLAB.get())));
        public static final RegistryObject<Block> BAMBOO_BLOCK_VERTICAL_SLAB = BLOCKS.register(
                        "bamboo_block_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.BAMBOO_BLOCK.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.BAMBOO_BLOCK_SLAB.get())));
        public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_VERTICAL_SLAB = BLOCKS.register(
                        "stripped_bamboo_block_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart03.STRIPPED_BAMBOO_BLOCK_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_WHITE_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_white_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.ASHPEN_WHITE_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.ASHPEN_WHITE_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_BLACK_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_black_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.BLACK_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.BLACK_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_BLUE_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_blue_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.BLUE_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.BLUE_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_BROWN_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_brown_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.BROWN_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.BROWN_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_CYAN_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_cyan_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.CYAN_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.CYAN_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_GRAY_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_gray_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.GRAY_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.GRAY_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_GREEN_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_green_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.GREEN_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.GREEN_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_LIGHT_BLUE_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_light_blue_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.LIGHT_BLUE_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.LIGHT_BLUE_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_LIGHT_GRAY_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_light_gray_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.LIGHT_GRAY_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.LIGHT_GRAY_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_LIME_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_lime_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.LIME_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.LIME_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_MAGENTA_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_magenta_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.MAGENTA_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.MAGENTA_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_ORANGE_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_orange_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.ORANGE_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.ORANGE_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_PINK_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_pink_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.PINK_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.PINK_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_PURPLE_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_purple_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.PURPLE_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.PURPLE_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_RED_VERTICAL_SLAB = BLOCKS.register("ashpen_red_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.RED_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.RED_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> ASHPEN_YELLOW_VERTICAL_SLAB = BLOCKS.register(
                        "ashpen_yellow_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart03.YELLOW_ASHPEN_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart03.YELLOW_ASHPEN_SLAB.get())));
        public static final RegistryObject<Block> SNOW_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "snow_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart04.SNOW_BRICKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart04.SNOW_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> SNOW_VERTICAL_SLAB = BLOCKS.register("snow_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.SNOW_BLOCK,
                                        BlockBehaviour.Properties.copy(BlocksPart04.SNOW_SLAB.get())));
        public static final RegistryObject<Block> SNOWY_GRASS_BLOCK_VERTICAL_SLAB = BLOCKS.register(
                        "snowy_grass_block_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart04.SNOWY_GRASS_BLOCK.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart04.SNOWY_GRASS_BLOCK_SLAB.get())));
        public static final RegistryObject<Block> MANGROVE_VERTICAL_SLAB = BLOCKS.register("mangrove_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart04.MANGROVE_PLANKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart04.MANGROVE_SLAB.get())));
        public static final RegistryObject<Block> STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
                        "steel_block_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart04.STEEL_BLOCK.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.STEEL_BLOCK_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
                        "polished_steel_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.POLISHED_STEEL.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.POLISHED_STEEL_SLAB.get())));
        public static final RegistryObject<Block> PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
                        "pressed_steel_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.PRESSED_STEEL.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.PRESSED_STEEL_SLAB.get())));
        public static final RegistryObject<Block> CUT_STEEL_VERTICAL_SLAB = BLOCKS.register("cut_steel_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.CUT_STEEL.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart05.CUT_STEEL_SLAB.get())));
        public static final RegistryObject<Block> CAUTION_BLACK_VERTICAL_SLAB = BLOCKS.register(
                        "caution_black_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.CAUTION_BLACK_SLAB.get())));
        public static final RegistryObject<Block> CAUTION_BLUE_VERTICAL_SLAB = BLOCKS.register(
                        "caution_blue_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.CAUTION_BLUE_SLAB.get())));
        public static final RegistryObject<Block> CAUTION_FACTORY_VERTICAL_SLAB = BLOCKS.register(
                        "caution_factory_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.CAUTION_FACTORY_SLAB.get())));
        public static final RegistryObject<Block> CAUTION_RED_VERTICAL_SLAB = BLOCKS.register(
                        "caution_red_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.CAUTION_RED_SLAB.get())));
        public static final RegistryObject<Block> FRAMED_CAUTION_VERTICAL_SLAB = BLOCKS.register(
                        "framed_caution_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.FRAMED_CAUTION_SLAB.get())));
        public static final RegistryObject<Block> CAUTION_WHITE_VERTICAL_SLAB = BLOCKS.register(
                        "caution_white_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.CAUTION_WHITE_SLAB.get())));
        public static final RegistryObject<Block> CAUTION_LIME_VERTICAL_SLAB = BLOCKS.register(
                        "caution_lime_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.CAUTION_LIME_SLAB.get())));
        public static final RegistryObject<Block> CAUTION_PINK_VERTICAL_SLAB = BLOCKS.register(
                        "caution_pink_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.CAUTION_PINK_SLAB.get())));
        public static final RegistryObject<Block> CAUTION_YELLOW_VERTICAL_SLAB = BLOCKS.register(
                        "caution_yellow_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.CAUTION_YELLOW_SLAB.get())));
        public static final RegistryObject<Block> CAUTION_CANDY_VERTICAL_SLAB = BLOCKS.register(
                        "caution_candy_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.CAUTION_CANDY_SLAB.get())));
        public static final RegistryObject<Block> CAUTION_COTTONCANDY_VERTICAL_SLAB = BLOCKS.register(
                        "caution_cottoncandy_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.CAUTION_COTTONCANDY_SLAB.get())));
        public static final RegistryObject<Block> CAUTION_MINTCANDY_VERTICAL_SLAB = BLOCKS.register(
                        "caution_mintcandy_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.CAUTION_MINTCANDY_SLAB.get())));
        public static final RegistryObject<Block> CAUTION_CITRUSCANDY_VERTICAL_SLAB = BLOCKS.register(
                        "caution_citruscandy_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.CAUTION_CITRUSCANDY_SLAB.get())));
        public static final RegistryObject<Block> WHITE_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "white_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.WHITE_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "light_gray_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.LIGHT_GRAY_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> GRAY_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "gray_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.GRAY_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> BLACK_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "black_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.BLACK_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> BROWN_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "brown_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.BROWN_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> RED_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "red_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart06.RED_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> ORANGE_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "orange_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart06.ORANGE_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> YELLOW_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "yellow_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart06.YELLOW_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> LIME_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "lime_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart06.LIME_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> GREEN_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "green_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart06.GREEN_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> CYAN_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "cyan_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart06.CYAN_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "light_blue_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart06.LIGHT_BLUE_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> BLUE_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "blue_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart06.BLUE_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> PURPLE_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "purple_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart06.PURPLE_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> MAGENTA_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "magenta_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart06.MAGENTA_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> PINK_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
                        "pink_stained_bricks_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart06.PINK_STAINED_BRICKS_SLAB.get())));
        public static final RegistryObject<Block> WHITE_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "white_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.WHITE_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "light_gray_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.LIGHT_GRAY_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "gray_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.GRAY_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> BLACK_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "black_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.BLACK_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> BROWN_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "brown_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.BROWN_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> RED_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "red_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.RED_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> ORANGE_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "orange_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.ORANGE_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> YELLOW_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "yellow_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.YELLOW_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> LIME_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "lime_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.LIME_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> GREEN_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "green_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.GREEN_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> CYAN_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "cyan_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.CYAN_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "light_blue_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.LIGHT_BLUE_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "blue_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.BLUE_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> PURPLE_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "purple_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.PURPLE_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> MAGENTA_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "magenta_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.MAGENTA_STAINED_BRICK_TILES_SLAB.get())));
        public static final RegistryObject<Block> PINK_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
                        "pink_stained_brick_tiles_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.AIR,
                                        BlockBehaviour.Properties.copy(BlocksPart05.PINK_STAINED_BRICK_TILES_SLAB.get())));

        public static final RegistryObject<Block> POLISHED_WHITE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_white_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_WHITE_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_WHITE_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_ORANGE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_orange_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_ORANGE_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_ORANGE_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_MAGENTA_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_magenta_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_MAGENTA_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_MAGENTA_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_LIGHT_BLUE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_light_blue_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_LIGHT_BLUE_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_LIGHT_BLUE_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_YELLOW_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_yellow_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_YELLOW_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_YELLOW_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_LIME_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_lime_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_LIME_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_LIME_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_PINK_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_pink_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_PINK_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_PINK_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_GRAY_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_gray_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_GRAY_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_GRAY_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_LIGHT_GRAY_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_light_gray_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_LIGHT_GRAY_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_LIGHT_GRAY_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_CYAN_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_cyan_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_CYAN_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_CYAN_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_PURPLE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_purple_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_PURPLE_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_PURPLE_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_BLUE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_blue_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_BLUE_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_BLUE_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_BROWN_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_brown_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_BROWN_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_BROWN_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_GREEN_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_green_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_GREEN_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_GREEN_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_RED_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_red_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_RED_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_RED_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> POLISHED_BLACK_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
                        "polished_black_concrete_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.POLISHED_BLACK_CONCRETE.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.POLISHED_BLACK_CONCRETE_SLAB.get())));
        public static final RegistryObject<Block> BLACK_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
                        "black_factory_mesh_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.BLACK_FACTORY_MESH.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.BLACK_FACTORY_MESH_SLAB.get())));
        public static final RegistryObject<Block> BLUE_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
                        "blue_factory_mesh_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.BLUE_FACTORY_MESH.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.BLUE_FACTORY_MESH_SLAB.get())));
        public static final RegistryObject<Block> BROWN_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
                        "brown_factory_mesh_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.BROWN_FACTORY_MESH.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.BROWN_FACTORY_MESH_SLAB.get())));
        public static final RegistryObject<Block> GRAY_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
                        "gray_factory_mesh_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.GRAY_FACTORY_MESH.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.GRAY_FACTORY_MESH_SLAB.get())));
        public static final RegistryObject<Block> GREEN_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
                        "green_factory_mesh_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.GREEN_FACTORY_MESH.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.GREEN_FACTORY_MESH_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_GRAY_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
                        "light_gray_factory_mesh_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.LIGHT_GRAY_FACTORY_MESH.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.LIGHT_GRAY_FACTORY_MESH_SLAB.get())));
        public static final RegistryObject<Block> LIME_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
                        "lime_factory_mesh_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.LIME_FACTORY_MESH.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.LIME_FACTORY_MESH_SLAB.get())));
        public static final RegistryObject<Block> ORANGE_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
                        "orange_factory_mesh_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.ORANGE_FACTORY_MESH.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.ORANGE_FACTORY_MESH_SLAB.get())));
        public static final RegistryObject<Block> RED_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
                        "red_factory_mesh_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.RED_FACTORY_MESH.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.RED_FACTORY_MESH_SLAB.get())));
        public static final RegistryObject<Block> YELLOW_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
                        "yellow_factory_mesh_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.YELLOW_FACTORY_MESH.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.YELLOW_FACTORY_MESH_SLAB.get())));
        public static final RegistryObject<Block> PACKED_MUD_VERTICAL_SLAB = BLOCKS.register("packed_mud_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.PACKED_MUD.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.PACKED_MUD_SLAB.get())));
        public static final RegistryObject<Block> MUD_BRICK_VERTICAL_SLAB = BLOCKS.register("mud_brick_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart06.MUD_BRICKS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.MUD_BRICK_SLAB.get())));
        public static final RegistryObject<Block> GLASS_VERTICAL_SLAB = BLOCKS.register("glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.GLASS_SLAB.get())));
        public static final RegistryObject<Block> WHITE_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "white_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.WHITE_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.WHITE_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_GRAY_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "light_gray_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.LIGHT_GRAY_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.LIGHT_GRAY_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> GRAY_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "gray_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.GRAY_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.GRAY_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> BLACK_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "black_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.BLACK_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.BLACK_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> BROWN_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "brown_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.BROWN_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.BROWN_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> RED_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "red_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.RED_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.RED_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> ORANGE_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "orange_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.ORANGE_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.ORANGE_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> YELLOW_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "yellow_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.YELLOW_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.YELLOW_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> LIME_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "lime_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.LIME_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.LIME_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> GREEN_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "green_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.GREEN_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.GREEN_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> CYAN_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "cyan_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.CYAN_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.CYAN_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_BLUE_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "light_blue_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.LIGHT_BLUE_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.LIGHT_BLUE_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> BLUE_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "blue_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.BLUE_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.BLUE_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> PURPLE_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "purple_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.PURPLE_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.PURPLE_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> MAGENTA_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "magenta_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.MAGENTA_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.MAGENTA_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> PINK_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "pink_stained_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.PINK_STAINED_GLASS,
                                        BlockBehaviour.Properties.copy(BlocksPart06.PINK_STAINED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> WHITE_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "white_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.WHITE_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.WHITE_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_GRAY_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "light_gray_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.LIGHT_GRAY_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.LIGHT_GRAY_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> GRAY_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "gray_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.GRAY_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.GRAY_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> BLACK_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "black_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BLACK_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.BLACK_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> BROWN_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "brown_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BROWN_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.BROWN_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> RED_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "red_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.RED_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.RED_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> ORANGE_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "orange_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.ORANGE_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.ORANGE_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> YELLOW_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "yellow_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.YELLOW_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.YELLOW_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> LIME_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "lime_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.LIME_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.LIME_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> GREEN_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "green_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.GREEN_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.GREEN_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> CYAN_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "cyan_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.CYAN_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.CYAN_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_BLUE_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "light_blue_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.LIGHT_BLUE_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.LIGHT_BLUE_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> BLUE_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "blue_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BLUE_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.BLUE_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> PURPLE_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "purple_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.PURPLE_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.PURPLE_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> MAGENTA_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "magenta_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.MAGENTA_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.MAGENTA_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> PINK_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "pink_mosaic_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.PINK_MOSAIC_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.PINK_MOSAIC_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_WHITE_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_white_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_WHITE_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_WHITE_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_LIGHT_GRAY_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_light_gray_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_LIGHT_GRAY_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_LIGHT_GRAY_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_GRAY_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_gray_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_GRAY_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_GRAY_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_BLACK_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_black_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_BLACK_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_BLACK_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_BROWN_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_brown_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_BROWN_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_BROWN_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_RED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_red_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_RED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_RED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_ORANGE_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_orange_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_ORANGE_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_ORANGE_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_YELLOW_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_yellow_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_YELLOW_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_YELLOW_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_LIME_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_lime_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_LIME_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_LIME_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_GREEN_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_green_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_GREEN_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_GREEN_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_CYAN_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_cyan_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_CYAN_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_CYAN_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_LIGHT_BLUE_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_light_blue_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_LIGHT_BLUE_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_LIGHT_BLUE_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_BLUE_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_blue_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_BLUE_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_BLUE_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_PURPLE_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_purple_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_PURPLE_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_PURPLE_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_MAGENTA_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_magenta_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_MAGENTA_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_MAGENTA_GLASS_SLAB.get())));
        public static final RegistryObject<Block> FACTORY_PINK_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "factory_pink_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart05.FACTORY_PINK_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.FACTORY_PINK_GLASS_SLAB.get())));
        public static final RegistryObject<Block> WHITE_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "white_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.WHITE_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.WHITE_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_GRAY_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "light_gray_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.LIGHT_GRAY_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.LIGHT_GRAY_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> GRAY_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "gray_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.GRAY_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.GRAY_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> BLACK_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "black_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BLACK_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.BLACK_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> BROWN_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "brown_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BROWN_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BlocksPart06.BROWN_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> RED_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "red_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.RED_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(RED_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> ORANGE_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "orange_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.ORANGE_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(ORANGE_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> YELLOW_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "yellow_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.YELLOW_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(YELLOW_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> LIME_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "lime_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.LIME_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(LIME_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> GREEN_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "green_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.GREEN_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(GREEN_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> CYAN_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "cyan_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.CYAN_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(CYAN_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> LIGHT_BLUE_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "light_blue_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.LIGHT_BLUE_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(LIGHT_BLUE_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> BLUE_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "blue_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.BLUE_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(BLUE_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> PURPLE_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "purple_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.PURPLE_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(PURPLE_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> MAGENTA_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "magenta_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.MAGENTA_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(MAGENTA_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> PINK_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
                        "pink_glazed_glass_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart01.PINK_GLAZED_GLASS.get(),
                                        BlockBehaviour.Properties.copy(PINK_GLAZED_GLASS_SLAB.get())));
        public static final RegistryObject<Block> OAK_LOG_VERTICAL_SLAB = BLOCKS.register("oak_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.OAK_LOG,
                                        BlockBehaviour.Properties.copy(OAK_LOG_SLAB.get())));
        public static final RegistryObject<Block> STRIPPED_OAK_LOG_VERTICAL_SLAB = BLOCKS.register(
                        "stripped_oak_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.STRIPPED_OAK_LOG,
                                        BlockBehaviour.Properties.copy(STRIPPED_OAK_LOG_SLAB.get())));
        public static final RegistryObject<Block> SPRUCE_LOG_VERTICAL_SLAB = BLOCKS.register("spruce_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.SPRUCE_LOG,
                                        BlockBehaviour.Properties.copy(SPRUCE_LOG_SLAB.get())));
        public static final RegistryObject<Block> STRIPPED_SPRUCE_LOG_VERTICAL_SLAB = BLOCKS.register(
                        "stripped_spruce_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.STRIPPED_SPRUCE_LOG,
                                        BlockBehaviour.Properties.copy(STRIPPED_SPRUCE_LOG_SLAB.get())));
        public static final RegistryObject<Block> BIRCH_LOG_VERTICAL_SLAB = BLOCKS.register("birch_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.BIRCH_LOG,
                                        BlockBehaviour.Properties.copy(BIRCH_LOG_SLAB.get())));
        public static final RegistryObject<Block> STRIPPED_BIRCH_LOG_VERTICAL_SLAB = BLOCKS.register(
                        "stripped_birch_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.STRIPPED_BIRCH_LOG,
                                        BlockBehaviour.Properties.copy(STRIPPED_BIRCH_LOG_SLAB.get())));
        public static final RegistryObject<Block> JUNGLE_LOG_VERTICAL_SLAB = BLOCKS.register("jungle_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.JUNGLE_LOG,
                                        BlockBehaviour.Properties.copy(JUNGLE_LOG_SLAB.get())));
        public static final RegistryObject<Block> STRIPPED_JUNGLE_LOG_VERTICAL_SLAB = BLOCKS.register(
                        "stripped_jungle_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.STRIPPED_JUNGLE_LOG,
                                        BlockBehaviour.Properties.copy(STRIPPED_JUNGLE_LOG_SLAB.get())));
        public static final RegistryObject<Block> ACACIA_LOG_VERTICAL_SLAB = BLOCKS.register("acacia_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.ACACIA_LOG,
                                        BlockBehaviour.Properties.copy(ACACIA_LOG_SLAB.get())));
        public static final RegistryObject<Block> STRIPPED_ACACIA_LOG_VERTICAL_SLAB = BLOCKS.register(
                        "stripped_acacia_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.STRIPPED_ACACIA_LOG,
                                        BlockBehaviour.Properties.copy(STRIPPED_ACACIA_LOG_SLAB.get())));
        public static final RegistryObject<Block> DARK_OAK_LOG_VERTICAL_SLAB = BLOCKS.register(
                        "dark_oak_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.DARK_OAK_LOG,
                                        BlockBehaviour.Properties.copy(DARK_OAK_LOG_SLAB.get())));
        public static final RegistryObject<Block> STRIPPED_DARK_OAK_LOG_VERTICAL_SLAB = BLOCKS.register(
                        "stripped_dark_oak_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.STRIPPED_DARK_OAK_LOG,
                                        BlockBehaviour.Properties.copy(STRIPPED_DARK_OAK_LOG_SLAB.get())));
        public static final RegistryObject<Block> CRIMSON_STEM_VERTICAL_SLAB = BLOCKS.register(
                        "crimson_stem_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.CRIMSON_STEM,
                                        BlockBehaviour.Properties.copy(CRIMSON_STEM_SLAB.get())));
        public static final RegistryObject<Block> STRIPPED_CRIMSON_STEM_VERTICAL_SLAB = BLOCKS.register(
                        "stripped_crimson_stem_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.STRIPPED_CRIMSON_STEM,
                                        BlockBehaviour.Properties.copy(STRIPPED_CRIMSON_STEM_SLAB.get())));
        public static final RegistryObject<Block> WARPED_STEM_VERTICAL_SLAB = BLOCKS.register(
                        "warped_stem_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.WARPED_STEM,
                                        BlockBehaviour.Properties.copy(WARPED_STEM_SLAB.get())));
        public static final RegistryObject<Block> STRIPPED_WARPED_STEM_VERTICAL_SLAB = BLOCKS.register(
                        "stripped_warped_stem_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        Blocks.STRIPPED_WARPED_STEM,
                                        BlockBehaviour.Properties.copy(STRIPPED_WARPED_STEM_SLAB.get())));
        public static final RegistryObject<Block> MANGROVE_LOG_VERTICAL_SLAB = BLOCKS.register(
                        "mangrove_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart04.MANGROVE_LOG.get(),
                                        BlockBehaviour.Properties.copy(MANGROVE_LOG_SLAB.get())));
        public static final RegistryObject<Block> STRIPPED_MANGROVE_LOG_VERTICAL_SLAB = BLOCKS.register(
                        "stripped_mangrove_log_vertical_slab",
                        () -> new VerticalSlabBlock(
                                        BlocksPart04.STRIPPED_MANGROVE_LOG.get(),
                                        BlockBehaviour.Properties.copy(STRIPPED_MANGROVE_LOG_SLAB.get())));
}
