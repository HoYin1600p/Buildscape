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
        PillarParticleConfig config = PillarParticleConfig.getServerConfig();
        if (PillarParticleConfig.canPlayerEdit(player, config.edit_permission_mode, ConfigSetting.PILLAR_EDIT.scope)) return true;
        Services.PLATFORM.sendActionBarMessage(player,
                ComponentHelper.literal("BuildScape: This setting is locked by the server edit policy.").withStyle(ChatFormatting.RED));
        try {
            PacketFactory.sendToPlayer(player, new SyncConfigPacket(config, player));
            PillarIdManager manager = PillarIdManager.get();
            if (manager.hasLoaded()) SyncPillarIdsPacket.sendToPlayer(player, manager.getAllPillarDataForSync());
        } catch (RuntimeException exception) {
            BuildscapeCommon.LOGGER.warn("Unable to resync after a refused pillar edit", exception);
        }
        return false;
    }
}
