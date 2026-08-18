package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.item.CrossbowItem;

public abstract class IllagerRenderer extends MobRenderer {
   protected IllagerRenderer(final EntityRendererProvider.Context context, final IllagerModel model, final float shadow) {
      super(context, model, shadow);
      this.addLayer(new CustomHeadLayer(this, context.getModelSet(), context.getPlayerSkinRenderCache()));
   }

   public void extractRenderState(final AbstractIllager entity, final IllagerRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, this.itemModelResolver, partialTicks);
      state.isRiding = entity.isPassenger();
      state.mainArm = entity.getMainArm();
      state.armPose = entity.getArmPose();
      state.maxCrossbowChargeDuration = state.armPose == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE ? CrossbowItem.getChargeDuration(entity.getUseItem(), entity) : 0;
      state.ticksUsingItem = entity.getTicksUsingItem(partialTicks);
      state.isAggressive = entity.isAggressive();
   }
}
