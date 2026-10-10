package com.kingodogo.buildscape.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.lang.reflect.Constructor;

public class WorldGenTypeHelper {

    public static <T> Codec<T> unitCodec(T instance) {
        return Codec.of(com.mojang.serialization.Encoder.empty(), com.mojang.serialization.Decoder.unit(instance)).codec();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <P extends TreeDecorator> TreeDecoratorType<P> createTreeDecoratorType(Codec<P> codec) {
        try {
            Constructor<TreeDecoratorType> ctor = TreeDecoratorType.class.getDeclaredConstructor(Codec.class);
            ctor.setAccessible(true);
            return (TreeDecoratorType<P>) ctor.newInstance(codec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate TreeDecoratorType", e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <P extends FoliagePlacer> FoliagePlacerType<P> createFoliagePlacerType(Codec<P> codec) {
        try {
            Constructor<FoliagePlacerType> ctor = FoliagePlacerType.class.getDeclaredConstructor(Codec.class);
            ctor.setAccessible(true);
            return (FoliagePlacerType<P>) ctor.newInstance(codec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate FoliagePlacerType", e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <P extends TrunkPlacer> TrunkPlacerType<P> createTrunkPlacerType(Codec<P> codec) {
        try {
            Constructor<TrunkPlacerType> ctor = TrunkPlacerType.class.getDeclaredConstructor(Codec.class);
            ctor.setAccessible(true);
            return (TrunkPlacerType<P>) ctor.newInstance(codec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate TrunkPlacerType", e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <P extends BlockStateProvider> BlockStateProviderType<P> createBlockStateProviderType(Codec<P> codec) {
        try {
            Constructor<BlockStateProviderType> ctor = BlockStateProviderType.class.getDeclaredConstructor(Codec.class);
            ctor.setAccessible(true);
            return (BlockStateProviderType<P>) ctor.newInstance(codec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate BlockStateProviderType", e);
        }
    }
}
