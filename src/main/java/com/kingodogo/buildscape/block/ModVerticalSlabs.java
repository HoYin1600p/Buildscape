package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
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

    public static final Map<Block, Block> VERTICAL_SLABS = new HashMap<>();
    public static final List<Item> DYNAMIC_ITEMS = new ArrayList<>();


    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public static void onBlocksRegistry(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        // Use a list to collect slabs to avoid ConcurrentModificationException if we were modifying the source
        // (though ForgeRegistries.BLOCKS is iterable, it's safer to copy relevant ones)
        List<Block> slabs = new ArrayList<>();

        try {
            // Iterate all blocks currently registered (Vanilla + mods that ran before us)
            for (Block block : ForgeRegistries.BLOCKS) {
                if (block instanceof SlabBlock && !(block instanceof VerticalSlabBlock)) {
                    String path = block.getRegistryName().getPath().toLowerCase();
                    // Skip blocks that already appear to be vertical/upright variants from other mods
                    if (path.contains("vertical") || path.contains("upright")) continue;
                    slabs.add(block);
                }
            }

            // Also check ModBlocks if they haven't been added to registry yet (though with LOWEST they should be)
            ModBlocks.BLOCKS.getEntries().forEach(regObj -> {
                try {
                    Block block = regObj.get();
                    if (block instanceof SlabBlock && !(block instanceof VerticalSlabBlock)) {
                        // Avoid duplicates
                        if (!slabs.contains(block)) {
                            slabs.add(block);
                        }
                    }
                } catch (Exception ignored) {
                    // Block might not be initialized
                }
            });


            for (Block slab : slabs) {
                ResourceLocation id = slab.getRegistryName();
                if (id == null) continue;

                String newPath = "vertical_" + id.getPath();
                ResourceLocation verticalId = new ResourceLocation(BuildScape.MODID, newPath);

                if (registry.containsKey(verticalId)) continue;
                if (ForgeRegistries.BLOCKS.containsKey(verticalId)) continue; // Double check

                try {
                    Block verticalSlab = new VerticalSlabBlock(BlockBehaviour.Properties.copy(slab), slab)
                            .setRegistryName(verticalId);

                    registry.register(verticalSlab);
                    VERTICAL_SLABS.put(slab, verticalSlab);
                } catch (Exception e) {
                    BuildScape.LOGGER.error("Failed to register vertical slab for " + id, e);
                }
            }
        } catch (Exception e) {
            BuildScape.LOGGER.error("Critical error in Vertical Slab registration", e);
        }
    }

    @SubscribeEvent
    public static void onItemsRegistry(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        VERTICAL_SLABS.values().forEach(block -> {
            ResourceLocation id = block.getRegistryName();
            if (id != null && !registry.containsKey(id)) {
                Item item = new VerticalSlabBlockItem(block, new Item.Properties().tab(com.kingodogo.buildscape.item.ModCreativeModeTab.BUILDSCAPE_TAB))
                        .setRegistryName(id);
                registry.register(item);
                DYNAMIC_ITEMS.add(item);
            }
        });
    }

    public static class VerticalSlabBlockItem extends BlockItem {
        public VerticalSlabBlockItem(Block block, Properties properties) {
            super(block, properties);
        }

        @Override
        public net.minecraft.network.chat.Component getName(ItemStack stack) {
            if (getBlock() instanceof VerticalSlabBlock vsb) {
                return new net.minecraft.network.chat.TextComponent("Vertical ").append(vsb.getParentBlock().getName());
            }
            return super.getName(stack);
        }
    }
}
