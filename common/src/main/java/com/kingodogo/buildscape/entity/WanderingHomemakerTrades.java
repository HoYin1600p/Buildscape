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
        var sculkCatalyst = Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:sculk_catalyst"));
        if (sculkCatalyst == null || sculkCatalyst == Items.AIR) {
            sculkCatalyst = ModItems.SCULK_CATALYST.get().asItem();
        }
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(sculkCatalyst, 2), 3, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.MUFF_BLOCK.get().createStack(2), 4, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.CONFETTI_ITEM.get().createStack(32), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.RESIN_CLUMP.get().createStack(16), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.RESIN_BLOCK.get().createStack(16), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.CREAKING_HEART.get().createStack(2), 2, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.BOTTLE_OF_MIST.get().createStack(16), 4, 1, 0.05f);
        for (var color : net.minecraft.world.item.DyeColor.values()) {
            if (color == net.minecraft.world.item.DyeColor.WHITE) continue;
            var item = Services.PLATFORM.getItem(new com.kingodogo.buildscape.util.CommonId("buildscape", color.getName() + "_haze_bush"));
            add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(item, 6), 6, 1, 0.05f);
        }
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:mangrove_propagule")), 5), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.POPLAR_SAPLING.get().createStack(4), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:cherry_sapling")), 4), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:pale_oak_sapling")), 4), 8, 1, 0.05f);
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
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:sulfur_spike")), 32), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.PACKED_ICICLE_BLOCK.get().createStack(4), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:sulfur")), 4), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:cinnabar")), 2), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:wildflowers")), 4), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:leaf_litter")), 4), 6, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:short_dry_grass")), 6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:tall_dry_grass")), 6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:bush")), 6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:cactus_flower")), 6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.RED_BUSH.get().createStack(6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:firefly_bush")), 6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:open_eyeblossom")), 6), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.parse("minecraft:closed_eyeblossom")), 6), 8, 1, 0.05f);
        return list;
    }

    public static List<MerchantOffer> getFestiveTrades(Random random) {
        return getFestiveTrades(random, Services.isVersionCluster("v118x"));
    }

    public static List<MerchantOffer> getFestiveTrades(Random random, boolean includeLegacySnowyLeaves) {
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

        RegistrySupplier<ItemDefinition>[] stringLights = new RegistrySupplier[] {
                ModItems.WHITE_STRING_LIGHT, ModItems.ORANGE_STRING_LIGHT, ModItems.MAGENTA_STRING_LIGHT,
                ModItems.LIGHT_BLUE_STRING_LIGHT, ModItems.YELLOW_STRING_LIGHT, ModItems.LIME_STRING_LIGHT,
                ModItems.PINK_STRING_LIGHT, ModItems.GRAY_STRING_LIGHT, ModItems.LIGHT_GRAY_STRING_LIGHT,
                ModItems.CYAN_STRING_LIGHT, ModItems.PURPLE_STRING_LIGHT, ModItems.BLUE_STRING_LIGHT,
                ModItems.BROWN_STRING_LIGHT, ModItems.GREEN_STRING_LIGHT, ModItems.RED_STRING_LIGHT,
                ModItems.BLACK_STRING_LIGHT, ModItems.MULTICOLOR_STRING_LIGHT
        };
        add(list, new ItemStack(Items.EMERALD, 2), stringLights[random.nextInt(stringLights.length)].get().createStack(16), 8, 1, 0.05f);

        List<RegistrySupplier<ItemDefinition>> snowyLeaves = new ArrayList<>(List.of(
                ModItems.SNOWY_OAK_LEAVES, ModItems.SNOWY_SPRUCE_LEAVES,
                ModItems.SNOWY_BIRCH_LEAVES, ModItems.SNOWY_JUNGLE_LEAVES, ModItems.SNOWY_ACACIA_LEAVES,
                ModItems.SNOWY_DARK_OAK_LEAVES, ModItems.SNOWY_MANGROVE_LEAVES, ModItems.SNOWY_AZALEA_LEAVES,
                ModItems.SNOWY_FLOWERING_AZALEA_LEAVES, ModItems.SNOWY_CHERRY_LEAVES, ModItems.SNOWY_PALE_OAK_LEAVES,
                ModItems.SNOWY_ORANGE_POPLAR_LEAVES, ModItems.SNOWY_RED_POPLAR_LEAVES,
                ModItems.SNOWY_YELLOW_POPLAR_LEAVES));
        if (includeLegacySnowyLeaves) snowyLeaves.add(0, ModItems.SNOWY_LEAVES);
        add(list, new ItemStack(Items.EMERALD, 1), snowyLeaves.get(random.nextInt(snowyLeaves.size())).get().createStack(8), 8, 1, 0.05f);

        RegistrySupplier<ItemDefinition>[] ornaments = new RegistrySupplier[] {
                ModItems.WHITE_ORNAMENT, ModItems.ORANGE_ORNAMENT, ModItems.MAGENTA_ORNAMENT,
                ModItems.LIGHT_BLUE_ORNAMENT, ModItems.YELLOW_ORNAMENT, ModItems.LIME_ORNAMENT,
                ModItems.PINK_ORNAMENT, ModItems.GRAY_ORNAMENT, ModItems.LIGHT_GRAY_ORNAMENT,
                ModItems.CYAN_ORNAMENT, ModItems.PURPLE_ORNAMENT, ModItems.BLUE_ORNAMENT,
                ModItems.BROWN_ORNAMENT, ModItems.GREEN_ORNAMENT, ModItems.RED_ORNAMENT,
                ModItems.BLACK_ORNAMENT, ModItems.GLASS_ORNAMENT, ModItems.TINTED_GLASS_ORNAMENT
        };
        add(list, new ItemStack(Items.EMERALD, 2), ornaments[random.nextInt(ornaments.length)].get().createStack(16), 8, 1, 0.05f);

        RegistrySupplier<ItemDefinition>[] stars = new RegistrySupplier[] {
                ModItems.WHITE_STAR, ModItems.ORANGE_STAR, ModItems.MAGENTA_STAR,
                ModItems.LIGHT_BLUE_STAR, ModItems.YELLOW_STAR, ModItems.LIME_STAR,
                ModItems.PINK_STAR, ModItems.GRAY_STAR, ModItems.LIGHT_GRAY_STAR,
                ModItems.CYAN_STAR, ModItems.PURPLE_STAR, ModItems.BLUE_STAR,
                ModItems.BROWN_STAR, ModItems.GREEN_STAR, ModItems.RED_STAR,
                ModItems.BLACK_STAR, ModItems.GLOW_STAR
        };
        add(list, new ItemStack(Items.EMERALD, 1), stars[random.nextInt(stars.length)].get().createStack(8), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.PACKED_ICICLE_BLOCK.get().createStack(8), 8, 1, 0.05f);

        RegistrySupplier<ItemDefinition>[] glowLights = new RegistrySupplier[] {
                ModItems.GLOW_LIGHTS, ModItems.MULTICOLOR_GLOW_LIGHTS
        };
        add(list, new ItemStack(Items.EMERALD, 1), glowLights[random.nextInt(glowLights.length)].get().createStack(8), 8, 1, 0.05f);
        add(list, new ItemStack(Items.EMERALD, 1), ModItems.FESTIVE_LAMP.get().createStack(8), 8, 1, 0.05f);
        return list;
    }

    private static void add(List<MerchantOffer> list, ItemStack cost, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        list.add(Services.PLATFORM.createMerchantOffer(cost, result, maxUses, xp, priceMultiplier));
    }
}
