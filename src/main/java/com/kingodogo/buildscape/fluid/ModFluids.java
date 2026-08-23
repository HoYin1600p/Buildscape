package com.kingodogo.buildscape.fluid;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, BuildScape.MODID);

    public static final RegistryObject<ForgeFlowingFluid.Source> EXPERIENCE_STILL = FLUIDS.register(
            "experience_still",
            () -> new ForgeFlowingFluid.Source(ModFluids.PROPERTIES)
    );

    public static final RegistryObject<ForgeFlowingFluid.Flowing> EXPERIENCE_FLOWING = FLUIDS.register(
            "experience_flowing",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.PROPERTIES)
    );

    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> EXPERIENCE_STILL.get(),
            () -> EXPERIENCE_FLOWING.get(),
            FluidAttributes.builder(
                    new ResourceLocation(BuildScape.MODID, "fluid/experience_still"),
                    new ResourceLocation(BuildScape.MODID, "fluid/experience_flow")
            ).luminosity(15).density(1000).viscosity(1000)
    ).bucket(() -> (net.minecraft.world.item.BucketItem) ModItems.EXPERIENCE_BUCKET.get())
     .block(() -> (net.minecraft.world.level.block.LiquidBlock) ModBlocks.EXPERIENCE_BLOCK.get());
}
