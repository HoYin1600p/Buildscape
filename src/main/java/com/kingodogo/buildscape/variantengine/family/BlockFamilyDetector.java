package com.kingodogo.buildscape.variantengine.family;

import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockFamilyDetector {

    /**
     * Attempts to find or create a family for a given block based on naming conventions and properties.
     */
    private static final String[] BASE_SUFFIXES = {"_slab", "_stairs", "_stair", "slab_", "stairs_", "stair_"};
    private static final String[] BLOCK_SUFFIXES = {"_bricks", "_brick", "_tiles", "_planks", "_plank", "_block"};

    public static BlockFamily detectFamily(Block block) {
        ResourceLocation id = block.getRegistryName();
        if (id == null) return null;

        String path = id.getPath();
        String namespace = id.getNamespace();

        // Skip our own generated variants
        if (block instanceof com.kingodogo.buildscape.variantengine.block.VerticalSlabBlock ||
            block instanceof com.kingodogo.buildscape.variantengine.block.VerticalStairsBlock ||
            block instanceof com.kingodogo.buildscape.variantengine.block.QuarterPieceBlock) {
            return null;
        }

        // GLOBAL BLACKLIST: No functional, thin, or decorative blocks
        if (block instanceof net.minecraft.world.level.block.DoorBlock || 
            block instanceof net.minecraft.world.level.block.TrapDoorBlock ||
            block instanceof net.minecraft.world.level.block.FenceBlock ||
            block instanceof net.minecraft.world.level.block.FenceGateBlock ||
            block instanceof net.minecraft.world.level.block.WallBlock ||
            block instanceof net.minecraft.world.level.block.SignBlock ||
            block instanceof net.minecraft.world.level.block.ChainBlock ||
            block instanceof net.minecraft.world.level.block.LanternBlock ||
            block instanceof net.minecraft.world.level.block.FlowerBlock ||
            block instanceof net.minecraft.world.level.block.BushBlock ||
            block instanceof net.minecraft.world.level.block.VineBlock ||
            block instanceof net.minecraft.world.level.block.SaplingBlock ||
            block instanceof net.minecraft.world.level.block.ScaffoldingBlock ||
            block instanceof net.minecraft.world.level.block.ButtonBlock ||
            block instanceof net.minecraft.world.level.block.PressurePlateBlock ||
            block instanceof net.minecraft.world.level.block.LiquidBlock ||
            block instanceof net.minecraft.world.level.block.EntityBlock) {
            return null;
        }

        // String-based safety for modded blocks that don't use standard classes or materials
        String lowPath = path.toLowerCase();
        if (lowPath.contains("door") || lowPath.contains("trapdoor") || lowPath.contains("sign") ||
            lowPath.contains("fence") || lowPath.contains("wall") || lowPath.contains("chain") ||
            lowPath.contains("bars") || lowPath.contains("grate") || lowPath.contains("ladder") ||
            lowPath.contains("button") || lowPath.contains("pressure_plate") || lowPath.contains("pane") ||
            lowPath.contains("leaves") || lowPath.contains("glass") || lowPath.contains("candle") ||
            lowPath.contains("lantern") || lowPath.contains("torch") || lowPath.contains("flower") ||
            lowPath.contains("bush") || lowPath.contains("vine") || lowPath.contains("sapling") ||
            lowPath.contains("scaffolding") || lowPath.contains("gate") || lowPath.contains("fence_gate")) {
            return null;
        }

        boolean isSlab = block instanceof SlabBlock || path.contains("slab") || 
                         block.defaultBlockState().is(net.minecraft.tags.BlockTags.SLABS);
        
        boolean isStair = block instanceof StairBlock || path.contains("stair") || 
                           block.defaultBlockState().is(net.minecraft.tags.BlockTags.STAIRS);

        if (!isSlab && !isStair && !isLikelyBuildingBlock(block)) return null;

        Block base = null;
        boolean isOurMod = namespace.equals(com.kingodogo.buildscape.BuildScape.MODID);
        
        if (isSlab || isStair) {
            base = findBaseBlock(block);
            if (base == null) {
                // ULTRA GREEDY: If no base found, use the block itself as the family root
                // This ensures every modded slab/stair gets a vertical version.
                base = block;
            }
        } else if (isOurMod || hasPotentialVariants(id)) {
            base = block;
        }

        if (base == null || base == net.minecraft.world.level.block.Blocks.AIR) return null;

        BlockFamily family = new BlockFamily(base);
        
        // If the base block itself IS a slab or stair, mark it so we don't re-register it
        if (base instanceof net.minecraft.world.level.block.SlabBlock || base.defaultBlockState().is(net.minecraft.tags.BlockTags.SLABS)) {
            family.addVariant(BlockShape.SLAB, base);
        }
        if (base instanceof net.minecraft.world.level.block.StairBlock || base.defaultBlockState().is(net.minecraft.tags.BlockTags.STAIRS)) {
            family.addVariant(BlockShape.STAIRS, base);
        }

        // Populate existing relatives
        for (BlockShape shape : BlockShape.values()) {
            if (shape == BlockShape.BASE || family.hasVariant(shape)) continue;
            ResourceLocation variantId = getPredictedId(base.getRegistryName(), shape);
            Block variant = ForgeRegistries.BLOCKS.getValue(variantId);
            if (variant != null && variant != net.minecraft.world.level.block.Blocks.AIR) {
                family.addVariant(shape, variant);
            }
        }

        return family;
    }

    private static boolean isLikelyBuildingBlock(Block block) {
        // Exclude technical and functional blocks
        if (block instanceof net.minecraft.world.level.block.BaseEntityBlock) return false;
        if (block instanceof net.minecraft.world.level.block.CommandBlock) return false;
        if (block instanceof net.minecraft.world.level.block.StructureBlock) return false;
        if (block instanceof net.minecraft.world.level.block.JigsawBlock) return false;
        if (block instanceof net.minecraft.world.level.block.BarrierBlock) return false;
        
        // Exclude functional decorative/shape blocks - Including our custom Mod versions
        if (block instanceof net.minecraft.world.level.block.DoorBlock || block.getClass().getSimpleName().contains("Door")) return false;
        if (block instanceof net.minecraft.world.level.block.TrapDoorBlock || block.getClass().getSimpleName().contains("Trapdoor")) return false;
        if (block instanceof net.minecraft.world.level.block.FenceBlock) return false;
        if (block instanceof net.minecraft.world.level.block.FenceGateBlock) return false;
        if (block instanceof net.minecraft.world.level.block.WallBlock) return false;
        if (block instanceof net.minecraft.world.level.block.BasePressurePlateBlock) return false;
        if (block instanceof net.minecraft.world.level.block.LeavesBlock) return false;
        
        // Slabs/Stairs are always likely building blocks
        if (block instanceof SlabBlock || block instanceof StairBlock) return true;

        ResourceLocation id = block.getRegistryName();
        if (id != null) {
            String path = id.getPath().toLowerCase();
            // Expanded blacklist for greedy mode
            if (path.contains("grate") || path.contains("bars") || path.contains("ladder") || 
                path.contains("sign") || path.contains("fence") || path.contains("gate") ||
                path.contains("wall") || path.contains("pane") || path.contains("leaves") ||
                path.contains("door") || path.contains("trapdoor") || path.contains("chest") ||
                path.contains("barrel") || path.contains("table") || path.contains("bed") ||
                path.contains("button") || path.contains("candle") || path.contains("torch")) return false;
        }

        // For everything else, it MUST be a full cube approximately or a known solid material
        net.minecraft.world.level.material.Material mat = block.defaultBlockState().getMaterial();
        
        // Items in item frames or dropped items are not building blocks
        if (mat == net.minecraft.world.level.material.Material.AIR) return false;
        
        // Heuristic: If it has 'slab' or 'stair' in the name and we reach here, it's definitely a candidate
        String lowName = block.getRegistryName().getPath().toLowerCase();
        if (lowName.contains("slab") || lowName.contains("stair")) return true;

        return mat.isSolidBlocking() && mat.isSolid() && !mat.isReplaceable();
    }

    private static boolean hasPotentialVariants(ResourceLocation id) {
        String ns = id.getNamespace();
        String path = id.getPath();
        return ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(ns, path + "_slab")) || 
               ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(ns, path + "_stairs"));
    }

    private static Block findBaseBlock(Block block) {
        ResourceLocation id = block.getRegistryName();
        if (id == null) return null;
        String path = id.getPath();

        // Try stripping known slab/stair prefixes/suffixes
        String baseName = path;
        for (String suffix : BASE_SUFFIXES) {
            baseName = baseName.replace(suffix, "");
        }
        baseName = baseName.replaceAll("^_+|_+$", ""); // Clean trailing/leading underscores

        // Strategy 1: Exact match (e.g. stone_slab -> stone)
        Block base = tryGetBlock(id.getNamespace(), baseName);
        if (base != null) return base;

        // Strategy 2: Pluralization match (e.g. stone_brick_slab -> stone_bricks)
        base = tryGetBlock(id.getNamespace(), baseName + "s");
        if (base != null) return base;

        // Strategy 3: Handle suffix mismatches (e.g. cut_copper_slab -> cut_copper)
        // Already handled by Strategy 1 if names align.
        
        // Strategy 4: Try appending building suffixes to the base name (e.g., stone -> stone_bricks)
        for (String suffix : BLOCK_SUFFIXES) {
            base = tryGetBlock(id.getNamespace(), baseName + suffix);
            if (base != null) return base;
        }

        // Strategy 5: If still not found, try stripping more descriptors from baseName itself
        for (String suffix : BLOCK_SUFFIXES) {
            if (baseName.endsWith(suffix)) {
                String deeperBase = baseName.substring(0, baseName.length() - suffix.length());
                base = tryGetBlock(id.getNamespace(), deeperBase);
                if (base != null) return base;
            }
        }

        return null; 
    }

    private static Block tryGetBlock(String namespace, String path) {
        if (path.isEmpty()) return null;
        Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(namespace, path));
        if (b != null && b != net.minecraft.world.level.block.Blocks.AIR && !(b instanceof net.minecraft.world.level.block.EntityBlock)) {
            return b;
        }
        return null;
    }

    private static ResourceLocation getPredictedId(ResourceLocation baseId, BlockShape shape) {
        String base = baseId.getPath();
        String namespace = baseId.getNamespace();
        if (shape == BlockShape.SLAB) return new ResourceLocation(namespace, base + "_slab");
        if (shape == BlockShape.STAIRS) return new ResourceLocation(namespace, base + "_stairs");
        
        // Use our canonical naming for everything else
        return com.kingodogo.buildscape.variantengine.util.VariantNamingUtil.getGeneratedId(baseId, shape);
    }
}
