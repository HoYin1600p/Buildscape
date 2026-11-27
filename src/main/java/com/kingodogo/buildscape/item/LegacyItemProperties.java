package com.kingodogo.buildscape.item;

/** Registration properties from the Forge 1.18.2 item declarations. */
final class LegacyItemProperties {
    private LegacyItemProperties() {}

    static CommonItemProperties forItem(String id) {
        CommonItemProperties properties = CommonItemProperties.of();
        return switch (id) {
            case "wrench" -> properties.stacksTo(1);
            case "builders_workbench" -> properties.stacksTo(64);
            case "mangrove_sign" -> properties.stacksTo(16);
            case "bamboo_sign" -> properties.stacksTo(16);
            case "mangrove_boat" -> properties.stacksTo(1);
            case "confetti" -> properties.stacksTo(64);
            case "infinite_phoenix_firework_star" -> properties.stacksTo(1);
            case "bottle_of_mist" -> properties.stacksTo(64);
            case "ancient_ashen_scroll" -> properties.durability(28);
            case "poplar_boat" -> properties.stacksTo(1);
            case "straw_bed" -> properties.stacksTo(16);
            case "golden_jar" -> properties.rarity("UNCOMMON");
            case "festive_star" -> properties.rarity("RARE");
            case "experience_bucket" -> properties.stacksTo(1);
            case "festive_glint_shard" -> properties.rarity("RARE");
            case "golden_jar_pattern" -> properties.stacksTo(1).rarity("UNCOMMON");
            case "festive_star_pattern" -> properties.stacksTo(1).rarity("RARE");
            case "big_ornament_template" -> properties.rarity("UNCOMMON");
            case "stringlight_frame_pattern" -> properties.stacksTo(1).rarity("RARE");
            case "music_disc_celebration" -> properties.stacksTo(1).rarity("RARE");
            case "music_disc_snowfall" -> properties.stacksTo(1).rarity("RARE");
            case "music_disc_builder" -> properties.stacksTo(1).rarity("RARE");
            default -> properties;
        };
    }
}
