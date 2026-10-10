package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTrunkPlacerTypes {

    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES =
            DeferredRegister.create(
                    Registry.TRUNK_PLACER_TYPE_REGISTRY,
                    BuildScape.MODID
            );

    public static final RegistryObject<
            TrunkPlacerType<MangroveUpwardsBranchingTrunkPlacer>
            > MANGROVE_UPWARDS_BRANCHING = TRUNK_PLACER_TYPES.register(
            "mangrove_upwards_branching",
            () -> new TrunkPlacerType<>(MangroveUpwardsBranchingTrunkPlacer.CODEC)
    );
}
