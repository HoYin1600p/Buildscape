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
        String path = shapePrefix + "_" + baseId.getNamespace() + "_" + baseId.getPath();
        return new ResourceLocation(BuildScape.MODID, path);
    }

    /**
     * Generates a user-friendly name for a variant based on its parent and shape.
     * Example: "Oak Log" + "Vertical Slab" -> "Oak Vertical Slab"
     */
    public static String generateLangName(ResourceLocation parentId, BlockShape shape) {
        String path = parentId.getPath();
        // Remove common suffixes to avoid "Oak Slab Vertical Slab"
        path = path.replace("_slab", "").replace("_stairs", "").replace("_stair", "");

        String[] words = path.split("_");
        StringBuilder name = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
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
