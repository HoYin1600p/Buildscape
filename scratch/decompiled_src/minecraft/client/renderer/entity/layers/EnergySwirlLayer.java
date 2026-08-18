package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.resources.Identifier;

public abstract class EnergySwirlLayer extends RenderLayer {
   public EnergySwirlLayer(final RenderLayerParent renderer) {
      super(renderer);
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final EntityRenderState state, final float yRot, final float xRot) {
      if (this.isPowered(state)) {
         float t = state.ageInTicks;
         EntityModel model = (M)this.model();
         submitNodeCollector.order(1).submitModel(model, state, poseStack, RenderTypes.energySwirl(this.getTextureLocation(), this.xOffset(t) % 1.0F, t * 0.01F % 1.0F), lightCoords, OverlayTexture.NO_OVERLAY, -8355712, (UvMapping)null, state.outlineColor);
      }
   }

   protected abstract boolean isPowered(EntityRenderState state);

   protected abstract float xOffset(final float t);

   protected abstract Identifier getTextureLocation();

   protected abstract EntityModel model();
}
