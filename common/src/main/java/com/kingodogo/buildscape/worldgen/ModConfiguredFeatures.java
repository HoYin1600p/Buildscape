package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.block.ModBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class ModConfiguredFeatures {

    public interface FeatureSupplier<T> extends java.util.function.Supplier<T> {
        default boolean isPresent() {
            return get() != null;
        }
    }

    public static class FeatureHolder<T> implements FeatureSupplier<T> {
        private final String name;
        private final java.util.function.Supplier<T> supplier;
        private T value;

        public FeatureHolder(String name, java.util.function.Supplier<T> supplier) {
            this.name = name;
            this.supplier = supplier;
        }

        @Override
        public synchronized T get() {
            if (value == null && supplier != null) {
                value = supplier.get();
                try {
                    Services.PLATFORM.registerConfiguredFeature(
                            new CommonId(BuildscapeCommon.MOD_ID, name),
                            value
                    );
                } catch (Throwable e) {
                    BuildscapeCommon.LOGGER.warn("Failed to register configured feature {}", name, e);
                }
            }
            return value;
        }
    }

    public static class FeatureRegistry {
        public <T extends ConfiguredFeature<?, ?>> FeatureHolder<T> register(String name, java.util.function.Supplier<T> supplier) {
            return new FeatureHolder<>(name, supplier);
        }
    }

    public static final FeatureRegistry CONFIGURED_FEATURES = new FeatureRegistry();

    public static final FeatureHolder<ConfiguredFeature<?, ?>> POPLAR_TREE =
            CONFIGURED_FEATURES.register("poplar", () ->
                    new ConfiguredFeature<>(
                            Feature.TREE,
                            createPoplarTreeConfiguration()
                    )
            );

    public static final FeatureHolder<ConfiguredFeature<?, ?>> PALE_OAK_TREE =
            CONFIGURED_FEATURES.register("pale_oak", () ->
                    new ConfiguredFeature<>(
                            Feature.TREE,
                            createPaleOakTreeConfiguration()
                    )
            );

    public static final FeatureHolder<ConfiguredFeature<?, ?>> CHERRY_TREE =
            CONFIGURED_FEATURES.register("cherry", () ->
                    new ConfiguredFeature<>(
                            Feature.TREE,
                            createCherryTreeConfiguration()
                    )
            );

    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_MONETS =
            CONFIGURED_FEATURES.register("red_monets", () ->
                    createMonetConfiguration(ModBlocks.RED_MONETS.get())
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_MONETS =
            CONFIGURED_FEATURES.register("blue_monets", () ->
                    createMonetConfiguration(ModBlocks.BLUE_MONETS.get())
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_MONETS =
            CONFIGURED_FEATURES.register("purple_monets", () ->
                    createMonetConfiguration(ModBlocks.PURPLE_MONETS.get())
            );
    public static final FeatureHolder<
            ConfiguredFeature<?, ?>
            > LIGHT_BLUE_MONETS = CONFIGURED_FEATURES.register("light_blue_monets", () ->
            createMonetConfiguration(ModBlocks.LIGHT_BLUE_MONETS.get())
    );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_MONETS =
            CONFIGURED_FEATURES.register("pink_monets", () ->
                    createMonetConfiguration(ModBlocks.PINK_MONETS.get())
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> YELLOW_MONETS =
            CONFIGURED_FEATURES.register("yellow_monets", () ->
                    createMonetConfiguration(ModBlocks.YELLOW_MONETS.get())
            );

    @Deprecated
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETALS =
            CONFIGURED_FEATURES.register("red_petals", () ->
                    createPetalConfigurationWithRandomStates(ModBlocks.RED_PETAL.get())
            );

    @Deprecated
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETALS =
            CONFIGURED_FEATURES.register("blue_petals", () ->
                    createPetalConfigurationWithRandomStates(ModBlocks.BLUE_PETAL.get())
            );

    @Deprecated
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETALS =
            CONFIGURED_FEATURES.register("orange_petals", () ->
                    createPetalConfigurationWithRandomStates(ModBlocks.ORANGE_PETAL.get())
            );

    @Deprecated
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETALS =
            CONFIGURED_FEATURES.register("pink_petals", () ->
                    createPetalConfigurationWithRandomStates(vanillaBlock("minecraft:pink_petals"))
            );

    @Deprecated
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETALS =
            CONFIGURED_FEATURES.register("purple_petals", () ->
                    createPetalConfigurationWithRandomStates(ModBlocks.PURPLE_PETAL.get())
            );

    public static final FeatureHolder<ConfiguredFeature<?, ?>> ALL_PETALS =
            CONFIGURED_FEATURES.register("all_petals", () ->
                    createAllPetalsConfigurationWithRandomStates()
            );

    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_1_N =
            CONFIGURED_FEATURES.register("red_petal_1_n", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 1, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_1_S =
            CONFIGURED_FEATURES.register("red_petal_1_s", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 1, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_1_E =
            CONFIGURED_FEATURES.register("red_petal_1_e", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 1, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_1_W =
            CONFIGURED_FEATURES.register("red_petal_1_w", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 1, Direction.WEST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_2_N =
            CONFIGURED_FEATURES.register("red_petal_2_n", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 2, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_2_S =
            CONFIGURED_FEATURES.register("red_petal_2_s", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 2, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_2_E =
            CONFIGURED_FEATURES.register("red_petal_2_e", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 2, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_2_W =
            CONFIGURED_FEATURES.register("red_petal_2_w", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 2, Direction.WEST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_3_N =
            CONFIGURED_FEATURES.register("red_petal_3_n", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 3, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_3_S =
            CONFIGURED_FEATURES.register("red_petal_3_s", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 3, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_3_E =
            CONFIGURED_FEATURES.register("red_petal_3_e", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 3, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_3_W =
            CONFIGURED_FEATURES.register("red_petal_3_w", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 3, Direction.WEST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_4_N =
            CONFIGURED_FEATURES.register("red_petal_4_n", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 4, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_4_S =
            CONFIGURED_FEATURES.register("red_petal_4_s", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 4, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_4_E =
            CONFIGURED_FEATURES.register("red_petal_4_e", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 4, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> RED_PETAL_4_W =
            CONFIGURED_FEATURES.register("red_petal_4_w", () ->
                    createPetalConfiguration(ModBlocks.RED_PETAL.get(), 4, Direction.WEST)
            );

    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_1_N =
            CONFIGURED_FEATURES.register("blue_petal_1_n", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 1, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_1_S =
            CONFIGURED_FEATURES.register("blue_petal_1_s", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 1, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_1_E =
            CONFIGURED_FEATURES.register("blue_petal_1_e", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 1, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_1_W =
            CONFIGURED_FEATURES.register("blue_petal_1_w", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 1, Direction.WEST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_2_N =
            CONFIGURED_FEATURES.register("blue_petal_2_n", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 2, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_2_S =
            CONFIGURED_FEATURES.register("blue_petal_2_s", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 2, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_2_E =
            CONFIGURED_FEATURES.register("blue_petal_2_e", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 2, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_2_W =
            CONFIGURED_FEATURES.register("blue_petal_2_w", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 2, Direction.WEST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_3_N =
            CONFIGURED_FEATURES.register("blue_petal_3_n", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 3, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_3_S =
            CONFIGURED_FEATURES.register("blue_petal_3_s", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 3, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_3_E =
            CONFIGURED_FEATURES.register("blue_petal_3_e", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 3, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_3_W =
            CONFIGURED_FEATURES.register("blue_petal_3_w", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 3, Direction.WEST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_4_N =
            CONFIGURED_FEATURES.register("blue_petal_4_n", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 4, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_4_S =
            CONFIGURED_FEATURES.register("blue_petal_4_s", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 4, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_4_E =
            CONFIGURED_FEATURES.register("blue_petal_4_e", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 4, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> BLUE_PETAL_4_W =
            CONFIGURED_FEATURES.register("blue_petal_4_w", () ->
                    createPetalConfiguration(ModBlocks.BLUE_PETAL.get(), 4, Direction.WEST)
            );

    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_1_N =
            CONFIGURED_FEATURES.register("orange_petal_1_n", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    1,
                                    Direction.NORTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_1_S =
            CONFIGURED_FEATURES.register("orange_petal_1_s", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    1,
                                    Direction.SOUTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_1_E =
            CONFIGURED_FEATURES.register("orange_petal_1_e", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    1,
                                    Direction.EAST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_1_W =
            CONFIGURED_FEATURES.register("orange_petal_1_w", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    1,
                                    Direction.WEST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_2_N =
            CONFIGURED_FEATURES.register("orange_petal_2_n", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    2,
                                    Direction.NORTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_2_S =
            CONFIGURED_FEATURES.register("orange_petal_2_s", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    2,
                                    Direction.SOUTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_2_E =
            CONFIGURED_FEATURES.register("orange_petal_2_e", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    2,
                                    Direction.EAST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_2_W =
            CONFIGURED_FEATURES.register("orange_petal_2_w", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    2,
                                    Direction.WEST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_3_N =
            CONFIGURED_FEATURES.register("orange_petal_3_n", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    3,
                                    Direction.NORTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_3_S =
            CONFIGURED_FEATURES.register("orange_petal_3_s", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    3,
                                    Direction.SOUTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_3_E =
            CONFIGURED_FEATURES.register("orange_petal_3_e", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    3,
                                    Direction.EAST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_3_W =
            CONFIGURED_FEATURES.register("orange_petal_3_w", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    3,
                                    Direction.WEST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_4_N =
            CONFIGURED_FEATURES.register("orange_petal_4_n", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    4,
                                    Direction.NORTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_4_S =
            CONFIGURED_FEATURES.register("orange_petal_4_s", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    4,
                                    Direction.SOUTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_4_E =
            CONFIGURED_FEATURES.register("orange_petal_4_e", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    4,
                                    Direction.EAST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> ORANGE_PETAL_4_W =
            CONFIGURED_FEATURES.register("orange_petal_4_w", () ->
                    createPetalConfiguration(
                                    ModBlocks.ORANGE_PETAL.get(),
                                    4,
                                    Direction.WEST
                    )
            );

    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_1_N =
            CONFIGURED_FEATURES.register("pink_petal_1_n", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 1, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_1_S =
            CONFIGURED_FEATURES.register("pink_petal_1_s", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 1, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_1_E =
            CONFIGURED_FEATURES.register("pink_petal_1_e", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 1, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_1_W =
            CONFIGURED_FEATURES.register("pink_petal_1_w", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 1, Direction.WEST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_2_N =
            CONFIGURED_FEATURES.register("pink_petal_2_n", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 2, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_2_S =
            CONFIGURED_FEATURES.register("pink_petal_2_s", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 2, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_2_E =
            CONFIGURED_FEATURES.register("pink_petal_2_e", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 2, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_2_W =
            CONFIGURED_FEATURES.register("pink_petal_2_w", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 2, Direction.WEST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_3_N =
            CONFIGURED_FEATURES.register("pink_petal_3_n", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 3, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_3_S =
            CONFIGURED_FEATURES.register("pink_petal_3_s", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 3, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_3_E =
            CONFIGURED_FEATURES.register("pink_petal_3_e", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 3, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_3_W =
            CONFIGURED_FEATURES.register("pink_petal_3_w", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 3, Direction.WEST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_4_N =
            CONFIGURED_FEATURES.register("pink_petal_4_n", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 4, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_4_S =
            CONFIGURED_FEATURES.register("pink_petal_4_s", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 4, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_4_E =
            CONFIGURED_FEATURES.register("pink_petal_4_e", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 4, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PINK_PETAL_4_W =
            CONFIGURED_FEATURES.register("pink_petal_4_w", () ->
                    createPetalConfiguration(vanillaBlock("minecraft:pink_petals"), 4, Direction.WEST)
            );

    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_1_N =
            CONFIGURED_FEATURES.register("purple_petal_1_n", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    1,
                                    Direction.NORTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_1_S =
            CONFIGURED_FEATURES.register("purple_petal_1_s", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    1,
                                    Direction.SOUTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_1_E =
            CONFIGURED_FEATURES.register("purple_petal_1_e", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    1,
                                    Direction.EAST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_1_W =
            CONFIGURED_FEATURES.register("purple_petal_1_w", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    1,
                                    Direction.WEST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_2_N =
            CONFIGURED_FEATURES.register("purple_petal_2_n", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    2,
                                    Direction.NORTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_2_S =
            CONFIGURED_FEATURES.register("purple_petal_2_s", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    2,
                                    Direction.SOUTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_2_E =
            CONFIGURED_FEATURES.register("purple_petal_2_e", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    2,
                                    Direction.EAST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_2_W =
            CONFIGURED_FEATURES.register("purple_petal_2_w", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    2,
                                    Direction.WEST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_3_N =
            CONFIGURED_FEATURES.register("purple_petal_3_n", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    3,
                                    Direction.NORTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_3_S =
            CONFIGURED_FEATURES.register("purple_petal_3_s", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    3,
                                    Direction.SOUTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_3_E =
            CONFIGURED_FEATURES.register("purple_petal_3_e", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    3,
                                    Direction.EAST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_3_W =
            CONFIGURED_FEATURES.register("purple_petal_3_w", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    3,
                                    Direction.WEST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_4_N =
            CONFIGURED_FEATURES.register("purple_petal_4_n", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    4,
                                    Direction.NORTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_4_S =
            CONFIGURED_FEATURES.register("purple_petal_4_s", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    4,
                                    Direction.SOUTH
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_4_E =
            CONFIGURED_FEATURES.register("purple_petal_4_e", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    4,
                                    Direction.EAST
                    )
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> PURPLE_PETAL_4_W =
            CONFIGURED_FEATURES.register("purple_petal_4_w", () ->
                    createPetalConfiguration(
                                    ModBlocks.PURPLE_PETAL.get(),
                                    4,
                                    Direction.WEST
                    )
            );

    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_1_N =
            CONFIGURED_FEATURES.register("clover_1_n", () ->
                    createCloverConfiguration(1, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_1_S =
            CONFIGURED_FEATURES.register("clover_1_s", () ->
                    createCloverConfiguration(1, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_1_E =
            CONFIGURED_FEATURES.register("clover_1_e", () ->
                    createCloverConfiguration(1, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_1_W =
            CONFIGURED_FEATURES.register("clover_1_w", () ->
                    createCloverConfiguration(1, Direction.WEST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_2_N =
            CONFIGURED_FEATURES.register("clover_2_n", () ->
                    createCloverConfiguration(2, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_2_S =
            CONFIGURED_FEATURES.register("clover_2_s", () ->
                    createCloverConfiguration(2, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_2_E =
            CONFIGURED_FEATURES.register("clover_2_e", () ->
                    createCloverConfiguration(2, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_2_W =
            CONFIGURED_FEATURES.register("clover_2_w", () ->
                    createCloverConfiguration(2, Direction.WEST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_3_N =
            CONFIGURED_FEATURES.register("clover_3_n", () ->
                    createCloverConfiguration(3, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_3_S =
            CONFIGURED_FEATURES.register("clover_3_s", () ->
                    createCloverConfiguration(3, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_3_E =
            CONFIGURED_FEATURES.register("clover_3_e", () ->
                    createCloverConfiguration(3, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_3_W =
            CONFIGURED_FEATURES.register("clover_3_w", () ->
                    createCloverConfiguration(3, Direction.WEST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_4_N =
            CONFIGURED_FEATURES.register("clover_4_n", () ->
                    createCloverConfiguration(4, Direction.NORTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_4_S =
            CONFIGURED_FEATURES.register("clover_4_s", () ->
                    createCloverConfiguration(4, Direction.SOUTH)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_4_E =
            CONFIGURED_FEATURES.register("clover_4_e", () ->
                    createCloverConfiguration(4, Direction.EAST)
            );
    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER_4_W =
            CONFIGURED_FEATURES.register("clover_4_w", () ->
                    createCloverConfiguration(4, Direction.WEST)
            );

    public static final FeatureHolder<ConfiguredFeature<?, ?>> CLOVER =
            CONFIGURED_FEATURES.register("clover", () ->
                    createCloverConfigurationWithRandomStates(ModBlocks.CLOVER.get())
            );

    public static final FeatureHolder<ConfiguredFeature<?, ?>> WILDFLOWERS =
            CONFIGURED_FEATURES.register("wildflowers", () ->
                    createFlowerPatchConfiguration(
                                    vanillaBlock("minecraft:wildflowers"),
                                    com.kingodogo.buildscape.block.WildflowersBlock.FLOWER_AMOUNT,
                                    com.kingodogo.buildscape.block.WildflowersBlock.FACING
                    )
            );

    public static final FeatureHolder<ConfiguredFeature<?, ?>> LEAF_LITTER =
            CONFIGURED_FEATURES.register("leaf_litter", () ->
                    createFlowerPatchConfiguration(
                                    vanillaBlock("minecraft:leaf_litter"),
                                    com.kingodogo.buildscape.block.LeafLitterBlock.FLOWER_AMOUNT,
                                    com.kingodogo.buildscape.block.LeafLitterBlock.FACING
                    )
            );

    @Deprecated
    public static final FeatureHolder<
            ConfiguredFeature<?, ?>
            > COLORED_SPORE_BLOSSOM = CONFIGURED_FEATURES.register(
            "colored_spore_blossom",
            () ->
                    new ConfiguredFeature<>(
                            Feature.SIMPLE_BLOCK,
                            createColoredSporeBlossomConfiguration()
                    )
    );

    public static final FeatureHolder<
            ConfiguredFeature<?, ?>
            > ALL_COLORED_SPORE_BLOSSOM = CONFIGURED_FEATURES.register(
            "all_colored_spore_blossom",
            () ->
                    new ConfiguredFeature<>(
                            Feature.SIMPLE_BLOCK,
                            createAllColoredSporeBlossomConfiguration()
                    )
    );

    public static final FeatureHolder<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE = CONFIGURED_FEATURES.register(
            "mangrove_propagule",
            () ->
                    new ConfiguredFeature<>(
                            Feature.SIMPLE_BLOCK,
                            createMangrovePropaguleConfiguration()
                    )
    );

    public static final FeatureHolder<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_1 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_1",
            () ->
                    createMangrovePropagulePatchConfiguration(1)
    );
    public static final FeatureHolder<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_2 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_2",
            () ->
                    createMangrovePropagulePatchConfiguration(2)
    );
    public static final FeatureHolder<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_3 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_3",
            () ->
                    createMangrovePropagulePatchConfiguration(3)
    );
    public static final FeatureHolder<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_4 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_4",
            () ->
                    createMangrovePropagulePatchConfiguration(4)
    );
    public static final FeatureHolder<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_5 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_5",
            () ->
                    createMangrovePropagulePatchConfiguration(5)
    );
    public static final FeatureHolder<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_6 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_6",
            () ->
                    createMangrovePropagulePatchConfiguration(6)
    );
    public static final FeatureHolder<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_7 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_7",
            () ->
                    createMangrovePropagulePatchConfiguration(7)
    );
    public static final FeatureHolder<
            ConfiguredFeature<?, ?>
            > MANGROVE_PROPAGULE_PATCH_8 = CONFIGURED_FEATURES.register(
            "mangrove_propagule_patch_8",
            () ->
                    createMangrovePropagulePatchConfiguration(8)
    );

    private static ConfiguredFeature<?, ?> createMonetConfiguration(
            com.kingodogo.buildscape.block.BlockDefinition monetBlock
    ) {
        return Services.PLATFORM.createPatchFeature(
                BlockStateProvider.simple(monetBlock.defaultBlockState()),
                64, 7, 3
        );
    }

    private static ConfiguredFeature<?, ?> createPetalConfigurationWithRandomStates(
            com.kingodogo.buildscape.block.BlockDefinition petalBlock
    ) {
        IntegerProperty FLOWER_AMOUNT =
                com.kingodogo.buildscape.block.PetalBlock.FLOWER_AMOUNT;
        Property<Direction> FACING_PROP = BlockStateProperties.HORIZONTAL_FACING;

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

        BlockStateProvider stateProvider = Services.PLATFORM.createRandomStateProvider(states);
        return Services.PLATFORM.createPatchFeature(stateProvider, 32, 7, 3);
    }

    private static com.kingodogo.buildscape.block.BlockDefinition vanillaBlock(String id) {
        var definition = new com.kingodogo.buildscape.block.BlockDefinition(id);
        definition.setBlock(Services.PLATFORM.getBlock(com.kingodogo.buildscape.util.CommonId.parse(id)));
        return definition;
    }
    private static ConfiguredFeature<?, ?> createAllPetalsConfigurationWithRandomStates() {
        IntegerProperty FLOWER_AMOUNT =
                com.kingodogo.buildscape.block.PetalBlock.FLOWER_AMOUNT;
        Property<Direction> FACING_PROP = BlockStateProperties.HORIZONTAL_FACING;

        List<net.minecraft.world.level.block.state.BlockState> states =
                new ArrayList<>();

        com.kingodogo.buildscape.block.BlockDefinition[] petalBlocks = {
                ModBlocks.RED_PETAL.get(),
                ModBlocks.BLUE_PETAL.get(),
                ModBlocks.ORANGE_PETAL.get(),
                vanillaBlock("minecraft:pink_petals"),
                ModBlocks.PURPLE_PETAL.get(),
        };

        Direction[] facings = {
                Direction.NORTH,
                Direction.SOUTH,
                Direction.EAST,
                Direction.WEST,
        };

        for (com.kingodogo.buildscape.block.BlockDefinition petalBlock : petalBlocks) {
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

        BlockStateProvider stateProvider = Services.PLATFORM.createRandomStateProvider(states);
        return Services.PLATFORM.createPatchFeature(stateProvider, 192, 7, 3);
    }

    private static ConfiguredFeature<?, ?> createCloverConfigurationWithRandomStates(
            com.kingodogo.buildscape.block.BlockDefinition cloverBlock
    ) {
        IntegerProperty FLOWER_AMOUNT =
                com.kingodogo.buildscape.block.CloverBlock.FLOWER_AMOUNT;
        Property<Direction> FACING_PROP = BlockStateProperties.HORIZONTAL_FACING;

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

        BlockStateProvider stateProvider = Services.PLATFORM.createRandomStateProvider(states);
        return Services.PLATFORM.createPatchFeature(stateProvider, 64, 7, 3);
    }

    private static ConfiguredFeature<?, ?> createFlowerPatchConfiguration(
            com.kingodogo.buildscape.block.BlockDefinition block,
            IntegerProperty amountProp,
            net.minecraft.world.level.block.state.properties.Property<Direction> facingProp
    ) {
        if (!block.defaultBlockState().hasProperty(amountProp)) {
            var replacement = block.getBlock().getStateDefinition().getProperty("segment_amount");
            if (!(replacement instanceof IntegerProperty)) {
                throw new IllegalStateException("Missing flower patch amount property for " + block.getId());
            }
            amountProp = (IntegerProperty) replacement;
        }
        List<net.minecraft.world.level.block.state.BlockState> states = new ArrayList<>();
        Direction[] facings = { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };
        for (int state = 1; state <= 4; state++) {
            for (Direction facing : facings) {
                states.add(block.defaultBlockState().setValue(amountProp, state).setValue(facingProp, facing));
            }
        }
        BlockStateProvider stateProvider = Services.PLATFORM.createRandomStateProvider(states);
        return Services.PLATFORM.createPatchFeature(stateProvider, 64, 7, 3);
    }

    private static ConfiguredFeature<?, ?> createPetalConfiguration(
            com.kingodogo.buildscape.block.BlockDefinition petalBlock,
            int flowerAmount,
            Direction facing
    ) {
        net.minecraft.world.level.block.state.BlockState state =
                petalBlock.defaultBlockState();
        IntegerProperty FLOWER_AMOUNT =
                com.kingodogo.buildscape.block.PetalBlock.FLOWER_AMOUNT;
        Property<Direction> FACING_PROP = BlockStateProperties.HORIZONTAL_FACING;

        state = state
                .setValue(FLOWER_AMOUNT, flowerAmount)
                .setValue(FACING_PROP, facing);

        return Services.PLATFORM.createPatchFeature(BlockStateProvider.simple(state), 64, 7, 3);
    }

    private static ConfiguredFeature<?, ?> createCloverConfiguration(
            int flowerAmount,
            Direction facing
    ) {
        net.minecraft.world.level.block.state.BlockState state =
                ModBlocks.CLOVER.get().defaultBlockState();
        IntegerProperty FLOWER_AMOUNT =
                com.kingodogo.buildscape.block.CloverBlock.FLOWER_AMOUNT;
        Property<Direction> FACING_PROP = BlockStateProperties.HORIZONTAL_FACING;

        state = state
                .setValue(FLOWER_AMOUNT, flowerAmount)
                .setValue(FACING_PROP, facing);

        return Services.PLATFORM.createPatchFeature(BlockStateProvider.simple(state), 64, 7, 3);
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

        BlockStateProvider stateProvider = Services.PLATFORM.createRandomStateProvider(states);

        return new SimpleBlockConfiguration(stateProvider);
    }

    private static SimpleBlockConfiguration createMangrovePropaguleConfiguration() {
        return new SimpleBlockConfiguration(
                BlockStateProvider.simple(
                        ModBlocks.MANGROVE_PROPAGULE.get().defaultBlockState()
                )
        );
    }

    private static ConfiguredFeature<?, ?> createMangrovePropagulePatchConfiguration(
            int count
    ) {
        return Services.PLATFORM.createPatchFeature(
                BlockStateProvider.simple(
                        ModBlocks.MANGROVE_PROPAGULE.get().defaultBlockState()
                ),
                count,
                7,
                3
        );
    }

    public static String getRandomMangroveTreeVariant(Random random) {
        if (random.nextDouble() < 0.85) {
            return "tall_mangrove";
        }
        return "mangrove";
    }

    public static CommonId getMangroveTreeResourceLocation(
            String variant
    ) {
        return new CommonId(BuildscapeCommon.MOD_ID, variant);
    }

    private static TreeConfiguration createPoplarTreeConfiguration() {
        return Services.PLATFORM.createTreeConfiguration(
                BlockStateProvider.simple(
                        ModBlocks.POPLAR_LOG.get().defaultBlockState()
                ),
                new net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer(
                        4,
                        2,
                        0
                ),
                Services.PLATFORM.createWeightedStateProvider(
                        List.of(
                                ModBlocks.RED_POPLAR_LEAVES.get().defaultBlockState(),
                                ModBlocks.ORANGE_POPLAR_LEAVES.get().defaultBlockState(),
                                ModBlocks.YELLOW_POPLAR_LEAVES.get().defaultBlockState()
                        ),
                        List.of(1, 1, 1)
                ),
                new net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer(
                        net.minecraft.util.valueproviders.ConstantInt.of(2),
                        net.minecraft.util.valueproviders.ConstantInt.of(0),
                        3
                ),
                new net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize(1, 0, 1),
                List.of(),
                true
        );
    }

    private static TreeConfiguration createPaleOakTreeConfiguration() {
        return Services.PLATFORM.createTreeConfiguration(
                BlockStateProvider.simple(ModBlocks.PALE_OAK_LOG.get().defaultBlockState()),
                new net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer(5, 2, 1),
                BlockStateProvider.simple(ModBlocks.PALE_OAK_LEAVES.get().defaultBlockState()),
                new net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer(
                        net.minecraft.util.valueproviders.ConstantInt.of(2),
                        net.minecraft.util.valueproviders.ConstantInt.of(0),
                        3
                ),
                new net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize(1, 0, 1),
                List.of(Services.PLATFORM.getCreakingHeartTreeDecorator()),
                true
        );
    }

    private static TreeConfiguration createCherryTreeConfiguration() {
        return Services.PLATFORM.createTreeConfiguration(
                BlockStateProvider.simple(ModBlocks.CHERRY_LOG.get().defaultBlockState()),
                new net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(ModBlocks.CHERRY_LEAVES.get().defaultBlockState()),
                new net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer(
                        net.minecraft.util.valueproviders.ConstantInt.of(2),
                        net.minecraft.util.valueproviders.ConstantInt.of(0),
                        3
                ),
                new net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize(1, 0, 1),
                List.of(),
                true
        );
    }
}
