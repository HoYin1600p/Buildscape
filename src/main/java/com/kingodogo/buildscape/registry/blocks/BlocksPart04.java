package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.block.BigCandleBlock;
import com.kingodogo.buildscape.block.BigOrnamentBlock;
import com.kingodogo.buildscape.block.ClimbableChainBlock;
import com.kingodogo.buildscape.block.CloverBlock;
import com.kingodogo.buildscape.block.ColoredSporeBlossomBlock;
import com.kingodogo.buildscape.block.FestiveLampBlock;
import com.kingodogo.buildscape.block.FestiveStockingBlock;
import com.kingodogo.buildscape.block.FrostRoseBlock;
import com.kingodogo.buildscape.block.GlowLightsBlock;
import com.kingodogo.buildscape.block.IceCrystalBlock;
import com.kingodogo.buildscape.block.LargeChainBlock;
import com.kingodogo.buildscape.block.MangroveLeavesBlock;
import com.kingodogo.buildscape.block.MangrovePropaguleBlock;
import com.kingodogo.buildscape.block.MangroveRootsBlock;
import com.kingodogo.buildscape.block.ModBlock;
import com.kingodogo.buildscape.block.ModDoorBlock;
import com.kingodogo.buildscape.block.ModStairBlock;
import com.kingodogo.buildscape.block.ModStandingSignBlock;
import com.kingodogo.buildscape.block.ModTrapdoorBlock;
import com.kingodogo.buildscape.block.ModWallSignBlock;
import com.kingodogo.buildscape.block.ModWoodTypes;
import com.kingodogo.buildscape.block.MonetFlowerBlock;
import com.kingodogo.buildscape.block.MulticolorGlowLightsBlock;
import com.kingodogo.buildscape.block.OrnamentBlock;
import com.kingodogo.buildscape.block.PetalBlock;
import com.kingodogo.buildscape.block.RoseVinesBlock;
import com.kingodogo.buildscape.block.SnowyBushBlock;
import com.kingodogo.buildscape.block.SnowyFernBlock;
import com.kingodogo.buildscape.block.SnowyGrassBlock;
import com.kingodogo.buildscape.block.SnowyLargeFernBlock;
import com.kingodogo.buildscape.block.SnowyLeavesBlock;
import com.kingodogo.buildscape.block.SnowyShortGrassBlock;
import com.kingodogo.buildscape.block.SnowyTallGrassBlock;
import com.kingodogo.buildscape.block.StarBlock;
import com.kingodogo.buildscape.block.SteelBoltBlock;
import com.kingodogo.buildscape.block.StringLightBlock;
import com.kingodogo.buildscape.block.WeatheringClimbableChainBlock;
import com.kingodogo.buildscape.feature.icicle.IcicleBlock;
import com.kingodogo.buildscape.feature.icicle.IcicleCauldronBlock;
import com.kingodogo.buildscape.feature.icicle.PackedIcicleBlock;
import com.kingodogo.buildscape.feature.icicle.PointedIcicleBlock;
import com.kingodogo.buildscape.feature.pipe.HollowPipeBlock;
import com.kingodogo.buildscape.feature.pipe.PipeBlock;
import com.kingodogo.buildscape.registry.ModSounds;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WoodButtonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.ModBlocks.BLOCKS;

/** Part of the blocks registrations; see {@code ModBlocks}. */
public final class BlocksPart04 {

    private BlocksPart04() {
    }

    /** Forces this class to load so its registrations run in the order {@code ModBlocks} calls it. */
    public static void init() {
    }

        public static final RegistryObject<Block> EMERALD_CHAIN = BLOCKS.register(
                        "emerald_chain",
                        () -> new ClimbableChainBlock(
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.EMERALD)
                                                        .strength(5.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.CHAIN)
                                                        .noOcclusion()
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> ANCIENT_STEEL_CHAIN = BLOCKS.register("ancient_steel_chain",
                        () -> new ClimbableChainBlock(
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                                        .strength(5.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.CHAIN)
                                                        .noOcclusion()
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> NETHERITE_CHAIN = BLOCKS.register(
                        "netherite_chain",
                        () -> new ClimbableChainBlock(
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
                                                        .strength(50.0f, 1200.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.CHAIN)
                                                        .noOcclusion()
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> LARGE_IRON_CHAIN = BLOCKS.register(
                        "large_iron_chain",
                        () -> new LargeChainBlock(
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL)
                                                        .strength(5.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.CHAIN)
                                                        .noOcclusion()
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> LARGE_GOLD_CHAIN = BLOCKS.register(
                        "large_gold_chain",
                        () -> new LargeChainBlock(
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.GOLD)
                                                        .strength(3.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.CHAIN)
                                                        .noOcclusion()
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> LARGE_DIAMOND_CHAIN = BLOCKS.register("large_diamond_chain",
                        () -> new LargeChainBlock(
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.DIAMOND)
                                                        .strength(5.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.CHAIN)
                                                        .noOcclusion()
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> LARGE_EMERALD_CHAIN = BLOCKS.register("large_emerald_chain",
                        () -> new LargeChainBlock(
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.EMERALD)
                                                        .strength(5.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.CHAIN)
                                                        .noOcclusion()
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> LARGE_ANCIENT_STEEL_CHAIN = BLOCKS
                        .register("large_ancient_steel_chain", () -> new LargeChainBlock(
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
                                                        .strength(5.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.CHAIN)
                                                        .noOcclusion()
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> LARGE_NETHERITE_CHAIN = BLOCKS.register("large_netherite_chain",
                        () -> new LargeChainBlock(
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
                                                        .strength(50.0f, 1200.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.CHAIN)
                                                        .noOcclusion()
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> COPPER_CHAIN = BLOCKS.register(
                        "copper_chain",
                        () -> new WeatheringClimbableChainBlock(
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                                                        .strength(3.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.CHAIN)
                                                        .noOcclusion()
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> EXPOSED_COPPER_CHAIN = BLOCKS.register("exposed_copper_chain",
                        () -> new WeatheringClimbableChainBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.METAL,
                                                        MaterialColor.TERRACOTTA_ORANGE)
                                                        .strength(3.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.CHAIN)
                                                        .noOcclusion()
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> WEATHERED_COPPER_CHAIN = BLOCKS.register("weathered_copper_chain",
                        () -> new WeatheringClimbableChainBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.METAL,
                                                        MaterialColor.WARPED_WART_BLOCK)
                                                        .strength(3.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.CHAIN)
                                                        .noOcclusion()
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> OXIDIZED_COPPER_CHAIN = BLOCKS.register("oxidized_copper_chain",
                        () -> new WeatheringClimbableChainBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.METAL,
                                                        MaterialColor.WARPED_NYLIUM)
                                                        .strength(3.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.CHAIN)
                                                        .noOcclusion()
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> RED_ROSE_VINES = BLOCKS.register(
                        "red_rose_vines",
                        () -> new RoseVinesBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.REPLACEABLE_PLANT,
                                                        MaterialColor.COLOR_RED)
                                                        .strength(0.2f)
                                                        .sound(ModSounds.VINE_SOUNDS())
                                                        .noCollission()
                                                        .noOcclusion()));
        public static final RegistryObject<Block> BLACK_ROSE_VINES = BLOCKS.register(
                        "black_rose_vines",
                        () -> new RoseVinesBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.REPLACEABLE_PLANT,
                                                        MaterialColor.COLOR_BLACK)
                                                        .strength(0.2f)
                                                        .sound(ModSounds.VINE_SOUNDS())
                                                        .noCollission()
                                                        .noOcclusion()));
        public static final RegistryObject<Block> BLUE_ROSE_VINES = BLOCKS.register(
                        "blue_rose_vines",
                        () -> new RoseVinesBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.REPLACEABLE_PLANT,
                                                        MaterialColor.COLOR_BLUE)
                                                        .strength(0.2f)
                                                        .sound(ModSounds.VINE_SOUNDS())
                                                        .noCollission()
                                                        .noOcclusion()));
        public static final RegistryObject<Block> WHITE_ROSE_VINES = BLOCKS.register(
                        "white_rose_vines",
                        () -> new RoseVinesBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.REPLACEABLE_PLANT,
                                                        MaterialColor.SNOW)
                                                        .strength(0.2f)
                                                        .sound(ModSounds.VINE_SOUNDS())
                                                        .noCollission()
                                                        .noOcclusion()));
        public static final RegistryObject<Block> RED_MONETS = BLOCKS.register(
                        "red_monets",
                        () -> new MonetFlowerBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_RED)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> BLUE_MONETS = BLOCKS.register(
                        "blue_monets",
                        () -> new MonetFlowerBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BLUE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> PURPLE_MONETS = BLOCKS.register(
                        "purple_monets",
                        () -> new MonetFlowerBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PURPLE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> LIGHT_BLUE_MONETS = BLOCKS.register(
                        "light_blue_monets",
                        () -> new MonetFlowerBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.PLANT,
                                                        MaterialColor.COLOR_LIGHT_BLUE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> PINK_MONETS = BLOCKS.register(
                        "pink_monets",
                        () -> new MonetFlowerBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PINK)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> YELLOW_MONETS = BLOCKS.register(
                        "yellow_monets",
                        () -> new MonetFlowerBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_YELLOW)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> CLOVER = BLOCKS.register(
                        "clover",
                        () -> new CloverBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> RED_PETAL = BLOCKS.register(
                        "red_petal",
                        () -> new PetalBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_RED)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> BLUE_PETAL = BLOCKS.register(
                        "blue_petal",
                        () -> new PetalBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BLUE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> ORANGE_PETAL = BLOCKS.register(
                        "orange_petal",
                        () -> new PetalBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_ORANGE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> PINK_PETAL = BLOCKS.register(
                        "pink_petal",
                        () -> new PetalBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PINK)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> PURPLE_PETAL = BLOCKS.register(
                        "purple_petal",
                        () -> new PetalBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PURPLE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> RED_SPORE_BLOSSOM = BLOCKS.register(
                        "red_spore_blossom",
                        () -> new ColoredSporeBlossomBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_RED)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.SPORE_BLOSSOM),
                                        ColoredSporeBlossomBlock.hexToVector3f("#fe4a4e"),
                                        ColoredSporeBlossomBlock.hexToRGB("#fe4a4e")));
        public static final RegistryObject<Block> CYAN_SPORE_BLOSSOM = BLOCKS.register("cyan_spore_blossom",
                        () -> new ColoredSporeBlossomBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_CYAN)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.SPORE_BLOSSOM),
                                        ColoredSporeBlossomBlock.hexToVector3f("#1fdee2"),
                                        ColoredSporeBlossomBlock.hexToRGB("#1fdee2")));
        public static final RegistryObject<Block> BLUE_SPORE_BLOSSOM = BLOCKS.register("blue_spore_blossom",
                        () -> new ColoredSporeBlossomBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BLUE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.SPORE_BLOSSOM),
                                        ColoredSporeBlossomBlock.hexToVector3f("#4a7df4"),
                                        ColoredSporeBlossomBlock.hexToRGB("#4a7df4")));
        public static final RegistryObject<Block> PURPLE_SPORE_BLOSSOM = BLOCKS.register("purple_spore_blossom",
                        () -> new ColoredSporeBlossomBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PURPLE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.SPORE_BLOSSOM),
                                        ColoredSporeBlossomBlock.hexToVector3f("#a146f2"),
                                        ColoredSporeBlossomBlock.hexToRGB("#a146f2")));
        public static final RegistryObject<Block> ORANGE_SPORE_BLOSSOM = BLOCKS.register("orange_spore_blossom",
                        () -> new ColoredSporeBlossomBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_ORANGE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.SPORE_BLOSSOM),
                                        ColoredSporeBlossomBlock.hexToVector3f("#f76e32"),
                                        ColoredSporeBlossomBlock.hexToRGB("#f76e32")));
        public static final RegistryObject<Block> BIG_CANDLE = BLOCKS.register(
                        "big_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_WHITE_CANDLE = BLOCKS.register(
                        "big_white_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_ORANGE_CANDLE = BLOCKS.register(
                        "big_orange_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_MAGENTA_CANDLE = BLOCKS.register("big_magenta_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_LIGHT_BLUE_CANDLE = BLOCKS.register("big_light_blue_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_YELLOW_CANDLE = BLOCKS.register(
                        "big_yellow_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_LIME_CANDLE = BLOCKS.register(
                        "big_lime_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_PINK_CANDLE = BLOCKS.register(
                        "big_pink_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_GRAY_CANDLE = BLOCKS.register(
                        "big_gray_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_LIGHT_GRAY_CANDLE = BLOCKS.register("big_light_gray_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_CYAN_CANDLE = BLOCKS.register(
                        "big_cyan_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_PURPLE_CANDLE = BLOCKS.register(
                        "big_purple_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_BLUE_CANDLE = BLOCKS.register(
                        "big_blue_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_BROWN_CANDLE = BLOCKS.register(
                        "big_brown_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_GREEN_CANDLE = BLOCKS.register(
                        "big_green_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_RED_CANDLE = BLOCKS.register(
                        "big_red_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_BLACK_CANDLE = BLOCKS.register(
                        "big_black_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_AMETHYST_CANDLE = BLOCKS.register("big_amethyst_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> BIG_SCULK_CANDLE = BLOCKS.register(
                        "big_sculk_candle",
                        () -> new BigCandleBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .instabreak()
                                                        .sound(SoundType.CANDLE)
                                                        .lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? 12
                                                                        : 0)));
        public static final RegistryObject<Block> WHITE_ORNAMENT = BLOCKS.register(
                        "white_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> ORANGE_ORNAMENT = BLOCKS.register(
                        "orange_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> MAGENTA_ORNAMENT = BLOCKS.register(
                        "magenta_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.GLASS,
                                                        MaterialColor.COLOR_MAGENTA)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> LIGHT_BLUE_ORNAMENT = BLOCKS.register("light_blue_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.GLASS,
                                                        MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> YELLOW_ORNAMENT = BLOCKS.register(
                        "yellow_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> LIME_ORNAMENT = BLOCKS.register(
                        "lime_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.GLASS,
                                                        MaterialColor.COLOR_LIGHT_GREEN)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> PINK_ORNAMENT = BLOCKS.register(
                        "pink_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> GRAY_ORNAMENT = BLOCKS.register(
                        "gray_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> LIGHT_GRAY_ORNAMENT = BLOCKS.register("light_gray_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.GLASS,
                                                        MaterialColor.COLOR_LIGHT_GRAY)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> CYAN_ORNAMENT = BLOCKS.register(
                        "cyan_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> PURPLE_ORNAMENT = BLOCKS.register(
                        "purple_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> BLUE_ORNAMENT = BLOCKS.register(
                        "blue_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> BROWN_ORNAMENT = BLOCKS.register(
                        "brown_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> GREEN_ORNAMENT = BLOCKS.register(
                        "green_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> RED_ORNAMENT = BLOCKS.register(
                        "red_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> BLACK_ORNAMENT = BLOCKS.register(
                        "black_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> GLASS_ORNAMENT = BLOCKS.register(
                        "glass_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.NONE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> 7)));
        public static final RegistryObject<Block> TINTED_GLASS_ORNAMENT = BLOCKS.register("tinted_glass_ornament",
                        () -> new OrnamentBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.NONE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .dynamicShape()
                                                        .lightLevel(state -> 4), true));
        public static final RegistryObject<Block> BIG_WHITE_ORNAMENT = BLOCKS.register("big_white_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_ORANGE_ORNAMENT = BLOCKS.register("big_orange_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_MAGENTA_ORNAMENT = BLOCKS.register("big_magenta_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_MAGENTA).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_LIGHT_BLUE_ORNAMENT = BLOCKS.register("big_light_blue_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_LIGHT_BLUE).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_YELLOW_ORNAMENT = BLOCKS.register("big_yellow_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_LIME_ORNAMENT = BLOCKS.register("big_lime_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_LIGHT_GREEN).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_PINK_ORNAMENT = BLOCKS.register("big_pink_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_GRAY_ORNAMENT = BLOCKS.register("big_gray_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_LIGHT_GRAY_ORNAMENT = BLOCKS.register("big_light_gray_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_LIGHT_GRAY).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_CYAN_ORNAMENT = BLOCKS.register("big_cyan_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_PURPLE_ORNAMENT = BLOCKS.register("big_purple_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_BLUE_ORNAMENT = BLOCKS.register("big_blue_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_BROWN_ORNAMENT = BLOCKS.register("big_brown_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_GREEN_ORNAMENT = BLOCKS.register("big_green_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_RED_ORNAMENT = BLOCKS.register("big_red_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_BLACK_ORNAMENT = BLOCKS.register("big_black_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_GLASS_ORNAMENT = BLOCKS.register("big_glass_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.NONE).strength(0.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BIG_TINTED_GLASS_ORNAMENT = BLOCKS.register("big_tinted_glass_ornament",
                        () -> new BigOrnamentBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.NONE).strength(0.3f).sound(SoundType.GLASS).noOcclusion().dynamicShape().lightLevel(state -> state.getValue(BigOrnamentBlock.LIT) ? 4 : 0), true));
        public static final RegistryObject<Block> WHITE_STRING_LIGHT = BLOCKS.register("white_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> ORANGE_STRING_LIGHT = BLOCKS.register("orange_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> MAGENTA_STRING_LIGHT = BLOCKS.register("magenta_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.GLASS,
                                                        MaterialColor.COLOR_MAGENTA)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> LIGHT_BLUE_STRING_LIGHT = BLOCKS.register("light_blue_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.GLASS,
                                                        MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> YELLOW_STRING_LIGHT = BLOCKS.register("yellow_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> LIME_STRING_LIGHT = BLOCKS.register(
                        "lime_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.GLASS,
                                                        MaterialColor.COLOR_LIGHT_GREEN)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> PINK_STRING_LIGHT = BLOCKS.register(
                        "pink_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> GRAY_STRING_LIGHT = BLOCKS.register(
                        "gray_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> LIGHT_GRAY_STRING_LIGHT = BLOCKS.register("light_gray_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.GLASS,
                                                        MaterialColor.COLOR_LIGHT_GRAY)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> CYAN_STRING_LIGHT = BLOCKS.register(
                        "cyan_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> PURPLE_STRING_LIGHT = BLOCKS.register("purple_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> BLUE_STRING_LIGHT = BLOCKS.register(
                        "blue_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> BROWN_STRING_LIGHT = BLOCKS.register("brown_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> GREEN_STRING_LIGHT = BLOCKS.register("green_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> RED_STRING_LIGHT = BLOCKS.register(
                        "red_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> BLACK_STRING_LIGHT = BLOCKS.register("black_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> MULTICOLOR_STRING_LIGHT = BLOCKS.register("multicolor_string_light",
                        () -> new StringLightBlock(
                                        BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.NONE)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .lightLevel(state -> state.getValue(StringLightBlock.LIT) ? 15
                                                                        : 0)));
        public static final RegistryObject<Block> WHITE_STAR = BLOCKS.register(
                        "white_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> ORANGE_STAR = BLOCKS.register(
                        "orange_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_ORANGE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> MAGENTA_STAR = BLOCKS.register(
                        "magenta_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.PLANT,
                                                        MaterialColor.COLOR_MAGENTA)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> LIGHT_BLUE_STAR = BLOCKS.register(
                        "light_blue_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.PLANT,
                                                        MaterialColor.COLOR_LIGHT_BLUE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> YELLOW_STAR = BLOCKS.register(
                        "yellow_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_YELLOW)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> LIME_STAR = BLOCKS.register(
                        "lime_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.PLANT,
                                                        MaterialColor.COLOR_LIGHT_GREEN)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> PINK_STAR = BLOCKS.register(
                        "pink_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PINK)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> GRAY_STAR = BLOCKS.register(
                        "gray_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_GRAY)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> LIGHT_GRAY_STAR = BLOCKS.register(
                        "light_gray_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.PLANT,
                                                        MaterialColor.COLOR_LIGHT_GRAY)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> CYAN_STAR = BLOCKS.register(
                        "cyan_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_CYAN)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> PURPLE_STAR = BLOCKS.register(
                        "purple_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PURPLE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BLUE_STAR = BLOCKS.register(
                        "blue_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BLUE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BROWN_STAR = BLOCKS.register(
                        "brown_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BROWN)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> GREEN_STAR = BLOCKS.register(
                        "green_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_GREEN)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> RED_STAR = BLOCKS.register(
                        "red_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_RED)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> BLACK_STAR = BLOCKS.register(
                        "black_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BLACK)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> GLOW_STAR = BLOCKS.register(
                        "glow_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.NONE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> FESTIVE_STAR = BLOCKS.register(
                        "festive_star",
                        () -> new StarBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_RED)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(StarBlock.LIT) ? 15 : 0)));
        public static final RegistryObject<Block> SNOWY_LEAVES = BLOCKS.register(
                        "snowy_leaves",
                        () -> new SnowyLeavesBlock(
                                        BlockBehaviour.Properties.of(Material.LEAVES)
                                                        .strength(0.2f)
                                                        .sound(SoundType.GRASS)
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_OAK_LEAVES = BLOCKS.register(
                        "snowy_oak_leaves",
                        () -> new SnowyLeavesBlock(
                                        BlockBehaviour.Properties.of(Material.LEAVES)
                                                        .strength(0.2f)
                                                        .sound(SoundType.GRASS)
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_SPRUCE_LEAVES = BLOCKS.register("snowy_spruce_leaves",
                        () -> new SnowyLeavesBlock(
                                        BlockBehaviour.Properties.of(Material.LEAVES)
                                                        .strength(0.2f)
                                                        .sound(SoundType.GRASS)
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_BIRCH_LEAVES = BLOCKS.register("snowy_birch_leaves",
                        () -> new SnowyLeavesBlock(
                                        BlockBehaviour.Properties.of(Material.LEAVES)
                                                        .strength(0.2f)
                                                        .sound(SoundType.GRASS)
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_JUNGLE_LEAVES = BLOCKS.register("snowy_jungle_leaves",
                        () -> new SnowyLeavesBlock(
                                        BlockBehaviour.Properties.of(Material.LEAVES)
                                                        .strength(0.2f)
                                                        .sound(SoundType.GRASS)
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_ACACIA_LEAVES = BLOCKS.register("snowy_acacia_leaves",
                        () -> new SnowyLeavesBlock(
                                        BlockBehaviour.Properties.of(Material.LEAVES)
                                                        .strength(0.2f)
                                                        .sound(SoundType.GRASS)
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_DARK_OAK_LEAVES = BLOCKS.register("snowy_dark_oak_leaves",
                        () -> new SnowyLeavesBlock(
                                        BlockBehaviour.Properties.of(Material.LEAVES)
                                                        .strength(0.2f)
                                                        .sound(SoundType.GRASS)
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_MANGROVE_LEAVES = BLOCKS.register("snowy_mangrove_leaves",
                        () -> new SnowyLeavesBlock(
                                        BlockBehaviour.Properties.of(Material.LEAVES)
                                                        .strength(0.2f)
                                                        .sound(SoundType.GRASS)
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_AZALEA_LEAVES = BLOCKS.register("snowy_azalea_leaves",
                        () -> new SnowyLeavesBlock(
                                        BlockBehaviour.Properties.of(Material.LEAVES)
                                                        .strength(0.2f)
                                                        .sound(ModSounds.AZALEA_SOUNDS())
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_FLOWERING_AZALEA_LEAVES = BLOCKS
                        .register("snowy_flowering_azalea_leaves", () -> new SnowyLeavesBlock(
                                        BlockBehaviour.Properties.of(Material.LEAVES)
                                                        .strength(0.2f)
                                                        .sound(ModSounds.AZALEA_SOUNDS())
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));

        public static final RegistryObject<Block> SNOWY_CHERRY_LEAVES = BLOCKS.register("snowy_cherry_leaves",
                        () -> new SnowyLeavesBlock(
                                        BlockBehaviour.Properties.of(Material.LEAVES)
                                                        .strength(0.2f)
                                                        .sound(SoundType.GRASS)
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));

        public static final RegistryObject<Block> SNOWY_PALE_OAK_LEAVES = BLOCKS.register("snowy_pale_oak_leaves",
                        () -> new SnowyLeavesBlock(
                                        BlockBehaviour.Properties.of(Material.LEAVES)
                                                        .strength(0.2f)
                                                        .sound(SoundType.GRASS)
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> SNOWY_SHORT_GRASS = BLOCKS.register(
                        "snowy_short_grass",
                        () -> new SnowyShortGrassBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> SNOWY_TALL_GRASS = BLOCKS.register(
                        "snowy_tall_grass",
                        () -> new SnowyTallGrassBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.REPLACEABLE_PLANT,
                                                        MaterialColor.SNOW)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> SNOWY_FERN = BLOCKS.register(
                        "snowy_fern",
                        () -> new SnowyFernBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> SNOWY_LARGE_FERN = BLOCKS.register(
                        "snowy_large_fern",
                        () -> new SnowyLargeFernBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.REPLACEABLE_PLANT,
                                                        MaterialColor.SNOW)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> SNOWY_BUSH = BLOCKS.register(
                        "snowy_bush",
                        () -> new SnowyBushBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> GLOW_LIGHTS = BLOCKS.register(
                        "glow_lights",
                        () -> new GlowLightsBlock(
                                        BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT)
                                                        .strength(0.2f)
                                                        .sound(SoundType.SLIME_BLOCK)
                                                        .lightLevel(state -> state.getValue(GlowLightsBlock.LIT) ? 15
                                                                        : 0)
                                                        .noOcclusion()
                                                        .noCollission()
                                                        .instabreak()));
        public static final RegistryObject<Block> MULTICOLOR_GLOW_LIGHTS = BLOCKS.register("multicolor_glow_lights",
                        () -> new MulticolorGlowLightsBlock(
                                        BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT)
                                                        .strength(0.2f)
                                                        .sound(SoundType.SLIME_BLOCK)
                                                        .lightLevel(state -> state.getValue(
                                                                        MulticolorGlowLightsBlock.LIT) ? 15 : 0)
                                                        .noOcclusion()
                                                        .noCollission()
                                                        .instabreak()));
        public static final RegistryObject<Block> SNOW_BRICKS = BLOCKS.register(
                        "snow_bricks",
                        () -> new ModBlock(
                                        BlockBehaviour.Properties.of(Material.SNOW, MaterialColor.SNOW)
                                                        .strength(0.2f)
                                                        .sound(SoundType.SNOW)));
        public static final RegistryObject<Block> SNOW_BRICKS_STAIRS = BLOCKS.register("snow_bricks_stairs",
                        () -> new ModStairBlock(
                                        SNOW_BRICKS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.SNOW, MaterialColor.SNOW)
                                                        .strength(0.2f)
                                                        .sound(SoundType.SNOW)));
        public static final RegistryObject<Block> SNOW_BRICKS_SLAB = BLOCKS.register(
                        "snow_bricks_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.SNOW, MaterialColor.SNOW)
                                                        .strength(0.2f)
                                                        .sound(SoundType.SNOW)));
        public static final RegistryObject<Block> SNOW_BRICKS_WALL = BLOCKS.register(
                        "snow_bricks_wall",
                        () -> new WallBlock(
                                        BlockBehaviour.Properties.of(Material.SNOW, MaterialColor.SNOW)
                                                        .strength(0.2f)
                                                        .sound(SoundType.SNOW)));
        public static final RegistryObject<Block> SNOW_STAIRS = BLOCKS.register(
                        "snow_stairs",
                        () -> new ModStairBlock(
                                        Blocks.SNOW_BLOCK.defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.SNOW, MaterialColor.SNOW)
                                                        .strength(0.1f)
                                                        .sound(SoundType.SNOW)));
        public static final RegistryObject<Block> SNOW_SLAB = BLOCKS.register(
                        "snow_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.SNOW, MaterialColor.SNOW)
                                                        .strength(0.1f)
                                                        .sound(SoundType.SNOW)));
        public static final RegistryObject<Block> SNOWY_GRASS_BLOCK = BLOCKS.register(
                        "snowy_grass_block",
                        () -> new SnowyGrassBlock(
                                        BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.GRASS)
                                                        .strength(0.6f)
                                                        .randomTicks()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> SNOWY_GRASS_BLOCK_STAIRS = BLOCKS.register("snowy_grass_block_stairs",
                        () -> new ModStairBlock(
                                        SNOWY_GRASS_BLOCK.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.GRASS)
                                                        .strength(0.6f)
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> SNOWY_GRASS_BLOCK_SLAB = BLOCKS.register("snowy_grass_block_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.GRASS)
                                                        .strength(0.6f)
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> ICICLE = BLOCKS.register(
                        "icicle",
                        () -> new PointedIcicleBlock(
                                        BlockBehaviour.Properties.of(Material.ICE, MaterialColor.ICE)
                                                        .strength(0.5f)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()
                                                        .randomTicks()
                                                        .dynamicShape()));
        public static final RegistryObject<Block> ICICLE_BLOCK = BLOCKS.register(
                        "icicle_block",
                        () -> new IcicleBlock(
                                        BlockBehaviour.Properties.of(Material.ICE, MaterialColor.ICE)
                                                        .strength(0.5f)
                                                        .friction(0.989F)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> PACKED_ICICLE_BLOCK = BLOCKS.register("packed_icicle_block",
                        () -> new PackedIcicleBlock(
                                        BlockBehaviour.Properties.of(Material.ICE, MaterialColor.ICE)
                                                        .strength(0.5f)
                                                        .friction(0.989F)
                                                        .sound(SoundType.GLASS)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> ICICLE_CAULDRON = BLOCKS.register(
                        "icicle_cauldron",
                        () -> new IcicleCauldronBlock(
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL)
                                                        .strength(2.0f)
                                                        .sound(SoundType.METAL)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> ICE_CRYSTAL = BLOCKS.register(
                        "ice_crystal",
                        () -> new IceCrystalBlock(
                                        BlockBehaviour.Properties.of(Material.AMETHYST, MaterialColor.COLOR_LIGHT_BLUE)
                                                        .strength(1.5f)
                                                        .sound(SoundType.AMETHYST_CLUSTER)
                                                        .lightLevel(state -> 3)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> MANGROVE_LOG = BLOCKS.register(
                        "mangrove_log",
                        () -> new RotatedPillarBlock(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_MANGROVE_LOG = BLOCKS.register("stripped_mangrove_log",
                        () -> new RotatedPillarBlock(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_WOOD = BLOCKS.register(
                        "mangrove_wood",
                        () -> new RotatedPillarBlock(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> STRIPPED_MANGROVE_WOOD = BLOCKS.register("stripped_mangrove_wood",
                        () -> new RotatedPillarBlock(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(2.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_LEAVES = BLOCKS.register(
                        "mangrove_leaves",
                        () -> new MangroveLeavesBlock(
                                        BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.COLOR_GREEN)
                                                        .strength(0.2f)
                                                        .randomTicks()
                                                        .sound(SoundType.GRASS)
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)
                                                        .isSuffocating((state, reader, pos) -> false)
                                                        .isViewBlocking((state, reader, pos) -> false)));
        public static final RegistryObject<Block> MANGROVE_ROOTS = BLOCKS.register(
                        "mangrove_roots",
                        () -> new MangroveRootsBlock(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(0.7f)
                                                        .sound(
                                                                        ModSounds
                                                                                        .MANGROVE_ROOTS_SOUNDS())
                                                        .noOcclusion()));
        public static final RegistryObject<Block> MUDDY_MANGROVE_ROOTS = BLOCKS.register("muddy_mangrove_roots",
                        () -> new MangroveRootsBlock(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                                                        .strength(0.7f)
                                                        .sound(
                                                                        ModSounds
                                                                                        .MUDDY_MANGROVE_ROOTS_SOUNDS())
                                                        .noOcclusion()));
        public static final RegistryObject<Block> MANGROVE_PROPAGULE = BLOCKS.register("mangrove_propagule",
                        () -> new MangrovePropaguleBlock(
                                        BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_GREEN)
                                                        .strength(0.0f)
                                                        .randomTicks()
                                                        .sound(SoundType.GRASS)
                                                        .noCollission()
                                                        .noOcclusion()));
        public static final RegistryObject<Block> MANGROVE_PLANKS = BLOCKS.register(
                        "mangrove_planks",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(2.0f, 3.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_STAIRS = BLOCKS.register(
                        "mangrove_stairs",
                        () -> new ModStairBlock(
                                        MANGROVE_PLANKS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(2.0f, 3.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_SLAB = BLOCKS.register(
                        "mangrove_slab",
                        () -> new SlabBlock(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(2.0f, 3.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_FENCE = BLOCKS.register(
                        "mangrove_fence",
                        () -> new FenceBlock(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(2.0f, 3.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_FENCE_GATE = BLOCKS.register("mangrove_fence_gate",
                        () -> new FenceGateBlock(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(2.0f, 3.0f)
                                                        .sound(SoundType.WOOD)));
        public static final RegistryObject<Block> MANGROVE_DOOR = BLOCKS.register(
                        "mangrove_door",
                        () -> new ModDoorBlock(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(3.0f)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()));
        public static final RegistryObject<Block> MANGROVE_TRAPDOOR = BLOCKS.register(
                        "mangrove_trapdoor",
                        () -> new ModTrapdoorBlock(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(3.0f)
                                                        .sound(SoundType.WOOD)
                                                        .noOcclusion()
                                                        .isValidSpawn((state, reader, pos, entityType) -> false)));
        public static final RegistryObject<Block> MANGROVE_BUTTON = BLOCKS.register(
                        "mangrove_button",
                        () -> new WoodButtonBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.DECORATION,
                                                        MaterialColor.COLOR_RED)
                                                        .strength(0.5f)
                                                        .sound(SoundType.WOOD)
                                                        .noCollission()));
        public static final RegistryObject<Block> MANGROVE_PRESSURE_PLATE = BLOCKS.register("mangrove_pressure_plate",
                        () -> new PressurePlateBlock(
                                        PressurePlateBlock.Sensitivity.EVERYTHING,
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(0.5f)
                                                        .sound(SoundType.WOOD)
                                                        .noCollission()));
        public static final RegistryObject<Block> MANGROVE_SIGN = BLOCKS.register(
                        "mangrove_sign",
                        () -> new ModStandingSignBlock(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(1.0f)
                                                        .sound(SoundType.WOOD)
                                                        .noCollission(),
                                        ModWoodTypes.MANGROVE));
        public static final RegistryObject<Block> MANGROVE_WALL_SIGN = BLOCKS.register("mangrove_wall_sign",
                        () -> new ModWallSignBlock(
                                        BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                                                        .strength(1.0f)
                                                        .sound(SoundType.WOOD)
                                                        .noCollission()
                                                        .dropsLike(MANGROVE_SIGN.get()),
                                        ModWoodTypes.MANGROVE));
        public static final RegistryObject<Block> FESTIVE_STOCKING = BLOCKS.register(
                        "festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "festive"));
        public static final RegistryObject<Block> BLACK_FESTIVE_STOCKING = BLOCKS.register("black_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "black"));
        public static final RegistryObject<Block> BLUE_FESTIVE_STOCKING = BLOCKS.register("blue_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "blue"));
        public static final RegistryObject<Block> BROWN_FESTIVE_STOCKING = BLOCKS.register("brown_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "brown"));
        public static final RegistryObject<Block> CYAN_FESTIVE_STOCKING = BLOCKS.register("cyan_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "cyan"));
        public static final RegistryObject<Block> GRAY_FESTIVE_STOCKING = BLOCKS.register("gray_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "gray"));
        public static final RegistryObject<Block> GREEN_FESTIVE_STOCKING = BLOCKS.register("green_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "green"));
        public static final RegistryObject<Block> LIGHT_BLUE_FESTIVE_STOCKING = BLOCKS
                        .register("light_blue_festive_stocking", () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "light_blue"));
        public static final RegistryObject<Block> LIGHT_GRAY_FESTIVE_STOCKING = BLOCKS
                        .register("light_gray_festive_stocking", () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "light_gray"));
        public static final RegistryObject<Block> LIME_FESTIVE_STOCKING = BLOCKS.register("lime_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "lime"));
        public static final RegistryObject<Block> MAGENTA_FESTIVE_STOCKING = BLOCKS.register("magenta_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "magenta"));
        public static final RegistryObject<Block> ORANGE_FESTIVE_STOCKING = BLOCKS.register("orange_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "orange"));
        public static final RegistryObject<Block> PINK_FESTIVE_STOCKING = BLOCKS.register("pink_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "pink"));
        public static final RegistryObject<Block> PURPLE_FESTIVE_STOCKING = BLOCKS.register("purple_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "purple"));
        public static final RegistryObject<Block> RED_FESTIVE_STOCKING = BLOCKS.register("red_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "red"));
        public static final RegistryObject<Block> WHITE_FESTIVE_STOCKING = BLOCKS.register("white_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "white"));
        public static final RegistryObject<Block> YELLOW_FESTIVE_STOCKING = BLOCKS.register("yellow_festive_stocking",
                        () -> new FestiveStockingBlock(
                                        BlockBehaviour.Properties.of(Material.DECORATION)
                                                        .strength(0.1f)
                                                        .sound(SoundType.WOOL)
                                                        .noCollission()
                                                        .noOcclusion(),
                                        "yellow"));
        public static final RegistryObject<Block> FESTIVE_LAMP = BLOCKS.register(
                        "festive_lamp",
                        () -> new FestiveLampBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.BUILDABLE_GLASS,
                                                        MaterialColor.COLOR_RED)
                                                        .strength(0.3f)
                                                        .sound(SoundType.GLASS)
                                                        .lightLevel(state -> state.getValue(
                                                                        BlockStateProperties.LIT)
                                                                                        ? 15
                                                                                        : 0)));
        public static final RegistryObject<Block> FROST_ROSE = BLOCKS.register(
                        "frost_rose",
                        () -> new FrostRoseBlock(
                                        BlockBehaviour.Properties.of(
                                                        Material.PLANT,
                                                        MaterialColor.COLOR_LIGHT_BLUE)
                                                        .noCollission()
                                                        .instabreak()
                                                        .sound(SoundType.GRASS)));
        public static final RegistryObject<Block> STEEL_BLOCK = BLOCKS.register(
                        "steel_block",
                        () -> new Block(
                                        BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                                                        .strength(5.0f, 6.0f)
                                                        .requiresCorrectToolForDrops()
                                                        .sound(SoundType.COPPER)));
        public static final RegistryObject<Block> STEEL_PIPE = BLOCKS.register(
                        "steel_pipe",
                        () -> new PipeBlock(
                                        BlockBehaviour.Properties.copy(STEEL_BLOCK.get())));
        public static final RegistryObject<Block> HOLLOW_STEEL_PIPE = BLOCKS.register(
                        "hollow_steel_pipe",
                        () -> new HollowPipeBlock(
                                        BlockBehaviour.Properties.copy(STEEL_BLOCK.get())));
        public static final RegistryObject<Block> SCRAPED_STEEL = BLOCKS.register(
                        "scraped_steel",
                        () -> new Block(
                                        BlockBehaviour.Properties.copy(STEEL_BLOCK.get())));
        public static final RegistryObject<Block> RUSTIC_SCRAPED_STEEL = BLOCKS.register(
                        "rustic_scraped_steel",
                        () -> new Block(
                                        BlockBehaviour.Properties.copy(STEEL_BLOCK.get())));
        public static final RegistryObject<Block> STACKED_STEEL = BLOCKS.register(
                        "stacked_steel",
                        () -> new Block(
                                        BlockBehaviour.Properties.copy(STEEL_BLOCK.get())));
        public static final RegistryObject<Block> STEEL_PANELS = BLOCKS.register(
                        "steel_panels",
                        () -> new Block(
                                        BlockBehaviour.Properties.copy(STEEL_BLOCK.get())));
        public static final RegistryObject<Block> CROSSED_STEEL_PANELS = BLOCKS.register(
                        "crossed_steel_panels",
                        () -> new Block(
                                        BlockBehaviour.Properties.copy(STEEL_BLOCK.get())));
        public static final RegistryObject<Block> STEEL_MESH_BLOCK = BLOCKS.register(
                        "steel_mesh_block",
                        () -> new Block(
                                        BlockBehaviour.Properties.copy(STEEL_BLOCK.get())));
        public static final RegistryObject<Block> STEEL_BOLTS = BLOCKS.register(
                        "steel_bolts",
                        () -> new SteelBoltBlock(
                                        BlockBehaviour.Properties.copy(STEEL_BLOCK.get())));
}
