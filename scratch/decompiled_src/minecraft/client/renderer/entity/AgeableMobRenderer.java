package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

/** @deprecated */
@Deprecated
public abstract class AgeableMobRenderer extends MobRenderer {
   private final EntityModel adultModel;
   private final EntityModel babyModel;

   public AgeableMobRenderer(final EntityRendererProvider.Context context, final EntityModel adultModel, final EntityModel babyModel, final float shadow) {
      super(context, adultModel, shadow);
      this.adultModel = adultModel;
      this.babyModel = babyModel;
   }

   public void submit(final LivingEntityRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      this.model = state.isBaby ? this.babyModel : this.adultModel;
      super.submit(state, poseStack, submitNodeCollector, camera);
   }
}
