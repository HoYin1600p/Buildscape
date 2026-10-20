package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.block.plant.CactusFlowerBlock;
import com.kingodogo.buildscape.block.plant.ColoredMossBlock;
import com.kingodogo.buildscape.block.plant.ColoredMossLayersBlock;
import com.kingodogo.buildscape.block.plant.CreakingHeartBlock;
import com.kingodogo.buildscape.block.plant.DryGrassBlock;
import com.kingodogo.buildscape.block.plant.EyeblossomBlock;
import com.kingodogo.buildscape.block.plant.FireflyBushBlock;
import com.kingodogo.buildscape.block.light.FroglightBlock;
import com.kingodogo.buildscape.block.storage.GlassJarBlock;
import com.kingodogo.buildscape.block.plant.GoldenDandelionBlock;
import com.kingodogo.buildscape.block.plant.HangingMossBlock;
import com.kingodogo.buildscape.block.plant.HazeBushBlock;
import com.kingodogo.buildscape.block.plant.LeafHedgeBlock;
import com.kingodogo.buildscape.block.plant.LeafLayersBlock;
import com.kingodogo.buildscape.block.plant.ModBushBlock;
import com.kingodogo.buildscape.block.building.ModButtonBlock;
import com.kingodogo.buildscape.block.building.ModPressurePlateBlock;
import com.kingodogo.buildscape.block.building.ModSlabBlock;
import com.kingodogo.buildscape.block.building.ModStairBlock;
import com.kingodogo.buildscape.block.plant.MossOverlayBlock;
import com.kingodogo.buildscape.block.plant.PetalBlock;
import com.kingodogo.buildscape.block.plant.PottedHazeBushBlock;
import com.kingodogo.buildscape.block.decor.ResinClumpBlock;
import com.kingodogo.buildscape.block.plant.SnowyLeavesBlock;
import com.kingodogo.buildscape.block.building.SteelBoltBlock;
import com.kingodogo.buildscape.block.plant.TallDryGrassBlock;
import com.kingodogo.buildscape.block.building.VerticalSlabBlock;
import com.kingodogo.buildscape.block.building.WaterloggableGrateBlock;
import com.kingodogo.buildscape.feature.copper.CopperBulbBlock;
import com.kingodogo.buildscape.feature.copper.CopperChestBlock;
import com.kingodogo.buildscape.feature.copper.CopperTorchBlock;
import com.kingodogo.buildscape.feature.copper.CopperWallTorchBlock;
import com.kingodogo.buildscape.feature.copper.ExposedCopperBulbBlock;
import com.kingodogo.buildscape.feature.copper.FreshCopperBulbBlock;
import com.kingodogo.buildscape.feature.copper.ModCopperDoorBlock;
import com.kingodogo.buildscape.feature.copper.ModCopperTrapdoorBlock;
import com.kingodogo.buildscape.feature.copper.OxidizedCopperBulbBlock;
import com.kingodogo.buildscape.feature.copper.WeatheredCopperBulbBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringBarsBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringBoltBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringButtonBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringCopperBlocks;
import com.kingodogo.buildscape.feature.copper.WeatheringDoorBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringGrateBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringLanternBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringLargeChainBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringPressurePlateBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringRodBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringSlabBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringStairBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringTrapdoorBlock;
import com.kingodogo.buildscape.feature.copper.WeatheringVerticalSlabBlock;
import com.kingodogo.buildscape.feature.sculk.SculkCatalystBlock;
import com.kingodogo.buildscape.feature.sculk.SculkVeinBlock;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import com.kingodogo.buildscape.registry.ModSounds;
import com.kingodogo.buildscape.registry.items.CopperItems;
import com.kingodogo.buildscape.registry.items.WoodItems;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.blocks.BlocksRegister.BLOCKS;

/**
 * The blocks PALE_MOSS_OVERLAY through LARGE_EXPOSED_COPPER_CHAIN of the registry, part 9 of 12, in registration order.
 * Named for its dominant families (copper, foliage, decor); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link ShapedWoodBlocks#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class CopperBlocks {

    private CopperBlocks() {}

    /** Forces this class to load, which registers its blocks. */
    public static void init() {}

    static {
        ShapedWoodBlocks.init();
    }

    public static final RegistryObject<Block> PALE_MOSS_OVERLAY = BLOCKS.register("pale_moss_overlay",
            () -> new MossOverlayBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET)));
    public static final RegistryObject<Block> PALE_MOSS_LAYERS = BLOCKS.register("pale_moss_layers",
            () -> new ColoredMossLayersBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET),
                    () -> CopperItems.PALE_MOSS_LAYERS.get(), () -> CopperBlocks.PALE_MOSS_BLOCK.get()));
    public static final RegistryObject<Block> PALE_MOSS_BLOCK = BLOCKS.register("pale_moss_block",
            () -> new ColoredMossBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK),
                    ShapedWoodBlocks.PALE_MOSS_CARPET,
                    PALE_MOSS_OVERLAY, PALE_MOSS_LAYERS, () -> ShapedWoodBlocks.PALE_OAK_SAPLING.get(),
                    () -> CopperBlocks.CLOSED_EYEBLOSSOM.get()));
    public static final RegistryObject<Block> PALE_MOSS_BLOCK_SLAB = BLOCKS.register("pale_moss_block_slab",
            () -> new ModSlabBlock(PALE_MOSS_BLOCK.get(), BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK)));
    public static final RegistryObject<Block> PALE_MOSS_BLOCK_STAIRS = BLOCKS.register("pale_moss_block_stairs",
            () -> new ModStairBlock(PALE_MOSS_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK)));
    public static final RegistryObject<Block> PALE_MOSS_BLOCK_VERTICAL_SLAB = BLOCKS.register(
            "pale_moss_block_vertical_slab",
            () -> new VerticalSlabBlock(PALE_MOSS_BLOCK.get(), BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK)));
    public static final RegistryObject<Block> PALE_HANGING_MOSS = BLOCKS.register("pale_hanging_moss",
            () -> new HangingMossBlock(
                    BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET).noCollission().strength(0.2F).randomTicks()));

    public static final RegistryObject<Block> CREAKING_HEART = BLOCKS.register("creaking_heart",
            () -> new CreakingHeartBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(2.0F)));
    public static final RegistryObject<Block> RESIN_CLUMP = BLOCKS.register("resin_clump",
            () -> new ResinClumpBlock(BlockBehaviour.Properties.copy(Blocks.GLOW_LICHEN).lightLevel(state -> 7)
                    .hasPostProcess((state, getter, pos) -> true).emissiveRendering((state, getter, pos) -> true)));
    public static final RegistryObject<Block> RESIN_BLOCK = BLOCKS.register("resin_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.TERRACOTTA)));
    public static final RegistryObject<Block> RESIN_BLOCK_SLAB = BLOCKS.register("resin_block_slab",
            () -> new ModSlabBlock(RESIN_BLOCK.get(), BlockBehaviour.Properties.copy(Blocks.TERRACOTTA)));
    public static final RegistryObject<Block> RESIN_BLOCK_STAIRS = BLOCKS.register("resin_block_stairs",
            () -> new ModStairBlock(RESIN_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.TERRACOTTA)));
    public static final RegistryObject<Block> RESIN_BLOCK_VERTICAL_SLAB = BLOCKS.register("resin_block_vertical_slab",
            () -> new VerticalSlabBlock(RESIN_BLOCK.get(), BlockBehaviour.Properties.copy(Blocks.TERRACOTTA)));
    public static final RegistryObject<Block> RESIN_BRICKS = BLOCKS.register("resin_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BRICKS)));
    public static final RegistryObject<Block> RESIN_BRICK_SLAB = BLOCKS.register("resin_brick_slab",
            () -> new ModSlabBlock(RESIN_BRICKS.get(), BlockBehaviour.Properties.copy(Blocks.BRICKS)));
    public static final RegistryObject<Block> RESIN_BRICK_STAIRS = BLOCKS.register("resin_brick_stairs",
            () -> new ModStairBlock(RESIN_BRICKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.BRICKS)));
    public static final RegistryObject<Block> RESIN_BRICK_WALL = BLOCKS.register("resin_brick_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS)));
    public static final RegistryObject<Block> RESIN_BRICK_VERTICAL_SLAB = BLOCKS.register("resin_brick_vertical_slab",
            () -> new VerticalSlabBlock(RESIN_BRICKS.get(), BlockBehaviour.Properties.copy(Blocks.BRICKS)));
    public static final RegistryObject<Block> CHISELED_RESIN_BRICKS = BLOCKS.register("chiseled_resin_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BRICKS)));

    public static final RegistryObject<Block> CLOSED_EYEBLOSSOM = BLOCKS.register("closed_eyeblossom",
            () -> new EyeblossomBlock(false, MobEffects.BLINDNESS, 7, BlockBehaviour.Properties.copy(Blocks.POPPY)));
    public static final RegistryObject<Block> OPEN_EYEBLOSSOM = BLOCKS.register("open_eyeblossom",
            () -> new EyeblossomBlock(true, MobEffects.CONFUSION, 7,
                    BlockBehaviour.Properties.copy(Blocks.POPPY).lightLevel(state -> 7)
                            .hasPostProcess((state, getter, pos) -> true)
                            .emissiveRendering((state, getter, pos) -> true)));
    public static final RegistryObject<Block> WILDFLOWERS = BLOCKS.register("wildflowers",
            () -> new PetalBlock(
                    BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET).noCollission().sound(SoundType.GRASS)));
    public static final RegistryObject<Block> LEAF_LITTER = BLOCKS.register("leaf_litter",
            () -> new PetalBlock(
                    BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET).noCollission().sound(SoundType.GRASS)));
    public static final RegistryObject<Block> CACTUS_FLOWER = BLOCKS.register("cactus_flower",
            () -> new CactusFlowerBlock(BlockBehaviour.Properties.copy(Blocks.DANDELION)));
    public static final RegistryObject<Block> BUSH = BLOCKS.register("bush",
            () -> new ModBushBlock(BlockBehaviour.Properties.copy(Blocks.DEAD_BUSH)));
    public static final RegistryObject<Block> RED_BUSH = BLOCKS.register("red_bush",
            () -> new ModBushBlock(BlockBehaviour.Properties.copy(Blocks.DEAD_BUSH)));
    public static final Map<DyeColor, RegistryObject<Block>> COLORED_HAZE_BUSHES = new EnumMap<>(DyeColor.class);
    public static final Map<DyeColor, RegistryObject<Block>> POTTED_COLORED_HAZE_BUSHES = new EnumMap<>(DyeColor.class);

    private static RegistryObject<Block> registerHazeBush(DyeColor color) {
        RegistryObject<Block> obj = BLOCKS.register(color.getName() + "_haze_bush",
                () -> new HazeBushBlock(color, BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.WOOD)
                        .noCollission().instabreak().sound(SoundType.GRASS)));
        COLORED_HAZE_BUSHES.put(color, obj);
        return obj;
    }

    private static RegistryObject<Block> registerPottedHazeBush(DyeColor color, RegistryObject<Block> bush) {
        RegistryObject<Block> obj = BLOCKS.register("potted_" + color.getName() + "_haze_bush",
                () -> new PottedHazeBushBlock(color, () -> (FlowerPotBlock) Blocks.FLOWER_POT, bush,
                        BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
        POTTED_COLORED_HAZE_BUSHES.put(color, obj);
        return obj;
    }

    public static final RegistryObject<Block> WHITE_HAZE_BUSH = registerHazeBush(DyeColor.WHITE);
    public static final RegistryObject<Block> ORANGE_HAZE_BUSH = registerHazeBush(DyeColor.ORANGE);
    public static final RegistryObject<Block> MAGENTA_HAZE_BUSH = registerHazeBush(DyeColor.MAGENTA);
    public static final RegistryObject<Block> LIGHT_BLUE_HAZE_BUSH = registerHazeBush(DyeColor.LIGHT_BLUE);
    public static final RegistryObject<Block> YELLOW_HAZE_BUSH = registerHazeBush(DyeColor.YELLOW);
    public static final RegistryObject<Block> LIME_HAZE_BUSH = registerHazeBush(DyeColor.LIME);
    public static final RegistryObject<Block> PINK_HAZE_BUSH = registerHazeBush(DyeColor.PINK);
    public static final RegistryObject<Block> GRAY_HAZE_BUSH = registerHazeBush(DyeColor.GRAY);
    public static final RegistryObject<Block> LIGHT_GRAY_HAZE_BUSH = registerHazeBush(DyeColor.LIGHT_GRAY);
    public static final RegistryObject<Block> CYAN_HAZE_BUSH = registerHazeBush(DyeColor.CYAN);
    public static final RegistryObject<Block> PURPLE_HAZE_BUSH = registerHazeBush(DyeColor.PURPLE);
    public static final RegistryObject<Block> BLUE_HAZE_BUSH = registerHazeBush(DyeColor.BLUE);
    public static final RegistryObject<Block> BROWN_HAZE_BUSH = registerHazeBush(DyeColor.BROWN);
    public static final RegistryObject<Block> GREEN_HAZE_BUSH = registerHazeBush(DyeColor.GREEN);
    public static final RegistryObject<Block> RED_HAZE_BUSH = registerHazeBush(DyeColor.RED);
    public static final RegistryObject<Block> BLACK_HAZE_BUSH = registerHazeBush(DyeColor.BLACK);
    public static final RegistryObject<Block> FIREFLY_BUSH = BLOCKS.register("firefly_bush",
            () -> new FireflyBushBlock(BlockBehaviour.Properties.copy(Blocks.GRASS).lightLevel(state -> 2)
                    .hasPostProcess((state, getter, pos) -> true).emissiveRendering((state, getter, pos) -> true)));
    public static final RegistryObject<Block> DRY_GRASS = BLOCKS.register("dry_grass",
            () -> new DryGrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS)));
    public static final RegistryObject<Block> TALL_DRY_GRASS = BLOCKS.register("tall_dry_grass",
            () -> new TallDryGrassBlock(BlockBehaviour.Properties.copy(Blocks.TALL_GRASS)));
    public static final RegistryObject<Block> GOLDEN_DANDELION = BLOCKS.register("golden_dandelion",
            () -> new GoldenDandelionBlock(BlockBehaviour.Properties.copy(Blocks.DANDELION)));

    public static final RegistryObject<Block> POTTED_CLOSED_EYEBLOSSOM = BLOCKS.register("potted_closed_eyeblossom",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CLOSED_EYEBLOSSOM,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_OPEN_EYEBLOSSOM = BLOCKS.register("potted_open_eyeblossom",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, OPEN_EYEBLOSSOM,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY).lightLevel(state -> 7)));
    public static final RegistryObject<Block> POTTED_GOLDEN_DANDELION = BLOCKS.register("potted_golden_dandelion",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, GOLDEN_DANDELION,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_CACTUS_FLOWER = BLOCKS.register("potted_cactus_flower",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CACTUS_FLOWER,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_BUSH = BLOCKS.register("potted_bush",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, BUSH,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_RED_BUSH = BLOCKS.register("potted_red_bush",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, RED_BUSH,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_WHITE_HAZE_BUSH = registerPottedHazeBush(DyeColor.WHITE,
            WHITE_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_ORANGE_HAZE_BUSH = registerPottedHazeBush(DyeColor.ORANGE,
            ORANGE_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_MAGENTA_HAZE_BUSH = registerPottedHazeBush(DyeColor.MAGENTA,
            MAGENTA_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_LIGHT_BLUE_HAZE_BUSH = registerPottedHazeBush(DyeColor.LIGHT_BLUE,
            LIGHT_BLUE_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_YELLOW_HAZE_BUSH = registerPottedHazeBush(DyeColor.YELLOW,
            YELLOW_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_LIME_HAZE_BUSH = registerPottedHazeBush(DyeColor.LIME,
            LIME_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_PINK_HAZE_BUSH = registerPottedHazeBush(DyeColor.PINK,
            PINK_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_GRAY_HAZE_BUSH = registerPottedHazeBush(DyeColor.GRAY,
            GRAY_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_LIGHT_GRAY_HAZE_BUSH = registerPottedHazeBush(DyeColor.LIGHT_GRAY,
            LIGHT_GRAY_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_CYAN_HAZE_BUSH = registerPottedHazeBush(DyeColor.CYAN,
            CYAN_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_PURPLE_HAZE_BUSH = registerPottedHazeBush(DyeColor.PURPLE,
            PURPLE_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_BLUE_HAZE_BUSH = registerPottedHazeBush(DyeColor.BLUE,
            BLUE_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_BROWN_HAZE_BUSH = registerPottedHazeBush(DyeColor.BROWN,
            BROWN_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_GREEN_HAZE_BUSH = registerPottedHazeBush(DyeColor.GREEN,
            GREEN_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_RED_HAZE_BUSH = registerPottedHazeBush(DyeColor.RED,
            RED_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_BLACK_HAZE_BUSH = registerPottedHazeBush(DyeColor.BLACK,
            BLACK_HAZE_BUSH);
    public static final RegistryObject<Block> POTTED_FIREFLY_BUSH = BLOCKS.register("potted_firefly_bush",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FIREFLY_BUSH,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY).lightLevel(state -> 2)));
    public static final RegistryObject<Block> POTTED_DRY_GRASS = BLOCKS.register("potted_dry_grass",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DRY_GRASS,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_FROST_ROSE = BLOCKS.register("potted_frost_rose",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DecorBlocks.FROST_ROSE,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_RED_MONETS = BLOCKS.register("potted_red_monets",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, WoodBlocks.RED_MONETS,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_BLUE_MONETS = BLOCKS.register("potted_blue_monets",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, WoodBlocks.BLUE_MONETS,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_PURPLE_MONETS = BLOCKS.register("potted_purple_monets",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, WoodBlocks.PURPLE_MONETS,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_LIGHT_BLUE_MONETS = BLOCKS.register("potted_light_blue_monets",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, WoodBlocks.LIGHT_BLUE_MONETS,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_PINK_MONETS = BLOCKS.register("potted_pink_monets",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, WoodBlocks.PINK_MONETS,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_YELLOW_MONETS = BLOCKS.register("potted_yellow_monets",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DecorBlocks.YELLOW_MONETS,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_POPLAR_SAPLING = BLOCKS.register("potted_poplar_sapling",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, ShapedWoodBlocks.POPLAR_SAPLING,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_PALE_OAK_SAPLING = BLOCKS.register("potted_pale_oak_sapling",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, ShapedWoodBlocks.PALE_OAK_SAPLING,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_CHERRY_SAPLING = BLOCKS.register("potted_cherry_sapling",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, ShapedWoodBlocks.CHERRY_SAPLING,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_MANGROVE_PROPAGULE = BLOCKS.register("potted_mangrove_propagule",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DecorBlocks.MANGROVE_PROPAGULE,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_WILDFLOWERS = BLOCKS.register("potted_wildflowers",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, WILDFLOWERS,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_CLOVER = BLOCKS.register("potted_clover",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DecorBlocks.CLOVER,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_LEAF_LITTER = BLOCKS.register("potted_leaf_litter",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, LEAF_LITTER,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_TALL_DRY_GRASS = BLOCKS.register("potted_tall_dry_grass",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, TALL_DRY_GRASS,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_SNOWY_BUSH = BLOCKS.register("potted_snowy_bush",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DecorBlocks.SNOWY_BUSH,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_SNOWY_SHORT_GRASS = BLOCKS.register("potted_snowy_short_grass",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DecorBlocks.SNOWY_SHORT_GRASS,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_SNOWY_TALL_GRASS = BLOCKS.register("potted_snowy_tall_grass",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DecorBlocks.SNOWY_TALL_GRASS,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_SNOWY_FERN = BLOCKS.register("potted_snowy_fern",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DecorBlocks.SNOWY_FERN,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));
    public static final RegistryObject<Block> POTTED_SNOWY_LARGE_FERN = BLOCKS.register("potted_snowy_large_fern",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DecorBlocks.SNOWY_LARGE_FERN,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY)));

    public static final RegistryObject<Block> OCHRE_FROGLIGHT = BLOCKS.register("ochre_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> PEARLESCENT_FROGLIGHT = BLOCKS.register("pearlescent_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> VERDANT_FROGLIGHT = BLOCKS.register("verdant_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));

    public static final RegistryObject<Block> SCULK = BLOCKS.register("sculk",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR).strength(0.2F)
                    .lightLevel(state -> 0).color(MaterialColor.COLOR_BLACK)));
    public static final RegistryObject<Block> SCULK_SLAB = BLOCKS.register("sculk_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR).strength(0.2F)
                    .lightLevel(state -> 0).color(MaterialColor.COLOR_BLACK)));
    public static final RegistryObject<Block> SCULK_STAIRS = BLOCKS.register("sculk_stairs",
            () -> new ModStairBlock(SCULK.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR).strength(0.2F)
                            .lightLevel(state -> 0).color(MaterialColor.COLOR_BLACK)));
    public static final RegistryObject<Block> SCULK_WALL = BLOCKS.register("sculk_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR).strength(0.2F)
                    .lightLevel(state -> 0).color(MaterialColor.COLOR_BLACK)));
    public static final RegistryObject<Block> SCULK_VERTICAL_SLAB = BLOCKS.register("sculk_vertical_slab",
            () -> new VerticalSlabBlock(SCULK.get(), BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR).strength(0.2F)
                    .lightLevel(state -> 0).color(MaterialColor.COLOR_BLACK)));
    public static final RegistryObject<Block> SCULK_VEIN = BLOCKS.register("sculk_vein",
            () -> new SculkVeinBlock(BlockBehaviour.Properties.copy(Blocks.GLOW_LICHEN).lightLevel(state -> 0)));
    public static final RegistryObject<Block> SCULK_CATALYST = BLOCKS.register("sculk_catalyst",
            () -> new SculkCatalystBlock(
                    BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR).strength(3.0F, 3.0F)
                            .lightLevel(state -> 6).randomTicks()));
    public static final RegistryObject<Block> SCULK_SHRIEKER = BLOCKS.register("sculk_shrieker",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR).strength(3.0F, 3.0F)
                    .lightLevel(state -> 0).noOcclusion()));
    public static final RegistryObject<Block> SCULK_SENSOR = BLOCKS.register("sculk_sensor",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR).lightLevel(state -> 1).noOcclusion()));

    public static final RegistryObject<Block> COPPER_TORCH = BLOCKS.register("copper_torch",
            () -> new CopperTorchBlock(BlockBehaviour.Properties.copy(Blocks.TORCH).lightLevel(state -> 14)));
    public static final RegistryObject<Block> COPPER_WALL_TORCH = BLOCKS.register("copper_wall_torch",
            () -> new CopperWallTorchBlock(BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).lightLevel(state -> 14)));

    public static final RegistryObject<Block> COPPER_ROD = BLOCKS.register("copper_rod",
            () -> new WeatheringRodBlock(BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD)));
    public static final RegistryObject<Block> WAXED_COPPER_ROD = BLOCKS.register("waxed_copper_rod",
            () -> new LightningRodBlock(BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD)));
    public static final RegistryObject<Block> EXPOSED_COPPER_ROD = BLOCKS.register("exposed_copper_rod",
            () -> new WeatheringRodBlock(BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD)));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_ROD = BLOCKS.register("waxed_exposed_copper_rod",
            () -> new LightningRodBlock(BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD)));
    public static final RegistryObject<Block> WEATHERED_COPPER_ROD = BLOCKS.register("weathered_copper_rod",
            () -> new WeatheringRodBlock(BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD)));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_ROD = BLOCKS.register("waxed_weathered_copper_rod",
            () -> new LightningRodBlock(BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_ROD = BLOCKS.register("oxidized_copper_rod",
            () -> new WeatheringRodBlock(BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_ROD = BLOCKS.register("waxed_oxidized_copper_rod",
            () -> new LightningRodBlock(BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD)));

    public static final RegistryObject<Block> COPPER_LANTERN = BLOCKS.register("copper_lantern",
            () -> new WeatheringLanternBlock(
                    BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_COPPER_LANTERN = BLOCKS.register("waxed_copper_lantern",
            () -> new LanternBlock(
                    BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> EXPOSED_COPPER_LANTERN = BLOCKS.register("exposed_copper_lantern",
            () -> new WeatheringLanternBlock(
                    BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_LANTERN = BLOCKS.register(
            "waxed_exposed_copper_lantern",
            () -> new LanternBlock(
                    BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WEATHERED_COPPER_LANTERN = BLOCKS.register("weathered_copper_lantern",
            () -> new WeatheringLanternBlock(
                    BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_LANTERN = BLOCKS.register(
            "waxed_weathered_copper_lantern",
            () -> new LanternBlock(
                    BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_LANTERN = BLOCKS.register("oxidized_copper_lantern",
            () -> new WeatheringLanternBlock(
                    BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_LANTERN = BLOCKS.register(
            "waxed_oxidized_copper_lantern",
            () -> new LanternBlock(
                    BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER)));

    public static final RegistryObject<Block> SLIT_COPPER = BLOCKS.register("slit_copper",
            () -> new WeatheringBlock(BlockBehaviour.Properties.copy(Blocks.CUT_COPPER)));
    public static final RegistryObject<Block> WAXED_SLIT_COPPER = BLOCKS.register("waxed_slit_copper",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.CUT_COPPER)));
    public static final RegistryObject<Block> EXPOSED_SLIT_COPPER = BLOCKS.register("exposed_slit_copper",
            () -> new WeatheringBlock(BlockBehaviour.Properties.copy(Blocks.EXPOSED_CUT_COPPER)));
    public static final RegistryObject<Block> WAXED_EXPOSED_SLIT_COPPER = BLOCKS.register("waxed_exposed_slit_copper",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.EXPOSED_CUT_COPPER)));
    public static final RegistryObject<Block> WEATHERED_SLIT_COPPER = BLOCKS.register("weathered_slit_copper",
            () -> new WeatheringBlock(BlockBehaviour.Properties.copy(Blocks.WEATHERED_CUT_COPPER)));
    public static final RegistryObject<Block> WAXED_WEATHERED_SLIT_COPPER = BLOCKS.register(
            "waxed_weathered_slit_copper",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.WEATHERED_CUT_COPPER)));
    public static final RegistryObject<Block> OXIDIZED_SLIT_COPPER = BLOCKS.register("oxidized_slit_copper",
            () -> new WeatheringBlock(BlockBehaviour.Properties.copy(Blocks.OXIDIZED_CUT_COPPER)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_SLIT_COPPER = BLOCKS.register("waxed_oxidized_slit_copper",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OXIDIZED_CUT_COPPER)));

    public static final RegistryObject<Block> SLIT_COPPER_STAIRS = BLOCKS.register("slit_copper_stairs",
            () -> new WeatheringStairBlock(SLIT_COPPER.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(SLIT_COPPER.get())));
    public static final RegistryObject<Block> WAXED_SLIT_COPPER_STAIRS = BLOCKS.register("waxed_slit_copper_stairs",
            () -> new ModStairBlock(WAXED_SLIT_COPPER.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(WAXED_SLIT_COPPER.get())));
    public static final RegistryObject<Block> EXPOSED_SLIT_COPPER_STAIRS = BLOCKS.register("exposed_slit_copper_stairs",
            () -> new WeatheringStairBlock(EXPOSED_SLIT_COPPER.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(EXPOSED_SLIT_COPPER.get())));
    public static final RegistryObject<Block> WAXED_EXPOSED_SLIT_COPPER_STAIRS = BLOCKS.register(
            "waxed_exposed_slit_copper_stairs",
            () -> new ModStairBlock(WAXED_EXPOSED_SLIT_COPPER.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(WAXED_EXPOSED_SLIT_COPPER.get())));
    public static final RegistryObject<Block> WEATHERED_SLIT_COPPER_STAIRS = BLOCKS.register(
            "weathered_slit_copper_stairs",
            () -> new WeatheringStairBlock(WEATHERED_SLIT_COPPER.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(WEATHERED_SLIT_COPPER.get())));
    public static final RegistryObject<Block> WAXED_WEATHERED_SLIT_COPPER_STAIRS = BLOCKS.register(
            "waxed_weathered_slit_copper_stairs",
            () -> new ModStairBlock(WAXED_WEATHERED_SLIT_COPPER.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(WAXED_WEATHERED_SLIT_COPPER.get())));
    public static final RegistryObject<Block> OXIDIZED_SLIT_COPPER_STAIRS = BLOCKS.register(
            "oxidized_slit_copper_stairs",
            () -> new WeatheringStairBlock(OXIDIZED_SLIT_COPPER.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(OXIDIZED_SLIT_COPPER.get())));
    public static final RegistryObject<Block> WAXED_OXIDIZED_SLIT_COPPER_STAIRS = BLOCKS.register(
            "waxed_oxidized_slit_copper_stairs",
            () -> new ModStairBlock(WAXED_OXIDIZED_SLIT_COPPER.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(WAXED_OXIDIZED_SLIT_COPPER.get())));

    public static final RegistryObject<Block> SLIT_COPPER_SLAB = BLOCKS.register("slit_copper_slab",
            () -> new WeatheringSlabBlock(BlockBehaviour.Properties.copy(SLIT_COPPER.get())));
    public static final RegistryObject<Block> WAXED_SLIT_COPPER_SLAB = BLOCKS.register("waxed_slit_copper_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(WAXED_SLIT_COPPER.get())));
    public static final RegistryObject<Block> EXPOSED_SLIT_COPPER_SLAB = BLOCKS.register("exposed_slit_copper_slab",
            () -> new WeatheringSlabBlock(BlockBehaviour.Properties.copy(EXPOSED_SLIT_COPPER.get())));
    public static final RegistryObject<Block> WAXED_EXPOSED_SLIT_COPPER_SLAB = BLOCKS.register(
            "waxed_exposed_slit_copper_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(WAXED_EXPOSED_SLIT_COPPER.get())));
    public static final RegistryObject<Block> WEATHERED_SLIT_COPPER_SLAB = BLOCKS.register("weathered_slit_copper_slab",
            () -> new WeatheringSlabBlock(BlockBehaviour.Properties.copy(WEATHERED_SLIT_COPPER.get())));
    public static final RegistryObject<Block> WAXED_WEATHERED_SLIT_COPPER_SLAB = BLOCKS.register(
            "waxed_weathered_slit_copper_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(WAXED_WEATHERED_SLIT_COPPER.get())));
    public static final RegistryObject<Block> OXIDIZED_SLIT_COPPER_SLAB = BLOCKS.register("oxidized_slit_copper_slab",
            () -> new WeatheringSlabBlock(BlockBehaviour.Properties.copy(OXIDIZED_SLIT_COPPER.get())));
    public static final RegistryObject<Block> WAXED_OXIDIZED_SLIT_COPPER_SLAB = BLOCKS.register(
            "waxed_oxidized_slit_copper_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(WAXED_OXIDIZED_SLIT_COPPER.get())));

    public static final RegistryObject<Block> SLIT_COPPER_VERTICAL_SLAB = BLOCKS.register("slit_copper_vertical_slab",
            () -> new WeatheringVerticalSlabBlock(SLIT_COPPER.get(),
                    BlockBehaviour.Properties.copy(SLIT_COPPER_SLAB.get())));
    public static final RegistryObject<Block> WAXED_SLIT_COPPER_VERTICAL_SLAB = BLOCKS
            .register("waxed_slit_copper_vertical_slab", () -> new VerticalSlabBlock(WAXED_SLIT_COPPER.get(),
                    BlockBehaviour.Properties.copy(WAXED_SLIT_COPPER_SLAB.get())));
    public static final RegistryObject<Block> EXPOSED_SLIT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "exposed_slit_copper_vertical_slab", () -> new WeatheringVerticalSlabBlock(EXPOSED_SLIT_COPPER.get(),
                    BlockBehaviour.Properties.copy(EXPOSED_SLIT_COPPER_SLAB.get())));
    public static final RegistryObject<Block> WAXED_EXPOSED_SLIT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "waxed_exposed_slit_copper_vertical_slab", () -> new VerticalSlabBlock(WAXED_EXPOSED_SLIT_COPPER.get(),
                    BlockBehaviour.Properties.copy(WAXED_EXPOSED_SLIT_COPPER_SLAB.get())));
    public static final RegistryObject<Block> WEATHERED_SLIT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "weathered_slit_copper_vertical_slab", () -> new WeatheringVerticalSlabBlock(WEATHERED_SLIT_COPPER.get(),
                    BlockBehaviour.Properties.copy(WEATHERED_SLIT_COPPER_SLAB.get())));
    public static final RegistryObject<Block> WAXED_WEATHERED_SLIT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "waxed_weathered_slit_copper_vertical_slab", () -> new VerticalSlabBlock(WAXED_WEATHERED_SLIT_COPPER.get(),
                    BlockBehaviour.Properties.copy(WAXED_WEATHERED_SLIT_COPPER_SLAB.get())));
    public static final RegistryObject<Block> OXIDIZED_SLIT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "oxidized_slit_copper_vertical_slab", () -> new WeatheringVerticalSlabBlock(OXIDIZED_SLIT_COPPER.get(),
                    BlockBehaviour.Properties.copy(OXIDIZED_SLIT_COPPER_SLAB.get())));
    public static final RegistryObject<Block> WAXED_OXIDIZED_SLIT_COPPER_VERTICAL_SLAB = BLOCKS.register(
            "waxed_oxidized_slit_copper_vertical_slab", () -> new VerticalSlabBlock(WAXED_OXIDIZED_SLIT_COPPER.get(),
                    BlockBehaviour.Properties.copy(WAXED_OXIDIZED_SLIT_COPPER_SLAB.get())));

    public static final RegistryObject<Block> CHISELED_COPPER = BLOCKS.register("chiseled_copper",
            () -> new WeatheringBlock(BlockBehaviour.Properties.copy(Blocks.CUT_COPPER)));
    public static final RegistryObject<Block> WAXED_CHISELED_COPPER = BLOCKS.register("waxed_chiseled_copper",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.CUT_COPPER)));
    public static final RegistryObject<Block> EXPOSED_CHISELED_COPPER = BLOCKS.register("exposed_chiseled_copper",
            () -> new WeatheringBlock(BlockBehaviour.Properties.copy(Blocks.EXPOSED_CUT_COPPER)));
    public static final RegistryObject<Block> WAXED_EXPOSED_CHISELED_COPPER = BLOCKS.register(
            "waxed_exposed_chiseled_copper",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.EXPOSED_CUT_COPPER)));
    public static final RegistryObject<Block> WEATHERED_CHISELED_COPPER = BLOCKS.register("weathered_chiseled_copper",
            () -> new WeatheringBlock(BlockBehaviour.Properties.copy(Blocks.WEATHERED_CUT_COPPER)));
    public static final RegistryObject<Block> WAXED_WEATHERED_CHISELED_COPPER = BLOCKS.register(
            "waxed_weathered_chiseled_copper",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.WEATHERED_CUT_COPPER)));
    public static final RegistryObject<Block> OXIDIZED_CHISELED_COPPER = BLOCKS.register("oxidized_chiseled_copper",
            () -> new WeatheringBlock(BlockBehaviour.Properties.copy(Blocks.OXIDIZED_CUT_COPPER)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_CHISELED_COPPER = BLOCKS.register(
            "waxed_oxidized_chiseled_copper",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OXIDIZED_CUT_COPPER)));

    public static final RegistryObject<Block> COPPER_GRATE = BLOCKS.register("copper_grate",
            () -> new WeatheringGrateBlock(BlockBehaviour.Properties.copy(Blocks.CUT_COPPER).noOcclusion()
                    .sound(ModSounds.COPPER_GRATE_SOUNDS())));
    public static final RegistryObject<Block> WAXED_COPPER_GRATE = BLOCKS.register("waxed_copper_grate",
            () -> new WaterloggableGrateBlock(BlockBehaviour.Properties.copy(Blocks.CUT_COPPER).noOcclusion()
                    .sound(ModSounds.COPPER_GRATE_SOUNDS())));
    public static final RegistryObject<Block> EXPOSED_COPPER_GRATE = BLOCKS.register("exposed_copper_grate",
            () -> new WeatheringGrateBlock(BlockBehaviour.Properties.copy(Blocks.EXPOSED_CUT_COPPER).noOcclusion()
                    .sound(ModSounds.COPPER_GRATE_SOUNDS())));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_GRATE = BLOCKS.register("waxed_exposed_copper_grate",
            () -> new WaterloggableGrateBlock(BlockBehaviour.Properties.copy(Blocks.EXPOSED_CUT_COPPER).noOcclusion()
                    .sound(ModSounds.COPPER_GRATE_SOUNDS())));
    public static final RegistryObject<Block> WEATHERED_COPPER_GRATE = BLOCKS.register("weathered_copper_grate",
            () -> new WeatheringGrateBlock(BlockBehaviour.Properties.copy(Blocks.WEATHERED_CUT_COPPER).noOcclusion()
                    .sound(ModSounds.COPPER_GRATE_SOUNDS())));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_GRATE = BLOCKS
            .register("waxed_weathered_copper_grate", () -> new WaterloggableGrateBlock(BlockBehaviour.Properties
                    .copy(Blocks.WEATHERED_CUT_COPPER).noOcclusion().sound(ModSounds.COPPER_GRATE_SOUNDS())));
    public static final RegistryObject<Block> OXIDIZED_COPPER_GRATE = BLOCKS.register("oxidized_copper_grate",
            () -> new WeatheringGrateBlock(BlockBehaviour.Properties.copy(Blocks.OXIDIZED_CUT_COPPER).noOcclusion()
                    .sound(ModSounds.COPPER_GRATE_SOUNDS())));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_GRATE = BLOCKS
            .register("waxed_oxidized_copper_grate", () -> new WaterloggableGrateBlock(BlockBehaviour.Properties
                    .copy(Blocks.OXIDIZED_CUT_COPPER).noOcclusion().sound(ModSounds.COPPER_GRATE_SOUNDS())));

    public static final RegistryObject<Block> COPPER_BULB = BLOCKS.register("copper_bulb",
            () -> new WeatheringCopperBlocks.FreshBulb(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
                    .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 15 : 0)
                    .sound(ModSounds.COPPER_BULB_SOUNDS())
                    .isRedstoneConductor((state, reader, pos) -> false)));
    public static final RegistryObject<Block> WAXED_COPPER_BULB = BLOCKS.register("waxed_copper_bulb",
            () -> new FreshCopperBulbBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
                    .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 15 : 0)
                    .sound(ModSounds.COPPER_BULB_SOUNDS())
                    .isRedstoneConductor((state, reader, pos) -> false)));
    public static final RegistryObject<Block> EXPOSED_COPPER_BULB = BLOCKS.register("exposed_copper_bulb",
            () -> new WeatheringCopperBlocks.ExposedBulb(BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER)
                    .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 12 : 0)
                    .sound(ModSounds.COPPER_BULB_SOUNDS())
                    .isRedstoneConductor((state, reader, pos) -> false)));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_BULB = BLOCKS.register("waxed_exposed_copper_bulb",
            () -> new ExposedCopperBulbBlock(BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER)
                    .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 12 : 0)
                    .sound(ModSounds.COPPER_BULB_SOUNDS())
                    .isRedstoneConductor((state, reader, pos) -> false)));
    public static final RegistryObject<Block> WEATHERED_COPPER_BULB = BLOCKS.register("weathered_copper_bulb",
            () -> new WeatheringCopperBlocks.WeatheredBulb(BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER)
                    .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 8 : 0)
                    .sound(ModSounds.COPPER_BULB_SOUNDS())
                    .isRedstoneConductor((state, reader, pos) -> false)));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_BULB = BLOCKS.register(
            "waxed_weathered_copper_bulb",
            () -> new WeatheredCopperBulbBlock(BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER)
                    .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 8 : 0)
                    .sound(ModSounds.COPPER_BULB_SOUNDS())
                    .isRedstoneConductor((state, reader, pos) -> false)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_BULB = BLOCKS.register("oxidized_copper_bulb",
            () -> new WeatheringCopperBlocks.OxidizedBulb(BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER)
                    .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 4 : 0)
                    .sound(ModSounds.COPPER_BULB_SOUNDS())
                    .isRedstoneConductor((state, reader, pos) -> false)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_BULB = BLOCKS.register("waxed_oxidized_copper_bulb",
            () -> new OxidizedCopperBulbBlock(BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER)
                    .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 4 : 0)
                    .sound(ModSounds.COPPER_BULB_SOUNDS())
                    .isRedstoneConductor((state, reader, pos) -> false)));

    public static final RegistryObject<Block> COPPER_DOOR = BLOCKS.register("copper_door",
            () -> new WeatheringDoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_COPPER_DOOR = BLOCKS.register("waxed_copper_door",
            () -> new ModCopperDoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> EXPOSED_COPPER_DOOR = BLOCKS.register("exposed_copper_door",
            () -> new WeatheringDoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_DOOR = BLOCKS.register("waxed_exposed_copper_door",
            () -> new ModCopperDoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WEATHERED_COPPER_DOOR = BLOCKS.register("weathered_copper_door",
            () -> new WeatheringDoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_DOOR = BLOCKS
            .register("waxed_weathered_copper_door", () -> new ModCopperDoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_DOOR = BLOCKS.register("oxidized_copper_door",
            () -> new WeatheringDoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_DOOR = BLOCKS.register("waxed_oxidized_copper_door",
            () -> new ModCopperDoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER).noOcclusion().sound(SoundType.COPPER)));

    public static final RegistryObject<Block> COPPER_TRAPDOOR = BLOCKS.register("copper_trapdoor",
            () -> new WeatheringTrapdoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_COPPER_TRAPDOOR = BLOCKS.register("waxed_copper_trapdoor",
            () -> new ModCopperTrapdoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> EXPOSED_COPPER_TRAPDOOR = BLOCKS.register("exposed_copper_trapdoor",
            () -> new WeatheringTrapdoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_TRAPDOOR = BLOCKS
            .register("waxed_exposed_copper_trapdoor", () -> new ModCopperTrapdoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WEATHERED_COPPER_TRAPDOOR = BLOCKS.register("weathered_copper_trapdoor",
            () -> new WeatheringTrapdoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_TRAPDOOR = BLOCKS
            .register("waxed_weathered_copper_trapdoor", () -> new ModCopperTrapdoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_TRAPDOOR = BLOCKS.register("oxidized_copper_trapdoor",
            () -> new WeatheringTrapdoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER).noOcclusion().sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_TRAPDOOR = BLOCKS
            .register("waxed_oxidized_copper_trapdoor", () -> new ModCopperTrapdoorBlock(
                    BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER).noOcclusion().sound(SoundType.COPPER)));

    public static final RegistryObject<Block> COPPER_BARS = BLOCKS.register("copper_bars",
            () -> new WeatheringBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_COPPER_BARS = BLOCKS.register("waxed_copper_bars",
            () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> EXPOSED_COPPER_BARS = BLOCKS.register("exposed_copper_bars",
            () -> new WeatheringBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_BARS = BLOCKS.register("waxed_exposed_copper_bars",
            () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WEATHERED_COPPER_BARS = BLOCKS.register("weathered_copper_bars",
            () -> new WeatheringBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_BARS = BLOCKS.register(
            "waxed_weathered_copper_bars",
            () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_BARS = BLOCKS.register("oxidized_copper_bars",
            () -> new WeatheringBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_BARS = BLOCKS.register("waxed_oxidized_copper_bars",
            () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));

    public static final RegistryObject<Block> COPPER_BUTTON = BLOCKS.register("copper_button",
            () -> new WeatheringButtonBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noCollission()
                    .strength(0.5F).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_COPPER_BUTTON = BLOCKS.register("waxed_copper_button",
            () -> new ModButtonBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noCollission().strength(0.5F)
                    .sound(SoundType.COPPER)));
    public static final RegistryObject<Block> EXPOSED_COPPER_BUTTON = BLOCKS.register("exposed_copper_button",
            () -> new WeatheringButtonBlock(BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER).noCollission()
                    .strength(0.5F).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_BUTTON = BLOCKS
            .register("waxed_exposed_copper_button", () -> new ModButtonBlock(BlockBehaviour.Properties
                    .copy(Blocks.EXPOSED_COPPER).noCollission().strength(0.5F).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WEATHERED_COPPER_BUTTON = BLOCKS.register("weathered_copper_button",
            () -> new WeatheringButtonBlock(BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER).noCollission()
                    .strength(0.5F).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_BUTTON = BLOCKS
            .register("waxed_weathered_copper_button", () -> new ModButtonBlock(BlockBehaviour.Properties
                    .copy(Blocks.WEATHERED_COPPER).noCollission().strength(0.5F).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_BUTTON = BLOCKS.register("oxidized_copper_button",
            () -> new WeatheringButtonBlock(BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER).noCollission()
                    .strength(0.5F).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_BUTTON = BLOCKS
            .register("waxed_oxidized_copper_button", () -> new ModButtonBlock(BlockBehaviour.Properties
                    .copy(Blocks.OXIDIZED_COPPER).noCollission().strength(0.5F).sound(SoundType.COPPER)));

    public static final RegistryObject<Block> COPPER_PRESSURE_PLATE = BLOCKS.register("copper_pressure_plate",
            () -> new WeatheringPressurePlateBlock(150, BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
                    .noCollission().strength(0.5F).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_COPPER_PRESSURE_PLATE = BLOCKS
            .register("waxed_copper_pressure_plate", () -> new ModPressurePlateBlock(150, BlockBehaviour.Properties
                    .copy(Blocks.COPPER_BLOCK).noCollission().strength(0.5F).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> EXPOSED_COPPER_PRESSURE_PLATE = BLOCKS.register(
            "exposed_copper_pressure_plate", () -> new WeatheringPressurePlateBlock(150, BlockBehaviour.Properties
                    .copy(Blocks.EXPOSED_COPPER).noCollission().strength(0.5F).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_PRESSURE_PLATE = BLOCKS.register(
            "waxed_exposed_copper_pressure_plate", () -> new ModPressurePlateBlock(150, BlockBehaviour.Properties
                    .copy(Blocks.EXPOSED_COPPER).noCollission().strength(0.5F).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WEATHERED_COPPER_PRESSURE_PLATE = BLOCKS.register(
            "weathered_copper_pressure_plate", () -> new WeatheringPressurePlateBlock(150, BlockBehaviour.Properties
                    .copy(Blocks.WEATHERED_COPPER).noCollission().strength(0.5F).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_PRESSURE_PLATE = BLOCKS.register(
            "waxed_weathered_copper_pressure_plate", () -> new ModPressurePlateBlock(150, BlockBehaviour.Properties
                    .copy(Blocks.WEATHERED_COPPER).noCollission().strength(0.5F).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_PRESSURE_PLATE = BLOCKS.register(
            "oxidized_copper_pressure_plate", () -> new WeatheringPressurePlateBlock(150, BlockBehaviour.Properties
                    .copy(Blocks.OXIDIZED_COPPER).noCollission().strength(0.5F).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_PRESSURE_PLATE = BLOCKS.register(
            "waxed_oxidized_copper_pressure_plate", () -> new ModPressurePlateBlock(150, BlockBehaviour.Properties
                    .copy(Blocks.OXIDIZED_COPPER).noCollission().strength(0.5F).sound(SoundType.COPPER)));

    public static final RegistryObject<Block> COPPER_BOLTS = BLOCKS.register("copper_bolts",
            () -> new WeatheringBoltBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)));
    public static final RegistryObject<Block> WAXED_COPPER_BOLTS = BLOCKS.register("waxed_copper_bolts",
            () -> new SteelBoltBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)));
    public static final RegistryObject<Block> EXPOSED_COPPER_BOLTS = BLOCKS.register("exposed_copper_bolts",
            () -> new WeatheringBoltBlock(BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER)));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_BOLTS = BLOCKS.register("waxed_exposed_copper_bolts",
            () -> new SteelBoltBlock(BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER)));
    public static final RegistryObject<Block> WEATHERED_COPPER_BOLTS = BLOCKS.register("weathered_copper_bolts",
            () -> new WeatheringBoltBlock(BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER)));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_BOLTS = BLOCKS.register(
            "waxed_weathered_copper_bolts",
            () -> new SteelBoltBlock(BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_BOLTS = BLOCKS.register("oxidized_copper_bolts",
            () -> new WeatheringBoltBlock(BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_BOLTS = BLOCKS.register(
            "waxed_oxidized_copper_bolts",
            () -> new SteelBoltBlock(BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER)));

    public static final RegistryObject<Block> GLASS_JAR = BLOCKS.register("glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS).strength(0.3F).sound(SoundType.GLASS)
                    .noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> WHITE_GLASS_JAR = BLOCKS.register("white_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SNOW).strength(0.3F)
                    .sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> ORANGE_GLASS_JAR = BLOCKS.register("orange_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> MAGENTA_GLASS_JAR = BLOCKS.register("magenta_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_MAGENTA)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> LIGHT_BLUE_GLASS_JAR = BLOCKS.register("light_blue_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_LIGHT_BLUE)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> YELLOW_GLASS_JAR = BLOCKS.register("yellow_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_YELLOW)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> LIME_GLASS_JAR = BLOCKS.register("lime_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_LIGHT_GREEN)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> PINK_GLASS_JAR = BLOCKS.register("pink_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PINK)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> GRAY_GLASS_JAR = BLOCKS.register("gray_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> LIGHT_GRAY_GLASS_JAR = BLOCKS.register("light_gray_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_LIGHT_GRAY)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> CYAN_GLASS_JAR = BLOCKS.register("cyan_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_CYAN)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> PURPLE_GLASS_JAR = BLOCKS.register("purple_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> BLUE_GLASS_JAR = BLOCKS.register("blue_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLUE)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> BROWN_GLASS_JAR = BLOCKS.register("brown_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> GREEN_GLASS_JAR = BLOCKS.register("green_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> RED_GLASS_JAR = BLOCKS.register("red_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_RED).strength(0.3F)
                    .sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> BLACK_GLASS_JAR = BLOCKS.register("black_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_BLACK)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> TINTED_GLASS_JAR = BLOCKS.register("tinted_glass_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_GRAY)
                    .strength(0.3F).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> GOLDEN_JAR = BLOCKS.register("golden_jar",
            () -> new GlassJarBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.GOLD).strength(0.3F)
                    .sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false)
                    .isViewBlocking((state, reader, pos) -> false)));

    public static final RegistryObject<Block> SNOWY_ORANGE_POPLAR_LEAVES = BLOCKS.register("snowy_orange_poplar_leaves",
            () -> new SnowyLeavesBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isValidSpawn((state, reader, pos, entityType) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false)));

    public static final RegistryObject<Block> SNOWY_RED_POPLAR_LEAVES = BLOCKS.register("snowy_red_poplar_leaves",
            () -> new SnowyLeavesBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isValidSpawn((state, reader, pos, entityType) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false)));

    public static final RegistryObject<Block> SNOWY_YELLOW_POPLAR_LEAVES = BLOCKS.register("snowy_yellow_poplar_leaves",
            () -> new SnowyLeavesBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isValidSpawn((state, reader, pos, entityType) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false)));

    public static final RegistryObject<Block> SNOWY_ORANGE_POPLAR_LEAF_LAYERS = BLOCKS.register(
            "snowy_orange_poplar_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                            .strength(0.2f)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_orange_poplar"));

    public static final RegistryObject<Block> SNOWY_RED_POPLAR_LEAF_LAYERS = BLOCKS.register(
            "snowy_red_poplar_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                            .strength(0.2f)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_red_poplar"));

    public static final RegistryObject<Block> SNOWY_YELLOW_POPLAR_LEAF_LAYERS = BLOCKS.register(
            "snowy_yellow_poplar_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                            .strength(0.2f)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_yellow_poplar"));

    public static final RegistryObject<Block> SNOWY_ORANGE_POPLAR_LEAF_HEDGE = BLOCKS.register(
            "snowy_orange_poplar_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    WoodItems.SNOWY_ORANGE_POPLAR_LEAF_HEDGE));

    public static final RegistryObject<Block> SNOWY_RED_POPLAR_LEAF_HEDGE = BLOCKS.register(
            "snowy_red_poplar_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    WoodItems.SNOWY_RED_POPLAR_LEAF_HEDGE));

    public static final RegistryObject<Block> SNOWY_YELLOW_POPLAR_LEAF_HEDGE = BLOCKS.register(
            "snowy_yellow_poplar_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    WoodItems.SNOWY_YELLOW_POPLAR_LEAF_HEDGE));

    public static final RegistryObject<Block> ACACIA_LADDER = BLOCKS.register("acacia_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_BLACK_LADDER = BLOCKS.register("ashpen_black_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_BLUE_LADDER = BLOCKS.register("ashpen_blue_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_BROWN_LADDER = BLOCKS.register("ashpen_brown_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_CYAN_LADDER = BLOCKS.register("ashpen_cyan_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_GRAY_LADDER = BLOCKS.register("ashpen_gray_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_GREEN_LADDER = BLOCKS.register("ashpen_green_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_LIGHT_BLUE_LADDER = BLOCKS.register("ashpen_light_blue_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_LIGHT_GRAY_LADDER = BLOCKS.register("ashpen_light_gray_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_LIME_LADDER = BLOCKS.register("ashpen_lime_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_MAGENTA_LADDER = BLOCKS.register("ashpen_magenta_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_ORANGE_LADDER = BLOCKS.register("ashpen_orange_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_PINK_LADDER = BLOCKS.register("ashpen_pink_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_PURPLE_LADDER = BLOCKS.register("ashpen_purple_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_RED_LADDER = BLOCKS.register("ashpen_red_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_WHITE_LADDER = BLOCKS.register("ashpen_white_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> ASHPEN_YELLOW_LADDER = BLOCKS.register("ashpen_yellow_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> BAMBOO_LADDER = BLOCKS.register("bamboo_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> BIRCH_LADDER = BLOCKS.register("birch_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> CHERRY_LADDER = BLOCKS.register("cherry_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> CRIMSON_LADDER = BLOCKS.register("crimson_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> DARK_OAK_LADDER = BLOCKS.register("dark_oak_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> JUNGLE_LADDER = BLOCKS.register("jungle_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> MANGROVE_LADDER = BLOCKS.register("mangrove_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> OAK_LADDER = BLOCKS.register("oak_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> PALE_OAK_LADDER = BLOCKS.register("pale_oak_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> POPLAR_LADDER = BLOCKS.register("poplar_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> SPRUCE_LADDER = BLOCKS.register("spruce_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> STRIPPED_BAMBOO_LADDER = BLOCKS.register("stripped_bamboo_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));
    public static final RegistryObject<Block> WARPED_LADDER = BLOCKS.register("warped_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER)));

    public static final RegistryObject<Block> COPPER_CHEST = BLOCKS.register("copper_chest",
            () -> new WeatheringCopperBlocks.Chest(BlockBehaviour.Properties.copy(Blocks.CHEST).sound(SoundType.COPPER),
                    () -> ModBlockEntities.COPPER_CHEST.get()));
    public static final RegistryObject<Block> EXPOSED_COPPER_CHEST = BLOCKS.register("exposed_copper_chest",
            () -> new WeatheringCopperBlocks.Chest(BlockBehaviour.Properties.copy(Blocks.CHEST).sound(SoundType.COPPER),
                    () -> ModBlockEntities.COPPER_CHEST.get()));
    public static final RegistryObject<Block> WEATHERED_COPPER_CHEST = BLOCKS.register("weathered_copper_chest",
            () -> new WeatheringCopperBlocks.Chest(BlockBehaviour.Properties.copy(Blocks.CHEST).sound(SoundType.COPPER),
                    () -> ModBlockEntities.COPPER_CHEST.get()));
    public static final RegistryObject<Block> OXIDIZED_COPPER_CHEST = BLOCKS.register("oxidized_copper_chest",
            () -> new WeatheringCopperBlocks.Chest(BlockBehaviour.Properties.copy(Blocks.CHEST).sound(SoundType.COPPER),
                    () -> ModBlockEntities.COPPER_CHEST.get()));
    public static final RegistryObject<Block> WAXED_COPPER_CHEST = BLOCKS.register("waxed_copper_chest",
            () -> new CopperChestBlock(true, BlockBehaviour.Properties.copy(Blocks.CHEST).sound(SoundType.COPPER),
                    () -> ModBlockEntities.COPPER_CHEST.get()));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_CHEST = BLOCKS.register("waxed_exposed_copper_chest",
            () -> new CopperChestBlock(true, BlockBehaviour.Properties.copy(Blocks.CHEST).sound(SoundType.COPPER),
                    () -> ModBlockEntities.COPPER_CHEST.get()));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_CHEST = BLOCKS
            .register("waxed_weathered_copper_chest",
                    () -> new CopperChestBlock(true,
                            BlockBehaviour.Properties.copy(Blocks.CHEST).sound(SoundType.COPPER),
                            () -> ModBlockEntities.COPPER_CHEST.get()));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_CHEST = BLOCKS
            .register("waxed_oxidized_copper_chest",
                    () -> new CopperChestBlock(true,
                            BlockBehaviour.Properties.copy(Blocks.CHEST).sound(SoundType.COPPER),
                            () -> ModBlockEntities.COPPER_CHEST.get()));

    public static final RegistryObject<Block> GOLD_BARS = BLOCKS.register("gold_bars",
            () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)));
    public static final RegistryObject<Block> STEEL_BARS = BLOCKS.register("steel_bars",
            () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)));

    public static final RegistryObject<Block> IRON_MESH = BLOCKS.register("iron_mesh",
            () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)));
    public static final RegistryObject<Block> GOLD_MESH = BLOCKS.register("gold_mesh",
            () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)));
    public static final RegistryObject<Block> STEEL_MESH = BLOCKS.register("steel_mesh",
            () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS)));

    public static final RegistryObject<Block> COPPER_MESH = BLOCKS.register("copper_mesh",
            () -> new WeatheringBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> EXPOSED_COPPER_MESH = BLOCKS.register("exposed_copper_mesh",
            () -> new WeatheringBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WEATHERED_COPPER_MESH = BLOCKS.register("weathered_copper_mesh",
            () -> new WeatheringBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_MESH = BLOCKS.register("oxidized_copper_mesh",
            () -> new WeatheringBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));

    public static final RegistryObject<Block> WAXED_COPPER_MESH = BLOCKS.register("waxed_copper_mesh",
            () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_MESH = BLOCKS.register("waxed_exposed_copper_mesh",
            () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_MESH = BLOCKS.register(
            "waxed_weathered_copper_mesh",
            () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_MESH = BLOCKS.register("waxed_oxidized_copper_mesh",
            () -> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER)));

    public static final RegistryObject<Block> GLEAMING_FROGLIGHT = BLOCKS.register("gleaming_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> IVORY_FROGLIGHT = BLOCKS.register("ivory_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> ASHEN_FROGLIGHT = BLOCKS.register("ashen_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> ONYX_FROGLIGHT = BLOCKS.register("onyx_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> DUSKY_FROGLIGHT = BLOCKS.register("dusky_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> SCARLET_FROGLIGHT = BLOCKS.register("scarlet_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> RUSSET_FROGLIGHT = BLOCKS.register("russet_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> VERIDIAN_FROGLIGHT = BLOCKS.register("veridian_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> TIDAL_FROGLIGHT = BLOCKS.register("tidal_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> CERULEAN_FROGLIGHT = BLOCKS.register("cerulean_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> AZURE_FROGLIGHT = BLOCKS.register("azure_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> IRIDESCENT_FROGLIGHT = BLOCKS.register("iridescent_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));
    public static final RegistryObject<Block> CORALINE_FROGLIGHT = BLOCKS.register("coraline_froglight",
            () -> new FroglightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15)));

    public static final RegistryObject<Block> LARGE_COPPER_CHAIN = BLOCKS.register(
            "large_copper_chain",
            () -> new WeatheringLargeChainBlock(
                    BlockBehaviour.Properties.copy(Blocks.CHAIN)
                            .sound(SoundType.CHAIN)));
    public static final RegistryObject<Block> LARGE_EXPOSED_COPPER_CHAIN = BLOCKS.register(
            "large_exposed_copper_chain",
            () -> new WeatheringLargeChainBlock(
                    BlockBehaviour.Properties.copy(Blocks.CHAIN)
                            .sound(SoundType.CHAIN)));
}
