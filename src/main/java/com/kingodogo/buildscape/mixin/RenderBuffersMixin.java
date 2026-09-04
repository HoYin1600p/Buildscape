package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.FestiveRenderTypes;
import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.SortedMap;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBuffers.class)
public abstract class RenderBuffersMixin {

    @Shadow @Final
    private SortedMap<RenderType, BufferBuilder> fixedBuffers;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void buildscape$addFestiveGlintBuffers(CallbackInfo ci) {
        buildscape$addBuffer(FestiveRenderTypes.festiveArmorGlint());
        buildscape$addBuffer(FestiveRenderTypes.festiveArmorEntityGlint());
        buildscape$addBuffer(FestiveRenderTypes.festiveGlint());
        buildscape$addBuffer(FestiveRenderTypes.festiveGlintDirect());
        buildscape$addBuffer(FestiveRenderTypes.festiveGlintTranslucent());
        buildscape$addBuffer(FestiveRenderTypes.festiveEntityGlint());
        buildscape$addBuffer(FestiveRenderTypes.festiveEntityGlintDirect());
    }

    @Unique
    private void buildscape$addBuffer(RenderType type) {
        fixedBuffers.computeIfAbsent(type, key -> new BufferBuilder(key.bufferSize()));
    }
}
