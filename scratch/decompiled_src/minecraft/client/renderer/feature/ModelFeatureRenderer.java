package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.feature.submit.BatchableSubmit;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.UvMapping;
import org.jspecify.annotations.Nullable;

public class ModelFeatureRenderer extends RenderTypeFeatureRenderer {
   public static final FeatureRendererType TYPE = FeatureRendererType.create("Entity Model");
   private final PoseStack poseStack = new PoseStack();

   protected void buildGroup(final FeatureFrameContext context, final List submits) {
      for(ModelFeatureRenderer.Submit submit : submits) {
         this.prepareModel(submit);
      }

   }

   private void prepareModel(final ModelFeatureRenderer.Submit submit) {
      this.poseStack.last().set(submit.pose());
      VertexConsumer buffer = this.getVertexBuilder(submit.renderType());
      if (submit.sheetedDecalPose() != null) {
         buffer = new SheetedDecalTextureGenerator(buffer, submit.sheetedDecalPose(), 1.0F);
      } else if (submit.uvMapping() != null) {
         buffer = submit.uvMapping().wrap(buffer);
      }

      Model model = submit.model();
      model.setupAnim(submit.state());
      model.renderToBuffer(this.poseStack, buffer, submit.lightCoords(), submit.overlayCoords(), submit.tintedColor());
   }

   public static record CrumblingOverlay(int progress, PoseStack.Pose cameraPose) {
   }

   public static record Submit(RenderType renderType, PoseStack.Pose pose, Model model, Object state, int lightCoords, int overlayCoords, int tintedColor, @Nullable UvMapping uvMapping, PoseStack.@Nullable Pose sheetedDecalPose) implements BatchableSubmit, TranslucentSubmit {
      public Object batchKey() {
         return this.renderType;
      }

      public float distanceToCameraSq() {
         return TranslucentSubmit.computeDistanceToCameraSq(this.pose.pose());
      }

      public FeatureRendererType featureType() {
         return ModelFeatureRenderer.TYPE;
      }
   }
}
