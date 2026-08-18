package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

public class GuiEntityRenderer extends PictureInPictureRenderer {
   private final EntityRenderDispatcher entityRenderDispatcher;

   public GuiEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
      this.entityRenderDispatcher = entityRenderDispatcher;
   }

   public Class getRenderStateClass() {
      return GuiEntityRenderState.class;
   }

   protected void renderToTexture(final GuiEntityRenderState entityState, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector) {
      Minecraft.getInstance().gameRenderer.lighting().setupFor(Entry.ENTITY_IN_UI);
      Vector3fc translation = entityState.translation();
      poseStack.translate(translation.x(), translation.y(), translation.z());
      poseStack.mulPose(entityState.rotation());
      Quaternionfc overriddenCameraAngle = entityState.overrideCameraAngle();
      CameraRenderState cameraRenderState = new CameraRenderState();
      if (overriddenCameraAngle != null) {
         cameraRenderState.orientation = overriddenCameraAngle.conjugate(new Quaternionf()).rotateY((float)Math.PI);
      }

      this.entityRenderDispatcher.submit(entityState.renderState(), cameraRenderState, 0.0D, 0.0D, 0.0D, poseStack, submitNodeCollector);
   }

   protected float getTranslateY(final int height, final int guiScale) {
      return (float)height / 2.0F;
   }

   protected String getTextureLabel() {
      return "entity";
   }
}
