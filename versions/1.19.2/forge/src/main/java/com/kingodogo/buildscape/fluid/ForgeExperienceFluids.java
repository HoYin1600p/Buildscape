package com.kingodogo.buildscape.fluid;

import com.kingodogo.buildscape.BuildscapeCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgeExperienceFluids {
    public static boolean isExperience(Fluid fluid) {
        return ModFluids.isExperience(fluid);
    }

    public static boolean cannotMix(Fluid first, Fluid second) {
        return ModFluids.cannotMix(first, second);
    }

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, BuildscapeCommon.MOD_ID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, BuildscapeCommon.MOD_ID);

    public static final RegistryObject<FluidType> EXPERIENCE_FLUID_TYPE = FLUID_TYPES.register(
            "experience",
            () -> new FluidType(FluidType.Properties.create()
                    .lightLevel(15)
                    .density(1000)
                    .viscosity(1000)) {
                @Override
                public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions() {
                        private final ResourceLocation STILL = new ResourceLocation(BuildscapeCommon.MOD_ID, "fluid/experience_still");
                        private final ResourceLocation FLOW = new ResourceLocation(BuildscapeCommon.MOD_ID, "fluid/experience_flow");
                        @Override public ResourceLocation getStillTexture() { return STILL; }
                        @Override public ResourceLocation getFlowingTexture() { return FLOW; }
                    });
                }
            }
    );

    public static final RegistryObject<ForgeFlowingFluid.Source> EXPERIENCE_STILL = FLUIDS.register(
            "experience_still",
            () -> wrapped(() -> new ForgeFlowingFluid.Source(ForgeExperienceFluids.PROPERTIES))
    );

    public static final RegistryObject<ForgeFlowingFluid.Flowing> EXPERIENCE_FLOWING = FLUIDS.register(
            "experience_flowing",
            () -> wrapped(() -> new ForgeFlowingFluid.Flowing(ForgeExperienceFluids.PROPERTIES))
    );

    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            EXPERIENCE_FLUID_TYPE,
            EXPERIENCE_STILL,
            EXPERIENCE_FLOWING
    ).bucket(() -> (net.minecraft.world.item.BucketItem) com.kingodogo.buildscape.platform.Services.PLATFORM.getItem(new com.kingodogo.buildscape.util.CommonId("buildscape", "experience_bucket")))
     .block(() -> (net.minecraft.world.level.block.LiquidBlock) com.kingodogo.buildscape.platform.Services.PLATFORM.getBlock(new com.kingodogo.buildscape.util.CommonId("buildscape", "experience_liquid")));
    private static <T> T wrapped(java.util.function.Supplier<T> factory) {
        var value = new java.util.concurrent.atomic.AtomicReference<T>();
        com.kingodogo.buildscape.platform.Services.PLATFORM.wrapRegistryAction(() -> value.set(factory.get()));
        return value.get();
    }
}
