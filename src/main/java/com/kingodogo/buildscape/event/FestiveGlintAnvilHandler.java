package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.util.FestiveGlintHelper;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class FestiveGlintAnvilHandler {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (left.isEmpty() || right.isEmpty()) {
            return;
        }

        if (right.is(ModItems.FESTIVE_GLINT_SHARD.get())) {
            // Can only be applied to already enchanted items
            if (!FestiveGlintHelper.isEnchantedItem(left)) {
                return;
            }

            ItemStack output = left.copy();
            output.setCount(1);
            FestiveGlintHelper.applyFestiveGlint(output);

            // Handle custom name if modified in anvil text field
            String name = event.getName();
            if (name != null && !name.isEmpty()) {
                if (!name.equals(left.getHoverName().getString())) {
                    output.setHoverName(new TextComponent(name));
                }
            } else if (left.hasCustomHoverName()) {
                output.resetHoverName();
            }

            event.setOutput(output);
            event.setCost(0);
            event.setMaterialCost(0);
        }
    }
}
