package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WandererTradeEvents {

    @SubscribeEvent
    public void onWandererTrades(
            WandererTradesEvent event) {
        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                5),
                        new ItemStack(ModItems.MANGROVE_PROPAGULE.get(), 1),
                        8,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(Items.EMERALD, 1),
                        new ItemStack(ModItems.POPLAR_SAPLING.get(), 2),
                        8,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(Items.EMERALD, 1),
                        new ItemStack(ModItems.CHERRY_SAPLING.get(), 2),
                        8,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(Items.EMERALD, 1),
                        new ItemStack(ModItems.PALE_OAK_SAPLING.get(), 2),
                        8,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.RED_MONETS.get(), 1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.BLUE_MONETS.get(), 1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.PURPLE_MONETS.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.LIGHT_BLUE_MONETS.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.PINK_MONETS.get(), 1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.YELLOW_MONETS.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.CLOVER.get(), 4),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.RED_ROSE_VINES.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.BLACK_ROSE_VINES.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.BLUE_ROSE_VINES.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.WHITE_ROSE_VINES.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.SNOWY_GRASS_BLOCK.get(),
                                1),
                        2,
                        1,
                        0.05f));
        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.RED_SPORE_BLOSSOM.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.CYAN_SPORE_BLOSSOM.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.BLUE_SPORE_BLOSSOM.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.PURPLE_SPORE_BLOSSOM.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(
                                ModItems.ORANGE_SPORE_BLOSSOM.get(),
                                1),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.ICICLE.get(), 2),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.SULFUR_SPIKE.get(), 2),
                        6,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.SULFUR.get(), 1),
                        8,
                        1,
                        0.05f));

        event
                .getGenericTrades()
                .add((trader, rand) -> new MerchantOffer(
                        new ItemStack(
                                Items.EMERALD,
                                1),
                        new ItemStack(ModItems.CINNABAR.get(), 1),
                        8,
                        1,
                        0.05f));

        event
                .getRareTrades()
                .add((trader, rand) -> {
                    if (rand.nextInt(5000) == 0) {
                        return new MerchantOffer(
                                new ItemStack(Items.DIAMOND, 12),
                                new ItemStack(ModItems.ANCIENT_ASHEN_SCROLL.get(), 1),
                                1,
                                1,
                                0.0f
                        );
                    }
                    return null;
                });
    }
}
