package com.kingodogo.buildscape.variantengine.registry;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.block.QuarterPieceBlock;
import com.kingodogo.buildscape.variantengine.block.VerticalSlabBlock;
import com.kingodogo.buildscape.variantengine.block.VerticalStairsBlock;
import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import com.kingodogo.buildscape.variantengine.family.BlockFamily;
import com.kingodogo.buildscape.variantengine.family.BlockFamilyDetector;
import com.kingodogo.buildscape.variantengine.util.BlockBiMaps;
import com.kingodogo.buildscape.variantengine.util.BlockDetectionUtil;
import com.kingodogo.buildscape.variantengine.util.VariantNamingUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;

/**
 * Handles the detection and registration of vertical variants.
 * Subscribes to the 'zz_buildscape_variants' mod bus to ensure it runs at the VERY END
 * of the registry phase, catching blocks from all other mods.
 */
@Mod.EventBusSubscriber(modid = com.kingodogo.buildscape.BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VariantRegistrar {

    private static final Map<Block, BlockFamily> DETECTED_FAMILIES = new HashMap<>();
    private static volatile boolean isScanningComplete = false;

    public static boolean isScanningComplete() { return isScanningComplete; }

    private static final String[] REGISTRAR_BLACKLIST = {
        "torch", "lantern", "candle", "lamp", "sign", "banner", "carpet",
        "door", "trapdoor", "fence", "gate", "wall", "bars", "pane",
        "button", "lever", "rail", "piston", "tnt", "spawner",
        "flower", "sapling", "leaves", "mushroom", "shroom",
        "chest", "barrel", "table", "bed", "anvil", "furnace",
        "cauldron", "hopper", "dispenser", "dropper", "observer",
        "brewing", "enchant", "head", "skull", "pot", "vine",
        "tiki", "flag", "hook", "rope", "cage", "jar", "jars",
        "chain", "web", "cobweb", "string", "sack", "urn", "urns",
        "pouch", "compressium", "hourglass"
    };

    /**
     * Phase 1: Block Registration.
     * Triggered at the absolute end of mod loading.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        try {
            IForgeRegistry<Block> registry = event.getRegistry();
            scanAndRegisterVariants(registry);
        } catch (Exception e) {
            BuildScape.LOGGER.error("Critical error during variant block registration", e);
        }
    }

    /**
     * Phase 2: Item Registration.
     * Triggered for our variants to make them accessible in inventory.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        try {
            registerVariantItems(event.getRegistry());
        } catch (Exception e) {
        }
    }

    private static void scanAndRegisterVariants(IForgeRegistry<Block> registry) {
        DETECTED_FAMILIES.clear();
        BlockBiMaps.BASE_BLOCKS.clear();

        long startTime = System.currentTimeMillis();

        // --- CACHE CHECK ---
        String fingerprint = VariantScanCache.computeFingerprint();
        VariantScanCache.CacheData cached = VariantScanCache.tryLoad(fingerprint);


        if (cached != null && cached.valid) {
            // Fast path: reconstruct families directly from cached block IDs
            int count = commitFromCache(cached, registry);
            BuildScape.LOGGER.info("BuildScape: Cache hit! Registered {} variants from cache in {}ms (skipped full scan).",
                    count, System.currentTimeMillis() - startTime);
            isScanningComplete = true; 
            return;
        }


        // --- FULL SCAN (slow path, only runs when mods change) ---
        BuildScape.LOGGER.info("BuildScape: No cache or mods changed. Running full block scan...");

        int scanned = 0, detected = 0;

        for (Block block : registry) {
            ResourceLocation id = safeRegistryName(block);
            if (id == null) continue;

            scanned++;

            if (id.getNamespace().equals(BuildScape.MODID)) {
                String p = id.getPath();
                if (p.startsWith("v_slab_") || p.startsWith("v_stair_") || p.startsWith("q_piece_")) continue;
            }

            if (processBlock(block, registry)) {
                detected++;
            }
        }

        // Canonicalize
        Map<ResourceLocation, BlockFamily> canonical = canonicalize(DETECTED_FAMILIES);

        DETECTED_FAMILIES.clear();
        for (BlockFamily cf : canonical.values()) {
            DETECTED_FAMILIES.put(cf.getBaseBlock(), cf);
            seedBiMaps(cf);
        }

        // Register new variants
        int totalGenerated = commitNewVariants(registry);

        // Save results to cache for next launch
        VariantScanCache.CacheData toSave = buildCacheData();
        VariantScanCache.save(fingerprint, toSave);

        BuildScape.LOGGER.info("BuildScape: Full scan done in {}ms: scanned={}, detected={}, canonicalized={}, generated={}",
                System.currentTimeMillis() - startTime, scanned, detected, canonical.size(), totalGenerated);
        isScanningComplete = true;
    }

    /**
     * Fast-path: directly re-registers variants using cached block IDs.
     * Avoids all detectFamily() calls.
     */
    private static int commitFromCache(VariantScanCache.CacheData cached, IForgeRegistry<Block> registry) {
        int count = 0;
        BlockShape[] targets = {BlockShape.VERTICAL_SLAB, BlockShape.VERTICAL_STAIRS};
        com.kingodogo.buildscape.config.VerticalConfig config = com.kingodogo.buildscape.config.VerticalConfig.get();

        for (Map.Entry<String, Set<String>> entry : cached.baseToShapes.entrySet()) {
            String baseIdStr = entry.getKey();
            ResourceLocation baseId;
            try {
                baseId = new ResourceLocation(baseIdStr);
            } catch (Exception e) {
                continue;
            }

            Block base = registry.getValue(baseId);
            if (base == null || base == Blocks.AIR) continue;

            String ns = baseId.getNamespace();
            // config check removed to retain candidate families in memory for UI

            BlockFamily family = new BlockFamily(base);
            DETECTED_FAMILIES.put(base, family);

            // Restore companion slabs/stairs into the family
            String slabComp = cached.baseToSlabCompanion.get(baseIdStr);
            if (slabComp != null) {
                Block slab = registry.getValue(new ResourceLocation(slabComp));
                if (slab != null && slab != Blocks.AIR) {
                    family.addVariant(BlockShape.SLAB, slab);
                    BlockBiMaps.setBlockOf(BlockShape.SLAB, base, slab);
                }
            }
            String stairComp = cached.baseToStairCompanion.get(baseIdStr);
            if (stairComp != null) {
                Block stair = registry.getValue(new ResourceLocation(stairComp));
                if (stair != null && stair != Blocks.AIR) {
                    family.addVariant(BlockShape.STAIRS, stair);
                    BlockBiMaps.setBlockOf(BlockShape.STAIRS, base, stair);
                }
            }

            Set<String> shapes = entry.getValue();
            for (BlockShape shape : targets) {
                if (!shapes.contains(shape.getSerializedName())) continue;

                String path = baseId.getPath();
                if (!shouldGenerate(family, base, path, ns, shape)) {
                    continue; // Skip generating if current code excludes it!
                }

                ResourceLocation genId = VariantNamingUtil.getGeneratedId(baseId, shape);
                if (registry.containsKey(genId)) {
                    Block existing = registry.getValue(genId);
                    if (existing != null) {
                        family.addVariant(shape, existing);
                        BlockBiMaps.setBlockOf(shape, base, existing);
                    }
                    continue;
                }

                Block generated = createVariantBlock(base, shape);
                if (generated != null) {
                    generated.setRegistryName(genId);
                    registry.register(generated);
                    family.addVariant(shape, generated);
                    BlockBiMaps.setBlockOf(shape, base, generated);
                    count++;
                }
            }

            BlockBiMaps.BASE_BLOCKS.add(base);
        }
        return count;
    }

    /**
     * Builds a CacheData snapshot from the current DETECTED_FAMILIES state,
     * capturing which base blocks and shapes were selected for generation.
     */
    private static VariantScanCache.CacheData buildCacheData() {
        VariantScanCache.CacheData data = new VariantScanCache.CacheData();
        BlockShape[] targets = {BlockShape.VERTICAL_SLAB, BlockShape.VERTICAL_STAIRS};

        for (Map.Entry<Block, BlockFamily> e : DETECTED_FAMILIES.entrySet()) {
            Block base = e.getKey();
            BlockFamily family = e.getValue();
            ResourceLocation baseId = safeRegistryName(base);
            if (baseId == null) continue;

            String path = baseId.getPath().toLowerCase();
            String ns = baseId.getNamespace().toLowerCase();

            // config check removed to save candidate families into cache
            if (ns.equals("compressium") || isBlacklisted(path)) continue;

            Set<String> shapes = new LinkedHashSet<>();
            for (BlockShape shape : targets) {
                // Record the shape if we actually generated it (i.e. it's a buildscape variant)
                Block variant = BlockBiMaps.getBlockOf(shape, base);
                if (variant != null) {
                    ResourceLocation vid = safeRegistryName(variant);
                    if (vid != null && vid.getNamespace().equals(BuildScape.MODID)) {
                        shapes.add(shape.getSerializedName());
                    }
                }
            }

            // shapes empty check removed to save families without generated variants into cache
            
            String baseIdStr = baseId.toString();
            data.baseToShapes.put(baseIdStr, shapes);

            // Save companion IDs
            Block slab = family.getVariants().get(BlockShape.SLAB);
            if (slab != null) {
                ResourceLocation sid = safeRegistryName(slab);
                if (sid != null) data.baseToSlabCompanion.put(baseIdStr, sid.toString());
            }
            Block stair = family.getVariants().get(BlockShape.STAIRS);
            if (stair != null) {
                ResourceLocation sid = safeRegistryName(stair);
                if (sid != null) data.baseToStairCompanion.put(baseIdStr, sid.toString());
            }
        }
        return data;
    }


    private static boolean processBlock(Block block, IForgeRegistry<Block> registry) {
        BlockFamily detected = BlockFamilyDetector.detectFamily(block, registry);
        if (detected == null) return false;

        Block base = detected.getBaseBlock();
        BlockFamily existing = DETECTED_FAMILIES.computeIfAbsent(base, BlockFamily::new);
        
        final boolean[] addedNew = {false};
        detected.getVariants().forEach((shape, varBlock) -> {
            if (shape != BlockShape.BASE && !existing.hasVariant(shape)) {
                existing.addVariant(shape, varBlock);
                addedNew[0] = true;
            }
        });
        return addedNew[0];
    }

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
                boolean currentIsShaped = isShaped(base);
                boolean existingIsShaped = isShaped(existing.getBaseBlock());

                if (!currentIsShaped && existingIsShaped) {
                    existing.getVariants().forEach((shape, blk) -> { if (shape != BlockShape.BASE) family.addVariant(shape, blk); });
                    canonical.put(coreId, family);
                } else if (currentIsShaped == existingIsShaped) {
                    String currentNs = baseId.getNamespace();
                    String existingNs = safeRegistryName(existing.getBaseBlock()).getNamespace();
                    if (!currentNs.equals("minecraft") && existingNs.equals("minecraft")) {
                        existing.getVariants().forEach((shape, blk) -> { if (shape != BlockShape.BASE) family.addVariant(shape, blk); });
                        canonical.put(coreId, family);
                    } else {
                        family.getVariants().forEach((shape, blk) -> { if (shape != BlockShape.BASE) existing.addVariant(shape, blk); });
                    }
                } else {
                    family.getVariants().forEach((shape, blk) -> { if (shape != BlockShape.BASE) existing.addVariant(shape, blk); });
                }
            }
        }
        return canonical;
    }

    private static int commitNewVariants(IForgeRegistry<Block> registry) {
        int count = 0;
        BlockShape[] targets = {BlockShape.VERTICAL_SLAB, BlockShape.VERTICAL_STAIRS};

        int skippedBlacklist = 0;
        int skippedConfigAllow = 0;
        int skippedShouldGenerate = 0;

        for (BlockFamily family : DETECTED_FAMILIES.values()) {
            Block base = family.getBaseBlock();
            ResourceLocation baseId = safeRegistryName(base);
            if (baseId == null) continue;

            String path = baseId.getPath().toLowerCase();
            String ns = baseId.getNamespace().toLowerCase();



            if (ns.equals("compressium") || isBlacklisted(path)) {
                skippedBlacklist++;
                continue;
            }

            for (BlockShape shape : targets) {
                if (family.hasVariant(shape)) continue;

                ResourceLocation genId = VariantNamingUtil.getGeneratedId(baseId, shape);
                if (registry.containsKey(genId)) {
                    family.addVariant(shape, registry.getValue(genId));
                    continue;
                }

                if (!shouldGenerate(family, base, path, ns, shape)) {
                    skippedShouldGenerate++;
                    continue;
                }

                String cleanBase = path.replaceAll("_slab$", "").replaceAll("_stairs?$", "").replaceAll("^_+|_+$", "");
                if (hasExistingVerticalInRegistry(registry, ns, cleanBase, shape == BlockShape.VERTICAL_SLAB ? "slab" : "stairs")) continue;

                Block generated = createVariantBlock(base, shape);
                if (generated != null) {
                    generated.setRegistryName(genId);
                    registry.register(generated);
                    family.addVariant(shape, generated);
                    BlockBiMaps.setBlockOf(shape, base, generated);
                    count++;
                }
            }
        }
        BuildScape.LOGGER.info("Buildscape Registerd Vertical Varients Diagnostic: generated={}, configAllowedSkip={}, blacklistedSkip={}, shouldGenSkip={}", 
            count, skippedConfigAllow, skippedBlacklist, skippedShouldGenerate);
        return count;
    }

    private static boolean shouldGenerate(BlockFamily family, Block base, String path, String ns, BlockShape shape) {
        if (path.contains("sandstone")) {
            return true; // Force allow All Sandstone variants
        }

        com.kingodogo.buildscape.config.VerticalConfig config = com.kingodogo.buildscape.config.VerticalConfig.get();
        ResourceLocation baseId = safeRegistryName(base);
        if (baseId != null) {
            String baseIdStr = baseId.toString();
            if (config.getBlocklistedFamilies().contains(baseIdStr) || config.isModBlocklisted(ns)) {
                return false; // Explicitly blocked
            }
            if (config.getExplicitlyGreyFamilies().contains(baseIdStr)) {
                return false; // Force Grey (Disabled)
            }
            if (config.getAllowedFamilies().contains(baseIdStr) || config.isModAllowed(ns)) {
                return true; // Explicitly allowed
            }
        }

        boolean isLog = path.contains("log") || path.contains("wood") || path.contains("_stem") || path.contains("_hyphae");
        boolean isGlass = BlockFamilyDetector.isGlass(base);
        // Use word-boundary check: "sand" in "sandstone" should NOT count as a falling block
        boolean isTrueSand = path.equals("sand") || path.endsWith("_sand") || path.startsWith("sand_");
        boolean isTrueGravel = path.equals("gravel") || path.endsWith("_gravel") || path.startsWith("gravel_");
        boolean isFalling = base instanceof net.minecraft.world.level.block.FallingBlock || isTrueSand || isTrueGravel;
        boolean hasHorizontal = (shape == BlockShape.VERTICAL_SLAB) ? 
            (family.hasVariant(BlockShape.SLAB) || BlockDetectionUtil.isSlab(base)) :
            (family.hasVariant(BlockShape.STAIRS) || BlockDetectionUtil.isStair(base));

        if (isTrueSand || path.contains("concrete_powder") || isTrueGravel) {
            return false; // Skip Sand/Concrete Powders/Gravel by default, keep Sandstone
        }

        if ((ns.equals("minecraft") || ns.equals("buildscape")) && !path.contains("ashenking")) {
             return hasHorizontal || isLog || isGlass || isFalling;
        }
        return hasHorizontal || isLog || isGlass || isFalling;
    }

    private static void registerVariantItems(IForgeRegistry<Item> registry) {
        int count = 0;
        for (BlockFamily family : DETECTED_FAMILIES.values()) {
            for (Block variant : family.getVariants().values()) {
                ResourceLocation id = safeRegistryName(variant);
                if (id == null || !id.getNamespace().equals(BuildScape.MODID)) continue;
                String path = id.getPath();
                if (!path.startsWith("v_slab_") && !path.startsWith("v_stair_") && !path.startsWith("q_piece_") && 
                    !path.startsWith("slab_") && !path.startsWith("stairs_")) continue;
                if (registry.containsKey(id)) continue;

                registry.register(new com.kingodogo.buildscape.variantengine.item.VariantBlockItem(variant, 
                    new Item.Properties().tab(com.kingodogo.buildscape.item.ModCreativeModeTab.BUILDSCAPE_TAB)).setRegistryName(id));
                count++;
            }
        }
    }

    private static void seedBiMaps(BlockFamily cf) {
        Block base = cf.getBaseBlock();
        cf.getVariants().forEach((shape, var) -> BlockBiMaps.setBlockOf(shape, base, var));
    }

    private static Block createVariantBlock(Block parent, BlockShape shape) {
        Block.Properties props = Block.Properties.copy(parent).noOcclusion()
            .lightLevel((state) -> parent.defaultBlockState().getLightEmission())
            .isValidSpawn((state, level, pos, type) -> false);
        
        if (parent.getStateDefinition().getProperties().contains(BlockStateProperties.AXIS)) {
             props.color(parent.defaultBlockState().setValue(BlockStateProperties.AXIS, net.minecraft.core.Direction.Axis.Y).getMapColor(null, null));
        }

        if (shape == BlockShape.VERTICAL_SLAB) return new VerticalSlabBlock(props, parent);
        if (shape == BlockShape.VERTICAL_STAIRS) return new VerticalStairsBlock(props, parent);
        if (shape == BlockShape.QUARTER_PIECE) return new QuarterPieceBlock(props, parent);
        if (shape == BlockShape.SLAB) return new net.minecraft.world.level.block.SlabBlock(props);
        if (shape == BlockShape.STAIRS) return new net.minecraft.world.level.block.StairBlock(parent::defaultBlockState, props);
        return null;
    }

    private static boolean hasExistingVerticalInRegistry(IForgeRegistry<Block> registry, String ns, String base, String shape) {
        String[] patterns = {"vertical_" + base + "_" + shape, base + "_vertical_" + shape, "v_" + shape + "_" + base, "v" + shape + "_" + base, base + "_v_" + shape};
        String[] namespaces = {ns, "v_slab", "vertical_slabs", "verticalslabs", "quark", "v_plus", "everycomp"};
        for (String n : namespaces) for (String p : patterns) if (registry.containsKey(new ResourceLocation(n, p))) return true;
        return false;
    }

    private static boolean isBlacklisted(String path) {
        for (String k : REGISTRAR_BLACKLIST) if (path.contains(k)) return true;
        return false;
    }

    private static boolean isShaped(Block b) {
        if (b instanceof net.minecraft.world.level.block.SlabBlock || b instanceof net.minecraft.world.level.block.StairBlock) return true;
        ResourceLocation id = safeRegistryName(b);
        return id != null && (id.getPath().contains("slab") || id.getPath().contains("stair"));
    }

    private static ResourceLocation toCoreId(ResourceLocation id) {
        String p = id.getPath().toLowerCase().replace("_stairs", "").replace("_stair", "").replace("_slab", "").replace("stairs_", "").replace("stair_", "").replace("slab_", "");
        p = p.replaceAll("^v_slab_|^v_stair_|^vslab_|^vstair_|^_+|_+$", "");
        return new ResourceLocation(id.getNamespace(), p.isEmpty() ? id.getPath() : p);
    }

    private static ResourceLocation safeRegistryName(Block b) { try { return b.getRegistryName(); } catch (Exception e) { return null; } }

    public static List<BlockFamily> getDetectedFamilies() { return new ArrayList<>(DETECTED_FAMILIES.values()); }

    /**
     * Handles remapping of missing blocks/items to avoid handshake errors when loading old worlds.
     */
    @Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Remapper {
        @SubscribeEvent
        public static void onMissingBlockMappings(RegistryEvent.MissingMappings<Block> event) {
            handleMappings(event);
        }

        @SubscribeEvent
        public static void onMissingItemMappings(RegistryEvent.MissingMappings<Item> event) {
            handleMappings(event);
        }

        @SubscribeEvent
        public static void onMissingBlockEntityMappings(RegistryEvent.MissingMappings<BlockEntityType<?>> event) {
            handleMappings(event);
        }

        private static <T extends net.minecraftforge.registries.IForgeRegistryEntry<T>> void handleMappings(RegistryEvent.MissingMappings<T> event) {
            for (RegistryEvent.MissingMappings.Mapping<T> mapping : event.getAllMappings()) {
                String ns = mapping.key.getNamespace();
                String path = mapping.key.getPath();
                
                // Catch our mod's removed items AND legacy everycomp items related to us
                boolean isLegacyBuildScape = ns.equals(BuildScape.MODID) || (ns.equals("everycomp") && path.contains("buildscape"));
                
                if (isLegacyBuildScape) {
                    if (path.contains("counter") || path.contains("leaves") || path.contains("pavement") || 
                        path.contains("decorated") || path.contains("vertical") || path.contains("_v_") || 
                        path.contains("v_slab") || path.contains("v_stair") || path.contains("q_piece") || path.contains("hedge")) {
                        
                        mapping.ignore();
                    }
                }
            }
        }
    }
}
