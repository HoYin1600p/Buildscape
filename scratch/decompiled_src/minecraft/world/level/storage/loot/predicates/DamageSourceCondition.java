package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.predicates.DamageSourcePredicate;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public record DamageSourceCondition(Optional predicate) implements LootItemCondition {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DamageSourcePredicate.CODEC.optionalFieldOf("predicate").forGetter(DamageSourceCondition::predicate)).apply(i, DamageSourceCondition::new));

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Set getReferencedContextParams() {
      return Set.of(LootContextParams.ORIGIN, LootContextParams.DAMAGE_SOURCE);
   }

   public boolean test(final LootContext context) {
      DamageSource damageSource = (DamageSource)context.getOptionalParameter(LootContextParams.DAMAGE_SOURCE);
      Vec3 pos = (Vec3)context.getOptionalParameter(LootContextParams.ORIGIN);
      if (pos != null && damageSource != null) {
         return this.predicate.isEmpty() || ((DamageSourcePredicate)this.predicate.get()).matches(context.getLevel(), pos, damageSource);
      } else {
         return false;
      }
   }

   public static LootItemCondition.Builder hasDamageSource(final DamageSourcePredicate.Builder builder) {
      return () -> new DamageSourceCondition(Optional.of(builder.build()));
   }
}
