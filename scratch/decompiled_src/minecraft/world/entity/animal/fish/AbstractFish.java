package net.minecraft.world.entity.animal.fish;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Bucketable;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractFish extends WaterAnimal implements Bucketable {
   private static final EntityDataAccessor FROM_BUCKET = SynchedEntityData.defineId(AbstractFish.class, EntityDataSerializers.BOOLEAN);
   private static final boolean DEFAULT_FROM_BUCKET = false;

   public AbstractFish(final EntityType type, final Level level) {
      super(type, level);
      this.moveControl = new AbstractFish.FishMoveControl(this);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0D);
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.fromBucket();
   }

   public boolean removeWhenFarAway(final double distSqr) {
      return !this.fromBucket() && !this.hasCustomName();
   }

   public int getMaxSpawnClusterSize() {
      return 8;
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(FROM_BUCKET, false);
   }

   public boolean fromBucket() {
      return this.entityData.get(FROM_BUCKET);
   }

   public void setFromBucket(final boolean fromBucket) {
      this.entityData.set(FROM_BUCKET, fromBucket);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putBoolean("FromBucket", this.fromBucket());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setFromBucket(input.getBooleanOr("FromBucket", false));
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(2, new AvoidEntityGoal(this, Player.class, 8.0F, 1.6D, 1.4D, EntitySelector.NO_SPECTATORS));
      this.goalSelector.addGoal(4, new AbstractFish.FishSwimGoal(this));
   }

   protected PathNavigation createNavigation(final Level level) {
      return new WaterBoundPathNavigation(this, level);
   }

   protected void travelInWater(final Vec3 input, final double baseGravity, final boolean isFalling, final double oldY) {
      this.moveRelative(0.01F, input);
      this.move(MoverType.SELF, this.getDeltaMovement());
      this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
      if (this.getTarget() == null) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
      }

   }

   public void aiStep() {
      if (!this.isInWater() && this.onGround() && this.verticalCollision) {
         this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F), (double)0.4F, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F)));
         this.setOnGround(false);
         this.needsSync = true;
         this.makeSound(this.getFlopSound());
      }

      super.aiStep();
   }

   protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
      return (InteractionResult)Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
   }

   public void saveToBucketTag(final ItemStack bucket) {
      Bucketable.saveDefaultDataToBucketTag(this, bucket);
   }

   public void loadFromBucketTag(final CompoundTag tag) {
      Bucketable.loadDefaultDataFromBucketTag(this, tag);
   }

   public SoundEvent getPickupSound() {
      return SoundEvents.BUCKET_FILL_FISH;
   }

   protected boolean canRandomSwim() {
      return true;
   }

   protected abstract SoundEvent getFlopSound();

   protected SoundEvent getSwimSound() {
      return SoundEvents.FISH_SWIM;
   }

   protected void playStepSound(final BlockPos pos, final BlockState blockState) {
   }

   private static class FishMoveControl extends MoveControl {
      public FishMoveControl(final AbstractFish fish) {
         super(fish);
      }

      public void tick() {
         if (((AbstractFish)this.mob).isEyeInFluid(FluidTags.WATER)) {
            ((AbstractFish)this.mob).setDeltaMovement(((AbstractFish)this.mob).getDeltaMovement().add(0.0D, 0.005D, 0.0D));
         }

         if (this.operation == MoveControl.Operation.MOVE_TO && !((AbstractFish)this.mob).getNavigation().isDone()) {
            float targetSpeed = (float)(this.speedModifier * ((AbstractFish)this.mob).getAttributeValue(Attributes.MOVEMENT_SPEED));
            ((AbstractFish)this.mob).setSpeed(Mth.lerp(0.125F, ((AbstractFish)this.mob).getSpeed(), targetSpeed));
            double xd = this.wantedX - ((AbstractFish)this.mob).getX();
            double yd = this.wantedY - ((AbstractFish)this.mob).getY();
            double zd = this.wantedZ - ((AbstractFish)this.mob).getZ();
            if (yd != 0.0D) {
               double dd = Math.sqrt(xd * xd + yd * yd + zd * zd);
               ((AbstractFish)this.mob).setDeltaMovement(((AbstractFish)this.mob).getDeltaMovement().add(0.0D, (double)((AbstractFish)this.mob).getSpeed() * (yd / dd) * 0.1D, 0.0D));
            }

            if (xd != 0.0D || zd != 0.0D) {
               float yRotD = (float)(Mth.atan2(zd, xd) * (double)(180F / (float)Math.PI)) - 90.0F;
               ((AbstractFish)this.mob).setYRot(this.rotlerp(((AbstractFish)this.mob).getYRot(), yRotD, 90.0F));
               ((AbstractFish)this.mob).yBodyRot = ((AbstractFish)this.mob).getYRot();
            }

         } else {
            ((AbstractFish)this.mob).setSpeed(0.0F);
         }
      }
   }

   private static class FishSwimGoal extends RandomSwimmingGoal {
      private final AbstractFish fish;

      public FishSwimGoal(final AbstractFish fish) {
         super(fish, 1.0D, 40);
         this.fish = fish;
      }

      public boolean canUse() {
         return this.fish.canRandomSwim() && super.canUse();
      }
   }
}
