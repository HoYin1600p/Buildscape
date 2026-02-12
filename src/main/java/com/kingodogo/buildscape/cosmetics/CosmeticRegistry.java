package com.kingodogo.buildscape.cosmetics;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for parsing and resolving cosmetic IDs to Minecraft registry
 * entries.
 * 
 * Cosmetic IDs are strings in format:
 * - "item:namespace:item_id" - Item cosmetic (e.g.,
 * "item:minecraft:diamond_sword")
 * - "block:namespace:block_id" - Block cosmetic (e.g.,
 * "block:minecraft:gold_block")
 * - "nbt:custom_data" - NBT-based cosmetic (custom data stored in game)
 * - "type:armor_set_1" - Type-based cosmetic (armor sets, etc.)
 * 
 * All cosmetic assets (models, textures) are built into the game.
 * This registry only resolves IDs to registry entries.
 */
public class CosmeticRegistry {
    private static final CosmeticRegistry INSTANCE = new CosmeticRegistry();

    // Cache for resolved items/blocks to avoid repeated lookups
    private final Map<String, Item> itemCache = new HashMap<>();
    private final Map<String, Block> blockCache = new HashMap<>();
    private final Map<String, ItemStack> itemStackCache = new HashMap<>();

    // Custom cosmetic type definitions (can be extended)
    private final Map<String, CosmeticType> typeDefinitions = new HashMap<>();

    private CosmeticRegistry() {
        // Initialize custom types if needed
        // Example: typeDefinitions.put("armor_set_1", new CosmeticType(...));
    }

    public static CosmeticRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Parse a cosmetic ID string and return the type and identifier.
     *
     * @param cosmeticId Cosmetic ID string (e.g.,
     *                   "buildscape:cosmatics/gear/diamond_sword")
     * @return Parsed cosmetic info, or null if invalid format
     */
    @Nullable
    public CosmeticInfo parseCosmeticId(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return null;
        }

        // Handle new format: buildscape:cosmatics/category/id
        if (cosmeticId.startsWith("buildscape:cosmatics/")) {
            String path = cosmeticId.substring("buildscape:cosmatics/".length());
            String[] parts = path.split("/", 2);
            if (parts.length == 2) {
                String category = parts[0]; // e.g. "gear", "particle", "wings"
                String id = parts[1];

                // Map new categories to internal types
                String type = category;
                if (category.equals("gear"))
                    type = "item"; // Default to item for gear

                // Get legacy ID from CosmeticManager if possible for resolving
                CosmeticManager.CosmeticMetadata metadata = CosmeticManager.getInstance().getMetadata(cosmeticId);
                String legacyId = (metadata != null) ? metadata.legacyId() : null;

                if (legacyId != null) {
                    // Parse legacy ID to get namespace and id
                    String[] legacyParts = legacyId.split(":", 3);
                    if (legacyParts.length >= 2) {
                        String lType = legacyParts[0];
                        String namespace = legacyParts.length >= 3 ? legacyParts[1] : "minecraft";
                        String lId = legacyParts.length >= 3 ? legacyParts[2] : legacyParts[1];
                        return new CosmeticInfo(lType, namespace, lId, cosmeticId);
                    }
                }

                return new CosmeticInfo(type, "buildscape", id, cosmeticId);
            }
        }

        // Fallback to legacy format: type:namespace:id
        String[] parts = cosmeticId.split(":", 3);
        if (parts.length < 2) {
            BuildScape.getLogger().warn("Invalid cosmetic ID format: " + cosmeticId);
            return null;
        }

        String type = parts[0];
        String namespace = parts.length >= 3 ? parts[1] : "minecraft";
        String id = parts.length >= 3 ? parts[2] : parts[1];

        return new CosmeticInfo(type, namespace, id, cosmeticId);
    }

    /**
     * Resolve a cosmetic ID to an Item.
     * 
     * @param cosmeticId Cosmetic ID string
     * @return Item if found, null otherwise
     */
    @Nullable
    public Item resolveToItem(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return null;
        }

        // Check cache first
        if (itemCache.containsKey(cosmeticId)) {
            return itemCache.get(cosmeticId);
        }

        CosmeticInfo info = parseCosmeticId(cosmeticId);
        if (info == null || !info.type.equals("item")) {
            return null;
        }

        ResourceLocation resourceLocation = new ResourceLocation(info.namespace + ":" + info.id);
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);

        if (item != null) {
            itemCache.put(cosmeticId, item);
        } else {
            BuildScape.getLogger().warn("Could not resolve cosmetic item: " + cosmeticId);
        }

        return item;
    }

    /**
     * Resolve a cosmetic ID to a Block.
     * 
     * @param cosmeticId Cosmetic ID string
     * @return Block if found, null otherwise
     */
    @Nullable
    public Block resolveToBlock(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return null;
        }

        // Check cache first
        if (blockCache.containsKey(cosmeticId)) {
            return blockCache.get(cosmeticId);
        }

        CosmeticInfo info = parseCosmeticId(cosmeticId);
        if (info == null || !info.type.equals("block")) {
            return null;
        }

        ResourceLocation resourceLocation = new ResourceLocation(info.namespace + ":" + info.id);
        Block block = ForgeRegistries.BLOCKS.getValue(resourceLocation);

        if (block != null) {
            blockCache.put(cosmeticId, block);
        } else {
            BuildScape.getLogger().warn("Could not resolve cosmetic block: " + cosmeticId);
        }

        return block;
    }

    /**
     * Resolve a cosmetic ID to an ItemStack.
     * Handles both item and block cosmetics (blocks are converted to ItemStacks).
     * For particle trails, returns a placeholder item (nether star) for display.
     * 
     * @param cosmeticId Cosmetic ID string
     * @return ItemStack if found, null otherwise
     */
    public ItemStack resolveToItemStack(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return null;
        }

        // Check if this is a custom HEAD cosmetic - these should NOT resolve to
        // ItemStacks
        // Custom head cosmetics like builder's hat use custom models, not ItemStack
        // models
        CosmeticManager.CosmeticMetadata meta = CosmeticManager.getInstance().getMetadata(cosmeticId);
        if (meta != null && meta.type() == CosmeticManager.CosmeticType.HEAD) {
            // Return null for custom head cosmetics - they use custom rendering
            return null;
        }

        // Check cache first
        if (itemStackCache.containsKey(cosmeticId)) {
            ItemStack cached = itemStackCache.get(cosmeticId);
            return cached != null ? cached.copy() : null;
        }

        ItemStack result = null;

        CosmeticInfo info = parseCosmeticId(cosmeticId);
        if (info != null) {
            // Check if it's a particle trail (use nether star as placeholder)
            if (info.type.equals("particle")) {
                result = new ItemStack(net.minecraft.world.item.Items.NETHER_STAR);
            } else if (info.type.equals("wings")) {
                // For wings, try to resolve as item (usually elytra)
                Item item = resolveToItem(cosmeticId);
                if (item != null) {
                    result = new ItemStack(item);
                } else {
                    // Placeholder for wings if item not found
                    result = new ItemStack(net.minecraft.world.item.Items.FEATHER);
                }
            } else {
                // Try as item first
                Item item = resolveToItem(cosmeticId);
                if (item != null) {
                    result = new ItemStack(item);
                } else {
                    // Try as block
                    Block block = resolveToBlock(cosmeticId);
                    if (block != null) {
                        result = new ItemStack(block.asItem());
                    } else {
                        // Try as custom type
                        if (info.type.equals("type")) {
                            CosmeticType typeDef = typeDefinitions.get(info.id);
                            if (typeDef != null) {
                                result = typeDef.createItemStack();
                            }
                        } else {
                            // Fallback: if cosmetic metadata has a legacyId, try resolving that
                            // Reuse the meta variable we already have from the HEAD check above
                            if (meta != null && meta.legacyId() != null && !meta.legacyId().isEmpty()) {
                                Item legacyItem = resolveToItem(meta.legacyId());
                                if (legacyItem == null) {
                                    // legacyId might be block:... or item:...
                                    Block legacyBlock = resolveToBlock(meta.legacyId());
                                    if (legacyBlock != null) {
                                        result = new ItemStack(legacyBlock.asItem());
                                    }
                                } else {
                                    result = new ItemStack(legacyItem);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Cache result (even if null to avoid repeated lookups)
        itemStackCache.put(cosmeticId, result != null ? result.copy() : null);

        return result != null ? result.copy() : null;
    }

    /**
     * Check if a cosmetic ID is valid and can be resolved.
     * 
     * @param cosmeticId Cosmetic ID string
     * @return true if valid and resolvable
     */
    public boolean isValid(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return false;
        }

        CosmeticInfo info = parseCosmeticId(cosmeticId);
        if (info == null) {
            return false;
        }

        switch (info.type) {
            case "item":
                return resolveToItem(cosmeticId) != null;
            case "block":
                return resolveToBlock(cosmeticId) != null;
            case "type":
                return typeDefinitions.containsKey(info.id);
            case "nbt":
                // NBT cosmetics are handled separately
                return true;
            default:
                return false;
        }
    }

    /**
     * Clear all caches.
     * Useful for reloading or debugging.
     */
    public void clearCache() {
        itemCache.clear();
        blockCache.clear();
        itemStackCache.clear();
    }

    /**
     * Internal class to hold parsed cosmetic information.
     */
    public static class CosmeticInfo {
        public final String type;
        public final String namespace;
        public final String id;
        public final String fullId;

        public CosmeticInfo(String type, String namespace, String id, String fullId) {
            this.type = type;
            this.namespace = namespace;
            this.id = id;
            this.fullId = fullId;
        }
    }

    /**
     * Interface for custom cosmetic types.
     * Extend this to create custom cosmetic type definitions.
     */
    public interface CosmeticType {
        ItemStack createItemStack();
    }
}
