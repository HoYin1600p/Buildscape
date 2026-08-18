package net.minecraft.client.model.monster.guardian;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class GuardianParticleModel extends Model {
   public GuardianParticleModel(final ModelPart root) {
      super(root, RenderTypes::entityCutout);
   }
}
