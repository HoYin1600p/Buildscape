package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;

public class MinecartRenderer extends AbstractMinecartRenderer {
   public MinecartRenderer(final EntityRendererProvider.Context context, final ModelLayerLocation model) {
      super(context, model);
   }

   public MinecartRenderState createRenderState() {
      return new MinecartRenderState();
   }
}
