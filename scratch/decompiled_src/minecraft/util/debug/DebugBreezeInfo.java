package net.minecraft.util.debug;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DebugBreezeInfo(Optional attackTarget, Optional jumpTarget) {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT.apply(ByteBufCodecs::optional), DebugBreezeInfo::attackTarget, BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional), DebugBreezeInfo::jumpTarget, DebugBreezeInfo::new);
}
