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

/**
 * Cosmetic Preview Panel (Panel 2)
 * 
 * Large, zoomable, rotatable 3D cosmetic preview.
 * Renders items in 3D similar to pillar blocks - rotating and floating.
 * Resolves selected cosmetic ID to ItemStack/Block via CosmeticRegistry.
 * Fully clipped viewport using scissor.
 * Renders selected cosmetic from Panel 1.
 * 
 * Dimensions: 21% width × 25% height
 * Position: (45%, 0%)
 */
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
        // Initialize rotation animation
        itemStartTime = System.nanoTime() / 1000000L; // Convert to milliseconds
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Enable scissor clipping
        double guiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        
        int scissorX = (int)(startX * guiScale);
        int scissorY = (int)(windowHeight - (startY + height) * guiScale);
        int scissorWidth = (int)(width * guiScale);
        int scissorHeight = (int)(height * guiScale);
        
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        
        // Render background
        GuiComponent.fill(poseStack, startX, startY, endX, endY, 0x80000000);
        
        // Render title
        String title = "Cosmetic Preview";
        int titleWidth = mc.font.width(title);
        mc.font.draw(poseStack, title, 
            startX + (width - titleWidth) / 2, 
            startY + PADDING, 
            0xFFFFFF);
        
        // Get selected cosmetic
        String selectedCosmeticId = state.getSelectedCosmeticId();
        if (selectedCosmeticId == null || selectedCosmeticId.isEmpty()) {
            // Render placeholder text
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
        
        // Resolve cosmetic ID to ItemStack
        ItemStack stack = cosmeticRegistry.resolveToItemStack(selectedCosmeticId);
        if (stack == null || stack.isEmpty()) {
            // Render error text with the ID for debugging
            String error = "Invalid cosmetic";
            int textWidth = mc.font.width(error);
            int centerY = startY + height / 2;
            mc.font.draw(poseStack, error, 
                startX + (width - textWidth) / 2, 
                centerY - 5, 
                0xFF0000);
            
            // Show the ID that failed to resolve
            String idText = selectedCosmeticId.length() > 30 ? selectedCosmeticId.substring(0, 27) + "..." : selectedCosmeticId;
            int idWidth = mc.font.width(idText);
            mc.font.draw(poseStack, idText, 
                startX + (width - idWidth) / 2, 
                centerY + 10, 
                0x888888);
            RenderSystem.disableScissor();
            return;
        }
        
        // Reset timer if item changed
        if (!selectedCosmeticId.equals(lastSelectedCosmeticId)) {
            itemStartTime = System.nanoTime() / 1000000L;
            lastSelectedCosmeticId = selectedCosmeticId;
        }
        
        // Calculate rotation and floating animation (similar to pillar blocks)
        long currentTime = System.nanoTime() / 1000000L;
        float elapsedSeconds = (currentTime - itemStartTime) / 1000.0f;
        
        // Rotation speed: 90 degrees per second (same as pillar blocks)
        float rotationSpeed = 90.0f;
        rotation = (elapsedSeconds * rotationSpeed) % 360.0f;
        
        // Floating animation (bobbing up and down)
        float bobAmount = (float) Math.sin(elapsedSeconds * 2.0f) * 0.05f; // Bob up and down
        bobOffset = bobAmount;
        
        // Calculate center of panel (below title, within bounds)
        int titleHeight = 15;
        int availableHeight = height - PADDING * 2 - titleHeight - 20; // Leave space for name at bottom
        int centerX = startX + width / 2;
        int centerY = startY + PADDING + titleHeight + availableHeight / 2;
        
        // Ensure center is within panel bounds
        centerX = Math.max(startX + 20, Math.min(endX - 20, centerX));
        centerY = Math.max(startY + titleHeight + 20, Math.min(endY - 30, centerY));
        
        // Render item in 3D (similar to pillar entity rendering)
        render3DItem(poseStack, stack, centerX, centerY, partialTick);
        
        // Render cosmetic name below item
        String cosmeticName = selectedCosmeticId;
        // Extract item name from ID
        if (cosmeticName.contains(":")) {
            String[] parts = cosmeticName.split(":");
            if (parts.length >= 3) {
                cosmeticName = parts[2]; // Get item name
                // Format: minecraft:diamond_sword -> Diamond Sword
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
    
    /**
     * Render item in 3D (similar to pillar entity rendering).
     * Uses proper 3D rendering with EntityRenderDispatcher-like setup.
     */
    private void render3DItem(PoseStack poseStack, ItemStack stack, int centerX, int centerY, float partialTick) {
        Level level = mc.level;
        if (level == null) {
            // Fallback to 2D if no level
            render2DItem(poseStack, stack, centerX, centerY);
            return;
        }
        
        // Set up 3D rendering context (same as pillar entity rendering)
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        // Setup lighting for GUI 3D rendering (same as pillar blocks)
        Vector3f light1 = new Vector3f(0.2f, 1.0f, -0.7f);
        light1.normalize();
        Vector3f light2 = new Vector3f(-0.2f, 1.0f, 0.7f);
        light2.normalize();
        RenderSystem.setupGui3DDiffuseLighting(light1, light2);
        
        // Get buffer source for 3D rendering
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        
        poseStack.pushPose();
        
        // Translate to center of panel (GUI coordinates)
        // Z offset for depth - items need to be closer than entities
        float bobY = bobOffset * 15.0f; // Apply floating animation
        poseStack.translate(centerX, centerY + bobY, 100.0f);
        
        // Apply scale based on zoom (similar to pillar entity scaling)
        // Items are smaller than entities, so scale accordingly
        float baseScale = 30.0f * zoom; // Base scale for items
        poseStack.scale(baseScale, -baseScale, baseScale); // Invert Y for GUI coordinate system
        
        // Apply rotation around Y axis (horizontal rotation, same as pillar blocks)
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
        
        // Get item model
        BakedModel model = itemRenderer.getModel(stack, level, null, 0);
        
        // Check if item has enchantments for glint rendering
        boolean hasGlint = stack.hasFoil();
        
        // Full brightness for GUI rendering (same as pillar blocks)
        int lightLevel = 15728880; // Full brightness (15 sky, 15 block)
        int overlay = 0; // No overlay
        
        // Render the item in 3D using FIXED transform type (same as pillar blocks)
        // This provides true 3D rendering with proper lighting and shadows
        try {
            itemRenderer.render(stack, ItemTransforms.TransformType.FIXED, 
                hasGlint, poseStack, bufferSource, lightLevel, overlay, model);
            bufferSource.endBatch(); // Flush buffers
        } catch (Exception e) {
            // If 3D rendering fails, fallback to 2D
            BuildScape.getLogger().debug("3D item rendering failed, using 2D fallback: " + e.getMessage());
            poseStack.popPose();
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            render2DItem(poseStack, stack, centerX, centerY);
            return;
        }
        
        poseStack.popPose();
        
        // Clean up rendering state
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }
    
    /**
     * Enhanced 2D rendering with rotation and floating effects.
     * This is the primary rendering method to ensure items are always visible.
     */
    private void render2DItem(PoseStack poseStack, ItemStack stack, int centerX, int centerY) {
        // Calculate floating offset
        float bobY = bobOffset * 15.0f;
        
        // Calculate final render position (within bounds)
        int renderX = Math.max(startX + 10, Math.min(endX - 10, centerX));
        int renderY = Math.max(startY + 25, Math.min(endY - 25, (int)(centerY + bobY)));
        
        // Render background circle first (behind item)
        int bgSize = (int)(20 * zoom);
        GuiComponent.fill(poseStack, 
            renderX - bgSize / 2, renderY - bgSize / 2,
            renderX + bgSize / 2, renderY + bgSize / 2,
            0x30000000);
        
        // Render the item with rotation and scaling
        poseStack.pushPose();
        
        // Translate to render position
        poseStack.translate(renderX, renderY, 0);
        
        // Apply zoom
        float itemScale = zoom * 2.5f; // Scale for better visibility
        poseStack.scale(itemScale, itemScale, 1.0f);
        
        // Apply rotation (visual effect - rotate around Z axis for 2D)
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotation));
        
        // Calculate item position (centered)
        int itemSize = 16;
        int itemX = -itemSize / 2;
        int itemY = -itemSize / 2;
        
        // Render item using GUI renderer - this is the standard way
        itemRenderer.renderGuiItem(stack, itemX, itemY);
        itemRenderer.renderGuiItemDecorations(mc.font, stack, itemX, itemY);
        
        poseStack.popPose();
    }
    
    @Override
    protected boolean handleMouseScrolled(double mouseX, double mouseY, double delta) {
        // Zoom in/out with scroll
        zoom += (float)delta * 0.1f;
        zoom = Math.max(0.5f, Math.min(2.0f, zoom)); // Clamp between 0.5x and 2x
        return true;
    }
    
    @Override
    protected boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        // Click to reset rotation
        if (button == 0) {
            itemStartTime = System.nanoTime() / 1000000L;
            rotation = 0.0f;
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean handleMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // Drag to rotate manually
        if (button == 0) {
            rotation += (float)dragX * 2.0f;
            if (rotation < 0) rotation += 360.0f;
            if (rotation >= 360.0f) rotation -= 360.0f;
            // Update start time to maintain smooth animation
            itemStartTime = System.nanoTime() / 1000000L;
            return true;
        }
        return false;
    }
}
