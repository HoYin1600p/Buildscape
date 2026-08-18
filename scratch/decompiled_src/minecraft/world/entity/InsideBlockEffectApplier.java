package net.minecraft.world.entity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.util.Util;

public interface InsideBlockEffectApplier {
   InsideBlockEffectApplier NOOP = new InsideBlockEffectApplier() {
      public void apply(final InsideBlockEffectType type) {
      }

      public void runBefore(final InsideBlockEffectType type, final Consumer effect) {
      }

      public void runAfter(final InsideBlockEffectType type, final Consumer effect) {
      }
   };

   void apply(InsideBlockEffectType type);

   void runBefore(InsideBlockEffectType type, Consumer effect);

   void runAfter(InsideBlockEffectType type, Consumer effect);

   public static class StepBasedCollector implements InsideBlockEffectApplier {
      private static final InsideBlockEffectType[] APPLY_ORDER = InsideBlockEffectType.values();
      private static final int NO_STEP = -1;
      private final Set effectsInStep = EnumSet.noneOf(InsideBlockEffectType.class);
      private final Map beforeEffectsInStep = Util.makeEnumMap(InsideBlockEffectType.class, (type) -> new ArrayList());
      private final Map afterEffectsInStep = Util.makeEnumMap(InsideBlockEffectType.class, (type) -> new ArrayList());
      private final List finalEffects = new ArrayList();
      private int lastStep = -1;

      public void advanceStep(final int step) {
         if (this.lastStep != step) {
            this.lastStep = step;
            this.flushStep();
         }

      }

      public void applyAndClear(final Entity entity) {
         this.flushStep();

         for(Consumer effect : this.finalEffects) {
            if (!entity.isAlive()) {
               break;
            }

            effect.accept(entity);
         }

         this.finalEffects.clear();
         this.lastStep = -1;
      }

      private void flushStep() {
         for(InsideBlockEffectType type : APPLY_ORDER) {
            List beforeEffects = (List)this.beforeEffectsInStep.get(type);
            this.finalEffects.addAll(beforeEffects);
            beforeEffects.clear();
            if (this.effectsInStep.remove(type)) {
               this.finalEffects.add(type.effect());
            }

            List afterEffects = (List)this.afterEffectsInStep.get(type);
            this.finalEffects.addAll(afterEffects);
            afterEffects.clear();
         }

      }

      public void apply(final InsideBlockEffectType type) {
         this.effectsInStep.add(type);
      }

      public void runBefore(final InsideBlockEffectType type, final Consumer effect) {
         ((List)this.beforeEffectsInStep.get(type)).add(effect);
      }

      public void runAfter(final InsideBlockEffectType type, final Consumer effect) {
         ((List)this.afterEffectsInStep.get(type)).add(effect);
      }
   }
}
