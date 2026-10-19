package com.kingodogo.buildscape.client.network;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.ConfettiBurstClient;
import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.feature.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.feature.homemaker.HomemakerCooldownTracker;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager;
import com.kingodogo.buildscape.network.EditRefusedPacket;
import com.kingodogo.buildscape.network.SyncConfigPacket;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Client-side work of every server-to-client packet.
 *
 * <p>Each packet's {@code handle} reaches this class only through
 * {@code DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.xxx(...))}, so a dedicated
 * server never loads it. Call the methods on the client main thread (inside {@code enqueueWork}).
 */
@OnlyIn(Dist.CLIENT)
public final class ClientPacketHandlers {
    private ClientPacketHandlers() {
    }

    public static void showActionBar(Component message) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(message, true);
        }
    }

    public static void spawnConfettiBurst(double x, double y, double z, float lookX, float lookY, float lookZ,
                                          int burstLevel, long seed) {
        ConfettiBurstClient.spawn(x, y, z, lookX, lookY, lookZ, burstLevel, seed);
    }

    public static void showEditRefusal(EditRefusedPacket.Reason reason, ConfigSetting setting) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof BuildScapeConfigScreen open) {
            open.onServerRefusal(reason, setting);
        }
    }

    public static void applyServerConfig(SyncConfigPacket packet) {
        PillarParticleConfig.setServerConfig(packet);
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof BuildScapeConfigScreen open) {
            open.onServerConfigSynced();
        }
    }

    public static void applyGameRules(boolean fastLeafDecay, boolean disableEndermanGriefing,
                                      boolean disableCreeperGriefing, boolean disableGhastGriefing,
                                      boolean cakeStacking, boolean waterBottleStacking) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            mc.level.getGameRules().getRule(ModGameRules.FAST_LEAF_DECAY).set(fastLeafDecay, null);
            mc.level.getGameRules().getRule(ModGameRules.DISABLE_ENDERMAN_GRIEFING).set(disableEndermanGriefing, null);
            mc.level.getGameRules().getRule(ModGameRules.DISABLE_CREEPER_GRIEFING).set(disableCreeperGriefing, null);
            mc.level.getGameRules().getRule(ModGameRules.DISABLE_GHAST_GRIEFING).set(disableGhastGriefing, null);
            mc.level.getGameRules().getRule(ModGameRules.IS_CAKE_STACK).set(cakeStacking, null);
            mc.level.getGameRules().getRule(ModGameRules.IS_WATER_BOTTLE_STACK).set(waterBottleStacking, null);
            ModGameRules.setClientStackingRules(cakeStacking, waterBottleStacking);
            ModGameRules.clientFastLeafDecay = fastLeafDecay;
            ModGameRules.clientDisableEndermanGriefing = disableEndermanGriefing;
            ModGameRules.clientDisableCreeperGriefing = disableCreeperGriefing;
            ModGameRules.clientDisableGhastGriefing = disableGhastGriefing;
        }
    }

    public static void setHomemakerCooldown(long cooldownEndTime) {
        HomemakerCooldownTracker.cooldownEndTime = cooldownEndTime;
    }

    public static void applySignFrame(BlockPos pos, String frameId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            BlockEntity be = mc.level.getBlockEntity(pos);
            if (be instanceof SignBlockEntity sign) {
                if (frameId.isEmpty()) {
                    sign.getTileData().remove(SignFrameAttachment.NBT_KEY);
                } else {
                    sign.getTileData().putString(SignFrameAttachment.NBT_KEY, frameId);
                }
            }
        }
    }

    /** Stores a complete pillar sync from the server and refreshes an open config screen's pillar tab. */
    public static void replacePillarsFromServer(List<PillarIdManager.PillarData> complete) {
        PillarIdManager.getClient().replaceFromServerSync(complete);
        pillarsSynced();
    }

    /**
     * Tells an open config screen that the pillar list changed. A failing refresh must not break the sync, so a
     * RuntimeException is only logged at debug level.
     */
    public static void pillarsSynced() {
        try {
            BuildScapeConfigScreen.onPillarsSynced();
        } catch (RuntimeException e) {
            BuildScape.LOGGER.debug("Config screen refresh after pillar sync failed", e);
        }
    }
}
