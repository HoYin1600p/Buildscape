package net.minecraft.client.renderer.fog.environment;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.jspecify.annotations.Nullable;

public abstract class MobEffectFogEnvironment extends FogEnvironment {
   public abstract Holder getMobEffect();

   public boolean providesColor() {
      return false;
   }

   public boolean modifiesDarkness() {
      return true;
   }

   public boolean isApplicable(final @Nullable FogType fogType, final Entity entity) {
      if (entity instanceof LivingEntity livingEntity) {
         if (livingEntity.hasEffect(this.getMobEffect())) {
            return true;
         }
      }

      return false;
   }
}
