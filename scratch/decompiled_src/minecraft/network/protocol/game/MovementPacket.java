package net.minecraft.network.protocol.game;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public interface MovementPacket extends Packet {
   PacketType type();

   boolean hasPosition();

   boolean hasRotation();
}
