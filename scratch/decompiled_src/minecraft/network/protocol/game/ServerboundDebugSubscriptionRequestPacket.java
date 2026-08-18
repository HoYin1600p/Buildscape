package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.debug.DebugSubscription;

public record ServerboundDebugSubscriptionRequestPacket(Set subscriptions) implements Packet {
   private static final StreamCodec SET_STREAM_CODEC = ByteBufCodecs.registry(Registries.DEBUG_SUBSCRIPTION).apply(ByteBufCodecs.collection(ReferenceOpenHashSet::new, 32));
   public static final StreamCodec STREAM_CODEC = SET_STREAM_CODEC.map(ServerboundDebugSubscriptionRequestPacket::new, ServerboundDebugSubscriptionRequestPacket::subscriptions);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_DEBUG_SUBSCRIPTION_REQUEST;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleDebugSubscriptionRequest(this);
   }
}
