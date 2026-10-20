package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.block.building.LogSlabBlock;
import com.kingodogo.buildscape.block.building.ModSlabBlock;
import com.kingodogo.buildscape.block.building.ModStairBlock;
import com.kingodogo.buildscape.block.building.ModWallBlock;
import com.kingodogo.buildscape.block.decor.SoftFabricBlock;
import com.kingodogo.buildscape.block.decor.SpoolBlock;
import com.kingodogo.buildscape.block.building.VerticalSlabBlock;
import com.kingodogo.buildscape.feature.geyser.PotentSulfurBlock;
import com.kingodogo.buildscape.feature.geyser.SulfurSpikeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.blocks.BlocksRegister.BLOCKS;

/**
 * The blocks OAK_LOG_SLAB through CINNABAR_BRICKS_STAIRS of the registry, part 7 of 12, in registration order.
 * Named for its dominant families (stone, decor, wood); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link GlassBlocks#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class ShapedStoneBlocks {

    private ShapedStoneBlocks() {}

    /** Forces this class to load, which registers its blocks. */
    public static void init() {}

    static {
        GlassBlocks.init();
    }

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
            () -> new LogSlabBlock(DecorBlocks.MANGROVE_LOG.get(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> MANGROVE_LOG_STAIRS = BLOCKS.register("mangrove_log_stairs",
            () -> new ModStairBlock(DecorBlocks.MANGROVE_LOG.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(DecorBlocks.MANGROVE_LOG.get())));
    public static final RegistryObject<Block> STRIPPED_MANGROVE_LOG_SLAB = BLOCKS
            .register("stripped_mangrove_log_slab",
                    () -> new LogSlabBlock(DecorBlocks.STRIPPED_MANGROVE_LOG.get(), BlockBehaviour.Properties
                            .of(Material.WOOD, MaterialColor.COLOR_RED).strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_MANGROVE_LOG_STAIRS = BLOCKS.register(
            "stripped_mangrove_log_stairs",
            () -> new ModStairBlock(DecorBlocks.STRIPPED_MANGROVE_LOG.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(DecorBlocks.STRIPPED_MANGROVE_LOG.get())));

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
    public static final RegistryObject<Block> STRIPPED_ASHPEN_WOOD_STAIRS = BLOCKS.register(
            "stripped_ashpen_wood_stairs",
            () -> new ModStairBlock(STRIPPED_ASHPEN_WOOD.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(STRIPPED_ASHPEN_WOOD.get())));

    public static final RegistryObject<Block> BIT_POLISHED_TUFF_VERTICAL_SLAB = BLOCKS.register(
            "bit_polished_tuff_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BIT_POLISHED_TUFF.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.BIT_POLISHED_TUFF_SLAB.get())));
    public static final RegistryObject<Block> BIT_TUFF_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "bit_tuff_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BIT_TUFF_BRICKS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.BIT_TUFF_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> PODZOL_VERTICAL_SLAB = BLOCKS.register("podzol_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(SandstoneBlocks.PODZOL_SLAB.get())));
    public static final RegistryObject<Block> DIRT_VERTICAL_SLAB = BLOCKS.register("dirt_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(SandstoneBlocks.DIRT_SLAB.get())));
    public static final RegistryObject<Block> MYCELIUM_VERTICAL_SLAB = BLOCKS.register("mycelium_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(SandstoneBlocks.MYCELIUM_SLAB.get())));
    public static final RegistryObject<Block> MUD_VERTICAL_SLAB = BLOCKS.register("mud_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.MUD.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.MUD_SLAB.get())));
    public static final RegistryObject<Block> BLACK_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "black_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BLACK_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BLACK_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> BLUE_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "blue_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BLUE_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BLUE_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> GREEN_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "green_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.GREEN_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.GREEN_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "orange_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.ORANGE_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.ORANGE_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> PINK_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "pink_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.PINK_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.PINK_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> RED_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "red_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.RED_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.RED_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> WHITE_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "white_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.WHITE_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.WHITE_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "yellow_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.YELLOW_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.YELLOW_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> BLACK_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "black_smooth_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.BLACK_SMOOTH_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BLACK_SMOOTH_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> BLUE_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "blue_smooth_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.BLUE_SMOOTH_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BLUE_SMOOTH_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> GREEN_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "green_smooth_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.GREEN_SMOOTH_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.GREEN_SMOOTH_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "orange_smooth_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.ORANGE_SMOOTH_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.ORANGE_SMOOTH_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> PINK_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "pink_smooth_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.PINK_SMOOTH_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.PINK_SMOOTH_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> RED_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "red_smooth_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.RED_SMOOTH_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.RED_SMOOTH_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> WHITE_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "white_smooth_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.WHITE_SMOOTH_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.WHITE_SMOOTH_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_SMOOTH_SANDSTONE_VERTICAL_SLAB = BLOCKS.register(
            "yellow_smooth_sandstone_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.YELLOW_SMOOTH_SANDSTONE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.YELLOW_SMOOTH_SANDSTONE_SLAB.get())));
    public static final RegistryObject<Block> BLACK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "black_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BLACK_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BLACK_TILES_SLAB.get())));
    public static final RegistryObject<Block> BLUE_TILES_VERTICAL_SLAB = BLOCKS.register("blue_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BLUE_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BLUE_TILES_SLAB.get())));
    public static final RegistryObject<Block> BROWN_TILES_VERTICAL_SLAB = BLOCKS.register(
            "brown_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BROWN_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BROWN_TILES_SLAB.get())));
    public static final RegistryObject<Block> CYAN_TILES_VERTICAL_SLAB = BLOCKS.register("cyan_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.CYAN_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.CYAN_TILES_SLAB.get())));
    public static final RegistryObject<Block> GRAY_TILES_VERTICAL_SLAB = BLOCKS.register("gray_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.GRAY_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.GRAY_TILES_SLAB.get())));
    public static final RegistryObject<Block> GREEN_TILES_VERTICAL_SLAB = BLOCKS.register(
            "green_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.GREEN_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.GREEN_TILES_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_TILES_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.LIGHT_BLUE_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.LIGHT_BLUE_TILES_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_TILES_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.LIGHT_GRAY_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.LIGHT_GRAY_TILES_SLAB.get())));
    public static final RegistryObject<Block> LIME_TILES_VERTICAL_SLAB = BLOCKS.register("lime_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.LIME_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.LIME_TILES_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_TILES_VERTICAL_SLAB = BLOCKS.register(
            "magenta_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.MAGENTA_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.MAGENTA_TILES_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_TILES_VERTICAL_SLAB = BLOCKS.register(
            "orange_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.ORANGE_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.ORANGE_TILES_SLAB.get())));
    public static final RegistryObject<Block> PINK_TILES_VERTICAL_SLAB = BLOCKS.register("pink_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.PINK_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.PINK_TILES_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_TILES_VERTICAL_SLAB = BLOCKS.register(
            "purple_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.PURPLE_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.PURPLE_TILES_SLAB.get())));
    public static final RegistryObject<Block> RED_TILES_VERTICAL_SLAB = BLOCKS.register("red_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.RED_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.RED_TILES_SLAB.get())));
    public static final RegistryObject<Block> WHITE_TILES_VERTICAL_SLAB = BLOCKS.register(
            "white_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.WHITE_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.WHITE_TILES_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_TILES_VERTICAL_SLAB = BLOCKS.register(
            "yellow_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.YELLOW_TILES.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.YELLOW_TILES_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_BASALT_VERTICAL_SLAB = BLOCKS.register(
            "polished_basalt_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.POLISHED_BASALT,
                    BlockBehaviour.Properties.copy(StoneBlocks.POLISHED_BASALT_SLAB.get())));
    public static final RegistryObject<Block> DRIPSTONE_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "dripstone_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.DRIPSTONE_BLOCK,
                    BlockBehaviour.Properties.copy(StoneBlocks.DRIPSTONE_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> END_STONE_VERTICAL_SLAB = BLOCKS.register("end_stone_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.END_STONE,
                    BlockBehaviour.Properties.copy(StoneBlocks.END_STONE_SLAB.get())));
    public static final RegistryObject<Block> QUARTZ_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "quartz_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.QUARTZ_BRICKS,
                    BlockBehaviour.Properties.copy(StoneBlocks.QUARTZ_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> QUARTZ_PILLAR_VERTICAL_SLAB = BLOCKS.register(
            "quartz_pillar_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.QUARTZ_PILLAR,
                    BlockBehaviour.Properties.copy(StoneBlocks.QUARTZ_PILLAR_SLAB.get())));
    public static final RegistryObject<Block> CALCITE_VERTICAL_SLAB = BLOCKS.register("calcite_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.CALCITE,
                    BlockBehaviour.Properties.copy(StoneBlocks.CALCITE_SLAB.get())));
    public static final RegistryObject<Block> BONE_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "bone_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BONE_BLOCK,
                    BlockBehaviour.Properties.copy(StoneBlocks.BONE_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> STACKED_BONE_VERTICAL_SLAB = BLOCKS.register(
            "stacked_bone_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.STACKED_BONE_BLOCK.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.STACKED_BONE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_BONE_VERTICAL_SLAB = BLOCKS.register(
            "polished_bone_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.POLISHED_BONE_BLOCK.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.POLISHED_BONE_SLAB.get())));
    public static final RegistryObject<Block> BONE_BRICK_VERTICAL_SLAB = BLOCKS.register(
            "bone_brick_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.BONE_BRICKS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BONE_BRICK_SLAB.get())));
    public static final RegistryObject<Block> OBSIDIAN_VERTICAL_SLAB = BLOCKS.register("obsidian_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.OBSIDIAN,
                    BlockBehaviour.Properties.copy(StoneBlocks.OBSIDIAN_SLAB.get())));
    public static final RegistryObject<Block> SMOOTH_BASALT_VERTICAL_SLAB = BLOCKS.register(
            "smooth_basalt_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.SMOOTH_BASALT,
                    BlockBehaviour.Properties.copy(StoneBlocks.SMOOTH_BASALT_SLAB.get())));
    public static final RegistryObject<Block> MOSS_BLOCK_VERTICAL_SLAB = BLOCKS.register("moss_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(StoneBlocks.MOSS_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> AMETHYST_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "amethyst_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(StoneBlocks.AMETHYST_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> MOSSY_CALCITE_VERTICAL_SLAB = BLOCKS.register(
            "mossy_calcite_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.MOSSY_CALCITE.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.MOSSY_CALCITE_SLAB.get())));
    public static final RegistryObject<Block> BIT_COPPER_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "bit_copper_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BIT_COPPER_BLOCK.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BIT_COPPER_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> BIT_EXPOSED_COPPER_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "bit_exposed_copper_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BIT_EXPOSED_COPPER_BLOCK.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BIT_EXPOSED_COPPER_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> BIT_WEATHERED_COPPER_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "bit_weathered_copper_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BIT_WEATHERED_COPPER_BLOCK.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BIT_WEATHERED_COPPER_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> BIT_OXIDIZED_COPPER_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "bit_oxidized_copper_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BIT_OXIDIZED_COPPER_BLOCK.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BIT_OXIDIZED_COPPER_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> BIT_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "bit_cut_copper_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BIT_CUT_COPPER.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BIT_CUT_COPPER_SLAB.get())));
    public static final RegistryObject<Block> BIT_EXPOSED_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "bit_exposed_cut_copper_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BIT_EXPOSED_CUT_COPPER.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BIT_EXPOSED_CUT_COPPER_SLAB.get())));
    public static final RegistryObject<Block> BIT_WEATHERED_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "bit_weathered_cut_copper_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BIT_WEATHERED_CUT_COPPER.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BIT_WEATHERED_CUT_COPPER_SLAB.get())));
    public static final RegistryObject<Block> BIT_OXIDIZED_CUT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "bit_oxidized_cut_copper_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BIT_OXIDIZED_CUT_COPPER.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BIT_OXIDIZED_CUT_COPPER_SLAB.get())));
    public static final RegistryObject<Block> BLACK_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "black_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BLACK_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.BLACK_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> BLUE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "blue_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BLUE_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.BLUE_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> BROWN_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "brown_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BROWN_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.BROWN_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> CYAN_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "cyan_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.CYAN_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.CYAN_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> GRAY_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "gray_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.GRAY_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.GRAY_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> GREEN_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "green_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.GREEN_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.GREEN_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.LIGHT_BLUE_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.LIGHT_BLUE_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.LIGHT_GRAY_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.LIGHT_GRAY_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> LIME_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "lime_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.LIME_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.LIME_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "magenta_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.MAGENTA_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.MAGENTA_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "orange_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.ORANGE_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.ORANGE_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> PINK_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "pink_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.PINK_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.PINK_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "purple_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.PURPLE_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.PURPLE_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> RED_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "red_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.RED_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.RED_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> WHITE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "white_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.WHITE_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.WHITE_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "yellow_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.YELLOW_CONCRETE,
                    BlockBehaviour.Properties.copy(StoneBlocks.YELLOW_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> HAY_BALE_VERTICAL_SLAB = BLOCKS.register("hay_bale_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(WoodBlocks.HAY_BALE_SLAB.get())));
    public static final RegistryObject<Block> BAMBOO_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "bamboo_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.BAMBOO_BLOCK.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.BAMBOO_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "stripped_bamboo_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(WoodBlocks.STRIPPED_BAMBOO_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_WHITE_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_white_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.ASHPEN_WHITE_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.ASHPEN_WHITE_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_BLACK_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_black_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.BLACK_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.BLACK_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_BLUE_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_blue_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.BLUE_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.BLUE_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_BROWN_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_brown_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.BROWN_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.BROWN_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_CYAN_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_cyan_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.CYAN_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.CYAN_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_GRAY_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_gray_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.GRAY_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.GRAY_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_GREEN_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_green_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.GREEN_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.GREEN_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_LIGHT_BLUE_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_light_blue_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.LIGHT_BLUE_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.LIGHT_BLUE_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_LIGHT_GRAY_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_light_gray_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.LIGHT_GRAY_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.LIGHT_GRAY_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_LIME_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_lime_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.LIME_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.LIME_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_MAGENTA_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_magenta_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.MAGENTA_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.MAGENTA_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_ORANGE_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_orange_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.ORANGE_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.ORANGE_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_PINK_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_pink_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.PINK_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.PINK_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_PURPLE_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_purple_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.PURPLE_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.PURPLE_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_RED_VERTICAL_SLAB = BLOCKS.register("ashpen_red_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.RED_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.RED_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_YELLOW_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_yellow_vertical_slab",
            () -> new VerticalSlabBlock(
                    WoodBlocks.YELLOW_ASHPEN_PLANKS.get(),
                    BlockBehaviour.Properties.copy(WoodBlocks.YELLOW_ASHPEN_SLAB.get())));
    public static final RegistryObject<Block> SNOW_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "snow_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    DecorBlocks.SNOW_BRICKS.get(),
                    BlockBehaviour.Properties.copy(DecorBlocks.SNOW_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> SNOW_VERTICAL_SLAB = BLOCKS.register("snow_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.SNOW_BLOCK,
                    BlockBehaviour.Properties.copy(DecorBlocks.SNOW_SLAB.get())));
    public static final RegistryObject<Block> SNOWY_GRASS_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "snowy_grass_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    DecorBlocks.SNOWY_GRASS_BLOCK.get(),
                    BlockBehaviour.Properties.copy(DecorBlocks.SNOWY_GRASS_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> MANGROVE_VERTICAL_SLAB = BLOCKS.register("mangrove_vertical_slab",
            () -> new VerticalSlabBlock(
                    DecorBlocks.MANGROVE_PLANKS.get(),
                    BlockBehaviour.Properties.copy(DecorBlocks.MANGROVE_SLAB.get())));
    public static final RegistryObject<Block> STEEL_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "steel_block_vertical_slab",
            () -> new VerticalSlabBlock(
                    DecorBlocks.STEEL_BLOCK.get(),
                    BlockBehaviour.Properties.copy(DecorBlocks.STEEL_BLOCK_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "polished_steel_vertical_slab",
            () -> new VerticalSlabBlock(
                    DecorBlocks.POLISHED_STEEL.get(),
                    BlockBehaviour.Properties.copy(DecorBlocks.POLISHED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> PRESSED_STEEL_VERTICAL_SLAB = BLOCKS.register(
            "pressed_steel_vertical_slab",
            () -> new VerticalSlabBlock(
                    DecorBlocks.PRESSED_STEEL.get(),
                    BlockBehaviour.Properties.copy(DecorBlocks.PRESSED_STEEL_SLAB.get())));
    public static final RegistryObject<Block> CUT_STEEL_VERTICAL_SLAB = BLOCKS.register("cut_steel_vertical_slab",
            () -> new VerticalSlabBlock(
                    DecorBlocks.CUT_STEEL.get(),
                    BlockBehaviour.Properties.copy(DecorBlocks.CUT_STEEL_SLAB.get())));
    public static final RegistryObject<Block> CAUTION_BLACK_VERTICAL_SLAB = BLOCKS.register(
            "caution_black_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(DecorBlocks.CAUTION_BLACK_SLAB.get())));
    public static final RegistryObject<Block> CAUTION_BLUE_VERTICAL_SLAB = BLOCKS.register(
            "caution_blue_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(DecorBlocks.CAUTION_BLUE_SLAB.get())));
    public static final RegistryObject<Block> CAUTION_FACTORY_VERTICAL_SLAB = BLOCKS.register(
            "caution_factory_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(DecorBlocks.CAUTION_FACTORY_SLAB.get())));
    public static final RegistryObject<Block> CAUTION_RED_VERTICAL_SLAB = BLOCKS.register(
            "caution_red_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(DecorBlocks.CAUTION_RED_SLAB.get())));
    public static final RegistryObject<Block> FRAMED_CAUTION_VERTICAL_SLAB = BLOCKS.register(
            "framed_caution_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(DecorBlocks.FRAMED_CAUTION_SLAB.get())));
    public static final RegistryObject<Block> CAUTION_WHITE_VERTICAL_SLAB = BLOCKS.register(
            "caution_white_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(DecorBlocks.CAUTION_WHITE_SLAB.get())));
    public static final RegistryObject<Block> CAUTION_LIME_VERTICAL_SLAB = BLOCKS.register(
            "caution_lime_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(DecorBlocks.CAUTION_LIME_SLAB.get())));
    public static final RegistryObject<Block> CAUTION_PINK_VERTICAL_SLAB = BLOCKS.register(
            "caution_pink_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(DecorBlocks.CAUTION_PINK_SLAB.get())));
    public static final RegistryObject<Block> CAUTION_YELLOW_VERTICAL_SLAB = BLOCKS.register(
            "caution_yellow_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(DecorBlocks.CAUTION_YELLOW_SLAB.get())));
    public static final RegistryObject<Block> CAUTION_CANDY_VERTICAL_SLAB = BLOCKS.register(
            "caution_candy_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(DecorBlocks.CAUTION_CANDY_SLAB.get())));
    public static final RegistryObject<Block> CAUTION_COTTONCANDY_VERTICAL_SLAB = BLOCKS.register(
            "caution_cottoncandy_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(DecorBlocks.CAUTION_COTTONCANDY_SLAB.get())));
    public static final RegistryObject<Block> CAUTION_MINTCANDY_VERTICAL_SLAB = BLOCKS.register(
            "caution_mintcandy_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(DecorBlocks.CAUTION_MINTCANDY_SLAB.get())));
    public static final RegistryObject<Block> CAUTION_CITRUSCANDY_VERTICAL_SLAB = BLOCKS.register(
            "caution_citruscandy_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(DecorBlocks.CAUTION_CITRUSCANDY_SLAB.get())));
    public static final RegistryObject<Block> WHITE_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "white_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.WHITE_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.LIGHT_GRAY_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> GRAY_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "gray_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.GRAY_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> BLACK_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "black_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.BLACK_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> BROWN_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "brown_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.BROWN_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> RED_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "red_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.RED_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "orange_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.ORANGE_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "yellow_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.YELLOW_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> LIME_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "lime_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.LIME_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> GREEN_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "green_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.GREEN_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> CYAN_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "cyan_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.CYAN_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.LIGHT_BLUE_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> BLUE_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "blue_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.BLUE_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "purple_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.PURPLE_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "magenta_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.MAGENTA_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> PINK_STAINED_BRICKS_VERTICAL_SLAB = BLOCKS.register(
            "pink_stained_bricks_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.PINK_STAINED_BRICKS_SLAB.get())));
    public static final RegistryObject<Block> WHITE_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "white_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.WHITE_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.LIGHT_GRAY_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "gray_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.GRAY_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> BLACK_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "black_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.BLACK_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> BROWN_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "brown_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.BROWN_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> RED_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "red_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.RED_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "orange_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.ORANGE_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "yellow_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.YELLOW_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> LIME_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "lime_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.LIME_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> GREEN_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "green_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.GREEN_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> CYAN_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "cyan_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.CYAN_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.LIGHT_BLUE_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "blue_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.BLUE_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "purple_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.PURPLE_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "magenta_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.MAGENTA_STAINED_BRICK_TILES_SLAB.get())));
    public static final RegistryObject<Block> PINK_STAINED_BRICK_TILES_VERTICAL_SLAB = BLOCKS.register(
            "pink_stained_brick_tiles_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.AIR,
                    BlockBehaviour.Properties.copy(MasonryBlocks.PINK_STAINED_BRICK_TILES_SLAB.get())));

    public static final RegistryObject<Block> POLISHED_WHITE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_white_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_WHITE_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_WHITE_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_ORANGE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_orange_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_ORANGE_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_ORANGE_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_MAGENTA_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_magenta_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_MAGENTA_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_MAGENTA_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_LIGHT_BLUE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_light_blue_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_LIGHT_BLUE_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_LIGHT_BLUE_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_YELLOW_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_yellow_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_YELLOW_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_YELLOW_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_LIME_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_lime_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_LIME_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_LIME_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_PINK_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_pink_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_PINK_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_PINK_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_GRAY_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_gray_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_GRAY_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_GRAY_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_LIGHT_GRAY_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_light_gray_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_LIGHT_GRAY_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_LIGHT_GRAY_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_CYAN_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_cyan_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_CYAN_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_CYAN_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_PURPLE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_purple_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_PURPLE_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_PURPLE_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_BLUE_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_blue_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_BLUE_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_BLUE_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_BROWN_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_brown_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_BROWN_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_BROWN_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_GREEN_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_green_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_GREEN_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_GREEN_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_RED_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_red_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_RED_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_RED_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> POLISHED_BLACK_CONCRETE_VERTICAL_SLAB = BLOCKS.register(
            "polished_black_concrete_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.POLISHED_BLACK_CONCRETE.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.POLISHED_BLACK_CONCRETE_SLAB.get())));
    public static final RegistryObject<Block> BLACK_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
            "black_factory_mesh_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.BLACK_FACTORY_MESH.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.BLACK_FACTORY_MESH_SLAB.get())));
    public static final RegistryObject<Block> BLUE_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
            "blue_factory_mesh_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.BLUE_FACTORY_MESH.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.BLUE_FACTORY_MESH_SLAB.get())));
    public static final RegistryObject<Block> BROWN_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
            "brown_factory_mesh_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.BROWN_FACTORY_MESH.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.BROWN_FACTORY_MESH_SLAB.get())));
    public static final RegistryObject<Block> GRAY_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
            "gray_factory_mesh_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.GRAY_FACTORY_MESH.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.GRAY_FACTORY_MESH_SLAB.get())));
    public static final RegistryObject<Block> GREEN_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
            "green_factory_mesh_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.GREEN_FACTORY_MESH.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.GREEN_FACTORY_MESH_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_factory_mesh_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.LIGHT_GRAY_FACTORY_MESH.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.LIGHT_GRAY_FACTORY_MESH_SLAB.get())));
    public static final RegistryObject<Block> LIME_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
            "lime_factory_mesh_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.LIME_FACTORY_MESH.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.LIME_FACTORY_MESH_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
            "orange_factory_mesh_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.ORANGE_FACTORY_MESH.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.ORANGE_FACTORY_MESH_SLAB.get())));
    public static final RegistryObject<Block> RED_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
            "red_factory_mesh_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.RED_FACTORY_MESH.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.RED_FACTORY_MESH_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_FACTORY_MESH_VERTICAL_SLAB = BLOCKS.register(
            "yellow_factory_mesh_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.YELLOW_FACTORY_MESH.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.YELLOW_FACTORY_MESH_SLAB.get())));
    public static final RegistryObject<Block> PACKED_MUD_VERTICAL_SLAB = BLOCKS.register("packed_mud_vertical_slab",
            () -> new VerticalSlabBlock(
                    GlassBlocks.PACKED_MUD.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.PACKED_MUD_SLAB.get())));
    public static final RegistryObject<Block> MUD_BRICK_VERTICAL_SLAB = BLOCKS.register("mud_brick_vertical_slab",
            () -> new VerticalSlabBlock(
                    GlassBlocks.MUD_BRICKS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.MUD_BRICK_SLAB.get())));
    public static final RegistryObject<Block> GLASS_VERTICAL_SLAB = BLOCKS.register("glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.GLASS_SLAB.get())));
    public static final RegistryObject<Block> WHITE_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "white_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.WHITE_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.WHITE_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.LIGHT_GRAY_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.LIGHT_GRAY_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> GRAY_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "gray_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.GRAY_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.GRAY_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> BLACK_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "black_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BLACK_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.BLACK_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> BROWN_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "brown_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BROWN_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.BROWN_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> RED_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "red_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.RED_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.RED_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "orange_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.ORANGE_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.ORANGE_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "yellow_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.YELLOW_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.YELLOW_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> LIME_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "lime_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.LIME_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.LIME_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> GREEN_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "green_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.GREEN_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.GREEN_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> CYAN_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "cyan_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.CYAN_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.CYAN_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.LIGHT_BLUE_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.LIGHT_BLUE_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> BLUE_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "blue_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.BLUE_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.BLUE_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "purple_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.PURPLE_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.PURPLE_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "magenta_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.MAGENTA_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.MAGENTA_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> PINK_STAINED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "pink_stained_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    Blocks.PINK_STAINED_GLASS,
                    BlockBehaviour.Properties.copy(GlassBlocks.PINK_STAINED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> WHITE_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "white_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.WHITE_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.WHITE_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.LIGHT_GRAY_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.LIGHT_GRAY_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> GRAY_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "gray_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.GRAY_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.GRAY_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> BLACK_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "black_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BLACK_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.BLACK_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> BROWN_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "brown_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BROWN_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.BROWN_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> RED_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "red_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.RED_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.RED_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "orange_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.ORANGE_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.ORANGE_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "yellow_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.YELLOW_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.YELLOW_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> LIME_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "lime_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.LIME_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.LIME_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> GREEN_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "green_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.GREEN_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.GREEN_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> CYAN_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "cyan_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.CYAN_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.CYAN_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.LIGHT_BLUE_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.LIGHT_BLUE_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> BLUE_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "blue_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.BLUE_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.BLUE_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "purple_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.PURPLE_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.PURPLE_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "magenta_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.MAGENTA_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.MAGENTA_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> PINK_MOSAIC_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "pink_mosaic_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    SandstoneBlocks.PINK_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.PINK_MOSAIC_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_WHITE_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_white_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    DecorBlocks.FACTORY_WHITE_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_WHITE_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_LIGHT_GRAY_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_light_gray_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    DecorBlocks.FACTORY_LIGHT_GRAY_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_LIGHT_GRAY_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_GRAY_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_gray_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_GRAY_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_GRAY_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_BLACK_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_black_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_BLACK_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_BLACK_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_BROWN_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_brown_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_BROWN_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_BROWN_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_RED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_red_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_RED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_RED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_ORANGE_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_orange_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_ORANGE_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_ORANGE_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_YELLOW_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_yellow_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_YELLOW_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_YELLOW_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_LIME_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_lime_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_LIME_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_LIME_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_GREEN_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_green_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_GREEN_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_GREEN_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_CYAN_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_cyan_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_CYAN_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_CYAN_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_LIGHT_BLUE_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_light_blue_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_LIGHT_BLUE_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_LIGHT_BLUE_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_BLUE_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_blue_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_BLUE_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_BLUE_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_PURPLE_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_purple_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_PURPLE_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_PURPLE_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_MAGENTA_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_magenta_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_MAGENTA_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_MAGENTA_GLASS_SLAB.get())));
    public static final RegistryObject<Block> FACTORY_PINK_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "factory_pink_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    MasonryBlocks.FACTORY_PINK_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.FACTORY_PINK_GLASS_SLAB.get())));
    public static final RegistryObject<Block> WHITE_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "white_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.WHITE_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.WHITE_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "light_gray_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.LIGHT_GRAY_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.LIGHT_GRAY_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> GRAY_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "gray_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.GRAY_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.GRAY_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> BLACK_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "black_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.BLACK_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.BLACK_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> BROWN_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "brown_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.BROWN_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.BROWN_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> RED_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "red_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.RED_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.RED_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> ORANGE_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "orange_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.ORANGE_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.ORANGE_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> YELLOW_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "yellow_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.YELLOW_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.YELLOW_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> LIME_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "lime_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.LIME_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.LIME_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> GREEN_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "green_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.GREEN_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.GREEN_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> CYAN_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "cyan_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.CYAN_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.CYAN_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "light_blue_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.LIGHT_BLUE_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.LIGHT_BLUE_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> BLUE_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "blue_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.BLUE_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.BLUE_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> PURPLE_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "purple_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.PURPLE_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.PURPLE_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> MAGENTA_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "magenta_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.MAGENTA_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.MAGENTA_GLAZED_GLASS_SLAB.get())));
    public static final RegistryObject<Block> PINK_GLAZED_GLASS_VERTICAL_SLAB = BLOCKS.register(
            "pink_glazed_glass_vertical_slab",
            () -> new VerticalSlabBlock(
                    StoneBlocks.PINK_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(GlassBlocks.PINK_GLAZED_GLASS_SLAB.get())));
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
                    DecorBlocks.MANGROVE_LOG.get(),
                    BlockBehaviour.Properties.copy(MANGROVE_LOG_SLAB.get())));
    public static final RegistryObject<Block> STRIPPED_MANGROVE_LOG_VERTICAL_SLAB = BLOCKS.register(
            "stripped_mangrove_log_vertical_slab",
            () -> new VerticalSlabBlock(
                    DecorBlocks.STRIPPED_MANGROVE_LOG.get(),
                    BlockBehaviour.Properties.copy(STRIPPED_MANGROVE_LOG_SLAB.get())));

    public static final RegistryObject<Block> ASHPEN_LOG_VERTICAL_SLAB = BLOCKS.register("ashpen_log_vertical_slab",
            () -> new VerticalSlabBlock(
                    ShapedStoneBlocks.ASHPEN_LOG.get(),
                    BlockBehaviour.Properties.copy(ShapedStoneBlocks.ASHPEN_LOG_SLAB.get())));
    public static final RegistryObject<Block> ASHPEN_WOOD_VERTICAL_SLAB = BLOCKS.register(
            "ashpen_wood_vertical_slab",
            () -> new VerticalSlabBlock(
                    ShapedStoneBlocks.ASHPEN_WOOD.get(),
                    BlockBehaviour.Properties.copy(ShapedStoneBlocks.ASHPEN_WOOD_SLAB.get())));

    public static final RegistryObject<Block> BLACK_DYE_SACK = BLOCKS.register("black_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));
    public static final RegistryObject<Block> BLUE_DYE_SACK = BLOCKS.register("blue_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.BLUE_WOOL)));
    public static final RegistryObject<Block> BROWN_DYE_SACK = BLOCKS.register("brown_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_WOOL)));
    public static final RegistryObject<Block> CYAN_DYE_SACK = BLOCKS.register("cyan_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL)));
    public static final RegistryObject<Block> GRAY_DYE_SACK = BLOCKS.register("gray_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.GRAY_WOOL)));
    public static final RegistryObject<Block> GREEN_DYE_SACK = BLOCKS.register("green_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.GREEN_WOOL)));
    public static final RegistryObject<Block> LIGHT_BLUE_DYE_SACK = BLOCKS.register("light_blue_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_WOOL)));
    public static final RegistryObject<Block> LIGHT_GRAY_DYE_SACK = BLOCKS.register("light_gray_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_WOOL)));
    public static final RegistryObject<Block> LIME_DYE_SACK = BLOCKS.register("lime_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.LIME_WOOL)));
    public static final RegistryObject<Block> MAGENTA_DYE_SACK = BLOCKS.register("magenta_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.MAGENTA_WOOL)));
    public static final RegistryObject<Block> ORANGE_DYE_SACK = BLOCKS.register("orange_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.ORANGE_WOOL)));
    public static final RegistryObject<Block> PINK_DYE_SACK = BLOCKS.register("pink_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.PINK_WOOL)));
    public static final RegistryObject<Block> PURPLE_DYE_SACK = BLOCKS.register("purple_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.PURPLE_WOOL)));
    public static final RegistryObject<Block> RED_DYE_SACK = BLOCKS.register("red_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.RED_WOOL)));
    public static final RegistryObject<Block> WHITE_DYE_SACK = BLOCKS.register("white_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)));
    public static final RegistryObject<Block> YELLOW_DYE_SACK = BLOCKS.register("yellow_dye_sack",
            () -> new SoftFabricBlock(BlockBehaviour.Properties.copy(Blocks.YELLOW_WOOL)));
    public static final RegistryObject<Block> GLOW_INK_SACK = BLOCKS.register("glow_ink_sack",
            () -> new SoftFabricBlock(
                    BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL).lightLevel(state -> 7)));

    public static final RegistryObject<Block> SPOOL = BLOCKS.register("spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)));
    public static final RegistryObject<Block> GLOWING_SPOOL = BLOCKS.register("glowing_spool",
            () -> new SpoolBlock(
                    BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL).lightLevel(state -> 7)));
    public static final RegistryObject<Block> BLACK_SPOOL = BLOCKS.register("black_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)));
    public static final RegistryObject<Block> BLUE_SPOOL = BLOCKS.register("blue_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.BLUE_WOOL)));
    public static final RegistryObject<Block> BROWN_SPOOL = BLOCKS.register("brown_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_WOOL)));
    public static final RegistryObject<Block> CYAN_SPOOL = BLOCKS.register("cyan_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL)));
    public static final RegistryObject<Block> GRAY_SPOOL = BLOCKS.register("gray_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.GRAY_WOOL)));
    public static final RegistryObject<Block> GREEN_SPOOL = BLOCKS.register("green_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.GREEN_WOOL)));
    public static final RegistryObject<Block> LIGHT_BLUE_SPOOL = BLOCKS.register("light_blue_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_WOOL)));
    public static final RegistryObject<Block> LIGHT_GRAY_SPOOL = BLOCKS.register("light_gray_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_WOOL)));
    public static final RegistryObject<Block> LIME_SPOOL = BLOCKS.register("lime_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.LIME_WOOL)));
    public static final RegistryObject<Block> MAGENTA_SPOOL = BLOCKS.register("magenta_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.MAGENTA_WOOL)));
    public static final RegistryObject<Block> ORANGE_SPOOL = BLOCKS.register("orange_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.ORANGE_WOOL)));
    public static final RegistryObject<Block> PINK_SPOOL = BLOCKS.register("pink_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.PINK_WOOL)));
    public static final RegistryObject<Block> PURPLE_SPOOL = BLOCKS.register("purple_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.PURPLE_WOOL)));
    public static final RegistryObject<Block> RED_SPOOL = BLOCKS.register("red_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.RED_WOOL)));
    public static final RegistryObject<Block> WHITE_SPOOL = BLOCKS.register("white_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)));
    public static final RegistryObject<Block> YELLOW_SPOOL = BLOCKS.register("yellow_spool",
            () -> new SpoolBlock(BlockBehaviour.Properties.copy(Blocks.YELLOW_WOOL)));

    public static final RegistryObject<Block> SULFUR = BLOCKS.register("sulfur",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> POLISHED_SULFUR = BLOCKS.register("polished_sulfur",
            () -> new Block(BlockBehaviour.Properties.copy(SULFUR.get())));
    public static final RegistryObject<Block> CHISELED_SULFUR = BLOCKS.register("chiseled_sulfur",
            () -> new Block(BlockBehaviour.Properties.copy(SULFUR.get())));
    public static final RegistryObject<Block> SULFUR_BRICKS = BLOCKS.register("sulfur_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(SULFUR.get())));
    public static final RegistryObject<Block> SULFUR_STAIRS = BLOCKS.register("sulfur_stairs",
            () -> new ModStairBlock(SULFUR.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SULFUR.get())));
    public static final RegistryObject<Block> SULFUR_SLAB = BLOCKS.register("sulfur_slab",
            () -> new ModSlabBlock(SULFUR.get(), BlockBehaviour.Properties.copy(SULFUR.get())));
    public static final RegistryObject<Block> SULFUR_WALL = BLOCKS.register("sulfur_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(SULFUR.get())));
    public static final RegistryObject<Block> POLISHED_SULFUR_STAIRS = BLOCKS.register("polished_sulfur_stairs",
            () -> new ModStairBlock(POLISHED_SULFUR.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(POLISHED_SULFUR.get())));
    public static final RegistryObject<Block> POLISHED_SULFUR_SLAB = BLOCKS.register("polished_sulfur_slab",
            () -> new ModSlabBlock(POLISHED_SULFUR.get(),
                    BlockBehaviour.Properties.copy(POLISHED_SULFUR.get())));
    public static final RegistryObject<Block> POLISHED_SULFUR_WALL = BLOCKS.register("polished_sulfur_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(POLISHED_SULFUR.get())));
    public static final RegistryObject<Block> SULFUR_BRICKS_STAIRS = BLOCKS.register("sulfur_bricks_stairs",
            () -> new ModStairBlock(SULFUR_BRICKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SULFUR_BRICKS.get())));
    public static final RegistryObject<Block> SULFUR_BRICKS_SLAB = BLOCKS.register("sulfur_bricks_slab",
            () -> new ModSlabBlock(SULFUR_BRICKS.get(),
                    BlockBehaviour.Properties.copy(SULFUR_BRICKS.get())));
    public static final RegistryObject<Block> SULFUR_BRICKS_WALL = BLOCKS.register("sulfur_bricks_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(SULFUR_BRICKS.get())));
    public static final RegistryObject<Block> SULFUR_SPIKE = BLOCKS.register("sulfur_spike",
            () -> new SulfurSpikeBlock(BlockBehaviour.Properties.copy(Blocks.POINTED_DRIPSTONE)));
    public static final RegistryObject<Block> POTENT_SULFUR = BLOCKS.register("potent_sulfur",
            () -> new PotentSulfurBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistryObject<Block> CINNABAR = BLOCKS.register("cinnabar",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> POLISHED_CINNABAR = BLOCKS.register("polished_cinnabar",
            () -> new Block(BlockBehaviour.Properties.copy(CINNABAR.get())));
    public static final RegistryObject<Block> CHISELED_CINNABAR = BLOCKS.register("chiseled_cinnabar",
            () -> new Block(BlockBehaviour.Properties.copy(CINNABAR.get())));
    public static final RegistryObject<Block> CINNABAR_BRICKS = BLOCKS.register("cinnabar_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(CINNABAR.get())));
    public static final RegistryObject<Block> CINNABAR_STAIRS = BLOCKS.register("cinnabar_stairs",
            () -> new ModStairBlock(CINNABAR.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(CINNABAR.get())));
    public static final RegistryObject<Block> CINNABAR_SLAB = BLOCKS.register("cinnabar_slab",
            () -> new ModSlabBlock(CINNABAR.get(), BlockBehaviour.Properties.copy(CINNABAR.get())));
    public static final RegistryObject<Block> CINNABAR_WALL = BLOCKS.register("cinnabar_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(CINNABAR.get())));
    public static final RegistryObject<Block> POLISHED_CINNABAR_STAIRS = BLOCKS.register("polished_cinnabar_stairs",
            () -> new ModStairBlock(POLISHED_CINNABAR.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(POLISHED_CINNABAR.get())));
    public static final RegistryObject<Block> POLISHED_CINNABAR_SLAB = BLOCKS.register("polished_cinnabar_slab",
            () -> new ModSlabBlock(POLISHED_CINNABAR.get(),
                    BlockBehaviour.Properties.copy(POLISHED_CINNABAR.get())));
    public static final RegistryObject<Block> POLISHED_CINNABAR_WALL = BLOCKS.register("polished_cinnabar_wall",
            () -> new ModWallBlock(BlockBehaviour.Properties.copy(POLISHED_CINNABAR.get())));
    public static final RegistryObject<Block> CINNABAR_BRICKS_STAIRS = BLOCKS.register("cinnabar_bricks_stairs",
            () -> new ModStairBlock(CINNABAR_BRICKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(CINNABAR_BRICKS.get())));
}
