package net.minecraft.client.multiplayer.prediction;

import net.minecraft.network.protocol.Packet;

@FunctionalInterface
public interface PredictiveAction {
   Packet predict(int sequence);
}
