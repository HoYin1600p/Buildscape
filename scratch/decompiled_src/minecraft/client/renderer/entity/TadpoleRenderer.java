package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.animal.frog.TadpoleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class TadpoleRenderer extends MobRenderer {
   private static final Identifier TADPOLE_TEXTURE = Identifier.withDefaultNamespace("textures/entity/tadpole/tadpole.png");

   public TadpoleRenderer(final EntityRendererProvider.Context context) {
      super(context, new TadpoleModel(context.bakeLayer(ModelLayers.TADPOLE)), 0.14F);
   }

   public Identifier getTextureLocation(final LivingEntityRenderState state) {
      return TADPOLE_TEXTURE;
   }

   public LivingEntityRenderState createRenderState() {
      return new LivingEntityRenderState();
   }
}
