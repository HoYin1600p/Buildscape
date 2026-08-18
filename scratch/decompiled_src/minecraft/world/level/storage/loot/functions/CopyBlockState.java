package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyBlockState extends LootItemConditionalFunction {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").forGetter((f) -> f.block), Codec.STRING.listOf().fieldOf("properties").forGetter((f) -> f.properties.stream().map(Property::getName).toList()))).apply(i, CopyBlockState::new));
   private final Holder block;
   private final Set properties;

   private CopyBlockState(final Optional condition, final Holder block, final Set properties) {
      super(condition);
      this.block = block;
      this.properties = properties;
   }

   private CopyBlockState(final Optional condition, final Holder block, final List propertyNames) {
      this(condition, block, (Set)propertyNames.stream().map(((Block)block.value()).getStateDefinition()::getProperty).filter(Objects::nonNull).collect(Collectors.toSet()));
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Set getReferencedContextParams() {
      return Set.of(LootContextParams.BLOCK_STATE);
   }

   protected ItemStack run(final ItemStack itemStack, final LootContext context) {
      BlockState state = (BlockState)context.getOptionalParameter(LootContextParams.BLOCK_STATE);
      if (state != null) {
         itemStack.update(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY, (itemState) -> {
            for(Property property : this.properties) {
               if (state.hasProperty(property)) {
                  itemState = itemState.with(property, state);
               }
            }

            return itemState;
         });
      }

      return itemStack;
   }

   public static CopyBlockState.Builder copyState(final Block block) {
      return new CopyBlockState.Builder(block);
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private final Holder block;
      private final ImmutableSet.Builder properties = ImmutableSet.builder();

      private Builder(final Block block) {
         this.block = block.builtInRegistryHolder();
      }

      public CopyBlockState.Builder copy(final Property property) {
         if (!((Block)this.block.value()).getStateDefinition().getProperties().contains(property)) {
            throw new IllegalStateException("Property " + String.valueOf(property) + " is not present on block " + String.valueOf(this.block));
         } else {
            this.properties.add(property);
            return this;
         }
      }

      protected CopyBlockState.Builder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return new CopyBlockState(this.getCondition(), this.block, this.properties.build());
      }
   }
}
