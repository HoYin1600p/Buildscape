package com.kingodogo.buildscape.variantengine.registry;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.family.BlockFamily;
import com.kingodogo.buildscape.variantengine.family.BlockFamilyDetector;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRegistryScanner {

    private static final java.util.Map<Block, BlockFamily> DETECTED_FAMILIES = new java.util.HashMap<>();

    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        DETECTED_FAMILIES.clear();
        BuildScape.LOGGER.info("VariantEngine: Scanning registries for block families...");

        // Use the event's registry as the source of truth
        for (Block block : event.getRegistry()) {
            BlockFamily detected = BlockFamilyDetector.detectFamily(block);
            if (detected != null) {
                Block base = detected.getBaseBlock();
                
                // Redundancy Check: If the base block itself is a slab or stair, 
                // but a 'fuller' block (planks, etc) exists that would own it, skip creating a separate family.
                if (base instanceof net.minecraft.world.level.block.SlabBlock || base instanceof net.minecraft.world.level.block.StairBlock) {
                    // This is a last-resort base. We'll only use it if no better base emerged.
                    // For now, let's keep it but mark it for a second pass.
                }

                BlockFamily family = DETECTED_FAMILIES.computeIfAbsent(base, k -> new BlockFamily(k));
                detected.getVariants().forEach(family::addVariant);
            }
        }

        // Post-processing: Canonicalize families to prevent duplicates.
        // Strategy: namespace:path is used as the canonical key. Strip only slab/stair/vertical
        // suffixes — do NOT strip _bricks/_block/_planks etc. as those are content descriptors, not
        // shape suffixes. This prevents unrelated blocks from colliding under the same canonical key.
        java.util.Map<net.minecraft.resources.ResourceLocation, BlockFamily> canonicalFamilies = new java.util.HashMap<>();

        for (BlockFamily family : DETECTED_FAMILIES.values()) {
            Block base = family.getBaseBlock();
            net.minecraft.resources.ResourceLocation baseId = base.getRegistryName();
            if (baseId == null) continue;

            // Strip ONLY shape-suffixes to determine the canonical family key.
            // We deliberately do NOT strip material-suffixes like _bricks, _planks, _block because
            // those are part of the block's identity and stripping them causes false merges between
            // unrelated mod families (e.g. mymod:oak_block and mymod:oak_planks colliding).
            String corePath = baseId.getPath()
                .replace("_stairs", "").replace("_stair", "")
                .replace("_slab", "")
                .replaceAll("^v_slab_|^v_stair_", "")
                .replaceAll("^_+|_+$", ""); // clean leading/trailing underscores
            net.minecraft.resources.ResourceLocation coreId = new net.minecraft.resources.ResourceLocation(baseId.getNamespace(), corePath);

            BlockFamily existing = canonicalFamilies.get(coreId);
            if (existing != null) {
                Block existingBase = existing.getBaseBlock();
                boolean currentIsShaped = base instanceof net.minecraft.world.level.block.SlabBlock
                    || base instanceof net.minecraft.world.level.block.StairBlock
                    || base.defaultBlockState().is(net.minecraft.tags.BlockTags.SLABS)
                    || base.defaultBlockState().is(net.minecraft.tags.BlockTags.STAIRS);
                boolean existingIsShaped = existingBase instanceof net.minecraft.world.level.block.SlabBlock
                    || existingBase instanceof net.minecraft.world.level.block.StairBlock
                    || existingBase.defaultBlockState().is(net.minecraft.tags.BlockTags.SLABS)
                    || existingBase.defaultBlockState().is(net.minecraft.tags.BlockTags.STAIRS);

                if (!currentIsShaped && existingIsShaped) {
                    // Current base is a full block — it wins. Merge existing variants in.
                    existing.getVariants().forEach((shape, block) -> {
                        if (shape != com.kingodogo.buildscape.variantengine.builder.BlockShape.BASE) {
                            family.addVariant(shape, block);
                        }
                    });
                    canonicalFamilies.put(coreId, family);
                } else {
                    // Existing is fuller or equal — merge current's non-base variants into it.
                    family.getVariants().forEach((shape, block) -> {
                        if (shape != com.kingodogo.buildscape.variantengine.builder.BlockShape.BASE) {
                            existing.addVariant(shape, block);
                        }
                    });
                }
            } else {
                canonicalFamilies.put(coreId, family);
            }
        }

        DETECTED_FAMILIES.clear();
        for (BlockFamily cf : canonicalFamilies.values()) {
            DETECTED_FAMILIES.put(cf.getBaseBlock(), cf);

            // Seed BiMaps for SLAB and STAIRS variants so VariantRegistrar can detect them.
            // This is critical for orphan slab/stair blocks from other mods (where the slab IS the
            // base block of the family) — without this seed, hasStandardSlab/hasStandardStair would
            // always return false and no vertical variants would ever be registered for those mods.
            Block familyBase = cf.getBaseBlock();
            java.util.Map<com.kingodogo.buildscape.variantengine.builder.BlockShape, Block> variants = cf.getVariants();

            // Populate SLAB and STAIRS into BiMaps
            Block slabVariant = variants.get(com.kingodogo.buildscape.variantengine.builder.BlockShape.SLAB);
            if (slabVariant != null) {
                com.kingodogo.buildscape.variantengine.util.BlockBiMaps.setBlockOf(
                    com.kingodogo.buildscape.variantengine.builder.BlockShape.SLAB, familyBase, slabVariant);
            } else if (familyBase instanceof net.minecraft.world.level.block.SlabBlock
                    || familyBase.defaultBlockState().is(net.minecraft.tags.BlockTags.SLABS)) {
                // Orphan case: the family base IS the slab — register it as its own slab variant
                // so the shouldRegister gate in VariantRegistrar passes.
                com.kingodogo.buildscape.variantengine.util.BlockBiMaps.setBlockOf(
                    com.kingodogo.buildscape.variantengine.builder.BlockShape.SLAB, familyBase, familyBase);
                BuildScape.LOGGER.debug("VariantEngine: Orphan slab base detected: {}", familyBase.getRegistryName());
            }

            Block stairVariant = variants.get(com.kingodogo.buildscape.variantengine.builder.BlockShape.STAIRS);
            if (stairVariant != null) {
                com.kingodogo.buildscape.variantengine.util.BlockBiMaps.setBlockOf(
                    com.kingodogo.buildscape.variantengine.builder.BlockShape.STAIRS, familyBase, stairVariant);
            } else if (familyBase instanceof net.minecraft.world.level.block.StairBlock
                    || familyBase.defaultBlockState().is(net.minecraft.tags.BlockTags.STAIRS)) {
                // Orphan case: the family base IS the stair — register it as its own stair variant.
                com.kingodogo.buildscape.variantengine.util.BlockBiMaps.setBlockOf(
                    com.kingodogo.buildscape.variantengine.builder.BlockShape.STAIRS, familyBase, familyBase);
                BuildScape.LOGGER.debug("VariantEngine: Orphan stair base detected: {}", familyBase.getRegistryName());
            }

            // Populate remaining variants (vertical slabs, vertical stairs, etc.) into BiMaps
            for (java.util.Map.Entry<com.kingodogo.buildscape.variantengine.builder.BlockShape, Block> entry : variants.entrySet()) {
                com.kingodogo.buildscape.variantengine.builder.BlockShape shape = entry.getKey();
                if (shape == com.kingodogo.buildscape.variantengine.builder.BlockShape.SLAB
                    || shape == com.kingodogo.buildscape.variantengine.builder.BlockShape.STAIRS
                    || shape == com.kingodogo.buildscape.variantengine.builder.BlockShape.BASE) continue;
                com.kingodogo.buildscape.variantengine.util.BlockBiMaps.setBlockOf(shape, familyBase, entry.getValue());
            }
        }

        BuildScape.LOGGER.info("VariantEngine: Found {} unique block families for completion.", DETECTED_FAMILIES.size());

        // Now register missing variants
        VariantRegistrar.registerMissingBlocks(event.getRegistry(), new ArrayList<>(DETECTED_FAMILIES.values()));
    }

    public static List<BlockFamily> getDetectedFamilies() {
        return new ArrayList<>(DETECTED_FAMILIES.values());
    }
}
