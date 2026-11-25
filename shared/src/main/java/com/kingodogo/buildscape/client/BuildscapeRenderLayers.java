package com.kingodogo.buildscape.client;
public final class BuildscapeRenderLayers {

    public enum Layer {
        SOLID,
        CUTOUT,
        CUTOUT_MIPPED,
        TRANSLUCENT
    }

    private BuildscapeRenderLayers() {}
    public static Layer getRenderLayer(String path) {
        if (path == null) {
            return Layer.SOLID;
        }

        if (path.contains("glass")
                || path.endsWith("_ornament")
                || path.endsWith("_string_light")
                || (path.startsWith("bit_") && path.endsWith("copper_grate"))
                || path.endsWith("flaming_steel_grate")
                || isTranslucentExact(path)) {
            return Layer.TRANSLUCENT;
        }

        if (path.contains("leaves") || path.endsWith("_leaf_hedge")) {
            return Layer.CUTOUT_MIPPED;
        }

        if (path.startsWith("potted_")
                || path.endsWith("_haze_bush")
                || path.equals("ice_crystal")
                || path.contains("hollow")
                || path.contains("wallpaper_flat")
                || path.contains("steel_mesh_block")
                || path.endsWith("_leaf_layers")
                || path.endsWith("_decorated_pot")
                || path.endsWith("_festive_stocking")
                || path.endsWith("_star")
                || path.endsWith("_chain")
                || path.endsWith("_petal")
                || path.endsWith("_spore_blossom")
                || path.endsWith("_monets")
                || path.endsWith("_trophy")
                || path.endsWith("_rose_vines")
                || path.endsWith("_door")
                || path.endsWith("_trapdoor")
                || path.endsWith("_bolts")
                || path.endsWith("_sign")
                || path.endsWith("_sapling")
                || path.endsWith("_bars")
                || path.endsWith("_mesh")
                || path.endsWith("_ladder")
                || isBackportCutout(path)
                || isCutoutExact(path)) {
            return Layer.CUTOUT;
        }

        return Layer.SOLID;
    }
    private static boolean isBackportCutout(String path) {
        return switch (path) {
            case "pale_hanging_moss",
                    "pale_moss_carpet",
                    "pale_moss_layers",
                    "pale_moss_overlay",
                    "red_bush",
                    "resin_clump" -> true;
            default -> false;
        };
    }
    private static boolean isTranslucentExact(String path) {
        return switch (path) {
            case "cascade_block",
                    "cascade_block_no_mist",
                    "golden_jar",
                    "icicle_block",
                    "packed_icicle_block",
                    "steel_grate" -> true;
            default -> false;
        };
    }
    private static boolean isCutoutExact(String path) {
        return switch (path) {
            case "clover",
                    "festive_stocking",
                    "frost_rose",
                    "glow_lights",
                    "icicle",
                    "multicolor_glow_lights",
                    "brown_mushroom_shelves",
                    "red_mushroom_shelves",
                    "poplar_sapling",
                    "snowy_bush",
                    "snowy_fern",
                    "snowy_large_fern",
                    "snowy_short_grass",
                    "snowy_tall_grass",
                    "steel_bolts",
                    "straw_bed",
                    "ashenking_diamond_pillar",
                    "ashenking_emerald_pillar",
                    "ashenking_gold_pillar",
                    "ashenking_netherite_pillar" -> true;
            default -> false;
        };
    }
}
