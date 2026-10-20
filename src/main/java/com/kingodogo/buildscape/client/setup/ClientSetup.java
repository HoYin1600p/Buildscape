package com.kingodogo.buildscape.client.setup;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.light.GlowLightsBlockEntity;
import com.kingodogo.buildscape.block.sign.ModWoodTypes;
import com.kingodogo.buildscape.block.decor.OrnamentBlock;
import com.kingodogo.buildscape.block.light.StringLightBlock;
import com.kingodogo.buildscape.client.BuildscapeRenderLayers;
import com.kingodogo.buildscape.client.ClientEvents;
import com.kingodogo.buildscape.client.DryFoliageColor;
import com.kingodogo.buildscape.client.ModKeyBinds;
import com.kingodogo.buildscape.client.model.TintedLeafHedgeModel;
import com.kingodogo.buildscape.client.model.WanderingHomemakerModel;
import com.kingodogo.buildscape.client.renderer.ColoredItemFrameRenderer;
import com.kingodogo.buildscape.client.renderer.CopperChestRenderer;
import com.kingodogo.buildscape.client.renderer.DecoratedPotBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.FallingIcicleRenderer;
import com.kingodogo.buildscape.client.renderer.FestiveStockingBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.FestiveWanderingHomemakerRenderer;
import com.kingodogo.buildscape.client.renderer.GlassJarBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.HollowLogBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.IcicleCauldronBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.MangroveBoatRenderer;
import com.kingodogo.buildscape.client.renderer.PillarBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.PoplarBoatRenderer;
import com.kingodogo.buildscape.client.renderer.ShelfRenderer;
import com.kingodogo.buildscape.client.renderer.TrappedDecoratedPotBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.WanderingHomemakerRenderer;
import com.kingodogo.buildscape.client.screen.BuildersPouchScreen;
import com.kingodogo.buildscape.client.screen.BuildersWorkbenchScreen;
import com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData;
import com.kingodogo.buildscape.client.workbench.ClientBlockColorCatalog;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.feature.biome.BiomeBrushItem;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModEntities;
import com.kingodogo.buildscape.registry.ModItems;
import com.kingodogo.buildscape.registry.ModMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Client-only mod-bus setup: registers key bindings, block entity and entity renderers, menu screens, block and item
 * colors, layer definitions and the special models, and swaps the leaf hedge models for tinted ones when models bake.
 */
@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onRegisterClientReloadListeners(
            RegisterClientReloadListenersEvent event) {
        ResourceManagerReloadListener listener = resourceManager -> ClientBlockColorCatalog.invalidate();
        event.registerReloadListener(listener);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModWoodTypes.MANGROVE.getClass();
            ModWoodTypes.BAMBOO.getClass();
        });

        event.enqueueWork(() -> {
            ModKeyBinds.register();
        });

        event.enqueueWork(() -> {
            ClientEvents.initializeConfigCallback();
        });

        event.enqueueWork(() -> {
            MinecraftForgeClient.registerTooltipComponentFactory(
                    ShulkerBoxTooltipData.class,
                    data -> data);
        });

        event.enqueueWork(() -> {
            BuildscapeRenderLayers.register();
        });

        event.enqueueWork(() -> {
            registerScreens();
            registerBlockEntityRenderers();
            registerEntityRenderers();
            registerBlockColors();
            registerItemColors();
        });
    }

    private static void registerScreens() {
        MenuScreens.register(
                ModMenuTypes.BUILDERS_WORKBENCH_MENU.get(),
                BuildersWorkbenchScreen::new);
        MenuScreens.register(
                ModMenuTypes.BUILDERS_POUCH_MENU.get(),
                BuildersPouchScreen::new);
    }

    private static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(
                ModBlockEntities.PILLAR_BLOCK_ENTITY
                        .get(),
                PillarBlockEntityRenderer::new);
        BlockEntityRenderers.register(
                ModBlockEntities.DECORATED_POT_BLOCK_ENTITY
                        .get(),
                DecoratedPotBlockEntityRenderer::new);
        BlockEntityRenderers.register(
                ModBlockEntities.TRAPPED_DECORATED_POT_BLOCK_ENTITY
                        .get(),
                TrappedDecoratedPotBlockEntityRenderer::new);
        BlockEntityRenderers.register(
                ModBlockEntities.ICICLE_CAULDRON_BLOCK_ENTITY
                        .get(),
                IcicleCauldronBlockEntityRenderer::new);

        BlockEntityRenderers.register(
                ModBlockEntities.FESTIVE_STOCKING_BLOCK_ENTITY
                        .get(),
                FestiveStockingBlockEntityRenderer::new);
        BlockEntityRenderers.register(
                ModBlockEntities.GLASS_JAR_BLOCK_ENTITY
                        .get(),
                GlassJarBlockEntityRenderer::new);
        BlockEntityRenderers.register(
                ModBlockEntities.COPPER_CHEST
                        .get(),
                CopperChestRenderer::new);
        BlockEntityRenderers.register(
                ModBlockEntities.SHELF
                        .get(),
                ShelfRenderer::new);

        BlockEntityRenderers.register(
                ModBlockEntities.MANGROVE_SIGN_BLOCK_ENTITY
                        .get(),
                SignRenderer::new);

        BlockEntityRenderers.register(
                ModBlockEntities.BAMBOO_SIGN_BLOCK_ENTITY
                        .get(),
                SignRenderer::new);

        BlockEntityRenderers.register(
                ModBlockEntities.HOLLOW_LOG_BLOCK_ENTITY
                        .get(),
                HollowLogBlockEntityRenderer::new);
    }

    private static void registerEntityRenderers() {
        EntityRenderers.register(
                ModEntities.FALLING_ICICLE.get(),
                FallingIcicleRenderer::new);

        EntityRenderers.register(
                ModEntities.MANGROVE_BOAT.get(),
                MangroveBoatRenderer::new);

        EntityRenderers.register(
                ModEntities.POPLAR_BOAT.get(),
                PoplarBoatRenderer::new);

        EntityRenderers.register(
                ModEntities.COLORED_ITEM_FRAME.get(),
                ColoredItemFrameRenderer::new);

        EntityRenderers.register(
                ModEntities.SEAT_ENTITY.get(),
                NoopRenderer::new);

        EntityRenderers.register(
                ModEntities.FESTIVE_STOCKING.get(),
                NoopRenderer::new);

        EntityRenderers.register(
                ModEntities.WANDERING_HOMEMAKER.get(),
                WanderingHomemakerRenderer::new);
        EntityRenderers.register(
                ModEntities.FESTIVE_WANDERING_HOMEMAKER.get(),
                FestiveWanderingHomemakerRenderer::new);
    }

    private static void registerBlockColors() {
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        for (BlockTint tint : blockTints()) {
            blockColors.register(
                    tint.provider(),
                    tint.blocks().stream().map(Supplier::get).toArray(Block[]::new));
        }
    }

    /** The block colour registrations, in registration order. */
    private static List<BlockTint> blockTints() {
        return List.of(
                blockTint(GRASS_LAYER_TINT,
                        ModBlocks.CLOVER),
                blockTint(GRASS_LAYER_TINT,
                        ModBlocks.RED_PETAL,
                        ModBlocks.BLUE_PETAL,
                        ModBlocks.ORANGE_PETAL,
                        ModBlocks.PINK_PETAL,
                        ModBlocks.PURPLE_PETAL),
                blockTint(ORNAMENT_TINT,
                        ModBlocks.WHITE_ORNAMENT,
                        ModBlocks.ORANGE_ORNAMENT,
                        ModBlocks.MAGENTA_ORNAMENT,
                        ModBlocks.LIGHT_BLUE_ORNAMENT,
                        ModBlocks.YELLOW_ORNAMENT,
                        ModBlocks.LIME_ORNAMENT,
                        ModBlocks.PINK_ORNAMENT,
                        ModBlocks.GRAY_ORNAMENT,
                        ModBlocks.LIGHT_GRAY_ORNAMENT,
                        ModBlocks.CYAN_ORNAMENT,
                        ModBlocks.PURPLE_ORNAMENT,
                        ModBlocks.BLUE_ORNAMENT,
                        ModBlocks.BROWN_ORNAMENT,
                        ModBlocks.GREEN_ORNAMENT,
                        ModBlocks.RED_ORNAMENT,
                        ModBlocks.BLACK_ORNAMENT,
                        ModBlocks.GLASS_ORNAMENT,
                        ModBlocks.TINTED_GLASS_ORNAMENT),
                blockTint(STRING_LIGHT_TINT,
                        ModBlocks.WHITE_STRING_LIGHT,
                        ModBlocks.ORANGE_STRING_LIGHT,
                        ModBlocks.MAGENTA_STRING_LIGHT,
                        ModBlocks.LIGHT_BLUE_STRING_LIGHT,
                        ModBlocks.YELLOW_STRING_LIGHT,
                        ModBlocks.LIME_STRING_LIGHT,
                        ModBlocks.PINK_STRING_LIGHT,
                        ModBlocks.GRAY_STRING_LIGHT,
                        ModBlocks.LIGHT_GRAY_STRING_LIGHT,
                        ModBlocks.CYAN_STRING_LIGHT,
                        ModBlocks.PURPLE_STRING_LIGHT,
                        ModBlocks.BLUE_STRING_LIGHT,
                        ModBlocks.BROWN_STRING_LIGHT,
                        ModBlocks.GREEN_STRING_LIGHT,
                        ModBlocks.RED_STRING_LIGHT,
                        ModBlocks.BLACK_STRING_LIGHT,
                        ModBlocks.MULTICOLOR_STRING_LIGHT),
                blockTint(BIOME_FOLIAGE_TINT,
                        ModBlocks.OAK_LEAF_LAYERS,
                        ModBlocks.JUNGLE_LEAF_LAYERS,
                        ModBlocks.ACACIA_LEAF_LAYERS,
                        ModBlocks.DARK_OAK_LEAF_LAYERS,
                        ModBlocks.OAK_LEAF_HEDGE,
                        ModBlocks.JUNGLE_LEAF_HEDGE,
                        ModBlocks.ACACIA_LEAF_HEDGE,
                        ModBlocks.DARK_OAK_LEAF_HEDGE),
                blockTint(BUSH_TINT,
                        ModBlocks.BUSH),
                blockTint(LEAF_LITTER_TINT,
                        ModBlocks.LEAF_LITTER),
                blockTint(WILDFLOWERS_TINT,
                        ModBlocks.WILDFLOWERS),
                blockTint(SPRUCE_LEAF_TINT,
                        ModBlocks.SPRUCE_LEAF_LAYERS,
                        ModBlocks.SPRUCE_LEAF_HEDGE),
                blockTint(BIRCH_LEAF_TINT,
                        ModBlocks.BIRCH_LEAF_LAYERS,
                        ModBlocks.BIRCH_LEAF_HEDGE),
                blockTint(NO_TINT,
                        ModBlocks.AZALEA_LEAF_LAYERS,
                        ModBlocks.FLOWERING_AZALEA_LEAF_LAYERS,
                        ModBlocks.AZALEA_LEAF_HEDGE,
                        ModBlocks.FLOWERING_AZALEA_LEAF_HEDGE,
                        ModBlocks.ORANGE_POPLAR_LEAF_LAYERS,
                        ModBlocks.RED_POPLAR_LEAF_LAYERS,
                        ModBlocks.YELLOW_POPLAR_LEAF_LAYERS,
                        ModBlocks.CHERRY_LEAF_LAYERS,
                        ModBlocks.PALE_OAK_LEAF_LAYERS,
                        ModBlocks.SNOWY_CHERRY_LEAF_LAYERS,
                        ModBlocks.SNOWY_PALE_OAK_LEAF_LAYERS,
                        ModBlocks.ORANGE_POPLAR_LEAF_HEDGE,
                        ModBlocks.RED_POPLAR_LEAF_HEDGE,
                        ModBlocks.YELLOW_POPLAR_LEAF_HEDGE,
                        ModBlocks.CHERRY_LEAF_HEDGE,
                        ModBlocks.PALE_OAK_LEAF_HEDGE,
                        ModBlocks.SNOWY_CHERRY_LEAF_HEDGE,
                        ModBlocks.SNOWY_PALE_OAK_LEAF_HEDGE,
                        ModBlocks.SNOWY_CHERRY_LEAVES,
                        ModBlocks.SNOWY_PALE_OAK_LEAVES,
                        ModBlocks.SNOWY_ORANGE_POPLAR_LEAF_LAYERS,
                        ModBlocks.SNOWY_RED_POPLAR_LEAF_LAYERS,
                        ModBlocks.SNOWY_YELLOW_POPLAR_LEAF_LAYERS,
                        ModBlocks.SNOWY_ORANGE_POPLAR_LEAF_HEDGE,
                        ModBlocks.SNOWY_RED_POPLAR_LEAF_HEDGE,
                        ModBlocks.SNOWY_YELLOW_POPLAR_LEAF_HEDGE,
                        ModBlocks.SNOWY_ORANGE_POPLAR_LEAVES,
                        ModBlocks.SNOWY_RED_POPLAR_LEAVES,
                        ModBlocks.SNOWY_YELLOW_POPLAR_LEAVES),
                blockTint(NO_TINT,
                        ModBlocks.SNOWY_OAK_LEAF_LAYERS,
                        ModBlocks.SNOWY_SPRUCE_LEAF_LAYERS,
                        ModBlocks.SNOWY_BIRCH_LEAF_LAYERS,
                        ModBlocks.SNOWY_JUNGLE_LEAF_LAYERS,
                        ModBlocks.SNOWY_ACACIA_LEAF_LAYERS,
                        ModBlocks.SNOWY_DARK_OAK_LEAF_LAYERS,
                        ModBlocks.SNOWY_MANGROVE_LEAF_LAYERS,
                        ModBlocks.SNOWY_AZALEA_LEAF_LAYERS,
                        ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS,
                        ModBlocks.SNOWY_OAK_LEAF_HEDGE,
                        ModBlocks.SNOWY_SPRUCE_LEAF_HEDGE,
                        ModBlocks.SNOWY_BIRCH_LEAF_HEDGE,
                        ModBlocks.SNOWY_JUNGLE_LEAF_HEDGE,
                        ModBlocks.SNOWY_ACACIA_LEAF_HEDGE,
                        ModBlocks.SNOWY_DARK_OAK_LEAF_HEDGE,
                        ModBlocks.SNOWY_MANGROVE_LEAF_HEDGE,
                        ModBlocks.SNOWY_AZALEA_LEAF_HEDGE,
                        ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE),
                blockTint(MANGROVE_LEAF_TINT,
                        ModBlocks.MANGROVE_LEAF_LAYERS,
                        ModBlocks.MANGROVE_LEAF_HEDGE),
                blockTint(MANGROVE_LEAF_TINT,
                        ModBlocks.MANGROVE_LEAVES),
                blockTint(WATER_TINT,
                        ModBlocks.CASCADE_BLOCK),
                blockTint(WATER_TINT,
                        ModBlocks.CASCADE_BLOCK_NO_MIST),
                blockTint(GLOW_LIGHTS_TINT,
                        ModBlocks.GLOW_LIGHTS),
                blockTint(COMPOSTER_TINT,
                        () -> Blocks.COMPOSTER));
    }

    private static void registerItemColors() {
        ItemColors itemColors = Minecraft.getInstance().getItemColors();
        for (ItemTint tint : itemTints(itemColors)) {
            itemColors.register(
                    tint.provider(),
                    tint.items().stream().map(Supplier::get).toArray(ItemLike[]::new));
        }
    }

    /** The item colour registrations, in registration order. */
    private static List<ItemTint> itemTints(ItemColors vanillaItemColors) {
        return List.of(
                itemTint(CASCADE_ITEM_TINT,
                        ModItems.CASCADE_BLOCK),
                itemTint(PHOENIX_STAR_TINT,
                        ModItems.INFINITE_PHOENIX_FIREWORK_STAR),
                itemTint(CASCADE_ITEM_TINT,
                        ModItems.CASCADE_BLOCK_NO_MIST),
                itemTint(leafItemTint(vanillaItemColors),
                        ModItems.OAK_LEAF_LAYERS,
                        ModItems.LEAF_LITTER,
                        ModItems.SPRUCE_LEAF_LAYERS,
                        ModItems.BIRCH_LEAF_LAYERS,
                        ModItems.JUNGLE_LEAF_LAYERS,
                        ModItems.ACACIA_LEAF_LAYERS,
                        ModItems.DARK_OAK_LEAF_LAYERS,
                        ModItems.AZALEA_LEAF_LAYERS,
                        ModItems.FLOWERING_AZALEA_LEAF_LAYERS,
                        ModItems.SNOWY_OAK_LEAF_LAYERS,
                        ModItems.SNOWY_SPRUCE_LEAF_LAYERS,
                        ModItems.SNOWY_BIRCH_LEAF_LAYERS,
                        ModItems.SNOWY_JUNGLE_LEAF_LAYERS,
                        ModItems.SNOWY_ACACIA_LEAF_LAYERS,
                        ModItems.SNOWY_DARK_OAK_LEAF_LAYERS,
                        ModItems.SNOWY_MANGROVE_LEAF_LAYERS,
                        ModItems.SNOWY_AZALEA_LEAF_LAYERS,
                        ModItems.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS,
                        ModItems.MANGROVE_LEAF_LAYERS,
                        ModItems.OAK_LEAF_HEDGE,
                        ModItems.SPRUCE_LEAF_HEDGE,
                        ModItems.BIRCH_LEAF_HEDGE,
                        ModItems.JUNGLE_LEAF_HEDGE,
                        ModItems.ACACIA_LEAF_HEDGE,
                        ModItems.DARK_OAK_LEAF_HEDGE,
                        ModItems.AZALEA_LEAF_HEDGE,
                        ModItems.FLOWERING_AZALEA_LEAF_HEDGE,
                        ModItems.BUSH,
                        ModItems.SNOWY_OAK_LEAF_HEDGE,
                        ModItems.SNOWY_SPRUCE_LEAF_HEDGE,
                        ModItems.SNOWY_BIRCH_LEAF_HEDGE,
                        ModItems.SNOWY_JUNGLE_LEAF_HEDGE,
                        ModItems.SNOWY_ACACIA_LEAF_HEDGE,
                        ModItems.SNOWY_DARK_OAK_LEAF_HEDGE,
                        ModItems.SNOWY_MANGROVE_LEAF_HEDGE,
                        ModItems.SNOWY_AZALEA_LEAF_HEDGE,
                        ModItems.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE,
                        ModItems.MANGROVE_LEAF_HEDGE,
                        ModItems.MANGROVE_LEAVES),
                itemTint(BIOME_BRUSH_TINT,
                        ModItems.COPPER_BIOME_BRUSH,
                        ModItems.DIAMOND_BIOME_BRUSH,
                        ModItems.NETHERITE_BIOME_BRUSH));
    }

    /** One colour provider and the blocks it tints, registered together. */
    private record BlockTint(BlockColor provider, List<Supplier<? extends Block>> blocks) {
    }

    /** One colour provider and the items it tints, registered together. */
    private record ItemTint(ItemColor provider, List<Supplier<? extends ItemLike>> items) {
    }

    @SafeVarargs
    private static BlockTint blockTint(BlockColor provider, Supplier<? extends Block>... blocks) {
        return new BlockTint(provider, List.of(blocks));
    }

    @SafeVarargs
    private static ItemTint itemTint(ItemColor provider, Supplier<? extends ItemLike>... items) {
        return new ItemTint(provider, List.of(items));
    }

    /** Grass-coloured second layer of the clover and petal blocks. */
    private static final BlockColor GRASS_LAYER_TINT = (state, reader, pos, tintIndex) -> {
        if (reader == null || pos == null) {
            return 0x7FA832;
        }
        if (tintIndex == 1) {
            if (reader instanceof LevelReader levelReader) {
                return levelReader.getBiome(pos).value().getGrassColor(pos.getX(), pos.getZ());
            }
            return 0x7FA832;
        }
        return -1;
    };

    private static final BlockColor ORNAMENT_TINT = (state, reader, pos, tintIndex) -> {
        if (tintIndex != 0) {
            return -1;
        }

        if (state.hasProperty(OrnamentBlock.STRING_COLOR)) {
            OrnamentBlock.StringColor stringColor = state.getValue(OrnamentBlock.STRING_COLOR);
            return stringColor.getColor();
        }

        return 0xFFFFFF;
    };

    private static final BlockColor STRING_LIGHT_TINT = (state, reader, pos, tintIndex) -> {
        if (tintIndex != 0) {
            return -1;
        }

        if (state.hasProperty(StringLightBlock.STRING_COLOR)) {
            StringLightBlock.StringColor stringColor = state.getValue(StringLightBlock.STRING_COLOR);
            return stringColor.getColor();
        }

        return 0xFFFFFF;
    };

    /** Biome foliage colour for the oak-family leaf layers and hedges. */
    private static final BlockColor BIOME_FOLIAGE_TINT = (state, reader, pos, tintIndex) -> {
        if (tintIndex != 0) {
            return -1;
        }

        if (reader != null && pos != null && reader instanceof LevelReader levelReader) {
            return levelReader.getBiome(pos).value().getFoliageColor();
        }

        return 0x48B518;
    };

    private static final BlockColor BUSH_TINT = (state, reader, pos, tintIndex) -> {
        if (tintIndex != 0) {
            return -1;
        }
        if (reader != null && pos != null) {
            return BiomeColors.getAverageFoliageColor(reader, pos);
        }
        return FoliageColor.getDefaultColor();
    };

    private static final BlockColor LEAF_LITTER_TINT = (state, reader, pos, tintIndex) -> {
        if (tintIndex != 0) {
            return -1;
        }
        if (reader != null && pos != null) {
            return DryFoliageColor.getDryFoliageColor(reader, pos);
        }
        return DryFoliageColor.getDefaultColor();
    };

    private static final BlockColor WILDFLOWERS_TINT = (state, reader, pos, tintIndex) -> {
        if (tintIndex == 1 || tintIndex == 0) {
            if (reader != null && pos != null) {
                return BiomeColors.getAverageFoliageColor(reader, pos);
            }
            return FoliageColor.getDefaultColor();
        }
        return -1;
    };

    private static final BlockColor SPRUCE_LEAF_TINT = fixedLeafTint(0x619961);

    private static final BlockColor BIRCH_LEAF_TINT = fixedLeafTint(0x80a755);

    /** Leaves whose textures carry their own colour: no tint. */
    private static final BlockColor NO_TINT = (state, reader, pos, tintIndex) -> -1;

    private static final BlockColor MANGROVE_LEAF_TINT = (state, reader, pos, tintIndex) -> {
        if (tintIndex != 0) {
            return -1;
        }
        if (reader != null && pos != null && reader instanceof LevelReader levelReader) {
            return levelReader.getBiome(pos).value().getFoliageColor();
        }
        return 0x92c648;
    };

    private static final BlockColor WATER_TINT = (state, reader, pos, tintIndex) -> {
        if (tintIndex != 0) {
            return -1;
        }
        if (reader != null && pos != null) {
            return BiomeColors.getAverageWaterColor(reader, pos);
        }
        return 0x3F76E4;
    };

    private static final BlockColor GLOW_LIGHTS_TINT = (state, reader, pos, tintIndex) -> {
        if (tintIndex != 0) {
            return -1;
        }

        if (reader != null && pos != null) {
            BlockEntity be = reader.getBlockEntity(pos);
            if (be instanceof GlowLightsBlockEntity glowBE) {
                List<String> dyeColors = glowBE.getDyeColors();
                if (dyeColors != null && !dyeColors.isEmpty()) {
                    int hash = pos.hashCode() ^ state.hashCode();
                    int colorIndex = Math.abs(hash) % dyeColors.size();
                    String colorCode = dyeColors.get(colorIndex);

                    try {
                        if (colorCode != null && colorCode.startsWith("#") && colorCode.length() == 7) {
                            int color = Integer.parseInt(colorCode.substring(1), 16);
                            return color | 0xFF000000;
                        }
                    } catch (NumberFormatException e) {
                        // malformed tint colour code; fall through to the default tint
                    }
                }
            }
        }

        return -1;
    };

    private static final BlockColor COMPOSTER_TINT = (state, reader, pos, tintIndex) -> {
        if (tintIndex == 0) {
            if (reader != null && pos != null) {
                return BiomeColors.getAverageGrassColor(reader, pos);
            }
            return GrassColor.get(0.5, 1.0);
        }
        return -1;
    };

    /** A leaf tint with one fixed colour on tint layer 0. */
    private static BlockColor fixedLeafTint(int color) {
        return (state, reader, pos, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }
            return color;
        };
    }

    /** Water-blue tint for the cascade block items. */
    private static final ItemColor CASCADE_ITEM_TINT = (stack, tintIndex) -> tintIndex == 0 ? 0x3F76E4 : -1;

    /** Average of the explosion colours stored on the infinite phoenix firework star. */
    private static final ItemColor PHOENIX_STAR_TINT = (stack, tintIndex) -> {
        if (tintIndex != 1) {
            return -1;
        }
        CompoundTag tag = stack.getTagElement(NbtKeys.EXPLOSION);
        int[] colors = tag != null ? tag.getIntArray(NbtKeys.COLORS) : null;
        if (colors != null && colors.length > 0) {
            if (colors.length == 1) {
                return colors[0];
            }
            int r = 0, g = 0, b = 0;
            for (int c : colors) {
                r += (c >> 16) & 0xFF;
                g += (c >> 8) & 0xFF;
                b += c & 0xFF;
            }
            return ((r / colors.length) << 16) | ((g / colors.length) << 8) | (b / colors.length);
        }
        return 0xFFB000;
    };

    /** Tint of the leaf layer, hedge and bush items, taken from the matching vanilla leaves where there are any. */
    private static ItemColor leafItemTint(ItemColors vanillaItemColors) {
        return (stack, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }
            Item item = stack.getItem();
            if (item instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();

                int color = -1;
                if (block == ModBlocks.LEAF_LITTER.get()) {
                    color = DryFoliageColor.getDefaultColor();
                } else if (block == ModBlocks.BUSH.get()
                        || block == ModBlocks.OAK_LEAF_HEDGE.get()
                        || block == ModBlocks.OAK_LEAF_LAYERS.get()) {
                    color = vanillaItemColors.getColor(new ItemStack(Items.OAK_LEAVES), 0);
                } else if (block == ModBlocks.JUNGLE_LEAF_HEDGE.get() || block == ModBlocks.JUNGLE_LEAF_LAYERS.get()) {
                    color = vanillaItemColors.getColor(new ItemStack(Items.JUNGLE_LEAVES), 0);
                } else if (block == ModBlocks.ACACIA_LEAF_HEDGE.get() || block == ModBlocks.ACACIA_LEAF_LAYERS.get()) {
                    color = vanillaItemColors.getColor(new ItemStack(Items.ACACIA_LEAVES), 0);
                } else if (block == ModBlocks.DARK_OAK_LEAF_HEDGE.get()
                        || block == ModBlocks.DARK_OAK_LEAF_LAYERS.get()) {
                    color = vanillaItemColors.getColor(new ItemStack(Items.DARK_OAK_LEAVES), 0);
                } else if (block == ModBlocks.SPRUCE_LEAF_HEDGE.get() || block == ModBlocks.SPRUCE_LEAF_LAYERS.get()) {
                    color = 0x619961;
                } else if (block == ModBlocks.BIRCH_LEAF_HEDGE.get() || block == ModBlocks.BIRCH_LEAF_LAYERS.get()) {
                    color = 0x80a755;
                } else if (block == ModBlocks.MANGROVE_LEAVES.get()
                        || block == ModBlocks.MANGROVE_LEAF_LAYERS.get()
                        || block == ModBlocks.MANGROVE_LEAF_HEDGE.get()) {
                    color = 0x92c648;
                }

                return color;
            }
            return -1;
        };
    }

    /** Foliage colour of the biome a biome brush has captured. */
    private static final ItemColor BIOME_BRUSH_TINT = (stack, tintIndex) -> {
        if (tintIndex != 1) {
            return -1;
        }
        if (stack.getItem() instanceof BiomeBrushItem brush) {
            String biomeStr = brush.getCapturedBiome(stack);
            if (biomeStr != null && !biomeStr.isEmpty()) {
                ResourceLocation biomeKey = ResourceLocation.tryParse(biomeStr);
                if (biomeKey != null) {
                    ClientLevel level = Minecraft.getInstance().level;
                    if (level != null) {
                        Optional<? extends Registry<Biome>> registryOpt = level.registryAccess()
                                .registry(Registry.BIOME_REGISTRY);
                        if (registryOpt.isPresent()) {
                            Biome biome = registryOpt.get().get(biomeKey);
                            if (biome != null) {
                                return biome.getFoliageColor();
                            }
                        }
                    }
                }
            }
        }
        return -1;
    };

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WanderingHomemakerModel.LAYER_LOCATION, WanderingHomemakerModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        ForgeModelBakery.addSpecialModel(
                new ResourceLocation(BuildScape.MODID, "block/stringlight_frame"));
    }

    @SubscribeEvent
    public static void onModelBake(
            ModelBakeEvent event) {
        Set<ResourceLocation> leafHedgeModels = new HashSet<>();
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/oak_leaf_hedge_post"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/oak_leaf_hedge_side"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/oak_leaf_hedge_side_tall"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/oak_leaf_hedge_inventory"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/spruce_leaf_hedge_post"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/spruce_leaf_hedge_side"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/spruce_leaf_hedge_side_tall"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/spruce_leaf_hedge_inventory"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/birch_leaf_hedge_post"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/birch_leaf_hedge_side"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/birch_leaf_hedge_side_tall"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/birch_leaf_hedge_inventory"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/jungle_leaf_hedge_post"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/jungle_leaf_hedge_side"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/jungle_leaf_hedge_side_tall"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/jungle_leaf_hedge_inventory"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/acacia_leaf_hedge_post"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/acacia_leaf_hedge_side"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/acacia_leaf_hedge_side_tall"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/acacia_leaf_hedge_inventory"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/dark_oak_leaf_hedge_post"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/dark_oak_leaf_hedge_side"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/dark_oak_leaf_hedge_side_tall"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/dark_oak_leaf_hedge_inventory"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/azalea_leaf_hedge_post"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/azalea_leaf_hedge_side"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/azalea_leaf_hedge_side_tall"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/azalea_leaf_hedge_inventory"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/flowering_azalea_leaf_hedge_post"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/flowering_azalea_leaf_hedge_side"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/flowering_azalea_leaf_hedge_side_tall"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "block/flowering_azalea_leaf_hedge_inventory"));

        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "item/oak_leaf_hedge"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "item/spruce_leaf_hedge"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "item/birch_leaf_hedge"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "item/jungle_leaf_hedge"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "item/acacia_leaf_hedge"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "item/dark_oak_leaf_hedge"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "item/azalea_leaf_hedge"));
        leafHedgeModels.add(
                new ResourceLocation(
                        BuildScape.MODID,
                        "item/flowering_azalea_leaf_hedge"));

        int wrappedCount = 0;
        int notFoundCount = 0;
        for (ResourceLocation modelLocation : leafHedgeModels) {
            BakedModel originalModel = event
                    .getModelRegistry()
                    .get(modelLocation);
            if (originalModel != null) {
                event
                        .getModelRegistry()
                        .put(
                                modelLocation,
                                new TintedLeafHedgeModel(
                                        originalModel));
                wrappedCount++;
            } else {
                notFoundCount++;
            }
        }

    }

}
