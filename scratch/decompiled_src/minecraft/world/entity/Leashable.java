package net.minecraft.world.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface Leashable {
   String LEASH_TAG = "leash";
   double LEASH_TOO_FAR_DIST = 12.0D;
   double LEASH_ELASTIC_DIST = 6.0D;
   double MAXIMUM_ALLOWED_LEASHED_DIST = 16.0D;
   Vec3 AXIS_SPECIFIC_ELASTICITY = new Vec3(0.8D, 0.2D, 0.8D);
   float SPRING_DAMPENING = 0.7F;
   double TORSIONAL_ELASTICITY = 10.0D;
   double STIFFNESS = 0.11D;
   List ENTITY_ATTACHMENT_POINT = ImmutableList.of(new Vec3(0.0D, 0.5D, 0.5D));
   List LEASHER_ATTACHMENT_POINT = ImmutableList.of(new Vec3(0.0D, 0.5D, 0.0D));
   List SHARED_QUAD_ATTACHMENT_POINTS = ImmutableList.of(new Vec3(-0.5D, 0.5D, 0.5D), new Vec3(-0.5D, 0.5D, -0.5D), new Vec3(0.5D, 0.5D, -0.5D), new Vec3(0.5D, 0.5D, 0.5D));

   Leashable.@Nullable LeashData getLeashData();

   void setLeashData(Leashable.@Nullable LeashData leashData);

   default boolean isLeashed() {
      return this.getLeashData() != null && this.getLeashData().leashHolder != null;
   }

   default boolean mayBeLeashed() {
      return this.getLeashData() != null;
   }

   default boolean canHaveALeashAttachedTo(final Entity entity) {
      if (this == entity) {
         return false;
      } else {
         return this.leashDistanceTo(entity) > this.leashSnapDistance() ? false : this.canBeLeashed();
      }
   }

   default double leashDistanceTo(final Entity entity) {
      return entity.getBoundingBox().getCenter().distanceTo(((Entity)this).getBoundingBox().getCenter());
   }

   default boolean canBeLeashed() {
      return true;
   }

   default void setDelayedLeashHolderId(final int entityId) {
      this.setLeashData(new Leashable.LeashData(entityId));
      dropLeash((Entity)this, false, false);
   }

   default void readLeashData(final ValueInput input) {
      Leashable.LeashData newLeashData = (Leashable.LeashData)input.read("leash", Leashable.LeashData.CODEC).orElse((Object)null);
      if (this.getLeashData() != null && newLeashData == null) {
         this.removeLeash();
      }

      this.setLeashData(newLeashData);
   }

   default void writeLeashData(final ValueOutput output, final Leashable.@Nullable LeashData leashData) {
      output.storeNullable("leash", Leashable.LeashData.CODEC, leashData);
   }

   private static void restoreLeashFromSave(final Entity entity, final Leashable.LeashData leashData) {
      if (leashData.delayedLeashInfo != null) {
         Level leashUuid = entity.level();
         if (leashUuid instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)leashUuid;
            Optional leashUuid = leashData.delayedLeashInfo.left();
            Optional pos = leashData.delayedLeashInfo.right();
            if (leashUuid.isPresent()) {
               Entity leasher = serverLevel.getEntity((UUID)leashUuid.get());
               if (leasher != null) {
                  setLeashedTo(entity, leasher, true);
                  return;
               }
            } else if (pos.isPresent()) {
               setLeashedTo(entity, LeashFenceKnotEntity.getOrCreateKnot(serverLevel, (BlockPos)pos.get()), true);
               return;
            }

            if (entity.tickCount > 100) {
               entity.spawnAtLocation(serverLevel, Items.LEAD);
               ((Leashable)entity).setLeashData((Leashable.LeashData)null);
            }
         }
      }

   }

   default void dropLeash() {
      dropLeash((Entity)this, true, true);
   }

   default void removeLeash() {
      dropLeash((Entity)this, true, false);
   }

   default void onLeashRemoved() {
   }

   private static void dropLeash(final Entity entity, final boolean sendPacket, final boolean dropLead) {
      Leashable.LeashData leashData = ((Leashable)entity).getLeashData();
      if (leashData != null && leashData.leashHolder != null) {
         ((Leashable)entity).setLeashData((Leashable.LeashData)null);
         ((Leashable)entity).onLeashRemoved();
         Level var5 = entity.level();
         if (var5 instanceof ServerLevel) {
            ServerLevel level = (ServerLevel)var5;
            if (dropLead) {
               entity.spawnAtLocation(level, Items.LEAD);
            }

            if (sendPacket) {
               level.getChunkSource().sendToTrackingPlayers(entity, new ClientboundSetEntityLinkPacket(entity, (Entity)null));
            }

            leashData.leashHolder.notifyLeasheeRemoved((Leashable)entity);
         }
      }

   }

   static void tickLeash(final ServerLevel level, final Entity entity) {
      Leashable.LeashData leashData = ((Leashable)entity).getLeashData();
      if (leashData != null && leashData.delayedLeashInfo != null) {
         restoreLeashFromSave(entity, leashData);
      }

      if (leashData != null && leashData.leashHolder != null) {
         if (!entity.canInteractWithLevel() || !leashData.leashHolder.canInteractWithLevel()) {
            if (level.getGameRules().get(GameRules.ENTITY_DROPS)) {
               ((Leashable)entity).dropLeash();
            } else {
               ((Leashable)entity).removeLeash();
            }
         }

         Entity leashHolder = ((Leashable)entity).getLeashHolder();
         if (leashHolder != null && leashHolder.level() == entity.level()) {
            double distanceTo = ((Leashable)entity).leashDistanceTo(leashHolder);
            ((Leashable)entity).whenLeashedTo(leashHolder);
            if (distanceTo > ((Leashable)entity).leashSnapDistance()) {
               level.playSound((Entity)null, leashHolder.getX(), leashHolder.getY(), leashHolder.getZ(), SoundEvents.LEAD_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
               ((Leashable)entity).leashTooFarBehaviour();
            } else if (distanceTo > ((Leashable)entity).leashElasticDistance() - (double)leashHolder.getBbWidth() - (double)entity.getBbWidth() && ((Leashable)entity).checkElasticInteractions(leashHolder, leashData)) {
               ((Leashable)entity).onElasticLeashPull();
            } else {
               ((Leashable)entity).closeRangeLeashBehaviour(leashHolder);
            }

            entity.setYRot((float)((double)entity.getYRot() - leashData.angularMomentum));
            leashData.angularMomentum *= (double)angularFriction(entity);
         }

      }
   }

   default void onElasticLeashPull() {
      Entity entity = (Entity)this;
      entity.checkFallDistanceAccumulation();
   }

   default double leashSnapDistance() {
      return 12.0D;
   }

   default double leashElasticDistance() {
      return 6.0D;
   }

   static float angularFriction(final Entity entity) {
      if (entity.onGround()) {
         return entity.level().getBlockState(entity.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction() * 0.91F;
      } else {
         return entity.isInLiquid() ? 0.8F : 0.91F;
      }
   }

   default void whenLeashedTo(final Entity leashHolder) {
      leashHolder.notifyLeashHolder(this);
   }

   default void leashTooFarBehaviour() {
      this.dropLeash();
   }

   default void closeRangeLeashBehaviour(final Entity leashHolder) {
   }

   default boolean checkElasticInteractions(final Entity leashHolder, final Leashable.LeashData leashData) {
      boolean quadConnection = leashHolder.supportQuadLeashAsHolder() && this.supportQuadLeash();
      List wrenches = computeElasticInteraction((Entity)this, leashHolder, quadConnection ? SHARED_QUAD_ATTACHMENT_POINTS : ENTITY_ATTACHMENT_POINT, quadConnection ? SHARED_QUAD_ATTACHMENT_POINTS : LEASHER_ATTACHMENT_POINT);
      if (wrenches.isEmpty()) {
         return false;
      } else {
         Leashable.Wrench result = Leashable.Wrench.accumulate(wrenches).scale(quadConnection ? 0.25D : 1.0D);
         leashData.angularMomentum += 10.0D * result.torque();
         Vec3 relativeVelocityToLeasher = getHolderMovement(leashHolder).subtract(((Entity)this).getKnownMovement());
         ((Entity)this).addDeltaMovement(result.force().multiply(AXIS_SPECIFIC_ELASTICITY).add(relativeVelocityToLeasher.scale(0.11D)));
         return true;
      }
   }

   private static Vec3 getHolderMovement(final Entity leashHolder) {
      if (leashHolder instanceof Mob mob) {
         if (mob.isNoAi()) {
            return Vec3.ZERO;
         }
      }

      return leashHolder.getKnownMovement();
   }

   private static List computeElasticInteraction(final Entity entity, final Entity leashHolder, final List entityAttachmentPoints, final List leasherAttachmentPoints) {
      double slackDistance = ((Leashable)entity).leashElasticDistance();
      Vec3 currentMovement = getHolderMovement(entity);
      float entityYRot = entity.getYRot() * ((float)Math.PI / 180F);
      Vec3 entityDimensions = new Vec3((double)entity.getBbWidth(), (double)entity.getBbHeight(), (double)entity.getBbWidth());
      float leashHolderYRot = leashHolder.getYRot() * ((float)Math.PI / 180F);
      Vec3 leasherDimensions = new Vec3((double)leashHolder.getBbWidth(), (double)leashHolder.getBbHeight(), (double)leashHolder.getBbWidth());
      List wrenches = new ArrayList();

      for(int i = 0; i < entityAttachmentPoints.size(); ++i) {
         Vec3 entityAttachVector = ((Vec3)entityAttachmentPoints.get(i)).multiply(entityDimensions).yRot(-entityYRot);
         Vec3 entityAttachPos = entity.position().add(entityAttachVector);
         Vec3 leasherAttachVector = ((Vec3)leasherAttachmentPoints.get(i)).multiply(leasherDimensions).yRot(-leashHolderYRot);
         Vec3 leasherAttachPos = leashHolder.position().add(leasherAttachVector);
         computeDampenedSpringInteraction(leasherAttachPos, entityAttachPos, slackDistance, currentMovement, entityAttachVector).ifPresent(wrenches::add);
      }

      return wrenches;
   }

   private static Optional computeDampenedSpringInteraction(final Vec3 pivotPoint, final Vec3 objectPosition, final double springSlack, final Vec3 objectMotion, final Vec3 leverArm) {
      double distance = objectPosition.distanceTo(pivotPoint);
      if (distance < springSlack) {
         return Optional.empty();
      } else {
         Vec3 displacement = pivotPoint.subtract(objectPosition).normalize().scale(distance - springSlack);
         double torque = Leashable.Wrench.torqueFromForce(leverArm, displacement);
         boolean sameDirectionToMovement = objectMotion.dot(displacement) >= 0.0D;
         if (sameDirectionToMovement) {
            displacement = displacement.scale((double)0.3F);
         }

         return Optional.of(new Leashable.Wrench(displacement, torque));
      }
   }

   default boolean supportQuadLeash() {
      return false;
   }

   default Vec3[] getQuadLeashOffsets() {
      return createQuadLeashOffsets((Entity)this, 0.0D, 0.5D, 0.5D, 0.5D);
   }

   static Vec3[] createQuadLeashOffsets(final Entity entity, final double frontOffset, final double frontBack, final double leftRight, final double height) {
      float width = entity.getBbWidth();
      double frontOffsetScaled = frontOffset * (double)width;
      double frontBackScaled = frontBack * (double)width;
      double leftRightScaled = leftRight * (double)width;
      double heightScaled = height * (double)entity.getBbHeight();
      return new Vec3[]{new Vec3(-leftRightScaled, heightScaled, frontBackScaled + frontOffsetScaled), new Vec3(-leftRightScaled, heightScaled, -frontBackScaled + frontOffsetScaled), new Vec3(leftRightScaled, heightScaled, -frontBackScaled + frontOffsetScaled), new Vec3(leftRightScaled, heightScaled, frontBackScaled + frontOffsetScaled)};
   }

   default Vec3 getLeashOffset(final float partialTicks) {
      return this.getLeashOffset();
   }

   default Vec3 getLeashOffset() {
      Entity entity = (Entity)this;
      return new Vec3(0.0D, (double)entity.getEyeHeight(), (double)(entity.getBbWidth() * 0.4F));
   }

   default void setLeashedTo(final Entity holder, final boolean synch) {
      if (this != holder) {
         setLeashedTo((Entity)this, holder, synch);
      }
   }

   private static void setLeashedTo(final Entity entity, final Entity holder, final boolean synch) {
      Leashable.LeashData leashData = ((Leashable)entity).getLeashData();
      if (leashData == null) {
         leashData = new Leashable.LeashData(holder);
         ((Leashable)entity).setLeashData(leashData);
      } else {
         Entity oldHolder = leashData.leashHolder;
         leashData.setLeashHolder(holder);
         if (oldHolder != null && oldHolder != holder) {
            oldHolder.notifyLeasheeRemoved((Leashable)entity);
         }
      }

      if (synch) {
         Level var5 = entity.level();
         if (var5 instanceof ServerLevel) {
            ServerLevel level = (ServerLevel)var5;
            level.getChunkSource().sendToTrackingPlayers(entity, new ClientboundSetEntityLinkPacket(entity, holder));
         }
      }

      if (entity.isPassenger()) {
         entity.stopRiding();
      }

   }

   default @Nullable Entity getLeashHolder() {
      return getLeashHolder((Entity)this);
   }

   private static @Nullable Entity getLeashHolder(final Entity entity) {
      Leashable.LeashData leashData = ((Leashable)entity).getLeashData();
      if (leashData == null) {
         return null;
      } else {
         Entity ntt = entity.level().getEntity(leashData.delayedLeashHolderId);
         if (leashData.delayedLeashHolderId != 0 && entity.level().isClientSide() && ntt != null) {
            leashData.setLeashHolder(ntt);
         }

         return leashData.leashHolder;
      }
   }

   static List leashableLeashedTo(final Entity entity) {
      return leashableInArea(entity, (l) -> l.getLeashHolder() == entity);
   }

   static List leashableInArea(final Entity entity, final Predicate test) {
      return leashableInArea(entity.level(), entity.getBoundingBox().getCenter(), test);
   }

   static List leashableInArea(final Level level, final Vec3 pos, final Predicate test) {
      double size = 32.0D;
      AABB scanArea = AABB.ofSize(pos, 32.0D, 32.0D, 32.0D);
      return level.getEntitiesOfClass(Entity.class, scanArea, (e) -> {
         if (e instanceof Leashable leashable) {
            if (test.test(leashable)) {
               return true;
            }
         }

         return false;
      }).stream().map(Leashable.class::cast).toList();
   }

   public static final class LeashData {
      public static final Codec CODEC = Codec.xor(UUIDUtil.CODEC.fieldOf("UUID").codec(), BlockPos.CODEC).xmap(Leashable.LeashData::new, (data) -> {
         Entity patt0$temp = data.leashHolder;
         if (patt0$temp instanceof LeashFenceKnotEntity leashKnot) {
            return Either.right(leashKnot.getPos());
         } else {
            return data.leashHolder != null ? Either.left(data.leashHolder.getUUID()) : (Either)Objects.requireNonNull(data.delayedLeashInfo, "Invalid LeashData had no attachment");
         }
      });
      private int delayedLeashHolderId;
      public @Nullable Entity leashHolder;
      public @Nullable Either delayedLeashInfo;
      public double angularMomentum;

      private LeashData(final Either delayedLeashInfo) {
         this.delayedLeashInfo = delayedLeashInfo;
      }

      private LeashData(final Entity entity) {
         this.leashHolder = entity;
      }

      private LeashData(final int entityId) {
         this.delayedLeashHolderId = entityId;
      }

      public void setLeashHolder(final Entity leashHolder) {
         this.leashHolder = leashHolder;
         this.delayedLeashInfo = null;
         this.delayedLeashHolderId = 0;
      }
   }

   public static record Wrench(Vec3 force, double torque) {
      public static final Leashable.Wrench ZERO = new Leashable.Wrench(Vec3.ZERO, 0.0D);

      public static double torqueFromForce(final Vec3 leverArm, final Vec3 force) {
         return leverArm.z * force.x - leverArm.x * force.z;
      }

      public static Leashable.Wrench accumulate(final List wrenches) {
         if (wrenches.isEmpty()) {
            return ZERO;
         } else {
            double x = 0.0D;
            double y = 0.0D;
            double z = 0.0D;
            double t = 0.0D;

            for(Leashable.Wrench wrench : wrenches) {
               Vec3 force = wrench.force;
               x += force.x;
               y += force.y;
               z += force.z;
               t += wrench.torque;
            }

            return new Leashable.Wrench(new Vec3(x, y, z), t);
         }
      }

      public Leashable.Wrench scale(final double scale) {
         return new Leashable.Wrench(this.force.scale(scale), this.torque * scale);
      }
   }
}
