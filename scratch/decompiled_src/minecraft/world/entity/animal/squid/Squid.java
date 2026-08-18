package net.minecraft.world.entity.animal.squid;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Squid extends AgeableWaterCreature {
   public float xBodyRot;
   public float xBodyRotO;
   public float zBodyRot;
   public float zBodyRotO;
   public float tentacleMovement;
   public float oldTentacleMovement;
   public float tentacleAngle;
   public float oldTentacleAngle;
   private float speed;
   private float tentacleSpeed;
   private float rotateSpeed;
   private Vec3 movementVector = Vec3.ZERO;
   private static final EntityDimensions BABY_DIMENSIONS = EntityDimensions.scalable(0.5F, 0.5F).withEyeHeight(0.37F);

   public Squid(final EntityType type, final Level level) {
      super(type, level);
      this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new Squid.SquidRandomMovementGoal(this));
      this.goalSelector.addGoal(1, new Squid.SquidFleeGoal());
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SQUID_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.SQUID_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SQUID_DEATH;
   }

   protected SoundEvent getSquirtSound() {
      return SoundEvents.SQUID_SQUIRT;
   }

   public boolean canBeLeashed() {
      return true;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   public @Nullable AgeableMob getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      return (AgeableMob)EntityTypes.SQUID.create(level, EntitySpawnReason.BREEDING);
   }

   protected double getDefaultGravity() {
      return 0.08D;
   }

   public void aiStep() {
      super.aiStep();
      this.xBodyRotO = this.xBodyRot;
      this.zBodyRotO = this.zBodyRot;
      this.oldTentacleMovement = this.tentacleMovement;
      this.oldTentacleAngle = this.tentacleAngle;
      this.tentacleMovement += this.tentacleSpeed;
      if ((double)this.tentacleMovement > (Math.PI * 2D)) {
         if (this.level().isClientSide()) {
            this.tentacleMovement = ((float)Math.PI * 2F);
         } else {
            this.tentacleMovement -= ((float)Math.PI * 2F);
            if (this.random.nextInt(10) == 0) {
               this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
            }

            this.level().broadcastEntityEvent(this, (byte)19);
         }
      }

      if (this.isInWater()) {
         if (this.tentacleMovement < (float)Math.PI) {
            float tentacleScale = this.tentacleMovement / (float)Math.PI;
            this.tentacleAngle = Mth.sin((double)(tentacleScale * tentacleScale * (float)Math.PI)) * (float)Math.PI * 0.25F;
            if ((double)tentacleScale > 0.75D) {
               if (this.isLocalInstanceAuthoritative()) {
                  this.setDeltaMovement(this.movementVector);
               }

               this.rotateSpeed = 1.0F;
            } else {
               this.rotateSpeed *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            if (this.isLocalInstanceAuthoritative()) {
               this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            }

            this.rotateSpeed *= 0.99F;
         }

         Vec3 movement = this.getDeltaMovement();
         double horizontalMovement = movement.horizontalDistance();
         this.yBodyRot += (-((float)Mth.atan2(movement.x, movement.z)) * (180F / (float)Math.PI) - this.yBodyRot) * 0.1F;
         this.setYRot(this.yBodyRot);
         this.zBodyRot += (float)Math.PI * this.rotateSpeed * 1.5F;
         this.xBodyRot += (-((float)Mth.atan2(horizontalMovement, movement.y)) * (180F / (float)Math.PI) - this.xBodyRot) * 0.1F;
      } else {
         this.tentacleAngle = Mth.abs(Mth.sin((double)this.tentacleMovement)) * (float)Math.PI * 0.25F;
         if (!this.level().isClientSide()) {
            double yd = this.getDeltaMovement().y;
            if (this.hasEffect(MobEffects.LEVITATION)) {
               yd = 0.05D * (double)(this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1);
            } else {
               yd -= this.getGravity();
            }

            this.setDeltaMovement(0.0D, yd * (double)this.getAirDrag(), 0.0D);
         }

         this.xBodyRot += (-90.0F - this.xBodyRot) * 0.02F;
      }

   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      if (super.hurtServer(level, source, damage) && this.getLastHurtByMob() != null) {
         this.spawnInk();
         return true;
      } else {
         return false;
      }
   }

   private Vec3 rotateVector(final Vec3 vec) {
      Vec3 v = vec.xRot(this.xBodyRotO * ((float)Math.PI / 180F));
      return v.yRot(-this.yBodyRotO * ((float)Math.PI / 180F));
   }

   private void spawnInk() {
      this.makeSound(this.getSquirtSound());
      Vec3 pos = this.rotateVector(new Vec3(0.0D, -1.0D, 0.0D)).add(this.getX(), this.getY(), this.getZ());

      for(int i = 0; i < 30; ++i) {
         Vec3 dir = this.rotateVector(new Vec3((double)this.random.nextFloat() * 0.6D - 0.3D, -1.0D, (double)this.random.nextFloat() * 0.6D - 0.3D));
         float inkPosOffsetScale = this.isBaby() ? 0.1F : 0.3F;
         Vec3 dirOffset = dir.scale((double)(inkPosOffsetScale + this.random.nextFloat() * 2.0F));
         ((ServerLevel)this.level()).sendParticles(this.getInkParticle(), pos.x, pos.y + 0.5D, pos.z, 0, dirOffset.x, dirOffset.y, dirOffset.z, (double)0.1F);
      }

   }

   protected ParticleOptions getInkParticle() {
      return ParticleTypes.SQUID_INK;
   }

   public void travel(final Vec3 input) {
      this.move(MoverType.SELF, this.getDeltaMovement());
   }

   public void handleEntityEvent(final @EntityEvent.Value byte id) {
      if (id == 19) {
         this.tentacleMovement = 0.0F;
      } else {
         super.handleEntityEvent(id);
      }

   }

   public boolean hasMovementVector() {
      return this.movementVector.lengthSqr() > (double)1.0E-5F;
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      SpawnGroupData spawnGroupData = (SpawnGroupData)Objects.requireNonNullElseGet(groupData, () -> new AgeableMob.AgeableMobGroupData(0.05F));
      return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
   }

   private class SquidFleeGoal extends Goal {
      private static final float SQUID_FLEE_SPEED = 3.0F;
      private static final float SQUID_FLEE_MIN_DISTANCE = 5.0F;
      private static final float SQUID_FLEE_MAX_DISTANCE = 10.0F;
      private int fleeTicks;

      private SquidFleeGoal() {
         Objects.requireNonNull(Squid.this);
         super();
      }

      public boolean canUse() {
         LivingEntity entity = Squid.this.getLastHurtByMob();
         if (Squid.this.isInWater() && entity != null) {
            return Squid.this.distanceToSqr(entity) < 100.0D;
         } else {
            return false;
         }
      }

      public void start() {
         this.fleeTicks = 0;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         ++this.fleeTicks;
         LivingEntity lastHurtByMob = Squid.this.getLastHurtByMob();
         if (lastHurtByMob != null) {
            Vec3 fleeTo = new Vec3(Squid.this.getX() - lastHurtByMob.getX(), Squid.this.getY() - lastHurtByMob.getY(), Squid.this.getZ() - lastHurtByMob.getZ());
            BlockState blockState = Squid.this.level().getBlockState(BlockPos.containing(Squid.this.getX() + fleeTo.x, Squid.this.getY() + fleeTo.y, Squid.this.getZ() + fleeTo.z));
            FluidState fluidState = Squid.this.level().getFluidState(BlockPos.containing(Squid.this.getX() + fleeTo.x, Squid.this.getY() + fleeTo.y, Squid.this.getZ() + fleeTo.z));
            if (fluidState.is(FluidTags.WATER) || blockState.isAir()) {
               double length = fleeTo.length();
               if (length > 0.0D) {
                  fleeTo.normalize();
                  double avoidSpeed = 3.0D;
                  if (length > 5.0D) {
                     avoidSpeed -= (length - 5.0D) / 5.0D;
                  }

                  if (avoidSpeed > 0.0D) {
                     fleeTo = fleeTo.scale(avoidSpeed);
                  }
               }

               if (blockState.isAir()) {
                  fleeTo = fleeTo.subtract(0.0D, fleeTo.y, 0.0D);
               }

               Squid.this.movementVector = new Vec3(fleeTo.x / 20.0D, fleeTo.y / 20.0D, fleeTo.z / 20.0D);
            }

            if (this.fleeTicks % 10 == 5) {
               Squid.this.level().addParticle(ParticleTypes.BUBBLE, Squid.this.getX(), Squid.this.getY(), Squid.this.getZ(), 0.0D, 0.0D, 0.0D);
            }

         }
      }
   }

   private static class SquidRandomMovementGoal extends Goal {
      private final Squid squid;

      public SquidRandomMovementGoal(final Squid squid) {
         this.squid = squid;
      }

      public boolean canUse() {
         return true;
      }

      public void tick() {
         int noActionTime = this.squid.getNoActionTime();
         if (noActionTime > 100) {
            this.squid.movementVector = Vec3.ZERO;
         } else if (this.squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !this.squid.wasTouchingWater || !this.squid.hasMovementVector()) {
            float angle = this.squid.getRandom().nextFloat() * ((float)Math.PI * 2F);
            this.squid.movementVector = new Vec3((double)(Mth.cos((double)angle) * 0.2F), (double)(-0.1F + this.squid.getRandom().nextFloat() * 0.2F), (double)(Mth.sin((double)angle) * 0.2F));
         }

      }
   }
}
