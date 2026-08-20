package com.kingodogo.buildscape.stat;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public class ModStats {
    public static final ResourceLocation INTERACT_WITH_PILLAR = new ResourceLocation(BuildScape.MODID, "interact_with_pillar");
    public static final ResourceLocation HAMMER_USED = new ResourceLocation(BuildScape.MODID, "hammer_used");
    public static final ResourceLocation BLOCKS_PLACED = new ResourceLocation(BuildScape.MODID, "blocks_placed");
    public static final ResourceLocation HOLLOW_LOGS_PLACED = new ResourceLocation(BuildScape.MODID, "hollow_logs_placed");
    public static final ResourceLocation ICICLES_PLACED = new ResourceLocation(BuildScape.MODID, "icicles_placed");
    public static final ResourceLocation ORNAMENTS_PLACED = new ResourceLocation(BuildScape.MODID, "ornaments_placed");
    public static final ResourceLocation STRING_LIGHTS_PLACED = new ResourceLocation(BuildScape.MODID, "string_lights_placed");
    public static final ResourceLocation STARS_PLACED = new ResourceLocation(BuildScape.MODID, "stars_placed");
    public static final ResourceLocation SNOWY_LEAVES_PLACED = new ResourceLocation(BuildScape.MODID, "snowy_leaves_placed");
    public static final ResourceLocation JARS_CRAFTED = new ResourceLocation(BuildScape.MODID, "jars_crafted");
    public static final ResourceLocation STOCKINGS_CRAFTED = new ResourceLocation(BuildScape.MODID, "stockings_crafted");

    public static void registerStats() {
        registerCustomStat("interact_with_pillar", INTERACT_WITH_PILLAR);
        registerCustomStat("hammer_used", HAMMER_USED);
        registerCustomStat("blocks_placed", BLOCKS_PLACED);
        registerCustomStat("hollow_logs_placed", HOLLOW_LOGS_PLACED);
        registerCustomStat("icicles_placed", ICICLES_PLACED);
        registerCustomStat("ornaments_placed", ORNAMENTS_PLACED);
        registerCustomStat("string_lights_placed", STRING_LIGHTS_PLACED);
        registerCustomStat("stars_placed", STARS_PLACED);
        registerCustomStat("snowy_leaves_placed", SNOWY_LEAVES_PLACED);
        registerCustomStat("jars_crafted", JARS_CRAFTED);
        registerCustomStat("stockings_crafted", STOCKINGS_CRAFTED);
    }

    private static void registerCustomStat(String name, ResourceLocation id) {
        Registry.register(Registry.CUSTOM_STAT, name, id);
        Stats.CUSTOM.get(id, StatFormatter.DEFAULT);
    }
}
