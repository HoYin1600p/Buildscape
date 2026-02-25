package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
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
public class ModVerticalStairs {

    public static final Map<Block, Block> VERTICAL_STAIRS = new HashMap<>();
    public static final List<Item> DYNAMIC_ITEMS = new ArrayList<>();

    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public static void onBlocksRegistry(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        List<Block> stairs = new ArrayList<>();

        try {
            for (Block block : ForgeRegistries.BLOCKS) {
                if (block instanceof StairBlock && !(block instanceof VerticalStairBlock)) {
                    String path = block.getRegistryName().getPath().toLowerCase();
                    // Skip blocks that already appear to be vertical/upright variants from other mods
                    if (path.contains("vertical") || path.contains("upright")) continue;
                    stairs.add(block);
                }
            }

            ModBlocks.BLOCKS.getEntries().forEach(regObj -> {
                try {
                    Block block = regObj.get();
                    if (block instanceof StairBlock && !(block instanceof VerticalStairBlock)) {
                        if (!stairs.contains(block)) {
                            stairs.add(block);
                        }
                    }
                } catch (Exception ignored) {
                }
            });


            for (Block stair : stairs) {
                ResourceLocation id = stair.getRegistryName();
                if (id == null) continue;

                String newPath = "vertical_" + id.getPath();
                ResourceLocation verticalId = new ResourceLocation(BuildScape.MODID, newPath);

                if (registry.containsKey(verticalId)) continue;
                if (ForgeRegistries.BLOCKS.containsKey(verticalId)) continue;

                try {
                    Block verticalStair = new VerticalStairBlock(BlockBehaviour.Properties.copy(stair), stair)
                            .setRegistryName(verticalId);

                    registry.register(verticalStair);
                    VERTICAL_STAIRS.put(stair, verticalStair);
                } catch (Exception e) {
                    BuildScape.LOGGER.error("Failed to register vertical stair for " + id, e);
                }
            }
        } catch (Exception e) {
            BuildScape.LOGGER.error("Critical error in Vertical Stair registration", e);
        }
    }

    @SubscribeEvent
    public static void onItemsRegistry(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        VERTICAL_STAIRS.values().forEach(block -> {
            ResourceLocation id = block.getRegistryName();
            if (id != null && !registry.containsKey(id)) {
                Item item = new VerticalStairBlockItem(block, new Item.Properties().tab(BuildScape.BUILDSCAPE_TAB))
                        .setRegistryName(id);
                registry.register(item);
                DYNAMIC_ITEMS.add(item);
            }
        });
    }

    public static class VerticalStairBlockItem extends BlockItem {
        public VerticalStairBlockItem(Block block, Properties properties) {
            super(block, properties);
        }

        @Override
        public net.minecraft.network.chat.Component getName(ItemStack stack) {
            if (getBlock() instanceof VerticalStairBlock vsb) {
                return new net.minecraft.network.chat.TextComponent("Vertical ").append(vsb.getParentBlock().getName());
            }
            return super.getName(stack);
        }
    }
}

