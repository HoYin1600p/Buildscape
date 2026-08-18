package net.minecraft.world.level.storage.loot.functions;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class EnchantRandomlyFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(RegistryCodecs.holderSet(Registries.ENCHANTMENT).optionalFieldOf("options").forGetter((f) -> f.options), Codec.BOOL.optionalFieldOf("only_compatible", true).forGetter((f) -> f.onlyCompatible), Codec.BOOL.optionalFieldOf("include_additional_cost_component", false).forGetter((f) -> f.includeAdditionalCostComponent))).apply(i, EnchantRandomlyFunction::new));
   private final Optional options;
   private final boolean onlyCompatible;
   private final boolean includeAdditionalCostComponent;

   private EnchantRandomlyFunction(final Optional condition, final Optional options, final boolean onlyCompatible, final boolean includeAdditionalCostComponent) {
      super(condition);
      this.options = options;
      this.onlyCompatible = onlyCompatible;
      this.includeAdditionalCostComponent = includeAdditionalCostComponent;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Set getReferencedContextParams() {
      return this.includeAdditionalCostComponent ? Set.of(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED) : Set.of();
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      RandomSource random = context.getRandom();
      boolean targetIsBook = itemStack.is(Items.BOOK);
      boolean shouldCheckCompatibility = !targetIsBook && this.onlyCompatible;
      Stream compatibleEnchantmentsStream = ((Stream)this.options.map(HolderSet::stream).orElseGet(() -> context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).listElements().map(Function.identity()))).filter((candidate) -> !shouldCheckCompatibility || ((Enchantment)candidate.value()).canEnchant(itemStack));
      List compatibleEnchantments = compatibleEnchantmentsStream.toList();
      Optional enchantment = Util.getRandomSafe(compatibleEnchantments, random);
      if (enchantment.isEmpty()) {
         LOGGER.warn("Couldn't find a compatible enchantment for {}", itemStack);
         return itemStack;
      } else {
         return this.enchantItem(itemStack, (Holder)enchantment.get(), context);
      }
   }

   private ItemStack enchantItem(ItemStack itemStack, final Holder enchantment, final LootContext context) {
      RandomSource random = context.getRandom();
      int level = Mth.nextInt(random, ((Enchantment)enchantment.value()).getMinLevel(), ((Enchantment)enchantment.value()).getMaxLevel());
      if (itemStack.is(Items.BOOK)) {
         itemStack = new ItemStack(Items.ENCHANTED_BOOK);
      }

      itemStack.enchant(enchantment, level);
      if (this.includeAdditionalCostComponent && context.hasParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED)) {
         itemStack.set(DataComponents.ADDITIONAL_TRADE_COST, 2 + random.nextInt(5 + level * 10) + 3 * level);
      }

      return itemStack;
   }

   public static EnchantRandomlyFunction.Builder randomEnchantment() {
      return new EnchantRandomlyFunction.Builder();
   }

   public static EnchantRandomlyFunction.Builder randomApplicableEnchantment(final HolderGetter enchantments) {
      return randomEnchantment().withOneOf(enchantments.getOrThrow(EnchantmentTags.ON_RANDOM_LOOT));
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private Optional options = Optional.empty();
      private boolean onlyCompatible = true;
      private boolean includeAdditionalCostComponent = false;

      protected EnchantRandomlyFunction.Builder getThis() {
         return this;
      }

      public EnchantRandomlyFunction.Builder withEnchantment(final Holder enchantment) {
         this.options = Optional.of(HolderSet.direct(enchantment));
         return this;
      }

      public EnchantRandomlyFunction.Builder withOneOf(final HolderSet enchantments) {
         this.options = Optional.of(enchantments);
         return this;
      }

      public EnchantRandomlyFunction.Builder withOptions(final HolderSet enchantments) {
         this.options = Optional.of(enchantments);
         return this;
      }

      public EnchantRandomlyFunction.Builder allowingIncompatibleEnchantments() {
         this.onlyCompatible = false;
         return this;
      }

      public EnchantRandomlyFunction.Builder includeAdditionalCostComponent() {
         this.includeAdditionalCostComponent = true;
         return this;
      }

      public LootItemFunction build() {
         return new EnchantRandomlyFunction(this.getCondition(), this.options, this.onlyCompatible, this.includeAdditionalCostComponent);
      }
   }
}
