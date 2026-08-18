package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ClientboundUpdateAttributesPacket implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundUpdateAttributesPacket::getEntityId, ClientboundUpdateAttributesPacket.AttributeSnapshot.STREAM_CODEC.apply(ByteBufCodecs.list(128)), ClientboundUpdateAttributesPacket::getValues, ClientboundUpdateAttributesPacket::new);
   private final int entityId;
   private final List attributes;

   public ClientboundUpdateAttributesPacket(final int entityId, final Collection values) {
      this.entityId = entityId;
      this.attributes = Lists.newArrayList();

      for(AttributeInstance value : values) {
         this.attributes.add(new ClientboundUpdateAttributesPacket.AttributeSnapshot(value.getAttribute(), value.getBaseValue(), value.getModifiers()));
      }

   }

   private ClientboundUpdateAttributesPacket(final int entityId, final List attributes) {
      this.entityId = entityId;
      this.attributes = attributes;
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_UPDATE_ATTRIBUTES;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleUpdateAttributes(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public List getValues() {
      return this.attributes;
   }

   public static record AttributeSnapshot(Holder attribute, double base, Collection modifiers) {
      public static final StreamCodec MODIFIER_STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, AttributeModifier::id, ByteBufCodecs.DOUBLE, AttributeModifier::amount, AttributeModifier.Operation.STREAM_CODEC, AttributeModifier::operation, AttributeModifier::new);
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Attribute.STREAM_CODEC, ClientboundUpdateAttributesPacket.AttributeSnapshot::attribute, ByteBufCodecs.DOUBLE, ClientboundUpdateAttributesPacket.AttributeSnapshot::base, MODIFIER_STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)), ClientboundUpdateAttributesPacket.AttributeSnapshot::modifiers, ClientboundUpdateAttributesPacket.AttributeSnapshot::new);
   }
}
