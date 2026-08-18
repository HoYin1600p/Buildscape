package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

public abstract class Model {
   protected final ModelPart root;
   protected final Function renderType;
   private final List allParts;

   public Model(final ModelPart root, final Function renderType) {
      this.root = root;
      this.renderType = renderType;
      this.allParts = root.getAllParts();
   }

   public final Function renderType() {
      return this.renderType;
   }

   public final RenderType renderType(final Identifier texture) {
      return (RenderType)this.renderType().apply(texture);
   }

   public final void renderToBuffer(final PoseStack poseStack, final VertexConsumer buffer, final int lightCoords, final int overlayCoords, final int color) {
      this.root().render(poseStack, buffer, lightCoords, overlayCoords, color);
   }

   public final ModelPart root() {
      return this.root;
   }

   public final List allParts() {
      return this.allParts;
   }

   public void setupAnim(final Object state) {
      this.resetPose();
   }

   public final void resetPose() {
      for(ModelPart part : this.allParts) {
         part.resetPose();
      }

   }

   public static class Simple extends Model {
      public Simple(final ModelPart root, final Function renderType) {
         super(root, renderType);
      }

      public void setupAnim(final Unit state) {
      }
   }
}
