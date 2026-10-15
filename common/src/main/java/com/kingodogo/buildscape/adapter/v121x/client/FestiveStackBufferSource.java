package com.kingodogo.buildscape.adapter.v121x.client;

import com.kingodogo.buildscape.util.FestiveGlintHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;

/** Foil selection belongs to one render invocation, never to the previous rendered stack. */
public final class FestiveStackBufferSource implements MultiBufferSource {
    private final MultiBufferSource delegate;
    private final ItemStack stack;

    private FestiveStackBufferSource(MultiBufferSource delegate, ItemStack stack) {
        this.delegate = delegate;
        this.stack = stack;
    }

    public static MultiBufferSource forStack(MultiBufferSource source, ItemStack stack) {
        // A nested item render or NeoForge's armour overload must use its own stack.
        while (source instanceof FestiveStackBufferSource scoped) {
            source = scoped.delegate;
        }
        return new FestiveStackBufferSource(source, stack);
    }

    public static boolean isFestive(MultiBufferSource source) {
        return source instanceof FestiveStackBufferSource scoped
                && FestiveGlintHelper.hasFestiveGlint(scoped.stack);
    }

    @Override
    public VertexConsumer getBuffer(RenderType type) {
        if (isFestive(this)) {
            if (type == RenderType.armorEntityGlint()) {
                type = FestiveRenderTypes.festiveArmorEntityGlint();
            } else if (type == RenderType.glint()) {
                type = FestiveRenderTypes.festiveGlint();
            } else if (type == RenderType.glintTranslucent()) {
                type = FestiveRenderTypes.festiveGlintTranslucent();
            } else if (type == RenderType.entityGlint()) {
                type = FestiveRenderTypes.festiveEntityGlint();
            } else if (type == RenderType.entityGlintDirect()) {
                type = FestiveRenderTypes.festiveEntityGlintDirect();
            }
        }
        return delegate.getBuffer(type);
    }
}
