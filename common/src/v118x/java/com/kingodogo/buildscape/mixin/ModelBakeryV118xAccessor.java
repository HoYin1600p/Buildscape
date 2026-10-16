package com.kingodogo.buildscape.mixin;

import java.util.Map;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelBakery.class)
public interface ModelBakeryV118xAccessor {
    @Accessor("topLevelModels") Map<ResourceLocation, ?> buildscape$getTopLevelModels();
    @Accessor("bakedCache") Map<?, ?> buildscape$getBakedCache();
    @Mutable
    @Accessor("bakedCache") void buildscape$setBakedCache(Map<?, ?> cache);
}
