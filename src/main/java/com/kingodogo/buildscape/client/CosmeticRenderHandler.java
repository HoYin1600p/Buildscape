package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.cosmetics.CosmeticRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

/**
 * Handles rendering of cosmetic items as overlays on the player.
 * Cosmetics render on top of vanilla armor as a separate layer.
 * Supports a toggle to render only cosmetics (hide vanilla armor).
 */
@Mod.EventBusSubscriber(modid = com.kingodogo.buildscape.BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CosmeticRenderHandler {

    // Toggle for cosmetics-only mode (hide vanilla armor)
    private static boolean renderCosmeticsOnly = false;

    // Flag to prevent recursive rendering
    private static final ThreadLocal<Boolean> isRenderingCosmetics = new ThreadLocal<>();

    // Store cosmetics to render as overlay (per frame)
    private static final ThreadLocal<Map<Integer, String>> cosmeticsToRender = new ThreadLocal<>();

    /**
     * Set whether to render only cosmetics (hide vanilla armor).
     */
    public static void setRenderCosmeticsOnly(boolean only) {
        renderCosmeticsOnly = only;
    }

    /**
     * Get whether to render only cosmetics.
     */
    public static boolean isRenderCosmeticsOnly() {
        return renderCosmeticsOnly;
    }

    /**
     * Render cosmetics as overlay on top of vanilla armor after player rendering.
     * This creates a separate layer that renders on top of everything.
     */
    // @SubscribeEvent
    public static void onRenderPlayerPost(RenderLivingEvent.Post<? extends LivingEntity, ?> event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) {
            return;
        }

        // Skip if we're already rendering cosmetics overlay (prevents recursion)
        if (Boolean.TRUE.equals(isRenderingCosmetics.get())) {
            return;
        }

        // Only render for the local player
        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player) {
            return;
        }

        // Get cosmetics to render
        Map<Integer, String> equippedCosmetics = cosmeticsToRender.get();
        if (equippedCosmetics == null || equippedCosmetics.isEmpty()) {
            cosmeticsToRender.remove();
            return;
        }

        // Render cosmetics overlay on top of vanilla gear without removing vanilla
        renderCosmeticsOverlay(event, player, equippedCosmetics);

        cosmeticsToRender.remove();
    }

    /**
     * Prepare for player rendering.
     * Store cosmetics and vanilla armor.
     * If "cosmetics only" mode, hide vanilla armor.
     * Otherwise, keep vanilla armor and render cosmetics as overlay in Post.
     */
    // @SubscribeEvent
    public static void onRenderPlayerPre(RenderLivingEvent.Pre<? extends LivingEntity, ?> event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) {
            return;
        }

        // Ensure cosmetics-only toggle is off (UI toggle removed).
        renderCosmeticsOnly = false;

        // Skip if we're already rendering cosmetics overlay (prevents recursion)
        if (Boolean.TRUE.equals(isRenderingCosmetics.get())) {
            return;
        }

        // Only apply cosmetics for the local player
        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player) {
            return;
        }

        // Prefer the current tab state (reflects immediate GUI equips); fall back to
        // config
        Map<Integer, String> equippedCosmetics = com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersTabState
                .getInstance()
                .getEquippedCosmeticsBySlot();
        if (equippedCosmetics == null || equippedCosmetics.isEmpty()) {
            CosmeticsConfig config = CosmeticsConfig.get();
            equippedCosmetics = config.getEquippedCosmetics(player.getUUID());
        }

        if (equippedCosmetics.isEmpty()) {
            return;
        }

        // Store cosmetics for overlay rendering
        cosmeticsToRender.set(equippedCosmetics);
    }

    /**
     * Render cosmetics as overlay using RenderLevelStageEvent.
     * This happens after all entities are rendered, so we can render cosmetics on
     * top.
     */
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        // No-op: overlay is now handled entirely in the Post event
    }

    /**
     * Restore original equipment.
     */
    private static void renderCosmeticsOverlay(RenderLivingEvent.Post<? extends LivingEntity, ?> event,
                                               AbstractClientPlayer player,
                                               Map<Integer, String> equippedCosmetics) {
        Minecraft mc = Minecraft.getInstance();
        net.minecraft.client.renderer.entity.EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        PlayerRenderer playerRenderer = (PlayerRenderer) dispatcher.getRenderer(player);
        if (playerRenderer == null)
            return;

        // Store current equipment (vanilla)
        ItemStack[] original = new ItemStack[4];
        original[0] = player.getItemBySlot(EquipmentSlot.HEAD).copy();
        original[1] = player.getItemBySlot(EquipmentSlot.CHEST).copy();
        original[2] = player.getItemBySlot(EquipmentSlot.LEGS).copy();
        original[3] = player.getItemBySlot(EquipmentSlot.FEET).copy();

        // Apply cosmetics into slots (temporarily)
        CosmeticRegistry registry = CosmeticRegistry.getInstance();
        com.kingodogo.buildscape.cosmetics.CosmeticManager cosmeticManager = com.kingodogo.buildscape.cosmetics.CosmeticManager
                .getInstance();

        // Track custom head cosmetics to render separately
        String customHeadCosmetic = null;

        // FIRST: Clear head slot if we have a custom HEAD cosmetic to prevent any
        // helmet from showing
        for (Map.Entry<Integer, String> entry : equippedCosmetics.entrySet()) {
            String cosmeticId = entry.getValue();
            if (cosmeticId == null || cosmeticId.isEmpty())
                continue;

            // Check if this is a custom HEAD cosmetic
            com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticMetadata metadata = cosmeticManager
                    .getMetadata(cosmeticId);
            if (metadata != null
                    && metadata.type() == com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticType.HEAD) {
                if (entry.getKey() == 0) { // Slot 0 is head
                    customHeadCosmetic = cosmeticId;
                    // Overlay mode: Do NOT clear the head slot. Let vanilla helmet render.
                    break; // Found custom head cosmetic, exit early
                }
            }
        }

        // SECOND: Apply all other cosmetics (non-HEAD cosmetics)
        for (Map.Entry<Integer, String> entry : equippedCosmetics.entrySet()) {
            String cosmeticId = entry.getValue();
            if (cosmeticId == null || cosmeticId.isEmpty())
                continue;

            // Skip custom HEAD cosmetics - they're handled separately
            com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticMetadata metadata = cosmeticManager
                    .getMetadata(cosmeticId);
            if (metadata != null
                    && metadata.type() == com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticType.HEAD) {
                continue; // Skip ItemStack resolution for custom head cosmetics
            }

            ItemStack cosmeticStack = registry.resolveToItemStack(cosmeticId);
            if (cosmeticStack == null || cosmeticStack.isEmpty())
                continue;
            EquipmentSlot slot = getSlotForIndex(entry.getKey());
            if (slot != null) {
                setItemSlotSilent(player, slot, cosmeticStack);
            }
        }

        try {
            isRenderingCosmetics.set(true);

            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            int packedLight = dispatcher.getPackedLightCoords(player, event.getPartialTick());
            float partialTick = event.getPartialTick();

            poseStack.pushPose();

            // Render as a second layer: keep depth test AND depth writes so the layer
            // correctly occludes with the world and particles. Blend stays enabled for
            // semi-transparent pieces but we write depth to avoid "behind-screen"
            // artifacts.
            com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
            com.mojang.blaze3d.systems.RenderSystem.depthMask(true);
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();

            playerRenderer.render(player, player.getYRot(), partialTick, poseStack, bufferSource, packedLight);

            bufferSource.endBatch();
            poseStack.popPose();

            com.mojang.blaze3d.systems.RenderSystem.disableBlend();
            com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();

        } catch (Exception e) {
            com.kingodogo.buildscape.BuildScape.getLogger()
                    .debug("Failed to render cosmetic overlay: " + e.getMessage());
        } finally {
            isRenderingCosmetics.remove();
            // Restore original vanilla equipment
            setItemSlotSilent(player, EquipmentSlot.HEAD, original[0]);
            setItemSlotSilent(player, EquipmentSlot.CHEST, original[1]);
            setItemSlotSilent(player, EquipmentSlot.LEGS, original[2]);
            setItemSlotSilent(player, EquipmentSlot.FEET, original[3]);
        }
    }

    /**
     * Get EquipmentSlot from slot index.
     */
    private static EquipmentSlot getSlotForIndex(int slotIndex) {
        switch (slotIndex) {
            case 0:
                return EquipmentSlot.HEAD;
            case 1:
                return EquipmentSlot.CHEST;
            case 2:
                return EquipmentSlot.LEGS;
            case 3:
                return EquipmentSlot.FEET;
            default:
                return null;
        }
    }

    /**
     * Set item slot silently without triggering sounds or events.
     * Directly modifies the inventory array to bypass sound triggers.
     */
    private static void setItemSlotSilent(AbstractClientPlayer player, EquipmentSlot slot, ItemStack stack) {
        if (player == null)
            return;

        // Use direct inventory access to avoid triggering sounds
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

    private static void renderCustomHeadCosmetic(
            AbstractClientPlayer player,
            PlayerRenderer playerRenderer,
            String cosmeticId,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            float partialTick) {
        try {
            // Position the model on the player's head
            poseStack.pushPose();

            // Link to the head bone using the vanilla model
            // This ensures it follows sneaking, looking, animations, etc.
            playerRenderer.getModel().head.translateAndRotate(poseStack);

            // Ensure renderer is initialized
            UniversalCosmeticRenderer.init(Minecraft.getInstance().getEntityModels());

            // Universal render
            UniversalCosmeticRenderer.renderHeadCosmetic(cosmeticId, poseStack, bufferSource, packedLight, player);

            poseStack.popPose();
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildScape.getLogger()
                    .debug("Failed to render custom head cosmetic: " + e.getMessage());
        }
    }
}
