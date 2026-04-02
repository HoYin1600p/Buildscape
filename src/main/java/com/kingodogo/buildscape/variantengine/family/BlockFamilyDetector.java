package com.kingodogo.buildscape.variantengine.family;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import com.kingodogo.buildscape.variantengine.util.BlockDetectionUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

/**
 * Detects block families for the VariantEngine.
 *
 * KEY DESIGN: We process EVERY block that passes through. For each block:
 *   - If it IS a slab/stair → we track it and try to find its base block.
 *   - If it is NOT a slab/stair → we check if it HAS a slab/stair in the registry.
 *
 * BLACKLIST: Applied to the "core name" (shape suffixes stripped) so that
 * "spruce_tiki_torch_slab" is caught even though the block IS a valid slab.
 */
public class BlockFamilyDetector {

    private static final String[] SHAPE_SUFFIXES = {"_slab", "slab_", "_stairs", "_stair", "stairs_", "stair_"};
    private static final String[] BLOCK_SUFFIXES = {"_bricks", "_brick", "_tiles", "_tile", "_planks", "_plank", "_block", "_log", "_wood", "_stem", "_hyphae", "_bale"};
    private static final String[] VERTICAL_MARKERS = {"_vertical_", "vertical_", "v_slab", "v_stair", "vslab", "vstair", "_v_"};

    // -----------------------------------------------------------------------
    // Blacklisted KEYWORDS — if the "core name" (after stripping slab/stair
    // suffixes) contains ANY of these, the block is rejected.
    // This catches things like "spruce_tiki_torch_slab" because the core
    // "spruce_tiki_torch" contains "torch".
    // -----------------------------------------------------------------------
    private static final String[] BLACKLISTED_KEYWORDS = {
        // Lighting & decoration
        "torch", "lantern", "candle", "lamp", "light_bulb", "sconce",
        // Flora & nature
        "flower", "grass_", "shroom", "mushroom", "sapling", "crop", "seed",
        "leaves", "vine", "bush", "fern", "azalea", "moss_carpet",
        "lily", "rose", "tulip", "daisy", "orchid", "allium", "cornflower",
        "dripleaf", "spore", "wart", "kelp", "seagrass", "cactus", "reeds", "sugar_cane", "cane",
        // Furniture & containers
        "chest", "barrel", "table", "shelf", "shelves", "bed", "chair",
        "bench", "stool", "counter", "cabinet", "drawer", "wardrobe",
        "desk", "bookshelf", "hourglass", "jar", "vial", "bottle",
        // Redstone & technical
        "button", "lever", "rail", "tripwire", "repeater", "comparator",
        "piston", "observer", "dropper", "dispenser", "hopper",
        "tnt", "brewing", "enchant", "furnace", "smoker",
        "blast_furnace", "cauldron",
        // Decorative non-building
        "sign", "banner", "carpet", "pot", "painting", "frame",
        "head", "skull", "statue", "icon", "decorative",
        "ring", "gear", "star", "vent", "ornament", "bamboo",
        // Additional decorative blocks (user request)
        "sack", "urn", "pouch", "compressium", "hourglass",
        // Functional/Technical
        "door", "trapdoor", "ladder", "bars", "wall", "fence", "gate",
        "barrier", "jigsaw", "structure", "spawner",
        // Equipment & items (shouldn't be blocks but some mods...)
        "sword", "pickaxe", "axe", "shovel", "hoe",
        "helmet", "chestplate", "leggings", "boots", "shield", "pattern",
        // Misc
        "anvil", "bell", "grindstone", "stonecutter", "loom",
        "smithing", "cartography", "fletching", "composter",
        "beehive", "bee_nest", "conduit", "end_portal", "nether_portal",
        "respawn_anchor", "lodestone", "target", "sculk",
        "canopy", "nest", "web", "cobweb", "string", "chain",
        "command_block", "debug", "petrified", "infested", "egg",
        // Mod-specific decorative
        "tiki", "wind_vane", "weather_vane", "flag", "planter",
        "hanging_pot", "jar", "cage", "hook", "rope",
        "clock", "globe", "notice_board", "hat_stand",
        "sconce", "chandelier", "pergola", "lattice",
        // Exemptions for user requests (exact matches or very specific substrings)
        "pumpkin_stem", "melon_stem", "attached_", "pumpkin_seed", "melon_seed", "void_air", "cave_air"
    };

    public static BlockFamily detectFamily(Block block) {
        return detectFamily(block, ForgeRegistries.BLOCKS);
    }

    public static BlockFamily detectFamily(Block block, IForgeRegistry<Block> registry) {
        try {
            return detectFamilyInternal(block, registry);
        } catch (Exception e) {
            return null;
        }
    }

    private static BlockFamily detectFamilyInternal(Block block, IForgeRegistry<Block> registry) {
        ResourceLocation id = block.getRegistryName();
        if (id == null) return null;

        String path = id.getPath().toLowerCase();
        String namespace = id.getNamespace().toLowerCase();

        // Skip our own generated variants — they'll be handled by the registrar
        if (namespace.equals(BuildScape.MODID) && (path.startsWith("v_slab_") || path.startsWith("v_stair_") || path.startsWith("q_piece_"))) {
            return null;
        }

        if (block == Blocks.AIR || block == Blocks.CAVE_AIR || block == Blocks.VOID_AIR) {
            return null;
        }

        // ---------------------------------------------------------------
        // CORE NAME BLACKLIST: Strip slab/stair suffixes FIRST, then check.
        // This catches "spruce_tiki_torch_slab" because the core name
        // "spruce_tiki_torch" contains "torch".
        // ---------------------------------------------------------------
        String coreName = stripShapeSuffixes(path);
        if (namespace.equals("compressium") || isCoreNameBlacklisted(coreName, namespace)) return null;

        // === DETECTION: What kind of block is this? ===
        boolean isSlab = BlockDetectionUtil.isSlab(block);
        boolean isStair = BlockDetectionUtil.isStair(block);
        boolean isVertical = (path.contains("vertical") || path.contains("v_slab") || path.contains("v_stair") || path.contains("vslab") || path.contains("vstair"));

        // === STRATEGY A: Block IS a slab/stair — find its base, create a family ===
        if (isSlab || isStair || isVertical) {
            Block base = findBaseBlock(block, registry);
            if (base == null) base = block; // Orphan: slab/stair with no base block found
            if (base == Blocks.AIR) return null;

            // ALSO blacklist-check the BASE block's core name
            ResourceLocation baseId = base.getRegistryName();
            if (baseId != null) {
                String baseCoreName = stripShapeSuffixes(baseId.getPath().toLowerCase());
                if (isCoreNameBlacklisted(baseCoreName, baseId.getNamespace().toLowerCase())) return null;
            }

            BlockFamily family = new BlockFamily(base);

            // Assign the detected block to its shape
            if (isSlab) family.addVariant(BlockShape.SLAB, block);
            if (isStair) family.addVariant(BlockShape.STAIRS, block);
            if (isVertical && path.contains("slab")) family.addVariant(BlockShape.VERTICAL_SLAB, block);
            else if (isVertical) family.addVariant(BlockShape.VERTICAL_STAIRS, block);

            // Scan registry for any sibling variants we can find
            scanForSiblings(base, family, registry);

            return family;
        }

        // === STRATEGY B: Block is NOT a slab/stair — check if it SHOULD have variants ===
        if (!isLikelyBuildingBlock(block)) return null;

        // See if this base block has a slab or stair in the registry
        Block slab = findCompanionMultiNs(id, "_slab", registry);
        if (slab == null) slab = findCompanionMultiNs(id, "_slabs", registry);

        Block stair = findCompanionMultiNs(id, "_stairs", registry);
        if (stair == null) stair = findCompanionMultiNs(id, "_stair", registry);

        // Also try stripped forms: "oak_planks" → "oak_slab", "oak_stairs"
        // CRITICAL: Do NOT strip for logs/wood/stems, otherwise "oak_log" finds "oak_slab".
        if ((slab == null || stair == null) && !path.contains("log") && !path.contains("wood") && !path.contains("_stem") && !path.contains("_hyphae")) {
            String stripped = stripMaterialSuffix(path);
            if (!stripped.equals(path)) {
                if (slab == null) {
                    slab = findCompanionAllNs(namespace, stripped, "_slab", registry);
                    if (slab == null) slab = findCompanionAllNs(namespace, stripped, "_slabs", registry);
                }
                if (stair == null) {
                    stair = findCompanionAllNs(namespace, stripped, "_stairs", registry);
                    if (stair == null) stair = findCompanionAllNs(namespace, stripped, "_stair", registry);
                }
            }
        }

        if (slab == null && stair == null) {
            // even if they don't have standard horizontal slabs/stairs.
            boolean isLogOrFalling = path.contains("log") || path.contains("wood") || path.contains("_stem") || path.contains("_hyphae") || 
                                   block instanceof net.minecraft.world.level.block.FallingBlock || path.contains("sand") || path.contains("gravel");
            
            if (isGlass(block) || isLogOrFalling) {
                return new BlockFamily(block);
            }
            // Return base family for all full blocks to configure them in dashboard
            return new BlockFamily(block);
        }

        BlockFamily family = new BlockFamily(block);
        if (slab != null) family.addVariant(BlockShape.SLAB, slab);
        if (stair != null) family.addVariant(BlockShape.STAIRS, stair);

        return family;
    }

    public static boolean isGlass(Block block) {
        if (block == null) return false;
        ResourceLocation id = block.getRegistryName();
        if (id == null) return false;
        String path = id.getPath().toLowerCase();
        return block instanceof net.minecraft.world.level.block.GlassBlock ||
               block instanceof net.minecraft.world.level.block.StainedGlassBlock ||
               path.endsWith("glass") || path.equals("glass");
    }

    /**
     * Given a base block, look for sibling slab/stairs AND existing vertical variants in the registry.
     */
    private static void scanForSiblings(Block base, BlockFamily family, IForgeRegistry<Block> registry) {
        ResourceLocation baseId = base.getRegistryName();
        if (baseId == null) return;

        String basePath = baseId.getPath().toLowerCase();
        String ns = baseId.getNamespace();

        // Clean the base path of any shape suffixes
        String cleaned = stripShapeSuffixes(basePath);

        // ---- Scan for horizontal slab/stair ----
        if (!family.hasVariant(BlockShape.SLAB)) {
            Block slab = findCompanionAllNs(ns, cleaned, "_slab", registry);
            if (slab != null) family.addVariant(BlockShape.SLAB, slab);
        }
        if (!family.hasVariant(BlockShape.STAIRS)) {
            Block stair = findCompanionAllNs(ns, cleaned, "_stairs", registry);
            if (stair == null) stair = findCompanionAllNs(ns, cleaned, "_stair", registry);
            if (stair != null) family.addVariant(BlockShape.STAIRS, stair);
        }

        // Also try with material suffix stripped
        // CRITICAL: Do NOT strip for logs/wood/stems, otherwise "oak_log" finds "oak_slab".
        String strippedMat = stripMaterialSuffix(cleaned);
        if (!strippedMat.equals(cleaned) && !basePath.contains("log") && !basePath.contains("wood") && !basePath.contains("_stem") && !basePath.contains("_hyphae")) {
            if (!family.hasVariant(BlockShape.SLAB)) {
                Block slab = findCompanionAllNs(ns, strippedMat, "_slab", registry);
                if (slab != null) family.addVariant(BlockShape.SLAB, slab);
            }
            if (!family.hasVariant(BlockShape.STAIRS)) {
                Block stair = findCompanionAllNs(ns, strippedMat, "_stairs", registry);
                if (stair == null) stair = findCompanionAllNs(ns, strippedMat, "_stair", registry);
                if (stair != null) family.addVariant(BlockShape.STAIRS, stair);
            }
        }

        // ---- Scan for EXISTING vertical variants from other mods ----
        // If another mod already provides vertical slabs/stairs (e.g., "vertical_oak_slab"),
        // mark them in the family so we don't create duplicates.
        if (!family.hasVariant(BlockShape.VERTICAL_SLAB)) {
            Block existing = findExistingVerticalVariant(ns, cleaned, "slab", registry);
            if (existing != null) family.addVariant(BlockShape.VERTICAL_SLAB, existing);
        }
        if (!family.hasVariant(BlockShape.VERTICAL_STAIRS)) {
            Block existing = findExistingVerticalVariant(ns, cleaned, "stairs", registry);
            if (existing == null) existing = findExistingVerticalVariant(ns, cleaned, "stair", registry);
            if (existing != null) family.addVariant(BlockShape.VERTICAL_STAIRS, existing);
        }
    }

    /**
     * Search for existing vertical variants from other mods.
     * Checks patterns like: vertical_oak_slab, oak_vertical_slab, v_slab_oak, vslab_oak, etc.
     */
    private static Block findExistingVerticalVariant(String primaryNs, String baseName, String shapeStr, IForgeRegistry<Block> registry) {
        // Common naming patterns for vertical variants
        String[][] patterns = {
            // {namespace_hint, path}
            {primaryNs, "vertical_" + baseName + "_" + shapeStr},      // vertical_oak_slab
            {primaryNs, baseName + "_vertical_" + shapeStr},           // oak_vertical_slab
            {primaryNs, "v_" + shapeStr + "_" + baseName},            // v_slab_oak (our pattern, but from other ns)
            {primaryNs, "v" + shapeStr + "_" + baseName},             // vslab_oak
            {primaryNs, baseName + "_v_" + shapeStr},                 // oak_v_slab
        };

        // Check in primary namespace AND common mod namespaces that provide vertical variants
        String[] namespacesToCheck = {primaryNs, "v_slab", "vertical_slabs", "verticalslabs", "everycomp", "quark", "v_plus"};

        for (String checkNs : namespacesToCheck) {
            for (String[] pattern : patterns) {
                Block b = tryGetBlock(checkNs, pattern[1], registry);
                if (b != null) return b;
            }
        }

        return null;
    }

    // -----------------------------------------------------------------------
    // Companion finders
    // -----------------------------------------------------------------------

    /** Look for companion in the same namespace only. */
    private static Block findCompanionMultiNs(ResourceLocation baseId, String suffix, IForgeRegistry<Block> registry) {
        // Try same namespace first
        Block b = findCompanion(baseId.getNamespace(), baseId.getPath(), suffix, registry);
        if (b != null) return b;
        // Try everycomp (many modpacks use this)
        b = findCompanion("everycomp", baseId.getPath(), suffix, registry);
        return b;
    }

    /** Look for companion across multiple likely namespaces. */
    private static Block findCompanionAllNs(String primaryNs, String basePath, String suffix, IForgeRegistry<Block> registry) {
        Block b = findCompanion(primaryNs, basePath, suffix, registry);
        if (b != null) return b;
        // Try minecraft as fallback
        if (!primaryNs.equals("minecraft")) {
            b = findCompanion("minecraft", basePath, suffix, registry);
            if (b != null) return b;
        }
        return null;
    }

    private static Block findCompanion(String namespace, String basePath, String suffix, IForgeRegistry<Block> registry) {
        ResourceLocation candidateId = new ResourceLocation(namespace, basePath + suffix);
        if (registry.containsKey(candidateId)) {
            Block b = registry.getValue(candidateId);
            if (b != null && b != Blocks.AIR) return b;
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // Find base block for a slab/stair by stripping shape suffixes
    // -----------------------------------------------------------------------
    private static Block findBaseBlock(Block block, IForgeRegistry<Block> registry) {
        ResourceLocation id = block.getRegistryName();
        if (id == null) return null;
        String path = id.getPath();
        String ns = id.getNamespace();

        // Strip shape suffixes and vertical markers
        String cleaned = stripShapeSuffixes(path);
        for (String s : VERTICAL_MARKERS) cleaned = cleaned.replace(s, "");
        cleaned = cleaned.replaceAll("^_+|_+$", "");

        if (cleaned.isEmpty()) return null;

        // Search strategy: same namespace first, then minecraft, then buildscape
        String[] namespaces = {ns, "minecraft", "buildscape"};

        for (String currentNs : namespaces) {
            // Direct match
            Block b = tryGetBlock(currentNs, cleaned, registry);
            if (b != null) return b;

            // Plural form
            b = tryGetBlock(currentNs, cleaned + "s", registry);
            if (b != null) return b;

            // Try with common block suffixes
            for (String s : BLOCK_SUFFIXES) {
                b = tryGetBlock(currentNs, cleaned + s, registry);
                if (b != null) return b;
                b = tryGetBlock(currentNs, cleaned + s + "s", registry);
                if (b != null) return b;
            }

            // Try stripping common prefixes
            String[] modPrefixes = {"polished_", "cut_", "smooth_", "cracked_", "mossy_", "waxed_", "exposed_", "weathered_", "oxidized_"};
            for (String m : modPrefixes) {
                if (cleaned.startsWith(m)) {
                    b = tryGetBlock(currentNs, cleaned.substring(m.length()), registry);
                    if (b != null) return b;
                }
            }
        }

        return null;
    }

    // -----------------------------------------------------------------------
    // Core Name Blacklist: strips slab/stair suffixes FIRST, then checks.
    // -----------------------------------------------------------------------
    private static boolean isCoreNameBlacklisted(String coreName, String namespace) {
        if (coreName == null || coreName.isEmpty()) return true;

        // Check against all blacklisted keywords
        for (String keyword : BLACKLISTED_KEYWORDS) {
            if (coreName.contains(keyword)) return true;
        }

        // buildscape:quartz_pillar is a block entity
        if (namespace.equals("buildscape") && coreName.equals("quartz_pillar")) return true;

        // Structural blocks (pillars/columns) for vanilla/buildscape only
        boolean isStructural = coreName.contains("pillar") || coreName.contains("column") ||
                             coreName.contains("beam") || coreName.contains("post");
        if (isStructural && (namespace.equals("minecraft") || namespace.equals("buildscape")) && !coreName.contains("ashenking") && !coreName.contains("vertical")) {
            return true;
        }

        return false;
    }

    public static boolean isFallingBlock(Block block) {
        if (block instanceof net.minecraft.world.level.block.FallingBlock) return true;
        ResourceLocation id = block.getRegistryName();
        if (id != null) {
            String path = id.getPath().toLowerCase();
            // Use word-boundary checks: "sand" and "gravel" should not match "sandstone"
            boolean isSand = path.equals("sand") || path.endsWith("_sand") || path.startsWith("sand_");
            boolean isGravel = path.equals("gravel") || path.endsWith("_gravel") || path.startsWith("gravel_");
            return isSand || isGravel;
        }
        return false;
    }

    // -----------------------------------------------------------------------
    // Building block eligibility for Strategy B (non-slab/stair base blocks)
    // -----------------------------------------------------------------------
    private static boolean isLikelyBuildingBlock(Block block) {
        ResourceLocation id = block.getRegistryName();
        if (id == null) return false;

        String path = id.getPath().toLowerCase();
        String namespace = id.getNamespace().toLowerCase();

        // 1. Blacklist check FIRST (catches hourglass, bamboo poles, ornaments, etc.)
        String coreName = stripShapeSuffixes(path);
        if (isCoreNameBlacklisted(coreName, namespace)) return false;

        // Skip technical/logic blocks
        if (block instanceof net.minecraft.world.level.block.CommandBlock ||
            block instanceof net.minecraft.world.level.block.StructureBlock ||
            block instanceof net.minecraft.world.level.block.BarrierBlock) return false;

        // If it's already a slab/stair/fence/wall skip Strategy B
        if (block instanceof SlabBlock || block instanceof StairBlock) return false;

        // Glass, Logistics, and Falling blocks are always valid building blocks for our needs
        boolean isLogType = path.contains("log") || path.contains("wood") || path.contains("_stem") || path.contains("_hyphae");
        if (isGlass(block) || block instanceof net.minecraft.world.level.block.FallingBlock || isLogType ||
            namespace.equals("minecraft") && (path.contains("sand") || path.contains("gravel"))) return true;

        // Functional check: Building blocks should usually be full cubes.
        // canOcclude() is true for solid cubes and false for thin/special shaped blocks.
        try {
            if (!block.defaultBlockState().canOcclude()) return false;
            
            // Full Collision Check: Ensures "hourglass" or "twigs" or thin poles don't get variants
            // Glass and Logs are exempt as their collision/occlusion is handled specifically.
            if (!isGlass(block) && !isLogType) {
                boolean isFull = block.defaultBlockState().isCollisionShapeFullBlock(net.minecraft.world.level.EmptyBlockGetter.INSTANCE, net.minecraft.core.BlockPos.ZERO);
                if (!isFull) return false;
            }
        } catch (Exception ignored) {}

        // Modded blocks: if they passed blacklist and canOcclude, they are likely safe.
        if (!namespace.equals("minecraft") && !namespace.equals("buildscape")) return true;

        // Vanilla/buildscape: check material
        try {
            net.minecraft.world.level.material.Material mat = block.defaultBlockState().getMaterial();
            return mat.isSolid() || mat.isSolidBlocking() ||
                   mat == net.minecraft.world.level.material.Material.WOOD ||
                   mat == net.minecraft.world.level.material.Material.STONE ||
                   mat == net.minecraft.world.level.material.Material.METAL ||
                   mat == net.minecraft.world.level.material.Material.GLASS ||
                   mat == net.minecraft.world.level.material.Material.HEAVY_METAL ||
                   mat == net.minecraft.world.level.material.Material.DIRT ||
                   mat == net.minecraft.world.level.material.Material.GRASS ||
                   mat == net.minecraft.world.level.material.Material.SAND ||
                   mat == net.minecraft.world.level.material.Material.CLAY ||
                   mat == net.minecraft.world.level.material.Material.SNOW ||
                   mat == net.minecraft.world.level.material.Material.TOP_SNOW ||
                   mat == net.minecraft.world.level.material.Material.ICE ||
                   mat == net.minecraft.world.level.material.Material.ICE_SOLID ||
                   mat == net.minecraft.world.level.material.Material.VEGETABLE;
        } catch (Exception e) {
            return false;
        }
    }

    // -----------------------------------------------------------------------
    // Utility: Strip shape suffixes from a path
    // -----------------------------------------------------------------------
    private static String stripShapeSuffixes(String path) {
        String result = path;
        for (String s : SHAPE_SUFFIXES) result = result.replace(s, "");
        return result.replaceAll("^_+|_+$", "");
    }

    private static final String[] STRIP_MATERIAL_PATTERNS = {"_planks?$", "_bricks?$", "_tiles?$", "_block$", "_log$", "_wood$", "_stem$", "_hyphae$", "_bale$"};
    private static String stripMaterialSuffix(String path) {
        String result = path;
        for (String pattern : STRIP_MATERIAL_PATTERNS) {
            result = result.replaceAll(pattern, "");
        }
        return result;
    }

    private static Block tryGetBlock(String namespace, String path, IForgeRegistry<Block> registry) {
        if (path.isEmpty()) return null;
        try {
            Block b = registry.getValue(new ResourceLocation(namespace, path));
            if (b != null && b != Blocks.AIR) return b;
        } catch (Exception ignored) {}
        return null;
    }
}
