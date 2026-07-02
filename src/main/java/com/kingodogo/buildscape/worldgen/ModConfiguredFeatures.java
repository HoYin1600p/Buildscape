package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(
        modid = BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ModConfiguredFeatures {

    public static final DeferredRegister<
            ConfiguredFeature<?, ?>
            > CONFIGURED_FEATURES = DeferredRegister.create(
            Registry.CONFIGURED_FEATURE_REGISTRY,
            BuildScape.MODID
    );

    public static final RegistryObject<ConfiguredFeature<?, ?>> MANGROVE =
            CONFIGURED_FEATURES.register("mangrove", () ->
                    new ConfiguredFeature<>(Feature.TREE, createMangroveTreeConfiguration())
            );

    public static final RegistryObject<ConfiguredFeature<?, ?>> TALL_MANGROVE =
            CONFIGURED_FEATURES.register("tall_mangrove", () ->
                    new ConfiguredFeature<>(
                            Feature.TREE,
                            createTallMangroveTreeConfiguration()
                    )
            );

    public static final RegistryObject<ConfiguredFeature<?, ?>> POPLAR_TREE =
            CONFIGURED_FEATURES.register("poplar", () ->
                    new ConfiguredFeature<>(
                            Feature.TREE,
                            createPoplarTreeConfiguration()
                    )
            );

    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_MONETS =
            CONFIGURED_FEATURES.register("red_monets", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createMonetConfiguration(ModBlocks.RED_MONETS.get())
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_MONETS =
            CONFIGURED_FEATURES.register("blue_monets", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createMonetConfiguration(ModBlocks.BLUE_MONETS.get())
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_MONETS =
            CONFIGURED_FEATURES.register("purple_monets", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createMonetConfiguration(ModBlocks.PURPLE_MONETS.get())
                    )
            );
    public static final RegistryObject<
            ConfiguredFeature<?, ?>
            > LIGHT_BLUE_MONETS = CONFIGURED_FEATURES.register("light_blue_monets", () ->
            new ConfiguredFeature<>(
                    Feature.RANDOM_PATCH,
                    createMonetConfiguration(ModBlocks.LIGHT_BLUE_MONETS.get())
            )
    );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_MONETS =
            CONFIGURED_FEATURES.register("pink_monets", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createMonetConfiguration(ModBlocks.PINK_MONETS.get())
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> YELLOW_MONETS =
            CONFIGURED_FEATURES.register("yellow_monets", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createMonetConfiguration(ModBlocks.YELLOW_MONETS.get())
                    )
            );

    @Deprecated
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETALS =
            CONFIGURED_FEATURES.register("red_petals", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfigurationWithRandomStates(ModBlocks.RED_PETAL.get())
                    )
            );

    @Deprecated
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETALS =
            CONFIGURED_FEATURES.register("blue_petals", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfigurationWithRandomStates(ModBlocks.BLUE_PETAL.get())
                    )
            );

    @Deprecated
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETALS =
            CONFIGURED_FEATURES.register("orange_petals", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfigurationWithRandomStates(ModBlocks.ORANGE_PETAL.get())
                    )
            );

    @Deprecated
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETALS =
            CONFIGURED_FEATURES.register("pink_petals", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfigurationWithRandomStates(ModBlocks.PINK_PETAL.get())
                    )
            );

    @Deprecated
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETALS =
            CONFIGURED_FEATURES.register("purple_petals", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfigurationWithRandomStates(ModBlocks.PURPLE_PETAL.get())
                    )
            );

    public static final RegistryObject<ConfiguredFeature<?, ?>> ALL_PETALS =
            CONFIGURED_FEATURES.register("all_petals", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createAllPetalsConfigurationWithRandomStates()
                    )
            );

    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_1_N =
            CONFIGURED_FEATURES.register("red_petal_1_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 1, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_1_S =
            CONFIGURED_FEATURES.register("red_petal_1_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 1, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_1_E =
            CONFIGURED_FEATURES.register("red_petal_1_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 1, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_1_W =
            CONFIGURED_FEATURES.register("red_petal_1_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 1, Direction.WEST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_2_N =
            CONFIGURED_FEATURES.register("red_petal_2_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 2, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_2_S =
            CONFIGURED_FEATURES.register("red_petal_2_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 2, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_2_E =
            CONFIGURED_FEATURES.register("red_petal_2_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 2, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_2_W =
            CONFIGURED_FEATURES.register("red_petal_2_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 2, Direction.WEST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_3_N =
            CONFIGURED_FEATURES.register("red_petal_3_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 3, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_3_S =
            CONFIGURED_FEATURES.register("red_petal_3_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 3, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_3_E =
            CONFIGURED_FEATURES.register("red_petal_3_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 3, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_3_W =
            CONFIGURED_FEATURES.register("red_petal_3_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 3, Direction.WEST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_4_N =
            CONFIGURED_FEATURES.register("red_petal_4_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 4, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_4_S =
            CONFIGURED_FEATURES.register("red_petal_4_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 4, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_4_E =
            CONFIGURED_FEATURES.register("red_petal_4_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 4, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> RED_PETAL_4_W =
            CONFIGURED_FEATURES.register("red_petal_4_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.RED_PETAL.get(), 4, Direction.WEST)
                    )
            );

    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_1_N =
            CONFIGURED_FEATURES.register("blue_petal_1_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 1, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_1_S =
            CONFIGURED_FEATURES.register("blue_petal_1_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 1, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_1_E =
            CONFIGURED_FEATURES.register("blue_petal_1_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 1, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_1_W =
            CONFIGURED_FEATURES.register("blue_petal_1_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 1, Direction.WEST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_2_N =
            CONFIGURED_FEATURES.register("blue_petal_2_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 2, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_2_S =
            CONFIGURED_FEATURES.register("blue_petal_2_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 2, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_2_E =
            CONFIGURED_FEATURES.register("blue_petal_2_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 2, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_2_W =
            CONFIGURED_FEATURES.register("blue_petal_2_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 2, Direction.WEST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_3_N =
            CONFIGURED_FEATURES.register("blue_petal_3_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 3, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_3_S =
            CONFIGURED_FEATURES.register("blue_petal_3_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 3, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_3_E =
            CONFIGURED_FEATURES.register("blue_petal_3_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 3, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_3_W =
            CONFIGURED_FEATURES.register("blue_petal_3_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 3, Direction.WEST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_4_N =
            CONFIGURED_FEATURES.register("blue_petal_4_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 4, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_4_S =
            CONFIGURED_FEATURES.register("blue_petal_4_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 4, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_4_E =
            CONFIGURED_FEATURES.register("blue_petal_4_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 4, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> BLUE_PETAL_4_W =
            CONFIGURED_FEATURES.register("blue_petal_4_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 4, Direction.WEST)
                    )
            );

    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_1_N =
            CONFIGURED_FEATURES.register("orange_petal_1_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    1,
                                    Direction.NORTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_1_S =
            CONFIGURED_FEATURES.register("orange_petal_1_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    1,
                                    Direction.SOUTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_1_E =
            CONFIGURED_FEATURES.register("orange_petal_1_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    1,
                                    Direction.EAST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_1_W =
            CONFIGURED_FEATURES.register("orange_petal_1_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    1,
                                    Direction.WEST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_2_N =
            CONFIGURED_FEATURES.register("orange_petal_2_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    2,
                                    Direction.NORTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_2_S =
            CONFIGURED_FEATURES.register("orange_petal_2_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    2,
                                    Direction.SOUTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_2_E =
            CONFIGURED_FEATURES.register("orange_petal_2_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    2,
                                    Direction.EAST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_2_W =
            CONFIGURED_FEATURES.register("orange_petal_2_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    2,
                                    Direction.WEST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_3_N =
            CONFIGURED_FEATURES.register("orange_petal_3_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    3,
                                    Direction.NORTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_3_S =
            CONFIGURED_FEATURES.register("orange_petal_3_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    3,
                                    Direction.SOUTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_3_E =
            CONFIGURED_FEATURES.register("orange_petal_3_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    3,
                                    Direction.EAST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_3_W =
            CONFIGURED_FEATURES.register("orange_petal_3_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    3,
                                    Direction.WEST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_4_N =
            CONFIGURED_FEATURES.register("orange_petal_4_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    4,
                                    Direction.NORTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_4_S =
            CONFIGURED_FEATURES.register("orange_petal_4_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    4,
                                    Direction.SOUTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_4_E =
            CONFIGURED_FEATURES.register("orange_petal_4_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    4,
                                    Direction.EAST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> ORANGE_PETAL_4_W =
            CONFIGURED_FEATURES.register("orange_petal_4_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    4,
                                    Direction.WEST
                            )
                    )
            );

    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_1_N =
            CONFIGURED_FEATURES.register("pink_petal_1_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 1, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_1_S =
            CONFIGURED_FEATURES.register("pink_petal_1_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 1, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_1_E =
            CONFIGURED_FEATURES.register("pink_petal_1_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 1, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_1_W =
            CONFIGURED_FEATURES.register("pink_petal_1_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 1, Direction.WEST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_2_N =
            CONFIGURED_FEATURES.register("pink_petal_2_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 2, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_2_S =
            CONFIGURED_FEATURES.register("pink_petal_2_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 2, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_2_E =
            CONFIGURED_FEATURES.register("pink_petal_2_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 2, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_2_W =
            CONFIGURED_FEATURES.register("pink_petal_2_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 2, Direction.WEST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_3_N =
            CONFIGURED_FEATURES.register("pink_petal_3_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 3, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_3_S =
            CONFIGURED_FEATURES.register("pink_petal_3_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 3, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_3_E =
            CONFIGURED_FEATURES.register("pink_petal_3_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 3, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_3_W =
            CONFIGURED_FEATURES.register("pink_petal_3_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 3, Direction.WEST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_4_N =
            CONFIGURED_FEATURES.register("pink_petal_4_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 4, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_4_S =
            CONFIGURED_FEATURES.register("pink_petal_4_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 4, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_4_E =
            CONFIGURED_FEATURES.register("pink_petal_4_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 4, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PINK_PETAL_4_W =
            CONFIGURED_FEATURES.register("pink_petal_4_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(ModBlocks.PINK_PETAL.get(), 4, Direction.WEST)
                    )
            );

    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_1_N =
            CONFIGURED_FEATURES.register("purple_petal_1_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    1,
                                    Direction.NORTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_1_S =
            CONFIGURED_FEATURES.register("purple_petal_1_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    1,
                                    Direction.SOUTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_1_E =
            CONFIGURED_FEATURES.register("purple_petal_1_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    1,
                                    Direction.EAST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_1_W =
            CONFIGURED_FEATURES.register("purple_petal_1_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    1,
                                    Direction.WEST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_2_N =
            CONFIGURED_FEATURES.register("purple_petal_2_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    2,
                                    Direction.NORTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_2_S =
            CONFIGURED_FEATURES.register("purple_petal_2_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    2,
                                    Direction.SOUTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_2_E =
            CONFIGURED_FEATURES.register("purple_petal_2_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    2,
                                    Direction.EAST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_2_W =
            CONFIGURED_FEATURES.register("purple_petal_2_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    2,
                                    Direction.WEST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_3_N =
            CONFIGURED_FEATURES.register("purple_petal_3_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    3,
                                    Direction.NORTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_3_S =
            CONFIGURED_FEATURES.register("purple_petal_3_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    3,
                                    Direction.SOUTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_3_E =
            CONFIGURED_FEATURES.register("purple_petal_3_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    3,
                                    Direction.EAST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_3_W =
            CONFIGURED_FEATURES.register("purple_petal_3_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    3,
                                    Direction.WEST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_4_N =
            CONFIGURED_FEATURES.register("purple_petal_4_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    4,
                                    Direction.NORTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_4_S =
            CONFIGURED_FEATURES.register("purple_petal_4_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    4,
                                    Direction.SOUTH
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_4_E =
            CONFIGURED_FEATURES.register("purple_petal_4_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    4,
                                    Direction.EAST
                            )
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> PURPLE_PETAL_4_W =
            CONFIGURED_FEATURES.register("purple_petal_4_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    4,
                                    Direction.WEST
                            )
                    )
            );

    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_1_N =
            CONFIGURED_FEATURES.register("clover_1_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(1, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_1_S =
            CONFIGURED_FEATURES.register("clover_1_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(1, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_1_E =
            CONFIGURED_FEATURES.register("clover_1_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(1, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_1_W =
            CONFIGURED_FEATURES.register("clover_1_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(1, Direction.WEST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_2_N =
            CONFIGURED_FEATURES.register("clover_2_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(2, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_2_S =
            CONFIGURED_FEATURES.register("clover_2_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(2, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_2_E =
            CONFIGURED_FEATURES.register("clover_2_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(2, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_2_W =
            CONFIGURED_FEATURES.register("clover_2_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(2, Direction.WEST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_3_N =
            CONFIGURED_FEATURES.register("clover_3_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(3, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_3_S =
            CONFIGURED_FEATURES.register("clover_3_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(3, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_3_E =
            CONFIGURED_FEATURES.register("clover_3_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(3, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_3_W =
            CONFIGURED_FEATURES.register("clover_3_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(3, Direction.WEST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_4_N =
            CONFIGURED_FEATURES.register("clover_4_n", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(4, Direction.NORTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_4_S =
            CONFIGURED_FEATURES.register("clover_4_s", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(4, Direction.SOUTH)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_4_E =
            CONFIGURED_FEATURES.register("clover_4_e", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(4, Direction.EAST)
                    )
            );
    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER_4_W =
            CONFIGURED_FEATURES.register("clover_4_w", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfiguration(4, Direction.WEST)
                    )
            );

    public static final RegistryObject<ConfiguredFeature<?, ?>> CLOVER =
            CONFIGURED_FEATURES.register("clover", () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createCloverConfigurationWithRandomStates(ModBlocks.CLOVER.get())
                    )
            );

    @Deprecated
    public static final RegistryObject<
            ConfiguredFeature<?, ?>
            > COLORED_SPORE_BLOSSOM = CONFIGURED_FEATURES.register(
            "colored_spore_blossom",
            () ->
                    new ConfiguredFeature<>(
                            Feature.SIMPLE_BLOCK,
                            createColoredSporeBlossomConfiguration()
                    )
    );

    public static final RegistryObject<
            ConfiguredFeature<?, ?>
            > ALL_COLORED_SPORE_BLOSSOM = CONFIGURED_FEATURES.register(
            "all_colored_spore_blossom",
            () ->
                    new ConfiguredFeature<>(
                            Feature.SIMPLE_BLOCK,
                            createAllColoredSporeBlossomConfiguration()
                    )
    );

    public static final RegistryObject<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE = CONFIGURED_FEATURES.register(
            "mangrove_propagule",
            () ->
                    new ConfiguredFeature<>(
                            Feature.SIMPLE_BLOCK,
                            createMangrovePropaguleConfiguration()
                    )
    );

    public static final RegistryObject<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_1 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_1",
            () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createMangrovePropagulePatchConfiguration(1)
                    )
    );
    public static final RegistryObject<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_2 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_2",
            () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createMangrovePropagulePatchConfiguration(2)
                    )
    );
    public static final RegistryObject<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_3 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_3",
            () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createMangrovePropagulePatchConfiguration(3)
                    )
    );
    public static final RegistryObject<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_4 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_4",
            () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createMangrovePropagulePatchConfiguration(4)
                    )
    );
    public static final RegistryObject<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_5 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_5",
            () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createMangrovePropagulePatchConfiguration(5)
                    )
    );
    public static final RegistryObject<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_6 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_6",
            () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createMangrovePropagulePatchConfiguration(6)
                    )
    );
    public static final RegistryObject<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_7 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_7",
            () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createMangrovePropagulePatchConfiguration(7)
                    )
    );
    public static final RegistryObject<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_8 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_8",
            () ->
                    new ConfiguredFeature<>(
                            Feature.RANDOM_PATCH,
                            createMangrovePropagulePatchConfiguration(8)
                    )
    );

    private static RandomPatchConfiguration createMonetConfiguration(
            net.minecraft.world.level.block.Block monetBlock
    ) {
        ConfiguredFeature<?, ?> simpleBlockFeature = new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(monetBlock.defaultBlockState())
                )
        );

        PlacedFeature placedFeature = new PlacedFeature(
                Holder.direct(simpleBlockFeature),
                List.of(
                        net.minecraft.world.level.levelgen.placement.BlockPredicateFilter.forPredicate(
                                net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.matchesBlocks(
                                        List.of(net.minecraft.world.level.block.Blocks.AIR)
                                )
                        )
                )
        );

        return new RandomPatchConfiguration(64, 7, 3, Holder.direct(placedFeature));
    }

    private static RandomPatchConfiguration createPetalConfigurationWithRandomStates(
            net.minecraft.world.level.block.Block petalBlock
    ) {
        IntegerProperty FLOWER_AMOUNT =
                com.kingodogo.buildscape.block.PetalBlock.FLOWER_AMOUNT;
        DirectionProperty FACING_PROP = BlockStateProperties.HORIZONTAL_FACING;

        List<net.minecraft.world.level.block.state.BlockState> states =
                new ArrayList<>();

        Direction[] facings = {
                Direction.NORTH,
                Direction.SOUTH,
                Direction.EAST,
                Direction.WEST,
        };
        for (int state = 1; state <= 4; state++) {
            for (Direction facing : facings) {
                net.minecraft.world.level.block.state.BlockState blockState = petalBlock
                        .defaultBlockState()
                        .setValue(FLOWER_AMOUNT, state)
                        .setValue(FACING_PROP, facing);
                states.add(blockState);
            }
        }

        RandomStateProvider stateProvider = new RandomStateProvider(states);

        ConfiguredFeature<?, ?> simpleBlockFeature = new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(stateProvider)
        );

        PlacedFeature placedFeature = new PlacedFeature(
                Holder.direct(simpleBlockFeature),
                List.of(
                        net.minecraft.world.level.levelgen.placement.BlockPredicateFilter.forPredicate(
                                net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.matchesBlocks(
                                        List.of(net.minecraft.world.level.block.Blocks.AIR)
                                )
                        )
                )
        );

        return new RandomPatchConfiguration(32, 7, 3, Holder.direct(placedFeature));
    }

    private static RandomPatchConfiguration createAllPetalsConfigurationWithRandomStates() {
        IntegerProperty FLOWER_AMOUNT =
                com.kingodogo.buildscape.block.PetalBlock.FLOWER_AMOUNT;
        DirectionProperty FACING_PROP = BlockStateProperties.HORIZONTAL_FACING;

        List<net.minecraft.world.level.block.state.BlockState> states =
                new ArrayList<>();

        net.minecraft.world.level.block.Block[] petalBlocks = {
                ModBlocks.RED_PETAL.get(),
                ModBlocks.BLUE_PETAL.get(),
                ModBlocks.ORANGE_PETAL.get(),
                ModBlocks.PINK_PETAL.get(),
                ModBlocks.PURPLE_PETAL.get(),
        };

        Direction[] facings = {
                Direction.NORTH,
                Direction.SOUTH,
                Direction.EAST,
                Direction.WEST,
        };

        for (net.minecraft.world.level.block.Block petalBlock : petalBlocks) {
            for (int state = 1; state <= 4; state++) {
                for (Direction facing : facings) {
                    net.minecraft.world.level.block.state.BlockState blockState =
                            petalBlock
                                    .defaultBlockState()
                                    .setValue(FLOWER_AMOUNT, state)
                                    .setValue(FACING_PROP, facing);
                    states.add(blockState);
                }
            }
        }

        RandomStateProvider stateProvider = new RandomStateProvider(states);

        ConfiguredFeature<?, ?> simpleBlockFeature = new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(stateProvider)
        );

        PlacedFeature placedFeature = new PlacedFeature(
                Holder.direct(simpleBlockFeature),
                List.of(
                        net.minecraft.world.level.levelgen.placement.BlockPredicateFilter.forPredicate(
                                net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.matchesBlocks(
                                        List.of(net.minecraft.world.level.block.Blocks.AIR)
                                )
                        )
                )
        );

        return new RandomPatchConfiguration(
                192,
                7,
                3,
                Holder.direct(placedFeature)
        );
    }

    private static RandomPatchConfiguration createCloverConfigurationWithRandomStates(
            net.minecraft.world.level.block.Block cloverBlock
    ) {
        IntegerProperty FLOWER_AMOUNT =
                com.kingodogo.buildscape.block.CloverBlock.FLOWER_AMOUNT;
        DirectionProperty FACING_PROP = BlockStateProperties.HORIZONTAL_FACING;

        List<net.minecraft.world.level.block.state.BlockState> states =
                new ArrayList<>();

        Direction[] facings = {
                Direction.NORTH,
                Direction.SOUTH,
                Direction.EAST,
                Direction.WEST,
        };
        for (int state = 1; state <= 4; state++) {
            for (Direction facing : facings) {
                net.minecraft.world.level.block.state.BlockState blockState =
                        cloverBlock
                                .defaultBlockState()
                                .setValue(FLOWER_AMOUNT, state)
                                .setValue(FACING_PROP, facing);
                states.add(blockState);
            }
        }

        RandomStateProvider stateProvider = new RandomStateProvider(states);

        ConfiguredFeature<?, ?> simpleBlockFeature = new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(stateProvider)
        );

        PlacedFeature placedFeature = new PlacedFeature(
                Holder.direct(simpleBlockFeature),
                List.of(
                        net.minecraft.world.level.levelgen.placement.BlockPredicateFilter.forPredicate(
                                net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.matchesBlocks(
                                        List.of(net.minecraft.world.level.block.Blocks.AIR)
                                )
                        )
                )
        );

        return new RandomPatchConfiguration(64, 7, 3, Holder.direct(placedFeature));
    }

    private static RandomPatchConfiguration createPetalConfiguration(
            net.minecraft.world.level.block.Block petalBlock,
            int flowerAmount,
            Direction facing
    ) {
        net.minecraft.world.level.block.state.BlockState state =
                petalBlock.defaultBlockState();
        IntegerProperty FLOWER_AMOUNT =
                com.kingodogo.buildscape.block.PetalBlock.FLOWER_AMOUNT;
        DirectionProperty FACING_PROP = BlockStateProperties.HORIZONTAL_FACING;

        state = state
                .setValue(FLOWER_AMOUNT, flowerAmount)
                .setValue(FACING_PROP, facing);

        ConfiguredFeature<?, ?> simpleBlockFeature = new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(state))
        );

        PlacedFeature placedFeature = new PlacedFeature(
                Holder.direct(simpleBlockFeature),
                List.of(
                        net.minecraft.world.level.levelgen.placement.BlockPredicateFilter.forPredicate(
                                net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.matchesBlocks(
                                        List.of(net.minecraft.world.level.block.Blocks.AIR)
                                )
                        )
                )
        );

        return new RandomPatchConfiguration(64, 7, 3, Holder.direct(placedFeature));
    }

    private static RandomPatchConfiguration createCloverConfiguration(
            int flowerAmount,
            Direction facing
    ) {
        net.minecraft.world.level.block.state.BlockState state =
                ModBlocks.CLOVER.get().defaultBlockState();
        IntegerProperty FLOWER_AMOUNT =
                com.kingodogo.buildscape.block.CloverBlock.FLOWER_AMOUNT;
        DirectionProperty FACING_PROP = BlockStateProperties.HORIZONTAL_FACING;

        state = state
                .setValue(FLOWER_AMOUNT, flowerAmount)
                .setValue(FACING_PROP, facing);

        ConfiguredFeature<?, ?> simpleBlockFeature = new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(state))
        );

        PlacedFeature placedFeature = new PlacedFeature(
                Holder.direct(simpleBlockFeature),
                List.of(
                        net.minecraft.world.level.levelgen.placement.BlockPredicateFilter.forPredicate(
                                net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.matchesBlocks(
                                        List.of(net.minecraft.world.level.block.Blocks.AIR)
                                )
                        )
                )
        );

        return new RandomPatchConfiguration(64, 7, 3, Holder.direct(placedFeature));
    }

    private static SimpleBlockConfiguration createColoredSporeBlossomConfiguration() {
        return new SimpleBlockConfiguration(
                BlockStateProvider.simple(
                        ModBlocks.RED_SPORE_BLOSSOM.get().defaultBlockState()
                )
        );
    }

    private static SimpleBlockConfiguration createAllColoredSporeBlossomConfiguration() {
        List<net.minecraft.world.level.block.state.BlockState> states =
                new ArrayList<>();

        states.add(ModBlocks.RED_SPORE_BLOSSOM.get().defaultBlockState());
        states.add(ModBlocks.CYAN_SPORE_BLOSSOM.get().defaultBlockState());
        states.add(ModBlocks.BLUE_SPORE_BLOSSOM.get().defaultBlockState());
        states.add(ModBlocks.PURPLE_SPORE_BLOSSOM.get().defaultBlockState());
        states.add(ModBlocks.ORANGE_SPORE_BLOSSOM.get().defaultBlockState());

        RandomStateProvider stateProvider = new RandomStateProvider(states);

        return new SimpleBlockConfiguration(stateProvider);
    }

    private static SimpleBlockConfiguration createMangrovePropaguleConfiguration() {
        return new SimpleBlockConfiguration(
                BlockStateProvider.simple(
                        ModBlocks.MANGROVE_PROPAGULE.get().defaultBlockState()
                )
        );
    }

    private static RandomPatchConfiguration createMangrovePropagulePatchConfiguration(
            int count
    ) {
        ConfiguredFeature<?, ?> simpleBlockFeature = new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(
                                ModBlocks.MANGROVE_PROPAGULE.get().defaultBlockState()
                        )
                )
        );

        PlacedFeature placedFeature = new PlacedFeature(
                Holder.direct(simpleBlockFeature),
                List.of(
                        net.minecraft.world.level.levelgen.placement.BlockPredicateFilter.forPredicate(
                                net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.matchesBlocks(
                                        List.of(net.minecraft.world.level.block.Blocks.AIR)
                                )
                        )
                )
        );

        return new RandomPatchConfiguration(
                count,
                7,
                3,
                Holder.direct(placedFeature)
        );
    }

    private static TreeConfiguration createMangroveTreeConfiguration() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(
                        ModBlocks.MANGROVE_LOG.get().defaultBlockState()
                ),
                new MangroveUpwardsBranchingTrunkPlacer(
                        2,
                        1,
                        4,
                        UniformInt.of(1, 4),
                        0.5F,
                        UniformInt.of(0, 1)
                ),
                BlockStateProvider.simple(
                        ModBlocks.MANGROVE_LEAVES.get().defaultBlockState()
                ),
                new MangroveRandomSpreadFoliagePlacer(
                        ConstantInt.of(3),
                        ConstantInt.of(0),
                        ConstantInt.of(2),
                        70
                ),
                new TwoLayersFeatureSize(2, 0, 2)
        )
                .decorators(
                        List.of(
                                new MangroveRootDecorator(
                                        ConstantInt.of(8),
                                        ConstantInt.of(15),
                                        0.2F,
                                        UniformInt.of(1, 3)
                                ),
                                new MangroveMossCarpetDecorator(0.5F),
                                new MangroveLeaveVineDecorator(0.125F),
                                new MangrovePropaguleDecorator(
                                        0.14F,
                                        ConstantInt.of(1),
                                        ConstantInt.of(0),
                                        2
                                ),
                                new BeehiveDecorator(0.01F)
                        )
                )
                .ignoreVines()
                .build();
    }

    private static TreeConfiguration createTallMangroveTreeConfiguration() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(
                        ModBlocks.MANGROVE_LOG.get().defaultBlockState()
                ),
                new MangroveUpwardsBranchingTrunkPlacer(
                        4,
                        1,
                        9,
                        UniformInt.of(1, 6),
                        0.5F,
                        UniformInt.of(0, 1)
                ),
                BlockStateProvider.simple(
                        ModBlocks.MANGROVE_LEAVES.get().defaultBlockState()
                ),
                new MangroveRandomSpreadFoliagePlacer(
                        ConstantInt.of(3),
                        ConstantInt.of(0),
                        ConstantInt.of(2),
                        70
                ),
                new TwoLayersFeatureSize(3, 0, 2)
        )
                .decorators(
                        List.of(
                                new MangroveRootDecorator(
                                        ConstantInt.of(8),
                                        ConstantInt.of(15),
                                        0.2F,
                                        UniformInt.of(3, 7)
                                ),
                                new MangroveMossCarpetDecorator(0.5F),
                                new MangroveLeaveVineDecorator(0.125F),
                                new MangrovePropaguleDecorator(
                                        0.14F,
                                        ConstantInt.of(1),
                                        ConstantInt.of(0),
                                        2
                                ),
                                new BeehiveDecorator(0.01F)
                        )
                )
                .ignoreVines()
                .build();
    }

    public static String getRandomMangroveTreeVariant(Random random) {
        if (random.nextDouble() < 0.85) {
            return "tall_mangrove";
        }
        return "mangrove";
    }

    public static ResourceLocation getMangroveTreeResourceLocation(
            String variant
    ) {
        return ResourceLocation.tryParse(BuildScape.MODID + ":" + variant);
    }

    private static TreeConfiguration createPoplarTreeConfiguration() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(
                        ModBlocks.POPLAR_LOG.get().defaultBlockState()
                ),
                new net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer(
                        4,
                        2,
                        0
                ),
                new net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider(
                        net.minecraft.util.random.SimpleWeightedRandomList.<net.minecraft.world.level.block.state.BlockState>builder()
                                .add(ModBlocks.RED_POPLAR_LEAVES.get().defaultBlockState(), 1)
                                .add(ModBlocks.ORANGE_POPLAR_LEAVES.get().defaultBlockState(), 1)
                                .add(ModBlocks.YELLOW_POPLAR_LEAVES.get().defaultBlockState(), 1)
                ),
                new net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer(
                        net.minecraft.util.valueproviders.ConstantInt.of(2),
                        net.minecraft.util.valueproviders.ConstantInt.of(0),
                        3
                ),
                new net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize(1, 0, 1)
        )
                .ignoreVines()
                .build();
    }
}
