package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.block.LeafHedgeBlock;
import com.kingodogo.buildscape.block.PillarBlockEntity;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.event.ItemFrameParticleHandler;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import com.kingodogo.buildscape.block.CustomSoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public class ClientEvents {

    private static Component overlayMessage = null;
    private static long overlayMessageTime = 0;
    private static int lastHedgeStep = -1;

    public static void setOverlayMessage(Component message) {
        overlayMessage = message;
        overlayMessageTime = System.currentTimeMillis();
    }

    public static Component getOverlayMessage() {
        return overlayMessage;
    }

    public static long getOverlayMessageTime() {
        return overlayMessageTime;
    }

    public static void renderOverlay(com.mojang.blaze3d.vertex.PoseStack poseStack, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();

        if (overlayMessage != null) {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - overlayMessageTime;

            if (elapsed > 5000) {
                overlayMessage = null;
                return;
            }

            int x = screenWidth / 2;
            int y = screenHeight / 2 + 30;

            poseStack.pushPose();
            poseStack.translate(0, 0, 500);

            float elapsedSeconds = elapsed / 1000.0f;
            float scale = 1.0f;

            if (elapsedSeconds < 0.25f) {
                scale = (elapsedSeconds / 0.25f) * 1.2f;
            } else if (elapsedSeconds < 0.4f) {
                scale = 1.2f - ((elapsedSeconds - 0.25f) / 0.15f) * 0.2f;
            }

            poseStack.translate(x, y, 0);
            poseStack.scale(scale, scale, 1.0f);
            poseStack.translate(-x, -y, 0);

            Services.PLATFORM.beginGuiOverlayRender();
            String msg = overlayMessage.getString();
            int textWidth = mc.font.width(msg);
            Services.PLATFORM.drawShadow(poseStack, mc.font, msg, x - textWidth / 2, y, 0xFFFF5555);
            Services.PLATFORM.endGuiOverlayRender();

            poseStack.popPose();
        }
    }

    public static void renderLevel(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.Camera camera, Object bufferSource) {
        InvisibleFrameOverlayRenderer.renderOverlay(poseStack, camera, bufferSource);
        MuffBlockRenderer.renderMuffOutlines(poseStack, camera, bufferSource);
        PillarMarkerRenderer.renderPillarMarkers(poseStack, camera, bufferSource);
        renderPlacementPreview(poseStack, camera, bufferSource);
    }

    public static boolean renderBlockHighlight(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.Camera camera, Object bufferSource, net.minecraft.world.phys.HitResult target) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return false;
        if (target instanceof net.minecraft.world.phys.BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            BlockState state = mc.level.getBlockState(pos);
            if (WrenchClientHandler.shouldCancelBlockHighlight(mc.player, target)) {
                WrenchClientHandler.renderWrenchHighlight(poseStack, bufferSource, camera, pos, state, mc.player);
                return true;
            }
            if (TreeChopHandler.shouldCancelHighlight(mc.player, target)) {
                TreeChopHandler.renderConnectedLogHighlights(poseStack, bufferSource, camera, pos, state);
                return true;
            }
        }
        return HammerClientHandler.renderBlockHighlight(poseStack, camera, bufferSource, target);
    }

    private static void renderPlacementPreview(
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            net.minecraft.client.Camera camera,
            Object bufferSource
    ) {
        int opacityPct = com.kingodogo.buildscape.config.BuildscapeClientConfig.get().getPlacementPreviewOpacity();
        if (opacityPct <= 0) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || camera == null) return;
        if (!(mc.hitResult instanceof net.minecraft.world.phys.BlockHitResult blockHit)
                || mc.hitResult.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK) {
            return;
        }
        net.minecraft.world.InteractionHand hand = net.minecraft.world.InteractionHand.MAIN_HAND;
        net.minecraft.world.item.ItemStack held = mc.player.getMainHandItem();
        if (!isPreviewableBlockItem(held)) {
            hand = net.minecraft.world.InteractionHand.OFF_HAND;
            held = mc.player.getOffhandItem();
            if (!isPreviewableBlockItem(held)) {
                return;
            }
        }
        net.minecraft.world.item.context.BlockPlaceContext placeContext =
                new net.minecraft.world.item.context.BlockPlaceContext(mc.player, hand, held, blockHit);
        if (!placeContext.canPlace()) {
            return;
        }
        net.minecraft.world.level.block.Block block = ((net.minecraft.world.item.BlockItem) held.getItem()).getBlock();
        BlockPos placePos = placeContext.getClickedPos();
        BlockState previewState;
        if (block instanceof com.kingodogo.buildscape.block.VerticalStairBlock stairBlock) {
            net.minecraft.core.Direction facing = com.kingodogo.buildscape.block.VerticalStairBlock.getPlacementFacing(
                    placePos, blockHit.getDirection(), blockHit.getLocation());
            previewState = stairBlock.defaultBlockState()
                    .setValue(com.kingodogo.buildscape.block.VerticalStairBlock.FACING, facing);
        } else if (block instanceof com.kingodogo.buildscape.block.VerticalSlabBlock slabBlock) {
            previewState = slabBlock.getStateForPlacement(placeContext);
            if (previewState == null) return;
            if (previewState.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED)) {
                previewState = previewState.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED, false);
            }
        } else {
            return;
        }

        net.minecraft.world.phys.Vec3 cameraPos = com.kingodogo.buildscape.platform.Services.PLATFORM.getCameraPosition(camera);
        poseStack.pushPose();
        poseStack.translate(placePos.getX() - cameraPos.x, placePos.getY() - cameraPos.y, placePos.getZ() - cameraPos.z);
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(0.96875f, 0.96875f, 0.96875f);
        poseStack.translate(-0.5, -0.5, -0.5);
        activePreviewAlpha = computePreviewAlpha(opacityPct);
        try {
            com.kingodogo.buildscape.platform.Services.PLATFORM.renderFallingIcicleBlock(
                    mc.level, previewState, placePos, placePos, poseStack, bufferSource);
        } finally {
            activePreviewAlpha = 1.0f;
            poseStack.popPose();
        }
    }

    private static float computePreviewAlpha(int opacityPct) {
        if (opacityPct <= 0) return 0.0f;
        if (opacityPct >= 100) return 1.0f;
        if (opacityPct <= 50) {
            float t = (opacityPct - 1) / 49.0f;
            return 0.18f + t * (0.50f - 0.18f);
        }
        float t = (opacityPct - 50) / 49.0f;
        return 0.50f + t * (0.85f - 0.50f);
    }

    private static volatile float activePreviewAlpha = 1.0f;

    public static float getActivePreviewAlpha() {
        return activePreviewAlpha;
    }

    private static boolean isPreviewableBlockItem(net.minecraft.world.item.ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem)) {
            return false;
        }
        net.minecraft.world.level.block.Block b = blockItem.getBlock();
        return b instanceof com.kingodogo.buildscape.block.VerticalStairBlock
                || b instanceof com.kingodogo.buildscape.block.VerticalSlabBlock;
    }

    public static boolean onKeyInput(int key, int action) {
        return WrenchClientHandler.handleKeyInput(key, action);
    }

    public static void onRightClick() {
        HammerClientHandler.onRightClick();
    }

    public static void resetAllPillarParticles() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        BlockPos playerPos = mc.player.blockPosition();
        int chunkX = playerPos.getX() >> 4;
        int chunkZ = playerPos.getZ() >> 4;

        for (int x = -8; x <= 8; x++) {
            for (int z = -8; z <= 8; z++) {
                LevelChunk chunk = mc.level.getChunk(chunkX + x, chunkZ + z);
                if (chunk != null) {
                    for (BlockEntity be : chunk.getBlockEntities().values()) {
                        if (be instanceof PillarBlockEntity pillarBE && pillarBE.hasDisplayItem()) {
                            pillarBE.resetParticleTick(true);
                        }
                    }
                }
            }
        }
    }

    public static void initializeConfigCallback() {
        PillarParticleConfig.addConfigReloadCallback((isRemote) -> {
            if (Minecraft.getInstance().level != null) {
                Minecraft.getInstance().execute(ClientEvents::resetAllPillarParticles);
            }
        });
        com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.warmupAsync();
    }

    public static void onClientTick() {
        com.kingodogo.buildscape.network.SyncPillarIdsPacket.expireClientState();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && !com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.isItemsCached()) {
            com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen.warmupAsync();
        }
        if (mc.level == null || mc.player == null) {
            return;
        }
        ModKeyBinds.tick();
        if (mc.level == null || mc.player == null || mc.isPaused()) {
            return;
        }

        TreeChopHandler.clientTick();
        SmokeVentParticleHandler.clientTick();
        ItemFrameParticleHandler.clientTick(mc.level, mc.player, mc.level.getGameTime());
        BiomeBrushClientHandler.tickClient(mc.player);

        Player player = mc.player;
        if (Services.PLATFORM.isOnGround(player)) {
            BlockPos playerBlockPos = player.blockPosition();
            BlockPos blockBelowPlayer = playerBlockPos.below();
            BlockState blockBelow = mc.level.getBlockState(blockBelowPlayer);

            if (blockBelow.getBlock() instanceof LeafHedgeBlock) {
                double playerY = player.getY();
                double blockTopY = blockBelowPlayer.getY() + 1.0;

                if (playerY >= blockTopY - 0.3 && playerY <= blockTopY + 0.5) {
                    float stepInterval = 2.0f;
                    int currentStep = (int) (Services.PLATFORM.getWalkDistance(player) / stepInterval);

                    if (currentStep != lastHedgeStep && Services.PLATFORM.isWalkAnimationMoving(player)) {
                        lastHedgeStep = currentStep;

                        CustomSoundType sounds = CustomSoundType.GRASS;
                        net.minecraft.sounds.SoundEvent stepSound = sounds.getStepSound();
                        float volume = 0.15f;
                        float pitch = 1.0f;

                        if (sounds instanceof com.kingodogo.buildscape.block.CustomSoundType customSounds) {
                            volume = customSounds.getStepVolume();
                            pitch = customSounds.getStepPitch();
                        }

                        mc.level.playLocalSound(
                                blockBelowPlayer.getX() + 0.5,
                                blockBelowPlayer.getY() + 0.5,
                                blockBelowPlayer.getZ() + 0.5,
                                stepSound,
                                SoundSource.BLOCKS,
                                volume,
                                pitch,
                                false
                        );
                    }
                }
            } else {
                if (lastHedgeStep != -1) {
                    lastHedgeStep = -1;
                }
            }
        }
    }

    public static void onClientDisconnect() {
        onClientWorldUnload();
        overlayMessage = null;
        overlayMessageTime = 0;
        lastHedgeStep = -1;

        PillarParticleConfig.clearServerConfig();
        com.kingodogo.buildscape.config.PillarIdManager.resetClientCache();
        com.kingodogo.buildscape.network.SyncPillarIdsPacket.clearClientState();
        MuffBlockManager.clear();
    }

    public static void onClientWorldUnload() {
        com.kingodogo.buildscape.client.biome.ClientBiomeTints.clear();
        TreeChopHandler.reset();
        overlayMessage = null;
        overlayMessageTime = 0;
        lastHedgeStep = -1;

        try {
            com.kingodogo.buildscape.client.renderer.PillarBlockEntityRenderer.clearEntityCache();
            com.kingodogo.buildscape.client.renderer.ArmorPillarRenderer.clearAllCaches();
            com.kingodogo.buildscape.particle.TintedParticleColorTracker.clear();
            com.kingodogo.buildscape.event.ItemFrameParticleHandler.clearCaches();
            MuffBlockManager.clear();
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Error clearing client caches on world unload", e);
        }
    }
}
