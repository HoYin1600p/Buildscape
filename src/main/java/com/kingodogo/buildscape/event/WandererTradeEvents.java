package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.registry.ModItems;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WandererTradeEvents {

    /** An emerald-priced generic wandering trader offer. */
    private record EmeraldTrade(int emeralds, Supplier<? extends ItemLike> result, int resultCount, int maxUses) {

        void addTo(WandererTradesEvent event) {
            event.getGenericTrades()
                    .add((trader, rand) -> new MerchantOffer(
                            new ItemStack(Items.EMERALD, emeralds),
                            new ItemStack(result.get(), resultCount),
                            maxUses,
                            1,
                            0.05f));
        }
    }

    private static EmeraldTrade trade(int emeralds, Supplier<? extends ItemLike> result, int resultCount, int maxUses) {
        return new EmeraldTrade(emeralds, result, resultCount, maxUses);
    }

    /** The generic trades, in the order they are added. */
    private static List<EmeraldTrade> genericTrades() {
        return List.of(
                trade(5, ModItems.MANGROVE_PROPAGULE, 1, 8),
                trade(1, ModItems.POPLAR_SAPLING, 2, 8),
                trade(1, ModItems.CHERRY_SAPLING, 2, 8),
                trade(1, ModItems.PALE_OAK_SAPLING, 2, 8),
                trade(1, ModItems.RED_MONETS, 1, 6),
                trade(1, ModItems.BLUE_MONETS, 1, 6),
                trade(1, ModItems.PURPLE_MONETS, 1, 6),
                trade(1, ModItems.LIGHT_BLUE_MONETS, 1, 6),
                trade(1, ModItems.PINK_MONETS, 1, 6),
                trade(1, ModItems.YELLOW_MONETS, 1, 6),
                trade(1, ModItems.CLOVER, 4, 6),
                trade(1, ModItems.RED_ROSE_VINES, 1, 6),
                trade(1, ModItems.BLACK_ROSE_VINES, 1, 6),
                trade(1, ModItems.BLUE_ROSE_VINES, 1, 6),
                trade(1, ModItems.WHITE_ROSE_VINES, 1, 6),
                trade(1, ModItems.SNOWY_GRASS_BLOCK, 1, 2),
                trade(1, ModItems.RED_SPORE_BLOSSOM, 1, 6),
                trade(1, ModItems.CYAN_SPORE_BLOSSOM, 1, 6),
                trade(1, ModItems.BLUE_SPORE_BLOSSOM, 1, 6),
                trade(1, ModItems.PURPLE_SPORE_BLOSSOM, 1, 6),
                trade(1, ModItems.ORANGE_SPORE_BLOSSOM, 1, 6),
                trade(1, ModItems.ICICLE, 2, 6),
                trade(1, ModItems.SULFUR_SPIKE, 2, 6),
                trade(1, ModItems.SULFUR, 1, 8),
                trade(1, ModItems.CINNABAR, 1, 8));
    }

    @SubscribeEvent
    public void onWandererTrades(WandererTradesEvent event) {
        for (EmeraldTrade trade : genericTrades()) {
            trade.addTo(event);
        }

        event.getRareTrades().add((trader, rand) -> {
            if (rand.nextInt(5000) == 0) {
                return new MerchantOffer(
                        new ItemStack(Items.DIAMOND, 12),
                        new ItemStack(ModItems.ANCIENT_ASHEN_SCROLL.get(), 1),
                        1,
                        1,
                        0.0f);
            }
            return null;
        });
    }
}
