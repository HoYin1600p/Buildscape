package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.cosmetics.CosmeticRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CosmeticPreviewPanel extends BasePanel {
    private static final int PADDING = 10;
    
    private final CosmeticRegistry cosmeticRegistry = CosmeticRegistry.getInstance();
    private final SupportersTabState state = SupportersTabState.getInstance();
    private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    private final Minecraft mc = Minecraft.getInstance();
    
    private float rotation = 0.0f;
    private float zoom = 1.0f;
    private long itemStartTime = 0;
    private String lastSelectedCosmeticId = null;
    private float bobOffset = 0.0f;
    
    @Override
    public void init() {
        itemStartTime = System.nanoTime() / 1000000L;
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        
        int scissorX = (int)(startX * guiScale);
        int scissorY = (int)(windowHeight - (startY + height) * guiScale);
        int scissorWidth = (int)(width * guiScale);
        int scissorHeight = (int)(height * guiScale);
        
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);

        GuiComponent.fill(poseStack, startX, startY, endX, endY, 0x80000000);

        String title = "Cosmetic Preview";
        int titleWidth = mc.font.width(title);
        mc.font.draw(poseStack, title, 
            startX + (width - titleWidth) / 2, 
            startY + PADDING, 
            0xFFFFFF);

        String selectedCosmeticId = state.getSelectedCosmeticId();
        if (selectedCosmeticId == null || selectedCosmeticId.isEmpty()) {
            String placeholder = "Select a cosmetic";
            int textWidth = mc.font.width(placeholder);
            int centerY = startY + height / 2;
            mc.font.draw(poseStack, placeholder, 
                startX + (width - textWidth) / 2, 
                centerY - 5, 
                0xAAAAAA);
            
            String hint = "Click a cosmetic to preview";
            int hintWidth = mc.font.width(hint);
            mc.font.draw(poseStack, hint, 
                startX + (width - hintWidth) / 2, 
                centerY + 10, 
                0x888888);
            RenderSystem.disableScissor();
            return;
        }

        ItemStack stack = cosmeticRegistry.resolveToItemStack(selectedCosmeticId);
        if ((stack == null || stack.isEmpty()) && state.getBestSlotForCosmetic(selectedCosmeticId) == SupportersTabState.SLOT_HEAD) {
            stack = new ItemStack(net.minecraft.world.item.Items.LEATHER_HELMET);
        }

        if (stack == null || stack.isEmpty()) {
            String error = "Invalid cosmetic";
            int textWidth = mc.font.width(error);
            int centerY = startY + height / 2;
            mc.font.draw(poseStack, error, 
                startX + (width - textWidth) / 2, 
                centerY - 5, 
                0xFF0000);

            String idText = selectedCosmeticId.length() > 30 ? selectedCosmeticId.substring(0, 27) + "..." : selectedCosmeticId;
            int idWidth = mc.font.width(idText);
            mc.font.draw(poseStack, idText, 
                startX + (width - idWidth) / 2, 
                centerY + 10, 
                0x888888);
            RenderSystem.disableScissor();
            return;
        }

        if (!selectedCosmeticId.equals(lastSelectedCosmeticId)) {
            itemStartTime = System.nanoTime() / 1000000L;
            lastSelectedCosmeticId = selectedCosmeticId;
        }

        long currentTime = System.nanoTime() / 1000000L;
        float elapsedSeconds = (currentTime - itemStartTime) / 1000.0f;

        float rotationSpeed = 90.0f;
        rotation = (elapsedSeconds * rotationSpeed) % 360.0f;

        float bobAmount = (float) Math.sin(elapsedSeconds * 2.0f) * 0.05f;
        bobOffset = bobAmount;

        int titleHeight = 15;
        int availableHeight = height - PADDING * 2 - titleHeight - 20;
        int centerX = startX + width / 2;
        int centerY = startY + PADDING + titleHeight + availableHeight / 2;

        centerX = Math.max(startX + 20, Math.min(endX - 20, centerX));
        centerY = Math.max(startY + titleHeight + 20, Math.min(endY - 30, centerY));

        render3DItem(poseStack, stack, centerX, centerY, partialTick);

        String cosmeticName = selectedCosmeticId;
        if (cosmeticName.contains(":")) {
            String[] parts = cosmeticName.split(":");
            if (parts.length >= 3) {
                cosmeticName = parts[2];
                cosmeticName = cosmeticName.replace("_", " ");
                String[] words = cosmeticName.split(" ");
                StringBuilder formatted = new StringBuilder();
                for (String word : words) {
                    if (word.length() > 0) {
                        formatted.append(Character.toUpperCase(word.charAt(0)));
                        if (word.length() > 1) {
                            formatted.append(word.substring(1));
                        }
                        formatted.append(" ");
                    }
                }
                cosmeticName = formatted.toString().trim();
            }
        }
        if (cosmeticName.length() > 30) {
            cosmeticName = cosmeticName.substring(0, 27) + "...";
        }
        int nameWidth = mc.font.width(cosmeticName);
        mc.font.draw(poseStack, cosmeticName, 
            startX + (width - nameWidth) / 2, 
            startY + height - 15, 
            0xCCCCCC);
        
        RenderSystem.disableScissor();
    }
    
    private void render3DItem(PoseStack poseStack, ItemStack stack, int centerX, int centerY, float partialTick) {
        Level level = mc.level;
        if (level == null) {
            render2DItem(poseStack, stack, centerX, centerY);
            return;
        }

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Vector3f light1 = new Vector3f(0.2f, 1.0f, -0.7f);
        light1.normalize();
        Vector3f light2 = new Vector3f(-0.2f, 1.0f, 0.7f);
        light2.normalize();
        RenderSystem.setupGui3DDiffuseLighting(light1, light2);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        
        poseStack.pushPose();

        float bobY = bobOffset * 15.0f;
        poseStack.translate(centerX, centerY + bobY, 100.0f);

        float baseScale = 30.0f * zoom;
        poseStack.scale(baseScale, -baseScale, baseScale);

        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));

        BakedModel model = itemRenderer.getModel(stack, level, null, 0);

        boolean hasGlint = stack.hasFoil();

        int lightLevel = 15728880;
        int overlay = 0;

        try {
            itemRenderer.render(stack, ItemTransforms.TransformType.FIXED, 
                hasGlint, poseStack, bufferSource, lightLevel, overlay, model);
            bufferSource.endBatch();
        } catch (Exception e) {
            // Suppressed debug log to prevent render loop spam
            poseStack.popPose();
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            render2DItem(poseStack, stack, centerX, centerY);
            return;
        }
        
        poseStack.popPose();

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }
    
    private void render2DItem(PoseStack poseStack, ItemStack stack, int centerX, int centerY) {
        float bobY = bobOffset * 15.0f;

        int renderX = Math.max(startX + 10, Math.min(endX - 10, centerX));
        int renderY = Math.max(startY + 25, Math.min(endY - 25, (int)(centerY + bobY)));

        int bgSize = (int)(20 * zoom);
        GuiComponent.fill(poseStack, 
            renderX - bgSize / 2, renderY - bgSize / 2,
            renderX + bgSize / 2, renderY + bgSize / 2,
            0x30000000);

        poseStack.pushPose();

        poseStack.translate(renderX, renderY, 0);

        float itemScale = zoom * 2.5f;
        poseStack.scale(itemScale, itemScale, 1.0f);

        poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotation));

        int itemSize = 16;
        int itemX = -itemSize / 2;
        int itemY = -itemSize / 2;

        itemRenderer.renderGuiItem(stack, itemX, itemY);
        itemRenderer.renderGuiItemDecorations(mc.font, stack, itemX, itemY);
        
        poseStack.popPose();
    }
    
    @Override
    protected boolean handleMouseScrolled(double mouseX, double mouseY, double delta) {
        zoom += (float)delta * 0.1f;
        zoom = Math.max(0.5f, Math.min(2.0f, zoom));
        return true;
    }
    
    @Override
    protected boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            itemStartTime = System.nanoTime() / 1000000L;
            rotation = 0.0f;
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean handleMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            rotation += (float)dragX * 2.0f;
            if (rotation < 0) rotation += 360.0f;
            if (rotation >= 360.0f) rotation -= 360.0f;
            itemStartTime = System.nanoTime() / 1000000L;
            return true;
        }
        return false;
    }
}
