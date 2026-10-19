package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.network.PacketDistributor;

/**
 * Server-side permission check for the edit packets. It applies the same {@link ConfigSetting} rule the
 * config screen applies, and a refused sender is told why and given the server's current state again.
 */
public final class EditGuard {
    private EditGuard() {
    }

    public static int permissionLevel(ServerPlayer player) {
        return ConfigSetting.permissionLevel(player.hasPermissions(2),
                player.hasPermissions(PillarParticleConfig.CONFIG_PERMISSION_LEVEL));
    }

    public static boolean allows(ServerPlayer player, ConfigSetting setting) {
        return setting.canEdit(PillarParticleConfig.getServerConfig().edit_permission_mode, permissionLevel(player));
    }

    /** True when the sender may perform the operation; otherwise refuses it and resyncs the sender. */
    public static boolean require(ServerPlayer player, ConfigSetting setting) {
        if (allows(player, setting)) return true;
        player.displayClientMessage(new TextComponent(refusalText(setting)).withStyle(ChatFormatting.RED), false);
        resync(player, setting);
        return false;
    }

    static String refusalText(ConfigSetting setting) {
        return setting == ConfigSetting.POLICY
                ? "BuildScape: Permission level 4 is required to change the edit policy."
                : "BuildScape: This setting is locked by the server edit policy.";
    }

    private static void resync(ServerPlayer player, ConfigSetting setting) {
        try {
            ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncConfigPacket(PillarParticleConfig.getServerConfig()));
            if (setting == ConfigSetting.WORLD_RULES) {
                GameRules rules = player.getLevel().getGameRules();
                ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SyncGameRulesPacket(
                        rules.getBoolean(ModGameRules.FAST_LEAF_DECAY),
                        rules.getBoolean(ModGameRules.DISABLE_ENDERMAN_GRIEFING),
                        rules.getBoolean(ModGameRules.DISABLE_CREEPER_GRIEFING),
                        rules.getBoolean(ModGameRules.DISABLE_GHAST_GRIEFING),
                        rules.getBoolean(ModGameRules.IS_CAKE_STACK),
                        rules.getBoolean(ModGameRules.IS_WATER_BOTTLE_STACK)));
            } else if (setting == ConfigSetting.PILLAR_EDIT || setting == ConfigSetting.PILLAR_BULK) {
                PillarIdManager manager = PillarIdManager.get();
                if (manager.hasLoaded()) SyncPillarIdsPacket.sendToPlayer(player, manager.getAllPillarDataForSync());
            }
        } catch (RuntimeException e) {
            com.kingodogo.buildscape.BuildScape.LOGGER.warn("Unable to resync {} after a refused edit", player.getGameProfile().getName(), e);
        }
    }
}
