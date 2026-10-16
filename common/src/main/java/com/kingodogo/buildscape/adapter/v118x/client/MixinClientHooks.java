package com.kingodogo.buildscape.adapter.v118x.client;

import com.kingodogo.buildscape.client.renderer.FestiveGlintHandler;
import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import com.kingodogo.buildscape.mixinsupport.IMixinFactory;
import com.kingodogo.buildscape.mixin.StonecutterMenuLegacyAccessor;
import com.kingodogo.buildscape.util.BeaconBeamScanState;
import com.kingodogo.buildscape.util.BeaconScanContext;
import com.kingodogo.buildscape.util.GhostFilterMenu;
import com.kingodogo.buildscape.util.StonecutterMenuExtension;
import com.kingodogo.buildscape.stat.ModStats;
import com.kingodogo.buildscape.world.ModGameRules;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Client-only half of the 1.18.2 MixinFactory. It holds every member that touches client classes
 * so the dedicated server can verify and load MixinFactory itself (the bytecode verifier loads
 * client types whenever one is passed where a supertype is expected).
 */
public final class MixinClientHooks {
    public static final MixinClientHooks INSTANCE = new MixinClientHooks();

    /**
     * How an extra (standalone) model registered by the loader is found in the baked model manager.
     * Forge keeps it under a ModelResourceLocation with an empty variant; Fabric bakes it under the plain
     * ResourceLocation, which only its BakedModelManagerHelper can read, so the Fabric client replaces this.
     */
    public static java.util.function.BiFunction<ModelManager, net.minecraft.resources.ResourceLocation, BakedModel> EXTRA_MODEL_LOOKUP =
            (manager, id) -> manager.getModel(new net.minecraft.client.resources.model.ModelResourceLocation(id, ""));

    private MixinClientHooks() {}


    public void renderSignFrame(SignBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSourceObj, int combinedLight, int combinedOverlay) {
        if (blockEntity == null || blockEntity.getLevel() == null) return;
        MultiBufferSource bufferSource = (MultiBufferSource) bufferSourceObj;

        SignFrameType frameType = SignFrameAttachment.getFrame(blockEntity);
        if (frameType == SignFrameType.NONE || frameType.getModelLocation() == null) return;

        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BakedModel frameModel = EXTRA_MODEL_LOOKUP.apply(modelManager,
                new net.minecraft.resources.ResourceLocation(frameType.getModelLocation().getNamespace(), frameType.getModelLocation().getPath()));
        if (frameModel == null || frameModel == modelManager.getMissingModel()) return;

        BlockState state = blockEntity.getBlockState();
        Block block = state.getBlock();

        poseStack.pushPose();

        if (block instanceof WallSignBlock) {
            Direction facing = state.getValue(WallSignBlock.FACING);
            float yRot = (facing.toYRot() + 180.0F) % 360.0F;
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-yRot));
            poseStack.translate(-0.5D, -0.5D, -0.5D);
        } else if (block instanceof StandingSignBlock) {
            float rotation = (float) (state.getValue(StandingSignBlock.ROTATION) * 360) / 16.0F;
            float yRot = (rotation + 180.0F) % 360.0F;
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-yRot));
            poseStack.translate(0.0D, 0.3125D, -0.4375D);
            poseStack.translate(-0.5D, -0.5D, -0.5D);
        } else {
            poseStack.popPose();
            return;
        }

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        dispatcher.getModelRenderer().renderModel(
                poseStack.last(),
                bufferSource.getBuffer(RenderType.cutout()),
                state,
                frameModel,
                1.0F, 1.0F, 1.0F,
                combinedLight,
                combinedOverlay
        );

        poseStack.popPose();
    }

    private static void addWidgetToScreen(net.minecraft.client.gui.screens.Screen screen, AbstractWidget widget) {
        ((com.kingodogo.buildscape.mixin.ScreenV118xInvoker) screen).buildscape$addRenderableWidget(widget);
    }

    public void addPauseScreenButton(PauseScreen screen, int width, int height, List<? extends GuiEventListener> children) {
        if (BuildscapeClientConfig.get().isConfigButtonHidden()) return;

        int targetX = width / 2 + 102;
        int targetY = height / 4 + 48;
        boolean isFullPauseMenu = false;
        AbstractWidget statsButton = null;

        for (GuiEventListener listener : children) {
            if (listener instanceof AbstractWidget widget) {
                Component msg = widget.getMessage();
                if (msg instanceof TranslatableComponent tc) {
                    String key = tc.getKey();
                    if (key.equals("menu.returnToGame")) {
                        isFullPauseMenu = true;
                    } else if (statsButton == null && (key.equals("menu.statistics") || key.equals("gui.stats"))) {
                        statsButton = widget;
                    }
                }
            }
        }

        if (!isFullPauseMenu) return;

        if (statsButton != null) {
            targetX = statsButton.x + statsButton.getWidth() + 4;
            targetY = statsButton.y;
        }

        Button configButton = new Button(
                targetX - 2,
                targetY,
                20,
                20,
                new TextComponent("B"),
                button -> Minecraft.getInstance().setScreen(com.kingodogo.buildscape.platform.Services.PLATFORM.createConfigScreen(screen))
        );
        addWidgetToScreen(screen, configButton);
    }

    public void addStonecutterCutAllButton(StonecutterScreen screen, int x, int y, StonecutterMenu menu) {
        addWidgetToScreen(screen, new Button(x, y, 18, 10, TextComponent.EMPTY, button -> {
            boolean current = ((StonecutterMenuExtension) menu).buildscape$isCutAll();
            ((StonecutterMenuExtension) menu).buildscape$setCutAll(!current);
            Minecraft mc = Minecraft.getInstance();
            if (mc.gameMode != null) {
                mc.gameMode.handleInventoryButtonClick(menu.containerId, -123);
            }
        }) {
            
            public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                boolean active = ((StonecutterMenuExtension) menu).buildscape$isCutAll();
                int borderColor = 0xFF000000;
                int bgColor = active ? 0xFF107C41 : 0xFF4A4A4A;

                fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, borderColor);
                fill(poseStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, bgColor);

                int thumbX = active ? this.x + this.width - 7 : this.x + 1;
                int thumbColor = 0xFFFFFFFF;
                fill(poseStack, thumbX, this.y + 1, thumbX + 6, this.y + this.height - 1, thumbColor);

                if (this.isHovered) {
                    this.renderToolTip(poseStack, mouseX, mouseY);
                }
            }

            
            public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
                boolean active = ((StonecutterMenuExtension) menu).buildscape$isCutAll();
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(new TranslatableComponent("tooltip.buildscape.stonecutter.title")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                tooltip.add(new TranslatableComponent(active
                        ? "tooltip.buildscape.cut_all.on"
                        : "tooltip.buildscape.cut_stack.off").withStyle(ChatFormatting.BOLD));
                tooltip.add(new TranslatableComponent(active
                        ? "tooltip.buildscape.cut_all.desc"
                        : "tooltip.buildscape.cut_stack.desc").withStyle(ChatFormatting.GRAY));
                screen.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);
            }
        });
    }

    public void renderClippedBeaconBeam(BeaconBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSourceObj, int combinedLight, int combinedOverlay) {
        int cutoff = BeaconScanContext.confirmedHeight(blockEntity.getLevel(), blockEntity.getBlockPos());
        if (cutoff >= BeaconBeamScanState.UNLIMITED) return;
        MultiBufferSource bufferSource = (MultiBufferSource) bufferSourceObj;

        long gameTime = blockEntity.getLevel().getGameTime();
        List<BeaconBlockEntity.BeaconBeamSection> sections = blockEntity.getBeamSections();
        int yOffset = 0;
        for (BeaconBlockEntity.BeaconBeamSection section : sections) {
            int remaining = cutoff - yOffset;
            if (remaining <= 0) break;
            int sectionHeight = section.getHeight();
            com.kingodogo.buildscape.mixin.BeaconRendererInvoker.invokeRenderBeaconBeam(
                    poseStack, bufferSource, partialTicks, gameTime, yOffset,
                    Math.min(sectionHeight, remaining), section.getColor());
            yOffset += sectionHeight;
        }
    }

    public void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, PoseStack poseStack) {
        if (slot.hasItem() || !slot.isActive() || !(menu instanceof GhostFilterMenu filters)) return;
        Item filter = filters.buildscape$getFilterItem(slot.index);
        if (filter == null) return;

        ItemStack placeholder = new ItemStack(filter);
        placeholder.getOrCreateTag().putBoolean("ghost", true);
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer renderer = minecraft.getItemRenderer();
        float previousBlitOffset = renderer.blitOffset;
        float[] previousShaderColor = RenderSystem.getShaderColor();
        float previousRed = previousShaderColor[0];
        float previousGreen = previousShaderColor[1];
        float previousBlue = previousShaderColor[2];
        float previousAlpha = previousShaderColor[3];

        try {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            renderer.blitOffset = 100.0F;
            renderer.renderAndDecorateItem(com.kingodogo.buildscape.adapter.v118x.client.ClientAccess.player(), placeholder, slot.x, slot.y,
                    slot.x + slot.y * 176);
        } finally {
            renderer.blitOffset = previousBlitOffset;
            RenderSystem.setShaderColor(previousRed, previousGreen, previousBlue, previousAlpha);
        }
    }

    public void renderAnvilZeroCostLabel(Object anvilScreen, Object poseStack) {
        if (anvilScreen instanceof net.minecraft.client.gui.screens.inventory.AnvilScreen screen && poseStack instanceof PoseStack ps) {
            AnvilMenu menu = screen.getMenu();
            if (menu.getCost() == 0 && menu.getSlot(2).hasItem()) {
                Font font = Minecraft.getInstance().font;
                int imageWidth = ((com.kingodogo.buildscape.mixin.ContainerScreenV118xAccessor) screen).buildscape$getImageWidth();
                Component component = new TranslatableComponent("container.repair.cost", 0);
                int k = imageWidth - 8 - font.width(component) - 2;
                GuiComponentBridge.fill(ps, k - 2, 67, imageWidth - 8, 79, 0x4F000000);
                font.drawShadow(ps, component, (float) k, 69.0F, 0x80FF20);
            }
        }
    }

    public VertexConsumer getFestiveFoilBufferDirect(Object bufferSource, Object renderType, boolean noEntity) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    public VertexConsumer getFestiveFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    public VertexConsumer getFestiveArmorFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    public VertexConsumer getFestiveCompassFoilBuffer(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    public VertexConsumer getFestiveCompassFoilBufferDirect(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    public Object wrapGhostBufferSource(Object bufferSource, ItemStack stack) {
        if (bufferSource instanceof MultiBufferSource source && !stack.isEmpty() && stack.hasTag() && stack.getTag().getBoolean("ghost")) {
            return new com.kingodogo.buildscape.adapter.v118x.RenderFactory.TransparentMultiBufferSource(source, 0.3F);
        }
        return bufferSource;
    }

    public VertexConsumer wrapPipeSpillVertexConsumer(VertexConsumer original, Object level, BlockPos pos, BlockState state, net.minecraft.world.level.material.FluidState fluid) {
        if (level instanceof net.minecraft.world.level.BlockAndTintGetter tintGetter) {
            return com.kingodogo.buildscape.adapter.v118x.RenderFactory.PipeSpillVertexConsumer.wrap(original, tintGetter, pos, state, fluid);
        }
        return original;
    }

    public boolean renderStatsEntry(Object statObj, PoseStack poseStack, int left, int top, int width) {
        if (statObj instanceof net.minecraft.stats.Stat<?> stat) {
            if (stat == ModStats.HEADER_MINECRAFT_STAT || stat == ModStats.HEADER_BUILDSCAPE_STAT || stat == ModStats.HEADER_OTHER_STAT) {
                Minecraft mc = Minecraft.getInstance();
                net.minecraft.network.chat.Component text;
                int color;
                if (stat == ModStats.HEADER_MINECRAFT_STAT) {
                    text = new net.minecraft.network.chat.TextComponent("§6§l═══ Minecraft Statistics ═══");
                    color = 0xFFFFAA00;
                } else if (stat == ModStats.HEADER_BUILDSCAPE_STAT) {
                    text = new net.minecraft.network.chat.TextComponent("§b§l═══ Buildscape Statistics ═══");
                    color = 0xFF55FFFF;
                } else {
                    text = new net.minecraft.network.chat.TextComponent("§e§l═══ Other Mod Statistics ═══");
                    color = 0xFFFFFF55;
                }
                int textWidth = mc.font.width(text);
                float centerX = left + width / 2.0f - textWidth / 2.0f;
                mc.font.draw(poseStack, text, centerX, (float) (top + 1), color);
                return true;
            }
        }
        return false;
    }

    public ItemStack cycleAdvancementIcon(Object node, Object displayObject) {
        var display = (net.minecraft.advancements.DisplayInfo) displayObject;
        ItemStack fallback = display.getIcon();
        if (!(node instanceof net.minecraft.advancements.Advancement advancement)) return fallback;
        var id = advancement.getId();
        if (!id.getNamespace().equals("buildscape")) return fallback;
        List<String> names = ADVANCEMENT_ICONS.get(id.getPath());
        if (names == null) return fallback;
        List<Item> items = new ArrayList<>();
        for (String name : names) {
            Item item = net.minecraft.core.Registry.ITEM.get(new net.minecraft.resources.ResourceLocation("buildscape", name));
            if (item != null && item != net.minecraft.world.item.Items.AIR) items.add(item);
        }
        if (items.isEmpty()) return fallback;
        return new ItemStack(items.get((int) ((System.currentTimeMillis() / 1200L) % items.size())));
    }

    private static final java.util.Map<String, List<String>> ADVANCEMENT_ICONS = java.util.Map.of(
            "one_more_block", List.of("steel_block", "scraped_steel", "rustic_scraped_steel", "stacked_steel", "steel_panels", "crossed_steel_panels", "steel_mesh_block", "pressed_steel", "cut_steel", "polished_steel", "factory_steel_panel", "steel_casing", "steel_trim", "steel_pillar", "bolted_steel_pillar", "steel_fan"),
            "okay_one_more", coloredNames("", "_tiles", true),
            "actually_one_last", List.of("ashpen_white_planks", "orange_ashpen_planks", "magenta_ashpen_planks", "light_blue_ashpen_planks", "yellow_ashpen_planks", "lime_ashpen_planks", "pink_ashpen_planks", "gray_ashpen_planks", "light_gray_ashpen_planks", "cyan_ashpen_planks", "purple_ashpen_planks", "blue_ashpen_planks", "brown_ashpen_planks", "green_ashpen_planks", "red_ashpen_planks", "black_ashpen_planks", "ashpen_log", "stripped_ashpen_log", "ashpen_wood", "stripped_ashpen_wood"),
            "one_last_one_i_promise", List.of("bit_copper_block", "bit_cut_copper", "bit_chiseled_copper", "bit_exposed_copper_block", "bit_exposed_cut_copper", "bit_exposed_chiseled_copper", "bit_weathered_copper_block", "bit_weathered_cut_copper", "bit_weathered_chiseled_copper", "bit_oxidized_copper_block", "bit_oxidized_cut_copper", "bit_oxidized_chiseled_copper"),
            "string_me_along", coloredNames("", "_spool", false),
            "the_entire_catalogue", List.of("white_wallpaper", "orange_wallpaper", "magenta_wallpaper", "light_blue_wallpaper", "yellow_wallpaper", "lime_wallpaper", "pink_wallpaper", "cyan_wallpaper", "purple_wallpaper", "red_wallpaper"),
            "rainbow_mood_light", List.of("russet_froglight", "tidal_froglight", "scarlet_froglight", "cerulean_froglight", "gleaming_froglight", "azure_froglight"),
            "a_very_buildscape_christmas", List.of("festive_stocking", "red_ornament", "multicolor_string_light", "glow_star", "snowy_spruce_leaves"));

    private static List<String> coloredNames(String prefix, String suffix, boolean allColors) {
        List<String> names = new ArrayList<>();
        String[] colors = allColors
                ? new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"}
                : new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "cyan", "purple", "blue", "red"};
        for (String color : colors) names.add(prefix + color + suffix);
        return List.copyOf(names);
    }



    public void setScreen(Object screen) {
        if (Minecraft.getInstance() != null) {
            Minecraft.getInstance().setScreen((net.minecraft.client.gui.screens.Screen) screen);
        }
    }

    public void sortGeneralStatsList(Object listObj, java.util.Comparator<?> originalComparatorObj) {
        if (listObj instanceof it.unimi.dsi.fastutil.objects.ObjectArrayList<?> list) {
            @SuppressWarnings("unchecked")
            java.util.Comparator<net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation>> originalComparator =
                    (java.util.Comparator<net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation>>) originalComparatorObj;
            @SuppressWarnings("unchecked")
            it.unimi.dsi.fastutil.objects.ObjectArrayList<net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation>> typedList =
                    (it.unimi.dsi.fastutil.objects.ObjectArrayList<net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation>>) list;
            boolean hasOtherMods = false;
            for (net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation> s : typedList) {
                String ns = s.getValue().getNamespace();
                if (!"minecraft".equals(ns) && !"buildscape".equals(ns)) {
                    hasOtherMods = true;
                    break;
                }
            }
            if (!hasOtherMods && ModStats.HEADER_OTHER_STAT != null) {
                typedList.remove(ModStats.HEADER_OTHER_STAT);
            }

            typedList.sort((a, b) -> {
                int pA = getStatsPriority(a);
                int pB = getStatsPriority(b);
                if (pA != pB) {
                    return Integer.compare(pA, pB);
                }
                return originalComparator.compare(a, b);
            });
        }
    }

    private static int getStatsPriority(net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation> stat) {
        if (stat == ModStats.HEADER_MINECRAFT_STAT) return 0;
        net.minecraft.resources.ResourceLocation id = stat.getValue();
        if ("minecraft".equals(id.getNamespace())) return 1;
        if (stat == ModStats.HEADER_BUILDSCAPE_STAT) return 2;
        if ("buildscape".equals(id.getNamespace())) return 3;
        if (stat == ModStats.HEADER_OTHER_STAT) return 4;
        return 5;
    }

    private static boolean buildscape$isRenderingCustomTooltip = false;

    public void renderCustomScreenTooltip(net.minecraft.client.gui.screens.Screen screen, PoseStack poseStack, List<?> components, int mouseX, int mouseY, ItemStack hoverStack, Font font, Object itemRendererObj, int screenWidth, int screenHeight) {
        if (buildscape$isRenderingCustomTooltip) {
            return;
        }
        if (!com.kingodogo.buildscape.event.TagTooltipHandler.isShulkerPreviewEnabled()) {
            return;
        }
        if (!net.minecraft.client.gui.screens.Screen.hasShiftDown() || hoverStack == null || hoverStack.isEmpty()) {
            return;
        }

        try {
            buildscape$isRenderingCustomTooltip = true;

            com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData shulkerData = com.kingodogo.buildscape.event.TagTooltipHandler.getShulkerTooltipData(hoverStack);
            com.kingodogo.buildscape.client.tooltip.BuildersPouchTooltipData pouchData = shulkerData == null ? com.kingodogo.buildscape.event.TagTooltipHandler.getBuildersPouchTooltipData(hoverStack) : null;

            if (shulkerData == null && pouchData == null) {
                return;
            }

            int textTooltipWidth = 0;
            int textTooltipHeight = (components != null && components.size() == 1) ? -2 : 0;
            if (components != null) {
                for (Object compObj : components) {
                    if (compObj instanceof net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent comp) {
                        int k = comp.getWidth(font);
                        if (k > textTooltipWidth) {
                            textTooltipWidth = k;
                        }
                        textTooltipHeight += comp.getHeight();
                    }
                }
            }

            int textX = mouseX + 12;
            int textY = mouseY - 12;

            if (textX + textTooltipWidth > screenWidth) {
                textX -= 28 + textTooltipWidth;
            }
            if (textY + textTooltipHeight + 6 > screenHeight) {
                textY = screenHeight - textTooltipHeight - 6;
            }
            if (textY < 4) {
                textY = 4;
            }

            int customWidth = 9 * 18 + 14;
            int customHeight = shulkerData != null ? (3 * 18 + 14) : (1 * 18 + 14);

            int customX = textX - 3;
            int customY = textY + textTooltipHeight + 6;

            if (customY + customHeight > screenHeight - 4) {
                customY = (textY - 3) - customHeight - 3;
            }
            if (customY < 4) {
                customY = 4;
            }

            if (customX + customWidth > screenWidth - 4) {
                customX = screenWidth - customWidth - 4;
            }
            if (customX < 4) {
                customX = 4;
            }

            ItemRenderer itemRenderer = itemRendererObj instanceof ItemRenderer r ? r : (net.minecraft.client.Minecraft.getInstance() != null ? net.minecraft.client.Minecraft.getInstance().getItemRenderer() : null);
            if (itemRenderer == null) {
                return;
            }
            poseStack.pushPose();
            if (shulkerData != null) {
                float[] tint = com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData.getTint(shulkerData.getColor());
                renderTooltipBox(font, customX, customY, poseStack, itemRenderer, 400, 3, tint, shulkerData.getFilterStacks(), shulkerData.getRealStacks());
            } else {
                float[] tint = com.kingodogo.buildscape.client.tooltip.BuildersPouchTooltipData.hexToRgb(com.kingodogo.buildscape.client.tooltip.BuildersPouchTooltipData.COLOR_GOLD);
                renderTooltipBox(font, customX, customY, poseStack, itemRenderer, 400, 1, tint, pouchData.getFilterStacks(), pouchData.getRealStacks());
            }
            poseStack.popPose();
        } catch (Throwable t) {
        } finally {
            buildscape$isRenderingCustomTooltip = false;
        }
    }

    private static void renderTooltipBox(Font font, int x, int y, PoseStack poseStack, ItemRenderer itemRenderer, int blitOffset,
                                         int rows, float[] tint, NonNullList<ItemStack> filterStacks, NonNullList<ItemStack> realStacks) {
        Minecraft mc = Minecraft.getInstance();
        int width = 9 * 18 + 14;
        int height = rows * 18 + 14;
        ResourceLocation texture = new ResourceLocation("buildscape", "textures/gui/shulker_box_tooltip.png");

        RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(tint[0], tint[1], tint[2], 1.0F);
        RenderSystem.setShaderTexture(0, texture);

        net.minecraft.client.gui.GuiComponent.blit(poseStack, x, y, 0, 0, 7, 7, 32, 32);
        net.minecraft.client.gui.GuiComponent.blit(poseStack, x + width - 7, y, 25, 0, 7, 7, 32, 32);
        net.minecraft.client.gui.GuiComponent.blit(poseStack, x, y + height - 7, 0, 25, 7, 7, 32, 32);
        net.minecraft.client.gui.GuiComponent.blit(poseStack, x + width - 7, y + height - 7, 25, 25, 7, 7, 32, 32);

        for (int col = 0; col < 9; col++) {
            net.minecraft.client.gui.GuiComponent.blit(poseStack, x + 7 + col * 18, y, 7, 0, 18, 7, 32, 32);
            net.minecraft.client.gui.GuiComponent.blit(poseStack, x + 7 + col * 18, y + height - 7, 7, 25, 18, 7, 32, 32);
        }

        for (int row = 0; row < rows; row++) {
            net.minecraft.client.gui.GuiComponent.blit(poseStack, x, y + 7 + row * 18, 0, 7, 7, 18, 32, 32);
            net.minecraft.client.gui.GuiComponent.blit(poseStack, x + width - 7, y + 7 + row * 18, 25, 7, 7, 18, 32, 32);
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                net.minecraft.client.gui.GuiComponent.blit(poseStack, x + 7 + col * 18, y + 7 + row * 18, 7, 7, 18, 18, 32, 32);
            }
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col;
                int slotX = x + 7 + col * 18;
                int slotY = y + 7 + row * 18;

                ItemStack filter = index < filterStacks.size() ? filterStacks.get(index) : ItemStack.EMPTY;
                ItemStack real = index < realStacks.size() ? realStacks.get(index) : ItemStack.EMPTY;

                int itemX = slotX + 1;
                int itemY = slotY + 1;

                try {
                    if (!real.isEmpty()) {
                        float prevBlit = itemRenderer.blitOffset;
                        itemRenderer.blitOffset = blitOffset + 100.0F;
                        itemRenderer.renderAndDecorateItem(com.kingodogo.buildscape.adapter.v118x.client.ClientAccess.player(), real, itemX, itemY, index);
                        itemRenderer.renderGuiItemDecorations(font, real, itemX, itemY);
                        itemRenderer.blitOffset = prevBlit;
                    } else if (!filter.isEmpty()) {
                        float prevBlit = itemRenderer.blitOffset;
                        itemRenderer.blitOffset = blitOffset + 100.0F;
                        itemRenderer.renderAndDecorateItem(com.kingodogo.buildscape.adapter.v118x.client.ClientAccess.player(), filter, itemX, itemY, index);
                        itemRenderer.blitOffset = prevBlit;
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private static final class GuiComponentBridge extends net.minecraft.client.gui.GuiComponent {
        public static void fill(PoseStack poseStack, int minX, int minY, int maxX, int maxY, int color) {
            net.minecraft.client.gui.GuiComponent.fill(poseStack, minX, minY, maxX, maxY, color);
        }
    }
    private static final java.util.regex.Pattern BUILDSCAPE_CUSTOM_LOADER =
            java.util.regex.Pattern.compile("\"loader\"\\s*:");

    private Map<ResourceLocation, net.minecraft.client.renderer.block.model.BlockModel> buildscape$parsedModels;
    private volatile boolean buildscape$hasCustomGeometry;

    public void onModelBakeryPreload(Object bakeryObj, Object resourceManagerObj) {
        if (!BuildscapeClientConfig.get().isParallelModelLoadingEnabled() ||
                com.kingodogo.buildscape.client.performance.LaunchFasterInterop.isParallelModelLoadingEnabled()) {
            return;
        }

        long startedAt = System.nanoTime();
        List<ResourceLocation> modelFiles = buildscape$listBundledModels();
        if (modelFiles.isEmpty()) return;

        net.minecraft.server.packs.resources.ResourceManager resourceManager = (net.minecraft.server.packs.resources.ResourceManager) resourceManagerObj;
        java.util.concurrent.ConcurrentMap<ResourceLocation, net.minecraft.client.renderer.block.model.BlockModel> parsedModels = new java.util.concurrent.ConcurrentHashMap<>();
        java.util.concurrent.atomic.AtomicInteger fallbackCount = new java.util.concurrent.atomic.AtomicInteger();

        com.kingodogo.buildscape.client.performance.BuildscapeStartupWork.forEachIndex(modelFiles.size(), index -> {
            ResourceLocation fileLocation = modelFiles.get(index);
            try (
                    net.minecraft.server.packs.resources.Resource resource = resourceManager.getResource(fileLocation);
                    java.io.InputStream stream = resource.getInputStream()
            ) {
                String json = new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                if (BUILDSCAPE_CUSTOM_LOADER.matcher(json).find()) {
                    fallbackCount.incrementAndGet();
                    return;
                }

                ResourceLocation modelLocation = buildscape$toModelLocation(fileLocation);
                net.minecraft.client.renderer.block.model.BlockModel model = net.minecraft.client.renderer.block.model.BlockModel.fromString(json);
                model.name = modelLocation.toString();
                parsedModels.put(modelLocation, model);
            } catch (Exception exception) {
                fallbackCount.incrementAndGet();
                com.kingodogo.buildscape.BuildscapeCommon.LOGGER.debug(
                        "Buildscape model preload deferred {} to the normal loader",
                        fileLocation,
                        exception
                );
            }
        });

        buildscape$parsedModels = parsedModels;
        long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000L;
        com.kingodogo.buildscape.BuildscapeCommon.LOGGER.info(
                "Buildscape startup parsed {} model files in parallel ({} ms, {} sequential fallbacks)",
                parsedModels.size(),
                elapsedMillis,
                fallbackCount.get()
        );
    }

    public Object onModelBakeryLoadModel(Object locationObj) {
        if (!(locationObj instanceof ResourceLocation location)) return null;
        Map<ResourceLocation, net.minecraft.client.renderer.block.model.BlockModel> parsed = buildscape$parsedModels;
        if (parsed == null || !"buildscape".equals(location.getNamespace())) {
            return null;
        }
        return parsed.get(location);
    }

    public void onModelBakeryDetectCustomGeometry(Object locationObj, Object modelObj) {
        if (locationObj instanceof ResourceLocation location && modelObj instanceof net.minecraft.client.renderer.block.model.BlockModel model) {
            if ("buildscape".equals(location.getNamespace())) {
                if (model instanceof com.kingodogo.buildscape.adapter.v118x.client.CustomGeometryAccess access
                        && access.buildscape$hasCustomGeometry()) {
                    buildscape$hasCustomGeometry = true;
                }
            }
        }
    }

    public void onModelBakeryRelease() {
        buildscape$parsedModels = null;
    }

    @SuppressWarnings("unchecked")
    public void onModelBakeryBakeParallel(Object bakeryObj) {
        if (!BuildscapeClientConfig.get().isParallelModelBakingEnabled() ||
                com.kingodogo.buildscape.client.performance.LaunchFasterInterop.isParallelModelBakingEnabled()) {
            return;
        }

        if (buildscape$hasCustomGeometry) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.info(
                    "Buildscape startup kept model baking sequential because custom geometry was detected"
            );
            return;
        }

        try {
            ModelBakery bakery = (ModelBakery) bakeryObj;
            var access = (com.kingodogo.buildscape.mixin.ModelBakeryV118xAccessor) bakery;
            Map<ResourceLocation, ?> topLevelModels = access.buildscape$getTopLevelModels();

            List<ResourceLocation> candidates = topLevelModels.keySet().stream()
                    .filter(location -> "buildscape".equals(location.getNamespace()))
                    .toList();
            if (candidates.isEmpty()) return;

            Map<?, ?> origCache = access.buildscape$getBakedCache();
            access.buildscape$setBakedCache(new java.util.concurrent.ConcurrentHashMap<>(origCache));

            long startedAt = System.nanoTime();
            java.util.Set<ResourceLocation> completed = java.util.concurrent.ConcurrentHashMap.newKeySet();
            java.util.concurrent.atomic.AtomicInteger fallbackCount = new java.util.concurrent.atomic.AtomicInteger();

            com.kingodogo.buildscape.client.performance.BuildscapeStartupWork.forEachIndex(candidates.size(), index -> {
                ResourceLocation location = candidates.get(index);
                try {
                    Object bakedModel = bakery.bake(location, net.minecraft.client.resources.model.BlockModelRotation.X0_Y0);
                    if (bakedModel != null) {
                        completed.add(location);
                    } else {
                        fallbackCount.incrementAndGet();
                    }
                } catch (Throwable exception) {
                    fallbackCount.incrementAndGet();
                    com.kingodogo.buildscape.BuildscapeCommon.LOGGER.debug(
                            "Buildscape model bake deferred {} to the normal loader",
                            location,
                            exception
                    );
                }
            });

            long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000L;
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.info(
                    "Buildscape startup baked {} top-level models in parallel ({} ms, {} sequential fallbacks)",
                    completed.size(),
                    elapsedMillis,
                    fallbackCount.get()
            );
        } catch (Throwable t) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Buildscape parallel model baking encountered error: {}", t.getMessage());
        }
    }

    private static ResourceLocation buildscape$toModelLocation(ResourceLocation fileLocation) {
        String path = fileLocation.getPath();
        String modelPath = path.substring("models/".length(), path.length() - ".json".length());
        return new ResourceLocation(fileLocation.getNamespace(), modelPath);
    }

    private static List<ResourceLocation> buildscape$listBundledModels() {
        var models = new java.util.TreeSet<ResourceLocation>(java.util.Comparator.comparing(ResourceLocation::toString));
        String prefix = "assets/buildscape/models/";
        try {
            java.net.URL location = com.kingodogo.buildscape.BuildscapeCommon.class
                    .getProtectionDomain().getCodeSource().getLocation();
            java.nio.file.Path source = java.nio.file.Path.of(location.toURI());
            if (java.nio.file.Files.isRegularFile(source)) {
                try (var jar = new java.util.jar.JarFile(source.toFile())) {
                    jar.stream().map(java.util.jar.JarEntry::getName)
                            .filter(name -> name.startsWith(prefix) && name.endsWith(".json"))
                            .map(name -> new ResourceLocation("buildscape", "models/" + name.substring(prefix.length())))
                            .forEach(models::add);
                }
            } else {
                // Development loaders keep compiled classes and resources in separate roots.
                var roots = com.kingodogo.buildscape.BuildscapeCommon.class.getClassLoader().getResources(prefix);
                while (roots.hasMoreElements()) {
                    var rootUrl = roots.nextElement();
                    if (!rootUrl.getProtocol().equals("file")) continue;
                    var root = java.nio.file.Path.of(rootUrl.toURI());
                    try (var paths = java.nio.file.Files.walk(root)) {
                        paths.filter(java.nio.file.Files::isRegularFile).map(root::relativize)
                                .map(path -> path.toString().replace(java.io.File.separatorChar, '/'))
                                .filter(path -> path.endsWith(".json"))
                                .map(path -> new ResourceLocation("buildscape", "models/" + path))
                                .forEach(models::add);
                    }
                }
            }
        } catch (Exception exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.debug("Cannot enumerate bundled models", exception);
        }
        return List.copyOf(models);
    }

    public void handleFireworkStarterTick(Object starterObj) {
        if (!(starterObj instanceof net.minecraft.client.particle.Particle particle)) return;
        try {
            var particleAccess = (com.kingodogo.buildscape.mixin.ParticleV118xAccessor) particle;
            if (particleAccess.buildscape$getAge() != 0) return;
            if (!(starterObj instanceof com.kingodogo.buildscape.mixin.FireworkStarterV118xAccessor starter)) return;
            net.minecraft.nbt.ListTag explosions = starter.buildscape$getExplosions();
            if (explosions == null || explosions.isEmpty()) return;

            net.minecraft.nbt.ListTag vanillaExplosions = new net.minecraft.nbt.ListTag();

            for (int i = 0; i < explosions.size(); ++i) {
                net.minecraft.nbt.CompoundTag tag = explosions.getCompound(i);
                byte type = tag.getByte("Type");

                if (com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.isCustomShape(type)) {
                    var shapeOpt = com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.getByNumericId(type);
                    if (shapeOpt.isPresent()) {
                        var shape = shapeOpt.get();
                        boolean trail = tag.getBoolean("Trail");
                        boolean flicker = tag.getBoolean("Flicker");
                        int[] colors = tag.getIntArray("Colors");
                        int[] fadeColors = tag.getIntArray("FadeColors");

                        double pxVal = particleAccess.buildscape$getX();
                        double pyVal = particleAccess.buildscape$getY();
                        double pzVal = particleAccess.buildscape$getZ();
                        double pxdVal = particleAccess.buildscape$getXd();
                        double pzdVal = particleAccess.buildscape$getZd();

                        float yaw = 0.0F;
                        if (tag.contains("ShotYaw")) {
                            yaw = tag.getFloat("ShotYaw");
                        } else if (Math.hypot(pxdVal, pzdVal) > 0.05) {
                            yaw = (float) (net.minecraft.util.Mth.atan2(pxdVal, pzdVal) * (180.0F / (float) Math.PI));
                        } else if (com.kingodogo.buildscape.adapter.v118x.client.ClientAccess.player() != null) {
                            yaw = com.kingodogo.buildscape.adapter.v118x.client.ClientAccess.player().getYRot();
                        }

                        particle.setBoundingBox(particle.getBoundingBox().inflate(120.0D, 120.0D, 120.0D));

                        com.kingodogo.buildscape.firework.CustomFireworkRenderer.renderExplosion(
                                shape,
                                pxVal, pyVal, pzVal,
                                colors, fadeColors,
                                trail, flicker,
                                yaw,
                                (px, py, pz, vx, vy, vz, c, fc, tr, fl) -> {
                                    try {
                                        starter.buildscape$createParticle(px, py, pz, vx, vy, vz, c, fc, tr, fl);
                                    } catch (Throwable ignored) {}
                                }
                        );
                    }
                } else {
                    vanillaExplosions.add(tag);
                }
            }

            starter.buildscape$setExplosions(vanillaExplosions);
        } catch (Throwable t) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.debug("Custom firework starter tick failed: {}", t.getMessage());
        }
    }
}

