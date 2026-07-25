package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.block.ModBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers non-solid render layers in one pass over Buildscape's blocks.
 *
 * @author hoyin1600p
 */
public final class BuildscapeRenderLayers {
    private BuildscapeRenderLayers() {
    }

    public static void register() {
        for (RegistryObject<net.minecraft.world.level.block.Block> entry : ModBlocks.BLOCKS.getEntries()) {
            RenderType renderType = getRenderType(entry.getId().getPath());
            if (renderType != null) {
                ItemBlockRenderTypes.setRenderLayer(entry.get(), renderType);
            }
        }
    }

    private static RenderType getRenderType(String path) {
        if (
                path.contains("glass") ||
                path.endsWith("_ornament") ||
                path.endsWith("_string_light") ||
                path.startsWith("bit_") && path.endsWith("copper_grate") ||
                isTranslucentExact(path)
        ) {
            return RenderType.translucent();
        }

        if (path.contains("leaves") || path.endsWith("_leaf_hedge")) {
            return RenderType.cutoutMipped();
        }

        if (
                path.contains("wallpaper_flat") ||
                path.endsWith("_leaf_layers") ||
                path.endsWith("_decorated_pot") ||
                path.endsWith("_festive_stocking") ||
                path.endsWith("_star") ||
                path.endsWith("_chain") ||
                path.endsWith("_petal") ||
                path.endsWith("_spore_blossom") ||
                path.endsWith("_monets") ||
                path.endsWith("_rose_vines") ||
                path.endsWith("_door") ||
                path.endsWith("_trapdoor") ||
                path.endsWith("_sign") ||
                isCutoutExact(path)
        ) {
            return RenderType.cutout();
        }

        return null;
    }

    private static boolean isTranslucentExact(String path) {
        return switch (path) {
            case "cascade_block",
                    "cascade_block_no_mist",
                    "icicle_block",
                    "packed_icicle_block",
                    "steel_grate" -> true;
            default -> false;
        };
    }

    private static boolean isCutoutExact(String path) {
        return switch (path) {
            case "clover",
                    "decorated_pot",
                    "festive_stocking",
                    "frost_rose",
                    "glow_lights",
                    "icicle",
                    "mangrove_propagule",
                    "mangrove_roots",
                    "muddy_mangrove_roots",
                    "multicolor_glow_lights",
                    "brown_mushroom_shelves",
                    "red_mushroom_shelves",
                    "poplar_sapling",
                    "snowy_bush",
                    "snowy_fern",
                    "snowy_large_fern",
                    "snowy_short_grass",
                    "snowy_tall_grass",
                    "straw_bed",
                    "sulfur_spike",
                    "ashenking_diamond_pillar",
                    "ashenking_emerald_pillar",
                    "ashenking_gold_pillar",
                    "ashenking_netherite_pillar" -> true;
            default -> false;
        };
    }
}
