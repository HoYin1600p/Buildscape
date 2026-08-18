package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class NoopRenderer extends EntityRenderer {
   public NoopRenderer(final EntityRendererProvider.Context context) {
      super(context);
   }

   public EntityRenderState createRenderState() {
      return new EntityRenderState();
   }
}
