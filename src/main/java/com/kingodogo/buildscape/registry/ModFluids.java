package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.fluid.ExperienceFluidMatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public class ModFluids {
    /** The still experience fluid, or null while its registry object is not present. */
    @Nullable
    public static Fluid experienceStillOrNull() {
        return EXPERIENCE_STILL.isPresent() ? EXPERIENCE_STILL.get() : null;
    }

    @Nullable
    private static Fluid experienceFlowingOrNull() {
        return EXPERIENCE_FLOWING.isPresent() ? EXPERIENCE_FLOWING.get() : null;
    }

    /** Never throws: false when the registry objects are not present. */
    public static boolean isExperience(Fluid fluid) {
        return ExperienceFluidMatch.matches(fluid, experienceStillOrNull(), experienceFlowingOrNull());
    }

    /** Exactly the still experience fluid; false when it is not present. */
    public static boolean isExperienceStill(Fluid fluid) {
        return ExperienceFluidMatch.matches(fluid, experienceStillOrNull());
    }

    /** Same fluid as still experience (Fluid#isSame); false when it is not present. */
    public static boolean isExperienceSame(Fluid fluid) {
        Fluid still = experienceStillOrNull();
        return fluid != null && still != null && fluid.isSame(still);
    }

    public static boolean cannotMix(Fluid first, Fluid second) {
        return (isExperience(first) && Fluids.LAVA.isSame(second))
                || (isExperience(second) && Fluids.LAVA.isSame(first));
    }

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS,
            BuildScape.MODID);

    public static final RegistryObject<ForgeFlowingFluid.Source> EXPERIENCE_STILL = FLUIDS.register(
            "experience_still",
            () -> new ForgeFlowingFluid.Source(ModFluids.PROPERTIES));

    public static final RegistryObject<ForgeFlowingFluid.Flowing> EXPERIENCE_FLOWING = FLUIDS.register(
            "experience_flowing",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.PROPERTIES));

    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> EXPERIENCE_STILL.get(),
            () -> EXPERIENCE_FLOWING.get(),
            FluidAttributes.builder(
                    new ResourceLocation(BuildScape.MODID, "fluid/experience_still"),
                    new ResourceLocation(BuildScape.MODID, "fluid/experience_flow")).luminosity(15).density(1000)
                    .viscosity(1000))
            .bucket(() -> (BucketItem) ModItems.EXPERIENCE_BUCKET.get())
            .block(() -> (LiquidBlock) ModBlocks.EXPERIENCE_BLOCK.get());
}
