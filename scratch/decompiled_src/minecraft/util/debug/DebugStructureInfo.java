package net.minecraft.util.debug;

import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record DebugStructureInfo(BoundingBox boundingBox, List pieces) {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(BoundingBox.STREAM_CODEC, DebugStructureInfo::boundingBox, DebugStructureInfo.Piece.STREAM_CODEC.apply(ByteBufCodecs.list()), DebugStructureInfo::pieces, DebugStructureInfo::new);

   public static record Piece(BoundingBox boundingBox, boolean isStart) {
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(BoundingBox.STREAM_CODEC, DebugStructureInfo.Piece::boundingBox, ByteBufCodecs.BOOL, DebugStructureInfo.Piece::isStart, DebugStructureInfo.Piece::new);
   }
}
