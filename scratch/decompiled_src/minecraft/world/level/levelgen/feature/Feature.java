package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;

public interface Feature {
   Codec DIRECT_CODEC = BuiltInRegistries.FEATURE_TYPE.byNameCodec().dispatch(Feature::codec, (t) -> t);
   Codec CODEC = RegistryCodecs.holder(Registries.FEATURE, DIRECT_CODEC);
   Codec LIST_CODEC = RegistryCodecs.holderSet(Registries.FEATURE, DIRECT_CODEC);

   MapCodec codec();

   boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin);

   default Stream getSubFeatures() {
      return Stream.empty();
   }

   default void setBlock(final LevelWriter level, final BlockPos pos, final BlockState blockState) {
      level.setBlockAndUpdate(pos, blockState);
   }

   default void safeSetBlock(final WorldGenLevel level, final BlockPos pos, final BlockState state, final Predicate canReplace) {
      if (canReplace.test(level.getBlockState(pos))) {
         level.setBlock(pos, state, 2);
      }

   }

   default void markAboveForPostProcessing(final WorldGenLevel level, final BlockPos placePos) {
      BlockPos.MutableBlockPos pos = placePos.mutable();

      for(int i = 0; i < 2; ++i) {
         pos.move(Direction.UP);
         if (level.getBlockState(pos).isAir()) {
            return;
         }

         level.getChunk(pos).markPosForPostProcessing(pos);
      }

   }
}
