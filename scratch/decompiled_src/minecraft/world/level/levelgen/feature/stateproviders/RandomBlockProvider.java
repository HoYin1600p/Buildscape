package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public record RandomBlockProvider(HolderSet blocks) implements BlockStateProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("blocks").forGetter(RandomBlockProvider::blocks)).apply(i, RandomBlockProvider::new));

   public MapCodec codec() {
      return CODEC;
   }

   public BlockState getState(final LevelAccessor level, final RandomSource random, final BlockPos pos) {
      return (BlockState)this.getState(random).orElseGet(() -> level.getBlockState(pos));
   }

   public @Nullable BlockState getOptionalState(final LevelAccessor level, final RandomSource random, final BlockPos pos) {
      return (BlockState)this.getState(random).orElse((Object)null);
   }

   private Optional getState(final RandomSource random) {
      return this.blocks.getRandomElement(random).map(Holder::value).map(Block::defaultBlockState);
   }
}
