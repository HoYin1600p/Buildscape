package net.minecraft.advancements.predicates.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record EntityFlagsPredicate(Optional isOnGround, Optional isOnFire, Optional isCrouching, Optional isSprinting, Optional isSwimming, Optional isFlying, Optional isBaby, Optional isInWater, Optional isFallFlying) implements EntitySubPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.BOOL.optionalFieldOf("is_on_ground").forGetter(EntityFlagsPredicate::isOnGround), Codec.BOOL.optionalFieldOf("is_on_fire").forGetter(EntityFlagsPredicate::isOnFire), Codec.BOOL.optionalFieldOf("is_sneaking").forGetter(EntityFlagsPredicate::isCrouching), Codec.BOOL.optionalFieldOf("is_sprinting").forGetter(EntityFlagsPredicate::isSprinting), Codec.BOOL.optionalFieldOf("is_swimming").forGetter(EntityFlagsPredicate::isSwimming), Codec.BOOL.optionalFieldOf("is_flying").forGetter(EntityFlagsPredicate::isFlying), Codec.BOOL.optionalFieldOf("is_baby").forGetter(EntityFlagsPredicate::isBaby), Codec.BOOL.optionalFieldOf("is_in_water").forGetter(EntityFlagsPredicate::isInWater), Codec.BOOL.optionalFieldOf("is_fall_flying").forGetter(EntityFlagsPredicate::isFallFlying)).apply(i, EntityFlagsPredicate::new));

   public boolean matches(final Entity entity) {
      if (this.isOnGround.isPresent() && entity.onGround() != this.isOnGround.get()) {
         return false;
      } else if (this.isOnFire.isPresent() && entity.isOnFire() != this.isOnFire.get()) {
         return false;
      } else if (this.isCrouching.isPresent() && entity.isCrouching() != this.isCrouching.get()) {
         return false;
      } else if (this.isSprinting.isPresent() && entity.isSprinting() != this.isSprinting.get()) {
         return false;
      } else if (this.isSwimming.isPresent() && entity.isSwimming() != this.isSwimming.get()) {
         return false;
      } else {
         if (this.isFlying.isPresent()) {
            boolean var10000;
            label68: {
               label67: {
                  if (entity instanceof LivingEntity) {
                     LivingEntity living = (LivingEntity)entity;
                     if (living.isFallFlying()) {
                        break label67;
                     }

                     if (living instanceof Player) {
                        Player player = (Player)living;
                        if (player.getAbilities().flying) {
                           break label67;
                        }
                     }
                  }

                  var10000 = false;
                  break label68;
               }

               var10000 = true;
            }

            boolean entityIsFlying = var10000;
            if (entityIsFlying != this.isFlying.get()) {
               return false;
            }
         }

         if (this.isInWater.isPresent() && entity.isInWater() != this.isInWater.get()) {
            return false;
         } else {
            if (this.isFallFlying.isPresent() && entity instanceof LivingEntity) {
               LivingEntity living = (LivingEntity)entity;
               if (living.isFallFlying() != this.isFallFlying.get()) {
                  return false;
               }
            }

            if (this.isBaby.isPresent() && entity instanceof LivingEntity) {
               LivingEntity living = (LivingEntity)entity;
               if (living.isBaby() != this.isBaby.get()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public boolean matches(final Entity entity, final ServerLevel level, final @Nullable Vec3 position) {
      return this.matches(entity);
   }

   public static class Builder {
      private Optional isOnGround = Optional.empty();
      private Optional isOnFire = Optional.empty();
      private Optional isCrouching = Optional.empty();
      private Optional isSprinting = Optional.empty();
      private Optional isSwimming = Optional.empty();
      private Optional isFlying = Optional.empty();
      private Optional isBaby = Optional.empty();
      private Optional isInWater = Optional.empty();
      private Optional isFallFlying = Optional.empty();

      public static EntityFlagsPredicate.Builder flags() {
         return new EntityFlagsPredicate.Builder();
      }

      public EntityFlagsPredicate.Builder setOnGround(final Boolean onGround) {
         this.isOnGround = Optional.of(onGround);
         return this;
      }

      public EntityFlagsPredicate.Builder setOnFire(final Boolean onFire) {
         this.isOnFire = Optional.of(onFire);
         return this;
      }

      public EntityFlagsPredicate.Builder setCrouching(final Boolean crouching) {
         this.isCrouching = Optional.of(crouching);
         return this;
      }

      public EntityFlagsPredicate.Builder setSprinting(final Boolean sprinting) {
         this.isSprinting = Optional.of(sprinting);
         return this;
      }

      public EntityFlagsPredicate.Builder setSwimming(final Boolean swimming) {
         this.isSwimming = Optional.of(swimming);
         return this;
      }

      public EntityFlagsPredicate.Builder setIsFlying(final Boolean flying) {
         this.isFlying = Optional.of(flying);
         return this;
      }

      public EntityFlagsPredicate.Builder setIsBaby(final Boolean baby) {
         this.isBaby = Optional.of(baby);
         return this;
      }

      public EntityFlagsPredicate.Builder setIsInWater(final Boolean inWater) {
         this.isInWater = Optional.of(inWater);
         return this;
      }

      public EntityFlagsPredicate.Builder setIsFallFlying(final Boolean fallFlying) {
         this.isFallFlying = Optional.of(fallFlying);
         return this;
      }

      public EntityFlagsPredicate build() {
         return new EntityFlagsPredicate(this.isOnGround, this.isOnFire, this.isCrouching, this.isSprinting, this.isSwimming, this.isFlying, this.isBaby, this.isInWater, this.isFallFlying);
      }
   }
}
