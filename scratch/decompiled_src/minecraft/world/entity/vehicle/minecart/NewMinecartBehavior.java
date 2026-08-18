package net.minecraft.world.entity.vehicle.minecart;

import com.mojang.datafixers.util.Pair;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class NewMinecartBehavior extends MinecartBehavior {
   public static final int POS_ROT_LERP_TICKS = 3;
   public static final double ON_RAIL_Y_OFFSET = 0.1D;
   public static final double OPPOSING_SLOPES_REST_AT_SPEED_THRESHOLD = 0.005D;
   private NewMinecartBehavior.@Nullable StepPartialTicks cacheIndexAlpha;
   private int cachedLerpDelay;
   private float cachedPartialTick;
   private int lerpDelay = 0;
   public final List lerpSteps = new LinkedList();
   public final List currentLerpSteps = new LinkedList();
   public double currentLerpStepsTotalWeight = 0.0D;
   public NewMinecartBehavior.MinecartStep oldLerp = NewMinecartBehavior.MinecartStep.ZERO;

   public NewMinecartBehavior(final AbstractMinecart minecart) {
      super(minecart);
   }

   public void tick() {
      Level pos = this.level();
      if (pos instanceof ServerLevel serverLevel) {
         BlockPos var5 = this.minecart.getCurrentBlockPosOrRailBelow();
         BlockState state = this.level().getBlockState(var5);
         if (this.minecart.isFirstTick()) {
            this.minecart.setOnRails(BaseRailBlock.isRail(state));
            this.adjustToRails(var5, state, true);
         }

         this.minecart.applyGravity();
         this.minecart.moveAlongTrack(serverLevel);
      } else {
         this.lerpClientPositionAndRotation();
         boolean onRails = BaseRailBlock.isRail(this.level().getBlockState(this.minecart.getCurrentBlockPosOrRailBelow()));
         this.minecart.setOnRails(onRails);
      }
   }

   private void lerpClientPositionAndRotation() {
      if (--this.lerpDelay <= 0) {
         this.setOldLerpValues();
         this.currentLerpSteps.clear();
         if (!this.lerpSteps.isEmpty()) {
            this.currentLerpSteps.addAll(this.lerpSteps);
            this.lerpSteps.clear();
            this.currentLerpStepsTotalWeight = 0.0D;

            for(NewMinecartBehavior.MinecartStep minecartStep : this.currentLerpSteps) {
               this.currentLerpStepsTotalWeight += (double)minecartStep.weight;
            }

            this.lerpDelay = this.currentLerpStepsTotalWeight == 0.0D ? 0 : 3;
         }
      }

      if (this.cartHasPosRotLerp()) {
         this.setPos(this.getCartLerpPosition(1.0F));
         this.setDeltaMovement(this.getCartLerpMovements(1.0F));
         this.setXRot(this.getCartLerpXRot(1.0F));
         this.setYRot(this.getCartLerpYRot(1.0F));
      }

   }

   public void setOldLerpValues() {
      this.oldLerp = new NewMinecartBehavior.MinecartStep(this.position(), this.getDeltaMovement(), this.getYRot(), this.getXRot(), 0.0F);
   }

   public boolean cartHasPosRotLerp() {
      return !this.currentLerpSteps.isEmpty();
   }

   public float getCartLerpXRot(final float partialTicks) {
      NewMinecartBehavior.StepPartialTicks currentStepPartialTicks = this.getCurrentLerpStep(partialTicks);
      return Mth.rotLerp(currentStepPartialTicks.partialTicksInStep, currentStepPartialTicks.previousStep.xRot, currentStepPartialTicks.currentStep.xRot);
   }

   public float getCartLerpYRot(final float partialTicks) {
      NewMinecartBehavior.StepPartialTicks currentStepPartialTicks = this.getCurrentLerpStep(partialTicks);
      return Mth.rotLerp(currentStepPartialTicks.partialTicksInStep, currentStepPartialTicks.previousStep.yRot, currentStepPartialTicks.currentStep.yRot);
   }

   public Vec3 getCartLerpPosition(final float partialTicks) {
      NewMinecartBehavior.StepPartialTicks currentStepPartialTicks = this.getCurrentLerpStep(partialTicks);
      return Mth.lerp((double)currentStepPartialTicks.partialTicksInStep, currentStepPartialTicks.previousStep.position, currentStepPartialTicks.currentStep.position);
   }

   public Vec3 getCartLerpMovements(final float partialTicks) {
      NewMinecartBehavior.StepPartialTicks currentStepPartialTicks = this.getCurrentLerpStep(partialTicks);
      return Mth.lerp((double)currentStepPartialTicks.partialTicksInStep, currentStepPartialTicks.previousStep.movement, currentStepPartialTicks.currentStep.movement);
   }

   private NewMinecartBehavior.StepPartialTicks getCurrentLerpStep(final float partialTick) {
      if (partialTick == this.cachedPartialTick && this.lerpDelay == this.cachedLerpDelay && this.cacheIndexAlpha != null) {
         return this.cacheIndexAlpha;
      } else {
         float alpha = ((float)(3 - this.lerpDelay) + partialTick) / 3.0F;
         float countUp = 0.0F;
         float indexedPartialTick = 1.0F;
         boolean foundIndex = false;

         int index;
         for(index = 0; index < this.currentLerpSteps.size(); ++index) {
            float weight = ((NewMinecartBehavior.MinecartStep)this.currentLerpSteps.get(index)).weight;
            if (!(weight <= 0.0F)) {
               countUp += weight;
               if ((double)countUp >= this.currentLerpStepsTotalWeight * (double)alpha) {
                  float current = countUp - weight;
                  indexedPartialTick = (float)(((double)alpha * this.currentLerpStepsTotalWeight - (double)current) / (double)weight);
                  foundIndex = true;
                  break;
               }
            }
         }

         if (!foundIndex) {
            index = this.currentLerpSteps.size() - 1;
         }

         NewMinecartBehavior.MinecartStep currentStep = (NewMinecartBehavior.MinecartStep)this.currentLerpSteps.get(index);
         NewMinecartBehavior.MinecartStep previousStep = index > 0 ? (NewMinecartBehavior.MinecartStep)this.currentLerpSteps.get(index - 1) : this.oldLerp;
         this.cacheIndexAlpha = new NewMinecartBehavior.StepPartialTicks(indexedPartialTick, currentStep, previousStep);
         this.cachedLerpDelay = this.lerpDelay;
         this.cachedPartialTick = partialTick;
         return this.cacheIndexAlpha;
      }
   }

   public void adjustToRails(final BlockPos targetBlockPos, final BlockState currentState, final boolean instant) {
      if (BaseRailBlock.isRail(currentState)) {
         RailShape shape = (RailShape)currentState.getValue(((BaseRailBlock)currentState.getBlock()).getShapeProperty());
         Pair exits = AbstractMinecart.exits(shape);
         Vec3 exit0 = (new Vec3((Vec3i)exits.getFirst())).scale(0.5D);
         Vec3 exit1 = (new Vec3((Vec3i)exits.getSecond())).scale(0.5D);
         Vec3 horizontalOutDirection = exit0.horizontal();
         Vec3 horizontalInDirection = exit1.horizontal();
         if (this.getDeltaMovement().length() > (double)1.0E-5F && this.getDeltaMovement().dot(horizontalOutDirection) < this.getDeltaMovement().dot(horizontalInDirection) || this.isDecending(horizontalInDirection, shape)) {
            Vec3 swap = horizontalOutDirection;
            horizontalOutDirection = horizontalInDirection;
            horizontalInDirection = swap;
         }

         float yRot = 180.0F - (float)(Math.atan2(horizontalOutDirection.z, horizontalOutDirection.x) * 180.0D / Math.PI);
         yRot += this.minecart.isFlipped() ? 180.0F : 0.0F;
         Vec3 previousPosition = this.position();
         boolean inCorner = exit0.x() != exit1.x() && exit0.z() != exit1.z();
         Vec3 targetPosition;
         if (inCorner) {
            Vec3 from0to1 = exit1.subtract(exit0);
            Vec3 from0toPos = previousPosition.subtract(Vec3.atBottomCenterOf(targetBlockPos)).subtract(exit0);
            Vec3 travelVectorFrom0 = from0to1.scale(from0to1.dot(from0toPos) / from0to1.dot(from0to1));
            targetPosition = Vec3.atBottomCenterOf(targetBlockPos).add(exit0).add(travelVectorFrom0);
            yRot = 180.0F - (float)(Math.atan2(travelVectorFrom0.z, travelVectorFrom0.x) * 180.0D / Math.PI);
            yRot += this.minecart.isFlipped() ? 180.0F : 0.0F;
         } else {
            boolean zSnap = exit0.subtract(exit1).x != 0.0D;
            boolean xSnap = exit0.subtract(exit1).z != 0.0D;
            targetPosition = new Vec3(xSnap ? Vec3.atCenterOf(targetBlockPos).x : previousPosition.x, (double)targetBlockPos.getY(), zSnap ? Vec3.atCenterOf(targetBlockPos).z : previousPosition.z);
         }

         Vec3 diffFromBlock = targetPosition.subtract(previousPosition);
         this.setPos(previousPosition.add(diffFromBlock));
         float xRot = 0.0F;
         boolean inHill = exit0.y() != exit1.y();
         if (inHill) {
            Vec3 inPosition = Vec3.atBottomCenterOf(targetBlockPos).add(horizontalInDirection);
            double horizontalDistanceFromIn = inPosition.distanceTo(this.position());
            this.setPos(this.position().add(0.0D, horizontalDistanceFromIn + 0.1D, 0.0D));
            xRot = this.minecart.isFlipped() ? 45.0F : -45.0F;
         } else {
            this.setPos(this.position().add(0.0D, 0.1D, 0.0D));
         }

         this.setRotation(yRot, xRot);
         double adjustDistance = previousPosition.distanceTo(this.position());
         if (adjustDistance > 0.0D) {
            this.lerpSteps.add(new NewMinecartBehavior.MinecartStep(this.position(), this.getDeltaMovement(), this.getYRot(), this.getXRot(), instant ? 0.0F : (float)adjustDistance));
         }

      }
   }

   private void setRotation(float yRot, float xRot) {
      double yRotDiff = (double)Math.abs(yRot - this.getYRot());
      if (yRotDiff >= 175.0D && yRotDiff <= 185.0D) {
         this.minecart.setFlipped(!this.minecart.isFlipped());
         yRot -= 180.0F;
         xRot *= -1.0F;
      }

      xRot = Math.clamp(xRot, -45.0F, 45.0F);
      this.setXRot(xRot % 360.0F);
      this.setYRot(yRot % 360.0F);
   }

   public void moveAlongTrack(final ServerLevel level) {
      for(NewMinecartBehavior.TrackIteration trackIteration = new NewMinecartBehavior.TrackIteration(); trackIteration.shouldIterate() && this.minecart.isAlive(); trackIteration.firstIteration = false) {
         Vec3 initialStepDeltaMovement = this.getDeltaMovement();
         BlockPos currentPos = this.minecart.getCurrentBlockPosOrRailBelow();
         BlockState currentState = this.level().getBlockState(currentPos);
         boolean onRails = BaseRailBlock.isRail(currentState);
         if (this.minecart.isOnRails() != onRails) {
            this.minecart.setOnRails(onRails);
            this.adjustToRails(currentPos, currentState, false);
         }

         if (onRails) {
            this.minecart.resetFallDistance();
            this.minecart.setOldPosAndRot();
            if (currentState.is(Blocks.ACTIVATOR_RAIL)) {
               this.minecart.activateMinecart(level, currentPos.getX(), currentPos.getY(), currentPos.getZ(), currentState.getValue(PoweredRailBlock.POWERED));
            }

            RailShape shape = (RailShape)currentState.getValue(((BaseRailBlock)currentState.getBlock()).getShapeProperty());
            Vec3 newDeltaMovement = this.calculateTrackSpeed(level, initialStepDeltaMovement.horizontal(), trackIteration, currentPos, currentState, shape);
            if (trackIteration.firstIteration) {
               trackIteration.movementLeft = newDeltaMovement.horizontalDistance();
            } else {
               trackIteration.movementLeft += newDeltaMovement.horizontalDistance() - initialStepDeltaMovement.horizontalDistance();
            }

            this.setDeltaMovement(newDeltaMovement);
            trackIteration.movementLeft = this.minecart.makeStepAlongTrack(currentPos, shape, trackIteration.movementLeft);
         } else {
            this.minecart.comeOffTrack(level);
            trackIteration.movementLeft = 0.0D;
         }

         Vec3 stepPosition = this.position();
         Vec3 stepDelta = stepPosition.subtract(this.minecart.oldPosition());
         double stepLength = stepDelta.length();
         if (stepLength > (double)1.0E-5F) {
            if (!(stepDelta.horizontalDistanceSqr() > (double)1.0E-5F)) {
               if (!this.minecart.isOnRails()) {
                  this.setXRot(this.minecart.onGround() ? 0.0F : Mth.rotLerp(0.2F, this.getXRot(), 0.0F));
               }
            } else {
               float yRot = 180.0F - (float)(Math.atan2(stepDelta.z, stepDelta.x) * 180.0D / Math.PI);
               float xRot = this.minecart.onGround() && !this.minecart.isOnRails() ? 0.0F : 90.0F - (float)(Math.atan2(stepDelta.horizontalDistance(), stepDelta.y) * 180.0D / Math.PI);
               yRot += this.minecart.isFlipped() ? 180.0F : 0.0F;
               xRot *= this.minecart.isFlipped() ? -1.0F : 1.0F;
               this.setRotation(yRot, xRot);
            }

            this.lerpSteps.add(new NewMinecartBehavior.MinecartStep(stepPosition, this.getDeltaMovement(), this.getYRot(), this.getXRot(), (float)Math.min(stepLength, this.getMaxSpeed(level))));
         } else if (initialStepDeltaMovement.horizontalDistanceSqr() > 0.0D) {
            this.lerpSteps.add(new NewMinecartBehavior.MinecartStep(stepPosition, this.getDeltaMovement(), this.getYRot(), this.getXRot(), 1.0F));
         }

         if (stepLength > (double)1.0E-5F || trackIteration.firstIteration) {
            this.minecart.applyEffectsFromBlocks();
            this.minecart.applyEffectsFromBlocks();
         }
      }

   }

   private Vec3 calculateTrackSpeed(final ServerLevel level, final Vec3 deltaMovement, final NewMinecartBehavior.TrackIteration trackIteration, final BlockPos currentPos, final BlockState currentState, final RailShape shape) {
      Vec3 newDeltaMovement = deltaMovement;
      if (!trackIteration.hasGainedSlopeSpeed) {
         Vec3 slopedDeltaMovement = this.calculateSlopeSpeed(deltaMovement, shape);
         if (slopedDeltaMovement.horizontalDistanceSqr() != deltaMovement.horizontalDistanceSqr()) {
            trackIteration.hasGainedSlopeSpeed = true;
            newDeltaMovement = slopedDeltaMovement;
         }
      }

      if (trackIteration.firstIteration) {
         Vec3 playerInputMovement = this.calculatePlayerInputSpeed(newDeltaMovement);
         if (playerInputMovement.horizontalDistanceSqr() != newDeltaMovement.horizontalDistanceSqr()) {
            trackIteration.hasHalted = true;
            newDeltaMovement = playerInputMovement;
         }
      }

      if (!trackIteration.hasHalted) {
         Vec3 haltedDeltaMovement = this.calculateHaltTrackSpeed(newDeltaMovement, currentState);
         if (haltedDeltaMovement.horizontalDistanceSqr() != newDeltaMovement.horizontalDistanceSqr()) {
            trackIteration.hasHalted = true;
            newDeltaMovement = haltedDeltaMovement;
         }
      }

      if (trackIteration.firstIteration) {
         newDeltaMovement = this.minecart.applyNaturalSlowdown(newDeltaMovement);
         if (newDeltaMovement.lengthSqr() > 0.0D) {
            double speed = Math.min(newDeltaMovement.length(), this.minecart.getMaxSpeed(level));
            newDeltaMovement = newDeltaMovement.normalize().scale(speed);
         }
      }

      if (!trackIteration.hasBoosted) {
         Vec3 boostedDeltaMovement = this.calculateBoostTrackSpeed(newDeltaMovement, currentPos, currentState);
         if (boostedDeltaMovement.horizontalDistanceSqr() != newDeltaMovement.horizontalDistanceSqr()) {
            trackIteration.hasBoosted = true;
            newDeltaMovement = boostedDeltaMovement;
         }
      }

      return newDeltaMovement;
   }

   private Vec3 calculateSlopeSpeed(final Vec3 deltaMovement, final RailShape shape) {
      double slideSpeed = Math.max(0.0078125D, deltaMovement.horizontalDistance() * 0.02D);
      if (this.minecart.isInWater()) {
         slideSpeed *= 0.2D;
      }

      Vec3 var10000;
      switch (shape) {
         case ASCENDING_EAST:
            var10000 = deltaMovement.add(-slideSpeed, 0.0D, 0.0D);
            break;
         case ASCENDING_WEST:
            var10000 = deltaMovement.add(slideSpeed, 0.0D, 0.0D);
            break;
         case ASCENDING_NORTH:
            var10000 = deltaMovement.add(0.0D, 0.0D, slideSpeed);
            break;
         case ASCENDING_SOUTH:
            var10000 = deltaMovement.add(0.0D, 0.0D, -slideSpeed);
            break;
         default:
            var10000 = deltaMovement;
      }

      return var10000;
   }

   private Vec3 calculatePlayerInputSpeed(final Vec3 deltaMovement) {
      Entity moveIntent = this.minecart.getFirstPassenger();
      if (moveIntent instanceof ServerPlayer player) {
         Vec3 moveIntent = player.getLastClientMoveIntent();
         if (moveIntent.lengthSqr() > 0.0D) {
            Vec3 riderMovement = moveIntent.normalize();
            double ownDist = deltaMovement.horizontalDistanceSqr();
            if (riderMovement.lengthSqr() > 0.0D && ownDist < 0.01D) {
               return deltaMovement.add((new Vec3(riderMovement.x, 0.0D, riderMovement.z)).normalize().scale(0.001D));
            }
         }

         return deltaMovement;
      } else {
         return deltaMovement;
      }
   }

   private Vec3 calculateHaltTrackSpeed(final Vec3 deltaMovement, final BlockState state) {
      if (state.is(Blocks.POWERED_RAIL) && !state.getValue(PoweredRailBlock.POWERED)) {
         return deltaMovement.length() < 0.03D ? Vec3.ZERO : deltaMovement.scale(0.5D);
      } else {
         return deltaMovement;
      }
   }

   private Vec3 calculateBoostTrackSpeed(final Vec3 deltaMovement, final BlockPos pos, final BlockState state) {
      if (state.is(Blocks.POWERED_RAIL) && state.getValue(PoweredRailBlock.POWERED)) {
         if (deltaMovement.length() > 0.01D) {
            return deltaMovement.normalize().scale(deltaMovement.length() + 0.06D);
         } else {
            Vec3 powerDirection = this.minecart.getRedstoneDirection(pos);
            return powerDirection.lengthSqr() <= 0.0D ? deltaMovement : powerDirection.scale(deltaMovement.length() + 0.2D);
         }
      } else {
         return deltaMovement;
      }
   }

   public double stepAlongTrack(final BlockPos pos, final RailShape shape, double movementLeft) {
      if (movementLeft < (double)1.0E-5F) {
         return 0.0D;
      } else {
         Vec3 oldPosition = this.position();
         Pair exits = AbstractMinecart.exits(shape);
         Vec3i exit0 = (Vec3i)exits.getFirst();
         Vec3i exit1 = (Vec3i)exits.getSecond();
         Vec3 movement = this.getDeltaMovement().horizontal();
         if (movement.length() < (double)1.0E-5F) {
            this.setDeltaMovement(Vec3.ZERO);
            return 0.0D;
         } else {
            boolean inHill = exit0.getY() != exit1.getY();
            Vec3 horizontalInDirection = (new Vec3(exit1)).scale(0.5D).horizontal();
            Vec3 horizontalOutDirection = (new Vec3(exit0)).scale(0.5D).horizontal();
            if (movement.dot(horizontalOutDirection) < movement.dot(horizontalInDirection)) {
               horizontalOutDirection = horizontalInDirection;
            }

            Vec3 outPosition = Vec3.atBottomCenterOf(pos).add(horizontalOutDirection).add(0.0D, 0.1D, 0.0D).add(horizontalOutDirection.normalize().scale((double)1.0E-5F));
            if (inHill && !this.isDecending(movement, shape)) {
               outPosition = outPosition.add(0.0D, 1.0D, 0.0D);
            }

            Vec3 towardsOut = outPosition.subtract(this.position()).normalize();
            movement = towardsOut.scale(movement.length() / towardsOut.horizontalDistance());
            Vec3 newPosition = oldPosition.add(movement.normalize().scale(movementLeft * (double)(inHill ? Mth.SQRT_OF_TWO : 1.0F)));
            if (oldPosition.distanceToSqr(outPosition) <= oldPosition.distanceToSqr(newPosition)) {
               movementLeft = outPosition.subtract(newPosition).horizontalDistance();
               newPosition = outPosition;
            } else {
               movementLeft = 0.0D;
            }

            this.minecart.move(MoverType.SELF, newPosition.subtract(oldPosition));
            BlockState newBlockState = this.level().getBlockState(BlockPos.containing(newPosition));
            if (inHill) {
               if (BaseRailBlock.isRail(newBlockState)) {
                  RailShape newRailShape = (RailShape)newBlockState.getValue(((BaseRailBlock)newBlockState.getBlock()).getShapeProperty());
                  if (this.restAtVShape(shape, newRailShape)) {
                     return 0.0D;
                  }
               }

               double horizontalDistanceFromOut = outPosition.horizontal().distanceTo(this.position().horizontal());
               double projectYPos = outPosition.y + (this.isDecending(movement, shape) ? horizontalDistanceFromOut : -horizontalDistanceFromOut);
               if (this.position().y < projectYPos) {
                  this.setPos(this.position().x, projectYPos, this.position().z);
               }
            }

            if (this.position().distanceTo(oldPosition) < (double)1.0E-5F && newPosition.distanceTo(oldPosition) > (double)1.0E-5F) {
               this.setDeltaMovement(Vec3.ZERO);
               return 0.0D;
            } else {
               this.setDeltaMovement(movement);
               return movementLeft;
            }
         }
      }
   }

   private boolean restAtVShape(final RailShape currentRailShape, final RailShape newRailShape) {
      if (this.getDeltaMovement().lengthSqr() < 0.005D && newRailShape.isSlope() && this.isDecending(this.getDeltaMovement(), currentRailShape) && !this.isDecending(this.getDeltaMovement(), newRailShape)) {
         this.setDeltaMovement(Vec3.ZERO);
         return true;
      } else {
         return false;
      }
   }

   public double getMaxSpeed(final ServerLevel level) {
      return (double)((Integer)level.getGameRules().get(GameRules.MAX_MINECART_SPEED)).intValue() * (this.minecart.isInWater() ? 0.5D : 1.0D) / 20.0D;
   }

   private boolean isDecending(final Vec3 movement, final RailShape shape) {
      boolean var10000;
      switch (shape) {
         case ASCENDING_EAST:
            var10000 = movement.x < 0.0D;
            break;
         case ASCENDING_WEST:
            var10000 = movement.x > 0.0D;
            break;
         case ASCENDING_NORTH:
            var10000 = movement.z > 0.0D;
            break;
         case ASCENDING_SOUTH:
            var10000 = movement.z < 0.0D;
            break;
         default:
            var10000 = false;
      }

      return var10000;
   }

   public double getSlowdownFactor() {
      return this.minecart.isVehicle() ? 0.997D : 0.975D;
   }

   public boolean pushAndPickupEntities() {
      boolean pickedUp = this.pickupEntities(this.minecart.getBoundingBox().inflate(0.2D, 0.0D, 0.2D));
      if (!this.minecart.horizontalCollision && !this.minecart.verticalCollision) {
         return false;
      } else {
         boolean pushed = this.pushEntities(this.minecart.getBoundingBox().inflate(1.0E-7D));
         return pickedUp && !pushed;
      }
   }

   public boolean pickupEntities(final AABB hitbox) {
      if (this.minecart.isRideable() && !this.minecart.isVehicle()) {
         List entities = this.level().getEntities(this.minecart, hitbox, EntitySelector.pushableBy(this.minecart));
         if (!entities.isEmpty()) {
            for(Entity entity : entities) {
               if (!(entity instanceof Player) && !(entity instanceof IronGolem) && !(entity instanceof AbstractMinecart) && !this.minecart.isVehicle() && !entity.isPassenger()) {
                  boolean pickedUp = entity.startRiding(this.minecart);
                  if (pickedUp) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean pushEntities(final AABB hitbox) {
      boolean pushed = false;
      if (this.minecart.isRideable()) {
         List entities = this.level().getEntities(this.minecart, hitbox, EntitySelector.pushableBy(this.minecart));
         if (!entities.isEmpty()) {
            for(Entity entity : entities) {
               if (entity instanceof Player || entity instanceof IronGolem || entity instanceof AbstractMinecart || this.minecart.isVehicle() || entity.isPassenger()) {
                  entity.push(this.minecart);
                  pushed = true;
               }
            }
         }
      } else {
         for(Entity entity : this.level().getEntities(this.minecart, hitbox)) {
            if (!this.minecart.hasPassenger(entity) && entity.isPushable() && entity instanceof AbstractMinecart) {
               entity.push(this.minecart);
               pushed = true;
            }
         }
      }

      return pushed;
   }

   public static record MinecartStep(Vec3 position, Vec3 movement, float yRot, float xRot, float weight) {
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Vec3.STREAM_CODEC, NewMinecartBehavior.MinecartStep::position, Vec3.STREAM_CODEC, NewMinecartBehavior.MinecartStep::movement, ByteBufCodecs.ROTATION_BYTE, NewMinecartBehavior.MinecartStep::yRot, ByteBufCodecs.ROTATION_BYTE, NewMinecartBehavior.MinecartStep::xRot, ByteBufCodecs.FLOAT, NewMinecartBehavior.MinecartStep::weight, NewMinecartBehavior.MinecartStep::new);
      public static final NewMinecartBehavior.MinecartStep ZERO = new NewMinecartBehavior.MinecartStep(Vec3.ZERO, Vec3.ZERO, 0.0F, 0.0F, 0.0F);
   }

   private static record StepPartialTicks(float partialTicksInStep, NewMinecartBehavior.MinecartStep currentStep, NewMinecartBehavior.MinecartStep previousStep) {
   }

   private static class TrackIteration {
      public double movementLeft = 0.0D;
      public boolean firstIteration = true;
      public boolean hasGainedSlopeSpeed = false;
      public boolean hasHalted = false;
      public boolean hasBoosted = false;

      public boolean shouldIterate() {
         return this.firstIteration || this.movementLeft > (double)1.0E-5F;
      }
   }
}
