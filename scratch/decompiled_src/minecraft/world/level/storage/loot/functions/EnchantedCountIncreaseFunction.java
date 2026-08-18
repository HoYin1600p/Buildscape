package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class EnchantedCountIncreaseFunction extends LootItemConditionalFunction {
   public static final int NO_LIMIT = 0;
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(Enchantment.CODEC.fieldOf("enchantment").forGetter((f) -> f.enchantment), NumberProviders.CODEC.fieldOf("count").forGetter((f) -> f.count), Codec.INT.optionalFieldOf("limit", 0).forGetter((f) -> f.limit))).apply(i, EnchantedCountIncreaseFunction::new));
   private final Holder enchantment;
   private final Holder count;
   private final int limit;

   private EnchantedCountIncreaseFunction(final Optional condition, final Holder enchantment, final Holder count, final int limit) {
      super(condition);
      this.enchantment = enchantment;
      this.count = count;
      this.limit = limit;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Set getReferencedContextParams() {
      return Set.of(LootContextParams.ATTACKING_ENTITY);
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validateHolder(context, "count", this.count);
   }

   private boolean hasLimit() {
      return this.limit > 0;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      Entity killer = (Entity)context.getOptionalParameter(LootContextParams.ATTACKING_ENTITY);
      if (killer instanceof LivingEntity entity) {
         int level = EnchantmentHelper.getEnchantmentLevel(this.enchantment, entity);
         if (level == 0) {
            return itemStack;
         }

         float addition = (float)level * ((NumberProvider)this.count.value()).getFloat(context);
         itemStack.grow(Math.round(addition));
         if (this.hasLimit()) {
            itemStack.limitSize(this.limit);
         }
      }

      return itemStack;
   }

   public static EnchantedCountIncreaseFunction.Builder lootingMultiplier(final HolderGetter enchantments, final Holder count) {
      return new EnchantedCountIncreaseFunction.Builder(enchantments.getOrThrow(Enchantments.LOOTING), count);
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private final Holder enchantment;
      private final Holder count;
      private int limit = 0;

      public Builder(final Holder enchantment, final Holder count) {
         this.enchantment = enchantment;
         this.count = count;
      }

      protected EnchantedCountIncreaseFunction.Builder getThis() {
         return this;
      }

      public EnchantedCountIncreaseFunction.Builder setLimit(final int limit) {
         this.limit = limit;
         return this;
      }

      public LootItemFunction build() {
         return new EnchantedCountIncreaseFunction(this.getCondition(), this.enchantment, this.count, this.limit);
      }
   }
}
