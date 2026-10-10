package com.kingodogo.buildscape.stat;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.stats.Stat;

public final class ModStats {
    public static final CommonId INTERACT_WITH_PILLAR = id("interact_with_pillar");
    public static final CommonId HAMMER_USED = id("hammer_used");
    public static final CommonId BLOCKS_PLACED = id("blocks_placed");
    public static final CommonId HOLLOW_LOGS_PLACED = id("hollow_logs_placed");
    public static final CommonId ICICLES_PLACED = id("icicles_placed");
    public static final CommonId ORNAMENTS_PLACED = id("ornaments_placed");
    public static final CommonId STRING_LIGHTS_PLACED = id("string_lights_placed");
    public static final CommonId STARS_PLACED = id("stars_placed");
    public static final CommonId SNOWY_LEAVES_PLACED = id("snowy_leaves_placed");
    public static final CommonId JARS_CRAFTED = id("jars_crafted");
    public static final CommonId STOCKINGS_CRAFTED = id("stockings_crafted");
    public static final CommonId FROSTY_ROSES_PLACED = id("frosty_roses_placed");
    public static final CommonId CASCADE_BLOCKS_PLACED = id("cascade_blocks_placed");
    public static final CommonId SMOKE_VENTS_PLACED = id("smoke_vents_placed");
    public static final CommonId SMOKE_VENTS_DYED = id("smoke_vents_dyed");
    public static final CommonId MUFF_BLOCKS_ACTIVATED = id("muff_blocks_activated");
    public static final CommonId BOLTS_PLACED = id("bolts_placed");
    public static final CommonId CONFETTI_USED = id("confetti_used");

    public static final CommonId HEADER_MINECRAFT = id("header_minecraft");
    public static final CommonId HEADER_BUILDSCAPE = id("header_buildscape");
    public static final CommonId HEADER_OTHER = id("header_other");

    public static Stat<?> HEADER_MINECRAFT_STAT;
    public static Stat<?> HEADER_BUILDSCAPE_STAT;
    public static Stat<?> HEADER_OTHER_STAT;
    private static boolean registered;

    private ModStats() {
    }

    public static synchronized void registerStats() {
        if (registered) return;
        HEADER_MINECRAFT_STAT = register("header_minecraft", HEADER_MINECRAFT);
        HEADER_BUILDSCAPE_STAT = register("header_buildscape", HEADER_BUILDSCAPE);
        HEADER_OTHER_STAT = register("header_other", HEADER_OTHER);

        register("interact_with_pillar", INTERACT_WITH_PILLAR);
        register("hammer_used", HAMMER_USED);
        register("blocks_placed", BLOCKS_PLACED);
        register("hollow_logs_placed", HOLLOW_LOGS_PLACED);
        register("icicles_placed", ICICLES_PLACED);
        register("ornaments_placed", ORNAMENTS_PLACED);
        register("string_lights_placed", STRING_LIGHTS_PLACED);
        register("stars_placed", STARS_PLACED);
        register("snowy_leaves_placed", SNOWY_LEAVES_PLACED);
        register("jars_crafted", JARS_CRAFTED);
        register("stockings_crafted", STOCKINGS_CRAFTED);
        register("frosty_roses_placed", FROSTY_ROSES_PLACED);
        register("cascade_blocks_placed", CASCADE_BLOCKS_PLACED);
        register("smoke_vents_placed", SMOKE_VENTS_PLACED);
        register("smoke_vents_dyed", SMOKE_VENTS_DYED);
        register("muff_blocks_activated", MUFF_BLOCKS_ACTIVATED);
        register("bolts_placed", BOLTS_PLACED);
        register("confetti_used", CONFETTI_USED);
        registered = true;
    }

    private static Stat<?> register(String name, CommonId id) {
        return Services.PLATFORM.registerCustomStat(name, id);
    }

    private static CommonId id(String path) {
        return CommonId.of(BuildscapeCommon.MOD_ID, path);
    }
}
