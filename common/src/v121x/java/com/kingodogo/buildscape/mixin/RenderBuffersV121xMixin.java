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
        buildscape$put(buffers, RenderType.armorEntityGlint(), FestiveRenderTypes.festiveArmorEntityGlint());
        buildscape$put(buffers, RenderType.glint(), FestiveRenderTypes.festiveGlint());
        buildscape$put(buffers, RenderType.glintTranslucent(), FestiveRenderTypes.festiveGlintTranslucent());
        buildscape$put(buffers, RenderType.entityGlint(), FestiveRenderTypes.festiveEntityGlint());
        buildscape$put(buffers, RenderType.entityGlintDirect(), FestiveRenderTypes.festiveEntityGlintDirect());
        return buffers;
    }

    private static void buildscape$put(SequencedMap<RenderType, ByteBufferBuilder> buffers, RenderType vanilla, RenderType type) {
        // Preserve vanilla's draw order, including depth-equal glint before later entity layers.
        var ordered = new java.util.LinkedHashMap<RenderType, ByteBufferBuilder>();
        for (var entry : buffers.entrySet()) {
            ordered.put(entry.getKey(), entry.getValue());
            if (entry.getKey() == vanilla) {
                ordered.put(type, new ByteBufferBuilder(type.bufferSize()));
            }
        }
        if (!ordered.containsKey(type)) {
            ordered.put(type, new ByteBufferBuilder(type.bufferSize()));
        }
        buffers.clear();
        buffers.putAll(ordered);
    }
}
