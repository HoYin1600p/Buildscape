package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.CriterionTriggerInstance;

public interface CriterionTrigger {
   Codec codec();

   default Criterion createCriterion(final CriterionTriggerInstance instance) {
      return new Criterion(this, instance);
   }
}
