package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.client.biome.ClientBiomeTints;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record SyncBiomeTintPacket(BiomeTintPayload payload) implements CommonPacket {
    public static final CommonId ID = new CommonId("buildscape", "sync_biome_tint");

    public static SyncBiomeTintPacket decode(FriendlyByteBuf buffer) {
        return new SyncBiomeTintPacket(BiomeTintPayload.read(buffer));
    }

    @Override public CommonId getId() { return ID; }
    @Override public PacketDirection getDirection() { return PacketDirection.SERVER_TO_CLIENT; }
    @Override public void write(FriendlyByteBuf buffer) { payload.write(buffer); }

    @Override public void handle(Player player) {
        if (player != null) ClientBiomeTints.receive(Services.PLATFORM.getEntityLevel(player), payload);
    }
}
