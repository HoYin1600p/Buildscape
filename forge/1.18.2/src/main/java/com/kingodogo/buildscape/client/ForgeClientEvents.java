package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.adapter.v118x.client.HazeBushParticleHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildscapeCommon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ForgeClientEvents {
    private ForgeClientEvents() {}

    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public static void tooltip(net.minecraftforge.event.entity.player.ItemTooltipEvent event) {
        com.kingodogo.buildscape.event.TagTooltipHandler.handleItemTooltip(event.getItemStack(), event.getToolTip());
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        ClientEvents.onClientTick();
        HazeBushParticleHandler.clientTick();
        GeyserParticleHandler.onClientTick();
    }

    @SubscribeEvent
    public static void keys(InputEvent.KeyInputEvent event) { ClientEvents.onKeyInput(event.getKey(), event.getAction()); }

    @SubscribeEvent
    public static void mouse(InputEvent.MouseInputEvent event) {
        if (event.getButton() == 1 && event.getAction() == 1) ClientEvents.onRightClick();
    }

    @SubscribeEvent
    public static void scroll(InputEvent.MouseScrollEvent event) {
        if (Minecraft.getInstance().screen == null && ZoomHandler.isZooming()) {
            ZoomHandler.handleScroll(event.getScrollDelta());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void advancementScroll(net.minecraftforge.client.event.ScreenEvent.MouseScrollEvent.Pre event) {
        if (event.getScreen() instanceof net.minecraft.client.gui.screens.advancements.AdvancementsScreen screen) {
            net.minecraft.client.gui.screens.advancements.AdvancementTab tab =
                    net.minecraftforge.fml.util.ObfuscationReflectionHelper.getPrivateValue(
                            net.minecraft.client.gui.screens.advancements.AdvancementsScreen.class, screen, "f_97316_");
            ClientAdvancementEvents.pan(tab, 0.0D, event.getScrollDelta());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void interaction(InputEvent.ClickInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.isAttack()) BiomeBrushClientHandler.onAttack(mc.player, mc.hitResult);
        if (event.isAttack() && mc.screen == null && TreeChopHandler.shouldCancelLeftClick(mc.player, mc.hitResult)) {
            event.setCanceled(true);
            event.setSwingHand(true);
        }
    }

    @SubscribeEvent
    public static void highlight(DrawSelectionEvent.HighlightBlock event) {
        if (ClientEvents.renderBlockHighlight(event.getPoseStack(), event.getCamera(), event.getMultiBufferSource(), event.getTarget())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void fov(EntityViewRenderEvent.FieldOfView event) { event.setFOV(event.getFOV() * ZoomHandler.getZoomLevel()); }

    @SubscribeEvent
    public static void camera(EntityViewRenderEvent.CameraSetup event) {
        float[] angles = ZoomHandler.getSmoothedRotation(event.getYaw(), event.getPitch());
        event.setYaw(angles[0]);
        event.setPitch(angles[1]);
    }

    @SubscribeEvent
    public static void fluidFogColor(EntityViewRenderEvent.FogColors event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null && com.kingodogo.buildscape.fluid.ForgeExperienceFluids
                .isExperience(mc.level.getFluidState(event.getCamera().getBlockPosition()).getType())) {
            event.setRed(0.3F);
            event.setGreen(0.9F);
            event.setBlue(0.1F);
        }
    }

    @SubscribeEvent
    public static void fluidFogDistance(EntityViewRenderEvent.RenderFogEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null && com.kingodogo.buildscape.fluid.ForgeExperienceFluids
                .isExperience(mc.level.getFluidState(event.getCamera().getBlockPosition()).getType())) {
            event.setFarPlaneDistance(12.0F);
            event.setNearPlaneDistance(0.5F);
        }
    }

    @SubscribeEvent
    public static void overlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        ClientEvents.renderOverlay(event.getMatrixStack(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
        PillarOverlayHandler.renderOverlay(event.getMatrixStack());
        Minecraft mc = Minecraft.getInstance();
        String text = TreeChopHandler.getOverlayText(mc.player, mc.hitResult);
        if (text != null && !text.isEmpty()) {
            net.minecraft.client.gui.GuiComponent.drawCenteredString(event.getMatrixStack(), mc.font, text,
                    event.getWindow().getGuiScaledWidth() / 2, event.getWindow().getGuiScaledHeight() / 2 + 30, 0xFFFFFF);
        }
    }

    @SubscribeEvent
    public static void worldOverlays(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        var buffers = mc.renderBuffers().bufferSource();
        ClientEvents.renderLevel(event.getPoseStack(), event.getCamera(), buffers);
        buffers.endBatch();
    }

    @SubscribeEvent
    public static void disconnect(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientEvents.onClientDisconnect();
        HazeBushParticleHandler.clear();
    }

    @SubscribeEvent
    public static void unload(WorldEvent.Unload event) {
        if (event.getWorld().isClientSide()) {
            ClientEvents.onClientWorldUnload();
            HazeBushParticleHandler.clear();
        }
    }

    @SubscribeEvent
    public static void chunkLoad(ChunkEvent.Load event) {
        if (event.getWorld() != null && event.getWorld().isClientSide() && event.getChunk() instanceof LevelChunk chunk) {
            HazeBushParticleHandler.onChunkLoad(chunk);
        }
    }

    @SubscribeEvent
    public static void chunkUnload(ChunkEvent.Unload event) {
        if (event.getWorld() != null && event.getWorld().isClientSide() && event.getChunk() instanceof LevelChunk chunk) {
            HazeBushParticleHandler.onChunkUnload(chunk);
        }
    }
}
