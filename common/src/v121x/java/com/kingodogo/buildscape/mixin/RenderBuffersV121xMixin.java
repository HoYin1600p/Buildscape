package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v121x.client.FestiveRenderTypes;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import java.util.SequencedMap;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderBuffers.class)
public abstract class RenderBuffersV121xMixin {

    @ModifyArg(method = "<init>", index = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;immediateWithBuffers(Ljava/util/SequencedMap;Lcom/mojang/blaze3d/vertex/ByteBufferBuilder;)Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;"))
    private SequencedMap<RenderType, ByteBufferBuilder> buildscape$addFestiveGlintBuffers(SequencedMap<RenderType, ByteBufferBuilder> buffers) {
        buildscape$put(buffers, FestiveRenderTypes.festiveArmorEntityGlint());
        buildscape$put(buffers, FestiveRenderTypes.festiveGlint());
        buildscape$put(buffers, FestiveRenderTypes.festiveGlintTranslucent());
        buildscape$put(buffers, FestiveRenderTypes.festiveEntityGlint());
        buildscape$put(buffers, FestiveRenderTypes.festiveEntityGlintDirect());
        return buffers;
    }

    private static void buildscape$put(SequencedMap<RenderType, ByteBufferBuilder> buffers, RenderType type) {
        buffers.put(type, new ByteBufferBuilder(type.bufferSize()));
    }
}
