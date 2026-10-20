package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.block.base.ModBlock;
import com.kingodogo.buildscape.block.building.ModSlabBlock;
import com.kingodogo.buildscape.block.building.ModStairBlock;
import com.kingodogo.buildscape.block.building.ModWallBlock;
import com.kingodogo.buildscape.registry.ModSounds;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.blocks.BlocksRegister.BLOCKS;

/**
 * The blocks YELLOW_FACTORY_MESH_SLAB through PINK_GLAZED_GLASS_STAIRS of the registry, part 6 of 12, in registration order.
 * Named for its dominant families (glass, stone, decor); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link MasonryBlocks#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class GlassBlocks {

    private GlassBlocks() {}

    /** Forces this class to load, which registers its blocks. */
    public static void init() {}

    static {
        MasonryBlocks.init();
    }

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
            () -> new ModSlabBlock(SandstoneBlocks.WHITE_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.WHITE_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> WHITE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "white_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.WHITE_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.WHITE_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "light_gray_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.LIGHT_GRAY_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.LIGHT_GRAY_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "light_gray_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.LIGHT_GRAY_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.LIGHT_GRAY_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> GRAY_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "gray_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.GRAY_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.GRAY_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> GRAY_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "gray_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.GRAY_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.GRAY_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> BLACK_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "black_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.BLACK_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.BLACK_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> BLACK_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "black_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.BLACK_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.BLACK_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> BROWN_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "brown_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.BROWN_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.BROWN_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> BROWN_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "brown_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.BROWN_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.BROWN_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> RED_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "red_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.RED_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.RED_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> RED_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "red_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.RED_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.RED_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> ORANGE_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "orange_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.ORANGE_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.ORANGE_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> ORANGE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "orange_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.ORANGE_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.ORANGE_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> YELLOW_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "yellow_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.YELLOW_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.YELLOW_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> YELLOW_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "yellow_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.YELLOW_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.YELLOW_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> LIME_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "lime_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.LIME_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.LIME_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> LIME_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "lime_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.LIME_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.LIME_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> GREEN_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "green_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.GREEN_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.GREEN_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> GREEN_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "green_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.GREEN_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.GREEN_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> CYAN_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "cyan_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.CYAN_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.CYAN_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> CYAN_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "cyan_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.CYAN_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.CYAN_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "light_blue_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.LIGHT_BLUE_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.LIGHT_BLUE_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "light_blue_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.LIGHT_BLUE_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.LIGHT_BLUE_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> BLUE_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "blue_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.BLUE_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.BLUE_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> BLUE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "blue_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.BLUE_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.BLUE_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> PURPLE_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "purple_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.PURPLE_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.PURPLE_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> PURPLE_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "purple_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.PURPLE_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.PURPLE_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> MAGENTA_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "magenta_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.MAGENTA_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.MAGENTA_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> MAGENTA_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "magenta_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.MAGENTA_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.MAGENTA_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> PINK_MOSAIC_GLASS_SLAB = BLOCKS.register(
            "pink_mosaic_glass_slab",
            () -> new ModSlabBlock(SandstoneBlocks.PINK_MOSAIC_GLASS.get(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.PINK_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> PINK_MOSAIC_GLASS_STAIRS = BLOCKS.register(
            "pink_mosaic_glass_stairs",
            () -> new ModStairBlock(
                    SandstoneBlocks.PINK_MOSAIC_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.PINK_MOSAIC_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_WHITE_GLASS_SLAB = BLOCKS.register(
            "factory_white_glass_slab",
            () -> new ModSlabBlock(DecorBlocks.FACTORY_WHITE_GLASS.get(),
                    BlockBehaviour.Properties.copy(DecorBlocks.FACTORY_WHITE_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_WHITE_GLASS_STAIRS = BLOCKS.register(
            "factory_white_glass_stairs",
            () -> new ModStairBlock(
                    DecorBlocks.FACTORY_WHITE_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(DecorBlocks.FACTORY_WHITE_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_LIGHT_GRAY_GLASS_SLAB = BLOCKS.register(
            "factory_light_gray_glass_slab",
            () -> new ModSlabBlock(DecorBlocks.FACTORY_LIGHT_GRAY_GLASS.get(),
                    BlockBehaviour.Properties.copy(DecorBlocks.FACTORY_LIGHT_GRAY_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_LIGHT_GRAY_GLASS_STAIRS = BLOCKS.register(
            "factory_light_gray_glass_stairs",
            () -> new ModStairBlock(
                    DecorBlocks.FACTORY_LIGHT_GRAY_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(DecorBlocks.FACTORY_LIGHT_GRAY_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_GRAY_GLASS_SLAB = BLOCKS.register(
            "factory_gray_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_GRAY_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_GRAY_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_GRAY_GLASS_STAIRS = BLOCKS.register(
            "factory_gray_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_GRAY_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_GRAY_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_BLACK_GLASS_SLAB = BLOCKS.register(
            "factory_black_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_BLACK_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_BLACK_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_BLACK_GLASS_STAIRS = BLOCKS.register(
            "factory_black_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_BLACK_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_BLACK_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_BROWN_GLASS_SLAB = BLOCKS.register(
            "factory_brown_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_BROWN_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_BROWN_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_BROWN_GLASS_STAIRS = BLOCKS.register(
            "factory_brown_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_BROWN_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_BROWN_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_RED_GLASS_SLAB = BLOCKS.register(
            "factory_red_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_RED_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_RED_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_RED_GLASS_STAIRS = BLOCKS.register(
            "factory_red_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_RED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_RED_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_ORANGE_GLASS_SLAB = BLOCKS.register(
            "factory_orange_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_ORANGE_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_ORANGE_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_ORANGE_GLASS_STAIRS = BLOCKS.register(
            "factory_orange_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_ORANGE_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_ORANGE_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_YELLOW_GLASS_SLAB = BLOCKS.register(
            "factory_yellow_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_YELLOW_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_YELLOW_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_YELLOW_GLASS_STAIRS = BLOCKS.register(
            "factory_yellow_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_YELLOW_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_YELLOW_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_LIME_GLASS_SLAB = BLOCKS.register(
            "factory_lime_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_LIME_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_LIME_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_LIME_GLASS_STAIRS = BLOCKS.register(
            "factory_lime_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_LIME_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_LIME_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_GREEN_GLASS_SLAB = BLOCKS.register(
            "factory_green_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_GREEN_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_GREEN_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_GREEN_GLASS_STAIRS = BLOCKS.register(
            "factory_green_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_GREEN_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_GREEN_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_CYAN_GLASS_SLAB = BLOCKS.register(
            "factory_cyan_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_CYAN_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_CYAN_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_CYAN_GLASS_STAIRS = BLOCKS.register(
            "factory_cyan_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_CYAN_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_CYAN_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_LIGHT_BLUE_GLASS_SLAB = BLOCKS.register(
            "factory_light_blue_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_LIGHT_BLUE_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_LIGHT_BLUE_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_LIGHT_BLUE_GLASS_STAIRS = BLOCKS.register(
            "factory_light_blue_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_LIGHT_BLUE_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_LIGHT_BLUE_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_BLUE_GLASS_SLAB = BLOCKS.register(
            "factory_blue_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_BLUE_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_BLUE_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_BLUE_GLASS_STAIRS = BLOCKS.register(
            "factory_blue_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_BLUE_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_BLUE_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_PURPLE_GLASS_SLAB = BLOCKS.register(
            "factory_purple_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_PURPLE_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_PURPLE_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_PURPLE_GLASS_STAIRS = BLOCKS.register(
            "factory_purple_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_PURPLE_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_PURPLE_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_MAGENTA_GLASS_SLAB = BLOCKS.register(
            "factory_magenta_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_MAGENTA_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_MAGENTA_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_MAGENTA_GLASS_STAIRS = BLOCKS.register(
            "factory_magenta_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_MAGENTA_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_MAGENTA_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_PINK_GLASS_SLAB = BLOCKS.register(
            "factory_pink_glass_slab",
            () -> new ModSlabBlock(MasonryBlocks.FACTORY_PINK_GLASS.get(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_PINK_GLASS.get())));
    public static final RegistryObject<Block> FACTORY_PINK_GLASS_STAIRS = BLOCKS.register(
            "factory_pink_glass_stairs",
            () -> new ModStairBlock(
                    MasonryBlocks.FACTORY_PINK_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(MasonryBlocks.FACTORY_PINK_GLASS.get())));
    public static final RegistryObject<Block> WHITE_GLAZED_GLASS_SLAB = BLOCKS.register(
            "white_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.WHITE_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.WHITE_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> WHITE_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "white_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.WHITE_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.WHITE_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_GLAZED_GLASS_SLAB = BLOCKS.register(
            "light_gray_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.LIGHT_GRAY_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.LIGHT_GRAY_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> LIGHT_GRAY_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "light_gray_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.LIGHT_GRAY_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.LIGHT_GRAY_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> GRAY_GLAZED_GLASS_SLAB = BLOCKS.register(
            "gray_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.GRAY_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.GRAY_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> GRAY_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "gray_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.GRAY_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.GRAY_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> BLACK_GLAZED_GLASS_SLAB = BLOCKS.register(
            "black_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.BLACK_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BLACK_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> BLACK_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "black_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.BLACK_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BLACK_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> BROWN_GLAZED_GLASS_SLAB = BLOCKS.register(
            "brown_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.BROWN_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BROWN_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> BROWN_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "brown_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.BROWN_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BROWN_GLAZED_GLASS.get())));

    public static final RegistryObject<Block> RED_GLAZED_GLASS_SLAB = BLOCKS.register(
            "red_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.RED_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.RED_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> RED_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "red_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.RED_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.RED_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> ORANGE_GLAZED_GLASS_SLAB = BLOCKS.register(
            "orange_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.ORANGE_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.ORANGE_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> ORANGE_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "orange_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.ORANGE_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.ORANGE_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> YELLOW_GLAZED_GLASS_SLAB = BLOCKS.register(
            "yellow_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.YELLOW_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.YELLOW_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> YELLOW_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "yellow_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.YELLOW_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.YELLOW_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> LIME_GLAZED_GLASS_SLAB = BLOCKS.register(
            "lime_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.LIME_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.LIME_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> LIME_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "lime_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.LIME_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.LIME_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> GREEN_GLAZED_GLASS_SLAB = BLOCKS.register(
            "green_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.GREEN_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.GREEN_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> GREEN_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "green_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.GREEN_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.GREEN_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> CYAN_GLAZED_GLASS_SLAB = BLOCKS.register(
            "cyan_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.CYAN_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.CYAN_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> CYAN_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "cyan_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.CYAN_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.CYAN_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_GLAZED_GLASS_SLAB = BLOCKS.register(
            "light_blue_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.LIGHT_BLUE_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.LIGHT_BLUE_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> LIGHT_BLUE_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "light_blue_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.LIGHT_BLUE_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.LIGHT_BLUE_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> BLUE_GLAZED_GLASS_SLAB = BLOCKS.register(
            "blue_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.BLUE_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BLUE_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> BLUE_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "blue_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.BLUE_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.BLUE_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> PURPLE_GLAZED_GLASS_SLAB = BLOCKS.register(
            "purple_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.PURPLE_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.PURPLE_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> PURPLE_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "purple_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.PURPLE_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.PURPLE_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> MAGENTA_GLAZED_GLASS_SLAB = BLOCKS.register(
            "magenta_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.MAGENTA_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.MAGENTA_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> MAGENTA_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "magenta_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.MAGENTA_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.MAGENTA_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> PINK_GLAZED_GLASS_SLAB = BLOCKS.register(
            "pink_glazed_glass_slab",
            () -> new ModSlabBlock(StoneBlocks.PINK_GLAZED_GLASS.get(),
                    BlockBehaviour.Properties.copy(StoneBlocks.PINK_GLAZED_GLASS.get())));
    public static final RegistryObject<Block> PINK_GLAZED_GLASS_STAIRS = BLOCKS.register(
            "pink_glazed_glass_stairs",
            () -> new ModStairBlock(
                    StoneBlocks.PINK_GLAZED_GLASS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(StoneBlocks.PINK_GLAZED_GLASS.get())));
}
