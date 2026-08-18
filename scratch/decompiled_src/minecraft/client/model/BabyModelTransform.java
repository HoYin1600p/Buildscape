package net.minecraft.client.model;

import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;

public record BabyModelTransform(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, float babyHeadScale, float babyBodyScale, float bodyYOffset, Set headParts) implements MeshTransformer {
   public BabyModelTransform(final Set headParts) {
      this(false, 5.0F, 2.0F, headParts);
   }

   public BabyModelTransform(final boolean scaleHead, final float babyYHeadOffset, final float babyZHeadOffset, final Set headParts) {
      this(scaleHead, babyYHeadOffset, babyZHeadOffset, 2.0F, 2.0F, 24.0F, headParts);
   }

   public MeshDefinition apply(final MeshDefinition mesh) {
      float headScale = this.scaleHead ? 1.5F / this.babyHeadScale : 1.0F;
      float bodyScale = 1.0F / this.babyBodyScale;
      UnaryOperator headTransform = (p) -> p.translated(0.0F, this.babyYHeadOffset, this.babyZHeadOffset).scaled(headScale);
      UnaryOperator bodyTransform = (p) -> p.translated(0.0F, this.bodyYOffset, 0.0F).scaled(bodyScale);
      MeshDefinition babyMesh = new MeshDefinition();

      for(Map.Entry entry : mesh.getRoot().getChildren()) {
         String name = (String)entry.getKey();
         PartDefinition part = (PartDefinition)entry.getValue();
         babyMesh.getRoot().addOrReplaceChild(name, part.transformed(this.headParts.contains(name) ? headTransform : bodyTransform));
      }

      return babyMesh;
   }
}
