package com.kingodogo.buildscape.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderDispatcher.class)
public interface PillarEntityRenderDispatcherInvoker {
    @Invoker("renderFlame")
    void buildscape$renderFlame(PoseStack poseStack, MultiBufferSource bufferSource, Entity entity);
}
