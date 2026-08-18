package net.minecraft.world.entity.animal.equine;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LlamaFollowCaravanGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Llama extends AbstractChestedHorse implements RangedAttackMob {
   private static final int MAX_STRENGTH = 5;
   private static final EntityDataAccessor DATA_STRENGTH_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor DATA_VARIANT_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
   private static final EntityDimensions BABY_DIMENSIONS = EntityTypes.LLAMA.getDimensions().withAttachments(EntityAttachments.builder().attach(EntityAttachment.PASSENGER, 0.0F, EntityTypes.LLAMA.getHeight() - 0.25F, -0.3F)).scale(0.5F);
   private boolean didSpit;
   private @Nullable Llama caravanHead;
   private @Nullable Llama caravanTail;

   public Llama(final EntityType type, final Level level) {
      super(type, level);
      this.getNavigation().setRequiredPathLength(40.0F);
   }

   public boolean isTraderLlama() {
      return false;
   }

   private void setStrength(final int strength) {
      this.entityData.set(DATA_STRENGTH_ID, Math.max(1, Math.min(5, strength)));
   }

   private void setRandomStrength(final RandomSource random) {
      int maxStrength = random.nextFloat() < 0.04F ? 5 : 3;
      this.setStrength(1 + random.nextInt(maxStrength));
   }

   public int getStrength() {
      return this.entityData.get(DATA_STRENGTH_ID);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.store("Variant", Llama.Variant.LEGACY_CODEC, this.getVariant());
      output.putInt("Strength", this.getStrength());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      this.setStrength(input.getIntOr("Strength", 0));
      super.readAdditionalSaveData(input);
      this.setVariant((Llama.Variant)input.read("Variant", Llama.Variant.LEGACY_CODEC).orElse(Llama.Variant.DEFAULT));
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
      this.goalSelector.addGoal(2, new LlamaFollowCaravanGoal(this, (double)2.1F));
      this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25D, 40, 20.0F));
      this.goalSelector.addGoal(3, new PanicGoal(this, 1.2D));
      this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(5, new TemptGoal(this, 1.25D, (i) -> i.is(ItemTags.LLAMA_TEMPT_ITEMS), false));
      this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.7D));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new Llama.LlamaHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new Llama.LlamaAttackWolfGoal(this));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return createBaseChestedHorseAttributes();
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_STRENGTH_ID, 0);
      entityData.define(DATA_VARIANT_ID, 0);
   }

   public Llama.Variant getVariant() {
      return Llama.Variant.byId(this.entityData.get(DATA_VARIANT_ID));
   }

   private void setVariant(final Llama.Variant variant) {
      this.entityData.set(DATA_VARIANT_ID, variant.id);
   }

   public @Nullable Object get(final DataComponentType type) {
      return type == DataComponents.LLAMA_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type);
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      this.applyImplicitComponentIfPresent(components, DataComponents.LLAMA_VARIANT);
      super.applyImplicitComponents(components);
   }

   protected boolean applyImplicitComponent(final DataComponentType type, final Object value) {
      if (type == DataComponents.LLAMA_VARIANT) {
         this.setVariant((Llama.Variant)castComponentValue(DataComponents.LLAMA_VARIANT, value));
         return true;
      } else {
         return super.applyImplicitComponent(type, value);
      }
   }

   public boolean isFood(final ItemStack itemStack) {
      return itemStack.is(ItemTags.LLAMA_FOOD);
   }

   protected boolean handleEating(final Player player, final ItemStack itemStack) {
      int ageUp = 0;
      int temper = 0;
      float heal = 0.0F;
      boolean itemUsed = false;
      if (itemStack.is(Items.WHEAT)) {
         ageUp = 10;
         temper = 3;
         heal = 2.0F;
      } else if (itemStack.is(Items.HAY_BLOCK)) {
         ageUp = 90;
         temper = 6;
         heal = 10.0F;
         if (this.isTamed() && this.getAge() == 0 && this.canFallInLove()) {
            itemUsed = true;
            this.setInLove(player);
         }
      }

      if (this.getHealth() < this.getMaxHealth() && heal > 0.0F) {
         this.heal(heal);
         itemUsed = true;
      }

      if (this.isBaby() && ageUp > 0 && !this.isAgeLocked()) {
         this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
         if (!this.level().isClientSide()) {
            this.ageUp(ageUp);
            itemUsed = true;
         }
      }

      if (temper > 0 && (itemUsed || !this.isTamed()) && this.getTemper() < this.getMaxTemper() && !this.level().isClientSide()) {
         this.modifyTemper(temper);
         itemUsed = true;
      }

      if (itemUsed && !this.isSilent()) {
         SoundEvent eatingSound = this.getEatingSound();
         if (eatingSound != null) {
            this.level().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), this.getEatingSound(), this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
         }
      }

      return itemUsed;
   }

   public boolean isImmobile() {
      return this.isDeadOrDying() || this.isEating();
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
      RandomSource random = level.getRandom();
      this.setRandomStrength(random);
      Llama.Variant variant;
      if (groupData instanceof Llama.LlamaGroupData) {
         variant = ((Llama.LlamaGroupData)groupData).variant;
      } else {
         variant = (Llama.Variant)Util.getRandom((Object[])Llama.Variant.values(), random);
         groupData = new Llama.LlamaGroupData(variant);
      }

      this.setVariant(variant);
      return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
   }

   protected boolean canPerformRearing() {
      return false;
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.LLAMA_ANGRY;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.LLAMA_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.LLAMA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.LLAMA_DEATH;
   }

   protected SoundEvent getEatingSound() {
      return SoundEvents.LLAMA_EAT;
   }

   protected void playStepSound(final BlockPos pos, final BlockState blockState) {
      this.playSound(SoundEvents.LLAMA_STEP, 0.15F, 1.0F);
   }

   protected void playChestEquipsSound() {
      this.playSound(SoundEvents.LLAMA_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   public int getInventoryColumns() {
      return this.hasChest() ? this.getStrength() : 0;
   }

   public boolean canUseSlot(final EquipmentSlot slot) {
      return true;
   }

   public int getMaxTemper() {
      return 30;
   }

   public boolean canMate(final Animal partner) {
      if (partner != this && partner instanceof Llama llama) {
         if (this.canParent() && llama.canParent()) {
            return true;
         }
      }

      return false;
   }

   public @Nullable Llama getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      Llama baby = this.makeNewLlama();
      if (baby != null) {
         this.setOffspringAttributes(partner, baby);
         Llama otherLlama = (Llama)partner;
         int babyStrength = this.random.nextInt(Math.max(this.getStrength(), otherLlama.getStrength())) + 1;
         if (this.random.nextFloat() < 0.03F) {
            ++babyStrength;
         }

         baby.setStrength(babyStrength);
         baby.setVariant(this.random.nextBoolean() ? this.getVariant() : otherLlama.getVariant());
      }

      return baby;
   }

   protected @Nullable Llama makeNewLlama() {
      return (Llama)EntityTypes.LLAMA.create(this.level(), EntitySpawnReason.BREEDING);
   }

   private void spit(final LivingEntity target) {
      LlamaSpit spit = new LlamaSpit(this.level(), this);
      double xd = target.getX() - this.getX();
      double yd = target.getY(0.3333333333333333D) - spit.getY();
      double zd = target.getZ() - this.getZ();
      double yo = Math.sqrt(xd * xd + zd * zd) * (double)0.2F;
      Level var12 = this.level();
      if (var12 instanceof ServerLevel serverLevel) {
         Projectile.spawnProjectileUsingShoot(spit, serverLevel, ItemStack.EMPTY, xd, yd + yo, zd, 1.5F, this.rangedAttackUncertainty(serverLevel));
      }

      if (!this.isSilent()) {
         this.level().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
      }

      this.didSpit = true;
   }

   private void setDidSpit(final boolean b) {
      this.didSpit = b;
   }

   public boolean causeFallDamage(final double fallDistance, final float damageModifier, final DamageSource damageSource) {
      int dmg = this.calculateFallDamage(fallDistance, damageModifier);
      if (dmg <= 0) {
         return false;
      } else {
         if (fallDistance >= 6.0D) {
            this.hurt(damageSource, (float)dmg);
            this.propagateFallToPassengers(fallDistance, damageModifier, damageSource);
         }

         this.playBlockFallSound();
         return true;
      }
   }

   public void leaveCaravan() {
      if (this.caravanHead != null) {
         this.caravanHead.caravanTail = null;
      }

      this.caravanHead = null;
   }

   public void joinCaravan(final Llama tail) {
      this.caravanHead = tail;
      this.caravanHead.caravanTail = this;
   }

   public boolean hasCaravanTail() {
      return this.caravanTail != null;
   }

   public boolean inCaravan() {
      return this.caravanHead != null;
   }

   public @Nullable Llama getCaravanHead() {
      return this.caravanHead;
   }

   protected double followLeashSpeed() {
      return 2.0D;
   }

   public boolean supportQuadLeash() {
      return false;
   }

   protected void followMommy(final ServerLevel level) {
      if (!this.inCaravan() && this.isBaby()) {
         super.followMommy(level);
      }

   }

   public boolean canEatGrass() {
      return false;
   }

   public void performRangedAttack(final LivingEntity target, final float power) {
      this.spit(target);
   }

   public float rangedAttackUncertainty(final Level level) {
      return 10.0F;
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, 0.75D * (double)this.getEyeHeight(), (double)this.getBbWidth() * 0.5D);
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
   }

   protected Vec3 getPassengerAttachmentPoint(final Entity passenger, final EntityDimensions dimensions, final float scale) {
      return getDefaultPassengerAttachmentPoint(this, passenger, dimensions.attachments());
   }

   private static class LlamaAttackWolfGoal extends NearestAttackableTargetGoal {
      public LlamaAttackWolfGoal(final Llama llama) {
         super(llama, Wolf.class, 16, false, true, (target, level) -> !((Wolf)target).isTame());
      }

      protected double getFollowDistance() {
         return super.getFollowDistance() * 0.25D;
      }
   }

   private static class LlamaGroupData extends AgeableMob.AgeableMobGroupData {
      public final Llama.Variant variant;

      private LlamaGroupData(final Llama.Variant variant) {
         super(true);
         this.variant = variant;
      }
   }

   private static class LlamaHurtByTargetGoal extends HurtByTargetGoal {
      public LlamaHurtByTargetGoal(final Llama llama) {
         super(llama);
      }

      public boolean canContinueToUse() {
         Mob var2 = this.mob;
         if (var2 instanceof Llama llama) {
            if (llama.didSpit) {
               llama.setDidSpit(false);
               return false;
            }
         }

         return super.canContinueToUse();
      }
   }

   public static enum Variant implements StringRepresentable {
      CREAMY(0, "creamy"),
      WHITE(1, "white"),
      BROWN(2, "brown"),
      GRAY(3, "gray");

      public static final Llama.Variant DEFAULT = CREAMY;
      private static final IntFunction BY_ID = ByIdMap.continuous(Llama.Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
      public static final Codec CODEC = StringRepresentable.fromEnum(Llama.Variant::values);
      /** @deprecated */
      @Deprecated
      public static final Codec LEGACY_CODEC = Codec.INT.xmap(BY_ID::apply, Llama.Variant::getId);
      public static final StreamCodec STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Llama.Variant::getId);
      private final int id;
      private final String name;

      private Variant(final int id, final String name) {
         this.id = id;
         this.name = name;
      }

      public int getId() {
         return this.id;
      }

      public static Llama.Variant byId(final int id) {
         return (Llama.Variant)BY_ID.apply(id);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Llama.Variant[] $values() {
         return new Llama.Variant[]{CREAMY, WHITE, BROWN, GRAY};
      }
   }
}
