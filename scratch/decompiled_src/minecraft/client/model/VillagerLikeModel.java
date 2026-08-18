package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public interface VillagerLikeModel {
   void translateToArms(final EntityRenderState state, PoseStack outputPoseStack);
}
