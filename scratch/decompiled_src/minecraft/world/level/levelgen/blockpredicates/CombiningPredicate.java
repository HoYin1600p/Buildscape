package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;

public abstract class CombiningPredicate implements BlockPredicate {
   protected final List predicates;

   protected CombiningPredicate(final List predicates) {
      this.predicates = predicates;
   }

   public static MapCodec codec(final Function constructor) {
      return RecordCodecBuilder.mapCodec((i) -> i.group(BlockPredicate.CODEC.listOf().fieldOf("predicates").forGetter((p) -> p.predicates)).apply(i, constructor));
   }
}
