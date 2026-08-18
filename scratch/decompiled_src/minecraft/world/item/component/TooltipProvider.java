package net.minecraft.world.item.component;

import java.util.function.Consumer;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public interface TooltipProvider {
   void addToTooltip(Item.TooltipContext context, Consumer consumer, TooltipFlag flag, DataComponentGetter components);

   @FunctionalInterface
   public interface Getter {
      TooltipProvider get(Object component);
   }
}
