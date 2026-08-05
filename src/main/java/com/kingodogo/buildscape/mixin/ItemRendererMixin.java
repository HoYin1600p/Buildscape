package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.TransparentMultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @ModifyVariable(
        method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private MultiBufferSource wrapBufferSource(
        MultiBufferSource source,
        ItemStack stack,
        ItemTransforms.TransformType transformType
    ) {
        if (!stack.isEmpty() && stack.hasTag() && stack.getTag().getBoolean("ghost")) {
            return new TransparentMultiBufferSource(source, 0.4f);
        }
        return source;
    }
}
