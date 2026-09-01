package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveRenderTypes;
import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBuffers.class)
public abstract class RenderBuffersMixin {

    @Shadow
    private static void put(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map, RenderType renderType) {
        throw new AssertionError();
    }

    @Inject(method = "lambda$new$1", at = @At("TAIL"))
    private void buildscape$addFestiveGlintBuffers(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map, CallbackInfo ci) {
        put(map, FestiveRenderTypes.festiveArmorGlint());
        put(map, FestiveRenderTypes.festiveArmorEntityGlint());
        put(map, FestiveRenderTypes.festiveGlint());
        put(map, FestiveRenderTypes.festiveGlintDirect());
        put(map, FestiveRenderTypes.festiveGlintTranslucent());
        put(map, FestiveRenderTypes.festiveEntityGlint());
        put(map, FestiveRenderTypes.festiveEntityGlintDirect());
    }
}
