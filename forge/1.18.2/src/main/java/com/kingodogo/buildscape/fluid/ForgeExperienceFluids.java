package com.kingodogo.buildscape.fluid;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgeExperienceFluids {
    public static boolean isExperience(Fluid fluid) {
        return fluid == EXPERIENCE_STILL.get() || fluid == EXPERIENCE_FLOWING.get();
    }

    public static boolean cannotMix(Fluid first, Fluid second) {
        return (isExperience(first) && net.minecraft.world.level.material.Fluids.LAVA.isSame(second))
                || (isExperience(second) && net.minecraft.world.level.material.Fluids.LAVA.isSame(first));
    }

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, BuildscapeCommon.MOD_ID);

    public static final RegistryObject<ForgeFlowingFluid.Source> EXPERIENCE_STILL = FLUIDS.register(
            "experience_still",
            () -> wrapped(() -> new ForgeFlowingFluid.Source(ForgeExperienceFluids.PROPERTIES))
    );

    public static final RegistryObject<ForgeFlowingFluid.Flowing> EXPERIENCE_FLOWING = FLUIDS.register(
            "experience_flowing",
            () -> wrapped(() -> new ForgeFlowingFluid.Flowing(ForgeExperienceFluids.PROPERTIES))
    );

    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> EXPERIENCE_STILL.get(),
            () -> EXPERIENCE_FLOWING.get(),
            FluidAttributes.builder(
                    new ResourceLocation(BuildscapeCommon.MOD_ID, "fluid/experience_still"),
                    new ResourceLocation(BuildscapeCommon.MOD_ID, "fluid/experience_flow")
            ).luminosity(15).density(1000).viscosity(1000)
    ).bucket(() -> (net.minecraft.world.item.BucketItem) com.kingodogo.buildscape.platform.Services.PLATFORM.getItem(new com.kingodogo.buildscape.util.CommonId("buildscape", "experience_bucket")))
     .block(() -> (net.minecraft.world.level.block.LiquidBlock) com.kingodogo.buildscape.platform.Services.PLATFORM.getBlock(new com.kingodogo.buildscape.util.CommonId("buildscape", "experience_liquid")));
    private static <T> T wrapped(java.util.function.Supplier<T> factory) {
        var value = new java.util.concurrent.atomic.AtomicReference<T>();
        com.kingodogo.buildscape.platform.Services.PLATFORM.wrapRegistryAction(() -> value.set(factory.get()));
        return value.get();
    }
}
