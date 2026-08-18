package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetItemCountFunction extends LootItemConditionalFunction {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(NumberProviders.CODEC.fieldOf("count").forGetter((f) -> f.count), Codec.BOOL.optionalFieldOf("add", false).forGetter((f) -> f.add))).apply(i, SetItemCountFunction::new));
   private final Holder count;
   private final boolean add;

   private SetItemCountFunction(final Optional condition, final Holder count, final boolean add) {
      super(condition);
      this.count = count;
      this.add = add;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validateHolder(context, "count", this.count);
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      int base = this.add ? itemStack.getCount() : 0;
      itemStack.setCount(base + ((NumberProvider)this.count.value()).getInt(context));
      return itemStack;
   }

   public static LootItemConditionalFunction.Builder setCount(final Holder count) {
      return simpleBuilder((conditions) -> new SetItemCountFunction(conditions, count, false));
   }

   public static LootItemConditionalFunction.Builder setCount(final Holder count, final boolean add) {
      return simpleBuilder((conditions) -> new SetItemCountFunction(conditions, count, add));
   }
}
