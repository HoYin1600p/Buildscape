package com.kingodogo.buildscape.variantengine.registry;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.block.QuarterPieceBlock;
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

    public static void registerMissingBlocks(IForgeRegistry<Block> registry, List<BlockFamily> families) {
        for (BlockFamily family : families) {
            for (BlockShape shape : BlockShape.values()) {
                if (family.hasVariant(shape)) continue; // Already exists

                // Implementation logic: Selective Registration
                // We ONLY register vertical versions if:
                // 1. The base block already has a slab/stair variant (proves it's a building block)
                // 2. OR the base block itself IS a slab/stair (orphan case from other mods)
                // 3. OR it's one of our own BuildScape blocks (we want everything for our own mod)
                boolean shouldRegister = false;
                String namespace = family.getBaseBlock().getRegistryName().getNamespace();
                boolean isOurMod = namespace.equals(BuildScape.MODID);
                Block base = family.getBaseBlock();

                // Check via BiMap (populated during scanning)
                boolean hasStandardSlab = com.kingodogo.buildscape.variantengine.util.BlockBiMaps.getBlockOf(BlockShape.SLAB, base) != null;
                boolean hasStandardStair = com.kingodogo.buildscape.variantengine.util.BlockBiMaps.getBlockOf(BlockShape.STAIRS, base) != null;

                // Fallback: directly check the family's variants map
                if (!hasStandardSlab) hasStandardSlab = family.hasVariant(BlockShape.SLAB);
                if (!hasStandardStair) hasStandardStair = family.hasVariant(BlockShape.STAIRS);

                // Fallback: the base block itself IS the slab or stair (orphan block — no companion base)
                if (!hasStandardSlab && (base instanceof net.minecraft.world.level.block.SlabBlock
                        || base.defaultBlockState().is(net.minecraft.tags.BlockTags.SLABS))) {
                    hasStandardSlab = true;
                }
                if (!hasStandardStair && (base instanceof net.minecraft.world.level.block.StairBlock
                        || base.defaultBlockState().is(net.minecraft.tags.BlockTags.STAIRS))) {
                    hasStandardStair = true;
                }

                if (shape == BlockShape.VERTICAL_SLAB && (hasStandardSlab || isOurMod)) {
                    shouldRegister = true;
                } else if (shape == BlockShape.VERTICAL_STAIRS && (hasStandardStair || isOurMod)) {
                    shouldRegister = true;
                }

                if (!shouldRegister) continue;

                // Create and register new block
                Block generated = createVariantBlock(family.getBaseBlock(), shape);
                if (generated != null) {
                    net.minecraft.resources.ResourceLocation id = VariantNamingUtil.getGeneratedId(family.getBaseBlock().getRegistryName(), shape);
                    // SAFETY: Never register if the ID is already taken.
                    if (registry.containsKey(id)) {
                        family.addVariant(shape, registry.getValue(id));
                        continue;
                    }

                    generated.setRegistryName(id);
                    registry.register(generated);
                    family.addVariant(shape, generated);
                    
                    // Register in BiMaps for the creative tab
                    com.kingodogo.buildscape.variantengine.util.BlockBiMaps.setBlockOf(shape, family.getBaseBlock(), generated);
                    
                    BuildScape.LOGGER.info("VariantEngine: Registered block: {}", id);
                }
            }
        }
    }

    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        for (BlockFamily family : BlockRegistryScanner.getDetectedFamilies()) {
            for (Block variant : family.getVariants().values()) {
                if (variant.getRegistryName() != null && variant.getRegistryName().getNamespace().equals(BuildScape.MODID)) {
                    // CRITICAL FIX: Only register if the item doesn't exist yet!
                    // This avoids double-registering ModBlocks' existing items.
                    if (ForgeRegistries.ITEMS.containsKey(variant.getRegistryName())) continue;

                    // It's one of ours, register an item for it
                    net.minecraft.resources.ResourceLocation id = variant.getRegistryName();
                    registry.register(new BlockItem(variant, new Item.Properties().tab(com.kingodogo.buildscape.item.ModCreativeModeTab.BUILDSCAPE_TAB))
                            .setRegistryName(id));
                    BuildScape.LOGGER.info("VariantEngine: Registered item: {}", id);
                }
            }
        }
    }

    private static Block createVariantBlock(Block parent, BlockShape shape) {
        Block.Properties props = Block.Properties.copy(parent);

        // ALWAYS disable occlusion for partial variants (Slabs/Stairs)
        // This prevents the engine from treating them as a full cube for physics/rendering.
        props.noOcclusion();

        // Inherit light emission (fallback if lightLevel predicate is too complex in parent)
        props.lightLevel((state) -> parent.defaultBlockState().getLightEmission());

        // Explosive Resistance & Friction are already copied by Properties.copy(parent)
        
        // Sanitize properties to avoid crashes if parent has functional predicates 
        // that depend on properties our variants don't have (like AXIS in Logs).
        props.isRedstoneConductor((state, level, pos) -> {
            if (state.hasProperty(VerticalSlabBlock.TYPE)) {
                return state.getValue(VerticalSlabBlock.TYPE) == VerticalSlabBlock.VerticalSlabType.DOUBLE && parent.defaultBlockState().isRedstoneConductor(level, pos);
            }
            return false;
        });
        props.isSuffocating((state, level, pos) -> {
            if (state.hasProperty(VerticalSlabBlock.TYPE)) {
                return state.getValue(VerticalSlabBlock.TYPE) == VerticalSlabBlock.VerticalSlabType.DOUBLE && parent.defaultBlockState().isSuffocating(level, pos);
            }
            return false;
        });
        props.isViewBlocking((state, level, pos) -> {
            if (state.hasProperty(VerticalSlabBlock.TYPE)) {
                return state.getValue(VerticalSlabBlock.TYPE) == VerticalSlabBlock.VerticalSlabType.DOUBLE && parent.defaultBlockState().isViewBlocking(level, pos);
            }
            return false;
        });
        props.isValidSpawn((state, level, pos, type) -> false); 
        props.hasPostProcess((state, level, pos) -> parent.defaultBlockState().hasPostProcess(level, pos));
        props.emissiveRendering((state, level, pos) -> parent.defaultBlockState().emissiveRendering(level, pos));

        // Sanitize map color for pillar blocks (logs, etc.) that use dynamic color based on AXIS
        if (parent.getStateDefinition().getProperties().contains(net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS)) {
             props.color(parent.defaultBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS, net.minecraft.core.Direction.Axis.Y).getMapColor(null, null));
        }

        if (shape == BlockShape.VERTICAL_SLAB) return new VerticalSlabBlock(props, parent);
        if (shape == BlockShape.VERTICAL_STAIRS) return new VerticalStairsBlock(props, parent);
        if (shape == BlockShape.QUARTER_PIECE) return new QuarterPieceBlock(props, parent);
        return null;
    }
}
