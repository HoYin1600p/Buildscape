package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class LivingEntityEmissiveLayer extends RenderLayer {
   private final Function textureProvider;
   private final LivingEntityEmissiveLayer.AlphaFunction alphaFunction;
   private final EntityModel model;
   private final Function bufferProvider;
   private final boolean alwaysVisible;

   public LivingEntityEmissiveLayer(final RenderLayerParent renderer, final Function textureProvider, final LivingEntityEmissiveLayer.AlphaFunction alphaFunction, final EntityModel model, final Function bufferProvider, final boolean alwaysVisible) {
      super(renderer);
      this.textureProvider = textureProvider;
      this.alphaFunction = alphaFunction;
      this.model = model;
      this.bufferProvider = bufferProvider;
      this.alwaysVisible = alwaysVisible;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final LivingEntityRenderState state, final float yRot, final float xRot) {
      if (!state.isInvisible || this.alwaysVisible) {
         float alpha = this.alphaFunction.apply(state, state.ageInTicks);
         if (!(alpha <= 1.0E-5F)) {
            int color = ARGB.white(alpha);
            RenderType renderType = (RenderType)this.bufferProvider.apply((Identifier)this.textureProvider.apply(state));
            submitNodeCollector.order(1).submitModel(this.model, state, poseStack, renderType, lightCoords, LivingEntityRenderer.getOverlayCoords(state, 0.0F), color, (UvMapping)null, state.outlineColor);
         }
      }
   }

   public interface AlphaFunction {
      float apply(final LivingEntityRenderState state, final float ageInTicks);
   }
}
