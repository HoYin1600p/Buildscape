package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.cosmetics.CosmeticManager;
import com.kingodogo.buildscape.cosmetics.CosmeticRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import com.mojang.authlib.GameProfile;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Player Avatar Panel (Panel 5)
 * Full 3D player model render.
 * Resolves equipped cosmetic IDs to ItemStack via CosmeticRegistry.
 * Applies selected cosmetics live.
 * Always shows player skin, even when no cosmetics are equipped.
 * Clipped viewport with scissor.
 * Dimensions: 21% width × 55% height
 * Position: (45%, 26%)
 */
public class PlayerAvatarPanel extends BasePanel {
    private static final double FIXED_GUI_SCALE = 2.0; // Fixed GUI scale for consistency
    private static final int PADDING = 10;
    private static final UUID KINGODOGO_UUID = UUID.fromString("f84c6a79-0a4e-45e1-875e-e049e012769f"); // Kingodogo's UUID
    private static final String KINGODOGO_USERNAME = "Kingodogo";
    
    private final CosmeticRegistry cosmeticRegistry = CosmeticRegistry.getInstance();
    private final SupportersTabState state = SupportersTabState.getInstance();
    private final Minecraft mc = Minecraft.getInstance();
    
    // Player rotation for mouse drag (3D sphere rotation)
    // Using spherical coordinates for smooth rotation in any direction
    private float rotationYaw = 0.0f; // Horizontal rotation (left/right) in degrees
    private float rotationPitch = 0.0f; // Vertical rotation (up/down) in degrees
    private boolean isDragging = false;
    
    // Zoom control for player avatar
    private float playerZoom = 2.0f; // Zoom level (default 200%)
    private static final float MIN_ZOOM = 0.5f; // Minimum zoom (50%)
    private static final float MAX_ZOOM = 3.0f; // Maximum zoom (300%)
    
    // Walking animation toggle
    private boolean isWalking = false; // Whether player should show walking animation
    private long lastFrameTime = 0; // Last frame time for delta calculation
    private double walkX = 0.0; // Current X position
    private double walkZ = 0.0; // Current Z position
    private double walkXOld = 0.0; // Previous X position (for movement detection)
    private double walkZOld = 0.0; // Previous Z position
    private boolean keyboardWalking = false; // True while WASD is held
    private double keyboardDirX = 0.0; // Current keyboard-driven direction X
    private double keyboardDirZ = 1.0; // Current keyboard-driven direction Z
    
    // Track particle spawn times for GUI preview
    private final Map<String, Long> itemAnimationTimes = new java.util.HashMap<>();
    
    // Internal GUI particle system for 3D preview
    private static class GuiParticle {
        double x, y, z;
        double vx, vy, vz;
        int age;
        int lifetime;
        float[] color;
        float scale;
        
        GuiParticle(double x, double y, double z, double vx, double vy, double vz, int lifetime, float[] color) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            this.age = 0;
            this.lifetime = lifetime;
            this.color = color;
            this.scale = 0.5f + (float)Math.random() * 0.5f;
        }
        
        void tick() {
            age++;
            x += vx;
            y += vy;
            z += vz;
            
            // Gravity effect - glow up then fall
            if (age < lifetime * 0.3) {
                vy += 0.005;
            } else {
                vy -= 0.01;
            }
            
            // Slow down horizontal movement
            vx *= 0.95;
            vz *= 0.95;
        }
        
        boolean isDead() {
            return age >= lifetime;
        }
    }
    
    private final java.util.List<GuiParticle> guiParticles = new java.util.ArrayList<>();
    
    @Override
    public void init() {
        // Initialize player avatar rendering
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // All coordinates are in GUI-scaled space - no scale transformation needed
        // Everything will naturally scale with the user's GUI scale setting
        
        // Calculate scissor in actual window pixels (physical pixels)
        // Panel bounds (startX, startY, width, height) are in GUI-scaled coordinates
        // Scissor needs to be in actual window pixels, so convert GUI-scaled to actual pixels
        double actualGuiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        int scissorX = (int)(startX * actualGuiScale);
        int scissorWidth = (int)(width * actualGuiScale);
        int scissorY = windowHeight - (int)((startY + height) * actualGuiScale);
        int scissorHeight = (int)(height * actualGuiScale);
        
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        
        // Render background
        GuiComponent.fill(poseStack, startX, startY, endX, endY, 0x80000000);
        
        // Render title
        String title = "Player Avatar";
        int titleWidth = mc.font.width(title);
        mc.font.draw(poseStack, title, 
            startX + (width - titleWidth) / 2, 
            startY + PADDING, 
            0xFFFFFF);
        
        // Render rotation and zoom hints
        String hint = "Click and drag to rotate";
        int hintWidth = mc.font.width(hint);
        mc.font.draw(poseStack, hint,
            startX + (width - hintWidth) / 2,
            startY + PADDING + 12,
            0xAAAAAA);
        
        // Show zoom hint
        String zoomHint = "Ctrl + Scroll to zoom";
        int zoomHintWidth = mc.font.width(zoomHint);
        mc.font.draw(poseStack, zoomHint,
            startX + (width - zoomHintWidth) / 2,
            startY + PADDING + 24,
            0x888888);
        
        // Show current zoom level if not 1.0
        if (Math.abs(playerZoom - 1.0f) > 0.01f) {
            String zoomLevel = String.format("%.0f%%", playerZoom * 100.0f);
            int zoomLevelWidth = mc.font.width(zoomLevel);
            mc.font.draw(poseStack, zoomLevel,
                startX + (width - zoomLevelWidth) / 2,
                startY + PADDING + 36,
                0x00FF00);
        }
        
        // Render play/pause toggle button (small icon button, bottom right of panel)
        int buttonSize = 16; // Small square button
        int buttonX = endX - buttonSize - 5;
        int buttonY = endY - buttonSize - 5;
        
        // Check if mouse is hovering over button
        boolean buttonHovered = mouseX >= buttonX && mouseX <= buttonX + buttonSize &&
                               mouseY >= buttonY && mouseY <= buttonY + buttonSize;
        
        // Render button background (subtle, semi-transparent)
        int buttonColor = buttonHovered ? 0xAA555555 : 0xAA333333;
        GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + buttonSize, buttonY + buttonSize, buttonColor);
        
        // Render button border (thin)
        GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + buttonSize, buttonY + 1, 0xFF666666); // Top
        GuiComponent.fill(poseStack, buttonX, buttonY + buttonSize - 1, buttonX + buttonSize, buttonY + buttonSize, 0xFF666666); // Bottom
        GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + 1, buttonY + buttonSize, 0xFF666666); // Left
        GuiComponent.fill(poseStack, buttonX + buttonSize - 1, buttonY, buttonX + buttonSize, buttonY + buttonSize, 0xFF666666); // Right
        
        // Render play/pause icon
        int centerX = buttonX + buttonSize / 2;
        int centerY = buttonY + buttonSize / 2;
        
        if (isWalking) {
            // Render pause icon (two vertical bars)
            int barWidth = 2;
            int barHeight = 8;
            int barSpacing = 2;
            // Left bar
            GuiComponent.fill(poseStack, 
                centerX - barSpacing / 2 - barWidth, centerY - barHeight / 2,
                centerX - barSpacing / 2, centerY + barHeight / 2,
                0xFFFFFFFF);
            // Right bar
            GuiComponent.fill(poseStack,
                centerX + barSpacing / 2, centerY - barHeight / 2,
                centerX + barSpacing / 2 + barWidth, centerY + barHeight / 2,
                0xFFFFFFFF);
        } else {
            // Render play icon (triangle pointing right)
            int triangleSize = 8;
            // Draw triangle using filled rectangles (simple approach)
            // Triangle points: left point, top-right point, bottom-right point
            for (int y = -triangleSize / 2; y <= triangleSize / 2; y++) {
                int width = triangleSize / 2 - Math.abs(y);
                GuiComponent.fill(poseStack,
                    centerX - triangleSize / 4, centerY + y,
                    centerX - triangleSize / 4 + width, centerY + y + 1,
                    0xFFFFFFFF);
            }
        }
        
        // Get cosmetics to render: equipped cosmetics from slots AND equipped set
        // This includes particle trails which aren't in slots
        Map<Integer, String> equippedBySlot = state.getEquippedCosmeticsBySlot();
        Set<String> equippedSet = state.getEquippedCosmetics();
        Set<String> cosmeticsToRender = new java.util.HashSet<>();
        
        // Add slot-based cosmetics
        cosmeticsToRender.addAll(equippedBySlot.values());
        
        // Add non-slot cosmetics (like particle trails)
        cosmeticsToRender.addAll(equippedSet);
        
        // If no equipped cosmetics, show selected cosmetic for preview
        if (cosmeticsToRender.isEmpty()) {
            String selectedCosmeticId = state.getSelectedCosmeticId();
            if (selectedCosmeticId != null && !selectedCosmeticId.isEmpty()) {
                cosmeticsToRender.add(selectedCosmeticId);
            }
        }
        
        // Calculate render area (below title) - moved up by 25 pixels total (15 + 10 more)
        int renderAreaY = startY + PADDING - 5; // Moved up 10 more pixels (from 5 to -5, total 25px up from original)
        int renderAreaHeight = height - (PADDING * 2 - 5); // Adjusted to match
        int renderAreaX = startX + PADDING;
        int renderAreaWidth = width - PADDING * 2;
        
        // Always render player model (with or without cosmetics)
        // Try 3D rendering first, fallback to enhanced display
        boolean rendered3D = false;
        if (mc.player != null && mc.level != null) {
            try {
                render3DPlayerModel(poseStack, renderAreaX, renderAreaY, renderAreaWidth, renderAreaHeight, 
                    cosmeticsToRender, partialTick);
                rendered3D = true;
            } catch (Exception e) {
                // Fallback to enhanced display
                BuildScape.getLogger().debug("3D player rendering not available, using fallback: " + e.getMessage());
            }
        } else {
            // Offline mode - render with "Kingodogo" skin
            try {
                renderOfflinePlayer(poseStack, renderAreaX, renderAreaY, renderAreaWidth, renderAreaHeight, 
                    cosmeticsToRender, partialTick);
                rendered3D = true;
            } catch (Exception e) {
                BuildScape.getLogger().debug("Offline player rendering failed: " + e.getMessage());
            }
        }
        
        // If 3D rendering didn't work, use enhanced display
        if (!rendered3D) {
            renderEnhancedDisplay(poseStack, renderAreaX, renderAreaY, renderAreaWidth, renderAreaHeight, 
                cosmeticsToRender);
        }
        
        // Render cosmetic slots at the bottom (force on top of the player model)
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        poseStack.pushPose();
        poseStack.translate(0, 0, 400); // ensure slots + labels draw above the 3D model
        renderCosmeticSlots(poseStack, renderAreaX, renderAreaY, renderAreaWidth, renderAreaHeight, mouseX, mouseY);
        poseStack.popPose();
        RenderSystem.depthMask(true);
        
        RenderSystem.disableScissor();
        
        // Tooltips are now rendered at tab level to ensure they're on top of everything
        // Don't render here to avoid being hidden behind other panels
    }
    
    /**
     * Render 3D player model using EntityRenderDispatcher.
     * Shows the actual player's skin, or "Kingodogo" skin if offline.
     */
    private void render3DPlayerModel(PoseStack poseStack, int x, int y, int width, int height, 
                                     Set<String> equippedCosmetics, float partialTick) {
        AbstractClientPlayer player;
        
        // If offline, create a dummy player with "Kingodogo" skin
        if (mc.player == null || mc.level == null) {
            player = createOfflinePlayer();
            if (player == null) {
                renderEnhancedDisplay(poseStack, x, y, width, height, equippedCosmetics);
                return;
            }
        } else {
            player = mc.player;
        }
        
        // Reset per-frame keyboard walking flag
        keyboardWalking = false;
        
        // Calculate center and scale
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        
            // Save player's original equipment
        ItemStack originalHelmet = player.getItemBySlot(EquipmentSlot.HEAD).copy();
        ItemStack originalChestplate = player.getItemBySlot(EquipmentSlot.CHEST).copy();
        ItemStack originalLeggings = player.getItemBySlot(EquipmentSlot.LEGS).copy();
        ItemStack originalBoots = player.getItemBySlot(EquipmentSlot.FEET).copy();
        ItemStack originalMainHand = player.getItemBySlot(EquipmentSlot.MAINHAND).copy();
        
        // Save original silent state
        boolean wasSilent = player.isSilent();
        
        try {
            // Suppress ALL sounds during entire rendering process
            player.setSilent(true);
            
            // Apply cosmetic equipment (only cosmetic armor, no vanilla armor)
            // We NO LONGER clear all vanilla armor here, so any slots without 
            // cosmetics will show the player's actual vanilla equipment.
            applyCosmeticEquipment(player, equippedCosmetics);
            
            // Set up rendering
            EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
            PlayerRenderer playerRenderer = (PlayerRenderer) dispatcher.getRenderer(player);
            
            if (playerRenderer == null) {
                return;
            }
            
            // Calculate scale to fit in panel
            // Panel dimensions (width, height) are in GUI-scaled coordinates
            // They already scale with GUI scale, so we need to scale the multiplier inversely
            // to maintain consistent player size relative to panel at all GUI scales
            float playerHeight = 1.8f; // Standard player height
            float baseScale = Math.min(width, height) * 0.4f / playerHeight;
            baseScale = Math.max(0.1f, Math.min(1.0f, baseScale)); // Clamp base scale
            // Apply zoom multiplier
            float scale = baseScale * playerZoom;
            
            // Get actual GUI scale to adjust rendering scale
            // If player is 200% at GUI scale 2, scale the multiplier inversely with GUI scale
            // to maintain consistent appearance at all GUI scales
            double actualGuiScale = mc.getWindow().getGuiScale();
            double guiScaleMultiplier = 35.0 * (2.0 / actualGuiScale); // Scale inversely with GUI scale
            
            // Set up pose stack for rendering
            poseStack.pushPose();
            
            // Translate to center of render area
            // For GUI rendering, we need to account for the coordinate system
            poseStack.translate(centerX, centerY + height * 0.2f, 100.0f); // Z offset for depth
            
            // Translate player up so rotation and zoom center is at body center (not feet)
            // Player height is 1.8 blocks, body center is at ~0.9 blocks (half height)
            poseStack.translate(0.0, 0.9, 0.0);
            
            // Apply 3D sphere rotation around body center
            // Order: Yaw (horizontal) first, then Pitch (vertical)
            // This creates smooth rotation in any direction like a sphere
            poseStack.mulPose(Vector3f.YP.rotationDegrees(rotationYaw)); // Horizontal rotation (left/right)
            poseStack.mulPose(Vector3f.XP.rotationDegrees(rotationPitch)); // Vertical rotation (up/down)
            
            // Apply scale AFTER rotation (invert Y to match GUI coordinate system)
            // Scale needs to be larger for GUI rendering, multiplied by zoom
            // Zoom is applied here so it scales around the body center
            // Scale multiplier is adjusted inversely with GUI scale to maintain consistent appearance
            float guiScale = scale * (float)guiScaleMultiplier * playerZoom; // Apply zoom to scale
            poseStack.scale(guiScale, -guiScale, guiScale);
            
            // Translate back down to position player correctly
            poseStack.translate(0.0, -0.9, 0.0);
            
            // Save player state (only if not walking, to preserve animation state)
            // CRITICAL: Save position BEFORE any modifications to prevent teleport bugs
            float prevX = (float) player.getX();
            float prevY = (float) player.getY();
            float prevZ = (float) player.getZ();
            double prevXOld = player.xOld;
            double prevYOld = player.yOld;
            double prevZOld = player.zOld;
            
            // If walking, don't save old position - we're managing it ourselves
            if (isWalking) {
                prevXOld = player.xOld; // Keep our managed old position
                prevYOld = player.yOld;
                prevZOld = player.zOld;
            }
            float prevYRot = player.getYRot();
            float prevXRot = player.getXRot();
            float prevYBodyRot = player.yBodyRot;
            float prevYHeadRot = player.yHeadRot;
            float prevYRotO = player.yRotO;
            float prevXRotO = player.xRotO;
            float prevYBodyRotO = player.yBodyRotO;
            float prevYHeadRotO = player.yHeadRotO;
            int prevTickCount = player.tickCount;
            int originalTickCount = prevTickCount; // Save for restoration
            // Save animation state for restoration
            int prevHurtTime = 0;
            int prevDeathTime = 0;
            boolean prevSprinting = false;
            boolean prevShiftKeyDown = false;
            float prevAnimationSpeed = 0.0f;
            float prevAnimationSpeedOld = 0.0f;
            float prevAnimationPosition = 0.0f;
            int prevSwingTime = 0;
            float prevAttackAnim = 0.0f;
            float prevOAttackAnim = 0.0f;
            float prevSpeed = 0.0f;
            if (player instanceof net.minecraft.world.entity.LivingEntity) {
                net.minecraft.world.entity.LivingEntity living = (net.minecraft.world.entity.LivingEntity) player;
                prevHurtTime = living.hurtTime;
                prevDeathTime = living.deathTime;
                prevSprinting = living.isSprinting();
                prevShiftKeyDown = living.isShiftKeyDown();
                prevAnimationSpeed = living.animationSpeed;
                prevAnimationSpeedOld = living.animationSpeedOld;
                prevAnimationPosition = living.animationPosition;
                prevSwingTime = living.swingTime;
                prevAttackAnim = living.attackAnim;
                prevOAttackAnim = living.oAttackAnim;
                prevSpeed = living.getSpeed();
            }
            
            // Handle animations based on walking state
            if (isWalking || keyboardWalking) {
                // Calculate movement for walking animation
                long currentTime = System.currentTimeMillis();
                
                // Calculate frame delta time
                if (lastFrameTime == 0) {
                    lastFrameTime = currentTime;
                }
                double deltaTime = (currentTime - lastFrameTime) / 1000.0; // Convert to seconds
                lastFrameTime = currentTime;
                
                // Walking speed: 0.2 blocks per second (normal walking speed)
                float walkSpeed = 0.2f;
                
                // Update direction based on keyboard (WASD) or forward default when toggled
                double dirX = 0.0;
                double dirZ = 1.0; // default forward
                keyboardWalking = updateKeyboardWalkDirection();
                if (keyboardWalking) {
                    dirX = keyboardDirX;
                    dirZ = keyboardDirZ;
                }
                
                // Normalize direction to avoid faster diagonal movement
                double len = Math.sqrt(dirX * dirX + dirZ * dirZ);
                if (len > 0.0) {
                    dirX /= len;
                    dirZ /= len;
                } else {
                    dirX = 0.0;
                    dirZ = 0.0;
                }
                
                // OLD position is from LAST frame (stored in walkXOld/walkZOld)
                double oldX = walkXOld;
                double oldZ = walkZOld;
                
                // Move by delta time in chosen direction
                walkX += deltaTime * walkSpeed * dirX;
                walkZ += deltaTime * walkSpeed * dirZ;
                walkXOld = walkX;
                walkZOld = walkZ;
                
                // Calculate movement delta - this is critical for leg animation
                double deltaX = walkX - oldX;
                double deltaZ = walkZ - oldZ;
                double deltaMag = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                
                // Ensure we always have movement (Minecraft needs movement to animate legs)
                // Minimum movement of 0.01 blocks per frame ensures animation triggers
                if (deltaMag < 0.01) {
                    double adjust = 0.01 / (deltaMag == 0.0 ? 1.0 : deltaMag);
                    deltaX *= adjust;
                    deltaZ *= adjust;
                    walkX = oldX + deltaX;
                    walkZ = oldZ + deltaZ;
                }
                
                // Set position (relative to pose stack origin)
                player.setPos(walkX, 0, walkZ);
                
                // Set OLD position - THIS IS WHAT TRIGGERS WALKING ANIMATION
                // Minecraft compares current position to old position to determine if moving
                player.xOld = oldX;
                player.yOld = 0.0;
                player.zOld = oldZ;
                
                // Keep cape anchored to body (prevents floaty cape when still)
                player.xCloak = player.getX();
                player.yCloak = player.getY();
                player.zCloak = player.getZ();
                player.xCloakO = player.xCloak;
                player.yCloakO = player.yCloak;
                player.zCloakO = player.zCloak;
                
                // Update tick count for animation timing
                player.tickCount++;
                
                // Reset rotations (all rotation handled in pose stack)
                player.setYRot(0.0f);
                player.setXRot(0.0f);
                player.yBodyRot = 0.0f;
                player.yHeadRot = 0.0f;
                player.yRotO = 0.0f;
                player.xRotO = 0.0f;
                player.yBodyRotO = 0.0f;
                player.yHeadRotO = 0.0f;
                
                if (player instanceof net.minecraft.world.entity.LivingEntity) {
                    net.minecraft.world.entity.LivingEntity living = (net.minecraft.world.entity.LivingEntity) player;
                    living.setSprinting(false);
                    living.setShiftKeyDown(false);
                    
                    // Animation speed - controls how fast legs move
                    // 0.5f is normal walking speed
                    float animSpeed = 0.5f;
                    living.animationSpeed = animSpeed;
                    living.animationSpeedOld = animSpeed;
                    
                    // CRITICAL: Update animation position based on movement
                    // This is what actually makes the legs animate
                    // animationPosition controls the leg swing cycle (0.0 to 1.0)
                    float movementAmount = (float)deltaX * 10.0f; // Scale movement to animation
                    living.animationPosition += movementAmount;
                    // Keep animation position in valid range (0.0 to 1.0)
                    if (living.animationPosition > 1.0f) {
                        living.animationPosition -= 1.0f;
                    }
                    
                    // Prevent other animations
                    living.hurtTime = 0;
                    living.deathTime = 0;
                    living.swingTime = 0;
                    living.attackAnim = 0.0f;
                    living.oAttackAnim = 0.0f;
                    
                    // SET DELTA MOVEMENT - This makes legs swing
                    // The delta movement tells Minecraft the entity is moving
                    living.setDeltaMovement(deltaX, 0, 0);
                    living.setSpeed(animSpeed);
                }
                
                // Save current position as old for NEXT frame
                walkXOld = walkX;
            } else {
                // Static pose - no animations
                // Position player at origin for rendering (relative to pose stack)
                player.setPos(0, 0, 0);
                player.xOld = 0.0;
                player.yOld = 0.0;
                player.zOld = 0.0;
                walkX = 0.0;
                walkZ = 0.0;
                walkXOld = 0.0;
                walkZOld = 0.0;
                player.xCloak = player.getX();
                player.yCloak = player.getY();
                player.zCloak = player.getZ();
                player.xCloakO = player.xCloak;
                player.yCloakO = player.yCloak;
                player.zCloakO = player.zCloak;
                // Keep tickCount at 0 to prevent item updates (like elytra rustle)
                player.tickCount = 0;
                
                // Set rotation to 0 - all rotation is handled in pose stack for sphere rotation
                player.setYRot(0.0f);
                player.setXRot(0.0f);
                player.yBodyRot = 0.0f;
                player.yHeadRot = 0.0f;
                player.yRotO = 0.0f;
                player.xRotO = 0.0f;
                player.yBodyRotO = 0.0f;
                player.yHeadRotO = 0.0f;
                // Static pose - no animations
                player.tickCount = 0;
                player.setDeltaMovement(0, 0, 0);
                
                if (player instanceof net.minecraft.world.entity.LivingEntity) {
                    net.minecraft.world.entity.LivingEntity living = (net.minecraft.world.entity.LivingEntity) player;
                    // Reset hurt and death timers to prevent hurt/dying animations
                    living.hurtTime = 0;
                    living.deathTime = 0;
                    // Set to idle pose - prevent walking/running animations
                    living.setSprinting(false);
                    living.setShiftKeyDown(false);
                    // Prevent entity from updating animations
                    living.animationSpeed = 0.0f;
                    living.animationSpeedOld = 0.0f;
                    living.animationPosition = 0.0f;
                    // Prevent attack/swing animations
                    living.swingTime = 0;
                    living.attackAnim = 0.0f;
                    living.oAttackAnim = 0.0f;
                    // Freeze all movement
                    living.setDeltaMovement(0, 0, 0);
                    living.setSpeed(0.0f);
                }
            }
            
            // Apply roll rotation to pose stack for full 3D rotation
            // This allows rotation in all directions
            
            // Set up lighting (full brightness for GUI)
            int lightLevel = 15728880; // Full brightness (15 sky, 15 block)
            
            // Get buffer source
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            
            // Enable depth test and setup lighting for 3D rendering
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            // Setup lighting for GUI 3D rendering
            Vector3f light1 = new Vector3f(0.2f, 1.0f, -0.7f);
            light1.normalize();
            Vector3f light2 = new Vector3f(-0.2f, 1.0f, 0.7f);
            light2.normalize();
            RenderSystem.setupGui3DDiffuseLighting(light1, light2);
            
            // Render player (rotatable via mouse drag - 3D sphere rotation)
            try {
                // The entity renderer expects world coordinates, but we're in GUI space
                // All rotations are applied in the pose stack, so pass 0.0f to renderer
                // The pose stack handles all rotation (yaw and pitch) for sphere rotation
                playerRenderer.render(player, 0.0f, partialTick, poseStack, bufferSource, lightLevel);
                bufferSource.endBatch(); // Flush buffers
                
                // Render particle trails around player in GUI
                renderParticleTrails(poseStack, equippedCosmetics, player, partialTick);
            } catch (Exception e) {
                BuildScape.getLogger().error("Error rendering player: " + e.getMessage());
                // Don't print stack trace in production, just log
            } finally {
                RenderSystem.disableDepthTest();
                RenderSystem.disableBlend();
            }
            
            // Restore player state
            // CRITICAL: Always restore position to prevent teleport bugs
            // Only restore if we're using the actual player (not a dummy)
            if (player == mc.player) {
                // Always restore position to prevent teleporting
                player.setPos(prevX, prevY, prevZ);
                if (!isWalking) {
                    // When not walking, restore old position too
                    player.xOld = prevXOld;
                    player.yOld = prevYOld;
                    player.zOld = prevZOld;
                }
                // When walking, old position is managed by walking system - don't restore it
            }
            player.setYRot(prevYRot);
            player.setXRot(prevXRot);
            player.yBodyRot = prevYBodyRot;
            player.yHeadRot = prevYHeadRot;
            player.yRotO = prevYRotO;
            player.xRotO = prevXRotO;
            player.yBodyRotO = prevYBodyRotO;
            player.yHeadRotO = prevYHeadRotO;
            player.tickCount = originalTickCount; // Restore original tick count
            // Restore animation state
            if (player instanceof net.minecraft.world.entity.LivingEntity) {
                net.minecraft.world.entity.LivingEntity living = (net.minecraft.world.entity.LivingEntity) player;
                living.hurtTime = prevHurtTime;
                living.deathTime = prevDeathTime;
                living.setSprinting(prevSprinting);
                living.setShiftKeyDown(prevShiftKeyDown);
                living.animationSpeed = prevAnimationSpeed;
                living.animationSpeedOld = prevAnimationSpeedOld;
                living.animationPosition = prevAnimationPosition;
                living.swingTime = prevSwingTime;
                living.attackAnim = prevAttackAnim;
                living.oAttackAnim = prevOAttackAnim;
                living.setSpeed(prevSpeed);
            }
            
            poseStack.popPose();
            
        } finally {
            // Always restore original equipment (only if it's a real player)
            // Keep silent during restoration to prevent sounds
            if (player == mc.player) {
                setItemSlotSilent(player, EquipmentSlot.HEAD, originalHelmet);
                setItemSlotSilent(player, EquipmentSlot.CHEST, originalChestplate);
                setItemSlotSilent(player, EquipmentSlot.LEGS, originalLeggings);
                setItemSlotSilent(player, EquipmentSlot.FEET, originalBoots);
                setItemSlotSilent(player, EquipmentSlot.MAINHAND, originalMainHand);
            }
            
            // Restore original silent state AFTER everything is done
            player.setSilent(wasSilent);
        }
    }
    
    /**
     * Apply cosmetic equipment to player silently (without triggering sounds).
     * Only applies cosmetic armor/items, matching them to the correct slots.
     */
    private void applyCosmeticEquipment(AbstractClientPlayer player, Set<String> cosmeticIds) {
        if (cosmeticIds == null || cosmeticIds.isEmpty()) {
            return;
        }
        
        // Apply cosmetics to correct equipment slots based on item type
        // Use direct inventory access to avoid triggering sounds
        for (String cosmeticId : cosmeticIds) {
            ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
            if (stack != null && !stack.isEmpty()) {
                EquipmentSlot slot = getSlotForCosmetic(stack);
                if (slot != null) {
                    setItemSlotSilent(player, slot, stack);
                }
            }
        }
    }
    
    /**
     * Set item slot silently without triggering sounds or events.
     * Directly modifies the inventory array to bypass sound triggers.
     */
    private void setItemSlotSilent(AbstractClientPlayer player, EquipmentSlot slot, ItemStack stack) {
        if (player == null) return;
        
        // Use direct inventory access to avoid triggering sounds
        // Access the equipment slots directly without going through setItemSlot
        net.minecraft.world.entity.player.Inventory inventory = player.getInventory();
        
        switch (slot) {
            case HEAD:
                inventory.armor.set(3, stack); // Head is index 3 in armor list
                break;
            case CHEST:
                inventory.armor.set(2, stack); // Chest is index 2 in armor list
                break;
            case LEGS:
                inventory.armor.set(1, stack); // Legs is index 1 in armor list
                break;
            case FEET:
                inventory.armor.set(0, stack); // Feet is index 0 in armor list
                break;
            case MAINHAND:
                // For main hand, set the selected hotbar slot
                inventory.setItem(inventory.selected, stack);
                break;
            default:
                break;
        }
    }
    
    /**
     * Get the correct equipment slot for a cosmetic item.
     */
    private EquipmentSlot getSlotForCosmetic(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        
        net.minecraft.world.item.Item item = stack.getItem();
        
        // Check for elytra (goes in chest slot)
        if (item instanceof net.minecraft.world.item.ElytraItem) {
            return EquipmentSlot.CHEST;
        }
        
        // Check for armor items
        if (item instanceof net.minecraft.world.item.ArmorItem) {
            net.minecraft.world.item.ArmorItem armor = (net.minecraft.world.item.ArmorItem) item;
            return armor.getSlot();
        }
        
        // Weapons go in main hand
        if (item instanceof net.minecraft.world.item.SwordItem || 
            item instanceof net.minecraft.world.item.BowItem ||
            item instanceof net.minecraft.world.item.TridentItem ||
            item instanceof net.minecraft.world.item.AxeItem) {
            return EquipmentSlot.MAINHAND;
        }
        
        return null;
    }
    
    
    /**
     * Render enhanced display when 3D rendering is not available.
     * Shows player representation with equipped cosmetics.
     */
    private void renderEnhancedDisplay(PoseStack poseStack, int x, int y, int width, int height, 
                                     Set<String> equippedCosmetics) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        
        // Always show player representation
        // Render player icon/silhouette
        String playerIcon = "⛹"; // Player emoji as placeholder
        int iconWidth = mc.font.width(playerIcon);
        mc.font.draw(poseStack, playerIcon, 
            centerX - iconWidth / 2, 
            centerY - 40, 
            0xFFFFFF);
        
        // Render player name if available
        if (mc.player != null) {
            String playerName = mc.player.getName().getString();
            if (playerName.length() > 15) {
                playerName = playerName.substring(0, 12) + "...";
            }
            int nameWidth = mc.font.width(playerName);
            mc.font.draw(poseStack, playerName, 
                centerX - nameWidth / 2, 
                centerY - 20, 
                0xCCCCCC);
        }
        
        if (equippedCosmetics.isEmpty()) {
            // Show message when no cosmetics
            String noCosmetics = "No cosmetics equipped";
            int textWidth = mc.font.width(noCosmetics);
            mc.font.draw(poseStack, noCosmetics, 
                centerX - textWidth / 2, 
                centerY + 5, 
                0xAAAAAA);
            
            String hint = "Player skin visible";
            int hintWidth = mc.font.width(hint);
            mc.font.draw(poseStack, hint, 
                centerX - hintWidth / 2, 
                centerY + 20, 
                0x888888);
        } else {
            // Show equipped cosmetics below player
            int itemY = centerY + 10;
            int maxItems = Math.min(equippedCosmetics.size(), 5); // Show up to 5 items
            int itemIndex = 0;
            
            String equippedLabel = "Equipped:";
            int labelWidth = mc.font.width(equippedLabel);
            mc.font.draw(poseStack, equippedLabel, 
                centerX - labelWidth / 2, 
                itemY, 
                0xCCCCCC);
            itemY += 15;
            
            for (String cosmeticId : equippedCosmetics) {
                if (itemIndex >= maxItems) break;
                
                // Check if it's a particle trail
                boolean isParticleTrail = CosmeticManager.getInstance().isParticleTrail(cosmeticId);
                
                ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
                if (stack != null && !stack.isEmpty()) {
                    // Render item icon centered
                    int itemX = centerX - 8;
                    mc.getItemRenderer().renderGuiItem(stack, itemX, itemY);
                    mc.getItemRenderer().renderGuiItemDecorations(mc.font, stack, itemX, itemY);
                    
                    // Show particle trail indicator
                    if (isParticleTrail) {
                        String trailText = "✨";
                        mc.font.draw(poseStack, trailText, 
                            itemX + 18, 
                            itemY + 2, 
                            0xFFFF00); // Yellow sparkle
                    }
                    
                    itemY += 20;
                    itemIndex++;
                } else if (isParticleTrail) {
                    // Show particle trail even if no ItemStack (just text)
                    String trailName = cosmeticId;
                    if (cosmeticId.startsWith("buildscape:cosmatics/")) {
                        trailName = cosmeticId.substring(cosmeticId.lastIndexOf("/") + 1);
                    } else {
                        trailName = trailName.replace("particle:", "");
                    }
                    trailName = trailName.replace("_", " ");
                    int nameWidth = mc.font.width(trailName);
                    mc.font.draw(poseStack, trailName, 
                        centerX - nameWidth / 2, 
                        itemY, 
                        0xFFFF00); // Yellow
                    itemY += 15;
                    itemIndex++;
                }
            }
        }
    }
    
    /**
     * Create an offline player entity with "Kingodogo" UUID for skin rendering.
     * This allows the skin to be loaded from Mojang's servers.
     */
    private AbstractClientPlayer createOfflinePlayer() {
        if (mc.level == null) {
            return null;
        }
        
        // Use the actual "Kingodogo" UUID if known, otherwise generate from username
        // For offline mode, we need a level to create the entity
        try {
            GameProfile gameProfile = new GameProfile(KINGODOGO_UUID, KINGODOGO_USERNAME);
            
            // Create a dummy client player entity
            AbstractClientPlayer dummyPlayer = new AbstractClientPlayer(mc.level, gameProfile) {
                @Override
                public boolean isSpectator() {
                    return false;
                }
                
                @Override
                public boolean isCreative() {
                    return false;
                }
            };
            
            // Set level reference for rendering
            dummyPlayer.level = mc.level;
            
            // Initialize player state to prevent glitches
            dummyPlayer.setPos(0, 0, 0);
            dummyPlayer.setYRot(0.0f); // 0° faces camera
            dummyPlayer.setXRot(0.0f);
            dummyPlayer.yBodyRot = 0.0f;
            dummyPlayer.yHeadRot = 0.0f;
            dummyPlayer.yRotO = 0.0f; // Previous rotation (prevents interpolation)
            dummyPlayer.xRotO = 0.0f;
            dummyPlayer.yBodyRotO = 0.0f;
            dummyPlayer.yHeadRotO = 0.0f;
            dummyPlayer.tickCount = 0;
            dummyPlayer.setDeltaMovement(0, 0, 0);
            // Disable sounds for offline player
            dummyPlayer.setSilent(true);
            // Prevent animations for offline player (same as online player)
            if (dummyPlayer instanceof net.minecraft.world.entity.LivingEntity) {
                net.minecraft.world.entity.LivingEntity living = (net.minecraft.world.entity.LivingEntity) dummyPlayer;
                living.hurtTime = 0;
                living.deathTime = 0;
                living.setSprinting(false);
                living.setShiftKeyDown(false);
                living.animationSpeed = 0.0f;
                living.animationSpeedOld = 0.0f;
                living.animationPosition = 0.0f;
                living.swingTime = 0;
                living.attackAnim = 0.0f;
                living.oAttackAnim = 0.0f;
                living.setDeltaMovement(0, 0, 0);
                living.setSpeed(0.0f);
            }
            
            return dummyPlayer;
        } catch (Exception e) {
            BuildScape.getLogger().error("Failed to create offline player: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Render offline player with "Kingodogo" skin.
     * Creates a dummy player entity with "Kingodogo" UUID for skin rendering.
     */
    private void renderOfflinePlayer(PoseStack poseStack, int x, int y, int width, int height, 
                                     Set<String> equippedCosmetics, float partialTick) {
        // Try to create and render offline player
        AbstractClientPlayer offlinePlayer = createOfflinePlayer();
        if (offlinePlayer != null && mc.level != null) {
            // Use the same rendering logic as online player
            try {
                render3DPlayerModel(poseStack, x, y, width, height, equippedCosmetics, partialTick);
                return;
            } catch (Exception e) {
                BuildScape.getLogger().debug("Failed to render offline player 3D: " + e.getMessage());
            }
        }
        
        // Fallback to enhanced display
        renderEnhancedDisplay(poseStack, x, y, width, height, equippedCosmetics);
        
        // Show offline indicator
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        String offlineText = "Offline Mode - " + KINGODOGO_USERNAME;
        int textWidth = mc.font.width(offlineText);
        mc.font.draw(poseStack, offlineText, 
            centerX - textWidth / 2, 
            centerY + 30, 
            0xFF8800);
        
        // Show equipped cosmetics if any
        if (equippedCosmetics != null && !equippedCosmetics.isEmpty()) {
            int itemY = centerY + 25;
            int maxItems = Math.min(equippedCosmetics.size(), 5);
            int itemIndex = 0;
            
            String equippedLabel = "Equipped:";
            int labelWidth = mc.font.width(equippedLabel);
            mc.font.draw(poseStack, equippedLabel, 
                centerX - labelWidth / 2, 
                itemY, 
                0xCCCCCC);
            itemY += 15;
            
            for (String cosmeticId : equippedCosmetics) {
                if (itemIndex >= maxItems) break;
                
                // Check if it's a particle trail
                boolean isParticleTrail = CosmeticManager.getInstance().isParticleTrail(cosmeticId);
                
                ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
                if (stack != null && !stack.isEmpty()) {
                    int itemX = centerX - 8;
                    mc.getItemRenderer().renderGuiItem(stack, itemX, itemY);
                    mc.getItemRenderer().renderGuiItemDecorations(mc.font, stack, itemX, itemY);
                    
                    // Show particle trail indicator
                    if (isParticleTrail) {
                        String trailText = "✨";
                        mc.font.draw(poseStack, trailText, 
                            itemX + 18, 
                            itemY + 2, 
                            0xFFFF00); // Yellow sparkle
                    }
                    
                    itemY += 20;
                    itemIndex++;
                } else if (isParticleTrail) {
                    // Show particle trail even if no ItemStack (just text)
                    String trailName = cosmeticId;
                    if (cosmeticId.startsWith("buildscape:cosmatics/")) {
                        trailName = cosmeticId.substring(cosmeticId.lastIndexOf("/") + 1);
                    } else {
                        trailName = trailName.replace("particle:", "");
                    }
                    trailName = trailName.replace("_", " ");
                    int nameWidth = mc.font.width(trailName);
                    mc.font.draw(poseStack, trailName, 
                        centerX - nameWidth / 2, 
                        itemY, 
                        0xFFFF00); // Yellow
                    itemY += 15;
                    itemIndex++;
                }
            }
        } else {
            String noCosmetics = "No cosmetics equipped";
            int textWidth2 = mc.font.width(noCosmetics);
            mc.font.draw(poseStack, noCosmetics, 
                centerX - textWidth2 / 2, 
                centerY + 25, 
                0xAAAAAA);
        }
    }
    
    @Override
    protected boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        // Scale mouse coordinates to match the scale transformation
        double actualGuiScale = mc.getWindow().getGuiScale();
        double scaleFactor = FIXED_GUI_SCALE / actualGuiScale;
        double scaledMouseX = mouseX / scaleFactor;
        double scaledMouseY = mouseY / scaleFactor;
        
        // Check if clicking on play/pause toggle button
        int buttonSize = 16;
        int buttonX = endX - buttonSize - 5;
        int buttonY = endY - buttonSize - 5;
        
        if (scaledMouseX >= buttonX && scaledMouseX <= buttonX + buttonSize &&
            scaledMouseY >= buttonY && scaledMouseY <= buttonY + buttonSize) {
            // Toggle walking animation
            isWalking = !isWalking;
            if (isWalking) {
                // Start walking
                lastFrameTime = System.currentTimeMillis();
                walkX = 0.0;
                walkZ = 0.0;
                walkXOld = 0.0;
                walkZOld = 0.0;
            } else {
                // Stop walking
                walkX = 0.0;
                walkZ = 0.0;
                walkXOld = 0.0;
                walkZOld = 0.0;
                lastFrameTime = 0;
            }
            return true;
        }
        
        // Check if clicking on cosmetic slots
        SupportersTabState state = SupportersTabState.getInstance();
        int[] slots = {
            SupportersTabState.SLOT_HEAD,
            SupportersTabState.SLOT_CHEST,
            SupportersTabState.SLOT_LEGS,
            SupportersTabState.SLOT_FEET,
            SupportersTabState.SLOT_WINGS,
            SupportersTabState.SLOT_TRAIL
        };
        
        int slotSize = 22;
        int spacing = 4;
        
        // Use the same render area calculations as in render()
        int renderAreaY = startY + PADDING + 20;
        int renderAreaHeight = height - (PADDING * 2 + 20);
        int renderAreaX = startX + PADDING;
        int renderAreaWidth = width - PADDING * 2;
        
        int totalWidth = (slotSize * slots.length) + (spacing * (slots.length - 1));
        int slotStartX = renderAreaX + (renderAreaWidth - totalWidth) / 2;
        int slotY = renderAreaY + renderAreaHeight - slotSize - 10;
        
        for (int i = 0; i < slots.length; i++) {
            int slotX = slotStartX + i * (slotSize + spacing);
            if (scaledMouseX >= slotX && scaledMouseX <= slotX + slotSize &&
                scaledMouseY >= slotY && scaledMouseY <= slotY + slotSize) {
                
                int slotIndex = slots[i];
                String equippedId = state.getEquippedCosmeticInSlot(slotIndex);
                
                if (equippedId != null && !equippedId.isEmpty()) {
                    if (button == 1) { // Right click to unequip
                        state.unequipCosmeticFromSlot(slotIndex);
                    } else { // Left click to select in list
                        state.setSelectedCosmeticId(equippedId);
                    }
                    return true;
                }
            }
        }
        
        // Start dragging to rotate entire player body
        if (button == 0) {
            isDragging = true;
            return true;
        }
        return false;
    }
    
    /**
     * Render cosmetic slots at the bottom of the panel in a line.
     */
    private void renderCosmeticSlots(PoseStack poseStack, int x, int y, int width, int height, int mouseX, int mouseY) {
        SupportersTabState state = SupportersTabState.getInstance();
        CosmeticRegistry registry = CosmeticRegistry.getInstance();
        
        // Define slots to show
        int[] slots = {
            SupportersTabState.SLOT_HEAD,
            SupportersTabState.SLOT_CHEST,
            SupportersTabState.SLOT_LEGS,
            SupportersTabState.SLOT_FEET,
            SupportersTabState.SLOT_WINGS,
            SupportersTabState.SLOT_TRAIL
        };
        
        int slotSize = 22;
        int spacing = 4;
        int totalWidth = (slotSize * slots.length) + (spacing * (slots.length - 1));
        int slotStartX = x + (width - totalWidth) / 2;
        int slotY = y + height - slotSize - 10;
        
        // Scale mouse coordinates to match the scale transformation
        double actualGuiScale = mc.getWindow().getGuiScale();
        double scaleFactor = FIXED_GUI_SCALE / actualGuiScale;
        double scaledMouseX = mouseX / scaleFactor;
        double scaledMouseY = mouseY / scaleFactor;
        
        for (int i = 0; i < slots.length; i++) {
            int slotX = slotStartX + i * (slotSize + spacing);
            int slotIndex = slots[i];
            String equippedId = state.getEquippedCosmeticInSlot(slotIndex);
            
            // Draw slot background
            boolean isHovered = scaledMouseX >= slotX && scaledMouseX <= slotX + slotSize &&
                               scaledMouseY >= slotY && scaledMouseY <= slotY + slotSize;
            
            int bgColor = isHovered ? 0xAA444444 : 0x80000000;
            int borderColor = 0xFF888888;
            
            // If this is the selected cosmetic, highlight it
            if (equippedId != null && equippedId.equals(state.getSelectedCosmeticId())) {
                borderColor = 0xFFFFFF00; // Yellow highlight
            } else if (isHovered) {
                borderColor = 0xFFFFFFFF; // White highlight on hover
            }
            
            GuiComponent.fill(poseStack, slotX, slotY, slotX + slotSize, slotY + slotSize, bgColor);
            
            // Draw border
            GuiComponent.fill(poseStack, slotX, slotY, slotX + slotSize, slotY + 1, borderColor);
            GuiComponent.fill(poseStack, slotX, slotY + slotSize - 1, slotX + slotSize, slotY + slotSize, borderColor);
            GuiComponent.fill(poseStack, slotX, slotY, slotX + 1, slotY + slotSize, borderColor);
            GuiComponent.fill(poseStack, slotX + slotSize - 1, slotY, slotX + slotSize, slotY + slotSize, borderColor);
            
            if (equippedId != null && !equippedId.isEmpty()) {
                ItemStack stack = registry.resolveToItemStack(equippedId);
                if (stack != null && !stack.isEmpty()) {
                    // Render item icon centered in slot
                    int itemX = slotX + (slotSize - 16) / 2;
                    int itemY = slotY + (slotSize - 16) / 2;
                    
                    mc.getItemRenderer().renderGuiItem(stack, itemX, itemY);
                    mc.getItemRenderer().renderGuiItemDecorations(mc.font, stack, itemX, itemY);
                    
                    // Small "✨" indicator if it's a trail
                    if (CosmeticManager.getInstance().isParticleTrail(equippedId)) {
                        poseStack.pushPose();
                        poseStack.translate(0, 0, 200);
                        mc.font.draw(poseStack, "✨", slotX + slotSize - 9, slotY + 1, 0xFFFF00);
                        poseStack.popPose();
                    }
                }
            } else {
                // Draw placeholder icon/text for empty slot
                String label = "";
                switch (slotIndex) {
                    case SupportersTabState.SLOT_HEAD -> label = "H";
                    case SupportersTabState.SLOT_CHEST -> label = "C";
                    case SupportersTabState.SLOT_LEGS -> label = "L";
                    case SupportersTabState.SLOT_FEET -> label = "F";
                    case SupportersTabState.SLOT_WINGS -> label = "W";
                    case SupportersTabState.SLOT_TRAIL -> label = "P";
                }
                if (!label.isEmpty()) {
                    int labelWidth = mc.font.width(label);
                    mc.font.draw(poseStack, label, slotX + (slotSize - labelWidth) / 2 + 1, slotY + (slotSize - 8) / 2 + 1, 0x44FFFFFF);
                }
            }
            
            // Render tooltip if hovered
            if (isHovered && equippedId != null && !equippedId.isEmpty()) {
                // Tooltips are handled by the main screen, but we can draw a simple one here
                // for the slot content if needed.
            }
        }
    }
    
    /**
     * Render tooltips for hovered cosmetic slots.
     * Called from tab level to ensure tooltips render on top of all other components.
     */
    public void renderTooltips(PoseStack poseStack, int mouseX, int mouseY) {
        // CRITICAL: First check if mouse is actually within this panel's bounds
        // This prevents tooltips from showing when hovering over other panels (like available items)
        if (mouseX < startX || mouseX >= startX + width || mouseY < startY || mouseY >= startY + height) {
            return; // Mouse is outside this panel, don't show tooltips
        }
        
        // Calculate render area (same as in render method)
        int renderAreaY = startY + PADDING + 20;
        int renderAreaHeight = height - (PADDING * 2 + 20);
        int renderAreaX = startX + PADDING;
        int renderAreaWidth = width - PADDING * 2;
        
        // Calculate slot area - slots are at the bottom of render area
        int[] slots = {
            SupportersTabState.SLOT_HEAD,
            SupportersTabState.SLOT_CHEST,
            SupportersTabState.SLOT_LEGS,
            SupportersTabState.SLOT_FEET,
            SupportersTabState.SLOT_WINGS,
            SupportersTabState.SLOT_TRAIL
        };
        int slotSize = 22;
        int spacing = 4;
        int totalWidth = (slotSize * slots.length) + (spacing * (slots.length - 1));
        int slotStartX = renderAreaX + (renderAreaWidth - totalWidth) / 2;
        int slotY = renderAreaY + renderAreaHeight - slotSize - 10;
        int slotAreaHeight = slotSize + 20; // Add some margin for hover detection
        
        // CRITICAL: Only check slots if mouse is actually over the slot area at the bottom
        // This prevents detecting slots when hovering over panel1
        if (mouseX >= slotStartX - 5 && mouseX < slotStartX + totalWidth + 5 && 
            mouseY >= slotY - 5 && mouseY < slotY + slotAreaHeight) {
            renderCosmeticSlotTooltips(poseStack, renderAreaX, renderAreaY, renderAreaWidth, renderAreaHeight, mouseX, mouseY);
        }
    }
    
    /**
     * Render tooltips for hovered cosmetic slots.
     */
    private void renderCosmeticSlotTooltips(PoseStack poseStack, int x, int y, int width, int height, int mouseX, int mouseY) {
        // CRITICAL: First check if mouse is actually within this panel's bounds
        // This prevents tooltips from showing when hovering over other panels (like available items)
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return; // Mouse is outside this panel, don't show tooltips
        }
        
        SupportersTabState state = SupportersTabState.getInstance();
        CosmeticRegistry registry = CosmeticRegistry.getInstance();
        
        // Define slots to check
        int[] slots = {
            SupportersTabState.SLOT_HEAD,
            SupportersTabState.SLOT_CHEST,
            SupportersTabState.SLOT_LEGS,
            SupportersTabState.SLOT_FEET,
            SupportersTabState.SLOT_WINGS,
            SupportersTabState.SLOT_TRAIL
        };
        
        int slotSize = 22;
        int spacing = 4;
        int totalWidth = (slotSize * slots.length) + (spacing * (slots.length - 1));
        int slotStartX = x + (width - totalWidth) / 2;
        int slotY = y + height - slotSize - 10;
        
        // Mouse coordinates are already in GUI-scaled space (same as slot coordinates)
        // No scaling needed - slots are rendered in GUI-scaled space
        int slotMouseX = mouseX;
        int slotMouseY = mouseY;
        
        // CRITICAL: Only show tooltip if mouse is actually over one of the slots
        // This prevents tooltips from showing when hovering over other panels (like available items)
        boolean isOverAnySlot = false;
        String hoveredCosmeticId = null;
        String slotLabel = null;
        
        for (int i = 0; i < slots.length; i++) {
            int slotX = slotStartX + i * (slotSize + spacing);
            int slotIndex = slots[i];
            
            boolean isHovered = slotMouseX >= slotX && slotMouseX <= slotX + slotSize &&
                               slotMouseY >= slotY && slotMouseY <= slotY + slotSize;
            
            if (isHovered) {
                isOverAnySlot = true;
                hoveredCosmeticId = state.getEquippedCosmeticInSlot(slotIndex);
                
                // Get slot label
                switch (slotIndex) {
                    case SupportersTabState.SLOT_HEAD -> slotLabel = "Head";
                    case SupportersTabState.SLOT_CHEST -> slotLabel = "Chest";
                    case SupportersTabState.SLOT_LEGS -> slotLabel = "Legs";
                    case SupportersTabState.SLOT_FEET -> slotLabel = "Feet";
                    case SupportersTabState.SLOT_WINGS -> slotLabel = "Wings";
                    case SupportersTabState.SLOT_TRAIL -> slotLabel = "Trail";
                }
                break;
            }
        }
        
        // CRITICAL: Only render tooltip if mouse is actually over a slot
        // This prevents tooltips from showing when hovering over other panels
        if (!isOverAnySlot) {
            return; // Mouse is not over any slot, don't show tooltips
        }
        
        // Render tooltip if hovered
        String tooltipText = null;
        if (hoveredCosmeticId != null && !hoveredCosmeticId.isEmpty()) {
            // Get tooltip text
            tooltipText = hoveredCosmeticId;
            ItemStack stack = registry.resolveToItemStack(hoveredCosmeticId);
            if (stack != null && !stack.isEmpty()) {
                net.minecraft.network.chat.Component hoverName = stack.getHoverName();
                if (hoverName != null) {
                    tooltipText = hoverName.getString();
                }
            }
            
            if (tooltipText == null || tooltipText.isEmpty() || tooltipText.equals(hoveredCosmeticId)) {
                CosmeticManager cosmeticManager = CosmeticManager.getInstance();
                com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticMetadata metadata = cosmeticManager.getMetadata(hoveredCosmeticId);
                if (metadata != null && metadata.name != null && !metadata.name.isEmpty()) {
                    tooltipText = metadata.name;
                } else {
                    String idPart = hoveredCosmeticId;
                    if (hoveredCosmeticId.startsWith("buildscape:cosmatics/")) {
                        idPart = hoveredCosmeticId.substring(hoveredCosmeticId.lastIndexOf("/") + 1);
                    }
                    tooltipText = idPart.replace("_", " ");
                }
            }
        } else if (slotLabel != null) {
            tooltipText = slotLabel + " (Empty)";
        }
        
        if (tooltipText != null && !tooltipText.isEmpty()) {
            // Render tooltip - ensure it's visible
            RenderSystem.disableScissor();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            
            poseStack.pushPose();
            poseStack.translate(0, 0, 500);
            
            // Calculate tooltip size with minimal padding
            int textWidth = mc.font.width(tooltipText);
            int textHeight = mc.font.lineHeight;
            int padding = 3; // Minimal padding
            int tooltipWidth = textWidth + padding * 2;
            int tooltipHeight = textHeight + padding * 2;
            
            int tooltipX = mouseX + 10;
            int tooltipY = mouseY - 12;
            
            int screenWidth = mc.screen != null ? mc.screen.width : mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.screen != null ? mc.screen.height : mc.getWindow().getGuiScaledHeight();
            if (tooltipX + tooltipWidth > screenWidth) {
                tooltipX = mouseX - tooltipWidth - 10;
            }
            if (tooltipY < 0) {
                tooltipY = mouseY + 20;
            }
            if (tooltipY + tooltipHeight > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 2;
            }
            
            // Background - tight fit
            GuiComponent.fill(poseStack, tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + tooltipHeight, 0xF0000000);
            // Border - 1 pixel border
            GuiComponent.fill(poseStack, tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + 1, 0xFFCCCCCC);
            GuiComponent.fill(poseStack, tooltipX, tooltipY + tooltipHeight - 1, tooltipX + tooltipWidth, tooltipY + tooltipHeight, 0xFFCCCCCC);
            GuiComponent.fill(poseStack, tooltipX, tooltipY, tooltipX + 1, tooltipY + tooltipHeight, 0xFFCCCCCC);
            GuiComponent.fill(poseStack, tooltipX + tooltipWidth - 1, tooltipY, tooltipX + tooltipWidth, tooltipY + tooltipHeight, 0xFFCCCCCC);
            
            // Text - centered with padding
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            mc.font.draw(poseStack, tooltipText, tooltipX + padding, tooltipY + padding, 0xFFFFFF);
            
            poseStack.popPose();
            
            // Restore state
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
    }
    
    @Override
    protected boolean handleMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // Scale drag coordinates to match the scale transformation
        double actualGuiScale = mc.getWindow().getGuiScale();
        double scaleFactor = FIXED_GUI_SCALE / actualGuiScale;
        double scaledDragX = dragX / scaleFactor;
        double scaledDragY = dragY / scaleFactor;
        
        // Rotate player in 3D sphere - smooth rotation in any direction
        if (button == 0 && isDragging) {
            float rotationSpeed = 2.0f; // Degrees per pixel
            
            // Horizontal drag (left/right) - rotates around Y axis (yaw)
            // Drag right = rotate right, drag left = rotate left
            rotationYaw -= (float)scaledDragX * rotationSpeed;
            
            // Vertical drag (up/down) - rotates around X axis (pitch)
            // Drag down = rotate down, drag up = rotate up
            rotationPitch += (float)scaledDragY * rotationSpeed;
            
            // Clamp pitch to prevent flipping (keep it between -90 and 90 degrees)
            rotationPitch = Math.max(-90.0f, Math.min(90.0f, rotationPitch));
            
            // Normalize yaw rotation to 0-360 range for smooth continuous rotation
            while (rotationYaw < 0) rotationYaw += 360.0f;
            while (rotationYaw >= 360.0f) rotationYaw -= 360.0f;
            
            return true;
        }
        return false;
    }
    
    /**
     * Handle mouse release to stop dragging.
     * This should be called from the parent tab.
     */
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && isDragging) {
            isDragging = false;
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean handleMouseScrolled(double mouseX, double mouseY, double delta) {
        // Check if Ctrl is pressed for zoom
        // Use Screen.hasControlDown() for proper Ctrl detection
        if (net.minecraft.client.gui.screens.Screen.hasControlDown()) {
            // Zoom in/out with Ctrl + scroll
            float zoomSpeed = 0.1f; // Zoom speed per scroll tick
            playerZoom += (float)delta * zoomSpeed;
            playerZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, playerZoom)); // Clamp zoom
            return true;
        }
        
        return false;
    }

    /**
     * Update keyboard-driven walking direction (WASD) for avatar preview.
     * @return true if any movement key is held
     */
    private boolean updateKeyboardWalkDirection() {
        if (mc.player == null || mc.options == null) {
            keyboardDirX = 0.0;
            keyboardDirZ = 1.0;
            return false;
        }
        
        boolean forward = mc.options.keyUp.isDown();
        boolean back = mc.options.keyDown.isDown();
        boolean left = mc.options.keyLeft.isDown();
        boolean right = mc.options.keyRight.isDown();
        
        double dirX = 0.0;
        double dirZ = 0.0;
        if (forward) dirZ += 1.0;
        if (back) dirZ -= 1.0;
        if (left) dirX -= 1.0;
        if (right) dirX += 1.0;
        
        boolean any = dirX != 0.0 || dirZ != 0.0;
        if (any) {
            keyboardDirX = dirX;
            keyboardDirZ = dirZ;
        }
        return any;
    }
    
    /**
     * Reset player rotation to default.
     */
    public void resetRotation() {
        rotationYaw = 0.0f;
        rotationPitch = 0.0f;
    }
    
    /**
     * Reset zoom to default.
     */
    public void resetZoom() {
        playerZoom = 2.0f;
    }
    
    /**
     * Render particle trails around the player in the GUI.
     * Similar to how particles render in the world.
     */
    private void renderParticleTrails(PoseStack poseStack, Set<String> equippedCosmetics, 
                                     AbstractClientPlayer player, float partialTick) {
        if (equippedCosmetics == null || equippedCosmetics.isEmpty()) {
            guiParticles.clear();
            return;
        }
        
        // Find particle trail cosmetic
        String particleTrailId = null;
        for (String cosmeticId : equippedCosmetics) {
            if (CosmeticManager.getInstance().isParticleTrail(cosmeticId)) {
                particleTrailId = cosmeticId;
                break;
            }
        }
        
        if (particleTrailId == null) {
            guiParticles.clear();
            return;
        }
        
        // Update existing particles
        java.util.Iterator<GuiParticle> it = guiParticles.iterator();
        while (it.hasNext()) {
            GuiParticle p = it.next();
            p.tick();
            if (p.isDead()) {
                it.remove();
            }
        }
        
        // Get color
        UUID playerUuid = mc.player != null ? mc.player.getUUID() : null;
        com.kingodogo.buildscape.config.CosmeticsConfig config = 
            com.kingodogo.buildscape.config.CosmeticsConfig.get();
        String storedColor = playerUuid != null ? config.getCosmeticColor(playerUuid, particleTrailId) : null;
        float[] color = com.kingodogo.buildscape.client.ParticleTrailHandler.getParticleColor(particleTrailId);
        
        if (storedColor != null && !storedColor.isEmpty()) {
            try {
                String hex = storedColor.startsWith("#") ? storedColor.substring(1) : storedColor;
                int rgb = Integer.parseInt(hex, 16);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                color = new float[]{r / 255.0f, g / 255.0f, b / 255.0f};
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        
        // Spawn particles around player in GUI
        long currentTime = System.currentTimeMillis();
        Long lastSpawnTime = itemAnimationTimes.getOrDefault(particleTrailId, 0L);
        
        // Spawn particles periodically
        if (currentTime - lastSpawnTime > 100) {
            java.util.Random rand = player.level.random;
            
            // Spawn 1-2 particles
            int particleCount = 1 + rand.nextInt(2);
            for (int i = 0; i < particleCount; i++) {
                // Position relative to player model center
                // Behind player
                double spawnX = (rand.nextDouble() - 0.5) * 0.4;
                double spawnY = 0.2 + (rand.nextDouble() - 0.5) * 0.2;
                double spawnZ = -0.3 - rand.nextDouble() * 0.3;
                
                // Initial velocity
                double vx = (rand.nextDouble() - 0.5) * 0.02;
                double vy = 0.02 + rand.nextDouble() * 0.02;
                double vz = (rand.nextDouble() - 0.5) * 0.02;
                
                guiParticles.add(new GuiParticle(spawnX, spawnY, spawnZ, vx, vy, vz, 40 + rand.nextInt(20), color));
            }
            
            itemAnimationTimes.put(particleTrailId, currentTime);
        }
        
        // Render particles
        if (!guiParticles.isEmpty()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            
            for (GuiParticle p : guiParticles) {
                poseStack.pushPose();
                poseStack.translate(p.x, p.y, p.z);
                
                // Billboard effect - face the camera
                // Since we're in the player's pose stack which has rotations, 
                // we should undo the horizontal and vertical rotations
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(-rotationPitch));
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(-rotationYaw));
                
                float alpha = 1.0f - (float)p.age / p.lifetime;
                RenderSystem.setShaderColor(p.color[0], p.color[1], p.color[2], alpha);
                
                // Render as a small sparkle (cross)
                float size = p.scale * 0.1f;
                float halfSize = size / 2.0f;
                
                // Draw a small cross
                GuiComponent.fill(poseStack, (int)(-halfSize * 20), (int)(-1), (int)(halfSize * 20), (int)(1), 0xFFFFFFFF);
                GuiComponent.fill(poseStack, (int)(-1), (int)(-halfSize * 20), (int)(1), (int)(halfSize * 20), 0xFFFFFFFF);
                
                poseStack.popPose();
            }
            
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableDepthTest();
        }
    }
}
