package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WebBlock extends Block {
   public WebBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   protected void entityInside(final BlockState state, final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier, final boolean isPrecise) {
      Vec3 speedMultiplier = new Vec3(0.25D, (double)0.05F, 0.25D);
      if (entity instanceof LivingEntity livingEntity) {
         if (livingEntity.hasEffect(MobEffects.WEAVING)) {
            speedMultiplier = new Vec3(0.5D, 0.25D, 0.5D);
         }
      }

      entity.makeStuckInBlock(state, speedMultiplier);
   }
}
