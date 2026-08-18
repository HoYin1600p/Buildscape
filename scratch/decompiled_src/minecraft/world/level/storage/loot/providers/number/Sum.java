package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record Sum(HolderSet summands) implements NumberProvider {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(NumberProviders.LIST_CODEC.fieldOf("summands").forGetter(Sum::summands)).apply(i, Sum::new));

   @SafeVarargs
   public static Holder sum(final Holder... summands) {
      return Holder.direct(new Sum(HolderSet.direct(summands)));
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public int getInt(final LootContext context) {
      float value = 0.0F;

      for(Holder provider : this.summands) {
         value += ((NumberProvider)provider.value()).getFloat(context);
      }

      return Mth.floor(value);
   }

   public float getFloat(final LootContext context) {
      float value = 0.0F;

      for(Holder provider : this.summands) {
         value += ((NumberProvider)provider.value()).getFloat(context);
      }

      return value;
   }

   public void validate(final ValidationContext context) {
      NumberProvider.super.validate(context);
      Validatable.validateHolderSet(context, "summands", this.summands);
   }
}
