package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProviderTypes {

    public static final DeferredRegister<
            BlockStateProviderType<?>
            > BLOCK_STATE_PROVIDER_TYPES = DeferredRegister.create(
            Registry.BLOCK_STATE_PROVIDER_TYPE_REGISTRY,
            BuildScape.MODID
    );

    public static final RegistryObject<
            BlockStateProviderType<RandomStateProvider>
            > RANDOM_STATE = BLOCK_STATE_PROVIDER_TYPES.register("random_state", () ->
            new BlockStateProviderType<>(RandomStateProvider.CODEC)
    );
}
