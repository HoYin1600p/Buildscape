package net.minecraft.advancements.predicates;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;

public record CollectionPredicate(Optional contains, Optional counts, Optional size) implements Predicate {
   public static Codec codec(final Codec elementCodec) {
      return RecordCodecBuilder.create((i) -> i.group(CollectionContentsPredicate.codec(elementCodec).optionalFieldOf("contains").forGetter(CollectionPredicate::contains), CollectionCountsPredicate.codec(elementCodec).optionalFieldOf("count").forGetter(CollectionPredicate::counts), MinMaxBounds.Ints.CODEC.optionalFieldOf("size").forGetter(CollectionPredicate::size)).apply(i, CollectionPredicate::new));
   }

   public boolean test(final Iterable value) {
      if (this.contains.isPresent() && !((CollectionContentsPredicate)this.contains.get()).test(value)) {
         return false;
      } else if (this.counts.isPresent() && !((CollectionCountsPredicate)this.counts.get()).test(value)) {
         return false;
      } else {
         return !this.size.isPresent() || ((MinMaxBounds.Ints)this.size.get()).matches(Iterables.size(value));
      }
   }
}
