package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.block.decor.BoneDiceBlock;
import com.kingodogo.buildscape.block.storage.DecoratedPotBlock;
import com.kingodogo.buildscape.block.building.GlazedGlassBlock;
import com.kingodogo.buildscape.block.base.ModBlock;
import com.kingodogo.buildscape.block.building.ModSlabBlock;
import com.kingodogo.buildscape.block.building.ModStairBlock;
import com.kingodogo.buildscape.block.plant.MossLayersBlock;
import com.kingodogo.buildscape.block.plant.MossOverlayBlock;
import com.kingodogo.buildscape.block.building.MudSlabBlock;
import com.kingodogo.buildscape.block.plant.MushroomShelvesBlock;
import com.kingodogo.buildscape.block.building.SilkTouchOnlyPaneBlock;
import com.kingodogo.buildscape.block.plant.SnowOverlayBlock;
import com.kingodogo.buildscape.feature.pillar.AshenKingPillarBlock;
import com.kingodogo.buildscape.feature.pillar.PillarBlock;
import com.kingodogo.buildscape.registry.ModSounds;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.blocks.BlocksRegister.BLOCKS;

/**
 * The blocks MUD_STAIRS through CYAN_DECORATED_POT of the registry, part 2 of 12, in registration order.
 * Named for its dominant families (stone, glass, decor); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link SandstoneBlocks#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class StoneBlocks {

    private StoneBlocks() {}

    /** Forces this class to load, which registers its blocks. */
    public static void init() {}

    static {
        SandstoneBlocks.init();
    }

    public static final RegistryObject<Block> MUD_STAIRS = BLOCKS.register("mud_stairs",
            () -> new ModStairBlock(SandstoneBlocks.MUD.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SandstoneBlocks.MUD.get())));
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

    public static final RegistryObject<Block> SMOOTH_SANDSTONE_WALL = BLOCKS.register("smooth_sandstone_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLACK_TILES_SLAB = BLOCKS.register(
            "black_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> BLUE_TILES_SLAB = BLOCKS.register(
            "blue_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> BROWN_TILES_SLAB = BLOCKS.register(
            "brown_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> CYAN_TILES_SLAB = BLOCKS.register(
            "cyan_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> GRAY_TILES_SLAB = BLOCKS.register(
            "gray_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> GREEN_TILES_SLAB = BLOCKS.register(
            "green_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> LIGHT_BLUE_TILES_SLAB = BLOCKS.register("light_blue_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> LIGHT_GRAY_TILES_SLAB = BLOCKS.register("light_gray_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> LIME_TILES_SLAB = BLOCKS.register(
            "lime_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> MAGENTA_TILES_SLAB = BLOCKS.register("magenta_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_MAGENTA)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> ORANGE_TILES_SLAB = BLOCKS.register(
            "orange_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> PINK_TILES_SLAB = BLOCKS.register(
            "pink_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> PURPLE_TILES_SLAB = BLOCKS.register(
            "purple_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> RED_TILES_SLAB = BLOCKS.register(
            "red_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> WHITE_TILES_SLAB = BLOCKS.register(
            "white_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> YELLOW_TILES_SLAB = BLOCKS.register(
            "yellow_tiles_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> BLACK_TILES_WALL = BLOCKS.register(
            "black_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> BLUE_TILES_WALL = BLOCKS.register(
            "blue_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> BROWN_TILES_WALL = BLOCKS.register(
            "brown_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> CYAN_TILES_WALL = BLOCKS.register(
            "cyan_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> GRAY_TILES_WALL = BLOCKS.register(
            "gray_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> GREEN_TILES_WALL = BLOCKS.register(
            "green_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> LIGHT_BLUE_TILES_WALL = BLOCKS.register("light_blue_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> LIGHT_GRAY_TILES_WALL = BLOCKS.register("light_gray_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> LIME_TILES_WALL = BLOCKS.register(
            "lime_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> MAGENTA_TILES_WALL = BLOCKS.register("magenta_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_MAGENTA)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> ORANGE_TILES_WALL = BLOCKS.register(
            "orange_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> PINK_TILES_WALL = BLOCKS.register(
            "pink_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> PURPLE_TILES_WALL = BLOCKS.register(
            "purple_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> RED_TILES_WALL = BLOCKS.register(
            "red_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> WHITE_TILES_WALL = BLOCKS.register(
            "white_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> YELLOW_TILES_WALL = BLOCKS.register(
            "yellow_tiles_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> POLISHED_BASALT_STAIRS = BLOCKS.register("polished_basalt_stairs",
            () -> new ModStairBlock(
                    Blocks.POLISHED_BASALT.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(1.5f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.BASALT)));
    public static final RegistryObject<Block> POLISHED_BASALT_SLAB = BLOCKS.register("polished_basalt_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(1.5f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.BASALT)));
    public static final RegistryObject<Block> POLISHED_BASALT_WALL = BLOCKS.register("polished_basalt_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(1.5f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.BASALT)));
    public static final RegistryObject<Block> DRIPSTONE_BLOCK_STAIRS = BLOCKS.register("dripstone_block_stairs",
            () -> new ModStairBlock(
                    Blocks.DRIPSTONE_BLOCK.defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_BROWN)
                            .strength(1.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.DRIPSTONE_BLOCK)));
    public static final RegistryObject<Block> DRIPSTONE_BLOCK_SLAB = BLOCKS.register("dripstone_block_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_BROWN)
                            .strength(1.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.DRIPSTONE_BLOCK)));
    public static final RegistryObject<Block> DRIPSTONE_BLOCK_WALL = BLOCKS.register("dripstone_block_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_BROWN)
                            .strength(1.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.DRIPSTONE_BLOCK)));
    public static final RegistryObject<Block> END_STONE_STAIRS = BLOCKS.register(
            "end_stone_stairs",
            () -> new ModStairBlock(
                    Blocks.END_STONE.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND)
                            .strength(3.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> END_STONE_SLAB = BLOCKS.register(
            "end_stone_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND)
                            .strength(3.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> END_STONE_WALL = BLOCKS.register(
            "end_stone_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND)
                            .strength(3.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> STONE_WALL = BLOCKS.register(
            "stone_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE)
                            .strength(1.5f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> QUARTZ_BRICKS_STAIRS = BLOCKS.register("quartz_bricks_stairs",
            () -> new ModStairBlock(
                    Blocks.QUARTZ_BRICKS.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> QUARTZ_BRICKS_SLAB = BLOCKS.register("quartz_bricks_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> QUARTZ_BRICKS_WALL = BLOCKS.register("quartz_bricks_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> QUARTZ_PILLAR_STAIRS = BLOCKS.register("quartz_pillar_stairs",
            () -> new ModStairBlock(
                    Blocks.QUARTZ_PILLAR.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> QUARTZ_PILLAR_SLAB = BLOCKS.register("quartz_pillar_slab",
            () -> new ModSlabBlock(
                    Blocks.QUARTZ_PILLAR,
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> QUARTZ_PILLAR_WALL = BLOCKS.register("quartz_pillar_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                            .strength(0.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> CALCITE_STAIRS = BLOCKS.register(
            "calcite_stairs",
            () -> new ModStairBlock(
                    Blocks.CALCITE.defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_WHITE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> CALCITE_SLAB = BLOCKS.register(
            "calcite_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_WHITE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> CALCITE_WALL = BLOCKS.register(
            "calcite_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_WHITE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> BONE_BLOCK_STAIRS = BLOCKS.register(
            "bone_block_stairs",
            () -> new ModStairBlock(
                    Blocks.BONE_BLOCK.defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.SAND)
                            .strength(2.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.BONE_BLOCK)));
    public static final RegistryObject<Block> BONE_BLOCK_SLAB = BLOCKS.register(
            "bone_block_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.SAND)
                            .strength(2.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.BONE_BLOCK)));
    public static final RegistryObject<Block> BONE_BLOCK_WALL = BLOCKS.register(
            "bone_block_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.SAND)
                            .strength(2.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.BONE_BLOCK)));
    public static final RegistryObject<Block> STACKED_BONE_BLOCK = BLOCKS.register(
            "stacked_bone_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.SAND)
                            .strength(2.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.BONE_BLOCK)));
    public static final RegistryObject<Block> STACKED_BONE_STAIRS = BLOCKS.register(
            "stacked_bone_stairs",
            () -> new ModStairBlock(
                    STACKED_BONE_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(STACKED_BONE_BLOCK.get())));
    public static final RegistryObject<Block> STACKED_BONE_SLAB = BLOCKS.register(
            "stacked_bone_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.copy(STACKED_BONE_BLOCK.get())));
    public static final RegistryObject<Block> STACKED_BONE_WALL = BLOCKS.register(
            "stacked_bone_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.copy(STACKED_BONE_BLOCK.get())));

    public static final RegistryObject<Block> POLISHED_BONE_BLOCK = BLOCKS.register(
            "polished_bone_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.SAND)
                            .strength(2.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.BONE_BLOCK)));
    public static final RegistryObject<Block> POLISHED_BONE_STAIRS = BLOCKS.register(
            "polished_bone_stairs",
            () -> new ModStairBlock(
                    POLISHED_BONE_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(POLISHED_BONE_BLOCK.get())));
    public static final RegistryObject<Block> POLISHED_BONE_SLAB = BLOCKS.register(
            "polished_bone_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.copy(POLISHED_BONE_BLOCK.get())));
    public static final RegistryObject<Block> POLISHED_BONE_WALL = BLOCKS.register(
            "polished_bone_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.copy(POLISHED_BONE_BLOCK.get())));

    public static final RegistryObject<Block> BONE_BRICKS = BLOCKS.register(
            "bone_bricks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.SAND)
                            .strength(2.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.BONE_BLOCK)));
    public static final RegistryObject<Block> BONE_BRICK_STAIRS = BLOCKS.register(
            "bone_brick_stairs",
            () -> new ModStairBlock(
                    BONE_BRICKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(BONE_BRICKS.get())));
    public static final RegistryObject<Block> BONE_BRICK_SLAB = BLOCKS.register(
            "bone_brick_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.copy(BONE_BRICKS.get())));
    public static final RegistryObject<Block> BONE_BRICK_WALL = BLOCKS.register(
            "bone_brick_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.copy(BONE_BRICKS.get())));

    public static final RegistryObject<Block> CHISELED_BONE_BLOCK = BLOCKS.register(
            "chiseled_bone_block",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.SAND)
                            .strength(2.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.BONE_BLOCK)));

    public static final RegistryObject<Block> BONE_PILLAR = BLOCKS.register(
            "bone_pillar",
            () -> new RotatedPillarBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.SAND)
                            .strength(2.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.BONE_BLOCK)));
    public static final RegistryObject<Block> BONE_DICE = BLOCKS.register(
            "bone_dice",
            () -> new BoneDiceBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.SAND)
                            .strength(0.2f)
                            .sound(SoundType.BONE_BLOCK)));
    public static final RegistryObject<Block> BEDROCK_STAIRS = BLOCKS.register(
            "bedrock_stairs",
            () -> new ModStairBlock(
                    Blocks.BEDROCK.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(-1.0f, 3600000.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BEDROCK_SLAB = BLOCKS.register(
            "bedrock_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(-1.0f, 3600000.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BEDROCK_WALL = BLOCKS.register(
            "bedrock_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(-1.0f, 3600000.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BEDROCK_PANE = BLOCKS.register(
            "bedrock_pane",
            () -> new SilkTouchOnlyPaneBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(-1.0f, 3600000.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)
                            .noOcclusion()));
    public static final RegistryObject<Block> OBSIDIAN_STAIRS = BLOCKS.register(
            "obsidian_stairs",
            () -> new ModStairBlock(
                    Blocks.OBSIDIAN.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(50.0f, 1200.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> OBSIDIAN_SLAB = BLOCKS.register(
            "obsidian_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(50.0f, 1200.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PRISMARINE_BRICKS_WALL = BLOCKS.register("prismarine_bricks_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                            .strength(0.4f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> DARK_PRISMARINE_WALL = BLOCKS.register("dark_prismarine_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                            .strength(0.4f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> QUARTZ_BLOCK_WALL = BLOCKS.register(
            "quartz_block_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                            .strength(0.4f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> SMOOTH_QUARTZ_WALL = BLOCKS.register("smooth_quartz_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                            .strength(0.4f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> SMOOTH_BASALT_STAIRS = BLOCKS.register("smooth_basalt_stairs",
            () -> new ModStairBlock(
                    Blocks.SMOOTH_BASALT.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(0.4f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.BASALT)));
    public static final RegistryObject<Block> SMOOTH_BASALT_SLAB = BLOCKS.register("smooth_basalt_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(0.4f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.BASALT)));
    public static final RegistryObject<Block> MOSS_BLOCK_SLAB = BLOCKS.register(
            "moss_block_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.MOSS, MaterialColor.COLOR_GREEN)
                            .strength(0.4f)
                            .sound(SoundType.MOSS)));
    public static final RegistryObject<Block> AMETHYST_BLOCK_SLAB = BLOCKS.register("amethyst_block_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.AMETHYST,
                            MaterialColor.COLOR_MAGENTA)
                            .strength(0.4f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.AMETHYST)));
    public static final RegistryObject<Block> MOSSY_CALCITE = BLOCKS.register(
            "mossy_calcite",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_WHITE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> MOSSY_CALCITE_STAIRS = BLOCKS.register("mossy_calcite_stairs",
            () -> new ModStairBlock(
                    MOSSY_CALCITE.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_WHITE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> MOSSY_CALCITE_SLAB = BLOCKS.register("mossy_calcite_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_WHITE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> MOSSY_CALCITE_WALL = BLOCKS.register("mossy_calcite_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.TERRACOTTA_WHITE)
                            .strength(0.75f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CALCITE)));
    public static final RegistryObject<Block> BIT_COPPER_BLOCK_SLAB = BLOCKS.register("bit_copper_block_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_COPPER_BLOCK_WALL = BLOCKS.register("bit_copper_block_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_EXPOSED_COPPER_BLOCK_SLAB = BLOCKS
            .register("bit_exposed_copper_block_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.TERRACOTTA_LIGHT_GRAY)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_EXPOSED_COPPER_BLOCK_WALL = BLOCKS
            .register("bit_exposed_copper_block_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.TERRACOTTA_LIGHT_GRAY)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_WEATHERED_COPPER_BLOCK_SLAB = BLOCKS
            .register("bit_weathered_copper_block_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.WARPED_NYLIUM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_WEATHERED_COPPER_BLOCK_WALL = BLOCKS
            .register("bit_weathered_copper_block_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.WARPED_NYLIUM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_OXIDIZED_COPPER_BLOCK_SLAB = BLOCKS
            .register("bit_oxidized_copper_block_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_OXIDIZED_COPPER_BLOCK_WALL = BLOCKS
            .register("bit_oxidized_copper_block_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_CUT_COPPER_SLAB = BLOCKS.register("bit_cut_copper_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_CUT_COPPER_WALL = BLOCKS.register("bit_cut_copper_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_EXPOSED_CUT_COPPER_SLAB = BLOCKS
            .register("bit_exposed_cut_copper_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.TERRACOTTA_LIGHT_GRAY)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_EXPOSED_CUT_COPPER_WALL = BLOCKS
            .register("bit_exposed_cut_copper_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.TERRACOTTA_LIGHT_GRAY)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_WEATHERED_CUT_COPPER_SLAB = BLOCKS
            .register("bit_weathered_cut_copper_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.WARPED_NYLIUM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_WEATHERED_CUT_COPPER_WALL = BLOCKS
            .register("bit_weathered_cut_copper_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.METAL,
                            MaterialColor.WARPED_NYLIUM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_OXIDIZED_CUT_COPPER_SLAB = BLOCKS
            .register("bit_oxidized_cut_copper_slab", () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> BIT_OXIDIZED_CUT_COPPER_WALL = BLOCKS
            .register("bit_oxidized_cut_copper_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_STEM)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.COPPER)));

    public static final RegistryObject<Block> MOSS_LAYERS = BLOCKS.register(
            "moss_layers",
            () -> new MossLayersBlock(
                    BlockBehaviour.Properties.of(
                            Material.REPLACEABLE_PLANT,
                            MaterialColor.COLOR_GREEN)
                            .strength(0.4f)
                            .sound(SoundType.MOSS)
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
                            .sound(SoundType.MOSS)
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
                            .sound(SoundType.SNOW)
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
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLUE_CONCRETE_STAIRS = BLOCKS.register("blue_concrete_stairs",
            () -> new ModStairBlock(
                    Blocks.BLUE_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BROWN_CONCRETE_STAIRS = BLOCKS.register("brown_concrete_stairs",
            () -> new ModStairBlock(
                    Blocks.BROWN_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> CYAN_CONCRETE_STAIRS = BLOCKS.register("cyan_concrete_stairs",
            () -> new ModStairBlock(
                    Blocks.CYAN_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GRAY_CONCRETE_STAIRS = BLOCKS.register("gray_concrete_stairs",
            () -> new ModStairBlock(
                    Blocks.GRAY_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GREEN_CONCRETE_STAIRS = BLOCKS.register("green_concrete_stairs",
            () -> new ModStairBlock(
                    Blocks.GREEN_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> LIGHT_BLUE_CONCRETE_STAIRS = BLOCKS
            .register("light_blue_concrete_stairs", () -> new ModStairBlock(
                    Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> LIGHT_GRAY_CONCRETE_STAIRS = BLOCKS
            .register("light_gray_concrete_stairs", () -> new ModStairBlock(
                    Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> LIME_CONCRETE_STAIRS = BLOCKS.register("lime_concrete_stairs",
            () -> new ModStairBlock(
                    Blocks.LIME_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> MAGENTA_CONCRETE_STAIRS = BLOCKS.register("magenta_concrete_stairs",
            () -> new ModStairBlock(
                    Blocks.MAGENTA_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_MAGENTA)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> ORANGE_CONCRETE_STAIRS = BLOCKS.register("orange_concrete_stairs",
            () -> new ModStairBlock(
                    Blocks.ORANGE_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PINK_CONCRETE_STAIRS = BLOCKS.register("pink_concrete_stairs",
            () -> new ModStairBlock(
                    Blocks.PINK_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PURPLE_CONCRETE_STAIRS = BLOCKS.register("purple_concrete_stairs",
            () -> new ModStairBlock(
                    Blocks.PURPLE_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> RED_CONCRETE_STAIRS = BLOCKS.register("red_concrete_stairs",
            () -> new ModStairBlock(
                    Blocks.RED_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> WHITE_CONCRETE_STAIRS = BLOCKS.register("white_concrete_stairs",
            () -> new ModStairBlock(
                    Blocks.WHITE_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> YELLOW_CONCRETE_STAIRS = BLOCKS.register("yellow_concrete_stairs",
            () -> new ModStairBlock(
                    Blocks.YELLOW_CONCRETE.defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));

    public static final RegistryObject<Block> WHITE_CONCRETE_WALL = BLOCKS.register("white_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> LIGHT_GRAY_CONCRETE_WALL = BLOCKS.register("light_gray_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GRAY_CONCRETE_WALL = BLOCKS.register("gray_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLACK_CONCRETE_WALL = BLOCKS.register("black_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BROWN_CONCRETE_WALL = BLOCKS.register("brown_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> RED_CONCRETE_WALL = BLOCKS.register(
            "red_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> ORANGE_CONCRETE_WALL = BLOCKS.register("orange_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> YELLOW_CONCRETE_WALL = BLOCKS.register("yellow_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> LIME_CONCRETE_WALL = BLOCKS.register("lime_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GREEN_CONCRETE_WALL = BLOCKS.register("green_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> CYAN_CONCRETE_WALL = BLOCKS.register("cyan_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> LIGHT_BLUE_CONCRETE_WALL = BLOCKS.register("light_blue_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLUE_CONCRETE_WALL = BLOCKS.register("blue_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PURPLE_CONCRETE_WALL = BLOCKS.register("purple_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> MAGENTA_CONCRETE_WALL = BLOCKS.register("magenta_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_MAGENTA)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PINK_CONCRETE_WALL = BLOCKS.register("pink_concrete_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));

    public static final RegistryObject<Block> BLACK_CONCRETE_SLAB = BLOCKS.register("black_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BLUE_CONCRETE_SLAB = BLOCKS.register("blue_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> BROWN_CONCRETE_SLAB = BLOCKS.register("brown_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> CYAN_CONCRETE_SLAB = BLOCKS.register("cyan_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GRAY_CONCRETE_SLAB = BLOCKS.register("gray_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> GREEN_CONCRETE_SLAB = BLOCKS.register("green_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> LIGHT_BLUE_CONCRETE_SLAB = BLOCKS.register("light_blue_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> LIGHT_GRAY_CONCRETE_SLAB = BLOCKS.register("light_gray_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> LIME_CONCRETE_SLAB = BLOCKS.register("lime_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> MAGENTA_CONCRETE_SLAB = BLOCKS.register("magenta_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.STONE,
                            MaterialColor.COLOR_MAGENTA)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> ORANGE_CONCRETE_SLAB = BLOCKS.register("orange_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PINK_CONCRETE_SLAB = BLOCKS.register("pink_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> PURPLE_CONCRETE_SLAB = BLOCKS.register("purple_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> RED_CONCRETE_SLAB = BLOCKS.register(
            "red_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> WHITE_CONCRETE_SLAB = BLOCKS.register("white_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SNOW)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));
    public static final RegistryObject<Block> YELLOW_CONCRETE_SLAB = BLOCKS.register("yellow_concrete_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .strength(1.8f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE)));

    public static final RegistryObject<Block> BROWN_MUSHROOM_SHELVES = BLOCKS.register("brown_mushroom_shelves",
            () -> new MushroomShelvesBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.3f)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<Block> RED_MUSHROOM_SHELVES = BLOCKS.register("red_mushroom_shelves",
            () -> new MushroomShelvesBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                            .strength(0.3f)
                            .sound(SoundType.WOOD)
                            .noOcclusion()));

    public static final RegistryObject<Block> QUARTZ_PILLAR = BLOCKS.register(
            "quartz_pillar",
            () -> new PillarBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                            .strength(0.8f)
                            .sound(SoundType.STONE)
                            .noOcclusion()));

    public static final RegistryObject<Block> STONE_PILLAR = BLOCKS.register(
            "stone_pillar",
            () -> new PillarBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE)
                            .strength(1.5f, 6.0f)
                            .sound(SoundType.STONE)
                            .noOcclusion()));

    public static final RegistryObject<Block> DEEPSLATE_PILLAR = BLOCKS.register(
            "deepslate_pillar",
            () -> new PillarBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(3.0f, 6.0f)
                            .sound(SoundType.DEEPSLATE)
                            .noOcclusion()));

    public static final RegistryObject<Block> MOSSY_PILLAR = BLOCKS.register(
            "mossy_pillar",
            () -> new PillarBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_GREEN)
                            .strength(2.0f, 6.0f)
                            .sound(SoundType.STONE)
                            .noOcclusion()));

    public static final RegistryObject<Block> AMETHYST_PILLAR = BLOCKS.register("amethyst_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.AMETHYST, MaterialColor.COLOR_PURPLE)
                    .strength(1.5f, 6.0f).sound(SoundType.AMETHYST).noOcclusion()));
    public static final RegistryObject<Block> ANDESITE_PILLAR = BLOCKS.register("andesite_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).strength(1.5f, 6.0f)
                    .sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<Block> BASALT_PILLAR = BLOCKS.register("basalt_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                    .strength(1.25f, 4.2f).sound(SoundType.BASALT).noOcclusion()));
    public static final RegistryObject<Block> BLACKSTONE_PILLAR = BLOCKS.register("blackstone_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                    .strength(1.5f, 6.0f).sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<Block> BLUE_ICE_PILLAR = BLOCKS.register("blue_ice_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.ICE, MaterialColor.COLOR_LIGHT_BLUE)
                    .strength(2.8f, 2.8f).friction(0.989f).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistryObject<Block> CALCITE_PILLAR = BLOCKS.register("calcite_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_WHITE)
                    .strength(0.75f, 0.75f).sound(SoundType.CALCITE).noOcclusion()));
    public static final RegistryObject<Block> CINNABAR_PILLAR = BLOCKS.register("cinnabar_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED)
                    .strength(1.5f, 6.0f).sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<Block> COPPER_PILLAR = BLOCKS.register("copper_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                    .strength(3.0f, 6.0f).sound(SoundType.COPPER).noOcclusion()));
    public static final RegistryObject<Block> DARK_PRISMARINE_PILLAR = BLOCKS.register("dark_prismarine_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIAMOND)
                    .strength(1.5f, 6.0f).sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<Block> DIORITE_PILLAR = BLOCKS.register("diorite_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
                    .strength(1.5f, 6.0f).sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<Block> DRIPSTONE_PILLAR = BLOCKS.register("dripstone_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_BROWN)
                    .strength(1.5f, 1.0f).sound(SoundType.DRIPSTONE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> EXPOSED_COPPER_PILLAR = BLOCKS.register("exposed_copper_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.TERRACOTTA_LIGHT_BLUE)
                    .strength(3.0f, 6.0f).sound(SoundType.COPPER).noOcclusion()));
    public static final RegistryObject<Block> GRANITE_PILLAR = BLOCKS.register("granite_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIRT).strength(1.5f, 6.0f)
                    .sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<Block> NETHERRACK_PILLAR = BLOCKS.register("netherrack_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.NETHER)
                    .strength(0.4f, 0.4f).sound(SoundType.NETHERRACK).noOcclusion()));
    public static final RegistryObject<Block> OBSIDIAN_PILLAR = BLOCKS.register("obsidian_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                    .strength(50.0f, 1200.0f).sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<Block> OXIDIZED_COPPER_PILLAR = BLOCKS.register("oxidized_copper_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_NYLIUM)
                    .strength(3.0f, 6.0f).sound(SoundType.COPPER).noOcclusion()));
    public static final RegistryObject<Block> PACKED_MUD_PILLAR = BLOCKS.register("packed_mud_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.TERRACOTTA_BROWN)
                    .strength(1.0f, 3.0f).sound(SoundType.GRAVEL).noOcclusion()));
    public static final RegistryObject<Block> PRISMARINE_PILLAR = BLOCKS.register("prismarine_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_CYAN)
                    .strength(1.5f, 6.0f).sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<Block> SCULK_PILLAR = BLOCKS.register("sculk_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.SCULK, MaterialColor.COLOR_BLACK)
                    .strength(0.6f, 0.6f).sound(SoundType.SCULK_SENSOR).noOcclusion()));
    public static final RegistryObject<Block> SULFUR_PILLAR = BLOCKS.register("sulfur_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW)
                    .strength(1.5f, 6.0f).sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<Block> TUFF_PILLAR = BLOCKS.register("tuff_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_GRAY)
                    .strength(1.5f, 6.0f).sound(SoundType.TUFF).noOcclusion()));
    public static final RegistryObject<Block> WEATHERED_COPPER_PILLAR = BLOCKS.register("weathered_copper_pillar",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_BLUE)
                    .strength(3.0f, 6.0f).sound(SoundType.COPPER).noOcclusion()));

    public static final RegistryObject<Block> ASHENKING_DIAMOND_PILLAR = BLOCKS.register(
            "ashenking_diamond_pillar",
            () -> new AshenKingPillarBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DIAMOND)
                            .strength(3.0f, 6.0f)
                            .sound(SoundType.STONE)
                            .lightLevel(state -> 15)
                            .noOcclusion()));

    public static final RegistryObject<Block> ASHENKING_GOLD_PILLAR = BLOCKS.register(
            "ashenking_gold_pillar",
            () -> new AshenKingPillarBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.GOLD)
                            .strength(3.0f, 6.0f)
                            .sound(SoundType.STONE)
                            .lightLevel(state -> 15)
                            .noOcclusion()));

    public static final RegistryObject<Block> ASHENKING_EMERALD_PILLAR = BLOCKS.register(
            "ashenking_emerald_pillar",
            () -> new AshenKingPillarBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.EMERALD)
                            .strength(3.0f, 6.0f)
                            .sound(SoundType.STONE)
                            .lightLevel(state -> 15)
                            .noOcclusion()));

    public static final RegistryObject<Block> ASHENKING_NETHERITE_PILLAR = BLOCKS.register(
            "ashenking_netherite_pillar",
            () -> new AshenKingPillarBlock(
                    BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                            .strength(5.0f, 6.0f)
                            .sound(SoundType.STONE)
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
}
