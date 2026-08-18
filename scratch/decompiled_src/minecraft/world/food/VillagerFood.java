package net.minecraft.world.food;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record VillagerFood(int nutrition) {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.POSITIVE_INT.fieldOf("nutrition").forGetter(VillagerFood::nutrition)).apply(i, VillagerFood::new));
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, VillagerFood::nutrition, VillagerFood::new);
}
