package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class EnchantWithLevelsFunction extends LootItemConditionalFunction {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(NumberProviders.CODEC.fieldOf("levels").forGetter((f) -> f.levels), RegistryCodecs.holderSet(Registries.ENCHANTMENT).optionalFieldOf("options").forGetter((f) -> f.options), Codec.BOOL.optionalFieldOf("include_additional_cost_component", false).forGetter((f) -> f.includeAdditionalCostComponent))).apply(i, EnchantWithLevelsFunction::new));
   private final Holder levels;
   private final Optional options;
   private final boolean includeAdditionalCostComponent;

   private EnchantWithLevelsFunction(final Optional condition, final Holder levels, final Optional options, final boolean includeAdditionalCostComponent) {
      super(condition);
      this.levels = levels;
      this.options = options;
      this.includeAdditionalCostComponent = includeAdditionalCostComponent;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Set getReferencedContextParams() {
      return this.includeAdditionalCostComponent ? Set.of(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED) : Set.of();
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validateHolder(context, "levels", this.levels);
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      RandomSource random = context.getRandom();
      RegistryAccess registryAccess = context.getLevel().registryAccess();
      int enchantmentCost = ((NumberProvider)this.levels.value()).getInt(context);
      ItemStack result = EnchantmentHelper.enchantItem(random, itemStack, enchantmentCost, registryAccess, this.options);
      if (this.includeAdditionalCostComponent && context.hasParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED) && !result.isEmpty() && enchantmentCost > 0) {
         result.set(DataComponents.ADDITIONAL_TRADE_COST, enchantmentCost);
      }

      return result;
   }

   public static EnchantWithLevelsFunction.Builder enchantWithLevels(final HolderGetter enchantments, final Holder levels) {
      return (new EnchantWithLevelsFunction.Builder(levels)).withOptions(enchantments.getOrThrow(EnchantmentTags.ON_RANDOM_LOOT));
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private final Holder levels;
      private Optional options = Optional.empty();
      private boolean includeAdditionalCostComponent = false;

      public Builder(final Holder levels) {
         this.levels = levels;
      }

      protected EnchantWithLevelsFunction.Builder getThis() {
         return this;
      }

      public EnchantWithLevelsFunction.Builder withOptions(final HolderSet tag) {
         this.options = Optional.of(tag);
         return this;
      }

      public EnchantWithLevelsFunction.Builder withOptions(final Optional options) {
         this.options = options;
         return this;
      }

      public EnchantWithLevelsFunction.Builder includeAdditionalCostComponent() {
         this.includeAdditionalCostComponent = true;
         return this;
      }

      public LootItemFunction build() {
         return new EnchantWithLevelsFunction(this.getCondition(), this.levels, this.options, this.includeAdditionalCostComponent);
      }
   }
}
