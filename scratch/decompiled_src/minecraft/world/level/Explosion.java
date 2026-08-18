package net.minecraft.world.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface Explosion {
   static DamageSource getDefaultDamageSource(final Level level, final @Nullable Entity source) {
      return level.damageSources().explosion(source, getIndirectSourceEntity(source));
   }

   static @Nullable LivingEntity getIndirectSourceEntity(final @Nullable Entity source) {
      Entity var1 = source;
      byte var2 = 0;

      while(true) {
         switch (var1.typeSwitch<invokedynamic>(var1, var2)) {
            case -1:
            default:
               return null;
            case 0:
               PrimedTnt primedTnt = (PrimedTnt)var1;
               return primedTnt.getOwner();
            case 1:
               return (LivingEntity)var1;
            case 2:
               Projectile projectile = (Projectile)var1;
               Entity var7 = projectile.getOwner();
               if (var7 instanceof LivingEntity) {
                  return (LivingEntity)var7;
               }

               var2 = 3;
         }
      }
   }

   ServerLevel level();

   Explosion.BlockInteraction getBlockInteraction();

   @Nullable LivingEntity getIndirectSourceEntity();

   @Nullable Entity getDirectSourceEntity();

   float radius();

   Vec3 center();

   boolean canTriggerBlocks();

   boolean shouldAffectBlocklikeEntities();

   public static enum BlockInteraction {
      KEEP(false),
      DESTROY(true),
      DESTROY_WITH_DECAY(true),
      TRIGGER_BLOCK(false);

      private final boolean shouldAffectBlocklikeEntities;

      private BlockInteraction(final boolean shouldAffectBlocklikeEntities) {
         this.shouldAffectBlocklikeEntities = shouldAffectBlocklikeEntities;
      }

      public boolean shouldAffectBlocklikeEntities() {
         return this.shouldAffectBlocklikeEntities;
      }

      // $FF: synthetic method
      private static Explosion.BlockInteraction[] $values() {
         return new Explosion.BlockInteraction[]{KEEP, DESTROY, DESTROY_WITH_DECAY, TRIGGER_BLOCK};
      }
   }
}
