package com.kingodogo.buildscape.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/** Lets 1.21.1 drop the intrusive holder of a block that is built only as a property reference and never registered. */
@Mixin(MappedRegistry.class)
public interface MappedRegistryV121xAccessor<T> {
    @Accessor("unregisteredIntrusiveHolders")
    Map<T, Holder.Reference<T>> buildscape$unregisteredIntrusiveHolders();
}
