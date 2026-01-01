package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPlacementModifiers {

    public static final DeferredRegister<
            PlacementModifierType<?>
            > PLACEMENT_MODIFIERS = DeferredRegister.create(
            Registry.PLACEMENT_MODIFIER_REGISTRY,
            BuildScape.MODID
    );

    public static final RegistryObject<
            PlacementModifierType<MossBlockCeilingPlacement>
            > MOSS_BLOCK_CEILING_PLACEMENT = PLACEMENT_MODIFIERS.register(
            "moss_block_ceiling_placement",
            () -> () -> MossBlockCeilingPlacement.CODEC
    );
}
