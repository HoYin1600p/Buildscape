package com.kingodogo.buildscape.compat.vertical;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VerticalRegistry {

    private static final VerticalResourcePack DYNAMIC_PACK = new VerticalResourcePack();

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES || event.getPackType() == PackType.SERVER_DATA) {
            String title = "BuildScape Vertical " + (event.getPackType() == PackType.SERVER_DATA ? "Data" : "Resources");
            String description = "Dynamic vertical variants";
            
            event.addRepositorySource((consumer, constructor) -> {
                Pack pack = constructor.create(
                    "buildscape_vertical", 
                    new net.minecraft.network.chat.TextComponent(title),
                    true, 
                    () -> DYNAMIC_PACK, 
                    new net.minecraft.server.packs.metadata.pack.PackMetadataSection(
                        new net.minecraft.network.chat.TextComponent(description), 
                        9
                    ), 
                    Pack.Position.BOTTOM, 
                    PackSource.BUILT_IN
                );
                if (pack != null) {
                    consumer.accept(pack);
                }
            });
        }
    }

    public static final Map<Block, VerticalSlabBlock> VERTICAL_SLABS = new HashMap<>();
    public static final Map<Block, VerticalStairsBlock> VERTICAL_STAIRS = new HashMap<>();
    
    public static final Map<ResourceLocation, Block> REGISTERED_BLOCKS = new HashMap<>();
    public static final Map<ResourceLocation, Item> REGISTERED_ITEMS = new HashMap<>();

    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public static void onBlocksRegistry(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        BuildScape.LOGGER.info("VerticalRegistry: Starting block registration scan");

        // 1. Scan for Slabs
        VerticalScanner.findSlabs(registry).forEach(parent -> {
            ResourceLocation verticalId = getVerticalId(parent.getRegistryName());

            if (!registry.containsKey(verticalId)) {
                VerticalSlabBlock verticalSlab = new VerticalSlabBlock(BlockBehaviour.Properties.copy(parent), parent);
                verticalSlab.setRegistryName(verticalId);
                registry.register(verticalSlab);
                VERTICAL_SLABS.put(parent, verticalSlab);
                REGISTERED_BLOCKS.put(verticalId, verticalSlab);
                BuildScape.LOGGER.info("Registered vertical slab: " + verticalId + " from " + parent.getRegistryName());
            }
        });

        // 2. Scan for Stairs
        VerticalScanner.findStairs(registry).forEach(parent -> {
            ResourceLocation verticalId = getVerticalId(parent.getRegistryName());

            if (!registry.containsKey(verticalId)) {
                VerticalStairsBlock verticalStair = new VerticalStairsBlock(BlockBehaviour.Properties.copy(parent), parent);
                verticalStair.setRegistryName(verticalId);
                registry.register(verticalStair);
                VERTICAL_STAIRS.put(parent, verticalStair);
                REGISTERED_BLOCKS.put(verticalId, verticalStair);
                BuildScape.LOGGER.info("Registered vertical stair: " + verticalId + " from " + parent.getRegistryName());
            }
        });

        BuildScape.LOGGER.info("VerticalRegistry: Registered " + VERTICAL_SLABS.size() + " slabs and " + VERTICAL_STAIRS.size() + " stairs");
    }

    private static ResourceLocation getVerticalId(ResourceLocation parentId) {
        String path = parentId.getPath();
        String namespace = parentId.getNamespace();
        String newPath;
        
        if (namespace.equals("minecraft") || namespace.equals(BuildScape.MODID)) {
            newPath = "vertical_" + path;
        } else {
            newPath = path.replace("_slab", "_vertical_slab")
                          .replace("_stairs", "_vertical_stairs")
                          .replace("_stair", "_vertical_stair");
            if (!newPath.contains(namespace)) {
                newPath = namespace + "_" + newPath;
            }
        }
        return new ResourceLocation(BuildScape.MODID, newPath);
    }

    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public static void onItemsRegistry(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        REGISTERED_BLOCKS.forEach((id, block) -> {
            if (!registry.containsKey(id)) {
                Item item = new VerticalBlockItem(block, new Item.Properties())
                        .setRegistryName(id);
                registry.register(item);
                REGISTERED_ITEMS.put(id, item);
            }
        });
    }

    public static class VerticalBlockItem extends BlockItem {
        public VerticalBlockItem(Block block, Properties properties) {
            super(block, properties);
        }

        @Override
        public net.minecraft.network.chat.Component getName(ItemStack stack) {
            Block block = getBlock();
            if (block instanceof VerticalSlabBlock vsb) {
                return new TextComponent("Vertical ").append(vsb.getParentBlock().getName());
            } else if (block instanceof VerticalStairsBlock vtb) {
                return new TextComponent("Vertical ").append(vtb.getParentBlock().getName());
            }
            return super.getName(stack);
        }

        @Override
        public void appendHoverText(ItemStack stack, Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            tooltip.add(new TextComponent("§7Added by §bBuildscape"));
        }
    }
}
