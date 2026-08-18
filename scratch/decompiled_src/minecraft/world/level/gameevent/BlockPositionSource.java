package net.minecraft.world.level.gameevent;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record BlockPositionSource(BlockPos pos) implements PositionSource {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BlockPos.CODEC.fieldOf("pos").forGetter(BlockPositionSource::pos)).apply(i, BlockPositionSource::new));
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, BlockPositionSource::pos, BlockPositionSource::new);

   public Optional getPosition(final Level level) {
      return Optional.of(Vec3.atCenterOf(this.pos));
   }

   public PositionSourceType getType() {
      return PositionSourceType.BLOCK;
   }

   public static class Type implements PositionSourceType {
      public MapCodec codec() {
         return BlockPositionSource.CODEC;
      }

      public StreamCodec streamCodec() {
         return BlockPositionSource.STREAM_CODEC;
      }
   }
}
