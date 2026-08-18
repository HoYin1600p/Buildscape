package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

public record LodestoneTracker(Optional target, boolean tracked) {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(GlobalPos.CODEC.optionalFieldOf("target").forGetter(LodestoneTracker::target), Codec.BOOL.optionalFieldOf("tracked", true).forGetter(LodestoneTracker::tracked)).apply(i, LodestoneTracker::new));
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional), LodestoneTracker::target, ByteBufCodecs.BOOL, LodestoneTracker::tracked, LodestoneTracker::new);

   public LodestoneTracker tick(final ServerLevel level) {
      if (this.tracked && !this.target.isEmpty()) {
         if (((GlobalPos)this.target.get()).dimension() != level.dimension()) {
            return this;
         } else {
            BlockPos blockPos = ((GlobalPos)this.target.get()).pos();
            return level.isInWorldBounds(blockPos) && level.getPoiManager().existsAtPosition(PoiTypes.LODESTONE, blockPos) ? this : new LodestoneTracker(Optional.empty(), true);
         }
      } else {
         return this;
      }
   }
}
