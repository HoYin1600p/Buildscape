package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.util.ExtraCodecs;

public record Criterion(CriterionTrigger trigger, CriterionTriggerInstance triggerInstance) {
   private static final MapCodec MAP_CODEC = ExtraCodecs.dispatchOptionalValue("trigger", "conditions", CriteriaTriggers.CODEC, Criterion::trigger, Criterion::criterionCodec);
   public static final Codec CODEC = MAP_CODEC.codec();

   private static Codec criterionCodec(final CriterionTrigger trigger) {
      return trigger.codec().xmap((instance) -> new Criterion(trigger, instance), Criterion::triggerInstance);
   }
}
