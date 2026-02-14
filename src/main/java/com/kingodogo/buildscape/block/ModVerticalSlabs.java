package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModVerticalSlabs {

    // Map of Original Slab Block -> Our Vertical Slab Block
    public static final Map<Block, VerticalSlabBlock> VERTICAL_SLABS = new HashMap<>();

    // Ordered list for tab insertion
    public static final List<Item> DYNAMIC_ITEMS = new ArrayList<>();

    @SubscribeEvent
    public static void onBlocksRegistry(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        // 1. Scan ForgeRegistries.BLOCKS
        for (Block block : ForgeRegistries.BLOCKS) {
            ResourceLocation regName = block.getRegistryName();
            if (regName == null) continue;
            String id = regName.getNamespace();
            if (block instanceof SlabBlock && !id.equals(BuildScape.MODID)) {
                registerVerticalSlab(registry, block);
            }
        }

        // 2. Scan our own ModBlocks.
        ModBlocks.BLOCKS.getEntries().forEach(regObj -> {
            Block block = regObj.get();
            if (block instanceof SlabBlock && !(block instanceof VerticalSlabBlock)) {
                registerVerticalSlab(registry, block);
            }
        });
    }

    private static void registerVerticalSlab(IForgeRegistry<Block> registry, Block slab) {
        ResourceLocation slabId = slab.getRegistryName();
        if (slabId == null || VERTICAL_SLABS.containsKey(slab)) return;

        // Check if another mod already registered a vertical slab for this material.
        String slabPath = slabId.getPath();
        String matName = slabPath.replace("_slab", "").replace("slab_", "");
        for (Block other : ForgeRegistries.BLOCKS) {
            ResourceLocation otherId = other.getRegistryName();
            if (otherId == null || otherId.getNamespace().equals(BuildScape.MODID)) continue;

            String otherPath = otherId.getPath().toLowerCase();
            // Look specifically for: vertical_MAT_slab, MAT_vertical_slab, MAT_vslab, etc.
            boolean isVertical = otherPath.contains("vertical") || otherPath.contains("vslab") || otherPath.startsWith("v_");
            boolean matchesMat = otherPath.contains(matName);

            if (isVertical && matchesMat) {
                // Highly specific match - skip our generation to avoid duplicates
                return;
            }
        }

        // Name: "vertical_minecraft_oak_slab" or "vertical_modid_slab_path"
        // Including namespace ensures uniqueness to avoid collisions between different mods
        String namespacePrefix = slabId.getNamespace().equals("minecraft") ? "" : slabId.getNamespace() + "_";
        String newPath = "vertical_" + namespacePrefix + slabId.getPath();
        ResourceLocation newId = new ResourceLocation(BuildScape.MODID, newPath);

        // Safety: Ensure we don't try to register same ID twice if multiple mods produce same slug
        if (VERTICAL_SLABS.values().stream().anyMatch(v -> {
            ResourceLocation vr = v.getRegistryName();
            return vr != null && vr.equals(newId);
        })) return;

        VerticalSlabBlock verticalSlab = new VerticalSlabBlock(slab);
        verticalSlab.setRegistryName(newId);

        registry.register(verticalSlab);
        VERTICAL_SLABS.put(slab, verticalSlab);
    }

    @SubscribeEvent
    public static void onItemsRegistry(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        // Register BlockItems for all our vertical slabs
        VERTICAL_SLABS.forEach((slab, verticalSlab) -> {
            ResourceLocation id = verticalSlab.getRegistryName();
            if (id == null) return;

            Item.Properties props = new Item.Properties().tab(BuildScape.BUILDSCAPE_TAB);

            BlockItem item = new BlockItem(verticalSlab, props) {
                @Override
                public Component getName(ItemStack stack) {
                    return new TranslatableComponent("buildscape.vertical_slab_prefix").append(new TranslatableComponent(slab.asItem().getDescriptionId()));
                }
            };
            item.setRegistryName(id);
            registry.register(item);
            DYNAMIC_ITEMS.add(item);
        });
    }
}
