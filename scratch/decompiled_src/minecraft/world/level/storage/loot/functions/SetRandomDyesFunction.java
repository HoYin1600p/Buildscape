package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetRandomDyesFunction extends LootItemConditionalFunction {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(NumberProviders.CODEC.fieldOf("number_of_dyes").forGetter((f) -> f.numberOfDyes)).apply(i, SetRandomDyesFunction::new));
   private final Holder numberOfDyes;

   private SetRandomDyesFunction(final Optional condition, final Holder numberOfDyes) {
      super(condition);
      this.numberOfDyes = numberOfDyes;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      RandomSource random = context.getRandom();
      int rolls = ((NumberProvider)this.numberOfDyes.value()).getInt(context);
      if (rolls <= 0) {
         return itemStack;
      } else {
         List dyes = new ArrayList(rolls);

         for(int i = 0; i < rolls; ++i) {
            dyes.add((DyeColor)Util.getRandom(DyeColor.VALUES, random));
         }

         return DyedItemColor.applyDyes(itemStack, dyes);
      }
   }

   public static LootItemConditionalFunction.Builder withCount(final Holder numberOfDyes) {
      return simpleBuilder((conditions) -> new SetRandomDyesFunction(conditions, numberOfDyes));
   }
}
