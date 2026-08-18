package net.minecraft.network.codec;

@FunctionalInterface
public interface StreamEncoder {
   void encode(Object output, Object value);
}
