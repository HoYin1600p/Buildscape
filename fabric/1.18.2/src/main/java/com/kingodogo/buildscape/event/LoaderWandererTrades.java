package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

public final class LoaderWandererTrades {
    private LoaderWandererTrades() {}
    public static void register() {
        TradeOfferHelper.registerWanderingTraderOffers(1, offers -> {
            for (var trade : WandererTradeTable.generic()) {
                var id = CommonId.parse(trade.item().contains(":") ? trade.item() : "buildscape:" + trade.item());
                var item = Services.PLATFORM.getItem(id);
                if (item == null || item == Items.AIR) throw new IllegalStateException("Missing wandering trade item: " + id);
                offers.add((trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, trade.emeralds()),
                        new ItemStack(item, trade.count()), trade.maxUses(), 1, .05F));
            }
        });
        TradeOfferHelper.registerWanderingTraderOffers(2, offers -> {
            var scroll = Services.PLATFORM.getItem(new CommonId("buildscape", "ancient_ashen_scroll"));
            offers.add((trader, random) -> random.nextInt(5000) == 0
                    ? new MerchantOffer(new ItemStack(Items.DIAMOND, 12), new ItemStack(scroll), 1, 1, 0F) : null);
        });
    }
}
