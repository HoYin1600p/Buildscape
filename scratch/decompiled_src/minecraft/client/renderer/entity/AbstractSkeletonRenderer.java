package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.item.Items;

public abstract class AbstractSkeletonRenderer extends HumanoidMobRenderer {
   public AbstractSkeletonRenderer(final EntityRendererProvider.Context context, final ModelLayerLocation body, final ArmorModelSet armorSet) {
      this(context, armorSet, new SkeletonModel(context.bakeLayer(body)));
   }

   public AbstractSkeletonRenderer(final EntityRendererProvider.Context context, final ArmorModelSet armorSet, final SkeletonModel bodyModel) {
      super(context, bodyModel, 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, ArmorModelSet.bake(armorSet, context.getModelSet(), SkeletonModel::new), context.getEquipmentRenderer()));
   }

   public void extractRenderState(final AbstractSkeleton entity, final SkeletonRenderState state, final float partialTicks) {
      super.extractRenderState((Mob)entity, (HumanoidRenderState)state, partialTicks);
      state.isAggressive = entity.isAggressive();
      state.isShaking = entity.isShaking();
      state.isHoldingBow = entity.getMainHandItem().is(Items.BOW);
   }

   protected boolean isShaking(final SkeletonRenderState state) {
      return state.isShaking;
   }

   protected HumanoidModel.ArmPose getArmPose(final AbstractSkeleton mob, final HumanoidArm arm) {
      return mob.getMainArm() == arm && mob.isAggressive() && mob.getMainHandItem().is(Items.BOW) ? HumanoidModel.ArmPose.BOW_AND_ARROW : super.getArmPose(mob, arm);
   }
}
