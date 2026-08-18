package net.minecraft.network.codec;

@FunctionalInterface
public interface StreamDecoder {
   Object decode(Object input);
}
