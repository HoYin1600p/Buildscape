package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockParticleOption implements ParticleOptions {
   private static final Codec BLOCK_STATE_CODEC = Codec.withAlternative(BlockState.CODEC, BuiltInRegistries.BLOCK.byNameCodec(), Block::defaultBlockState);
   private final ParticleType type;
   private final BlockState state;

   public static MapCodec codec(final ParticleType type) {
      return BLOCK_STATE_CODEC.xmap((state) -> new BlockParticleOption(type, state), (o) -> o.state).fieldOf("block_state");
   }

   public static StreamCodec streamCodec(final ParticleType type) {
      return ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY).map((state) -> new BlockParticleOption(type, state), (o) -> o.state);
   }

   public BlockParticleOption(final ParticleType type, final BlockState state) {
      this.type = type;
      this.state = state;
   }

   public ParticleType getType() {
      return this.type;
   }

   public BlockState getState() {
      return this.state;
   }
}
