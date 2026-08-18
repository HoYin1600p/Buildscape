package net.minecraft.server.commands.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.CommandResponseTracker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.slot.SlotCollection;
import net.minecraft.world.item.slot.SlotSource;
import org.jspecify.annotations.Nullable;

public interface ItemAccessor {
   void setItems(CommandSourceStack source, SlotSource slotSource, ItemAccessor.SetterFunction function) throws CommandSyntaxException;

   SlotCollection getSlots(CommandSourceStack source, SlotSource slotSource) throws CommandSyntaxException;

   int getReplaceSuccess(CommandSourceStack source, CommandResponseTracker tracker, @Nullable ItemStack knownItem) throws CommandSyntaxException;

   int getModifySuccess(CommandSourceStack source, CommandResponseTracker tracker) throws CommandSyntaxException;

   @FunctionalInterface
   public interface SetterFunction {
      int apply(Object target, SlotCollection slots);
   }
}
