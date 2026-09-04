package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.block.LeafHedgeBlock;
import com.kingodogo.buildscape.block.PillarBlockEntity;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = com.kingodogo.buildscape.BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class ClientEvents {

    private static boolean wasPressed = false;
    private static boolean wasZoomKeyPressed = false;

    private static Component overlayMessage = null;
    private static long overlayMessageTime = 0;
    private static final long OVERLAY_DURATION = 5000;

    private static int lastHedgeStep = -1;

    public static void setOverlayMessage(Component message) {
        overlayMessage = message;
        overlayMessageTime = System.currentTimeMillis();
    }

    public static void resetAllPillarParticles() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        net.minecraft.core.BlockPos playerPos = mc.player.blockPosition();
        int chunkX = playerPos.getX() >> 4;
        int chunkZ = playerPos.getZ() >> 4;

        for (int x = -8; x <= 8; x++) {
            for (int z = -8; z <= 8; z++) {
                net.minecraft.world.level.chunk.LevelChunk chunk = mc.level.getChunk(
                        chunkX + x,
                        chunkZ + z
                );
                if (chunk != null) {
                    for (BlockEntity be : chunk.getBlockEntities().values()) {
                        if (
                                be instanceof PillarBlockEntity pillarBE &&
                                        pillarBE.hasDisplayItem()
                        ) {
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
                Minecraft.getInstance()
                        .execute(() -> {
                            resetAllPillarParticles();
                        });
            }
        });
    }

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();

        renderOverlay(event.getMatrixStack(), screenWidth, screenHeight);
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

            com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();
            GuiComponent.drawCenteredString(
                    poseStack,
                    mc.font,
                    overlayMessage,
                    x,
                    y,
                    0xFFFF5555
            );
            com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();

            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }


        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) {
            return;
        }


        net.minecraft.world.entity.player.Player player = mc.player;
        if (player.isOnGround()) {
            BlockPos playerBlockPos = player.blockPosition();
            BlockPos blockBelowPlayer = playerBlockPos.below();
            BlockState blockBelow = mc.level.getBlockState(blockBelowPlayer);


            if (blockBelow.getBlock() instanceof LeafHedgeBlock) {
                double playerY = player.getY();
                double blockTopY = blockBelowPlayer.getY() + 1.0;

                if (playerY >= blockTopY - 0.3 && playerY <= blockTopY + 0.5) {
                    float stepInterval = 2.0f;
                    int currentStep = (int) (player.walkDist / stepInterval);

                    if (
                            currentStep != lastHedgeStep && player.walkDist > player.walkDistO
                    ) {
                        lastHedgeStep = currentStep;

                        SoundType sounds = blockBelow.getSoundType();
                        net.minecraft.sounds.SoundEvent stepSound = sounds.getStepSound();
                        float volume = 0.15f;
                        float pitch = 1.0f;

                        if (
                                sounds instanceof com.kingodogo.buildscape.block.CustomSoundType customSounds
                        ) {
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

    @SubscribeEvent
    public static void onClientDisconnect(
            ClientPlayerNetworkEvent.LoggedOutEvent event
    ) {
        overlayMessage = null;
        overlayMessageTime = 0;
        wasPressed = false;
        lastHedgeStep = -1;

        wasZoomKeyPressed = false;

        com.kingodogo.buildscape.config.PillarIdManager.resetWorldCache();

        com.kingodogo.buildscape.config.PillarParticleConfig.clearServerConfig();
        com.kingodogo.buildscape.config.PillarIdManager.fullReset();
        com.kingodogo.buildscape.client.MuffBlockManager.clear();
    }

    @SubscribeEvent
    public static void onClientWorldUnload(WorldEvent.Unload event) {
        overlayMessage = null;
        overlayMessageTime = 0;
        wasPressed = false;
        wasZoomKeyPressed = false;
        lastHedgeStep = -1;

        try {
            com.kingodogo.buildscape.client.renderer.PillarBlockEntityRenderer.clearEntityCache();
            com.kingodogo.buildscape.particle.TintedDripParticle.clearColorCache();
            com.kingodogo.buildscape.event.ItemFrameParticleHandler.clearCaches();
            com.kingodogo.buildscape.config.PillarParticleConfig.clearServerConfig();
            com.kingodogo.buildscape.client.MuffBlockManager.clear();
        } catch (Exception e) {
            System.err.println(
                    "BuildScape: Error clearing caches on world unload: " + e.getMessage()
            );
        }
    }
}
