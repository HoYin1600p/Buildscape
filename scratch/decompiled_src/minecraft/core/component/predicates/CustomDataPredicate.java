package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.predicates.NbtPredicate;
import net.minecraft.core.component.DataComponentGetter;

public record CustomDataPredicate(NbtPredicate value) implements DataComponentPredicate {
   public static final Codec CODEC = NbtPredicate.CODEC.xmap(CustomDataPredicate::new, CustomDataPredicate::value);

   public boolean matches(final DataComponentGetter components) {
      return this.value.matches(components);
   }

   public static CustomDataPredicate customData(final NbtPredicate value) {
      return new CustomDataPredicate(value);
   }
}
