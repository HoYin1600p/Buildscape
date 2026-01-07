package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.PillarBlock;
import com.kingodogo.buildscape.block.PillarBlockEntity;
import com.kingodogo.buildscape.event.ItemFrameParticleHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class PillarOverlayHandler {

    private static final int MARGIN_RIGHT = 5;
    private static final int MARGIN_BOTTOM = 5;

    private static final int SWATCH_SIZE = 14;
    private static final int SWATCH_SPACING = 3;
    private static final int SWATCH_BORDER = 1;

    private static final int LIME_COLOR = 0xFF32CD32;
    private static final int BORDER_WIDTH = 2;
    private static final int CORNER_RADIUS = 3;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        HitResult hitResult = mc.hitResult;
        if (hitResult == null) {
            return;
        }

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if (entityHit.getEntity() instanceof ItemFrame) {
                ItemFrame itemFrame = (ItemFrame) entityHit.getEntity();

                if (ItemFrameParticleHandler.hasCustomColors(itemFrame)) {
                    renderItemFrameOverlay(event.getMatrixStack(), mc, itemFrame);
                }
            }
            return;
        }

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos pos = blockHit.getBlockPos();
        BlockState state = mc.level.getBlockState(pos);

        if (!(state.getBlock() instanceof PillarBlock)) {
            return;
        }

        ItemStack mainHand = mc.player.getMainHandItem();
        ItemStack offHand = mc.player.getOffhandItem();
        boolean holdingDye =
                mainHand.getItem() instanceof DyeItem ||
                        offHand.getItem() instanceof DyeItem;
        if (!holdingDye) {
            return;
        }

        BlockEntity be = mc.level.getBlockEntity(pos);
        if (!(be instanceof PillarBlockEntity)) {
            return;
        }
        PillarBlockEntity pillarBE = (PillarBlockEntity) be;

        if (!pillarBE.hasCustomColors()) {
            return;
        }

        renderPillarOverlay(event.getMatrixStack(), mc, pillarBE);
    }

    private static void renderItemFrameOverlay(
            PoseStack poseStack,
            Minecraft mc,
            ItemFrame itemFrame
    ) {
        Font font = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        String frameId = ItemFrameParticleHandler.getFrameId(itemFrame);
        List<String> colors = ItemFrameParticleHandler.getParticleColors(itemFrame);

        if (frameId == null && (colors == null || colors.isEmpty())) {
            return;
        }

        renderOverlay(
                poseStack,
                font,
                screenWidth,
                screenHeight,
                frameId != null ? frameId : "Unknown",
                colors,
                ItemFrameParticleHandler.MAX_DYE_COLORS
        );
    }

    private static void renderPillarOverlay(
            PoseStack poseStack,
            Minecraft mc,
            PillarBlockEntity pillarBE
    ) {
        Font font = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        String pillarId = pillarBE.getPillarId();
        List<String> colors = pillarBE.getParticleColors();

        if (pillarId == null && (colors == null || colors.isEmpty())) {
            return;
        }

        renderOverlay(
                poseStack,
                font,
                screenWidth,
                screenHeight,
                pillarId != null ? pillarId : "Unknown",
                colors,
                PillarBlockEntity.MAX_DYE_COLORS
        );
    }

    private static void renderOverlay(
            PoseStack poseStack,
            Font font,
            int screenWidth,
            int screenHeight,
            String id,
            List<String> colors,
            int maxColors
    ) {
        int numColors = colors != null ? colors.size() : 0;
        int swatchRowWidth = numColors > 0
                ? numColors * (SWATCH_SIZE + SWATCH_SPACING) - SWATCH_SPACING
                : 0;

        int idWidth = font.width(id);

        String colorCountText = "Colors: " + numColors + "/" + maxColors;
        int colorCountWidth = font.width(colorCountText);

        int overlayWidth =
                Math.max(idWidth, Math.max(colorCountWidth, swatchRowWidth)) + 16;
        int overlayHeight = 10 + 12 + SWATCH_SIZE + 12 + 8;

        int x = screenWidth - overlayWidth - MARGIN_RIGHT;
        int y = screenHeight - overlayHeight - MARGIN_BOTTOM;

        int bgColor = 0xDD1A1A1A;

        GuiComponent.fill(
                poseStack,
                x + CORNER_RADIUS,
                y,
                x + overlayWidth - CORNER_RADIUS,
                y + overlayHeight,
                bgColor
        );
        GuiComponent.fill(
                poseStack,
                x,
                y + CORNER_RADIUS,
                x + CORNER_RADIUS,
                y + overlayHeight - CORNER_RADIUS,
                bgColor
        );
        GuiComponent.fill(
                poseStack,
                x + overlayWidth - CORNER_RADIUS,
                y + CORNER_RADIUS,
                x + overlayWidth,
                y + overlayHeight - CORNER_RADIUS,
                bgColor
        );

        GuiComponent.fill(
                poseStack,
                x + 1,
                y + 1,
                x + CORNER_RADIUS,
                y + CORNER_RADIUS,
                bgColor
        );
        GuiComponent.fill(
                poseStack,
                x + overlayWidth - CORNER_RADIUS,
                y + 1,
                x + overlayWidth - 1,
                y + CORNER_RADIUS,
                bgColor
        );
        GuiComponent.fill(
                poseStack,
                x + 1,
                y + overlayHeight - CORNER_RADIUS,
                x + CORNER_RADIUS,
                y + overlayHeight - 1,
                bgColor
        );
        GuiComponent.fill(
                poseStack,
                x + overlayWidth - CORNER_RADIUS,
                y + overlayHeight - CORNER_RADIUS,
                x + overlayWidth - 1,
                y + overlayHeight - 1,
                bgColor
        );

        drawRoundedBorder(
                poseStack,
                x,
                y,
                overlayWidth,
                overlayHeight,
                LIME_COLOR,
                BORDER_WIDTH,
                CORNER_RADIUS
        );

        int textX = x + (overlayWidth - idWidth) / 2;
        int textY = y + 6;
        font.drawShadow(poseStack, id, textX, textY, 0xFFFFD700);

        if (numColors > 0) {
            int swatchStartX = x + (overlayWidth - swatchRowWidth) / 2;
            int swatchY = textY + 14;

            for (int i = 0; i < numColors; i++) {
                String colorCode = colors.get(i);
                int color = parseColorCode(colorCode);
                int swatchX = swatchStartX + i * (SWATCH_SIZE + SWATCH_SPACING);

                GuiComponent.fill(
                        poseStack,
                        swatchX - SWATCH_BORDER,
                        swatchY - SWATCH_BORDER,
                        swatchX + SWATCH_SIZE + SWATCH_BORDER,
                        swatchY + SWATCH_SIZE + SWATCH_BORDER,
                        0xFF000000
                );

                GuiComponent.fill(
                        poseStack,
                        swatchX,
                        swatchY,
                        swatchX + SWATCH_SIZE,
                        swatchY + SWATCH_SIZE,
                        0xFF000000 | color
                );
            }

            int countY = swatchY + SWATCH_SIZE + 4;
            int countX = x + (overlayWidth - colorCountWidth) / 2;
            font.drawShadow(poseStack, colorCountText, countX, countY, 0xFFAAAAAA);
        }
    }

    private static void drawRoundedBorder(
            PoseStack poseStack,
            int x,
            int y,
            int width,
            int height,
            int color,
            int borderWidth,
            int cornerRadius
    ) {
        for (int i = 0; i < borderWidth; i++) {
            GuiComponent.fill(
                    poseStack,
                    x + cornerRadius,
                    y + i,
                    x + width - cornerRadius,
                    y + i + 1,
                    color
            );
        }

        for (int i = 0; i < borderWidth; i++) {
            GuiComponent.fill(
                    poseStack,
                    x + cornerRadius,
                    y + height - borderWidth + i,
                    x + width - cornerRadius,
                    y + height - borderWidth + i + 1,
                    color
            );
        }

        for (int i = 0; i < borderWidth; i++) {
            GuiComponent.fill(
                    poseStack,
                    x + i,
                    y + cornerRadius,
                    x + i + 1,
                    y + height - cornerRadius,
                    color
            );
        }

        for (int i = 0; i < borderWidth; i++) {
            GuiComponent.fill(
                    poseStack,
                    x + width - borderWidth + i,
                    y + cornerRadius,
                    x + width - borderWidth + i + 1,
                    y + height - cornerRadius,
                    color
            );
        }

        GuiComponent.fill(
                poseStack,
                x + 1,
                y,
                x + cornerRadius,
                y + borderWidth,
                color
        );
        GuiComponent.fill(
                poseStack,
                x,
                y + 1,
                x + borderWidth,
                y + cornerRadius,
                color
        );

        GuiComponent.fill(
                poseStack,
                x + width - cornerRadius,
                y,
                x + width - 1,
                y + borderWidth,
                color
        );
        GuiComponent.fill(
                poseStack,
                x + width - borderWidth,
                y + 1,
                x + width,
                y + cornerRadius,
                color
        );

        GuiComponent.fill(
                poseStack,
                x + 1,
                y + height - borderWidth,
                x + cornerRadius,
                y + height,
                color
        );
        GuiComponent.fill(
                poseStack,
                x,
                y + height - cornerRadius,
                x + borderWidth,
                y + height - 1,
                color
        );

        GuiComponent.fill(
                poseStack,
                x + width - cornerRadius,
                y + height - borderWidth,
                x + width - 1,
                y + height,
                color
        );
        GuiComponent.fill(
                poseStack,
                x + width - borderWidth,
                y + height - cornerRadius,
                x + width,
                y + height - 1,
                color
        );
    }

    private static int parseColorCode(String colorCode) {
        if (colorCode == null || colorCode.isEmpty()) {
            return 0xFFFFFF;
        }

        try {
            String hex = colorCode.startsWith("#")
                    ? colorCode.substring(1)
                    : colorCode;
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }
}
