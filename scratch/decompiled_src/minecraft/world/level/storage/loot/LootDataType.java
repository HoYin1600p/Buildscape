package net.minecraft.world.level.storage.loot;

import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public record LootDataType(ResourceKey registryKey, LootDataType.ContextGetter contextGetter) {
   public static final LootDataType PREDICATE = new LootDataType(Registries.PREDICATE, LootDataType.ContextGetter.constant(LootContextParamSets.ALL_PARAMS));
   public static final LootDataType MODIFIER = new LootDataType(Registries.ITEM_MODIFIER, LootDataType.ContextGetter.constant(LootContextParamSets.ALL_PARAMS));
   public static final LootDataType SLOT_SOURCE = new LootDataType(Registries.SLOT_SOURCE, LootDataType.ContextGetter.constant(LootContextParamSets.ALL_PARAMS));
   public static final LootDataType TABLE = new LootDataType(Registries.LOOT_TABLE, LootTable::getParamSet);
   public static final LootDataType NUMBER_PROVIDER = new LootDataType(Registries.NUMBER_PROVIDER, LootDataType.ContextGetter.constant(LootContextParamSets.ALL_PARAMS));

   public void runValidation(final ValidationContextSource contextSource, final ResourceKey key, final Validatable value) {
      ContextKeySet contextKeys = this.contextGetter.context(value);
      ValidationContext rootContext = contextSource.context(contextKeys).enterElement(new ProblemReporter.RootElementPathElement(key), key);
      value.validate(rootContext);
   }

   public void runValidation(final ValidationContextSource contextSource, final HolderLookup lookup) {
      lookup.listElements().forEach((holder) -> this.runValidation(contextSource, holder.key(), (Validatable)holder.value()));
   }

   public void runValidation(final ValidationContextSource contextSource, final HolderLookup.Provider registries) {
      HolderLookup registry = registries.lookupOrThrow(this.registryKey());
      this.runValidation(contextSource, registry);
   }

   public void runValidationIfPresent(final ValidationContextSource contextSource, final HolderLookup.Provider registries) {
      registries.lookup(this.registryKey()).ifPresent((registry) -> this.runValidation(contextSource, registry));
   }

   public static Stream values() {
      return Stream.of(PREDICATE, MODIFIER, SLOT_SOURCE, TABLE, NUMBER_PROVIDER);
   }

   @FunctionalInterface
   public interface ContextGetter {
      ContextKeySet context(Object value);

      static LootDataType.ContextGetter constant(final ContextKeySet v) {
         return (value) -> v;
      }
   }
}
