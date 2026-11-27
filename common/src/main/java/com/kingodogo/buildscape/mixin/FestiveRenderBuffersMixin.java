package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.client.FestiveRenderTypes;
import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.SortedMap;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderBuffers.class)
public abstract class FestiveRenderBuffersMixin {

    @ModifyArg(method = "<init>", index = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;immediateWithBuffers(Ljava/util/Map;Lcom/mojang/blaze3d/vertex/BufferBuilder;)Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;"))
    private java.util.Map<RenderType, BufferBuilder> buildscape$addFestiveGlintBuffers(java.util.Map<RenderType, BufferBuilder> buffers) {
        buildscape$put(buffers, RenderType.armorEntityGlint(), FestiveRenderTypes.festiveArmorEntityGlint());
        buildscape$put(buffers, RenderType.glint(), FestiveRenderTypes.festiveGlint());
        buildscape$put(buffers, RenderType.glintTranslucent(), FestiveRenderTypes.festiveGlintTranslucent());
        buildscape$put(buffers, RenderType.entityGlint(), FestiveRenderTypes.festiveEntityGlint());
        buildscape$put(buffers, RenderType.entityGlintDirect(), FestiveRenderTypes.festiveEntityGlintDirect());
        return buffers;
    }

    private static void buildscape$put(java.util.Map<RenderType, BufferBuilder> buffers, RenderType vanilla, RenderType type) {
        if (!buffers.containsKey(type)) {
            buffers.put(type, new BufferBuilder(type.bufferSize()));
        }
    }
}
