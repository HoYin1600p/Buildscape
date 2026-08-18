package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetComponentsFunction extends LootItemConditionalFunction {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(DataComponentPatch.CODEC.fieldOf("components").forGetter((f) -> f.components)).apply(i, SetComponentsFunction::new));
   private final DataComponentPatch components;

   private SetComponentsFunction(final Optional condition, final DataComponentPatch components) {
      super(condition);
      this.components = components;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      itemStack.applyComponentsAndValidate(this.components);
      return itemStack;
   }

   public static LootItemConditionalFunction.Builder setComponent(final DataComponentType type, final Object value) {
      return simpleBuilder((conditions) -> new SetComponentsFunction(conditions, DataComponentPatch.builder().set(type, value).build()));
   }
}
