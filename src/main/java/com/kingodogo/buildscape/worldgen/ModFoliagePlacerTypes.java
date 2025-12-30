package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModFoliagePlacerTypes {

    public static final DeferredRegister<
            FoliagePlacerType<?>
            > FOLIAGE_PLACER_TYPES = DeferredRegister.create(
            Registry.FOLIAGE_PLACER_TYPE_REGISTRY,
            BuildScape.MODID
    );

    public static final RegistryObject<
            FoliagePlacerType<MangroveRandomSpreadFoliagePlacer>
            > MANGROVE_RANDOM_SPREAD = FOLIAGE_PLACER_TYPES.register(
            "mangrove_random_spread",
            () -> new FoliagePlacerType<>(MangroveRandomSpreadFoliagePlacer.CODEC)
    );
}
