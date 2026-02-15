package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.cosmetics.CosmeticManager;
import com.kingodogo.buildscape.cosmetics.CosmeticRegistry;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerAvatarPanel extends BasePanel {
    private static final double FIXED_GUI_SCALE = 2.0;
    private static final int PADDING = 10;
    private static final UUID KINGODOGO_UUID = UUID.fromString("f84c6a79-0a4e-45e1-875e-e049e012769f");
    private static final String KINGODOGO_USERNAME = "Kingodogo";

    private final CosmeticRegistry cosmeticRegistry = CosmeticRegistry.getInstance();
    private final SupportersTabState state = SupportersTabState.getInstance();
    private final Minecraft mc = Minecraft.getInstance();

    private float rotationYaw = 0.0f;
    private float rotationPitch = 0.0f;
    private boolean isDragging = false;

    private float playerZoom = 2.0f;
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 3.0f;

    private boolean isWalking = false;
    private long lastFrameTime = 0;
    private double walkX = 0.0;
    private double walkZ = 0.0;
    private double walkXOld = 0.0;
    private double walkZOld = 0.0;
    private boolean keyboardWalking = false;
    private double keyboardDirX = 0.0;
    private double keyboardDirZ = 1.0;

    private final Map<String, Long> itemAnimationTimes = new java.util.HashMap<>();

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
            this.scale = 0.5f + (float) Math.random() * 0.5f;
        }

        void tick() {
            age++;
            x += vx;
            y += vy;
            z += vz;

            if (age < lifetime * 0.3) {
                vy += 0.005;
            } else {
                vy -= 0.01;
            }

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
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {

        double actualGuiScale = mc.getWindow().getGuiScale();
        int windowHeight = mc.getWindow().getHeight();
        int scissorX = (int) (startX * actualGuiScale);
        int scissorWidth = (int) (width * actualGuiScale);
        int scissorY = windowHeight - (int) ((startY + height) * actualGuiScale);
        int scissorHeight = (int) (height * actualGuiScale);

        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);

        GuiComponent.fill(poseStack, startX, startY, endX, endY, 0x80000000);

        // Draw panel border
        int borderColor = 0xFF666666;
        GuiComponent.fill(poseStack, startX - 1, startY - 1, endX + 1, startY, borderColor); // Top
        GuiComponent.fill(poseStack, startX - 1, endY, endX + 1, endY + 1, borderColor); // Bottom
        GuiComponent.fill(poseStack, startX - 1, startY, startX, endY, borderColor); // Left
        GuiComponent.fill(poseStack, endX, startY, endX + 1, endY, borderColor); // Right

        String title = "Player Avatar";
        int titleWidth = mc.font.width(title);
        mc.font.draw(poseStack, title,
                startX + (width - titleWidth) / 2,
                startY + PADDING,
                0xFFFFFF);

        String hint = "Click and drag to rotate";
        int hintWidth = mc.font.width(hint);
        mc.font.draw(poseStack, hint,
                startX + (width - hintWidth) / 2,
                startY + PADDING + 12,
                0xAAAAAA);

        String zoomHint = "Ctrl + Scroll to zoom";
        int zoomHintWidth = mc.font.width(zoomHint);
        mc.font.draw(poseStack, zoomHint,
                startX + (width - zoomHintWidth) / 2,
                startY + PADDING + 24,
                0x888888);

        if (Math.abs(playerZoom - 1.0f) > 0.01f) {
            String zoomLevel = String.format("%.0f%%", playerZoom * 100.0f);
            int zoomLevelWidth = mc.font.width(zoomLevel);
            mc.font.draw(poseStack, zoomLevel,
                    startX + (width - zoomLevelWidth) / 2,
                    startY + PADDING + 36,
                    0x00FF00);
        }

        // Scale button size based on panel dimensions for proper GUI scaling
        double guiScaleFactor = 2.0 / actualGuiScale; // Normalize to scale 2
        int buttonSize = (int) (16 * guiScaleFactor);
        buttonSize = Math.max(12, Math.min(24, buttonSize)); // Clamp between 12-24 pixels
        int buttonX = endX - buttonSize - (int) (5 * guiScaleFactor);
        int buttonY = endY - buttonSize - (int) (5 * guiScaleFactor);

        boolean buttonHovered = mouseX >= buttonX && mouseX <= buttonX + buttonSize &&
                mouseY >= buttonY && mouseY <= buttonY + buttonSize;

        int buttonColor = buttonHovered ? 0xAA555555 : 0xAA333333;
        GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + buttonSize, buttonY + buttonSize, buttonColor);

        GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + buttonSize, buttonY + 1, 0xFF666666);
        GuiComponent.fill(poseStack, buttonX, buttonY + buttonSize - 1, buttonX + buttonSize, buttonY + buttonSize,
                0xFF666666);
        GuiComponent.fill(poseStack, buttonX, buttonY, buttonX + 1, buttonY + buttonSize, 0xFF666666);
        GuiComponent.fill(poseStack, buttonX + buttonSize - 1, buttonY, buttonX + buttonSize, buttonY + buttonSize,
                0xFF666666);

        int centerX = buttonX + buttonSize / 2;
        int centerY = buttonY + buttonSize / 2;

        if (isWalking) {
            int barWidth = 2;
            int barHeight = 8;
            int barSpacing = 2;
            GuiComponent.fill(poseStack,
                    centerX - barSpacing / 2 - barWidth, centerY - barHeight / 2,
                    centerX - barSpacing / 2, centerY + barHeight / 2,
                    0xFFFFFFFF);
            GuiComponent.fill(poseStack,
                    centerX + barSpacing / 2, centerY - barHeight / 2,
                    centerX + barSpacing / 2 + barWidth, centerY + barHeight / 2,
                    0xFFFFFFFF);
        } else {
            int triangleSize = 8;
            for (int y = -triangleSize / 2; y <= triangleSize / 2; y++) {
                int width = triangleSize / 2 - Math.abs(y);
                GuiComponent.fill(poseStack,
                        centerX - triangleSize / 4, centerY + y,
                        centerX - triangleSize / 4 + width, centerY + y + 1,
                        0xFFFFFFFF);
            }
        }

        Map<Integer, String> equippedBySlot = state.getEquippedCosmeticsBySlot();
        Set<String> equippedSet = state.getEquippedCosmetics();
        Set<String> cosmeticsToRender = new java.util.HashSet<>();

        cosmeticsToRender.addAll(equippedBySlot.values());

        cosmeticsToRender.addAll(equippedSet);

        if (cosmeticsToRender.isEmpty()) {
            String selectedCosmeticId = state.getSelectedCosmeticId();
            if (selectedCosmeticId != null && !selectedCosmeticId.isEmpty()) {
                cosmeticsToRender.add(selectedCosmeticId);
            }
        }

        int slotY = getSlotY();
        int renderAreaY = startY + PADDING - 5;
        // Constrain player render area to stop above the slots
        int playerRenderBottom = slotY;
        int renderAreaHeight = playerRenderBottom - renderAreaY;
        int renderAreaX = startX + PADDING;
        int renderAreaWidth = width - PADDING * 2;

        // Clip player to avoid overlapping slots
        int playerScissorBottomGL = windowHeight - (int) (slotY * actualGuiScale);
        int playerScissorHeight = (int) ((slotY - startY) * actualGuiScale);
        RenderSystem.enableScissor(scissorX, playerScissorBottomGL, scissorWidth, playerScissorHeight);

        boolean rendered3D = false;
        if (mc.player != null && mc.level != null) {
            try {
                render3DPlayerModel(poseStack, renderAreaX, renderAreaY, renderAreaWidth, renderAreaHeight,
                        cosmeticsToRender, partialTick);
                rendered3D = true;
            } catch (Exception e) {
                BuildScape.getLogger().debug("3D player rendering not available, using fallback: " + e.getMessage());
            }
        } else {
            try {
                renderOfflinePlayer(poseStack, renderAreaX, renderAreaY, renderAreaWidth, renderAreaHeight,
                        cosmeticsToRender, partialTick);
                rendered3D = true;
            } catch (Exception e) {
                BuildScape.getLogger().debug("Offline player rendering failed: " + e.getMessage());
            }
        }

        // Restore original scissor for the rest of the UI
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);

        if (!rendered3D) {
            renderEnhancedDisplay(poseStack, renderAreaX, renderAreaY, renderAreaWidth, renderAreaHeight,
                    cosmeticsToRender);
        }

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        poseStack.pushPose();
        poseStack.translate(0, 0, 400);
        poseStack.translate(0, 0, 400);
        // Pass the calculated slotY directly
        renderCosmeticSlots(poseStack, renderAreaX, slotY, renderAreaWidth, getSlotSize(), mouseX, mouseY);
        poseStack.popPose();
        RenderSystem.depthMask(true);

        RenderSystem.disableScissor();

    }

    private void render3DPlayerModel(PoseStack poseStack, int x, int y, int width, int height,
            Set<String> equippedCosmetics, float partialTick) {
        AbstractClientPlayer player;

        if (mc.player == null || mc.level == null) {
            player = createOfflinePlayer();
            if (player == null) {
                renderEnhancedDisplay(poseStack, x, y, width, height, equippedCosmetics);
                return;
            }
        } else {
            player = mc.player;
        }

        keyboardWalking = false;

        int centerX = x + width / 2;
        int centerY = y + height / 2;

        ItemStack originalHelmet = player.getItemBySlot(EquipmentSlot.HEAD).copy();
        ItemStack originalChestplate = player.getItemBySlot(EquipmentSlot.CHEST).copy();
        ItemStack originalLeggings = player.getItemBySlot(EquipmentSlot.LEGS).copy();
        ItemStack originalBoots = player.getItemBySlot(EquipmentSlot.FEET).copy();
        ItemStack originalMainHand = player.getItemBySlot(EquipmentSlot.MAINHAND).copy();

        boolean wasSilent = player.isSilent();

        try {
            player.setSilent(true);

            applyCosmeticEquipment(player, equippedCosmetics);

            EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
            PlayerRenderer playerRenderer = (PlayerRenderer) dispatcher.getRenderer(player);

            if (playerRenderer == null) {
                return;
            }

            float playerHeight = 1.8f;
            float baseScale = Math.min(width, height) * 0.4f / playerHeight;
            baseScale = Math.max(0.1f, Math.min(1.0f, baseScale));
            float scale = baseScale * playerZoom;

            double actualGuiScale = mc.getWindow().getGuiScale();
            double guiScaleMultiplier = 35.0 * (2.0 / actualGuiScale);

            poseStack.pushPose();

            poseStack.translate(centerX, centerY + height * 0.2f, 100.0f);

            poseStack.translate(0.0, 0.9, 0.0);

            poseStack.mulPose(Vector3f.YP.rotationDegrees(rotationYaw));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(rotationPitch));

            float guiScale = scale * (float) guiScaleMultiplier * playerZoom;
            poseStack.scale(guiScale, -guiScale, guiScale);

            poseStack.translate(0.0, -0.9, 0.0);

            float prevX = (float) player.getX();
            float prevY = (float) player.getY();
            float prevZ = (float) player.getZ();
            double prevXOld = player.xOld;
            double prevYOld = player.yOld;
            double prevZOld = player.zOld;

            if (isWalking) {
                prevXOld = player.xOld;
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
            int originalTickCount = prevTickCount;
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
                net.minecraft.world.entity.LivingEntity living = player;
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

            if (isWalking || keyboardWalking) {
                long currentTime = System.currentTimeMillis();

                if (lastFrameTime == 0) {
                    lastFrameTime = currentTime;
                }
                double deltaTime = (currentTime - lastFrameTime) / 1000.0;
                lastFrameTime = currentTime;

                float walkSpeed = 0.2f;

                double dirX = 0.0;
                double dirZ = 1.0;
                keyboardWalking = updateKeyboardWalkDirection();
                if (keyboardWalking) {
                    dirX = keyboardDirX;
                    dirZ = keyboardDirZ;
                }

                double len = Math.sqrt(dirX * dirX + dirZ * dirZ);
                if (len > 0.0) {
                    dirX /= len;
                    dirZ /= len;
                } else {
                    dirX = 0.0;
                    dirZ = 0.0;
                }

                double oldX = walkXOld;
                double oldZ = walkZOld;

                walkX += deltaTime * walkSpeed * dirX;
                walkZ += deltaTime * walkSpeed * dirZ;
                walkXOld = walkX;
                walkZOld = walkZ;

                double deltaX = walkX - oldX;
                double deltaZ = walkZ - oldZ;
                double deltaMag = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

                if (deltaMag < 0.01) {
                    double adjust = 0.01 / (deltaMag == 0.0 ? 1.0 : deltaMag);
                    deltaX *= adjust;
                    deltaZ *= adjust;
                    walkX = oldX + deltaX;
                    walkZ = oldZ + deltaZ;
                }

                player.setPos(walkX, 0, walkZ);

                player.xOld = oldX;
                player.yOld = 0.0;
                player.zOld = oldZ;

                player.xCloak = player.getX();
                player.yCloak = player.getY();
                player.zCloak = player.getZ();
                player.xCloakO = player.xCloak;
                player.yCloakO = player.yCloak;
                player.zCloakO = player.zCloak;

                player.tickCount++;

                player.setYRot(0.0f);
                player.setXRot(0.0f);
                player.yBodyRot = 0.0f;
                player.yHeadRot = 0.0f;
                player.yRotO = 0.0f;
                player.xRotO = 0.0f;
                player.yBodyRotO = 0.0f;
                player.yHeadRotO = 0.0f;

                if (player instanceof net.minecraft.world.entity.LivingEntity) {
                    net.minecraft.world.entity.LivingEntity living = player;
                    living.setSprinting(false);
                    living.setShiftKeyDown(false);

                    float animSpeed = 0.5f;
                    living.animationSpeed = animSpeed;
                    living.animationSpeedOld = animSpeed;

                    float movementAmount = (float) deltaX * 10.0f;
                    living.animationPosition += movementAmount;
                    if (living.animationPosition > 1.0f) {
                        living.animationPosition -= 1.0f;
                    }

                    living.hurtTime = 0;
                    living.deathTime = 0;
                    living.swingTime = 0;
                    living.attackAnim = 0.0f;
                    living.oAttackAnim = 0.0f;

                    living.setDeltaMovement(deltaX, 0, 0);
                    living.setSpeed(animSpeed);
                }

                walkXOld = walkX;
            } else {
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
                player.tickCount = 0;

                player.setYRot(0.0f);
                player.setXRot(0.0f);
                player.yBodyRot = 0.0f;
                player.yHeadRot = 0.0f;
                player.yRotO = 0.0f;
                player.xRotO = 0.0f;
                player.yBodyRotO = 0.0f;
                player.yHeadRotO = 0.0f;
                player.tickCount = 0;
                player.setDeltaMovement(0, 0, 0);

                if (player instanceof net.minecraft.world.entity.LivingEntity) {
                    net.minecraft.world.entity.LivingEntity living = player;
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
            }

            int lightLevel = 15728880;

            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Vector3f light0 = new Vector3f(0.2f, 1.0f, 0.7f);
            light0.normalize();
            Vector3f light1 = new Vector3f(-0.2f, 1.0f, 0.7f);
            light1.normalize();
            RenderSystem.setupGuiFlatDiffuseLighting(light0, light1);

            try {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

                playerRenderer.render(player, 0.0f, partialTick, poseStack, bufferSource, lightLevel);
                bufferSource.endBatch();

                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

                renderParticleTrails(poseStack, equippedCosmetics, player, partialTick);
            } catch (Exception e) {
                BuildScape.getLogger().error("Error rendering player: " + e.getMessage());
            } finally {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.disableDepthTest();
                RenderSystem.disableBlend();
            }

            if (player == mc.player) {
                player.setPos(prevX, prevY, prevZ);
                if (!isWalking) {
                    player.xOld = prevXOld;
                    player.yOld = prevYOld;
                    player.zOld = prevZOld;
                }
            }
            player.setYRot(prevYRot);
            player.setXRot(prevXRot);
            player.yBodyRot = prevYBodyRot;
            player.yHeadRot = prevYHeadRot;
            player.yRotO = prevYRotO;
            player.xRotO = prevXRotO;
            player.yBodyRotO = prevYBodyRotO;
            player.yHeadRotO = prevYHeadRotO;
            player.tickCount = originalTickCount;
            if (player instanceof net.minecraft.world.entity.LivingEntity) {
                net.minecraft.world.entity.LivingEntity living = player;
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
            if (player == mc.player) {
                setItemSlotSilent(player, EquipmentSlot.HEAD, originalHelmet);
                setItemSlotSilent(player, EquipmentSlot.CHEST, originalChestplate);
                setItemSlotSilent(player, EquipmentSlot.LEGS, originalLeggings);
                setItemSlotSilent(player, EquipmentSlot.FEET, originalBoots);
                setItemSlotSilent(player, EquipmentSlot.MAINHAND, originalMainHand);
            }

            player.setSilent(wasSilent);
        }
    }

    private void applyCosmeticEquipment(AbstractClientPlayer player, Set<String> cosmeticIds) {
        if (cosmeticIds == null || cosmeticIds.isEmpty()) {
            return;
        }

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

    private void setItemSlotSilent(AbstractClientPlayer player, EquipmentSlot slot, ItemStack stack) {
        if (player == null)
            return;

        net.minecraft.world.entity.player.Inventory inventory = player.getInventory();

        switch (slot) {
            case HEAD:
                inventory.armor.set(3, stack);
                break;
            case CHEST:
                inventory.armor.set(2, stack);
                break;
            case LEGS:
                inventory.armor.set(1, stack);
                break;
            case FEET:
                inventory.armor.set(0, stack);
                break;
            case MAINHAND:
                inventory.setItem(inventory.selected, stack);
                break;
            default:
                break;
        }
    }

    private EquipmentSlot getSlotForCosmetic(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        net.minecraft.world.item.Item item = stack.getItem();

        if (item instanceof net.minecraft.world.item.ElytraItem) {
            return EquipmentSlot.CHEST;
        }

        if (item instanceof net.minecraft.world.item.ArmorItem armor) {
            return armor.getSlot();
        }

        if (item instanceof net.minecraft.world.item.SwordItem ||
                item instanceof net.minecraft.world.item.BowItem ||
                item instanceof net.minecraft.world.item.TridentItem ||
                item instanceof net.minecraft.world.item.AxeItem) {
            return EquipmentSlot.MAINHAND;
        }

        return null;
    }

    private void renderEnhancedDisplay(PoseStack poseStack, int x, int y, int width, int height,
            Set<String> equippedCosmetics) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;

        String playerIcon = "⛹";
        int iconWidth = mc.font.width(playerIcon);
        mc.font.draw(poseStack, playerIcon,
                centerX - iconWidth / 2,
                centerY - 40,
                0xFFFFFF);

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
            int itemY = centerY + 10;
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
                if (itemIndex >= maxItems)
                    break;

                boolean isParticleTrail = CosmeticManager.getInstance().isParticleTrail(cosmeticId);

                ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
                if ((stack == null || stack.isEmpty()) && SupportersTabState.getInstance().getBestSlotForCosmetic(cosmeticId) == SupportersTabState.SLOT_HEAD) {
                    stack = new ItemStack(net.minecraft.world.item.Items.LEATHER_HELMET);
                }

                if (stack != null && !stack.isEmpty()) {
                    int itemX = centerX - 8;
                    mc.getItemRenderer().renderGuiItem(stack, itemX, itemY);
                    mc.getItemRenderer().renderGuiItemDecorations(mc.font, stack, itemX, itemY);

                    if (isParticleTrail) {
                        String trailText = "✨";
                        mc.font.draw(poseStack, trailText,
                                itemX + 18,
                                itemY + 2,
                                0xFFFF00);
                    }

                    itemY += 20;
                    itemIndex++;
                } else if (isParticleTrail) {
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
                            0xFFFF00);
                    itemY += 15;
                    itemIndex++;
                }
            }
        }
    }

    private AbstractClientPlayer createOfflinePlayer() {
        if (mc.level == null) {
            return null;
        }

        try {
            GameProfile gameProfile = new GameProfile(KINGODOGO_UUID, KINGODOGO_USERNAME);

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

            dummyPlayer.level = mc.level;

            dummyPlayer.setPos(0, 0, 0);
            dummyPlayer.setYRot(0.0f);
            dummyPlayer.setXRot(0.0f);
            dummyPlayer.yBodyRot = 0.0f;
            dummyPlayer.yHeadRot = 0.0f;
            dummyPlayer.yRotO = 0.0f;
            dummyPlayer.xRotO = 0.0f;
            dummyPlayer.yBodyRotO = 0.0f;
            dummyPlayer.yHeadRotO = 0.0f;
            dummyPlayer.tickCount = 0;
            dummyPlayer.setDeltaMovement(0, 0, 0);
            dummyPlayer.setSilent(true);
            if (dummyPlayer instanceof net.minecraft.world.entity.LivingEntity) {
                net.minecraft.world.entity.LivingEntity living = dummyPlayer;
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

    private void renderOfflinePlayer(PoseStack poseStack, int x, int y, int width, int height,
            Set<String> equippedCosmetics, float partialTick) {
        AbstractClientPlayer offlinePlayer = createOfflinePlayer();
        if (offlinePlayer != null && mc.level != null) {
            try {
                render3DPlayerModel(poseStack, x, y, width, height, equippedCosmetics, partialTick);
                return;
            } catch (Exception e) {
                BuildScape.getLogger().debug("Failed to render offline player 3D: " + e.getMessage());
            }
        }

        renderEnhancedDisplay(poseStack, x, y, width, height, equippedCosmetics);

        int centerX = x + width / 2;
        int centerY = y + height / 2;
        String offlineText = "Offline Mode - " + KINGODOGO_USERNAME;
        int textWidth = mc.font.width(offlineText);
        mc.font.draw(poseStack, offlineText,
                centerX - textWidth / 2,
                centerY + 30,
                0xFF8800);

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
                if (itemIndex >= maxItems)
                    break;

                boolean isParticleTrail = CosmeticManager.getInstance().isParticleTrail(cosmeticId);

                ItemStack stack = cosmeticRegistry.resolveToItemStack(cosmeticId);
                if (stack != null && !stack.isEmpty()) {
                    int itemX = centerX - 8;
                    mc.getItemRenderer().renderGuiItem(stack, itemX, itemY);
                    mc.getItemRenderer().renderGuiItemDecorations(mc.font, stack, itemX, itemY);

                    if (isParticleTrail) {
                        String trailText = "✨";
                        mc.font.draw(poseStack, trailText,
                                itemX + 18,
                                itemY + 2,
                                0xFFFF00);
                    }

                    itemY += 20;
                    itemIndex++;
                } else if (isParticleTrail) {
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
                            0xFFFF00);
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
        // Removed incorrect scaling logic that was causing ghost clicks/hovers
        double scaledMouseX = mouseX;
        double scaledMouseY = mouseY;

        int buttonSize = 16;
        int buttonX = endX - buttonSize - 5;
        int buttonY = endY - buttonSize - 5;

        if (scaledMouseX >= buttonX && scaledMouseX <= buttonX + buttonSize &&
                scaledMouseY >= buttonY && scaledMouseY <= buttonY + buttonSize) {
            isWalking = !isWalking;
            if (isWalking) {
                lastFrameTime = System.currentTimeMillis();
                walkX = 0.0;
                walkZ = 0.0;
                walkXOld = 0.0;
                walkZOld = 0.0;
            } else {
                walkX = 0.0;
                walkZ = 0.0;
                walkXOld = 0.0;
                walkZOld = 0.0;
                lastFrameTime = 0;
            }
            return true;
        }

        SupportersTabState state = SupportersTabState.getInstance();
        int[] slots = {
                SupportersTabState.SLOT_HEAD,
                SupportersTabState.SLOT_CHEST,
                SupportersTabState.SLOT_LEGS,
                SupportersTabState.SLOT_FEET,
                SupportersTabState.SLOT_WINGS,
                SupportersTabState.SLOT_TRAIL
        };

        int slotSize = getSlotSize();
        int spacing = getSlotSpacing();

        int renderAreaX = startX + PADDING;
        int renderAreaWidth = width - PADDING * 2;

        int totalWidth = (slotSize * slots.length) + (spacing * (slots.length - 1));
        int slotStartX = renderAreaX + (renderAreaWidth - totalWidth) / 2;
        int slotY = getSlotY();

        for (int i = 0; i < slots.length; i++) {
            int slotX = slotStartX + i * (slotSize + spacing);
            if (scaledMouseX >= slotX && scaledMouseX <= slotX + slotSize &&
                    scaledMouseY >= slotY && scaledMouseY <= slotY + slotSize) {

                int slotIndex = slots[i];
                String equippedId = state.getEquippedCosmeticInSlot(slotIndex);

                if (equippedId != null && !equippedId.isEmpty()) {
                    if (button == 1) {
                        state.unequipCosmeticFromSlot(slotIndex);
                    } else {
                        state.setSelectedCosmeticId(equippedId);
                    }
                    return true;
                }
            }
        }

        if (button == 0) {
            isDragging = true;
            return true;
        }
        return false;
    }

    private void renderCosmeticSlots(PoseStack poseStack, int x, int y, int width, int height, int mouseX, int mouseY) {
        SupportersTabState state = SupportersTabState.getInstance();
        CosmeticRegistry registry = CosmeticRegistry.getInstance();

        int[] slots = {
                SupportersTabState.SLOT_HEAD,
                SupportersTabState.SLOT_CHEST,
                SupportersTabState.SLOT_LEGS,
                SupportersTabState.SLOT_FEET,
                SupportersTabState.SLOT_WINGS,
                SupportersTabState.SLOT_TRAIL
        };

        // Scale slot size and spacing based on GUI scale for proper rendering at all
        // scales
        int slotSize = getSlotSize();
        int spacing = getSlotSpacing();

        int totalWidth = (slotSize * slots.length) + (spacing * (slots.length - 1));
        int slotStartX = x + (width - totalWidth) / 2;
        // Use passed Y directly as the top of the slots
        int slotY = y;

        // Removed incorrect scaling logic
        double scaledMouseX = mouseX;
        double scaledMouseY = mouseY;

        // Determine preview slot based on hovered cosmetic
        int previewSlot = -999;
        String previewId = state.getPreviewCosmeticId();
        if (previewId != null) {
            previewSlot = state.getBestSlotForCosmetic(previewId);
        }

        for (int i = 0; i < slots.length; i++) {
            int slotX = slotStartX + i * (slotSize + spacing);
            int slotIndex = slots[i];
            String equippedId = state.getEquippedCosmeticInSlot(slotIndex);

            boolean isHovered = scaledMouseX >= slotX && scaledMouseX <= slotX + slotSize &&
                    scaledMouseY >= slotY && scaledMouseY <= slotY + slotSize;

            boolean isPreviewTarget = (slotIndex == previewSlot);

            int bgColor = isHovered ? 0xAA444444 : 0x80000000;
            int borderColor = 0xFF888888;

            if (isPreviewTarget) {
                borderColor = 0xFFFFFF00; // Yellow highlight for preview
                if (!isHovered)
                    bgColor = 0xAA444400; // Slight yellow tint if not directly hovered
            } else if (equippedId != null && equippedId.equals(state.getSelectedCosmeticId())) {
                borderColor = 0xFFFFFF00;
            } else if (isHovered) {
                borderColor = 0xFFFFFFFF;
            }

            GuiComponent.fill(poseStack, slotX, slotY, slotX + slotSize, slotY + slotSize, bgColor);

            GuiComponent.fill(poseStack, slotX, slotY, slotX + slotSize, slotY + 1, borderColor);
            GuiComponent.fill(poseStack, slotX, slotY + slotSize - 1, slotX + slotSize, slotY + slotSize, borderColor);
            GuiComponent.fill(poseStack, slotX, slotY, slotX + 1, slotY + slotSize, borderColor);
            GuiComponent.fill(poseStack, slotX + slotSize - 1, slotY, slotX + slotSize, slotY + slotSize, borderColor);

            if (equippedId != null && !equippedId.isEmpty()) {
                ItemStack stack = registry.resolveToItemStack(equippedId);

                // Fallback for custom head cosmetics that don't have an ItemStack
                if ((stack == null || stack.isEmpty()) && state.getBestSlotForCosmetic(equippedId) == SupportersTabState.SLOT_HEAD) {
                    stack = new ItemStack(net.minecraft.world.item.Items.LEATHER_HELMET);
                }

                if (stack != null && !stack.isEmpty()) {
                    int itemX = slotX + (slotSize - 16) / 2;
                    int itemY = slotY + (slotSize - 16) / 2;

                    mc.getItemRenderer().renderGuiItem(stack, itemX, itemY);
                    mc.getItemRenderer().renderGuiItemDecorations(mc.font, stack, itemX, itemY);

                    if (CosmeticManager.getInstance().isParticleTrail(equippedId)) {
                        poseStack.pushPose();
                        poseStack.translate(0, 0, 200);
                        mc.font.draw(poseStack, "✨", slotX + slotSize - 9, slotY + 1, 0xFFFF00);
                        poseStack.popPose();
                    }
                }
            } else {
                // Render empty slot icon (Vanilla style)
                ResourceLocation iconLoc = null;
                switch (slotIndex) {
                    case SupportersTabState.SLOT_HEAD -> iconLoc = InventoryMenu.EMPTY_ARMOR_SLOT_HELMET;
                    case SupportersTabState.SLOT_CHEST -> iconLoc = InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE;
                    case SupportersTabState.SLOT_LEGS -> iconLoc = InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS;
                    case SupportersTabState.SLOT_FEET -> iconLoc = InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS;
                    case SupportersTabState.SLOT_WINGS -> iconLoc = InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
                }

                if (iconLoc != null) {
                    TextureAtlasSprite sprite = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(iconLoc);
                    RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                    RenderSystem.enableBlend();

                    int iconW = 16;
                    int iconH = 16;
                    int iconX = slotX + (slotSize - iconW) / 2;
                    int iconY = slotY + (slotSize - iconH) / 2;

                    GuiComponent.blit(poseStack, iconX, iconY, 0, iconW, iconH, sprite);
                    RenderSystem.disableBlend();
                } else {
                    // Fallback for Trail or others without icons
                    String label = "";
                    if (slotIndex == SupportersTabState.SLOT_TRAIL)
                        label = "P";

                    if (!label.isEmpty()) {
                        int labelWidth = mc.font.width(label);
                        mc.font.draw(poseStack, label, slotX + (slotSize - labelWidth) / 2 + 1,
                                slotY + (slotSize - 8) / 2 + 1, 0x44FFFFFF);
                    }
                }
            }
        }
    }

    public void renderTooltips(PoseStack poseStack, int mouseX, int mouseY) {
        if (mouseX < startX || mouseX >= startX + width || mouseY < startY || mouseY >= startY + height) {
            return;
        }

        int renderAreaY = startY + PADDING + 20;
        int renderAreaHeight = height - (PADDING * 2 + 20);
        int renderAreaX = startX + PADDING;
        int renderAreaWidth = width - PADDING * 2;

        int[] slots = {
                SupportersTabState.SLOT_HEAD,
                SupportersTabState.SLOT_CHEST,
                SupportersTabState.SLOT_LEGS,
                SupportersTabState.SLOT_FEET,
                SupportersTabState.SLOT_WINGS,
                SupportersTabState.SLOT_TRAIL
        };
        int slotSize = getSlotSize();
        int spacing = getSlotSpacing();
        int totalWidth = (slotSize * slots.length) + (spacing * (slots.length - 1));
        int slotStartX = renderAreaX + (renderAreaWidth - totalWidth) / 2;
        int slotY = getSlotY();
        int slotAreaHeight = slotSize + 20;

        if (mouseX >= slotStartX - 5 && mouseX < slotStartX + totalWidth + 5 &&
                mouseY >= slotY - 5 && mouseY < slotY + slotAreaHeight) {
            renderCosmeticSlotTooltips(poseStack, renderAreaX, renderAreaY, renderAreaWidth, renderAreaHeight, mouseX,
                    mouseY);
        }
    }

    private void renderCosmeticSlotTooltips(PoseStack poseStack, int x, int y, int width, int height, int mouseX,
            int mouseY) {
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return;
        }

        SupportersTabState state = SupportersTabState.getInstance();
        CosmeticRegistry registry = CosmeticRegistry.getInstance();

        int[] slots = {
                SupportersTabState.SLOT_HEAD,
                SupportersTabState.SLOT_CHEST,
                SupportersTabState.SLOT_LEGS,
                SupportersTabState.SLOT_FEET,
                SupportersTabState.SLOT_WINGS,
                SupportersTabState.SLOT_TRAIL
        };

        int slotSize = getSlotSize();
        int spacing = getSlotSpacing();
        int totalWidth = (slotSize * slots.length) + (spacing * (slots.length - 1));
        int slotStartX = x + (width - totalWidth) / 2;
        int slotY = getSlotY();

        int slotMouseX = mouseX;
        int slotMouseY = mouseY;

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

                switch (slotIndex) {
                    case SupportersTabState.SLOT_HEAD -> slotLabel = "Head";
                    case SupportersTabState.SLOT_CHEST -> slotLabel = "Chest";
                    case SupportersTabState.SLOT_LEGS -> slotLabel = "Legs";
                    case SupportersTabState.SLOT_FEET -> slotLabel = "Feet";
                    case SupportersTabState.SLOT_WINGS -> slotLabel = "Wings / Back";
                    case SupportersTabState.SLOT_TRAIL -> slotLabel = "Trail";
                }
                break;
            }
        }

        if (!isOverAnySlot) {
            return;
        }

        String tooltipText = null;
        if (hoveredCosmeticId != null && !hoveredCosmeticId.isEmpty()) {
            // Prioritize Metadata Name
            CosmeticManager cosmeticManager = CosmeticManager.getInstance();
            com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticMetadata metadata = cosmeticManager
                    .getMetadata(hoveredCosmeticId);

            if (metadata != null && metadata.name() != null && !metadata.name().isEmpty()) {
                tooltipText = metadata.name();
            } else {
                // Fallback to Item Name
                ItemStack stack = registry.resolveToItemStack(hoveredCosmeticId);
                if (stack != null && !stack.isEmpty()) {
                    net.minecraft.network.chat.Component hoverName = stack.getHoverName();
                    if (hoverName != null) {
                        tooltipText = hoverName.getString();
                    }
                }
            }

            // Fallback to ID parsing if still null or equals ID or equals specific generic names
            if (tooltipText == null || tooltipText.isEmpty() || tooltipText.equals(hoveredCosmeticId) || "Nether Star".equals(tooltipText)) {
                String idPart = hoveredCosmeticId;
                if (hoveredCosmeticId.startsWith("buildscape:cosmatics/")) {
                    idPart = hoveredCosmeticId.substring(hoveredCosmeticId.lastIndexOf("/") + 1);
                }
                tooltipText = idPart.replace("_", " ");
                // Capitalize first letter of each word
                String[] words = tooltipText.split(" ");
                StringBuilder sb = new StringBuilder();
                for (String word : words) {
                    if (word.length() > 0) {
                        sb.append(Character.toUpperCase(word.charAt(0)));
                        if (word.length() > 1) {
                            sb.append(word.substring(1));
                        }
                        sb.append(" ");
                    }
                }
                tooltipText = sb.toString().trim();
            }
        } else if (slotLabel != null) {
            tooltipText = slotLabel + " (Empty)";
        }

        if (tooltipText != null && !tooltipText.isEmpty()) {
            RenderSystem.disableScissor();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            poseStack.pushPose();
            poseStack.translate(0, 0, 500);

            int textWidth = mc.font.width(tooltipText);
            int textHeight = mc.font.lineHeight;
            int padding = 3;
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

            GuiComponent.fill(poseStack, tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + tooltipHeight,
                    0xF0000000);
            GuiComponent.fill(poseStack, tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + 1, 0xFFCCCCCC);
            GuiComponent.fill(poseStack, tooltipX, tooltipY + tooltipHeight - 1, tooltipX + tooltipWidth,
                    tooltipY + tooltipHeight, 0xFFCCCCCC);
            GuiComponent.fill(poseStack, tooltipX, tooltipY, tooltipX + 1, tooltipY + tooltipHeight, 0xFFCCCCCC);
            GuiComponent.fill(poseStack, tooltipX + tooltipWidth - 1, tooltipY, tooltipX + tooltipWidth,
                    tooltipY + tooltipHeight, 0xFFCCCCCC);

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            mc.font.draw(poseStack, tooltipText, tooltipX + padding, tooltipY + padding, 0xFFFFFF);

            poseStack.popPose();

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
    }

    @Override
    protected boolean handleMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        double actualGuiScale = mc.getWindow().getGuiScale();
        double scaleFactor = FIXED_GUI_SCALE / actualGuiScale;
        double scaledDragX = dragX / scaleFactor;
        double scaledDragY = dragY / scaleFactor;

        if (button == 0 && isDragging) {
            float rotationSpeed = 2.0f;

            rotationYaw -= (float) scaledDragX * rotationSpeed;

            rotationPitch += (float) scaledDragY * rotationSpeed;

            rotationPitch = Math.max(-90.0f, Math.min(90.0f, rotationPitch));

            while (rotationYaw < 0)
                rotationYaw += 360.0f;
            while (rotationYaw >= 360.0f)
                rotationYaw -= 360.0f;

            return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && isDragging) {
            isDragging = false;
            return true;
        }
        return false;
    }

    @Override
    protected boolean handleMouseScrolled(double mouseX, double mouseY, double delta) {
        if (net.minecraft.client.gui.screens.Screen.hasControlDown()) {
            float zoomSpeed = 0.1f;
            playerZoom += (float) delta * zoomSpeed;
            playerZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, playerZoom));
            return true;
        }

        return false;
    }

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
        if (forward)
            dirZ += 1.0;
        if (back)
            dirZ -= 1.0;
        if (left)
            dirX -= 1.0;
        if (right)
            dirX += 1.0;

        boolean any = dirX != 0.0 || dirZ != 0.0;
        if (any) {
            keyboardDirX = dirX;
            keyboardDirZ = dirZ;
        }
        return any;
    }

    public void resetRotation() {
        rotationYaw = 0.0f;
        rotationPitch = 0.0f;
    }

    public void resetZoom() {
        playerZoom = 2.0f;
    }

    private void renderParticleTrails(PoseStack poseStack, Set<String> equippedCosmetics,
            AbstractClientPlayer player, float partialTick) {
        if (equippedCosmetics == null || equippedCosmetics.isEmpty()) {
            guiParticles.clear();
            return;
        }

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

        java.util.Iterator<GuiParticle> it = guiParticles.iterator();
        while (it.hasNext()) {
            GuiParticle p = it.next();
            p.tick();
            if (p.isDead()) {
                it.remove();
            }
        }

        UUID playerUuid = mc.player != null ? mc.player.getUUID() : null;
        com.kingodogo.buildscape.config.CosmeticsConfig config = com.kingodogo.buildscape.config.CosmeticsConfig.get();
        String storedColor = playerUuid != null ? config.getCosmeticColor(playerUuid, particleTrailId) : null;
        float[] color = com.kingodogo.buildscape.client.ParticleTrailHandler.getParticleColor(particleTrailId);

        if (storedColor != null && !storedColor.isEmpty()) {
            try {
                String hex = storedColor.startsWith("#") ? storedColor.substring(1) : storedColor;
                int rgb = Integer.parseInt(hex, 16);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                color = new float[] { r / 255.0f, g / 255.0f, b / 255.0f };
            } catch (NumberFormatException e) {
            }
        }

        long currentTime = System.currentTimeMillis();
        Long lastSpawnTime = itemAnimationTimes.getOrDefault(particleTrailId, 0L);

        if (currentTime - lastSpawnTime > 100) {
            java.util.Random rand = player.level.random;

            int particleCount = 1 + rand.nextInt(2);
            for (int i = 0; i < particleCount; i++) {
                double spawnX = (rand.nextDouble() - 0.5) * 0.4;
                double spawnY = 0.2 + (rand.nextDouble() - 0.5) * 0.2;
                double spawnZ = -0.3 - rand.nextDouble() * 0.3;

                double vx = (rand.nextDouble() - 0.5) * 0.02;
                double vy = 0.02 + rand.nextDouble() * 0.02;
                double vz = (rand.nextDouble() - 0.5) * 0.02;

                guiParticles.add(new GuiParticle(spawnX, spawnY, spawnZ, vx, vy, vz, 40 + rand.nextInt(20), color));
            }

            itemAnimationTimes.put(particleTrailId, currentTime);
        }

        if (!guiParticles.isEmpty()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();

            for (GuiParticle p : guiParticles) {
                poseStack.pushPose();
                poseStack.translate(p.x, p.y, p.z);

                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(-rotationPitch));
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(-rotationYaw));

                float alpha = 1.0f - (float) p.age / p.lifetime;
                RenderSystem.setShaderColor(p.color[0], p.color[1], p.color[2], alpha);

                float size = p.scale * 0.1f;
                float halfSize = size / 2.0f;

                GuiComponent.fill(poseStack, (int) (-halfSize * 20), -1, (int) (halfSize * 20), 1, 0xFFFFFFFF);
                GuiComponent.fill(poseStack, -1, (int) (-halfSize * 20), 1, (int) (halfSize * 20), 0xFFFFFFFF);

                poseStack.popPose();
            }

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableDepthTest();
        }
    }

    private double getGuiScaleFactor() {
        return 2.0 / mc.getWindow().getGuiScale();
    }

    private int getSlotSize() {
        int s = (int) (22 * getGuiScaleFactor());
        return Math.max(16, Math.min(32, s));
    }

    private int getSlotSpacing() {
        int s = (int) (4 * getGuiScaleFactor());
        return Math.max(2, Math.min(6, s));
    }

    private int getSlotY() {
        int margin = Math.max(1, (int) (height * 0.01));
        return startY + height - margin - getSlotSize();
    }
}
