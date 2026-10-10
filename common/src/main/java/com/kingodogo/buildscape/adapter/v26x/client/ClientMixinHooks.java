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
}
