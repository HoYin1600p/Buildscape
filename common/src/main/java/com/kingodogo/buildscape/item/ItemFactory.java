package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
public final class ItemFactory {

    private ItemFactory() {}
    public static BlockItem createBlockItem(Block block, CommonItemProperties props) {
        return createBlockItem("", block, props);
    }
    public static BlockItem createBlockItem(String id, Block block, CommonItemProperties props) {
        Item.Properties p = createProperties(props);
        if ("muff_block".equals(id)) {
            return Services.PLATFORM.createMuffBlockItem(block, p);
        }
        if ("cascade_block".equals(id) || "cascade_block_no_mist".equals(id)) {
            return Services.PLATFORM.createMistBlockItem(block, p);
        }
        if ("festive_star".equals(id)) {
            return Services.PLATFORM.createFestiveStarItem(block, p);
        }
        if ("golden_jar".equals(id)) {
            return Services.PLATFORM.createGoldenJarItem(block, p);
        }
        if (id.contains("glass_jar")) {
            return Services.PLATFORM.createGlassJarItem(block, p);
        }
        if (id.contains("copper_chest")) {
            return Services.PLATFORM.createCopperChestItem(block, p);
        }
        if (id.contains("festive_stocking")) {
            String color = id.equals("festive_stocking") ? null : id.replace("_festive_stocking", "");
            return Services.PLATFORM.createFestiveStockingItem(block, p, color);
        }
        return new BlockItem(block, p);
    }
    public static Item createItem(ItemDefinition def) {
        String id = def.getId();
        Item.Properties props = createProperties(def.getProperties());
        return switch (id) {
            case "builders_pouch" -> com.kingodogo.buildscape.platform.Services.PLATFORM.createBuildersPouchItem(props);
            case "wrench" -> Services.PLATFORM.createWrenchItem(props);
            case "iron_hammer" -> com.kingodogo.buildscape.platform.Services.PLATFORM.createHammerItem(HammerItem.HammerTier.IRON, props);
            case "diamond_hammer" -> com.kingodogo.buildscape.platform.Services.PLATFORM.createHammerItem(HammerItem.HammerTier.DIAMOND, props);
            case "netherite_hammer" -> com.kingodogo.buildscape.platform.Services.PLATFORM.createHammerItem(HammerItem.HammerTier.NETHERITE, props);
            case "copper_biome_brush" -> com.kingodogo.buildscape.platform.Services.PLATFORM.createBiomeBrushItem(BiomeBrushItem.BiomeBrushTier.COPPER, props);
            case "diamond_biome_brush" -> com.kingodogo.buildscape.platform.Services.PLATFORM.createBiomeBrushItem(BiomeBrushItem.BiomeBrushTier.DIAMOND, props);
            case "netherite_biome_brush" -> com.kingodogo.buildscape.platform.Services.PLATFORM.createBiomeBrushItem(BiomeBrushItem.BiomeBrushTier.NETHERITE, props);
            case "confetti" -> com.kingodogo.buildscape.platform.Services.PLATFORM.createConfettiItem(props);
            case "bottle_of_mist" -> com.kingodogo.buildscape.platform.Services.PLATFORM.createBottleOfMistItem(props);
            case "festive_glint_shard" -> Services.PLATFORM.createFestiveGlintShardItem(props);
            case "big_ornament_template" -> Services.PLATFORM.createBigOrnamentTemplateItem(props);
            case "stringlight_frame" -> Services.PLATFORM.createStringlightFrameItem(props);
            case "golden_jar_pattern" -> Services.PLATFORM.createPatternItem(props, "tooltip.buildscape.golden_jar_pattern");
            case "festive_star_pattern" -> Services.PLATFORM.createPatternItem(props, "tooltip.buildscape.festive_star_pattern");
            case "stringlight_frame_pattern" -> Services.PLATFORM.createPatternItem(props, "tooltip.buildscape.stringlight_frame_pattern");
            case "infinite_phoenix_firework_star" -> Services.PLATFORM.createInfinitePhoenixFireworkStarItem(props);
            case "experience_bucket" -> Services.PLATFORM.createExperienceBucketItem(props);
            case "mangrove_boat" -> Services.PLATFORM.createMangroveBoatItem(props);
            case "poplar_boat" -> Services.PLATFORM.createPoplarBoatItem(props);
            case "builders_hat" -> new BuildersHatItem(props);
            default -> {
                if (id.contains("item_frame")) {
                    String color = id.equals("invisible_item_frame") ? "invisible" : id.replace("_item_frame", "");
                    yield Services.PLATFORM.createColoredItemFrameItem(props, color);
                }
                yield new Item(props);
            }
        };
    }
    public static Item.Properties createProperties(CommonItemProperties props) {
        Item.Properties p = new Item.Properties();

        if (props.getMaxStackSize() > 0 && props.getMaxStackSize() != 64) {
            p.stacksTo(props.getMaxStackSize());
        }

        if (props.getDurability() > 0) {
            p.durability(props.getDurability());
        }

        if (props.isFireResistant()) {
            p.fireResistant();
        }

        if (props.getRarity() != null) {
            p.rarity(resolveRarity(props.getRarity()));
        }

        return p;
    }
    public static Rarity resolveRarity(String rarity) {
        if (rarity == null) return Rarity.COMMON;
        return switch (rarity.toUpperCase()) {
            case "UNCOMMON" -> Rarity.UNCOMMON;
            case "RARE" -> Rarity.RARE;
            case "EPIC" -> Rarity.EPIC;
            default -> Rarity.COMMON;
        };
    }
}
