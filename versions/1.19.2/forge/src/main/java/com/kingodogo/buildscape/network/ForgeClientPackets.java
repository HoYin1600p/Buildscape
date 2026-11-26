package com.kingodogo.buildscape.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Loaded only by the Dist.CLIENT packet dispatch; no client registrations in S7a. */
@OnlyIn(Dist.CLIENT)
final class ForgeClientPackets {
    private ForgeClientPackets() {}
    static void handle(CommonPacket packet, Player player) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> packet.handle(player == null ? client.player : player));
    }
}
