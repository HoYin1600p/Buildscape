package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.core.MappedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(MappedRegistry.class)
public interface RegistryStateAccessor {
    @Accessor("frozen") boolean buildscape$isFrozen();
    @Accessor("frozen") void buildscape$setFrozen(boolean value);
    @Accessor("unregisteredIntrusiveHolders") Map<?, ?> buildscape$getHolders();
    @Accessor("unregisteredIntrusiveHolders") void buildscape$setHolders(Map<?, ?> holders);
}
