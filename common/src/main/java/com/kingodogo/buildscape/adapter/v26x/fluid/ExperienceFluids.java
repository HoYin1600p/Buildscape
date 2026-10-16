package com.kingodogo.buildscape.adapter.v26x.fluid;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.Objects;
import java.util.function.Supplier;

/** Creates the pair once, before blocks/items, independently of registry event ordering. */
public final class ExperienceFluids {
    public record Pair(FlowingFluid still, FlowingFluid flowing) {
        public Pair {
            Objects.requireNonNull(still);
            Objects.requireNonNull(flowing);
        }
    }

    private static Supplier<Pair> factory = () -> new Pair(new ExperienceFluid.Source(), new ExperienceFluid.Flowing());
    private static Pair pair;

    private ExperienceFluids() {}

    public static synchronized void setFactory(Supplier<Pair> loaderFactory) {
        if (pair != null) throw new IllegalStateException("Experience fluids have already been created");
        factory = Objects.requireNonNull(loaderFactory);
    }

    private static synchronized Pair pair() {
        if (pair == null) Services.PLATFORM.wrapRegistryAction(() -> pair = Objects.requireNonNull(factory.get()));
        return pair;
    }

    public static FlowingFluid still() { return pair().still(); }
    public static FlowingFluid flowing() { return pair().flowing(); }

    public static void register() {
        Services.PLATFORM.wrapRegistryAction(() -> {
            Services.PLATFORM.register(BuiltInRegistries.FLUID, new CommonId("buildscape", "experience_still"), still());
            Services.PLATFORM.register(BuiltInRegistries.FLUID, new CommonId("buildscape", "experience_flowing"), flowing());
        });
    }
}
