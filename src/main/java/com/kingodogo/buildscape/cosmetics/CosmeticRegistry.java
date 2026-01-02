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

public class CosmeticRegistry {
    private static final CosmeticRegistry INSTANCE = new CosmeticRegistry();
    
    private final Map<String, Item> itemCache = new HashMap<>();
    private final Map<String, Block> blockCache = new HashMap<>();
    private final Map<String, ItemStack> itemStackCache = new HashMap<>();
    
    private final Map<String, CosmeticType> typeDefinitions = new HashMap<>();
    
    private CosmeticRegistry() {
    }
    
    public static CosmeticRegistry getInstance() {
        return INSTANCE;
    }
    
    @Nullable
    public CosmeticInfo parseCosmeticId(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return null;
        }

        if (cosmeticId.startsWith("buildscape:cosmatics/")) {
            String path = cosmeticId.substring("buildscape:cosmatics/".length());
            String[] parts = path.split("/", 2);
            if (parts.length == 2) {
                String category = parts[0];
                String id = parts[1];

                String type = category;
                if (category.equals("gear")) type = "item";

                CosmeticManager.CosmeticMetadata metadata = CosmeticManager.getInstance().getMetadata(cosmeticId);
                String legacyId = (metadata != null) ? metadata.legacyId : null;
                
                if (legacyId != null) {
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
    
    @Nullable
    public Item resolveToItem(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return null;
        }

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
    
    @Nullable
    public Block resolveToBlock(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return null;
        }

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
    
    public ItemStack resolveToItemStack(String cosmeticId) {
        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return null;
        }

        CosmeticManager.CosmeticMetadata meta = CosmeticManager.getInstance().getMetadata(cosmeticId);
        if (meta != null && meta.type == CosmeticManager.CosmeticType.HEAD) {
            return null;
        }

        if (itemStackCache.containsKey(cosmeticId)) {
            ItemStack cached = itemStackCache.get(cosmeticId);
            return cached != null ? cached.copy() : null;
        }
        
        ItemStack result = null;
        
        CosmeticInfo info = parseCosmeticId(cosmeticId);
        if (info != null) {
            if (info.type.equals("particle")) {
                result = new ItemStack(net.minecraft.world.item.Items.NETHER_STAR);
            } else if (info.type.equals("wings")) {
                Item item = resolveToItem(cosmeticId);
                if (item != null) {
                    result = new ItemStack(item);
                } else {
                    result = new ItemStack(net.minecraft.world.item.Items.FEATHER);
                }
            } else {
                Item item = resolveToItem(cosmeticId);
                if (item != null) {
                    result = new ItemStack(item);
                } else {
                    Block block = resolveToBlock(cosmeticId);
                    if (block != null) {
                        result = new ItemStack(block.asItem());
                    } else {
                        if (info.type.equals("type")) {
                            CosmeticType typeDef = typeDefinitions.get(info.id);
                            if (typeDef != null) {
                                result = typeDef.createItemStack();
                            }
                        } else {
                            if (meta != null && meta.legacyId != null && !meta.legacyId.isEmpty()) {
                                Item legacyItem = resolveToItem(meta.legacyId);
                                if (legacyItem == null) {
                                    Block legacyBlock = resolveToBlock(meta.legacyId);
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

        itemStackCache.put(cosmeticId, result != null ? result.copy() : null);
        
        return result != null ? result.copy() : null;
    }
    
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
                return true;
            default:
                return false;
        }
    }
    
    public void clearCache() {
        itemCache.clear();
        blockCache.clear();
        itemStackCache.clear();
    }
    
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
    
    public interface CosmeticType {
        ItemStack createItemStack();
    }
}

