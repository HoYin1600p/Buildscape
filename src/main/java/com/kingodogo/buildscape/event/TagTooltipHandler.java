package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.item.FestiveStockingItem;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class TagTooltipHandler {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof FestiveStockingItem) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("StoredItem", 10)) {
                CompoundTag storedTag = tag.getCompound("StoredItem");
                if (!storedTag.isEmpty()) {
                    ItemStack storedItem = ItemStack.of(storedTag);
                    if (!storedItem.isEmpty()) {
                        List<Component> tooltip = event.getToolTip();
                        tooltip.add(
                                new TranslatableComponent(
                                        "tooltip.buildscape.festive_stocking.contains",
                                        storedItem.getCount(),
                                        storedItem.getDisplayName()
                                )
                        );
                    }
                }
            }
        }
    }
}
