package com.kingodogo.buildscape.variantengine.registry;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import com.kingodogo.buildscape.variantengine.family.BlockFamily;
import com.kingodogo.buildscape.variantengine.family.BlockFamilyDetector;
import com.kingodogo.buildscape.variantengine.util.BlockBiMaps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRegistryScanner {

    private static final Map<Block, BlockFamily> DETECTED_FAMILIES = new HashMap<>();

    // -----------------------------------------------------------------------
    // Phase 1: Scan — runs at LOWEST so all other mods have registered first.
    // Each block is processed in its own try-catch so one bad block cannot
    // silently abort the entire scan.
    // -----------------------------------------------------------------------
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        DETECTED_FAMILIES.clear();
        BlockBiMaps.BASE_BLOCKS.clear();

        IForgeRegistry<Block> registry = event.getRegistry();
        BuildScape.LOGGER.info("VariantEngine: Scanning all registered blocks for stair/slab families...");

        int scanned = 0, detected = 0, errors = 0;

        // Iterate every block that is in the Forge registry at this point.
        // At LOWEST priority, all other mods' DeferredRegister handlers (NORMAL priority)
        // have already fired, so every modded block is present here.
        for (Block block : registry) {
            scanned++;
            try {
                processBlock(block);
                detected++;
            } catch (Exception e) {
                errors++;
                BuildScape.LOGGER.warn("VariantEngine: Skipping block {} due to error during detection: {}",
                        safeId(block), e.getMessage());
            }
        }

        BuildScape.LOGGER.info("VariantEngine: Scan complete. Scanned={}, FamilyCandidates={}, Errors={}",
                scanned, detected, errors);

        // -----------------------------------------------------------------------
        // Phase 2: Canonicalize — merge slab/stair orphans with full-block families
        // that share the same root name (e.g. oak → oak_slab, oak_stairs).
        // -----------------------------------------------------------------------
        Map<ResourceLocation, BlockFamily> canonicalFamilies = canonicalize(DETECTED_FAMILIES);

        // -----------------------------------------------------------------------
        // Phase 3: Commit — clear + rebuild DETECTED_FAMILIES from canonical map,
        // then seed BlockBiMaps with SLAB / STAIRS / vertical shapes.
        // -----------------------------------------------------------------------
        DETECTED_FAMILIES.clear();
        for (BlockFamily cf : canonicalFamilies.values()) {
            DETECTED_FAMILIES.put(cf.getBaseBlock(), cf);
            seedBiMaps(cf);
        }

        BuildScape.LOGGER.info("VariantEngine: {} canonical families ready for vertical variant registration.",
                DETECTED_FAMILIES.size());

        // -----------------------------------------------------------------------
        // Phase 4: Register missing vertical blocks.
        // -----------------------------------------------------------------------
        VariantRegistrar.registerMissingBlocks(registry, new ArrayList<>(DETECTED_FAMILIES.values()));
    }

    // -----------------------------------------------------------------------
    // Individual block processing — deliberately does NOT contain try/catch;
    // the caller wraps each call and logs any exception.
    // -----------------------------------------------------------------------
    private static void processBlock(Block block) {
        BlockFamily detected = BlockFamilyDetector.detectFamily(block);
        if (detected == null) return;

        Block base = detected.getBaseBlock();
        BlockFamily existing = DETECTED_FAMILIES.computeIfAbsent(base, BlockFamily::new);
        // Merge all variants from the detected family into the accumulated one.
        detected.getVariants().forEach((shape, varBlock) -> {
            if (shape != BlockShape.BASE) {
                existing.addVariant(shape, varBlock);
            }
        });
    }

    // -----------------------------------------------------------------------
    // Canonicalize families so that "oak" (full block) wins over "oak_slab"
    // or "oak_stairs" as the family root.
    //
    // Key rules:
    //  • Strip ONLY shape suffixes (_stairs, _stair, _slab) to compute the
    //    canonical key — never strip material suffixes (_bricks, _planks…)
    //    because that causes unrelated blocks to collide.
    //  • A full block beats a shaped block as the canonical base.
    //  • Variants are always merged (non-destructively) into the winner.
    // -----------------------------------------------------------------------
    private static Map<ResourceLocation, BlockFamily> canonicalize(Map<Block, BlockFamily> raw) {
        Map<ResourceLocation, BlockFamily> canonical = new LinkedHashMap<>();

        for (BlockFamily family : raw.values()) {
            Block base = family.getBaseBlock();
            ResourceLocation baseId = safeRegistryName(base);
            if (baseId == null) continue;

            ResourceLocation coreId = toCoreId(baseId);

            BlockFamily existing = canonical.get(coreId);
            if (existing == null) {
                canonical.put(coreId, family);
            } else {
                // Decide which base is "fuller" (not a shaped block itself).
                boolean currentIsShaped = isShaped(base);
                boolean existingIsShaped = isShaped(existing.getBaseBlock());

                if (!currentIsShaped && existingIsShaped) {
                    // Current wins — absorb the existing variants into it, then replace.
                    existing.getVariants().forEach((shape, blk) -> {
                        if (shape != BlockShape.BASE) family.addVariant(shape, blk);
                    });
                    canonical.put(coreId, family);
                } else {
                    // Existing wins (or they're equal) — merge current into existing.
                    family.getVariants().forEach((shape, blk) -> {
                        if (shape != BlockShape.BASE) existing.addVariant(shape, blk);
                    });
                }
            }
        }

        return canonical;
    }

    // -----------------------------------------------------------------------
    // Seed BlockBiMaps from the canonical family.
    // Because BlockFamilyDetector now uses the SAME broad detection logic
    // (instanceof OR name-contains OR BlockTags) when it populates the family
    // variant map, we only need to read from the variant map directly.
    // -----------------------------------------------------------------------
    private static void seedBiMaps(BlockFamily cf) {
        Block familyBase = cf.getBaseBlock();
        Map<BlockShape, Block> variants = cf.getVariants();

        Block slabVariant = variants.get(BlockShape.SLAB);
        if (slabVariant != null) {
            BlockBiMaps.setBlockOf(BlockShape.SLAB, familyBase, slabVariant);
            if (slabVariant == familyBase) {
                BuildScape.LOGGER.info("VariantEngine: Orphan-slab family: {}", safeRegistryName(familyBase));
            }
        }

        Block stairVariant = variants.get(BlockShape.STAIRS);
        if (stairVariant != null) {
            BlockBiMaps.setBlockOf(BlockShape.STAIRS, familyBase, stairVariant);
            if (stairVariant == familyBase) {
                BuildScape.LOGGER.info("VariantEngine: Orphan-stair family: {}", safeRegistryName(familyBase));
            }
        }

        // Seed any other shapes (vertical slab, vertical stairs, etc.) that already exist
        for (Map.Entry<BlockShape, Block> entry : variants.entrySet()) {
            BlockShape shape = entry.getKey();
            if (shape == BlockShape.SLAB || shape == BlockShape.STAIRS || shape == BlockShape.BASE) continue;
            BlockBiMaps.setBlockOf(shape, familyBase, entry.getValue());
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Returns true if the block is itself a shaped block (slab or stair). */
    private static boolean isShaped(Block block) {
        try {
            if (block instanceof net.minecraft.world.level.block.SlabBlock) return true;
            if (block instanceof net.minecraft.world.level.block.StairBlock) return true;
            if (block.defaultBlockState().is(net.minecraft.tags.BlockTags.SLABS)) return true;
            if (block.defaultBlockState().is(net.minecraft.tags.BlockTags.STAIRS)) return true;
        } catch (Exception ignored) {}
        // Name-based fallback: if the block's path contains slab/stair keywords it is shaped.
        ResourceLocation id = safeRegistryName(block);
        if (id != null) {
            String p = id.getPath();
            if (p.contains("slab") || p.contains("stair")) return true;
        }
        return false;
    }

    /**
     * Strips shape suffixes from a block path to get the canonical "core" name.
     * Only shape suffixes are removed; material suffixes (_bricks, _planks, etc.)
     * are intentionally preserved to prevent unrelated families from colliding.
     */
    private static ResourceLocation toCoreId(ResourceLocation id) {
        String p = id.getPath();
        // Remove shape suffixes (order matters — longest first to avoid partial matches)
        p = p.replace("_stairs", "").replace("_stair", "").replace("_slab", "");
        // Remove our own generated prefixes (if somehow they appear)
        if (p.startsWith("v_slab_"))  p = p.substring(7);
        if (p.startsWith("v_stair_")) p = p.substring(8);
        // Clean up leading/trailing underscores left by removals
        p = p.replaceAll("^_+|_+$", "");
        if (p.isEmpty()) p = id.getPath(); // safety: never produce an empty path
        return new ResourceLocation(id.getNamespace(), p);
    }

    private static ResourceLocation safeRegistryName(Block block) {
        try { return block.getRegistryName(); } catch (Exception e) { return null; }
    }

    private static String safeId(Block block) {
        try {
            ResourceLocation id = block.getRegistryName();
            return id != null ? id.toString() : block.getClass().getSimpleName();
        } catch (Exception e) {
            return block.getClass().getSimpleName();
        }
    }

    public static List<BlockFamily> getDetectedFamilies() {
        return new ArrayList<>(DETECTED_FAMILIES.values());
    }
}
