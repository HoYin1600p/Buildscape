package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public interface CommonPacket {
    CommonId getId();
    PacketDirection getDirection();
    void write(FriendlyByteBuf buf);
    void handle(Player player);
}
