package com.kingodogo.buildscape.variantengine.util;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import net.minecraft.resources.ResourceLocation;

/**
 * Utility class for generating registry IDs and language names for variants.
 */
public class VariantNamingUtil {

    /**
     * Generates a unique registry ID for a variant block based on its parent and shape.
     * Format: buildscape:{shape}/{original_namespace}/{original_path}
     */
    public static ResourceLocation getGeneratedId(ResourceLocation baseId, BlockShape shape) {
        String shapePrefix = shape == BlockShape.VERTICAL_SLAB ? "v_slab" : 
                             shape == BlockShape.VERTICAL_STAIRS ? "v_stair" : 
                             shape == BlockShape.QUARTER_PIECE ? "q_piece" : 
                             shape == BlockShape.VERTICAL_QUARTER_PIECE ? "vq_piece" : shape.asString();
        
        String ns = baseId.getNamespace().toLowerCase();
        String path = baseId.getPath().toLowerCase();

        // 1. Aggressively clean the path to remove horizontal/generated keywords
        path = path.replace("_stairs", "").replace("_stair", "").replace("_slab", "")
                   .replace("stairs_", "").replace("stair_", "").replace("slab_", "");
        
        // Remove our own variant prefixes if they somehow ended up here from an orphan search
        if (path.startsWith("v_slab_")) path = path.substring(7);
        if (path.startsWith("v_stair_")) path = path.substring(8);
        if (path.startsWith("vslab_")) path = path.substring(6);
        if (path.startsWith("vstair_")) path = path.substring(7);

        path = path.replaceAll("^_+|_+$", "");

        // 2. Build the final path.
        // We want: buildscape:v_slab_oak (for minecraft)
        // We want: buildscape:v_slab_yellow_concrete (for buildscape)
        // We want: buildscape:v_slab_biomesoplenty_fir_log (for other mods)
        String finalPath;
        if (ns.equals("minecraft") || ns.equalsIgnoreCase(BuildScape.MODID)) {
            finalPath = shapePrefix + "_" + path;
        } else {
            finalPath = shapePrefix + "_" + ns + "_" + path;
        }

        return new ResourceLocation(BuildScape.MODID, finalPath);
    }

    /**
     * Generates a user-friendly name for a variant based on its parent and shape.
     * Example: "Oak Log" + "Vertical Slab" -> "Oak Vertical Slab"
     */
    public static String generateLangName(ResourceLocation parentId, BlockShape shape) {
        String path = parentId.getPath().toLowerCase();
        // Remove common suffixes to avoid "Oak Slab Vertical Slab"
        path = path.replace("_stairs", "").replace("_stair", "").replace("_slab", "")
                   .replace("stairs_", "").replace("stair_", "").replace("slab_", "")
                   .replace("_planks", "").replace("_plank", "")
                   .replace("_bricks", "").replace("_brick", "")
                   .replace("_block", "");
        path = path.replaceAll("^_+|_+$", "");

        String[] words = path.split("_");
        StringBuilder name = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            // Skip redundant shape words that might still be in the path
            if (word.equals("slab") || word.equals("stair") || word.equals("stairs")) continue;
            
            name.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                name.append(word.substring(1).toLowerCase());
            }
            name.append(" ");
        }

        String shapeName = shape.asString().replace("_", " ");
        String[] shapeWords = shapeName.split(" ");
        for (String word : shapeWords) {
            if (word.isEmpty()) continue;
            name.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                name.append(word.substring(1).toLowerCase());
            }
            name.append(" ");
        }

        return name.toString().trim();
    }
}
