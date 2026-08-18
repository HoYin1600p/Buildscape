package net.minecraft.world.entity.animal.dolphin;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.DolphinJumpGoal;
import net.minecraft.world.entity.ai.goal.FollowPlayerRiddenEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Dolphin extends AgeableWaterCreature {
   private static final EntityDataAccessor GOT_FISH = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor MOISTNESS_LEVEL = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.INT);
   private static final TargetingConditions SWIM_WITH_PLAYER_TARGETING = TargetingConditions.forNonCombat().range(10.0D).ignoreLineOfSight();
   public static final int TOTAL_AIR_SUPPLY = 4800;
   private static final int TOTAL_MOISTNESS_LEVEL = 2400;
   public static final Predicate ALLOWED_ITEMS = (e) -> !e.hasPickUpDelay() && e.isAlive() && e.isInWater();
   public static final float BABY_SCALE = 0.65F;
   private static final EntityDimensions BABY_DIMENSIONS = EntityTypes.DOLPHIN.getDimensions().scale(0.65F).withEyeHeight(0.09375F).withAttachments(EntityAttachments.builder().attach(EntityAttachment.PASSENGER, 0.0F, 0.3125F, 0.0F));
   private static final boolean DEFAULT_GOT_FISH = false;
   private @Nullable BlockPos treasurePos;

   public Dolphin(final EntityType type, final Level level) {
      super(type, level);
      this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
      this.lookControl = new SmoothSwimmingLookControl(this, 10);
      this.setCanPickUpLoot(true);
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      this.setAirSupply(this.getMaxAirSupply());
      this.setXRot(0.0F);
      SpawnGroupData spawnGroupData = (SpawnGroupData)Objects.requireNonNullElseGet(groupData, () -> new AgeableMob.AgeableMobGroupData(0.1F));
      return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
   }

   public @Nullable Dolphin getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      return (Dolphin)EntityTypes.DOLPHIN.create(level, EntitySpawnReason.BREEDING);
   }

   public float getAgeScale() {
      return this.isBaby() ? 0.65F : 1.0F;
   }

   protected void handleAirSupply(final int preTickAirSupply) {
   }

   public boolean gotFish() {
      return this.entityData.get(GOT_FISH);
   }

   public void setGotFish(final boolean gotFish) {
      this.entityData.set(GOT_FISH, gotFish);
   }

   public int getMoistnessLevel() {
      return this.entityData.get(MOISTNESS_LEVEL);
   }

   public void setMoisntessLevel(final int level) {
      this.entityData.set(MOISTNESS_LEVEL, level);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(GOT_FISH, false);
      entityData.define(MOISTNESS_LEVEL, 2400);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putBoolean("GotFish", this.gotFish());
      output.putInt("Moistness", this.getMoistnessLevel());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setGotFish(input.getBooleanOr("GotFish", false));
      this.setMoisntessLevel(input.getIntOr("Moistness", 2400));
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new BreathAirGoal(this));
      this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
      this.goalSelector.addGoal(1, new Dolphin.DolphinSwimToTreasureGoal(this));
      this.goalSelector.addGoal(2, new Dolphin.DolphinSwimWithPlayerGoal(this, 4.0D));
      this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(5, new DolphinJumpGoal(this, 10));
      this.goalSelector.addGoal(6, new MeleeAttackGoal(this, (double)1.2F, true));
      this.goalSelector.addGoal(7, new Dolphin.MoveToItemGoal());
      this.goalSelector.addGoal(8, new Dolphin.PlayWithItemsGoal());
      this.goalSelector.addGoal(8, new FollowPlayerRiddenEntityGoal(this, AbstractBoat.class));
      this.goalSelector.addGoal(8, new FollowPlayerRiddenEntityGoal(this, AbstractNautilus.class));
      this.goalSelector.addGoal(9, new AvoidEntityGoal(this, Guardian.class, 8.0F, 1.0D, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Guardian.class)).setAlertOthers());
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, (double)1.2F).add(Attributes.ATTACK_DAMAGE, 3.0D);
   }

   protected PathNavigation createNavigation(final Level level) {
      return new WaterBoundPathNavigation(this, level);
   }

   public void playAttackSound() {
      this.playSound(SoundEvents.DOLPHIN_ATTACK, 1.0F, 1.0F);
   }

   public boolean canAttack(final LivingEntity target) {
      return !this.isBaby() && super.canAttack(target);
   }

   public int getMaxAirSupply() {
      return 4800;
   }

   protected int increaseAirSupply(final int currentSupply) {
      return this.getMaxAirSupply();
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 1;
   }

   protected boolean canDispenserEquipIntoSlot(final EquipmentSlot slot) {
      return slot == EquipmentSlot.MAINHAND && this.canPickUpLoot();
   }

   protected void pickUpItem(final ServerLevel level, final ItemEntity entity) {
      if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
         ItemStack itemStack = entity.getItem();
         if (this.canHoldItem(itemStack)) {
            this.onItemPickup(entity);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemStack);
            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            this.take(entity, itemStack.getCount());
            entity.discard();
         }
      }

   }

   public void tick() {
      super.tick();
      if (this.isNoAi()) {
         this.setAirSupply(this.getMaxAirSupply());
      } else {
         if (this.isInWaterOrRain()) {
            this.setMoisntessLevel(2400);
         } else {
            this.setMoisntessLevel(this.getMoistnessLevel() - 1);
            if (this.getMoistnessLevel() <= 0) {
               this.hurt(this.damageSources().dryOut(), 1.0F);
            }

            if (this.onGround()) {
               this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5D, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F)));
               this.setYRot(this.random.nextFloat() * 360.0F);
               this.setOnGround(false);
               this.needsSync = true;
            }
         }

         if (this.level().isClientSide() && this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.03D) {
            Vec3 viewVector = this.getViewVector(0.0F);
            float c = Mth.cos((double)(this.getYRot() * ((float)Math.PI / 180F))) * 0.3F;
            float s = Mth.sin((double)(this.getYRot() * ((float)Math.PI / 180F))) * 0.3F;
            float multiplier = 1.2F - this.random.nextFloat() * 0.7F;

            for(int i = 0; i < 2; ++i) {
               this.level().addParticle(ParticleTypes.DOLPHIN, this.getX() - viewVector.x * (double)multiplier + (double)c, this.getY() - viewVector.y, this.getZ() - viewVector.z * (double)multiplier + (double)s, 0.0D, 0.0D, 0.0D);
               this.level().addParticle(ParticleTypes.DOLPHIN, this.getX() - viewVector.x * (double)multiplier - (double)c, this.getY() - viewVector.y, this.getZ() - viewVector.z * (double)multiplier - (double)s, 0.0D, 0.0D, 0.0D);
            }
         }

      }
   }

   public void handleEntityEvent(final @EntityEvent.Value byte id) {
      if (id == 38) {
         this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
      } else {
         super.handleEntityEvent(id);
      }

   }

   private void addParticlesAroundSelf(final ParticleOptions particle) {
      for(int i = 0; i < 7; ++i) {
         double xa = this.random.nextGaussian() * 0.01D;
         double ya = this.random.nextGaussian() * 0.01D;
         double za = this.random.nextGaussian() * 0.01D;
         this.level().addParticle(particle, this.getRandomX(1.0D), this.getRandomY() + 0.2D, this.getRandomZ(1.0D), xa, ya, za);
      }

   }

   protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (!itemStack.isEmpty() && itemStack.is(ItemTags.FISHES)) {
         if (!this.level().isClientSide()) {
            this.playSound(SoundEvents.DOLPHIN_EAT, 1.0F, 1.0F);
         }

         if (this.canAgeUp()) {
            itemStack.consume(1, player);
            this.ageUp(getSpeedUpSecondsWhenFeeding(-this.age), true);
         } else {
            this.setGotFish(true);
            itemStack.consume(1, player);
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.mobInteract(player, hand);
      }
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.DOLPHIN_HURT;
   }

   protected @Nullable SoundEvent getDeathSound() {
      return SoundEvents.DOLPHIN_DEATH;
   }

   protected @Nullable SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.DOLPHIN_AMBIENT_WATER : SoundEvents.DOLPHIN_AMBIENT;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.DOLPHIN_SPLASH;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.DOLPHIN_SWIM;
   }

   protected boolean closeToNextPos() {
      BlockPos target = this.getNavigation().getTargetPos();
      return target != null ? target.closerToCenterThan(this.position(), 12.0D) : false;
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
   }

   protected void travelInWater(final Vec3 input, final double baseGravity, final boolean isFalling, final double oldY) {
      this.moveRelative(this.getSpeed(), input);
      this.move(MoverType.SELF, this.getDeltaMovement());
      this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
      if (this.getTarget() == null) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
      }

   }

   public boolean canBeLeashed() {
      return true;
   }

   private static class DolphinSwimToTreasureGoal extends Goal {
      private final Dolphin dolphin;
      private boolean stuck;

      public DolphinSwimToTreasureGoal(final Dolphin dolphin) {
         this.dolphin = dolphin;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean isInterruptable() {
         return false;
      }

      public boolean canUse() {
         return this.dolphin.gotFish() && this.dolphin.getAirSupply() >= 100;
      }

      public boolean canContinueToUse() {
         BlockPos treasurePos = this.dolphin.treasurePos;
         if (treasurePos == null) {
            return false;
         } else {
            return !BlockPos.containing((double)treasurePos.getX(), this.dolphin.getY(), (double)treasurePos.getZ()).closerToCenterThan(this.dolphin.position(), 4.0D) && !this.stuck && this.dolphin.getAirSupply() >= 100;
         }
      }

      public void start() {
         if (this.dolphin.level() instanceof ServerLevel) {
            ServerLevel level = (ServerLevel)this.dolphin.level();
            this.stuck = false;
            this.dolphin.getNavigation().stop();
            BlockPos dolphinPos = this.dolphin.blockPosition();
            BlockPos treasurePos = level.findNearestMapStructure(StructureTags.DOLPHIN_LOCATED, dolphinPos, 50, false);
            if (treasurePos != null) {
               this.dolphin.treasurePos = treasurePos;
               level.broadcastEntityEvent(this.dolphin, (byte)38);
            } else {
               this.stuck = true;
            }
         }
      }

      public void stop() {
         BlockPos treasurePos = this.dolphin.treasurePos;
         if (treasurePos == null || BlockPos.containing((double)treasurePos.getX(), this.dolphin.getY(), (double)treasurePos.getZ()).closerToCenterThan(this.dolphin.position(), 4.0D) || this.stuck) {
            this.dolphin.setGotFish(false);
         }

      }

      public void tick() {
         if (this.dolphin.treasurePos != null) {
            Level level = this.dolphin.level();
            if (this.dolphin.closeToNextPos() || this.dolphin.getNavigation().isDone()) {
               Vec3 treasurePos = Vec3.atCenterOf(this.dolphin.treasurePos);
               Vec3 nextPos = DefaultRandomPos.getPosTowards(this.dolphin, 16, 1, treasurePos, (double)((float)Math.PI / 8F));
               if (nextPos == null) {
                  nextPos = DefaultRandomPos.getPosTowards(this.dolphin, 8, 4, treasurePos, (double)((float)Math.PI / 2F));
               }

               if (nextPos != null) {
                  BlockPos next = BlockPos.containing(nextPos);
                  if (!level.getFluidState(next).is(FluidTags.WATER) || !level.getBlockState(next).isPathfindable(PathComputationType.WATER)) {
                     nextPos = DefaultRandomPos.getPosTowards(this.dolphin, 8, 5, treasurePos, (double)((float)Math.PI / 2F));
                  }
               }

               if (nextPos == null) {
                  this.stuck = true;
                  return;
               }

               this.dolphin.getLookControl().setLookAt(nextPos.x, nextPos.y, nextPos.z, (float)(this.dolphin.getMaxHeadYRot() + 20), (float)this.dolphin.getMaxHeadXRot());
               this.dolphin.getNavigation().moveTo(nextPos.x, nextPos.y, nextPos.z, 1.3D);
               if (level.getRandom().nextInt(this.adjustedTickDelay(80)) == 0) {
                  level.broadcastEntityEvent(this.dolphin, (byte)38);
               }
            }

         }
      }
   }

   private static class DolphinSwimWithPlayerGoal extends Goal {
      private final Dolphin dolphin;
      private final double speedModifier;
      private @Nullable Player player;

      public DolphinSwimWithPlayerGoal(final Dolphin dolphin, final double speedModifier) {
         this.dolphin = dolphin;
         this.speedModifier = speedModifier;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         this.player = getServerLevel(this.dolphin).getNearestPlayer(Dolphin.SWIM_WITH_PLAYER_TARGETING, this.dolphin);
         if (this.player == null) {
            return false;
         } else {
            return this.player.isSwimming() && this.dolphin.getTarget() != this.player;
         }
      }

      public boolean canContinueToUse() {
         return this.player != null && this.player.isSwimming() && this.dolphin.distanceToSqr(this.player) < 256.0D;
      }

      public void start() {
         this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100), this.dolphin);
      }

      public void stop() {
         this.player = null;
         this.dolphin.getNavigation().stop();
      }

      public void tick() {
         this.dolphin.getLookControl().setLookAt(this.player, (float)(this.dolphin.getMaxHeadYRot() + 20), (float)this.dolphin.getMaxHeadXRot());
         if (this.dolphin.distanceToSqr(this.player) < 6.25D) {
            this.dolphin.getNavigation().stop();
         } else {
            this.dolphin.getNavigation().moveTo(this.player, this.speedModifier);
         }

         if (this.player.isSwimming() && this.player.level().getRandom().nextInt(6) == 0) {
            this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100), this.dolphin);
         }

      }
   }

   private abstract class ItemGoal extends Goal {
      private ItemGoal() {
         Objects.requireNonNull(Dolphin.this);
         super();
      }

      protected void drop(final ItemStack itemStack) {
         if (!itemStack.isEmpty()) {
            double yHandPos = Dolphin.this.getEyeY() - (double)0.3F;
            ItemEntity thrownItem = new ItemEntity(Dolphin.this.level(), Dolphin.this.getX(), yHandPos, Dolphin.this.getZ(), itemStack);
            thrownItem.setPickUpDelay(40);
            thrownItem.setThrower(Dolphin.this);
            float pow = 0.3F;
            float dir = Dolphin.this.random.nextFloat() * ((float)Math.PI * 2F);
            float pow2 = 0.02F * Dolphin.this.random.nextFloat();
            thrownItem.setDeltaMovement((double)(0.3F * -Mth.sin((double)(Dolphin.this.getYRot() * ((float)Math.PI / 180F))) * Mth.cos((double)(Dolphin.this.getXRot() * ((float)Math.PI / 180F))) + Mth.cos((double)dir) * pow2), (double)(0.3F * Mth.sin((double)(Dolphin.this.getXRot() * ((float)Math.PI / 180F))) * 1.5F), (double)(0.3F * Mth.cos((double)(Dolphin.this.getYRot() * ((float)Math.PI / 180F))) * Mth.cos((double)(Dolphin.this.getXRot() * ((float)Math.PI / 180F))) + Mth.sin((double)dir) * pow2));
            Dolphin.this.level().addFreshEntity(thrownItem);
         }
      }

      protected boolean dropItem() {
         ItemStack itemStack = Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (itemStack.isEmpty()) {
            return false;
         } else {
            this.drop(itemStack);
            Dolphin.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            return true;
         }
      }
   }

   private class MoveToItemGoal extends Dolphin.ItemGoal {
      private int cooldown;

      public MoveToItemGoal() {
         Objects.requireNonNull(Dolphin.this);
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (this.cooldown > Dolphin.this.tickCount) {
            return false;
         } else {
            List items = Dolphin.this.level().getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), Dolphin.ALLOWED_ITEMS);
            return !items.isEmpty();
         }
      }

      public void stop() {
         if (this.dropItem()) {
            this.cooldown = Dolphin.this.tickCount + Dolphin.this.random.nextInt(100);
         }

      }

      public void start() {
         List items = Dolphin.this.level().getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), Dolphin.ALLOWED_ITEMS);
         if (!items.isEmpty()) {
            Dolphin.this.getNavigation().moveTo((Entity)items.get(0), (double)1.2F);
            Dolphin.this.playSound(SoundEvents.DOLPHIN_PLAY, 1.0F, 1.0F);
         }

         this.cooldown = 0;
      }

      public void tick() {
         List items = Dolphin.this.level().getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), Dolphin.ALLOWED_ITEMS);
         if (!items.isEmpty()) {
            Dolphin.this.getNavigation().moveTo((Entity)items.get(0), (double)1.2F);
         }

      }
   }

   private class PlayWithItemsGoal extends Dolphin.ItemGoal {
      private PlayWithItemsGoal() {
         Objects.requireNonNull(Dolphin.this);
         super();
      }

      public boolean canUse() {
         return !Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
      }

      public void stop() {
         this.dropItem();
      }

      public void tick() {
         this.dropItem();
      }
   }
}
