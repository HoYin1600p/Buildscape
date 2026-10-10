package com.kingodogo.buildscape.entity;

import com.kingodogo.buildscape.item.ItemDefinition;
import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.RegistrySupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class WanderingHomemakerTrades {

    private WanderingHomemakerTrades() {}

    public static List<MerchantOffer> getStandardTrades() {
        List<MerchantOffer> list = new ArrayList<>();
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.MANGROVE_PROPAGULE.get().createStack(5), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.POPLAR_SAPLING.get().createStack(4), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.CHERRY_SAPLING.get().createStack(4), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.PALE_OAK_SAPLING.get().createStack(4), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.RED_MONETS.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.BLUE_MONETS.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.PURPLE_MONETS.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.LIGHT_BLUE_MONETS.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.PINK_MONETS.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.YELLOW_MONETS.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.CLOVER.get().createStack(7), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.RED_ROSE_VINES.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.BLACK_ROSE_VINES.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.BLUE_ROSE_VINES.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.WHITE_ROSE_VINES.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.SNOWY_GRASS_BLOCK.get().createStack(2), 2, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.RED_SPORE_BLOSSOM.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.CYAN_SPORE_BLOSSOM.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.BLUE_SPORE_BLOSSOM.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.PURPLE_SPORE_BLOSSOM.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.ORANGE_SPORE_BLOSSOM.get().createStack(2), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.ICICLE.get().createStack(32), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.SULFUR_SPIKE.get().createStack(32), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.PACKED_ICICLE_BLOCK.get().createStack(4), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.SULFUR.get().createStack(4), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.CINNABAR.get().createStack(2), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.WILDFLOWERS.get().createStack(4), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.LEAF_LITTER.get().createStack(4), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.DRY_GRASS.get().createStack(6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.TALL_DRY_GRASS.get().createStack(6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.BUSH.get().createStack(6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.CACTUS_FLOWER.get().createStack(6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.RED_BUSH.get().createStack(6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.FIREFLY_BUSH.get().createStack(6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.OPEN_EYEBLOSSOM.get().createStack(6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.CLOSED_EYEBLOSSOM.get().createStack(6), 8, 1, 0.05f);
        return list;
    }

    public static List<MerchantOffer> getFestiveTrades(Random random) {
        List<MerchantOffer> list = new ArrayList<>();
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.WHITE_SAND.get().createStack(8), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.GREEN_SAND.get().createStack(8), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.RED_SAND.get().createStack(8), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.RED_TILES.get().createStack(8), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.LIME_TILES.get().createStack(8), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.SNOW_OVERLAY.get().createStack(4), 4, 1, 0.05f);

        RegistrySupplier<ItemDefinition>[] stockings = new RegistrySupplier[] {
                ModItems.BLACK_FESTIVE_STOCKING, ModItems.BLUE_FESTIVE_STOCKING, ModItems.BROWN_FESTIVE_STOCKING,
                ModItems.CYAN_FESTIVE_STOCKING, ModItems.GRAY_FESTIVE_STOCKING, ModItems.GREEN_FESTIVE_STOCKING,
                ModItems.LIGHT_BLUE_FESTIVE_STOCKING, ModItems.LIGHT_GRAY_FESTIVE_STOCKING,
                ModItems.LIME_FESTIVE_STOCKING,
                ModItems.MAGENTA_FESTIVE_STOCKING, ModItems.ORANGE_FESTIVE_STOCKING, ModItems.PINK_FESTIVE_STOCKING,
                ModItems.PURPLE_FESTIVE_STOCKING, ModItems.RED_FESTIVE_STOCKING, ModItems.WHITE_FESTIVE_STOCKING,
                ModItems.YELLOW_FESTIVE_STOCKING, ModItems.FESTIVE_STOCKING
        };
        add(list, new ItemStack(Items.EMERALD, 1), stockings[random.nextInt(stockings.length)].get().createStack(8), 4, 1, 0.05f);
        return list;
    }

    private static void add(List<MerchantOffer> list, ItemStack cost, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        list.add(Services.PLATFORM.createMerchantOffer(cost, result, maxUses, xp, priceMultiplier));
    }
}
