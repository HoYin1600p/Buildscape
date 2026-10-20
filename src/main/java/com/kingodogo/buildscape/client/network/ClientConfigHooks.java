package com.kingodogo.buildscape.client.network;

import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.network.UpdateConfigPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Client-only reads that the shared config code needs.
 *
 * <p>Callers reach this class only after a {@code Dist.CLIENT} check or from inside
 * {@code DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ...)}, so a dedicated server never loads it.
 */
@OnlyIn(Dist.CLIENT)
public final class ClientConfigHooks {
    private ClientConfigHooks() {}

    public static boolean hasLocalServer() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft != null && minecraft.hasSingleplayerServer();
    }

    public static boolean isConnected() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.getConnection() != null && minecraft.level != null;
    }

    /** True when the local player may upload config edits to the server. */
    public static boolean canUploadConfig() {
        return Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.hasPermissions(PillarParticleConfig.CONFIG_PERMISSION_LEVEL);
    }

    public static List<UpdateConfigPacket> splitForUpload(PillarParticleConfig config) {
        LocalPlayer player = Minecraft.getInstance().player;
        return UpdateConfigPacket.splitForClient(config, -1, player != null && player.hasPermissions(2),
                player != null && player.hasPermissions(PillarParticleConfig.CONFIG_PERMISSION_LEVEL));
    }
}
