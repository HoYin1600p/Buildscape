package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;

public final class EditGuard {
    private EditGuard() {}

    public static boolean requirePillarEdit(ServerPlayer player) {
        return require(player, ConfigSetting.PILLAR_EDIT);
    }

    public static boolean require(ServerPlayer player, ConfigSetting setting) {
        PillarParticleConfig config = PillarParticleConfig.getServerConfig();
        if (PillarParticleConfig.canPlayerEdit(player, config.edit_permission_mode, setting.scope)) return true;
        Services.PLATFORM.sendActionBarMessage(player,
                ComponentHelper.literal("BuildScape: This setting is locked by the server edit policy.").withStyle(ChatFormatting.RED));
        try {
            PacketFactory.sendToPlayer(player, new EditRefusedPacket(setting));
            PacketFactory.sendToPlayer(player, new SyncConfigPacket(config, player));
            if (setting == ConfigSetting.PILLAR_EDIT) {
                PillarIdManager manager = PillarIdManager.get();
                if (manager.hasLoaded()) SyncPillarIdsPacket.sendToPlayer(player, manager.getAllPillarDataForSync());
            } else if (setting == ConfigSetting.WORLD_RULES) {
                PacketFactory.sendToPlayer(player, com.kingodogo.buildscape.world.ModGameRules.snapshot(Services.PLATFORM.getEntityLevel(player)));
            }
        } catch (RuntimeException exception) {
            BuildscapeCommon.LOGGER.warn("Unable to resync after a refused edit", exception);
        }
        return false;
    }
}
