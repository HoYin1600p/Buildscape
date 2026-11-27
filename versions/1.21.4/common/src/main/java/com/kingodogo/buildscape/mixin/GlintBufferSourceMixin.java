package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.client.FestiveRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiBufferSource.BufferSource.class)
public abstract class GlintBufferSourceMixin {
    // Entity previews and render stages flush named vanilla buffers before drawing the next
    // stack. Flush the festive counterpart too, without consulting the current stack context.
    @Inject(method = "endBatch(Lnet/minecraft/client/renderer/RenderType;)V", at = @At("RETURN"))
    private void buildscape$flushFestiveGlint(RenderType type, CallbackInfo ci) {
        RenderType festive;
        if (type == RenderType.armorEntityGlint()) {
            festive = FestiveRenderTypes.festiveArmorEntityGlint();
        } else if (type == RenderType.glint()) {
            festive = FestiveRenderTypes.festiveGlint();
        } else if (type == RenderType.glintTranslucent()) {
            festive = FestiveRenderTypes.festiveGlintTranslucent();
        } else if (type == RenderType.entityGlint()) {
            festive = FestiveRenderTypes.festiveEntityGlint();
        } else {
            return;
        }
        ((MultiBufferSource.BufferSource) (Object) this).endBatch(festive);
    }
}
