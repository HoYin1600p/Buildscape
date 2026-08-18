package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public abstract class MobRenderer extends LivingEntityRenderer {
   public MobRenderer(final EntityRendererProvider.Context context, final EntityModel model, final float shadow) {
      super(context, model, shadow);
   }

   protected boolean shouldShowName(final Mob entity, final double distanceToCameraSq) {
      return super.shouldShowName((LivingEntity)entity, distanceToCameraSq) && (entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
   }

   protected float getShadowRadius(final LivingEntityRenderState state) {
      return super.getShadowRadius(state) * state.ageScale;
   }

   protected static boolean checkMagicName(final Entity entity, final String magicName) {
      Component customName = entity.getCustomName();
      return customName != null && magicName.equals(customName.getString());
   }
}
