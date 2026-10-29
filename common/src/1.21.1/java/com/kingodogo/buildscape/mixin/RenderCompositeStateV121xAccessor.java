package com.kingodogo.buildscape.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// CompositeState is package-private; a named mixin target is remapped by the loader.
@Mixin(targets = "net.minecraft.client.renderer.RenderType$CompositeState")
public interface RenderCompositeStateV121xAccessor {
    @Accessor("states") ImmutableList<RenderStateShard> buildscape$getStates();
}
