package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextArg;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyComponentsFunction extends LootItemConditionalFunction {
   private static final Codec GETTER_CODEC = LootContextArg.createArgCodec((builder) -> builder.anyEntity(CopyComponentsFunction.DirectSource::new).anyBlockEntity(CopyComponentsFunction.BlockEntitySource::new).anyItemStack(CopyComponentsFunction.DirectSource::new));
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(GETTER_CODEC.fieldOf("source").forGetter((f) -> f.source), DataComponentType.CODEC.listOf().optionalFieldOf("include").forGetter((f) -> f.include), DataComponentType.CODEC.listOf().optionalFieldOf("exclude").forGetter((f) -> f.exclude))).apply(i, CopyComponentsFunction::new));
   private final LootContextArg source;
   private final Optional include;
   private final Optional exclude;
   private final Predicate bakedPredicate;

   private CopyComponentsFunction(final Optional condition, final LootContextArg source, final Optional include, final Optional exclude) {
      super(condition);
      this.source = source;
      this.include = include.map(List::copyOf);
      this.exclude = exclude.map(List::copyOf);
      List componentPredicates = new ArrayList(2);
      exclude.ifPresent((s) -> componentPredicates.add((Predicate)(e) -> !s.contains(e)));
      include.ifPresent((s) -> componentPredicates.add(s::contains));
      this.bakedPredicate = Util.allOf(componentPredicates);
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Set getReferencedContextParams() {
      return Set.of(this.source.contextParam());
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      DataComponentGetter data = (DataComponentGetter)this.source.get(context);
      if (data != null) {
         if (data instanceof DataComponentMap) {
            DataComponentMap sourceComponents = (DataComponentMap)data;
            itemStack.applyComponents(sourceComponents.filter(this.bakedPredicate));
         } else {
            Collection exclude = (Collection)this.exclude.orElse(List.of());
            ((Stream)this.include.map(Collection::stream).orElse(BuiltInRegistries.DATA_COMPONENT_TYPE.listElements().map(Holder::value))).forEach((componentType) -> {
               if (!exclude.contains(componentType)) {
                  TypedDataComponent value = data.getTyped(componentType);
                  if (value != null) {
                     itemStack.set(value);
                  }

               }
            });
         }
      }

      return itemStack;
   }

   public static CopyComponentsFunction.Builder copyComponentsFromEntity(final ContextKey source) {
      return new CopyComponentsFunction.Builder(new CopyComponentsFunction.DirectSource(source));
   }

   public static CopyComponentsFunction.Builder copyComponentsFromBlockEntity(final ContextKey source) {
      return new CopyComponentsFunction.Builder(new CopyComponentsFunction.BlockEntitySource(source));
   }

   private static record BlockEntitySource(ContextKey contextParam) implements LootContextArg.Getter {
      public DataComponentGetter get(final BlockEntity blockEntity) {
         return blockEntity.collectComponents();
      }
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private final LootContextArg source;
      private Optional include = Optional.empty();
      private Optional exclude = Optional.empty();

      private Builder(final LootContextArg source) {
         this.source = source;
      }

      public CopyComponentsFunction.Builder include(final DataComponentType type) {
         if (this.include.isEmpty()) {
            this.include = Optional.of(ImmutableList.builder());
         }

         ((ImmutableList.Builder)this.include.get()).add(type);
         return this;
      }

      public CopyComponentsFunction.Builder exclude(final DataComponentType type) {
         if (this.exclude.isEmpty()) {
            this.exclude = Optional.of(ImmutableList.builder());
         }

         ((ImmutableList.Builder)this.exclude.get()).add(type);
         return this;
      }

      protected CopyComponentsFunction.Builder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return new CopyComponentsFunction(this.getCondition(), this.source, this.include.map(ImmutableList.Builder::build), this.exclude.map(ImmutableList.Builder::build));
      }
   }

   private static record DirectSource(ContextKey contextParam) implements LootContextArg.Getter {
      public DataComponentGetter get(final DataComponentGetter value) {
         return value;
      }
   }
}
