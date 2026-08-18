package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.resources.Identifier;

public abstract class RenderLayer {
   private final RenderLayerParent renderer;

   public RenderLayer(final RenderLayerParent renderer) {
      this.renderer = renderer;
   }

   protected static void coloredCutoutModelCopyLayerRender(final Model model, final Identifier texture, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final LivingEntityRenderState state, final int color, final int order) {
      if (!state.isInvisible) {
         renderColoredCutoutModel(model, texture, poseStack, submitNodeCollector, lightCoords, state, color, order);
      }

   }

   protected static void renderColoredCutoutModel(final Model model, final Identifier texture, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final LivingEntityRenderState state, final int color, final int order) {
      submitNodeCollector.order(order).submitModel(model, state, poseStack, RenderTypes.entityCutout(texture), lightCoords, LivingEntityRenderer.getOverlayCoords(state, 0.0F), color, (UvMapping)null, state.outlineColor);
   }

   public EntityModel getParentModel() {
      return this.renderer.getModel();
   }

   public abstract void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, EntityRenderState state, float yRot, float xRot);
}
