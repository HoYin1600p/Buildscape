package net.minecraft.world.entity.animal.equine;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class Horse extends AbstractHorse {
   private static final EntityDataAccessor DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(Horse.class, EntityDataSerializers.INT);
   private static final EntityDimensions BABY_DIMENSIONS = EntityTypes.HORSE.getDimensions().withAttachments(EntityAttachments.builder().attach(EntityAttachment.PASSENGER, 0.0F, EntityTypes.HORSE.getHeight() - 0.125F, 0.0F)).scale(0.7F);
   private static final int DEFAULT_VARIANT = 0;

   public Horse(final EntityType type, final Level level) {
      super(type, level);
   }

   protected void randomizeAttributes(final RandomSource random) {
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)generateMaxHealth(random::nextInt));
      this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(generateSpeed(random::nextDouble));
      this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(generateJumpStrength(random::nextDouble));
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_ID_TYPE_VARIANT, 0);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putInt("Variant", this.getTypeVariant());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setTypeVariant(input.getIntOr("Variant", 0));
   }

   private void setTypeVariant(final int i) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, i);
   }

   private int getTypeVariant() {
      return this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   private void setVariantAndMarkings(final Variant variant, final Markings markings) {
      this.setTypeVariant(variant.getId() & 255 | markings.getId() << 8 & '\uff00');
   }

   public Variant getVariant() {
      return Variant.byId(this.getTypeVariant() & 255);
   }

   private void setVariant(final Variant variant) {
      this.setTypeVariant(variant.getId() & 255 | this.getTypeVariant() & -256);
   }

   public @Nullable Object get(final DataComponentType type) {
      return type == DataComponents.HORSE_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type);
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      this.applyImplicitComponentIfPresent(components, DataComponents.HORSE_VARIANT);
      super.applyImplicitComponents(components);
   }

   protected boolean applyImplicitComponent(final DataComponentType type, final Object value) {
      if (type == DataComponents.HORSE_VARIANT) {
         this.setVariant((Variant)castComponentValue(DataComponents.HORSE_VARIANT, value));
         return true;
      } else {
         return super.applyImplicitComponent(type, value);
      }
   }

   public Markings getMarkings() {
      return Markings.byId((this.getTypeVariant() & '\uff00') >> 8);
   }

   protected void playGallopSound(final SoundType soundType) {
      super.playGallopSound(soundType);
      if (this.random.nextInt(10) == 0) {
         this.playSound(this.isBaby() ? SoundEvents.HORSE_BREATHE_BABY : SoundEvents.HORSE_BREATHE, soundType.getVolume() * 0.6F, soundType.getPitch());
      }

   }

   protected SoundEvent getAmbientSound() {
      return this.isBaby() ? SoundEvents.HORSE_AMBIENT_BABY : SoundEvents.HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return this.isBaby() ? SoundEvents.HORSE_DEATH_BABY : SoundEvents.HORSE_DEATH;
   }

   protected SoundEvent getEatingSound() {
      return this.isBaby() ? SoundEvents.HORSE_EAT_BABY : SoundEvents.HORSE_EAT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return this.isBaby() ? SoundEvents.HORSE_HURT_BABY : SoundEvents.HORSE_HURT;
   }

   protected SoundEvent getAngrySound() {
      return this.isBaby() ? SoundEvents.HORSE_ANGRY_BABY : SoundEvents.HORSE_ANGRY;
   }

   public InteractionResult mobInteract(final Player player, final InteractionHand hand) {
      boolean shouldOpenInventory = !this.isBaby() && this.isTamed() && player.isSecondaryUseActive();
      if (!this.isVehicle() && !shouldOpenInventory && (!this.isBaby() || !player.isHolding(Items.GOLDEN_DANDELION))) {
         ItemStack itemStack = player.getItemInHand(hand);
         if (!itemStack.isEmpty()) {
            if (this.isFood(itemStack)) {
               return this.fedFood(player, itemStack);
            }

            if (!this.isTamed()) {
               this.makeMad();
               return InteractionResult.SUCCESS;
            }
         }

         return super.mobInteract(player, hand);
      } else {
         return super.mobInteract(player, hand);
      }
   }

   public boolean canMate(final Animal partner) {
      if (partner == this) {
         return false;
      } else if (!(partner instanceof Donkey) && !(partner instanceof Horse)) {
         return false;
      } else {
         return this.canParent() && ((AbstractHorse)partner).canParent();
      }
   }

   public @Nullable AgeableMob getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      if (partner instanceof Donkey) {
         Mule baby = (Mule)EntityTypes.MULE.create(level, EntitySpawnReason.BREEDING);
         if (baby != null) {
            this.setOffspringAttributes(partner, baby);
         }

         return baby;
      } else {
         Horse horsePartner = (Horse)partner;
         Horse baby = (Horse)EntityTypes.HORSE.create(level, EntitySpawnReason.BREEDING);
         if (baby != null) {
            int selectSkin = this.random.nextInt(9);
            Variant variant;
            if (selectSkin < 4) {
               variant = this.getVariant();
            } else if (selectSkin < 8) {
               variant = horsePartner.getVariant();
            } else {
               variant = (Variant)Util.getRandom((Object[])Variant.values(), this.random);
            }

            int selectMarking = this.random.nextInt(5);
            Markings markings;
            if (selectMarking < 2) {
               markings = this.getMarkings();
            } else if (selectMarking < 4) {
               markings = horsePartner.getMarkings();
            } else {
               markings = (Markings)Util.getRandom((Object[])Markings.values(), this.random);
            }

            baby.setVariantAndMarkings(variant, markings);
            this.setOffspringAttributes(partner, baby);
         }

         return baby;
      }
   }

   public boolean canUseSlot(final EquipmentSlot slot) {
      return true;
   }

   protected void hurtArmor(final DamageSource damageSource, final float damage) {
      this.doHurtEquipment(damageSource, damage, new EquipmentSlot[]{EquipmentSlot.BODY});
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
      RandomSource random = level.getRandom();
      Variant variant;
      if (groupData instanceof Horse.HorseGroupData) {
         variant = ((Horse.HorseGroupData)groupData).variant;
      } else {
         variant = (Variant)Util.getRandom((Object[])Variant.values(), random);
         groupData = new Horse.HorseGroupData(variant);
      }

      this.setVariantAndMarkings(variant, (Markings)Util.getRandom((Object[])Markings.values(), random));
      return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
   }

   public static class HorseGroupData extends AgeableMob.AgeableMobGroupData {
      public final Variant variant;

      public HorseGroupData(final Variant variant) {
         super(true);
         this.variant = variant;
      }
   }
}
