package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ExplosionCondition implements LootItemCondition {
   private static final ExplosionCondition INSTANCE = new ExplosionCondition();
   public static final MapCodec MAP_CODEC = MapCodec.unit(INSTANCE);

   private ExplosionCondition() {
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Set getReferencedContextParams() {
      return Set.of(LootContextParams.EXPLOSION_RADIUS);
   }

   public boolean test(final LootContext context) {
      Float explosionRadius = (Float)context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
      if (explosionRadius != null) {
         RandomSource random = context.getRandom();
         float probability = 1.0F / explosionRadius;
         return random.nextFloat() <= probability;
      } else {
         return true;
      }
   }

   public static LootItemCondition.Builder survivesExplosion() {
      return () -> INSTANCE;
   }
}
