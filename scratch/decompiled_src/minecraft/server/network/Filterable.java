package net.minecraft.server.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Filterable(Object raw, Optional filtered) {
   public static Codec codec(final Codec valueCodec) {
      Codec fullCodec = RecordCodecBuilder.create((i) -> i.group(valueCodec.fieldOf("raw").forGetter(Filterable::raw), valueCodec.optionalFieldOf("filtered").forGetter(Filterable::filtered)).apply(i, Filterable::new));
      Codec simpleCodec = valueCodec.xmap(Filterable::passThrough, Filterable::raw);
      return Codec.withAlternative(fullCodec, simpleCodec);
   }

   public static StreamCodec streamCodec(final StreamCodec valueCodec) {
      return StreamCodec.composite(valueCodec, Filterable::raw, valueCodec.apply(ByteBufCodecs::optional), Filterable::filtered, Filterable::new);
   }

   public static Filterable passThrough(final Object value) {
      return new Filterable(value, Optional.empty());
   }

   public static Filterable from(final FilteredText text) {
      return new Filterable(text.raw(), text.isFiltered() ? Optional.of(text.filteredOrEmpty()) : Optional.empty());
   }

   public Object get(final boolean filterEnabled) {
      return filterEnabled ? this.filtered.orElse(this.raw) : this.raw;
   }

   public Filterable map(final Function function) {
      return new Filterable(function.apply(this.raw), this.filtered.map(function));
   }

   public Optional resolve(final Function function) {
      Optional newRaw = (Optional)function.apply(this.raw);
      if (newRaw.isEmpty()) {
         return Optional.empty();
      } else if (this.filtered.isPresent()) {
         Optional newFiltered = (Optional)function.apply(this.filtered.get());
         return newFiltered.isEmpty() ? Optional.empty() : Optional.of(new Filterable(newRaw.get(), newFiltered));
      } else {
         return Optional.of(new Filterable(newRaw.get(), Optional.empty()));
      }
   }
}
