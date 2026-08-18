package net.minecraft.network.protocol;

import net.minecraft.network.codec.StreamCodec;

@FunctionalInterface
public interface CodecModifier {
   StreamCodec apply(StreamCodec original, Object context);
}
