package net.minecraft.network.codec;

@FunctionalInterface
public interface StreamMemberEncoder {
   void encode(Object value, Object output);
}
