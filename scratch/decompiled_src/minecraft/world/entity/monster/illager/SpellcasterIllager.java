package net.minecraft.world.entity.monster.illager;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.IntFunction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public abstract class SpellcasterIllager extends AbstractIllager {
   private static final EntityDataAccessor DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(SpellcasterIllager.class, EntityDataSerializers.BYTE);
   private static final int DEFAULT_SPELLCASTING_TICKS = 0;
   protected int spellCastingTickCount = 0;
   private SpellcasterIllager.IllagerSpell currentSpell = SpellcasterIllager.IllagerSpell.NONE;

   protected SpellcasterIllager(final EntityType type, final Level level) {
      super(type, level);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_SPELL_CASTING_ID, (byte)0);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.spellCastingTickCount = input.getIntOr("SpellTicks", 0);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putInt("SpellTicks", this.spellCastingTickCount);
   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isCastingSpell()) {
         return AbstractIllager.IllagerArmPose.SPELLCASTING;
      } else {
         return this.isCelebrating() ? AbstractIllager.IllagerArmPose.CELEBRATING : AbstractIllager.IllagerArmPose.CROSSED;
      }
   }

   public boolean isCastingSpell() {
      if (this.level().isClientSide()) {
         return this.entityData.get(DATA_SPELL_CASTING_ID) > 0;
      } else {
         return this.spellCastingTickCount > 0;
      }
   }

   public void setIsCastingSpell(final SpellcasterIllager.IllagerSpell spell) {
      this.currentSpell = spell;
      this.entityData.set(DATA_SPELL_CASTING_ID, (byte)spell.id);
   }

   protected SpellcasterIllager.IllagerSpell getCurrentSpell() {
      return !this.level().isClientSide() ? this.currentSpell : SpellcasterIllager.IllagerSpell.byId(this.entityData.get(DATA_SPELL_CASTING_ID));
   }

   protected void customServerAiStep(final ServerLevel level) {
      super.customServerAiStep(level);
      if (this.spellCastingTickCount > 0) {
         --this.spellCastingTickCount;
      }

   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide() && this.isCastingSpell()) {
         SpellcasterIllager.IllagerSpell spell = this.getCurrentSpell();
         float red = (float)spell.spellColor[0];
         float green = (float)spell.spellColor[1];
         float blue = (float)spell.spellColor[2];
         float bodyAngle = this.yBodyRot * ((float)Math.PI / 180F) + Mth.cos((double)((float)this.tickCount * 0.6662F)) * 0.25F;
         float cos = Mth.cos((double)bodyAngle);
         float sin = Mth.sin((double)bodyAngle);
         double handDistance = 0.6D * (double)this.getScale();
         double handHeight = 1.8D * (double)this.getScale();
         this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, red, green, blue), this.getX() + (double)cos * handDistance, this.getY() + handHeight, this.getZ() + (double)sin * handDistance, 0.0D, 0.0D, 0.0D);
         this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, red, green, blue), this.getX() - (double)cos * handDistance, this.getY() + handHeight, this.getZ() - (double)sin * handDistance, 0.0D, 0.0D, 0.0D);
      }

   }

   protected int getSpellCastingTime() {
      return this.spellCastingTickCount;
   }

   protected abstract SoundEvent getCastingSoundEvent();

   protected static enum IllagerSpell {
      NONE(0, 0.0D, 0.0D, 0.0D),
      SUMMON_VEX(1, 0.7D, 0.7D, 0.8D),
      FANGS(2, 0.4D, 0.3D, 0.35D),
      WOLOLO(3, 0.7D, 0.5D, 0.2D),
      DISAPPEAR(4, 0.3D, 0.3D, 0.8D),
      BLINDNESS(5, 0.1D, 0.1D, 0.2D);

      private static final IntFunction BY_ID = ByIdMap.continuous((e) -> e.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      private final int id;
      private final double[] spellColor;

      private IllagerSpell(final int id, final double red, final double green, final double blue) {
         this.id = id;
         this.spellColor = new double[]{red, green, blue};
      }

      public static SpellcasterIllager.IllagerSpell byId(final int id) {
         return (SpellcasterIllager.IllagerSpell)BY_ID.apply(id);
      }

      // $FF: synthetic method
      private static SpellcasterIllager.IllagerSpell[] $values() {
         return new SpellcasterIllager.IllagerSpell[]{NONE, SUMMON_VEX, FANGS, WOLOLO, DISAPPEAR, BLINDNESS};
      }
   }

   protected class SpellcasterCastingSpellGoal extends Goal {
      public SpellcasterCastingSpellGoal() {
         Objects.requireNonNull(SpellcasterIllager.this);
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return SpellcasterIllager.this.getSpellCastingTime() > 0;
      }

      public void start() {
         super.start();
         SpellcasterIllager.this.navigation.stop();
      }

      public void stop() {
         super.stop();
         SpellcasterIllager.this.setIsCastingSpell(SpellcasterIllager.IllagerSpell.NONE);
      }

      public void tick() {
         if (SpellcasterIllager.this.getTarget() != null) {
            SpellcasterIllager.this.getLookControl().setLookAt(SpellcasterIllager.this.getTarget(), (float)SpellcasterIllager.this.getMaxHeadYRot(), (float)SpellcasterIllager.this.getMaxHeadXRot());
         }

      }
   }

   protected abstract class SpellcasterUseSpellGoal extends Goal {
      protected int attackWarmupDelay;
      protected int nextAttackTickCount;

      protected SpellcasterUseSpellGoal() {
         Objects.requireNonNull(SpellcasterIllager.this);
         super();
      }

      public boolean canUse() {
         LivingEntity target = SpellcasterIllager.this.getTarget();
         if (target != null && target.isAlive()) {
            if (SpellcasterIllager.this.isCastingSpell()) {
               return false;
            } else {
               return SpellcasterIllager.this.tickCount >= this.nextAttackTickCount;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         LivingEntity target = SpellcasterIllager.this.getTarget();
         return target != null && target.isAlive() && this.attackWarmupDelay > 0;
      }

      public void start() {
         this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
         SpellcasterIllager.this.spellCastingTickCount = this.getCastingTime();
         this.nextAttackTickCount = SpellcasterIllager.this.tickCount + this.getCastingInterval();
         SoundEvent spellPrepareSound = this.getSpellPrepareSound();
         if (spellPrepareSound != null) {
            SpellcasterIllager.this.playSound(spellPrepareSound, 1.0F, 1.0F);
         }

         SpellcasterIllager.this.setIsCastingSpell(this.getSpell());
      }

      public void tick() {
         --this.attackWarmupDelay;
         if (this.attackWarmupDelay == 0) {
            this.performSpellCasting();
            SpellcasterIllager.this.playSound(SpellcasterIllager.this.getCastingSoundEvent(), 1.0F, 1.0F);
         }

      }

      protected abstract void performSpellCasting();

      protected int getCastWarmupTime() {
         return 20;
      }

      protected abstract int getCastingTime();

      protected abstract int getCastingInterval();

      protected abstract @Nullable SoundEvent getSpellPrepareSound();

      protected abstract SpellcasterIllager.IllagerSpell getSpell();
   }
}
