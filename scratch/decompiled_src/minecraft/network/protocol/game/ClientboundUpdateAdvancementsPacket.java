package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

public record ClientboundUpdateAdvancementsPacket(boolean shouldReset, List added, Set removed, Map progress, boolean showAdvancements) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, ClientboundUpdateAdvancementsPacket::shouldReset, AdvancementHolder.LIST_STREAM_CODEC, ClientboundUpdateAdvancementsPacket::added, Identifier.STREAM_CODEC.apply(ByteBufCodecs.collection(Sets::newLinkedHashSetWithExpectedSize)), ClientboundUpdateAdvancementsPacket::removed, ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, AdvancementProgress.STREAM_CODEC), ClientboundUpdateAdvancementsPacket::progress, ByteBufCodecs.BOOL, ClientboundUpdateAdvancementsPacket::showAdvancements, ClientboundUpdateAdvancementsPacket::new);

   public ClientboundUpdateAdvancementsPacket(final boolean reset, final Collection newAdvancements, final Set removedAdvancements, final Map progress, final boolean showAdvancements) {
      this(reset, List.copyOf(newAdvancements), Set.copyOf(removedAdvancements), Map.copyOf(progress), showAdvancements);
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_UPDATE_ADVANCEMENTS;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleUpdateAdvancementsPacket(this);
   }
}
