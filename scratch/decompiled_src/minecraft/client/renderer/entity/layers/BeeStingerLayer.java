package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.animal.bee.BeeStingerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

public class BeeStingerLayer extends StuckInBodyLayer {
   private static final Identifier BEE_STINGER_LOCATION = Identifier.withDefaultNamespace("textures/entity/bee/bee_stinger.png");

   public BeeStingerLayer(final LivingEntityRenderer renderer, final EntityRendererProvider.Context context) {
      super(renderer, new BeeStingerModel(context.bakeLayer(ModelLayers.BEE_STINGER)), Unit.INSTANCE, BEE_STINGER_LOCATION, StuckInBodyLayer.PlacementStyle.ON_SURFACE);
   }

   protected int numStuck(final AvatarRenderState state) {
      return state.stingerCount;
   }
}
