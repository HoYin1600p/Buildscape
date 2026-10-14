package com.kingodogo.buildscape.adapter.v121x.client;

import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.StonecutterMenuExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

/** Client-only implementations of mixin hooks. */
public final class ClientMixins {
    private ClientMixins() {}
    public static void renderSignFrame(SignBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSourceObj, int combinedLight, int combinedOverlay) {
        if (blockEntity == null || blockEntity.getLevel() == null) return;
        MultiBufferSource bufferSource = (MultiBufferSource) bufferSourceObj;

        SignFrameType frameType = SignFrameAttachment.getFrame(blockEntity);
        if (frameType == SignFrameType.NONE || frameType.getModelLocation() == null) return;

        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        com.kingodogo.buildscape.util.CommonId modelLoc = frameType.getModelLocation();
        net.minecraft.resources.ResourceLocation rl = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(modelLoc.getNamespace(), modelLoc.getPath());
        net.minecraft.client.resources.model.ModelResourceLocation mrl = new net.minecraft.client.resources.model.ModelResourceLocation(rl, "standalone");
        BakedModel frameModel = modelManager.getModel(mrl);
        if (frameModel == null || frameModel == modelManager.getMissingModel()) return;

        BlockState state = blockEntity.getBlockState();
        Block block = state.getBlock();

        poseStack.pushPose();

        if (block instanceof WallSignBlock) {
            Direction facing = state.getValue(WallSignBlock.FACING);
            float yRot = (facing.toYRot() + 180.0F) % 360.0F;
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
            poseStack.translate(-0.5D, -0.5D, -0.5D);
        } else if (block instanceof StandingSignBlock) {
            float rotation = (float) (state.getValue(StandingSignBlock.ROTATION) * 360) / 16.0F;
            float yRot = (rotation + 180.0F) % 360.0F;
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
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

    private static java.util.function.BiConsumer<Screen, AbstractWidget> widgetRegistrar;

    public static void installWidgetRegistrar(java.util.function.BiConsumer<Screen, AbstractWidget> registrar) {
        widgetRegistrar = java.util.Objects.requireNonNull(registrar);
    }

    private static void addWidgetToScreen(Screen screen, AbstractWidget widget) {
        if (widgetRegistrar == null) throw new IllegalStateException("Client widget access has not been installed");
        widgetRegistrar.accept(screen, widget);
    }

    public static void addPauseScreenButton(PauseScreen screen, int width, int height, List<? extends GuiEventListener> children) {
        if (BuildscapeClientConfig.get().isConfigButtonHidden()) return;

        int targetX = width / 2 + 102;
        int targetY = height / 4 + 48;
        boolean isFullPauseMenu = false;
        AbstractWidget statsButton = null;

        for (GuiEventListener listener : children) {
            if (listener instanceof AbstractWidget widget) {
                Component msg = widget.getMessage();
                String stringVal = msg.getString();
                if ("menu.returnToGame".equals(stringVal) || msg.toString().contains("menu.returnToGame")) {
                    isFullPauseMenu = true;
                } else if (statsButton == null && (stringVal.contains("statistics") || msg.toString().contains("menu.statistics") || msg.toString().contains("gui.stats"))) {
                    statsButton = widget;
                }
            }
        }

        if (!isFullPauseMenu) return;

        if (statsButton != null) {
            targetX = statsButton.getX() + statsButton.getWidth() + 4;
            targetY = statsButton.getY();
        }

        Button configButton = Button.builder(
                Component.literal("B"),
                button -> Minecraft.getInstance().setScreen(Services.PLATFORM.createConfigScreen(screen))
        ).bounds(targetX - 2, targetY, 20, 20).build();
        addWidgetToScreen(screen, configButton);
    }

    public static void addStonecutterCutAllButton(StonecutterScreen screen, int x, int y, StonecutterMenu menu) {
        Button button = new Button(x, y, 18, 10, Component.empty(), b -> {
            boolean current = ((StonecutterMenuExtension) menu).buildscape$isCutAll();
            ((StonecutterMenuExtension) menu).buildscape$setCutAll(!current);
            Minecraft mc = Minecraft.getInstance();
            if (mc.gameMode != null) {
                mc.gameMode.handleInventoryButtonClick(menu.containerId, -123);
            }
        }, supplier -> supplier.get()) {
            @Override
            public void renderWidget(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                boolean active = ((StonecutterMenuExtension) menu).buildscape$isCutAll();
                int left = this.getX(), top = this.getY(), w = this.getWidth(), h = this.getHeight();
                int borderColor = 0xFF000000;
                int bgColor = active ? 0xFF107C41 : 0xFF4A4A4A;
                graphics.fill(left, top, left + w, top + h, borderColor);
                graphics.fill(left + 1, top + 1, left + w - 1, top + h - 1, bgColor);
                int thumbX = active ? left + w - 7 : left + 1;
                graphics.fill(thumbX, top + 1, thumbX + 6, top + h - 1, 0xFFFFFFFF);
                if (this.isHovered()) {
                    java.util.List<Component> tooltip = new java.util.ArrayList<>();
                    tooltip.add(Component.translatable("tooltip.buildscape.stonecutter.title")
                            .withStyle(net.minecraft.ChatFormatting.GOLD, net.minecraft.ChatFormatting.BOLD));
                    tooltip.add(Component.translatable(active ? "tooltip.buildscape.cut_all.on" : "tooltip.buildscape.cut_stack.off")
                            .withStyle(net.minecraft.ChatFormatting.BOLD));
                    tooltip.add(Component.translatable(active ? "tooltip.buildscape.cut_all.desc" : "tooltip.buildscape.cut_stack.desc")
                            .withStyle(net.minecraft.ChatFormatting.GRAY));
                    graphics.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
                }
            }
        };
        addWidgetToScreen(screen, button);
    }

    public static void renderAnvilZeroCostLabel(Object anvilScreen, Object graphicsObject) {
        if (!(anvilScreen instanceof net.minecraft.client.gui.screens.inventory.AnvilScreen screen)
                || !(graphicsObject instanceof net.minecraft.client.gui.GuiGraphics graphics)) return;
        if (screen.getMenu().getCost() != 0 || !screen.getMenu().getSlot(2).hasItem()) return;
        net.minecraft.client.gui.Font font = Minecraft.getInstance().font;
        Component label = Component.translatable("container.repair.cost", 0);
        int imageWidth = 176;
        int labelX = imageWidth - 8 - font.width(label) - 2;
        graphics.fill(labelX - 2, 67, imageWidth - 8, 79, 0x4F000000);
        graphics.drawString(font, label, labelX, 69, 0xFF80FF20);
    }

    public static void renderClippedBeaconBeam(net.minecraft.world.level.block.entity.BeaconBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
        int cutoff = com.kingodogo.buildscape.util.BeaconScanContext.confirmedHeight(blockEntity.getLevel(), blockEntity.getBlockPos());
        long gameTime = blockEntity.getLevel().getGameTime();
        int yOffset = 0;
        for (net.minecraft.world.level.block.entity.BeaconBlockEntity.BeaconBeamSection section : blockEntity.getBeamSections()) {
            int remaining = cutoff - yOffset;
            if (remaining <= 0) break;
            int sectionHeight = section.getHeight();
            net.minecraft.client.renderer.blockentity.BeaconRenderer.renderBeaconBeam(poseStack, (MultiBufferSource) bufferSource,
                    net.minecraft.client.renderer.blockentity.BeaconRenderer.BEAM_LOCATION, partialTicks, 1.0F, gameTime,
                    yOffset, Math.min(sectionHeight, remaining), section.getColor(), 0.2F, 0.25F);
            yOffset += sectionHeight;
        }
    }

    private static final java.util.Map<String, List<String>> ADVANCEMENT_ICONS = java.util.Map.of(
            "one_more_block", List.of("steel_block", "scraped_steel", "rustic_scraped_steel", "stacked_steel", "steel_panels", "crossed_steel_panels", "steel_mesh_block", "pressed_steel", "cut_steel", "polished_steel", "factory_steel_panel", "steel_casing", "steel_trim", "steel_pillar", "bolted_steel_pillar", "steel_fan"),
            "okay_one_more", advancementColors("", "_tiles", true),
            "actually_one_last", List.of("ashpen_white_planks", "orange_ashpen_planks", "magenta_ashpen_planks", "light_blue_ashpen_planks", "yellow_ashpen_planks", "lime_ashpen_planks", "pink_ashpen_planks", "gray_ashpen_planks", "light_gray_ashpen_planks", "cyan_ashpen_planks", "purple_ashpen_planks", "blue_ashpen_planks", "brown_ashpen_planks", "green_ashpen_planks", "red_ashpen_planks", "black_ashpen_planks", "ashpen_log", "stripped_ashpen_log", "ashpen_wood", "stripped_ashpen_wood"),
            "one_last_one_i_promise", List.of("bit_copper_block", "bit_cut_copper", "bit_chiseled_copper", "bit_exposed_copper_block", "bit_exposed_cut_copper", "bit_exposed_chiseled_copper", "bit_weathered_copper_block", "bit_weathered_cut_copper", "bit_weathered_chiseled_copper", "bit_oxidized_copper_block", "bit_oxidized_cut_copper", "bit_oxidized_chiseled_copper"),
            "string_me_along", advancementColors("", "_spool", false),
            "the_entire_catalogue", List.of("white_wallpaper", "orange_wallpaper", "magenta_wallpaper", "light_blue_wallpaper", "yellow_wallpaper", "lime_wallpaper", "pink_wallpaper", "cyan_wallpaper", "purple_wallpaper", "red_wallpaper"),
            "rainbow_mood_light", List.of("russet_froglight", "tidal_froglight", "scarlet_froglight", "cerulean_froglight", "gleaming_froglight", "azure_froglight"),
            "a_very_buildscape_christmas", List.of("festive_stocking", "red_ornament", "multicolor_string_light", "glow_star", "snowy_spruce_leaves"));

    private static List<String> advancementColors(String prefix, String suffix, boolean allColors) {
        List<String> names = new java.util.ArrayList<>();
        String[] colors = allColors
                ? new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"}
                : new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "cyan", "purple", "blue", "red"};
        for (String color : colors) names.add(prefix + color + suffix);
        return List.copyOf(names);
    }

    public static net.minecraft.world.item.ItemStack cycleAdvancementIcon(Object node, Object displayObject) {
        var display = (net.minecraft.advancements.DisplayInfo) displayObject;
        net.minecraft.world.item.ItemStack fallback = display.getIcon();
        if (!(node instanceof net.minecraft.advancements.AdvancementNode advancement)) return fallback;
        var id = advancement.holder().id();
        if (!id.getNamespace().equals("buildscape")) return fallback;
        List<String> names = ADVANCEMENT_ICONS.get(id.getPath());
        if (names == null) return fallback;
        List<net.minecraft.world.item.Item> items = new java.util.ArrayList<>();
        for (String name : names) {
            net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("buildscape", name));
            if (item != net.minecraft.world.item.Items.AIR) items.add(item);
        }
        if (items.isEmpty()) return fallback;
        return new net.minecraft.world.item.ItemStack(items.get((int) ((System.currentTimeMillis() / 1200L) % items.size())));
    }

    @SuppressWarnings("unchecked")
    public static void sortGeneralStatsList(Object object, java.util.Comparator<?> comparator) {
        List<net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation>> stats =
                (List<net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation>>) object;
        java.util.Comparator<net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation>> original =
                (java.util.Comparator<net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation>>) comparator;
        boolean hasOtherMods = false;
        for (var stat : stats) {
            String namespace = stat.getValue().getNamespace();
            if (!"minecraft".equals(namespace) && !"buildscape".equals(namespace)) {
                hasOtherMods = true;
                break;
            }
        }
        if (!hasOtherMods && com.kingodogo.buildscape.stat.ModStats.HEADER_OTHER_STAT != null) {
            stats.remove(com.kingodogo.buildscape.stat.ModStats.HEADER_OTHER_STAT);
        }
        stats.sort((a, b) -> {
            int priorityA = statPriority(a);
            int priorityB = statPriority(b);
            if (priorityA != priorityB) return Integer.compare(priorityA, priorityB);
            return original.compare(a, b);
        });
    }

    private static int statPriority(net.minecraft.stats.Stat<net.minecraft.resources.ResourceLocation> stat) {
        if (stat == com.kingodogo.buildscape.stat.ModStats.HEADER_MINECRAFT_STAT) return 0;
        net.minecraft.resources.ResourceLocation id = stat.getValue();
        if ("minecraft".equals(id.getNamespace())) return 1;
        if (stat == com.kingodogo.buildscape.stat.ModStats.HEADER_BUILDSCAPE_STAT) return 2;
        if ("buildscape".equals(id.getNamespace())) return 3;
        if (stat == com.kingodogo.buildscape.stat.ModStats.HEADER_OTHER_STAT) return 4;
        return 5;
    }

    public static boolean renderStatsEntry(Object stat, Object graphicsObject, int left, int top, int width) {
        if (!(graphicsObject instanceof net.minecraft.client.gui.GuiGraphics graphics) || stat == null) return false;
        Component text;
        if (stat == com.kingodogo.buildscape.stat.ModStats.HEADER_MINECRAFT_STAT) {
            text = Component.literal("§6§l═══ Minecraft Statistics ═══");
        } else if (stat == com.kingodogo.buildscape.stat.ModStats.HEADER_BUILDSCAPE_STAT) {
            text = Component.literal("§b§l═══ Buildscape Statistics ═══");
        } else if (stat == com.kingodogo.buildscape.stat.ModStats.HEADER_OTHER_STAT) {
            text = Component.literal("§e§l═══ Other Mod Statistics ═══");
        } else return false;
        net.minecraft.client.gui.Font font = Minecraft.getInstance().font;
        int color = stat == com.kingodogo.buildscape.stat.ModStats.HEADER_MINECRAFT_STAT ? 0xFFFFAA00
                : stat == com.kingodogo.buildscape.stat.ModStats.HEADER_BUILDSCAPE_STAT ? 0xFF55FFFF : 0xFFFFFF55;
        float centerX = left + width / 2.0F - font.width(text) / 2.0F;
        graphics.drawString(font, text, (int) centerX, top + 1, color, false);
        return true;
    }

    private static boolean shiftDown() {
        return net.minecraft.client.gui.screens.Screen.hasShiftDown();
    }

    private static net.minecraft.world.item.ItemStack tooltipHoverStack = net.minecraft.world.item.ItemStack.EMPTY;

    private static boolean previewEnabled(net.minecraft.world.item.ItemStack stack) {
        return com.kingodogo.buildscape.event.TagTooltipHandler.isShulkerPreviewEnabled() && previewData(stack) != null;
    }

    /** Runs when the tooltip text of an item is built, which is right before that tooltip is drawn. */
    public static List<Component> prepareCustomTooltipText(net.minecraft.world.item.ItemStack stack, List<Component> original) {
        tooltipHoverStack = stack == null ? net.minecraft.world.item.ItemStack.EMPTY : stack;
        List<Component> text = new java.util.ArrayList<>(original);
        com.kingodogo.buildscape.event.TagTooltipHandler.handleItemTooltip(stack, text);
        if (previewEnabled(stack) && !shiftDown()) {
            Component hint = Component.translatable("tooltip.buildscape.hold_shift_contents");
            if (text.stream().noneMatch(line -> line.getString().equals(hint.getString()))) text.add(hint);
        }
        return text;
    }

    private record PreviewData(net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> filters,
            net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> items, float[] tint) {}

    private static PreviewData previewData(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem
                && blockItem.getBlock() instanceof net.minecraft.world.level.block.ShulkerBoxBlock shulker) {
            var items = net.minecraft.core.NonNullList.withSize(27, net.minecraft.world.item.ItemStack.EMPTY);
            var filters = net.minecraft.core.NonNullList.withSize(27, net.minecraft.world.item.ItemStack.EMPTY);
            var container = stack.get(net.minecraft.core.component.DataComponents.CONTAINER);
            if (container != null) container.copyInto(items);
            var data = stack.get(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA);
            if (data != null) {
                var ghosts = data.copyTag().getList("GhostFilters", net.minecraft.nbt.Tag.TAG_STRING);
                for (int i = 0; i < 27 && i < ghosts.size(); i++) {
                    var id = net.minecraft.resources.ResourceLocation.tryParse(ghosts.getString(i));
                    if (id == null) continue;
                    net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(id);
                    if (item != net.minecraft.world.item.Items.AIR) filters.set(i, new net.minecraft.world.item.ItemStack(item));
                }
            }
            if (items.stream().anyMatch(item -> !item.isEmpty()) || filters.stream().anyMatch(item -> !item.isEmpty()))
                return new PreviewData(filters, items, com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData.getTint(shulker.getColor()));
            return null;
        }
        var pouch = com.kingodogo.buildscape.event.TagTooltipHandler.getBuildersPouchTooltipData(stack);
        if (pouch == null) return null;
        return new PreviewData(pouch.getFilterStacks(), pouch.getRealStacks(),
                com.kingodogo.buildscape.client.tooltip.BuildersPouchTooltipData.hexToRgb(
                        com.kingodogo.buildscape.client.tooltip.BuildersPouchTooltipData.COLOR_GOLD));
    }

    /** Called after the text tooltip is drawn; draws the preview outside it like the reference. */
    @SuppressWarnings("unchecked")
    public static void renderCustomScreenTooltip(Object context, List<?> components, int mouseX, int mouseY,
            net.minecraft.client.gui.Font font) {
        net.minecraft.world.item.ItemStack stack = tooltipHoverStack;
        tooltipHoverStack = net.minecraft.world.item.ItemStack.EMPTY;
        if (!(context instanceof net.minecraft.client.gui.GuiGraphics graphics) || !shiftDown()
                || stack.isEmpty() || !com.kingodogo.buildscape.event.TagTooltipHandler.isShulkerPreviewEnabled()) return;
        try {
            PreviewData preview = previewData(stack);
            if (preview == null) return;
            int textWidth = 0;
            int textHeight = components.size() == 1 ? -2 : 0;
            for (Object component : components) {
                var tooltip = (net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent) component;
                textWidth = Math.max(textWidth, tooltip.getWidth(font));
                textHeight += tooltip.getHeight();
            }
            var origin = net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner.INSTANCE
                    .positionTooltip(graphics.guiWidth(), graphics.guiHeight(), mouseX, mouseY, textWidth, textHeight);
            int rows = (preview.items().size() + 8) / 9;
            int width = 9 * 18 + 14, height = rows * 18 + 14;
            int x = origin.x() - 3;
            int y = origin.y() + textHeight + 6;
            if (y + height > graphics.guiHeight() - 4) y = (origin.y() - 3) - height - 3;
            if (y < 4) y = 4;
            if (x + width > graphics.guiWidth() - 4) x = graphics.guiWidth() - width - 4;
            if (x < 4) x = 4;
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 400.0F);
            drawPreview(graphics, font, preview, x, y, width, height, rows);
            graphics.pose().popPose();
        } catch (RuntimeException exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.debug("BuildScape: Suppressed error during custom tooltip rendering", exception);
        }
    }

    private static void drawPreview(net.minecraft.client.gui.GuiGraphics graphics, net.minecraft.client.gui.Font font,
            PreviewData preview, int x, int y, int width, int height, int rows) {
        var texture = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("buildscape", "textures/gui/shulker_box_tooltip.png");
        graphics.setColor(preview.tint()[0], preview.tint()[1], preview.tint()[2], 1.0F);
        try {
            graphics.blit(texture, x, y, 0, 0, 7, 7, 32, 32);
            graphics.blit(texture, x + width - 7, y, 25, 0, 7, 7, 32, 32);
            graphics.blit(texture, x, y + height - 7, 0, 25, 7, 7, 32, 32);
            graphics.blit(texture, x + width - 7, y + height - 7, 25, 25, 7, 7, 32, 32);
            for (int col = 0; col < 9; col++) {
                graphics.blit(texture, x + 7 + col * 18, y, 7, 0, 18, 7, 32, 32);
                graphics.blit(texture, x + 7 + col * 18, y + height - 7, 7, 25, 18, 7, 32, 32);
            }
            for (int row = 0; row < rows; row++) {
                graphics.blit(texture, x, y + 7 + row * 18, 0, 7, 7, 18, 32, 32);
                graphics.blit(texture, x + width - 7, y + 7 + row * 18, 25, 7, 7, 18, 32, 32);
                for (int col = 0; col < 9; col++) graphics.blit(texture, x + 7 + col * 18, y + 7 + row * 18, 7, 7, 18, 18, 32, 32);
            }
        } finally {
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        for (int slot = 0; slot < preview.items().size(); slot++) {
            net.minecraft.world.item.ItemStack real = preview.items().get(slot);
            net.minecraft.world.item.ItemStack filter = slot < preview.filters().size() ? preview.filters().get(slot) : net.minecraft.world.item.ItemStack.EMPTY;
            net.minecraft.world.item.ItemStack shown = real.isEmpty() ? filter : real;
            if (shown.isEmpty()) continue;
            int itemX = x + 8 + slot % 9 * 18, itemY = y + 8 + slot / 9 * 18;
            graphics.renderItem(shown, itemX, itemY);
            if (!real.isEmpty()) graphics.renderItemDecorations(font, real, itemX, itemY);
        }
    }

    public static VertexConsumer getFestiveFoilBufferDirect(Object bufferSource, Object renderType, boolean noEntity) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    public static VertexConsumer getFestiveFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    public static VertexConsumer getFestiveArmorFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    public static VertexConsumer getFestiveCompassFoilBuffer(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    public static VertexConsumer getFestiveCompassFoilBufferDirect(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return ((MultiBufferSource) bufferSource).getBuffer((RenderType) renderType);
    }

    public static void setScreen(Object screen) {
        if (Minecraft.getInstance() != null) {
            Minecraft.getInstance().setScreen((net.minecraft.client.gui.screens.Screen) screen);
        }
    }

}
