package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class SpiderEyesLayer extends EyesLayer {
   private static final RenderType SPIDER_EYES = RenderTypes.eyes(Identifier.withDefaultNamespace("textures/entity/spider/spider_eyes.png"));

   public SpiderEyesLayer(final RenderLayerParent renderer) {
      super(renderer);
   }

   public RenderType renderType() {
      return SPIDER_EYES;
   }
}
