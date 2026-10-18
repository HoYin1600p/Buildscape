package com.kingodogo.buildscape.config;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Ordered defaults, including tag entries, shared by pillar items and presets. */
final class DefaultPillarItems {
    private static final com.google.gson.Gson GSON = new com.google.gson.Gson();
    private DefaultPillarItems() { }
    // ===== VANILLA MINECRAFT ITEMS =====
    private static final List<String> VANILLA = List.of(
            "minecraft:elder_guardian_spawn_egg",
            "minecraft:diamond_hoe",
            "minecraft:end_crystal",
            "minecraft:player_head",
            "minecraft:experience_bottle",
            "minecraft:netherite_chestplate",
            "minecraft:emerald",
            "minecraft:nether_star",
            "minecraft:trident",
            "minecraft:enchanting_table",
            "minecraft:skeleton_horse_spawn_egg",
            "minecraft:heart_of_the_sea",
            "minecraft:zoglin_spawn_egg",
            "minecraft:diamond_pickaxe",
            "minecraft:dragon_egg",
            "minecraft:nautilus_shell",
            "minecraft:totem_of_undying",
            "minecraft:diamond_block",
            "minecraft:netherite_pickaxe",
            "minecraft:bedrock",
            "minecraft:glow_squid_spawn_egg",
            "minecraft:netherite_ingot",
            "minecraft:slime_spawn_egg",
            "minecraft:enchanted_golden_apple",
            "minecraft:netherite_axe",
            "minecraft:golden_carrot",
            "minecraft:ancient_debris",
            "minecraft:beacon",
            "minecraft:written_book",
            "minecraft:creeper_spawn_egg",
            "minecraft:dragon_breath",
            "minecraft:zombie_head",
            "minecraft:skeleton_skull",
            "minecraft:conduit",
            "minecraft:elytra",
            "minecraft:axolotl_spawn_egg",
            "minecraft:golden_apple",
            "minecraft:netherite_hoe",
            "minecraft:netherite_helmet",
            "minecraft:creeper_head",
            "minecraft:netherite_block",
            "minecraft:ravager_spawn_egg",
            "minecraft:goat_spawn_egg",
            "minecraft:diamond_boots",
            "minecraft:deepslate_diamond_ore",
            "minecraft:wither_skeleton_skull",
            "minecraft:diamond_chestplate",
            "minecraft:netherite_boots",
            "minecraft:dragon_head",
            "minecraft:diamond_ore",
            "minecraft:diamond_shovel",
            "minecraft:netherite_leggings",
            "minecraft:mojang_banner_pattern",
            "minecraft:netherite_shovel",
            "minecraft:netherite_scrap",
            "minecraft:emerald_block",
            "minecraft:turtle_spawn_egg",
            "minecraft:diamond",
            "minecraft:diamond_sword",
            "minecraft:diamond_axe",
            "minecraft:diamond_helmet",
            "minecraft:villager_spawn_egg",
            "minecraft:gold_block",
            "minecraft:poisonous_potato",
            "minecraft:pufferfish",
            "minecraft:wither_skeleton_spawn_egg",
            "minecraft:enderman_spawn_egg",
            "minecraft:diamond_leggings",
            "minecraft:shulker_spawn_egg",
            "minecraft:spyglass",
            "minecraft:shulker_shell",
            "minecraft:netherite_sword");

    // ===== VAULT / MODDED ITEMS =====
    private static final List<String> VAULT = List.of(
            "the_vault:vault_crystal",
            "the_vault:echo_pog",
            "#the_vault:fruits",
            "the_vault:card_deck",
            "the_vault:neuralizer",
            "the_vault:vault_artifact",
            "the_vault:vault_god_charm",
            "the_vault:grinch_egg",
            "the_vault:antique",
            "#the_vault:gems",
            "the_vault:boost_modification_stone",
            "the_vault:treasure_keyring",
            "#the_vault:keys",
            "the_vault:deck_socket",
            "the_vault:yeti_egg",
            "the_vault:soul_vortex",
            "#the_vault:magnet",
            "the_vault:santa_egg",
            "the_vault:knowledge_star",
            "the_vault:tool",
            "#the_vault:playerchunks",
            "the_vault:gem_pog",
            "the_vault:pvp_trophy",
            "the_vault:spicy_hearty_burger",
            "#the_vault:playerclusters",
            "the_vault:herald_trophy",
            "the_vault:omega_pog",
            "the_vault:unidentified_artifact",
            "#the_vault:vault_gear",
            "#the_vault:crystal_capstones",
            "#the_vault:unique",
            "the_vault:companion_egg",
            "#the_vault:perfectgems",
            "#the_vault:companion_relic",
            "the_vault:greedy_meal",
            "the_vault:greed_coin",
            "#the_vault:sigil",
            "the_vault:unique_shard",
            "#the_vault:vault_globe",
            "the_vault:greedy_anchor",
            "the_vault:objective_modifier_stumble",
            "the_vault:objective_modifier_shuffle",
            "#the_vault:trinket",
            "#the_vault:etching",
            "#the_vault:greed_trinket");

    // ===== BUILDSCAPE ITEMS =====
    private static final List<String> BUILDSCAPE = List.of(
            "buildscape:ancient_ashen_scroll",
            "buildscape:ashenking_gold_pillar",
            "buildscape:ashenking_diamond_pillar",
            "buildscape:ashenking_emerald_pillar",
            "buildscape:ashenking_netherite_pillar",
            "buildscape:music_disc_celebration",
            "buildscape:music_disc_builder",
            "buildscape:music_disc_snowfall",
            "buildscape:festive_glint_shard",
            "buildscape:builders_pouch",
            "buildscape:diamond_hammer",
            "buildscape:netherite_hammer",
            "buildscape:diamond_biome_brush",
            "buildscape:netherite_biome_brush",
            "buildscape:confetti",
            "buildscape:stone_pillar_trophy",
            "buildscape:gold_pillar_trophy",
            "buildscape:emerald_pillar_trophy",
            "buildscape:diamond_pillar_trophy",
            "buildscape:netherite_pillar_trophy",
            "buildscape:golden_jar_trophy",
            "buildscape:firelight_trophy",
            "buildscape:diamond_hammer_trophy",
            "buildscape:gold_buildscape_trophy",
            "buildscape:emerald_buildscape_trophy",
            "buildscape:diamond_buildscape_trophy",
            "buildscape:netherite_buildscape_trophy",
            "buildscape:gold_ornament_trophy",
            "buildscape:emerald_stocking_trophy",
            "buildscape:star_trophy",
            "buildscape:string_light_trophy",
            "buildscape:big_ornament_template",
            "buildscape:festive_star_pattern",
            "buildscape:golden_jar_pattern",
            "buildscape:stringlight_frame_pattern",
            "buildscape:infinite_phoenix_firework_star",
            "buildscape:bone_dice");

    static Set<String> baseItems() {
        Set<String> items = new LinkedHashSet<>(VANILLA);
        items.addAll(BUILDSCAPE);
        return items;
    }

    static List<String> vaultItems() {
        return VAULT;
    }

    static Set<String> items(boolean vaultLoaded) {
        Set<String> items = baseItems();
        if (vaultLoaded) items.addAll(VAULT);
        return items;
    }

    static boolean merge405(com.google.gson.JsonObject document, Set<String> items, boolean vaultLoaded) {
        boolean changed = false;
        // An empty list already allows every item; narrowing it would change its meaning.
        boolean unrestricted = items.isEmpty();
        if (!document.has("defaults_version")) {
            if (!unrestricted) items.addAll(BUILDSCAPE.subList(1, BUILDSCAPE.size()));
            document.addProperty("defaults_version", 405);
            changed = true;
        }
        if (vaultLoaded && !document.has("vault_defaults_version")) {
            if (!unrestricted) items.addAll(VAULT.subList(33, VAULT.size()));
            document.addProperty("vault_defaults_version", 405);
            changed = true;
        }
        return changed;
    }

    static void writeGrouped(Writer writer, boolean vaultLoaded) throws IOException {
        writer.write("{\n  // Item IDs that trigger particles when placed on pillars\n  \"items\": [\n");
        List<List<String>> groups = vaultLoaded
                ? List.of(VANILLA, VAULT, BUILDSCAPE) : List.of(VANILLA, BUILDSCAPE);
        List<String> comments = vaultLoaded
                ? List.of("===== VANILLA MINECRAFT ITEMS =====", "===== VAULT / MODDED ITEMS =====", "===== BUILDSCAPE ITEMS =====")
                : List.of("===== VANILLA MINECRAFT ITEMS =====", "===== BUILDSCAPE ITEMS =====");
        for (int g = 0; g < groups.size(); g++) {
            if (g > 0) writer.write("\n");
            writer.write("    // " + comments.get(g) + "\n");
            List<String> group = groups.get(g);
            for (int i = 0; i < group.size(); i++) {
                writer.write("    " + GSON.toJson(group.get(i)));
                if (g < groups.size() - 1 || i < group.size() - 1) writer.write(",");
                writer.write("\n");
            }
        }
        writer.write("  ],\n  \"defaults_version\": 405");
        if (vaultLoaded) writer.write(",\n  \"vault_defaults_version\": 405");
        writer.write("\n}\n");
    }
}
