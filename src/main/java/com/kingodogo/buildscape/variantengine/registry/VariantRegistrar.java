package com.kingodogo.buildscape.variantengine.registry;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.block.QuarterPieceBlock;
import com.kingodogo.buildscape.variantengine.block.VerticalQuarterPieceBlock;
import com.kingodogo.buildscape.variantengine.block.VerticalSlabBlock;
import com.kingodogo.buildscape.variantengine.block.VerticalStairsBlock;
import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import com.kingodogo.buildscape.variantengine.family.BlockFamily;
import com.kingodogo.buildscape.variantengine.util.VariantNamingUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VariantRegistrar {

    /**
     * Registers the actual Block instances for missing family variants.
     */
    public static void registerMissingBlocks(IForgeRegistry<Block> registry, List<BlockFamily> families) {
        for (BlockFamily family : families) {
            for (BlockShape shape : BlockShape.values()) {
                if (family.hasVariant(shape)) continue; // Already exists

                // Create and register new block
                Block generated = createVariantBlock(family.getBaseBlock(), shape);
                if (generated != null) {
                    generated.setRegistryName(VariantNamingUtil.getGeneratedId(family.getBaseBlock().getRegistryName(), shape));
                    registry.register(generated);
                    family.addVariant(shape, generated);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        for (BlockFamily family : BlockRegistryScanner.getDetectedFamilies()) {
            for (Block variant : family.getVariants().values()) {
                if (variant.getRegistryName() != null && variant.getRegistryName().getNamespace().equals(BuildScape.MODID)) {
                    // It's one of ours, check if it needs an item
                    if (ForgeRegistries.ITEMS.getValue(variant.getRegistryName()) == null) {
                        registry.register(new BlockItem(variant, new Item.Properties().tab(com.kingodogo.buildscape.item.ModCreativeModeTab.BUILDSCAPE_TAB))
                                .setRegistryName(variant.getRegistryName()));
                    }
                }
            }
        }
    }

    private static Block createVariantBlock(Block parent, BlockShape shape) {
        Block.Properties props = Block.Properties.copy(parent);

        // Sanitize properties to avoid crashes if parent has functional predicates 
        // that depend on properties our variants don't have (like AXIS in Logs).
        props.isRedstoneConductor((state, level, pos) -> false);
        props.isSuffocating((state, level, pos) -> false);
        props.isViewBlocking((state, level, pos) -> false);
        props.isValidSpawn((state, level, pos, type) -> false);
        props.hasPostProcess((state, level, pos) -> false);
        props.emissiveRendering((state, level, pos) -> false);
        //props.isNormalCube((state, level, pos) -> false);

        if (shape == BlockShape.VERTICAL_SLAB) return new VerticalSlabBlock(props, parent);
        if (shape == BlockShape.VERTICAL_STAIRS) return new VerticalStairsBlock(props, parent);
        if (shape == BlockShape.QUARTER_PIECE) return new QuarterPieceBlock(props, parent);
        if (shape == BlockShape.VERTICAL_QUARTER_PIECE) return new VerticalQuarterPieceBlock(props, parent);
        return null;
    }
}
