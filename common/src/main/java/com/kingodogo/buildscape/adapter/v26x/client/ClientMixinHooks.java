package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import com.kingodogo.buildscape.mixinsupport.IMixinFactory;
import com.kingodogo.buildscape.mixin.StonecutterMenuAccessor;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.StonecutterMenuExtension;
import com.kingodogo.buildscape.world.ModGameRules;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gamerules.GameRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.kingodogo.buildscape.adapter.v26x.RenderCapture;

/** Client implementation of the mixin facade. */
public final class ClientMixinHooks {
    private ClientMixinHooks() {}

    public static void renderSignFrame(SignBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
        ClientSignFrames.render(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }

    private static void addWidgetToScreen(Screen screen, AbstractWidget widget) {
        try {
            for (java.lang.reflect.Method m : Screen.class.getDeclaredMethods()) {
                if (m.getParameterCount() == 1 && (m.getName().equals("addRenderableWidget") || m.getName().equals("m_142416_"))) {
                    m.setAccessible(true);
                    m.invoke(screen, widget);
                    return;
                }
            }
            for (java.lang.reflect.Field f : Screen.class.getDeclaredFields()) {
                if (java.util.List.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    java.util.List list = (java.util.List) f.get(screen);
                    if (list != null) list.add(widget);
                }
            }
        } catch (Throwable exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed to add Buildscape screen widget", exception);
        }
    }

    public static void addPauseScreenButton(PauseScreen screen, int width, int height, List<? extends GuiEventListener> children) {
        if (BuildscapeClientConfig.get().isConfigButtonHidden()) return;

        int targetX = width / 2 + 104;
        int targetY = height / 4 + 48;

        for (GuiEventListener listener : children) {
            if (listener instanceof AbstractWidget widget) {
                String text = widget.getMessage().getString();
                if (text.contains("Stats") || text.contains("Statistics")) {
                    targetX = widget.getX() + widget.getWidth() + 4;
                    targetY = widget.getY();
                    break;
                }
            }
        }

        Button button = Button.builder(Component.literal("BS"), b -> {
            Minecraft.getInstance().setScreenAndShow(Services.PLATFORM.createConfigScreen(screen));
        }).bounds(targetX, targetY, 20, 20).build();
        addWidgetToScreen(screen, button);
    }

    public static void addStonecutterCutAllButton(StonecutterScreen screen, int x, int y, StonecutterMenu menu) {
        Button button = Button.builder(Component.literal("All"), b -> {
            boolean active = !((StonecutterMenuExtension) menu).buildscape$isCutAll();
            ((StonecutterMenuExtension) menu).buildscape$setCutAll(active);
            Minecraft mc = Minecraft.getInstance();
            if (mc.gameMode != null) {
                mc.gameMode.handleInventoryButtonClick(menu.containerId, -123);
            }
        }).bounds(x, y, 18, 10).build();
        addWidgetToScreen(screen, button);
    }

    public static void renderClippedBeaconBeam(BeaconBlockEntity blockEntity, float partialTicks, PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
        if (blockEntity.getLevel() == null) return;
        int height = com.kingodogo.buildscape.util.BeaconScanContext.confirmedHeight(blockEntity.getLevel(), blockEntity.getBlockPos());
        if (height >= com.kingodogo.buildscape.util.BeaconBeamScanState.UNLIMITED) return;
        var state = new net.minecraft.client.renderer.blockentity.state.BeaconRenderState();
        net.minecraft.client.renderer.blockentity.BeaconRenderer.extract(blockEntity, state, partialTicks, net.minecraft.world.phys.Vec3.ZERO);
        if (bufferSource instanceof RenderCapture capture) {
            capture.record(poseStack, (pose, collector, camera) -> submitClippedBeam(state, pose, collector, height));
        } else if (bufferSource instanceof net.minecraft.client.renderer.SubmitNodeCollector collector) {
            submitClippedBeam(state, poseStack, collector, height);
        }
    }

    public static void submitClippedBeam(net.minecraft.client.renderer.blockentity.state.BeaconRenderState state,
            PoseStack pose, net.minecraft.client.renderer.SubmitNodeCollector collector, int height) {
        int offset = 0;
        for (int i = 0; i < state.sections.size() && offset < height; i++) {
            var section = state.sections.get(i);
            int length = Math.min(height - offset, i == state.sections.size() - 1 ? height - offset : section.height());
            if (length > 0) net.minecraft.client.renderer.blockentity.BeaconRenderer.submitBeaconBeam(pose, collector,
                    net.minecraft.client.renderer.blockentity.BeaconRenderer.BEAM_LOCATION, state.beamRadiusScale,
                    state.animationTime, offset, length, section.color(),
                    net.minecraft.client.renderer.blockentity.BeaconRenderer.SOLID_BEAM_RADIUS,
                    net.minecraft.client.renderer.blockentity.BeaconRenderer.BEAM_GLOW_RADIUS);
            offset += length;
        }
    }

    public static void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, PoseStack poseStack) {
        renderFilterPlaceholder(menu, slot, (Object) ClientGuiHooks.current());
    }

    public static void renderAnvilZeroCostLabel(Object anvilScreen, Object poseStack) {
        if (!(anvilScreen instanceof net.minecraft.client.gui.screens.inventory.AnvilScreen screen)
                || !(poseStack instanceof net.minecraft.client.gui.GuiGraphicsExtractor graphics)) return;
        if (screen.getMenu().getCost() != 0 || !screen.getMenu().getSlot(2).hasItem()) return;
        var font = Minecraft.getInstance().font;
        Component label = Component.translatable("container.repair.cost", 0);
        int x = 166 - font.width(label);
        graphics.fill(x - 2, 67, 168, 79, 0x4F000000);
        graphics.text(font, label, x, 69, 0xFF80FF20);
    }

    public static void renderFilterPlaceholder(AbstractContainerMenu menu, Slot slot, Object context) {
        if (!(context instanceof net.minecraft.client.gui.GuiGraphicsExtractor graphics)
                || slot.hasItem() || !slot.isActive()
                || !(menu instanceof com.kingodogo.buildscape.util.GhostFilterMenu filters)) return;
        Item filter = filters.buildscape$getFilterItem(slot.index);
        if (filter == null) return;
        graphics.fakeItem(new ItemStack(filter), slot.x, slot.y);
        graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x80C6C6C6);
    }

    private static VertexConsumer foilBuffer(Object buffer, Object type,
            net.minecraft.client.renderer.rendertype.RenderType glint) {
        var renderType = (net.minecraft.client.renderer.rendertype.RenderType) type;
        var foil = com.kingodogo.buildscape.mixinsupport.FestiveSubmission.festiveGlint(glint);
        if (buffer instanceof RenderCapture capture)
            return new DualVertexConsumer(capture.geometry(renderType), capture.geometry(foil));
        if (buffer instanceof VertexConsumer consumer) return consumer;
        if (buffer instanceof net.minecraft.client.renderer.SubmitNodeCollector collector) {
            RenderCapture capture = new RenderCapture();
            VertexConsumer consumer = new DualVertexConsumer(capture.geometry(renderType), capture.geometry(foil));
            // Custom geometry consumes the captured vertex list during the later draw phase.
            capture.submit(new PoseStack(), collector, null);
            return consumer;
        }
        throw new IllegalArgumentException("Expected a captured or submitted Buildscape render buffer");
    }

    public static VertexConsumer getFestiveFoilBufferDirect(Object bufferSource, Object renderType, boolean noEntity) {
        return foilBuffer(bufferSource, renderType, noEntity
                ? net.minecraft.client.renderer.rendertype.RenderTypes.glint()
                : net.minecraft.client.renderer.rendertype.RenderTypes.entityGlint());
    }

    public static VertexConsumer getFestiveFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return foilBuffer(bufferSource, renderType, isItem
                ? net.minecraft.client.renderer.rendertype.RenderTypes.glint()
                : net.minecraft.client.renderer.rendertype.RenderTypes.entityGlint());
    }

    public static VertexConsumer getFestiveArmorFoilBuffer(Object bufferSource, Object renderType, boolean isItem) {
        return foilBuffer(bufferSource, renderType, net.minecraft.client.renderer.rendertype.RenderTypes.armorEntityGlint());
    }

    public static VertexConsumer getFestiveCompassFoilBuffer(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return foilBuffer(bufferSource, renderType, net.minecraft.client.renderer.rendertype.RenderTypes.glint());
    }

    public static VertexConsumer getFestiveCompassFoilBufferDirect(Object bufferSource, Object renderType, PoseStack.Pose pose) {
        return foilBuffer(bufferSource, renderType, net.minecraft.client.renderer.rendertype.RenderTypes.glint());
    }

    public static void setScreen(Object screen) {
        if (Minecraft.getInstance() != null) {
            Minecraft.getInstance().setScreenAndShow((net.minecraft.client.gui.screens.Screen) screen);
        }
    }

    public static ItemStack cycleAdvancementIcon(Object node, Object displayObject) {
        var display = (net.minecraft.advancements.DisplayInfo) displayObject;
        ItemStack fallback = display.getIcon().create();
        if (!(node instanceof net.minecraft.advancements.AdvancementNode advancement)) return fallback;
        var id = advancement.holder().id();
        if (!id.getNamespace().equals("buildscape")) return fallback;
        List<String> names = ADVANCEMENT_ICONS.get(id.getPath());
        if (names == null) return fallback;
        List<Item> items = new ArrayList<>();
        for (String name : names) {
            Item item = BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("buildscape", name));
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

    @SuppressWarnings("unchecked")
    public static void sortGeneralStatsList(Object object, java.util.Comparator<?> comparator) {
        List<net.minecraft.stats.Stat<?>> stats = (List<net.minecraft.stats.Stat<?>>) object;
        java.util.Comparator<net.minecraft.stats.Stat<?>> original = (java.util.Comparator<net.minecraft.stats.Stat<?>>) comparator;
        boolean hasOther = stats.stream().anyMatch(stat -> !statNamespace(stat).equals("minecraft") && !statNamespace(stat).equals("buildscape"));
        addHeader(stats, com.kingodogo.buildscape.stat.ModStats.HEADER_MINECRAFT_STAT);
        addHeader(stats, com.kingodogo.buildscape.stat.ModStats.HEADER_BUILDSCAPE_STAT);
        if (hasOther) addHeader(stats, com.kingodogo.buildscape.stat.ModStats.HEADER_OTHER_STAT);
        else stats.remove(com.kingodogo.buildscape.stat.ModStats.HEADER_OTHER_STAT);
        stats.sort(java.util.Comparator.comparingInt(ClientMixinHooks::statPriority).thenComparing(original));
    }

    private static void addHeader(List<net.minecraft.stats.Stat<?>> stats, net.minecraft.stats.Stat<?> header) {
        if (header != null && !stats.contains(header)) stats.add(header);
    }

    private static String statNamespace(net.minecraft.stats.Stat<?> stat) {
        return stat.getValue() instanceof net.minecraft.resources.Identifier id ? id.getNamespace() : "";
    }

    private static int statPriority(net.minecraft.stats.Stat<?> stat) {
        if (stat == com.kingodogo.buildscape.stat.ModStats.HEADER_MINECRAFT_STAT) return 0;
        if (stat == com.kingodogo.buildscape.stat.ModStats.HEADER_BUILDSCAPE_STAT) return 2;
        if (stat == com.kingodogo.buildscape.stat.ModStats.HEADER_OTHER_STAT) return 4;
        return switch (statNamespace(stat)) { case "minecraft" -> 1; case "buildscape" -> 3; default -> 5; };
    }

    public static boolean renderStatsEntry(Object stat, Object context, int left, int top, int width) {
        if (!(context instanceof net.minecraft.client.gui.GuiGraphicsExtractor graphics) || stat == null) return false;
        String title;
        int color;
        if (stat == com.kingodogo.buildscape.stat.ModStats.HEADER_MINECRAFT_STAT) {
            title = "\u2550\u2550\u2550 Minecraft Statistics \u2550\u2550\u2550"; color = 0xFFFFAA00;
        } else if (stat == com.kingodogo.buildscape.stat.ModStats.HEADER_BUILDSCAPE_STAT) {
            title = "\u2550\u2550\u2550 Buildscape Statistics \u2550\u2550\u2550"; color = 0xFF55FFFF;
        } else if (stat == com.kingodogo.buildscape.stat.ModStats.HEADER_OTHER_STAT) {
            title = "\u2550\u2550\u2550 Other Mod Statistics \u2550\u2550\u2550"; color = 0xFFFFFF55;
        } else return false;
        Component text = Component.literal(title).withStyle(net.minecraft.ChatFormatting.BOLD);
        graphics.centeredText(Minecraft.getInstance().font, text, left + width / 2, top + 1, color);
        return true;
    }

    public static void registerFixedRenderBuffers(Object buffers) {
        if (!(buffers instanceof net.minecraft.client.renderer.RenderBuffers)) return;
        // 26.2 stages all render types in one shared buffer. There is no per-type fixed map.
        // Initialize every festive setup here; the item/model submission mixins select these
        // same cached types and the staged buffer allocates their draws when submitted.
        try {
            com.kingodogo.buildscape.mixinsupport.FestiveSubmission.festiveGlint(net.minecraft.client.renderer.rendertype.RenderTypes.glint());
            com.kingodogo.buildscape.mixinsupport.FestiveSubmission.festiveGlint(net.minecraft.client.renderer.rendertype.RenderTypes.glintTranslucent());
            com.kingodogo.buildscape.mixinsupport.FestiveSubmission.festiveGlint(net.minecraft.client.renderer.rendertype.RenderTypes.entityGlint());
            com.kingodogo.buildscape.mixinsupport.FestiveSubmission.festiveGlint(net.minecraft.client.renderer.rendertype.RenderTypes.armorEntityGlint());
        } catch (RuntimeException exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error("Failed to initialize festive glint render types", exception);
        }
    }

    private static boolean shiftDown() {
        var window = Minecraft.getInstance().getWindow();
        return com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, 340)
                || com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, 344);
    }

    public static List<Component> prepareCustomTooltipText(ItemStack stack, List<Component> original) {
        List<Component> text = new ArrayList<>(original);
        com.kingodogo.buildscape.event.TagTooltipHandler.handleItemTooltip(stack, text);
        if (com.kingodogo.buildscape.event.TagTooltipHandler.isShulkerPreviewEnabled() && previewData(stack) != null && !shiftDown()) {
            Component hint = Component.translatable("tooltip.buildscape.hold_shift_contents");
            if (text.stream().noneMatch(line -> line.getString().equals(hint.getString()))) text.add(hint);
        }
        return text;
    }

    private static Object previewData(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem
                && blockItem.getBlock() instanceof net.minecraft.world.level.block.ShulkerBoxBlock shulker) {
            NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
            NonNullList<ItemStack> filters = NonNullList.withSize(27, ItemStack.EMPTY);
            var container = stack.get(net.minecraft.core.component.DataComponents.CONTAINER);
            if (container != null) container.copyInto(items);
            var data = stack.get(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA);
            if (data != null) {
                var tag = data.copyTagWithoutId();
                var ghosts = tag.getListOrEmpty("GhostFilters");
                for (int i = 0; i < 27 && i < ghosts.size(); i++) {
                    var id = net.minecraft.resources.Identifier.tryParse(ghosts.getString(i).orElse(""));
                    if (id == null) continue;
                    Item item = BuiltInRegistries.ITEM.getValue(id);
                    if (item != null && item != net.minecraft.world.item.Items.AIR) filters.set(i, new ItemStack(item));
                }
            }
            if (items.stream().anyMatch(item -> !item.isEmpty()) || filters.stream().anyMatch(item -> !item.isEmpty()))
                return new com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData(filters, items, shulker.getColor());
            return com.kingodogo.buildscape.event.TagTooltipHandler.getShulkerTooltipData(stack);
        }
        return com.kingodogo.buildscape.event.TagTooltipHandler.getBuildersPouchTooltipData(stack);
    }

    public static Object createClientTooltipComponent(Object data) {
        return previewComponent(data, false, 0);
    }

    private static com.kingodogo.buildscape.mixinsupport.TooltipPreviewComponent previewComponent(Object data, boolean outside, int textHeight) {
        if (data instanceof com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData shulker)
            return new com.kingodogo.buildscape.mixinsupport.TooltipPreviewComponent(shulker.getFilterStacks(), shulker.getRealStacks(),
                    com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData.getHexColor(shulker.getColor()), outside, textHeight, ClientMixinHooks::extractPreviewImage);
        if (data instanceof com.kingodogo.buildscape.client.tooltip.BuildersPouchTooltipData pouch)
            return new com.kingodogo.buildscape.mixinsupport.TooltipPreviewComponent(pouch.getFilterStacks(), pouch.getRealStacks(),
                    com.kingodogo.buildscape.client.tooltip.BuildersPouchTooltipData.COLOR_GOLD, outside, textHeight, ClientMixinHooks::extractPreviewImage);
        return null;
    }

    @SuppressWarnings("unchecked")
    public static void renderCustomScreenTooltip(Object context, List<?> components, int mouseX, int mouseY, ItemStack stack, Font font) {
        if (!(context instanceof net.minecraft.client.gui.GuiGraphicsExtractor) || !shiftDown()
                || !com.kingodogo.buildscape.event.TagTooltipHandler.isShulkerPreviewEnabled()) return;
        try {
            for (Object component : components) if (component instanceof com.kingodogo.buildscape.mixinsupport.TooltipPreviewComponent) return;
            int textHeight = 0;
            for (Object component : components)
                if (component instanceof net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent tooltip) textHeight += tooltip.getHeight(font);
            var preview = previewComponent(previewData(stack), true, textHeight);
            if (preview != null) ((List<net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent>) components).add(preview);
        } catch (RuntimeException exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed to attach custom tooltip preview", exception);
        }
    }

    private static void extractPreviewImage(com.kingodogo.buildscape.mixinsupport.TooltipPreviewComponent preview,
            Font font, int x, int y, net.minecraft.client.gui.GuiGraphicsExtractor graphics) {
        int width = preview.imageWidth(), height = preview.imageHeight();
        if (preview.outside) {
            x -= 3;
            int textTop = y - preview.textHeight - 2;
            y += 4;
            if (y + height > graphics.guiHeight() - 4) y = textTop - height - 6;
            x = Math.max(4, Math.min(x, graphics.guiWidth() - width - 4));
            y = Math.max(4, Math.min(y, graphics.guiHeight() - height - 4));
        }
        graphics.nextStratum();
        try {
            var texture = net.minecraft.resources.Identifier.fromNamespaceAndPath("buildscape", "textures/gui/shulker_box_tooltip.png");
            int tint = 0xFF000000 | preview.color;
            int rows = (preview.items.size() + 8) / 9;
            previewTile(graphics, texture, x, y, 0, 0, 7, 7, tint);
            previewTile(graphics, texture, x + width - 7, y, 25, 0, 7, 7, tint);
            previewTile(graphics, texture, x, y + height - 7, 0, 25, 7, 7, tint);
            previewTile(graphics, texture, x + width - 7, y + height - 7, 25, 25, 7, 7, tint);
            for (int col = 0; col < 9; col++) {
                previewTile(graphics, texture, x + 7 + col * 18, y, 7, 0, 18, 7, tint);
                previewTile(graphics, texture, x + 7 + col * 18, y + height - 7, 7, 25, 18, 7, tint);
            }
            for (int row = 0; row < rows; row++) {
                previewTile(graphics, texture, x, y + 7 + row * 18, 0, 7, 7, 18, tint);
                previewTile(graphics, texture, x + width - 7, y + 7 + row * 18, 25, 7, 7, 18, tint);
                for (int col = 0; col < 9; col++) previewTile(graphics, texture, x + 7 + col * 18, y + 7 + row * 18, 7, 7, 18, 18, tint);
            }
            graphics.nextStratum();
            for (int i = 0; i < preview.items.size(); i++) {
                ItemStack real = preview.items.get(i), filter = i < preview.filters.size() ? preview.filters.get(i) : ItemStack.EMPTY;
                int itemX = x + 8 + (i % 9) * 18, itemY = y + 8 + (i / 9) * 18;
                if (!real.isEmpty()) {
                    graphics.fakeItem(real, itemX, itemY, i);
                    graphics.itemDecorations(font, real, itemX, itemY);
                } else if (!filter.isEmpty()) graphics.fakeItem(filter, itemX, itemY, i);
            }
        } catch (RuntimeException exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed to extract custom tooltip preview", exception);
        }
    }

    private static void previewTile(net.minecraft.client.gui.GuiGraphicsExtractor graphics, net.minecraft.resources.Identifier texture,
            int x, int y, int u, int v, int width, int height, int color) {
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, x, y, (float) u, (float) v, width, height, 32, 32, color);
    }

    private record FireworkMetadata(net.minecraft.nbt.ListTag explosions, float yaw) {}
    private static final java.util.Map<Object, FireworkMetadata> FIREWORK_METADATA =
            java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    public static void handleFireworkStarterTick(Object starter) {
        ItemStack stack = com.kingodogo.buildscape.mixinsupport.FireworkContext.take(starter);
        if (stack.isEmpty()) return;
        try {
            var custom = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
            if (custom == null) return;
            var root = custom.copyTag();
            var fireworks = root.getCompound("Fireworks");
            if (fireworks.isEmpty()) return;
            var explosions = fireworks.get().getListOrEmpty("Explosions");
            boolean customShape = false;
            for (var element : explosions) {
                if (element instanceof net.minecraft.nbt.CompoundTag tag
                        && com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.isCustomShape(tag.getByteOr("Type", (byte) 0))) {
                    customShape = true;
                    break;
                }
            }
            if (!customShape) return;
            double xd = particleCoordinate(starter, "xd"), zd = particleCoordinate(starter, "zd");
            float yaw = Math.hypot(xd, zd) > 0.05
                    ? (float) Math.toDegrees(Math.atan2(xd, zd))
                    : Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.getYRot();
            yaw = root.getFloatOr("ShotYaw", yaw);
            FIREWORK_METADATA.put(starter, new FireworkMetadata(explosions.copy(), yaw));
            var particle = (net.minecraft.client.particle.Particle) starter;
            particle.setBoundingBox(particle.getBoundingBox().inflate(120.0D));
        } catch (ReflectiveOperationException | RuntimeException exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed to prepare custom firework explosion", exception);
        }
    }

    public static boolean renderCustomFireworkExplosion(Object starter, Object colorsObject, Object fadesObject,
            boolean trail, boolean flicker) {
        FireworkMetadata metadata = FIREWORK_METADATA.get(starter);
        if (metadata == null || !(starter instanceof com.kingodogo.buildscape.mixinsupport.FireworkSparkAccess sparks)) return false;
        try {
            var life = net.minecraft.client.particle.FireworkParticles.Starter.class.getDeclaredField("life");
            life.setAccessible(true);
            int index = life.getInt(starter) / 2;
            if (index < 0 || index >= metadata.explosions().size()
                    || !(metadata.explosions().get(index) instanceof net.minecraft.nbt.CompoundTag tag)) return false;
            var shape = com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.getByNumericId(tag.getByteOr("Type", (byte) 0));
            if (shape.isEmpty()) return false;
            int[] colors = ((it.unimi.dsi.fastutil.ints.IntList) colorsObject).toIntArray();
            int[] fades = ((it.unimi.dsi.fastutil.ints.IntList) fadesObject).toIntArray();
            float yaw = tag.getFloatOr("ShotYaw", metadata.yaw());
            com.kingodogo.buildscape.firework.CustomFireworkRenderer.renderExplosion(shape.get(),
                    particleCoordinate(starter, "x"), particleCoordinate(starter, "y"), particleCoordinate(starter, "z"),
                    colors, fades, trail, flicker, yaw,
                    (x, y, z, vx, vy, vz, pointColors, fadeColors, sparkTrail, sparkFlicker) ->
                            sparks.buildscape$spawnSpark(x, y, z, vx, vy, vz,
                                    new it.unimi.dsi.fastutil.ints.IntArrayList(pointColors),
                                    new it.unimi.dsi.fastutil.ints.IntArrayList(fadeColors), sparkTrail, sparkFlicker));
            return true;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            FIREWORK_METADATA.remove(starter);
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed to render custom firework explosion; using the vanilla burst", exception);
            return false;
        }
    }

    private static double particleCoordinate(Object particle, String name) throws ReflectiveOperationException {
        var field = net.minecraft.client.particle.Particle.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.getDouble(particle);
    }
}
