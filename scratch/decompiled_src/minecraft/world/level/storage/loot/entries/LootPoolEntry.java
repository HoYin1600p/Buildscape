package net.minecraft.world.level.storage.loot.entries;

import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;

public interface LootPoolEntry {
   int getWeight(final float luck);

   void createItemStack(Consumer output, LootContext context);
}
