package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.DistancePredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

public class LevitationTrigger extends SimpleCriterionTrigger {
   public Codec codec() {
      return LevitationTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Vec3 start, final int duration) {
      this.trigger(player, (t) -> t.matches(player, start, duration));
   }

   public static record TriggerInstance(Optional player, Optional distance, MinMaxBounds.Ints duration) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(LevitationTrigger.TriggerInstance::player), DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(LevitationTrigger.TriggerInstance::distance), MinMaxBounds.Ints.CODEC.optionalFieldOf("duration", MinMaxBounds.Ints.ANY).forGetter(LevitationTrigger.TriggerInstance::duration)).apply(i, LevitationTrigger.TriggerInstance::new));

      public static Criterion levitated(final DistancePredicate distance) {
         return CriteriaTriggers.LEVITATION.createCriterion(new LevitationTrigger.TriggerInstance(Optional.empty(), Optional.of(distance), MinMaxBounds.Ints.ANY));
      }

      public boolean matches(final ServerPlayer player, final Vec3 start, final int duration) {
         if (this.distance.isPresent() && !((DistancePredicate)this.distance.get()).matches(start.x, start.y, start.z, player.getX(), player.getY(), player.getZ())) {
            return false;
         } else {
            return this.duration.matches(duration);
         }
      }
   }
}
