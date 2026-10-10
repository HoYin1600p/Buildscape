package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.block.LeafHedgeBlock;
import com.kingodogo.buildscape.block.PillarBlockEntity;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.SoundType;
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
    }

    public static boolean renderBlockHighlight(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.Camera camera, Object bufferSource, net.minecraft.world.phys.HitResult target) {
        return HammerClientHandler.renderBlockHighlight(poseStack, camera, bufferSource, target);
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
    }

    public static void onClientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) {
            return;
        }

        TreeChopHandler.clientTick();
        SmokeVentParticleHandler.clientTick();
        ZoomHandler.tick();

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

                        SoundType sounds = blockBelow.getSoundType();
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
        overlayMessage = null;
        overlayMessageTime = 0;
        lastHedgeStep = -1;

        PillarParticleConfig.clearServerConfig();
        com.kingodogo.buildscape.config.PillarIdManager.resetClientCache();
        com.kingodogo.buildscape.network.SyncPillarIdsPacket.clearClientState();
        MuffBlockManager.clear();
    }

    public static void onClientWorldUnload() {
        overlayMessage = null;
        overlayMessageTime = 0;
        lastHedgeStep = -1;

        try {
            try {
                Class.forName("com.kingodogo.buildscape.client.renderer.PillarBlockEntityRenderer").getMethod("clearEntityCache").invoke(null);
            } catch (Throwable ignored) {}
            try {
                Class.forName("com.kingodogo.buildscape.client.renderer.ArmorPillarRenderer").getMethod("clearAllCaches").invoke(null);
            } catch (Throwable ignored) {}
            com.kingodogo.buildscape.particle.TintedParticleColorTracker.clear();
            com.kingodogo.buildscape.event.ItemFrameParticleHandler.clearCaches();
            MuffBlockManager.clear();
        } catch (Exception e) {
            System.err.println("BuildScape: Error clearing caches on world unload: " + e.getMessage());
        }
    }
}
