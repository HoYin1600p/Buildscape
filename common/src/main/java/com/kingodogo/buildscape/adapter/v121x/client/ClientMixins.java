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
        } catch (Throwable ignored) {}
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
        Button button = new Button.Builder(Component.empty(), b -> {
            boolean current = ((StonecutterMenuExtension) menu).buildscape$isCutAll();
            ((StonecutterMenuExtension) menu).buildscape$setCutAll(!current);
            Minecraft mc = Minecraft.getInstance();
            if (mc.gameMode != null) {
                mc.gameMode.handleInventoryButtonClick(menu.containerId, -123);
            }
        }).bounds(x, y, 18, 10).build();
        addWidgetToScreen(screen, button);
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
