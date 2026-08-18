package net.minecraft.network.protocol;

import net.minecraft.resources.Identifier;

public record PacketType(PacketFlow flow, Identifier id) {
   public String toString() {
      return this.flow.id() + "/" + String.valueOf(this.id);
   }
}
