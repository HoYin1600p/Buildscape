package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

public final class LoaderWandererTrades {
    private LoaderWandererTrades() {}

    public static void register(WandererTradesEvent event) {
        for (var trade : WandererTradeTable.generic()) {
            // The v121x adapter resolves later vanilla content to its restored counterpart.
            var id = CommonId.parse(trade.item().contains(":") ? trade.item() : "buildscape:" + trade.item());
            var item = Services.PLATFORM.getItem(id);
            if (item == null || item == Items.AIR) {
                com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Skipping unavailable v121x wandering trade item: {}", id);
                continue;
            }
            event.getGenericTrades().add((trader, random) -> new MerchantOffer(
                    new net.minecraft.world.item.trading.ItemCost(Items.EMERALD, trade.emeralds()), new ItemStack(item, trade.count()),
                    trade.maxUses(), 1, .05F));
        }
        var scroll = Services.PLATFORM.getItem(new CommonId("buildscape", "ancient_ashen_scroll"));
        if (scroll == null || scroll == Items.AIR) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Skipping unavailable v121x wandering rare trade: buildscape:ancient_ashen_scroll");
            return;
        }
        event.getRareTrades().add((trader, random) -> random.nextInt(5000) == 0
                ? new MerchantOffer(new net.minecraft.world.item.trading.ItemCost(Items.DIAMOND, 12), new ItemStack(scroll), 1, 1, 0F) : null);
    }
}
