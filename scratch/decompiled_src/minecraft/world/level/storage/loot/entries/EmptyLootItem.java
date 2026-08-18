package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EmptyLootItem extends SingleEntryContainerBase {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> uniformFields(i).apply(i, EmptyLootItem::new));

   private EmptyLootItem(final int weight, final int quality, final Optional condition, final Optional modifier) {
      super(weight, quality, condition, modifier);
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public void createItemStack(final Consumer output, final LootContext context) {
   }

   public static UniformContainerBase.Builder emptyItem() {
      return simpleBuilder(EmptyLootItem::new);
   }
}
