package net.minecraft.client.renderer.entity;

import java.util.function.UnaryOperator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.object.boat.RaftModel;

public class RaftRenderer extends AbstractBoatRenderer {
   private final EntityModel model;

   public RaftRenderer(final EntityRendererProvider.Context context, final ModelLayerLocation modelId) {
      super(context, modelId.model().withPath((UnaryOperator)((p) -> "textures/entity/" + p + ".png")));
      this.model = new RaftModel(context.bakeLayer(modelId));
   }

   protected EntityModel model() {
      return this.model;
   }
}
