package com.kingodogo.buildscape.worldgen;

import java.util.List;

/** Reference biome additions shared with the loader hooks and data validation. */
public final class BiomePlacements {
    public record Entry(String biomeTag, List<String> features) {}
    public static final List<Entry> ENTRIES = List.of(
            new Entry("minecraft:buildscape/flower_forests", List.of("red_monets", "blue_monets", "purple_monets", "light_blue_monets", "pink_monets", "yellow_monets")),
            new Entry("minecraft:buildscape/birch_forests", List.of("all_petals", "clover", "wildflowers", "leaf_litter")),
            new Entry("minecraft:buildscape/lush_caves", List.of("all_colored_spore_blossom")),
            new Entry("minecraft:buildscape/swamps", List.of("clover_swamp", "mangrove_propagule_patch_1", "mangrove_propagule_patch_2", "mangrove_propagule_patch_3", "mangrove_propagule_patch_4", "mangrove_propagule_patch_5", "mangrove_propagule_patch_6", "mangrove_propagule_patch_7", "mangrove_propagule_patch_8")));

    private BiomePlacements() {}
}
