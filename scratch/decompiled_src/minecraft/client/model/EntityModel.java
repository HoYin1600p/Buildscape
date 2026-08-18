package net.minecraft.client.model;

import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public abstract class EntityModel extends Model {
   public static final float MODEL_Y_OFFSET = -1.501F;

   protected EntityModel(final ModelPart root) {
      this(root, RenderTypes::entityCutout);
   }

   protected EntityModel(final ModelPart root, final Function renderType) {
      super(root, renderType);
   }
}
