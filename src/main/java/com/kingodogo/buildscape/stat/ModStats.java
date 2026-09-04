package com.kingodogo.buildscape.stat;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
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
    public static final ResourceLocation FROSTY_ROSES_PLACED = new ResourceLocation(BuildScape.MODID, "frosty_roses_placed");
    public static final ResourceLocation CASCADE_BLOCKS_PLACED = new ResourceLocation(BuildScape.MODID, "cascade_blocks_placed");
    public static final ResourceLocation SMOKE_VENTS_PLACED = new ResourceLocation(BuildScape.MODID, "smoke_vents_placed");
    public static final ResourceLocation SMOKE_VENTS_DYED = new ResourceLocation(BuildScape.MODID, "smoke_vents_dyed");
    public static final ResourceLocation MUFF_BLOCKS_ACTIVATED = new ResourceLocation(BuildScape.MODID, "muff_blocks_activated");
    public static final ResourceLocation BOLTS_PLACED = new ResourceLocation(BuildScape.MODID, "bolts_placed");
    public static final ResourceLocation CONFETTI_USED = new ResourceLocation(BuildScape.MODID, "confetti_used");

    public static final ResourceLocation HEADER_MINECRAFT = new ResourceLocation(BuildScape.MODID, "header_minecraft");
    public static final ResourceLocation HEADER_BUILDSCAPE = new ResourceLocation(BuildScape.MODID, "header_buildscape");
    public static final ResourceLocation HEADER_OTHER = new ResourceLocation(BuildScape.MODID, "header_other");

    public static Stat<ResourceLocation> HEADER_MINECRAFT_STAT;
    public static Stat<ResourceLocation> HEADER_BUILDSCAPE_STAT;
    public static Stat<ResourceLocation> HEADER_OTHER_STAT;

    public static void registerStats() {
        HEADER_MINECRAFT_STAT = registerCustomStat("header_minecraft", HEADER_MINECRAFT);
        HEADER_BUILDSCAPE_STAT = registerCustomStat("header_buildscape", HEADER_BUILDSCAPE);
        HEADER_OTHER_STAT = registerCustomStat("header_other", HEADER_OTHER);

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
        registerCustomStat("frosty_roses_placed", FROSTY_ROSES_PLACED);
        registerCustomStat("cascade_blocks_placed", CASCADE_BLOCKS_PLACED);
        registerCustomStat("smoke_vents_placed", SMOKE_VENTS_PLACED);
        registerCustomStat("smoke_vents_dyed", SMOKE_VENTS_DYED);
        registerCustomStat("muff_blocks_activated", MUFF_BLOCKS_ACTIVATED);
        registerCustomStat("bolts_placed", BOLTS_PLACED);
        registerCustomStat("confetti_used", CONFETTI_USED);
    }

    private static Stat<ResourceLocation> registerCustomStat(String name, ResourceLocation id) {
        Registry.register(Registry.CUSTOM_STAT, name, id);
        return Stats.CUSTOM.get(id, StatFormatter.DEFAULT);
    }
}
