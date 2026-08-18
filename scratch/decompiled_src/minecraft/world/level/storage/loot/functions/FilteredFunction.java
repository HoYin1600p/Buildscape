package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FilteredFunction extends LootItemConditionalFunction {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(ItemPredicate.CODEC.fieldOf("item_filter").forGetter((f) -> f.filter), LootItemFunctions.CODEC.optionalFieldOf("on_pass").forGetter((f) -> f.onPass), LootItemFunctions.CODEC.optionalFieldOf("on_fail").forGetter((f) -> f.onFail))).apply(i, FilteredFunction::new));
   private final ItemPredicate filter;
   private final Optional onPass;
   private final Optional onFail;

   private FilteredFunction(final Optional condition, final ItemPredicate filter, final Optional onPass, final Optional onFail) {
      super(condition);
      this.filter = filter;
      this.onPass = onPass;
      this.onFail = onFail;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      Optional function = this.filter.test((ItemInstance)itemStack) ? this.onPass : this.onFail;
      return function.isPresent() ? (ItemStack)((LootItemFunction)((Holder)function.get()).value()).apply(itemStack, context) : itemStack;
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validateHolder(context, "on_pass", this.onPass);
      Validatable.validateHolder(context, "on_fail", this.onFail);
   }

   public static FilteredFunction.Builder filtered(final ItemPredicate predicate) {
      return new FilteredFunction.Builder(predicate);
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private final ItemPredicate itemPredicate;
      private Optional onPass = Optional.empty();
      private Optional onFail = Optional.empty();

      private Builder(final ItemPredicate itemPredicate) {
         this.itemPredicate = itemPredicate;
      }

      protected FilteredFunction.Builder getThis() {
         return this;
      }

      public FilteredFunction.Builder onPass(final LootItemFunction onPass) {
         this.onPass = Optional.of(Holder.direct(onPass));
         return this;
      }

      public FilteredFunction.Builder onFail(final LootItemFunction onFail) {
         this.onFail = Optional.of(Holder.direct(onFail));
         return this;
      }

      public LootItemFunction build() {
         return new FilteredFunction(this.getCondition(), this.itemPredicate, this.onPass, this.onFail);
      }
   }
}
