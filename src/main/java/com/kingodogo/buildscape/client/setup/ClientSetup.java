package com.kingodogo.buildscape.client.setup;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.GlowLightsBlockEntity;
import com.kingodogo.buildscape.block.ModWoodTypes;
import com.kingodogo.buildscape.block.OrnamentBlock;
import com.kingodogo.buildscape.block.StringLightBlock;
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
import net.minecraft.client.color.block.BlockColors;
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

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onRegisterClientReloadListeners(
            RegisterClientReloadListenersEvent event) {
        ResourceManagerReloadListener listener = resourceManager ->
                ClientBlockColorCatalog.invalidate();
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
                    data -> data
            );
        });

        event.enqueueWork(() -> {
            BuildscapeRenderLayers.register();
        });

        event.enqueueWork(() -> {
            MenuScreens.register(
                    ModMenuTypes.BUILDERS_WORKBENCH_MENU.get(),
                    BuildersWorkbenchScreen::new
            );
            MenuScreens.register(
                    ModMenuTypes.BUILDERS_POUCH_MENU.get(),
                    BuildersPouchScreen::new
            );












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






















            BlockColors blockColors = Minecraft
                    .getInstance().getBlockColors();
            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (reader == null || pos == null) {
                            return 0x7FA832;
                        }
                        if (tintIndex == 1) {
                            if (reader instanceof LevelReader levelReader) {
                                return levelReader
                                        .getBiome(pos)
                                        .value()
                                        .getGrassColor(pos.getX(),
                                                pos.getZ());
                            }
                            return 0x7FA832;
                        }
                        return -1;
                    },
                    ModBlocks.CLOVER.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (reader == null || pos == null) {
                            return 0x7FA832;
                        }
                        if (tintIndex == 1) {
                            if (reader instanceof LevelReader levelReader) {
                                return levelReader
                                        .getBiome(pos)
                                        .value()
                                        .getGrassColor(pos.getX(),
                                                pos.getZ());
                            }
                            return 0x7FA832;
                        }
                        return -1;
                    },
                    ModBlocks.RED_PETAL.get(),
                    ModBlocks.BLUE_PETAL.get(),
                    ModBlocks.ORANGE_PETAL.get(),
                    ModBlocks.PINK_PETAL.get(),
                    ModBlocks.PURPLE_PETAL.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex != 0) {
                            return -1;
                        }

                        if (state.hasProperty(
                                OrnamentBlock.STRING_COLOR)) {
                            OrnamentBlock.StringColor stringColor = state
                                    .getValue(
                                            OrnamentBlock.STRING_COLOR);
                            return stringColor.getColor();
                        }

                        return 0xFFFFFF;
                    },
                    ModBlocks.WHITE_ORNAMENT.get(),
                    ModBlocks.ORANGE_ORNAMENT.get(),
                    ModBlocks.MAGENTA_ORNAMENT.get(),
                    ModBlocks.LIGHT_BLUE_ORNAMENT.get(),
                    ModBlocks.YELLOW_ORNAMENT.get(),
                    ModBlocks.LIME_ORNAMENT.get(),
                    ModBlocks.PINK_ORNAMENT.get(),
                    ModBlocks.GRAY_ORNAMENT.get(),
                    ModBlocks.LIGHT_GRAY_ORNAMENT.get(),
                    ModBlocks.CYAN_ORNAMENT.get(),
                    ModBlocks.PURPLE_ORNAMENT.get(),
                    ModBlocks.BLUE_ORNAMENT.get(),
                    ModBlocks.BROWN_ORNAMENT.get(),
                    ModBlocks.GREEN_ORNAMENT.get(),
                    ModBlocks.RED_ORNAMENT.get(),
                    ModBlocks.BLACK_ORNAMENT.get(),
                    ModBlocks.GLASS_ORNAMENT.get(),
                    ModBlocks.TINTED_GLASS_ORNAMENT.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex != 0) {
                            return -1;
                        }

                        if (state.hasProperty(
                                StringLightBlock.STRING_COLOR)) {
                            StringLightBlock.StringColor stringColor = state
                                    .getValue(
                                            StringLightBlock.STRING_COLOR);
                            return stringColor.getColor();
                        }

                        return 0xFFFFFF;
                    },
                    ModBlocks.WHITE_STRING_LIGHT.get(),
                    ModBlocks.ORANGE_STRING_LIGHT.get(),
                    ModBlocks.MAGENTA_STRING_LIGHT.get(),
                    ModBlocks.LIGHT_BLUE_STRING_LIGHT.get(),
                    ModBlocks.YELLOW_STRING_LIGHT.get(),
                    ModBlocks.LIME_STRING_LIGHT.get(),
                    ModBlocks.PINK_STRING_LIGHT.get(),
                    ModBlocks.GRAY_STRING_LIGHT.get(),
                    ModBlocks.LIGHT_GRAY_STRING_LIGHT.get(),
                    ModBlocks.CYAN_STRING_LIGHT.get(),
                    ModBlocks.PURPLE_STRING_LIGHT.get(),
                    ModBlocks.BLUE_STRING_LIGHT.get(),
                    ModBlocks.BROWN_STRING_LIGHT.get(),
                    ModBlocks.GREEN_STRING_LIGHT.get(),
                    ModBlocks.RED_STRING_LIGHT.get(),
                    ModBlocks.BLACK_STRING_LIGHT.get(),
                    ModBlocks.MULTICOLOR_STRING_LIGHT.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex != 0) {
                            return -1;
                        }

                        if (reader != null
                                && pos != null
                                && reader instanceof LevelReader levelReader) {
                            return levelReader.getBiome(pos).value()
                                    .getFoliageColor();
                        }

                        return 0x48B518;
                    },
                    ModBlocks.OAK_LEAF_LAYERS.get(),
                    ModBlocks.JUNGLE_LEAF_LAYERS.get(),
                    ModBlocks.ACACIA_LEAF_LAYERS.get(),
                    ModBlocks.DARK_OAK_LEAF_LAYERS.get(),
                    ModBlocks.OAK_LEAF_HEDGE.get(),
                    ModBlocks.JUNGLE_LEAF_HEDGE.get(),
                    ModBlocks.ACACIA_LEAF_HEDGE.get(),
                    ModBlocks.DARK_OAK_LEAF_HEDGE.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex != 0) return -1;
                        if (reader != null && pos != null) {
                            return BiomeColors.getAverageFoliageColor(reader, pos);
                        }
                        return FoliageColor.getDefaultColor();
                    },
                    ModBlocks.BUSH.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex != 0) return -1;
                        if (reader != null && pos != null) {
                            return DryFoliageColor.getDryFoliageColor(reader, pos);
                        }
                        return DryFoliageColor.getDefaultColor();
                    },
                    ModBlocks.LEAF_LITTER.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex == 1 || tintIndex == 0) {
                            if (reader != null && pos != null) {
                                return BiomeColors.getAverageFoliageColor(reader, pos);
                            }
                            return FoliageColor.getDefaultColor();
                        }
                        return -1;
                    },
                    ModBlocks.WILDFLOWERS.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex != 0) {
                            return -1;
                        }
                        return 0x619961;
                    },
                    ModBlocks.SPRUCE_LEAF_LAYERS.get(),
                    ModBlocks.SPRUCE_LEAF_HEDGE.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex != 0) {
                            return -1;
                        }
                        return 0x80a755;
                    },
                    ModBlocks.BIRCH_LEAF_LAYERS.get(),
                    ModBlocks.BIRCH_LEAF_HEDGE.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        return -1;
                    },
                    ModBlocks.AZALEA_LEAF_LAYERS.get(),
                    ModBlocks.FLOWERING_AZALEA_LEAF_LAYERS.get(),
                    ModBlocks.AZALEA_LEAF_HEDGE.get(),
                    ModBlocks.FLOWERING_AZALEA_LEAF_HEDGE.get(),
                    ModBlocks.ORANGE_POPLAR_LEAF_LAYERS.get(),
                    ModBlocks.RED_POPLAR_LEAF_LAYERS.get(),
                    ModBlocks.YELLOW_POPLAR_LEAF_LAYERS.get(),
                    ModBlocks.CHERRY_LEAF_LAYERS.get(),
                    ModBlocks.PALE_OAK_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_CHERRY_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_PALE_OAK_LEAF_LAYERS.get(),
                    ModBlocks.ORANGE_POPLAR_LEAF_HEDGE.get(),
                    ModBlocks.RED_POPLAR_LEAF_HEDGE.get(),
                    ModBlocks.YELLOW_POPLAR_LEAF_HEDGE.get(),
                    ModBlocks.CHERRY_LEAF_HEDGE.get(),
                    ModBlocks.PALE_OAK_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_CHERRY_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_PALE_OAK_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_CHERRY_LEAVES.get(),
                    ModBlocks.SNOWY_PALE_OAK_LEAVES.get(),
                    ModBlocks.SNOWY_ORANGE_POPLAR_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_RED_POPLAR_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_YELLOW_POPLAR_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_ORANGE_POPLAR_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_RED_POPLAR_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_YELLOW_POPLAR_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_ORANGE_POPLAR_LEAVES.get(),
                    ModBlocks.SNOWY_RED_POPLAR_LEAVES.get(),
                    ModBlocks.SNOWY_YELLOW_POPLAR_LEAVES.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        return -1;
                    },
                    ModBlocks.SNOWY_OAK_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_SPRUCE_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_BIRCH_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_JUNGLE_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_ACACIA_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_DARK_OAK_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_MANGROVE_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_AZALEA_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS.get(),
                    ModBlocks.SNOWY_OAK_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_SPRUCE_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_BIRCH_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_JUNGLE_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_ACACIA_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_DARK_OAK_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_MANGROVE_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_AZALEA_LEAF_HEDGE.get(),
                    ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex != 0) {
                            return -1;
                        }
                        if (reader != null
                                && pos != null
                                && reader instanceof LevelReader levelReader) {
                            return levelReader.getBiome(pos).value()
                                    .getFoliageColor();
                        }
                        return 0x92c648;
                    },
                    ModBlocks.MANGROVE_LEAF_LAYERS.get(),
                    ModBlocks.MANGROVE_LEAF_HEDGE.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex != 0) {
                            return -1;
                        }
                        if (reader != null
                                && pos != null
                                && reader instanceof LevelReader levelReader) {
                            return levelReader.getBiome(pos).value()
                                    .getFoliageColor();
                        }
                        return 0x92c648;
                    },
                    ModBlocks.MANGROVE_LEAVES.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex != 0) {
                            return -1;
                        }
                        if (reader != null && pos != null) {
                            return BiomeColors.getAverageWaterColor(reader, pos);
                        }
                        return 0x3F76E4;
                    },
                    ModBlocks.CASCADE_BLOCK.get()
            );

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex != 0) {
                            return -1;
                        }
                        if (reader != null && pos != null) {
                            return BiomeColors.getAverageWaterColor(reader, pos);
                        }
                        return 0x3F76E4;
                    },
                    ModBlocks.CASCADE_BLOCK_NO_MIST.get()
            );

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex != 0) {
                            return -1;
                        }

                        if (reader != null && pos != null) {
                            BlockEntity be = reader
                                    .getBlockEntity(pos);
                            if (be instanceof GlowLightsBlockEntity glowBE) {
                                List<String> dyeColors = glowBE
                                        .getDyeColors();
                                if (dyeColors != null && !dyeColors.isEmpty()) {
                                    int hash = pos.hashCode()
                                            ^ state.hashCode();
                                    int colorIndex = Math.abs(hash)
                                            % dyeColors.size();
                                    String colorCode = dyeColors
                                            .get(colorIndex);

                                    try {
                                        if (colorCode != null
                                                && colorCode.startsWith(
                                                "#")
                                                && colorCode.length() == 7) {
                                            int color = Integer
                                                    .parseInt(colorCode
                                                                    .substring(1),
                                                            16);
                                            return color | 0xFF000000;
                                        }
                                    } catch (NumberFormatException e) {
                                    }
                                }
                            }
                        }

                        return -1;
                    },
                    ModBlocks.GLOW_LIGHTS.get());

            blockColors.register(
                    (state, reader, pos, tintIndex) -> {
                        if (tintIndex == 0) {
                            if (reader != null && pos != null) {
                                return BiomeColors.getAverageGrassColor(reader, pos);
                            }
                            return GrassColor.get(0.5D, 1.0D);
                        }
                        return -1;
                    },
                    Blocks.COMPOSTER);

            ItemColors itemColors = Minecraft
                    .getInstance().getItemColors();
            ItemColors vanillaItemColors = Minecraft
                    .getInstance().getItemColors();

            itemColors.register(
                    (stack, tintIndex) -> tintIndex == 0 ? 0x3F76E4 : -1,
                    ModItems.CASCADE_BLOCK.get()
            );

            itemColors.register(
                    (stack, tintIndex) -> {
                        if (tintIndex != 1) return -1;
                        CompoundTag tag = stack.getTagElement(NbtKeys.EXPLOSION);
                        int[] colors = tag != null ? tag.getIntArray(NbtKeys.COLORS) : null;
                        if (colors != null && colors.length > 0) {
                            if (colors.length == 1) return colors[0];
                            int r = 0, g = 0, b = 0;
                            for (int c : colors) {
                                r += (c >> 16) & 0xFF;
                                g += (c >> 8) & 0xFF;
                                b += c & 0xFF;
                            }
                            return ((r / colors.length) << 16) | ((g / colors.length) << 8) | (b / colors.length);
                        }
                        return 0xFFB000;
                    },
                    ModItems.INFINITE_PHOENIX_FIREWORK_STAR.get()
            );

            itemColors.register(
                    (stack, tintIndex) -> tintIndex == 0 ? 0x3F76E4 : -1,
                    ModItems.CASCADE_BLOCK_NO_MIST.get()
            );

            itemColors.register(
                    (stack, tintIndex) -> {
                        if (tintIndex != 0) {
                            return -1;
                        }
                        Item item = stack.getItem();
                        if (item instanceof BlockItem) {
                            Block block = ((BlockItem) item)
                                    .getBlock();

                            int color = -1;
                            if (block == ModBlocks.LEAF_LITTER.get()) {
                                color = DryFoliageColor.getDefaultColor();
                            } else if (block == ModBlocks.BUSH.get()
                                    || block == ModBlocks.OAK_LEAF_HEDGE.get()
                                    || block == ModBlocks.OAK_LEAF_LAYERS.get()) {
                                color = vanillaItemColors.getColor(
                                        new ItemStack(
                                                Items.OAK_LEAVES),
                                        0);
                            } else if (block == ModBlocks.JUNGLE_LEAF_HEDGE.get()
                                    || block == ModBlocks.JUNGLE_LEAF_LAYERS
                                    .get()) {
                                color = vanillaItemColors.getColor(
                                        new ItemStack(
                                                Items.JUNGLE_LEAVES),
                                        0);
                            } else if (block == ModBlocks.ACACIA_LEAF_HEDGE.get()
                                    || block == ModBlocks.ACACIA_LEAF_LAYERS
                                    .get()) {
                                color = vanillaItemColors.getColor(
                                        new ItemStack(
                                                Items.ACACIA_LEAVES),
                                        0);
                            } else if (block == ModBlocks.DARK_OAK_LEAF_HEDGE.get()
                                    || block == ModBlocks.DARK_OAK_LEAF_LAYERS
                                    .get()) {
                                color = vanillaItemColors.getColor(
                                        new ItemStack(
                                                Items.DARK_OAK_LEAVES),
                                        0);
                            } else if (block == ModBlocks.SPRUCE_LEAF_HEDGE.get()
                                    || block == ModBlocks.SPRUCE_LEAF_LAYERS
                                    .get()) {
                                color = 0x619961;
                            } else if (block == ModBlocks.BIRCH_LEAF_HEDGE.get()
                                    || block == ModBlocks.BIRCH_LEAF_LAYERS
                                    .get()) {
                                color = 0x80a755;
                            } else if (block == ModBlocks.MANGROVE_LEAVES.get()
                                    || block == ModBlocks.MANGROVE_LEAF_LAYERS
                                    .get()
                                    ||
                                    block == ModBlocks.MANGROVE_LEAF_HEDGE
                                            .get()) {
                                color = 0x92c648;
                            }

                            return color;
                        }
                        return -1;
                    },
                    ModItems.OAK_LEAF_LAYERS.get(),
                    ModItems.LEAF_LITTER.get(),
                    ModItems.SPRUCE_LEAF_LAYERS.get(),
                    ModItems.BIRCH_LEAF_LAYERS.get(),
                    ModItems.JUNGLE_LEAF_LAYERS.get(),
                    ModItems.ACACIA_LEAF_LAYERS.get(),
                    ModItems.DARK_OAK_LEAF_LAYERS.get(),
                    ModItems.AZALEA_LEAF_LAYERS.get(),
                    ModItems.FLOWERING_AZALEA_LEAF_LAYERS.get(),
                    ModItems.SNOWY_OAK_LEAF_LAYERS.get(),
                    ModItems.SNOWY_SPRUCE_LEAF_LAYERS.get(),
                    ModItems.SNOWY_BIRCH_LEAF_LAYERS.get(),
                    ModItems.SNOWY_JUNGLE_LEAF_LAYERS.get(),
                    ModItems.SNOWY_ACACIA_LEAF_LAYERS.get(),
                    ModItems.SNOWY_DARK_OAK_LEAF_LAYERS.get(),
                    ModItems.SNOWY_MANGROVE_LEAF_LAYERS.get(),
                    ModItems.SNOWY_AZALEA_LEAF_LAYERS.get(),
                    ModItems.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS.get(),
                    ModItems.MANGROVE_LEAF_LAYERS.get(),
                    ModItems.OAK_LEAF_HEDGE.get(),
                    ModItems.SPRUCE_LEAF_HEDGE.get(),
                    ModItems.BIRCH_LEAF_HEDGE.get(),
                    ModItems.JUNGLE_LEAF_HEDGE.get(),
                    ModItems.ACACIA_LEAF_HEDGE.get(),
                    ModItems.DARK_OAK_LEAF_HEDGE.get(),
                    ModItems.AZALEA_LEAF_HEDGE.get(),
                    ModItems.FLOWERING_AZALEA_LEAF_HEDGE.get(),
                    ModItems.BUSH.get(),
                    ModItems.SNOWY_OAK_LEAF_HEDGE.get(),
                    ModItems.SNOWY_SPRUCE_LEAF_HEDGE.get(),
                    ModItems.SNOWY_BIRCH_LEAF_HEDGE.get(),
                    ModItems.SNOWY_JUNGLE_LEAF_HEDGE.get(),
                    ModItems.SNOWY_ACACIA_LEAF_HEDGE.get(),
                    ModItems.SNOWY_DARK_OAK_LEAF_HEDGE.get(),
                    ModItems.SNOWY_MANGROVE_LEAF_HEDGE.get(),
                    ModItems.SNOWY_AZALEA_LEAF_HEDGE.get(),
                    ModItems.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE.get(),
                    ModItems.MANGROVE_LEAF_HEDGE.get(),
                    ModItems.MANGROVE_LEAVES.get());

            itemColors.register(
                    (stack, tintIndex) -> {
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
                                        Optional<? extends Registry<Biome>> registryOpt = level.registryAccess().registry(Registry.BIOME_REGISTRY);
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
                    },
                    ModItems.COPPER_BIOME_BRUSH.get(),
                    ModItems.DIAMOND_BIOME_BRUSH.get(),
                    ModItems.NETHERITE_BIOME_BRUSH.get()
            );
        });
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WanderingHomemakerModel.LAYER_LOCATION, WanderingHomemakerModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        ForgeModelBakery.addSpecialModel(
                new ResourceLocation(BuildScape.MODID, "block/stringlight_frame")
        );
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
